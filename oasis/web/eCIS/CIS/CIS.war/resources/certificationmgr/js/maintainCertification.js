//-----------------------------------------------------------------------------
//  Description: js file for entity certification page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  04/06/2012       Parker      Call the framework's validation function
//  07/01/2013       hxk         Issue 141840
//                               Do not do any field enable/disabling when entity is readonly.
//  08/05/2014       wkong       Issue 156066
//  02/01/2018       dpang       Issue 191109: add system parameter to enable deleting existing certification record.
//  09/21/2018       dmeng       Issue 195835: grid replacement.
//  ---------------------------------------------------
var isChanged = false;
var rowid = -1;

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
    setFormFieldOnPolicyNo();
}

function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
    testgrid1.recordset("CSOURCETABLE").value = 'POLICY';
}

function CICertificationForm_btnClick(asBtn){
     switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
     }    
}
function btnClick(btnID) {
    if (btnID != 'save' && btnID != 'add' && btnID != 'delete' && isChanged) {
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
        commonAddRow("testgrid")
    } else if (btnID == 'delete') {
        rowid = commonDeleteRow("testgrid", allowDeleteExistRecord() ? 'Y' : 'N');

    } else if (btnID == 'save') {
        setObjectValue("process", "saveCertification");
        first(testgrid1);
        while (!testgrid1.recordset.eof) {
            var upd = testgrid1.recordset("UPDATE_IND").value;
            if (upd == 'I' || upd == 'Y')
                if (!validateGrid())
                    return;
            next(testgrid1);
        }
        testgrid_update();
    } else if(btnID == 'refresh')  {
            setObjectValue("process", "loadCertification");
        // Submit the form;  it's either a save or a refresh.
        submitFirstForm();
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;
    var msg = "";
    if (field.name == "boardExamDate") {
        msg = validateDateGreatThanDOB(field);
    }
    else if (field.name == "certifiedDate" ||
             field.name == "eligExprDate") {
        msg = validateDateGreatThanDOB(field);
        msg += validateCertifiedExprDate();
    }
    if (msg != '') {
        alert(msg);
        field.focus();
        postChangeReselectField(field);
        return false;
    }
}

function getDisplayText(field) {
    var index = field.selectedIndex;
    var displayText = '';
    if (index!=-1) {
      displayText = field.options[field.selectedIndex].text;
    }
    return displayText;
}

/*
  If the field value is not null, field.value >= dateOfBirth
*/
function validateDateGreatThanDOB(field) {
    var msg = '';
    var dateOfBirth = getObjectValue("dateOfBirth");
    var fieldDate = field.value;
    if(fieldDate!='') {
      if (isStringCodeValue(dateOfBirth)) {
        if (isDate2OnOrAfterDate1(dateOfBirth, fieldDate) != 'Y')
          msg = getMessage("ci.common.error.dateOfBirth.after", [getLabel(field), dateOfBirth]) + "\n";
      }
    }
    return msg;
}

/*
  certified date <= elig expr date
*/
function validateCertifiedExprDate() {
    var msg = '';
    var certifiedDate = getObject("certifiedDate");
    var eligExprDate = getObject("eligExprDate");
    if (certifiedDate.value != '' && eligExprDate.value != '') {
        if (isDate2OnOrAfterDate1(certifiedDate.value,eligExprDate.value) != 'Y')
            msg = getMessage("ci.common.error.certifiedDate.after", [getLabel(eligExprDate), getLabel(certifiedDate)]) + "\n";
    }
    return msg;
}

function validateDataRule() {
    var board_exam_date    = testgrid1.recordset("CBOARDEXAMDATE").value;
    var certified_exp_date = testgrid1.recordset("CELIGEXPRDATE").value;
    var entity_board_fk    = testgrid1.recordset("CENTITYBOARDID").value;
    var certification_type = testgrid1.recordset("CCERTIFICATETYPECODE").value;

    if (check_BoardName_CertType_EXCL=='Y') {
       if (isStringCodeValue(entity_board_fk) && isStringCodeValue(certification_type)) {
           //alert("Invalid Data/Operration\nBoard Name and Certificate Type should be mutually exclusive.");
           alert(getMessage("ci.common.error.Board.Name.CertType.excl"));
           return false;
       }
    }
    if (check_BoardDt_CertExp_Dt_EXCL=="Y") {
       if (isStringValue(board_exam_date) && isStringValue(certified_exp_date) ) {
           //alert("Invalid Data/Operration\nBoard Certification Exp. Date and Certificate Expiration Date will be mutually exclusive.");
           alert(getMessage("ci.common.error.Board.Date.CertExp.Date.excl"));
           return false;
       }
    }
    if (check_BoardName_Date_REQ=="Y") {
       if (isStringCodeValue(entity_board_fk) && isEmpty(board_exam_date)) {
           //alert("Invalid Data/Operration\nBoard Certification Date is required for the Board.");
           alert(getMessage("ci.common.error.Board.Name.Date.req"));
           return false;
       }
       if (!isStringCodeValue(entity_board_fk) && isStringValue(board_exam_date)) {
           //alert("Invalid Data/Operration\nBoard Name is required for the Board Certification Date.");
           alert(getMessage("ci.common.error.Board.Date.Name.req"));
           return false;
       }
    }
    if (check_CertType_Date_REQ=="Y") {
         if (!isStringCodeValue(certification_type) && isStringValue(certified_exp_date)) {
             //alert("Invalid Data/Operration\nCertificate Type is required for Certificate Expiration Date.");
             alert(getMessage("ci.common.error.Certified.Date.CertType.req"));
             return false;
         }
        if (isStringCodeValue(certification_type) && isEmpty(certified_exp_date)) {
            //alert("Invalid Data/Operration\nCertificate Expiration Date is required for Certificate Type.");
            alert(getMessage("ci.common.error.Certified.CertType.Date.req"));
            return false;
        }
      }
    return true;
}

function validateGrid() {
    selectRowById("testgrid", testgrid1.recordset("ID").value);
    if (!validate(document.forms[0])) {
        return;
    }
    var msg = '';
    msg = validateDateGreatThanDOB(getObject("boardExamDate"));
    msg += validateDateGreatThanDOB(getObject("certifiedDate"));
    msg += validateDateGreatThanDOB(getObject("eligExprDate"));
    msg += validateCertifiedExprDate();
    if(msg!='') {
        alert(msg);
        return false;
    }
    var entityBoardFk = testgrid1.recordset("CENTITYBOARDID").value;
    if (entityBoardFk == '-1')
      testgrid1.recordset("CENTITYBOARDID").value = '';
    var certificationTypeCode = testgrid1.recordset("CCERTIFICATETYPECODE").value;
    if (certificationTypeCode == '-1')
      testgrid1.recordset("CCERTIFICATETYPECODE").value = '';
    var boardStatus = testgrid1.recordset("CBOARDSTATUS").value;
    if (boardStatus == '-1')
      testgrid1.recordset("CBOARDSTATUS").value = '';
    var riskClassCode = testgrid1.recordset("CRISKCLASSCODE").value;
    if (riskClassCode == '-1')
      testgrid1.recordset("CRISKCLASSCODE").value = '';
    if (!validateDataRule()) {return false;}

    return true;
}

/*
  set form fields based on policyno field value.
  policyno is not null, set form fields to readonly.
  This certification record cannot be updated.
*/
function setFormFieldOnPolicyNo() {
    if (isEntityReadOnlyYN == "Y")     {
        return;
    }
    var policyNo = testgrid1.recordset("CPOLICYNO").value;
    if(policyNo!='' && policyNo!=null)
        setFormFieldDisabled(true);
    else
        setFormFieldDisabled(false);
}

/*
  handle record checkbox click event to enable/disable delete button.
*/
function userRowchange(obj) {
    switch (obj.name) {
        case "chkCSELECT_IND":
            handleDeleteButton();
            break;
    }
}

/*
  Enable or disable form fields based on the passed in boolean.
*/
function setFormFieldDisabled(enabledBoolean) {
    enableDisableField(getObject("entityBoardId"), enabledBoolean);
    enableDisableField(getObject("certificateTypeCode"), enabledBoolean);
    enableDisableField(getObject("registrationNo"), enabledBoolean);
    enableDisableField(getObject("riskClassCode"), enabledBoolean);
    enableDisableField(getObject("territory"), enabledBoolean);
    enableDisableField(getObject("boardStatus"), enabledBoolean);
    enableDisableField(getObject("boardExamDate"), enabledBoolean);
    enableDisableField(getObject("specialty"), enabledBoolean);
    enableDisableField(getObject("certifiedDate"), enabledBoolean);
    enableDisableField(getObject("eligExprDate"), enabledBoolean);
}
//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return (isChanged ||
            isPageGridsDataChanged());
}
function handleReadyStateReady() {
    handleDeleteButton();
}

function handleDeleteButton() {
    checkIfEnableDeleteButton(testgrid1, "certfdDelete", allowDeleteExistRecord());
}
function getGridId() {
    return 'testgrid';
}

function allowDeleteExistRecord() {
    return sys_parm_ci_del_certification == "Y" && isEntityReadOnlyYN == 'N';
}

function handleOnSelectAll(gridId, checked) {
    handleDeleteButton();
}