//-----------------------------------------------------------------------------
// Javascript file for maintainQuoteTransfer.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   April 28, 2016
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/28/2016       wdang       167534 - Initial Version.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// The function of update all select indicator
//-----------------------------------------------------------------------------
function quoteTransferList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

//-----------------------------------------------------------------------------
// The entry function for clicking button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            var selectedRecords = quoteTransferListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
            var size = selectedRecords.length;
            if (size == 0) {
                handleError(getMessage("pm.maintainQuoteTransfer.noselection.error"));
                return false;
            }
            else {
                commonOnSubmit('performTransfer', false, false, false, true);
                break;
            }
        case 'CLOSE':
            if (isEmpty(getObjectValue("workflowState"))) {
                closeThisDivPopup(true);
            }
            else {
                commonOnSubmit('closePage', true, true, true);
            }
            break;
    }
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    var objName = c.name;
    if (objName == 'chkCSELECT_IND') {
        if (quoteTransferListGrid1.recordset("UPDATE_IND").value == "N") {
            quoteTransferListGrid1.recordset("UPDATE_IND").value = "Y";
        }
        setTableProperty(quoteTransferListGrid1, "gridDataChange", true);
        if (window.postOnChange) {
            postOnChange(field);
        }
    }
}

//-----------------------------------------------------------------------------
// Then entry function to navigate policy.
//-----------------------------------------------------------------------------
function navigatePolicy(policyNo) {
    var url = getAppPath()+"/policymgr/findPolicy.do?isGlobalSearch=Y&termStatusCode=ALL&process=findAllPolicy&policyNoCriteria=" + policyNo;
    window.frameElement.document.parentWindow.setWindowLocation(url);
}