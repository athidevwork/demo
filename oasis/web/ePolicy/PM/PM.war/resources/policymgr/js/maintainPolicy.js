//-----------------------------------------------------------------------------
// Javascript file for maintainPolicy.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   April 28, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/28/2010       syang       106437 - Modified loadPolicyNotes() to pass the callback function.
// 06/11/2010       bhong       108653 - Added logics to cover: Alternate Flow: Policy Cancellation during Renewal WIP mode
// 07/06/2010       syang       103797 - Handled to open the Entity Detail page from policy page.
// 07/23/2010       dzhang      103800 - Handled to open the Manage Quick Pay page from policy page.
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 09/21/2010       syang       Issue 111445 - Modified the width and height of Additiona lInsured page.
// 10/06/2010       syang       Issue 112731 - Modified handleOnButtonClick("ADDIINS") to set scrolling flag to true.
// 01/12/2011       dzhang      Issue 113568 - Handled to open View Shared Limit page from policy page.
// 03/18/2011       ryzhao      Issue 113559 - Add "PROCESS_ERP" case in handleOnButtonClick() function.
// 04/06/2011       fcb         Issue 119324 - Add logic to save also if isNewPolicyCreated is true.
// 05/01/2011       fcb         Issue 105791 - Added convertCoverage, submitConvertCoverage, handleOnConvertCoverageTransaction
// 08/24/2011       lmjiang     Issue 124365 - Re-size the 'Limit Sharing' grid height to remove the duplicate bar.
// 09/21/2011       wfu         120554 - Fixed errors in quote pages and multiple grid pages.
// 11/25/2011       syang       127661 - Before opening Maintain Underwriter page, system should save changed firstly.
// 07/17/2012       ryzhao      135662 - Variable italicsFieldId should have different value for POLICY or QUOTE.
//                                       Modified all the related buttons to set different value to italicsFieldId.
// 12/27/2012       tcheng      139862 - Added handleExitWorkflow() to pop up warning message.
// 03/26/2013       jshen       143400 - Pass viewMode & endorsementQuoteId(if exist) to retrieve record exist information.
// 06/26/2013       tcheng      145144 - Modified function handleOnChange to remove blank string for policy no.
// 07/01/2013       adeng       117011 - Modified submitConvertCoverage() to set value of new field
//                                       "transactionComment2" to input form field "transactionComment2".
// 07/24/2013       awu         146526 - Modified handOnLoad to skip button label logic if invokeWorkFlow return true.
// 12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
// 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
// 06/20/2014       jyang       155366 - Modified handleonbuttonclick function to increase the size of Tail Quote popup page.
// 08/25/2014       AWU         152034 - Modified handleOnSubmit to append risk/coverage/coverage class IDs to URL for RATE.
// 11/04/2014       kxiang      158857 - Modified handleOnSubmit to change logic to set 'changeB'.
// 03/16/2016       eyin        168545 - Variable italicsFieldId should have different value for POLICY or QUOTE.
//                                       Modified 'View Sh Lmt.' button to set different value to italicsFieldId.
// 04/21/2016       tzeng       167532 - Modified handleOnButtonClick to add Renewal Flag button.
// 03/10/2017       wli         180675 - 1. Added commonOnUIProcess() in the method named handleOnLoad() for UI change.
//                                       2. Added some logic in the method named handleOnSubmit() when tab style for UI change.
//                                       3. Added functions "autoSaveSubTab, handleOnSecondaryTabClick, callBackAutoSaveForFrame
//                                          processMainPageAfterAutoSaveSubTab,requireSaveChangedMainPage" for UI change.
// 07/26/2017       lzhang      182246 - clean up unsaved message for page changes
// 09/06/2017       lzhang      187946 - Modified currentTabIsHideForCurrentRow: check whether tail quote tab is hidden
// 11/09/2017       tzeng       187689 - 1) Modified handleOnSubmit(), handleOnSecondaryTabClick(), preButtonClick() to
//                                          support processAutoSaveSubTab().
//                                       2) Modified processMainPageAfterAutoSaveSubTab() to always do extract if click
//                                          WIP/SAVEWIP.
// 12/13/2017       wrong       190191 - 1. Modified handleOnSecondaryTabClick to add clearOperationForTabStyle() to clear
//                                          operation value when switching sub tab in no data change case.
//                                       2. Modified callBackAutoSaveForFrame() to clear operation value when saving
//                                          sub tab data.
// 12/27/2017       xnie        190192 - Modified handleOnButtonClick/handleOnShowPageInFrame to support Select Address
//                                       button/tab italic.
// 12/28/2017       tzeng       190488 - Added handleOnRevertSubTabChangesBeforeLeave() for when make WIP transaction
//                                       on TAIL page then click other sub tabs/buttons to revert the changes.
// 07/24/2018       wrong       194562 - Modified handleOnSubmit() to move set "changeB" logic to the start of function.
// 09/17/2018       ryzhao      195271 - Modified autoSaveSubTab() to set indicator if it is auto save data from sub-tab
//                                       when saving component information.
// 10/15/2018       wrong       188391 - Enhanced to support for Underlying coverage.
//-----------------------------------------------------------------------------
var originalPolicyNo;
var italicsFieldId = "NONE";
var pageName = "Policy";

function handleOnLoad() {
	if(getUIStyle() == "T") {
        commonOnUIProcess();
    }
    setInputFormField("needToHandleExitWorkFlow", "Y");
    var workFlowFlag = invokeWorkflow();
    setRecordExistsStyle();
}

