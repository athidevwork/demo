//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 06/08/2015       wdang       163197: 1) Removed the function thisopenEntityMiniPopupWin().
//                                      2) Added function isOpenEntityMiniPopupByFrame() which will be called
//                                         by thisopenEntityMiniPopupWin() in common.js file.
// 10/23/2018       dpang       195835: Change window.frameElement.document.parentWindow to getParentWindow() for grid replacement.
//-----------------------------------------------------------------------------
var url = getAppPath()+"/policymgr/viewPolicySummary.do?";
function handleOnLoad(){
}
function riskSummaryGrid_selectRow(id) {
    var selectedDataGrid = getXMLDataForGridName("riskSummaryGrid");
    var riskBaseRecordId = selectedDataGrid.recordset("ID").value;
    loadCoverage(riskBaseRecordId);

}

function loadCoverage(riskBaseRecordId) {
    var coverageSumUrl = url + "process=loadAllCoverageSummary";
    var parentWindow = getParentWindow();
    //coverage has some common parmeters with policy,so we need to load it from policySummary.js
    var coverageUrl = parentWindow.getCoverageUrl();
    coverageSumUrl += coverageUrl;
    coverageSumUrl += "&riskBaseRecordId=" + riskBaseRecordId;
    parentWindow.getObject("iframeCoverage").src = coverageSumUrl;
}
//-----------------------------------------------------------------------------
// Opens the entity mini popup window.The thisopenEntityMiniPopupWin in common.js uses openDivPopup().
// It seems openDivPopup() has some problems when the main page is in a small iframe.
// The iframe will limit the popup div's size.So here we create a new function.
//-----------------------------------------------------------------------------
function isOpenEntityMiniPopupByFrame() {
    return true;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'COPY_ADDR_PHONE':
            var selAddrPhoneUrl = getAppPath() + "/riskmgr/performRiskAddressPhoneCopy.do?" + commonGetMenuQueryString()
                + "&entityId=" + riskSummaryGrid1.recordset("CENTITYID").value
                + "&policyId=" + getObjectValue("policyId")
                + "&policyNo=" + getObjectValue("policyNo")
                + "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate")
                + "&termEffectiveToDate=" + getObjectValue("termEffectiveToDate");
            var divPopupId = window.open(selAddrPhoneUrl, "AddressPhone", 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
            break;
    }
}