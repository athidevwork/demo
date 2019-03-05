//-----------------------------------------------------------------------------
// Javascript file for maintainInsuredTrackingListGrid.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   April 08, 2015
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 02/16/2016       tzeng       167532 - Initial version.
// 03/10/2017       wli         180675 - Used "getOpenCtxOfDivPopUp()" to call openDivPopup() for UI change.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// The entry function for clicking button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var selectedDataGrid = getXMLDataForGridName(getRenewalFlagGridName());
    switch (asBtn) {
        case 'SAVE':
            saveRenewalFlag();
    }
}

//-----------------------------------------------------------------------------
//The entry when clicking Save button
//-----------------------------------------------------------------------------
function saveRenewalFlag() {
    commonOnSubmit('saveAllRenewalFlag', false, false, false, true);
}

//-----------------------------------------------------------------------------
//The entry when adding row
//-----------------------------------------------------------------------------
function maintainRenewalFlagListGrid_setInitialValues() {
    sendAJAXRequest("getInitialValuesForRenewalFlag");
}

//-----------------------------------------------------------------------------
// Send AJAX request
//-----------------------------------------------------------------------------
function sendAJAXRequest(process) {
    // set url
    var url = getAppPath() + "/policymgr/renewalflagmgr/maintainRenewalFlag.do?"
            + commonGetMenuQueryString() + "&process=" + process;

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

//-----------------------------------------------------------------------------
// The call back function when a row is added
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForRenewalFlag(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

//-----------------------------------------------------------------------------
// Set current date when viewed checked on
//-----------------------------------------------------------------------------
function rowchange(c) {
    var selectedDataGrid = getXMLDataForGridName(getRenewalFlagGridName());
    if(c.checked) {
        selectedDataGrid.recordset("CREVIEWEDDATE").value = formatDate(new Date(), 'mm/dd/yyyy');
    }
    else {
        selectedDataGrid.recordset("CREVIEWEDDATE").value = '';
    }
}

//-----------------------------------------------------------------------------
// Return Renewal Flag grid name
//-----------------------------------------------------------------------------
function getRenewalFlagGridName() {
    return "maintainRenewalFlagListGrid";
}

//-----------------------------------------------------------------------------
// View Renewal Flag audit name
//-----------------------------------------------------------------------------
function viewRenewalFlagAuditHistory(contextId) {
    var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
            + commonGetMenuQueryString() + "&process=loadAllAudit" +
                                           "&fromPage=renewalFlag-renewalFlag" +
                                           "&contextId=" + contextId +
                                           "&renewalFlagCode=" + getObjectValue("flagCode") +
                                           "&sourceTableName=" + getObjectValue("sourceTableName") +
                                           "&sourceRecordId=" + getObjectValue("sourceRecordId");
    var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}