//-----------------------------------------------------------------------------
// Java script file for viewMultiCancelConfirmation.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   Aug 15, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2011       syang       126447 - Added handleOnLoad() to handle column in Transaction Detail grid for different level.
// 11/10/2011       syang       126447 - Move the function showHideTableColumn() to common.js.
// 02/28/2012       xnie        130244 - Modified handleOnProcessMultiCancelConfirmationDone to handle exception.
// 05/07/2014       Jyang       153212 - 1.Modified handleOnButtonClick to get destination amalgamation policy no from
//                                        parentWindow and set amalgamation success message for later use. And removed
//                                        amalgamationDate.
//                                       2.Modified handleOnProcessMultiCancelConfirmationDone to popup successful message
//                                        once amalgamation done successfully.
//-----------------------------------------------------------------------------

function confirmationListGrid_selectRow(id) {
    // Filter transaction detail grid
    setTableProperty(eval("transDetailListGrid"), "selectedTableRowNo", null);
    transDetailListGrid_filter("CCANCELDISTINCTID='" + id + "'");

    if (isEmptyRecordset(transDetailListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(transDetailListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(transDetailListGrid1));
        // System calls handleTableColumn() to handle column after grid is completed.
        var testCode = 'getTableProperty(getTableForGrid(\"transDetailListGrid\"), "isUserReadyStateReadyComplete")'
            + '&&!getTableProperty(getTableForGrid(\"transDetailListGrid\"), "filtering")';
        var callbackCode = 'handleTableColumn()';
        executeWhenTestSucceeds(testCode, callbackCode, 100);
    }
}

function handleOnButtonClick(btn) {
    if (btn == "CONTINUE") {
        showProcessingDivPopup();
        var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewMultiCancelConfirmation.do?process=processMultiCancelConfirmation&"
                + commonGetMenuQueryString();
        var parentWindow = window.frameElement.document.parentWindow;
        var successMessage = parentWindow.getMessage("pm.amalgamation.success.info", new Array(parentWindow.getObjectValue("amalgamationTo")));
        parentWindow.setInputFormField("successMessage", successMessage);
        if (parentWindow.hasObject("amalgamationTo")) {
            url += "&amalgamationTo=" + parentWindow.getObjectValue("amalgamationTo");
        }
        if (parentWindow.hasObject("amalgamationB")) {
            url += "&amalgamationB=" + parentWindow.getObjectValue("amalgamationB");
        }
        if (parentWindow.hasObject("amalgamationMethod")) {
            url += "&amalgamationMethod=" + parentWindow.getObjectValue("amalgamationMethod");
        }
        if (parentWindow.hasObject("claimsAccessIndicator")) {
            url += "&claimsAccessIndicator=" + parentWindow.getObjectValue("claimsAccessIndicator");
        }
        if (parentWindow.hasObject("markAsDdl")) {
            url += "&markAsDdl=" + parentWindow.getObjectValue("markAsDdl");
        }
        new AJAXRequest("get", url, '', handleOnProcessMultiCancelConfirmationDone, false);
    }
}
//-----------------------------------------------------------------------------
// System refresh page to start invoking workflow.
//-----------------------------------------------------------------------------
function handleOnProcessMultiCancelConfirmationDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            //if there is any exception, return and do nothing
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var parentWindow = window.frameElement.document.parentWindow;
            var oPolicyNo = parentWindow.getObjectValue("amalgamationTo");
            var amalgamationB = parentWindow.getObjectValue("amalgamationB");
            if (amalgamationB == "Y" && oPolicyNo) {
                var oMessage = parentWindow.getObject("successMessage");
                if (oMessage && !isEmpty(oMessage.value)) {
                    alert(oMessage.value);
                }
            }
            window.frameElement.document.parentWindow.frameElement.document.parentWindow.refreshPage();
        }
    }
}

function handleOnLoad() {
    handleTableColumn();
}

function handleTableColumn(){
    var cancelLevel = getObjectValue("cancelLevel").toUpperCase();
    if (cancelLevel == 'RISK') {
        showHideTableColumn("CCOVERAGEDESCRIPTION", false);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
    else if (cancelLevel == 'COVERAGE') {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
    else if (cancelLevel == 'COVERAGE CLASS') {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", true);
    }
    else if (cancelLevel == 'COMPONENT') {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", true);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
}