function handleOnChange(field) {
    if (field.name == "policyNoEdit") {
        var policyNo = field.value;
        if (policyNo != trim(policyNo)) {
            policyNo = trim(policyNo);
            field.value = policyNo;
        }
        var policyId = policyHeader.policyId;
        var url = "maintainPolicy.do?modifiedPolicyNo=" + policyNo + "&policyId="+ policyId +"&process=verifyPolicyNo";
        // initiate async call
        new AJAXRequest("get", url, '', verifyPolicyNoDone, false);
    }
}

function handleOnFocus(field) {
    if (field.name == "policyNoEdit") {
        originalPolicyNo = field.value;
    }
}

function verifyPolicyNoDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                document.forms[0].policyNoEdit.value = originalPolicyNo;
            }
        }
    }
}

function handleOnSubmit(action) {
    var proceed = true;

    var isNewPolicyCreated = getObjectValue("isNewPolicyCreated");
    // Fix issue 101945, set the parameter changeB to inputRecord.
    if (action == 'SAVE' || action == 'SAVEWIP' || action == 'RATE') {
        // When policy data is changed, the changeB indicator will be set to 'Y' and policy level component change
        // does not touch this indicator.
        if (isChanged || isNewPolicyCreated == 'Y') {
            setInputFormField("changeB", "Y");
        }
        else {
            setInputFormField("changeB", "N");
        }
    }

    if(getUIStyle() == "T") {
        operation = action;
        removeMessagesForFrame();
        if(!isReservedTab(getPreviousTab()) && !isPreviewButtonClicked() && action != "SAVE") {
            processAutoSaveSubTab(getCurrentTab());
            if (autoSaveResultType != commonOnSubmitReturnTypes.noDataChange) {
                return false;
            }
        }
    }

    switch (action) {
        case 'SAVE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOL", getFormActionAttribute());
            document.forms[0].process.value = "savePolicy";
            loadSaveOptions("PM_POLICY", "submitForm");
            proceed = false;
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOL", getFormActionAttribute());
            document.forms[0].process.value = "savePolicy";

            handleSaveOptionSelection("WIP");
            break;
        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOL", getFormActionAttribute());
            document.forms[0].process.value = "savePolicy";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;

        case 'OOS_CHANGE':
            document.forms[0].process.value = "loadPolicyDetail";
            setInputFormField("ooseChangeB", "Y");
            break;

        case 'OOS_DELETE':
            document.forms[0].process.value = "deleteOosPolicyDetail";
            setInputFormField("ooseChangeB", "N");
            break;

        default:
            proceed = false;
    }

    return proceed;
}

