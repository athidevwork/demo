package dti.pm.componentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
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
public class ProcessCorpOrgComponentAction extends PMBaseAction {
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
        return loadAllCorpOrgDiscountMember(mapping, form, request, response);
    }

    /**
     * Method to Corp/Org discount member.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCorpOrgDiscountMember(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCorpOrgDiscountMember", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);

            // Retrieve all
            RecordLoadProcessor selIndLoadProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordSet rs = getComponentManager().loadAllCorpOrgDiscountMember(inputRecord, selIndLoadProcessor);

            // Set loaded data into request.
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, rs.getSummaryRecord());
            // Set currentGridId to every gridID on page before load gird header then load grid header for each grid.
            loadGridHeader(request);
            // Load the list of values after loading the data.
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load process Corp/Org discount page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCorpOrgDiscountMember", af);
        return af;
    }

    /**
     * Process discount.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processDiscount(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDiscount", new Object[]{mapping, form, request, response});
        }
        String forwardString = "processDiscount";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields.
            securePage(request, form, false);
            // Map regiona team textXML to RecordSet for input.
            RecordSet inputRecords = getInputRecordSet(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Process the discount.
            getComponentManager().processCorpOrgDiscount(inputRecord, inputRecords);
            // Set searchMember to reload all member.
            inputRecord.setFieldValue("searchMember", YesNoFlag.Y);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.processingCorpOrgComponent.process.error", "Failed to process discount.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processDiscount", af);
        return af;
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
}
