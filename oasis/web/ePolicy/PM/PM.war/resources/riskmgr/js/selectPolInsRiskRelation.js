//-----------------------------------------------------------------------------
// Javascript file for selectPolInsRiskRelation.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/01/2012       sxm         133179 - Reset the record set to first after looping through it.
// 03/23/2015       wdang       161448 - 1) Added the new field location to call parentWindow.addPolInsRisks().
//                                       2) Added getLocationPropertyId() to support entity mini popup window for location risk.
// 03/10/2017       wrong       180675 - Modified code to call addPolInsRisks() and addCompInsRisks() in iframe
//                                       in new UI tab style.
// 07/17/2017       wrong       168374 - Modified handleOnButtonClick() to add new field value pcf risk county code and
//                                       pcf risk class code in oPolInsRiskList.
// 11/02/2018       clm         195889 -  Grid replacement
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var oPolInsRiskList = new Array();
    switch (asBtn) {
        case 'DONE':
            var count = 0;
            var riskRelTypeCodeBySel = getObjectValue("riskRelationType");
            var riskRelRateBySel = getObjectValue("riskRelationRate");
            var multiRiskRelation = getObjectValue("multiRiskRelation");
            first(selectPolInsRiskGrid1);
            while (!selectPolInsRiskGrid1.recordset.eof) {
                if (selectPolInsRiskGrid1.recordset("CSELECT_IND").value == -1) {
                    if (multiRiskRelation != "Y") {
                        oPolInsRiskList[count] = {GENERICCODE:selectPolInsRiskGrid1.recordset("CGENERICCODE").value,
                        ENTITYID:selectPolInsRiskGrid1.recordset("CENTITYID").value,
                        LOCATION:selectPolInsRiskGrid1.recordset("CLOCATION").value,
                        RISKBASERECORDID:selectPolInsRiskGrid1.recordset("CRISKBASERECORDID").value,
                        RISKCODE:selectPolInsRiskGrid1.recordset("CRISKCODE").value,
                        RISKCLASSCODE:selectPolInsRiskGrid1.recordset("CRISKCLASSCODE").value,
                        COUNTYCODEUSEDTORATE:selectPolInsRiskGrid1.recordset("CCOUNTYCODEUSEDTORATE").value,
                        PRACTICESTATECODE:selectPolInsRiskGrid1.recordset("CPRACTICESTATECODE").value,
                        RISKRELATIONTYPECODE:riskRelTypeCodeBySel,
                        RISKRELATIONRATE:riskRelRateBySel,
                        PCFRISKCOUNTYCODE:selectPolInsRiskGrid1.recordset("CPCFRISKCOUNTYCODE").value,
                        PCFRISKCLASSCODE:selectPolInsRiskGrid1.recordset("CPCFRISKCLASSCODE").value};
                    }
                    else {
                        var externalId = getObject("policyList");
                        var policyId = getObjectValue("policyList");
                        oPolInsRiskList[count] = {EXTERNALID:externalId,
                        POLICYID:policyId,
                        SOURCERECORDID:selectPolInsRiskGrid1.recordset("CRISKBASERECORDID").value,
                        MULTIRISKENTITYID:selectPolInsRiskGrid1.recordset("CENTITYID").value,
                        RISKRELATIONTYPECODE:riskRelTypeCodeBySel,
                        RISKRELATIONRATE:riskRelRateBySel};
                    }
                    count++;
                }
                next(selectPolInsRiskGrid1);
            }
            first(selectPolInsRiskGrid1);
            if (count == 0) {
                handleError(getMessage("pm.maintainRiskRelation.selectPolInsRisk.noSelection.error"));
            }
            else {
                if (multiRiskRelation != "Y") {
                    //getParentWindow().addPolInsRisks(oPolInsRiskList, true);
                    getReturnCtxOfDivPopUp().addPolInsRisks(oPolInsRiskList, true);
                }
                else {
                    // NOTE: When multiRiskRelation is set to Y, the risk list comes not from the current
                    //       policy, and we need to save them as Company Insured Risks.
                    getReturnCtxOfDivPopUp().addCompInsRisks(oPolInsRiskList, true, true);
                }
                commonOnButtonClick("CLOSE_RO_DIV");
            }
            break;
        case 'CANCEL':
            commonOnButtonClick("CLOSE_RO_DIV");
            break;
    }
}

function selectPolInsRiskForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

function handleOnChange(obj) {
    if (obj.name == "policyList") {
        getParentWindow().openSelectPolInsRiskRelation(getObjectValue("riskEntityId"),
            getObjectValue("multiRiskRelation"), getObjectValue("policyList"));
        commonOnButtonClick("CLOSE_RO_DIV");
    }
    return true;
}

//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    if (isFieldExistsInRecordset(selectPolInsRiskGrid1.recordset, "CLOCATION")) {
        return selectPolInsRiskGrid1.recordset("CLOCATION").value;
    } else {
        return null;
    }
}
