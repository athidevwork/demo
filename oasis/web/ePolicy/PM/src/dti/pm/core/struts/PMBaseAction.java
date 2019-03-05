package dti.pm.core.struts;

import dti.cs.activityhistorymgr.ActivityHistoryManager;
import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.cachemgr.UserCacheManager;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.filter.CharacterEncodingFilter;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.XMLRecordSetMapper;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.BaseAction;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisGrid;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.tags.WebLayer;
import dti.oasis.tags.XMLGridHeader;
import dti.oasis.tags.XMLGridHeaderDOMLoader;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.CollectionUtils;
import dti.oasis.util.DefaultPageDefLoadProcessor;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LocaleUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.cache.PolicyOasisFieldsCache;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.entitlementmgr.PolicyDataSecurityPageDefLoadProcessor;
import dti.pm.policyattributesmgr.PolicyAttributesFactory;
import dti.pm.policymgr.CreatePolicyFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.quotemgr.QuoteFields;
import dti.pm.quotemgr.QuoteManager;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.riskmgr.RiskManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.impl.TransactionManagerImpl;
import dti.pm.workflowmgr.WorkFlowFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains Stuts Action related methods to aid the development of PM Action classes.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 12, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/16/2007       sxm         Removed hidden field policyLockId from Policy Header
 * 08/23/2007       sxm         Fixed logging error in execute() - getInputRecordSet(request)) failed
 *                              in Coverage Page since it uses XXXtxtXML instead.
 * 08/27/2007       sxm         Modified getPolicyHeader to allow loading risk/coverage header w/o riskId/coverageId
 * 08/29/2007       sxm         Modified writeAjaxResponse to handle related field/row IDs
 * 10/02/2007       sxm         Do not set policy lock message in request
 * 04/01/2008       joe         Refactor securePage() - move logic of calling ActionHelper.securePage() and
 *                              setting up beans for every field id into BaseAction.
 * 09/30/2008       sxm         enabled excludedFieldsList when set a page to readonly
 * 10/08/2009       mgitelm     95437: Added the ability to strip off the prefix of the fieldId to match the data column
 *                              (to support the old style of defining fieldIs in eClaims/eCIS) on the Action Class level
 * 12/04/2009       James       Move common methods to BaseAction
 * 01/18/2010       James       Issue#101408 Record user activity
 * 03/09/2011       Dzhang      Issue#94232 Added setAllDataBean() & loadAllGridHeader() & getAllInputRecordSet() to support multi-grid.
 * 06/15/2011       syang       111676 - Added getExcelCSVData() to get data to export excel.
 * 08/10/2011       Joe         Issue 123893 - Modified handleError()/handleErrorPopup()/handleErrorIFrame()
 *                              to make the error message more user friendly.
 * 08/19/2011       fcb         124144 - transaction info added to the logging in execute() method.
 * 11/21/2011       fcb         126781 - set endQuoteId to empty string when it is equal to the string "null".
 * 07/24/2012       awu         129250 - Added handleErrorForAutoSaveAjax().
 * 08/09/2012       tcheng      135952 - Modified getPolicyHeader to added a condition for setting coverageHeader as null when riskHeader is null
 * 12/27/2012       tcheng      139862 - Added loadWarningMessage() to pop up warning message.
 * 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
 * 07/25/2014       awu         152034 - 1). Modified execute to set the policy header to session for Rating transaction.
 *                                       2). Modified getPolicyHeader to set coverageHeader and currentSelectedMap to null
 *                                           once risk header was set to null.
 * 07/28/2014       xnie        155378 - Added resetDisplayIndicatorForDeletedRs() for case when save record error,
 *                                       system will reset display indicator to 'Y' for official record which was closed
 *                                       by temp record.
 * 10/06/2014        awu         157694 - Modified getExcelCSVData to get the data from records by column name instead of index.
 * 12/04/2014        awu         159187 - Modified handleErrorForAutoSaveAjax to report the error message to page.
 * 01/26/2014        fcb         159897 - Added logic to determine if the current process is a monitor process (refresh
 *                                        lock thread or long running transaction thread).
 * 03/13/2015        awu         161778 - 1. Modified loadPolicyHeader to remove the logic of setting the parameter isSamePolicyHeaderB
 *                                        2. Modified loadPolicyHeader to set the new parameter isSamePolicyB.
 * 06/17/2015        wdang       163772 - Modified getExcelCSVData to use text format rather than numeric format even if some texts start with $ or ($.
 * 09/22/2015        Elvin       Issue 160360: add Preview functionality
 * 11/04/2015        tzeng       165790 - Modified loadPolicyHeader() to set policy phase code from request.
 * 10/19/2015        eyin        166440 - Modified loadPolicyHeader(), replacing the logic that throws an exception with the logic to log a warning
 *                                        in the java log messages when unLockPreviouslyHeldLock failed.
 * 10/19/2015        eyin        167171 - 1) Modified securePage(), Add logic to make sure fields is not null before deep cloning.
 *                                        2) Modified processExcelExport(), Add logic to remove spaces when dispType is not null.
 *                                        3) Modified resetDisplayIndicatorForDeletedRs(), Add logic to make sure inputRecordSet is not null.
 * 12/23/2015        kxiang      168269 - Modified loadPolicyHeader to get endQuoteId & policyViewMode from work flow.
 * 01/28/2016        wdang       169024 - Added addSaveMessages().
 * 06/23/2016        tzeng       167531 - Modified addSaveMessages() to add JS and prompt type message process.
 * 08/26/2016        wdang       168534 - Enable select Renewal Quotes from policy in view mode drop down and vice versa.
 * 10/17/2016        xnie        180447 - Modified execute() to include 'WIP' to view mode option when wip transaction
 *                                        exists and current term is transaction initial term.
 * 02/22/2017        tzeng       168385 - Modified execute() to reset requested transaction time after each action calling.
 * 02/28/2017        mlm         183387 - Refactored to handle preview for long running transactions.
 * 04/26/2017        mlm         184827 - Refactored to initialize previewRequest in policy header correctly.
 * 06/09/2017        xnie        185775 - Reverted 180447 fix.
 * 07/24/2017        eyin        185377 - Modified execute(), set noLoadingDiv value as request attribute if exists.
 * 11/08/2017        mlm         189535 - Refactored to remove the save indicator added by hasValidToken in handlePreviewRequest
 * 12/04/2017        lzhang      190020 - Add updatePolicyHeader() method
 * 03/07/2018        cesar       189605 - call BaseAction.saveToken() to be used for CSRFInterceptor
 * 04/17/2018        cesar       192691 - update use session token when an Ajax request is expecting confirmation
 * 07/11/2018        cesar       193446 - CSRF implementation.
 * ---------------------------------------------------
 */

public abstract class PMBaseAction extends BaseAction {

