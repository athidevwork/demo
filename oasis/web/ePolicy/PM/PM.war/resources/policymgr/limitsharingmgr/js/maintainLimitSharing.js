//-----------------------------------------------------------------------------
// Javascript file for maintainLimitSharing.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 16, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/16/2010       syang      Issue 110383 - If system determines the shared detail grid is changed while adding share group/detail,
//                             system saves the changes and then refreshes page and continues to add share group/detail.
// 08/23/2010       syang      Issue 108651 - Modified handleOnChange() and added handleIndicatorInShareDetail() to handle Renew indicator.
// 01/28/2011       fcb        117193 - document.all(gridDetailDivId) replaced with hasObject(gridDetailDivId)
// 08/14/2012       adeng      135972 - Modified handleOnButtonClick() to alert warning message when click on add button if
//                                      there is no risk/voverage available
// 08/24/2012       adeng      135972 - Reverted the previous change and using new solution.Modified sharedGroupListGrid_setInitialValues()
//                                      to Pass in one more parameter 'hasRisk'.
// 08/31/2012       adeng      135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
// 10/15/2013       adeng      145622 - Modified sharedDetailListGrid_setInitialValues() to pass in one more parameter
//                                      'shareDtlRiskEffectiveToDate' which is effective to date of risk.
// 12/05/2014       awu        158561 - Modified addSharedDetails to remove the row number setting.
// 01/08/2015       awu        157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                      to wrap the multiple rows deleting.
// 08/24/2015       eyin       165581 - Added a variable 'skipSubmitSaveB', to avoid calling saveAllLimitSharing.do
//                                      again and again when allLimitSharing data validation is failed.
// 11/01/2016       eyin       180850 - Modified submitSave(), add removeMessages() if there is no Data change on
//                                      the screen when the 'SAVE' button is clicked on.
// 03/10/2017       wli        180675 - Initialized the field "autoSaveResultType" and added syncResultToParent()
//                                      call in the method named "submitSave()" for UI change.
// 05/23/2017       lzhang     185079 - pass parameter when call getParentWindow()
// 07/12/2017       lzhang     186847 - Reflect grid replacement project changes
// 11/16/2018       xnie       196951 - Modified sharedDetailListGrid_setInitialValues to add escape for
//                                      ShareDtlCoverageShortDesc.
//-----------------------------------------------------------------------------
var sharedDetailRecord;
var shareGroupMasterId;
var currentGroupNo;
var skipSubmitSaveB = false;

function validateGrid() {
    return true;
}

function submitSave() {
    alternateGrid_update('sharedGroupListGrid');
    alternateGrid_update('sharedDetailListGrid');
    //setInputFormField("processCode", 'SAVE');
    if (!isChanged && !isPageGridsDataChanged()) {
		syncResultToParent(commonOnSubmitReturnTypes.noDataChange);
        removeMessages();
        return;
    }
    proceed = commonValidateGrid('sharedGroupListGrid');
    if (proceed) {
        document.forms[0].action = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?" + "&process=saveAllLimitSharing";
        showProcessingDivPopup();
		autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
        submitFirstForm();
    }
    else {
		autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
        removeMessages();
    }

	syncResultToParent(autoSaveResultType);
}

