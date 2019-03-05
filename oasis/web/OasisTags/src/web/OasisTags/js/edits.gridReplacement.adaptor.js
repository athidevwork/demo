/**
 * Created by kshen on 4/14/2016.
 */
/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 09/21/2018        dpang      195835 - Changed jquery selector used to find detail form elements.
 10/26/2018        cesar      196687 - Added getObject(). this will be called when jqxGrid is enabled.
 ----------------------------------------------------------------------------
 */

function getDataSrc(field) {
    return $(field).data("dtiDatasrc");
}

function getDataField(field) {
    return $(field).data("dtiDatafld");
}

// Get all input form fields including some invisible fields caused by field dependency and page entitlements
function getAllFormFieldsForGrid(gridId) {
    var table = getTableForGrid(gridId);
    var divId = getTableProperty(table, "gridDetailDivId");
    var inputFields = getTableProperty(table, "allFormFieldsForGrid");
    if (!inputFields) {
        if (dti.oasis.string.isEmpty(divId)){
            inputFields = $(":input[data-dti-datasrc='#" + gridId + "1']");
        }else{
            var gridDetailDiv = $("#" + divId);
            inputFields = $(":input", gridDetailDiv);
        }
        setTableProperty(table, "allFormFieldsForGrid", inputFields);
    }
    return inputFields;
}

function selectAnotherRowInBoundGrid(field){
    // This function is removed to the keydown.dti.oasis.grid event of a grid.
}

function selectRowWithProcessingDlg(gridId, rowId) {
    dti.oasis.grid.selectRowWithProcessingDlg(gridId, rowId);
}

function selectRow(gridId, rowId) {
    dti.oasis.grid.selectRowById(gridId, rowId);
}

/**
 * 1. Changed use dti.oasis.page.getProperty("gridIds") to get grid names.
 * 2. Changed the selector for getting detail fields.
 */
function baseEnableDisableGridDetailFields(stageCode, gridId) {
    var beginTime = new Date();

    // This needs to be executed for all grids because the page entitlement (or other app level logic) could executes
    // for all grid form fields and will make a non-editable row fields as editable.

    // Eg. Coverage-Component Scenario

    // When a row in Coverage grid is selected, it automatically selects the 1st row in the Component grid.

    // If we enforce the following logic only for the grid id passed to this function, then all fields for Coverage grid
    // will be disabled based on EDIT_IND successfully. However, since the process is going to select the 1st row in
    // the Component grid automatically, then this function will be executed for the Component grid, which fires
    // page entitlements. The page entitlement could potentially enable fields associated with the Coverage grid,
    // which is in-correct because the selected coverage row is non-editable.
    var gridIds = dti.oasis.page.getProperty("gridIds");

    for (var tbl=0; tbl<gridIds.length; tbl++) {
        var dataGrid = getXMLDataForGridName(gridIds[tbl]);
        var gridTbl = getTableForXMLData(dataGrid);

        if (stageCode == "PRE" && gridId != gridTbl.id) {

            // The PRE stage is used to look for all fields that are currently hidden or readOnly, but was visible and
            // editable during initial page load and revert them back to the page load stage - ie. make them as visible
            // and editable again, so that the OBR or page entitlement or the page specific logic can determine
            // what to do for the currently selected row.

            // This must be done only for the current grid because the OBR, page entitlement and the page specific logic
            // will be executed again for the current grid only.
            //
            // This should not be done for all grids because the other grid's OBR, page entitlement and the page
            // specific logic may have already executed and some of the field's visibility/editable attribute might
            // have changed as per business requirement. Since the OBR, page entitlement and the page specific logic are
            // not going to be called again for other grids, if we revert the grid field's attribute to the initial page
            // load state, then we inadvertently remove the already enforced business requirement on those fields.

            continue;
        }

        if (!dti.oasis.grid.hasRows(gridIds[tbl])) {
            continue;
        }

        if (dataGrid.recordset.EOF || dataGrid.recordset.BOF) {
            continue;
        }

        functionExists = eval("window.commonEnableDisableGridDetailFields");
        if (functionExists) {
            commonEnableDisableGridDetailFields(dataGrid, getTableProperty(gridTbl, "gridDetailDivId"), getTableProperty(gridTbl, "selectedRowId"));
        } else {
            var detailFormElements = null;
            var currEditInd = dataGrid.recordset('EDIT_IND').value.toUpperCase();
            var isDisabled = (currEditInd == 'N');
            if (isDisabled && stageCode == 'POST') {
                // The row is non-editable. Get all enabled fields associated with the grid and make them read-only them.
                // Using JQuery, find all "editable" input fields (irrespective of input, select etc tags) which has
                // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.
                detailFormElements = $("td[isEditable='Y'] :input[data-dti-datasrc='#" + dataGrid.id + "']");
            } else if(!isDisabled && stageCode == 'PRE') {
                // The row is an editable. Get all readonly fields associated with the grid and make them editable.
                // Using JQuery, find all "non-editable" input fields (irrespective of input, select etc tags) which has
                // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.

                //Look for all hidden fields that are visible & editable when the page was loaded initially and make them editable.
                detailFormElements = $("td[isEditableWhenVisible][isEditableOnInitialPageLoad='Y'] :input[data-dti-datasrc='#" + dataGrid.id + "']");
                for (var i=0; i<detailFormElements.length; i++) {
                    /*
                     Radio Option and Checkbox HTML elements are rendered within a table of its own.
                     So, locate the correct <TD> that holds the HTML elements to set "isEditableWhenVisible" attribute value to "Y".
                     */
                    var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
                    for (var j = 0; j < suffixArray.length; j++) {
                        var containerId = detailFormElements[i].name + suffixArray[j];
                        if (hasObject(containerId)) {
                            var container = $("#" + containerId);
                            container.attr("isEditableWhenVisible", "Y");
                        }
                    }
                    //Show the field as editable.
                    hideShowField(detailFormElements[i], false);
                }

                //Look for all read-only fields and make them editable.
                detailFormElements = $("td[isEditable='N'][isEditableOnInitialPageLoad='Y'] :input[data-dti-datasrc='#" + dataGrid.id + "']");
            }
            if (detailFormElements) {
                for (var i = 0; i < detailFormElements.length; i++) {
                    enableDisableField(detailFormElements[i], isDisabled);
                }
            }
        }
    }

    var endTime = new Date();
    logDebug("Time spent in baseOnRowSelected:" + (endTime.getTime() - beginTime.getTime()) + "ms");
    return true;
}


function isPageGridsDataChanged() {
    return dti.oasis.page.isPageGridsDataChanged();
}

function isGridDataChanged(gridId) {
    return dti.oasis.grid.isGridDataChanged(gridId);
}

function hideShowElementByClassName(element, isHidden, visibleDisplayStyle) {
    if (isHidden) {
        $(element).addClass("dti-hide");
    } else {
        $(element).removeClass("dti-hide");
    }
}

/**
 * get the element object reference
 * @param objid     id and name are supported
 */
function getObject(objid, returnSingleObject) {
    var foundObject = null;
    if (typeof objid == "string") {
        var result = getObjectById(objid);

        if(result == null || result.length == 0) {
            result = getObjectsByName(objid);
            if (result == null || result.length == 0) {
                if (window[objid] && window[objid].isOasisDataIsland) {
                    result = window[objid];
                }
                if (result == null || result.length == 0) {
                    return undefined;
                }
            }
        }
        if (result.length == 1 || (returnSingleObject && result.length>1)) {
            foundObject = result[0];
        } else {
            foundObject = result;
        }
    } else {
        foundObject = objid;
    }
    return foundObject;
}