    /**
     * Method that returns the action forward mapping for the request after loading policy header information for the
     * provided policy attributes (via request parameter).
     * <p/>
     * If policy number is not provided, the process will automatically unlocks any lock held on prior policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "execute");

        ActionForward af = null;
        Record inputRecord = getInputRecord(request);

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "execute", "Request URI:" + request.getRequestURI() + "', process: '" + request.getParameter("process") + "', parameters as Input Record: " + inputRecord);
            String txtXML = request.getParameter("txtXML");
            if (!StringUtils.isBlank(txtXML)) {
                l.logp(Level.FINE, getClass().getName(), "execute", "txtXML as Input RecordSet: " + getInputRecordSet(request));
            }
        }

        boolean isAjaxRequest = isAjaxRequest(request);
        try {
            String policyNo = "";

            if (!StringUtils.isBlank(request.getParameter(RequestIds.POLICY_NO))) {
                policyNo = request.getParameter(RequestIds.POLICY_NO);
            }
            else {
                if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_NO)) {
                    policyNo = (String) RequestStorageManager.getInstance().get(RequestStorageIds.POLICY_NO);
                }
            }

            boolean requestDueToFailure = false;
            if (MessageManager.getInstance().hasErrorMessages()) {
                requestDueToFailure = true;
            }

            // Load the Policy Header even if the policyNo is blank so that if the previous policy was locked, it will be unlocked.
            PolicyHeader policyHeader = null;
            try {

                //Initialize the save token. Don't do this for Ajax requests because the save token is already set for the page.
                if (!isAjaxRequest) {
                    initializeLoadRequestForSave(request, getTokenConstant());
                    if (inputRecord.hasField("noLoadingDiv")
                        && YesNoFlag.getInstance(inputRecord.getStringValue("noLoadingDiv")).booleanValue()) {
                        request.setAttribute("noLoadingDiv", true);
                    }
                }

                RequestStorageManager.getInstance().set(RequestIds.IS_PREVIEW_REQUEST,
                        (YesNoFlag.getInstance((String) inputRecord.getFieldValue(RequestIds.IS_PREVIEW_REQUEST, "N")).booleanValue()));

                policyHeader = loadPolicyHeader(request);

                handlePreviewRequest(request, policyHeader);

                if (inputRecord.getBooleanValue("processRatingB", false).booleanValue()) {
                    UserSessionManager.getInstance().getUserSession().set("PreviousPolicyHeader", policyHeader);
                }

                // Load the PolicyHeader Oasis Fields for the included policyHeader.jsp.
                if (policyHeader != null) {
                    //For access trail
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME, "POLICY_TERM_HISTORY");
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO, policyHeader.getPolicyNo());
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK, policyHeader.getPolicyTermHistoryId());

                    // If the Policy Header is loaded:
                    // 1.  Load the Policy Header specific OasisFields and create field/value beans
                    securePage(request, form, true, true);

                    // record for header fields
                    Record record = (Record) request.getAttribute(RequestIds.NON_GRID_FIELDS_RECORD);
                    request.setAttribute(RequestIds.HEADER_FIELDS_RECORD, record);
                    request.removeAttribute(RequestIds.NON_GRID_FIELDS_RECORD);

                    OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
                    Term currentTerm = policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId());
                    boolean isWipAvailable = currentTerm.isWipExists();
                    boolean isOfficialAailable = currentTerm.isOfficialExists();
                    boolean isEndorsementQuoteAailable = currentTerm.isEndorsementQuoteExists();
                    boolean isRenewalQuoteAailable = currentTerm.isRenewalQuoteExists();

                    OasisFormField field = (OasisFormField) fieldsMap.get("policyViewMode");
                    String lovSql = "[NO_ADD_SELECT_OPTION]LIST:";
                    if (isOfficialAailable) {
                        lovSql = lovSql + "OFFICIAL,Official,";
                    }
                    if (isWipAvailable) {
                        lovSql = lovSql + "WIP,WIP,";
                    }
                    if (isEndorsementQuoteAailable) {
                        //get Endorsment Quote list
                        RecordSet rs = getPolicyManager().loadAllEndorsementQuote(policyHeader);
                        Iterator itor = rs.getRecords();
                        while (itor.hasNext()) {
                            Record endQuoteRecord = (Record) itor.next();
                            String endQuoteId = endQuoteRecord.getStringValue("endorsementQuoteId");
                            lovSql = lovSql + "ENDQUOTE,Endorsement Quote: " + endQuoteId + ",";
                        }

                    }
                    else if (isRenewalQuoteAailable) {
                        //get renewal quote list
                        RecordSet rs = getPolicyManager().loadAllEndorsementQuote(policyHeader);
                        Iterator itor = rs.getRecords();
                        while (itor.hasNext()) {
                            Record endQuoteRecord = (Record) itor.next();
                            String endQuoteId = endQuoteRecord.getStringValue("endorsementQuoteId");
                            lovSql = lovSql + "ENDQUOTE,Renewal Quote: " + endQuoteId + ",";
                        }
                    }

                    if (PolicyAttributesFactory.getInstance().isDisplayQuoteInViewModeEnable(
                        policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(),
                        policyHeader.getPolicyCycleCode(),
                        policyHeader.getQuoteCycleCode(),
                        policyHeader.getLastTransactionInfo().getTransactionTypeCode(),
                        policyHeader.getRecordMode(),
                        policyHeader.getPolicyStatus())) {
                        Record r = policyHeader.toRecord();
                        r.setFieldValue(QuoteFields.SELECT_MODE,
                            QuoteFields.joinSelectMode(QuoteFields.EXCL_SELF, QuoteFields.TERM_SENS, QuoteFields.EXCL_NB,
                                QuoteFields.EXCL_INVALID, QuoteFields.EXCL_ACCEPTED));
                        RecordSet rs = getQuoteManager().loadQuoteVersions(r);

                        for (int i = 0; i < rs.getRecordList().size(); i ++ ) {
                            r = rs.getRecordList().get(i);
                            String text = PolicyAttributesFactory.getInstance().getDisplayQuoteInViewModeText(
                                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate(),
                                PolicyFields.getPolicyCycleCode(r),
                                PolicyFields.getQuoteCycleCode(r),
                                r);
                            if (text != null) {
                                lovSql = lovSql + "QUOTE:"+PolicyHeaderFields.getPolicyNo(r) +
                                    "," + text + ",";
                            }
                        }
                    }
                    field.setLovSql(lovSql);
                    // 2.  Load any list of values for the Policy Header
                    loadListOfValues(request, form);
                    // 3.  Reset the map attribute to a specific policyHeaderFieldsMap name
                    request.setAttribute(KEY_PH_FIELDS, fieldsMap);
                    request.setAttribute(IOasisAction.KEY_HEADER_PAGE_FIELDS, fieldsMap);
                    //set selected endquote

                    //TODO: see workaround below
//                    request.removeAttribute(IOasisAction.KEY_FIELDS);
//                    if(policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()){
//                    request.setAttribute("policyViewMode","ENDQUOTE:"+policyHeader.getLastTransactionId());
//                }
                }
            }
            catch (Exception e) {
                l.logp(Level.WARNING, getClass().getName(), "execute", "Failed to load the policy information for policyNo '" + policyNo + "'", e);
            }
            final StringBuffer requestInformation = new StringBuffer();
            requestInformation.append("URI:'").append(request.getRequestURI()).append("', ");
            requestInformation.append("process:'").append(request.getParameter(RequestIds.PROCESS)).append("', ");
            requestInformation.append("UserId:'").append(UserSessionManager.getInstance().getUserSession().getUserId()).append("', ");
            if (!StringUtils.isBlank(policyNo)) {
                if (policyHeader != null) {

                    if (l.isLoggable(Level.INFO)) {
                        if (!mapping.getPath().endsWith("maintainLock") || l.isLoggable(Level.FINE)) {
                            requestInformation.append("Policy No:'").append(policyHeader.getPolicyNo()).append("', ");
                            requestInformation.append("Policy Id:'").append(policyHeader.getPolicyId()).append("', ");
                            requestInformation.append("Policy Type:'").append(policyHeader.getPolicyTypeCode()).append("', ");
                            requestInformation.append("View Mode:'").append(policyHeader.getPolicyIdentifier().getPolicyViewMode()).append("', ");
                            requestInformation.append("Lock Id:'").append(policyHeader.getPolicyIdentifier().getPolicyLockId()).append("', ");
                            requestInformation.append("IsSelectedTermInWIP:'").append(policyHeader.isWipB()).append("', ");
                            requestInformation.append("Own Lock?").append(policyHeader.getPolicyIdentifier().ownLock()).append("', ");
                            requestInformation.append("Policy History Term Id:'").append(policyHeader.getPolicyTermHistoryId()).append("', ");
                            requestInformation.append("Term Base Id:'").append(policyHeader.getTermBaseRecordId()).append("', ");
                            requestInformation.append("Effective From Date:'").append(policyHeader.getTermEffectiveFromDate()).append("', ");
                            requestInformation.append("Effective To Date:'").append(policyHeader.getTermEffectiveToDate()).append("', ");
                            requestInformation.append("Risk Id:'").append(inputRecord.getStringValue("riskId", "")).append("', ");
                            requestInformation.append("Coverage Id:'").append(inputRecord.getStringValue("coverageId", "")).append("', ");
                            requestInformation.append("Latest Transaction Id:'").append(policyHeader.getLastTransactionInfo().getTransactionLogId()).append("' ");

                            l.logp(Level.INFO, getClass().getName(), "execute", "Request Information: " + requestInformation.toString());
                        }

                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "execute", "policyHeader = " + policyHeader);
                        }

                    }
                    af = super.execute(mapping, form, request, response);
                }
                else {
                    if (requestDueToFailure) {
                        l.logp(Level.INFO, getClass().getName(), "execute", "Request Information: " + requestInformation.toString());
                        // Don't forward to failure page if this request is already the result of a failure.
                        af = super.execute(mapping, form, request, response);
                    }
                    else {
                        l.logp(Level.INFO, getClass().getName(), "execute", "Request Information: " + requestInformation.toString());
                        // Failed to load the PolicyHeader for the requested PolicyNo.
                        l.logp(Level.WARNING, getClass().getName(), "execute", "Unable to load the policy information for policyNo '" + policyNo + "'");
                        MessageManager.getInstance().addErrorMessage("pm.findpolicy.noData.msg", new Object[]{policyNo});
                        if (isAjaxRequest) {
                            // If  this is an Ajax request, write the error
                            writeAjaxResponse(response, new Record(), true);
                        }
                        else {
                            // Otherwise, return the global forward to the find policy page
                            af = mapping.findForward("loadPolicyFailure");
                        }
                    }
                }
            }
            else {
                requestInformation.append("Policy No Is Not Provided");
                l.logp(Level.INFO, getClass().getName(), "execute", "Request Information: " + requestInformation.toString());
                af = super.execute(mapping, form, request, response);
            }
            // Reset notification transaction time
            OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
            if (oasisUser.hasRequestedTransactionTime()) {
                oasisUser.setRequestedTransactionTime(null);
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error loading policy header information.", e);
            l.throwing(getClass().getName(), "execute", ae);
            String forwardString = handleError(AppException.UNEXPECTED_ERROR, "Error loading policy header information.", e, request, mapping);
            af = mapping.findForward(forwardString);
        }

        // Set all fields in fieldsMap to readonly if there's "readOnly" attribute is set to "Y" in request.
        // The fieldId of this indicator field is defined in EntitlementFields class.
        // For some unkown reason, sometime the readOnly attribute is set as String
        // Below codes are from safe perspective
        Object oReadonly = request.getAttribute(EntitlementFields.READ_ONLY);
        boolean isReadonly = false;
        if (oReadonly != null) {
            if (oReadonly instanceof String) {
                isReadonly = YesNoFlag.getInstance((String) oReadonly).booleanValue();
            }
            else if (oReadonly instanceof YesNoFlag) {
                isReadonly = ((YesNoFlag) oReadonly).booleanValue();
            }
        }

        Object oHanldeLayer = request.getAttribute(EntitlementFields.HANDLE_LAYER);
        boolean isHanldeLayer = true;
        if (oHanldeLayer != null) {
            if (oHanldeLayer instanceof String) {
                isHanldeLayer = YesNoFlag.getInstance((String) oHanldeLayer).booleanValue();
            }
            else if (oHanldeLayer instanceof YesNoFlag) {
                isHanldeLayer = ((YesNoFlag) oHanldeLayer).booleanValue();
            }
        }

        if (isReadonly) {
            String [] excludedFieldsList = null;
            if (hasAlwaysEnabledFieldIds())
                excludedFieldsList = getAlwaysEnabledFieldIds().split(",");
            setFieldsToReadOnly(request, excludedFieldsList, isHanldeLayer, true);
        }

        if (isSaveInProgress()) {
            if (isAjaxRequest && MessageManager.getInstance().hasConfirmationPrompts()) {
                // The Ajax request was a save request, and did not complete because it requires confirmations.
                // Remove the save in-progress session attribute to indicate that the save process has been exited.
                updateSessionToken(request,getTokenConstant());
                removeSaveInProgressIndicator();
            }
            else if (request.getAttribute(IOasisAction.KEY_ERROR) == null &&
                request.getAttribute(IOasisAction.KEY_VALIDATION_ERROR) == null &&
                MessageManager.getInstance().hasErrorMessages() == false
                ) {

                saveProcessSucceeded(request, getTokenConstant());
            }
            else {
                updateSessionToken(request, getTokenConstant());
                saveProcessFailed(request);
            }
        } else {
            //make sure a new token gets generated when loading a page. this will avoid calling the saveToken() on all the action classes initial load.
            //the IS_STRUTS_LOAD_ACTION is to make sure that it is loading for the first time.
            if (!isAjaxRequest(request) && request.getAttribute(RequestStorageIds.IS_STRUTS_LOAD_ACTION) == null) {
                //saveToken(request, getTokenConstant());
                updateCSRFTokenInForwardParameter(request);
            }
        }

        request.getSession().removeAttribute(RequestStorageIds.IS_STRUTS_LOAD_ACTION);

        request.removeAttribute(RequestStorageIds.IS_STRUTS_LOAD_ACTION);
        l.exiting(getClass().getName(), "execute", af);
        return af;
    }

    protected void handlePreviewRequest(HttpServletRequest request, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "handlePreviewRequest", new Object[]{request, policyHeader});
        if (policyHeader != null) {
            // start with the assumption that this is not a PREVIEW request.
            policyHeader.setPreviewRequest(false);
            // Handle AJAX requests submitted for workflow call backs (For large policies)
            boolean isAjaxReq = isAjaxRequest(request);
            // Ensure the preview doesn't kick-off, if the page is refreshed by end user's F5 for non ajax requests
            boolean isValidToken = false;
            if (!isAjaxReq) {
                boolean isSaveInProgress = isSaveInProgress();
                isValidToken = hasValidSaveToken(request);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "execute", "isSaveInProgress?" + String.valueOf(isSaveInProgress));
                }
                // Issue 189535 - If this request is not part of save request, remove the save indicator added by hasValidSaveToken() call.
                if (!isSaveInProgress) {
                    removeSaveInProgressIndicator();
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "execute", "Removed the save indicator set by hasValidSaveToken call.");
                    }
                }
            }
            // Setup the PREVIEW indicator only for AJAX calls (large policies) or if the request has a valid token (for small) policies.
            // Checking for valid token will ensure the preview doesn't kick-off, if the page is refreshed by end user's F5 for small policies requests
            if (isAjaxReq || isValidToken) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "execute", "About to check for PREVIEW Request existence in the request.");
                }
                if (RequestStorageManager.getInstance().has(RequestIds.IS_PREVIEW_REQUEST)) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "handlePreviewRequest", "Adding PREVIEW Request indicator into User Session and Policy Header");
                    }
                    policyHeader.setPreviewRequest((Boolean) RequestStorageManager.getInstance().get(RequestIds.IS_PREVIEW_REQUEST));
                } else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "handlePreviewRequest", "This request is not part of the PREVIEW Request.");
                    }
                }
            }
            if (!isAjaxReq) {
                // If the save process is completed successfully and a preview indicator is saved into user session (which will occur only for preview requests),
                // then clean-up the session variables and set up the preview indicator in the policy header so that the preview request can start from the browser.
                if (UserSessionManager.getInstance().getUserSession().has(TransactionManagerImpl.IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY) &&
                        UserSessionManager.getInstance().getUserSession().has(RequestIds.IS_PREVIEW_REQUEST)) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "handlePreviewRequest", "About to remove PREVIEW Request indicator from User Session");
                    }
                    policyHeader.setPreviewRequest(((YesNoFlag) UserSessionManager.getInstance().getUserSession().get(RequestIds.IS_PREVIEW_REQUEST)).booleanValue());
                    UserSessionManager.getInstance().getUserSession().remove(RequestIds.IS_PREVIEW_REQUEST);
                    UserSessionManager.getInstance().getUserSession().remove(TransactionManagerImpl.IS_SAVE_REQUEST_COMPLETED_SUCCESSFULLY);

                }
            }
        }
        l.exiting(getClass().getName(), "handlePreviewRequest", policyHeader);
        return;
    }

    /**
     * Method to check if a process is a monitor process of a long running transaction or for lock refresh
     *
     * @param process
     * @return
     */
    protected boolean isMonitorPolicyProcess(String process) {
        Logger l = LogUtils.enterLog(getClass(), "isMonitorPolicyProcess", new Object[]{process});
        boolean isMonitorProcess = false;
        if (process != null &&
            (process.equalsIgnoreCase("monitorLongRunningTransaction") ||
                process.equalsIgnoreCase("continueMonitoring") ||
                process.equalsIgnoreCase("refreshPolicyLock"))) {
            isMonitorProcess = true;
        }
        l.exiting(getClass().getName(), "isMonitorPolicyProcess", isMonitorProcess);

        return isMonitorProcess;
    }

