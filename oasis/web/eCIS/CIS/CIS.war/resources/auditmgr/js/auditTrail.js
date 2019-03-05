//-----------------------------------------------------------------------------
// Functions to support Audit tab pages.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 07/07/2016       Elvin       Issue 177718: remove initPage, selectFirstRowInGrid when handleOnLoad
// 07/14/2016       ylu         177661: skip check data change, since it is display page.
// 9/22/2018       dpang        195835: Override isPageDataChanged.
// 9/28/2018       dpang        195417: When refreshing, clear criteria to populate with default values
//-----------------------------------------------------------------------------

var rowid = -1;
var filter_operationTableFldID = "filterCriteria_operationTable";
var filter_fromDateFldID = "filterCriteria_fromDate";
var filter_toDateFldID = "filterCriteria_toDate";

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
}

function testgrid_setInitialValues() {
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'search':
            if (isDate2OnOrAfterDate1(getObjectValue(filter_fromDateFldID), getObjectValue(filter_toDateFldID))=='N') {
                alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(filter_toDateFldID), getLabel(filter_fromDateFldID))));
                proceed = false;
            } else {
                setInputFormField("process", "searchAuditTrailData");
            }
            break;
    }
    return proceed;
}

function handleOnButtonClick(action) {
    switch (action) {
        case 'refresh':
            //clear criteria to populate with default values
            clearCriteria();
            currentlySelectedGridId = "";
            var sAction = getFormActionAttribute();
            var url = getAppPath() + "/"+ sAction +
                "?process=getInitialValuesForSearchCriteria&date=" + new Date();
            new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
            //since get the default criteria, let's do default search
            showProcessingDivPopup();
            commonOnSubmit('search');
            break;
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == 'auditViewPref') {
        if (field.checked) {
            updateGridAuditViewPref(-1, 'USER_VIEW');
        } else {
            updateGridAuditViewPref(0, 'TECH_VIEW');
        }
    }
}

function updateGridAuditViewPref(auditViewPref, auditViewPrefValue) {
    testgrid_updatenode("CAUDITVIEWPREF", auditViewPref);
    testgrid_updatenode("CAUDITVIEWPREFVALUE", auditViewPrefValue);
}

/**
 * Override isPageDataChanged, in case isOkToChange page pops up when clicking entity list navigation
 * after checking field auditViewPref.
 */
function isPageDataChanged() {
    return false;
}

function clearCriteria() {
    setObjectValue("filterCriteria_operationTable", "");
    setObjectValue("filterCriteria_fromDate", "");
    setObjectValue("filterCriteria_toDate", "");
}
