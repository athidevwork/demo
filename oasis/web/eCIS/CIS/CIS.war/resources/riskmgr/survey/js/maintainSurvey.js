//-----------------------------------------------------------------------------
// Functions to support Survey Tracking page.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 04/18/2018       ylu         109179: refactor Audit history popup page: to use correct field name to pass parameter
// 10/19/2018       dzou        grid replacement
//-----------------------------------------------------------------------------

var hasErrorMessages ;
//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function surveyListGrid_selectRow(pk) {
    rowid = pk;
    getRow(surveyListGrid1, pk);
}



//-----------------------------------------------------------------------------
// This function is to set initial values for new added row in grid.
//-----------------------------------------------------------------------------
function surveyListGrid_setInitialValues(){
    surveyListGrid1.recordset("CENTITYID").value = getObjectValue("pk");
    getInitSurveyValueViaAjax();

}
function getInitSurveyValueViaAjax(){
    var url = getAppPath()+"/maintainSurvey.do?process=getInitialValuesForNewSurvey"+
             "&entityId="+getObjectValue("entityId")+"&date="+new Date();
      new AJAXRequest("get", url, '', handleOnGetInitValue, false);
}

function handleOnGetInitValue(ajax){
    currentlySelectedGridId = "surveyListGrid";
    commonHandleOnGetInitialValues(ajax);
}

function btnClick(action){
    var actionUpper = action.toUpperCase();
    switch (actionUpper) {
        case 'SELECT':
            surveyListGrid_updatenode("CSELECTIND", -1);
            break;
        case 'DESELECT':
            surveyListGrid_updatenode("CSELECTIND", 0);
            break;
        case 'SAVE':
            var passed = commonValidateGrid("surveyListGrid");
            if (passed) {
                setObjectValue("process", "saveAllSurvey");
                surveyListGrid_update();
            }
            break;
        case 'ADD':
            commonOnButtonClick('ADD');
            break;
        case 'REFRESH':
            if (isPageDataChanged()) {
                if (!confirm(ciRefreshPageConfirmation)) {
                    return;
                }
            }
            setObjectValue("process", "loadAllSurvey");
            submitFirstForm();
            break;
        case 'ENTITY':
            goToEntityModify(getObjectValue("pk"),
                    getObjectValue("entityType"));
            break;
        case 'PHONENUMBER':
        case 'ENTITYCLASS' :
        case 'ENTITYROLE':
        case 'VENDOR':
        case 'VENDORADDRESS':
        case 'ADDRESS':
        // Go to the appropriate page.
            goToEntityModule(btnID, getObjectValue("pk"),
                getObjectValue("entityName"),
                getObjectValue("entityType"));
            break;
    }
 }

function checkForm() {
    // do nothing ..
}

function viewSurveyHistory(rmSurveyId) {
    openAuditTrailPopup('SurveyHistory', "OASIS_AUDIT_TRAIL", "", "RM_SURVEY", rmSurveyId);
}