//-----------------------------------------------------------------------------
// Overwrite commonOnButtonClick
//-----------------------------------------------------------------------------
function commonOnButtonClick(asBtn) {
    if(isTabStyle()){
        if(preButtonClick(asBtn)) {
            handleOnButtonClick(asBtn);
        }
    }else{
        handleOnButtonClick(asBtn);
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case "CANCEL":
            // Alternate Flow: Policy Cancellation during Renewal WIP mode
            var policyScreenMode = getObjectValue("policyScreenMode");
            if (policyScreenMode == "RENEWWIP") {
                alert(getMessage("pm.maintainCancellation.cancellationNotPermitted.error"));
            }
            else {
                performCancellation("POLICY", policyHeader.policyId, policyHeader.termEffectiveFromDate, policyHeader.termEffectiveToDate);
            }
            break;
        case "TAIL":
            var tailUrl = getAppPath() + "/tailmgr/maintainTail.do?"
                + commonGetMenuQueryString() + "&process=loadAllTail";
            italicsFieldId = "TAILB";
            var divPopupId = openDivPopup("", tailUrl, true, true, "", "", 830, 800, "", "", "", false);
            break;
        case "ADDIINS":
            var addiInsUrl = getAppPath() + "/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do?"
                + commonGetMenuQueryString() + "&process=loadAllAdditionalInsured";

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "ADDINSUREDB";
            }
            else {
                italicsFieldId = "ADDINSUREDQUOTEB";
            }
            var divPopupId = openDivPopup("", addiInsUrl, true, true, "", "", 860, 730, "", "", "", false, "", "", true);
            break;
        case "REINSTATE":
            effFromDate = getObjectValue("termEffectiveFromDate");
            effToDate = getObjectValue("termEffectiveToDate");
            var policyStatus = getObjectValue("policyStatus");
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            performReinstate("POLICY", termBaseRecordId, policyHeader.policyId, effFromDate, effToDate, policyStatus, "");
            break;
        case "AUDIT":
            var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=policy-policy";
            var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
            break;
        case "REINSURANCE":
            var reinsuranceUrl = getAppPath() + "/policymgr/reinsurancemgr/maintainReinsurance.do?"
                + commonGetMenuQueryString() + "&process=loadAllReinsurance";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "REINSURANCEB";
            }
            else {
                italicsFieldId = "REINSURANCEQUOTEB";
            }
            var divPopupId = openDivPopup("", reinsuranceUrl, true, true, "", "", 850, 720, "", "", "", false);
            break;
        case "CHG_EXP_DATE":
            var chgExpDateUrl = getAppPath() + "/transactionmgr/changeTermExpirationDate.do?"
                + commonGetMenuQueryString();
            var divPopupId = openDivPopup("", chgExpDateUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
        case "LIMITSHARING":
            var limitSharingUrl = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?"
                + commonGetMenuQueryString() + "&process=loadAllLimitSharing";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SHAREGROUPB";
            }
            else {
                italicsFieldId = "SHAREGROUPQUOTEB";
            }
            var divPopupId = openDivPopup("", limitSharingUrl, true, true, "", "", 950, 880, "", "", "", false);
            break;
        case "OOS_CHANGE":
            commonOnSubmit("OOS_CHANGE", true, true, true);
            break;
        case "OOS_DELETE":
            commonOnSubmit("OOS_DELETE", true, true, true);
            break;
        case "TAIL_QUOTE":
            var tailQuoteUrl = getAppPath() + "/policymgr/tailquotemgr/maintainTailQuote.do?"
                + commonGetMenuQueryString() + "&process=loadAllTailQuoteTransaction";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "TAILQUOTEB";
            }
            else {
                italicsFieldId = "TAILQUOTEQUOTEB";
            }
            var divPopupId = openDivPopup("", tailQuoteUrl, true, true, "", "", 950, 860, "", "", "", false);
            break;
        case "SELECT_ADDRESS":
            var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=POLICYHOLDER";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SELECTADDRESSB";
            }
            else {
                italicsFieldId = "SELECTADDRESSGQUOTEB";
            }
            var divPopupId = openDivPopup("", selAddrUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
        case "UNDERWRITER":
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Underwriter", "N", "");
            if (saveRequired) {
                break;
            }
            else {
                var underwriterUrl = getAppPath() + "/policymgr/underwritermgr/maintainUnderwriter.do?" + commonGetMenuQueryString();
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "ADDLINFOB";
                }
                else {
                    italicsFieldId = "ADDLINFOQUOTEB";
                }
                var divPopupId = openDivPopup("", underwriterUrl, true, true, "", "", 800, 600, "", "", "", false);
            }
            break;
        case "PAYMENT":
            var paymentUrl = getAppPath() + "/policymgr/premiummgr/viewPayment.do?"
                + commonGetMenuQueryString() + "&process=loadAllPayment";
            var divPopupId = openDivPopup("", paymentUrl, true, true, "", "", 800, 500, "", "", "", false);
            break;
        case "ADMIN_HISTORY":
            var adminhistoryUrl = getAppPath() + "/transactionmgr/viewPolicyAdministratorHistory.do?"
                + commonGetMenuQueryString() + "&process=loadAllPolicyAdminHistory";
            var divPopupId = openDivPopup("", adminhistoryUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
        case "QUOTE_STATUS":
            var quotestatusUrl = getAppPath() + "/policymgr/processQuoteStatus.do?"
                + commonGetMenuQueryString() + "&process=loadAllQuoteStatus";
            var divPopupId = openDivPopup("", quotestatusUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
        // for component
        case 'ADDCOMP':
            // the coverageBaseRecordId will be passed to the procedure as coverageId.
            var productCoverageCode = getObjectValue("policyTypeCode");
            var coverageBaseRecordId = getObjectValue("policyId");
            var coverageBaseEffectiveFromDate = getObjectValue("termEffectiveFromDate");
            var url = getAppPath() + "/componentmgr/selectComponent.do?"
                + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                + "&coverageBaseRecordId=" + coverageBaseRecordId
                + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate;
            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'ADD_NEW_COMP':
            commonAddRow("componentListGrid");
            break;
        case 'DELETECOMP':
            // if officialRecordId=0, commonDeleteRow() will cause error. Set it to "" here directly.
            var gridId = "componentListGrid";
            var rs = getXMLDataForGridName(gridId).recordset;
            var officalRecordId = null;
            if (isFieldExistsInRecordset(rs, "COFFICIALRECORDID")) {
                officalRecordId = rs.Fields("COFFICIALRECORDID").value;
                if (officalRecordId == "0") {
                    rs("COFFICIALRECORDID").value = "";
                }
            }
            commonDeleteRow(gridId);
            break;
        case 'CYCLE_DETAIL':
            var sTransactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
            var sProductCovComponentId = componentListGrid1.recordset("CPRODUCTCOVCOMPONENTID").value;
            var sRecordModeCode = componentListGrid1.recordset("CRECORDMODECODE").value;
            var sComponentEffectiveFromDate = componentListGrid1.recordset("CCOMPONENTEFFECTIVEFROMDATE").value;
            var sComponentEffectiveToDate = componentListGrid1.recordset("CCOMPONENTEFFECTIVETODATE").value;
            var sComponentCycleDate = componentListGrid1.recordset("CCOMPONENTCYCLEDATE").value;

            var url = getAppPath() + "/componentmgr/cycleDetail.do?"
                + commonGetMenuQueryString() + "&transactionLogId=" + sTransactionLogId +
                      "&productCovComponentId=" + sProductCovComponentId + "&componentOwner=POLICY" +
                      "&recordModeCode=" + sRecordModeCode + "&componentEffectiveFromDate=" + sComponentEffectiveFromDate +
                      "&componentEffectiveToDate=" + sComponentEffectiveToDate + "&componentCycleDate=" + sComponentCycleDate;

            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'SURCHARGE_POINTS':
            // The sCoverageBaseRecordId is equal the policy Pk for policy level component.
            var sTermBaseRecordId = getObjectValue("termBaseRecordId");
            var sTransactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
            var sCoverageBaseRecordId = getObjectValue("policyId");
            var url = getAppPath() + "/componentmgr/maintainSurchargePoints.do?"
                + commonGetMenuQueryString() + "&coverageBaseRecordId=" + sCoverageBaseRecordId;
            var processingDivId = openDivPopup("", url, true, true, "", "", 900, 900, "", "", "", false);
            break;
        case 'EAPP':
            var spolicyId = getObjectValue("policyId");
            var sTermBaseRecordId = getObjectValue("termBaseRecordId");
            var eappListUrl = getAppPath() + "/policymgr/applicationmgr/maintainApplication.do?"
                + commonGetMenuQueryString() + "&policyId=" + spolicyId + "&termBaseRecordId=" + sTermBaseRecordId;
            var divPopupId = openDivPopup("", eappListUrl, true, true, "", "", 850, 720, "", "", "", false);
            break;
        case 'PTNOTES':
            var sPolicyNo = getObjectValue("policyNo");
            var url = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?"
                + commonGetMenuQueryString() + "&policyNumber=" + sPolicyNo;
            var processingDivId = openDivPopup("", url, true, true, "", "", 900, 600, "", "", "", false);
            break;
        case 'EXCESS':
            var url = getAppPath() + "/coveragemgr/maintainExcessCoverage.do?" + commonGetMenuQueryString();
            italicsFieldId = "EXCESSB";
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        // Fix issue 100751
        case 'CHGCOMPVALUE':
            if (preOoseChangeValidation("component", "componentListGrid", "CPOLCOVCOMPBASERECID")) {
                changeComponentType = "chgCompValue";
                addOoseComponent();
            }
            break;
        case 'CHGCOMPDATE':
            if (preOoseChangeValidation("component", "componentListGrid", "CPOLCOVCOMPBASERECID")) {
                changeComponentType = "chgCompDate";
                addOoseComponent();
            }
            break;
        case 'ENTITYDTL':
            var policyId = policyHeader.policyId;
            var transactionLogId = 0;
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var termEffectiveFromDate = policyHeader.termEffectiveFromDate;
            var termEffectiveToDate = policyHeader.termEffectiveToDate;
            var url = getAppPath() + "/transactionmgr/viewProfessionalEntityDetail.do?policyId=" + policyId +
                      "&transactionLogId=" + transactionLogId + "&termBaseRecordId=" + termBaseRecordId +
                      "&termEffectiveFromDate=" + termEffectiveFromDate + "&termEffectiveToDate=" + termEffectiveToDate;
            italicsFieldId = "CROSSREFB";
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case 'MANAGEQUICKPAY':
            var policyId = policyHeader.policyId;
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPay.do?" + commonGetMenuQueryString() +
                    "&policyId=" + policyId + "&termBaseRecordId=" + termBaseRecordId;
            italicsFieldId = "MANUALQPB";                
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "SHAREDLIMIT":
            var sharedLimitUrl = getAppPath() + "/policymgr/limitsharingmgr/viewSharedLimit.do?"
                    + commonGetMenuQueryString() + "&process=loadAllSharedLimit";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SHAREDLIMITSB";
            }
            else {
                italicsFieldId = "SHAREDLIMITSQUOTEB";
            }
            var divPopupId = openDivPopup("", sharedLimitUrl, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "PROCESS_ERP":
            var policyViewMode = getObjectValue("policyViewMode");
            var transLogId = "";
            var transEffDate = "";
            // transLogId: Primary key value of the current transaction if WIP transaction exists and policy is in WIP mode
            if (policyHeader.wipB && policyViewMode == "WIP") {
                transLogId = policyHeader.lastTransactionInfo.transactionLogId;
                transEffDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
            }
            var processErpUrl = getAppPath() + "/componentmgr/experiencemgr/processErp.do?"
                    + commonGetMenuQueryString()
                    + "&headerHidden=Y"
                    + "&policyId=" + policyHeader.policyId
                    + "&transLogId=" + transLogId
                    + "&termId=" + getObjectValue("termBaseRecordId")
                    + "&termEff=" + policyHeader.termEffectiveFromDate
                    + "&termExp=" + policyHeader.termEffectiveToDate
                    + "&transEff=" + transEffDate;
            var divPopupId = openDivPopup("", processErpUrl, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "MAINTAIN_TAX":
            var url = getAppPath() + "/policymgr/taxmgr/maintainTax.do?"
                    + commonGetMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "MAINTAINTAXB";
            }
            else {
                italicsFieldId = "MAINTAINTAXQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "", "", 850, 720, "", false);
            break;
        case "RENEWAL_FLAG":
            var renewalFlagUrl = getAppPath() + "/policymgr/renewalflagmgr/maintainRenewalFlag.do?" + commonGetMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RENEWALFLAGB";
            }
            else {
                italicsFieldId = "RENEWALFLAGQUOTEB";
            }
            var divPopupId = openDivPopup("", renewalFlagUrl, true, true, "", "", 800, 600, "", "", "", false);
            break;
        case "POL_UNDERLYING":
            var url = getAppPath() + "/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do?"
                    + commonGetMenuQueryString() + "&process=loadAllUnderlyingCoverage"
                    + "&isPolicyLevel=Y";
            if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "UNDERLYINGPOLB";
                }
            else {
                italicsFieldId = "UNDERLYINGPOLQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "900", "730", "", "", "", false);
            break;
        case 'CLOSE_DIV':
            closeThisDivPopup(false);
            break;
        case 'CLOSE_RO_DIV':
            closeThisDivPopup(true);
            break;
        case 'PREVIEW':
            onPreviewButtonClick();
        default:break;
    }
}

