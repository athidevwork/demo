//-----------------------------------------------------------------------------
// Javascript file for selectProductCoverage.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Sep 17, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/17/2010       syang       Issue 111769 - System shouldn't add default components if no any coverage was added.
// 07/18/2011       syang       121208 - Grouping coverage.
// 07/28/2011       syang       121208 - Move the methods removeValueFromArray() and isArrayContains() to common.js. 
// 09/07/2012       wfu         137134 - Modified handleOnButtonClick for selecting coverages to add. If a coverage
//                              group is clappsed, its members cannot be loaded in the recordset. So change logic to
//                              get records by coverage ids storing in the array of all selected coverages.
// 04/24/2013       awu         141758 - Modified handleOnButtonClick of DONE button for performance issue.
//05/22/2013        adeng       144494 - Modified selectCoverageForm_btnClick,when user click on select all,not only
//                              need update select Index but also need put coverage id into the array.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
// 06/11/2018       cesar       193651 - Modified handleOnButtonClick() to use getParentWindow().
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
    var parentWindow = getParentWindow();
    switch (asBtn) {
        case 'DONE':
            if (checkedCovgIds.length == 0) {
                alert(getMessage("pm.addCoverage.noselection.error"));
                return false;
            }
            var oCoverageList = new Array();
            var oCoverage = null;
            var id = null;
            for (var i = 0; i < checkedCovgIds.length; i++) {
                id = checkedCovgIds[i];
                if (selectCoverageGrid1.recordset.EOF) {
                    first(selectCoverageGrid1);
                }
                getRow(selectCoverageGrid1, id);
                oCoverage = getObjectFromRecordset(selectCoverageGrid1);
                oCoverageList[i] = oCoverage
            }
            closeThisDivPopup();
            parentWindow.addAllCoverage(oCoverageList);
            break;
        case 'CANCEL':
            closeThisDivPopup();
            break;
    }
}

function selectCoverageForm_btnClick(asBtn) {
    var isSelected;
    switch (asBtn) {
        case 'SELECT':
            isSelected=true;
            break;
        case 'DESELECT':
            isSelected=false;
            break;
    }
    if (!isEmptyRecordset(selectCoverageGrid1.recordset)) {
        first(selectCoverageGrid1);
        while (!selectCoverageGrid1.recordset.eof) {
            var prodCoverageCode = selectCoverageGrid1.recordset("CPRODUCTCOVERAGECODE").value;
            var coverageGroupCode = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
            checkAllCovgs(selectCoverageGrid1, coverageGroupCode, isSelected);
            next(selectCoverageGrid1);
        }
        first(selectCoverageGrid1);
    }
}

//-----------------------------------------------------------------------------
// Filter coverage data and disable select all checkbox while system groups coverage.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (!isEmptyRecordset(selectCoverageGrid1.recordset) && selectCoverageGrid1.recordset.Fields.count > 1 && hasObject("selectCoverageDetailDiv")) {
        filterCoverageData(null);
    }
    // Handle coverage detail
    if (getObjectValue("coverageDetailDisplay") != 'Y') {
        hideShowElementByClassName(getObject("selectCoverageDetail"), true);
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
    selectCoverageGrid_filter(filterCondition);
}

//-----------------------------------------------------------------------------
// To handle on-click event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    // Check all coverages when checking the group row.
    var prodCoverageCode = selectCoverageGrid1.recordset("CPRODUCTCOVERAGECODE").value;
    var coverageGroupCode = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var isSelected = selectCoverageGrid1.recordset("CSELECT_IND").value == "-1";
    if (prodCoverageCode == "***") {
        var expandCollapse = selectCoverageGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
        if (expandCollapse == "-") {
            first(selectCoverageGrid1);
            while (!selectCoverageGrid1.recordset.eof) {
                checkAllCovgs(selectCoverageGrid1, coverageGroupCode, isSelected);
                next(selectCoverageGrid1);
            }
            first(selectCoverageGrid1);
        }
    }
    else {
        checkAllCovgs(selectCoverageGrid1, coverageGroupCode, isSelected);
        // System should un-check the group row if the subordinate coverage is unchecked.
        if (!isSelected) {
            first(selectCoverageGrid1);
            while (!selectCoverageGrid1.recordset.eof) {
                var tempCoverageGroupCode = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
                var tempProdcoverageCode = selectCoverageGrid1.recordset("CPRODUCTCOVERAGECODE").value;
                if (coverageGroupCode == tempCoverageGroupCode && tempProdcoverageCode == "***") {
                    selectCoverageGrid1.recordset("CSELECT_IND").value = "0";
                    break;
                }
                next(selectCoverageGrid1);
            }
            first(selectCoverageGrid1);
        }
    }
}

