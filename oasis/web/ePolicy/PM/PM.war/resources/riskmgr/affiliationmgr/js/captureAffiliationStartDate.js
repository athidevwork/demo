// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2018       clm         195889 -  Grid replacement using closeWindow
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Done':
            var effDate = getObjectValue("effDate");
            if (isEmpty(effDate)) {
                handleError(getMessage("pm.captureAffiliationStartDate.empty.error"));
            }
            else {
                closeWindow(function () {
                    getReturnCtxOfDivPopUp().setInputFormField("capturedAffiStartDate", effDate);
                    getReturnCtxOfDivPopUp().commonAddRow(getReturnCtxOfDivPopUp().getCurrentlySelectedGridId());
                });
            }
            break;
    }

}