function viewUnderlyingPolicy(){
    var url = getAppPath() + "/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do?"
      + commonGetMenuQueryString() + "&process=loadAllUnderlyingPolicy";
    if (policyHeader.policyCycleCode == 'POLICY') {
        italicsFieldId = "UNDERLYINGB";
    }
    else {
        italicsFieldId = "UNDERLYINGQUOTEB";
    }
    var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function loadPolicyNotes() {
    if (window.loadNotes) {
        var policyId = getObjectValue("policyId");
        loadNotesWithReloadOption(policyId, "POLICY", "POLICY", true,'',"handleNotesExist");
    }
    else {
        alert(getMessage("pm.common.notes.functionality.notAvailable.error"));
    }
}

function handleNotesExist(srcTblNameVal, srcRecFkVal, notesInList) {
    var url = "maintainPolicy.do?" + commonGetMenuQueryString() + "&process=isPolicyNotesExist"
              + "&policyId=" + getObjectValue("policyId") + "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate");
    // initiate async call
    new AJAXRequest("get", url, '', handleOnIsPolicyNotesExist, false);
}

function handleOnIsPolicyNotesExist(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var policyNotesExist = oValueList[0]["ISPOLICYNOTESEXIST"];
                var el = getSingleObject("policyNotes");
                if (policyNotesExist == "Y") {
                    el.innerHTML = "Y";
                }
                else {
                    el.innerHTML = "";
                }
                maintainNoteImageForAllNoteFields();
            }
        }
    }
}
function handleSpecialHandlingExist(srcTblNameVal, srcRecFkVal) {
    var url = getAppPath() + "/policymgr/maintainPolicy.do?" + commonGetMenuQueryString() + "&process=isPolicySpecialHandlingExist"
              + "&policyId=" + getObjectValue("policyId") + "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate");
    new AJAXRequest("get", url, '', handleOnIsPolicySpecialHandlingExist, false);
    }

