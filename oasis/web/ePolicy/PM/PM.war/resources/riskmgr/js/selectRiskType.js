// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Select':
            var riskTypeCode = selectRiskTypeGrid1.recordset("CRISKTYPECODE").value;
            var parentWindow = getParentWindow();
            if (getObjectValue("openWhichWindow") == "existing") {
                //set flag to make it sure that it open "selectExistingRisk"
                parentWindow.setOpenWindow(getObjectValue("openWhichWindow"));
                //               window.opener.document.openWhichWindow.value = getObjectValue("openWhichWindow");
                parentWindow.handleOnSelectRiskType(btn, riskTypeCode);
            }
            else {
                parentWindow.handleOnSelectRiskType(btn, riskTypeCode);
            }
            closeThisDivPopup(true);
            break;

        case 'Cancel':
            closeThisDivPopup(true);
            break;
    }

    return true;
}
