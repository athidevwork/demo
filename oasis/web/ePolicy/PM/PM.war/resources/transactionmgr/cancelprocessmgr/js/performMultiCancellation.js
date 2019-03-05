//-----------------------------------------------------------------------------
// Javascript file for performMultiCancellation.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 10, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/10/2010       syang       107032 - Modified cancelList_btnClick() to move to the first row after update node.
// 08/24/2010       dzhang      110567 - Modified filterCancelItem() to add hideEmptyTable&showNonEmptyTable logic.
// 01/14/2011       syang       105832 - Modified handleOnChange() to handle discipline decline list.
// 05/26/2011       fcb         119027 - Modified performMultiCancellation() to validate if rows were selected at this level.
//                                     - Added call to getChanges()
// 08/10/2011       syang       123994 - Modified handleOnChange() to hide the DDL form for non risk cancel level.
// 08/18/2011       syang       121201 - 1) Modified handleOnLoad() and added handleDDLFields() to handle DDL fields.
//                                       2) Change the flow of multi cancellation for risk/coverage/coverage class/component,
//                                       added handleOnMultiCancelConfirm() and getUrlForViewMultiCancelConfirmation() to process
//                                       multi cancel confirmation.
// 11/04/2011       syang       126448 - Modified cancelListGrid_selectRow() to default and sync cancel type/reason/method for checked row.
// 11/10/2011       syang       126447 - Move the function showHideTableColumn() to common.js.
// 11/11/2011       syang       127136 - Modified handleOnLoad() and performMultiCancellation() to handle COI cancellation.
// 11/22/2011       syang       127402 - Removed the duplicated method cancelList_btnClick().
// 03/13/2012       xnie        131109 - 1) Added a new function preProcess() to set cancel date default value for details
//                                       which cancel dates are null.
//                                       2) Modified handleOnButtonClick to call preProcess() before process cancellation.
// 03/27/2012       xnie        131109 - 1) Modified preProcess() to let original row get focus.
// 04/03/2012       lmjiang     130241 - The value of cancellation method,type,reason fields are empty when the cancellation level is selected as component.
// 04/10/2012       xnie        131109 - 1) Modified preProcess() to let first checked row get focus.
// 04/11/2012       xnie        132565 - 1) Modified cancelList_btnClick() to go to grid first record for any cancel level.
// 05/23/2012       xnie        132862 - 1) Moved logic which checks if there is no any record checked from
//                                          performMultiCancellation() to preProcess().
// 06/08/2012       xnie        132862 - 1) Modified performMultiCancellation() to do commonValidateForm() just for
//                                          records which have been checked.
//                                       2) Modified handleOnChange() to clear cancel date value.
// 08/14/2012       adeng       134537 - Modified cancelListGrid_selectRow() to copy value to the field named "CCANCELDATE_DISP_ONLY",
//                                       while the date format is "dd/mon/yyyy".
// 09/10/2012       ryzhao      133360 - 1) When there is only WARNING message, we still need to display the cancel info.
//                                       2) Refresh the view cancel info page directly to forward to cancel confirmation page. 
// 09/18/2012       ryzhao      133360 - Fix the issue that brought in by last fix.
// 11/12/2012       adeng       131204 - 1) Added a new function hasSelectedRow() to check user has selected some records.
//                                       2) Added a new function setValuesForFields() to set cancel date default value for details
//                                       which cancel dates are null.
//                                       3) Modified handleOnChange() to set detail cancel date default value as cancel
//                                       date in Cancel/Expire information panel;
//                                       4) Remove function preProcess() and remove precondition--preProcess() when
//                                       submitting form in handleOnButtonClick();
//                                       5) Modified cancelList_btnClick() to make sure to set detail cancel date when
//                                       click on select all checkbox when the date format is "dd/mon/yyyy";
//                                       6) Added a new function clearValuesForFields() to Clear values for fields in Cancel Detail;
//                                       7) Modified handleOnchange() to call clearValuesForFields() when change cancel level.
// 03/21/2013       tcheng      141729 - 1) Modified setValuesForFields() to select correct row;
//                                       2) Modified hasSelectedRow() to correct logic;
//                                       3) Modified handleOnChange() to set values for fields after validate effective date correctly;
// 07/10/2013       adeng       117011 - Because added new comment field on Cancel/Expire information section,made below changes:
//                                       1) Modified handleOnChange() to call setValuesForFields() when change it.
//                                       2) Modified cancelListGrid_selectRow() to set value to the detail's new field
//                                          as the one on the top section.
//                                       3) Modified cancelList_btnClick() to set value to the detail's new field as the
//                                          one on the top section when user selecting all check box.
//                                       4) Modified clearValuesForFields() to clear value of the new field in cancel detail.
//                                       5) Modified performMultiCancellation() to set value of new field "transComment2"
//                                          to input form field "newTransactionComment2".
// 04/08/2014       sxm         153765 - Replaced handleDDLFields with field dependency.
// 05/14/2014       jyang       153212 - 1) Modified getUrlForViewMultiCancelConfirmation(), removed amalgamationDate.
//                                       2) Added amalgamationTriggeredCancelListGrid_selectRow() to get input value from
//                                          Cancel/Expire Information and set to cancel detail fields.
//                                       3) Modified setValuesForFields(), add separate setValue logic for amalgamation
//                                          triggered scenario.
//                                       4) Added postAjaxRefresh() to hide/show cancel detail section when amalgamationB
//                                          value changed.
// 06/06/2014       adeng       152052 - Modified setDefaultValuesForAjaxField() to use
//                                       the fireEvent("onChange") to instead of the fireAjax function.
// 07/04/2014       wdang       155780 - Modified postAjaxRefresh() to call hasObject("ddlDetailPanel")
//                                       before getObject("ddlDetailPanel").
// 07/04/2014      kxiang       155331 - Changed function name of addFilterConditionByFilter to addMultiFilterConditions,
//                                       and move it to pmcore/js/common.js.
// 07/24/2014      kxiang       155452 - In function cancelListGrid_selectRow,move setDefaultValuesForAjaxField into if
//                                       condition.
// 07/08/2016      eyin         176476 - modified function handleOnMultiCancelConfirm, open Future Cancellation Details
//                                       Popup if future cancellation exists.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if(isEmptyRecordset(cancelListGrid1.recordset)){
        hideShowElementByClassName(getObject("cancellationTopForms"), true);
    }
    if (hasObject("cancellationLevel") && !isInViewCancelInfoPage()) {
        filterCancelItem();
    }else if (hasObject("cancellationLevel")){
        filterTaibleColumn();
    }
}
//-----------------------------------------------------------------------------
// System displays discipline decline list div only when markAsDdl is "Y".
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    var objName = obj.name;
    if (objName == "cancellationLevel") {
        // Hide the DDL form for non risk level.
        if ("RISK" != obj.value) {
            getObject("markAsDdl").value = "";
            //clear the value for method, reason and type when such fields are hidden.
            if ("COMPONENT" ==  obj.value) {
                if (hasObject("cancellationMethod")){
                    getObject("cancellationMethod").value = "";
                }
                if (hasObject("cancellationType")){
                    getObject("cancellationType").value = "";
                }
                if (hasObject("cancellationReason")){
                    getObject("cancellationReason").value = "";
                }
                cancelListGrid1.recordset("CCANCELREASON").value = "";
                cancelListGrid1.recordset("CCANCELMETHOD").value = "";
                cancelListGrid1.recordset("CCANCELTYPE").value = "";
            }
        }
        cancelListGrid1.recordset("CCANCELDATE").value = "";
        var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performMultiCancellation.do?"
            + "process=getInitialValueForMultiCancel"
            + "&cancellationLevel=" + getObjectValue("cancellationLevel").toUpperCase();
        new AJAXRequest("get", url, '', handleOnGetInitialValuesDone, false);
        clearValuesForFields();
    }
    else if (objName == "markAsDdl") {
        setValuesForFields();
    }
    // Change cancel type, system empties the cancel reason and method list, but doesn't set these values to empty.
    else if(objName == "cancelType"){
         cancelListGrid1.recordset("CCANCELREASON").value = "";
         cancelListGrid1.recordset("CCANCELMETHOD").value = "";
    }
    else if(objName == "cancelReason"){
         cancelListGrid1.recordset("CCANCELMETHOD").value = "";
    }
    // If user checked some records and didn't input detail cancel date, and then input cancel date in Cancel/Expire
    // information panel, we need to set detail cancel date default value as cancel date in Cancel/Expire information panel.
    else if (objName == "cancellationType" ||
            objName == "cancellationReason" ||
            objName == "cancellationMethod" ||
            objName == "transComment2") {
        setValuesForFields();
    }
    else if (objName == "cancellationDate") {
        if (datemaskclear()) {
            setValuesForFields();
        }
    }
}

function handleOnGetInitialValuesDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // parse and set initial values
            var oValueList = parseXML(data);
            setFormFieldValuesByObject(oValueList[0]);
            // Next, execute the page entitlements
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(false);
            }
            //clear filter and filter by cancel level
            clearFilter();
            //deselect all candidates
            getObject("chkCSELECT_ALL").checked = false;
            cancelList_btnClick('DESELECT');
        }
    }
}


function handleOnButtonClick(btn) {
    switch (btn) {
        case "PROCESS":
            // Implement logics: Alternate Flow:  Amalgamate to New Policy
            // If Amalgamate is enabled and the amalgamate method is NEW
            var isAmalgamateEnabled = getObject("amalgamationB").value;
            var method = getObject("amalgamationMethod").value;

            if (isAmalgamateEnabled == "Y" && method == "NEW") {
                // execute amalgamate to new policy logics
                selectPolicyHolderForCreatePolicy("PM", "POLICY");
            }
            else {
                performMultiCancellation();
            }
            break;
        case "Continue":
            setInputFormField("processCode","CONTINUE");
            performMultiCancellation();
            break;
        case "FILTER":
            filterCancelItem();
            break;
        case "CLEAR":
            clearFilter();
            break;
    }
}

function filterCancelItem() {

    var cancelLevel = getObjectValue("cancellationLevel");

    //if is in Multi Cancel page, need to filter cancelable data then filter columns
    var filterStr = "";
    if (isCancelRisk(cancelLevel)) {
        filterStr = "CRISKNAMEDISPLAY!=''";
        filterStr = addMultiFilterConditions(filterStr, "CRISKTYPECODE", getObject("riskTypeFilter"));
    }
    else if (isCancelCoverage(cancelLevel)) {
        filterStr = "CCOVERAGEDISPLAY!=''";
        filterStr = addMultiFilterConditions(filterStr, "CRISKTYPECODE", getObject("riskTypeFilter"));
        filterStr = addMultiFilterConditions(filterStr, "CCOVERAGEDESCRIPTION", getObject("coverageFilter"));
    }
    else if (isCancelCoverageClass(cancelLevel)) {
        filterStr = "CSUBCOVERAGEDISPLAY!=''";
        filterStr = addMultiFilterConditions(filterStr, "CRISKTYPECODE", getObject("riskTypeFilter"));
        filterStr = addMultiFilterConditions(filterStr, "CCOVERAGEDESCRIPTION", getObject("coverageFilter"));
        filterStr = addMultiFilterConditions(filterStr, "CSUBCOVERAGEDESCRIPTION", getObject("coverageClassFilter"));
    }
    else if (isCancelComponent(cancelLevel)) {
        filterStr = "CCOMPONENTDISPLAY!=''";
        filterStr = addMultiFilterConditions(filterStr, "CRISKTYPECODE", getObject("riskTypeFilter"));
        filterStr = addMultiFilterConditions(filterStr, "CCOVERAGEDESCRIPTION", getObject("coverageFilter"));
        filterStr = addMultiFilterConditions(filterStr, "CCOMPONENTDESCRIPTION", getObject("componentFilter"));
    }
    else if (isCancelCoi(cancelLevel)) {
        filterStr = addMultiFilterConditions(filterStr, "CENTITYID", getObject("coiHolderFilter"));
    }

    //filter cancelable items
    cancelListGrid_filter(filterStr);
    if (isEmptyRecordset(cancelListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(cancelListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(cancelListGrid1));
        selectFirstRowInGrid("cancelListGrid");
    }

    var testCode = 'getTableProperty(getTableForGrid(\"cancelListGrid\"), "isUserReadyStateReadyComplete")'
        + '&&!getTableProperty(getTableForGrid(\"cancelListGrid\"), "filtering")';
    var callbackCode = 'filterTaibleColumn()';
    executeWhenTestSucceeds(testCode, callbackCode, 100);


}

function filterTaibleColumn() {
    var cancelLevel = getObjectValue("cancellationLevel");

    if (isCancelRisk(cancelLevel)) {
        showHideTableColumn("CCOVERAGEDESCRIPTION", false);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
    else if (isCancelCoverage(cancelLevel)) {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
    else if (isCancelCoverageClass(cancelLevel)) {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", false);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", true);
    }
    else if (isCancelComponent(cancelLevel)) {
        showHideTableColumn("CCOVERAGEDESCRIPTION", true);
        showHideTableColumn("CCOMPONENTDESCRIPTION", true);
        showHideTableColumn("CSUBCOVERAGEDESCRIPTION", false);
    }
}

function clearFilter() {
    clearMultiSelectField("riskTypeFilter");
    clearMultiSelectField("coverageFilter");
    clearMultiSelectField("componentFilter");
    clearMultiSelectField("coverageClassFilter");
    clearMultiSelectField("coiHolderFilter");
    //redisplay all cancelable items
    filterCancelItem();
}

function clearMultiSelectField(fieldName) {
    var multiSelFld = getObject(fieldName);
    for (var i = 0; i < multiSelFld.options.length; i++) {
        multiSelFld.options[i].selected = false;
    }
    getObject(fieldName + "MultiSelectText").value = "";
}

function isCancelRisk(cancelLevel) {
    return cancelLevel.toUpperCase() == 'RISK';
}

function isCancelCoverage(cancelLevel) {
    return cancelLevel.toUpperCase() == 'COVERAGE';
}

function isCancelCoverageClass(cancelLevel) {
    return cancelLevel.toUpperCase() == 'COVERAGE CLASS';
}

function isCancelComponent(cancelLevel) {
    return cancelLevel.toUpperCase() == 'COMPONENT';
}

function isCancelCoi(cancelLevel) {
    return cancelLevel.toUpperCase() == 'COI';
}

function performMultiCancellation() {
    var XMLData = cancelListGrid1;
    var selectedRowId = 0;
    var validForm = true;

    if (!isEmptyRecordset(XMLData.recordset)) {
        // Get the current selected row.
        selectedRowId = XMLData.recordset("ID").value;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if(XMLData.recordset("CSELECT_IND").value == '-1' && !commonValidateForm()) {
                validForm = false;
                break;
            }
            next(XMLData);
        }
        first(XMLData);
        // Select the current selected row.
        getRow(XMLData, selectedRowId);
    }

    if (validForm) {
        // If the processCode is CONTINUE, it means it is called by view cancel information page.
        if (isCancelCoi(getObjectValue("cancellationLevel")) || (hasObject("processCode") && getObjectValue("processCode") == "CONTINUE")) {
            var parentWindow = window.frameElement.document.parentWindow;
            // The "showMultiConfirmationB" is for risk/coverage/coverage class/component, if it is Y, system closes
            // the current view cancel information page and pop up the view confirmation page.
            if (hasObject("showMultiConfirmationB") && getObjectValue("showMultiConfirmationB") == "Y") {
                var url = getUrlForViewMultiCancelConfirmation() + "&processCode=CONTINUE";
                document.forms[0].txtXML.value = getChanges(cancelListGrid1);
                cancelList.action = url;
                baseOnSubmit(cancelList);
            }
            else { // For COI.
                setInputFormField("newTransactionComment2", getObjectValue("transComment2"));
                document.forms[0].process.value = "performMultiCancellation";
                submitForm(true);
            }
        }
        else {
            // Validate cancellation transactions.
            alternateGrid_update("cancelListGrid", "CSELECT_IND = '-1' or CSELECT_IND = '0'");
            document.forms[0].txtXML.value = getChanges(cancelListGrid1);
            document.forms[0].process.value = "viewMultiCancelConfirmation";
            postAjaxSubmit("/transactionmgr/cancelprocessmgr/performMultiCancellation.do", "viewMultiCancelConfirmation", false, false, handleOnMultiCancelConfirm);

        }
    }
}

function isInViewCancelInfoPage(){
    return hasObject("PM_CC_CONTINUE");
}

//-----------------------------------------------------------------------------
// Get policy no from "create policy" page
//-----------------------------------------------------------------------------
function amalgamateToNewPolicyDone(policyNo) {
    getObject("amalgamationTo").value = policyNo;
    performMultiCancellation();
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox.
// In order to sync the selected records, system set the update_ind to 'Y'.
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    var objName = obj.name;
    if (objName == 'chkCSELECT_IND') {
        if (cancelListGrid1.recordset("UPDATE_IND").value == "N"){
            cancelListGrid1.recordset("UPDATE_IND").value = "Y";
        }
        setTableProperty(cancelListGrid1, "gridDataChange", true);
        if (window.postOnChange) {
            postOnChange(field);
        }
    }
}

function getChanges(XMLData) {
    return getSelectedOnly(XMLData);
}

//-----------------------------------------------------------------------------
// Selecting different row, system checks and defaults cancel data by associated
// field in top cancellation section.
//-----------------------------------------------------------------------------
function cancelListGrid_selectRow() {
    var isChecked = false;
    if(cancelListGrid1.recordset("CSELECT_IND").value == '-1'){
        isChecked = true;
    }
    if (isChecked && hasObject("cancelDate") && isEmpty(getObjectValue("cancelDate")) && !isEmpty(getObjectValue("cancellationDate"))) {
        cancelListGrid1.recordset("CCANCELDATE").value =getObjectValue("cancellationDate");
        //if the format of cancel date is "dd/mon/yyyy",the value should be copied to  CCANCELDATE_DISP_ONLY
        if (isFieldDefinedForGrid(getCurrentlySelectedGridId(), "CCANCELDATE_DISP_ONLY")) {
            cancelListGrid1.recordset("CCANCELDATE_DISP_ONLY").value = getObjectValue("cancellationDate_DISP_ONLY");
        }

    }
    if (isChecked) {
        if(hasObject("cancelType") && isEmpty(getObjectValue("cancelType")) && !isEmpty(getObjectValue("cancellationType"))){
            cancelListGrid1.recordset("CCANCELTYPE").value = getObjectValue("cancellationType");
            setDefaultValuesForAjaxField("cancelType");
        }
    }
    if (isChecked) {
        if(hasObject("cancelReason") && isEmpty(getObjectValue("cancelReason")) && !isEmpty(getObjectValue("cancellationReason"))){
            cancelListGrid1.recordset("CCANCELREASON").value = getObjectValue("cancellationReason");
            setDefaultValuesForAjaxField("cancelReason");
        }
    }
    if (isChecked && hasObject("cancelMethod") && isEmpty(getObjectValue("cancelMethod")) && !isEmpty(getObjectValue("cancellationMethod"))) {
        cancelListGrid1.recordset("CCANCELMETHOD").value = getObjectValue("cancellationMethod");
    }
    if (isChecked && hasObject("transactionComment2") && isEmpty(getObjectValue("transactionComment2")) && !isEmpty(getObjectValue("transComment2"))) {
        cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value = getObjectValue("transComment2");
        maintainNoteImageForAllNoteFields();
    }
}
//-----------------------------------------------------------------------------
// If future cancellation exist, then open Future Cancellation Popup firstly.
// If the status is invalid, system opens view cancel information page,
// else system opens confirmation page.
//-----------------------------------------------------------------------------
function handleOnMultiCancelConfirm(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var url = "";
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var futureCancellationExist = oValueList[0]["FUTURECANCELLATIONEXISTB"];
                if (futureCancellationExist == "Y") {
                    var status = oValueList[0]["STATUS"];
                    futureCancellationDetails(status);
                    return;
                }
            }

            if (oValueList.length > 0 && (oValueList[0]["STATUS"] == 'INVALID' || oValueList[0]["STATUS"] == 'WARNING')) {
                var status = oValueList[0]["STATUS"];
                url = getAppPath() + "/transactionmgr/cancelprocessmgr/ViewMultiCancelInfo.do?"
                    + commonGetMenuQueryString() + "&process=loadAllMultiCancelableInfo&status="+status+"&showMultiConfirmationB=Y"
                    + "&cancellationLevel=" + getObjectValue("cancellationLevel");
            }
            else{
                url = getUrlForViewMultiCancelConfirmation();
            }
            var processingDivId = openDivPopup("", url, true, true, "", "", 800, 600, "", "", "", false);
        }
    }
}

