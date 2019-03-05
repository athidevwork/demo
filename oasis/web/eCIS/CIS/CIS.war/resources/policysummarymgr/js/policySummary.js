//-----------------------------------------------------------------------------
//  Description: js file for policy summary page.
//
//  Author: hxk
//  Date:   12/20/2013
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  10/23/2018       dpang       195835: Grid replacement.
//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function handleOnLoad() {
    var url = getTopNavApplicationUrl("Policy")+"/policymgr/viewPolicySummary.do?entityId=" + getObjectValue("pk");
    getObject("iframePolicy").src = url;

    if (useJqxGrid) {
        $("#iframePolicy").height(1190);
    }
}
