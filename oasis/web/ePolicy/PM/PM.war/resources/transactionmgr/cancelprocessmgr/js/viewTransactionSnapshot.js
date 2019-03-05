//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 14, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 05/08/2013       adeng       143887 - Change loadTransactionByTerm to loadAllTransaction which is the new method in
//                                       action.
// 05/06/2014       fcb         151632 - Added refreshParentB for Risk Relation screen.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Execute when select row in transaction snapshot grid
//-----------------------------------------------------------------------------
function transactionSnapshotGrid_selectRow(id) {
    // Filter out term snapshot
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?process=loadAllTermSnapshot"
        + "&transactionLogId=" + id;
    getObject("termSnapshotIframe").src = url;
}

//-----------------------------------------------------------------------------
// Execute when select row in term snapshot grid
//-----------------------------------------------------------------------------
function termSnapshotGrid_selectRow(id) {
    var transactionLogId = window.frameElement.document.parentWindow.transactionSnapshotGrid1.recordset("id").value;
    // Filter out policy component snapshot
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?process=loadAllPolicyComponentSnapshot"
        + "&transactionLogId=" + transactionLogId + "&policyTermHistoryId=" + id;
    window.frameElement.document.parentWindow.getObject("policyComponentSnapshotIframe").src = url;

    // Filter out risk snapshot
    url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?process=loadAllRiskSnapshot"
        + "&transactionLogId=" + transactionLogId + "&policyTermHistoryId=" + id;
    window.frameElement.document.parentWindow.getObject("riskSnapshotIframe").src = url;

}

//-----------------------------------------------------------------------------
// Execute when select row in risk snapshot grid
//-----------------------------------------------------------------------------
function riskSnapshotGrid_selectRow(id) {
    var transactionLogId = window.frameElement.document.parentWindow.transactionSnapshotGrid1.recordset("id").value;
    // Filter out coverage snapshot
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?process=loadAllCoverageSnapshot"
        + "&transactionLogId=" + transactionLogId + "&policyTermHistoryId="
        + riskSnapshotGrid1.recordset("CPOLICYTERMHISTORYID").value + "&riskBaseRecordId="
        + riskSnapshotGrid1.recordset("CRISKBASERECORDID").value;
    window.frameElement.document.parentWindow.getObject("coverageSnapshotIframe").src = url;
}

//-----------------------------------------------------------------------------
// Execute when select row in coverage snapshot grid
//-----------------------------------------------------------------------------
function coverageSnapshotGrid_selectRow(id) {
    var transactionLogId = window.frameElement.document.parentWindow.transactionSnapshotGrid1.recordset("id").value;
    // Filter out coverage snapshot
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?process=loadAllCoverageComponentSnapshot"
        + "&transactionLogId=" + transactionLogId + "&policyTermHistoryId="
        + coverageSnapshotGrid1.recordset("CPOLICYTERMHISTORYID").value + "&riskBaseRecordId="
        + coverageSnapshotGrid1.recordset("CRISKBASERECORDID").value + "&coverageBaseRecordId="
        + coverageSnapshotGrid1.recordset("CCOVERAGEBASERECORDID").value        ;
    window.frameElement.document.parentWindow.getObject("coverageComponentSnapshotIframe").src = url;
}

