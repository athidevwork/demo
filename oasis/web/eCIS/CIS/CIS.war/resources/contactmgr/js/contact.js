//-----------------------------------------------------------------------------
//  Description: js file for entity contact page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  12/09/2008       kshen       Corrected the logic of checnk if page is changed.
//                               Corrected method openNotesForLitEvent.
//  03/22/2010       Kenney      Remove the logic of phone number validation.
//                               Phone number format will be handled by framework
//  09/08/2011       parker      for iss123482.correct contact logic
//  03/12/2014       Elvin       Issue 151626: add contact name required check
//  03/19/2014       kshen       Issue 148001.
//  05/02/2014       kfan        Issue 153077.
//  08/22/2014       ldong       Issue 156786.
//  09/10/2014       ylu         Issue 154617.
//  02/11/2015       jdingle     Issue 160975. Prevent error when user has Read Only access.
//  09/26/2018       dmeng       Issue 195835. Grid replacement
//  ---------------------------------------------------

var isChanged = false;

var SSNFldID = "socialSecurityNumber";
var emailFldID = "emailAddress";
var phoneACFldID = "areaCode";
var phoneExtFldID = "phoneExtension";
var phoneNumFldID = "phoneNumber";
var faxACFldID = "faxAreaCode";
var faxNumFldID = "faxPhoneNumber";
var primaryBFldID = "primaryB";
var effectiveFromDateFldID = "effectiveFromDate";
var effectiveToDateFldID = "effectiveToDate";
var effectiveFromDate = '';
var effectiveToDate = '';
var contactTypeFldID = "contactType";

var rowid = -1;


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
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
}

function testgrid_setInitialValues() {
    setObjectValue("addressId", "");
}
function CIContactForm_btnClick(asBtn) {
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
            getObjectValue("entityType"))
    } else if (btnID == 'add') {
        testgrid_insertrow();
        getRow(testgrid1, lastInsertedId);
        testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
    } else if (btnID == 'delete') {
        rowid = commonDeleteRow("testgrid");
        setObjectValue("process", "delete");
        handleDeleteButton();
    } else {
        if (btnID == 'save') {
            setObjectValue("process", "saveAllContact");
            if (!validateGrid()) {
                return;
            }
            testgrid_update();
        } else if(btnID == 'refresh') {
            setObjectValue("process", "loadAllContact");
            submitFirstForm();
        }
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;

    var msg = '';
    // Validate the SSN.
    if (field.name == SSNFldID) {
        msg += validateSSN(field);
    }
    // Validate the e-mail addresses.
    else if (field.name == emailFldID) {
        msg += validateEMail(field);
    }

    // validate phone number (removed code. validation handled by phone type format in framework)

    // validate phone area code
    else if (field.name == phoneACFldID) {
        if (isNaN(field.value))
            msg += getMessage("ci.entity.message.phoneAreaCode.number") + "\n";
    }
    // validate phone extension
    else if (field.name == phoneExtFldID) {
        if (isNaN(field.value))
            msg += getMessage("ci.entity.message.phoneExtension.number") + "\n";
    }

    // validate fax number (removed code. validation handled by phone type format in framework)

    // validate fax area code
    else if (field.name == faxACFldID) {
        if (isNaN(field.value))
            msg += getMessage("ci.entity.message.faxAreaCode.number") + "\n";
    }
    // primary B
    else if (field.name == primaryBFldID) {
        var currentRowID = testgrid1.recordset("ID").value;
        if (field.checked) {
            testgrid1.recordset.movefirst();
            while (!testgrid1.recordset.eof) {
                var compareRowID = testgrid1.recordset('ID').value;
                if (currentRowID != compareRowID) {
                    var comparePrimaryB = testgrid1.recordset("CPRIMARYB").value;
                    if (comparePrimaryB == -1) {
                        testgrid1.recordset("CPRIMARYB").value = 0;
                        testgrid1.recordset("CPRIMARYBCOMPUTED").value = "No";
                        if (testgrid1.recordset("UPDATE_IND").value == "N")
                            testgrid1.recordset("UPDATE_IND").value = "Y";
                    }
                }
                testgrid1.recordset.movenext();
            }
            first(testgrid1);
            getRow(testgrid1, currentRowID);
            testgrid1.recordset("CPRIMARYBCOMPUTED").value = "Yes";
        } else
            testgrid1.recordset("CPRIMARYBCOMPUTED").value = "No";
    }
    else if (field.name == effectiveFromDateFldID) {
        effectiveFromDate = field.value;
        if (effectiveFromDate != null && effectiveFromDate != '') {
            if (!isValueDate(effectiveFromDate))
                msg = msg + getMessage("ci.entity.message.effectiveFromDate.invalid");
        }
    }
    else if (field.name == effectiveToDateFldID) {
        effectiveToDate = field.value;
        if (effectiveToDate != null && effectiveToDate != '') {
            if (!isValueDate(effectiveToDate))
                msg = msg + getMessage("ci.entity.message.effectiveToDate.invalid");
        }
    } else if(field.name == "firstName" || field.name == "lastName" || field.name == "middleName") {
        var fn = getObjectValue("firstName");
        var mn = getObjectValue("middleName");
        var ln = getObjectValue("lastName");
        var name = "";
        if (fn!=null || fn!='')
            name = fn+' ';
        if (mn!=null || mn!='')
            name = name+mn+' ';
        if (ln!=null || ln!='')
            name = name+ln;
         testgrid1.recordset("CNAMECOMPUTED").value = name;
    }
    if (msg != '') {
        alert(msg);
        field.focus();
        postChangeReselectField(field);
        return false;
    }
}

