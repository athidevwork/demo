//-----------------------------------------------------------------------------
// Implements logic for load coverage billing.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 23, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------
function continueCurrentWorkFlow() {
    getObject("isBillingSetupDone").value = "Y";
    resetDivPopupDimensions();
    commonOnSubmit('closePage', true, true, true);
}

function existCurrentWorkFlow() {
    getObject("isBillingSetupDone").value = "N";
    resetDivPopupDimensions();
    commonOnSubmit('closePage', true, true, true);
}

function resetDivPopupDimensions() {
    if (window.frameElement) {
        window.frameElement.height =  window.frameElement.getAttribute("priorHeight");
        window.frameElement.width =  window.frameElement.getAttribute("priorWidth");
        window.frameElement.scroll =  window.frameElement.getAttribute("priorScroll");
    }
}

function handleOnLoad() {
    if (window.frameElement) {
        window.frameElement.setAttribute("priorHeight", window.frameElement.height);
        window.frameElement.setAttribute("priorWidth", window.frameElement.width);
        window.frameElement.setAttribute("priorScroll", window.frameElement.scroll);
        window.frameElement.height = "555";
        window.frameElement.width = "800";
        window.frameElement.scroll = "yes";
    }
    var url = getTopNavApplicationUrl("FM") +
            "/billingrelationmgr/manageCovgBillingRelation.do?policyNo=" +
            getObjectValue("policyNo") + "&transactionLogId=" + policyHeader.lastTransactionInfo.transactionLogId;
    getObject("covgBillingIframe").src = url;
}