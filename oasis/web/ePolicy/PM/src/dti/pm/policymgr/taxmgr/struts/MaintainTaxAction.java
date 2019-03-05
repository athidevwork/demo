package dti.pm.policymgr.taxmgr.struts;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.data.RowAccessorRecordLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.policymgr.taxmgr.TaxManager;
import dti.pm.policymgr.taxmgr.impl.MaintainTaxEntitlementRecordLoadProcessor;
import dti.pm.policymgr.taxmgr.impl.MaintainTaxRiskEntitlementRecordLoadProcessor;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;

/**
 * Action class to maintain tax
 * <p/>
 * <p>
 * (C) 2014 Delphi Technology, inc. (dti)
 * </p>
 * Date: Oct 13, 2014
 * 
 * @author wdang
 */
/*
 * 
 * Revision Date Revised By Description
 * --------------------------------------------------- 
 * 10/13/2014    wdang      158112 - Initial version, Maintain Premium Tax Information.
 * ---------------------------------------------------
 */

public class MaintainTaxAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllTax(mapping, form, request, response);
    }
    
    /**
     * load all risk/tax information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllTax(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTax", new Object[] {mapping, form, request, response });
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load risk
            RecordSet riskRs = (RecordSet) request.getAttribute(RISK_GRID_RECORD_SET);
            if (riskRs == null) {
                RecordLoadProcessor loadProcessor = new MaintainTaxRiskEntitlementRecordLoadProcessor(policyHeader);
                riskRs = getTaxManager().loadAllRisk(policyHeader, inputRecord, loadProcessor);
            }
            // Set loaded risk data into request
            setDataBean(request, riskRs, RISK_GRID_ID);

            // Load tax
            RecordSet taxRs = (RecordSet) request.getAttribute(TAX_GRID_RECORD_SET);
            if (taxRs == null) {
                RecordLoadProcessor loadProcessor = new MaintainTaxEntitlementRecordLoadProcessor(policyHeader, riskRs);
                loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor,
                        new AddOrigFieldsRecordLoadProcessor(new String[]{
                                TaxFields.EFFECTIVE_TO_DATE,
                                TaxFields.STATE_CODE,
                                TaxFields.COUNTY_TAX_CODE,
                                TaxFields.CITY_TAX_CODE,
                                TaxFields.TAX_LEVEL}));
                loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor,
                        new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, TaxFields.PREMIUM_TAX_RECORD_ID));
                
                taxRs = getTaxManager().loadAllTaxHeader(policyHeader, inputRecord, loadProcessor);
            }
            // Set loaded tax data into request
            setDataBean(request, taxRs, TAX_GRID_ID);

            // Make the Summary Record available for output
            Record output = riskRs.getSummaryRecord();
            output.setFields(taxRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);
            
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load risk grid header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RISK_GRID_ID);
            loadGridHeader(request, null, RISK_GRID_ID, RISK_GRID_LAYER_ID);
            // Load tax grid header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TAX_GRID_ID);
            loadGridHeader(request, null, TAX_GRID_ID, TAX_GRID_LAYER_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
            
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the maintain tax page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTax", af);
        return af;
    }
    
    /**
     * Save all updated tax.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllTax(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTax", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecordSet = null;
        
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);
                
                // Map textXML to RecordSet for input
                inputRecordSet = getInputRecordSet(request, TAX_GRID_ID);

                // Call the business component to implement the validate/save logic
                getTaxManager().saveAllTaxHeader(policyHeader, inputRecordSet);
            }
        }
        catch (ValidationException v) {
             // Set back to tax grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TAX_GRID_ID);
            // Save the input records into request
            request.setAttribute(TAX_GRID_RECORD_SET, inputRecordSet);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save tax.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTax", af);
        }
        return af;
    }
    
    /**
     * get Initial Values for Add Tax
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddTax(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForPriorAct",
                new Object[] { mapping, form, request, response });

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get the initial values for risk
            Record initialValuesRec = getTaxManager().getInitialValuesForAddTax(policyHeader, inputRecord);
            
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, initialValuesRec);
            
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            
            // get LOV labels for initial values
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        } catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Failed to get initial values for add tax.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddTax", af);
        }
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
            if (currentGridId.equals(RISK_GRID_ID)) {
                anchorName = getRiskAnchorColumnName();
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
     * Method to verify Configuration
     */
    public void verifyConfig() {
        if (getTaxManager() == null) {
            throw new ConfigurationException("The required property 'taxManager' is missing.");
        }
        if (getRiskAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'riskAnchorColumnName' is missing.");
        }
    }

    public String getRiskAnchorColumnName() {
        return m_riskAnchorColumnName;
    }

    public void setRiskAnchorColumnName(String riskAnchorColumnName) {
        m_riskAnchorColumnName = riskAnchorColumnName;
    }

    public TaxManager getTaxManager() {
        return m_taxManager;
    }

    public void setTaxManager(TaxManager taxManager) {
        m_taxManager = taxManager;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String RISK_GRID_ID = "riskListGrid";
    protected static final String RISK_GRID_RECORD_SET = "riskGridRecordSet";
    protected static final String RISK_GRID_LAYER_ID = "PM_RISK_LIST_GH";
    protected static final String TAX_GRID_ID = "taxListGrid";
    protected static final String TAX_GRID_RECORD_SET = "taxGridRecordSet";
    protected static final String TAX_GRID_LAYER_ID = "PM_MAINTAIN_TAX_GH";

    private TaxManager m_taxManager;
    private String m_riskAnchorColumnName;
    
}