function sharedGroupListGrid_selectRow(id) {
    shareGroupMasterId = id;
    setTableProperty(eval("sharedDetailListGrid"), "selectedTableRowNo", null);
    sharedDetailListGrid_filter("CSHAREDTLGROUPMASTERID=" + id);
    var detailXmlData = getXMLDataForGridName("sharedDetailListGrid");
    if (isEmptyRecordset(detailXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(detailXmlData));
        hideGridDetailDiv("sharedDetailDetailDiv");
    }
    else {
        showNonEmptyTable(getTableForXMLData(detailXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("sharedDetailDetailDiv"), false);
    }
}

function sharedGroupListGrid_setInitialValues() {
    var shareGroupNo = parseInt(currentGroupNo) + parseInt(1);
    var path = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForSharedGroup" + "&shareGroupNo=" + shareGroupNo;
    new AJAXRequest("get", path, '', setInitialValuesForSharedGroup, false);
}

function handleOnChange(field) {
    if (field.name == 'shareGroupEffToDate') {
        var currentSharedGroupGrid = getXMLDataForGridName("sharedGroupListGrid");
        var origShareGroupEffToDate = currentSharedGroupGrid.recordset("CORIGSHAREGROUPEFFTODATE").value;
        var modifedGroupEffToDate = field.value;
        if ((getRealDate(modifedGroupEffToDate)) < (getRealDate(origShareGroupEffToDate))) {
            if (confirm(getMessage("pm.maintainLimitSharing.modifyDetailExpDate.warning"))) {
                xPath = "//ROW[(UPDATE_IND != 'D') and (CSHAREDTLGROUPMASTERID= '" + shareGroupMasterId + "')]";
                sharedDetailRecords = sharedDetailListGrid1.documentElement.selectNodes(xPath);
                for (i = 0; i < sharedDetailRecords.length; i++) {
                    currentRecord = sharedDetailRecords.item(i);
                    detailDate = currentRecord.selectNodes("CSHAREDTLEFFTODATE")(0).text;
                    if (getRealDate(modifedGroupEffToDate) < getRealDate(detailDate)) {
                        currentRecord.selectNodes("CSHAREDTLEFFTODATE")(0).text = modifedGroupEffToDate;
                        currentRecord.selectNodes("UPDATE_IND")(0).text = "Y";
                        handleIndicatorInShareDetail(currentRecord, modifedGroupEffToDate);
                    }
                }
                currentSharedGroupGrid.recordset("CORIGSHAREGROUPEFFTODATE").value = modifedGroupEffToDate;
                // The shared detail grid has been changed, system should enable/disable current selected row.
                enableDisableRenewIndicator(field.value, policyHeader.termEffectiveToDate, "shareDtlRenewalB", "isRenewalBAvailable","sharedDetailListGrid");
            }
            else {
                document.forms[0].shareGroupEffToDate.value = origShareGroupEffToDate;
            }
        }
        // Issue 108651, handle Renew indicator for Shared Groups.
        enableDisableRenewIndicator(field.value, policyHeader.termEffectiveToDate, "renewalB", "isRenewalBEditable", "sharedGroupListGrid");
    }
    else if (field.name == "shareDtlEffToDate") {
        // Issue 108651, handle Renew indicator for Shared Group Details.
        enableDisableRenewIndicator(field.value, policyHeader.termEffectiveToDate, "shareDtlRenewalB", "isRenewalBAvailable","sharedDetailListGrid");
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_GROUP':
            if(invokeSaveBeforeAdd(asBtn)){
                return ;
            }
            sharedGroupRecords = sharedGroupListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (UPDATE_IND != 'D')]");
            if (sharedGroupRecords.length > 0) {
                currentGroupNo = sharedGroupRecords.item(sharedGroupRecords.length - 1).selectNodes("CSHAREGROUPNO")(0).text;
            }
            else {
                currentGroupNo = 0;
            }
            commonAddRow("sharedGroupListGrid");
            break;
        case 'ADD_DETAIL':
            // do pre-Insert validations
            if(invokeSaveBeforeAdd(asBtn)){
                return ;
            }
            var currentSharedGroupGrid = getXMLDataForGridName("sharedGroupListGrid");
            var shareLimitB  = currentSharedGroupGrid.recordset("CSHARELIMITB").value;
            var shareDeductB = currentSharedGroupGrid.recordset("CSHAREDEDUCTB").value;
            var shareSirB    = currentSharedGroupGrid.recordset("CSHARESIRB").value;
            var policySharedGroupMasterId = currentSharedGroupGrid.recordset("ID").value;

            var url = getAppPath() + "/policymgr/limitsharingmgr/selectSharedDetail.do?"
                    + commonGetMenuQueryString() + "&shareLimitB=" + shareLimitB
                    + "&shareDeductB=" + shareDeductB
                    + "&shareSirB=" + shareSirB
                    + "&policySharedGroupMasterId=" + policySharedGroupMasterId;
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'DELETE_GROUP':
            var currentSharedGroupGrid = getXMLDataForGridName("sharedGroupListGrid");
            shareDtlGroupMasterId = currentSharedGroupGrid.recordset("ID").value;
            var shareGroupDesc = currentSharedGroupGrid.recordset("CSHAREGROUPDESC").value;
            var cascadeDelete = "Y";

            sharedDetailRecords = sharedDetailListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (UPDATE_IND != 'D') and (CSHAREDTLGROUPMASTERID= '" + shareDtlGroupMasterId + "')]");
            if (sharedDetailRecords.length > 0) {
                var sysParm = getSysParmValue("PM_SHR_GRPS_NODELDTL");

                if (!isEmpty(sysParm)) {
                    if (sysParm.indexOf(shareGroupDesc) > -1) {
                        cascadeDelete = "N";
                    }
                }

                if (cascadeDelete == "Y") {
                    if (confirm(getMessage("pm.maintainLimitSharing.deleteDetailsAndGroup.warning"))) {

                        var SharedGroupDetailGrid1 = getXMLDataForGridName("sharedDetailListGrid");
                        beginDeleteMultipleRow("sharedDetailListGrid");
                        first(SharedGroupDetailGrid1);
                        while (!SharedGroupDetailGrid1.recordset.eof) {
                            if (SharedGroupDetailGrid1.recordset("CSHAREDTLGROUPMASTERID").value == shareDtlGroupMasterId) {
                                setSelectedRow("sharedDetailListGrid", SharedGroupDetailGrid1.recordset("ID").value);
                                sharedDetailListGrid_deleterow();
                            }
                            next(SharedGroupDetailGrid1);
                        }
                        first(SharedGroupDetailGrid1);
                        endDeleteMultipleRow("sharedDetailListGrid");

                        var gridId = "sharedGroupListGrid";
                        sharedGroupListGrid_deleterow();
                        setTableProperty(eval("sharedDetailListGrid"), "selectedTableRowNo", null);

                    }

                } else {
                    handleError(getMessage("pm.maintainLimitSharing.deleteSharedGroup.error"));
                }
            }
            else {
                var gridId = "sharedGroupListGrid";
                commonDeleteRow(gridId);
                setTableProperty(eval("sharedDetailListGrid"), "selectedTableRowNo", null);
            }
            break;
        case 'DELETE_DETAIL':
            var gridId = "sharedDetailListGrid";
            commonDeleteRow(gridId);
            setTableProperty(eval("sharedDetailListGrid"), "selectedTableRowNo", null);
            break;
    }
}
//-----------------------------------------------------------------------------
// Add shared details
//-----------------------------------------------------------------------------
function addSharedDetails(oSharedDetailList, showMessage) {
    var size = oSharedDetailList.length;
    var addedCount = 0;
    for (var i = 0; i < size; i++) {
        //sharedDetailRecord will be used in setInitialValues.
        sharedDetailRecord = oSharedDetailList.item(i);
        commonAddRow("sharedDetailListGrid");
    }
}