function validateGrid() {
    var msg = '';
    var count = 0;
    testgrid1.recordset.movefirst();
    while (!testgrid1.recordset.eof) {
        msg = '';
        count = count + 1;
        var upd_ind = testgrid1.recordset("UPDATE_IND").value;
        if (upd_ind == 'Y' || upd_ind == 'I') {
            //name is required
            if (!isStringValue(testgrid1.recordset("CLASTNAME").value) && !isStringValue(testgrid1.recordset("CFIRSTNAME").value)) {
                msg += getMessage("ci.entity.message.contact.nameReq") + "\n";
            }
            // SSN
            msg += validateSSN(testgrid1.recordset("CSOCIALSECURITYNUMBER"), getLabel(SSNFldID));
            // email
            msg += validateEMail(testgrid1.recordset("CEMAILADDRESS"), getLabel(emailFldID));
            // phone related
            var phoneAreaCode = testgrid1.recordset("CAREACODE").value;
            var phoneNumber = testgrid1.recordset("CPHONENUMBER").value;
            var phoneExtension = testgrid1.recordset("CPHONEEXTENSION").value;
            if (isNaN(phoneAreaCode))
                msg += getMessage("ci.entity.message.phoneAreaCode.number") + "\n";
            if (isNaN(phoneExtension))
                msg += getMessage("ci.entity.message.phoneExtension.number") + "\n";
            if (phoneNumber == null || phoneNumber == '') {
                if (phoneAreaCode != null && phoneAreaCode != '') {
                    msg += getMessage("ci.entity.message.phoneNumber.noEntered", [getLabel(phoneACFldID)]) + "\n";
                } else if (phoneExtension != null && phoneExtension != '') {
                    msg += getMessage("ci.entity.message.phoneNumber.noEntered", [getLabel(phoneExtFldID)]) + "\n";
                }
            }
            if (phoneAreaCode == null || phoneAreaCode == '') {
                if (phoneNumber != null && phoneNumber != '') {
                    msg += getMessage("ci.entity.message.areaCode.noEntered") + "\n";
                }
            }

            // fax related
            var faxNumber = testgrid1.recordset("CFAXPHONENUMBER").value;
            var faxAreaCode = testgrid1.recordset("CFAXAREACODE").value;
            if (isNaN(faxAreaCode))
                msg += getMessage("ci.entity.message.faxAreaCode.number") + "\n";
            if (faxNumber == null || faxNumber == '') {
                if (faxAreaCode != null && faxAreaCode != '') {
                    msg += getMessage("ci.entity.message.faxNumber.noEntered") + "\n";
                }
            }
            if (faxAreaCode == null || faxAreaCode == '') {
                if (faxNumber != null && faxNumber != '') {
                    msg += getMessage("ci.entity.message.faxAreaCode.noEntered") + "\n";
                }
            }

            // effectiveFromDate <= effectiveToDate
            effectiveFromDate = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
            if (effectiveFromDate != null && effectiveFromDate != '') {
                if (!isValueDate(effectiveFromDate))
                    msg += getMessage("ci.entity.message.effectiveFromDate.invalid") + "\n";
            }
            effectiveToDate = testgrid1.recordset("CEFFECTIVETODATE").value;
            if (effectiveToDate != null && effectiveToDate != '') {
                if (!isValueDate(effectiveToDate))
                    msg += getMessage("ci.entity.message.effectiveToDate.invalid") + "\n";
            }
            if (effectiveFromDate != null && effectiveFromDate != '' &&
                effectiveToDate != null && effectiveToDate != '') {
                if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
                    msg += getMessage("ci.entity.message.effectiveToDate.after") + "\n";
                }
            }
            if (msg != '') {
                selectRowById('testgrid', testgrid1.recordset("ID").value);
                alert(getMessage("ci.entity.message.row.wrongMessage", [count + "\n", msg]));
                return false;
            }
        }
        testgrid1.recordset.movenext();
    }
    return true;
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

