package dti.ci.contactmgr.struts;

import dti.ci.contactmgr.ContactFields;
import dti.ci.contactmgr.ContactManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.recordset.Record;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS contact page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 22, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 01/20/2011       Michael Li  Issue:116335
 * 03/12/2014       Elvin       Issue 151626: add js message for contact name required check
 * ---------------------------------------------------
*/

public class MaintainContactAction extends CIBaseAction {
    /**
     * Unspecified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllContact(mapping, form, request, response);
    }


    /**
     * Process load contacts of an entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllContact(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllContact", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllContactResult";

        try {
            securePage(request, form);

            OasisFields fields = ActionHelper.getFields(request);
            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY, "");
            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            // Load contact.
            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getContactManager().loadAllContact(inputRecord);
            }

            setDataBean(request, rs);
            loadGridHeader(request);

            request.getSession().setAttribute(ContactFields.ADDRESS_IS_SET, "false");

            String contAddr = SysParmProvider.getInstance().getSysParm(SYSPARM_RES_CI_CONTACT_SET_ADDR, "");
            if ("Y".equals(contAddr.trim())) {
                OasisFormField addrField = (OasisFormField) fields.getField(ContactFields.ADDRESS_ID);
                String addrFkLovSql = addrField.getLovSql();
                addrFkLovSql = (null == addrFkLovSql ? addrFkLovSql
                        : (addrFkLovSql.endsWith("2") ? addrFkLovSql.substring(0, addrFkLovSql.length() - 1) + PRIMARY_ADDRESS_B_DESC
                        : addrFkLovSql));

                addrField.setLovSql(addrFkLovSql);
                fields.put(ContactFields.ADDRESS_ID, addrField);

                request.getSession().setAttribute(ContactFields.ADDRESS_IS_SET, "true");
            }

            setEntityCommonInfoToRequest(request, inputRecord);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            publishOutputRecord(request, outputRecord);
            /* get LOV */
            loadListOfValues(request, form);

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityFK, this.getClass().getName());

            // set js messages
            addJsMessages();
            setCisHeaderFields(request);

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load contact page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllContact", af);
        }
        return af;
    }

    /**
     * Process save contacts of an entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllContact(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllContact", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveAllContactResult";
        RecordSet inputRecordSet = null;
        
        try {
            if (isTokenValid(request, true)) {
                securePage(request, form);
                inputRecordSet = getInputRecordSet(request);

                getContactManager().saveAllContact(inputRecordSet);
            }
        } catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRecordSet);

            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save contacts.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllContact", af);
        }
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");

        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneAreaCode.number");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneExtension.number");
        MessageManager.getInstance().addJsMessage("ci.entity.message.faxAreaCode.number");
        MessageManager.getInstance().addJsMessage("ci.entity.message.effectiveFromDate.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.effectiveToDate.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.noEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.areaCode.noEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.faxNumber.noEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.faxAreaCode.noEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.effectiveToDate.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.row.wrongMessage");
        MessageManager.getInstance().addJsMessage("ci.entity.message.contact.nameReq");
        MessageManager.getInstance().addJsMessage("ci.common.error.format.ssn");
        MessageManager.getInstance().addJsMessage("js.is.notes.notAvailable");
    }

    public void verifyConfig() {
        if (getContactManager() == null) {
            throw new ConfigurationException("The required property 'contactManager' is missing.");
        }
    }

    public ContactManager getContactManager() {
        return m_contactManager;
    }

    public void setContactManager(ContactManager contactManager) {
        m_contactManager = contactManager;
    }

    private ContactManager m_contactManager;
}
