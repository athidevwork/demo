/**
 * Created by kshen on 4/14/2016.
 */
/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 06/11/2018       fhuang      193793 - Modified endDeleteMultipleRow() to call initAndLoadGrid()
                                       to prevent empty recordset after delete multiple rows
 09/07/2018        cesar      194886 - Modified generateRowDataFromXMLData() not to export column value when field is masked.
 10/17/2018        cesar      196161 - enabled setAllNumbersColorFields() in readyStateReady().
                                       setAllNumbersColorInGrid() will be handled by calling getAllNumbersColorInGrid () in __getCellsRenderer().
 11/06/2018        clm        195889 - add the logic to set update_ind in rowchange
 11/12/2018        wreeder    196160 - Implement the XMLsort() method to call dti.oasis.grid.sort()
 11/16/2018        kshen      196922 - Changed the function endDeleteMultipleRow to remove the codes about reloading grid after deleteRows.
----------------------------------------------------------------------------
 */

var tblPropArray = getTablePropertyArray();

function getTablePropertyArray() {
    return dti.oasis.page.getGridPropertyArray();
}

function getTableProperty(tbl, propName) {
    return dti.oasis.grid.getProperty(tbl.id, propName);
}

function setTableProperty(tbl, propName, propValue) {
    dti.oasis.grid.setProperty(tbl.id, propName, propValue);
}

function getGridIdToFireSelectFirstRowInGrid() {
    return dti.oasis.page.getGridIdToFireSelectFirstRowInGrid();
}

function getRow(XMLData, rowId) {
    if (!isEmptyRecordset(XMLData.recordset)) {
        var i = 0;

        XMLData.recordset.MoveFirst();
        while (!XMLData.recordset.EOF) {
            if (XMLData.recordset("ID").value == rowId) {
                return i;
            }

            XMLData.recordset.MoveNext();
            i++;
        }
    }

    return -1;
}

function readyStateReady(tbl) {
    begintime = new Date();

    if (isEmptyRecordset(eval(tbl.id+'1').recordset)) {
        setTableProperty(tbl, "selectedTableRowNo", null);
    }

    if (window.userReadyStateReady)
        userReadyStateReady(tbl);

    if (getTableProperty(tbl, "filtering")) {
        setTableProperty(tbl, "filtering", false);
        dti.oasis.grid._protected.resolveFilteringDeferredObj(tbl.id);
    }

    if (getTableProperty(tbl, "sorting")) {
        setTableProperty(tbl, "sorting", false);
        dti.oasis.grid.getProperty(tbl.id, "sortingDeferredObj").resolve();
    }

    setAllNumbersColorFields();

    // TODO Set focus back to field
    //if (window.setFocusBackToOriginalField) {
    //    setFocusBackToOriginalField(tbl);
    //}
    endtime = new Date();
}

function selectFirstRowInGrid(gridName) {
    if (window.document.readyState != "complete") {
        dti.oasis.page.addGridIdToFireSelectFirstRowInGrid(gridName);
        return;
    }

    var XMLData = getXMLDataForGridName(gridName);
    currentlySelectedGridId = gridName;
    if (isEmptyRecordset(XMLData.recordset) == false) {
        if (XMLData != null && XMLData.recordset.RecordCount > 0) {
            dti.oasis.grid.selectFirstRowInGrid(gridName);
        }
    }
    else {
        // Hide the empty row in table.
        hideEmptyTable(getTableForGrid(gridName));
        hideGridDetailDiv(gridName);
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, gridName);
        }
    }
}

function showNonEmptyTable(tbl) {
    begintime = new Date();
    // loop through the tables
    for (var k = 0; k <= tblCount; k ++) {
        // look for our particular table
        if (tblPropArray[k].id == tbl.id) {
            tblPropArray[k].hasrows = true;
            //tbl.style.display = 'block';
            return true;
        }
    }

    endtime = new Date();

    return false;
}

