//-----------------------------------------------------------------------------
// for cancellation
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/10/2011       ryzhao      113558 - Modified several functions to meet the new requirement
//                                       of adding a new carrier field in the cancellation page.
// 01/14/2011       ryzhao      113558 - Add riskTypeCode parameter to performCancellation() function and validatePrePerformCancellation() function
//                                       for new carrier is available checking.
//                                       Remove all codes which are related for loading riskHeader logic.
// 01/18/2010       syang       105832 - Modified handleOnCaptureCancellationDone() to submit ddl information.
// 03/31/2011       dzhang      94232  - Modified performCancellation.
// 06/21/2013       adeng       117011 - Modified handleOnCaptureCancellationDone() to
//                                       1) add a new parameter "transactionComment2".
//                                       2) set value of the new parameter "transactionComment2"
//                                          to input form field "newTransactionComment2".
// o5/29/2014       jyang       149970 - Added param "cancelDesc" for  performCancellation,validatePrePerformCancellation,
//                                       handleOnValidatePrePerformCancellation,captureCancellationDetail functions to
//                                       pass the riskName/coverageDesc/coverageClassDesc to the captureCancellationDetail
//                                       page.
// 07/25/2014       awu         152034 - Modified to send cancellation level to refreshPage.
// 08/25/2014       awu         152034 - Modified to send flag to refreshPage only when we cancel policy.
// 12/30/2014       jyang       157750 - Modified captureCancellationDetail() and validatePrePerformCancellation() to
//                                       encode the riskTypeCode and cancelDesc before append them into URL.
// 07/05/2016       eyin        176476 - Modified handleOnCancellationDone(), to check if future cancellation records
//                                       exist, if yes, then open Future Cancellation Details Popup.
// 03/10/2017       wrong       180675 - Modified code to open div popups in primary page in new UI tab style.
// 11/02/2018       clm         195889 -  Grid replacement for getChanges(ddlListGrid1) when using JqxGrid in handleOnCaptureCancellationDone
//-----------------------------------------------------------------------------
var cancelDivId;
var currentCancelLevel;
function performCancellation(cancelLevel, baseId, cancelItemEffDate, cancelItemExpDate, accountingDate, riskTypeCode, isIbnrRisk, cancelDesc) {
    currentCancelLevel = cancelLevel;
    // Check if there are any data changed
    var isOkToProceed = commonIsOkToChangePages("", "");
    if (!isOkToProceed) {
        return;
    }

    //set initial values
    setInputFormField("cancellationLevel", cancelLevel);
    setInputFormField("baseId", baseId);
    setInputFormField("cancelDesc", cancelDesc);
    setInputFormField("isIbnrRisk", isIbnrRisk);
    var cancelAccountingDate = accountingDate;
    //set initial values for tail cancellation validation
    //set validation needed field
    if (cancelLevel == "TAIL") {
        cancelItemEffDate = "";
        cancelItemExpDate = "";
        var XMLData = coverageListGrid1;
        if (!isEmptyRecordset(XMLData.recordset)) {
            var absPosition = XMLData.recordset.AbsolutePosition;
            first(XMLData);
            while (!XMLData.recordset.eof) {
                if (XMLData.recordset('CSELECT_IND').value == '-1') {
                    if (cancelItemEffDate != "") {
                        cancelItemEffDate = cancelItemEffDate + ",";
                        cancelItemExpDate = cancelItemExpDate + ",";
                    }
                    cancelItemEffDate = cancelItemEffDate  + XMLData.recordset('CEFFECTIVEFROMDATE').value;
                    cancelItemExpDate = cancelItemExpDate  + XMLData.recordset('CEFFECTIVETODATE').value
                }
                next(XMLData);
            }
            first(XMLData);
            XMLData.recordset.move(absPosition - 1);
        }
    }
    setInputFormField("cancelItemEffDate", cancelItemEffDate);
    setInputFormField("cancelItemExpDate", cancelItemExpDate);

    if(!cancelAccountingDate){
        cancelAccountingDate = "";
    }

    validatePrePerformCancellation(cancelLevel, cancelAccountingDate, baseId, riskTypeCode, cancelDesc);
}

function validatePrePerformCancellation(cancelLevel, accountingDate, baseId, riskTypeCode, cancelDesc) {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performCancellation.do?process=validatePrePerformCancellation&"
        + "cancellationLevel=" + cancelLevel
        + "&baseId=" + baseId
        + "&riskTypeCode=" + encodeURIComponent(riskTypeCode)
        + "&" + commonGetMenuQueryString();

    if (isStringValue(accountingDate)) {
        url = url + "&accountingDate=" + accountingDate;
    }
    if (isStringValue(cancelDesc)) {
        url = url + "&cancelDesc=" + encodeURIComponent(cancelDesc);
    }
    new AJAXRequest("get", url, '', handleOnValidatePrePerformCancellation, false);
}

function handleOnValidatePrePerformCancellation(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            var cancelLevel = oValueList[0]["CANCELLATIONLEVEL"];
            var cancelAccountingDate = oValueList[0]["ACCOUNTINGDATE"];
            var policyTypeCode = oValueList[0]["POLICYTYPECODE"];
            var riskTypeCode = oValueList[0]["RISKTYPECODE"];
            var termEffDate = oValueList[0]["TERMEFFECTIVEFROMDATE"];
            var cancelDesc = oValueList[0]["CANCELDESC"];

            captureCancellationDetail(cancelLevel, cancelAccountingDate, policyTypeCode, riskTypeCode, termEffDate, cancelDesc);
        }
    }
}