function handleOnIsPolicySpecialHandlingExist(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var policySpecialHandlingExist = oValueList[0]["ISPOLICYSPECIALHANDLINGEXIST"];
                var oSpecialHandlingB = getObject("specialHandlingB");
                // Sync the label with updated value, the span id would be:
                // fieldId + ROSPAN1
                var oSpan = getObject("specialHandlingBROSPAN1")
                if (oSpecialHandlingB && oSpan) {
                    if (policySpecialHandlingExist == "Y") {
                        oSpecialHandlingB.value = "Y";
                        oSpan.innerText = "Yes";
                    }
                    else if (policySpecialHandlingExist == "N") {
                        oSpecialHandlingB.value = "N";
                        oSpan.innerText = "No";
                    }
                }
            }
        }
    }
}

//set underwriter display name
function setUnderwriter(name){
    getObject("addlInfo1").value = name;
    var el = getSingleObject("addlInfo1ROSPAN");
    if (el) {
        el.innerText = name;
    }
}

//This function is called when leaving from the page.
function getMenuQueryString(id, url) {
    var tempUrl = "";
    var riskId = getUrlParam(window.location,"riskId");
    var coverageId = getUrlParam(window.location,"coverageId");
    var coverageClassId = getUrlParam(window.location,"coverageClassId");
    if (riskId!=null) {
        tempUrl = tempUrl + "&riskId=" + riskId;
    }
    if (coverageId!=null) {
        tempUrl = tempUrl + "&coverageId=" + coverageId;
    }
    if (coverageClassId!=null) {
        tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
    }
    return tempUrl;
}
function viewComponentAuditHistory(contextId) {
    var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
        + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=policy-component" + "&contextId=" + contextId;
    var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}

//-----------------------------------------------------------------------------
// Instruct display special warning messages.
//-----------------------------------------------------------------------------
function showSpecialWarning() {
    return true;
}

function convertCoverage(transactionCode) {
    captureTransactionDetailsWithEffDate(transactionCode, "submitConvertCoverage");
}

function submitConvertCoverage() {
    if(objectComment2){
        setInputFormField("transactionComment2", objectComment2.value);
    }
    postAjaxSubmit("/transactionmgr/endorseTransaction.do", "convertCoverageTransaction", true, false, handleOnConvertCoverageTransaction);
}

function handleOnConvertCoverageTransaction(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            refreshPage();
        }
    }
}

function handleOnUnloadForDivPopup(divPopFrame) {
    if (italicsFieldId != "NONE") {
        italicsFieldIdList = italicsFieldId;
        commonOnSetButtonItalics(false);
        spliceTheRecordExistsFields();
    }
}
function getRecordExistsUrl() {
    var url = getCSPath() + "/recordexistsmgr/maintainRecordExists.do?process=retrieveRecordExistsIndicator" +
            "&policyId=" + policyHeader.policyId;

    url += "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate +
            "&pageCode=PM_POLICY" +
            "&subSystemId=PMS";
    if (hasObject("policyViewMode")) {
        var policyViewMode = getObjectValue("policyViewMode");
        url = url + "&policyViewMode=" + policyViewMode;
    }
    if (typeof(policyHeader) != 'undefined') {
        var endorsementQuoteId = policyHeader.lastTransactionInfo.endorsementQuoteId;
        if (!isEmpty(endorsementQuoteId) && (endorsementQuoteId != 'null')) {
            url = url + "&endorsementQuoteId=" + endorsementQuoteId;
        }
    }
    return url;
}

function handleExitWorkflow() {
    var transactionLogId = policyHeader.lastTransactionId;
    var url = "maintainPolicy.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
    // initiate async call
    new AJAXRequest("get", url, '', handleOnGetWarningMsg, false);
}

function handleOnUIProcess() {
    mainPageLock.initialLock();
    selectTab()
}

function selectTab() {
    if (isMainPageRefreshedFlg && isDefined(getBtnOperation())) {
        operation = getBtnOperation();
        clearBtnOperation();
    }

    if(hasErrorMessage()) {
        selectTabById(getPreviousTab());
    }else {
        if(getPreviousTab() == "") {
            selectFirstTab();
        }else {
            selectTabById(getCurrentTab());
        }
    }
}

