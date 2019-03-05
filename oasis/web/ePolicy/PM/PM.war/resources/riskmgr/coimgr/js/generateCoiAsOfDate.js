//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 06/26/2018       dpang       109175: Modified field id for refactoring 'Entity Role List' page in CIS.
// 11/22/2018       clm         195889: Grid Replacement :
//                                      1) add function addRecordToCurrentGridListForJqxGrid
//                                      2) add deferred loading logic in handleOnLoad
//                                      3) add select all logic for JqxGrid
//-----------------------------------------------------------------------------

function handleOnLoad() {
    var dataLoaded = "N";
    if (getObject("dataLoaded")) {
        dataLoaded = getObjectValue("dataLoaded");
    }
    if (dataLoaded == "") dataLoaded = "N";

    if (dataLoaded == "N") {
        var parentGridId = eval("getParentWindow()." + getObjectValue("parentGridId") + "1");
        var modXML = parentGridId.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
        var selectedEntityRoleLen = modXML.length;
        for (var i = 0; i < selectedEntityRoleLen; i++) {
            var currentRecord = modXML.item(i);
            var roleTypeCode = currentRecord.selectNodes("CROLETYPECODE").item(0).text;
            if (roleTypeCode == "COI_HOLDER") {
                if(!window["useJqxGrid"]) {
                    addRecordToCurrentGridList(asOfDateForGenCoiListGrid1, currentRecord);
                }
                else{
                    addRecordToCurrentGridListForJqxGrid(currentRecord);
                }
            }
        }
        // submit the data
        document.forms[0].txtXML.value = getChanges(asOfDateForGenCoiListGrid1);
        document.forms[0].action = getAppPath() + "/riskmgr/coimgr/standaloneGenerateCoi.do?" +
                                   commonGetMenuQueryString() + "&process=loadAllCoiHolder";
        showProcessingDivPopup();
        submitFirstForm();
    }
    if (isDefined(getObject("asOfDateForGenCoiListGrid1"))) {
        $.when(dti.oasis.grid.getLoadingPromise("asOfDateForGenCoiListGrid")).then(function () {
            if (getObject("chkCSELECT_ALL")) {
                getObject("chkCSELECT_ALL").checked = true;
            }
            else if ($("#".concat("asOfDateForGenCoiListGrid", "_chkCSELECT_ALL"))) {
                dti.oasis.grid.selectAll("asOfDateForGenCoiListGrid");
            }
        });
    }
}

function addRecordToCurrentGridList(asOfDateForGenCoiListGrid1, currentRecord) {
    var row = asOfDateForGenCoiListGrid1.createElement("ROW");
    row.setAttributeNode(currentRecord.getAttributeNode("id").cloneNode(true));
    row.setAttributeNode(currentRecord.getAttributeNode("index").cloneNode(true));
    row.setAttributeNode(currentRecord.getAttributeNode("col").cloneNode(true));
    row.setAttribute("col", "1");
    row.appendChild(currentRecord.selectNodes("CROLETYPECODE")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CEXTERNALID")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CROLENAME")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CEFFECTIVEFROMDATE")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CEFFECTIVETODATE")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("UPDATE_IND")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("DISPLAY_IND")(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("EDIT_IND")(0).cloneNode(true));
    asOfDateForGenCoiListGrid1.documentElement.appendChild(row);
}

function addRecordToCurrentGridListForJqxGrid(currentRecord) {
    asOfDateForGenCoiListGridGridInfo.data.columnNames = ["CROLETYPECODE", "CEXTERNALID", "CROLENAME", "CEFFECTIVEFROMDATE",
       "CEFFECTIVETODATE", "UPDATE_IND", "DISPLAY_IND", "EDIT_IND", "OBR_ENFORCED_RESULT", "@id", "@index", "@col"];
    asOfDateForGenCoiListGridGridInfo.data.rawData.push(
            {
                "CROLETYPECODE": currentRecord.selectNodes("CROLETYPECODE")[0].text,
                "CEXTERNALID": currentRecord.selectNodes("CEXTERNALID")[0].text,
                "CROLENAME": currentRecord.selectNodes("CROLENAME")[0].text,
                "CEFFECTIVEFROMDATE": currentRecord.selectNodes("CEFFECTIVEFROMDATE")[0].text,
                "CEFFECTIVETODATE": currentRecord.selectNodes("CEFFECTIVETODATE")[0].text,
                "UPDATE_IND": currentRecord.selectNodes("UPDATE_IND")[0].text,
                "DISPLAY_IND": currentRecord.selectNodes("DISPLAY_IND")[0].text,
                "EDIT_IND": currentRecord.selectNodes("EDIT_IND")[0].text,
                "@id": currentRecord.getAttribute("id"),
                "@index": currentRecord.getAttribute("index"),
                "@col": currentRecord.getAttribute("col")
            });
}

function asOfDateForGenCoiForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

function handleOnButtonClick(asBtn) { 

    switch (asBtn) {
        case 'SAVE_COI_DATE':
            if (isRecordSelected(asOfDateForGenCoiListGrid1)) {
                getObject("txtXML").value = getChanges(asOfDateForGenCoiListGrid1);
                var isCoiAsOfDateEditable = getObjectValue("isCoiAsOfDateEditable");
                if (isCoiAsOfDateEditable == "Y") {
                    var url = getAppPath() + "/riskmgr/coimgr/standaloneGenerateCoi.do?" + commonGetMenuQueryString();
                    postAjaxSubmit(url, "validateAsOfDate", false, false, handleOnValidateAsOfDate);
                }
                else {
                    handleOnCaptureAsOfDate();
                }
            }
            else {
                alert(getMessage("pm.generateCoiAsOfDate.coiHolder.select.error"));
            }
            break;
    }
}

function handleOnValidateAsOfDate(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null) && !processedConfirmationMessages) {
                return;
            }


            if (processedConfirmationMessages) {
                if (getConfirmationResponse("pm.generateClientCoi.asOfDate.error") == "Y") {
                    // uncheck invalid coi holder
                    var oValueList = parseXML(data);
                    if (oValueList.length > 0) {
                        var policyListStr = oValueList[0]["POLICYLISTSTR"];
                        var listArray = policyListStr.split(",");
                        for (var i = 0; i < listArray.length; i++) {
                            var policyNo = listArray[i];
                            var nodes = asOfDateForGenCoiListGrid1.documentElement.selectNodes(
                                "//ROW[CEXTERNAL_ID='" + policyNo + "']");
                            if (nodes.length == 1) {
                                var node = nodes.item(0);
                                node.selectNodes("CSELECT_IND").item(0).text = "0";
                            }
                        }
                        if(!window["useJqxGrid"]) {
                            getObject("chkCSELECT_ALL").checked = false;
                        }
                        else {
                            $("#".concat("asOfDateForGenCoiListGrid", "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
                        }
                    }

                    // check if there are any record checked
                    if (isRecordSelected(asOfDateForGenCoiListGrid1)) {
                        handleOnCaptureAsOfDate();
                    }
                    else {
                        if (getObject("chkCSELECT_ALL")) {
                            getObject("chkCSELECT_ALL").checked = false;
                        }
                        else if ($("#".concat("asOfDateForGenCoiListGrid", "_chkCSELECT_ALL"))) {
                            $("#".concat("asOfDateForGenCoiListGrid", "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
                        }
                    }
                }
                else {
                    // flow ends.
                }
            }
            else {
                handleOnCaptureAsOfDate();
            }
        }
    }
}

function handleOnCaptureAsOfDate() {
    // capture COI Claim History
    var pmCoiClaimsParam = "N";
    if (getObject("pmCoiClaimsParam")) {
        pmCoiClaimsParam = getObjectValue("pmCoiClaimsParam");
    }
    if (pmCoiClaimsParam == "Y") {
        captureCoiClaimHistory();
    }
    else {
        submitToGenerateAllClientCoi();
    }
}

function captureCoiClaimHistory() {
    var url = getAppPath() + "/riskmgr/coimgr/captureCoiClaimHistory.do?"
        + commonGetMenuQueryString() + "&process=display";
    var divPopupId = openDivPopup("", url, true, true, "", "", "500", "400", "", "", "", false);
}

function handleOnCaptureCoiClaimHistory(coiSelectLetter, coiIncludeExcludeClaim, coverageType, claimType, paymentType, coiCutoffDate) {
    // set coi claim history data values into input fields
    setInputFormField("coiSelectLetter", coiSelectLetter);
    setInputFormField("coiIncludeExcludeClaim", coiIncludeExcludeClaim);
    setInputFormField("coverageType", coverageType);
    setInputFormField("claimType", claimType);
    setInputFormField("paymentType", paymentType);
    setInputFormField("coiCutoffDate", coiCutoffDate);

    // Submit the page to generate COI
    submitToGenerateAllClientCoi();
}

function submitToGenerateAllClientCoi() {
    document.forms[0].process.value = "processAllCoi";
    showProcessingDivPopup();
    submitFirstForm();
}