    /**
     * Secure that the user has access to the web page.
     * This method defaults to loading the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     */
    public void securePage(HttpServletRequest request, ActionForm form) throws Exception {
        securePage(request, form, true, false);
    }

    /**
     * Secure that the user has access to the web page.
     * Optionally load the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     * @param loadFields boolean indicating if the fields should be loaded.
     */
    protected void securePage(HttpServletRequest request, ActionForm form, boolean loadFields) throws Exception {
        securePage(request, form, loadFields, false);
    }

    /**
     * Secure that the user has access to the web page.
     * Optionally load the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     * @param loadFields                 boolean indicating if the fields should be loaded.
     * @param loadPolicyHeaderFields     boolean indicating if the specific fields for the Policy Header page should be loaded
     */
    protected void securePage(HttpServletRequest request, ActionForm form, boolean loadFields, boolean loadPolicyHeaderFields) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "securePage", new Object[]{request, form});
        LogUtils.setPage("ActionClass:"+getClass().getName());
        // Create a Policy Data Security PageDefLoadProcessor if the Policy Header is loaded
        PageDefLoadProcessor pgDefLoadProcessor = null;
        PolicyHeader policyHeader = null;
        if (isPolicyHeaderLoaded(request)) {
            policyHeader = getPolicyHeader(request);
            pgDefLoadProcessor = PolicyDataSecurityPageDefLoadProcessor.getInstance(
                policyHeader.getPolicyIdentifier().getPolicyId());
        }
        else {
            // Otherwise, use the default PageDefLoadProcessor
            pgDefLoadProcessor = c_defaultPageDefLoadProcessor;
        }

        // Setup the class name variable to account for specialized policy header logic
        String className;
        if (loadPolicyHeaderFields) {
            className = KEY_PH_ACTION_CLASS;
        }
        else {
            className = getClass().getName();
        }

        if(loadFields) {
            //TODO: Remove redundant calls to loadListOfValues() and fieldsMap in jsp where no OasisFields are loaded (no UI)
            // This is a workaround
            request.removeAttribute(IOasisAction.KEY_FIELDS);

            //Either class caches OasisFields (property set to true) or it is a PolicyHeader
            if(policyHeader != null && (isCacheOasisFields() || className.equalsIgnoreCase(KEY_PH_ACTION_CLASS))){
                l.logp(Level.FINE, getClass().getName(), "securePage"," Right Action");
                UserCacheManager ucm = UserCacheManager.getInstance();

                // Handle multi-threaded access to UserCacheManager
                PolicyOasisFieldsCache policyOasisFieldsCache = null;
                synchronized (ucm) {
                    if (ucm.has(UserCacheManager.POLICY_OASIS_FIELDS_CACHE) &&
                        ((PolicyOasisFieldsCache)ucm.get(UserCacheManager.POLICY_OASIS_FIELDS_CACHE)).getPolicyId().equalsIgnoreCase(policyHeader.getPolicyId())) {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "securePage", " GOT "+UserCacheManager.POLICY_OASIS_FIELDS_CACHE+" CACHED POLICY ID: "+policyHeader.getPolicyId());

                        policyOasisFieldsCache = (PolicyOasisFieldsCache)ucm.get(UserCacheManager.POLICY_OASIS_FIELDS_CACHE);

                        if(policyOasisFieldsCache.has(className)){
                            if (l.isLoggable(Level.FINE))
                                l.logp(Level.FINE, getClass().getName(), "securePage", " HAS OASIS FIELDS FOR "+className+" IN CACHE. SKIPPING LOAD");
                            request.setAttribute(IOasisAction.KEY_FIELDS_CACHED, policyOasisFieldsCache.get(className));
                        }
                    }
                    if (policyOasisFieldsCache == null) {
                        if (l.isLoggable(Level.FINE))
                            l.logp(Level.FINE, getClass().getName(), "securePage"," DID NOT HAVE "+UserCacheManager.POLICY_OASIS_FIELDS_CACHE+" OR CACHE POLICY ID: "+policyHeader.getPolicyId());
                        policyOasisFieldsCache = new PolicyOasisFieldsCache(policyHeader.getPolicyId());
                        ucm.set(UserCacheManager.POLICY_OASIS_FIELDS_CACHE, policyOasisFieldsCache);
                    }
                }
                securePage(request, form, className, loadFields, pgDefLoadProcessor);
                OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
                if (fields != null) {
                    policyOasisFieldsCache.put(className, fields);

                    //Deep cloning
                    OasisFields clonedFields = (OasisFields) fields.clone();
                    request.setAttribute(IOasisAction.KEY_FIELDS, clonedFields);
                }
            } else {
                l.logp(Level.FINE, getClass().getName(), "securePage"," Wrong Action");
                securePage(request, form, className, loadFields, pgDefLoadProcessor);
            }
        } else {
            l.logp(Level.FINE, getClass().getName(), "securePage","OasisFields are not loaded");
            securePage(request, form, className, loadFields, pgDefLoadProcessor);
        }

        if (loadFields) {
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            if (fields != null) {
                // Map the PolicyHeader into request beans
                if (policyHeader != null && loadPolicyHeaderFields) {
                    Record policyRecord = policyHeader.toRecord();
                    ActionHelper.recordToBeans(request, policyRecord, fields, true);

                    OasisFormField field = (OasisFormField) fields.get("availablePolicyTerms");
                    field.setLovSql(policyHeader.getAvailablePolicyTerms());
                    field.setIsReadOnly(false);
                    field = (OasisFormField) fields.get("policyViewMode");
                    field.setIsReadOnly(false);
                }

            }
        }

        l.exiting(getClass().getName(), "securePage");
    }

    private boolean isPolicyHeaderLoaded(HttpServletRequest request) {
        return request.getAttribute(RequestIds.POLICY_HEADER) != null;
    }

    private PolicyHeader getLoadedPolicyHeader(HttpServletRequest request) {
        return (PolicyHeader) request.getAttribute(RequestIds.POLICY_HEADER);
    }

    protected PolicyHeader getPolicyHeader(HttpServletRequest request) {
        return getPolicyHeader(request, false);
    }

    protected PolicyHeader getPolicyHeader(HttpServletRequest request,
                                           boolean loadRiskHeader) {
        return getPolicyHeader(request, loadRiskHeader, false);
    }

    protected PolicyHeader getPolicyHeader(HttpServletRequest request,
                                           boolean loadRiskHeader,
                                           boolean loadCoverageHeader) {

        Logger l = LogUtils.enterLog(getClass(), "getPolicyHeader", new Object[]{request});

        PolicyHeader policyHeader = getLoadedPolicyHeader(request);

        if (policyHeader == null)
            policyHeader = loadPolicyHeader(request);

        if (policyHeader != null) {
            if (loadRiskHeader) {
                // Determine if the riskId exists as a request parameter
                String riskId = request.getParameter("riskId");

                // Load the RiskHeader into the policyHeader
                // If the riskId is not specified, the first riskId is loaded.
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);

                // Reset back into the request for future use
                request.setAttribute(RequestIds.POLICY_HEADER, policyHeader);

            }
            else {
                String riskId = request.getParameter("riskId");
                RiskHeader riskHeader = policyHeader.getRiskHeader();
                if (!StringUtils.isBlank(riskId) && riskHeader != null && !riskId.equals(riskHeader.getRiskId())) {
                    // If the riskId is passed into the request, and the loaded RiskHeader does not match, clear the RiskHeader
                    policyHeader.setRiskHeader(null);
                    policyHeader.setCoverageHeader(null);
                    policyHeader.clearCurrentSelectedMap();
                }
            }

            if (loadCoverageHeader) {
                // Determine if the riskId exists as a request parameter
                String coverageId = request.getParameter("coverageId");

                // Load the CoverageHeader into the policyHeader.
                // If the coverageId is not specified, the first coverageId is loaded.
                policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, coverageId);

                // Reset back into the request for future use
                request.setAttribute(RequestIds.POLICY_HEADER, policyHeader);
            }
            else {
                String coverageId = request.getParameter("coverageId");
                CoverageHeader coverageHeader = policyHeader.getCoverageHeader();
                if (!StringUtils.isBlank(coverageId) && coverageHeader != null && !coverageId.equals(coverageHeader.getCoverageId())
                    || (policyHeader.getRiskHeader() == null && coverageHeader != null)) {
                    // If the coverageId is passed into the request, and the loaded CoverageHeader does not match, clear the CoverageHeader
                    policyHeader.setCoverageHeader(null);
                    policyHeader.clearCurrentSelectedMap();
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyHeader", policyHeader);
        }

        return policyHeader;
    }

    /**
     * This method will clear the PolicyHeader from the RequestStorageManager and the UserSession,
     * and then call loadPolicyHeader().
     *
     * @param request
     * @return policyHeader
     */
    protected PolicyHeader reloadPolicyHeader(HttpServletRequest request) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "reloadPolicyHeader", new Object[]{request});
        }

        request.removeAttribute(RequestIds.POLICY_HEADER);
        RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
        PolicyHeader policyHeader = loadPolicyHeader(request);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "reloadPolicyHeader", policyHeader);
        }

        return policyHeader;
    }

    /**
     * Load the policy header information for the provided policy attributes via request parameters.
     * If policy number is not provided, the process will automatically unlock any previously owned locked,
     * and null is returned.
     * Otherwise, the loaded PolicyHeader is returned.
     */
    private PolicyHeader loadPolicyHeader(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyHeader", new Object[]{request});
        LogUtils.setPage("ActionClass:"+getClass().getName());
        PolicyHeader policyHeader = null;
        String policyNo = "";
        //endorsement quote  id used when policyViewMode="ENDQUOTE"
        String endQuoteId = request.getParameter("endQuoteId");
        if ("null".equalsIgnoreCase(endQuoteId)) {
            endQuoteId = "";
        }

        if (!StringUtils.isBlank(request.getParameter(RequestIds.POLICY_NO))) {
            policyNo = request.getParameter(RequestIds.POLICY_NO);
        }
        else {
            if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_NO)) {
                policyNo = (String) RequestStorageManager.getInstance().get(RequestStorageIds.POLICY_NO);
            }
        }

        if (!StringUtils.isBlank(policyNo)) {

            //if the policyNo is changed, unlock previour held lock
            if (UserSessionManager.getInstance().getUserSession().has(UserSessionIds.POLICY_HEADER)) {

                PolicyHeader previousPolicyHeader =
                    (PolicyHeader) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.POLICY_HEADER);
                if (previousPolicyHeader != null && !previousPolicyHeader.getPolicyNo().equals(policyNo)) {
                    if (getLockManager().unLockPreviouslyHeldLock()) {
                        UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                    }
                    else {
                        l.logp(Level.WARNING, getClass().getName(), "execute", "For policy number:'"+policyNo+"', " +
                            "it could not release the locks possible due to another user acquiring the lock in the meantime when the lock expired!");
                    }
                }
            }

            String policyTermHistoryId = null;
            PolicyViewMode policyViewMode = PolicyViewMode.WIP;

            if (request.getParameter(RequestIds.POLICY_TERM_HISTORY_ID) != null) {
                if (!StringUtils.isBlank(request.getParameter(RequestIds.POLICY_TERM_HISTORY_ID)))
                    policyTermHistoryId = request.getParameter(RequestIds.POLICY_TERM_HISTORY_ID);
            }
            else {
                // If there is an in progress workflow for this policyNo, system retrieves policyTermHistoryId from this workflow.
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyNo)) {
                    policyTermHistoryId = (String) wa.getWorkflowAttribute(policyNo, RequestIds.POLICY_TERM_HISTORY_ID);
                }
                else if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_TERM_HISTORY_ID)) {
                    policyTermHistoryId = RequestStorageManager.getInstance().get(RequestStorageIds.POLICY_TERM_HISTORY_ID).toString();
                }
            }

            // If there is an in progress workflow for this policyNo, system retrieves endQuoteId & policyViewMode from this workflow.
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            String polViewMode = "";
            if (wa.hasWorkflow(policyNo)) {
                if(StringUtils.isBlank(endQuoteId) && wa.hasWorkflowAttribute(policyNo, RequestIds.END_QUOTE_ID)) {
                    endQuoteId = (String) wa.getWorkflowAttribute(policyNo, RequestIds.END_QUOTE_ID);
                    if (wa.hasWorkflowAttribute(policyNo, RequestIds.POLICY_VIEW_MODE)) {
                        polViewMode = (String) wa.getWorkflowAttribute(policyNo, RequestIds.POLICY_VIEW_MODE);
                    }
                }
            }
            String viewMode = !StringUtils.isBlank(polViewMode)? polViewMode : request.getParameter(RequestIds.POLICY_VIEW_MODE);

            if (!StringUtils.isBlank(viewMode)) {
                if (viewMode.indexOf(PolicyViewMode.ENDQUOTE.getName()) > -1) {
                    viewMode = PolicyViewMode.ENDQUOTE.getName();
                }
                policyViewMode = PolicyViewMode.getInstance(viewMode);
            }

