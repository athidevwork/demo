package dti.pm.componentmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.impl.ComponentGroupRecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2011       wfu         127859 - Modified to support loading policy header without primary risk.
 * ---------------------------------------------------
 */
public class SelectComponentAction extends PMBaseAction {

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
        return loadAllAvailableComponent(mapping, form, request, response);
    }

    /**
     * Load all available components
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableComponent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableComponent",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            PolicyHeader policyHeader = getPolicyHeader(request);
            try {
                // Load risk header for coverage component
                String riskId = request.getParameter("riskId");
                // If the riskId is not specified, the primary risk is loaded.
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
            } catch (AppException ae) {
                // No primary risk existed and given riskId has no record.
            }

            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            Record record = getInputRecord(request);

            loadListOfValues(request, form);

            // Loads available components for selection
            RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
            List groupList = (List) request.getAttribute(FIELD_ID_COMPONENT_GROUP + "LOV");
            ComponentGroupRecordLoadProcessor groupProcessor = new ComponentGroupRecordLoadProcessor(groupList);
            RecordLoadProcessor processor = RecordLoadProcessorChainManager.
                getRecordLoadProcessor(selectIndProcessor, groupProcessor);
            RecordSet rs = getComponentManager().loadAllAvailableComponent(policyHeader, record, processor);

            // Add Js messages
            addJsMessages();

            // Load grid header bean
            loadGridHeader(request);

            // Sets data Bean
            setDataBean(request, rs);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load select Component page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableComponent", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addComponent.nodata.error");
        MessageManager.getInstance().addJsMessage("pm.addComponent.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public SelectComponentAction() {
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    private ComponentManager componentManager;
    private static final String FIELD_ID_COMPONENT_GROUP = "componentGroup_GH";
}
