//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/14/2010       tzhao       issue#109875 - Modified money format script to support multiple currency.
// 05/24/2018       mlm         193214 - Replaced .attributes('dataSrc') and .attributes('dataFld') with getDataSrc() and
//                                       getDataField() respectively.
//-----------------------------------------------------------------------------
var paraPattern = RegExp("^\\s*\\(.+\\)\\s*$");
var percentagePattern = RegExp("^\\s*((-|\\+)?\\d+)(\\.\\d+)*%{1}$");

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SHOWSUM':
            var url = "maintainExcessPremium.do?" + commonGetMenuQueryString() + "&process=loadAllExcessPremiumSummary" +
                      "&fromCoverage=" + getObjectValue("fromCoverage") +
                      "&productCoverageCode=" + getObjectValue("productCoverageCode") +
                      "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate") +
                      "&practiceStateCode=" + getObjectValue("practiceStateCode") +
                      "&coverageLimitCode=" + getObjectValue("coverageLimitCode") +
                      "&transactionLogId=" + getObjectValue("transactionLogId");
            setWindowLocation(url);
            break;
        case 'SHOWTRANS':
            var url = "maintainExcessPremium.do?" + commonGetMenuQueryString() + "&process=loadAllExcessPremium" +
                      "&fromCoverage=" + getObjectValue("fromCoverage") +
                      "&productCoverageCode=" + getObjectValue("productCoverageCode") +
                      "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate") +
                      "&practiceStateCode=" + getObjectValue("practiceStateCode") +
                      "&coverageLimitCode=" + getObjectValue("coverageLimitCode") +
                      "&transactionLogId=" + getObjectValue("transactionLogId");
            setWindowLocation(url);
            break;
    }
}

//-----------------------------------------------------------------------------
// The logics in this function is to enable applicable fields and adjust grid's layout.
//-----------------------------------------------------------------------------
function userReadyStateReady() {
    // In below condition, the whole grid is readonly and return without do anything
    // 1.Current screen is opened from the View Transaction screen.
    // 2.Current screen is opened from the Coverage Information but in "VIEW_POLICY", or "VIEW_ENDQUOTE" view mode.
    // 3.The Summary option is selected.
    var readOnly = false;
    var fromCoverage = getObjectValue("fromCoverage");
    var showSummary = getObjectValue("showSummary");
    var policyViewMode = getObjectValue("policyViewMode");
    if (fromCoverage == "N" || showSummary == "N" || policyViewMode != "WIP") {
        readOnly = true;
    }

    var oTable = getSingleObject("excessPremiumGrid");
    var rowCount = oTable.rows.length;

    // Get delta amount for each amount column
    var deltaArray = new Array();
    first(excessPremiumGrid1);
    while (!excessPremiumGrid1.recordset.eof) {
        var rowType = excessPremiumGrid1.recordset("CROWTYPE").value;
        if (rowType == "DELTA") {
            deltaArray[1] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT1").value);
            deltaArray[2] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT2").value);
            deltaArray[3] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT3").value);
            deltaArray[4] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT4").value);
            deltaArray[5] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT5").value);
            break;
        }
        next(excessPremiumGrid1);
    }

    first(excessPremiumGrid1);
    for (var i = 1; i < rowCount; i++) {
        // Check if current row is readonly
        // A row is read-only if one of the following conditions is met:
        // The value of row_type is not 'LAYER'.
        var curRowType = excessPremiumGrid1.recordset("CROWTYPE").value;
        var actionType = excessPremiumGrid1.recordset("CACTIONTYPE").value;
        var rowCells = getCellsInRow(oTable.rows[i]);
        for (var j = 0; j < rowCells.length; j++) {
            rowCells[j].childNodes[0].style.width = "100%";
        }
        if (curRowType != "LAYER" || actionType == "AUTO") {
            for (var j = 1; j < rowCells.length - 1; j++) {
                rowCells[j].innerHTML = " <div id='CLAYERAMOUNT" + j + "' datafld='CLAYERAMOUNT" + j + "' STYLE='width:100%' ></div>";
            }
            if (curRowType == "TOTAL" || curRowType == "DELTA" || curRowType == "DIFF") {
                rowCells[0].childNodes[0].style.fontWeight = "bold";
            }

            next(excessPremiumGrid1);
            continue;
        }
        // Check layerAmount1~layerAmount5 to see if they need to be enable
        // Amount field 'amt<n>' is read-only if one of the following conditions is met:
        // 1. The delta amount of the column is zero.
        // 2. The nth character of unprotect_by_state in the selected row is not 'Y' and code_seq is greater than
        // code_seq_prim_covg.
        var codeSeq = excessPremiumGrid1.recordset("CCODESEQ").value;
        var codeSeqPrimCovg = excessPremiumGrid1.recordset("CCODESEQPRIMCOVG").value;
        var state = excessPremiumGrid1.recordset("CUNPROTECTBYSTATE").value;
        for (var j = 1; j < rowCells.length - 1; j++) {
            var curState = state.substring(j - 1, j);
            if (readOnly || deltaArray[j] == 0 || (curState != 'Y' && parseInt(codeSeq) > parseInt(codeSeqPrimCovg))) {
                rowCells[j].innerHTML = " <div id='CLAYERAMOUNT" + j + "' datafld='CLAYERAMOUNT" + j + "' STYLE='width:100%' ></div>";
            }
        }
        next(excessPremiumGrid1);
    }

    // Select first row in grid
    first(excessPremiumGrid1);
    selectFirstRowInGrid("excessPremiumGrid");
}

