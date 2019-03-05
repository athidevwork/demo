//-----------------------------------------------------------------------------
// Javascript file
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   July 01, 2013
// Author: adeng
//
// Revision Date    Revised By  Description
//---------------------------------------------------------------------------------------------------------------------
// 07/01/2013       adeng       117011 - Modified submitCopy() and submitAccept() to set value of new field
//                                       "transactionComment2" to input form field "transactionComment2".
// 08/26/2016       wdang       167534 - Added support for Renewal Quote.
// 12/20/2016       tzeng       166929 - To prompt message if soft validation exists when accept or copy quote.
//                                       1) Added handleOnConfirmIfSoftValidationExists() to handle ajax callback.
//                                       2) Added variable continueCopyOrAcceptB.
//                                       3) Modified copyQuote() and acceptQuote() to add confirmIfSoftValidationExists()
//                                          at the code beginning.
//---------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------
// for reactive quote
//-----------------------------------------------------------------------------
var continueCopyOrAcceptB = true;
function reactiveQuote() {
    submitReactive();
}

function submitReactive() {
    postAjaxSubmit("/policymgr/maintainQuote.do", "reactiveQuote");
}

//-----------------------------------------------------------------------------
// for copy quote
//-----------------------------------------------------------------------------
function copyQuote(quoteCycleCode) {
    confirmIfSoftValidationExists();
    if (!continueCopyOrAcceptB) {
        return;
    }
    var quoteCycleCode = quoteCycleCode ? quoteCycleCode : policyHeader.quoteCycleCode;
    setInputFormField("quoteCycleCode", quoteCycleCode);
    captureTransactionDetails("QUOTE", "submitCopy");
}

function submitCopy() {
    var quoteCycleCode;
    if (hasObject("quoteCycleCode")){
        quoteCycleCode = getObjectValue("quoteCycleCode");
    }
    if (quoteCycleCode == "RN") {
        postAjaxSubmit("/quotemgr/maintainQuoteTransfer.do", "performCopy", false, false, handleOnCopyDone);
    }
    else {
        if (objectComment2) {
            setInputFormField("transactionComment2", objectComment2.value);
        }
        if (getSysParmValue("PM_QT_CM_OCC_CONV") == 'Y' &&
                confirm(getMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation"))) {
            convertQuoteCoverage();
        }
        else {
            postAjaxSubmit("/policymgr/maintainQuote.do", "copyQuote", false, false, handleOnCopyDone);
        }
    }
}

function convertQuoteCoverage() {
    var policyId = policyHeader.policyId;
    var termEffectiveFromDate = policyHeader.termEffectiveFromDate;
    var termEffectiveToDate = policyHeader.termEffectiveToDate;

    var path = getAppPath() +
               "/policymgr/selectQuoteRiskCoverage.do?" +
               commonGetMenuQueryString() +
               "&policyId=" + policyId +
               "&effectiveFromDate=" + termEffectiveFromDate +
               "&effectiveToDate=" + termEffectiveToDate;
    var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "selectQuoteRiskCoverage", false);
}

function handleOnSelectQuoteRiskCoverage(retroactiveDate, coverageList) {
    setInputFormField("retroactiveDate", retroactiveDate);
    setInputFormField("coverageList", coverageList);
    postAjaxSubmit("/policymgr/maintainQuote.do", "copyQuote", false, false, handleOnCopyDone);
}

function handleOnCopyDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var copiedQuoteNo;
            var copiedQuoteErrorB;
            var copiedQuoteErrorTrans;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            /* Parse xml and get inital values(s) */
            var oValue = parseXML(data);
            /* Set default value which fieldID is match with object's attribute name */
            if (oValue.length > 0) {
                if (!isEmpty(oValue[0]["ISCOPYQUOTEERROR"])) {
                    copiedQuoteErrorB = oValue[0]["ISCOPYQUOTEERROR"];
                }
                if (!isEmpty(oValue[0]["COPYQUOTEERRORTRANS"])) {
                    copiedQuoteErrorTrans = oValue[0]["COPYQUOTEERRORTRANS"];
                }
                if (!isEmpty(oValue[0]["COPIEDQUOTENO"])) {
                    copiedQuoteNo = oValue[0]["COPIEDQUOTENO"];
                }
                else {
                    return;
                }
            }

            if (copiedQuoteErrorB=="Y") {
                showCopyQuoteErrors(copiedQuoteNo, copiedQuoteErrorTrans);
            }
            else {
                loadCopiedQuote(copiedQuoteNo);
            }
        }
    }
}

