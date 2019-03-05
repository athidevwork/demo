//-----------------------------------------------------------------------------
// Dividend Audit javascript file.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Dec 23, 2013
// Author: awu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SEARCH':
            searchAudit();
            break;
    }
}

function searchAudit() {
    var url = getAppPath() + "/policymgr/dividendmgr/viewDividendAudit.do?";
    var transactionId = getObjectValue("transactionId");
    var showTermOrAll = getObjectValue("showTermOrAll");
    var newURL = url + commonGetMenuQueryString()
            + "&process=loadAllDividendAudit&transactionId="
            + transactionId + "&showTermOrAll=" + showTermOrAll;
    setWindowLocation(newURL);
}
