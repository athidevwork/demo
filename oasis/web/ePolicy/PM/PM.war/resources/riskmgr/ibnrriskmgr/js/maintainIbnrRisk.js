//-----------------------------------------------------------------------------
// Javascript file for maintanIbnrRisk.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   Mar 07, 2011
// Author: Dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//  05/20/2011       dzhang     120918 - Added handleOnLoad().
//  10/27/2011       dzhang     126639 - Added handleOnBlur() to validate field PT %.
//  11/02/2011       wfu        126624 - Removed handleOnLoad to avoid unnecessary data filtering since 120918 has been
//                                       taken care by changes in function fireAjax of edits.js.
//                                       Remove reconnect fields related logic in secondGrid_selectRow.
//  11/25/2011       dzhang     127324 - Modified selectAssociatedRiskType
//                                       1) Not display the selected risk in the Selected Associated Risk page.
//                                       2) During Cancel transaction, if user try to make some change in IBNR main page,
//                                          system will not invoke workflow until user click Close button.
//  01/04/2012       wfu        127802 - Modified find and added function handleOnSelectEntity, handleOnGetEntityName.
//  12/30/2014       jyang      157750 - Modified handleOnSelectAssociatedRiskType() and firstGrid_setInitialValues() to
//                                       encode the riskName,entityName before append it to URL.
//  01/08/2015       awu        157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                       to wrap the multiple rows deleting.
//  07/12/2017       lzhang     186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------

function firstGrid_selectRow(id) {
    filterChildGrids("firstGrid", firstGrid1.recordset("CRISKBASERECORDID").value);
}

function secondGrid_selectRow(id) {
    filterChildGrids("secondGrid", secondGrid1.recordset("CINACTIVEENTITYID").value);
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADD_ASSO_RISK':
            // reset temp fields for new associated risk
            setInputFormField("newRiskBaseRecordId", 0);
            setInputFormField("newRiskEffectiveFromDate", "");
            setInputFormField("newRiskEffectiveToDate", "");
            setInputFormField("newEntityName", "");
            setInputFormField("newEntityId", 0);
            setInputFormField("newProductCoverageCode", "");
            // get associated risk
            selectAssociatedRiskType("ADDASSO");
            break;
        case 'ADD_INC_RISK':
            var riskName = firstGrid1.recordset("CASSOCIATEDRISKNAME").value;
            if (isEmpty(riskName)) {
                alert(getMessage("pm.maintainInactive.associatedRiskNameRequired.error"));
                break;
            }
            setInputFormField("newInactiveEntityId", 0);
            setInputFormField("newInactiveRiskName", "");
            openEntitySelectWinFullName("newInactiveEntityId", "newInactiveRiskName", "handleFindClient()");
            filterChildGrids("firstGrid", firstGrid1.recordset("CRISKBASERECORDID").value);
            break;
        case 'CHANGE_ASSO_RISK':
            if (isChanged || isPageDataChanged()) {
                handleError(getMessage("pm.maintainInactive.unsavedData.error"));
            }
            else {
                validateForChangeAssociatedRisk();
            }
            break;
        case 'DELETE_ASSO_RISK':
            commonDeleteRow("firstGrid");
            filterChildGrids("firstGrid", firstGrid1.recordset("CRISKBASERECORDID").value);    
            break;
        case 'DELETE_INAC_RISK':
            deleteInactiveRisk("secondGrid");
            filterChildGrids("firstGrid", firstGrid1.recordset("CRISKBASERECORDID").value);
            break;
        case 'CLOSE':
            closeThisDivPopup(false);
            if (hasObject("isInWorkflow") && getObjectValue("isInWorkflow") == "Y") {
                window.frameElement.document.parentWindow.refreshPage();
            }
            break;
        case 'SAVE':
            currentlySelectedGridId = "secondGrid";
            commonOnSubmit('saveAllInactiveRisk', true, true, true, true);
            break;
    }

    return true;
}

