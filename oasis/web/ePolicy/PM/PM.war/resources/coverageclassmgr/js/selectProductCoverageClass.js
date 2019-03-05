//-----------------------------------------------------------------------------
// Java script file for selectProductCoverageClass.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   April 29, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/29/2011       syang       120316 - Modified handleOnButtonClick() to call addCoverageClassDone() to
//                                       select the first added coverage class.
// 07/20/2011       syang       121208 - Grouping coverage class.
// 07/28/2011       syang       121208 - Move the methods removeValueFromArray() and isArrayContains() to common.js. 
// 04/26/2013       awu         141758 - Modified handleOnButtonClick() to call addAllCoverageClass();
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
// 11/30/2018       xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------
// covgPkArray: an array to stored all selected header coverage (coverage type code is ***)
var covgPkArray = new Array();
// covgDelPkArray: an array to stored all de-selected header coverage (coverage type code is ***)
var covgDelPkArray = new Array();
// covgParentArray: an array to stored all selected header coverage's coverage parent value
var covgParentArray = new Array();
// checkedCovgIds: an array to stored all selected coverage Ids
var checkedCovgIds = new Array();
function handleOnButtonClick(asBtn) {

    switch (asBtn) {
        case 'DONE':
            showProcessingDivPopup();
            var selectId = selectCoverageClassGrid1.recordset("ID").value;
            var count = 0;
            var firstCoverageClassId = 0;
            var parent = getParentWindow();
            var oCoverageClassList = new Array();
            first(selectCoverageClassGrid1);
            while (!selectCoverageClassGrid1.recordset.eof) {
                var prodCoverageClassCode = selectCoverageClassGrid1.recordset("CPRODUCTCOVERAGECLASSCODE").value;
                // Eliminate the group row.
                if (selectCoverageClassGrid1.recordset("CSELECT_IND").value == -1 && "***" != prodCoverageClassCode) {
                    var oCoverageClass = getObjectFromRecordset(selectCoverageClassGrid1);
                    oCoverageClassList[count] = oCoverageClass;
                    //getParentWindow().addOneCoverageClass(oCoverageClass, true);
                    count++;
                }
                next(selectCoverageClassGrid1);
            }
            if (count == 0) {
                handleError(getMessage("pm.addCoverageClass.noselection.error"));
                // Select the current selected row.
                selectRowById("selectCoverageClassGrid", selectId);
                closeProcessingDivPopup();
            }
            else {
                parent.addAllCoverageClass(oCoverageClassList, divPopup);
            }
            break;
        case 'CANCEL':
            closeThisDivPopup();
            break;
    }
}

function selectCoverageClassForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

//-----------------------------------------------------------------------------
// Filter coverage data and disable select all checkbox while system groups coverage.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (!isEmptyRecordset(selectCoverageClassGrid1.recordset) && selectCoverageClassGrid1.recordset.Fields.count > 1) {
        filterCoverageData(null);
    }
    // Handle coverage class detail
    if (getObjectValue("coverageClassDetailDisplay") != 'Y') {
        hideShowElementByClassName(getObject("selectCoverageClassDetail"), true);
    }
}
//-----------------------------------------------------------------------------
// To filter the coverage data with the conditions
//-----------------------------------------------------------------------------
function filterCoverageData(condition) {
    var filterCondition = "CCOVERAGEGROUP!='' or (CCOVERAGEGROUP='' and CCOVERAGEGROUPCODE='')";
    if (condition) {
        filterCondition += condition;
    }
    selectCoverageClassGrid_filter(filterCondition);
}

//-----------------------------------------------------------------------------
// To handle on-click event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    // Check all coverages when checking the group row.
    var prodCoverageCode = selectCoverageClassGrid1.recordset("CPRODUCTCOVERAGECLASSCODE").value;
    var coverageGroupCode = selectCoverageClassGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var isSelected = selectCoverageClassGrid1.recordset("CSELECT_IND").value == -1;
    if (prodCoverageCode == "***") {
        var expandCollapse = selectCoverageClassGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
        if (expandCollapse == "-") {
            first(selectCoverageClassGrid1);
            while (!selectCoverageClassGrid1.recordset.eof) {
                checkAllCovgs(selectCoverageClassGrid1, coverageGroupCode, isSelected);
                next(selectCoverageClassGrid1);
            }
            first(selectCoverageClassGrid1);
        }
    }
    else {
        checkAllCovgs(selectCoverageClassGrid1, coverageGroupCode, isSelected);
        // System should un-check the group row if the subordinate coverage is unchecked.
        if (!isSelected) {
            first(selectCoverageClassGrid1);
            while (!selectCoverageClassGrid1.recordset.eof) {
                var tempCoverageGroupCode = selectCoverageClassGrid1.recordset("CCOVERAGEGROUPCODE").value;
                var tempProdcoverageCode = selectCoverageClassGrid1.recordset("CPRODUCTCOVERAGECLASSCODE").value;
                if (coverageGroupCode == tempCoverageGroupCode && tempProdcoverageCode == "***") {
                    selectCoverageClassGrid1.recordset("CSELECT_IND").value = "0";
                    break;
                }
                next(selectCoverageClassGrid1);
            }
            first(selectCoverageClassGrid1);
        }
    }
}

