package dti.pm.coverageclassmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coverageclassmgr.impl.CoverageClassGroupRecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for product Coverage Class selection.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2011       syang       121208 - Added CoverageClassGroupRecordLoadProcessor to retrieve coverage classes.
 * 08/12/2011       syang       121208 - Set the indicator of coverage class detail visibility.
 * ---------------------------------------------------
 */
public class SelectProductCoverageClassAction extends PMBaseAction {

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
        return loadAllAvailableCoverageClass(mapping, form, request, response);
    }

    /**
     * Method to load list of available coverage class.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAvailableCoverageClass(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableCoverageClass",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Set up load processor
            RecordLoadProcessor loadProc = AddSelectIndLoadProcessor.getInstance();

            // Load coverage class group and pass the group load processor.
            loadListOfValues(request, form);
            List groupList = (List) request.getAttribute(COVERAGE_CLASS_GROUP_LOV);
            CoverageClassGroupRecordLoadProcessor groupProcessor = new CoverageClassGroupRecordLoadProcessor(groupList);
            RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProc, groupProcessor);
            // Loads available coverages for selection
            RecordSet rs = getCoverageClassManager().loadAllAvailableCoverageClass(policyHeader, loadProcessor);

            // Sets data Bean
            setDataBean(request, rs);


            // Set page entitlement, and check available coverage classes
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.addCoverageClass.nodata.error");
                request.setAttribute(IS_BUTTON_DONE_ENABLE, "N");
            }
            else {
                request.setAttribute(IS_BUTTON_DONE_ENABLE, "Y");
            }

            // Set the indicator of coverage class detail visibility.
            Record inputRecord = getInputRecord(request);
            if(inputRecord.hasStringValue(COVERAGE_CLASS_DETAIL_DISPLAY)){
                request.setAttribute(COVERAGE_CLASS_DETAIL_DISPLAY, YesNoFlag.getInstance(inputRecord.getStringValue(COVERAGE_CLASS_DETAIL_DISPLAY)));
            }

            // Add Js messages
            addJsMessages();

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load selectProductCoverageClass page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableCoverageClass", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.noselection.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.nodata.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getCoverageClassManager() == null)
            throw new ConfigurationException("The required property 'coverageClassManager' is missing.");
    }

    public SelectProductCoverageClassAction() {
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    private CoverageClassManager m_coverageClassManager;
    protected static final String IS_BUTTON_DONE_ENABLE = "isButtonDoneEnable";
    private static final String COVERAGE_CLASS_GROUP_LOV = "coverageGroup_GHLOV";
    private static final String COVERAGE_CLASS_DETAIL_DISPLAY = "coverageClassDetailDisplay";
}
