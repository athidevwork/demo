//-----------------------------------------------------------------------------
// Javascript file for processTail.js
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 18, 2010
// Author: gchitta
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/18/2010       gchitta     issue 106055 - Modified handleOnCaptureFinancePercentageDone and CaptureFinancePercentage
//                              to handle additional value of "B" for captureFinancePercentage field
// 10/14/2010       syang       Issue 103811 - Modified updateSelectInd() to select the row that validSelectB is 'Y'
//                              when click "Select All" option.
// 11/01/2010       dzhang      113002 - disable Accept/Decline/Activate/Cancellation/Reinstate options if the valid row
//                              number is 0 or the coverageListGrid1 is empty.
// 11/18/2010       syang       Issue 114279 - Rollback the changes of captureFinancePercentage() made in issue 106055 since this method
//                              is a call-back method. Separated the logic into a new method installmentFinanceCharge().
// 12/01/2010       dzhang      114879 - Modify closePage().
// 04/11/2011       syang       116218 - Correct the "isAcceptAvailable" in initGridSelectCheckbox().
// 05/13/2011       wqfu        120687 - Modify closePage to determine if policy picture has been changed.
// 06/02/2011       wqfu        120617 - Modify closePage to determine if object is existed. Because this function is
//                              also used for Close button in maintain tail page. 
// 08/18/2011       syang       121201 - Modified closePage() to refresh page if it is in any workflow.
// 04/27/2012       xnie        132999 - Modified installmentFinanceCharge() and captureFinancePercentage() to add
//                              termBaseRecordId to url.
// 07/01/2013       adeng       117011 - Modified handleOnCaptureCancellationDone() to
//                                       1) pass one more parameter "transactionComment2".
//                                       2) set the new object "transactionComment2" to input form field
//                                          "newTransactionComment2".
// 04/28/2014       xnie        153450 - Modified closePage() to
//                                       1) call validateTailData() ignore if coverage record set
//                                          is NULL or not (PATAIL tail is added and then is deleted will cause NULL
//                                          record set case).
//                                       2) Refresh parent page or close tail coverage page ignore if coverage record
//                                          set is NULL or not (PATAIL tail is added and then is deleted will cause NULL
//                                          record set case).
//                                       3) When current page has no any data changed, but policy view mode is WIP and
//                                          policy header wip indicator is Y, system needs to validate tail data as well.
// 05/18/2017       lzhang      185288 - Modified initGridSelectCheckbox:
//                                       Enable checkbox when CISSELECTAVAILABLE is 'Y'
// 06/24/2017       ssheng      185382 - Modified closePage function not to display confirm message box in new UI tab style.
// 12/28/2017       tzeng       190488 - Modified closePage() to displayed confirm message when PM_AUTO_SAVE_WIP is
//                                       turned off in UT tab style.
// 07/31/2018       mlm         193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
//-----------------------------------------------------------------------------
function performTailProcess() {
    alternateGrid_update('coverageListGrid', "CSELECT_IND = '-1' or CSELECT_IND = '0'");
    document.forms[0].process.value = "performTailProcess";
    showProcessingDivPopup();
    submitFirstForm();
}

function reloadPage() {
    document.forms[0].process.value = "loadAllTail";
    showProcessingDivPopup();
    submitFirstForm();
}

function closePage() {

    //For Cancel/Update/Decline/Accept/Activate/Reinstate tail transaction,the created transaction is to be considered a change
    //so that we will need to warn them that by leaving the page, changes will be lost.
    var lastTranCode = policyHeader.lastTransactionInfo.transactionCode;
    var policyViewMode = getObjectValue("policyViewMode");
    var dataChangedB = isPageDataChanged();
    if (policyViewMode == "WIP" && policyHeader.wipB && (lastTranCode == "TLENDORSE" || lastTranCode == "TLCANCEL")) {
        isChanged = true;
    }
    if (isButtonStyle() && ((commonIsOkToChangePages() && isPageDataChanged())
            || (!isPageDataChanged() && policyViewMode == "WIP" && policyHeader.wipB))) {
        validateTailData();
    }
    else if (isTabStyle() && (isPageDataChanged() || (!isPageDataChanged() && policyViewMode == "WIP" && policyHeader.wipB))) {
        if (getSysParmValue("PM_AUTO_SAVE_WIP") == "Y") {
            validateTailData();
        }
        else {
            // If there has change on tail tab then navigate to other sub tabs,
            // the message "Data has been changed on sub tab; if you click on OK, changes will be lost" has been prompted
            // when doing commonIsOkToPendSubTab before, so this another prompt does not need to be displayed in this time.
            if (dataChangedB || commonIsOkToChangePages()) {
                validateTailData();
            }
            else {
                syncResultToParent(commonOnSubmitReturnTypes.saveInProgress);
            }
        }
    }
    else if (!isPageDataChanged() && !(policyViewMode == "WIP" && policyHeader.wipB)) {
        if (isInOtherWF) {
            window.frameElement.document.parentWindow.refreshPage(true);
        }
        else {
            // compare wip no or official no to determine if policy picture is changed. If policy
            // picture has been changed, parent page should be refreshed when closing.
            var parentWindow = window.frameElement.document.parentWindow;
            if (parentWindow.hasObject("wipNo") && parentWindow.hasObject("offNo") &&
                hasObject("wipNo") && hasObject("offNo") &&
                (getObjectValue("wipNo") != parentWindow.getObjectValue("wipNo") ||
                 getObjectValue("offNo") != parentWindow.getObjectValue("offNo"))) {
                parentWindow.refreshPage(true);
            }
            else if (isTabStyle()) {
                syncResultToParent(commonOnSubmitReturnTypes.noDataChange);
            }
            else {
                commonOnButtonClick('CLOSE_RO_DIV');
            }
        }
    }
}

