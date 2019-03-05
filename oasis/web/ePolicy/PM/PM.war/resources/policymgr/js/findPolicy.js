/*
  Description: js file for findPolicy.jsp

  Author: gjlong
  Date: Jan 31, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  10/08/2010       dzhang      112464: added function processDeps, hideAllLayers and checkFieldLayerDep.
  08/02/2011       xnie        123368: Reset policy holder name when search criteria has no
                                       a valid policy holder entity.
  05/04/2012       bhong       129528 - Added processQuestionnaire
  09/27/2012       xnie        133766 - Added reRatePolicy, onDemandReRate, batchReRate, handleOnBatchReRate,
                                        handleOnOnDemandReRate, and getTermList.
  12/11/2012       awu         137609 - 1. Modified maintainMailing to use ajax request to create policy mailing.
                                        2. Added handleCreatePolicyMailing to load the policy mailing popup page.
  12/12/2012       xnie        139838 - Modified handleOnBatchReRate to add requestId to message.
  01/29/2013       adeng       141368 - Modified handleCreatePolicyMailing to remove the contentWidth and contentHeight
                                        which were passed while rendering the divPopup.
  07/12/2013       adeng       144614 - 1. Modified findPolicy() to reset riskEntityName when search criteria has no
                                        a valid risk entity and add some logic to make it more humanized when the entity
                                        is invalid.
                                        2. Removed selectPolicyHolder() and selectRisk(), merge them into find() method.
                                        3. Modified handleOnChange() to clear the policyHolderNameEntityId/riskEntityId
                                        when policyHolderName/riskEntityName is changed.
  12/30/2014       jyang       157750 - Modified saveUserView() to encode the view name before append it to URL incase
                                        the user view name has special characters.
  03/25/2016       eyin        170323 - 1. Modify displayPolicy() to pass the value of new added buttons to URL.
                                        2. Add function processBackToList to sort grid based on orgSortColumn/orgSortType.
                                        3. Add function processBackToListGoToPage to highlight original selected policy.
                                        4. Add function updatePolicyListSession to update policyList in session.
  04/21/2016       huixu       Issue#169769 provide another way to export excel from XMLData
  12/20/2016       ssheng      181469 - Modified function handleOnKeyDown: When policyHolderName is not null and
                                        policyHolderNameEntityId is null or riskEntityName is not null and
                                        riskEntityId is null, system will not invoke findPolicy function.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  08/29/2017       wrong       187744 - 1) Modified processBackToListGoToPage() to add logic to process selecting
                                           relative row when back to list page.
  09/11/2017       kshen       Grid replacement. Changed setSelectedPolicies and processBackToList to support jqxGrid.
  10/12/2017       kshen       Grid replacement. Changed handleOnKeyDown to accept parameter event to support Firefox.
                                                 Changed processQuestionnaireDone to support jqxGrid.
  12/12/2017       kshen       Grid replacement.
  12/25/2017       wrong       185880 - Modified handleOnChange to set transactionStatus field value to 'ALL' when
                                        switching user view field value to 'Select'.
  12/28/2017       wrong       190427 - Modified clearThisFormFields() to exclude Token field situation.
  11/30/2018       xjli        195889 - Reflect grid replacement project changes.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
 */

var policyHolderName = "policyHolderName";
var policyHolderFK = "policyHolderNameEntityId";
//"policyHolderFK";
var riskEntityFK = "riskEntityId";
var riskEntityName = "riskEntityName";
var selectedPolicyNos = "";

function displayPolicySearchCriteria() {
    showProcessingImgIndicator();
    document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do";
    document.forms[0].process.value = "displaySearchCriteria";
    submitFirstForm();
}

