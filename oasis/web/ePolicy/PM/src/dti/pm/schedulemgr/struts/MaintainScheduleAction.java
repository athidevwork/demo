package dti.pm.schedulemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.http.RequestIds;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.schedulemgr.ScheduleManager;
import dti.pm.transactionmgr.TransactionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Schedule.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/12/2012       xnie        137972 - Modified addJsMessages() to remove message for schedule date checking.
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields' value in
 *                              getInitialValuesForXXX method.
 * ---------------------------------------------------
 */

public class MaintainScheduleAction extends PMBaseAction {
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllSchedule(mapping, form, request, response);
    }

    /**
     * Method to load list of available schedule infos for risk/coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllSchedule(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSchedule", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //get parameters from request
            Record inputRecord = getInputRecord(request);

            //check if is from coverage page
            boolean isFromCoverage = inputRecord.getBooleanValue("isFromCoverage", false).booleanValue();

            // Get the policy header from the request, and load the risk header
            PolicyHeader policyHeader = getPolicyHeader(request,true,isFromCoverage);

            if (!isFromCoverage) {
                policyHeader.setCoverageHeader(null);
            }else{
                request.setAttribute(REQUEST_ATTR_IS_FROM_COVERAGE,"Y");
            }

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();

            // Load the schedules
            if (rs == null){
                rs = getScheduleManager().loadAllSchedules(policyHeader,selectIndProcessor, inputRecord);
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            //set sysparm to request for validation in js
            String chkPolschdOverlap = SysParmProvider.getInstance().getSysParm(SysParmIds.CHK_POLSCHD_OVRLP);
            chkPolschdOverlap = chkPolschdOverlap == null ? "N" : chkPolschdOverlap;
            String chkPolschdOverlapNoEnt = SysParmProvider.getInstance().getSysParm(SysParmIds.SCHD_OVRLP_NOENT);
            chkPolschdOverlapNoEnt = chkPolschdOverlapNoEnt == null ? "N" : chkPolschdOverlapNoEnt;
            request.setAttribute(REQUEST_ATTR_CHK_POLSCHD_OVRLP,chkPolschdOverlap);
            request.setAttribute(REQUEST_ATTR_SCHD_OVRLP_NOENT,chkPolschdOverlapNoEnt);

            
            //add js messages to messagemanager for the current request
            addJsMessages();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            //load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the coverage page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllSchedule", af);
        return af;
    }

    public ActionForward getInitialValuesForSchedule(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(),
            "getInitialValuesForSchedule", new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // get parameters from request
            Record inputRecord = getInputRecord(request);

            //check if is from coverage page
            boolean isFromCoverage = inputRecord.getBooleanValue("isFromCoverage", false).booleanValue();

            // Get the policy header from the request, and load the risk header
            PolicyHeader policyHeader = getPolicyHeader(request,true,isFromCoverage);

            if (!isFromCoverage) {
                policyHeader.setCoverageHeader(null);
            }

            // Get the initial values
            Record initialValuesRec = getScheduleManager().getInitialValuesForSchedule(policyHeader);

             // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load Pending Renewal Transaction.", e, response);
        }
        
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForSchedule", af);
        return af;
    }

    /**
     * Method to save all schedules.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllSchedules(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSchedules", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString="saveResult";
        RecordSet inputRecords = null;
        //If the request has valid save token, then proceed with save; if not forward to load page.

        try {
            // Secure access to the page, without loading the Oasis Fields
            securePage(request, form, false);

            if (hasValidSaveToken(request)) {

                /* Generate input records */
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllSchedules",
                        "Saving the Scheudle input records: " + inputRecords);
                }

                /* Pull the policy header from request */
                PolicyHeader policyHeader = getPolicyHeader(request);


                /* Call the business component to implement the validate/save logic */
                getScheduleManager().saveAllSchedules(policyHeader, inputRecords);
            }
        }
        catch (ValidationException ve) {
            /* Save the input records into request */
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Special Handling page.",
                e, request, mapping);        
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllSchedules", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainSchedule.confirm.totalCount");
        MessageManager.getInstance().addJsMessage("pm.maintainSchedule.confirm.totalDuration");
        MessageManager.getInstance().addJsMessage("pm.maintainSchedule.invalidEffectiveToDate.error");
        MessageManager.getInstance().addJsMessage("pm.maintainSchedule.dateOverlap.error");
        MessageManager.getInstance().addJsMessage("pm.maintainSchedule.dateOverlapForSameEntity.error");
    }

    private ScheduleManager m_scheduleManager;

    public ScheduleManager getScheduleManager() {
        return m_scheduleManager;
    }

    public void setScheduleManager(ScheduleManager scheduleManager) {
        m_scheduleManager = scheduleManager;
    }

    private TransactionManager m_transactionManager;

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public MaintainScheduleAction() {
    }

    public static final String REQUEST_ATTR_CHK_POLSCHD_OVRLP = "chkPolschdOverlap";
    public static final String REQUEST_ATTR_SCHD_OVRLP_NOENT = "chkPolschdOverlapNoEnt";
    public static final String REQUEST_ATTR_IS_FROM_COVERAGE = "isFromCoverage";

}
