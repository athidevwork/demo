//-----------------------------------------------------------------------------
// Functions to support property edit page.
// Author: unknown
// Date:   unknown
// Modifications:
// Blake   3/16/2011     Modified handleOnChange function for issue 118391.
// kshen  04/19/2011     119753.
// ylu    08/06/2018     Issue 194134: Grid Replacement.
//-----

var hasErrorMessages = "";
function CIPropertyForm_btnClick(asBtn){
    switch (asBtn.toUpperCase()) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            testgrid_updatenode("CUSERSELECTEDB", 'Y');
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            testgrid_updatenode("CUSERSELECTEDB", 'N');
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
    }
}

function handleOnButtonClick(asBtn){
    switch (asBtn.toUpperCase()) {
        case 'SELECT':
            selectProperty();
            break;
        case 'REFRESH':
            setInputFormField("process", "lookupProperty");
            submitFirstForm();
            break;
        case 'CLEAR':
            clearFilterCriteria();
            break;
        default:
    }
}

function selectProperty(){
    var gridId = getCurrentlySelectedGridId();
    var xmlData = getXMLDataForGridName(gridId);
    var selectedDataArray = getSelectedKeys(xmlData);

    if (selectedDataArray.length != 1) {
        alert(getMessage("ci.common.error.onlyOneRow.noSelect"));
        return;
    } else {
        selectRowById("testgrid", testgrid1.recordset("ID").value);
        if (syncPropertyData()) commonOnButtonClick('CLOSE_RO_DIV');
    }
}

function syncPropertyData(){
    var syncSuccessfully = false;
    var clientProperty = new Object();
    var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
    var recordSet = xmlData.recordset;
    if (recordSet && recordSet.Fields) {
        for (var i = recordSet.Fields.Count - 1; i >= 0; i--) {
            var fieldName = recordSet.Fields(i).Name;
            var fieldValue = recordSet(fieldName).value;
            if (fieldName.startsWith('C')) {
                if (fieldName == "CCLIENTPROPERTYID") {// the anchor column
                    fieldName = "CLIENTPROPERTYID";
                    fieldValue = fieldValue.replace(/\D/g, "");
                }
                else {
                    fieldName = 'CP' + fieldName.substring(1); // all names in maintainPropertyLoss starts with CP
                }
                eval("clientProperty." + fieldName + "='" + replace(fieldValue, "\'", "\\\'") + "'");
            }
        }
    }
    var parentWindow = getParentWindow();
    if (parentWindow) {
        syncSuccessfully = parentWindow.handleOnSelectProperty(clientProperty);
    }
   return syncSuccessfully;
}

function userRowchange(obj) {
    switch (obj.name) {
       case "chkCSELECT_IND":
             if (testgrid1.recordset("CUSERSELECTEDB").value =='N') {
                 testgrid1.recordset("CUSERSELECTEDB").value ='Y';
              } else {
                 testgrid1.recordset("CUSERSELECTEDB").value ='N';
             }
             testgrid1.recordset("UPDATE_IND").value = "Y";
             handleOnChange(getObject("flt_userSelectedB"));
             break;
    }
}

function handleOnChange(field){
    if (isFilteringField(field.name)) {
        var filteringString = buildFilterStringBasedOnFilteringValues();
        setTableProperty(getTableForGrid("testgrid"), "selectedTableRowNo", 1);
        testgrid_filter(filteringString);
    } else if (eval("handleOnChangeNonFilterFields")) {
        handleOnChangeNonFilterFields(field);
    }
}

function getAllFilterFieldsAsArray() {
    var filteringFields = new Array();
        filteringFields.push("flt_city");
        filteringFields.push("flt_stateCode");
        filteringFields.push("flt_zipCode");
        filteringFields.push("flt_addressLine1");
        filteringFields.push("flt_propertyDescription");
        filteringFields.push("flt_propertyID");
        filteringFields.push("flt_propertyType");
        filteringFields.push("flt_locationCode");
        filteringFields.push("flt_userSelectedB");
    return filteringFields;
}

function isFilteringField(fieldName){
    var is = false;
    var allFilteringFields = getAllFilterFieldsAsArray();
    for (var i = 0; i < allFilteringFields.length && !is; i++) {
        is = (allFilteringFields[i] == fieldName);
    }
    return is;
}

function buildFilterStringBasedOnFilteringValues(){
    var allFilteringFields = getAllFilterFieldsAsArray();
    var filterString = " 1=1 ";
    for (var i = 0; i < allFilteringFields.length; i++) {
        var fieldValue = getObjectValue(allFilteringFields[i]);
        if (!isEmpty(fieldValue) && fieldValue != "-1") {
            var gridColumnName = allFilteringFields[i].replace("flt_", 'C').toUpperCase();
            filterString += " and " + gridColumnName + "='" + fieldValue + "'";
        }
    }
    return filterString;
}
function clearFilterCriteria(){
    var allFilteringFields = getAllFilterFieldsAsArray();
    for (var i = 0; i < allFilteringFields.length; i++) {
        var field = getObject(allFilteringFields[i]);
        setObjectValue(field, ""); // set to null,
    }
    testgrid_filter("1=1");
}