function findPolicy() {
    var policyNo = "";
    if (hasObject("policyNoCriteria")) {
        policyNo = getObjectValue("policyNoCriteria");
    }
    if (hasObject(policyHolderFK)) {
        var polHolderFk = getObjectValue(policyHolderFK);
        var polHolderFk = getObjectValue(policyHolderFK);
        var polHolderName = getObjectValue(policyHolderName);
        if (isEmpty(polHolderFk) && !isEmpty(polHolderName)) {
            var params = new Array("policy holder", "policy holder");
            if (confirm(getMessage("pm.findPolicy.invalidEntityName.warning", params))) {
                setObjectValue(policyHolderName, "");
            }
            else {
                return;
            }
        }
    }
    if (hasObject(riskEntityFK)) {
        var rskEntityFk = getObjectValue(riskEntityFK);
        var rskEntityName = getObjectValue(riskEntityName);
        if (isEmpty(rskEntityFk) && !isEmpty(rskEntityName)) {
            var params = new Array("risk", "risk");
            if (confirm(getMessage("pm.findPolicy.invalidEntityName.warning", params))) {
                setObjectValue(riskEntityName, "");
            }
            else {
                return;
            }
        }
    }
    if (validatePolicyNoCriteria(policyNo) && validateLatestTermAndTermStatus()) {
        showProcessingImgIndicator();
        document.forms[0].showMoreFlag.value = 'Y';
        showMoreOrLess();

        document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do";
        document.forms[0].process.value = "findAllPolicy";
        submitFirstForm();
    }
}

function handleOnKeyDown(field, event){
        var evt = fixEvent(event);
    if (dti.oasis.ui.isEnterKeyEvent(evt) &&
        !dti.oasis.grid.hasOpenedJqxGridMenu() &&
        !dti.oasis.grid.isJqxGridMenuElement(evt.target)) {

        var fieldName = field.name;
        var fieldValue = field.value;
        if (fieldName == policyHolderName || fieldName == riskEntityName) {
            baseOnChange('ST', evt);
            if (!(isEmpty(getObjectValue(policyHolderFK)) && !isEmpty(getObjectValue(policyHolderName))) && !(isEmpty(getObjectValue(riskEntityFK)) && !isEmpty(getObjectValue(riskEntityName)))) {
                // Automatically search for the matching policies if the "Enter" key is pressed.
                findPolicy();
                return false;
            }
        }
        else {
            // Automatically search for the matching policies if the "Enter" key is pressed.
            findPolicy();
            return false;
        }
    }

    return true;
}

function handleOnBlur(field) {

    // the field validation for findPolicy page.
    // to validate the date fields
    //findPolicyFieldValidation(field);
}

function find(fieldId) {
    if (fieldId == policyHolderName) {
        openEntitySelectWinFullName(policyHolderFK, policyHolderName);
    }
    if (fieldId == riskEntityName) {
        openEntitySelectWinFullName(riskEntityFK, riskEntityName);
    }
}

function showMoreOrLess()
{
    if (getObject("showMoreFlag").value == 'N') {
        window.document.forms[0].showMoreFlag.value = 'Y';
        // getObject("linkMoreLess").innerText ="Less Criteria";
        getObject("PM_SPOL_MORE").value = getMessage("pm.maintainPolicy.search.lessCriteria");

    }
    else {
        window.document.forms[0].showMoreFlag.value = 'N';
        // getObject("linkMoreLess").innerText="More Criteria";
        getObject("PM_SPOL_MORE").value = getMessage("pm.maintainPolicy.search.moreCriteria");

    }

    if (eval("window.processDeps")) {
       processDeps();
    }

}
function handleOnChange(field) {
    findPolicyFieldValidation(field);
    var fieldName = field.name;
    var fieldValue = field.value;
    if (fieldName == policyHolderName) {
        setObjectValue(policyHolderFK, "");
    }
    if (fieldName == riskEntityName) {
        setObjectValue(riskEntityFK, "");
    }

    switch (fieldName) {
        case "pmUserViewId":
            if (fieldValue == "" || (fieldValue == '-1')) {
                showMoreFlag = document.forms[0].showMoreFlag.value;
                clearThisFormFields(document.forms[0], true);
                document.forms[0].termStatusCode.value = 'ALL';
                document.forms[0].transactionStatus.value = 'ALL';
                document.forms[0].policyCycle[0].selected = true;
                document.forms[0].lastTermB.value = 'N';
                document.forms[0].showMoreFlag.value = showMoreFlag;

            }
            else {
                document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do?"
                    + "&process=loadUserView";

                submitFirstForm();
            }
            break;
    }
}
function clearThisFormFields(theForm, clearHidden) {
    if (window.clearThisFormFields.arguments.length < 2)
        clearHidden = false;
    /* Clear all fields. */
    var coll = theForm.elements;
    var i = 0;
    var list = '';
    for (i = 0; i < coll.length; i++) {
        var obj = coll[i];
        list += 'type for obj ' + i + ' = ' + obj.type + ';  ';
        switch (coll[i].type) {
            case 'select-multiple':
                coll[i].value = '';
                var x = coll[i].options.length;
                if (coll[i].options[0].value == "")
                {
                    coll[i].options[0].selected = true;
                }
                else {
                    coll[i].options[0].selected = false;
                }
                for (var y = 1; y < x; y++) {
                    coll[i].options[y].selected = false;
                }

                break;
            case 'text':
            case 'textarea':
            case 'select-multiple':
            case 'file':
                coll[i].value = '';
                break;
            case 'select-one':
                if (coll[i].options[0].value == "") {
                    coll[i].options[0].selected = true;
                }
                else {
                    coll[i].value = '-1';
                }
                break;
            case 'checkbox':
            case 'radio':
                coll[i].checked = false;
                break;
            case 'hidden':
                if (coll[i].name != 'org.apache.struts.taglib.html.TOKEN') {
                    if (clearHidden) coll[i].value = '';
                }
                break;
        }
    }
}
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "findAllPolicy";
            isChanged = false;
            break;

        default:
            proceed = false;
    }
    return proceed;
}