//-----------------------------------------------------------------------------
// Override and disable executing this method
//-----------------------------------------------------------------------------
function commonEnableDisableGridDetailFields() {

}

function rowchange(obj) {
    var prefix = obj.name.substring(0, obj.name.length - 1);
    if (prefix == "txtCLAYERAMOUNT") {
        if (!isNumber(obj)) {
            obj.value = "";
            window.event.returnValue = false;
        }
    }

    // Re-calculate the total value of this row
    var total = 0;
    var amtArray = new Array();
    amtArray[1] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT1").value);
    amtArray[2] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT2").value);
    amtArray[3] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT3").value);
    amtArray[4] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT4").value);
    amtArray[5] = unformatMoneyStrValAsStr(excessPremiumGrid1.recordset("CLAYERAMOUNT5").value);
    for (var i = 1; i <= 5; i++) {
        if (!isEmpty(amtArray[i])) {
            total += parseFloat(amtArray[i]);
        }
    }
    excessPremiumGrid1.recordset("CROWTOTAL").value = formatMoneyStrValAsStr(total);
    return true;
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllExcessPremium";
            break;
        case 'REFRESH':
            document.forms[0].process.value = "refresh";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Override this method since it does not work for editable grid
//-----------------------------------------------------------------------------
function commonOnChange(field) {
    try {
        var dataSrc = getDataSrc(field);
        var dataFld = getDataField(field);
        dataSrc = dataSrc.substring(1);

        var dataGrid = eval(dataSrc);

        if (dataGrid.recordset("UPDATE_IND").value == "N")
            dataGrid.recordset("UPDATE_IND").value = "Y";

        setTableProperty(dataGrid, "gridDataChange", true);
        if (window.postOnChange) {
            postOnChange(field);
        }

    }
    catch(ex) {
        // handle case where the field is not part of the grid and has no dataSrc
        var isExcluded = false;
        var functionExists = eval("window.excludeFieldsForSettingUpdateInd");
        if (functionExists) {
            var excludedFields = excludeFieldsForSettingUpdateInd();
            for (var i = 0; i < excludedFields.length; i++) {
                if (field.name == excludedFields[i]) {
                    isExcluded = true;
                    break;
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Override this method to enable unformat currency data
//-----------------------------------------------------------------------------
function getChanges(ReferenceXML)
{
    var modXML = ReferenceXML.documentElement.selectNodes("//ROW");
    var nodelen = modXML.length;
    var i;
    var j;
    var rowNode;
    var columnNode;
    var numColumnNodes;
    var result;
    var ID;
    var displayInd;
    var displayRows = "";
    var nonDisplayRows = "";

    for (i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        ID = rowNode.getAttribute("id");

        // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";

            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes() == true) {
                numColumnNodes = rowNode.childNodes.length;
                for (j = 0; j < numColumnNodes; j++) {
                    columnNode = rowNode.childNodes.item(j);
                    var nodeValue = encodeXMLChar(columnNode.text);
                    if(moneyFormatPattern.test(nodeValue)){
                        nodeValue = unformatMoneyStrValAsStr(nodeValue);
                    }
                    else if (percentagePattern.test(nodeValue)) {
                        nodeValue = convertPctToNumber(nodeValue);
                    }
                    result += "<" + columnNode.nodeName + ">" + nodeValue + "</" + columnNode.nodeName + ">";

                    if (columnNode.nodeName == "DISPLAY_IND")
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
