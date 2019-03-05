package dti.pm.componentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.SysParmProvider;
/**
 * Action class for process mass component.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 8, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/18/2015       kxiang      163584 - Modified display to add cycledB into recordset.
 * 01/11/2017       lzhang      182312 - Modified applyMassComponent to
 *                                       add skipHandleAfterViewValidationB.
 * 03/20/2017       eyin        180675 - Made change to open view Validation Error popup instead of forwarding to
 *                                       view Validation Error popup for tab style.
 * 12/14/2017       eyin        190085 - Modified applyMassComponent(), add JS message 'pm.process.mass.component.apply.info'.
 * ---------------------------------------------------
 */
public class ProcessMassComponentAction extends PMBaseAction {
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
        return display(mapping, form, request, response);
    }

    /**
     * Method to display process component.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "success";
        try {
            // Secures access to the page.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Load component from request.
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                // Set empty data into request.
                setEmptyDataBean(request);
                rs = (RecordSet) ((HashMap) request.getAttribute(dti.oasis.http.RequestIds.RECORDSET_MAP)).get(dti.oasis.http.RequestIds.DATA_BEAN);;
                rs.addFieldNameCollection(Arrays.asList(ComponentFields.CYCLED_B));
            }
            else {
                // Set loaded data into request.
                setDataBean(request, rs);
            }
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, inputRecord);
            // Load grid header.
            loadGridHeader(request);
            // Load the list of values.
            loadListOfValues(request, form);
            // Add Js messages.
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to display the process component page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * Apply component.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward applyMassComponent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "applyMassComponent", new Object[]{mapping, form, request, response});
        }
        String forwardString = "applyResult";
        RecordSet inputRecords = null;
        try {
            // Secures access to the page.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Get inputRecords.
            inputRecords = getInputRecordSet(request);
            // Apply all the component.
            Record record = getComponentManager().applyMassComponent(inputRecord, inputRecords);
            int rt = record.getIntegerValue("rt").intValue();
            String msg = record.getStringValue("msg");
            RequestStorageManager.getInstance().set(APPLY_RESULT, rt);
            if (rt == 1) {
                // System should set updateIndicator to "N" since the resultSet will be loaded from request not DB.
                for (int i = 0; i < inputRecords.getSize(); i++) {
                    inputRecords.getRecord(i).setUpdateIndicator(UpdateIndicator.NOT_CHANGED);
                }
                MessageManager.getInstance().addInfoMessage("pm.process.mass.component.apply.info", new String[]{msg});
                MessageManager.getInstance().addJsMessage("pm.process.mass.component.apply.info", new String[]{msg});
            }
            else {
                String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
                if(pmUIStyle.equals("B")){
                    // Fix issue 99964.
                    forwardString = "viewApplyError";
                }
                if (rt == 0) {
                    request.setAttribute("skipHandleAfterViewValidationB", false);
                }else{
                    request.setAttribute("skipHandleAfterViewValidationB", true);
                }
            }
        }
        catch (ValidationException v) {
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to apply component.", e, request, mapping);
        }
        // Save the input records into request.
        request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "applyMassComponent", af);
        return af;
    }

    /**
     * Get initial values for adding process componennt.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddProcessComponent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddProcessComponent", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Get the initial values.
            Record record = getComponentManager().getInitialValuesForAddProcessComponent(inputRecord);
            // Set output record.
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding process compoennt.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddProcessComponent", af);
        return af;
    }

    /**
     * Perform rating.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performRating(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performRating", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            //get policy header from request.
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Rate policy.
            String returnValue = getTransactionManager().performTransactionRating(policyHeader.toRecord());
            Record record = new Record();
            record.setFieldValue("RATE", returnValue);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to rate.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "performRating", af);
        return af;
    }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.process.mass.component.duplicated.error");
        MessageManager.getInstance().addJsMessage("pm.process.mass.component.rate.error");
        MessageManager.getInstance().addJsMessage("pm.process.mass.component.rate.info");
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    private ComponentManager m_componentManager;

    private static String APPLY_RESULT = "applyResult";
}
