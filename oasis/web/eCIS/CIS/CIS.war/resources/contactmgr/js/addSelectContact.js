/**
 * The js file for Add / Select Contact page.
 *
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   1/17/13
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/12/2014       Elvin       Issue 151626: add contact name required check
 * 09/10/2014       ylu         Issue 154617.
 * 09/19/2018       dpang       Issue 195835: grid replacement.
 * ---------------------------------------------------
 */

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

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'add':
            commonAddRow("testgrid");
            break;
        case 'delete':
            commonDeleteRow("testgrid");
            break;
        case 'select':
            selectContact();
            break;
        case 'close':
            if (!isOkToChangePages()) {
                return;
            }
            closeWindow();
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'save':
            if (!validateGrid()) {
                return proceed;
            }

            setObjectValue("process", "saveAllContact");
            proceed = true;
            break;
    }
    return proceed;
}

function handleReadyStateReady() {
    var isNewRecord = false;

    if (!isEmptyRecordset(testgrid1.recordset)) {
        if (testgrid1.recordset("UPDATE_IND").value == 'I') {
            isNewRecord = true;
        }
    }

    getObject("CI_ADDSEL_CONT_DEL").disabled = (!isNewRecord);
    getObject("CI_ADDSEL_CONT_SEL").disabled = isNewRecord;
}

function handleOnLoad() {
    if (isEmptyRecordset(testgrid1.recordset)) {
        getObject("CI_ADDSEL_CONT_DEL").disabled = true;
        getObject("CI_ADDSEL_CONT_SEL").disabled = true;
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
                    var comparePrimaryB = getItem(testgrid1, 'CPRIMARYB');
                    if (comparePrimaryB == -1) {
                        setItem(testgrid1, 'CPRIMARYB', 0);
                        setItem(testgrid1, 'CPRIMARYBCOMPUTED', 'No');
                        if (testgrid1.recordset("UPDATE_IND").value == "N")
                            testgrid1.recordset("UPDATE_IND").value = "Y";
                    }
                }
                testgrid1.recordset.movenext();
            }
            first(testgrid1);
            getRow(testgrid1, currentRowID);
            setItem(testgrid1, 'CPRIMARYBCOMPUTED', 'Yes');
        } else
            setItem(testgrid1, 'CPRIMARYBCOMPUTED', 'No');
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
        var fn = getObject("firstName").value;
        var mn = getObject("middleName").value;
        var ln = getObject("lastName").value;
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

//----------------------------------------------------------------------------------------------------
// Select contact.
//----------------------------------------------------------------------------------------------------
function selectContact() {
    closeWindow(function () {
        var contactId = testgrid1.recordset("ID").value;
        var contactName = testgrid1.recordset("CNAMECOMPUTED").value;
        var contactIdFieldName = getObjectValue("contactIdFieldName");
        var contactNameFiledName = getObjectValue("contactNameFiledName");

        if (isStringValue(contactIdFieldName) && getParentWindow().hasObject(contactIdFieldName)) {
            getParentWindow().setObjectValue(contactIdFieldName, contactId);
        }

        if (isStringValue(contactNameFiledName) && getParentWindow().hasObject(contactNameFiledName)) {
            getParentWindow().setObjectValue(contactNameFiledName, contactName);
        }

        if (isStringValue(getObjectValue("eventName"))) {
            if (eval("getParentWindow()." + getObjectValue("eventName"))) {
                contactInfo.contactId = contactId;
                contactInfo.contactName = contactName;
                eval("getParentWindow()." + getObjectValue("eventName") + "(contactInfo)");
            }
        }
    });
}

function testgrid_selectRow(pk) {
    getObject("CI_ADDSEL_CONT_DEL").disabled = !(testgrid1.recordset("UPDATE_IND").value == 'I');
    getObject("CI_ADDSEL_CONT_SEL").disabled = (testgrid1.recordset("UPDATE_IND").value == 'I');
}

function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
    //getObject("CI_ADDSEL_CONT_DEL").disabled = false;
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
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(ciDataChangedConfirmation)) {
            return false;
        }
    }
    return true;
}
