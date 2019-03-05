//-----------------------------------------------------------------------------
// Modifications:
// 04/16/2010  kenney     Modified to display detailed grid validation message.
// 11/16/2010  kenney     Modified function validate to display correct message "core.validate.error.requiredInRow"
// 11/16/2010  Witti      issue - 110622: Modified to validate grid data instead of form data.
// 01/11/2011  kenney     Added isFieldDefinedForGridWhenValidate to fix the bug when a page has both form and grid
// 11/21/2012  Parker      139200 - Fix panel hidden logic to skip hidden fields using CSS styles eto determine if hidden
// 05/28/2013  skommi      144168 - Added code in validate function to deal with required field within notefields
// 12/17/2013  Elvin      Issue 150348: add max length check for textarea field
// 01/15/2014  Elvin      Issue 151156: if max length of textarea field <= 0 then ignore length check
// 06/03/2014  wkong      Issue 151555: Phone Number's display mask of ###-####,
//                                      that is true. If making field required,
//                                      the mask is interpreted as entered value and is not recognized as empty.
// 09/12/2017  dpang      Issue 187778: Modified validate function to correctly check whether parentElement has hidden style.
// 05/24/2018  mlm        193214 - Replaced .attributes('dataSrc') and .attributes('dataFld') with getDataSrc() and
//                                 getDataField() respectively.
// 10/22/2018  ylu        196579 - count textarea's actual maxlength for IE11 due to special char, backward with IE8
//-----------------------------------------------------------------------------
function checkedAlready(checkedItems, item, ind) {
    for (var i = 0; i < ind; i++) {
        if (item == checkedItems[i])
            return true;
    }
    return false;
}

function validate(theform) {
    // Note: this will never be called!  No function overloading occurs in JS.
    // if you call validate with one parameter it will use the one with 4 parameters, the other 3 are undefined.
    // if you meant to call validate(theform, true), you should call validate(theform, true, null, null)
    // calling just validate(theform) is actually calling validate(theform, undefined, undefined, undefined)
    validate(theform, false);
}

