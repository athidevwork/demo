/*
    Return all columns, not just the changed columns as listed in the ROW.col attribute.
*/
//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/14/2010        tzhao      issue#109875 - Modified money format script to support multiple currency.
// 10/19/2010        gzeng      112909 - Set isScrolling to false for special pages.
// 02/21/2011        Kenney     Added getParentWindow
// 08/31/2011        Michael    Add commonOnRowSelected  userReadyStateReady   commonReadyStateReady
// 11/07/2011        Leo        Issue 126742
// 11/08/2011        bhong      112837 - Moved showProcessingDivPopup to divpopup.js
// 11/10/2011        parker     126614 - add definition for sorting.
// 01/05/2012        clm        126620 - add sorting property for table
// 09/13/2013        kshen      Issue 144341.
// 12/06/2013        Parker     Issue 148036 Refactor maintainRecordExists code to make one call per subsystem to the database.
// 09/13/2017        kshen      Grid replacement. Changed commonReadyStateReady to support jqxGrid.
// 12/12/2017        kshen      Grid replacement. Removed local variable sorting, and use getTableProperty to get {sorting} property to follow common pattern.
//                              Added missing variable definition userReadyStateReadyPass.
// 02/01/2018        dpang      191109: Added functions checkIfEnableDeleteButton and commonDeleteRecord.
// 04/16/2018        dpang      109216: Added table property selectRowInCommonReadyStateReady in function commonReadyStateReady.
// 05/07/2018        jld        193125: Add npi_no.
// 07/13/2018        cesar      194021 - Refactor commonAddRow(), postCommonAddRow(), setInitialUrlInGrid() into dti.oasis.grid
// 07/18/2018        cesar      194022 - Refactor commonDeleteRow() into dti.oasis.grid
// 07/31/2018        mlm        193962 - Refactored to promote getChanges() into framework.
// 07/31/2018        mlm        193967 - Refactored to promote logic from commonOnBeforeGotoPage, selectFirstRowInTable
//                                       and moveToFirstRowInTable into framework.
// 08/02/2018        jdingle    194134 - Update getTextForFirstElement.
// 08/01/2018        mlm        193968 - Refactored to promote setRecordsetByObject into framework as setCurrentRecordValues.
// 08/06/2018        ylu        Issue 194134: add window.eval to avoid Object expected error
// 09/07/2018        jdingle    195635 - Update commonDeleteRow to optionally prevent existing record deletion.
// 09/19/2018        dpang      195835 - In commonDeleteRow(),not only check if CSELECT_IND column is defined for grid but also check if it's visible.
// 09/19/2018        Elvin      Issue 195835: use getDataSrc() to get datasrc in commonOnChange
// 10/16/2018        Elvin      Issue 195835: update checkIfEnableDeleteButton function to support jqx grid
// 10/22/2018        jdingle    Issue 160238: handle multiple deletes.
// 11/05/2018        kshen      196632. Changed userReadyStateReady to call setRowStyle to match the function in PM.
//-----------------------------------------------------------------------------

var cisEntityFolderContactTabMenuItemID = "CI_CONTACT_MI";
var cisEntityFolderRelationshipTabMenuItemID = "CI_RELAT_MI";
var cisEntityFolderAuditTrailTabMenuItemID = "CI_AUDITRL_MI";
var cisEntityFolderWIPInquiryTabMenuItemID = "CI_WIPNQRY_MI";
var cisEntityFolderClaimsTabMenuItemID = "CI_CLAIMS_MI";
var cisEntityFolderPoliciesTabMenuItemID = "CI_POLICIES_MI";
var cisEntityFolderTrainingTabMenuItemID = "CI_TRAINING_MI";
var cisEntityFolderOrgGroupTabMenuItemID = "CI_ORGGROUP_MI";
var cisEntityFolderCertificationTabMenuItemID = "CI_CERTFD_MI";
var cisEntityFolderLicenseTabMenuItemID = "CI_LISEN_MI";
var cisEntityFolderDisabilityTabMenuItemID = "CI_DISB_MI";
var cisEntityFolderEducationTabMenuItemID = "CI_EDUC_MI";
var cisEntityFolderDenominatorTabMenuItemID = "CI_DENO_MI";
var cisEntityFolderVehicleTabMenuItemID = "CI_VEHICLE_MI";
var cisEntityFolderPropertyTabMenuItemID = "CI_PROP_MI";
var cisEntityFolderCorresTabMenuItemID = "CI_CORRES_MI";
var cisEntityFolderExpertWitnessTabMenuItmeID = "CI_EXPWIT_MI";
var cisEntityFolderPriorCarrierTabMenuItmeID = "CI_PRI_CARER_MI";
var cisEntityFolderAgentTabMenuItmeID = "CI_MNT_AGENT";
var cisEntityFolderSummaryTabMenuItemID = "CI_SUMMARY_MI";
var cisEntityFolderRiskManagementTabMenuItemID = "CI_RM_MI";
var cisEntityFolderBillingTabMenuItemID = "CI_BILLING_MI";
var cisEntityFolderSurveyTabMenuItemID = "CI_RM_SURVEY_MI";
var cisEntityAdditionalTabMenuItemID = "CI_ENT_ADDITIONAL";
var cisEntityHistoricalDataTabMenuItemID = "CI_ENT_HIST_DATA";
var cisEntityGlanceTabMenuItemID = "CI_ENTITY_GLANCE";
var cisEntityAddressTabMenuItemID = "CI_ENTADDRES_MI";
var cisEntityClientTabMenuItemID = "CI_ENTMODIFY_MI";
var cisEntityFolderClassificationTabMenuItemID = "CI_ENTCLASS_MI";
var cisEntityFolderRoleTabMenuItemID = "CI_ENTROLE_MI";
var cisEntityFolderPhoneTabMenuItemID = "CI_ENTPHONE_MI";
var cisEntityFolderVendorTabMenuItemID = "CI_ENTVENDOR_MI";
var cisEntityFolderVendorAddressTabMenuItemID = "CI_ENTVNDADR_MI";

var ciDataChangedConfirmation = getMessage("js.lose.changes.confirmation");
var ciRefreshPageConfirmation = getMessage("js.refresh.lose.changes.confirmation");


var currentSubmitAction;
var isForOose = "N";
var priorRowId = -1;
var postAjaxSubmitUrl;
var postAjaxSubmitCallbackFunction;
var userReadyStateReadyPass = 0;

//-----------------------------------------------------------------------------
// update the alternated grid
//-----------------------------------------------------------------------------
function alternateGrid_update(grid, filterValue) {
    // get coverageListGrid changes and set into coverageListGridtxtXML
    var modValue = '';
    var isFilterFlag = filterflag;
    var gridTbl = eval(grid);
    var gridXML = eval(grid + '1');
    var origGridXml = eval('orig' + grid + '1');
    var gridTxtXml = grid + 'txtXML' ;

    if (isMultiGridSupported) {
        isFilterFlag = getTableProperty(getTableForXMLData(gridXML), 'filterflag');
    }
    if (isFilterFlag) {
        syncChanges(origGridXml, gridXML, filterValue);
        modValue = getChanges(origGridXml);
    }
    else {
        modValue = getChanges(gridXML);
    }
    setInputFormField(gridTxtXml, modValue);
}

/**
 * Check if enable Delete button according to selected rows and if allow deleting existing records.
 */
function checkIfEnableDeleteButton(gridXmlData, deleteBtnId, allowDeleteExistRecord) {
    if (!hasObject(deleteBtnId)) {
        return;
    }

    var deleteBtn = getObject(deleteBtnId);
    if (isEmptyRecordset(gridXmlData.recordset)) {
        deleteBtn.disabled = true;
        return;
    }

    if (isColumnVisible(gridXmlData.id, "CSELECT_IND")) {
        var selectedRowSelector = "//ROW[CSELECT_IND='-1'";

        if (allowDeleteExistRecord) {
            var selectedRecord = gridXmlData.documentElement.selectNodes(selectedRowSelector + " and UPDATE_IND!='D']");
            deleteBtn.disabled = (selectedRecord.length == 0);
        } else {
            var selectedExistRecord = gridXmlData.documentElement.selectNodes(selectedRowSelector + " and (UPDATE_IND='N' or UPDATE_IND='Y')]");
            var selectedNewRecord = gridXmlData.documentElement.selectNodes(selectedRowSelector + " and UPDATE_IND='I']");

            deleteBtn.disabled = (selectedExistRecord.length > 0 || selectedNewRecord.length == 0);
        }
        return;
    }

    if (allowDeleteExistRecord) {
        deleteBtn.disabled = false;
    } else {
        var updateInd = gridXmlData.recordset("UPDATE_IND").value;
        deleteBtn.disabled = (updateInd != "I");
    }
}