function hideEmptyTable(tbl) {
    begintime = new Date();
    // loop through the tables
    for (var k = 0; k < tblCount; k ++) {
        // look for our particular table
        if (tblPropArray[k].id == tbl.id) {
            tblPropArray[k].hasrows = false;
            // hide the table
            // tbl.style.display = 'none';
            break;
        }
    }
    endtime = new Date();
}

function isEmptyRecordset(recordSet) {
    return (recordSet && recordSet.RecordCount == 0);
}

function isFieldDefinedForGrid(gridId, fieldName) {
    return dti.oasis.grid.hasColumn(gridId, fieldName);
}

function getSelectedRow(gridId) {
    return dti.oasis.grid.getSelectedRowId(gridId);
}

function setSelectedRow(gridId, rowId) {
    dti.oasis.grid.setSelectedRowId(gridId, rowId);
}

function getDivForGrid(gridId) {
    var gridDiv = getObjectById(gridId);
    return gridDiv;
}

function getLastIndex(origXMLData) {
    var gridId = origXMLData.getGridId();

    return dti.oasis.grid.getLastIndex(gridId);
}

function hasXMLDataForGridName(gridId) {
    return (typeof window[gridId + "1"] != "undefined");
}

function hasXMLDataForTable(table) {
    return (typeof window[table.id + "1"] != "undefined");
}

function getSelectedData(dataIsland) {
    var gridId = dataIsland.getGridId();

    return dti.oasis.grid.getSelectedDataArray(gridId);
}

function getSelectedDataString(dataIsland, rowDelim, colDelim) {
    var gridId = dataIsland.getGridId();

    return dti.oasis.grid.getSelectedDataString(gridId, rowDelim, colDelim);
}

function getSelectedKeys(dataIsland) {
    var gridId = dataIsland.getGridId();

    return dti.oasis.grid.getSelectedKeys(gridId);
}

function getSelectedKeysString(dataIsland, SelectCol, rowDelim) {
    var gridId = dataIsland.getGridId();

    return dti.oasis.grid.getSelectedKeysString(gridId, "C" + SelectCol, rowDelim);
}

function gotopage(tbl, type) {
    dti.oasis.grid.gotoPageByType(tbl.id, type);
}

function paginate(tbl, type) {
    gotopage(tbl, type);
}

function getRowIdByRowIndex(dataIsland, rowIndex) {
    var gridId = dataIsland.getGridId();

    return dti.oasis.grid.getRowIdByRawDataIndex(gridId, rowIndex);
}

function isRecordSelected(dataIsland) {
    var gridId = dataIsland.getGridId();
    return dti.oasis.grid.isRecordSelected(gridId);
}

function selectRowById(gridId, rowId) {
    // do nothing if we have no row to select
    if (rowId == null || gridId == null) {
        //alert("Invalid ID(s) or emplty grid");
        return;
    }

    // hide the grid if the grid has no row.
    if (!getTableProperty(getTableForGrid(gridId), "hasrows")) {
        currentlySelectedGridId = gridId;
        // Hide the empty row in table.
        // hideEmptyTable(getTableForGrid(gridId));
        hideGridDetailDiv(gridId);

        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, gridId);
        }
        return;
    }

    if (dti.oasis.grid.hasRow(gridId, rowId)) {
        dti.oasis.grid.selectRowById(gridId, rowId);
    }
}

function saveGridAsExcelCsv(gridId, dispType, fullExport) {
    if (fullExport) {
        isFullyExport = fullExport;
    } else {
        isFullyExport = false;
    }

    var recordCount = dti.oasis.grid.getRecordCount(gridId);

    if (recordCount > 0) {
        exportColumnsNames = generateExportColumns(gridId, null, fullExport);

        var exportType = eval(gridId + "_getExportType()");
        if (exportType == 'CSV') {
            sendGridToServerAsExcelCsv(gridId, getCorePath() + "/gridToExcelCSV.jsp?gridId=" + gridId + "&date=" + new Date(), dispType);
        } else {
            var pageName = pageTitle + '(' + pageCode + ')';
            pageName = encodeURIComponent(pageName);
            sendGridToServerAsExcelCsv(gridId, getCorePath() + "/gridToExcelXLS.jsp?exportType=" + exportType + "&gridId=" + gridId + "&date=" + new Date() + "&pageName=" + pageName, dispType);
        }
    } else {
        alert(getMessage("core.export.excel.nodata"));
    }

    return false;
}

