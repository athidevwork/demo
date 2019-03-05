//-----------------------------------------------------------------------------
// Javascript file for maintainQuickPay.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 23, 2010
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/10/2017       wli     issue 180675 - Initialized the parameter which named "subTabGridId" and used
//                                         "getOpenCtxOfDivPopUp()" to call openDivPopup() for UI change.
// 12/13/2017       wrong   issue 190191 - Delete variable subTabGridId due to deleted function ignoreErrorMessageCheck
//                                         in commonSecondlyTab.js.
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
// To handle the button click event.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            if (commonValidateForm()) {
                document.forms[0].process.value = "loadAllQuickPay";
                submitFirstForm();
            }
            break;
        case "QPDETAILS":
            var url = getAppPath() + "/policymgr/quickpaymgr/loadAllQuickPayDetail.do?"
                    + "&process=loadAllQuickPayDetail"
                    + "&policyId=" + transHistoryListGrid1.recordset("CPOLICYID").value
                    + "&termBaseId=" + transHistoryListGrid1.recordset("CTERMBASERECORDID").value
                    + "&origTransId=" + transHistoryListGrid1.recordset("CTRANSACTIONLOGID").value;
            var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "TRANDETAIL":
            getViewTransactionDetailInputParameter();
            break;
        case 'REMOVEQP':
            var submitRownum = transHistoryListGrid1.recordset("ID").value;
            setInputFormField("submitRownum", submitRownum);
            document.forms[0].process.value = "removeQuickPayDiscount";
            submitForm(false);
            break;
        case 'GIVEQPPERCENT':
            setInputFormField("giveMode", "percent");
            validateForAddQuickPayDiscount();
            break;
        case "GIVEQPDOLLAR":
            setInputFormField("giveMode", "dollar");
            validateForAddQuickPayDiscount();
            break;
        case 'CLOSEPAGE':
            isWipExist();
            break;
    }
}

//-----------------------------------------------------------------------------
// To handle the submit event.
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "completeQuickPayTransaction";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// To validate for add quick pay discount.
// Currently it used to check if the policy is short term policy.
//-----------------------------------------------------------------------------
function validateForAddQuickPayDiscount() {
    // set url
    var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPay.do?"
            + "&process=validateForAddQuickPayDiscount"
            + "&policyId=" + transHistoryListGrid1.recordset("CPOLICYID").value
            + "&termBaseId=" + transHistoryListGrid1.recordset("CTERMBASERECORDID").value
            + "&origTransId=" + transHistoryListGrid1.recordset("CTRANSACTIONLOGID").value;
    new AJAXRequest("get", url, "", handleValidateForAddQuickPayDiscountDone, false);
}

function handleValidateForAddQuickPayDiscountDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var isAddQuickPayAllowed = oValueList[0]["ISADDQUICKPAYALLOWED"];

                if (isAddQuickPayAllowed == 'Y') {
                    handleError(getMessage("pm.manageQuickPay.shortTermPolicy.error"));
                    return false;
                }
                else {
                    var giveMode = getObjectValue("giveMode");
                    if (giveMode && giveMode == "dollar")
                    {
                        var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPayDetail.do?"
                                + "&process=loadAllQuickPayTransactionDetail"
                                + "&policyId=" + transHistoryListGrid1.recordset("CPOLICYID").value
                                + "&termBaseId=" + transHistoryListGrid1.recordset("CTERMBASERECORDID").value
                                + "&origTransId=" + transHistoryListGrid1.recordset("CTRANSACTIONLOGID").value
                                + "&lastQpTransLogId=0"
                                + "&openMode=ADD_QPDISCOUNT";
                        var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
                    }

                    if (giveMode && giveMode == "percent") {
                        var submitRownum = transHistoryListGrid1.recordset("ID").value;
                        setInputFormField("submitRownum", submitRownum); 
                        document.forms[0].process.value = "addQuickPayDiscount";
                        submitForm(false);
                    }

                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To prepare the input parameter for view transaction detail
//-----------------------------------------------------------------------------
function getViewTransactionDetailInputParameter() {
    // set url
    var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPay.do?"
            + "&process=getLastQuickPayTransactionLogId"
            + "&policyId=" + transHistoryListGrid1.recordset("CPOLICYID").value
            + "&termBaseId=" + transHistoryListGrid1.recordset("CTERMBASERECORDID").value
            + "&origTransId=" + transHistoryListGrid1.recordset("CTRANSACTIONLOGID").value;
    new AJAXRequest("get", url, "", handleGetViewTransactionDetailInputParameterDone, false);
}

function handleGetViewTransactionDetailInputParameterDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var lastQuickPayTransactionLogId = oValueList[0]["LASTQPTRANSLOGID"];
                var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPayDetail.do?"
                        + "&process=loadAllQuickPayTransactionDetail"
                        + "&policyId=" + transHistoryListGrid1.recordset("CPOLICYID").value
                        + "&termBaseId=" + transHistoryListGrid1.recordset("CTERMBASERECORDID").value
                        + "&origTransId=" + transHistoryListGrid1.recordset("CTRANSACTIONLOGID").value
                        + "&lastQpTransLogId=" + lastQuickPayTransactionLogId
                        + "&openMode=VIEW_ONLY";
                var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To check if the WIP data exist in the page, the WIP data are created by Give QP [%] or Remove QP option.
//-----------------------------------------------------------------------------
function isWipExist() {
    if (hasObject("wipQpTransLogId") && !isEmpty(getObjectValue("wipQpTransLogId"))) {
        var wipQpTransLogId = getObjectValue("wipQpTransLogId");
        if (wipQpTransLogId > 0) {
            if (confirm(getMessage("pm.manageQuickPay.unsavedData.error"))) {
                var url = getAppPath() + "/policymgr/quickpaymgr/maintainQuickPay.do?"
                        + "&process=deleteQuickPayWip"
                        + "&qpTransLogId=" + wipQpTransLogId;
                new AJAXRequest("get", url, "", handleDeleteQuickPayWipDone, false);
            }
        }
        else {
            closeThisDivPopup(true);
        }
    }
    else {
        closeThisDivPopup(true);
    }
}

function handleDeleteQuickPayWipDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            closeThisDivPopup(true);
        }
    }
}