function sharedDetailListGrid_setInitialValues() {
    var currentSharedGroupGrid = getXMLDataForGridName("sharedGroupListGrid");
    shareDtlGroupMasterId = currentSharedGroupGrid.recordset("ID").value;
    shareGroupEffToDate = currentSharedGroupGrid.recordset("CSHAREGROUPEFFTODATE").value;
    shareGroupRenewalB = currentSharedGroupGrid.recordset("CRENEWALB").value;
    shareDtlRiskName = sharedDetailRecord.selectNodes("CSHAREDTLRISKNAME")(0).text;
    ShareDtlCoverageShortDesc = escape(sharedDetailRecord.selectNodes("CSHAREDTLCOVERAGESHORTDESC")(0).text);
    shareDtlOwnerB = sharedDetailRecord.selectNodes("CSHAREDTLOWNERB")(0).text;
    if (shareDtlOwnerB == "-1") {
        shareDtlOwnerB = "Y";
    }
    else {
        shareDtlOwnerB = "N";
    }
    shareDtlSharedLimitB = sharedDetailRecord.selectNodes("CSHAREDTLSHAREDLIMITB")(0).text;
    shareDtlRiskPrimaryInd = sharedDetailRecord.selectNodes("CSHAREDTLRISKPRIMARYIND")(0).text;
    shareDtlEntityType = sharedDetailRecord.selectNodes("CSHAREDTLRISKENTITYTYPE")(0).text;
    shareDtlCoverageSequenceNo = sharedDetailRecord.selectNodes("CSHAREDTLCOVERAGESEQUENCENO")(0).text;
    shareDtlPracticeStateCode = sharedDetailRecord.selectNodes("CSHAREDTLPRACTICESTATECODE")(0).text;
    shareDtlCoverageBaseRecordId = sharedDetailRecord.getAttributeNode("id").text;
    shareDtlRiskBaseRecordId = sharedDetailRecord.selectNodes("CRISKBASERECORDID")(0).text;
    shareDtlRiskEffectiveToDate = sharedDetailRecord.selectNodes("CEFFECTIVETODATE")(0).text;
    // System shold avoid passing the sharedDtlRiskName since it maybe contain special char '&' and blank string.
    sharedDetailListGrid1.recordset("CSHAREDTLRISKNAME").value = shareDtlRiskName;

    var url = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?" + commonGetMenuQueryString() +
            "&shareDtlGroupMasterId=" + shareDtlGroupMasterId +
            "&shareGroupEffToDate=" + shareGroupEffToDate +
            "&renewalB=" + shareGroupRenewalB +
            "&ShareDtlCoverageShortDesc=" + ShareDtlCoverageShortDesc +
            "&shareDtlOwnerB=" + shareDtlOwnerB +
            "&shareDtlSharedLimitB=" + shareDtlSharedLimitB +
            "&shareDtlRiskPrimaryInd=" + shareDtlRiskPrimaryInd +
            "&shareDtlEntityType=" + shareDtlEntityType +
            "&shareDtlCoverageSequenceNo=" + shareDtlCoverageSequenceNo +
            "&shareDtlPracticeStateCode=" + shareDtlPracticeStateCode +
            "&shareDtlCoverageBaseRecordId=" + shareDtlCoverageBaseRecordId +
            "&shareDtlRiskBaseRecordId=" + shareDtlRiskBaseRecordId +
            "&shareDtlRiskEffectiveToDate=" + shareDtlRiskEffectiveToDate +
            "&process=getInitialValuesForSharedDetail";
    // initiate async call
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function setInitialValuesForSharedGroup(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("sharedGroupListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}

function loadSeparateLimits() {
    var url = getAppPath() + "/policymgr/limitsharingmgr/maintainLimitSharing.do?" + commonGetMenuQueryString() + "&process=loadAllSeparateLimit";
    getObject("iframeSeparateLimits").src = url;
}
//-----------------------------------------------------------------------------
// Overwrite hideShowForm
//-----------------------------------------------------------------------------
function hideShowForm() {
    // Get the currently selected grid
    var currentGrid = "sharedGroupListGrid";
    var currentTbl = getTableForXMLData(getXMLDataForGridName(currentGrid));
    var gridDetailDivId = getTableProperty(currentTbl, "gridDetailDivId");

    if (isStringValue(gridDetailDivId)) {
        if (hasObject(gridDetailDivId))
            hideShowElementByClassName(getSingleObject(gridDetailDivId),
                    ( (getTableProperty(currentTbl, "hasrows") == true)) ? false : true);
    }
}
//for goto error
function getParentGridId() {
    return "sharedGroupListGrid";
}

function getChildGridId() {
    return "sharedDetailListGrid";
}
//load iframe
function handleOnLoad() {
    loadSeparateLimits();
    // If teh selectedSharedGroupId is not empty, system selects this shared group.
    var selectedSharedGroupId = getObjectValue("selectedSharedGroupId");
    if (!isEmpty(selectedSharedGroupId)) {
        selectRowById("sharedGroupListGrid", selectedSharedGroupId);
    }
    // If the limitShareType is not empty, system continues to add shared group/detail.
    var limitShareType = getObjectValue("limitShareType");
    if (!isEmpty(limitShareType)) {
        skipSubmitSaveB = true;
        handleOnButtonClick(limitShareType);
    }
}

//-----------------------------------------------------------------------------
// If system determines the shared detail grid is changed while adding share group/detail,
// system saves the changes and then refreshes page and continues to add share group/detail.
//-----------------------------------------------------------------------------
function invokeSaveBeforeAdd(addType) {
    var invokedB = false;
    if(skipSubmitSaveB){
        skipSubmitSaveB = false;
        return invokedB;
    }
    if (isGridDataChanged("sharedDetailListGrid")) {
        if (isTabStyle()) {
            getParentWindow(true).removeMessagesForFrame();
        }
        if (addType == 'ADD_DETAIL') {
            var currentSharedGroupGrid = getXMLDataForGridName("sharedGroupListGrid");
            var selectedSharedGroupId = currentSharedGroupGrid.recordset("ID").value;
            setInputFormField("selectedSharedGroupId", selectedSharedGroupId);
        }
        setInputFormField("addForType", addType);
        submitSave();
        invokedB = true;
    }
    return invokedB;
}
//-----------------------------------------------------------------------------
// When change the Expiration Date in shared group grid, the indicator should be changed in shared detail grid.
//-----------------------------------------------------------------------------
function handleIndicatorInShareDetail(record, effectiveToDate) {
    var expirationDate = policyHeader.termEffectiveToDate;
    if (record.selectNodes("CSHAREDTLRENEWALB") && isValueDate(effectiveToDate) && isValueDate(expirationDate)) {
        if (getRealDate(effectiveToDate) < getRealDate(expirationDate)) {
            record.selectNodes("CSHAREDTLRENEWALB")(0).text = "N";
            record.selectNodes("CSHAREDTLRENEWALBLOVLABEL")(0).text = "No";
            record.selectNodes("CISRENEWALBAVAILABLE")(0).text = "N";
        }
        else {
            record.selectNodes("CSHAREDTLRENEWALB")(0).text = "Y";
            record.selectNodes("CSHAREDTLRENEWALBLOVLABEL")(0).text = "Yes";
            record.selectNodes("CISRENEWALBAVAILABLE")(0).text = "Y";
        }
    }
}