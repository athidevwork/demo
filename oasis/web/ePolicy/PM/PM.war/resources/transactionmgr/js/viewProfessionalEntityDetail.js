//-----------------------------------------------------------------------------
// Javascript file for viewProfessionalEntityTransaction.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 02, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/29/2010       syang      103797 - Removed termBaseId and transactionId from openPremium.
// 08/06/2010       syang      103797 - Modified transactionListGrid_selectRow() to pass policyNo of selected transaction.
// 07/31/2018       mlm        193967 - Refactored to promote and rename from selectFirstRowInTable to
//                                      moveRecordPointerInRecordset into framework.
//-----------------------------------------------------------------------------

function transactionListGrid_selectRow(id) {
    var selectedDataGrid = getXMLDataForGridName("transactionListGrid");
    var ord = selectedDataGrid.recordset("CORD").value;
    // If the ord of selected row is not 2, system willn't retrieve detail. 
    if (ord != 2) {
        return;
    }
    var policyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var originalTransactionLogId = selectedDataGrid.recordset("CORIGINALTRANSACTIONLOGID").value;
    var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
    var termBaseRecordId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
    var termEffectiveFromDate = selectedDataGrid.recordset("CTERMEFFECTIVEDATE").value;
    var termEffectiveToDate = selectedDataGrid.recordset("CTERMEXPIRATIONDATE").value;
    var transDetailUrl = getAppPath() + "/transactionmgr/viewProfessionalEntityDetail.do?process=loadAllProfessionalEntityTransactionDetail" +
                         "&policyNo=" + policyNo +
                         "&transactionLogId=" + transactionLogId +
                         "&originalTransactionLogId=" + originalTransactionLogId +
                         "&termBaseRecordId=" + termBaseRecordId +
                         "&termEffectiveFromDate=" + termEffectiveFromDate +
                         "&termEffectiveToDate=" + termEffectiveToDate;
    getObject("iframeTransDetail").src = transDetailUrl;
}

function handleOnButtonClick(btn) {
    var proceed = true;
    switch (btn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllProfessionalEntityTransaction";
            submitForm(true);
            break;
        case 'PRINT':
            var policyId = getObjectValue("policyId");
            var transactionLogId = getObjectValue("transactionLogId");
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var termEffectiveFromDate = getObjectValue("termEffectiveFromDate");
            var termEffectiveToDate = getObjectValue("termEffectiveToDate");
            var ddPolicyId = getObjectValue("ddPolicyId");
            var ddRiskId = getObjectValue("ddRiskId");
            var ddChgRecord = getObjectValue("ddChgRecord");
            var ddChangeCode = getObjectValue("ddChangeCode");
            var ddChgDelta = getObjectValue("ddChgDelta");

            var paramsObj = new Object();
            paramsObj.reportCode = "PM_PROF_ENTITY_WORKSHEET";
            paramsObj.policyId = policyId;
            paramsObj.transactionLogId = transactionLogId;
            paramsObj.termBaseRecordId = termBaseRecordId;
            paramsObj.termEffectiveFromDate = termEffectiveFromDate;
            paramsObj.termEffectiveToDate = termEffectiveToDate;
            paramsObj.ddPolicyId = ddPolicyId;
            paramsObj.ddRiskId = ddRiskId;
            paramsObj.ddChgRecord = ddChgRecord;
            paramsObj.ddChangeCode = ddChangeCode;
            paramsObj.ddChgDelta = ddChgDelta;
            viewPolicyReport(paramsObj);
            break;
        case 'CLEAR':
            getObject("ddPolicyId").value = -1;
            getObject("ddRiskId").value = -1;
            getObject("ddChgRecord").value = "N";
            getObject("ddChangeCode").value = -1;
            getObject("ddChgDelta").value = "N";
            document.forms[0].process.value = "loadAllProfessionalEntityTransaction";
            submitForm(true);
            break;
        default: break;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Handle the links of two fields policyNo and memeberPremium.
//-----------------------------------------------------------------------------
function handleRowReadyStateReady(table, rowId) {
    handleFieldLink(table, rowId, 'policyNo');
    handleFieldLink(table, rowId, 'memberPremium');
}

function handleFieldLink(table, rowId, fieldName) {
    var fieldNameUpper = "C" + fieldName.toUpperCase();
    var XMLData = getXMLDataForTable(table);
    if (isFieldExistsInRecordset(XMLData.recordset, fieldNameUpper)) {
        var pagesize = getTableProperty(table, "pagesize");
        var pageno = getTableProperty(table, "pageno");
        var moveTo = pagesize * (pageno - 1) + rowId - 1;
        moveRecordPointerInRecordset(table, moveTo, true);
        var relType = XMLData.recordset("CTRANSEFFECTIVEFROMDATE").value;
        if (isEmpty(relType) || relType != "CI") {
            setLinkAsReadyOnly(table, rowId, fieldNameUpper);
        }
        else {
            resetReadOnlyLink(table, rowId, fieldNameUpper);
        }
    }
}
//-----------------------------------------------------------------------------
// Open premium page.
//-----------------------------------------------------------------------------
function openPremium() {
    var selectedDataGrid = getXMLDataForGridName("transactionListGrid");
    var policyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var viewPremiumUrl = getAppPath() + "/policymgr/premiummgr/viewPremium.do?"
        + "&policyNo=" + policyNo + "&process=loadAllPremium";
    var divPopupId = openDivPopup("", viewPremiumUrl, true, true, "15", "", 900, 600, 892, 592, "", false);
}
