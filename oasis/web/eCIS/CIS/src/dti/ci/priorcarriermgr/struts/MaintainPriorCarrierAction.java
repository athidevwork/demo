package dti.ci.priorcarriermgr.struts;

import dti.ci.helpers.CILinkGenerator;
import dti.ci.priorcarriermgr.PriorCarrierFields;
import dti.ci.priorcarriermgr.PriorCarrierManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Prior Carrier
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/22/2009       kshen       Set default term year to request when loading page.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 10/26/2011       kshen       124160. Added claimant filter criteria.
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 10/08/2012       kshen       Refactored the page.
 * 12/28/2012       kshen       Issue 139881.
 * ---------------------------------------------------
 */


public class MaintainPriorCarrierAction extends CIBaseAction {
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
        return loadAllPriorCarrier(mapping, form, request, response);
    }

    /**
     * Load all prior carrier records by filter criteria.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllPriorCarrier(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrier", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllPriorCarrierResult";
        try {
            securePage(request, form);

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

            // Load Prior Carrier.
            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getPriorCarrierManager().loadAllPriorCarrier(inputRecord);
            }

            setDataBean(request, rs);
            loadGridHeader(request);

            setEntityCommonInfoToRequest(request, inputRecord);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            String defaultTermYear = getPriorCarrierManager().getDefaultTermYear(inputRecord);
            request.setAttribute(PriorCarrierFields.DEFAULT_TERM_YEAR, defaultTermYear);

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
                    AppException.UNEXPECTED_ERROR, "Failed to load Prior Carrier page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrier", af);
        }
        return af;
    }

    /**
     * Save prior carrier.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward savePriorCarrier(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePriorCarrier", new Object[]{mapping, form, request, response});
        }
        
        String forwardString = "savePriorCarrierResult";
        RecordSet inputRs = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getPriorCarrierManager().saveAllPriorCarrier(inputRs);
            }
        } catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRs);

            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save all prior carrier.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePriorCarrier", af);
        }
        return af;

    }

    /**
     * Get initial values for adding prior carrier.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForPriorCarrier(ActionMapping mapping, ActionForm form,
                                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPriorCarrier", new Object[]{mapping, form, request, response});
        }

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            Record initialRecord = getPriorCarrierManager().getInitialValuesForPriorCarrier(inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialRecord);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialRecord);

            // prepare return values
            writeAjaxXmlResponse(response, initialRecord, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding prior carrier.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForPriorCarrier", af);
        return af;

    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
    }

    public void verifyConfig() {
        if (getPriorCarrierManager() == null) {
            throw new ConfigurationException("The required property 'priorCarrierManager' is missing.");
        }
    }

    public PriorCarrierManager getPriorCarrierManager() {
        return m_priorCarrierManager;
    }

    public void setPriorCarrierManager(PriorCarrierManager priorCarrierManager) {
        m_priorCarrierManager = priorCarrierManager;
    }

    private PriorCarrierManager m_priorCarrierManager;
}