//-----------------------------------------------------------------------------
// Validate fax number.
//-----------------------------------------------------------------------------
function validateFaxNumFieldOnChange(faxNumFld) {
    if (faxNumFld.value == null || faxNumFld.value == '') {
        return '';
    }
    if (validatePhoneNumString(faxNumFld.value)) {
        return '';
    } else {
        event.returnValue = false;
        return  getMessage("ci.common.error.format.ssn", new Array(getLabel(faxNumFld.name), "9999999", "999-9999")) + "\n";
    }
}


//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csloadnotes.js.
//-----------------------------------------------------------------------------
function openNotesForLitEvent(pk) {
    if (isPageDataChanged()) {
        if (!confirm(ciDataChangedConfirmation)) {
            return;
        }
    }

    if(pk == null){
        pk = testgrid1.recordset("ID").value;
    }
    if (window.loadNotesWithReloadOption) {
        loadNotesWithReloadOption(pk, 'CONTACT', 'ENTITY_CONTACT', true, false, "handleNotesExist");
    } else {
        alert(getMessage("js.is.notes.notAvailable"));
    }
}

function handleNotesExist(notesExist, sourceTableName, sourceRecordId) {
    if (sourceRecordId == testgrid1.recordset("id").value) {
        if (notesExist) {
            testgrid1.recordset("CNOTEIND").value = "Yes";
        } else {
            testgrid1.recordset("CNOTEIND").value = "No";
        }
    }
}

//-----------------------------------------------------------------------------
// Open mail client with mail address.
//-----------------------------------------------------------------------------
function sendMail(fieldId) {
    var field = getObject(fieldId);
    if (field && field.value != "") {
        if (!isStringValue(validateEMail(field)))
            location.href = "mailto:" + field.value;
        else
            alert(validateEMail(field));
    }
}

//-----------------------------------------------------------------------------
// Handle Onload event.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("testgrid")).then(function () {
        handleDeleteButton();
    });
}
function getGridId() {
    return 'testgrid';
}

function handleDeleteButton() {
    if (!hasObject("contactDelete") || isElementHidden(getObject("contactDelete"))) {
        return;
    }

    if (!getTableProperty(getTableForGrid("testgrid"), "hasrows")) {
        enableDisableField(getObject("contactDelete"), true);
    } else {
        var selectedExistRecord = testgrid1.documentElement.selectNodes("//ROW[CSELECT_IND='-1' and (UPDATE_IND='N' or UPDATE_IND='Y')]");
        var selectedNewRecord = testgrid1.documentElement.selectNodes("//ROW[CSELECT_IND='-1' and UPDATE_IND='I']");
        enableDisableField(getObject("contactDelete"), selectedExistRecord.length > 0 || selectedNewRecord.length == 0);
    }
}

function handleOnSelectAll(gridId, checked) {
    handleDeleteButton();
}