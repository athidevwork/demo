function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLEAR':
            clearFilter();
            break;
        case 'SEARCH':
            searchData();
            break;
        case 'UNLOCK':
            var XMLData = getXMLDataForGridName("policyListGrid");
            var selectedRowCount = XMLData.documentElement.selectNodes("//ROW[CSELECT_IND=-1]").length;
            if (selectedRowCount == 0) {
                handleError(getMessage("pm.maitainUnlockPolicy.noPolicySelected.error"));
            }
            else {
                syncChanges(origpolicyListGrid1, policyListGrid1, "CSELECT_IND='-1' or CSELECT_IND='0'");
                commonOnSubmit('unlockAllPolicy', false, false, false, true);
            }
            break;
    }
}

function clearFilter() {
    setInputFormField("noLoadData","Y");
    getObject("policyNoFilter").value = "";
    getObject("policyHolderNameFilter").value = "";
    commonOnSubmit('loadAllLockedPolicy', false, false, false, true);
}

function searchData() {
    setInputFormField("noLoadData","N");
    commonOnSubmit('loadAllLockedPolicy', false, false, false, true);
}


function policyList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
    first(policyListGrid1);
    selectFirstRowInGrid("policyListGrid");
}