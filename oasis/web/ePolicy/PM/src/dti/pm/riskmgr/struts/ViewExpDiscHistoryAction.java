package dti.pm.riskmgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.entityglancemgr.EntityGlanceFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.riskmgr.RiskFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for view experience discount history information.
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2018
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/31/2018       ryzhao      188891 - Initial version.
 * ---------------------------------------------------
 */

public class ViewExpDiscHistoryAction extends PMBaseAction {
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
        return loadExpHistoryInfo(mapping, form, request, response);
    }

    /**
     * Method to load experience discount history information.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadExpHistoryInfo(ActionMapping mapping,
                                            ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadExpHistoryInfo", new Object[]{mapping, form, request, response});
        String forwardString = "loadExpHistoryInfo";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Get data for transaction summary layer
            RecordSet rs = getComponentManager().loadExpHistoryInfo(inputRecord);
            if (rs.getSize() == 0) {
                // if no data found, add error message.
                MessageManager.getInstance().addErrorMessage(
                    "pm.viewExpDiscHistory.expHistoryInfo.noDataFound.error");
            }

            // Set all data beans to request
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Set title attributes
            setAttributesForTitle(output);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the experience discount history page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadExpHistoryInfo", af);
        return af;
    }

    /**
     * Set attributes which will be used in the panel title of the page.
     * 1. entityName
     * 2. entity type
     * 3. Client ID
     *
     * @param rec
     */
    private void setAttributesForTitle(Record rec) {
        Logger l = LogUtils.enterLog(getClass(), "setAttributesForTitle");

        if (rec.hasStringValue(AddressFields.ENTITY_NAME)) {
            AddressFields.setEntityName(rec, AddressFields.getEntityName(rec));
        }

        if (rec.hasStringValue(RiskFields.RISK_TYPE_DESC)) {
            RiskFields.setRiskTypeDesc(rec, RiskFields.getRiskTypeDesc(rec));
        }

        if (rec.hasStringValue(EntityGlanceFields.CLIENT_ID)) {
            rec.setFieldValue(EntityGlanceFields.CLIENT_ID, rec.getStringValue(EntityGlanceFields.CLIENT_ID));
        }

        l.exiting(getClass().getName(), "setAttributesForTitle");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ViewExpDiscHistoryAction() {
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    private ComponentManager m_componentManager;
}
