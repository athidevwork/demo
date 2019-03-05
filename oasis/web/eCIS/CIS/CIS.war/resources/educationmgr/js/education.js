//-----------------------------------------------------------------------------
// Functions to support Education page.
// Author: ?????
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 07/01/2013   hxk           Issue 141840
//                            If the entity is readonly, don't allow any enable/disable
//                            logic that can override readonly state.
// 01/03/2013   Elvin     Issue 150900: use Entity Select Search window instead of Select Institution window
// 03/10/2014   Elvin     Issue 150661: correct display row number in message, from new Array(i+1) to new Array(i+1+"")
// 07/16/2014   Elvin     Issue 154620: use date.getFullYear instead of getYear in js
// 08/05/2014   wkong     Issue 156066: Add country,state,city field name into entity search method.
// 12/01/2014   ylu       Issue 158252: populate entity_instituation_fk column and spell the institution name
// 07/14/2016   dpang     Issue 176370: set initial values when adding education.
// 02/01/2018   dpang     Issue 191109: add system parameter to enable deleting existing education record.
// 09/27/2018   dzou      Grid replacement
//-----------------------------------------------------------------------------
var isChanged = false;
//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(ciDataChangedConfirmation)) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}
//-----------------------------------------------------------------------------
// Add  the menu add string.
//-----------------------------------------------------------------------------
function addEduacation() {
    commonAddRow("testgrid");
    setFocusToFirstEditableFormField("testgrid");
}
//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}
//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;
    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";
    if (field.name == 'educationProfile_institutionStateCode') {
        if (field.value != '' && field.value != '-1')
        setObjectValue("educationProfile_institutionCountryCode","USA");
        setObjectValue("educationProfile_institutionCity","");
    }
    if (field.name == 'educationProfile_institutionCountryCode') {
        setObjectValue("educationProfile_institutionStateCode","-1");
        setObjectValue("educationProfile_institutionCity","");
    }
}
function CIEducationForm_btnClick(asBtn){
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
//-----------------------------------------------------------------------------
// override the function in gridbtnclicks.js to prevent deleting saved record
//-----------------------------------------------------------------------------
function frmGrid_btnClick(asBtn) {
    switch (asBtn) {
        case 'DELETE':
            commonDeleteRow("testgrid", allowDeleteExistRecord() ? 'Y' : 'N');
            break;
        case 'SAVE':
            if(!validateGrid()){
                return;
            }
            setObjectValue("process", "saveEducationData");
            testgrid_update();
            break;
        case 'ADD':
            addEduacation();
            break;
        case 'REFRESH':
            if (isPageDataChanged()) {
                if (!confirm(ciRefreshPageConfirmation)) {
                    return;
                }
            }
            setObjectValue("process", "loadEducationList");
            submitFirstForm();
            break;
    }
}
//-----------------------------------------------------------------------------
// This function for page elements disabled  by CEDUCATIONPOPUP_IND
//-----------------------------------------------------------------------------
function testgrid_selectRow(pk) {

    if (isEntityReadOnlyYN == "Y") {
        return;
    }
    rowid = pk;
    getRow(testgrid1, pk);
    if (testgrid1.recordset("CEDUCATIONPOPUPIND").value == "0") {
        document.CIEducationForm.educationProfile_institutionName.disabled = false;
        document.CIEducationForm.educationProfile_institutionStateCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCountryCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCity.disabled = false;
    } else if (testgrid1.recordset("CEDUCATIONPOPUPIND").value == "-1") {
        document.CIEducationForm.educationProfile_institutionName.disabled = false;
        document.CIEducationForm.educationProfile_institutionStateCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCountryCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCity.disabled = false;
    }
    else {
        document.CIEducationForm.educationProfile_institutionName.disabled = false;
        document.CIEducationForm.educationProfile_institutionStateCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCountryCode.disabled = false;
        document.CIEducationForm.educationProfile_institutionCity.disabled = false;
    }
}
//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    first(testgrid1);
    var count = 0;
    while (!testgrid1.recordset.eof) {
        count = count + 1;
        var upd = testgrid1.recordset("UPDATE_IND").value;
        if (upd == 'I' || upd == 'Y') {
            selectRowById("testgrid", testgrid1.recordset("ID").value);
            if (!validate(document.forms[0]))
                return;

            /* Education name may not be empty */
            if (testgrid1.recordset('CINSTITUTIONNAME').value == "") {
                alert(getMessage("ci.entity.message.institution.required", new Array(count+"")));
                return false;
            }

            /* validate date field */
            //these two values initialed in jsp.
            var fromBd = document.CIEducationForm.educationProfile_dateOfBirth.value;
            var fromDd = document.CIEducationForm.educationProfile_dateOfDeath.value;

            var fromGy = testgrid1.recordset('CGRADUATIONYEAR').value;
            var fromGy2 = parseInt(fromGy);
            var fromDt =  testgrid1.recordset('CEFFECTIVEFROMDATE').value;
            var toDt =  testgrid1.recordset('CEFFECTIVETODATE').value;

            if (fromGy != '' && fromGy.length != 4) {
                alert(getMessage("ci.entity.message.year.invalid", new Array(count+"")));
                return false;
            }
            else if (fromGy2 >= 3000 || fromGy2 <= 1900) {
                alert(getMessage("ci.entity.message.year.outOfRange", new Array(count+"")));
                return false;
            }
            if (fromGy !='' && fromBd !='') {                                                  //bug fix
                if (fromGy <= new Date(fromBd).getFullYear()) {
                    alert(getMessage("ci.entity.message.year.later", new Array(fromBd)));
                    return false;
                }
            }
            // Set up numeric dates for some validation
            if (fromDt != '')  {
                var numFromDt = parseFloat(fromDt.substr(6, 4) +
                    fromDt.substr(0,2) +
                    fromDt.substr(3,2));
            }

            if (fromBd != '' ) {
                var numBDt = parseFloat(fromBd.substr(0, 4) +
                    fromBd.substr(5,2) +
                    fromBd.substr(8,2)) ;
            }
            if (toDt != '') {
                var numToDt = parseFloat(toDt.substr(6, 4) +
                    toDt.substr(0,2) +
                    toDt.substr(3,2));
            }
            if (fromDd != '') {
                var numDDt = parseFloat(fromDd.substr(0, 4) +
                    fromDd.substr(5,2) +
                    fromDd.substr(8,2));
            }

            if (fromDt != '' && toDt != '' ) {
                if (numFromDt > numToDt) {
                    alert(getMessage("ci.entity.message.endDate.afterStartDate", new Array((count+""))));
                    return false;
                }
            }
            if (fromDt == '' && toDt != '') {
                alert(getMessage("ci.entity.message.startDate.entered", new Array((count+""))));
                return false;
            }

            if (fromDt != '' && fromBd != '') {
                if ( new Date(fromDt) < new Date(fromBd)){  //bug fix
                    alert(getMessage("ci.entity.message.startDate.earlier", new Array(fromBd.substr(0,10), count)));
                    return false;
                }
            }
            if (fromDt != '' && fromDd != '') {
                if ( new Date(fromDt) > new Date(fromDd)) {  //bug fix
                    alert(getMessage("ci.entity.message.startDate.notLater", new Array(fromDd.substr(0,10), count)));
                    return false;
                }
            }
            if (toDt != '' && fromDd != '') {
                if (new Date(toDt) > new Date(fromDd)) {
                    alert(getMessage("ci.entity.message.endDate.notLater", new Array(fromDd.substr(0,10), count)));
                    return false;
                }
            }

            /* validate date field */
            var fromSc = testgrid1.recordset("CINSTITUTIONSTATECODE").value;
            var fromCc = testgrid1.recordset("CINSTITUTIONCOUNTRYCODE").value;
            var sty = fromCc.indexOf("USA")
            if ((fromSc != '' && fromCc == '') && ( fromCc == '' && fromSc != '-1')) {
                testgrid1.recordset("CINSTITUTIONCOUNTRYCODE").value = 'USA';
            }
            if (((fromCc == 'USA' && fromSc == "") || (fromCc == 'USA' && fromSc == "-1")) || (( fromCc == 'U.S.A' && fromSc == "") || (fromCc == 'U.S.A' && fromSc == "-1"))) {
                alert(getMessage("ci.entity.message.stateCode.required", new Array(count+"")));
                return false;
            }
            if ((fromCc.valueOf() != '' && fromCc.valueOf() != 'USA') && (fromCc.valueOf() != '' && fromCc.valueOf() != 'U.S.A')) {
                testgrid1.recordset("CINSTITUTIONSTATECODE").value = '';
            }
        }

        next(testgrid1);
    }
    return true;
}
//-----------------------------------------------------------------------------
//  Initial TRAININGTYPECODE Values*/
//-----------------------------------------------------------------------------
function testgrid_setInitialValues() {
    testgrid1.recordset("CTRAININGTYPECODE").value = "MEDSCHOOL";
    //Set page entitlement flag
    testgrid1.recordset("CISDELETEAVAILABLE").value = "Y";
    testgrid1.recordset("CENTITYATTENDEEID").value  = getObjectValue("pk");

    var path = getAppPath() + "/ciEducation.do?process=getInitialValuesForAddEducation&currectTime=" + new Date();
    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}