function displayPolicy(pk) {
    showProcessingImgIndicator();
    selectRow(getCurrentlySelectedGridId(), pk);
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    var selectedPolicyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var policyURL = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + selectedPolicyNo + "&policyTermHistoryId=" + pk
                    +'&orgSortColumn='+(getTableProperty(getTableForXMLData(findPolicyListGrid1), "currentSortColumn") ?
                            getTableProperty(getTableForXMLData(findPolicyListGrid1), "currentSortColumn"):'')
                    +'&orgSortType='+(getTableProperty(getTableForXMLData(findPolicyListGrid1), "currentSortType") ?
                            getTableProperty(getTableForXMLData(findPolicyListGrid1), "currentSortType"):'')
                    +'&orgSortOrder='+(getTableProperty(getTableForXMLData(findPolicyListGrid1), "sortOrder") ?
                            getTableProperty(getTableForXMLData(findPolicyListGrid1), "sortOrder"):'')
                    +'&orgRowId=' + pk;

    if (findPolicyListGridSorted) {
        updatePolicyListSession(pk);
    }

    setWindowLocation(policyURL);
}

function exportFromXML(gridId){
    return true;
}

function findPolicyFieldValidation(field) {

    if ((field.name == "termEffectiveFromDate") ||
        (field.name == "termEffectiveToDate")) {
        validateDateFields("termEffectiveFromDate", "termEffectiveToDate", field);
    }

    if ((field.name == "termExpirationFromDate") ||
        (field.name == "termExpirationToDate")) {
        validateDateFields("termExpirationFromDate", "termExpirationToDate", field);
    }

    if (field.name == "riskEffectiveFromDate" ||
        field.name == "riskEffectiveToDate") {
        validateDateFields("riskEffectiveFromDate", "riskEffectiveToDate", field);
    }

    if (field.name == "coverageEffectiveFromDate" ||
        field.name == "coverageEffectiveToDate") {
        validateDateFields("coverageEffectiveFromDate", "coverageEffectiveToDate", field);
    }

    if (field.name == "componentEffectiveFromDate" ||
        field.name == "componentEffectiveToDate") {
        validateDateFields("componentEffectiveFromDate", "componentEffectiveToDate", field);
    }

}


// if dateField1 > date2Field2, the code reset the value for resetField
// and raise an alert too
// the caller is reposnible to pass in 2 date field Ids.

