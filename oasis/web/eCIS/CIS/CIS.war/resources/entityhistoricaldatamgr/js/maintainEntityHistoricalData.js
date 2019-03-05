//-----------------------------------------------------------------------------
//  Description: Javascript file for Entity Class List Page
//
//  Author: Michael Li
//  Date: 02/21/2011
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  10/28/2011       Michael    for issue126726
//  10/17/2018       ylu        Issue 195835: grid replacement.
//-----------------------------------------------------------------------------

function btnClick(btnID) {
    if (btnID == 'clear') {
        clearCriteria();
    } else if (btnID == 'query') {
        search();
    }
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// Clear the filter criteria.
//-----------------------------------------------------------------------------
function clearCriteria() {
    setObjectValue("filterInsuredFirstName", "");
    setObjectValue("filterInsuredLastName", "");
    setObjectValue("filterEndPolicyTermEffectiveDate", "");
    setObjectValue("filterStartPolicyTermEffectiveDate", "");
    setObjectValue("filterEndPolicyTermExpirationDate", "");
    setObjectValue("filterStartPolicyTermExpirationDate", "");
}

function search() {
    if (getObjectValue("filterInsuredFirstName") == ''
        && getObjectValue("filterInsuredLastName") == ''
        && getObjectValue("filterEndPolicyTermEffectiveDate") == ''
        && getObjectValue("filterStartPolicyTermEffectiveDate") == ''
        && getObjectValue("filterEndPolicyTermExpirationDate") == ''
        && getObjectValue("filterStartPolicyTermExpirationDate") == '') {
        alert(getMessage('ci.entity.message.searchCriteria.enter'));
    } else {
        setObjectValue("process", "loadAllAvailableEntityHistoricalDatas");
        submitFirstForm();
    }
}