//-----------------------------------------------------------------------------
// Functions to support Training page.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 07/01/2013   hxk       Issue 141840
//                        if entity readonly, avoid enable/disable logic.
// 01/03/2013   Elvin     Issue 150900: use Entity Select Search window instead of Select Institution window
// 07/04/2014   bzhu      Issue 154822
// 02/01/2018   dpang     Issue 191109
// 09/07/2018   Elvin     Issue 194134: delete hidden field which reading values from datasrc, update value in recordset directly
// 10/16/2018   Elvin     Issue 195835: grid replacement
//-----------------------------------------------------------------------------

function handleOnLoad(){
}

function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
    if (isEntityReadOnlyYN == "Y") {
        enableDisableFormFields(true);
        return;
    }

    var policyNo = testgrid1.recordset("CPOLICYNO").value;
    if (isEmpty(policyNo)) {
        enableDisableFormFields(false);
    } else {
        enableDisableFormFields(true);
    }
}

function testgrid_setInitialValues() {
    // Not sure this is necessary
    testgrid1.recordset("CTRAININGTYPECODE").value = "HOSPITAL";
    testgrid1.recordset("CENTITYATTENDEEID").value  = getObjectValue("pk");

    var path = getAppPath() + "/ciTraining.do?process=getInitialValuesForAddTraining&currectTime=" + new Date();
    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}

function CITrainingForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
    }
}

function userRowchange(obj) {
    switch (obj.name) {
        case "chkCSELECT_IND":
            handleDeleteButton();
            break;
    }
}

function handleReadyStateReady() {
    handleDeleteButton();
}

function handleDeleteButton() {
    checkIfEnableDeleteButton(testgrid1, "trainingDelete", allowDeleteExistRecord());
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'REFRESH':
            if (isOkToChangePages()) {
                reloadWindowLocation();
            }
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;

    first(testgrid1);
    while (!testgrid1.recordset.eof) {
        var updateInd = testgrid1.recordset("UPDATE_IND").value;
        if (updateInd == 'I' || updateInd == 'Y') {
            if (!validateBeforeSave(false)) {
                selectRowById('testgrid', testgrid1.recordset("ID").value);
                proceed = false;
                break;
            }
        }
        next(testgrid1);
    }
    return proceed;
}

function validateBeforeSave(OnChangeEventB) {
    var effectiveFromDate = '';
    var effectiveToDate = '';
    if (OnChangeEventB) {
        effectiveFromDate = getObjectValue("effectiveFromDate");
        effectiveToDate = getObjectValue("effectiveToDate");
    } else {
        effectiveFromDate = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
        effectiveToDate = testgrid1.recordset("CEFFECTIVETODATE").value;
    }

    var entityDateOfBirth = getObjectValue("dateOfBirth");
    var effectiveFromDateLabel = getLabel(getObject("effectiveFromDate"));
    var effectiveToDateLabel = getLabel(getObject("effectiveToDate"));
    var dateOfBirthLabel = 'Date of Birth/Inception ' + entityDateOfBirth;

    var errorMessage = '';
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        errorMessage += getMessage("ci.common.error.certifiedDate.after", new Array(effectiveToDateLabel, effectiveFromDateLabel)) + "\n";
    }
    if (isDate2OnOrAfterDate1(entityDateOfBirth, effectiveFromDate) == 'N') {
        errorMessage += getMessage("ci.common.error.certifiedDate.after", new Array(effectiveFromDateLabel, dateOfBirthLabel)) + "\n";
    }
    if (isDate2OnOrAfterDate1(entityDateOfBirth, effectiveToDate) == 'N') {
        errorMessage += getMessage("ci.common.error.certifiedDate.after", new Array(effectiveToDateLabel, dateOfBirthLabel)) + "\n";
    }

    if (!isEmpty(errorMessage)) {
        alert(errorMessage);
        return false;
    } else {
        return true;
    }
}

function handleOnChange(field) {
    if (field.name == "effectiveFromDate") {
        if (isDate2OnOrAfterDate1(field.value, getObjectValue("effectiveToDate")) == 'N') {
            alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(getObject("effectiveToDate")), getLabel(field))));
            return false;
        }
        if (isDate2OnOrAfterDate1(getObjectValue("dateOfBirth"), field.value) == 'N') {
            alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(field), 'Date of Birth/Inception ' + getObjectValue("dateOfBirth"))));
            return false;
        }
    } else if (field.name == "effectiveToDate") {
        if (isDate2OnOrAfterDate1(getObjectValue("effectiveFromDate"), field.value) == 'N') {
            alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(field), getLabel(getObject("effectiveFromDate")))));
            return false;
        }
        if (isDate2OnOrAfterDate1(getObjectValue("dateOfBirth"), field.value) == 'N') {
            alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(field), 'Date of Birth/Inception ' + getObjectValue("dateOfBirth"))));
            return false;
        }
    }
}

function enableDisableFormFields(isDisabled) {
    enableDisableField("trainingTypeCode", isDisabled);
    enableDisableField("institutionName", isDisabled);
    enableDisableField("riskClassCode", isDisabled);
    enableDisableField("effectiveFromDate", isDisabled);
    enableDisableField("effectiveToDate", isDisabled);
    enableDisableField("sourceTable", isDisabled);
}

function find(findId) {
    if (findId.toUpperCase()=="institutionName".toUpperCase()) {
        selectInstitutionName()
    }
}

function selectInstitutionName() {
    var ciSchoolClass = "HOSPITAL";
    if (hasObject("ciSchoolClass")) {
        ciSchoolClass = getObjectValue("ciSchoolClass");
    }
    openEntitySelectWinDfltEntCls('entityInstitutionFk', 'institutionName', '', '', '', '', ciSchoolClass,
        'Y', 'updateField', '', '', "handleOnSelectEntity()");
}

//-----------------------------------------------------------------------------
// This function is updateField from popup page.
//-----------------------------------------------------------------------------
function updateField() {
    testgrid1.recordset("CENTITYINSTITUTIONID").value = entityFk;
    testgrid1.recordset("CINSTITUTIONNAME").value = entityName;
    // below field is no long used
    testgrid1.recordset("CTRAININGPOPUPIND").value = '-1';
}

function allowDeleteExistRecord() {
    return getObjectValue("CM_DEL_TRAINING") == "Y" && isEntityReadOnlyYN == 'N';
}

function handleOnSelectAll(gridId, checked) {
    handleDeleteButton();
}

function handleOnSelectEntity() {
    if (testgrid1.recordset("UPDATE_IND").value != "I") {
        testgrid1.recordset("UPDATE_IND").value = "Y";
    }
}
