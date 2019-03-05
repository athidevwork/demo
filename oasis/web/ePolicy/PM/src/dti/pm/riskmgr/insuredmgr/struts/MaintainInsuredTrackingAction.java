package dti.pm.riskmgr.insuredmgr.struts;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.riskmgr.insuredmgr.InsuredTrackingFields;
import dti.pm.riskmgr.insuredmgr.InsuredTrackingManager;

/**
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 13, 2014
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 11/13/2014    wdang      157211 - Initial version, Maintain Insured Tracking Information.
 * ---------------------------------------------------
 */
public class MaintainInsuredTrackingAction extends PMBaseAction {

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
    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.loadAllInsuredTracking(mapping, form, request, response);
    }
    
    /**
     * load all Insured Tracking information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllInsuredTracking(ActionMapping mapping, ActionForm form,
                                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllInsuredTracking", new Object[] {mapping, form, request, response });
        String forwardString = "loadResult";

        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Gets grid record set
            RecordSet rs = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET) ;
            if (rs== null) {
                rs = getInsuredTrackingManager().loadAllInsuredTracking(policyHeader, inputRecord);
            }

            // Sets data bean
            setDataBean(request, rs);
            
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // fill list of values for search criteria
            fillLovsForSearchCriteria(request, policyHeader);

            // Loads list of values
            loadListOfValues(request, form);
            
            // Load grid header bean
            loadGridHeader(request);
            
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load insured tracking.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllInsuredTracking", af);
        return af;
    }
    
    /**
     * Save all Insured Tracking information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllInsuredTracking(ActionMapping mapping, ActionForm form,
                                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllInsuredTracking", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // get input
                Record inputRecord = getInputRecord(request);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Call the business component to implement the validate/save logic
                getInsuredTrackingManager().saveAllInsuredTracking(policyHeader, inputRecord, inputRecords);
            }
        }
        catch (ValidationException v) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save insured tracking.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllInsuredTracking", af);
        }
        return af;
    }

    /**
     * Get initial values for add insured tracking
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForInsuredTracking(ActionMapping mapping,
                                                            ActionForm form,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForInsuredTracking",
            new Object[]{mapping, form, request, response});

        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values
            Record initialValuesRec = getInsuredTrackingManager().getInitialValuesForInsuredTracking(policyHeader, inputRecord);

            // Set href on insured name
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField ghField = fields.getField(InsuredTrackingFields.INSURED_NAME_GH);
            initialValuesRec.setFieldValue(InsuredTrackingFields.INSURED_NAME_HREF, ghField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for insured tracking.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValuesForInsuredTracking");
        return null;
    }
    
    private void fillLovsForSearchCriteria(HttpServletRequest request, PolicyHeader policyHeader) {
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        OasisFormField field = null;
        String lov = null;
        if(fields != null){
            // policy terms
            field = (OasisFormField) fields.get(InsuredTrackingFields.SEARCH_TERM_HISTORY_ID);
            lov = "[NO_ADD_SELECT_OPTION] LIST:ALL, -ALL-";
            Iterator<Term> itr = policyHeader.getPolicyTerms();
            while (itr.hasNext()) {
                Term term = itr.next();
                String termString = null;
                if (FormatUtils.isDateFormatUS()) {
                    termString = (term.getEffectiveFromDate() + " - " + term.getEffectiveToDate());
                }
                else {
                    termString = FormatUtils.formatDateForDisplay(term.getEffectiveFromDate()) + " - " + FormatUtils.formatDateForDisplay(term.getEffectiveToDate());
                }
                lov += "," + term.getTermBaseRecordId() + "," + termString;
            }
            field.setLovSql(lov);
        }
    }

    public InsuredTrackingManager getInsuredTrackingManager() {
        return m_insuredTrackingManager;
    }

    public void setInsuredTrackingManager(InsuredTrackingManager insuredTrackingManager) {
        m_insuredTrackingManager = insuredTrackingManager;
    }

    private InsuredTrackingManager m_insuredTrackingManager;
}
