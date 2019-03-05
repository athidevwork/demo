package dti.pm.transactionmgr.coirenewalmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.coirenewalmgr.CoiRenewalManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for COI renewal event
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 23, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2010       dzhang      Rename class & remove hard code.
 * 07/06/2010       dzhang      Remove unused import statements.
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */

public class MaintainCoiRenewalEventAction extends PMBaseAction {

    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return maintainCoiRenewalEvent(mapping, form, request, response);
    }

    /**
     * Method to load COI renewal event page.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward maintainCoiRenewalEvent(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "maintainCoiRenewalEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            RecordSet rs = new RecordSet();
            setDataBean(request, rs);

            // Get initial value for output
            Record output = getCoiRenewalManager().getInitialValuesForSearchCriteria();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            //loadGridHeader(request);
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the COI Renewal Event page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "maintainCoiRenewalEvent", af);
        return af;
    }

    /**
     * Method to load list of COI renewal event data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllCoiRenewalEvent(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoiRenewalEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);

            // Load the Manuscript
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getCoiRenewalManager().loadAllCoiRenewalEvent(getInputRecord(request));
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // if no data found, add warning message.
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addWarningMessage("pm.coiRenewal.nodata.found.error");
            }

        }
        catch (ValidationException ve) {
            setDataBean(request, new RecordSet());
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load COI Renewal Event data.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoiRenewalEvent", af);
        return af;
    }

    /**
     * Method to load list of COI renewal event detail data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllCoiRenewalEventDetail(ActionMapping mapping,
                                                      ActionForm form,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoiRenewalEventDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadDetailResult";
        Record output;
        try {

            securePage(request, form);
            // Load the coi renewal event detail
            RecordSet rs = getCoiRenewalManager().loadAllCoiRenewalEventDetail(getInputRecord(request));

            // Set loaded coi renewal event detail data into request
            setDataBean(request, rs, COI_RENEWAL_DETAIL_GRID_ID);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            
            loadGridHeader(request, getAnchorColumnNames()[1], getGridHeaderLayerIds()[1]);
            loadListOfValues(request, form);

            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (ValidationException ve) {
            setDataBean(request, new RecordSet(), COI_RENEWAL_DETAIL_GRID_ID);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load COI Renewal Event Detail data.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoiRenewalEventDetail", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.coiRenewal.noPolicy.found.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoiRenewalManager() == null)
            throw new ConfigurationException("The required property 'CoiRenewalManager' is missing.");
        if (getAnchorColumnNames() == null)
            throw new ConfigurationException("The required property 'anchorColumnNames' is missing.");
        if (getGridHeaderLayerIds() == null) {
            throw new ConfigurationException("The required property 'gridHeaderLayerIds' is missing.");
        }
    }

    public MaintainCoiRenewalEventAction() {
    }

    public CoiRenewalManager getCoiRenewalManager() {
        return m_coiRenewalManager;
    }

    public void setCoiRenewalManager(CoiRenewalManager coiRenewalManager) {
        m_coiRenewalManager = coiRenewalManager;
    }

    /**
     * Get anchor column names
     *
     * @return String[]
     */
    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    /**
     * Set anchor column name
     *
     * @param anchorColumnNames anchorColumnNames
     */
    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    /**
     * Get grid header layer ids
     *
     * @return String[]
     */
    public String[] getGridHeaderLayerIds() {
        return m_gridHeaderLayerIds;
    }

    /**
     * Set grid header layer ids
     *
     * @param gridHeaderLayerIds  gridHeaderLayerIds
     */
    public void setGridHeaderLayerIds(String[] gridHeaderLayerIds) {
        m_gridHeaderLayerIds = gridHeaderLayerIds;
    }

    private String[] m_anchorColumnNames;
    private String[] m_gridHeaderLayerIds;
    private CoiRenewalManager m_coiRenewalManager;
    protected static final String COI_RENEWAL_DETAIL_GRID_ID = "COIRenewalDetailListGrid";

}
