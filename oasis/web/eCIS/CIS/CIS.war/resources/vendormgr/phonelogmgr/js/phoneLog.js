//-----------------------------------------------------------------------------
// Functions to support Phone Log page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 10/16/2018    Elvin       Issue 195835: grid replacement
// 11/08/2018    Elvin       Issue 195627: enable default values setting when adding phone log
//-----------------------------------------------------------------------------

var regPhoneNumber = /^(\((\d{3})\)(\ )?)(\d{3})-(\d{4})((\ )?x(\ )?(\d{1,4}))?$/;

function handleOnLoad() {
}

function PhoneLogForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            phoneLogGrid_updatenode("CSELECT_IND", -1);
            first(phoneLogGrid1);
            gotopage(phoneLogGrid, 'F');
            selectFirstRowInGrid("phoneLogGrid");
            break;
        case 'DESELECT':
            phoneLogGrid_updatenode("CSELECT_IND", 0);
            first(phoneLogGrid1);
            gotopage(phoneLogGrid, 'F');
            selectFirstRowInGrid("phoneLogGrid");
            break;
    }
}

function phoneLogGrid_selectRow(pk) {
}

function phoneLogGrid_setInitialValues() {
    var path = getAppPath() + "/phoneLog.do?process=getInitialValuesForAddPhoneLog";
    new AJAXRequest("get", path, '', handleOnGetInitialValues, false);
}

function handleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                for (var prop in oValueList[0]) {
                    var fieldName = prop.toUpperCase();
                    if (fieldName.indexOf("_") > 0) {
                        fieldName = fieldName.substring(fieldName.indexOf("_") + 1);
                    }
                    var fieldValue = oValueList[0][prop];
                    setItem(phoneLogGrid1, "C" + fieldName, fieldValue);
                }
            }
        }
    }
}

function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'REFRESH':
            if (isOkToChangePages()) {
                reloadWindowLocation();
            }
            break;
        case 'CLOSE':
            if (isOkToChangePages()) {
                closeWindow();
            }
            break;
        default:
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;

    first(phoneLogGrid1);
    while (!phoneLogGrid1.recordset.eof) {
        var updateInd = phoneLogGrid1.recordset("UPDATE_IND").value;
        if (updateInd == 'I' || updateInd == 'Y') {
            if (!validateBeforeSave()) {
                selectRowById('phoneLogGrid', phoneLogGrid1.recordset("ID").value);
                proceed = false;
                break;
            }
        }
        next(phoneLogGrid1);
    }
    return proceed;
}

function validateBeforeSave() {
    var phoneNumberLabel = getLabel(getObject("vendorPhoneLog_phoneNumber"));
    var durationLabel = getLabel(getObject("vendorPhoneLog_duration"));

    var errorMessage = '';
    var phoneNumber = phoneLogGrid1.recordset("CPHONENUMBER").value;
    var duration = phoneLogGrid1.recordset("CDURATION").value;

    if (!isEmpty(phoneNumber) && !regPhoneNumber.test(phoneNumber)) {
        errorMessage += getMessage("ci.vendor.phoneLog.invalidPhoneNumber", new Array(phoneNumberLabel)) + "\n";
    }
    if (!isEmpty(duration) && parseFloat(duration) <= 0) {
        errorMessage += getMessage("ci.common.error.element.greater", new Array(durationLabel)) + "\n";
    }

    if (!isEmpty(errorMessage)) {
        alert(errorMessage);
        return false;
    } else {
        return true;
    }
}

function handleOnChange(field) {
    if (field.name == 'vendorPhoneLog_phoneNumber') {
        if (!isEmpty(field.value) && !regPhoneNumber.test(field.value)) {
            alert(getMessage("ci.vendor.phoneLog.invalidPhoneNumber", new Array(getLabel(field))));
            return false;
        }
    } else if (field.name == 'vendorPhoneLog_duration') {
        if (!isEmpty(field.value) && parseFloat(field.value) <= 0) {
            alert(getMessage("ci.common.error.element.greater", new Array(getLabel(field))));
            return false;
        }
    }
}

//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csLoadNotes.js.
// loadNotes(sourceRecordFk, sourceTableName, noteGroupCode)
//-----------------------------------------------------------------------------
function openPhoneLogNotes() {
    var phoneLogId = phoneLogGrid1.recordset("CVENDORPHONELOGIDBAK").value;
    if (window.loadNotes) {
        loadNotesWithReloadOption(phoneLogId, 'VENDOR_PHONE_LOG', 'VENDOR_PHONE', false, false, "handleNotesExist");
    } else {
        alert(getMessage("ci.entity.message.notesError.notAvailable"));
    }
}

function handleNotesExist(notesExist, sourceTableName, sourceRecordId) {
    if (sourceRecordId == phoneLogGrid1.recordset("id").value) {
        if (notesExist) {
            phoneLogGrid1.recordset("CNOTEIND").value = "Y";
        } else {
            phoneLogGrid1.recordset("CNOTEIND").value = "N";
        }
    }
}