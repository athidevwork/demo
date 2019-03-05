/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 03/11/2008       awu        152695 - Modified find to open the Entity Select Search page.
 10/26/2018       ryzhao     196166 - Added handleOnButtonClick() to refresh policy page when this popup page is closed.
 12/04/2018       clm        195889 - Grid Replacement - using getParentWindow
 ----------------------------------------------------------------------------
 */
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            showProcessingDivPopup();
            // Enable and hide all disabled fields in a form before submit
            enableFieldsForSubmit(document.forms[0]);
            setObjectValue("process","changePolicyAdministrator");
            break;
    }
    return proceed;
}
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE_DIV':
            getParentWindow().refreshPage();
            break;
    }
}
function find(){
    //create two fields to store the result from the Entity Select Search window,then you can use it
    setInputFormField("newEntityId", 0);
    setInputFormField("newEntityName", "");

    var url = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=newEntityId" +
            "&entityFullNameFieldName=newEntityName" +
            "&eventName=handleOnSelectEntity()";
    openEntitySearchWindow(url);
}
function handleOnSelectEntity(){
    isChanged = true;
    setObjectValue("newAdmin", getObjectValue("newEntityName"));
    setObjectValue("policyHolderNameEntityId", getObjectValue("newEntityId"));
}