function selectInstitutionName() {
    var dataArray = getSelectedKeys(testgrid1);
    var ciSchoolClass = "MEDSCHOOL";
    var ciInstitutionFlt = "N";
    if (tblPropArray[0].hasrows == false) return;
    var fromSc = testgrid1.recordset("CINSTITUTIONSTATECODE").value;
    var fromCc = testgrid1.recordset("CINSTITUTIONCOUNTRYCODE").value;
    if (hasObject("ciSchoolClass")) {
        ciSchoolClass = getObjectValue("ciSchoolClass");
    }
    if (hasObject("ciInstitutionFlt")) {
        ciInstitutionFlt = getObjectValue("ciInstitutionFlt");
    }
    if (ciInstitutionFlt == "Y") {
        openEntitySelectWinEvtDfltEntClsState('educationProfile_entityInstitutionId','educationProfile_institutionName','', '', '', '',
            ciSchoolClass, 'Y', 'updateEducationFields',fromSc,fromCc, 'educationProfile_institutionCountryCode','educationProfile_institutionStateCode','educationProfile_institutionCity');
    } else {
        openEntitySelectWinEvtDfltEntCls('educationProfile_entityInstitutionId','educationProfile_institutionName','', '', '', '',
            ciSchoolClass, 'Y', 'updateEducationFields', 'educationProfile_institutionCountryCode','educationProfile_institutionStateCode','educationProfile_institutionCity');
    }
}