function handleOnShowPageInFrame(tabId) {
    switch(tabId) {
        case "COMPONENT":
			if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "POLCOMPONENTB";
            }
            else {
                italicsFieldId = "POLCOMPONENTQUOTEB";
            }
            showPageInFrame("", "componentDetail", tabId);
            break;
        case "ADDIINS":
            var addiInsUrl = getAppPath() + "/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do?"
                    + commonGetMenuQueryString() + "&process=loadAllAdditionalInsured";

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "ADDINSUREDB";
            }
            else {
                italicsFieldId = "ADDINSUREDQUOTEB";
            }
            showPageInFrame(addiInsUrl, subFrameId, tabId);
            break;
        case "UNDERWRITER":
            var underwriterUrl = getAppPath() + "/policymgr/underwritermgr/maintainUnderwriter.do?" + commonGetMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "ADDLINFOB";
            }
            else {
                italicsFieldId = "ADDLINFOQUOTEB";
            }
            showPageInFrame(underwriterUrl, subFrameId, tabId);
            break;
        case "SPHND":
            var url = getAppPath() + "/policymgr/specialhandlingmgr/maintainSpecialHandling.do?" + commonGetMenuQueryString();
            url += getMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SPECIALHANDLINGB";
            }
            else {
                italicsFieldId = "SPECIALHANDLINGQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "TAIL":
            var tailUrl = getAppPath() + "/tailmgr/maintainTail.do?"
                    + commonGetMenuQueryString() + "&process=loadAllTail";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "TAILB";
            }
            else {
                italicsFieldId = "QUOTETAILB";
            }
            showPageInFrame(tailUrl, subFrameId, tabId);
            break;
        case "MAINTAIN_TAX":
            var url = getAppPath() + "/policymgr/taxmgr/maintainTax.do?"
                    + commonGetMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "MAINTAINTAXB";
            }
            else {
                italicsFieldId = "MAINTAINTAXQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "TAX":
            var viewFundUrl = getAppPath() + "/policymgr/premiummgr/viewFund.do?"
                    + commonGetMenuQueryString() + "&process=loadAllFund&detailType=" + tabId;
            showPageInFrame(viewFundUrl, subFrameId, tabId);
            break;
        case "REINSURANCE":
            var reinsuranceUrl = getAppPath() + "/policymgr/reinsurancemgr/maintainReinsurance.do?"
                    + commonGetMenuQueryString() + "&process=loadAllReinsurance";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "REINSURANCEB";
            }
            else {
                italicsFieldId = "REINSURANCEQUOTEB";
            }
            showPageInFrame(reinsuranceUrl, subFrameId, tabId);
            break;
        case "CHG_EXP_DATE":
            var chgExpDateUrl = getAppPath() + "/transactionmgr/changeTermExpirationDate.do?"
                    + commonGetMenuQueryString();
            showPageInFrame(chgExpDateUrl, subFrameId, tabId);
            break;
        case "LIMITSHARING":
            var limitSharingUrl = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?"
                    + commonGetMenuQueryString() + "&process=loadAllLimitSharing";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SHAREGROUPB";
            }
            else {
                italicsFieldId = "SHAREGROUPQUOTEB";
            }
            showPageInFrame(limitSharingUrl, subFrameId, tabId);
            break;
        case "AUDIT":
            var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                    + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=policy-policy";
            showPageInFrame(viewAuditUrl, subFrameId, tabId);
            break;
        case "TAIL_QUOTE":
            var tailQuoteUrl = getAppPath() + "/policymgr/tailquotemgr/maintainTailQuote.do?"
                    + commonGetMenuQueryString() + "&process=loadAllTailQuoteTransaction";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "TAILQUOTEB";
            }
            else {
                italicsFieldId = "TAILQUOTEQUOTEB";
            }
            showPageInFrame(tailQuoteUrl, subFrameId, tabId);
            break;
        case "SELECT_ADDRESS":
            var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=POLICYHOLDER";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SELECTADDRESSB";
            }
            else {
                italicsFieldId = "SELECTADDRESSGQUOTEB";
            }
            showPageInFrame(selAddrUrl, subFrameId, tabId);
            break;
        case "PAYMENT":
            var paymentUrl = getAppPath() + "/policymgr/premiummgr/viewPayment.do?"
                    + commonGetMenuQueryString() + "&process=loadAllPayment";
            showPageInFrame(paymentUrl, subFrameId, tabId);
            break;
        case "ADMIN_HISTORY":
            var adminhistoryUrl = getAppPath() + "/transactionmgr/viewPolicyAdministratorHistory.do?"
                    + commonGetMenuQueryString() + "&process=loadAllPolicyAdminHistory";
            showPageInFrame(adminhistoryUrl, subFrameId, tabId);
            break;
        case "QUOTE_STATUS":
            var quotestatusUrl = getAppPath() + "/policymgr/processQuoteStatus.do?"
                    + commonGetMenuQueryString() + "&process=loadAllQuoteStatus";
            showPageInFrame(quotestatusUrl, subFrameId, tabId);
            break;
        case "UNDERLYING":
            var url = getAppPath() + "/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do?"
                    + commonGetMenuQueryString() + "&process=loadAllUnderlyingPolicy";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "UNDERLYINGB";
            }
            else {
                italicsFieldId = "UNDERLYINGQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "EXCESS":
            var url = getAppPath() + "/coveragemgr/maintainExcessCoverage.do?" + commonGetMenuQueryString();
            italicsFieldId = "EXCESSB";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "ENTITYDTL":
            var policyId = policyHeader.policyId;
            var transactionLogId = 0;
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var termEffectiveFromDate = policyHeader.termEffectiveFromDate;
            var termEffectiveToDate = policyHeader.termEffectiveToDate;
            var url = getAppPath() + "/transactionmgr/viewProfessionalEntityDetail.do?policyId=" + policyId +
                    "&transactionLogId=" + transactionLogId + "&termBaseRecordId=" + termBaseRecordId +
                    "&termEffectiveFromDate=" + termEffectiveFromDate + "&termEffectiveToDate=" + termEffectiveToDate;
            italicsFieldId = "CROSSREFB";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "MANAGEQUICKPAY":
            var policyId = policyHeader.policyId;
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPay.do?" + commonGetMenuQueryString() +
                    "&policyId=" + policyId + "&termBaseRecordId=" + termBaseRecordId;
            italicsFieldId = "MANUALQPB";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "SHAREDLIMIT":
            var sharedLimitUrl = getAppPath() + "/policymgr/limitsharingmgr/viewSharedLimit.do?"
                    + commonGetMenuQueryString() + "&process=loadAllSharedLimit";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SHAREDLIMITSB";
            }
            else {
                italicsFieldId = "SHAREDLIMITSQUOTEB";
            }
            showPageInFrame(sharedLimitUrl, subFrameId, tabId);
            break;
        case "RENEWAL_FLAG":
            var renewalFlagUrl = getAppPath() + "/policymgr/renewalflagmgr/maintainRenewalFlag.do?" + commonGetMenuQueryString();
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RENEWALFLAGB";
            }
            else {
                italicsFieldId = "RENEWALFLAGQUOTEB";
            }
            showPageInFrame(renewalFlagUrl, subFrameId, tabId);
            break;
        case "POL_UNDERLYING":
            var url = getAppPath() + "/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do?"
                        + commonGetMenuQueryString() + "&process=loadAllUnderlyingCoverage&coverageId"
                        + "&isPolicyLevel=Y";
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "UNDERLYINGPOLB";
            }
            else {
                italicsFieldId = "UNDERLYINGPOLQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
    }
}