function validateDateFields(dateFieldId1, dateFieldId2, resetField) {
    var date1;
    var date2;

    var value1
    var value2

    var dateField1 = getObject(dateFieldId1);
    var dateField2 = getObject(dateFieldId2);

    if ((isObject(dateField1)) && (isObject(dateField2))) {
        value1 = dateField1.value;
        value2 = dateField2.value;

        if ((!isNull(value1)) && (!isNull(value1))) {

            date1 = getRealDate(value1);
            date2 = getRealDate(value2);

            if ((!isNull(date1)) && (!isNull(date2)) && (date1 > date2 )) {

                handleError("\"" + getLabel(dateField1, 0) + "\" value can not be greater than \"" + getLabel(dateField2, 0) + "\" value.",
                    resetField.name);

                if (resetField == dateField1) {
                    dateField1.value = "";
                }
                else {
                    dateField2.value = "";
                }

            }
        }
    }

}
// find user view Id by name, return "" if not found.
function findUserViewByName(userViewName) {
    var oUserView = getObject("pmUserViewId");
    var userViewId = "";
    var len = oUserView.length;
    for (var i = 0; i < len; i++) {
        if (oUserView.options[i].text == userViewName) {
            userViewId = oUserView.options[i].value;
            break;
        }
    }
    return userViewId;
}
function saveUserView() {

    var updateind;
    var oUserView = getObject("pmUserViewId");
    var oldUserViewId = oUserView.value;
    var oldUserViewName = oUserView.options[oUserView.selectedIndex].text;
    if ((oUserView.value == -1) || (oUserView.value == "")) {
        oldUserViewName = "";
    }

    var userViewName = prompt("User view name: ", oldUserViewName);
    if (userViewName == null) {
        return;
    }
    while (trim(userViewName) == "") {
        handleError(getMessage("pm.maintainUserView.blank.warning"));
        userViewName = prompt("User view name: ", oldUserViewName);
        if (userViewName == null) {
            return;
        }
    }
    var newUserViewName = trim(userViewName);

    if (newUserViewName.length > 30) {
        handleError(getMessage("pm.maintainUserView.lengthCheck.warning"));
        return;
    }
    var params = new Array("\"" + newUserViewName + "\"");
    var existedUserViewId = findUserViewByName(newUserViewName);
    //no same name user view, just do insert a new with no prompt
    if (existedUserViewId == "") {
        if (oldUserViewName == "") {
            updateind = "I";
        }
        else {
            if (confirm(getMessage("pm.maintainUserView.create.warning", params))) {
                updateind = "I";
            }
            else {
                updateind = "Y"
            }
        }
    }
    else {
        //existed user view with the same name,if the similar is orig, do update with no prompt.
        if (existedUserViewId == oldUserViewId) {
            updateind = "Y";
        }
        //existed another user view with the same name
        else {
            if (confirm(getMessage("pm.maintainUserView.overwrite.warning", params))) {
                updateind = "Y";
            }
            else {
                return;
            }
        }
    }

    if (typeof findPolicyListGrid1 != "undefined") {
        document.forms[0].txtXML.value = getChanges(findPolicyListGrid1);
    }
    document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do?"
        + "&process=saveUserView" + "&userViewLongDescription=" + encodeURIComponent(newUserViewName) + "&updateind=" + updateind + "&existedUserViewId=" + existedUserViewId;
    submitFirstForm();
}

function deleteUserView() {
    var oUserView = getObject("pmUserViewId");
    if (oUserView.value == "") {
        return;
    }
    else {

        var selectedUserView = oUserView.options[oUserView.selectedIndex].text;
        var params = new Array("\"" + selectedUserView + "\"");
        if (confirm(getMessage("pm.maintainUserView.delete.warning", params))) {
            document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do?"
                + "&process=deleteUserView";
            submitFirstForm();
        }
        else {
            return;
        }
    }
}

function addAdditionalSql() {
    var addAddtionalSqlUrl = getAppPath() + "/policymgr/userviewmgr/manageAddtionalSql.do?" + "additionalSql=" + getObjectValue("additionalSql");
    var divPopupId = openDivPopup("", addAddtionalSqlUrl, true, true, "", "", 750, 550, 740, 542, "", false);
}

function handleOnLoad() {
    // System defaults to show "More >>>" and set showMoreFlag to 'N'
    getObject("PM_SPOL_MORE").value = getMessage("pm.maintainPolicy.search.moreCriteria");
    window.document.forms[0].showMoreFlag.value = 'N';
    processDeps();
    // disable selectAll checkbox
    if (hasObject("HCSELECT_IND")) {
        getObject("HCSELECT_IND").disabled = false;
    }

    if (hasObject("returnToList") &&
            getObjectValue("returnToList") == "Y" &&
            getObject("orgSortColumn") &&
            getObject("orgSortType") &&
            getObject("orgSortOrder") &&
            getObject("orgRowId")) {
        // invoke the next process when the table is ready
        var testCode = 'getTableProperty(getTableForGrid("findPolicyListGrid"), "isUserReadyStateReadyComplete")';
        var callbackCode = 'processBackToList();';
        executeWhenTestSucceeds(testCode, callbackCode, 50);
    }
}

