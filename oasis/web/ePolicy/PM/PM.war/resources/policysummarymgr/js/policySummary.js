//-----------------------------------------------------------------------------
// Javascript file for policySummary.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   Dec 24, 2015
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/24/2015       wdang       168069 - Initial Version.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
//-----------------------------------------------------------------------------

var italicsFieldId = "NONE";
var currentRiskId = null;
var currentCoverageId = null;
var currentCoverageClassId = null;

function handleOnLoad() {
    setInputFormField("needToHandleExitWorkFlow", "Y");
    invokeWorkflow();
}

function selectRowInGridOnPageLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("policySummaryListGrid")).then(function(){
        selectFirstRowInGrid("policySummaryListGrid");
    });
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOLICYSUMMARY", getFormActionAttribute());
            document.forms[0].process.value = "savePolicySummary";
            loadSaveOptions("PM_POLICY_SUMMARY", "submitForm");
            proceed = false;
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOLICYSUMMARY", getFormActionAttribute());
            document.forms[0].process.value = "savePolicySummary";

            handleSaveOptionSelection("WIP");
            break;
        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWPOLICYSUMMARY", getFormActionAttribute());
            document.forms[0].process.value = "savePolicySummary";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;
        default:
            proceed = false;
    }
    return proceed;
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
        case "REINSTATE":
            effFromDate = getObjectValue("termEffectiveFromDate");
            effToDate = getObjectValue("termEffectiveToDate");
            var policyStatus = policyHeader.policyStatus;
            var termBaseRecordId = policyHeader.termBaseRecordId;
            performReinstate("POLICY", termBaseRecordId, policyHeader.policyId, effFromDate, effToDate, policyStatus, "");
            break;
        case "AUDIT":
            var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                    + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=policy-policy";
            var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
            break;
        case "CHG_EXP_DATE":
            var chgExpDateUrl = getAppPath() + "/transactionmgr/changeTermExpirationDate.do?"
                    + commonGetMenuQueryString();
            var divPopupId = openDivPopup("", chgExpDateUrl, true, true, "", "", 600, 500, "", "", "", false);
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
        case 'EAPP':
            var spolicyId = getObjectValue("policyId");
            var sTermBaseRecordId = policyHeader.termBaseRecordId;
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
                    + "&termId=" + policyHeader.termBaseRecordId
                    + "&termEff=" + policyHeader.termEffectiveFromDate
                    + "&termExp=" + policyHeader.termEffectiveToDate
                    + "&transEff=" + transEffDate;
            var divPopupId = openDivPopup("", processErpUrl, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        default:break;
    }
}

//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    return policySummaryListGrid1.recordset("CLOCATION").value;
}

//-----------------------------------------------------------------------------
// Navigate to risk/riskSummary page.
//-----------------------------------------------------------------------------
function navigateToRiskTab(riskId) {
    var riskTabIds = {
        'PM_PT_VIEWRISK' : '~/riskmgr/maintainRisk.do',
        'PM_PT_VIEWRISKSUMMARY' : '~/riskmgr/viewRiskSummary.do'
    };
    $.each(riskTabIds, function(key, value) {
        var obj = getObject(key);
        if (!isElementHidden("obj")) {
            currentRiskId = riskId;
            currentCoverageId = "";
            currentCoverageClassId = "";
            doMenuItem(key,value);
        }
    });
}

//-----------------------------------------------------------------------------
// Navigate to coverage page.
//-----------------------------------------------------------------------------
function navigateToCoverageTab(riskId, coverageId) {
    currentRiskId = riskId;
    currentCoverageId = coverageId;
    currentCoverageClassId = "";
    doMenuItem('PM_PT_VIEWCVG','~/coveragemgr/maintainCoverage.do');
}

//This function is called when leaving from the page.
function getMenuQueryString(id, url) {
    var tempUrl = "";
    var riskId = currentRiskId == null ? getUrlParam(window.location,"riskId") : currentRiskId;
    var coverageId = currentCoverageId == null ? getUrlParam(window.location,"coverageId") : currentCoverageId;
    var coverageClassId = currentCoverageClassId == null ? getUrlParam(window.location,"coverageClassId") : currentCoverageClassId;
    if (!isEmpty(riskId)) {
        tempUrl = tempUrl + "&riskId=" + riskId;
    }
    if (!isEmpty(coverageId)) {
        tempUrl = tempUrl + "&coverageId=" + coverageId;
    }
    if (!isEmpty(coverageClassId)) {
        tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
    }
    return tempUrl;
}

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
    "&pageCode=PM_POLICY_SUMMARY" +
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
    var url = "viewPolicySummary.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
    // initiate async call
    new AJAXRequest("get", url, '', handleOnGetWarningMsg, false);
}