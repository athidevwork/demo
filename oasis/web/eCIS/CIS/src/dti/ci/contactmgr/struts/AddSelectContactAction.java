package dti.ci.contactmgr.struts;

import dti.ci.contactmgr.ContactFields;
import dti.ci.contactmgr.ContactManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The action class for Add Select Contact page.
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   1/17/13
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/12/2014       Elvin       Issue 151626: add js message for contact name required check
 * ---------------------------------------------------
 */
public class AddSelectContactAction extends CIBaseAction {
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
        return loadAllContactPopup(mapping, form, request, response);
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
    public ActionForward loadAllContactPopup(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllContact", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllContactResultPopup";

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

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            publishOutputRecord(request, outputRecord);
            /* get LOV */
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();

            saveToken(request);

            request.setAttribute(ICIConstants.PK_PROPERTY,
                    inputRecord.getStringValue(ICIConstants.PK_PROPERTY));
            request.setAttribute(ContactFields.EVENT_NAME,
                    inputRecord.getStringValue(ContactFields.EVENT_NAME, ""));
            request.setAttribute(ContactFields.CONTACT_ID_FIELD_NAME,
                    inputRecord.getStringValue(ContactFields.CONTACT_ID_FIELD_NAME, ""));
            request.setAttribute(ContactFields.CONTACT_NAME_FILED_NAME,
                    inputRecord.getStringValue(ContactFields.CONTACT_NAME_FILED_NAME, ""));
        } catch (Exception e) {
            forwardString = handleErrorPopup(
                    AppException.UNEXPECTED_ERROR, "Failed to load contact page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllContactPopup", af);
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

        try {
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
            }
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
    protected void addJsMessages() {
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
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
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
