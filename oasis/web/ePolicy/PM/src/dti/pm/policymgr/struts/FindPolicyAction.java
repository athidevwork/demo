package dti.pm.policymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import dti.pm.policymgr.userviewmgr.UserViewManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * action class for find policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/23/2007       sxm         Added logic to store/clear the policy list in user session
 * 09/10/2010       wfu         111776: Added js messages for search criteria.
 * 05/04/2012       bhong       129528 - Added processQuestionnaireRequest
 * 07/17/2013       adeng       144614 - Added js message.
 * 09/24/2013       xnie        148240 - Modified findAllPolicy() to
 *                                       a. Remove rs.getSize() == 1 condition
 *                                          which means policy has only one term so that system just displays
 *                                          policy latest term information when policy has multiple terms.
 *                                       b. Add code to display no data found error message when there is no
 *                                          any policy term data found.
 * 11/06/2014       kxiang      158411 - Modified findAllPolicy() to display the specific term if policyTermHistoryId
 *                                       contained in the request.
 * 05/29/2015       cv          #163055 - Added saveSearchCriteria() to save the search criteria.
 *                                        Added returnToList() to restore to the last search criteria.
 * 03/25/2016       eyin         170323 - 1. Modify method findAllPolicy to set attribute 'returnToList' as 'Y' only
 *                                        when process equals 'returnToList'.
 *                                        2. Add method updatePolicyListSession to update policyList in session.
 * 09/18/2016       lzhang       179121 - Modified findAllPolicy: only when system matches against one
 *                                        policy/quote with entered full policy no, system goes to policy detail.
 * 06/13/2018       wrong        192557 - Modified deleteUserView() to call hasValidSaveToken() to be used
 *                                        for CSRFInterceptor.
 * 09/14/2018       cesar        195306 - added saveToken(request) to findAllPolicy() to implement CSRF requirement.
 * ---------------------------------------------------
 */

public class FindPolicyAction extends PMBaseAction {