//-----------------------------------------------------------------------------
// This function is needed to navi to some tabs(vendor,address...)
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID != 'save' && btnID != 'add' && btnID != ' delete' && isChanged) {
        if (btnID == 'refresh') {
            if (!confirm(ciRefreshPageConfirmation)) {
                return;
            }
        } else {
            if (!confirm(ciDataChangedConfirmation)) {
                return;
            }
        }
    }
    if (btnID == 'address'
            || btnID == 'phonenumber'
            || btnID == 'entityclass'
            || btnID == 'entityrole'
            || btnID == 'vendor'
            || btnID == 'vendorAddress') {
        // Go to the appropriate page.
        goToEntityModule(btnID, getObjectValue("pk"),
            getObjectValue("entityName"),
            getObjectValue("entityType"));
    } else if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    } else if (btnID == 'add') {
        testgrid_insertrow();
        getRow(testgrid1, lastInsertedId);
        // initialize entityfk to entity PK and sourcetable to policy
        setItem(testgrid1, "CENTITYFK", getObjectValue("pk"));
        setItem(testgrid1, "CSOURCETABLE", "POLICY");
        setObjectValue("process", "add");
        initCertification();
    } else if (btnID == 'delete') {
        rowid = commonDeleteRecord(testgrid1, testgrid, allowDeleteExistRecord());
        setObjectValue("process", "delete");
        initCertification();
    } else {
        if (btnID == 'save') {
            setObjectValue("process", "save");
            if (!validateGrid()) {
                return;
            }
            testgrid_update();
        } else if (btnID == 'refresh')
            setObjectValue("process", "refresh");
        // Submit the form;  it's either a save or a refresh.

        submitFirstForm();
    }
}
//-----------------------------------------------------------------------------
// This function is updatefield from popup page.
//-----------------------------------------------------------------------------
function updateField() {
    stateCode = getObjectValue("educationProfile_institutionStateCode");
    if (stateCode != ''&& stateCode != '-1')
        countryCode = "USA";
    testgrid1.recordset("CENTITYINSTITUTIONID").value = entityFk;
    testgrid1.recordset("CEDUCATIONPOPUPIND").value = '-1';

    // set  CIEducationForm
    setObjectValue("educationProfile_institutionName", entityName);

    getObject("educationProfile_institutionName").disabled = false;
    getObject("educationProfile_institutionStateCode").disabled = false;
    getObject("educationProfile_institutionCountryCode").disabled = false;
    getObject("educationProfile_institutionCity").disabled = false;

    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";
}