/*            String policyLockId = "";
            if (request.getParameter(RequestIds.POLICY_LOCK_ID) != null) {
                policyLockId = request.getParameter(RequestIds.POLICY_LOCK_ID);
            }*/

            try {
                PolicyManager polMgr = getPolicyManager();
                String process = request.getParameter("process");
                String requestId = getClass().getName() + "&process=" + process;
                boolean isMonitorPolicy = isMonitorPolicyProcess(process);
                if (request.getParameter(POLICY_PHASE_CODE) != null) {
                    RequestStorageManager.getInstance().set(POLICY_PHASE_CODE, request.getParameter(POLICY_PHASE_CODE));
                }
                policyHeader = polMgr.loadPolicyHeader(policyNo, policyTermHistoryId, policyViewMode, endQuoteId, requestId, process, isMonitorPolicy);

                String activityDisplayInformation = MessageManager.getInstance().formatMessage(
                    "cs.policy.activityHistory.displayInformation",
                    new String[]{policyHeader.getPolicyHolderName()});

                getActivityHistoryManager().recordActivityHistory(
                    ApplicationContext.getInstance().getProperty("applicationId", "Policy"),
                    "POLICY", policyHeader.getPolicyNo(), policyHeader.getPolicyId(),
                    "", activityDisplayInformation, "");
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to find policy information for : " + policyNo, e);
                l.throwing(getClass().getName(), "loadPolicyHeader", ae);
                throw ae;
            }

            request.setAttribute(RequestIds.POLICY_HEADER, policyHeader);
            request.setAttribute(RequestIds.SELECTED_POLICY_VIEW_MODE, policyHeader.getPolicyIdentifier().getPolicyViewMode());
            request.setAttribute(RequestIds.IS_POLICY_VIEW_MODE_VISIBLE, YesNoFlag.getInstance(policyHeader.isShowViewMode()));
            //request.setAttribute(RequestIds.POLICY_LOCK_ID, policyHeader.getPolicyIdentifier().getPolicyLockId());
        }
        else {
            try {
                if (getLockManager().unLockPreviouslyHeldLock()) {
                    UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_HEADER);
                }
                else {
                    throw new AppException("Unable to unlock previously held lock.");
                }
                UserCacheManager.getInstance().remove(UserCacheManager.POLICY_OASIS_FIELDS_CACHE);
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to find policy information for : " + policyNo, e);
                l.throwing(getClass().getName(), "loadPolicyHeader", ae);
                throw ae;
            }
        }

        if (policyHeader != null) {
            if (UserSessionManager.getInstance().getUserSession().has(RequestIds.PREVIOUS_POLICY_NO) &&
                policyHeader.getPolicyNo().equals(UserSessionManager.getInstance().getUserSession().get(RequestIds.PREVIOUS_POLICY_NO))) {
                request.setAttribute(RequestIds.IS_SAME_POLICY_B, "Y");
            }
            else {
                request.setAttribute(RequestIds.IS_SAME_POLICY_B, "N");
            }
            UserSessionManager.getInstance().getUserSession().set(RequestIds.PREVIOUS_POLICY_NO, policyHeader.getPolicyNo());
        }
        else {
            request.setAttribute(RequestIds.IS_SAME_POLICY_B, "N");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        }
        return policyHeader;
    }

    /**
     * Handle exceptions - store it in the request and return the
     * forward string for the error page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for the Error page
     */
    protected String handleError(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {

        // Clear the workflow if an error is encountered
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        PolicyHeader policyHeader = getLoadedPolicyHeader(request);
        if (policyHeader != null && wa.hasWorkflow(policyHeader.getPolicyNo())) {
            wa.clearWorkflow(policyHeader.getPolicyNo());
        }

        String r = super.handleError(messageKey, debugMessage, e, request, mapping);
        if (!(e instanceof AppException)) {
            request.setAttribute(IOasisAction.KEY_ERROR, e);
        }
        return r;
    }


    /**
     * Handle exceptions on a popup - store it in the request and and return the
     * forward string for the error pop up page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for Error page for popups
     */
    protected String handleErrorPopup(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {

        // Clear the workflow if an error is encountered
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        PolicyHeader policyHeader = getLoadedPolicyHeader(request);
        if (policyHeader != null && wa.hasWorkflow(policyHeader.getPolicyNo())) {
            wa.clearWorkflow(policyHeader.getPolicyNo());
        }

        String r = super.handleErrorPopup(messageKey, debugMessage, e, request, mapping);
        if (!(e instanceof AppException)) {
            request.setAttribute(IOasisAction.KEY_ERROR, e);
        }
        return r;
    }

    /**
     * Handle exceptions on a iframe which is in a popup - store it in the request and and return the
     * forward string for the error iframe page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for Error page for iframes
     */
    protected String handleErrorIFrame(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {

        // Clear the workflow if an error is encountered
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        PolicyHeader policyHeader = getLoadedPolicyHeader(request);
        if (policyHeader != null && wa.hasWorkflow(policyHeader.getPolicyNo())) {
            wa.clearWorkflow(policyHeader.getPolicyNo());
        }

        String r = super.handleErrorIFrame(messageKey, debugMessage, e, request, mapping);
        if (!(e instanceof AppException)) {
            request.setAttribute(IOasisAction.KEY_ERROR, e);
        }
        return r;
    }

    /**
     * Handle exceptions for Ajax - write XML in response
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param response     current response
     */
    protected void handleErrorForAjax(String messageKey, String debugMessage, Exception e,
                                      HttpServletResponse response) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorForAjax", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        l.logp(Level.SEVERE, getClass().getName(), "handleErrorForAjax", "Failed while invoking the Ajax Request", ae);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        writeAjaxResponse(response, new Record(), true);

        l.exiting(getClass().getName(), "handleErrorForAjax");
    }

    /**
     * Handle exceptions for Ajax without the application error message
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param response     current response
     */
    protected void handleErrorForAutoSaveAjax(String messageKey, String debugMessage, Exception e,
                                              HttpServletResponse response) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorForAutoSaveAjax", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        l.logp(Level.SEVERE, getClass().getName(), "handleErrorForAutoSaveAjax", "Failed while invoking the Ajax Request", ae);

        if (!MessageManager.getInstance().hasMessage(messageKey)) {
            MessageManager.getInstance().addErrorMessage(messageKey, ae.getMessageParameters());
        }

        writeAjaxResponse(response, new Record(), true);

        l.exiting(getClass().getName(), "handleErrorForAutoSaveAjax");
    }

    /**
     * Get LOV labels for givien field values
     *
     * @param request
     * @param inputRecord Record with field values
     */
    protected void getLovLabelsForInitialValues(HttpServletRequest request, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLovLabelsForInitialValues", new Object[]{request, inputRecord});
        }

        // loop through all fields with initial values
        Record outputRecord = new Record();
        Iterator fieldNames = inputRecord.getFieldNames();
        while (fieldNames.hasNext()) {
            // get field name and value
            String fieldName = (String) fieldNames.next();
            String fieldValue = inputRecord.getStringValue(fieldName);

            // set the description for LOV field
            ArrayList lov = (ArrayList) request.getAttribute(fieldName + "LOV");
            if (lov != null) {
                int size = lov.size();
                int i;
                for (i = 0; i < size; i++) {
                    LabelValueBean lvb = (LabelValueBean) lov.get(i);
                    if (lvb.getValue().equals(fieldValue)) {
                        outputRecord.setFieldValue(fieldName + "LOVLABEL", lvb.getLabel());
                    }
                }
            }
        }

        // add the descriptions to the Record
        inputRecord.setFields(outputRecord);
        l.exiting(getClass().getName(), "getLovLabelsForInitialValues", inputRecord);
    }

    /**
     * This method is to initialize the value in page entitlment for specified fields.
     * It check if the field is visible in fieldsMap, set the value to associated indicator field that is
     * defined in page entitlement. The indicator fields id should be fieldId + "Available".
     * Ex. If field Id is "effectiveDate", the indicator field id should be "effectiveDateAvailable"
     *
     * @param record
     * @param fieldNameList
     * @param fieldsMap
     */
    protected void initializeEntitlementFieldFromOasisField(Record record,
                                                            String[] fieldNameList,
                                                            OasisFields fieldsMap) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeEntitlementFieldFromOasisField",
                new Object[]{record, fieldNameList, fieldsMap});
        }

        // loop through field list
        int len = fieldNameList.length;
        for (int i = 0; i < len; i++) {
            if (!StringUtils.isBlank(fieldNameList[i])) {
                OasisFormField field = fieldsMap.getField(fieldNameList[i]);
                String indFieldName = fieldNameList[i] + INDICATE_FIELD_SUFFIX;
                if (field != null && field.getIsVisible()) {
                    record.setFieldValue(indFieldName, "Y");
                }
                else {
                    record.setFieldValue(indFieldName, "N");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initializeEntitlementFieldFromOasisField");
        }
    }

    /**
     * If system doesn't retrieve recordset from DB, the page will be crashed and can't add row to the grid
     * beacause of there is no any defined field in the data island.
     * In order to avoid the above exception/error, we set an empty recordset
     * and make this recordset contains all the fields of the first layer.
     * We must make sure the anchorColumnName is the first field.
     *
     * @param request  current request
     */
    protected void setEmptyDataBean(HttpServletRequest request) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEmptyDataBean", new Object[]{request});
        }

        RecordSet rs = new RecordSet();
        List fieldNameList = new ArrayList();
        // Set anchorColumnName as the first field.
        String anchorColumnName = getAnchorColumnName();
        if (!StringUtils.isBlank(anchorColumnName)) {
            fieldNameList.add(anchorColumnName);
        }
        // Get fields.
        OasisFields oasisFields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        List layerIds = oasisFields.getLayerIds();
        if (layerIds != null) {
            // Get the fields of the first layer.
            List layerFields = oasisFields.getLayerFields((String) layerIds.get(0));
            for (int i = 0; i < layerFields.size(); i++) {
                OasisFormField field = (OasisFormField) layerFields.get(i);
                String fieldId = field.getFieldId();
                if (fieldId.indexOf("_GH") > 0) {
                    fieldId = fieldId.substring(0, fieldId.indexOf("_GH"));
                }
                // Avoid overwriting the anchorColumnName.
                if (!fieldNameList.contains(fieldId)) {
                    fieldNameList.add(fieldId);
                }
            }
        }
        rs.addFieldNameCollection(fieldNameList.listIterator());
        // Set Data Bean.
        setDataBean(request, rs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setEmptyDataBean");
        }
    }

    /**
     * Load grid header by anchor column name and grid layer id
     *
     * @param request
     * @param anchorColumnName
     * @param gridHeaderLayerId
     */
    protected void loadGridHeader(HttpServletRequest request,
                                  String anchorColumnName,
                                  String gridHeaderLayerId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadGridHeader", new Object[]{request, anchorColumnName, gridHeaderLayerId});
        }

        if (StringUtils.isBlank(anchorColumnName) || StringUtils.isBlank(gridHeaderLayerId)) {
            throw new IllegalArgumentException("Anchor column name or grid header layer may not be emtpy.");
        }

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        WebLayer layerFields = fields.getLayerFieldsMap(gridHeaderLayerId);
        String gridHeaderBeanName = "gridHeaderBean";
        XMLGridHeaderDOMLoader xmlLoader = new XMLGridHeaderDOMLoader(servlet.getServletContext());
        XMLGridHeader xmlHeader = xmlLoader.getHeader();
        xmlHeader.setGridHeaderFieldnameSuffix(null);
        xmlHeader.setFields(layerFields);
        xmlHeader.setAnchorColumnName(anchorColumnName);
        xmlHeader.setGridHeaderDefinesDisplayableColumnOrder(gridHeaderDefinesDisplayableColumnOrder());
        request.setAttribute(gridHeaderBeanName, xmlHeader);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadGridHeader");
        }
    }

    /**
     * Set all databean for multiple grids
     */
    protected void setAllDataBean(HttpServletRequest request, RecordSet[] recordSets) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setAllDataBean", new Object[]{request, recordSets});
        }

        int size = recordSets.length;
        if (size == 0 || size > 5) {
            throw new IllegalArgumentException("In valid record sets.");
        }

        String[] prefixs = {"first", "second", "third", "fourth", "fifth"};
        for (int i = 0; i < size; i++) {
            setDataBean(request, recordSets[i], prefixs[i]);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setAllDataBean");
        }
    }

    /**
     * Load all grid headers. This method is to simplify set grid header bean for multiply grids in the same page.
     *
     * @param request
     * @param anchorColumnNames
     */
    protected void loadAllGridHeader(HttpServletRequest request,
                                     String[] anchorColumnNames) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllGridHeader",
                new Object[]{request, anchorColumnNames});
        }

        int size = anchorColumnNames.length;
        if (size == 0 || size > 5) {
            throw new IllegalArgumentException("In valid anchor column names.");
        }
        String[] prefixs = {"first", "second", "third", "fourth", "fifth"};
        for (int i = 0; i < size; i++) {
            String anchorColumnName = anchorColumnNames[i];
            String gridHeaderBeanName = prefixs[i] + "GridHeaderBean";
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            XMLGridHeaderDOMLoader xmlLoader = new XMLGridHeaderDOMLoader(servlet.getServletContext());
            XMLGridHeader xmlHeader = xmlLoader.getHeader();
            xmlHeader.setGridHeaderFieldnameSuffix(null);
            xmlHeader.setFields(fields);
            xmlHeader.setGenerateMapWithoutPrefixes(this.getUseMapWithoutPrefixes());
            xmlHeader.setAnchorColumnName(anchorColumnName);
            xmlHeader.setGridHeaderDefinesDisplayableColumnOrder(gridHeaderDefinesDisplayableColumnOrder());
            request.setAttribute(gridHeaderBeanName, xmlHeader);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllGridHeader");
        }
    }

    /**
     * This method to get all record sets for multiple grids.
     *
     * @param request
     * @param anchorColumnNames
     * @return RecordSet[]
     */
    protected RecordSet[] getAllInputRecordSet(HttpServletRequest request,
                                               String[] anchorColumnNames) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllInputRecordSet", new Object[]{request, anchorColumnNames});
        }

        int size = anchorColumnNames.length;
        if (size == 0 || size > 5) {
            throw new IllegalArgumentException("In valid anchor column names.");
        }
        String[] prefixs = {"firstGrid", "secondGrid", "thirdGrid", "fourthGrid", "fifthGrid"};

        ArrayList rsList = new ArrayList();
        for (int i = 0; i < size; i++) {

            RecordSet inputRecordSet;
            String inputRecordSetKey = prefixs[i] + "RecordSet";
            if (RequestStorageManager.getInstance().has(inputRecordSetKey)) {
                inputRecordSet = (RecordSet) RequestStorageManager.getInstance().get(inputRecordSetKey);
            }
            else {
                inputRecordSet = new RecordSet();

                // Add all request parameters, except the textXML, as the SummaryRecord
                inputRecordSet.setSummaryRecord(getInputRecord(request));

                // Add the textXML as Records
                String textXMLName = prefixs[i] + dti.oasis.http.RequestIds.TEXT_XML;
                String textXML = request.getParameter(textXMLName);
                if (StringUtils.isBlank(textXML)) {
                    throw new AppException("The required " + textXMLName + " is missing from the request.");
                }
                XMLRecordSetMapper.getInstance(anchorColumnNames[i]).map(textXML, inputRecordSet);
                RequestStorageManager.getInstance().set(inputRecordSetKey, inputRecordSet);
            }
            rsList.add(inputRecordSet);
        }

        RecordSet[] rsArray = new RecordSet[size];
        rsList.toArray(rsArray);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllInputRecordSet", rsArray);
        }
        return rsArray;
    }

    /**
     * Get data for exporting excel CSV.
     *
     * @param request
     * @param inputRecords
     * @param layerId
     * @return String
     */
    protected String getExcelCSVData(HttpServletRequest request, RecordSet inputRecords, String layerId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExcelCSVData", new Object[]{request, inputRecords, layerId});
        }
        String delimiter = "\"";
        String comma = ",";
        setDataBean(request, inputRecords);
        loadGridHeader(request, getAnchorColumnName(), layerId);

        XMLGridHeader header = (XMLGridHeader) request.getAttribute("gridHeaderBean");
        BaseResultSet data = (BaseResultSet) request.getAttribute("dataBean");

        StringBuffer excelString = new StringBuffer("");
        OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        // Store the visible fields.
        List visibleFields = new ArrayList();
        if (fieldsMap.hasLayer(layerId)) {
            List layerFields = fieldsMap.getLayerFields(layerId);
            Iterator layerIt = layerFields.iterator();
            // Get column title, the fields have been sorted.
            while (layerIt.hasNext()) {
                OasisFormField field = (OasisFormField) (layerIt.next());
                if (field.getIsVisible()) {
                    visibleFields.add(field);
                    excelString.append(delimiter).append(field.getLabel()).append(delimiter).append(comma);
                }
            }
            excelString.append("\n");
            // Get fields
            boolean bDateInd, bDateTimeInd, bUrlInd, isDataPresent;
            int colCount = data.getColumnCount();
            // Process columns.
            header.processDataColumns(data);
            data.beforeFirst();
            // Start loop through rows.
            while (data.next()) {
                // Start loop through visible columns.
                int headerIdx = 0;
                Iterator fieldIdIt = visibleFields.iterator();
                while (fieldIdIt.hasNext()) {
                    OasisFormField visibleField = (OasisFormField) (fieldIdIt.next());
                    String visibleFieldName = visibleField.getFieldId();
                    visibleFieldName = visibleFieldName.substring(0, visibleFieldName.indexOf(header.getGridHeaderFieldnameSuffix()));
                    for (int i = 1; i <= colCount; i++) {
                        // System handles the visible fields only.
                        String dataColumnName = data.getColumnName(i);
                        if (!visibleFieldName.equalsIgnoreCase(dataColumnName)) {
                            continue;
                        }
                        // skip xxxLOVLABEL columns
                        if (dataColumnName.endsWith("LOVLABEL"))
                            continue;
                        // skip xxx_FORMATTED columns
                        if (dataColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                            continue;

                        bDateInd = false;
                        bDateTimeInd = false;
                        bUrlInd = false;

                        headerIdx = header.getHeaderIndex(dataColumnName).intValue();
                        String dataItem = data.getString(dataColumnName, "");
                        isDataPresent = (dataItem != null && dataItem.trim().length() > 0);
                        HashMap headerMap = header.getHeaderMap(headerIdx);
                        int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
                        Integer id = (Integer) headerMap.get(XMLGridHeader.CN_DISPLAY);
                        int display = (id == null) ? 0 : id.intValue();
                        String name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                        ArrayList lov = (ArrayList) headerMap.get(XMLGridHeader.CN_LISTDATA);
                        String iDec = (String) headerMap.get(XMLGridHeader.CN_DECIMALPLACES);
                        boolean isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();

                        // If this field is protected, set the data item to null as it should not appear in the xml
                        if (isProtected)
                            dataItem = "";
                        if (!StringUtils.isBlank(iDec))
                            if (!FormatUtils.isLong(iDec))
                                iDec = null;

                        // Start visible items
                        if (!headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
                            excelString.append(delimiter);
                            switch (type) {
                                //Percentage field type
                                case XMLGridHeader.TYPE_PERCENTAGE:
                                case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                                    if (iDec == null)
                                        excelString.append(FormatUtils.formatPercentage(dataItem));
                                    else
                                        excelString.append(FormatUtils.formatPercentage(dataItem, Integer.parseInt(iDec)));
                                    break;
                                case XMLGridHeader.TYPE_PHONE:
                                case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                                    excelString.append(FormatUtils.formatPhoneNumberForDisplay(dataItem));
                                    name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                    break;
                                case XMLGridHeader.TYPE_FORMATMONEY:
                                case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                                    if (iDec == null)
                                        excelString.append(FormatUtils.formatCurrency(dataItem));
                                    else
                                        excelString.append(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec)));
                                    break;
                                case XMLGridHeader.TYPE_FORMATDATE:
                                case XMLGridHeader.TYPE_DATE:
                                case XMLGridHeader.TYPE_UPDATEONLYDATE:
                                    java.util.Date dte = data.getDate(i);
                                    excelString.append(OasisTagHelper.formatDateAsXml(dte));
                                    if (!FormatUtils.isDateFormatUS()) {
                                        excelString.append(OasisTagHelper.formatCustomDateAsXml(dte));
                                        name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                    }
                                    bDateInd = true;
                                    break;
                                case XMLGridHeader.TYPE_FORMATDATETIME:
                                case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                                    java.util.Date dtTime = data.getDate(i);
                                    excelString.append(OasisTagHelper.formatDateTimeAsXml(dtTime));
                                    if (!FormatUtils.isDateFormatUS()) {
                                        excelString.append(OasisTagHelper.formatCustomDateTimeAsXml(dtTime));
                                        name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                    }
                                    bDateTimeInd = true;
                                    break;
                                case XMLGridHeader.TYPE_URL:
                                case XMLGridHeader.TYPE_UPDATEONLYURL:
                                    bUrlInd = true;
                                    if (isDataPresent) {
                                        if (display == XMLGridHeader.DISPLAY_MONEY) {
                                            if (iDec == null)
                                                excelString.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem)));
                                            else
                                                excelString.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec))));
                                        }
                                        else
                                            excelString.append(ResponseUtils.filter(dataItem));
                                    }
                                    break;
                                case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN:
                                    if (isDataPresent) {
                                        excelString.append(ResponseUtils.filter(dataItem));
                                    }
                                    if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                        String decodedValue = CollectionUtils.getDecodedValue(lov, dataItem);
                                        if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                            decodedValue = "";
                                        }
                                        excelString.append(ResponseUtils.filter(decodedValue));
                                        name += "LOVLABEL";
                                    }
                                    // Populate url column in data island if there's href defined for this drop down list field
                                    if (!StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                                        bUrlInd = true;
                                    }
                                    break;
                                case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                                    if (isDataPresent) {
                                        excelString.append(ResponseUtils.filter(dataItem));
                                    }
                                    if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                        String decodedValue = CollectionUtils.getDecodedValues(lov, dataItem.split(","));
                                        if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                            decodedValue = "";
                                        }
                                        excelString.append(ResponseUtils.filter(decodedValue));
                                        name += "LOVLABEL";
                                    }
                                    if (!StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                                        bUrlInd = true;
                                    }
                                    break;
                                default:
                                    if (isDataPresent) {
                                        // if a list of values is present for a readonly field, decode
                                        if (type == XMLGridHeader.TYPE_DEFAULT && lov != null)
                                            excelString.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                                        else if (!FormatUtils.isDateFormatUS() && FormatUtils.isDate(dataItem))
                                            excelString.append(ResponseUtils.filter(FormatUtils.formatDateForDisplay(dataItem)));
                                        else
                                            excelString.append(ResponseUtils.filter(dataItem));
                                    }
                                    break;
                            }
                            excelString.append(delimiter).append(comma);
                        }
                    }
                } // End Visible items
                // Append "\n" to the every line.
                excelString.append("\n");
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExcelCSVData", excelString);
        }
        return excelString.toString();
    }


    protected void processExcelExport(HttpServletRequest request,
                                      HttpServletResponse response, String textForFile, String fileName) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processExcelExport", new Object[]{request, response, textForFile});
        }

        String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
        String dispType = request.getParameter("dispositionType");

        if (StringUtils.isBlank(dispType)) {
            dispType = OasisGrid.ATTACH_DISP_TYPE;
        }
        else {
            dispType = org.apache.commons.lang3.StringUtils.deleteWhitespace(dispType);
        }

        String pageName = request.getParameter("pageName");

        String exportType = request.getParameter("exportType");

        if (StringUtils.isBlank(exportType)) {
            exportType = "XLSX";
        }

        Workbook wb = null;
        if (exportType.equalsIgnoreCase("XLSX"))
            wb = new XSSFWorkbook();
        else
            wb = new HSSFWorkbook();

        Sheet sheet = wb.createSheet("Sheet1");
