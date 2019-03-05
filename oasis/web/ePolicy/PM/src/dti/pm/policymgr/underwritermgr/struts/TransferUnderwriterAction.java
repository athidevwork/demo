package dti.pm.policymgr.underwritermgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Transfer Underwriter.
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 17, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class TransferUnderwriterAction extends PMBaseAction {
    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
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
        return loadAllPolicyByUnderwriter(mapping, form, request, response);
    }

    /**
     * display transfer underwriter page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward displayPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPolicyByUnderwriter(mapping, form, request, response);
    }

    /**
     * set all search criteria to null
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearSearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPolicyByUnderwriter(mapping, form, request, response);
    }

    /**
     * Method to load all fund info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllPolicyByUnderwriter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyByUnderwriter", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);           // Gets Policy Header
            Record inputRecord = getInputRecord(request);
            String process = request.getParameter(RequestIds.PROCESS);
            request.setAttribute(RequestIds.PROCESS, process);
            RecordSet rs = null;
            if (process.equals("displayPage") || process.equals("clearSearchCriteria")) {
                rs = getEmptyRecordSetForPolicyDetails();
                clearSearchCriteria(inputRecord);
            }
            else {
                rs = getUnderwriterManager().loadAllPolicyByUnderwriter(inputRecord);
                if (rs.getSize() <= 0) {
                    MessageManager.getInstance().addInfoMessage("pm.transferUnderwriter.policyList.noDataFound");
                }
            }
            if (process.equals("displayPage")) {
                inputRecord.setFieldValue("effDate", DateUtils.formatDate(new Date()));
            }
            //make the process button invisible if no policy is found
            if (rs.getSize() == 0) {
                inputRecord.setFieldValue("isProcessAvailable", YesNoFlag.N);
            }
            else {
                inputRecord.setFieldValue("isProcessAvailable", YesNoFlag.Y);
            }

            //initialize header title for result list
            String resultHeader = "pm.transferUnderwriter.policyList.header";
            String resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader);
            if (rs != null && rs.getSize()>0 &&
                rs.getFirstRecord().hasField("maxRows")){
                int intTotalRowsReturned = rs.getSize();
                int intMaxRowsConfigured = Integer.parseInt(rs.getFirstRecord().getStringValue("maxRows"));
                if(intTotalRowsReturned >= intMaxRowsConfigured){
                    resultHeader = "pm.transferUnderwriter.policyList.abortSearch.header";
                    resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader,
                        new String[]{String.valueOf(intMaxRowsConfigured)});
                }
            }
            request.setAttribute(RESULT_LIST_HEADER, resultHeaderMsg);

            // publish page field
            publishOutputRecord(request, inputRecord);
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load loadAllPolicyByUnderwriter page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPolicyByUnderwriter", af);
        return af;
    }

    /**
     * perform transfer underwriter
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performTransferUnderwriter(ActionMapping mapping,
                                                    ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingEvent",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);
            // Get inital values
            Record inputRecord = getInputRecord(request);
            Record record = getUnderwriterManager().performTransferUnderwriter(inputRecord);
            //to make the row showed
            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for mailing event.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForMailingEvent", af);
        return af;

    }


    //verify spring config
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getUnderwriterManager() == null)
            throw new ConfigurationException("The required property 'underwriterManager' is missing.");
    }

    public UnderwriterManager getUnderwriterManager() {
        return m_underwriterManager;
    }

    public void setUnderwriterManager(UnderwriterManager underwriterManager) {
        m_underwriterManager = underwriterManager;
    }


    //get empty reocrd set for data bean
    private RecordSet getEmptyRecordSetForPolicyDetails() {
        RecordSet rs = new RecordSet();
        ArrayList fields = new ArrayList();
        fields.add("policyNo");
        rs.addFieldNameCollection(fields);
        return rs;
    }

    private void clearSearchCriteria(Record inputRecord) {
        inputRecord.setFieldValue("agent", null);
        inputRecord.setFieldValue("countyCode", null);
        inputRecord.setFieldValue("issueCompanyEntityId", null);
        inputRecord.setFieldValue("issueState", null);
        inputRecord.setFieldValue("policyTypeCode", null);
        inputRecord.setFieldValue("territory", null);
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.noPolicySelected.error");
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.targetUnderwriterNotSelected.error");
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.sameFromAndToUnderwriter.error");
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.fromEntityChanged.error");
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.effDateChanged.error");
        MessageManager.getInstance().addJsMessage("pm.transferUnderwriter.success.info");
    }

    private UnderwriterManager m_underwriterManager;
    protected static final String RESULT_LIST_HEADER = "resultHeader";

}
