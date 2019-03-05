/*
 Description: js file for policySummary.jsp

 Author: zlzhu
 Date: Dec 19, 2007


 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 12/19/2007       zlzhu       77329  - Initial version.
 11/04/2014       kxiang      158411 - Modified viewPolicyDetail() to change the send url.
 10/23/2018       dpang       195835 - Adjust iframe height if use jqxgrid.
 -----------------------------------------------------------------------------
 (C) 2007 Delphi Technology, inc. (dti)
 */
var url = getAppPath()+"/policymgr/viewPolicySummary.do?";
//this url will be used by riskSummary.js in another iframe
var coverageUrl="";
var currentPolicyId;

function policySummaryGrid_selectRow(id) {
    //avoid to load same policy twice
    if(currentPolicyId==id){
        return;
    }
    currentPolicyId = id;
    var selectedDataGrid = getXMLDataForGridName("policySummaryGrid");
    var policyId = selectedDataGrid.recordset("ID").value;
    var policyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var termBaseRecordId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
    var termEffectiveFromDate = selectedDataGrid.recordset("CEFFECTIVEFROMDATE").value;
    var termEffectiveToDate = selectedDataGrid.recordset("CEFFECTIVETODATE").value;
    var policyTermId = selectedDataGrid.recordset("CEFFECTIVETODATE").value;
    loadTransaction(policyId,policyNo,termBaseRecordId,termEffectiveFromDate,termEffectiveToDate);
    loadRisk(policyId,policyNo,termBaseRecordId,termEffectiveFromDate,termEffectiveToDate);
    loadAgent(policyId);

}

function loadTransaction(policyId,policyNo,termBaseRecordId,termEffectiveFromDate,termEffectiveToDate) {
    var transSumUrl = url + "process=loadAllTransactionSummary";
    transSumUrl += "&policyId=" + policyId;
    transSumUrl += "&policyNo=" + policyNo;
    transSumUrl += "&termBaseRecordId=" + termBaseRecordId;
    transSumUrl += "&termEffectiveFromDate=" + termEffectiveFromDate;
    transSumUrl += "&termEffectiveToDate=" + termEffectiveToDate;
    getObject("iframeTransaction").src = transSumUrl;
}
function loadRisk(policyId,policyNo,termBaseRecordId,termEffectiveFromDate,termEffectiveToDate){
    var riskSumUrl = url + "process=loadAllRiskSummary";
    //clear old value first
    coverageUrl = "";
    coverageUrl += "&policyId=" + policyId;
    coverageUrl += "&policyNo=" + policyNo;
    coverageUrl += "&termBaseRecordId=" + termBaseRecordId;
    coverageUrl += "&effectiveFromDate=" + termEffectiveFromDate;
    coverageUrl += "&effectiveToDate=" + termEffectiveToDate;
    getObject("iframeRisk").src = riskSumUrl+coverageUrl;
}
function loadAgent(policyId){
    var agentSumUrl = url + "process=loadAllAgentSummary";
    agentSumUrl += "&policyId=" + policyId;
    getObject("iframeAgent").src = agentSumUrl;
}
//this function will be called after riskGrid be loaded,that time coverageUrl
//already had non-empty value.
function getCoverageUrl(){
    return coverageUrl;
}
//open a new window for maintain policy
function viewPolicyDetail(policyNo) {
    var url= getAppPath()+"/policymgr/findPolicy.do?isGlobalSearch=Y&policyNoCriteria=" + policyNo +
            "&termStatusCode=ALL&process=findAllPolicy";
    window.open(url, "", "location=yes,menubar=yes,toolbar=yes,directories=no,resizable=yes,opyhistory=no,scrollbars=yes");
}

function handleOnLoad() {
    if (useJqxGrid) {
        $("#iframeTransaction").height(180);
        $("#iframeRisk").height(220);
        $("#iframeCoverage").height(180);
        $("#iframeAgent").height(180);
    }
}