function handleOnViewMultiCancelInfo(status) {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/ViewMultiCancelInfo.do?"
            + commonGetMenuQueryString() + "&process=loadAllMultiCancelableInfo&status="+status+"&showMultiConfirmationB=Y"
            + "&cancellationLevel=" + getObjectValue("cancellationLevel");
    openDivPopup("", url, true, true, "", "", 800, 600, "", "", "", false);
}
//-----------------------------------------------------------------------------
// Build url for view multi cancellation confirmation page.
//-----------------------------------------------------------------------------
function getUrlForViewMultiCancelConfirmation() {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewMultiCancelConfirmation.do?"
            + commonGetMenuQueryString() + "&process=loadAllMultiCancelConfirmation"
            + "&cancellationLevel=" + getObjectValue("cancellationLevel") + "&accountingDate=" + getObjectValue("accountingDate")
            + "&cancellationReason=" + getObjectValue("cancellationReason") + "&cancellationMethod=" + getObjectValue("cancellationMethod");
    if (hasObject("amalgamationB")) {
        url += "&amalgamationB=" + getObjectValue("amalgamationB");
    }
    if (hasObject("amalgamationMethod")) {
        url += "&amalgamationMethod=" + getObjectValue("amalgamationMethod");
    }
    if (hasObject("claimsAccessIndicator")) {
        url += "&claimsAccessIndicator=" + getObjectValue("claimsAccessIndicator");
    }
    if (hasObject("markAsDdl")) {
        url += "&markAsDdl=" + getObjectValue("markAsDdl");
    }
    return url;
}
//-----------------------------------------------------------------------------
// Set default value when selecting all check box.
//-----------------------------------------------------------------------------
function cancelList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    if('SELECT' == asBtn){
        cancelListGrid_updatenode('UPDATE_IND', 'Y');
        var cancelLevel = getObjectValue("cancellationLevel");
        // Handle default values for Risk, Coverage and Coverage Class.
        if (isCancelRisk(cancelLevel) || isCancelCoverage(cancelLevel) || isCancelCoverageClass(cancelLevel) || isCancelComponent(cancelLevel)) {
            var cancellationDate = getObjectValue("cancellationDate");
            var cancellationType = getObjectValue("cancellationType");
            var cancellationReason = getObjectValue("cancellationReason");
            var cancellationMethod = getObjectValue("cancellationMethod");
            var transComment2 = getObjectValue("transComment2");
            first(cancelListGrid1);
            while (!cancelListGrid1.recordset.eof) {
                if (isEmpty(cancelListGrid1.recordset("CCANCELDATE").value)) {
                    cancelListGrid1.recordset("CCANCELDATE").value = cancellationDate;
                    //if the format of cancel date is "dd/mon/yyyy",the value should be copied to  CCANCELDATE_DISP_ONLY
                    if (isFieldDefinedForGrid(getCurrentlySelectedGridId(), "CCANCELDATE_DISP_ONLY")) {
                        cancelListGrid1.recordset("CCANCELDATE_DISP_ONLY").value = getObjectValue("cancellationDate_DISP_ONLY");
                    }
                }
                if (isCancelRisk(cancelLevel) || isCancelCoverage(cancelLevel) || isCancelCoverageClass(cancelLevel)) {
                    if (isEmpty(cancelListGrid1.recordset("CCANCELTYPE").value)) {
                        cancelListGrid1.recordset("CCANCELTYPE").value = cancellationType;
                        setDefaultValuesForAjaxField("cancelType");
                    }
                    if (isEmpty(cancelListGrid1.recordset("CCANCELREASON").value)) {
                        cancelListGrid1.recordset("CCANCELREASON").value = cancellationReason;
                        setDefaultValuesForAjaxField("cancelReason");
                    }
                    if (isEmpty(cancelListGrid1.recordset("CCANCELMETHOD").value)) {
                        cancelListGrid1.recordset("CCANCELMETHOD").value = cancellationMethod;
                    }
                }
                if (isEmpty(cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value)) {
                    cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value = transComment2;
                    maintainNoteImageForAllNoteFields();
                }
                next(cancelListGrid1);
            }
            first(cancelListGrid1);
        }
        else {
            first(cancelListGrid1);
        }
    }
}

