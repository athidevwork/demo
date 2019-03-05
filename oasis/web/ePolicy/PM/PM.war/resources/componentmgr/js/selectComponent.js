//-----------------------------------------------------------------------------
// Javascript file for selectComponent.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   July 27, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/28/2011       syang       122796 - Modify userRowchange() to move to the first position of grid after loop.
// 07/28/2011       syang       121208 - Move the methods removeValueFromArray() and isArrayContains() to common.js.
// 04/26/2013       awu         141758 - Modified selectComponentDone() to call addAllComponent when on coverage page.
// 07/19/2013       awu         146413 - 1. Modified handleOnButtonClick to call selectComponentDone directly;
//                                       2. Modified selectComponentDone to get data from XML instead of RecordSet.
// 11/22/2013       adeng       149224 - Modified selectComponentDone() to check if has the object componentOwner before
//                                       getting it's value.
// 03/20/2017       eyin        180675 - Modified to invoke call-back function with the proper parent window for UI change.
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
// 06/11/2018       cesar       193651 - Modified handleOnButtonClick() to use getParentWindow().
//-----------------------------------------------------------------------------
// compPkArray: an array to stored all selected header component (component type code is ***)
var compPkArray = new Array();
// compDelPkArray: an array to stored all de-selected header component (component type code is ***)
var compDelPkArray = new Array();
// compParentArray: an array to stored all selected header component's component parent value
var compParentArray = new Array();
// checkedCompIds: an array to stored all selected component Ids
var checkedCompIds = new Array();
var filterCondition;
var curRowId = "";

function handleOnButtonClick(asBtn) {
    var parentWindow = getParentWindow();
    var divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);

    var oCoverageList = new Array();
    switch (asBtn) {
        case 'DONE':
            var checkedIdSize = checkedCompIds.length;
            if (checkedIdSize == 0) {
                handleError(getMessage("pm.addComponent.noSelection.error"));
                break;
            }
            showProcessingDivPopup();
            selectComponentDone();
            break;
        case 'CANCEL':
            if (divPopup) {
                parentWindow.closeDiv(divPopup);
            } else {
                closeWindow();
            }
            break;
    }
}

//-----------------------------------------------------------------------------
// Expand or Collapse the component data
//-----------------------------------------------------------------------------
function groupExpandCollapse(pk) {
    var parent;
    var expandCollapse;

    // get the parent and expand/collapse indicator
    getRow(selectComponentGrid1, pk);
    parent = selectComponentGrid1.recordset("CCOMPONENTPARENT").value;
    expandCollapse = selectComponentGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
    addComponentPkAndParents(pk, parent, expandCollapse);

    // get componentParent filter conditions
    var parentCondition = " or CCOMPONENTPARENT='";
    var compSize = compParentArray.length;
    for (var i = 0; i < compSize; i++) {
        parentCondition += compParentArray[i] + "' or CCOMPONENTPARENT='";
    }
    parentCondition = parentCondition.substring(0, parentCondition.length - 22);

    // set table properties before filtering for selecting the row by ID later
    setTableProperty(selectComponentGrid, "isUserReadyStateReadyComplete", false);
    setTableProperty(selectComponentGrid, "selectedTableRowNo", null);
    filterComponentData(parentCondition);

    // get all selected pks from compPkArray and set from + to -
    var pkSize = compPkArray.length;
    for (var i = 0; i < pkSize; i++) {
        var id = compPkArray[i];
        getRow(selectComponentGrid1, id);
        selectComponentGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "-";
    }

    // get all selected pks from compDelPkArray and set from - to +
    var delPkSize = compDelPkArray.length;
    for (var i = 0; i < delPkSize; i++) {
        var id = compDelPkArray[i];
        getRow(selectComponentGrid1, id);
        selectComponentGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "+";
    }

    // get all checked component ids and make their selectbox to be checked
    var checkedIdSize = checkedCompIds.length;
    for (var i = 0; i < checkedIdSize; i++) {
        var id = checkedCompIds[i];
        getRow(selectComponentGrid1, id);
        // check the selectbox if it's not filtered out
        if (selectComponentGrid1.recordset.EOF)
            first(selectComponentGrid1);
        else {
            var componentParent = selectComponentGrid1.recordset("CCOMPONENTPARENT").value;
            //alert(expandCollapse+"|"+parent+"="+componentParent+"|"+id);
            if (parent != componentParent || expandCollapse == "+" && parent == componentParent) {
                selectComponentGrid1.recordset("CSELECT_IND").value = -1;
            }
        }
    }

    curRowId = pk;
}

function postGroupExpandCollapse(pk) {
    selectRowById("selectComponentGrid", pk);
}

