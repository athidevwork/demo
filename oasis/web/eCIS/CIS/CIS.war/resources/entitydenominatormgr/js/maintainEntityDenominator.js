//-----------------------------------------------------------------------------
// Functions to support Denominator  page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 07/01/2013   hxk           Issue 141840
//                            If the entity is readonly, don't do change confirmation logic
// 01/27/2015   bzhu          Issue 159739. Add existing check for object before validation.
// 02/02/2015   Elvin         Issue 159162: add validation between Start Date and End date
// 08/21/2015   ylu           Issue 164732: LOV column dependence
// 08/24/2015   kyle          Issue 164732: update for grid's filter and sort
// 09/07/2015   bzhu          Issue 165932: Use isPageDataChanged instead of isChanged.
// 01/18/2016   ylu           Issue 168677: exclude search criteria fields for data change warning
// 10/9/2018    dzou          Grid replacement
//-----------------------------------------------------------------------------

// var theAjaxUrl = getObjectValue("ajaxUrls").toUpperCase();
// var hasTheAjaxRequest = (theAjaxUrl != null && theAjaxUrl.indexOf(("^denominatorType^").toUpperCase()) > -1);

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'REFRESH':
            refreshPage();
            break;
    }
}

function handleOnChange(field) {
    if (field.name.indexOf("filterCriteria_") === 0) {
        if ((field.name === 'filterCriteria_effectiveFromDate' || field.name === 'filterCriteria_effectiveToDate')) {
            if (datemaskclear()) {
                refreshPage();
            }
        } else {
            refreshPage();
        }
    }
}

function handleOnSubmit(action) {
    var processed = true;
    switch (action) {
        case 'SAVE':
            if (!validateGrid()) {
                return false;
            }

            /* process CR/LR in comments field */
            first(testgrid1);
            while (!testgrid1.recordset.eof) {
                var comments = testgrid1.recordset("CCOMENTS").value;
                testgrid1.recordset("CCOMENTS").value = comments.replace(/\n/g, "&#x2028");
                next(testgrid1);
            }

            setInputFormField("process", "saveAllEntityDenominator");
            break;
    }

    return processed;
}

function handleReadyStateReady(table) {
    // OMIC entityDenominator_denominatorUnit LOV setting is:
    //
    // SELECT '-1', '-SELECT-' FROM dual UNION
    // SELECT code, short_description||', '|| state_code
    //   FROM county_code
    //  WHERE state_code = '^denominatorType^'
    //  ORDER BY 2
    // We will need to change the LOV SQL of denominatorUnit_GH for OMIC to:
    // SELECT code, short_description||', '|| state_code
    //   FROM county_code

    // if (hasTheAjaxRequest) {
    //     var cboTypeFields = getObject("cboCDENOMINATORTYPE");
    //     var cboUnitFields = getObject("cboCDENOMINATORUNIT");
    //
    //     if (isEmptyRecordset(testgrid1.recordset)) {
    //         return;
    //     } else if (testgrid1.recordset.recordCount == 1) {
    //         //one row
    //         loadDenominatorUnitOptions(cboTypeFields.value, cboUnitFields);
    //     } else {
    //         //more than one row
    //         for (var i = 0; i < cboTypeFields.length; i++) {
    //             loadDenominatorUnitOptions(cboTypeFields[i].value, cboUnitFields[i]);
    //         } // for
    //     }
    // }
}

function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
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
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function CIDenominatorForm_btnClick(asBtn){
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
    }
}

function refreshPage() {
    if (isPageDataChanged()) {
        if (isEntityReadOnlyYN != "Y") {
            if (!confirm(ciRefreshPageConfirmation)) {
                return;
            }
        }
    }

    if (isDate2OnOrAfterDate1(getObjectValue("entityDenominator_effectiveFromDate"), getObjectValue("entityDenominator_effectiveToDate")) == "N") {
        alert(getMessage("ci.detail.denominator.date.after", new Array(getLabel(getObject("entityDenominator_effectiveToDate")), getLabel(getObject("entityDenominator_effectiveFromDate")))));
        return;
    }

    setObjectValue("process", "refresh");
    submitFirstForm();
}


