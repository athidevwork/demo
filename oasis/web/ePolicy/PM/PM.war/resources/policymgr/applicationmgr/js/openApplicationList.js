//-----------------------------------------------------------------------------
// This function is to open application list page from policy actions 
//-----------------------------------------------------------------------------
function applicationList() {
    var url = getAppPath() + "/policymgr/applicationmgr/maintainApplication.do?"
        + commonGetMenuQueryString() + "&process=loadCurrentApplication" ;
    var divPopupId = openDivPopup("", url, true, true, "", "", 850, 720, "", "", "", false);
}