function generateExportColumns(gridId, tbl, isFullyExport) {
    var exportVisibleColumnNames = "";
    var exportHideColumnNames = "";
    var exportVisibleIndex = 0;
    var exportHideIndex = 0;
    exportVisibleColumnIds = [];
    exportHideColumnIds = [];

    var columns = $("#".concat(gridId)).jqxGrid("columns")["records"];
    var columnsConfig = dti.oasis.page.getGridInfo(gridId)["columnsConfig"];

    for (var i = 0; i < columns.length; i++) {
        var columnLabel = columns[i]["text"] || "";

        if (!dti.oasis.string.isEmpty(columnLabel)) {
            columnLabel = columnLabel.split(',').join(':;:');
            // Remove the <BR> and empty lines for exporting label. (It follows the rule in the current oasis. Do we need to changed to replace with blank space?)
            columnLabel = columnLabel.replace(/<br>+/g, '').replace(/<BR>+/g, '')
                .replace(/\r\n+/g, '').replace(/\n+/g, '')
                .replace(/&nbsp;/, ' ');

            if (!columns[i].hasOwnProperty("hidden") || !columns[i]["hidden"]) {
                exportVisibleColumnIds[exportVisibleIndex++] = columns[i]["datafield"];
                exportVisibleColumnNames += columnLabel + ",";
            } else {
                if (isFullyExport) {
                    if (columnLabel == columns[i]["datafield"]) {
                        for (var j = 0; j < columnsConfig.length; j++) {
                            if (columnsConfig[j]["id"] == columnLabel) {
                                if (columnsConfig[j]["label"] && !dti.oasis.string.isEmpty(columnsConfig[j]["label"])) {
                                    exportHideColumnIds[exportHideIndex++] = columns[i]["datafield"];
                                    exportHideColumnNames += columnLabel + (columnLabel.indexOf("(Hidden)") == -1 ? "(Hidden)," : ",");
                                }
                                break;
                            }
                        }
                    } else {
                        exportHideColumnIds[exportHideIndex++] = columns[i]["datafield"];
                        exportHideColumnNames += columnLabel + (columnLabel.indexOf("(Hidden)") == -1 ? "(Hidden)," : ",");
                    }
                }
            }
        }
    }

    var colNames = exportVisibleColumnNames;
    if (isFullyExport) {
        colNames += exportHideColumnNames;
        colNames += generateExportColumnsForNoConfigurationFields(gridId, exportHideIndex);
    }
    colNames = colNames.substring(0, colNames.length - 1);
    return colNames;
}

function generateExportColumnsForNoConfigurationFields(gridId, exportHideIndex, hideColumnArray) {
    var hiddenColumnName = "";

    var columnNames = dti.oasis.grid.getColumnNames(gridId);

    for (var i = 0; i < columnNames.length; i++) {
        var columnName = columnNames[i].toUpperCase();
        var columnNameUpper = columnName.toUpperCase();

        if (columnNameUpper == "ID" || columnNameUpper == "@ID" ||
            columnNameUpper == "INDEX" || columnNameUpper == "@INDEX" ||
            columnNameUpper == "COL" || columnNameUpper == "@COL" ||
            columnNameUpper.startsWith("DATE_") || columnNameUpper.startsWith("URL_") ||
            columnNameUpper == "UPDATE_IND" || columnNameUpper == "DISPLAY_IND" || columnNameUpper == "EDIT_IND" ||
            columnNameUpper == "OBR_ENFORCED_RESULT" || columnNameUpper == "$TEXT" ||
            columnNameUpper.endsWith("LOVLABEL") || columnNameUpper.endsWith("_DISP_ONLY")) {
            continue;
        }

        if (columnNames.indexOf(columnName.concat("LOVLABEL")) == -1) {
            if (exportVisibleColumnIds.indexOf(columnName) != -1 || exportHideColumnIds.indexOf(columnName) != -1) {
                continue;
            }
        }

        exportHideColumnIds[exportHideIndex++] = columnName;
        hiddenColumnName += columnName + ",";
    }

    return hiddenColumnName;
}