//-----------------------------------------------------------------------------
// Clear search Criteria
//-----------------------------------------------------------------------------
function clearFilter() {
    setObjectValue("filterCriteria_denominatorCode", "");
    setObjectValue("filterCriteria_denominatorType", "");
    setObjectValue("filterCriteria_effectiveFromDate", "");
    setObjectValue("filterCriteria_effectiveToDate", "");
    handleOnChange(getObject('filterCriteria_denominatorCode'));
}

//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    if (getTableProperty(getTableForGrid("testgrid"), "hasrows")) {
        var rowcount = testgrid1.recordset.recordCount;

        first(testgrid1);

        for (var i = 0; i < rowcount; i++) {
            var upd = testgrid1.recordset("UPDATE_IND").value;

            if (upd === 'I' || upd === 'Y') {
                selectRowById("testgrid", testgrid1.recordset("ID").value);
                var denominatorAmt = getObjectValue("denominatorAmt");

                if (isStringValue(denominatorAmt) && !isPositiveInteger(denominatorAmt)) {
                    alert(getMessage("ci.common.error.value.number", [getLabel("denominatorAmt"), i + 1]));
                    return false;
                }

                var effectiveFromDate = getObjectValue("effectiveFromDate");
                var effectiveToDate = getObjectValue("effectiveToDate");

                if (isStringValue(effectiveFromDate) && isStringValue(effectiveToDate) && isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) === 'N') {
                    alert(getMessage("ci.common.error.element.before", [getLabel("effectiveFromDate"), getLabel("effectiveToDate"), i + 1]));
                    return false;
                }
            }

            next(testgrid1);
        }
    }

    return true;
}

// function loadDenominatorUnitOptions(fieldDependenceValue, cboUnitFields) {
//     var cboUnitFieldValue = getUnitFieldValue(cboUnitFields);
//
//     var url = getFormActionAttribute();
//     url += "?process=getTypeCodeChange&denominatorType=" + fieldDependenceValue;
//     new AJAXRequest("get", url, '', function (ajax) {
//         if (ajax.readyState == 4) {
//             if (ajax.status == 200) {
//                 var data = ajax.responseXML;
//                 // do nothing if we don't have initial values or we got error
//                 if (!handleAjaxMessages(data, null))
//                     return;
//
//                 // parse and put values into LOV
//                 var oValueList = parseXML(data);
//                 if (oValueList != undefined) {
//                     clearDropDown(cboUnitFields);
//                     for (j = 0; j < oValueList.length; j++) {
//                         var label = oValueList[j]["LABEL"];
//                         var value = oValueList[j]["VALUE"];
//                         cboUnitFields.options.add(new Option(label, value));
//                         if (isStringValue(cboUnitFieldValue) && cboUnitFieldValue == value) {
//                             // make sure to display current value item in LOV
//                             cboUnitFields.options[j].selected = true;
//                         }
//                     }
//                 }
//             }
//         }
//     }, false); //new AJAX
// }
//
// function getUnitFieldValue(cboUnitFields) {
//     var fieldValue = "";
//     var rowId = $(findParentTrRow(cboUnitFields)).find("[name='CROWID']").val();
//     var originalRow = origtestgrid1.documentElement.selectSingleNode("//ROW[@id='" + rowId + "']");
//
//     if (originalRow != null) {
//         fieldValue = originalRow.getElementsByTagName("CDENOMINATORUNIT")[0].nodeTypedValue;
//     }
//
//     return fieldValue;
// }

//-----------------------------------------------------------------------------
// exclude search criteria fields change
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return ["filterCriteria_denominatorCode", "filterCriteria_denominatorType",
        "filterCriteria_effectiveFromDate", "filterCriteria_effectiveToDate"];
}