function setAdditionalSql(additionalSql) {
    document.forms[0].additionalSql.value = additionalSql;
}
function processMailing() {
    if (typeof(findPolicyListGrid1) == 'undefined') {
        handleError(getMessage("pm.findPolicy.processMailing.noSelection.error"));
    }
    else {
        var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
        if (selectedRecords.length == 0) {
            handleError(getMessage("pm.findPolicy.processMailing.noSelection.error"));
        }
        else {
            setSelectedPolicies();
            //go to select mailing type page
            var selectMailingTypeUrl = getAppPath() + "/policymgr/mailingmgr/selectMailingType.do?"
                + "process=selectMailingType";
            var divPopupId = openDivPopup("", selectMailingTypeUrl, true, true, "", "", "", "", "", "", "", false);

        }
    }
}
//go to Process Policy Mailing page
function maintainMailing(productMailingId) {
    var processMailingUrl = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
            + "process=createPolicyMailingForPolicy&productMailingId=" + productMailingId
            + "&selectedPolicyNos=" + selectedPolicyNos;
    postAjaxSubmit(processMailingUrl, "createPolicyMailingForPolicy", false, false, handleCreatePolicyMailing);
}

function handleCreatePolicyMailing(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var policyMailingId = oValueList[0]["ID"];
                var loadMailingURL = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
                        + "process=loadAllPolicyMailing&toBeSelectedMailingEvent="
                        + policyMailingId + "&pageType=popup&date=" + new Date();
                var divPopupId = openDivPopup("", loadMailingURL, true, true, "", "", 950, 1100, "", "", "", false);
            }
        }
    }
}

function setSelectedPolicies() {
    var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    var size = selectedRecords.length;
    selectedPolicyNos = "";
    for (var i = 0; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var policyNo = currentRecord.selectNodes("CPOLICYNO").item(0).text;
        if (i == 0) {
            selectedPolicyNos = policyNo;
        }
        else {
            selectedPolicyNos = selectedPolicyNos + "," + policyNo;
        }
    }
}
function findPolicyList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
//-----------------------------------------------------------------------------
// Fix issue 97811.
// System should prompt a warning message when the user selectes last term and WIP Term.
//-----------------------------------------------------------------------------
function validateLatestTermAndTermStatus() {
    if (getObject("lastTermB") && getObject("termStatusCode")) {
        var lastTermB = getObjectValue("lastTermB");
        var termStatusCode = getObjectValue("termStatusCode");
        if (!isEmpty(lastTermB) && !isEmpty(termStatusCode) && (lastTermB == 'Y') && (termStatusCode == 'WIP')) {
            handleError(getMessage("pm.findPolicy.mutuallyExclusive.conditions.error"));
            return false;
        }
    }
    return true;
}

function hideAllLayers() {
    var obj = getObject("PM_FIND_POLICY_MORE_CRITERIA");
    if (obj) {
        if (obj.length) {
            for (var i = 0; i < obj.length; i++)
                hideShowElementByClassName(obj[i], true);
        }
        else
            hideShowElementByClassName(obj, true);
    }
}

function checkFieldLayerDep(fieldObj) {
    if (!fieldObj) return;
    var obj;
    if (fieldObj.name == "showMoreFlag") {
        if (getObjectValue("showMoreFlag") == "Y") {
            obj = getObject("PM_FIND_POLICY_MORE_CRITERIA");
            if (obj) {
                if (obj.length) {
                    for (var i = 0; i < obj.length; i++)
                        hideShowElementByClassName(obj[i], false);
                }
                else
                    hideShowElementByClassName(obj, false);
            }

        }
    }
}