function selectAssociatedRiskType(openFrom) {
    var url = getAppPath() +
            "/riskmgr/ibnrriskmgr/selectAssociatedRisk.do?"
            + commonGetMenuQueryString() + "&process=loadAllAssociatedRiskType"
            + "&openFrom=" + openFrom
            + "&transactionLogId=" + policyHeader.lastTransactionInfo.transactionLogId
            + "&transEffDate=" + policyHeader.lastTransactionInfo.transEffectiveFromDate
            + "&issueCompanyId=" + getObjectValue("issueCompanyEntityId")
            + "&notInvokeWorkFlow=Y"
            + "&orgAssociatedRiskId=" + firstGrid1.recordset("CRISKBASERECORDID").value;
    var divPopupId = openDivPopup("", url, true, true, null, null, "", "", "", "", "", false);
}

function handleOnSelectAssociatedRiskType(riskName, riskBaseRecordId, riskEffectiveFromDate, riskEffectiveToDate, entityId, productCoverageCode) {
    setInputFormField("newRiskBaseRecordId", riskBaseRecordId);
    setInputFormField("newRiskEffectiveFromDate", riskEffectiveFromDate);
    setInputFormField("newRiskEffectiveToDate", riskEffectiveToDate);
    setInputFormField("newEntityName", riskName);
    setInputFormField("newEntityId", entityId);
    setInputFormField("newProductCoverageCode", productCoverageCode);
    if (!modifyAssociatedRisk) {
        if (!AssociatedRiskExists(riskBaseRecordId)) {
            // add a new row
            commonAddRow("firstGrid");
        }
    }
    else {
        var url = getAppPath() +
                "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?"
                + commonGetMenuQueryString() + "&process=getInitialValuesForAddAssociatedRisk"
                + "&riskBaseRecordId=" + riskBaseRecordId
                + "&riskEffectiveFromDate=" + riskEffectiveFromDate
                + "&riskEffectiveToDate=" + riskEffectiveToDate
                + "&associatedEntityId=" + entityId
                + "&associatedRiskName=" + encodeURIComponent(riskName)
                + "&productCoverageCode=" + productCoverageCode;
        new AJAXRequest("get", url, '', handleOnChangeAssociatedRiskName, false);
    }
}

function AssociatedRiskExists(riskBaseRecordId) {
    var findDup = false;
    if (!isEmptyRecordset(firstGrid1.recordset)) {
        first(firstGrid1);
        while (!firstGrid1.recordset.eof) {
            if (firstGrid1.recordset("CRISKBASERECORDID").value == riskBaseRecordId) {
                findDup = true;
                handleError(getMessage("pm.maintainInactive.addAssociatedRisk.riskExists.error"), "", firstGrid1.recordset("ID").value);
                break;
            }
            next(firstGrid1);
        }
    }

    return findDup;
}

function handleOnChangeAssociatedRiskName(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName("firstGrid");
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }

                if (!isEmptyRecordset(secondGrid1.recordset)) {
                    resetInactiveRiskDetails(oValueList[0]["associatedRiskName"], oValueList[0]["riskBaseRecordId"],
                            oValueList[0]["productCoverageCode"]);
                }
            }

            // set back the modifyAssociatedRisk to false
            modifyAssociatedRisk = false;
            //sync the changes to origfirstGrid.
            origfirstGrid1 = firstGrid1.cloneNode(true);
        }
    }
}

function resetInactiveRiskDetails(associatedRiskName, riskBaseRecordId, primaryProdCovgCode) {
    var selectedId = secondGrid1.recordset("ID").value;
    first(secondGrid1);
    while (!secondGrid1.recordset.eof) {
        secondGrid1.recordset("CASSOCIATEDRISKID").value = riskBaseRecordId;
        secondGrid1.recordset("UPDATE_IND").value = "Y";
        resetThirdGrid(secondGrid1.recordset("CINACTIVEENTITYID").value, associatedRiskName);
        next(secondGrid1);
    }
    selectRowById("secondGrid", selectedId);
}

