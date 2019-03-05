//-----------------------------------------------------------------------------
// Javascript file for viewQuote.jsp.
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
// Then entry function to navigate policy.
//-----------------------------------------------------------------------------
function navigatePolicy(policyNo) {
    var url = getAppPath()+"/policymgr/findPolicy.do?isGlobalSearch=Y&termStatusCode=ALL&process=findAllPolicy&policyNoCriteria=" + policyNo;
    window.frameElement.document.parentWindow.setWindowLocation(url);
}