//-----------------------------------------------------------------------------
// If 1) sub-tab is swithced,
//    2) primary tab is switched,
//  then auto save logic will be invoked,
//  will check if the data change exists on sub-tab firstly.
//-----------------------------------------------------------------------------
function autoSaveSubTab(toBeSavedTab) {
    // set autoSaveResultType as undefined
    setAutoSavedTabResultType();
    //if policy is official and tab is not allowed to modify, return nodatachange
    if(!policyHeader.wipB && !allowToModifyWhenOfficial(toBeSavedTab)) {
        setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
        return;
    }
    //remove messages on parent screen.
    removeMessagesForFrame();

    if(!isReservedTab(toBeSavedTab)) {
        mainPageLock.lock();
    }

    switch (toBeSavedTab) {
        case "COMPONENT":
            var functionExists = eval("window.commonOnSubmit");
            if(functionExists){
                setInputFormField("callFromSubTabB", "Y");
                commonOnSubmit('SAVEWIP', true, true, false);
            }
            break;
        case "ADDIINS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case "UNDERWRITER":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE',false,true,false,true);
            }
            break;
        case "SPHND":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case "TAIL":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
        case "MAINTAIN_TAX":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE', false, false, false, true);
            }
            break;
        case "REINSURANCE":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case "CHG_EXP_DATE":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('DONE');
            }
            break;
        case "LIMITSHARING":
            var functionExists = eval("getIFrameWindow().submitSave");
            if(functionExists){
                getIFrameWindow().submitSave();
            }
            break;
        case "TAIL_QUOTE":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                if(getIFrameWindow().isPageDataChanged()){
                    getIFrameWindow().commonOnButtonClick('PROCESS')
                }else{
                    autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
                }
            }
            break;
        case "SELECT_ADDRESS":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
        case "QUOTE_STATUS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case "UNDERLYING":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
        case "EXCESS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE',true,true,false,true);
            }
            break;
        case "MANAGEQUICKPAY":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE',true,true,false,true);
            }
            break;
        case "RENEWAL_FLAG":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
        case "POL_UNDERLYING":
        case "TAX":
        case "AUDIT":
        case "PAYMENT":
        case "ADMIN_HISTORY":
        case "ENTITYDTL":
        case "SHAREDLIMIT":
            // no auto save
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
    }

    if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange
            || autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed
            || autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        mainPageLock.unlock();
    }
}

function handleOnSecondaryTabClick(tabId) {
    operation = "switchSecondlyTab";
    // if click current tab, do nothing
    if(tabId == getCurrentTab()) {
        return;
    }

    removeMessagesForFrame();

    // cache tab ids for sub tab further action
    setCacheTabIds(getCurrentTab() + "," + tabId);

    processAutoSaveSubTab(getPreviousTab());

    /**
     *  if autoSaveResultType is undefined, means using ajax to submit sub page, do nothing here
     */
    if(isDefined(autoSaveResultType)) {
        if (autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully) {
            //the form of sub-tab was submitted.
            switchSecondlyTabFlg = true;
        }else if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
            /**
             * if no any data change exists on sub-tab, check if require to save policy page
             */
            //Issue 190191: clear operation value when in no data change situation.
            clearOperationForTabStyle();
            if(isChanged && requireSaveChangedMainPage(tabId)) {
                if (confirm(getMessage("pm.maintainPolicy.clickOk.changesSave.confirm"))) {
                    autoSaveWip();
                }else {
                    setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
                    return;
                }
            }else {
                selectTabById(tabId);
            }
        }
        else if (autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed ||
                autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
            //common validation failed on the Grid/Form of sub-Tab
            //No actions.
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }
    }
}