//-----------------------------------------------------------------------------
// To save all checked coverage ids into checkedCovgIds
//-----------------------------------------------------------------------------
function checkAllCovgs(selectCoverageGrid1, coverageGroupCode, isSelected) {
    var curProdCoverageCode = selectCoverageGrid1.recordset("CPRODUCTCOVERAGECODE").value;
    var curCoverageGroupCode = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var id = selectCoverageGrid1.recordset("ID").value;
    var pkSize = checkedCovgIds.length;
    if (curProdCoverageCode != "***" && coverageGroupCode == curCoverageGroupCode) {
        if (isSelected) {
            selectCoverageGrid1.recordset("CSELECT_IND").value = "-1";
            if (!isArrayContains(checkedCovgIds, id)) {
                checkedCovgIds[pkSize] = id;
            }
        }
        else {
            selectCoverageGrid1.recordset("CSELECT_IND").value = "0";
            checkedCovgIds = removeValueFromArray(checkedCovgIds, id);
        }
    }
}
//-----------------------------------------------------------------------------
// To display image in the grid
//-----------------------------------------------------------------------------
function userReadyStateReady(tbl) {
    if (isEmptyRecordset(selectCoverageGrid1.recordset)) {
        hideEmptyTable(tbl);
        hideGridDetailDiv("selectCoverageGrid");
    }
    else {
        showNonEmptyTable(tbl);
        showGridDetailDiv("selectCoverageGrid");
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
    getRow(selectCoverageGrid1, pk);
    var parent = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
    var expandCollapse = selectCoverageGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
    addCoveragePkAndParents(pk, parent, expandCollapse);

    // get componentParent filter conditions
    var parentCondition = " or CCOVERAGEGROUPCODE='";
    var compSize = covgParentArray.length;
    for (var i = 0; i < compSize; i++) {
        parentCondition += covgParentArray[i] + "' or CCOVERAGEGROUPCODE='";
    }
    parentCondition = parentCondition.substring(0, parentCondition.length - 24);
    // set table properties before filtering for selecting the row by ID later
    setTableProperty(selectCoverageGrid, "isUserReadyStateReadyComplete", false);
    setTableProperty(selectCoverageGrid, "selectedTableRowNo", null);
    filterCoverageData(parentCondition);

    // get all selected pks from compPkArray and set from + to -
    var pkSize = covgPkArray.length;
    for (var i = 0; i < pkSize; i++) {
        var id = covgPkArray[i];
        getRow(selectCoverageGrid1, id);
        selectCoverageGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "-";
    }

    // get all selected pks from compDelPkArray and set from - to +
    var delPkSize = covgDelPkArray.length;
    for (var i = 0; i < delPkSize; i++) {
        var id = covgDelPkArray[i];
        getRow(selectCoverageGrid1, id);
        selectCoverageGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "+";
    }

    // get all checked component ids and make their select box to be checked
    var checkedIdSize = checkedCovgIds.length;
    for (var i = 0; i < checkedIdSize; i++) {
        var id = checkedCovgIds[i];
        getRow(selectCoverageGrid1, id);
        // check the selectbox if it's not filtered out
        if (selectCoverageGrid1.recordset.EOF)
            first(selectCoverageGrid1);
        else {
            var coverageGroup = selectCoverageGrid1.recordset("CCOVERAGEGROUPCODE").value;
            if (parent != coverageGroup || expandCollapse == "+" && parent == coverageGroup) {
                selectCoverageGrid1.recordset("CSELECT_IND").value = "-1";
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
