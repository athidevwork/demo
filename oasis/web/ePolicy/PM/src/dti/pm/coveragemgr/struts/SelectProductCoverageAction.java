package dti.pm.coveragemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.coveragemgr.impl.CoverageGroupRecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for product Coverage selection.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   March 12, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2011       syang       121208 - Added CoverageGroupRecordLoadProcessor to retrieve coverage.
 * 08/12/2011       syang       121208 - Set the indicator of coverage detail visibility.
 * 01/24/2014       adeng       149172 - Modified loadAllAvailableCoverage() to pass oasisFields into
 *                                       CoverageGroupRecordLoadProcessor to do further process.
 * ---------------------------------------------------
 */
public class SelectProductCoverageAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllAvailableCoverage(mapping, form, request, response);
    }

    /**
     * Method to load list of available coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAvailableCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableCoverage",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Loads available coverages for selection
            RecordLoadProcessor selectLoadProcessor = AddSelectIndLoadProcessor.getInstance();

            // Load coverage class group and pass the group load processor.
            loadListOfValues(request, form);
            OasisFields oasisFields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

            List groupList = (List) request.getAttribute(COVERAGE_GROUP_LOV);
            CoverageGroupRecordLoadProcessor groupProcessor = new CoverageGroupRecordLoadProcessor(getCoverageManager(), groupList, policyHeader, oasisFields);
            RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(selectLoadProcessor, groupProcessor);
            RecordSet rs = getCoverageManager().loadAllAvailableCoverage(policyHeader, loadProcessor);

            // Set page entitlement
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.addCoverage.nodata.error");
                request.setAttribute(IS_BUTTON_DONE_ENABLE, "N");
            }
            else {
                request.setAttribute(IS_BUTTON_DONE_ENABLE, "Y");
            }

            // Set the indicator of coverage detail visibility.
            Record inputRecord = getInputRecord(request);
            if(inputRecord.hasStringValue(COVERAGE_DETAIL_DISPLAY)){
                request.setAttribute(COVERAGE_DETAIL_DISPLAY, YesNoFlag.getInstance(inputRecord.getStringValue(COVERAGE_DETAIL_DISPLAY)));
            }

            // Sets data Bean
            setDataBean(request, rs);

            // Add Js messages
            addJsMessages();

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load selectProductCoverage page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableCoverage", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addCoverage.noselection.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverage.nodata.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
    }

    public SelectProductCoverageAction() {
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    protected static final String IS_BUTTON_DONE_ENABLE = "isButtonDoneEnable";
    private CoverageManager m_coverageManager;
    private static final String COVERAGE_GROUP_LOV = "coverageGroup_GHLOV";
    private static final String COVERAGE_DETAIL_DISPLAY = "coverageDetailDisplay";
}