//            CreationHelper createHelper = wb.getCreationHelper();

        //Create Styles
        CellStyle cs;
        CellStyle csBold;

        //Bold Fond
        Font bold = wb.createFont();
        bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

        //Bold style
        csBold = wb.createCellStyle();
//        csBold.setBorderBottom(CellStyle.BORDER_THIN);
        csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        csBold.setFont(bold);

        int rowIdx = 0, colIdx = 0;

        //Get Mode
        String displayMode = ApplicationContext.getInstance().getProperty("gridExportExcel.infoHeaderMode", "HEADER");
        if(!displayMode.equalsIgnoreCase("NONE")) {
            //Get current Date and Time
            java.util.Date date = new java.util.Date(System.currentTimeMillis());
            String dateString = dti.oasis.util.FormatUtils.formatDateTimeForDisplay(date);

            //Get User
            String exportUser;
            OasisUser user = (dti.oasis.util.OasisUser) request.getSession().getAttribute(dti.oasis.struts.IOasisAction.KEY_OASISUSER);
            if(user!=null){
                exportUser = user.getUserName()+"("+user.getUserId()+")";
            } else {
                String userId = ActionHelper.getCurrentUserId(request);
                exportUser = userId;
            }

            if(displayMode.equalsIgnoreCase("HEADER")){
                Header header = sheet.getHeader();
                header.setLeft(HSSFHeader.fontSize((short) 9)+"Exported By: "+exportUser+"\n"+"Exported From: "+pageName);
                header.setRight(HSSFHeader.fontSize((short) 9)+"Exported On: "+dateString);
            } else if(displayMode.equalsIgnoreCase("FOOTER")){
                Footer footer = sheet.getFooter();
                footer.setLeft(HSSFFooter.fontSize((short) 9)+"Exported By: "+exportUser+"\n"+"Exported From: "+pageName);
                footer.setRight(HSSFFooter.fontSize((short) 9)+"Exported On: "+dateString);
            } else if(displayMode.equalsIgnoreCase("BODY"))  {
                Row infoRow;
                Cell infoCell;

                //First header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported By:");
                infoCell = infoRow.createCell(1);
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(exportUser);

                //Second header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported On:");
                infoCell = infoRow.createCell(1);
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(dateString);

                //Third header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported From:");
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(pageName);

                //Empty row
                rowIdx++;
            }
        }

        int hdrIndx = 0;
        for (String rowData : textForFile.split("\n")) {
            Row row = sheet.createRow(rowIdx++);
            colIdx = 0;
            rowData = rowData.trim();

            if (rowData.length() >= 2){
                if (rowData.endsWith(","))
                    rowData = rowData.substring(0, rowData.length() - 1);
                rowData = rowData.substring(1, rowData.length() - 1);
            }

            for (String value : rowData.split("\",\"")) {
                Cell cell = row.createCell(colIdx);
                CellStyle style = wb.createCellStyle();

                if (value.startsWith("$") || value.startsWith("($")) {
                    NumberFormat nf = NumberFormat.getCurrencyInstance(LocaleUtils.getOasisLocale());
                    ParsePosition pp = new ParsePosition(0);
                    Number n = nf.parse(value, pp);
                    // if the value is completely parsed
                    if (pp.getIndex() >= value.length()) {
                        style.setDataFormat((short) 8);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(style);
                        cell.setCellValue(n.doubleValue());
                    }
                    else {
                        cell.setCellValue(value);
                    }
                }
                else {
                    if(hdrIndx==0)
                        cell.setCellStyle(csBold);
                    cell.setCellValue(value);
                }
                colIdx++;
            }
            hdrIndx++;
        }

        response.setHeader("Content-Type", "application/vnd.ms-excel; charset=" + encoding);
        response.setHeader("Content-Disposition", dispType + "; filename=" + fileName);

        ServletOutputStream out = response.getOutputStream();
        wb.write(response.getOutputStream());
        out.flush();
        out.close();

        l.exiting(getClass().getName(), "exportExcelCSV");
    }

    /**
     * Method to load warning message.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadWarningMessage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadWarningMessage", new Object[]{mapping, form, request, response});

        try {
            // Secures access to the page
            securePage(request, form, false);

            String transactionLogId = request.getParameter("transactionLogId");

            // load warning message
            String warningMsg = getTransactionManager().loadWarningMessage(transactionLogId);

            Record inputRecord = new Record();
            PolicyFields.setWarning(inputRecord, warningMsg);

            writeAjaxXmlResponse(response, inputRecord);

        }
        catch (Exception e) {
            handleErrorForAjax("pm.common.warning.error", "Failed to get warning message.", e, response);
        }

        l.exiting(getClass().getName(), "loadWarningMessage", null);
        return null;
    }

    /**
     * Method to reset display indicator to 'Y' for official record which is closed by deleted temp record when saving
     * error.
     * <p/>
     *
     * @param inputRecordSet
     * @param recId
     */
    protected void resetDisplayIndicatorForDeletedRs(RecordSet inputRecordSet, String recId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetDisplayIndicatorForDeletedRs", new Object[]{inputRecordSet});
        }

        if (inputRecordSet != null) {
            Iterator it = inputRecordSet.getRecords();
            while (it.hasNext()) {
                Record r = (Record) it.next();
                if (r.getUpdateIndicator().equals(UpdateIndicator.DELETED)) {
                    String offId = r.getStringValue(OFFICIAL_RECORD_ID);
                    if (!StringUtils.isBlank(offId)) {
                        Record offRec = inputRecordSet.getSubSet(new RecordFilter(recId, offId)).getRecord(0);
                        offRec.setDisplayIndicator(DisplayIndicator.VISIBLE);
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetDisplayIndicatorForDeletedRs");
        }
    }

    /**
     * Show messages after save WIP, rating, save official, auto save when switching tab.
     * Messages are stored in UserSession, so that we are able to to carry information between requests.
     * TODO: Currently, messageParameters are not supported because Message Class does not have this attribute.
     * There's a workaround that we format message text before put it into UserSession.
     * <p/>
     * @param policyHeader
     * @param request
     */
    protected void addSaveMessages (PolicyHeader policyHeader, HttpServletRequest request) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addSaveMessages", new Object[]{policyHeader, request});
        }

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        if (!wa.hasWorkflow(policyHeader.getPolicyNo()) && userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
            List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
            for (Message message : messageList) {
                if (MessageCategory.INFORMATION.equals(message.getMessageCategory())) {
                    MessageManager.getInstance().addInfoMessage(message.getMessageKey());
                }
                else if (MessageCategory.WARNING.equals(message.getMessageCategory())) {
                    MessageManager.getInstance().addWarningMessage(message.getMessageKey(), null,
                        message.getMessageFieldId(), message.getMessageRowId(), message.getMessageGridId());
                }
                else if (MessageCategory.ERROR.equals(message.getMessageCategory())) {
                    MessageManager.getInstance().addErrorMessage(message.getMessageKey(), null,
                        message.getMessageFieldId(), message.getMessageRowId(), message.getMessageGridId());
                }
                else if (MessageCategory.JS_MESSAGE.equals(message.getMessageCategory())) {
                    MessageManager.getInstance().addJsMessage(message.getMessageKey());
                }
                else if (MessageCategory.CONFIRMATION_PROMPT.equals(message.getMessageCategory())) {
                    MessageManager.getInstance().addConfirmationPrompt(message.getMessageKey());
                }
            }
            messageList.clear();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addSaveMessages");
        }
    }

    /**
     *  Load riskHeader, coverageHeader and set them to policyHeader.
     * @param request
     * @param policyHeader
     * @return
     */
    public boolean updatePolicyHeader(HttpServletRequest request, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyHeader", new Object[]{policyHeader});
        }

        boolean hasRisk = true;

        try {
            //Try to load risk header
            String riskId = request.getParameter(RequestIds.RISK_ID);
            // If the riskId is not specified, the first riskId is loaded.
            policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
        }
        catch (AppException ae) {
            hasRisk = false;
        }

        if (policyHeader.hasRiskHeader()) {
            //Load the coverage header if coverageId is existing in the request.
            String coverageId = request.getParameter(RequestIds.COVERAGE_ID);
            if (coverageId != null && Long.parseLong(coverageId) > 0) {
                policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, coverageId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updatePolicyHeader", hasRisk);
        }
        return hasRisk;
    }

    /**
     * Returns the Token Constant to use.  Return null to utilize default Struts
     * token processing.  Otherwise, override this method to return a different token. This
     * will cause the overridden code within saveToken and isTokenValid to be executed
     * rather than the default Struts code.
     *
     * @return null
     */
    protected String getTokenConstant() {
        return null;
    }

    /**
     * Save a new transaction token in the user's current session, creating
     * a new session if necessary.  If you override getTokenConstant, this will save
     * the token using the constant rather than the default STRUTS token.
     *
     * @param request The servlet request we are processing
     */
    protected void saveToken(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "saveToken", request);

        saveToken(request, getTokenConstant());

        l.exiting(getClass().getName(), "saveToken");
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    protected PMBaseAction() {
        super();
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getActivityHistoryManager() == null)
            throw new ConfigurationException("The required property 'activityHistoryManager' is missing.");
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }


    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }//  regional office is configurable, the logic is shared by createPolicy and reissue policy


    public ActivityHistoryManager getActivityHistoryManager() {
        return m_activityHistoryManager;
    }

    public void setActivityHistoryManager(ActivityHistoryManager activityHistoryManager) {
        m_activityHistoryManager = activityHistoryManager;
    }

    public QuoteManager getQuoteManager() {
        return m_quoteManager;
    }

    public void setQuoteManager(QuoteManager quoteManager) {
        m_quoteManager = quoteManager;
    }

    protected Record addRegionalOfficeVisibility(HttpServletRequest request, Record record) {
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        OasisFormField field = (OasisFormField) fields.get(CreatePolicyFields.REGIONAL_OFFICE);
        String isVisible = field.getIsVisible() ? "Y" : "N";
        record.setFieldValue(CreatePolicyFields.REGIONAL_OFFICE + "IsVisible", isVisible);
        return record;
    }


    public boolean hasAlwaysEnabledFieldIds() {
        return !StringUtils.isBlank(m_alwaysEnabledFieldIds);
    }

    public String getAlwaysEnabledFieldIds() {
        return m_alwaysEnabledFieldIds;
    }

    public void setAlwaysEnabledFieldIds(String alwaysEnabledFieldIds) {
        m_alwaysEnabledFieldIds = alwaysEnabledFieldIds;
    }

    public boolean getUseMapWithoutPrefixes() {
        boolean m_useMapWithoutPrefixes = false;
        return m_useMapWithoutPrefixes;
    }

    public Boolean isCacheOasisFields() {
        return m_cacheOasisFields;
    }

    public void setCacheOasisFields(Boolean cacheOasisFields) {
        m_cacheOasisFields = cacheOasisFields;
    }

    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private CoverageManager m_coverageManager;
    private LockManager m_lockManager;
    private TransactionManager m_transactionManager;
    private ActivityHistoryManager m_activityHistoryManager;
    private QuoteManager m_quoteManager;
    private Boolean m_cacheOasisFields = false;

    private String m_alwaysEnabledFieldIds;

    private static PageDefLoadProcessor c_defaultPageDefLoadProcessor = DefaultPageDefLoadProcessor.getInstance();

    /**
     * Name of map in which the Policy Header specific fields are stored.
     */
    private static String KEY_PH_FIELDS = "policyHeaderFieldsMap";

    /**
     * Name of the action class for the Web Workbench in which
     * the Policy Header specific fields are stored.
     */
    private static String KEY_PH_ACTION_CLASS = "dti.pm.policymgr.struts.PolicyHeaderFieldsMap";

    private static String INDICATE_FIELD_SUFFIX ="Available";

    public static final String OFFICIAL_RECORD_ID = "officialRecordId";

    private static final String POLICY_PHASE_CODE = "policyPhaseCode";

}
