//-----------------------------------------------------------------------------
// Javascript file for maintainInsuredTrackingListGrid.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   April 08, 2015
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/08/2015       wdang       157211 - Initial Version.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// The entry function for clicking button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            searchInsuredTracking();
            break;
        case 'ADD':
            addInsuredTracking();
            break;
        case 'SAVE':
            saveInsuredTracking();
            break;
        case 'CLEAR':
            clearSearchCriteria();
            break;
        case 'DEL':
            delInsuredTracking();
            break;
    }
}

//-----------------------------------------------------------------------------
// The entry function for changing field
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    var fieldName = field.name;
    if (fieldName == 'searchEntityName') {
        setObjectValue('searchEntityId', "");
    }
    return true;
}

//-----------------------------------------------------------------------------
// Do not set change indicator when change these fields   
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return ['searchTermHistoryId', 'searchEntityId', 'searchEntityName', 'searchInsuredType'];
}

//-----------------------------------------------------------------------------
// The entry when clicking Clear button
//-----------------------------------------------------------------------------
function clearSearchCriteria() {
    if (handleIsOkToChangePages()) {
        var objnames = ['searchTermHistoryId', 'searchEntityId', 'searchEntityName', 'searchInsuredType'];
        var emptyvalues = ['-1', '', '', ''];
        for (var i = 0; i < objnames.length; i++) {
            var obj = getObject(objnames[i]);
            obj.value = emptyvalues[i];
            // for multiple select

            if (obj.type == 'select-multiple') {
                var x = obj.options.length;
                if (obj.options[0].value == "") {
                    obj.options[0].selected = true;
                }
                else {
                    obj.options[0].selected = false;
                }
                for (var y = 1; y < x; y++) {
                    obj.options[y].selected = false;
                }
                var objMSVAL = getObject(objnames[i] + 'MSVAL');
                if (objMSVAL != null) {
                    objMSVAL.value = emptyvalues[i];
                }
                var objMSTXT = getObject(objnames[i] + 'MultiSelectText');
                if (objMSTXT != null) {
                    objMSTXT.value = obj.options.item(obj.options.selectedIndex).text;
                }
            }
        }
        setInputFormField("riskId", policyHeader.riskHeader.riskId);
        commonOnSubmit('loadAllInsuredTracking', true, true, false, true);
    }
}

//-----------------------------------------------------------------------------
//The entry when clicking Delete button
//-----------------------------------------------------------------------------
function delInsuredTracking() {
    var currentGrid = getCurrentlySelectedGridId();
    commonDeleteRow(currentGrid);
}

function handleIsOkToChangePages(){
    var functionExists = eval("window.commonIsOkToChangePages");
    if (functionExists) {
        var isOkToProceed = commonIsOkToChangePages("DIV_PUP", "");
        if (!isOkToProceed) {
            return false;
        }
    }
    return true;
}
//-----------------------------------------------------------------------------
// The entry when clicking Search button
//-----------------------------------------------------------------------------
function searchInsuredTracking() {
    if (handleIsOkToChangePages()) {
        setInputFormField("riskId", policyHeader.riskHeader.riskId);
        commonOnSubmit('loadAllInsuredTracking', true, true, false, true);
    }
}

//-----------------------------------------------------------------------------
// The entry when clicking Add button
//-----------------------------------------------------------------------------
function addInsuredTracking() {
    setInputFormField("selectEntityId", 0);
    setInputFormField("selectInsuredName", "");
    openEntitySelectWinFullName("selectEntityId", "selectInsuredName", "handleOnSelectEntity()");
}

//-----------------------------------------------------------------------------
//The entry when clicking Save button
//-----------------------------------------------------------------------------
function saveInsuredTracking() {
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    setInputFormField("riskBaseRecordId", policyHeader.riskHeader.riskBaseRecordId);
    commonOnSubmit('saveAllInsuredTracking', false, false, false, true);
}

//-----------------------------------------------------------------------------
// The call back function when an entity is selected.
//-----------------------------------------------------------------------------
function handleOnSelectEntity() {
    commonAddRow(getCurrentlySelectedGridId());
}

//-----------------------------------------------------------------------------
// The entry when adding row
//-----------------------------------------------------------------------------
function maintainInsuredTrackingListGrid_setInitialValues() {
    sendAJAXRequest("getInitialValuesForInsuredTracking");
    maintainInsuredTrackingListGrid1.recordset("CINSUREDNAME").value = getObjectValue("selectInsuredName");
}

//-----------------------------------------------------------------------------
// The call back function when a row is added 
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForInsuredTracking(ajax) {
    commonHandleOnGetInitialValues(ajax, "INSUREDNAMEHREF");
}

//-----------------------------------------------------------------------------
// The function to call entity finder in search layer
//-----------------------------------------------------------------------------
function find(fieldId) {
    if (fieldId == "searchEntityName") {
        openEntitySelectWinFullName("searchEntityId", "searchEntityName");
    }
}

//-----------------------------------------------------------------------------
// Send AJAX request
//-----------------------------------------------------------------------------
function sendAJAXRequest(process) {
    // set url
    var url = getAppPath() + "/riskmgr/insuredmgr/maintainInsuredTracking.do?"
        + commonGetMenuQueryString() + "&process=" + process;

    switch (process) {
        case 'getInitialValuesForInsuredTracking':
            url += "&entityId=" + getObjectValue("selectEntityId");
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

//-----------------------------------------------------------------------------
// Handle the url of risk name column
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "maintainInsuredTrackingListGrid") {
        var xmlData = getXMLDataForGridName("maintainInsuredTrackingListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var riskNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CINSUREDNAME") {
                riskNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name == "URL_" + riskNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("insuredNameHref"))) {
                    href = "javascript:handleOnGridHref('maintainInsuredTrackingListGrid', '" + getObjectValue("insuredNameHref") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}