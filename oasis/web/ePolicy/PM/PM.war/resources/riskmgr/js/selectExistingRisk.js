//-----------------------------------------------------------------------------
// Javascript file for selectExistingRisk.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/01/2012       sxm         133179 - Reset the record set to first after looping through it.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    var riskTypeList = new Array();
    switch (asBtn) {
        case 'DONE':
            var count = 0;
            var addedCount = 0;
            var entityIdList = "";
            first(riskGrid1);
            while (!riskGrid1.recordset.eof) {
                if (riskGrid1.recordset("CSELECT_IND").value == -1) {
                    var entityId = riskGrid1.recordset("CENTITYID").value;
                    if (isEmpty(entityIdList)) {
                        entityIdList = entityId;
                    }
                    else {
                        entityIdList += "," + entityId;
                    }
                    count++;
                }
                next(riskGrid1);
            }
            first(riskGrid1);
            if (count == 0) {
                handleError(getMessage("pm.existingRisk.missing.error"));
            }
            else {
                window.frameElement.document.parentWindow.addRiskBatch(entityIdList);
            }
            break;
        case 'CANCEL':
            if (divPopup) {
                commonOnButtonClick('CLOSE_RO_DIV');
            }
            break;
    }
}
function riskGridForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}