function setDefaultValuesForAjaxField(fieldName) {
    var ajaxInfoField = null;
    try {
        ajaxInfoField = eval("ajaxInfoFor" + fieldName);
    }
    catch(ex) {
        ajaxInfoField = null;
    }
    if (ajaxInfoField != null) {
        getObject(fieldName).fireEvent("onChange");
    }
}
//-----------------------------------------------------------------------------
// If user checked some records and didn't input detail cancel date, and then
// input cancel date in Cancel/Expire information panel, we need to set detail
// cancel date default value as cancel date in Cancel/Expire information panel.
//-----------------------------------------------------------------------------
function setValuesForFields() {
    if (!hasSelectedRow()) {
        return;
    }
    var XMLData = cancelListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var selectedRowId = getSelectedRow("cancelListGrid");
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (hasObject("amalgamationB") && getObjectValue("amalgamationB") == "Y")
                amalgamationTriggeredCancelListGrid_selectRow();
            else
                cancelListGrid_selectRow();
            next(XMLData);
        }
        first(XMLData);
        // Select the current selected row.
        getRow(XMLData, selectedRowId);
    }
}
//-----------------------------------------------------------------------------
// Check if has selected rows.
//-----------------------------------------------------------------------------
function hasSelectedRow() {
    var selectedRecords = cancelListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    return selectedRecords.length != 0;
}