    /**
     * Prepare for and display the Home page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return displaySearchCriteria(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "displaySearchCriteria"
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
    public ActionForward displaySearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "displaySearchCriteria");

        request.setAttribute(RequestIds.IS_NEW_VALUE, "Y");

        return findAllPolicy(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "findAllPolicy"
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

    public ActionForward findAllPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "findAllPolicy", new Object[]{mapping, form, request, response});
        String forwardString = "success";
        String process = (String) request.getAttribute(RequestIds.PROCESS);

        // if the method is called from displaySearchCriteria method
        // the process attribute has been set to displaySearchCriteria already.
        // we set the process to findAllPolicy only when it is not set
        if (process == null) {
            process = "findAllPolicy";
            request.setAttribute(RequestIds.PROCESS, process);
        }
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            RecordSet rs;
            Record inputRecord = getInputRecord(request);
            boolean isGlobalSearch = inputRecord.getBooleanValue("isGlobalSearch", false).booleanValue();
            if (process.equalsIgnoreCase("findAllPolicy")) {
                // Clear the policy list in user session
                UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_LIST);

                //Save the search criteria to session. It will be re-used when user clicks on back to case list
                saveSearchCriteria(inputRecord);

                // Load the Policies/quotes by caling policyMgr..
                rs = getPolicyManager().findAllPolicy(inputRecord);
                if ((rs != null) && (rs.getSize() != 0)) {
                    String policyTermHistoryId = request.getParameter(PolicyHeaderFields.POLICY_TERM_HISTORY_ID);
                    if(StringUtils.isBlank(policyTermHistoryId)){
                        policyTermHistoryId = PolicyHeaderFields.getPolicyTermHistoryId(rs.getFirstRecord());
                    }
                    request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, String.valueOf(rs.getSize()));
                    request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, rs.getFirstRecord().getFieldValue("maxRows"));

                    boolean forwardToFindPol = false;
                    String policyNoCriteria = PolicyHeaderFields.getPolicyNoCriteria(inputRecord);
                    Iterator rsIter = rs.getRecords();
                    while (rsIter.hasNext()) {
                        Record r = (Record) rsIter.next();
                        String policyNo = PolicyHeaderFields.getPolicyNo(r);
                        if (!policyNoCriteria.equals(policyNo)){
                            forwardToFindPol = true;
                            break;
                        }
                    }
                    if (isGlobalSearch && !forwardToFindPol) {
                        forwardString = "globalSearchSuccess";
                        setForwardParameter(request, RequestStorageIds.POLICY_NO, PolicyHeaderFields.getPolicyNo(rs.getFirstRecord()));
                        setForwardParameter(request, RequestStorageIds.POLICY_TERM_HISTORY_ID, policyTermHistoryId);
                    }
                }
                else {
                    request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, "0");
                    request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, "0");
                    // if no data found, add error message.
                    MessageManager.getInstance().addErrorMessage("pm.findPolicy.findPolicyList.noDataFound.error");
                }
            }
            else {
                rs = new RecordSet();
                request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, "0");
                request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, "0");
            }

            setDataBean(request, rs);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            addJsMessages();
            loadGridHeader(request);

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the find policy page.", e, request, mapping);
        }

        l.exiting(getClass().getName(), "findAllPolicy", forwardString);
        return mapping.findForward(forwardString);
    }

    /**
     * load user view info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadUserView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadUserView", new Object[]{mapping, form, request, response});
        String forwardString = "success";
        Record output = null;
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            RecordSet rs;
            Record inputRecord = getInputRecord(request);
            output = getUserViewManager().loadUserView(inputRecord).getFirstRecord();

            rs = new RecordSet();
            request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, "0");
            request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, "0");
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            addJsMessages();
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load user view.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadUserView", af);
        return af;
    }

    /**
     * delete user view info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteUserView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "deleteUserView", new Object[]{mapping, form, request, response});
        String forwardString = "loadInitialPage";
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);
                Record inputRecord = getInputRecord(request);
                getUserViewManager().deleteUserView(inputRecord);
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to delete user view.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "deleteUserView", af);
        return af;
    }

    /**
     * save user view info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveUserView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveUserView", new Object[]{mapping, form, request, response});
        String forwardString = "success";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            String userViewId = "";
//             String existedUserViewId = inputRecord.getStringValue("existedUserViewId");
//             inputRecord.setFieldValue("pmUserViewId", existedUserViewId);
            //save user view
            if (hasValidSaveToken(request)) {
                userViewId = getUserViewManager().saveUserView(inputRecord);
                UserSessionManager.getInstance().getUserSession().set("pmUserViewId", userViewId);
            }
            else {
                userViewId = (String) UserSessionManager.getInstance().getUserSession().get("pmUserViewId");
            }
            RecordSet rs = new RecordSet();
            if (!StringUtils.isBlank(request.getParameter("txtXML"))) {
                rs = getInputRecordSet(request);
            }
            if (rs == null || rs.getSize() == 0) {
                request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, "0");
                request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, "0");
            }
            else {
                request.setAttribute(RequestIds.PROCESS, "saveUserView");
                request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, String.valueOf(rs.getSize()));
                request.setAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS, rs.getFirstRecord().getFieldValue("maxRows"));
            }
            setDataBean(request, rs);
            request.setAttribute("pmUserViewId", userViewId);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            addJsMessages();
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save user view.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveUserView", af);
        return af;
    }

    /**
     * Process and generate questionnaire for selected policies
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processQuestionnaireRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processQuestionnaireRequest", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            RecordSet inputRecords = getInputRecordSet(request);
            getApplicationManager().processQuestionnaireRequest(inputRecords); 

            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to generate questionnaire.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processQuestionnaireRequest", af);
        }
        return af;
    }

    // constructor
    public FindPolicyAction() {
        super();
    }

    //verify config
    public void verifyConfig() {       
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }
        if (getUserViewManager() == null) {
            throw new ConfigurationException("The required property 'userViewManager' is missing.");
        }
    }

    // getters..
    public UserViewManager getUserViewManager() {
        return m_userViewManager;
    }

    public void setUserViewManager(UserViewManager userViewManager) {
        m_userViewManager = userViewManager;
    }

    public ApplicationManager getApplicationManager() {
        return m_applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        m_applicationManager = applicationManager;
    }

    //transfer null object to String ""
    private Object nullToBlank(Object obj) {
        if (obj == null) {
            return "";
        }
        else {
            return obj;
        }
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainUserView.create.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainUserView.overwrite.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainUserView.lengthCheck.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainUserView.delete.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainUserView.blank.warning");
        MessageManager.getInstance().addJsMessage("pm.findPolicy.processMailing.noSelection.error");
        MessageManager.getInstance().addJsMessage("pm.findPolicy.mutuallyExclusive.conditions.error");
        MessageManager.getInstance().addJsMessage("pm.findPolicy.invalidEntityName.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicy.search.moreCriteria");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicy.search.lessCriteria");
        MessageManager.getInstance().addJsMessage("pm.findPolicy.processQuestionnaire.noSelection.error");
        MessageManager.getInstance().addJsMessage("pm.findPolicy.processQuestionnaire.done.info");
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRate.noSelection.error");
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRate.onDemandNonNumber.error");
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRate.maxOnDemandNumber.error");
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRateResult.batchSuccess.msg");
    }

    public void saveSearchCriteria(Record inputRecord) {
        UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_NO_SEARCH_CRITERIA);
        UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_NO_SEARCH_CRITERIA, inputRecord);
    }

    public ActionForward returnToList(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        try {
            Record inputRecord = (Record) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.POLICY_NO_SEARCH_CRITERIA);
            RequestStorageManager.getInstance().set("inputRecord", inputRecord);
            request.setAttribute(m_returnToList, "Y");
            return mapping.findForward("loadSearchCriteria");
        } catch (Exception e) {
            return mapping.findForward("loadInitialPage");
        }
    }

    public ActionForward updatePolicyListSession(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyListSession", new Object[]{mapping, form, request, response});
        }

        try {
            Record inputRecord = getInputRecord(request);
            String policyIdList = inputRecord.getStringValue(m_policyList, "");
            String policyTermHistoryIdList = inputRecord.getStringValue(m_policyTermHistoryIdList, "");

            PolicyList policyList = new PolicyList();
            String[] policyIdArr = policyIdList.split(",");
            String[] policyTermHistoryIdArr = policyTermHistoryIdList.split(",");
            if (policyIdArr.length == policyTermHistoryIdArr.length){
                for(int i=0; i<policyIdArr.length; i++){
                    PolicyListElement policyListElement = new PolicyListElement();
                    policyListElement.setPolicyNo(policyIdArr[i]);
                    policyListElement.setPolicyTermHistoryId(policyTermHistoryIdArr[i]);
                    policyList.add(policyListElement);
                }
            }

            UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_LIST, policyList);

            Record result = new Record();
            writeAjaxXmlResponse(response, result);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to update the policyList in Session.", e, response);
        }

        l.exiting(getClass().getName(), "updatePolicyListSession");
        return null;
    }

    private UserViewManager m_userViewManager;
    private ApplicationManager m_applicationManager;
    private String m_policyList = "policyList";
    private String m_policyTermHistoryIdList = "policyTermHistoryIdList";
    private String m_returnToList = "returnToList";
}