//-----------------------------------------------------------------------------
// Handle button click
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case "POLICY":
            var addiInsUrl = getAppPath() + "/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do?"
                + "&process=loadAllAdditionalInsured"
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&termEffectiveFromDate=" + termSnapshotGrid1.recordset("CTERMEFFECTIVEFROMDATE").value
                + "&termEffectiveToDate=" + termSnapshotGrid1.recordset("CTERMEFFECTIVETODATE").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var divPopupId = getRootWindow().openDivPopup("", addiInsUrl, true, true, "", "", 800, "", "", "", "", false);
            break;
        case "TRANS":
            var viewTransactionUrl = getAppPath() + "/transactionmgr/maintainTransaction.do?"
                + "&process=loadAllTransaction"
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + termSnapshotGrid1.recordset("CTERMBASERECORDID").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var divPopupId = getRootWindow().openDivPopup("", viewTransactionUrl, true, true, "", "", 950, 830, 935, 830, "", false);
            break;
        case "PREM":
            var viewPremiumUrl = getAppPath() + "/policymgr/premiummgr/viewPremium.do?"
                + "&process=loadAllPremium"
                + "&transactionId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var divPopupId = getRootWindow().openDivPopup("", viewPremiumUrl, true, true, "", "", 900, 600, 892, 592, "", false);
            break;
        case "DOC_PROCESS":
            var procecessFileUrl = getCSPath() + "/outputmgr/processOutput.do?"
                + "&lastTransactionId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + termSnapshotGrid1.recordset("CTERMBASERECORDID").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var policyId = getParentWindow().policyHeader.policyId;
            if (policyId != "") {
                procecessFileUrl += "&sourceTableName=POLICY&sourceRecordFk=" + policyId + "&subsystemId=PMS";
            }
            var divPopupId = getRootWindow().openDivPopup("", procecessFileUrl, true, true, "", "", "900", "700", "890", "690", "", true);
            break;
        case "RISK_RELATION":
            var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                + "&process=loadAllRiskRelation"
                + "&riskId=" + riskSnapshotGrid1.recordset("ID").value
                + "&riskBaseRecordId=" + riskSnapshotGrid1.recordset("CRISKBASERECORDID").value
                + "&currentRiskTypeCode=" + escape(riskSnapshotGrid1.recordset("CRISKTYPECODE").value)
                + "&endorsementQuoteId=" + riskSnapshotGrid1.recordset("CENDORSEMENTQUOTEID").value
                + "&riskEffectiveFromDate=" + riskSnapshotGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                + "&origRiskEffectiveFromDate=" + riskSnapshotGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                + "&riskEffectiveToDate=" + riskSnapshotGrid1.recordset("CRISKEFFECTIVETODATE").value
                + "&riskCountyCode=" + riskSnapshotGrid1.recordset("CRISKCOUNTY").value
                + "&reverse=N"
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&termEffectiveFromDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVEFROMDATE").value
                + "&termEffectiveToDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVETODATE").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date()
                + "&refreshParentB=N";
            var divPopupId = getRootWindow().openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
            break;
        case "AIE_LT":
            var url = getAppPath() + "/schedulemgr/maintainSchedule.do?"
                + "&process=loadAllSchedule&riskId=" + riskSnapshotGrid1.recordset("ID").value
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&termEffectiveFromDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVEFROMDATE").value
                + "&termEffectiveToDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVETODATE").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var divPopupId = getRootWindow().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case "MANUSCRIPT":
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do?"
                + "&process=loadAllManuscript"
                + "&coverageId=" + coverageSnapshotGrid1.recordset("id").value
                + "&coverageBaseRecordId=" + coverageSnapshotGrid1.recordset("CCOVERAGEBASERECORDID").value
                + "&coverageEffectiveToDate=" + coverageSnapshotGrid1.recordset("CCOVERAGEEFFECTIVETODATE").value
                + "&riskId=" + getParentWindow().document.frames["riskSnapshotIframe"].riskSnapshotGrid1.recordset("ID").value
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&termEffectiveFromDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVEFROMDATE").value
                + "&termEffectiveToDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVETODATE").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var idName = 'R_menuitem_PM_MANU_PUP';
            var mi = getObject(idName);
            if (mi) {
                mi.children[0].style.backgroundImage = '';
            }
            var divPopupId = getRootWindow().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case "PAG":
            var url = getAppPath() + "/schedulemgr/maintainSchedule.do?"
                + "&process=loadAllSchedule&isFromCoverage=Y&coverageId=" + coverageSnapshotGrid1.recordset('ID').value
                + "&riskId=" + getParentWindow().document.frames["riskSnapshotIframe"].riskSnapshotGrid1.recordset("ID").value       
                + "&transactionLogId=" + getParentWindow().transactionSnapshotGrid1.recordset("ID").value
                + "&termBaseRecordId=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset('CTERMBASERECORDID').value
                + "&termEffectiveFromDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVEFROMDATE").value
                + "&termEffectiveToDate=" + getParentWindow().document.frames["termSnapshotIframe"].termSnapshotGrid1.recordset("CTERMEFFECTIVETODATE").value
                + "&policyNo=" + getParentWindow().getObjectValue("policyNo")
                + "&snapshotB=Y&date=" + new Date();
            var divPopupId = getRootWindow().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        default:break;
    }
}

function getParentWindow() {
    return window.frameElement.document.parentWindow;
}

function getRootWindow() {
    var rootWindow = getParentWindow();
    if (rootWindow) {
        rootWindow = rootWindow.frameElement.document.parentWindow;
    }
    return rootWindow;
}