function exportExcelFromXMLData(gridId, startColumn) {
    var xmlData = getXMLDataForGridName(gridId);
    first(xmlData);
    var vCSVTxt = "\n";
    while (!xmlData.recordset.EOF) {
        for (var j = 0; j < exportVisibleColumnIds.length; j++) {
            if (j > 0) {
                vCSVTxt += ",";
            }
            vCSVTxt += generateRowDataFromXMLData(xmlData, exportVisibleColumnIds[j]);
        }
        if (isFullyExport) {
            for (var k = 0; k < exportHideColumnIds.length; k++) {
                vCSVTxt += ",";
                vCSVTxt += generateRowDataFromXMLData(xmlData, exportHideColumnIds[k], true);
            }
        }
        vCSVTxt += "\n";
        next(xmlData);
    }
    postFormTextForCsvHtmlForExcel(vCSVTxt, ourl, odispType);
}

function generateRowDataFromXMLData(xmlData, columnArray, isHidden) {
    var xmlValue = "";
    var columnInfo = columnArray.split(",,,");
    var columnName = columnInfo[0];
    var columnType = $('#'.concat(xmlData.getGridId())).jqxGrid('getcolumnproperty', columnName, 'columntype');
    var displayField = $('#'.concat(xmlData.getGridId())).jqxGrid('getcolumnproperty', columnName, 'displayfield');
    var gridId = xmlData.getGridId();

    var maskedField = dti.oasis.grid.isFieldMasked(gridId, columnName);
    if (maskedField) {
        xmlValue = dti.oasis.grid.DEFAULT_FORMATTED_MASKED_VALUE;
    } else {
        if (!columnName.endsWith("(Hidden)") && isHidden) {
            // TODO Format data?
            xmlValue = xmlData.recordset(columnName).value;
        } else {
            if (dti.oasis.grid.hasColumn(xmlData.getGridId(), columnName.concat("LOVLABEL"))) {
                try {
                    xmlValue = xmlData.recordset(columnName + "LOVLABEL").value;
                }
                catch (ex) {
                    xmlValue = xmlData.recordset(columnName).value;
                }
            }
            else if (!dti.oasis.string.isEmpty(displayField) && dti.oasis.grid.hasColumn(gridId, displayField)) {
                try {
                    xmlValue = xmlData.recordset(displayField).value;
                }
                catch (ex) {
                    xmlValue = xmlData.recordset(columnName).value;
                }
            }
            else {
                // TODO Format data?
                xmlValue = xmlData.recordset(columnName).value;
            }

            // Handle checkbox value
            if (columnType && columnType != "" && columnType.toLowerCase() == "checkbox") {
                if (xmlValue == "1" || xmlValue == "-1" || xmlValue.toUpperCase() == "Y" || xmlValue.toUpperCase() == "YES") {
                    xmlValue = "on";
                }
                else if (xmlValue == "0" || xmlValue.toUpperCase() == "N" || xmlValue.toUpperCase() == "NO") {
                    xmlValue = "off";
                }
            }
        }
    }

    return  '"' + xmlValue.replace(/^\s+/g, '').replace(/\s+$/g, '').replace(/\r\n+/g, ':;:').replace(/\n+/g, ':;:') + '"';
}

function sendGridToServerAsExcelHtml(grid, url, dispType) {
    ourl = url;
    odispType = dispType;
    exportColumnsNames = generateExportColumns(grid, null, false);
    formatGridAsCsvOrHtml(grid, 0, "HTML");
}

