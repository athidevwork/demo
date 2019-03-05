//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
// Revision    Revised   Description
// Date        By
// 09/24/2015  sxy       Modified for isue 164720
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    }
    else if (btnID == 'phonenumber'
            || btnID == 'entityclass'
            || btnID == 'entityrole'
            || btnID == 'vendor'
            || btnID == 'vendorAddress'
            || btnID == 'address') {
        // Go to the appropriate page.
        goToEntityModule(btnID, getObjectValue("pk"),
                getObjectValue("entityName"),
                getObjectValue("entityType"));
    } else if (btnID == "imageRight") {
        var sourceTable = "PROGRAM_STEP_HISTORY_VIEW" ;
        var sourceData = getObjectValue("pk");
        cisRmStartImageRightDeskTop(sourceTable, sourceData);
    }
}
function cisRmStartImageRightDeskTop(sourceTable, sourceData) {

    // First get the data element to be used
    // Check if the field is in the form

    if (sourceData == '') {
        alert(getMessage("ci.entity.message.imageRight.determine"));
        return;
    }
    // Second take the source data and source table and get the IR file number and drawer
    var url = getTopNavApplicationUrl("CS") + "/imagerightmgr/maintainImageRight.do?" +
              "&sourceData=" + sourceData +
              "&sourceTable=" + sourceTable;
    url +=   "&date=" + new Date();
    // initiate async call
    new AJAXRequest("get", url, '', handleStartImageRightDeskTop, false);
}
