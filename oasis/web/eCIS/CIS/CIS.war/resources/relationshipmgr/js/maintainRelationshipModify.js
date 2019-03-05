//-----------------------------------------------------------------------------
// Js functions for Modify CIS Relationship page.
// Author: unknown
// Date:   unknown
// Modifications:
// 11/11/2009       kenney      Modified for issue 96605
// 03/22/2010       kenney      Remove the logic of phone number validation.
//                              Phone number format will be handled by framework
// 03/25/2010       kshen       Modified for 101585.
// 02/15/2011       Michael     Changed for 112658.
// 03/16/2011       Michael     Changed for 118503.
// 11/11/2011       parker     Changed for 126752. data equals issue
// 12/22/2011       Michael    Changed for 127479 refact this page.
// 01/14/2016       kshen      159830. Use AJAX call OBR to get warning message.
// 04/15/2016       ylu        issue 170594: due to field dependence, validate zip code only when field is present
// 05/22/2018       dpang      issue 109089: Entity address refactor
// 06/08/2017       jdingle    Issue 190314. Save performance.
// 11/09/2018       Elvin      Issue 195835: grid replacement
//-----------------------------------------------------------------------------

function handleOnLoad() {
    if ("Y" == getObjectValue("saveSuccess")) {
        closeWindow(function () {
            if (getParentWindow()) {
                getParentWindow().refreshPage();
            }
        });
    }

    handleAddressFields();
    handleRelationTypeCodeRelatedFields();

    if (!isEmpty(getObjectValue("info_policy_number"))) {
        setObjectValue("addlInfo1", getObjectValue("info_policy_number"));
    }
}

function btnClick(btnID) {
    if (btnID == "cancel") {
        if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
            return;
        } else {
            closeWindow(function () {
                var parentWindow = getParentWindow();
                if (parentWindow) {
                    parentWindow.refreshPage();
                }
            });
        }
    } else if (btnID == 'save') {
        showProcessingDivPopup();
        if (!validate(document.forms[0])) {
            closeProcessingDivPopup();
            return;
        }
        if (!validateFormFields()) {
            closeProcessingDivPopup();
            return;
        }

        setObjectValue("process","save");
        submitFirstForm();
    }
}

function checkRelationTypeCode(fieldValue) {
    var msg = '';
    if (getObjectValue("PM_CIS_WIP_CHG") == 'Y') {
        var entityHasPolicy = getObject("entityHasPolicy");
        if (entityHasPolicy) {
            if (entityHasPolicy.value > 0) {
                if (getObjectValue("PM_CIS_WIP_CHG_RTYPE").indexOf(fieldValue) > -1) {
                    msg = getMessage("ci.entity.message.relation.changes") + "\n";
                }
            }
        }
    }
    return msg;
}

function handleRelationTypeCodeRelatedFields() {
    // relation_type_code/effective_from_date cannot be modified for an existing record, if relation_type_code is in sys_non_editable_code
    if (!isEmpty(getObjectValue("entityRelationId"))) {
        var relationTypeCode = getObjectValue("relationTypeCode");
        var nonEditableCodeComputed = getObjectValue("nonEditableCodeComputed");
        if (nonEditableCodeComputed.indexOf(relationTypeCode) >= 0) {
            setFieldReadonly("relationTypeCode");
            setFieldReadonly("effectiveFromDate");
        }

        // if entity has policy linked, effective to date cannot be modified
        var msg = checkRelationTypeCode(relationTypeCode);
        if (msg != '') {
            setFieldReadonly("effectiveToDate");
        } else {
            setFieldEditable("effectiveToDate");
        }
    }
}

function handleOnChange(field) {
    if (field.name == "relationTypeCode") {
        var msg = checkRelationTypeCode(field.value);
        if (!isEmpty(msg)) {
            alert(msg);
            event.returnValue = false;
            return;
        }

        handleOnRelationTypeCodeChange(field.value);
    } else if (field.name == "effectiveFromDate") {
        if (!validateEffectiveFromDate(field)) {
            event.returnValue = false;
            return;
        }
    } else if (field.name == "effectiveToDate") {
        if (!validateEffectiveToDate(field)) {
            event.returnValue = false;
            return;
        }
    } else if (field.name == "percentPractice") {
        var msg = validatePercentPractice(field);
        if (!isEmpty(msg)) {
            alert(msg);
            event.returnValue = false;
            return;
        }
    } else if (field.name == "phoneNumber") {
        if (isEmpty(field.value)) {
            setObjectValue("areaCode", "");
            setObjectValue("phoneExtension", "");
        }
    } else if (field.name == getAddressFieldId("ZIP_CODE")
        || field.name == getAddressFieldId("ZIP_PLUS_FOUR")
        || field.name == getAddressFieldId("ZIP_CODE_FOREIGN")
        || field.name == getAddressFieldId("STATE_CODE")
        || field.name == getAddressFieldId("CITY")
        || field.name == getAddressFieldId("COUNTRY_CODE")) {
        if (!addressFieldChanged(field)) {
            event.returnValue = false;
            return;
        }
    }
    return true;
}

