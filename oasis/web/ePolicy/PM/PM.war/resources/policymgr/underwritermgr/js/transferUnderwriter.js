// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/05/2013       awu         138241 - Changed  refreshPage and processTransferUnderwriter to add uwTypeCode and transferTeamB
// 09/12/2013       Parker      148260 - Add processing dialog for some policy page.
// 1/13/2014        awu         149023 - Add handleOnChange to reset the transferTeam indicator.
//-----------------------------------------------------------------------------

var selectedPolicyIds = "";
var selectedPolicyNos = "";
var numOfSelectedPolicies = 0;
var initialFromEntityId;
var initialEffDate;

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            if (commonValidateForm()) {
                showProcessingDivPopup();
                document.forms[0].action = getAppPath() + "/policymgr/underwritermgr/transferUnderwriter.do";
                document.forms[0].process.value = "loadAllPolicyByUnderwriter";
                submitFirstForm();
            }
            break;
        case 'CLEAR':
            document.forms[0].action = getAppPath() + "/policymgr/underwritermgr/transferUnderwriter.do";
            document.forms[0].process.value = "clearSearchCriteria";
            submitFirstForm();
            break;
        case 'PROCESS':
            processTransferUnderwriter();

    }
}

function processTransferUnderwriter() {
    if (validateInputCriteria()) {
        setSelectedPolicies();
        var data = "effDate=" + getObjectValue("effDate")
            + "&toEntityId=" + getObjectValue("toEntityId")
            + "&transferTeamB=" + getObjectValue("transferTeamB")
            + "&fromEntityId=" + getObjectValue("fromEntityId")
            + "&uwTypeCode=" + getObjectValue("uwTypeCode")
            + "&selectedpolicyIds=" + selectedPolicyIds
            + "&selectedPolicyNos=" + selectedPolicyNos
            + "&numOfSelectedPolicies=" + numOfSelectedPolicies;
        var path = getAppPath() + "/policymgr/underwritermgr/transferUnderwriter.do?process=performTransferUnderwriter";
        showProcessingDivPopup();
        new AJAXRequest("post", path, data, processAjaxResponseForTransferUnderwriter, false);
    }
}

function processAjaxResponseForTransferUnderwriter(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }

            else {
                var currentRecord = root.getElementsByTagName("ROW").item(0)
                var processResult = currentRecord.selectNodes("RETURN")(0).text;
                if (processResult != 'VALID') {
                    var viewErrMsgUrl = getAppPath() + "/policymgr/underwritermgr/viewTransUWErrorMsg.do?"
                        + "process=viewErrorMsg";
                    var divPopupId = openDivPopup("", viewErrMsgUrl, true, true, "", "", 600, 500, "", "", "", false);

                }
                else {
                    handleError(getMessage("pm.transferUnderwriter.success.info"));
                    refreshPage();
                }
            }
        }
    }
}
//refresh page for transfer underwriter
function refreshPage() {
    var url = location.href;
    // Strip of information after the "?"
    if (url.indexOf('?') > -1) {
        url = url.substring(0, url.indexOf('?'));
    }
    url = url + "?";
    if (getObject("process")) {
        url += "&process=" + getObjectValue("process");
    }
    if (getObject("effDate")) {
        url += "&effDate=" + getObjectValue("effDate");
    }
    if (getObject("fromEntityId")) {
        url += "&fromEntityId=" + getObjectValue("fromEntityId");
    }
    if (getObject("toEntityId")) {
        url += "&toEntityId=" + getObjectValue("toEntityId");
    }
    if (getObject("issueCompanyEntityId")) {
        url += "&issueCompanyEntityId=" + getObjectValue("issueCompanyEntityId");
    }
    if (getObject("issueState")) {
        url += "&issueState=" + getObjectValue("issueState");
    }
    if (getObject("policyTypeCode")) {
        url += "&policyTypeCode=" + getObjectValue("policyTypeCode");
    }
    if (getObject("countyCode")) {
        url += "&countyCode=" + getObjectValue("countyCode");
    }
    if (getObject("territory")) {
        url += "&territory=" + getObjectValue("territory");
    }
    if (getObject("agent")) {
        url += "&agent=" + getObjectValue("agent");
    }
    if (getObject("uwTypeCode")) {
        url += "&uwTypeCode=" + getObjectValue("uwTypeCode");
    }
    if (getObject("transferTeamB")) {
        url += "&transferTeamB=" + getObjectValue("transferTeamB");
    }
    setWindowLocation(url);
}
function setSelectedPolicies() {
    var selectedRecords = policyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    var size = selectedRecords.length;
    selectedPolicyIds = "";
    selectedPolicyNos = "";
    numOfSelectedPolicies = size;

    for (var i = 0; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var policyNo = currentRecord.selectNodes("CPOLICYNO")(0).text;
        var policyId = currentRecord.getAttribute("id");
        if (i == 0) {
            selectedPolicyNos = policyNo;
            selectedPolicyIds = policyId;
        }
        else {
            selectedPolicyNos = selectedPolicyNos + "," + policyNo;
            selectedPolicyIds = selectedPolicyIds + "," + policyId;
        }
    }
}

function validateInputCriteria() {
    if (getObjectValue('fromEntityId') != initialFromEntityId) {
        handleError(getMessage("pm.transferUnderwriter.fromEntityChanged.error"));
        return false;
    }
    if (getObjectValue('effDate') != initialEffDate) {
        handleError(getMessage("pm.transferUnderwriter.effDateChanged.error"));
        return false;
    }
    var selectedRecords = policyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    var size = selectedRecords.length;
    if (size == 0) {
        handleError(getMessage("pm.transferUnderwriter.noPolicySelected.error"));
        return false;
    }
    var fromEntityId = getObjectValue('fromEntityId');
    var toEntityId = getObjectValue('toEntityId');
    if (isEmpty(toEntityId)) {
        handleError(getMessage("pm.transferUnderwriter.targetUnderwriterNotSelected.error"));
        return false;
    }
    if (fromEntityId == toEntityId) {
        handleError(getMessage("pm.transferUnderwriter.sameFromAndToUnderwriter.error"));
        return false;
    }
    return true;

}
function transferUnderwriter_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
function handleOnLoad() {
    initialFromEntityId = getObjectValue('fromEntityId');
    initialEffDate = getObjectValue('effDate');
}

function handleOnChange(obj) {
    if (obj.name == "uwTypeCode") {
        setObjectValue("transferTeamB", "");
    }
}