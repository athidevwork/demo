//-----------------------------------------------------------------------------
// Javascript file for viewQuickPayDetail.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 22, 2010
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// To handle the select Quick Pay Transactions list grid row event.
// Load the risks/coverages records by the selected quick pay transaction record's id.
//-----------------------------------------------------------------------------
function firstGrid_selectRow(id) {
    var qpTransId = id;
    if(isEmpty(id)) {
        qpTransId = 0;
    }
    loadAllRiskCoverage(qpTransId);
}

//-----------------------------------------------------------------------------
// Load the risks/coverages records by the selected quick pay transaction record's id.
//-----------------------------------------------------------------------------
function loadAllRiskCoverage(qpTransId) {
    var frameObj = getObject("iframeRiskCoverage");

    if (typeof(frameObj) == "undefined") {
        return;
    }
    var url = getAppPath() + "/policymgr/quickpaymgr/loadAllQuickPayDetail.do?"
        + "&process=loadAllRiskCoverage"
        + "&policyId=" + getObjectValue("policyId")
        + "&termBaseId=" + getObjectValue("termBaseId")
        + "&origTransId=" + getObjectValue("origTransId")
        + "&qpTransLogId=" + qpTransId;
    frameObj.src = url;
}