function captureCancellationDetail(cancelLevel, accountingDate, policyTypeCode, riskTypeCode, termEffDate, cancelDesc) {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performCancellation.do?process=captureCancellationDetail&"
        + "cancellationLevel=" + cancelLevel
        + "&policyTypeCode=" + policyTypeCode
        + "&riskTypeCode=" + encodeURIComponent(riskTypeCode)
        + "&termEff=" + termEffDate
        + "&" + commonGetMenuQueryString();

    if (isStringValue(accountingDate)) {
        url = url + "&accountingDate=" + accountingDate;
    }
    if (isStringValue(cancelDesc)) {
        url = url + "&cancelDesc=" + encodeURIComponent(cancelDesc);
    }

    cancelDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
    commonOnPutParentWindowOfDivPopup(cancelDivId);
}

function handleOnCaptureCancellationDone(cancelDate, accountDate, cancelType,
                                         cancelReason, cancelMethod, cancelAddOccupant,
                                         cancelComments, transactionComment2, amalgamationB,amalgamationMethod,
                                         amalgamationTo,claimsAccessIndicator,successMessage,carrier,
                                         markAsDdl, ddlReason, ddlComments, ddlListGrid1) {
    setInputFormField("cancellationDate", cancelDate);
    setInputFormField("accountingDate", accountDate);
    setInputFormField("cancellationType", cancelType);
    setInputFormField("cancellationReason", cancelReason);
    setInputFormField("cancellationMethod", cancelMethod);
    setInputFormField("cancellationAddOccupant", cancelAddOccupant);
    setInputFormField("cancellationComments", cancelComments);
    setInputFormField("newTransactionComment2", transactionComment2);
    setInputFormField("amalgamationB", amalgamationB);
    setInputFormField("amalgamationMethod", amalgamationMethod);
    setInputFormField("amalgamationTo", amalgamationTo);
    setInputFormField("claimsAccessIndicator", claimsAccessIndicator);
    setInputFormField("successMessage", successMessage);
    setInputFormField("carrier", carrier);
    setInputFormField("markAsDdl", markAsDdl);
    setInputFormField("ddlReasonForRisk", ddlReason);
    setInputFormField("ddlCommentsForRisk", ddlComments);
    //Submit the discipline decline list
    if (!isEmptyRecordset(ddlListGrid1.recordset)) {
        if (!window["useJqxGrid"]) {
            document.forms[0].txtXML.value = getChanges(ddlListGrid1);
        }
        else {
            if (getUIStyle() == 'B') {
                document.forms[0].txtXML.value = window.frames[$("iframe").attr("id")].window.getChanges(ddlListGrid1);
            }
            else {
                document.forms[0].txtXML.value = $(".popupDialogDiv").find("iframe").get(0).contentWindow.getChanges(ddlListGrid1);
            }
        }
    }
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performCancellation.do?";
    postAjaxSubmit(url, "performCancellation", false, false, handleOnCancellationDone);
}

function handleOnCancellationDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            // parse and check if need to open future cancellation Detail page
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var futureCancellationExist = oValueList[0]["FUTURECANCELLATIONEXISTB"];
                if (futureCancellationExist == "Y") {
                    closeProcessingDivPopup();
                    futureCancellationDetails();
                    return;
                }
            }

            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, refresh page
            else {
                isCancelRiskRelation(data);

                // The cancellation is sucessfully, open amalgamated policy in new page
                var oPolicyNo = getObject("amalgamationTo");
                var amalgamationB = getObject("amalgamationB").value;
                if (amalgamationB == "Y" && oPolicyNo) {
                    //                    openPolicyInNewPage(oPolicyNo.value);
                    var oMessage = getObject("successMessage");
                    if (oMessage && !isEmpty(oMessage.value)) {
                        alert(oMessage.value);
                    }
                }
                if (currentCancelLevel == "POLICY") {
                    refreshPage("ALL");
                }
                else {
                    refreshPage();
                }

            }
        }
    }
}

function futureCancellationDetails(status) {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/futureCancellationDetail.do?process=captureFutureCancellationDetail"
            + "&" + commonGetMenuQueryString()+"&status="+status;

    var futurePopup = openDivPopup("", url, true, true, null, null, 800, 520, "", "", "", false);
}

function isCancelRiskRelation(data) {
    var oValueList = parseXML(data);
    if (oValueList.length > 0) {
        var cancelLevel = oValueList[0]["CANCELLATIONLEVEL"];
        if (cancelLevel && cancelLevel == "RISK RELATION") {
            setInputFormField("riskEffectiveFromDate", oValueList[0]["RISKEFFECTIVEFROMDATE"]);
            setInputFormField("riskEffectiveToDate", oValueList[0]["RISKEFFECTIVETODATE"]);
            setInputFormField("riskCountyCode", oValueList[0]["RISKCOUNTYCODE"]);
            setInputFormField("currentRiskTypeCode", oValueList[0]["CURRENTRISKTYPECODE"]);
            setInputFormField("origRiskEffectiveFromDate", oValueList[0]["ORIGRISKEFFECTIVEFROMDATE"]);
        }
    }
}