//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
// This is a common function for entity folder "tab" pages.
//-----------------------------------------------------------------------------
function cisEntityFolderGetMenuQueryString(id, url)
{
    var tempUrl = '';
    var tempPKUrl = '';
    if (getObject("pk")) {
        tempPKUrl = "pk=" + getObject("pk").value;
    }
    if (getObject("entityType")) {
        tempPKUrl = tempPKUrl + "&entityType=" + getObject("entityType").value;
    }
    if (getObject("entityName")) {
        tempPKUrl = tempPKUrl + "&entityName=" + encodeURIComponent(getObject("entityName").value);
    }
    // contact tab
    if (id == cisEntityFolderContactTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // relationship tab
    else if (id == cisEntityFolderRelationshipTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // audit trail tab
    else if (id == cisEntityFolderAuditTrailTabMenuItemID) {
        tempUrl = tempPKUrl;
    }

    // WIP Inquiry tab
    else if (id == cisEntityFolderWIPInquiryTabMenuItemID) {
        tempUrl = tempPKUrl;
    }

    // Claims tab
    else if (id == cisEntityFolderClaimsTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    //Policies
    else if (id == cisEntityFolderPoliciesTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Training tab
    else if (id == cisEntityFolderTrainingTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderOrgGroupTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Certification tab
    else if (id == cisEntityFolderCertificationTabMenuItemID) {
        tempUrl = tempPKUrl;
    }

    // License tab
    else if (id == cisEntityFolderLicenseTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Disability tab
    else if (id == cisEntityFolderDisabilityTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Education Tab
    else if (id == cisEntityFolderEducationTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Denominator tab
    else if (id == cisEntityFolderDenominatorTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Vehicle tab
    else if (id == cisEntityFolderVehicleTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Property tab
    else if (id == cisEntityFolderPropertyTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Correspondence tab
    else if (id == cisEntityFolderCorresTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // Expert Witness tab
    else if (id == cisEntityFolderExpertWitnessTabMenuItmeID) {
        tempUrl = tempPKUrl;
    }
    // Prior Carrier tab
    else if (id == cisEntityFolderPriorCarrierTabMenuItmeID) {
        tempUrl = tempPKUrl;
        // Agent tab
    }
    else if (id == cisEntityFolderAgentTabMenuItmeID) {
        tempUrl = tempPKUrl;
    }
    // Summary Tab
    else if (id == cisEntityFolderSummaryTabMenuItemID){
        tempUrl = tempPKUrl;
    }
    // Risk Management Tab
    else if (id == cisEntityFolderRiskManagementTabMenuItemID){
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderBillingTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    // survey page
    else if (id == cisEntityFolderSurveyTabMenuItemID) {
        tempUrl =  tempPKUrl+"&entityId=" + getObject("pk").value;
    }
    // entity additional page
    else if (id == cisEntityAdditionalTabMenuItemID) {
        tempUrl = tempPKUrl;
    }  // entity historical data page
    else if (id == cisEntityHistoricalDataTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityGlanceTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityAddressTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityClientTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderPhoneTabMenuItemID) {
        // Source should be defaulted to "All Source".
        tempUrl = tempPKUrl + "&phoneNumber_sourceRecordFK=-2";
    }
    else if (id == cisEntityFolderClassificationTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderRoleTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderVendorTabMenuItemID) {
        tempUrl = tempPKUrl;
    }
    else if (id == cisEntityFolderVendorAddressTabMenuItemID) {
        tempUrl = tempPKUrl;
    }

    return urlEncode(tempUrl);
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
// This is a common function for entity folder "tab" pages.
//-----------------------------------------------------------------------------
function cisEntityFolderIsOkToChangePages(id, url) {
    // contact tab page.
    if (id == cisEntityFolderContactTabMenuItemID) {
        return true;
    }
    // relationship tab page.
    else if (id == cisEntityFolderRelationshipTabMenuItemID) {
        return true;
    }
    // audit trail tab page.
    else if (id == cisEntityFolderAuditTrailTabMenuItemID) {
        return true;
    }

    // WIP Inquiry tab
    else if (id == cisEntityFolderWIPInquiryTabMenuItemID) {
        return true;
    }

    // Claims tab
    else if (id == cisEntityFolderClaimsTabMenuItemID) {
        return true;
    }
    //policies tab
    else if (id == cisEntityFolderPoliciesTabMenuItemID) {
        return true;
    }
    // Training tab
    else if (id == cisEntityFolderTrainingTabMenuItemID) {
        return true;
    }
    // OrgGroup Tab
    else if (id == cisEntityFolderOrgGroupTabMenuItemID) {
        return true;
    }
    // Certification tab
    else if (id == cisEntityFolderCertificationTabMenuItemID) {
        return true;
    }

    // License tab
    else if (id == cisEntityFolderLicenseTabMenuItemID) {
        return true;
    }

    // Disability tab
    else if (id == cisEntityFolderDisabilityTabMenuItemID) {
        return true;
    }
    // Education Tab
    else if (id == cisEntityFolderEducationTabMenuItemID) {
        return true;
    }
    // Denominator tab
    else if (id == cisEntityFolderDenominatorTabMenuItemID) {
        return true;
    }
    // Vehicle tab
    else if (id == cisEntityFolderVehicleTabMenuItemID) {
        return true;
    }
    // Property tab
    else if (id == cisEntityFolderPropertyTabMenuItemID) {
        return true;
    }
    // Correspondence tab
    else if (id == cisEntityFolderCorresTabMenuItemID) {
        return true;
    }
    // Expert Witness tab
    else if (id == cisEntityFolderExpertWitnessTabMenuItmeID) {
        return true;
    }
    //  Prior Carrier
    else if (id == cisEntityFolderPriorCarrierTabMenuItmeID) {
        return true;
    }
    // Summary
    else if (id == cisEntityFolderSummaryTabMenuItemID) {
        return true;
    }
    // RM
    else if (id == cisEntityFolderRiskManagementTabMenuItemID) {
        return true;
    }
    // Billing Tab
    else if (id == cisEntityFolderBillingTabMenuItemID) {
        return true;
    }
    // entity additional Tab
    else if (id == cisEntityAdditionalTabMenuItemID) {
        return true;
    }  // Entity Historical Data Tab
    else if (id == cisEntityHistoricalDataTabMenuItemID) {
        return true;
    }  // Address tab
    else if (id == cisEntityAddressTabMenuItemID) {
        return true;
    }  // Client tab
    else if (id == cisEntityClientTabMenuItemID) {
        return true;
    }  // Entity Classification tab
    else if (id == cisEntityFolderClassificationTabMenuItemID) {
        return true;
    } // Phone Number Tab
    else if (id == cisEntityFolderPhoneTabMenuItemID) {
        return true;
    }  // Entity Role Tab
    else if (id == cisEntityFolderRoleTabMenuItemID) {
        return true;
    }  // Vendor tab
    else if (id == cisEntityFolderVendorTabMenuItemID) {
        return true;
    }  // Vendor Address tab
    else if (id == cisEntityFolderVendorAddressTabMenuItemID) {
        return true;
    }
    return true;
}

function commonAddRow(gridId) {
    dti.oasis.grid.commonAddRow(gridId);
}

/**
 * TODO Changed to use commonDeleteRow instead?
 *
 * Delete grid records including new records which haven't been saved yet and/or
 * existing records which are allowed to be deleted.
 */
function commonDeleteRecord(gridXmlData, table, allowDeleteExistRecord) {
    var curRowId = gridXmlData.recordset("ID").value;
    var gridId = table.id;

    if (hasObject("chkCSELECT_IND")) {
        // get selected rowId array
        var selectedRowIds = getSelectedKeys(gridXmlData);

        if (selectedRowIds.length == 0) {
            alert(getMessage("ci.common.error.rowSelect.delete"));
            return curRowId;
        }

        if (confirm(getMessage("js.delete.confirmation"))) {
            first(gridXmlData);
            gotopage(table, 'F');

            var upd_ind = '';
            var hasExistRecord = false;
            var hasNewRecord = false;
            beginDeleteMultipleRow(gridId);
            for (var i = 0; i < selectedRowIds.length; i++) {
                selectRow(gridId, selectedRowIds[i]);
                upd_ind = gridXmlData.recordset("UPDATE_IND").value;

                if (upd_ind == 'I') {
                    hasNewRecord = true;
                } else {
                    hasExistRecord = true;
                }

                if (upd_ind == 'I' || allowDeleteExistRecord) {
                    eval(gridId + "_deleterow();");

                    if (curRowId == selectedRowIds[i] || gridXmlData.recordset.recordcount == 1) {
                        curRowId = '';
                    }
                }
            }
            endDeleteMultipleRow(gridId);

            if (hasExistRecord && !allowDeleteExistRecord) {
                alert(getMessage(hasNewRecord ? "ci.common.error.newRecords.delete" : "ci.common.error.existRecords.delete"));
            }
        }
    } else {
        if (confirm(getMessage("js.delete.confirmation"))) {
            eval(gridId + "_deleterow();");
        }
    }

    return curRowId;
}

function commonDeleteRow(gridId, allowDeleteExistRecordB) {
    // account for missing parameter
    if (typeof allowDeleteExistRecordB === 'undefined') {
        allowDeleteExistRecordB = 'Y';
    }
    var hasExistRecord = false;
    var hasNewRecord = false;
    var rs = null;
    var upd_ind = "";

    var currentSelectedRowId = getSelectedRow(gridId);
    if (isFieldDefinedForGrid(gridId, "CSELECT_IND") && isColumnVisible(gridId, "CSELECT_IND")) {
        var dataArray = getSelectedKeys(getXMLDataForGridName(gridId));

        if (dataArray.length === 0) {
            alert(getMessage("ci.common.error.rowSelect.delete"));
            return;
        }
        if (confirm(getMessage("js.delete.confirmation"))) {
            rs = getXMLDataForGridName(gridId).recordset;

            for (var i = 0; i < dataArray.length; i++) {
                setTableProperty(getTableForXMLData(eval(gridId + "1")), "selectedRowId", dataArray[i]);
                if (!isEmptyRecordset(rs)) {
                    selectRow(gridId, dataArray[i]);
                    upd_ind = eval(gridId + "1").recordset("UPDATE_IND").value;
                    if (upd_ind === 'I') {
                        hasNewRecord = true;
                    } else {
                        hasExistRecord = true;
                    }
                    if (upd_ind === 'I' || allowDeleteExistRecordB==='Y') {
                    dti.oasis.grid.commonDeleteRow(gridId);
                }
            }
        }
            if (hasExistRecord && allowDeleteExistRecordB==='N') {
                alert(getMessage(hasNewRecord ? "ci.common.error.newRecords.delete" : "ci.common.error.existRecords.delete"));
            }
        }
    } else {
        rs = getXMLDataForGridName(gridId).recordset;
        if (!isEmptyRecordset(rs) && confirm(getMessage("cs.records.delete.confirm"))) {
            upd_ind = eval(gridId + "1").recordset("UPDATE_IND").value;
            if (upd_ind === 'I' || allowDeleteExistRecordB==='Y') {
            dti.oasis.grid.commonDeleteRow(gridId);
            } else {
                alert(getMessage("ci.common.error.existRecords.delete"));
            }
        }
    }
}

// TODO Replace with framework function?
function commonEnableDisableFormFields(detailDivId) {
    if (isStringValue(detailDivId)) {
        var editInd;
        if (hasObject("EDIT_IND")) {
            editInd = getObjectValue('EDIT_IND');
        }
        if (editInd) {
            var isDisabled = (editInd == 'N');
            var detailFormElements = getDetailFormElements(detailDivId);
            for (var i = 0; i < detailFormElements.length; i++) {
                enableDisableField(detailFormElements[i], isDisabled);
            }
        }
    }
}

// TODO Replace with framework function?
function commonGetMenuQueryString(id, url) {
    var tempUrl = '';
    // Add parameter date to request to avoid IE cache
    tempUrl = tempUrl + "&date=" + new Date();

    return tempUrl;
}

//-----------------------------------------------------------------------------
// TODO The only difference between commonHandleOnGetAddlInfo and commonHandleOnGetInitialValues is passing "true" to setRecordsetByObject.
// The function is not used except for PM. Could we remove it?

// Common method to handle get Additional informaton by Ajax
//-----------------------------------------------------------------------------
function commonHandleOnGetAddlInfo(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0], true);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
        }
    }
}

function commonHandleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
        }
    }
}

// TODO Why we need to call the function refreshPage? What about if the function refreshPage doesn't exist?
function commonHandleOnPostAjaxSubmitDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var parentFrame = getParentFrame();
            if (parentFrame) {
                parentFrame.refreshPage();
            }
            else {
                refreshPage();
            }
        }
    }
}

function commonIsOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
            return false;
        }
    }
    return true;
}

function commonOnAfterSort(table, XMLData) {
    // Removed the invocation of handleOnAfterSort since it's already called by baseOnAfterSort.
}

function commonOnBeforeSort(table, XMLData) {
    if (XMLData.recordset.recordCount > 0 && !getTableProperty(table, "sorting")) {
        userReadyStateReadyPass = 1
    }

    // Removed invocation of handleOnBeforeSort since it's already called in baseOnBeforeSort.
}

function commonOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DELETE':
            var currentGrid = getCurrentlySelectedGridId();
            // if hidden field allowDeleteExistRecordB does not exist, will pass null
            commonDeleteRow(currentGrid, getObjectValue('allowDeleteExistRecordB'));
            break;

        case 'ADD':
            var currentGrid = getCurrentlySelectedGridId();
            commonAddRow(currentGrid);
            break;

        case 'CLOSE_DIV':
            closeThisDivPopup(false);
            break;

        case 'CLOSE_RO_DIV':
            closeThisDivPopup(true);
            break;
    }

    //Invoke page dependent button click logic if it exists
    var functionExists = window.eval && eval("window.handleOnButtonClick");
    if (functionExists != null) {
        handleOnButtonClick(asBtn);
    }
}

