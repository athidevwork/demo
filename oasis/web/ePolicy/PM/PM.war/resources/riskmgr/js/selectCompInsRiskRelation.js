//-----------------------------------------------------------------------------
// Javascript file for selectCompInsRiskRelation.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/01/2012       sxm         133179 - Reset the record set to first after looping through it.
// 03/10/2017       wrong       180675 - Modified code to call addCompInsRisks() in iframe in
//                                       new UI tab style.
// 11/02/2018       clm         195889 -  Grid replacement - remove divPopup assignment statement
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var oCompInsRiskList = new Array();
    switch (asBtn) {
        case 'DONE':
            var count = 0;
            first(selectCompInsRiskGrid1);
            while (!selectCompInsRiskGrid1.recordset.eof) {
                if (selectCompInsRiskGrid1.recordset("CSELECT_IND").value == -1) {
                    oCompInsRiskList[count] = {EXTERNALID:selectCompInsRiskGrid1.recordset("CEXTERNALID").value,
                    POLICYID:selectCompInsRiskGrid1.recordset("CPOLICYID").value,
                    SOURCERECORDID:selectCompInsRiskGrid1.recordset("CSOURCERECORDID").value};
                    count++;
                }
                next(selectCompInsRiskGrid1);
            }
            first(selectCompInsRiskGrid1);
            if (count == 0) {
                handleError(getMessage("pm.maintainRiskRelation.selectCompInsRisk.noSelection.error"));
            }
            else {
                getReturnCtxOfDivPopUp().addCompInsRisks(oCompInsRiskList, true, false);
                commonOnButtonClick("CLOSE_RO_DIV");
            }
            break;
        case 'CANCEL':
            commonOnButtonClick("CLOSE_RO_DIV");
            break;
    }
}

function selectCompInsRiskForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
