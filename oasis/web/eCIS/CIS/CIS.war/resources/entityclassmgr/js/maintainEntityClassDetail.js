//-----------------------------------------------------------------------------
//  Description: Javascript file for Entity Class Add/Modify Page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  09/17/2018       ylu         Issue 195835: grid replacement.
//  09/27/2018       jdingle     Issue 191748: Move network validation to OBR.
//  10/30/2018       ylu         Issue 195835: per code review:
//                               1). use common validate by webWB, not in handleOnSubmit() method
//                               2). simplized validateAllClassFields method for Add & Modify
//-----------------------------------------------------------------------------
var classCodeFldID      = "entityClass_entityClassCode";
var classCodeDescFldID  = "entityClass_entityClassCodeDesc";
var classEffFrDtFldID   = "entityClass_effectiveFromDate";
var classEffToDtFldID   = "entityClass_effectiveToDate";

function handleOnSubmit(action) {
    var processed = true;
    switch (action) {
        case "saveEntityClass":
            if (getObjectValue("addWithError") == "Y" &&
                !isStringValue(getObjectValue(classCodeFldID), true)) {
                alert(getMessage("ci.common.error.another.select", new Array(getLabel(classCodeFldID))));
                return false;
            }

            if (!validateAllClassFields()) {
                return false;
            }
            break;
    }
    return processed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case "cancel":
            if (isPageDataChanged() && !confirm(ciDataChangedConfirmation)) {
                return;
            }
            closeWindow(function () {
                getParentWindow().refreshPage();
            });
            break;
        case "refresh":
            setInputFormField("process", "refresh");
            submitFirstForm();
            break;
    }
}

function handleOnChange(field) {
    if (field.name == "entityClass_entityClassCode") {
        if (field.value != "NETWORK") {
            setObjectValue("entityClass_networkDiscount", "");
        }
    }
}

//-----------------------------------------------------------------------------
// Validate all entity class fields.
//-----------------------------------------------------------------------------
function validateAllClassFields() {
  var msg = '';
  var classCodeLabel = getLabel(classCodeFldID);
  var classCodeValue = getObjectValue(classCodeFldID);

  var classEffFrDtLabel = getLabel(classEffFrDtFldID);
  var classEffFrDtValue = getObjectValue(classEffFrDtFldID);

  var classEffToDtLabel = getLabel(classEffToDtFldID);
  var classEffToDtValue = getObjectValue(classEffToDtFldID);

  msg += validateClassDates(classCodeLabel, classCodeValue,
    classEffFrDtLabel, classEffFrDtValue,
    classEffToDtLabel, classEffToDtValue);

    if (hasObject("entityClass_currentEffectiveFromDate")) {
        var currentEffectiveFromDate = getObjectValue("entityClass_currentEffectiveFromDate");
        if (isStringValue(currentEffectiveFromDate)) {
            var effectiveFromDate = getObjectValue("entityClass_effectiveFromDate");

            if (isStringValue(effectiveFromDate)) {
                if (isDate2OnOrAfterDate1(currentEffectiveFromDate, effectiveFromDate) == 'N') {
                    msg += getMessage("ci.entity.class.newEffectiveFromDateBeforeCurrent.error", [currentEffectiveFromDate]);
                }
            } else {
                msg += getMessage("ci.entity.class.newEffectiveFromDateCannotBeEmpty.error");
            }
        }
    }

    if (getObjectValue("csVendorReqTaxId") == 'Y') {
        var dtToday = formatDate(new Date(), 'mm/dd/yyyy');
        if (classCodeValue == 'VENDOR') {
            if ((isDate2OnOrAfterDate1(classEffFrDtValue, dtToday) == 'Y' || !isStringValue(classEffFrDtValue)) &&
                (isDate2OnOrAfterDate1(classEffToDtValue, dtToday) == 'N' || !isStringValue(classEffToDtValue))
                ) {
                if ((getObjectValue("hasTaxIdExists") == 'N')) {
                    msg += getMessage("ci.entity.class.invalidVendor.error");
                }
            }
        }
    }

    if (msg != '') {
        alert(msg);
    return false;
  }
  return true;
}

