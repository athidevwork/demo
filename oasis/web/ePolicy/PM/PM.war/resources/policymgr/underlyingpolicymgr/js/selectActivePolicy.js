// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/09/2015       ssheng    166602 - Added parameter CSOURCERECORDID in function handleOnButtonClick.
// 03/10/2017       wli       180675 - Changed "window.frameElement.document.parentWindow" to "getReturnCtxOfDivPopUp()"
//                                     to call handleOnSelectPolicyDone for UI change.
// 10/15/2018       wrong     188391 - Modified handleOnButtonClick() to support for Underlying coverage.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            getReturnCtxOfDivPopUp().handleOnSelectPolicyDone(
                activePolicyListGrid1.recordset("ID").value,
                activePolicyListGrid1.recordset("CEXTERNALID").value,
                activePolicyListGrid1.recordset("CSOURCERECORDID").value,
                activePolicyListGrid1.recordset("CENTITYID").value);
            commonOnButtonClick("CLOSE_RO_DIV");
            break;        
    }
}