function commonOnChange(field) {
    try {
        var fieldObj = field;
        if (field.length && !field.options) {
            fieldObj = field[0];
        }

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
    } catch(ex) {
        var fieldName = '';
        if (field.length && !field.options) {
            fieldName = field[0].name;
        } else
            fieldName = field.name;
        // handle case where the field is not part of the grid and has no dataSrc
        var isExcluded = false;
        var functionExists = eval("window.excludeFieldsForSettingUpdateInd");
        if (functionExists) {
            var excludedFields = excludeFieldsForSettingUpdateInd();
            for (var i = 0; i < excludedFields.length; i++) {
                if (fieldName == excludedFields[i]) {
                    isExcluded = true;
                    break;
                }
            }
        }

        if (!isExcluded) {
            //Exclude fields by prefix
            var prefixArray = excludeFieldsByPrefixForSettingUpdateInd();
            for (var idx = 0; idx < prefixArray.length; idx++) {
                if (fieldName.startsWith(prefixArray[idx])) {
                    isExcluded = true;
                    break;
                }
            }
        }

        //not set isChanged if a filter criteria is changed by a user in a filter panel
        //filter field should have a filter suffix
        if (!isExcluded) {
            var filterSuffix = "FILTER";
            if (fieldName.toUpperCase().indexOf(filterSuffix) == fieldName.length - filterSuffix.length) {
                isExcluded = true;
            }
        }

        if (!isExcluded) {
            isChanged = true;
        }
    }
}

function excludeFieldsByPrefixForSettingUpdateInd() {
    return ["searchCriteria_", "filterCriteria_"];
}

function commonOnLoad() {
    MM_preloadImages(getCorePath() + notesImage);
    MM_preloadImages(getCorePath() + noNotesImage);

    //Invoke process dependent field-value layer function
    var functionExists = eval("window.processDeps");
    if (functionExists) {
        processDeps();
    }

    var functionExists = eval("window.handleConfirmations");
    if (functionExists) {
        handleConfirmations();
    }

    if (hasErrorMessages)
        handleError("");

    var functionExists = eval("window.handleFilterCisList");
    if (functionExists) {
        handleFilterCisList();
    }

    return true;
}

function commonOnRowSelected(gridId, rowId) {
    try {
        if (priorRowId != rowId || rowId == null) {
            //Invoke process dependent field-value layer function
            var functionExists = eval("window.processDeps");
            if (functionExists) {
                processDeps();
            }

            priorRowId = rowId;
        }
    }
    catch(ex) {
        alert("commonOnRowSelected: An exception occurred in the script. Error name: " + ex.name + ". Error message: " + ex.message);
    }

    // Filter child grids if there are multiple grids in the page.
    var functionExists = eval("window.filterChildGrids");
    if (functionExists) {
        filterChildGrids(gridId, rowId);
    }

    return true;
}

/**
 Handle the Submit event with the desired action.
 The form[0].process is first set to the action name.
 Next, this function validates required form fields.
 If form fields pass the validation, this function checks for a custom page-specific submit handler
 in the form of 'handleOnSubmit' where action is passed as a parameter
 If the submit handler exists, it is called, and the return value used to determine if the submit should proceed.
 The submit handler is usefull for such things as validating the form, and for overriding the process value.

 If the submit handler returns true, or if there is not submit handler, this functions finishes by submitting the form.
 Otherwise, the submit handler will not submit the form.
 */
