//-----------------------------------------------------------------------------
// Javascript file for maintainCoi.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 24, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/24/2010       syang       Issue 108651 - Modified handleOnChange() to handle Renew indicator when change effective to date.
// 12/22/2010       dzhang      Issue 103792 - Modified addCoi(): System decided select COI Holder by Search CIS or Select Client by system parameter's value.
// 11/28/2011       ryzhao      Issue 127626 - Added handlePostAddRow().
// 12/02/2011       wfu         Issue 127703 - Modified handleOnChange to disable Renew_B indicator if risk status is Cancel.
// 01/04/2012       wfu         Issue 127802 - Modified sendAJAXRequest and handleOnGetInitialValuesForCoi for correct entity name.
// 04/03/2012       lmjiang     Issue 128983 - Encode the value of parameter note as there may char '&' exists.
// 08/02/2012       adeng       Issue 135702 - Modified sendAJAXRequest to pass in the selected risk's risk id.
// 08/17/2012       xnie        Issue 120683 - Added setAddressChanges() to set address changes to input form field.
// 03/10/2014       adeng       Issue 152221 - Because we changed anchorColumnName from 'coiHolderId' to 'rowNum', we
//                                             should replace 'ID' with 'CCOIHOLDERID' in some places as follow,
//                                             1) handleOnButtonClick().
//                                             2) sendAJAXRequest().
// 07/29/2014       kxiang      Issue 155534 - 1)Modified function handlePostAddRow(), add Handle the url of COI name
//                                               column.
//                                             2)Modified function handleOnGetInitialValuesForCoi to set initial value
//                                               for coiNameHref.
// 08/13/2014       kxiang      Issue 155534 - Modified function handleOnGetInitialValuesForCoi to call common function
//                                             commonHandleOnGetInitialValues.
// 10/09/2014       wdang       Issue 156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 10/21/2014       fcb         Issue 158505 - validateCoiSelection: replaced ID with CCOIHOLDERID due to a previous
//                                             change in the logic to set the anchor column.
// 12/30/2014       jyang       Issue 157750 - Modified sendAJAXRequest() to encode coiEntityName before append to URL.
// 11/11/2016       eyin        Issue 181097 - 1) Add two JS level variables riskEffectiveFromDate and riskEffectiveToDate
//                                             2) Set the value for the 2 new added JS variables in addCoi().
//                                             3) Modified sendAJAXRequest(), to use JS variable riskEffectiveFromDate
//                                                && riskEffectiveToDate directly in string variable 'url'.
//                                             4) Modified handleOnChange(), rename the variable name, make them different
//                                                than the name of new JS level variables.
// 03/10/2017       wrong       Issue 180675 - 1) Added invokeWorkflow() case for new UI tab style.
//                                             2) Modified code to open div popups in primary page in new UI tab style.
//                                             3) Override lookupEntity function.
// 05/23/2017       lzhang      Issue 185079 - pass parameter when call getParentWindow()
// 07/31/2018       mlm         Issue 193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
// 11/02/2018       clm         Issue 195889 - Add addSelectAllEnabled in handleGetCustomPageOptions to disable the
//                                             checkbox in the grid header
//-----------------------------------------------------------------------------
var selectToGenerateCoiIds = "";
var riskEffectiveFromDate = "";
var riskEffectiveToDate = "";

function handleOnLoad() {
    if (isDefined(getObject("maintainCoiListGrid1"))) {
        $.when(dti.oasis.grid.getLoadingPromise("maintainCoiListGrid")).then(function () {
            // disable selectAll checkbox
            if (!window["useJqxGrid"] && !isEmptyRecordset(maintainCoiListGrid1.recordset)) {
                var nodes = maintainCoiListGrid1.documentElement.selectNodes("//ROW[CISGENERATEAVAILABLE='N']");
                if (nodes.length > 0 && hasObject("HCSELECT_IND")) {
                    getObject("HCSELECT_IND").disabled = true;
                }
            }
        });
    }
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if (isExeInvokeWorkFlow()) {
        invokeWorkflow();
    }
}

function maintainCoiForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
    first(maintainCoiListGrid1);
    selectFirstRowInGrid("maintainCoiListGrid");
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_COI':
            addCoi();
            break;
        case 'GENERATE':
            generateCoi();
            break;
        case "SELECT_ADDRESS":
            var keyId = maintainCoiListGrid1.recordset("CCOIHOLDERID").value;
            if (hasObject("sourceIdField")) {
                var sourceIdField = getObjectValue("sourceIdField");
                if (sourceIdField == "entityRoleId") {
                   keyId = maintainCoiListGrid1.recordset("CENTITYROLEID").value;
                }
            }
            var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=COIHOLDER"
                + "&entityId=" + maintainCoiListGrid1.recordset("CRISKENTITYID").value
                + "&entityRoleId=" + keyId;
            var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", selAddrUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = false;

    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllCoiHolder";
            var needToCaptureTransaction = "N";
            if (getObject("needToCaptureTransaction")) {
                needToCaptureTransaction = getObjectValue("needToCaptureTransaction");
            }
            if (needToCaptureTransaction == "Y") {
                captureTransactionDetails("ENDCOIHOLD", "submitForm");
            }
            else {
                proceed = true;
            }
            break;
    }

    return proceed;
}

//-----------------------------------------------------------------------------
// Add COI Holder
//-----------------------------------------------------------------------------
function addCoi() {
    setInputFormField("coiEntityId", 0);
    setInputFormField("coiEntityName", "");

    riskEffectiveFromDate = getObjectValue("riskEffectiveFromDate");
    riskEffectiveToDate = getObjectValue("riskEffectiveToDate");

    var isCisDesired = getSysParmValue("PM_COI_CS_SEARCH");
    if (isEmpty(isCisDesired) || (isCisDesired == 'Y')) {
        openEntitySelectWinFullName("coiEntityId", "coiEntityName", "handleOnSelectCoiEntity()");
    }
    else {
        var entityClassCode = getSysParmValue("PM_COI_CS_ROLES");
        if (isEmpty(entityClassCode)) {
            entityClassCode = 'COI_HOLDER';
        }
        lookupEntity(entityClassCode, policyHeader.termEffectiveFromDate,
                'coiEntityId', 'coiEntityName', 'handleOnSelectCoiEntity()', getParentWindow(true).subFrameId);
    }
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleOnSelectCoiEntity() {
    commonAddRow(getCurrentlySelectedGridId());
}

function maintainCoiListGrid_setInitialValues() {
    sendAJAXRequest("getInitialValuesForCoi");

    // Set entity risk ID
    maintainCoiListGrid1.recordset("CENTITYRISKID").value = getObjectValue("entityId");
}

function sendAJAXRequest(process) {
    // set url
    var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
        + commonGetMenuQueryString() + "&process=" + process;
    if (!riskEffectiveFromDate)
        riskEffectiveFromDate = $(getObject("riskEffectiveFromDate")).attr("value");
    if (!riskEffectiveToDate)
        riskEffectiveToDate = $(getObject("riskEffectiveToDate")).attr("value");

    switch (process) {
        case 'getInitialValuesForCoi':
            url += "&riskEffectiveFromDate=" + riskEffectiveFromDate
                   + "&riskEffectiveToDate=" + riskEffectiveToDate
                   + "&riskEntityId=" + getObjectValue("coiEntityId")
                   + "&coiName=" + encodeURIComponent(getObjectValue("coiEntityName"))
                   + "&riskId=" + policyHeader.riskHeader.riskId;
            break;
        case 'getNoteByNoteCode':
            url += "&coiHolderId=" + maintainCoiListGrid1.recordset("CCOIHOLDERID").value +
                   "&entityRoleId=" + maintainCoiListGrid1.recordset("CENTITYROLEID").value +
                   "&transactionLogId=" + maintainCoiListGrid1.recordset("CTRANSACTIONLOGID").value +
                   "&riskBaseRecordIdId=" + maintainCoiListGrid1.recordset("CRISKBASERECORDID").value +
                   "&noteCode=" + getObjectValue("noteCode") +
                   "&note=" + encodeURIComponent(getObjectValue("note"));
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnGetInitialValuesForCoi(ajax) {
    commonHandleOnGetInitialValues(ajax, "COINAMEHREF");
}

function handleOnGetNoteByNoteCode(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setInputFormField("note", oValueList[0]["note"]);
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To validate if no COI is selected
//-----------------------------------------------------------------------------
function validateCoiSelection() {
    var selectToGenerateCoiIds = "";
    var isSelected = false;
    if (!isEmptyRecordset(maintainCoiListGrid1.recordset)) {
        first(maintainCoiListGrid1);
        while (!maintainCoiListGrid1.recordset.eof) {
            var isGenerate = maintainCoiListGrid1.recordset("CSELECT_IND").value;
            if (isGenerate == "-1") {
                isSelected = true;
                selectToGenerateCoiIds += maintainCoiListGrid1.recordset("CCOIHOLDERID").value + ",";
            }
            next(maintainCoiListGrid1);
        }
        first(maintainCoiListGrid1);
        if (selectToGenerateCoiIds.length > 0) {
            selectToGenerateCoiIds = selectToGenerateCoiIds.substring(0, selectToGenerateCoiIds.length - 1);
            setInputFormField("selectToGenerateCoiIds", selectToGenerateCoiIds);
        }
        if (!isSelected) {
            handleError(getMessage("pm.generateCoi.noselection.error"));
        }
    }
    return isSelected;
}

//-----------------------------------------------------------------------------
// To generate COI
//-----------------------------------------------------------------------------
function generateCoi() {
    // First check if no COI's selected
    if (validateCoiSelection()) {
        captureAsOfDate();
    }
}

function captureAsOfDate() {
    var url = getAppPath() + "/riskmgr/coimgr/captureCoiAsOfDate.do?"
        + commonGetMenuQueryString() + "&process=display";
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "500", "400", "", "", "", false);
}

function handleOnCaptureAsOfDate(asOfDate) {
    // set asOfDate into a input field
    setInputFormField("coiAsOfDate", asOfDate);
    // capture COI Claim History
    var pmCoiClaimsParam = "N";
    if (getObject("pmCoiClaimsParam")) {
        pmCoiClaimsParam = getObjectValue("pmCoiClaimsParam");
    }
    if (pmCoiClaimsParam == "Y") {
        captureCoiClaimHistory();
    }
    else {
        submitToGenerateAllCoi();
    }
}

function captureCoiClaimHistory() {
    var url = getAppPath() + "/riskmgr/coimgr/captureCoiClaimHistory.do?"
        + commonGetMenuQueryString() + "&process=display";
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "500", "400", "", "", "", false);
}

function handleOnCaptureCoiClaimHistory(coiSelectLetter, coiIncludeExcludeClaim, coverageType, claimType, paymentType, coiCutoffDate) {
    // set coi claim history data values into input fields
    setInputFormField("coiSelectLetter", coiSelectLetter);
    setInputFormField("coiIncludeExcludeClaim", coiIncludeExcludeClaim);
    setInputFormField("coverageType", coverageType);
    setInputFormField("claimType", claimType);
    setInputFormField("paymentType", paymentType);
    setInputFormField("coiCutoffDate", coiCutoffDate);

    // Submit the page to generate COI
    submitToGenerateAllCoi();
}

function submitToGenerateAllCoi() {
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    document.forms[0].process.value = "generateAllCoi";
    submitFirstForm();
}

function handleReadyStateReady(table) {
    if (table.id == "maintainCoiListGrid")
        initGridSelectCheckbox();
}

function handlePostAddRow(table) {
    if (table.id == "maintainCoiListGrid") {
        var absolutePosition = maintainCoiListGrid1.recordset.AbsolutePosition;
        initGridSelectCheckbox();
        first(maintainCoiListGrid1);
        maintainCoiListGrid1.recordset.move(absolutePosition - 1);

        var xmlData = getXMLDataForGridName("maintainCoiListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var cOINameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CCOINAME") {
                cOINameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + cOINameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("COINAMEHREF"))) {
                    href = "javascript:handleOnGridHref('maintainCoiListGrid', '" + getObjectValue("COINAMEHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}
//-----------------------------------------------------------------------------
// To disable/enable Select to Generate checkbox
//-----------------------------------------------------------------------------
function initGridSelectCheckbox() {
    // Do nothing if the table is empty
    if (!getTableProperty(maintainCoiListGrid, "hasrows")) {
        return;
    }

    var disableChkSelectAll = true;
    // When there is pagination, not all records are in table. So move to the proper record first.
    resetRecordPointerToFirstRowInGridCurrentPage(maintainCoiListGrid);

    if(!window["useJqxGrid"]) {
        // Initialize the select check boxes in table
        //var isGenerateAvailable = getObjectValue('isGenerateAvailable');
        var XMLData = maintainCoiListGrid1;
        var chkSelArray = document.getElementsByName("chkCSELECT_IND");
        var size = chkSelArray.length;
        if (!isEmptyRecordset(XMLData.recordset)) {
            for (var i = 0; i < size; i++) {
                var isGenerateAvailable = XMLData.documentElement.selectNodes("//ROW").item(0).selectNodes("CISGENERATEAVAILABLE")(0).text;
                chkSelArray[i].disabled = isGenerateAvailable == "N";
                next(XMLData);
            }
        }
        // Move back to where we started
        resetRecordPointerToFirstRowInGridCurrentPage(maintainCoiListGrid);
    }
}

function handleOnChange(obj) {
    if (obj.name == "noteCode") {
        sendAJAXRequest("getNoteByNoteCode");
    }
    //change renew indicator if effective to date is less than risk expiration
    if (obj.name == "effectiveToDate") {
        var coiExpDate = obj.value;
        var riskEffectiveToDateObjVal = getObjectValue("riskEffectiveToDate");
        enableDisableRenewIndicator(coiExpDate, riskEffectiveToDateObjVal, "renewB", "isRenewBAvailable", "maintainCoiListGrid");

        // If risk is flat cancelled, disable the renew_b indicator.
        if (isValueDate(coiExpDate) && isValueDate(riskEffectiveToDate) && coiExpDate == riskEffectiveToDate) {
            var riskEffectiveFromDateObjVal = getObjectValue("riskEffectiveFromDate");
            if (isValueDate(riskEffectiveFromDateObjVal) && coiExpDate == riskEffectiveFromDateObjVal) {
                maintainCoiListGrid1.recordset("CISRENEWBAVAILABLE").value = 'N';
                maintainCoiListGrid1.recordset("CRENEWB").value = 'N';
                pageEntitlements(true, "maintainCoiListGrid");
            }
        }
    }
    return true;
}

function handleExitWorkflow(policyNo) {
    var riskId = policyHeader.riskHeader.riskId;
    var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
                    + commonGetMenuQueryString() + "&process=loadAllCoiHolder"
                    + "&riskId=" + riskId;
    setWindowLocation(url);
}

//-----------------------------------------------------------------------------
// To set address changes to input form field
//-----------------------------------------------------------------------------
function setAddressChanges(addressInfo) {
    maintainCoiListGrid1.recordset("CADDRESSCHANGES").value = addressInfo;
    if (maintainCoiListGrid1.recordset("ID").value <= -3000) {
        maintainCoiListGrid1.recordset("UPDATE_IND").value = "I";
    }
    else
    {
        maintainCoiListGrid1.recordset("UPDATE_IND").value = "Y";
    }
}

/*
 ** Overwrite lookupEntity with additional field in URL
 */
function lookupEntity(entityClassCode, effectiveFromDate, entityIdFieldName, entityNameFieldName, eventHandler, subFrameId) {
    entitylookupEntityIdFieldName = entityIdFieldName;
    entitylookupEntityNameFieldName = entityNameFieldName;
    entityLookupEventHandler = eventHandler;

    var path = getAppPath() +
            "/entitymgr/lookupEntity.do?entityClassCode=" + entityClassCode +
            "&effectiveFromDate=" + effectiveFromDate;
    if (isTabStyle()) {
        path += "&subFrameId=" + subFrameId;
    }
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, null, null, "", "", "", "", "lookupEntity", false);
}

function handleGetCustomPageOptions() {
    function __isCellEditable (jqxRowIndex, datafield, columntype, value) {
        var isGenerateAvailable = dti.oasis.grid.getRowDataByJqxRowIndex("maintainCoiListGrid", jqxRowIndex)["CISGENERATEAVAILABLE"];
        return (isGenerateAvailable !== "N");

    }

    function __isSelectAllEnabled(gridInfo) {
        if (gridInfo.id == "maintainCoiListGrid") {
            var nodes = gridInfo["data"]["rawData"].filter(function (node) {
                return node["CISGENERATEAVAILABLE"] == "N";
            });
            return !(nodes.length > 0);
        }
    }

    return dti.oasis.page.newCustomPageOptions()
            .addSelectAllEnabledFunction("maintainCoiListGrid", __isSelectAllEnabled)
            .cellBeginEdit("maintainCoiListGrid", "CSELECT_IND", __isCellEditable)
            .addIsCellEditableFunction("maintainCoiListGrid", "CSELECT_IND", __isCellEditable);
}

