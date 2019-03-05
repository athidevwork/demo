//-----------------------------------------------------------------------------
// Javascript file for applicationList.jsp
//
// (C) 2017 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/12/2017       lzhang      Issue 186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
isChanged=false

function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            var spolicyNo = getObject("policyNo").value;
            var stermBaseRecordId = getObject("termDesc").value;
            getObject("termDesc").value =  stermBaseRecordId;

            document.forms[0].action = getAppPath() + "/policymgr/applicationmgr/maintainApplication.do?"
            + "&process=" + "loadCurrentApplication" + "&policyId=" + spolicyNo + "&termBaseRecordId=" + stermBaseRecordId;

            submitFirstForm();

            break;
        case 'VIEWAPP':
            var ssysParm = getSysParmValue("PM_WEBAPP_URL");
            var currentapplicationListGrid = getXMLDataForGridName("applicationListGrid");
            var swebAppHeaderId = currentapplicationListGrid.recordset("CWEBAPPHEADERID").value;
            var sappId = currentapplicationListGrid.recordset("CAPPID").value;
            var appUrl = ssysParm + "?APP_appFk=" + swebAppHeaderId + "&appid=" + sappId;
            getObject("iframeResponse").src = appUrl;
            break;
        case 'CLOSE':
            commonOnButtonClick("CLOSE_RO_DIV");

        default:break;
    }
}
function handleOnSubmit(action) {
    var proceed = true;

    return proceed;
}
function handleOnLoad() {
    setDefaultPolicyTerm();
}

function setDefaultPolicyTerm() {
    //default policy term
    try {
        var currentapplicationListGrid = getXMLDataForGridName("applicationListGrid");
    }
    catch (ex) {
        // appliationList grid not exist, which means it loads without grid
        // simply returns.
        return;
    }
    var currTermBaseId = currentapplicationListGrid.recordset("CTERMBASEID").value;
    getObject("termDesc").value = currTermBaseId;

}
// When the iframe on load, system hides the div.
function iframeOnLoad() {
    var buttonGroups = iframeResponse.$(".horizontalButtonCollection");
    if (buttonGroups.length > 0) {
        hideShowElementByClassName(buttonGroups[0], true);
    }
}