function commonOnSubmit(action, skipFormValidation, skipGridValidation, saveIfNoChanges, showProcessingDivPopup) {
    currentSubmitAction = action;
    if (action.length >= 4 && action.substring(0, 4).toUpperCase() == "SAVE") {
        if (!isChanged && !isPageGridsDataChanged() && !saveIfNoChanges) {
            return;
        }
    }
    if (!isSaveInProgress()) {
        var proceed = true;

        // By default, set the process parameter to the proviced action, and submit the form.
        document.forms[0].process.value = action;

        // validate required fields in form/grid, except those in hidden Div
        var selectedGridId = getCurrentlySelectedGridId();
        if (!skipGridValidation && isDefined(selectedGridId) && (selectedGridId != "")) {
            proceed = commonValidateGrid(selectedGridId);
        }
        else if (!skipFormValidation) {
            proceed = commonValidateForm();
        }

        if (proceed) {
            // Check if a submit handler exists for this page
            var functionExists = eval("window.handleOnSubmit");

            if (functionExists != null) {
                // Call the page specific submit handler
                proceed = handleOnSubmit(action);
            }
        }

        if (proceed) {
            submitForm(showProcessingDivPopup);
        }
    }
    else {
        alert(getMessage("cs.save.process.notCompleted"));
    }
}


function commonReadyStateReady(table) {
    try {
        setTableProperty(table, "selectRowInCommonReadyStateReady", true);

        var selectedRow = getTableProperty(table, "selectedTableRowNo");
        var sorting = getTableProperty(table, "sorting");

        if (selectedRow && !sorting) {
            if (!dti.oasis.page.useJqxGrid()) {
                hiliteSelectRow(table.rows[selectedRow]);
                var rowid = getSelectedRow(table.id);

                if (rowid) {
                    selectRow(table.id, rowid);
                } else {
                    selectFirstRowInGrid(table.id);
                }

            }
        }
        else {
            var fireSelectFirstRowInGrid = dti.oasis.grid.getProperty(table.id, "autoSelectFirstRow", true);
            if (fireSelectFirstRowInGrid == true) {
                selectFirstRowInGrid(table.id);
            }

            // rset sorting indicators
            if (sorting) {
                userReadyStateReadyPass = 0;
            }
        }

        if (!isEmptyRecordset(eval(table.id+'1').recordset)) {
            showNonEmptyTable(table);
        }
        else {
            hideEmptyTable(table);
        }
    } finally {
        setTableProperty(table, "selectRowInCommonReadyStateReady", false);
    }
}

/** display all messages from the xml doc,
 the messages are identified by TEXT tag, created by BaseAction.writeAjaxXmlResponse
 optionally, if there is a tag with id:omessage, its innerHTML is replaced with the messages

 moved from displayMessagesifAny() in cmClaimTransactionList.js
 it takes a xmldocument from ajax response.

 jld: add return to pass back message for further use
 */
function displayAjaxMessages(xml) {
    var messages = xml.getElementsByTagName("TEXT");
    var messageText = "";
    var i = 0;
    while (i < messages.length) {
        messageText += messages[i++].text + "\n";
    }
    if (getSingleObject("omessage") != null ) {
        getSingleObject("omessage").innerHTML =  messageText;
    }
    if (!isEmpty(messageText)) {
        alert(messageText);
    }
    return messageText;
}

/*
    Enable and hide all disabled fields in a form before submit
*/
function enableFieldsForSubmit(theform) {
    var elems = theform.elements;

    for (var i = 0; i < elems.length; i++) {
        if (elems[i].disabled) {
            elems[i].style.visibility = "hidden";
            elems[i].disabled = false;
        }
    }
}

/**
 * TODO Remove the usages.
 *
 * @deprecated please use javascript standard function encodeURIComponent
 * @param url
 */
function encodeUrl(url) {
    var newUrl = url;
    newUrl = replace(newUrl, "%", "%25");
    newUrl = replace(newUrl, "[+]", "%2b");
    newUrl = replace(newUrl, "&", "%26");
    newUrl = replace(newUrl, "#", "%23");
    newUrl = replace(newUrl, "@", "%40");
    newUrl = replace(newUrl, "'", "%27");
    newUrl = replace(newUrl, "\"", "%22");
    return newUrl;
}

//---------------------------------------------------------------
// Handle message from notes JSPs in CommonServices telling the
// entity header whether or not the entity has notes.
//---------------------------------------------------------------
function entityHeaderHandleNotesExist(notesExist) {
    var testObj = eval('document.forms[0].entityHeaderNotesInd');
    if (testObj) {
        if (notesExist) {
            document.forms[0].entityHeaderNotesInd.value = "true";
        }
        else {
            document.forms[0].entityHeaderNotesInd.value = "false";
        }
    }
    testObj = eval('document.forms[0].entityHeaderNotesIndDescription');
    if (testObj) {
        testObj.className = "notes"
        if (notesExist) {
            getSingleObject('entityHeaderNotesIndDescriptionROSPAN1').childNodes[0].src = getCorePath() + notesImage;
            document.forms[0].entityHeaderNotesIndDescription.value = notesExistDisplayValue;
        }
        else {
            getSingleObject('entityHeaderNotesIndDescriptionROSPAN1').childNodes[0].src = getCorePath() + noNotesImage;
            document.forms[0].entityHeaderNotesIndDescription.value = notesNotExistDisplayValue;
        }
    }
    testObj = getSingleObject('entityHeaderNotesIndDescriptionROSPAN');
    if (!testObj) {
        testObj.className = "notes";
    }
    testObj = getSingleObject('entityHeaderNotesIndDescriptionROSPAN1');
    if (!testObj) {
        testObj = getSingleObject('entityHeaderNotesIndDescriptionROSPAN');
    }
    if (testObj) {
        if (notesExist) {
            testObj.childNodes[0].src = getCorePath() + notesImage;
        }
        else {
            testObj.childNodes[0].src = getCorePath() + noNotesImage;
        }
    }

    // update noteExistB in EntityInfo in session
    var path = getCISPath() + "/ciEntityModify.do?process=updateNoteExistInSession";
    path += "&noteExistB=" + (notesExist ? "Yes" : "No");
    new AJAXRequest("POST", path, '', function(ajax) {

    });
}

function handleVerifiedResult(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var text = ajax.responseText;
            if (isStringValue(text)) {
                refNumValid = text;
            } else {
                alert("Contact Customer Service - TEXT retrieval problem:\n" + ajax.statusText);
            }
        }
    }
}

function hasConfirmation(messageKey) {
    for (var i = 0; i < confirmMessages.length; i++) {
        if (confirmMessages[i].messageKey == messageKey) {
            return true;
        }
    }

    return false;
}

function hasProcessedAjaxErrorMessages() {
    return processedAjaxErrorMessages;
}


// TODO Is this function used? Do we need to move it to gui.js?
function hideActionItem(itemId) {
    if (getSingleObject("R_actionitem_" + itemId) != null)
        getSingleObject("R_actionitem_" + itemId).style.display = "none";
}

function hideShowForm(gridId) {
    var currentTbl = getTableForXMLData(getXMLDataForGridName(gridId));
    var gridDetailDivId = getTableProperty(currentTbl, "gridDetailDivId");

    if (isStringValue(gridDetailDivId)) {
        if (hasObject(gridDetailDivId))
            getSingleObject(gridDetailDivId).style.display =
                ( (getTableProperty(currentTbl, "hasrows") == true)) ? "block" : "none";
    }
}

function generateReferenceNumberData() {
    var data = "entityId=" + getObjectValue("pk");
    data += "&referenceNumber=" + getObjectValue("entity_referenceNumber");
    return data;
}

function getChanges(ReferenceXML) {
    return getChangesInRowsFormat(ReferenceXML);
}

function getConfirmationResponse(messageKey) {
    for (var i = 0; i < confirmMessages.length; i++) {
        if (confirmMessages[i].messageKey == messageKey);
        {
            return confirmResponses[i];
        }
    }

    return "";
}

/*
  Return an Array of form elements that are children of the DIV element with id = the given divId string.
 */
function getDetailFormElements(divId) {
    var detailElements = new Array();
    var detailDiv = getObject(divId);
    //to be canceled
    var allFormElements = detailDiv.document.forms[0].elements;
    var detailIdx = 0;
    for (var i = 0; i < allFormElements.length; i++) {
        if (isNodeChildOfId(allFormElements[i], divId)) {
            detailElements[detailIdx++] = allFormElements[i];
        }
    }
    return detailElements;
}

//-----------------------------------------------------------------------------
// Get the object ignore the case
//-----------------------------------------------------------------------------
function getObjectIgnoreCase(objName) {
    var elems = document.forms[0].elements;

    /* loop through elements */
    for (var i = 0; i < elems.length; i++) {
        if (elems[i].name.toUpperCase() == objName.toUpperCase()) {
            return elems[i];
        }
    }
    return undefined;
}

