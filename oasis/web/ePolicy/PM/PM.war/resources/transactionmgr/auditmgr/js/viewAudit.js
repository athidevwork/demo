//-----------------------------------------------------------------------------
// Javascript file for viewAudit.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   April 26, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/26/2010       syang       106401 - Add handleReadyStateReady to hide empty row in grid.
//-----------------------------------------------------------------------------

function handleOnChange(field) {
    var fieldName = field.name;
    var fieldValue = field.value;
    switch (fieldName) {
        case "YesNo":
            var selectedDataGrid = getXMLDataForGridName("auditListGrid");
            var detail;
            if (fieldValue == 'Y') {
                detail = selectedDataGrid.recordset("CUSERVIEWDESCR").value;
            }
            else if (fieldValue == 'N') {
                detail = selectedDataGrid.recordset("CTRANSDESCR").value;
            }
            setInputFormField("details", detail);
            break;
    }
}
function auditListGrid_selectRow(rowId) {
    setInputFormField("YesNo", 'N');
    var selectedDataGrid = getXMLDataForGridName("auditListGrid");
    detail = selectedDataGrid.recordset("CTRANSDESCR").value;
    setInputFormField("details", detail);
}
function handleOnLoad() {
    getObject("details").readOnly = true;
}
//-----------------------------------------------------------------------------
// Fix issue 106401, system shouldn't display the empty row in grid.
//-----------------------------------------------------------------------------
function handleReadyStateReady(tbl){
    if(isEmptyRecordset(auditListGrid1.recordset)){
        hideEmptyTable(tbl);
    }
}