function processDeps() {
    if(!dti.oasis.page.useJqxGrid()) {
        hideAllLayers();
        checkFieldLayerDep(getObject("showMoreFlag"));
    }else{
        if (getObjectValue("showMoreFlag") == "Y") {
            $('tr#PM_FIND_POLICY_MORE_CRITERIA').removeClass('dti-hide');
        }else {
            $('tr#PM_FIND_POLICY_MORE_CRITERIA').addClass('dti-hide');
        }
    }

}

function processQuestionnaire() {
    if (typeof(findPolicyListGrid1) == 'undefined') {
        handleError(getMessage("pm.findPolicy.processQuestionnaire.noSelection.error"));
    }
    else {
        var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
        if (selectedRecords.length == 0) {
            handleError(getMessage("pm.findPolicy.processQuestionnaire.noSelection.error"));
        }
        else {
            var url = getAppPath() + "/policymgr/findPolicy.do";
            document.forms[0].txtXML.value = getSelectedOnly(findPolicyListGrid1);
            postAjaxSubmit(url, "processQuestionnaireRequest", false, false, processQuestionnaireDone);
        }
    }
}

function processQuestionnaireDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            alert(getMessage("pm.findPolicy.processQuestionnaire.done.info"));
            // Uncheck selected rows

            if (dti.oasis.page.useJqxGrid()) {
                dti.oasis.grid.deselectAll("findPolicyListGrid");
            } else {
                getObject("chkCSELECT_ALL").checked = false;
                updateAllSelectInd("DESELECT", "findPolicyListGrid");
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Load all rerate options: On-demand/Batch/Report.
//-----------------------------------------------------------------------------
function reRatePolicy() {
    var url = getAppPath() + "/policymgr/loadReRateOptions.do?process=loadReRateOptions&date=" + new Date();
    openDivPopup("", url, true, true, "", "", "", "", "", "", "", false,"","",false);
}

function openReRateResult(proccess, workflowInstanceId) {
    var url = getAppPath() + "/policymgr/massReRate.do?process=";
    if (proccess) {
        url += proccess + "&workflowInstanceId=" + workflowInstanceId;
    }
    else {
        url += "openReRateResult";
    }
    url += "&date=" + new Date();
    openDivPopup("", url, true, true, "", "", "800", "700", "", "", "", false,"","",false);
}

//-----------------------------------------------------------------------------
// On-demand rerate.
//-----------------------------------------------------------------------------
function onDemandReRate() {
    if (typeof(findPolicyListGrid1) == 'undefined') {
        handleError(getMessage("pm.reRatePolicy.reRate.noSelection.error"));
    }
    else {
        var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
        var selectedRecordsCount = selectedRecords.length;
        if (selectedRecordsCount == 0) {
            handleError(getMessage("pm.reRatePolicy.reRate.noSelection.error"));
        }
        else {
            var osBypassBilling = getSysParmValue("PM_MAX_ONDEMAND_RATE", "100");
            if (!isNaN(osBypassBilling) && selectedRecordsCount > parseInt(osBypassBilling)) {
                var paras = new Array(osBypassBilling);
                handleError(getMessage("pm.reRatePolicy.reRate.maxOnDemandNumber.error", paras));
            }
            else {
                var url = getAppPath() + "/policymgr/massReRate.do";
                setInputFormField("termList", getTermList());
                postAjaxSubmit(url, "reRateOnDemand", false, false, handleOnOnDemandReRate);
            }
        }
    }
}

//--------------------------------------------------------------
// Callback function of onDemandReRate, it will invoke the workflow if any policies need to rerate.
//--------------------------------------------------------------
function handleOnOnDemandReRate(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            //if there is any exception, return and do nothing
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                invokeOnDemandReRateWorkflow(oValueList[0]["WORKFLOWINSTANCEID"], oValueList[0]["WORKFLOWSTATE"]);
            }
        }
    }
}

function invokeOnDemandReRateWorkflow(workflowInstanceId, workflowState) {
    var url = getAppPath() + "/workflowmgr/workflow.do?process=processNonPolicyWorkflow&" +
            "workflowInstanceId=" + workflowInstanceId +
            "&workflowState=" + workflowState;
    var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
    return true;
}

