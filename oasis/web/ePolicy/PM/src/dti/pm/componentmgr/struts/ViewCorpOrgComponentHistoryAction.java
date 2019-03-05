package dti.pm.componentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for process Org/Corp component.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 20, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ViewCorpOrgComponentHistoryAction extends PMBaseAction {
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
        return loadAllCorpOrgComponentHistory(mapping, form, request, response);
    }

    /**
     * Method to load cycle detail.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCorpOrgComponentHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCorpOrgComponentHistory", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Load the processing event recordSet from request.
            RecordSet eventRs = getComponentManager().loadAllProcessEventHistory(inputRecord);
            // Load the processing detail recordSet from request.
            RecordSet detailRs = getComponentManager().loadAllProcessDetailHistory(inputRecord);
            // Set loaded data into request.
            setDataBean(request, eventRs);
            setDataBean(request, detailRs, PROCESSING_DETAIL_GRID_ID);
            // Make the Summary Record available for output.
            Record output = eventRs.getSummaryRecord();
            output.setFields(detailRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);
            // Set currentGridId to every gridID on page before load gird header then load grid header for each grid.
            loadGridHeader(request);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PROCESSING_DETAIL_GRID_ID);
            loadGridHeader(request, null, PROCESSING_DETAIL_GRID_ID, PROCESSING_DETAIL_GRID_LAYER_ID);
            // Load the list of values after loading the data.
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load corp-org processing history page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCorpOrgComponentHistory", af);
        return af;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(PROCESSING_DETAIL_GRID_ID)) {
                anchorName = getDetailAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'detailAnchorColumnName' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public String getDetailAnchorColumnName() {
        return m_detailAnchorColumnName;
    }

    public void setDetailAnchorColumnName(String detailAnchorColumnName) {
        m_detailAnchorColumnName = detailAnchorColumnName;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String PROCESSING_EVENT_GRID_ID = "processingEventListGrid";
    protected static final String PROCESSING_DETAIL_GRID_ID = "processingDetailListGrid";
    protected static final String PROCESSING_EVENT_GRID_LAYER_ID = "PM_PRO_OC_DISC_HISTORY_EVENT_GH";
    protected static final String PROCESSING_DETAIL_GRID_LAYER_ID = "PM_PRO_OC_DISC_HISTORY_DETAIL_GH";
    protected static final String PROCESSING_EVENT_RECORD_SET = "processingEventRecordSet";
    protected static final String PROCESSING_DETAIL_RECORD_SET = "processingDetailRecordSet";
    private ComponentManager m_componentManager;
    private String m_detailAnchorColumnName;
}
