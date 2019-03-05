//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Dec 08, 2007
// Author: yhchen
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/23/2015       wdang       161448 - 1) Added the new field location to call parentWindow.handleOnSelectFteRisk().
//                                       2) Added getLocationPropertyId() to support entity mini popup window for location risk.
// 03/10/2017       wrong       180675 - Modified code to call handleSelectFteRisk() in iframe in new UI tab style.
//-----------------------------------------------------------------------------
var seledFteRisks;
var delay = 0;
function handleOnButtonClick(btn) {
    switch (btn) {

        case 'DONE':
            var risks = new Array();
            var riskCount = 0;
            first(selectFteRiskGrid1);
            while (!selectFteRiskGrid1.recordset.eof) {
                if (selectFteRiskGrid1.recordset("CSELECT_IND").value == -1) {
                    risks[riskCount] = {riskBaseRecordId:selectFteRiskGrid1.recordset("ID").value,
                        riskTypeCode:selectFteRiskGrid1.recordset("CRISKTYPECODE").value,
                        addCode:selectFteRiskGrid1.recordset("CGENERICCODE").value,
                        entityId:selectFteRiskGrid1.recordset("CENTITYID").value,
                        location:selectFteRiskGrid1.recordset("CLOCATION").value};
                    riskCount ++;
                }
                next(selectFteRiskGrid1);
            }
            if (riskCount == 0) {
                handleError(getMessage("pm.selectFteRisk.noSelectedRisk.error"));
                return false;
            }else{
                seledFteRisks = risks;
            }

            showProcessingDivPopup();
            setTimeout("handleSelectFteRisk()",delay);

            break;
    }

    return true;
}

function handleSelectFteRisk(){
   getReturnCtxOfDivPopUp().handleOnSelectFteRisk(seledFteRisks);
   closeProcessingDivPopup();
   commonOnButtonClick("CLOSE_DIV");
}
function selectFteRiskList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    if (isFieldExistsInRecordset(selectFteRiskGrid1.recordset, "CLOCATION")) {
        return selectFteRiskGrid1.recordset("CLOCATION").value;
    } else {
        return null;
    }
}