function loadCopiedQuote(copiedQuoteNo) {
    var url = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + copiedQuoteNo;
    setWindowLocation(url);
}

function showCopyQuoteErrors(copiedQuoteNo, copiedQuoteErrorTrans) {
    setInputFormField("copiedQuoteErrorTrans", copiedQuoteErrorTrans);
    var path = getAppPath() +
               "/transactionmgr/viewCopyQuoteValidationError.do?" +
               commonGetMenuQueryString() +
               "&remapTransctionId=" + copiedQuoteErrorTrans +
               "&remapPolicyNo=" + copiedQuoteNo;

    var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "showCopyQuoteErrors", false);
}

function handleOnShowCopyQuoteErrors(copiedQuoteNo) {
    loadCopiedQuote(copiedQuoteNo);
}


//-----------------------------------------------------------------------------
// for accept quote
//-----------------------------------------------------------------------------
function acceptQuote(quoteCycleCode) {
    confirmIfSoftValidationExists();
    if (!continueCopyOrAcceptB) {
        return;
    }
    var quoteCycleCode = quoteCycleCode ? quoteCycleCode : policyHeader.quoteCycleCode;
    setInputFormField("quoteCycleCode", quoteCycleCode);
    if (quoteCycleCode == "RN") {
        postAjaxSubmit("/quotemgr/maintainQuoteTransfer.do", "performApply", false, false, handleOnAcceptDone);
    }
    else {
        if (getSysParmValue("PM_USE_QT_TRANS_WIND") == 'Y') {
            captureTransactionQuoteDetails("QUOTE", "submitAccept");
        }
        else {
            captureTransactionDetails("QUOTE", "submitAccept");
        }
    }
}

function submitAccept() {
    if(objectComment2){
        setInputFormField("transactionComment2", objectComment2.value);
    }
    postAjaxSubmit("/policymgr/maintainQuote.do", "acceptQuote", false, false, handleOnAcceptDone);
}

function handleOnAcceptDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var parallelPolNo;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, we're done
            else {
                /* Parse xml and get inital values(s) */
                var oValue = parseXML(data);

                /* Set default value which fieldID is match with object's attribute name */
                if (oValue.length > 0) {
                    if (!isEmpty(oValue[0]["PARALLELPOLNO"])) {
                        parallelPolNo = oValue[0]["PARALLELPOLNO"];
                    }
                    else {
                        return;
                    }
                }
                var url = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + parallelPolNo;
                setWindowLocation(url);
            }
        }
    }
}

function confirmIfSoftValidationExists() {
    if (getSysParmValue("PM_CHECK_SOFT_VAL_B") == "Y") {
        var url = getAppPath() + "/policymgr/validationmgr/viewsoftvalidation.do?"
                + commonGetMenuQueryString() + "&process=hasSoftValidationExists";
        new AJAXRequest("post", url, "", handleOnConfirmIfSoftValidationExists, false);
    }
}

function handleOnConfirmIfSoftValidationExists(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var oValue = parseXML(data);
            if (!handleAjaxMessages(data) && processedConfirmationMessages && oValue.length > 0 &&
                !isEmpty(oValue[0]["SOFTVALIDATIONB"]) && oValue[0]["SOFTVALIDATIONB"] == "Y") {
                continueCopyOrAcceptB = false;
            }
            else {
                continueCopyOrAcceptB = true;
            }
        }
    }
}