function getSelectedRowDataJqx(gridId) {
    var selector = "#".concat(gridId);
    var gridObj = $(selector);
    var rawData = dti.oasis.grid.getRawData(gridId);

    var displayRows = "";
    var nonDisplayRows = "";

    for (var i = 0; i < rawData.length; i++) {
        var rowData = rawData[i];
        var rowId = rowData["@id"];

        if (rowData["CSELECT_IND"] == "-1" && gridObj.jqxGrid("getrowboundindexbyid", rowId) != -1) {
            var result = '<ROW id="' + rowId + '">';
            var displayInd = "";

            for (var p in rowData) {
                if (rowData.hasOwnProperty(p) && p.indexOf("@") == -1) {
                    var nodeValue = rowData[p];

                    if (moneyFormatPattern.test(nodeValue)) {
                        // deal the case the negative number
                        if (paraPattern.test(nodeValue)) {
                            nodeValue = nodeValue.replace(/\(/g, '');
                            nodeValue = nodeValue.replace(/\)/g, '');
                            nodeValue = "-" + nodeValue;
                        }
                        // remove '$"
                        nodeValue = nodeValue.replace(/\$/g, '');
                        // remove ","
                        nodeValue = nodeValue.replace(/,/g, '');
                    } else if (percentagePattern.test(nodeValue)) {
                        nodeValue = convertPctToNumber(nodeValue);
                    }
                    result += "<" + p + ">" + nodeValue + "</" + p + ">";

                    if (p == "DISPLAY_IND")
                        displayInd = nodeValue;
                }
            }

            result += "</ROW>";

            if (displayInd == "Y")
                displayRows += result;
            else
                nonDisplayRows += result;
        }
    }

    result = "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
    return result;
}

function XMLsort(xslStyleSheet, XMLData, field, fieldtype) {
    dti.oasis.grid.sort(getTableForXMLData(XMLData).id, field);
}

function insertRow(tbl, origXMLData, XMLData, addingDummyRecord) {
    dti.oasis.grid.addRow(tbl.id);
}

function deleteRow(tbl, origXMLData, XMLData, addingDummyRecord) {
    dti.oasis.grid.deleteRow(tbl.id);
}

function endDeleteMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    //var count = getTableProperty(table, "DeletedMultipleRowsCount");
    //if (count > 0) {
    //    var orig_grid_id = getOrigXMLDataId(getXMLDataForTable(table));
    //    eval(orig_grid_id + "_filter(" + USE_CURRENT_FILTER_VALUE + ")");
    //}
    setTableProperty(table, "isDeleteMultipleRow", false);
    setTableProperty(table, "DeletedMultipleRowsCount", 0);

    // The function initAndLoadGrid was called for fix the JS error on eFM / Payment Plan / Custom window.
    // Since re-load grid will cause the problem that the filter of the grid would be cleared, and it seems that the original
    // problem is solved after we use deferred object to loading child grid, comment out the function for now.
    //dti.oasis.grid.initAndLoadGrid({"gridInfo": dti.oasis.page.getGridInfo(gridId)});
    selectFirstRowInGrid(gridId);
}

function getLastInsertedRowId(gridId) {
    return dti.oasis.grid.getLastInsertedRowId(gridId);
}

function filter(table, filterString) {
    begintime = new Date();
    var filterObj = dti.oasis.filter.compile("DISPLAY_IND = \"Y\" and UPDATE_IND != \"D\" and UPDATE_IND != \"I-D\"");

    filterflag = true;
    setTableProperty(table, "filterflag", true);
    setTableProperty(table, "filtering", true);

    if (!isEmpty(filterString)) {
        filterObj = filterString == USE_CURRENT_FILTER_VALUE ? getTableProperty(table, "currentFilterValue") : filterObj.and(dti.oasis.filter.compile(filterString));
    }

    //filterObj.and(dti.oasis.filter.compile("@id!='-9999'"));

    setTableProperty(table, "currentFilterValue", filterObj);

    var filteringPromise = dti.oasis.grid.filter(table.id, filterObj);

    endtime = new Date();

    return filteringPromise;
}

