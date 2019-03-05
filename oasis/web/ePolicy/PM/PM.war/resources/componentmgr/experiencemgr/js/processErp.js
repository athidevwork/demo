//-----------------------------------------------------------------------------
// Javascript file for processErp.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:   Mar 18, 2011
// Author: ryzhao
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/04/2011       ryzhao      120182 - Modified handleOnButtonClick() with case SAVE to remove commonIsOkToChangePages()
//                                       check logic because it is not necessary. Change getChanges() to getChangesOnly()
//                                       for performance consideration.
//                                       Added handleOnBlur() function to deal with the comments length.
// 05/09/2011       ryzhao      120440 - Added handleOnLoad() function to show hint message and close the popup window
//                                       if it is a popup page and no ERP data found.
// 05/11/2011       ryzhao      120439 - Check if ERP list data has been changed before processing ERP or close window.
// 05/27/2011       ryzhao      120439 - Change commonIsOkToChangePages() to local function isOkToChangePage().
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            if (typeof erpListGrid1 == "undefined") {
                break;
            }
            document.forms[0].txtXML.value = getChangesOnly(erpListGrid1);
            var url = getAppPath() + "/componentmgr/experiencemgr/processErp.do?";
            postAjaxSubmit(url, "saveAllErp", false, false, handleOnSaveErpDone);
            break;
        case 'CLOSE':
            if (isOkToChangePage()) {
                closeThis();
            }
            break;
        case 'PROCESS_ERP':
            if (isOkToChangePage()) {
                var url = getAppPath() + "/componentmgr/experiencemgr/processErp.do?";
                postAjaxSubmit(url, "processErp", false, false, handleOnProcessErpDone);
            }
            break;
        case 'SHOW_ALL':
            getObject("showAll").value = "Y";
            getObject("renewalYear").value = "";
            showProcessingDivPopup();
            document.forms[0].action = getAppPath() + "/componentmgr/experiencemgr/processErp.do?date=" + new Date();
            document.forms[0].process.value = 'loadAllErp';
            submitFirstForm();
            break;
        case 'SEARCH':
            getObject("showAll").value = "N";
            showProcessingDivPopup();
            enableDisableField(getObject("renewalDate"), false);
            enableDisableField(getObject("processDate"), false);
            enableDisableField(getObject("erpIssueStateCode"), false);
            enableDisableField(getObject("batchNo"), false);
            document.forms[0].action = getAppPath() + "/componentmgr/experiencemgr/processErp.do?date=" + new Date();
            document.forms[0].process.value = 'loadAllErp';
            submitFirstForm();
            break;
        case 'DELETE_BATCH':
            var batchNo = getObjectValue("batchNo");
            if (isEmpty(batchNo)) {
                alert(getMessage("pm.processErp.deleteBatch.noBatchNo.error"));
                return;
            }
            if (confirm(getMessage("pm.processErp.deleteBatch.confirmation", new Array(batchNo)))) {
                var url = getAppPath() + "/componentmgr/experiencemgr/processErp.do?process=deleteErpBatch&batchNo=" + batchNo;
                new AJAXRequest("get", url, '', handleOnDeleteErpBatchDone, false);
            }
            break;
    }
}

function isOkToChangePage() {
    if (typeof erpListGrid1 == "undefined") {
        return true;
    }
    if (isGridDataChanged("erpListGrid")) {
        if (!confirm(getMessage("pm.common.clickOk.changesLost.confirm"))) {
            return false;
        }
    }
    return true;
}

function handleOnBlur(field) {
    if (field.name == "comments") {
        if (field.value.length > 500) {
            alert(getMessage("pm.processErp.saveErp.comments.too.long.error"));
            field.focus();
        }
    }
}

function closeThis() {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    if (divPopup) {
        window.frameElement.document.parentWindow.closeDiv(divPopup);
    }
}

function handleOnDeleteErpBatchDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            var isDeleteErpBatchSuccess = null;
            if (oValueList.length > 0) {
                isDeleteErpBatchSuccess = oValueList[0]["ISDELETEERPBATCHSUCCESS"];
            }
            // If delete erp batch failed, display error policies
            if (isDeleteErpBatchSuccess == 'N') {
                var path = getAppPath() + "/transactionmgr/viewRelatedPolicyError.do?process=viewRelatedPolicyErrorWithNoPolicyHeader";
                var divPopupId = openDivPopup("", path, true, true, "", "", "600", "400", "", "", "", false);
            }
            else {
                //refresh the page
                handleOnButtonClick('SEARCH');
            }
        }
    }
}

function handleOnSaveErpDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            //refresh the page
            handleOnButtonClick('SEARCH');
        }
    }
}

function handleOnProcessErpDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            //refresh the page
            handleOnButtonClick('SEARCH');
        }
    }
}

function erpListGrid_selectRow(id) {
    var erpGridXMLData = getXMLDataForGridName("erpListGrid");
    var url = getAppPath() + "/policyreportmgr/maintainPolicyReport.do?date=" + new Date()
            + "&process=generatePolicyReport&reportCode=CM_ERP_SUMMARY_DETAIL_PDF";
    url += "&year=" + erpGridXMLData.recordset("CRENEWALYEAR").value;
    url += "&processDate=" + erpGridXMLData.recordset("CPROCESSDATE").value;
    url += "&entityId=" + erpGridXMLData.recordset("CENTITYID").value;
    url += "&policyNo=" + erpGridXMLData.recordset("CPOLICYNO").value;
    getObject("iframeClaimDetails").src = url;
}

function handleReadyStateReady(table) {
    if (isEmptyRecordset(erpListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(erpListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(erpListGrid1));
        showGridDetailDiv("erpListGrid");
    }
}

function handleOnLoad() {
    var isPopupPage = getObjectValue("headerHidden");
    var erpTotalRows = getObjectValue("policySearchTotalRows");
    if (isPopupPage == 'Y' && erpTotalRows == 0) {
        alert(getMessage("pm.processErp.noErpDataFound.information"));
        closeThis();
    }
}