/**
 *   TODO Replace with framework function?
 *
 *   Return all selected columns data in the grid.
 */
function getSelectedRowData(gridId) {
    var gridData = getXMLDataForGridName(gridId);
    var modXML = gridData.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
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
        // Exclude rows with id=-9999 only if there is at least one real row because
        //they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";

            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes() == true) {
                numColumnNodes = rowNode.childNodes.length;
                for (j = 0; j < numColumnNodes; j++) {
                    columnNode = rowNode.childNodes.item(j);
                    var nodeValue = encodeXMLChar(columnNode.text);
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

function getTextForFirstElement(xmlDoc, elementName) {
    var text = "";
    var elements = xmlDoc.getElementsByTagName(elementName);
    if (elements && elements.length > 0) {
        text = dti.oasis.string.trim($(elements.item(0)).text());
    }
    return text;
}

function getTotalAmount(gridId, column) {
    var gridData = getXMLDataForGridName(gridId);
    var columnName = "C" + column.toUpperCase();
    var modXML = gridData.documentElement.selectNodes("//ROW[@id!='-9999']");
    var nodelen = modXML.length;
    var i;
    var newNode;
    var childnodelen;
    var childnode;
    var ID;
    var ttlamount = 0.0;
    for (i = 0; i < nodelen; i++) {
        newNode = modXML.item(i);
        ID = newNode.getAttribute("id");
        var tmpVal;
        var val = 0.0;
        tmpVal = gridData.documentElement.selectSingleNode("//ROW[@id ='" + ID + "']/" + columnName);
        if (tmpVal) {
            if (tmpVal.text.length > 0) {
                val = unformatMoneyStrValAsStr(tmpVal.text);
            } else {
                val = 0;
            }
        }
        ttlamount += parseFloat(val);
    }
    return ttlamount;
}

//get parameter value by name from url
//url:the url where parameter exists
//name:parameter name
function getUrlParam(url, name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = url.search.substr(1).match(reg);
    if (r != null)   return   unescape(r[2]);
    return   null;
}

function getValidFldValue(fldId) {
    var obj = getObject(fldId);
    if (isUndefined(obj) || isNull(obj)) {
        alert(getMessage("cs.field.error.undefined", new Array(fldId)));
        return "";
    }
    return obj.value;
}

//-----------------------------------------------------------------------------
// TODO Replace with framework function?
//
// Transform the grid's current selected row to object
//-----------------------------------------------------------------------------
function getObjectFromRecordset(gridId) {
    var excludeFieldId = ["CSELECT_IND","UPDATE_IND",
        "DISPLAY_IND","EDIT_IND","id","index",
        "col","$Text"];
    var fieldCount = gridId.recordset.Fields.count;
    var oRecord = new Object();
    // loop through columns
    for (var i = 0; i < fieldCount; i++) {
        var fieldName = gridId.recordset.Fields.Item(i).name;
        var fieldValue = gridId.recordset.Fields.Item(i).value;
        var exlCount = excludeFieldId.length;

        // Check if current field should be excluded
        var isFound = false;
        for (var j = 0; j < exlCount; j++) {
            if (fieldName == excludeFieldId[j]) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            continue;
        }

        // Exclude field which value contain "selectRow"
        if (fieldValue.indexOf("selectRow") != -1) {
            continue;
        }

        // Exclude field which value contain "javascript"
        if (fieldValue.indexOf("javascript") != -1) {
            continue;
        }

        fieldName = fieldName.substring(1);
        try {
            eval("oRecord." + fieldName + " = gridId.recordset.Fields.Item(i).value");
        }
        catch (ex) {
            alert(getMessage("cs.run.error.grid.value", new Array(fieldName)));
        }
    }
    return oRecord;
}

function goToModule(module) {
    if (isChanged) {
        if (!confirm(ciDataChangedConfirmation)) {
            return;
        }
    }
    var url = "";
    if (module == 'search') {
        url = "ciEntitySearch.do";
    }
    else {
        alert(getMessage("ci.entity.message.module.unknown"));
        return
    }
    setWindowLocation(url);

}

//-----------------------------------------------------------------------------
// go to any of the common entity-related modules from any page
//-----------------------------------------------------------------------------
function goToEntityModule(module, pk, entityName, entityType) {
    var url = "?pk=" + pk +
        "&entityName=" + encodeURIComponent(entityName) +
        "&entityType=" + entityType;
    if (module == 'address') {
        // Go to the address page.
        url = "ciAddressList.do" + url;
        setWindowLocation(urlEncode(url));
    }
    else if (module == 'phonenumber') {
        // Go to the phone number page.
        url = "ciPhoneNumberList.do" + url;
        setWindowLocation(urlEncode(url));
    }
    else if (module == 'entityclass') {
        // Go to the entity class page.
        url = "ciEntityClassList.do" + url;
        setWindowLocation(urlEncode(url));
    }
    else if (module == 'entityrole') {
        // Go to the entity role page.
        url = "ciEntityRole.do" + url;
        setWindowLocation(urlEncode(url));
    }
    else if (module == 'vendor') {
        // Go to the vendor page.
        url = "ciVendor.do" + url;
        setWindowLocation(urlEncode(url));
    }
    else if (module == 'vendorAddress') {
        // Go to the vendor address page.
        url = "ciVendorAddress.do" + url;
        setWindowLocation(urlEncode(url));
    }
}

function isConfirmationMessagesProcessed() {
    return processedConfirmationMessages;
}

/**
 * Check if the menu opens a div popup.
 *
 * This function overwrites the function in gui.js.
 *
 * @param menuId
 * @returns {boolean}
 */
function isDivPopUpMenu(menuId) {
    return (menuId != null && menuId.indexOf('PUP') > 0);
}

/*
  Return true if the given node is a child of a node with the id = the given id string; otherwise false.
 */
function isNodeChildOfId(node, id) {
    var isChild = false;
    for (var parent = node.parentNode; parent.nodeName.toUpperCase() != "FORM" && parent != document; parent = parent.parentNode) {
        if (parent.id == id) {
            isChild = true;
            break;
        }
    }
    return isChild;
}

//-----------------------------------------------------------------------------
// Identify the field is required or not.
// If the field is invisible then it will be treated as not-required.
//-----------------------------------------------------------------------------
function isRequiredField(fldId) {
    var req;
    var reqDefined = true;
    //If the element is invisible then it must be not required
    var pElem = getObject(fldId).parentElement;
    while (pElem != null) {
        if (pElem.style.display == "none") {
            return false;
        }
        pElem = pElem.parentElement;
    }
    eval("reqDefined = typeof REQ_" + fldId + " != 'undefined';");
    if (reqDefined) {
        eval("req = REQ_" + fldId + ";");
        if (req) {
            return true;
        }
    }
    return false;
}

function isValidationException(xmlDoc) {
    var isValidationException = false;
    root = xmlDoc.documentElement;
    if (root != null) {
        var validationExceptions = root.getElementsByTagName("VALIDATIONEXCEPTION");
        if (validationExceptions.length > 0 && validationExceptions.item(0).childNodes.item(0).text == "YES")
            isValidationException = true;
    }
    return isValidationException;
}

function openAuditTrailPopup(winName, historyType, sourceNo, operationTableName, operationId) {
    var url = 'ciAuditTrailPopup.do?historyType=' + historyType;

    if (sourceNo) {
        url += '&sourceNo=' + sourceNo;
    }
    if (operationTableName) {
        url += '&operationTableName=' + operationTableName;
    }
    if (operationId) {
        url += '&operationId=' + operationId;
    }
    openPopup(url, winName, 900, 625, 20, 20);
}

/*
    Sends an Ajax POST Request to submit the form, posting the form fields to the given uri.

    Parameters:
    uri - REQUIRED - The URI to invoke.
    process - OPTIONAL - If not null, the process request parameter is set to the provided process value.
    validateForm - OPTIONAL - If true, form will be validated.
        If validation fails, all processing is stopped.
    validateGrid - OPTIONAL - If true, grid is be validated before sending the request.
        If validation fails, all processing is stopped.
    callbackFunction - OPTIONAL - The JavaScript function to invoke when the Ajax request is complete.
        If not specified, the current page (or parent page if this is a DIV Popup) is reloaded.
    addConfirmationValues - OPTIONAL - If true, the confirmation values are added to the url.
 */
function postAjaxSubmit(uri, process, validateForm, validateGrid, callbackFunction, addConfirmationValue, async) {

    var proceed = true;

    // validate required fields in form/grid, except those in hidden Div
    var selectedGridId = getCurrentlySelectedGridId();
    if (validateGrid && isDefined(selectedGridId) && (selectedGridId != "")) {
        proceed = commonValidateGrid(selectedGridId);
    }
    else if (validateForm) {
        proceed = commonValidateForm();
    }

    if (proceed) {

        // Add the app path if not present.
        if (uri.indexOf(getAppPath()) == -1) {
            uri = getAppPath() + uri;
        }

        // Add the process if requested
        if (isDefined(process)) {
            if (uri.indexOf('?') > -1) {
                uri += '&process=' + process;
            }
            else {
                uri += '?process=' + process;
            }
        }
        postAjaxSubmitUrl = uri;

        // Add the confirmation responses if requested
        if (addConfirmationValue) {
            for (var i = 0; i < confirmMessages.length; i++) {
                uri += "&" + confirmMessages[i].messageKey + ".confirmed=" + confirmResponses[i];
            }
        }

        if (isUndefined(callbackFunction)) {
            callbackFunction = commonHandleOnPostAjaxSubmitDone;
        }
        else {
            postAjaxSubmitCallbackFunction = callbackFunction;
        }

        showProcessingDivPopup();
        startRefresh(document.forms[0], uri, callbackFunction, async);
    }
}

function postCommonAddRow(gridId) {
    priorRowId = dti.oasis.grid.postCommonAddRow(gridId);
    setFocusToFirstEditableFormField(gridId);
}

//-----------------------------------------------------------------------------
// Check if there are any modified records before OOSE risk/coverage/class/component data
//-----------------------------------------------------------------------------
function preOoseChangeValidation(type, gridId, baseRecordIdColumnName) {
    isForOose = "Y";
    var valid = true;
    var XMLData = getXMLDataForGridName(gridId);
    var rowIndex = 0;
    var currentRowId = getSelectedRow(gridId);
    var baseRecordId = XMLData.recordset(baseRecordIdColumnName).value;
    var recordModeCode = XMLData.recordset("CRECORDMODECODE").value;

    // Loop the riskListGrid recordset to do the check
    first(XMLData);
    while (!XMLData.recordset.eof) {
        var curRecordModeCode = XMLData.recordset("CRECORDMODECODE").value;
        var curBaseRecordId = XMLData.recordset(baseRecordIdColumnName).value;
        if (!isEmpty(curBaseRecordId)
            && curBaseRecordId == baseRecordId
            && (curRecordModeCode == "TEMP" || curRecordModeCode == "REQUEST")) {
            valid = false;
            currentRowId = XMLData.recordset("ID").value;
            alert(getMessage("pm.oose.modified.record.exist.error2", new Array(type)));
            break;
        }

        rowIndex ++;
        next(XMLData);
    }

    selectRowById(gridId, currentRowId, rowIndex);
    return valid;
}

// remove a given parameter (its name and value) from url
function removeParameterFromUrl(url, parameterName) {
    while (url.indexOf(parameterName) != -1) { // url might contain the same parameter more than once!
        var parameterIndex = url.indexOf(parameterName);
        var nextAmpsantIndex = url.indexOf('&', parameterIndex + 1);
        if (nextAmpsantIndex == -1) {
            parameterNameValueText = url.substring(parameterIndex);
        }
        else {
            parameterNameValueText = url.substring(parameterIndex, nextAmpsantIndex);
        }
        url = url.replace(parameterNameValueText, "");
        url = url.replace("&&", "&");
    }
    return url;
}

/*
    Sends an Ajax POST Request to submit the form after getting confirmation responses.
 */
function repostAjaxSubmitWithConfirmationValue(async) {
    // Get cached url
    var uri = postAjaxSubmitUrl;

    // Add the confirmation responses
    for (var i = 0; i < confirmMessages.length; i++) {
        uri += "&" + confirmMessages[i].messageKey + ".confirmed=" + confirmResponses[i];
    }

    // Set callback function
    var callbackFunction = postAjaxSubmitCallbackFunction;
    if (isUndefined(callbackFunction)) {
        callbackFunction = commonHandleOnPostAjaxSubmitDone;
    }

    // Start process
    showProcessingDivPopup();
    startRefresh(document.forms[0], uri, callbackFunction, async);
}

//-----------------------------------------------------------------------------
// Reset the OK to skip tax ID dups flag;  if the flag was Y and the user
// changed the tax ID, then set it to N, because now it's not OK to skip tax ID
// dups on a save.  Called in entity add and entity modify JSPs.
//-----------------------------------------------------------------------------
function resetOKToSkipTaxIDDupsFlag(taxIDField, entTypeField,
                                    okToSkipTaxIDDupsField) {
    if (entTypeField != null) {
        entType = entTypeField.value;
    }
    if (entType == null) {
        entType = '';
    }
    if
    (
        (taxIDField.name == 'entity_socialSecurityNumber' && entType == 'P') ||
        (taxIDField.name == 'entity_federalTaxID' && entType == 'O')
    ) {
        if (okToSkipTaxIDDupsField) {
            if (okToSkipTaxIDDupsField.value == 'Y') {
                okToSkipTaxIDDupsField.value = 'N';
                //        alert('ok to skip tax ID dups field is now N');
            }
        }
    }
}

function setFormFieldValuesByObject(dataObject) {
    for (var prop in dataObject) {
        setInputFormField(prop, dataObject[prop]);
    }
}

function setInitialUrlInGrid(xmlData) {
    dti.oasis.grid.setInitialUrlInGrid(xmlData);
}

/*
    Set the initial values in the named grid from the array of dataPairs.
    The dataPairs parameter is an array of dataKey/dataValue pairs,
    where the first dataKey is at the provided startingIndex,
    and the first dataValue is at startingIndex + 1.
*/
function setInitialValuesInGrid(gridName, dataPairs, startingIndex) {
    var i = startingIndex;
    var dataLength = dataPairs.length - 1;
    while (i < dataLength) {
        var initValue = gridName + ".recordset('C" + dataPairs[i].toUpperCase() + "').value = '" + dataPairs[i + 1] + "'";
        eval(initValue);
        if (hasObject(dataPairs[i]) && hasObject(dataPairs[i] + "LOVLABELSPAN")) {
            field = getObject(dataPairs[i]);
            field.value = dataPairs[i + 1];
            syncToLovLabelIfExists(field);
        }
        i = i + 2;
    }
}

function setIsChanged(boolIsChanged) {
    isChanged = boolIsChanged;
}

//-----------------------------------------------------------------------------
// Set default value for grid's current selected row by data object
//-----------------------------------------------------------------------------
function setRecordsetByObject(XMLData, dataObject, excludeEditDisplayUpdateInd) {
    setCurrentRecordValues(XMLData, dataObject, excludeEditDisplayUpdateInd);
}

// TODO Replace with framework function?
function showActionItem(itemId) {
    if (getSingleObject("R_actionitem_" + itemId) != null)
        getSingleObject("R_actionitem_" + itemId).style.display = "block";
}

function showMiniCISForEntityPk(entityIdField) {
    if (getObject(entityIdField) != null) {
        thisopenEntityMiniPopupWin(getObject(entityIdField).value);
    }
    else {
        alert(getMessage("cs.entity.miniPopup.error.noEntityId", new Array(entityIdField)));
    }
}

/*
    Submit the form as a Save action.
*/
function submitForm(forceToShowProcessingDivPopup) {
    if (isSaveInProgress() == false) {

        if (forceToShowProcessingDivPopup == undefined && (currentSubmitAction.substring(0, 4).toUpperCase() == "SAVE" || currentSubmitAction == 'Create'))
            showProcessingDivPopup();
        else if (forceToShowProcessingDivPopup)
            showProcessingDivPopup();

        enableFieldsForSubmit(document.forms[0]);

        if (getCurrentlySelectedGridId()) {
            eval(getCurrentlySelectedGridId() + "_update();")
        }
        else {
            submitFirstForm();
        }
    }
    else {
        alert(getMessage("cs.save.process.notCompleted"));
    }
}

//-----------------------------------------------------------------------------
// Sync LOV value in both grid header and page form
//-----------------------------------------------------------------------------
function syncLov(XMLData, fieldName, fieldValue) {
    /* Sync only LOVLABEL field is populated */
    if (isFieldExistsInRecordset(XMLData.recordset, "C" + fieldName + "LOVLABEL")) {
        /* Sync value in grid header */
        var fld = getObjectIgnoreCase(fieldName + "_GH");
        if (fld != undefined && fld.tagName == "SELECT") {
            XMLData.recordset("C" + fieldName + "LOVLABEL").value = getOptionDescription(fld, fieldValue);
        }

        /* Sync value in page form */
        fld = getObjectIgnoreCase(fieldName);
        if (fld != undefined && fld.tagName == "SELECT") {
            XMLData.recordset("C" + fieldName + "LOVLABEL").value = getOptionDescription(fld, fieldValue);
        }
    }
}

//-----------------------------------------------------------------------------
// Opens the entity mini popup window.
//-----------------------------------------------------------------------------
function thisopenEntityMiniPopupWin(pk) {
    if (pk == 0) {
        alert(getMessage("cs.entity.information.error.notRecorded"));
        return;
    }

    var path = getCISPath() + "/ciEntityMiniPopup.do?pk=" + pk;

    if (isDivPopupEnabled()) {
        //openDivPopup(popupTitle, urlToOpen, isModel, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight)
        openDivPopup("", path, true, true, "", "", "900", "890", "890", "880", "", true);
    }
    else {
        var mainwin = window.open(path, 'EntityMiniPopup',
            'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
        mainwin.focus();
    }
    return;
}

//-----------------------------------------------------------------------------
// update the status of SELECT_IND checkbox in the grid
// when click top selectAll/deselectAll checkbox
//-----------------------------------------------------------------------------
function updateAllSelectInd(asBtn, gridId) {
    var selectedGridId = gridId;
    if (!selectedGridId || selectedGridId == null || !eval(selectedGridId)) {
        selectedGridId = getCurrentlySelectedGridId();
    }
    var XMLData = getXMLDataForGridName(selectedGridId);
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        switch (asBtn) {
            case 'SELECT':
                eval(selectedGridId + "_updatenode('CSELECT_IND', -1)");
                break;
            case 'DESELECT':
                eval(selectedGridId + "_updatenode('CSELECT_IND', 0)");
                break;
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}

function userReadyStateReady(table) {
    //alert('userReadyStateReady');
    // do nothing if the table is not ready
    if (!table.id || table.readyState != 'complete')
        return;

    // When there is pagination, this function is called twice for sorting - once for the header and once for the data.
    // So do nothing for the first time.
    pages = getTableProperty(table, "pages");
    var sorting = getTableProperty(table, "sorting");
    if (sorting && pages > 1 && userReadyStateReadyPass == 1) {
        userReadyStateReadyPass ++;
    }
    else {
        var functionExists = eval("window.setRowStyle");
        if (functionExists) {
            setRowStyle(table);
        }

        // invoke the commonReadyStateReady only if we're not in middle of a process
        if (!getTableProperty(table, "isInCommonAddRow") && !getTableProperty(table, "isInSelectRowById")) {
            commonReadyStateReady(table);

            if (window.handleReadyStateReady)
                handleReadyStateReady(table);
        }
    }
    // set the table ready flag so that commonAddRow can take care of the rest
    setTableProperty(table, "isUserReadyStateReadyComplete", true);
}

//-----------------------------------------------------------------------------
// address type validation for foreign addresses
//-----------------------------------------------------------------------------
function validateAddressTypeForForeignAddress(addrTypeValue,
                                              addrTypeDesc, addrTypeLabel, usaAddrBValue) {
    if (usaAddrBValue == 'Y') {
        return '';
    }
    else {
        if (addrTypeValue == 'PREMISE') {
            return(getMessage("ci.common.error.foreignAddress.invalid", new Array(addrTypeDesc, addrTypeLabel)) + "\n");
        }
        else {
            return '';
        }
    }
}

//-----------------------------------------------------------------------------
// Validate Canadian postal code
//-----------------------------------------------------------------------------
function validateCanadaPostalCode(postalCodeFld) {
    if (isEmpty(postalCodeFld.value) || isFieldMasked(postalCodeFld)) {
        return "";
    }

    var postalCodeRegExp = new RegExp("^[a-zA-Z]\\d[a-zA-Z] \\d[a-zA-Z]\\d$");

    if (!postalCodeRegExp.test(postalCodeFld.value)) {
        return getMessage("ci.entity.message.postalCode.invalid");
    }
    return "";
}

//-----------------------------------------------------------------------------
// Validate that the class from date is less than or equal to the class to date.
//-----------------------------------------------------------------------------
function validateClassDates(classDesc, classValue, effFromDateDesc, effFromDateValue,
                            effToDateDesc, effToDateValue) {
    if (classValue == null || classValue == '') {
        return '';
    }
    var date1OnOrAfterDate2 = isDate2OnOrAfterDate1(effFromDateValue, effToDateValue);
    if (date1OnOrAfterDate2 == 'N') {
        return getMessage("ci.common.error.classDescription.after", new Array(classDesc, effToDateDesc, formatDateForDisplay(effToDateValue), effFromDateDesc, formatDateForDisplay(effFromDateValue))) + "\n";
    }
    else {
        return '';
    }
}

//-----------------------------------------------------------------------------
// country validation for foreign addresses
//-----------------------------------------------------------------------------
function validateCountryForForeignAddress(countryValue,
                                          countryLabel, usaAddrBValue) {
    if (usaAddrBValue == 'Y') {
        return '';
    }
    else {
        if (!isValue(countryValue)) {
            return(getMessage("ci.common.error.foreignAddress.required", new Array(countryLabel)) + "\n");
        }
        else {
            return '';
        }
    }
}

//-----------------------------------------------------------------------------
// Validate that a value has been entered for a verified tax ID and that the
// default tax ID has been verified.
//-----------------------------------------------------------------------------
function validateDefaultVerifiedTaxID(defaultTaxIDFld, SSNFld, SSNVerBFld,
                                      TINFld, TINVerBFld, vendorVerifySysParamValue) {

    if (isFieldMasked(getObject(SSNFldID))) return '';
    if (isFieldMasked(getObject(TINFldID))) return '';

    var msg = '';
    var dftlTaxIDValue = '';
    var dftlTaxIDLabel = 'Default Tax ID';
    var SSNValue = '';
    var SSNLabel = 'SSN';
    var SSNVerBValue = 'N';
    var SSNVerBLabel = 'SSN Verified';
    var TINVerBValue = 'N';
    var SSNVerBLabel = 'TIN Verified';
    var TINValue = '';
    var TINLabel = 'TIN';
    var isValueSSN = false;
    var isValueTIN = false;
    if (defaultTaxIDFld != null) {
        dftlTaxIDValue = defaultTaxIDFld.value;
        dftlTaxIDLabel = getLabel(defaultTaxIDFld);
    }
    if (SSNFld != null) {
        SSNValue = SSNFld.value;
        SSNLabel = getLabel(SSNFld);
        isValueSSN = isValue(SSNFld);
    }
    if (SSNVerBFld != null) {
        SSNVerBLabel = getLabel(SSNVerBFld);
        if (SSNVerBFld.type == 'checkbox' && SSNVerBFld.checked) {
            SSNVerBValue = 'Y';
        }
    }
    if (TINFld != null) {
        TINValue = TINFld.value;
        TINLabel = getLabel(TINFld);
        isValueTIN = isValue(TINFld);
    }
    if (TINVerBFld != null) {
        TINVerBLabel = getLabel(TINVerBFld);
        if (TINVerBFld.type == 'checkbox' && TINVerBFld.checked) {
            TINVerBValue = 'Y';
        }
    }
    if (SSNVerBValue == 'Y' && !isValueSSN) {
        msg += getMessage("ci.entity.message.value.verified", new Array(SSNLabel, SSNVerBLabel)) + "\n";
    }
    if (TINVerBValue == 'Y' && !isValueTIN) {
        msg += getMessage("ci.entity.message.value.verified", new Array(TINLabel, TINVerBLabel)) + "\n";
    }
    if (vendorVerifySysParamValue == 'Y') {
        if (dftlTaxIDValue == 'SSN' && SSNVerBValue != 'Y') {
            msg += getMessage("ci.entity.message.verified.beforeMaking", new Array(SSNLabel, SSNLabel, dftlTaxIDLabel)) + "\n";
        }
        if (dftlTaxIDValue == 'TIN' && TINVerBValue != 'Y') {
            msg += getMessage("ci.entity.message.verified.beforeMaking", new Array(TINLabel, TINLabel, dftlTaxIDLabel)) + "\n";
        }
    }
    return msg;
}

//-----------------------------------------------------------------------------
// Validate e-mail addresses.
//-----------------------------------------------------------------------------
function validateEMail(eMailFld, label) {

    if (isFieldMasked(eMailFld)) return '';

    if (eMailFld.value == null || eMailFld.value == '') {
        return '';
    }
    // Reg exp for bad e-mail addresses.
    var reNoGood = /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)/;
    // not valid
    // Reg exp for good e-mail addresses.
    var reGood = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    // valid
    if (!reNoGood.test(eMailFld.value) && reGood.test(eMailFld.value)) {
        return '';
    }
    else {
        return getMessage("ci.common.error.format.email", [ label ? label : getLabel(eMailFld.name), "username@domain.topleveldomain"]) + "\n";
    }
}

//-----------------------------------------------------------------------------
// Validate postal code of other country except Canada, USA.
//-----------------------------------------------------------------------------
function validateOtherCountryPostalCode(postalCodeFld) {
    if (isEmpty(postalCodeFld.value) || isFieldMasked(postalCodeFld)) {
        return "";
    }

    var postalCodeRegExp = new RegExp("^[\\s\\S]{0,10}$");

    if (!postalCodeRegExp.test(postalCodeFld.value)) {
        return getMessage("ci.entity.message.postalCode.invalid");
    }
    return "";
}

//-----------------------------------------------------------------------------
// Validate phone number.
//-----------------------------------------------------------------------------
function validatePhoneNumField(phoneNumFld) {
    if (phoneNumFld.value == null || phoneNumFld.value == '') {
        return '';
    }
    if (validatePhoneNumString(phoneNumFld.value)) {
        return '';
    }
    else {
        return getMessage("ci.common.error.format.ssn", new Array(getLabel(phoneNumFld.name), "9999999", "999-9999")) + "\n";
    }
}

//-----------------------------------------------------------------------------
// Validate phone number.
//-----------------------------------------------------------------------------
function validatePhoneNumString(phoneNum) {

    if (isFieldMasked(phoneNum)) return '';

    if (phoneNum == null || phoneNum == '') {
        return true;
    }
    var reTestForNumOnly = /[0-9]{7,7}/;
    var reTestForNumWithDash = /[0-9]{3,3}-[0-9]{4,4}/;
    if ((reTestForNumOnly.test(phoneNum) && phoneNum.length == 7) ||
        (reTestForNumWithDash.test(phoneNum) && phoneNum.length == 8)) {
        return true;
    }
    else {
        return false;
    }
}

//-----------------------------------------------------------------------------
// Validate reference number.
//-----------------------------------------------------------------------------
var refNumValid = 'N';
function validateReferenceNumber(refNum) {

    if (isFieldMasked(refNum)) return '';

    if (refNum.value == null || refNum.value == '') {
        return '';
    }
    var prefix = refNum.value.substring(0, 3);
    // Reg exp for good reference number.
    var reGood = /^...[0-9]{6,6}$/;
    // invalid
    if (getObjectValue("CI_REF_NUM_PREFIX").indexOf(prefix) < 0) {
        return getMessage("ci.common.error.reference.number") + "\n";
    }
    else if (!reGood.test(refNum.value)) {
        return getMessage("ci.common.error.reference.number") + "\n";
    }
    else if (varifyReferenceNumber()) {
        if (refNumValid == 'N'){
            return getMessage("ci.common.error.reference.number") + "\n";
        } else {
            return '';
        }
    }
    else {
        return '';
    }
}

//-----------------------------------------------------------------------------
// Validate SSN.
//-----------------------------------------------------------------------------
function validateSSN(SSNFld, label) {

    if (isFieldMasked(SSNFld)) return '';

    if (SSNFld.value == null || SSNFld.value == '') {
        return '';
    }
    var reTestForNumOnly = /[0-9]{9,9}/;
    var reTestForSSN = /[0-9]{3,3}-[0-9]{2,2}-[0-9]{4,4}/;
    if ((reTestForNumOnly.test(SSNFld.value) && SSNFld.value.length == 9) ||
        (reTestForSSN.test(SSNFld.value) && SSNFld.value.length == 11)) {
        return '';
    }
    else {
        return getMessage("ci.common.error.format.ssn",  [label ? label : getLabel(SSNFld.name), "999999999", "999-99-9999"]) + "\n";
    }
}

//-----------------------------------------------------------------------------
// state validation for addresses
//-----------------------------------------------------------------------------
function validateStateRequired(isValueState, stateLabel,
                               usaAddrBValue) {
    if (usaAddrBValue == 'N') {
        return '';
    }
    else {
        if (!isValueState) {
            return(getMessage("ci.common.error.classCode.required", new Array(stateLabel)) + "\n");
        }
        else {
            return '';
        }
    }
}

//-----------------------------------------------------------------------------
// Validate TIN.
//-----------------------------------------------------------------------------
function validateTIN(TINFld) {

    if (isFieldMasked(TINFld)) return '';

    if (TINFld.value == null || TINFld.value == '') {
        return '';
    }
    var reTestForNumOnly = /[0-9]{9,9}/;
    var reTestForTIN = /[0-9]{2,2}-[0-9]{7,7}/;
    if ((reTestForNumOnly.test(TINFld.value) && TINFld.value.length == 9) ||
        (reTestForTIN.test(TINFld.value) && TINFld.value.length == 10)) {
        return '';
    }
    else {
        return getMessage("ci.common.error.format.ssn", new Array(getLabel(TINFld.name), "999999999", "99-9999999")) + "\n";
    }
}

//-----------------------------------------------------------------------------
// Validate ZipCode.
//-----------------------------------------------------------------------------
function validateZipCode(zipCodeFld) {
    if (isEmpty(zipCodeFld.value) || isFieldMasked(zipCodeFld)) {
        return "";
    }

    if (zipCodeFld.value.length != 5
        || !isSignedFloat(zipCodeFld.value, true)
        || /[^0-9]/.test(zipCodeFld.value)) {
        return (getMessage("ci.entity.message.zipCode.invalid"));
    }

    return "";
}

//-----------------------------------------------------------------------------
// Validate NPI.
//-----------------------------------------------------------------------------
function validateNPI(NPIFld) {

    if (isFieldMasked(NPIFld)) return '';

    if (NPIFld.value == null || NPIFld.value == '') {
        return '';
    }
    var reTestForNumOnly = /[0-9]{10,10}/;

    if ((reTestForNumOnly.test(NPIFld.value) && NPIFld.value.length == 10)) {
        // append to the US NPI prefix.
        fullNPI = '80840'+ NPIFld.value;
        // use the LUHN implementation from scriptlib
        if (isCreditCard(fullNPI)) {
            return '';
        }
        else {
            return getMessage("ci.common.error.format.npi.check.digit") + "\n";
        }
    }
    else {
        return getMessage("ci.common.error.format.npi.numeric") + "\n";
    }
}

//Verify the entered Reference Number after fields changed
function varifyReferenceNumber() {
    var url = getAppPath() + "/ciEntityPersonModify.do?process=validateReferenceNumber";
    new AJAXRequest("post", url, generateReferenceNumberData(), handleVerifiedResult, false);
    return true;
}

// TODO Move to CSCommon.js
function viewPCFTransactionPopup(transactionId, policyTermHistoryId, stateCode) {
    var path = getAppPath() + "/pcfmgr/csPcfViewRemit.do?process=loadViewRemitTransactionDetail&transactionId="
        + transactionId + "&policyTermHistoryId=" + policyTermHistoryId + "&stateCode=" + stateCode;

    if (isDivPopupEnabled()) {
        openDivPopup("", path, true, true, "", "", "1600", "800", "890", "840", "", true);
    }
    else {
        var mainwin = window.open(path, 'PCF Remittance Transaction Detail Popup',
            'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
        mainwin.focus();
    }
    return;
}

//
// Create total amount row in the grid.This function only supports read-only
// grid but doesn't support adding/deleting rows in grid dynamically.
// Parameters:
// gridId  - Grid name to be processed
// columns - Column name(s) need to display the total amount in the grid.
//           Could be an arry(for multiple columns) or string(for single column)
//           The name must be matched to the column name in Java but not the real
//           name in the generated grid.
//
function writeTotalAmountInGrid (gridId, columns) {
    var totalText = "Total ";
    var columnNames;
    if (columns.constructor.toString().indexOf("Array") > 0) {
        columnNames = columns;
    } else if (columns.constructor.toString().indexOf("String") > 0) {
        columnNames = [columns];
    } else {
        alert(getMessage("cs.invoke.error.parameter.invalid"));
        return;
    }
    for (i = 0; i < columnNames.length; i++) {
        var columnHeader;
        try {
            columnHeader = getSingleObject("HC" + columnNames[i].toUpperCase()).innerText;
        } catch (error) {
            columnHeader = getSingleObject("HC" + columnNames[i].toUpperCase() + "L").innerText;
        }
        totalText += columnHeader + " : " + formatMoneyStrValAsObj('' + getTotalAmount(gridId, columnNames[i])) + "       ";
    }

    var gridDiv = "DIV_" + gridId;
    if (getSingleObject(gridDiv)) {
        var table = document.createElement("table");
        var tbody = document.createElement("tbody");
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        table.width = "100%";
        td.align = "center";

        var amount = document.createElement("span");
        amount.className = "oasis_formlabelreq";
        amount.id = "amountLABEL";
        amount.innerText = totalText;
        td.appendChild(amount);
        tr.appendChild(td);
        tbody.appendChild(tr);
        table.appendChild(tbody);
        getSingleObject(gridDiv).parentElement.appendChild(table);
    }
}