//-----------------------------------------------------------------------------
// Batch rerate.
//-----------------------------------------------------------------------------
function batchReRate() {
    if (typeof(findPolicyListGrid1) == 'undefined') {
        handleError(getMessage("pm.reRatePolicy.reRate.noSelection.error"));
    }
    else {
        var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
        if (selectedRecords.length == 0) {
            handleError(getMessage("pm.reRatePolicy.reRate.noSelection.error"));
        }
        else {
            var url = getAppPath() + "/policymgr/massReRate.do";
            setInputFormField("termList", getTermList());
            postAjaxSubmit(url, "reRateBatch", false, false, handleOnBatchReRate);
        }
    }
}

//--------------------------------------------------------------
// Re-call function of batchReRate.
//--------------------------------------------------------------
function handleOnBatchReRate(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            //if there is any exception, return and do nothing
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var requestId = oValueList[0]["REQUESTID"];
                alert(getMessage("pm.reRatePolicy.reRateResult.batchSuccess.msg", new Array(requestId)));
            }
        }
    }
}

//--------------------------------------------------------------
// Function get search criteria.
//--------------------------------------------------------------
function getTermList() {
    var selectedRecords = findPolicyListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    var size = selectedRecords.length;
    var termList = "";
    if (size > 0) {
        termList = selectedRecords.item(0).getAttribute("id");
    }
    for (var i = 1; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var policyTermHistoryId = currentRecord.getAttribute("id");
        termList = termList + "," + policyTermHistoryId;
    }
    return termList;
}

function processBackToList () {
    if (isStringValue(getObjectValue("orgSortColumn")) && getObjectValue("orgSortColumn") != 'null') {
        var order = "-";
        if (getObjectValue("orgSortOrder") == "-") {
            order = "+"
        }
        setTableProperty(getTableForXMLData(findPolicyListGrid1), "sortOrder", order);
        if (dti.oasis.page.useJqxGrid()) {
            findPolicyListGrid_sort(getObjectValue("orgSortColumn"), getObjectValue("orgSortType"), getObjectValue("orgSortOrder"));
        } else {
            findPolicyListGrid_sort(getObjectValue("orgSortColumn"), getObjectValue("orgSortType"));
        }
    }

    var testCode = 'isGridReadyStateIsCompleted("findPolicyListGrid") ' +
            '&& !getTableProperty(getTableForGrid("findPolicyListGrid"), "sorting") ';
    var callbackCode = 'processBackToListGoToPage();';
    executeWhenTestSucceeds(testCode, callbackCode, 50);
}

function processBackToListGoToPage(){
    if(isStringValue(getObjectValue("orgRowId")) && getObjectValue("orgRowId") != 'null'){
        var orgRowId = getObjectValue("orgRowId");
        var newOrgRowId = getObjectValue("policyTermHistoryId");
        if (newOrgRowId && newOrgRowId != orgRowId) {
            orgRowId = newOrgRowId;
        }
        selectRowById("findPolicyListGrid", orgRowId);
    }
}

var findPolicyListGridSorted = false;

function handleOnAfterSort() {
    findPolicyListGridSorted = true;
}

function updatePolicyListSession(orgRowId) {
    var policyList = "";
    var policyTermHistoryIdList = "";

    findPolicyListGrid1.recordset.movefirst();
    while (!findPolicyListGrid1.recordset.eof) {
        if (isStringValue(policyList)) {
            policyList += ",";
        }
        if (isStringValue(policyTermHistoryIdList)) {
            policyTermHistoryIdList += ",";
        }
        policyList += findPolicyListGrid1.recordset("CPOLICYNO").value;
        policyTermHistoryIdList += findPolicyListGrid1.recordset("ID").value;
        findPolicyListGrid1.recordset.movenext();
    }

    if(isStringValue(orgRowId) && orgRowId != 'null'){
        selectRowById("findPolicyListGrid", orgRowId);
    }

    if (isStringValue(policyList)) {
        var path = getAppPath() + "/policymgr/findPolicy.do?process=updatePolicyListSession"
                + "&date=" + new Date();

        var data = "policyList=" + policyList + "&policyTermHistoryIdList=" + policyTermHistoryIdList;

        new AJAXRequest("POST", path, data, handleUpdatePolicyListSessionResult, false);
    }
}

function handleUpdatePolicyListSessionResult(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var xml = ajax.responseXML;
            handleAjaxMessages(xml, null);
        }
    }
}