//-----------------------------------------------------------------------------
// To save all checked coverage ids into checkedCovgIds
//-----------------------------------------------------------------------------
function checkAllCovgs(selectCoverageClassGrid1, coverageGroupCode, isSelected) {
    var curProdCoverageCode = selectCoverageClassGrid1.recordset("CPRODUCTCOVERAGECLASSCODE").value;
    var curCoverageGroupCode = selectCoverageClassGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var id = selectCoverageClassGrid1.recordset("ID").value;
    var pkSize = checkedCovgIds.length;
    if (curProdCoverageCode != "***" && coverageGroupCode == curCoverageGroupCode) {
        if (isSelected) {
            selectCoverageClassGrid1.recordset("CSELECT_IND").value = "-1";
            if (!isArrayContains(checkedCovgIds, id)) {
                checkedCovgIds[pkSize] = id;
            }
        }
        else {
            selectCoverageClassGrid1.recordset("CSELECT_IND").value = "0";
            checkedCovgIds = removeValueFromArray(checkedCovgIds, id);
        }
    }
}
//-----------------------------------------------------------------------------
// To display image in the grid
//-----------------------------------------------------------------------------
function userReadyStateReady(tbl) {
    if (isEmptyRecordset(selectCoverageClassGrid1.recordset)) {
        hideEmptyTable(tbl);
        hideGridDetailDiv("selectCoverageClassGrid");
    }
    else {
        showNonEmptyTable(tbl);
        showGridDetailDiv("selectCoverageClassGrid");
        // Select the first row.
        selectFirstRowInGrid(tbl.id);
        processPlusMinusImages("CGROUPEXPANDCOLLAPSE");
    }
}

//-----------------------------------------------------------------------------
// Expand or Collapse the coverage data
//-----------------------------------------------------------------------------
function groupExpandCollapse(pk) {
    // get the parent and expand/collapse indicator
    getRow(selectCoverageClassGrid1, pk);
    var parent = selectCoverageClassGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var expandCollapse = selectCoverageClassGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
    addCoveragePkAndParents(pk, parent, expandCollapse);

    // get componentParent filter conditions
    var parentCondition = " or CCOVERAGEGROUPCODE='";
    var compSize = covgParentArray.length;
    for (var i = 0; i < compSize; i++) {
        parentCondition += covgParentArray[i] + "' or CCOVERAGEGROUPCODE='";
    }
    parentCondition = parentCondition.substring(0, parentCondition.length - 24);
    // set table properties before filtering for selecting the row by ID later
    setTableProperty(selectCoverageClassGrid, "isUserReadyStateReadyComplete", false);
    setTableProperty(selectCoverageClassGrid, "selectedTableRowNo", null);
    filterCoverageData(parentCondition);

    // get all selected pks from compPkArray and set from + to -
    var pkSize = covgPkArray.length;
    for (var i = 0; i < pkSize; i++) {
        var id = covgPkArray[i];
        getRow(selectCoverageClassGrid1, id);
        selectCoverageClassGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "-";
    }

    // get all selected pks from compDelPkArray and set from - to +
    var delPkSize = covgDelPkArray.length;
    for (var i = 0; i < delPkSize; i++) {
        var id = covgDelPkArray[i];
        getRow(selectCoverageClassGrid1, id);
        selectCoverageClassGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "+";
    }

    // get all checked component ids and make their select box to be checked
    var checkedIdSize = checkedCovgIds.length;
    for (var i = 0; i < checkedIdSize; i++) {
        var id = checkedCovgIds[i];
        getRow(selectCoverageClassGrid1, id);
        // check the selectbox if it's not filtered out
        if (selectCoverageClassGrid1.recordset.EOF)
            first(selectCoverageClassGrid1);
        else {
            var coverageGroup = selectCoverageClassGrid1.recordset("CCOVERAGEGROUPCODE").value;
            if (parent != coverageGroup || expandCollapse == "+" && parent == coverageGroup) {
                selectCoverageClassGrid1.recordset("CSELECT_IND").value = "-1";
            }
        }
    }
}
//-----------------------------------------------------------------------------
// To add/remove selected pk and coverageGroup value to/from arrays
//-----------------------------------------------------------------------------
function addCoveragePkAndParents(pk, parent, expandCollapse) {
    if (!pk || !parent) return;
    var isExpand = (expandCollapse == "+");

    // handle covgPkArray
    var pkSize = covgPkArray.length;
    var isPkAdded = isArrayContains(covgPkArray, pk);
    if (isExpand && !isPkAdded) {
        covgPkArray[pkSize] = pk;
    }
    else if (!isExpand && isPkAdded) {
        covgPkArray = removeValueFromArray(covgPkArray, pk);
    }

    // handle covgDelPkArray
    var delPkSize = covgDelPkArray.length;
    isPkAdded = isArrayContains(covgDelPkArray, pk);
    if (!isExpand && !isPkAdded) {
        covgDelPkArray[delPkSize] = pk;
    }
    else if (isExpand && isPkAdded) {
        covgDelPkArray = removeValueFromArray(covgDelPkArray, pk);
    }

    // handle covgParentArray
    var covgSize = covgParentArray.length;
    var isCovgParentAdded = isArrayContains(covgParentArray, parent);
    if (isExpand && !isCovgParentAdded) {
        covgParentArray[covgSize] = parent;
    }
    else if (!isExpand && isCovgParentAdded) {
        covgParentArray = removeValueFromArray(covgParentArray, parent);
    }
}