//-----------------------------------------------------------------------------
// Clear values for fields in Cancel Detail.
//-----------------------------------------------------------------------------
function clearValuesForFields() {
    var XMLData = cancelListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var selectedRowId = XMLData.recordset("ID").value;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if(cancelListGrid1.recordset("CCANCELDATE").value != ""){
                cancelListGrid1.recordset("CCANCELDATE").value = "";
                if (isFieldDefinedForGrid(getCurrentlySelectedGridId(), "CCANCELDATE_DISP_ONLY")) {
                    cancelListGrid1.recordset("CCANCELDATE_DISP_ONLY").value = "";
                }
            }
            if(cancelListGrid1.recordset("CCANCELTYPE").value != ""){
                cancelListGrid1.recordset("CCANCELTYPE").value = "";
            }
            if(cancelListGrid1.recordset("CCANCELREASON").value != ""){
                cancelListGrid1.recordset("CCANCELREASON").value = "";
            }
            if(cancelListGrid1.recordset("CCANCELMETHOD").value != ""){
                cancelListGrid1.recordset("CCANCELMETHOD").value = "";
            }
            if(cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value != ""){
                cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value = "";
            }
            next(XMLData);
        }
        first(XMLData);
        // Select the current selected row.
        getRow(XMLData, selectedRowId);
    }
}

