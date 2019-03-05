//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 14, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 08/24/2010       syang       Issue 108651 - Updated handleOnChange() to handle Renew indicator when change effective to date.
// 10/12/2010       wfu         111776: Replaced hardcode variable deleteQuestion with resource definition
// 10/28/2011       xnie        126107 - Changed RiskBaseRecordId field name to childRiskBaseRecordId.
// 01/04/2012       wfu         127802 - Modified empphysListGrid_setInitialValues to reload entity name.
// 10/23/2012       xnie        137735 - Modified handleOnSubmit to add field fteStatusCodeVisible.
// 10/09/2014       wdang       156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 11/25/2014       kxiang      158853 -
//                              1. Modified empphysListGrid_setInitialValues to change call function
//                              2. Added handleOnGetInitialValueForEmployedPhysician.
//                              3. Added handlePostAddRow to set href to grid employed physician.
// 01/08/2015       awu         157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                       to wrap the multiple rows deleting.
// 03/20/2015       wdang       161448 - 1) Added field location when inserting record to support location risk.
//                                     - 2) Added getLocationPropertyId() to support entity mini popup window for location risk.
// 03/10/2017       wrong       180675 - Modified code to open div popups in primary page in new UI tab style.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_FTE':
            addFte();
            break;
        case 'CALCULATE':
            calculateTotalFte();
            break;
        case 'DEL_FTE':
            deleteFte();
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    var fteStatusCodeVisible;

    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllEmployedPhysician";
            if (isFieldHidden("fteStatusCode")) {
                fteStatusCodeVisible = 'N';
            }
            else {
                fteStatusCodeVisible = 'Y';
            }
            setInputFormField("fteStatusCodeVisible", fteStatusCodeVisible);
            break;
    }

    return proceed;
}
//-----------------------------------------------------------------------------
// Calculate total Fte
//-----------------------------------------------------------------------------
function calculateTotalFte() {
    if (commonValidateForm()) {
        var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
        var url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?";
        getObject("txtXML").value = getChanges(empphysListGrid1);
        postAjaxSubmit(url, "calculateTotalFte", false, false, handleOnCalculateTotalFte);
    }
}

function handleOnCalculateTotalFte(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
                setInputFormField("fteTotal", getObjectValue("FTETOTAL"));
                getObject("fteTotalROSPAN").innerText = getObjectValue("FTETOTAL");
            }
        }
    }
}

//when fields are changed, create Ajax request to update fte and renewal fields
function handleOnChange(obj) {
    if (obj.name == 'actualHours' || obj.name == "employmentStatus" || obj.name == "effectiveToDate") {
        var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
        var url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?"
            + commonGetMenuQueryString() + "&process=getValuesForChangedRecord";

        url = url + "&actualHours=" + getObjectValue("actualHours")
            + "&fteEquivalent=" + getObjectValue("fteEquivalent")
            + "&fteFullTimeHrs=" + getObjectValue("fteFullTimeHrs")
            + "&employmentStatus=" + getObjectValue("employmentStatus")
            + "&ftePartTimeHrs=" + getObjectValue("ftePartTimeHrs")
            + "&ftePerDiemHrs=" + getObjectValue("ftePerDiemHrs")
            + "&entityId=" + xmlData.recordset("CENTITYID").value
            + "&effectiveToDate=" + getObjectValue("effectiveToDate")
            + "&policyFteRelationId=" + xmlData.recordset("CPOLICYFTERELATIONID").value
        //set UPDATE_IND, it is needed when validating end date
            + "&UPDATE_IND=" + xmlData.recordset("UPDATE_IND").value
            + "&origEffectiveToDate=" + xmlData.recordset("CORIGEFFECTIVETODATE").value
        //set isToValidate flag, it will validate the changed record
            + "&isToValidate=Y";

        if (obj.name == "effectiveToDate") {
            url = url + "&isEndDateChanged=Y";
        }
        new AJAXRequest("get", url, '', commonHandleOnGetAddlInfo, false);
        // If change effectiveToDate, system should continue to handle Renew indicator rather than return.
        if(obj.name != "effectiveToDate"){
            return !hasProcessedAjaxErrorMessages();
        }
    }
    // Issue 108651, handle Renew indicator.
    if (obj.name == "effectiveToDate") {
        var effectiveToDate = obj.value;
        var termExpirationDate = policyHeader.termEffectiveToDate;
        enableDisableRenewIndicator(effectiveToDate, termExpirationDate, "renewB", "isRenewBAvailable", "empphysListGrid");
    }
    return true;
}

