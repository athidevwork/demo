//-----------------------------------------------------------------------------
// Javascript file for viewInsuredInfo.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   June 29, 2012
// Author: tcheng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
function displayPolicy(pk) {
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    var selectedPolicyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var selectedRiskId = selectedDataGrid.recordset("CRISKID").value;
    var selectedpolicyId = selectedDataGrid.recordset("CPOLICYTERMHISTORYID").value;
    var policyURL = getAppPath() + "/riskmgr/maintainRisk.do?policyNo=" + selectedPolicyNo +
            "&policyTermHistoryId=" + selectedpolicyId + "&riskId=" + selectedRiskId +
            "&date=" + new Date();
    window.open(policyURL, "newWindow", "");
}