function handleEvent(event, jsonEntity) {
    // Called by fireEvent of entitySelectSearch.js
    if (event == "updateEducationFields") {
        var eduCity = jsonEntity.CCITY;

        var eduState = jsonEntity.CSTATECODE;
        if (eduState == '' || eduState == -1) {
            eduState = jsonEntity.CPROVINCE;
        }
        var eduCountry = jsonEntity.CCOUNTRYCODE;

        updateEducationFields(eduCity, eduState, eduCountry);

        if (testgrid1.recordset("UPDATE_IND").value != "I") {
            testgrid1.recordset("UPDATE_IND").value = "Y";
        }
    }
}

function updateEducationFields(city, stateCode, countryCode) {
    setObjectValue("educationProfile_institutionStateCode", stateCode);
    if (stateCode != ''&& stateCode != '-1' && (countryCode == '' || countryCode == '-1' ))
        countryCode = "USA";
    setObjectValue("educationProfile_institutionCountryCode", countryCode);
    setObjectValue("educationProfile_institutionCity", city);
    //158252: spell the name as same as that of C/S and 2012.2.0 web
    var institutionName = getObjectValue("educationProfile_institutionName");
    setObjectValue("educationProfile_institutionName", institutionName + ", " + city + ", " + stateCode + ", " + countryCode);
}


//this function it used to handle findertext field
function find(findId) {
  if (findId.toUpperCase()=="educationProfile_institutionName".toUpperCase()) {
      selectInstitutionName()
  }
}

//var sorting = false;
//var userReadyStateReadyPass = 0;
//function userReadyStateReady(table) {
//    // do nothing if the table is not ready
//    if (!table.id || table.readyState != 'complete')
//        return;
//
//    // When there is pagination, this function is called twice for sorting - once for the header and once for the data.
//    // So do nothing for the first time.
//    pages = getTableProperty(table, "pages");
//    if (sorting && pages > 1 && userReadyStateReadyPass == 1) {
//        userReadyStateReadyPass ++;
//    }
//    else {
//        // invoke the commonReadyStateReady only if we're not in middle of a process
//        if (!getTableProperty(table, "isInCommonAddRow") && !getTableProperty(table, "isInSelectRowById")) {
//            commonReadyStateReady(table);
//
//            if (window.handleReadyStateReady)
//                handleReadyStateReady(table);
//        }
//    }
//    // set the table ready flag so that commonAddRow can take care of the rest
//    setTableProperty(table, "isUserReadyStateReadyComplete", true);
//}
//
//function commonReadyStateReady(table) {
//    var selectedRow = getTableProperty(table, "selectedTableRowNo");
//    if (selectedRow && !sorting) {
//        hiliteSelectRow(table.rows[selectedRow]);
//        var rowid = getSelectedRow(table.id);
//        selectRow(table.id, rowid);
//    }
//    else {
//        selectFirstRowInGrid(table.id);
//
//        // rset sorting indicators
//        if (sorting) {
//            sorting = false;
//            userReadyStateReadyPass = 0;
//        }
//    }
//}
//-----------------------------------------------------------------------------
// handle record checkbox click event to enable/disable delete button.
//-----------------------------------------------------------------------------
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
    checkIfEnableDeleteButton(testgrid1, "EDUC_DEL", allowDeleteExistRecord());
}

function allowDeleteExistRecord() {
    return sys_parm_ci_del_education == "Y" && isEntityReadOnlyYN == 'N';
}

function handleOnSelectAll(gridId, checked) {
    handleDeleteButton();
}