//-----------------------------------------------------------------------------
// Delete Fte
//-----------------------------------------------------------------------------
function deleteFte() {
    var gridId = getCurrentlySelectedGridId();
    var rs = getXMLDataForGridName(gridId).recordset;
    if (!isEmptyRecordset(rs) && confirm(getMessage("pm.common.selected.record.delete.confirm"))) {
        var xmlData = getXMLDataForGridName(gridId);
        var origXMLData = getOrigXMLData(xmlData);
        var officalRecordId = null;

        if (isFieldExistsInRecordset(rs, "COFFICIALRECORDID")) {
            officalRecordId = rs.Fields("COFFICIALRECORDID").value;
        }
        if (isEmpty(officalRecordId)) {
            var functionExist = eval("window." + gridId + "_deleteDependentRow");
            if (functionExist != null) {
                eval(gridId + "_deleteDependentRow();");
            }
            eval(gridId + "_deleterow();");
        }
        else {
            var officalNode = origXMLData.documentElement.selectSingleNode("//ROW[@id='" + officalRecordId + "']");
            if (officalNode != null) {
                officalNode.selectSingleNode("./DISPLAY_IND").text = "Y";
                eval(gridId + "_filter(\"UPDATE_IND != 'D'\")");
            }

            first(xmlData);
            beginDeleteMultipleRow(gridId);
            while (!xmlData.recordset.eof) {
                if (xmlData.recordset.Fields("COFFICIALRECORDID").value == officalRecordId && xmlData.recordset.Fields("CRECORDMODECODE").value != "OFFICIAL") {
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
// Add Fte
//-----------------------------------------------------------------------------
function addFte() {
    var addFteByEntity = getObjectValue("addFteByEntity");
    if (addFteByEntity == 'Y') {
        setInputFormField("seledEntityId", 0);
        setInputFormField("seledEntityName", "");
        openEntitySelectWinFullName("seledEntityId", "seledEntityName", "handleFindClient()");
    }
    else {
        openFteRisk();
    }
}


function openFteRisk() {
    var riskBaseRecordId = getObjectValue("childRiskBaseRecordId");
    var coveragePartBaseRecordId = getObjectValue("coveragePartBaseRecordId");
    var path = getAppPath() + "/riskmgr/empphysmgr/selectFteRisk.do?"
        + commonGetMenuQueryString() + "&riskBaseRecordId=" + riskBaseRecordId
        + "&coveragePartBaseRecordId=" + coveragePartBaseRecordId;
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, null, null, "", "", "", "", "selectFteRisk", false);
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleFindClient() {
    commonAddRow(getCurrentlySelectedGridId());
}
//-----------------------------------------------------------------------------
// Call back function for select risk
//-----------------------------------------------------------------------------
function handleOnSelectFteRisk(risks) {

    var riskCount = risks.length;
    for (var i = 0; i < riskCount; i++) {
        var fteRisk = risks[i];
        setInputFormField("fteRiskEntityId", fteRisk.entityId);
        setInputFormField("fteLocation", fteRisk.location);
        setInputFormField("fteRiskTypeCode", fteRisk.riskTypeCode);
        setInputFormField("fteRiskAddCode", fteRisk.addCode);
        setInputFormField("fteRiskBaseRecordId", fteRisk.riskBaseRecordId);
        commonAddRow(getCurrentlySelectedGridId());
    }
    var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
    showNonEmptyTable(getTableForXMLData(xmlData));
}

function empphysListGrid_setInitialValues() {
    var addFteByEntity = getObjectValue("addFteByEntity");
    var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
    var url;
    if (addFteByEntity == 'Y') {
        url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForEmployedPhysician"
            + "&entityId=" + getObjectValue("seledEntityId");
    }
    else {
        url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?" + commonGetMenuQueryString()
            + "&entityId=" + getObjectValue("fteRiskEntityId")
            + "&location=" + getObjectValue("fteLocation")
            + "&riskTypeCode=" + escape(getObjectValue("fteRiskTypeCode"))
            + "&riskBaseRecordId=" + getObjectValue("fteRiskBaseRecordId")
            + "&addCode=" + getObjectValue("fteRiskAddCode")
            + "&process=getInitialValuesForEmployedPhysician";
    }
    xmlData.recordset("CRISKCHILDID").value = getObjectValue("childRiskBaseRecordId");

    if (policyHeader.riskHeader) {
        url = url + "&riskId=" + policyHeader.riskHeader.riskId;
    }
    new AJAXRequest("get", url, '', handleOnGetInitialValueForEmployedPhysician, false);
}

//-----------------------------------------------------------------------------
// exclude transaction date field
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return new Array(
        "transEffectiveFromDate"
        );
}

function handleOnGetInitialValueForEmployedPhysician(ajax) {
    commonHandleOnGetInitialValues(ajax, "RISKNAMEHREF");
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "empphysListGrid") {
        var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
        var fieldCount = xmlData.recordset.Fields.count;
        var riskNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if(xmlData.recordset.Fields.Item(i).name == "CENTITYID" && hasObject("fteRiskEntityId")) {
                xmlData.recordset.Fields.Item(i).value = getObjectValue("fteRiskEntityId");
            }
            if (xmlData.recordset.Fields.Item(i).name == "CRISKNAME") {
                riskNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + riskNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("RISKNAMEHREF"))) {
                    href = "javascript:handleOnGridHref('empphysListGrid', '"
                            + getObjectValue("RISKNAMEHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    if (isFieldExistsInRecordset(empphysListGrid1.recordset, "CLOCATION")) {
        return empphysListGrid1.recordset("CLOCATION").value;
    } else {
        return null;
    }
}