function handleOnRelationTypeCodeChange(relationTypeCode) {
    if (getObjectValue("CM_CLIENTREL_ADDL1") == "RELATION_DESC" || getObjectValue("CM_CLIENTREL_ADDL2") == "RELATION_DESC") {
        var relEntityId = getObjectValue("entityChildId");
        if (isStringValue(relationTypeCode)) {
            var reverseFlag = "N";
            if (getObjectValue("reverseRelationIndicator") == "REVERSE RELATION") {
                reverseFlag = "Y";
                relEntityId = getObjectValue("entityParentId");
            }

            var url = getCISPath() + "/ciRelationshipModify.do?process=getRelationshipDesc"
                + "&currEntityId=" + getObjectValue("pk") + "&relEntityId=" + relEntityId
                + "&relTypeCode=" + relationTypeCode + "&reverseRel=" + reverseFlag + "&type=RELATION_DESC";
            new AJAXRequest("get", url, "", handleOnGetRelationshipDesc);
        } else {
            if (getObjectValue("CM_CLIENTREL_ADDL1") == "RELATION_DESC") {
                setObjectValue("addlInfo1", "");
            } else if (getObjectValue("CM_CLIENTREL_ADDL2") == "RELATION_DESC") {
                setObjectValue("addlInfo2", "");
            }
        }
    }
}

function handleOnGetRelationshipDesc(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }

            var result = parseXML(data)[0];
            var relationshipDesc = result.relationshipDesc;
            if (getObjectValue("CM_CLIENTREL_ADDL1") == "RELATION_DESC") {
                setObjectValue("addlInfo1", relationshipDesc);
            } else if (getObjectValue("CM_CLIENTREL_ADDL2") == "RELATION_DESC") {
                setObjectValue("addlInfo2", relationshipDesc);
            }
        }
    }
}

function validateEffectiveFromDate(field) {
    var msg = '';
    var dateOfBirth = getObjectValue("dateOfBirth");
    var effectiveToDate = getObjectValue("effectiveToDate");
    var effectiveFromDate = field.value;

    if (isDate2OnOrAfterDate1(dateOfBirth, effectiveFromDate) == 'N') {
        msg += getMessage("ci.common.error.birthInception.after", [getLabel(field), dateOfBirth]) + "\n";
    }
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        msg += getMessage("ci.entity.message.relation.effectiveDate", [getLabel(field), getLabel(getObject("effectiveToDate")), effectiveToDate]) + "\n";
    }

    if (msg != '') {
        alert(msg);
        return false;
    }
    return true;
}

function validateEffectiveToDate(field) {
    var msg = '';
    var dateOfBirth = getObjectValue("dateOfBirth");
    var effectiveFromDate = getObjectValue("effectiveFromDate");
    var effectiveToDate = field.value;

    if (isDate2OnOrAfterDate1(dateOfBirth, effectiveToDate) == 'N') {
        msg += getMessage("ci.common.error.birthInception.after", [getLabel(field), dateOfBirth]) + "\n";
    }
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        msg += getMessage("ci.entity.message.relation.effectiveDate", [getLabel(getObject("effectiveFromDate")), getLabel(field), effectiveToDate]) + "\n";
    }

    if (msg != '') {
        alert(msg);
        return false;
    }
    return true;
}

function validatePercentPractice(field) {
    var objValue = field.value;
    var msg = '';
    var floatValue = '';
    if (objValue == '' || objValue == '%')
        msg = '';
    else {
        if (objValue.indexOf('%') == -1) {
            floatValue = objValue;
        } else {
            var splitObjValue = objValue.split("%");
            floatValue = splitObjValue[0];
        }
        if (!isFloat(floatValue)) {
            msg = getMessage("ci.common.error.value.numberPercent", [getLabel(field)]) + "\n";
        }
        if (floatValue < 0 || floatValue > 100) {
            msg = getMessage("ci.common.error.value.mustPercent", [getLabel(field)]) + "\n";
        }
    }
    return msg;
}

function validateFormFields() {
    if (!validateEffectiveFromDate(getObject("effectiveFromDate")) || !validateEffectiveToDate(getObject("effectiveToDate"))) {
        return false;
    }

    var msg = validatePercentPractice(getObject("percentPractice"));
    if (!isEmpty(msg)) {
        alert(msg);
        return false;
    }

    if (!validateCommonAddressFields()) {
        return false;
    }

    //  validateOscRelationshipCode
    if (!validateOscRelationshipCode()) {
        return false;
    }
    return true;
}

function validateOscRelationshipCode() {
    var isValid = true;
    var relationTypeCode = getObjectValue("relationTypeCode");

    if (relationTypeCode == 'OSC') {
        var url = getCISPath() + "/ciRelationshipModify.do?process=validateOscRelationshipCode";
        url += "&entityParentId=" + getObjectValue("entityParentId");
        url += "&entityChildId=" + getObjectValue("entityChildId");

        new AJAXRequest("get", url, "", function(ajax) {
            if (ajax.readyState == 4) {
                if (ajax.status == 200) {
                    var data = ajax.responseXML;
                    if (!handleAjaxMessages(data, null)) {
                        isValid = false;
                    }
                }
            }
        }, false);
    }
    return isValid;
}
