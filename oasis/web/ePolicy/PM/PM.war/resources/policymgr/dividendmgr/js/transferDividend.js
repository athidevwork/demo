//-----------------------------------------------------------------------------
// Transfer Dividend javascript file.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Dec 23, 2013
// Author: awu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'PROCESS':
            if (commonValidateGrid("riskDividendListGrid")) {
                var selectedAuditIdList = setSelectedAudit();
                setInputFormField("transferAuditDividendList", selectedAuditIdList);
                document.forms[0].process.value = "transferDividend";
            }
            else {
                proceed = false;
            }
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function setSelectedAudit() {
    var selectedRecords = riskDividendListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    var selectedAudit = "";
    var size = selectedRecords.length;
    for (var i = 0; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var auditId = currentRecord.selectNodes("CPOLICYDIVIDENDAUDITID")(0).text;
        if (i == 0) {
            selectedAudit = auditId;
        }
        else {
            selectedAudit = selectedAudit + "," + auditId;
        }
    }
    return selectedAudit;
}

function riskDividendList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}