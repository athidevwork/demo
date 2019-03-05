//-----------------------------------------------------------------------------
// JavaScript file for process dividend.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:    Mar 30, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//11/29/18          xgong       195889 - Updated handleOnButtonClick for gird replacement
//-----------------------------------------------------------------------------

function handleOnLoad() {
    // select first row of policy type
    if (getObjectValue("isFirstLoaded") == "Y" && getObject("policyType").options.length>1) {
        getObject("policyType").options[1].selected = true;      
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'FILTER':
            if (!isEmpty($("#iframeCalculatedDividend").attr("src"))) {
                var policyNoCriteria = getObjectValue("policyNoCriteria");
                var statusCode = getObjectValue("statusCode");
                getObject("iframeCalculatedDividend").contentWindow.filterDividend(policyNoCriteria,statusCode);
            }
            break;
        case 'SEARCH':
            document.forms[0].process.value = "loadAllPriorDividend";
            submitFirstForm();
            break;
        case 'CALCULATE':
            var url = getAppPath() + "/policymgr/dividendmgr/calculateDividend.do?process=display";
            var divPopupId = openDivPopup("", url, true, true, "", "", 700, 450, "", "", "", false);
            break;
        case 'RETRIEVE':
            var url = getAppPath() + "/policymgr/dividendmgr/processDividend.do"
                      + "?process=loadAllCalculatedDividend"
                      + "&dividendRuleId=" + priorDividendListGrid1.recordset("ID").value
                      + "&policyTermTypeCode=" + priorDividendListGrid1.recordset("CPOLICYTERMTYPECODE").value
                      + "&cancelB=" + priorDividendListGrid1.recordset("CCANCELB").value
                      + "&date=" + new Date();
            getObject("iframeCalculatedDividend").src = url;
        default:break;
    }
}