function callBackAutoSaveForFrame(autoSaveResult) {
    var switchSecondlyTabFlgLoc = switchSecondlyTabFlg;
    switchSecondlyTabFlg = false;
    var switchPrimaryTabFlgLoc = switchPrimaryTabFlg;
    switchPrimaryTabFlg = false;

    if(switchSecondlyTabFlgLoc || switchPrimaryTabFlgLoc){
        operation = undefined;
    }

    updateMainTokenWithIframe(getObject(subFrameId));

    if(autoSaveResult) {
        removeMessagesForFrame();
		handleOnItalicTabStyle();

        if(switchPrimaryTabFlgLoc){
            // check if require to save policy page
            if(isPageDataChanged()) {
                autoSaveWip();
            }else {
                showProcessingImgIndicator();
                setWindowLocation(nextPrimaryTabAction);
            }
        }

        if(switchSecondlyTabFlgLoc) {
            // check if require to save policy page
            if(isPageDataChanged() && requireSaveChangedMainPage(getCurrentTab())) {
                if (confirm(getMessage("pm.maintainPolicy.clickOk.changesSave.confirm"))) {
                    autoSaveWip();
                }else {
                    setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
                    if(requiredSubmitMainPage(getPreviousTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                            getIFrameWindow().isNeedToRefreshParentB()) {
                        refreshPage();
                    }
                }
                return;
            }else {
                if(requiredSubmitMainPage(getPreviousTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                        getIFrameWindow().isNeedToRefreshParentB()) {
                    setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
                    refreshPage();
                    return;
                }
                selectTabById(getCurrentTab());
            }
        }

        // if it's not switch primary tab or secondly tab but click "SAVE WIP" etc buttons
        if(!new RegExp("^switch.*Tab$").test(operation) && isDefined(operation)) {
            processMainPageAfterAutoSaveSubTab();
        }
    }else {
        if(switchPrimaryTabFlgLoc){
            nextPrimaryTabId = "";
            nextPrimaryTabAction = "";
            return;
        }

        if(switchSecondlyTabFlgLoc){
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }
    }
}

function processMainPageAfterAutoSaveSubTab() {
    switch(operation) {
        case "WIP":
        case "SAVEWIP":
            if(getCurrentTab() == "CHG_EXP_DATE" && isUndefined(currentSubmitAction) && operation == "WIP"){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOL", getFormActionAttribute());
                document.forms[0].process.value = "savePolicy";
            }
            manualSaveWIP("PM_PT_VIEWPOL", "savePolicy");
            break;
        case "OFFICIAL":
            if(getCurrentTab() == "CHG_EXP_DATE" && isUndefined(currentSubmitAction)){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOL", getFormActionAttribute());
                document.forms[0].process.value = "savePolicy";
            }
            manualSaveOfficial();
            break;
        case "RATE":
            if(getCurrentTab() == "CHG_EXP_DATE" && isUndefined(currentSubmitAction)){
                currentSubmitAction = "RATE";
            }
            setInputFormField("processRatingB", "Y");
            manualSaveWIP("PM_PT_VIEWPOL", "savePolicy");
            break;
        case "PREVIEW":
            onPreviewButtonClick();
            break;
        case "OOS_CHANGE":
            setInputFormField("ooseChangeB", "Y");
            manualSaveWIP("", "loadPolicyDetail");
            break;
        case 'OOS_DELETE':
            setInputFormField("ooseChangeB", "N");
            manualSaveWIP("", "deleteOosPolicyDetail");
            break;
        default:
            handleSaveOptionSelection(operation);
    }
}

/**
 * some tabs required to save changed policy page before open it
 */
var requireSaveChangedMainPageTabArr = ["UNDERWRITER"];
function requireSaveChangedMainPage(tabId) {
    return $.inArray(tabId, requireSaveChangedMainPageTabArr) > -1 ? true : false;
}

function preButtonClick(asBtn) {
    if(isReservedTab(getCurrentTab())) {
        operation = asBtn;
        return true;
    }
    var proceed = false;
    switch (asBtn) {
        case "PREVIEW":
            operation = asBtn;
            processAutoSaveSubTab(getCurrentTab());
            if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
                proceed = true;
            }
            break;
        default:
            proceed = true;
            break;
    }
    return proceed;
}

function currentTabIsHideForCurrentRow(tabId){
    var isHideB = true;
    switch (tabId) {
        case 'CHG_EXP_DATE':
            var indValue = "Y";
            if(hasObject('isChgTermExpDateAvailable')){
                indValue=getObjectValue('isChgTermExpDateAvailable');
            }
            isHideB = (indValue == 'N') ? true : false;
            break;
        case 'TAIL_QUOTE':
            var indValue = "Y";
            if(hasObject('isTailQuoteAvailable')){
                indValue=getObjectValue('isTailQuoteAvailable');
            }
            isHideB = (indValue == 'N') ? true : false;
            break;
        default:
            isHideB = false;
            break;
    }
    return isHideB;
}

function handleOnRevertSubTabChangesBeforeLeave(tabId) {
    switch(tabId) {
        case "TAIL":
            if (eval("getIFrameWindow().doBeforeAndCloseTailPage")) {
                getIFrameWindow().doBeforeAndCloseTailPage();
            }
            break;
    }
}