function getSelectedRows(xmlData) {
    var selectedXML = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    return selectedXML;
}

function userRowchange(obj) {
    if (obj.name == "chkCSELECT_IND") {
        var XMLData = coverageListGrid1;
        if (XMLData.recordset('CISSELECTAVAILABLE').value != 'Y') {
            window.event.returnValue = false;
        }
    }
}

function coverageList_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            updateSelectInd(-1);
            break;
        case 'DESELECT':
            updateSelectInd(0);
            break;
    }
}

function updateSelectInd(selectValue) {
    var XMLData = coverageListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (XMLData.recordset('CISSELECTAVAILABLE').value == 'Y') {
                if (selectValue == '0' || (selectValue == '-1' && XMLData.recordset('CVALIDSELECTB').value == 'Y')) {
                    XMLData.recordset('CSELECT_IND').value = selectValue;
                }
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}

function handleOnCaptureCancellationDone(cancelDate, accountDate, cancelType, cancelReason, cancelMethod, cancelAddOccupant, cancelComments, transactionComment2) {
    setInputFormField("cancellationDate", cancelDate);
    setInputFormField("accountingDate", accountDate);
    setInputFormField("cancellationType", cancelType);
    setInputFormField("cancellationReason", cancelReason);
    setInputFormField("cancellationMethod", cancelMethod);
    setInputFormField("cancellationComments", cancelComments);
    setInputFormField("newTransactionComment2", transactionComment2);
    performTailProcess();
}

function initGridSelectCheckbox() {
    var chkSelAll = document.getElementsByName("chkCSELECT_ALL")[0];
    // 113002 Hidden the button Accept/Decline/Activate/Cancellation/Reinstate if the table is empty.
    if (!getTableProperty(coverageListGrid, "hasrows")) {
        if (hasObject("isAcceptAvailable") && getObject("isAcceptAvailable").value == 'Y') {
            setInputFormField("isAcceptAvailableNeedResetToY", "Y");
            setInputFormField("isAcceptAvailable", "N");
            // Call the pageEntitlement to show/hidden the button Accept/Decline/Activate/Cancellation/Reinstate
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(false);
            }
        }
        return;
    }

    var disableChkSelectAll = true;
    // When there is pagination, not all records are in table. So move to the proper record first.
    resetRecordPointerToFirstRowInGridCurrentPage(coverageListGrid);

    // Initialize the select check boxes in table
    var XMLData = coverageListGrid1;
    var chkSelArray = document.getElementsByName("chkCSELECT_IND");
    var size = chkSelArray.length;
    if (!isEmptyRecordset(XMLData.recordset)) {
        for (var i = 0; i < size; i++) {
            if (XMLData.recordset('CISSELECTAVAILABLE').value == 'N') {
                chkSelArray[i].disabled = true;
            }
            else {
                chkSelArray[i].disabled = false;
                disableChkSelectAll = false;
            }
            next(XMLData);
        }

        if (disableChkSelectAll) {
            chkSelAll.disabled = true;
        }
        else {
            chkSelAll.disabled = false;
        }
    }
    else {
        chkSelAll.disabled = true;
    }
    // Move back to where we started
    resetRecordPointerToFirstRowInGridCurrentPage(coverageListGrid);
    // #113002 disable Accept/Decline/Activate/Cancellation/Reinstate options if the valid row number is 0
    var validNodes = XMLData.documentElement.selectNodes("//ROW[CISSELECTAVAILABLE = 'Y']");

    if ((validNodes.length <= 0) && (hasObject("isAcceptAvailable") && getObject("isAcceptAvailable").value == 'Y')) {
        setInputFormField("isAcceptAvailableNeedResetToY", "Y");
        setInputFormField("isAcceptAvailable", "N");
        // Call the pageEntitlement to show/hidden the button Accept/Decline/Activate/Cancellation/Reinstate
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(false);
        }
    }
}

function handleReadyStateReady(table) {
    if (table.id == "coverageListGrid")
        initGridSelectCheckbox();
}

function captureFinancePercentage() {
    if (getObjectValue("captureFinancePercentage") == 'Y') {
        var termBaseRecordId = coverageListGrid1.recordset("CTERMBASERECORDID").value;
        var url = getAppPath() + "/tailmgr/captureFinancePercent.do?termBaseRecordId=" + termBaseRecordId + "&"
            + commonGetMenuQueryString();
        var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
    }
    else {
        performTailProcess();
    }
}

function installmentFinanceCharge() {
    if (getObjectValue("captureFinancePercentage") == 'B') {
        var termBaseRecordId = coverageListGrid1.recordset("CTERMBASERECORDID").value;
        var url = getAppPath() + "/tailmgr/captureFinancePercent.do?termBaseRecordId=" + termBaseRecordId + "&"
                + commonGetMenuQueryString();
        var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
    }
}

function handleOnCaptureFinancePercentageDone(ratePercent) {
    setInputFormField("ratePercent", ratePercent);
    if (getObjectValue("captureFinancePercentage") == 'B') {
        alternateGrid_update('coverageListGrid', "CTAILRECORDMODECODE = 'TEMP'");
        document.forms[0].process.value = "saveTailCharge";
        showProcessingDivPopup();
        submitFirstForm();
    }
    else  {
        performTailProcess();
}   };
