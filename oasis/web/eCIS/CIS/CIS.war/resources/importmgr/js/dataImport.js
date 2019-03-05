//-----------------------------------------------------------------------------
// For Data Import
// Author: ldong
// Date:   July 1, 2014
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  04/27/2017       ddai         183685: Check if the grid exist before selectRow.
//  07/05/2017       dpang        184234: Check if the grid exists before saving.
//  04/20/2018       ylu          192772: refactor Import Data page
//  10/12/2018       dpang        195835: Grid replacement
//  ---------------------------------------------------
var rowid = "-1";
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'processFile':
            if (isWhitespace(getObjectValue("dataFile"))) {
                alert(getMessage("ci.import.error.file.isRequired", new Array(getLabel("dataFile"))));
                proceed = false;
            } else {
                setObjectValue("process", "processData");
            }
            break;
        case 'save':
            setObjectValue('process', 'saveData');
            break;
    }
    return proceed;
}

function entityGrid_selectRow(pk) {
    rowid = pk;
    getRow(entityGrid1, pk);
    if(getTableForGrid("addressGrid")){
        setTableProperty(getTableForGrid("addressGrid"), "selectedTableRowNo", null);
        //filter address grid
        addressGrid_filter("CSOURCERECORDID = '" + pk + "'" + " and UPDATE_IND != 'D' and @id != '-9999' ");
        if (!isEmptyRecordset(addressGrid1.recordset)) {
            showNonEmptyTable(addressGrid);
            eval(addressGrid1.recordset("CADDRESSID").value);
        }
        else {
            hideEmptyTable(getTableForXMLData(addressGrid1));
        }
    }

    if(getTableForGrid("licenseGrid")){
        setTableProperty(getTableForGrid("licenseGrid"), "selectedTableRowNo", null);
        //filter license grid
        licenseGrid_filter("CENTITYID = '" + pk + "'" + " and UPDATE_IND != 'D' and @id != '-9999' ");
        if (!isEmptyRecordset(licenseGrid1.recordset)) {
            showNonEmptyTable(licenseGrid);
            eval(licenseGrid1.recordset("CLICENSEPROFILEID").value);
        }
        else {
            hideEmptyTable(getTableForXMLData(licenseGrid1));
        }
    }
}

function addressGrid_selectRow(pk) {
    rowid = pk;
    getRow(addressGrid1, pk);
    setTableProperty(getTableForGrid("phoneGrid"), "selectedTableRowNo", null);
    //filter phone grid
    phoneGrid_filter("CSOURCERECORDID = '" + pk + "'" + " and UPDATE_IND != 'D' and @id != '-9999' ");
    if (!isEmptyRecordset(phoneGrid1.recordset)) {
        showNonEmptyTable(phoneGrid);
        eval(phoneGrid1.recordset("CPHONENUMBERID").value);
    }
    else {
        hideEmptyTable(getTableForXMLData(phoneGrid1));
    }
}

function savePage() {
    if (!isGridDataChanged("entityGrid")) {
        return;
    }
    var dataString = getSelectedKeysString(entityGrid1, "ENTITYSELECT", ',');
    var dataArray = dataString.substring(0, dataString.length - 1).split(',');
    if (dataArray.length == 0 || dataString == '') {
        return;
    }
    for (var i = 0; i < dataArray.length; i++) {
        selectRow("entityGrid", dataArray[i]);
        if (entityGrid1.recordset("CENTITYSTATUS").value != 'DUPLICATE') {
            alert(getMessage("ci.import.process.duplicate.select.error"));
            return;
        }
    }

    updateGrids();
    setObjectValue("process", "saveData");
    submitFirstForm();
}

//-----------------------------------------------------------------------------
// update the alternated grid
//-----------------------------------------------------------------------------
function updateGrids() {
    var grids = ["entityGrid", "addressGrid", "phoneGrid", "licenseGrid"];

    for (var i = 0; i < grids.length; i++) {
        if (isDefined(getObject(grids[i]))) {
            alternateGrid_update(grids[i]);
        }
    }
}