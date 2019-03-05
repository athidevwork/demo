
//-----------------------------------------------------------------------------
// Javascript file for maintainTax.jsp.
//
// (C) 2014 Delphi Technology, inc. (dti)
// Date:   Oct 13, 2014
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/13/2014       wdang       158112 - Initial version, Maintain Premium Tax Information.
//-----------------------------------------------------------------------------

function getTaxGridId() {
    return "taxListGrid";
}

function getRiskGridId() {
    return "riskListGrid";
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'TAX_ADD':
            commonAddRow(getTaxGridId());
            break;
        case 'TAX_DEL':
            commonDeleteRow(getTaxGridId());
            filterTaxListData();
            break;
    }
}

function selectRowInGridOnPageLoad(){
    $.when(dti.oasis.grid.getLoadingPromise(getRiskGridId())).then(function(){
        selectFirstRowInGrid(getRiskGridId());
    });
    $.when(dti.oasis.grid.getLoadingPromise(getTaxGridId())).then(function(){
        selectFirstRowInGrid(getTaxGridId());
    });
}

function handleOnLoad(){
    if (hasObject("selectRiskId")) {
        selectRowById(getRiskGridId(), getObjectValue("selectRiskId"));
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            var riskId = riskListGrid1.recordset("ID").value;
            if (riskId) {
                setInputFormField("selectRiskId", riskId);
            }
            
            document.forms[0].process.value = "saveAllTax";
            alternateGrid_update(getTaxGridId());
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function filterTaxListData(){
    var riskId = riskListGrid1.recordset("ID").value;
    var filterCondition = "CRISKID=" + riskId;

    setTableProperty(eval(getTaxGridId()), "selectedTableRowNo", null);
    taxListGrid_filter(filterCondition);
}

function riskListGrid_selectRow(id) {
    filterTaxListData();
    var taxTaxGridData = getXMLDataForGridName(getTaxGridId());
    if (isEmptyRecordset(taxTaxGridData.recordset)) {
        hideEmptyTable(getTableForXMLData(taxTaxGridData));
    }
    else {
        showNonEmptyTable(getTableForXMLData(taxTaxGridData));
    }
}

function taxListGrid_setInitialValues(){
    var xmlData = getXMLDataForGridName(getTaxGridId());

    var url = getAppPath() + "/policymgr/taxmgr/maintainTax.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForAddTax";

    var riskId = riskListGrid1.recordset("ID").value;
    url = url + "&riskId=" + riskId;

    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}