function getChanges(ReferenceXML) {
    return dti.oasis.grid.getGridChangesByColIndex(ReferenceXML.getGridId());
}

function getChangesOnly(ReferenceXML) {
    return dti.oasis.grid.getGridChangesOnly(ReferenceXML.getGridId());
}

function syncChanges(ReferenceXML, NewXML, filterValue) {
    // raw data approach doesn't require sync changes.
}

function getGridIds() {
    return dti.oasis.page.getProperty("gridIds");
}

function isFieldExistsInRecordset(recordSet, fieldName) {
    return dti.oasis.grid.hasColumn(recordSet.getGridId(), fieldName);
}

function getTableForGridElement(element) {
    return getTableForGrid(element.gridId);
}

function rowchange(c) {
    begintime = new Date();
    // stop hilighting the cell the element is in!
    //c.parentElement.bgColor='#073580';

    //if(c.name.endsWith(DISPLAY_FIELD_EXTENTION)){
    //    if(c.className=='clsNumFmtd')
    //        syncDisplayableFormattedNumberToGrid(c.formatPattern);
    //    else if(c.className=='clsDate')
    //        syncDisplayableDateToGrid();
    //}

    gridDataChange = true;
    if (isMultiGridSupported) {
        setTableProperty(getTableForGridElement(c), "gridDataChange", true);
    }

    // uncheck select-all if deselect a row
    if (dti.oasis.grid.hasSelectIndColumnName(c.gridId) &&
            dti.oasis.grid.getSelectIndColumnName(c.gridId).toUpperCase() == c.dataField.toUpperCase()) {
        if (!c.checked) {
            $("#".concat(c.gridId, "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
        }
    }
    else {
        var rowData = dti.oasis.grid.getRowDataByRowIndex(c.gridId, c.rowIndex);
        if (rowData["UPDATE_IND"] !== "I") {
            rowData["UPDATE_IND"] = "Y";
        }
    }

    // jqxGrid doesn't need to do so.
    // Update the value of original date field.
    //if (!dateFormatUS && c.name.endsWith(DISPLAY_FIELD_EXTENTION) && c.className=='clsDate') {
    //    var nodes = c.parentElement.parentElement.getElementsByTagName("INPUT");
    //    for (var i=0; i<nodes.length; i++) {
    //        if (nodes[i].name == normalizeFieldName(c) ) {
    //            updateHiddenFieldForDateField(c, nodes[i])
    //            c = nodes[i];
    //            break;
    //        }
    //    }
    //}

    // jqxGrid doesn't need to do so.
    // Update the value of original number formatted field.
    //if (c.name.endsWith(DISPLAY_FIELD_EXTENTION) && c.className=='clsNumFmtd') {
    //    var nodes = c.parentElement.parentElement.getElementsByTagName("INPUT");
    //    for (var i=0; i<nodes.length; i++) {
    //        if (nodes[i].name == normalizeFieldName(c) ) {
    //            updateHiddenFieldForNumberFormattedField(c, nodes[i], c.formatPattern)
    //            c = nodes[i];
    //            break;
    //        }
    //    }
    //}

    if (window.commonRowchange)
        commonRowchange(c);

    if (window.userRowchange)
        userRowchange(c);

    endtime = new Date();
//    logDebug("Time spent in rowchange:" + (endtime.getTime() - begintime.getTime()) + "ms");
}

/**
 * jqxGrid doesn't need this function to set table property. Overwrite the function to avoid JS errors.
 *
 * @param tbl
 * @param XMLData
 */
function setTable(tbl, XMLData) {
}

/**
 * jqxGrid doesn't need this function to set table property. Overwrite the function to avoid JS errors.
 *
 * @param XMLData
 */
function displayGridNavButtons(XMLData) {

}