//-----------------------------------------------------------------------------
// To add/remove selected pk and componentParent value to/from arrays
//-----------------------------------------------------------------------------
function addComponentPkAndParents(pk, parent, expandCollapse) {
    if (!pk || !parent) return;
    var isExpand = (expandCollapse == "+");

    // handle compPkArray
    var pkSize = compPkArray.length;
    var isPkAdded = isArrayContains(compPkArray, pk);
    if (isExpand && !isPkAdded) {
        compPkArray[pkSize] = pk;
    }
    else if (!isExpand && isPkAdded) {
        compPkArray = removeValueFromArray(compPkArray, pk);
    }

    // handle compDelPkArray
    var delPkSize = compDelPkArray.length;
    isPkAdded = isArrayContains(compDelPkArray, pk);
    if (!isExpand && !isPkAdded) {
        compDelPkArray[delPkSize] = pk;
    }
    else if (isExpand && isPkAdded) {
        compDelPkArray = removeValueFromArray(compDelPkArray, pk);
    }

    // handle compParentArray
    var compSize = compParentArray.length;
    var isCompParentAdded = isArrayContains(compParentArray, parent);
    if (isExpand && !isCompParentAdded) {
        compParentArray[compSize] = parent;
    }
    else if (!isExpand && isCompParentAdded) {
        compParentArray = removeValueFromArray(compParentArray, parent);
    }
}

function handleOnLoad() {
    if (selectComponentGrid1.recordset.Fields.count > 1) {
        filterComponentData(null);
    }
    // disable selectAll checkbox
    if (hasObject("HCSELECT_IND")) {
        getObject("HCSELECT_IND").disabled = true;
    }
}

//-----------------------------------------------------------------------------
// To filter the component data with the conditions
//-----------------------------------------------------------------------------
function filterComponentData(condition) {
    filterCondition = "CCOMPONENTGROUP!='' or (CCOMPONENTGROUP='' and CCOMPONENTPARENT='')";
    if (condition) {
        filterCondition += condition;
    }
    selectComponentGrid_filter(filterCondition);
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    // check all components if check the header row
    var compTypeCode = selectComponentGrid1.recordset("CCOMPONENTTYPECODE").value;
    var componentParent = selectComponentGrid1.recordset("CCOMPONENTPARENT").value;
    var isSelected = selectComponentGrid1.recordset("CSELECT_IND").value == -1;
    if (compTypeCode == "***") {
        var expandCollapse = selectComponentGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
        if (expandCollapse == "-") {
            first(selectComponentGrid1);
            while (!selectComponentGrid1.recordset.eof) {
                checkAllComps(selectComponentGrid1, componentParent, isSelected);
                next(selectComponentGrid1);
            }
            first(selectComponentGrid1);
        }
    }
    else {
        checkAllComps(selectComponentGrid1, componentParent, isSelected);
    }
}

//-----------------------------------------------------------------------------
// To save all checked component ids into checkedCompIds
//-----------------------------------------------------------------------------
function checkAllComps(selectComponentGrid1, componentParent, isSelected) {
    var curcompTypeCode = selectComponentGrid1.recordset("CCOMPONENTTYPECODE").value;
    var curCompParent = selectComponentGrid1.recordset("CCOMPONENTPARENT").value;
    var id = selectComponentGrid1.recordset("ID").value;
    var pkSize = checkedCompIds.length;
    if (curcompTypeCode != "***" && componentParent == curCompParent) {
        if (isSelected) {
            selectComponentGrid1.recordset("CSELECT_IND").value = -1;
            if (!isArrayContains(checkedCompIds, id)) {
                checkedCompIds[pkSize] = id;
                pkSize = checkedCompIds.length;
            }
        }
        else {
            selectComponentGrid1.recordset("CSELECT_IND").value = 0;
            checkedCompIds = removeValueFromArray(checkedCompIds, id);
        }
    }
}

//-----------------------------------------------------------------------------
// To display image in the grid
//-----------------------------------------------------------------------------
function userReadyStateReady(tbl) {

    processPlusMinusImages("CGROUPEXPANDCOLLAPSE");

    if (!isEmpty(curRowId)) {
        selectFirstRowInGrid(tbl.id)
        postGroupExpandCollapse(curRowId);
        curRowId = "";
    }
}

//-----------------------------------------------------------------------------
// Copy data from selected row and pass to parent page
//-----------------------------------------------------------------------------
function selectComponentDone() {
    var count = 0;
    var oComps = new Array();
    var componentOwner = oParentWindow.hasObject("componentOwner")?
            oParentWindow.getObjectValue("componentOwner"):"";
    for (var i = 0; i < checkedCompIds.length; i++) {
        var id = checkedCompIds[i];
        var originalXML = getOrigXMLData(getXMLDataForGridName("selectComponentGrid"));
        var originalNode = originalXML.documentElement.selectSingleNode("//ROW[@id='" + id + "']");
        var oComponent = new Object();
        for (var j = 0; j < originalNode.childNodes.length; j++) {
            var colName = originalNode.childNodes.item(j).tagName;
            colName = colName.substring(1);
            var dataValue = originalNode.childNodes.item(j).text;
            eval("oComponent." + colName + " = dataValue");
        }
        oComponent.PRODUCTCOVCOMPONENTID = id;
        oComps[count] = oComponent;
        count++;
    }
    if (count == 0) {
        handleError(getMessage("pm.addComponent.noSelection.error"));
        closeProcessingDivPopup();
    }
    else {
        if (componentOwner == "COVERAGE") {
            oParentWindow.addAllComponent(oComps, divPopup);
        }
        else {
            var result = oParentWindow.addComponents(oComps, true);
            hideShowElementByClassName(oParentWindow.getSingleObject("componentDetailDiv"), false);
            closeProcessingDivPopup();
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
        }
    }
}