//-----------------------------------------------------------------------------
// Hide or show cancel detail section when amalgamationB value changed
//-----------------------------------------------------------------------------
function postAjaxRefresh(field, AjaxUrls) {
    if (AjaxUrls.indexOf('fieldId=amalgamationB') > 0) {
        if (hasObject("amalgamationB") && getObjectValue("amalgamationB") == "Y") {
            if (hasObject("ddlDetailPanel")) {
                hideShowElementByClassName(getObject("ddlDetailPanel"), true);
            }
            setValuesForFields();
        }
        else {
            if (hasObject("ddlDetailPanel")) {
                hideShowElementByClassName(getObject("ddlDetailPanel"), false);
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Selecting different row, system checks and defaults cancel data by associated
// field in top cancellation section for amalgamation triggered scenario.
//-----------------------------------------------------------------------------
function amalgamationTriggeredCancelListGrid_selectRow() {
    var isChecked = false;
    if (cancelListGrid1.recordset("CSELECT_IND").value == '-1') {
        isChecked = true;
    }
    if (isChecked && hasObject("cancelDate")) {
        cancelListGrid1.recordset("CCANCELDATE").value = getObjectValue("cancellationDate");
        //if the format of cancel date is "dd/mon/yyyy",the value should be copied to  CCANCELDATE_DISP_ONLY
        if (isFieldDefinedForGrid(getCurrentlySelectedGridId(), "CCANCELDATE_DISP_ONLY")) {
            cancelListGrid1.recordset("CCANCELDATE_DISP_ONLY").value = getObjectValue("cancellationDate_DISP_ONLY");
        }
    }
    if (isChecked) {
        if (hasObject("cancelType")) {
            cancelListGrid1.recordset("CCANCELTYPE").value = getObjectValue("cancellationType");
        }
        setDefaultValuesForAjaxField("cancelType");
    }
    if (isChecked) {
        if (hasObject("cancelReason")) {
            cancelListGrid1.recordset("CCANCELREASON").value = getObjectValue("cancellationReason");
        }
        setDefaultValuesForAjaxField("cancelReason");
    }
    if (isChecked && hasObject("cancelMethod")) {
        cancelListGrid1.recordset("CCANCELMETHOD").value = getObjectValue("cancellationMethod");
    }
    if (isChecked && hasObject("transactionComment2")) {
        cancelListGrid1.recordset("CTRANSACTIONCOMMENT2").value = getObjectValue("transComment2");
        maintainNoteImageForAllNoteFields();
    }
    if (isChecked && hasObject("cancelComment")) {
        cancelListGrid1.recordset("CCANCELCOMMENT").value = "";
    }
}