function processingEventListGrid_selectRow(id) {
    // Filter out the processing detail for the selected event.
    setTableProperty(eval("processingDetailListGrid"), "selectedTableRowNo", null);
    processingDetailListGrid_filter("CPMCORPORGDISCHISTID = " + id);
    var memberXmlData = getXMLDataForGridName("processingDetailListGrid");
    if (isEmptyRecordset(memberXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(memberXmlData));
    }
    else {
        showNonEmptyTable(getTableForXMLData(memberXmlData));
        reconnectAllFields(document.forms[0]);
    }
}