package dti.pm.policymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.MultiValueField;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.http.RequestIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.CreatePolicyFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 26, 2007
 *
 * @author Sharon Ma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2007       sxm         Handle "create" as Ajax submit
 * 01/19/2011       wfu         113566 - Added logic to handle copying policy from risk.
 * 11/05/2015       tzeng       165790 - Modified copyNewPolicyFromRisk to add policy phase code.
 * 07/18/2018       xnie        192932 - Modified create() to remove UserSessionIds.POLICY_LIST from user session.
 * 09/14/2018       cesar       195306 - added saveToken(request) to display() to implement CSRF requirement.
 * ---------------------------------------------------
 */

public class CreatePolicyAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS,"display");
        return display(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "display"
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
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            PolicyManager policyMgr = getPolicyManager();

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get initial values
            Record outputRecord = policyMgr.getInitialValuesForCreatePolicy(getInputRecord(request));

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
            request.setAttribute(RequestIds.IS_NEW_VALUE,"Y");

            // Load the list of values
            loadListOfValues(request, form);

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the create policy page.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "validateFields"
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
    public ActionForward validateFields(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateFields", new Object[]{mapping, form, request, response});
        try {
            PolicyManager policyMgr = getPolicyManager();

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = addRegionalOfficeVisibility(request, getInputRecord(request));

            // process field change
            try {
                Record record = policyMgr.handleFieldChangeForCreatePolicy(inputRecord);
                // return term expiration date
                Record tempRecord = new Record();
                tempRecord.setFieldValue(CreatePolicyFields.TERM_EFFECTIVE_TO_DATE,
                    CreatePolicyFields.getTermEffectiveToDate(record));
                writeAjaxXmlResponse(response, tempRecord, true);
            }
            catch (ValidationException e) {
                handleValidationExceptionForAjax(e, response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate fields in the create policy page.", e, response);
        }

        // done
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateFields", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "findAllPolicyType"
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
    public ActionForward findAllPolicyType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "findAllPolicyType", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            PolicyManager policyMgr = getPolicyManager();
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the list of values
            loadListOfValues(request, form);

            // get input
            Record inputRecord = addRegionalOfficeVisibility(request, getInputRecord(request));

            // get the policy types
            RecordSet rs = policyMgr.findAllPolicyType(inputRecord, true);

            // Re-Publish the input record for use by the Oasis Tags and JSP
            publishOutputRecord(request, inputRecord);

            // Load the policy types
            setDataBean(request, rs);

            // load grid header for policy type list
            loadGridHeader(request);

            if (inputRecord.hasField(RequestIds.IS_FROM_COPY_NEW)
                && YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_FROM_COPY_NEW)).booleanValue()) {
                request.setAttribute(RequestIds.IS_FROM_COPY_NEW, YesNoFlag.Y);
                request.setAttribute(RequestIds.POLICY_NO, inputRecord.getStringValue(RequestIds.POLICY_NO));
                request.setAttribute(RequestIds.RISK_ID, inputRecord.getStringValue(RequestIds.RISK_ID));
            }
        } catch (ValidationException e) {
            handleValidationException(e, request);
        } catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to find policy types in the create policy page.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "findAllPolicyType", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "create"
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
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "create", new Object[]{mapping, form, request, response});
        try {
            PolicyManager policyMgr = getPolicyManager();

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Load the list of values
            loadListOfValues(request, form);

            // get input
            Record inputRecord = addRegionalOfficeVisibility(request, getInputRecord(request));

            // reset term expiration date in case we got multiple values
            Field field = inputRecord.getField(CreatePolicyFields.TERM_EFFECTIVE_TO_DATE);
            if (field.getClass() == MultiValueField.class) {
                String termExpirationDate = ((MultiValueField) field).getStringValue(0);
                inputRecord.setFieldValue(CreatePolicyFields.TERM_EFFECTIVE_TO_DATE, termExpirationDate);
            }

            try {
                Record tempRecord = new Record();

                //If the request has valid save token, then proceed with save; if not forward to load page.
                if(hasValidSaveToken(request)) {
                    // create policy and setup forward parameters
                    String policyNo = policyMgr.createPolicy(inputRecord, true);
                    tempRecord.setFieldValue(RequestIds.POLICY_NO, policyNo);
                }

                // Clear the policy list in user session
                UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_LIST);

                writeAjaxXmlResponse(response, tempRecord, true);
            }
            catch (ValidationException e) {
                handleValidationExceptionForAjax(e, response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to create policy.", e, response);
        }

        // done
        ActionForward af = mapping.findForward(null);
        l.exiting(getClass().getName(), "create", af);
        return af;
    }

    /**
     * This method is called to get initial values for copy new policy from risk.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForCopyNewPolicy(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCopyNewPolicy", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getRiskManager().getInitialValuesForCopyNewPolicy(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            request.setAttribute(RequestIds.IS_FROM_COPY_NEW, YesNoFlag.Y);
            request.setAttribute(RequestIds.POLICY_NO, inputRecord.getStringValue(RequestIds.POLICY_NO));
            request.setAttribute(RequestIds.RISK_ID, inputRecord.getStringValue(RequestIds.RISK_ID));
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the create policy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitialValuesForCopyNewPolicy", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "copyNewPolicyFromRisk"
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
    public ActionForward copyNewPolicyFromRisk(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "copyNewPolicyFromRisk", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = addRegionalOfficeVisibility(request, getInputRecord(request));

            Record tempRecord = new Record();

            //If the request has valid save token, then proceed with save; if not forward to load page.
            if(hasValidSaveToken(request)) {

                PolicyHeader policyHeader = getPolicyHeader(request, true);

                //Delete renewalWIP if under renewal wip transaction and PM_CONVERT_DEL_WIP includes
                //current policy type.
                if (policyHeader.getScreenModeCode().isRenewWIP()) {
                    int index = SysParmProvider.getInstance().getSysParm(
                        SysParmIds.PM_CONVERT_DEL_WIP, "").indexOf(policyHeader.getPolicyTypeCode());
                    if (index > -1) {
                        getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
                    }
                }

                PolicyManager policyManager = getPolicyManager();
                //validation fields for copy new policy
                policyManager.validatePolicyForCreate(inputRecord);
                //create additional parms retrieval
                CreatePolicyFields.setAddlParms(inputRecord, policyManager.buildAddlParmsField(inputRecord));
                CreatePolicyFields.setPolicyId(inputRecord, policyHeader.getPolicyId());
                CreatePolicyFields.setRiskBaseId(inputRecord, policyHeader.getRiskHeader().getRiskBaseRecordId());
                CreatePolicyFields.setPolicyCycle(inputRecord, inputRecord.getStringValue(CreatePolicyFields.POLICY_CYCLE_CODE));
                PolicyFields.setPolicyPhaseCode(inputRecord, policyHeader.getPolPhaseCode());
                //get parallel policy No
                Record polNoRec = policyManager.getParallelPolicyNo(inputRecord);

                //copy new policy from risk
                String policyNo = getRiskManager().copyNewPolicyFromRisk(polNoRec, inputRecord);
                tempRecord.setFieldValue(RequestIds.POLICY_NO, policyNo);
            }

            writeAjaxXmlResponse(response, tempRecord, true);

        } catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy new policy from risk.", e, response);
        }

        ActionForward af = mapping.findForward(null);
        l.exiting(getClass().getName(), "copyNewPolicyFromRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public CreatePolicyAction() {}

    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    // private memember variables...
    private PolicyManager policyManager;
}
