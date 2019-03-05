//-----------------------------------------------------------------------------
// for reinstate
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/21/2013       adeng       117011 - Modified submitReinstate() to set value of new field
//                                       "transactionComment2" to input form field "transactionComment2".
//-----------------------------------------------------------------------------
var reinstateDivId;
var vReinstateLevel;
var vBaseId;
var vSelectedId;
var vItemEffDate;
var vItemtoDate;
var vStatustype;
var vCancelTransactionId;
var vRiskRelId;
function performReinstate(reinstateLevel, baseId, selectedId, itemEffDate, itemtoDate, statustype, cancelTransactionId) {
    vReinstateLevel = reinstateLevel;
    vBaseId = baseId;
    vSelectedId = selectedId;
    vItemEffDate = itemEffDate;
    vItemtoDate = itemtoDate;
    vStatustype = statustype;
    vCancelTransactionId = cancelTransactionId;
    if (vReinstateLevel == "RISK RELATION") {
        vRiskRelId = baseId;
        captureTransactionDetails("REINSTATE", "submitReinstate");
    }
    else {
        validateStatusAndTerm();
    }
}

function validateStatusAndTerm() {
    var url = getAppPath() + "/transactionmgr/reinstateprocessmgr/performReinstate.do?process=validateStatusAndTerm" +
              '&' + commonGetMenuQueryString() + '&reinstateLevel=' + vReinstateLevel +
              '&baseId=' + vBaseId + '&itemEffDate=' + vItemEffDate + '&statustype=' +
              vStatustype + '&itemtoDate=' + vItemtoDate + "&transactionLogId=" + vCancelTransactionId;
    new AJAXRequest("get", url, '', handleOnValidateStatusAndTerm, false);
}

function handleOnValidateStatusAndTerm(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var lowReinstateLevel = vReinstateLevel.toLowerCase();
            var parms = new Array();
            parms[0] = lowReinstateLevel;
            if (confirm(getMessage("pm.maintainReinstate.confirm.continue", parms)))
                validateReinstate();
        }
    }
}


function validateReinstate() {
    var url = getAppPath() + "/transactionmgr/reinstateprocessmgr/performReinstate.do?process=validateReinstateProcess" +
              '&reinstateLevel=' + vReinstateLevel + '&baseId=' + vBaseId + '&selectedId=' + vSelectedId +
              '&itemEffDate=' + vItemEffDate + '&statustype=' + vStatustype + '&itemtoDate=' + vItemtoDate +
              "&" + commonGetMenuQueryString();
    new AJAXRequest("get", url, '', handleOnReinstateValidate, false);
}

function handleOnReinstateValidate(ajax) {

    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            captureTransactionDetails("REINSTATE", "submitReinstate");
        }
    }
}

function submitReinstate() {
    setInputFormField("acctDt", objectAccountingDate.value);
    setInputFormField("comments", objectComment.value);
    setInputFormField("transactionComment2", objectComment2.value);
    setInputFormField("reinstateLevel", vReinstateLevel);
    setInputFormField("baseId", vBaseId);
    setInputFormField("itemEffDate", vItemEffDate);
    setInputFormField("itemtoDate", vItemtoDate);
    setInputFormField("statustype", vStatustype);
    setInputFormField("riskRelationId", vRiskRelId);
    var url = getAppPath() + "/transactionmgr/reinstateprocessmgr/performReinstate.do?";
    postAjaxSubmit(url, "performReinstate", false, false, handleOnReinstateDone);
}

function handleOnReinstateDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            if (!handleAjaxMessages(data, null)) {
                return;
            }

            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
                return;
            }

            isReinstateRiskRelation(data);
            refreshPage();
        }
    }
}

function isReinstateRiskRelation(data) {
    var oValueList = parseXML(data);
    if (oValueList.length > 0) {
        var reinstateLevel = oValueList[0]["REINSTATELEVEL"];
        if (reinstateLevel && reinstateLevel == "RISK RELATION") {
            setInputFormField("riskEffectiveFromDate", oValueList[0]["RISKEFFECTIVEFROMDATE"]);
            setInputFormField("riskEffectiveToDate", oValueList[0]["RISKEFFECTIVETODATE"]);
            setInputFormField("riskCountyCode", oValueList[0]["RISKCOUNTYCODE"]);
            setInputFormField("currentRiskTypeCode", oValueList[0]["CURRENTRISKTYPECODE"]);
            setInputFormField("origRiskEffectiveFromDate", oValueList[0]["ORIGRISKEFFECTIVEFROMDATE"]);
        }
    }
}