function resetThirdGrid(inactiveEntityId, associatedRiskName) {
    if (!isEmptyRecordset(thirdGrid1.recordset)) {
        var selectedId = thirdGrid1.recordset("ID").value;
        thirdGrid_filter("");
        first(thirdGrid1);
        while (!thirdGrid1.recordset.eof) {
            if (thirdGrid1.recordset("CENTITYID").value == inactiveEntityId) {
                thirdGrid1.recordset("CASSORISKNAME").value = associatedRiskName;
                thirdGrid1.recordset("UPDATE_IND").value = "Y";
            }
            next(thirdGrid1);
        }
        selectRowById("thirdGrid", selectedId);
        syncChanges(origthirdGrid1, thirdGrid1);
    }
}

function handleFindClient() {
    commonAddRow("secondGrid");
}

function firstGrid_setInitialValues() {
    var url = getAppPath() +
            "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForAddAssociatedRisk"
            + "&riskBaseRecordId=" + getObjectValue("newRiskBaseRecordId")
            + "&riskEffectiveFromDate=" + getObjectValue("newRiskEffectiveFromDate")
            + "&riskEffectiveToDate=" + getObjectValue("newRiskEffectiveToDate")
            + "&associatedEntityId=" + getObjectValue("newEntityId")
            + "&associatedRiskName=" + encodeURIComponent(getObjectValue("newEntityName"))
            + "&productCoverageCode=" + getObjectValue("newProductCoverageCode");
    currentlySelectedGridId = "firstGrid";
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function secondGrid_setInitialValues() {
    var url = getAppPath() +
            "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForAddInactiveRisk"
            + "&inactiveEntityId=" + getObjectValue("newInactiveEntityId")
            + "&inactiveRiskName=" + encodeURIComponent(getObjectValue("newInactiveRiskName"))
            + "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate")
            + "&associatedRiskId=" + firstGrid1.recordset("CRISKBASERECORDID").value;
    currentlySelectedGridId = "secondGrid";
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

//-----------------------------------------------------------------------------
// This function is to filter child grids when select row in parent grid
//-----------------------------------------------------------------------------
function filterChildGrids(gridId, filterValue) {
    if (gridId == "firstGrid") {
        // Filter second grid
        setTableProperty(eval("secondGrid"), "selectedTableRowNo", null);
        secondGrid_filter("CASSOCIATEDRISKID='" + filterValue + "'");

        if (isEmptyRecordset(secondGrid1.recordset)) {
            hideEmptyTable(getTableForXMLData(secondGrid1));
            hideEmptyTable(getTableForXMLData(thirdGrid1));
        }
        else {
            showNonEmptyTable(getTableForXMLData(secondGrid1));
            hideShowElementByClassName(getObject("secondGridDetailDiv"), false);
            var inactiveRiskEntityId = secondGrid1.recordset("CINACTIVEENTITYID").value;
            filterChildGrids("secondGrid", inactiveRiskEntityId);
        }
    } else if (gridId == "secondGrid") {
        // Filter third grid
        setTableProperty(eval("thirdGrid"), "selectedTableRowNo", null);
        thirdGrid_filter("CENTITYID='" + filterValue + "'");

        var testCode = 'getTableProperty(getTableForGrid(\"thirdGrid\"), "isUserReadyStateReadyComplete")'
                + '&&!getTableProperty(getTableForGrid(\"thirdGrid\"), "filtering")';
        var callbackCode = 'filterThirdDone()';
        executeWhenTestSucceeds(testCode, callbackCode, 50);
    }
}
//-----------------------------------------------------------------------------
// This function will be executed to make sure the second form displayed correctly after filter third grid done
//-----------------------------------------------------------------------------
function filterThirdDone() {
    if (isEmptyRecordset(thirdGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(thirdGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(thirdGrid1));
    }
    hideShowElementByClassName(getObject("secondGridDetailDiv"), false);
}

var modifyAssociatedRisk = false;
function find(fieldId) {
    if (fieldId == "inactiveRiskName") {
        setInputFormField("inactiveEntityId", 0);
        setInputFormField("inactiveRiskName", "");
        openEntitySelectWinFullName("inactiveEntityId", "inactiveRiskName", "handleOnSelectEntity()");
        secondGrid1.recordset("UPDATE_IND").value = "Y";
        setTableProperty(secondGrid1, "gridDataChange", true);
    }
    else if (fieldId == "associatedRiskName") {
        setInputFormField("newRiskBaseRecordId", 0);
        setInputFormField("newRiskEffectiveFromDate", "");
        setInputFormField("newRiskEffectiveToDate", "");
        setInputFormField("newEntityName", "");
        setInputFormField("newEntityId", 0);
        setInputFormField("newProductCoverageCode", "");
        modifyAssociatedRisk = true;
        selectAssociatedRiskType("ADDASSO");
    }
}

function handleOnSelectEntity() {
    var url = getAppPath() + "/entitymgr/lookupEntity.do?process=getEntityName"
                           + "&entityId=" + getObjectValue("inactiveEntityId")
                           + "&entityIdFieldName=inactiveEntityId"
                           + "&entityNameFieldName=inactiveRiskName"
                           + "&date=" + new Date();
    new AJAXRequest("get", url, '', handleOnGetEntityName, false);
}

function handleOnGetEntityName(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                secondGrid1.recordset("CINACTIVEENTITYID").value = oValueList[0]["INACTIVEENTITYID"];
                secondGrid1.recordset("CINACTIVERISKNAME").value = oValueList[0]["INACTIVERISKNAME"];
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To validate for change associated risk.
// Currently it used to check if any of the inactives being moved to a different associated
// risk have any claims.
//-----------------------------------------------------------------------------
function validateForChangeAssociatedRisk() {
    var associatedRiskId = firstGrid1.recordset("CRISKBASERECORDID").value;
    var claimRecords = secondGrid1.documentElement.selectNodes("//ROW[(CHASCLAIMS='Y') and (CASSOCIATEDRISKID='" + associatedRiskId + "')]");
    var size = claimRecords.length;
    var paras = '';
    if (size > 0) {
        for (var i = 0; i < size; i++) {
            var currentRecord = claimRecords.item(i);
            var inactiveRiskName = currentRecord.selectNodes("CINACTIVERISKNAME")(0).text;
            if (i == 0) {
                paras = inactiveRiskName;
            }
            else {
                paras = paras + "," + inactiveRiskName;
            }
        }
        alert(getMessage("pm.maintainInactive.inactiveRiskHasClaimAttached.error", new Array(paras)));
        selectAssociatedRiskType("CHANGEASSO");
    }
    else {
        selectAssociatedRiskType("CHANGEASSO");
    }
}

//-----------------------------------------------------------------------------
// Delete IBNR Inactive Risk row when deleting Associated Risk row.
//-----------------------------------------------------------------------------
function firstGrid_deleteDependentRow() {
    if (!getTableProperty(secondGrid, "hasrows"))
        return;

    var associatedRiskId = firstGrid1.recordset("CRISKBASERECORDID").value;
    first(secondGrid1);
    beginDeleteMultipleRow("secondGrid");
    while (!secondGrid1.recordset.eof) {
        if (secondGrid1.recordset("CASSOCIATEDRISKID").value == associatedRiskId) {
            setSelectedRow("secondGrid", secondGrid1.recordset("ID").value);
            secondGrid_deleterow();
        }
        next(secondGrid1);
    }
    first(secondGrid1);
    endDeleteMultipleRow("secondGrid");
}

function deleteInactiveRisk(gridId) {
    var rs = getXMLDataForGridName(gridId).recordset;
    if (!isEmptyRecordset(rs) && confirm(getMessage("pm.common.selected.record.delete.confirm"))) {
        var xmlData = getXMLDataForGridName(gridId);
        var origXMLData = getOrigXMLData(xmlData);
        var officalRecordId = null;

        if (isFieldExistsInRecordset(rs, "COFFICIALRECORDID")) {
            officalRecordId = rs.Fields("COFFICIALRECORDID").value;
        }
        var inactiveId = xmlData.recordset("ID").value;
        if (isEmpty(officalRecordId)) {
            processThirdGridRow(inactiveId,officalRecordId);
            eval(gridId + "_deleterow();");
        }
        else {
            var officalNode = origXMLData.documentElement.selectSingleNode("//ROW[@id='" + officalRecordId + "']");
            if (officalNode != null) {
                officalNode.selectSingleNode("./DISPLAY_IND").text = "Y";
                eval(gridId + "_filter(\"UPDATE_IND != 'D'\")");
            }
            processThirdGridRow(inactiveId,officalRecordId);
            // Use xmlData.recordset instead of rs to avoid infinite loop as rs does NOT move along with xmlData
            first(xmlData);
            beginDeleteMultipleRow(gridId);
            while (!xmlData.recordset.eof) {
                if (xmlData.recordset.Fields("COFFICIALRECORDID").value == officalRecordId) {
                    setSelectedRow(gridId, xmlData.recordset("ID").value);
                    eval(gridId + "_deleterow();");
                }
                next(xmlData);
            }
            first(xmlData);
            endDeleteMultipleRow(gridId);
        }
        hideShowForm(gridId);
    }
}


//-----------------------------------------------------------------------------
// Delete the bottom grid row when deleting inactive Risk row.
//-----------------------------------------------------------------------------
function processThirdGridRow(inactiveId, officialRecordId) {
    if (!getTableProperty(thirdGrid, "hasrows"))
        return;
    var xmlData = getXMLDataForGridName("thirdGrid");
    var origXMLData = getOrigXMLData(xmlData);
    if (isEmpty(officialRecordId)) {
        first(thirdGrid1);
        beginDeleteMultipleRow("thirdGrid");
        while (!thirdGrid1.recordset.eof) {
            if (thirdGrid1.recordset("ID").value == inactiveId) {
                setSelectedRow("thirdGrid", thirdGrid1.recordset("ID").value);
                thirdGrid_deleterow();
            }
            next(thirdGrid1);
        }
        endDeleteMultipleRow("thirdGrid");
        first(thirdGrid1);
    }
    else {
        var officalNode = origXMLData.documentElement.selectSingleNode("//ROW[(@id= '" + officialRecordId + "') and (CCLOSINGTRANSLOGID ='" +policyHeader.lastTransactionId + "'"+")]");
        if (officalNode != null) {
            officalNode.selectSingleNode("./DISPLAY_IND").text = "Y";
            eval("thirdGrid_filter(\"UPDATE_IND != 'D'\")");
        }

        // Use xmlData.recordset instead of rs to avoid infinite loop as rs does NOT move along with xmlData
        first(xmlData);
        beginDeleteMultipleRow("thirdGrid");
        while (!xmlData.recordset.eof) {
            if (xmlData.recordset.Fields("ID").value == inactiveId) {
                setSelectedRow("thirdGrid", xmlData.recordset("ID").value);
                thirdGrid_deleterow();
            }
            next(xmlData);
        }
        endDeleteMultipleRow("thirdGrid");
    }

}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}

function handleOnSubmit(action) {
    document.forms[0].action = getAppPath() + "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?" +
                commonGetMenuQueryString() + "&process=" + action;
    return true;
}

//-----------------------------------------------------------------------------
// Validatioin for PT %
//-----------------------------------------------------------------------------
function handleOnBlur(obj) {
    // Modify PT %
    if (obj.name == "componentValue") {
        var ptPercent = getObjectValue("componentValue");
        if (!isEmpty(ptPercent)) {
            var floatValue = parseFloat(ptPercent);
            if (floatValue > 100 || floatValue < 0) {
                alert(getMessage("pm.maintainInactive.invalidPtPercent"));
                obj.focus();
                return false;
            }
        }
    }
}
