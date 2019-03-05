//-----------------------------------------------------------------------------
// Javascript file for maintainNationalProgram.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   May 25, 2011
// Author: Dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/09/2014       wdang       156038 - Modified handleOnSubmit() to add "riskId" as an input field.
// 11/27/2018       clm         195889 - change the RiskBaseRecordId to riskBaseRecordId in nationalProgramListGrid_setInitialValues
//-----------------------------------------------------------------------------

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllNationalProgram";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function nationalProgramListGrid_setInitialValues() {
    var url = getAppPath() +
            "/riskmgr/nationalprogrammgr/maintainNationalProgram.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForAddNationalProgram"
            + "&riskBaseRecordId=" + getObjectValue("riskBaseRecordId")
            + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate")
            + "&riskEffectiveToDate=" + getObjectValue("riskEffectiveToDate");
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}