function validate(theform, ignoreHiddenDiv, validationHandler, gridRowIndex) {
    var elems = theform.elements;
    var str = "";
    var req;
    var reqDefined = true;
    var checkedItems = new Array(elems.length);
    var ind = 0;
    var failedItems = new Array(elems.length);
    var failedInd = 0;
    for (var i = 0; i < elems.length; i++) {
        var valElem = true;
        if (ignoreHiddenDiv) {
            var pElem = elems[i].parentElement;
            // We should loop the container from the inner to the out to check whether the parentElement is hidden,
            // if the parentElement is hidden,system determines the subElement is hidden and shouldn't be validated.
            while (pElem != null) {
                if (hasObject("ANT_" + elems[i].name)) {
                    if (hasHiddenStyle($(pElem)) && pElem.id == "d_" + elems[i].name) {
                         pElem = pElem.parentElement;
                        continue;
                    }
                }

                if (hasHiddenStyle($(pElem))) {
                    valElem = false;
                    break;
                } else if (pElem.nodeName == 'TD' || pElem.nodeName == 'SPAN') {
                    if (hasClassName(pElem, FIELD_CLASS_HIDDEN) || hasClassName(pElem, FIELD_CLASS_READONLY)) {
                        valElem = false;
                        break;
                    }
                }
                pElem = pElem.parentElement;
            }
        }
        if (valElem) {
            switch (elems[i].type) {
                case "radio":
                case "checkbox":
                    if (!checkedAlready(checkedItems, elems[i].name, ind)) {
                        eval("reqDefined = typeof REQ_" + elems[i].name + " != 'undefined';");
                        if (reqDefined) {
                            eval("req = REQ_" + elems[i].name + ";");
                            if (req && !isValue(elems[i])) {
                                str += (gridRowIndex == null || !isFieldDefinedForGridWhenValidate(elems[i]) ? getMessage("core.validate.error.required", new Array(getLabel(elems[i], 0), "\n"))
                                        : getMessage("core.validate.error.requiredInRow", new Array(getLabel(elems[i], 0), gridRowIndex, "\n")));
                                failedItems[failedInd++] = elems[i].name;
                            }
                            checkedItems[ind++] = elems[i].name;
                        }
                    }
                    break;
                case "text":
                case "textarea":
                case "select-one":
                case "select-multiple":
                    if(elems[i].type == "select-multiple") {
                        var multiSelFld = getObject(elems[i].name);
                        var maxLength = "";
                        var valuesSelected = "";

                        if (isDefined(multiSelFld.maxLength)) {
                            maxLength = multiSelFld.maxLength;
                            if (maxLength != "undefined" || maxLength != "") {
                                for (var x = 0; x < multiSelFld.options.length; x++) {
                                    if (multiSelFld.options[x].selected && multiSelFld.options[x].value != '') {
                                        valuesSelected = valuesSelected + multiSelFld.options[x].value + ",";
                                    }
                                }
                                if (maxLength != "" && valuesSelected.length > maxLength) {
                                    var btn = getObject("btnFind");

                                    for(var z=0; z<btn.length;z++){
                                        var onClick = "" + btn[z].onclick;
                                        if( onClick.indexOf(elems[i].name)>0){
                                            btn[z].fireEvent("onclick");
                                            event.cancelBubble = true;
                                            break;
                                        }
                                    }
                                    //handleError(getMessage("core.validate.multiselect.maxlength.error"));
                                    alert(getMessage("core.validate.multiselect.maxlength.error"));
                                    return false;
                                }
                            }
                        }
                    }

                    eval("reqDefined = typeof REQ_" + elems[i].name + " != 'undefined';");
                    if (reqDefined) {
                        eval("req = REQ_" + elems[i].name + ";");
                        var isValueExists = false;
                        var gridValue = null;
                        if (req && elems[i].type == "select-multiple") {
                            var dataSourceName = elems[i].dataSrc
                            if (isDefined(dataSourceName)) {
                                if (!isEmpty(dataSourceName)) {
                                    //This is a grid bounded field.
                                    gridValue = getRecordSetFieldValue(elems[i]);
                                    isValueExists = (gridValue!="");
                                }
                            }
                            if (gridValue == null) {
                                // This is not a grid bounded field.
                                isValueExists = isValue(elems[i]);
                            }
                        } else {
                            var fieldId = elems[i].name;
                            if(fieldId && fieldId.endsWith(DISPLAY_FIELD_EXTENTION)){
                                // If the field is a formatted field, then remove the _DISP_ONLY suffix.
                                fieldId = fieldId.substring (0, fieldId.length - DISPLAY_FIELD_EXTENTION.length);
                                if(hasObject(fieldId)){
                                    isValueExists = isValue(getObject(fieldId));
                                }
                            }else{
                                isValueExists = isValue(elems[i]);
                            }
                        }
                        if (req && !isValueExists) {
                            str += (gridRowIndex == null || !isFieldDefinedForGridWhenValidate(elems[i]) ? getMessage("core.validate.error.required", new Array(getLabel(elems[i].name), "\n"))
                                    : getMessage("core.validate.error.requiredInRow", new Array(getLabel(elems[i].name), gridRowIndex, "\n")));
                            failedItems[failedInd++] = elems[i].name;
                        }
                    }

                    //max length check for textarea field
                    if (elems[i].type == "textarea") {
                        if(hasMaxlength(elems[i])){
                            var mLengthCfg = getMaxlength(elems[i]);
                            var mLength = getAdjustedTextareaTextLength(elems[i], mLengthCfg);
                            if (mLength > 0 && elems[i].value.length > mLength) {
                                str += (gridRowIndex == null || !isFieldDefinedForGridWhenValidate(elems[i]) ? getMessage("core.validate.error.textarea", new Array(getLabel(elems[i].name), mLengthCfg + "\n"))
                                    : getMessage("core.validate.error.textareaInRow", new Array(getLabel(elems[i].name), mLengthCfg, gridRowIndex + "\n")));
                                failedItems[failedInd++] = elems[i].name;
                            }
                        }
                    }
                    break;
            }
        }
    }

    if (str == "")
        return true;
    else {
        if (validationHandler != null && validationHandler.length > 0 && eval(validationHandler))
            eval(validationHandler + "(str, failedItems);");
        else
            alert(str);
        return false;
    }
}
function isValue(elem) {
    var reTestForSpace = /[  ]+/;
    var reTestForNonSpace = /[^ ]+/;

    // Added for issue 110622: get data island value instead of form data value
    var elemValue = elem.value;
    if (isEmpty(elemValue)) {
        var str = getRecordSetFieldValue(elem);
        if (!isEmpty(str))
            elemValue = str;
    }

    switch (elem.type) {
        case "text":
        case "hidden":
        case "textarea":
            return (elemValue != "" && elemValue.length > 0 && !(reTestForSpace.test(elemValue) && !reTestForNonSpace.test(elemValue)) );
            break;
        case "radio":
        case "checkbox":
            var rd = document.getElementsByName(elem.name);
            // if this is a checkbox and there's only 1,
            // it cannot possibly be required.
            if (elem.type == "checkbox" && rd.length == 1)
                return true;
            for (var i = 0; i < rd.length; i++) {
                if (rd[i].checked)
                    return true;
            }
            return false;
            break;
        case "select-one":
            return (elemValue != "-1" && elemValue != "" && elemValue.length > 0 &&
                    !(reTestForSpace.test(elemValue) && !reTestForNonSpace.test(elemValue)) );
            break;
        case "select-multiple":
            for (var i = 0; i < elem.length; i++) {
                var opt = elem.options[i];
                if (opt.selected && opt.value != "-1" && opt.value != "" && opt.value.length > 0 &&
                        !(reTestForSpace.test(opt.value) && !reTestForNonSpace.test(opt.value)))
                    return true;
            }
            return false;
            break;
    }
    return true;
}

// Get the data Island value by the form element
// Added by Witti for issue 110622.
function getRecordSetFieldValue(elem) {
    var fieldValue = '';
    try {
        var dataFId = getDataField(elem);
        var gridId = getDataSrc(elem);

        var isFieldInGrid = false;
        if (!isEmpty(gridId)) {
            gridId = gridId.substring(1, gridId.length - 1);
            //check whether it is a column in grid
            if (isFieldDefinedForGrid(gridId, dataFId)) {
                fieldValue = getXMLDataForGridName(gridId).recordset(dataFId).value;
                isFieldInGrid = true;
            }
            if (isFieldInGrid && fieldValue.indexOf("javascript:selectRowWithProcessingDlg(") >= 0) {
                // The field id is the anchor column. Retrieve the row id instead.
                fieldValue = getXMLDataForGridName(gridId).recordset("id").value;
            }
        }
    } catch(e) {
        //Not gird data field
    }
    return fieldValue;
}

function isFieldDefinedForGridWhenValidate(elem) {
    var isFieldInGrid = false;
    try {
        var dataFId = elem.attributes['dataFld'].value;
        var gridId = elem.attributes['dataSrc'].value;
        if (!isEmpty(gridId)) {
            gridId = gridId.substring(1, gridId.length - 1);
            //check whether it is a column in grid
            if (isFieldDefinedForGrid(gridId, dataFId)) {
                isFieldInGrid = true;
            }
        }
    } catch(e) {
        //Not gird data field
    }
    return isFieldInGrid;
}
