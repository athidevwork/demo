//-----------------------------------------------------------------------------
// Functions to support property edit page.
// Author: unknown
// Date:   unknown
// Modifications:
// kshen   10/29/2007     Modified the validation about percent field value.
// Blake   04/07/2011     Modified handleOnChangeNonFilterFields for issue 118351.
// Elvin   06/06/2013     Issue 143203: Add one row select validation and select selected row
// kshen   04/29/2014     Issue 149462. Fixed the problem about filtering and validation.
// dpang   08/06/2015     Issue 160224.
// dzou    10/15/2018     Grid replacement
// dpang   10/29/2018     Issue 196632: Change getYear to getFullYear.
//-----------------------------------------------------------------------------


var isChanged = false;
// function CIPropertyForm_btnClick(asBtn) moved to ciPropertyLookup.js

//-----------------------------------------------------------------------------
// This function is needed to navi to some tabs(vendor,address...)
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID != 'save' && btnID != 'add' && btnID != ' delete' && isPageGridsDataChanged()) {
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
        goToEntityModule(btnID, getObjectValue("pk"),
            getObjectValue("entityName"),
            getObjectValue("entityType"));
    } else if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    } else if (btnID == 'add') {
        commonAddRow("testgrid");
    } else if (btnID == 'delete') {
        commonDeleteRow("testgrid");
        setObjectValue("process", "delete");
        initCertification();
    } else {
        if (btnID == 'save') {
            setObjectValue("process", "saveAllProperty");
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

function frmGrid_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DELETE':
            commonDeleteRow("testgrid");
            break;
        case 'SAVE':
            if (!validateGrid()) {
                return;
            }
            setObjectValue("process", "saveAllProperty");
            testgrid_update();
            break;
        case 'ADD':
            commonAddRow("testgrid");
            break;
        case 'REFRESH':
            if (isPageGridsDataChanged()) {
                if (!confirm(getMessage("js.refresh.lose.changes.confirmation"))) {
                    return;
                }
            }

            var allFilteringFields = getAllFilterFieldsAsArray();
            for (var i = 0; i < allFilteringFields.length; i++) {
                var field = getObject(allFilteringFields[i]);
                field.value = ""; // set to null,
            }
            setObjectValue("process", "loadAllProperty");
            submitFirstForm();
            break;
    }
}

//-----------------------------------------------------------------------------
// Clear search Criteria
//-----------------------------------------------------------------------------
function clearFilter() {
    // call consolidated function clearFilterCriteria
   clearFilterCriteria();
}

// moved the filtering logic into ciPropertyLookup.js
function handleOnChangeNonFilterFields(obj) {
    if (obj.name == "clientProperty_occupancyPct" ||
        obj.name == "clientProperty_sprinkledPct" ||
        obj.name == "clientProperty_nonsprinkledPct") {
        chkPercentFld(obj);
    }

    if (obj.name == "clientProperty_nfipFloodB") {
        var flag = obj.value;
        if ((flag == null) || (flag == "") || (flag == 'N'))
        {
            setObjectValue("clientProperty_nfipFloodZone", "");
            getObject("clientProperty_nfipFloodZone").disabled = true;
        }
        else
        {
            getObject("clientProperty_nfipFloodZone").disabled = false;
        }
    }

    /* validate */
    lb = getLabel(obj);

    if (obj.name == "clientProperty_exposedAmt" ||
        obj.name == "clientProperty_contentsAmt" ||
        obj.name == "clientProperty_premiumRate" ||
        obj.name == "clientProperty_replacementValue" ||
        obj.name == "clientProperty_releasePrice") {
        realValue = unformatMoney(obj.value);
        if (!isSignedFloat(realValue, true)) {
            alert(getMessage("ci.common.error.propertyValue.number", new Array(lb)));
            event.returnValue = false;
            return;
        }
        obj.value = formatMoney(realValue);
    }

    if (obj.name == "clientProperty_acquisitionPrice" || obj.name == "clientProperty_appraisedPrice") {
        realValue = unformatMoney(obj.value);
        if (!isFloat(realValue, true)) {
            alert(getMessage("ci.common.error.value.number", new Array(lb)));
            event.returnValue = false;
            return;
        }
        obj.value = formatMoney(realValue);
    }

    if (obj.name == "clientProperty_bldgSqFt" ||
        obj.name == "clientProperty_numberOfFloors" ||
        obj.name == "clientProperty_numberOfBoilers") {
        if (!isInteger(obj.value, true)) {
            alert(getMessage("ci.common.error.value.number", new Array(lb)));
            event.returnValue = false;
            return;
        }
    }

    if (obj.name == "clientProperty_nfipFloodZone") {
        if (!isInteger(obj.value, true)) {
            alert(getMessage("ci.common.error.propertyValue.number", new Array(lb)));
            event.returnValue = false;
            return;
        }
    }

    if (obj.name == "clientProperty_yearBuilt") {
        if (obj.value != '') {
            lowerYear = getObjectValue("clientProperty_yearComputed");
            if (lowerYear == '' || lowerYear == null) {
                /* if can not get lower year boundary from hidden field, set a default value here */
                lowerYear = "1600";
            }
            var today = new Date();
            upperYear = today.getFullYear();
            if (isNum2GrtThanOrEqToNum1(obj.value, upperYear) != 'Y' ||
                isNum2GrtThanOrEqToNum1(lowerYear, obj.value) != 'Y') {
                alert(getMessage("ci.common.error.value.between", new Array(lb, lowerYear, upperYear)));
                event.returnValue = false;
                return;
            }
        }
    }

    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";

    gridDataChange = true;
    if (window.postOnChange) {
        postOnChange(obj);
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
            if (!validate(document.forms[0])) {
                return false;
            }
            var occPct = getObject("clientProperty_occupancyPct");
            var spPct = getObject("clientProperty_sprinkledPct");
            var nonspPct = getObject("clientProperty_nonsprinkledPct");
            var occPctLb = getLabel("clientProperty_occupancyPct");
            var spPctLb = getLabel("clientProperty_sprinkledPct");
            var nonspPctLb = getLabel("clientProperty_nonsprinkledPct");

            var acqDt = getObject("clientProperty_acquisitionDate");
            var acqDtLb = getLabel(acqDt);
            var relDt = getObject("clientProperty_releaseDate");
            var relDtLb = getLabel(relDt);
            var lowerDate = getObjectValue("clientProperty_dateComputed");
            if (lowerDate == '' || lowerDate == null) {
                lowerDate = "01/02/1800";
            }
            var today = formatDate(new Date(), 'mm/dd/yyyy');

            if (occPct.value != "") {
                if (!checkPercent(occPct.value)) {
                    alert(getMessage("ci.common.error.value.percent", new Array(occPctLb)));
                    occPct.select();
                    return false;
                } else {
                    var occPctValue = parseFloat(occPct.value.substr(0, occPct.value.length - 1));
                    if (occPctValue>100) {
                        alert(getMessage("ci.common.error.element.less", new Array(occPctLb)));
                        occPct.select();
                        return false;
                    }
                }
            }

            if (spPct.value!="") {
                if (!checkPercent(spPct.value)) {
                    alert(getMessage("ci.common.error.value.percent", new Array(spPctLb)));
                    spPct.select();
                    return false;
                } else {
                    var spPctValue = parseFloat(spPct.value.substr(0, spPct.value.length - 1));
                    if (spPctValue>100) {
                        alert(getMessage("ci.common.error.element.less", new Array(spPctLb)));
                        spPct.select();
                        return false;
                    }
                }
            }

            if (nonspPct.value!="") {
                if (!checkPercent(nonspPct.value)) {
                    alert(getMessage("ci.common.error.value.percent", new Array(nonspPct)));
                    nonspPct.select();
                    return false;
                } else {
                    var nonspPctValue = parseFloat(nonspPct.value.substr(0, nonspPct.value.length - 1));
                    if (nonspPctValue>100) {
                        alert(getMessage("ci.common.error.element.less", new Array(nonspPctLb)));
                        nonspPct.select();
                        return false;
                    }
                }
            }

            /* check total percent*/
            if (chkTotalPct(spPct.value, nonspPct.value) == false) {
                if (!confirm(getMessage("ci.common.error.element.addUp", new Array(spPctLb, nonspPctLb, count)))) {
                    return false;
                }
            }

            if ((acqDt.value != '') && (isDate2OnOrAfterDate1(lowerDate, acqDt.value) != 'Y' ||
                isDate2OnOrAfterDate1(acqDt.value, today) != 'Y')) {
                alert(getMessage("ci.common.error.element.beforeToday", new Array(acqDtLb, lowerDate, count)));
                return false;
            }

            if (acqDt.value != '' && relDt.value != '' && isDate2OnOrAfterDate1(acqDt.value, relDt.value) != 'Y') {
                alert(getMessage("ci.common.error.element.prior", new Array(relDtLb, acqDtLb, count)));
                return false;
            }
        }

        next(testgrid1);
    }
    return true;
}

//-----------------------------------------------------------------------------
// load  Notes
//-----------------------------------------------------------------------------
function loadPropertyNotes() {
    var pk = testgrid1.recordset("ID").value;
    if (window.loadNotesWithReloadOption) {
        loadNotesWithReloadOption(pk, 'CLIENT_PROPERTY', 'CLIENT_PROPERTY', false, false, 'handleNotesExist');
    } else {
        alert(getMessage("ci.entity.message.notesError.notAvailable"));
    }
}

//-----------------------------------------------------------------------------
// Handle Notes Exist.
//-----------------------------------------------------------------------------
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
// Select address
//-----------------------------------------------------------------------------
function selPropAddr() {
    var dataArray = getSelectedKeys(testgrid1);
    if (dataArray.length != 1) {
        alert(getMessage("ci.common.error.onlyOneRow.noSelect"));
        return;
    }
    selectRowById("testgrid", testgrid1.recordset("ID").value);
    var inAddressPK = testgrid1.recordset("CADDRESSID").value;
    var inSourceRecordFK = getObjectValue("pk");
    var inSourceTableName = "ENTITY";
    var inAddressTypeCode = '';
    openAddressSearchAddWin(inAddressPK, inSourceRecordFK, inSourceTableName,
            inAddressTypeCode, 'N', 'N', 'N', encodeUrl(getObjectValue("entityName")));
}

//-----------------------------------------------------------------------------
// Get address information
//-----------------------------------------------------------------------------
function getInfoFromAddressSearchAdd(infoFromAddressSearchAdd) {
    testgrid1.recordset("CADDRESSID").value = infoFromAddressSearchAdd[0];
    setObjectValue("address_addressLine1", infoFromAddressSearchAdd[3]);
    setObjectValue("address_city", infoFromAddressSearchAdd[6]);
    setObjectValue("address_stateCode", infoFromAddressSearchAdd[7]);
    setObjectValue("address_zipCode", infoFromAddressSearchAdd[9]);
    setObjectValue("address_addressLine2", infoFromAddressSearchAdd[4]);

    /* Grid data changed*/
    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";
}

//-----------------------------------------------------------------------------
// check if the percent is valid
//-----------------------------------------------------------------------------
function checkPercent(fldvalue) {
    if (fldvalue.indexOf('%') == -1)
    {
        fldvalue += '%';
    }
    var pattern = /^((\d{0,3}\.\d{0,3}\%)|(\d{0,3}\%))$/;
    return (pattern.test(fldvalue));
}

function chkPercentFld(fld)
{
    lb = getLabel(fld.name);
    var amt = fld.value;
    if (checkPercent(amt) == false) {
        alert(getMessage("ci.common.error.value.percent", new Array(lb)));
        postChangeReselectField(fld);
        return false;
    }
    else {
        return true;
    }
}


//-----------------------------------------------------------------------------
// set some fields to disabled
//-----------------------------------------------------------------------------
function handleReadyStateReady(tbl) {
    var flag = getObjectValue("clientProperty_nfipFloodB");
    if ((flag == null) || (flag == "") || (flag == 'N'))
    {
        getObject("clientProperty_nfipFloodZone").disabled = true;
    }
    else
    {
        getObject("clientProperty_nfipFloodZone").disabled = false;
    }
}

//-----------------------------------------------------------------------------
// Control disabled attribute of field clientProperty_nfipFloodZone and
//-----------------------------------------------------------------------------
function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
    var flag = getObjectValue("clientProperty_nfipFloodB");
    if ((flag == null) || (flag == "") || (flag == 'N'))
    {
        getObject("clientProperty_nfipFloodZone").disabled = true;
    }
    else
    {
        getObject("clientProperty_nfipFloodZone").disabled = false;
    }
}

function chkTotalPct(num1, num2)
{
    var total = 0;
    var rs1,rs2;
    if ((num1 == '' && num2 == '') || (num1 == '0%' && num2 == '0%')) {
        return true;
    }

    total += parseFloat(num1.substr(0, num1.length - 1)) + parseFloat(num2.substr(0, num2.length - 1));
    if (total != 100) {
        return false;
    }
    return true;
}

//-----------------------------------------------------------------------------
// Override base formatMoney function in edit.js
//-----------------------------------------------------------------------------
function formatMoney(number) {
    var cents = outputCents(number - 0);
    var incr = 0;
    if (cents == ".100") {
        incr = -1;
        cents = ".00";
    }
    var res = outputDollars(Math.floor(number - incr) + '') + cents;
    var WeightPattern = new RegExp("[0-9]");
    if (res != '' && res != '-' && WeightPattern.test(res) && res.substr(0, 1) != '-') {
        res = "$" + res;

    }
    else if (res.substr(0, 1) == '-')
    {
        res = "($" + res.substr(1, res.length) + ")";
    }
    return res;
}

//-----------------------------------------------------------------------------
// Unformat Money field
//-----------------------------------------------------------------------------
function unformatMoney(s) {
    var res = s;
    res = res.replace("$", "");
    res = res.replace(/,/g, "");
    if (res.indexOf("(") != -1) {
        res = "-" + res;
    }
    res = res.replace("(", "");
    res = res.replace(")", "");
    return res;
}
//
//
function goToModule(modulename) {
    if (modulename == 'search') {
        if (isPageGridsDataChanged()) {
            if (!confirm(ciDataChangedConfirmation)) {
                return;
            }
        }
        var url = "ciEntitySearch.do";
        setWindowLocation(url);
    }
}
//}


var hasErrorMessages = "";
function CIPropertyForm_btnClick(asBtn){
    switch (asBtn.toUpperCase()) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            testgrid_updatenode("CUSERSELECTEDB", 'Y');
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            testgrid_updatenode("CUSERSELECTEDB", 'N');
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
    }
}

function handleOnButtonClick(asBtn){
    switch (asBtn.toUpperCase()) {
        case 'SELECT':
            selectProperty();
            break;
        case 'REFRESH':
            setInputFormField("process", "lookupProperty");
            submitFirstForm();
            break;
        case 'CLEAR':
            clearFilterCriteria();
            break;
        default:
    }
}

function selectProperty(){
    var gridId = getCurrentlySelectedGridId();
    var xmlData = getXMLDataForGridName(gridId);
    var selectedDataArray = getSelectedKeys(xmlData);

    if (selectedDataArray.length != 1) {
        alert(getMessage("ci.common.error.onlyOneRow.noSelect"));
        return;
    } else {
        selectRowById("testgrid", testgrid1.recordset("ID").value);
        if (syncPropertyData()) commonOnButtonClick('CLOSE_RO_DIV');
    }
}

function syncPropertyData(){
    var syncSuccessfully = false;
    var clientProperty = new Object();
    var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
    var recordSet = xmlData.recordset;
    if (recordSet && recordSet.Fields) {
        for (var i = recordSet.Fields.Count - 1; i >= 0; i--) {
            var fieldName = recordSet.Fields(i).Name;
            var fieldValue = recordSet(fieldName).value;
            if (fieldName.startsWith('C')) {
                if (fieldName == "CCLIENT_PROPERTY_PK") {// the anchor column
                    fieldName = "CLIENTPROPERTYID";
                    fieldValue = fieldValue.replace(/\D/g, "");
                }
                else {
                    fieldName = 'CP' + fieldName.substring(1); // all names in maintainPropertyLoss starts with CP
                }
                eval("clientProperty." + fieldName + "='" + replace(fieldValue, "\'", "\\\'") + "'");
            }
        }
    }
    syncSuccessfully =  getParentWindow().handleOnSelectProperty(clientProperty);
    return syncSuccessfully;
}

function userRowchange(obj) {
    switch (obj.name) {
        case "chkCSELECT_IND":
            if (testgrid1.recordset("CUSERSELECTEDB").value == 'N') {
                testgrid1.recordset("CUSERSELECTEDB").value = 'Y';
            } else {
                testgrid1.recordset("CUSERSELECTEDB").value = 'N';
            }

            if (testgrid1.recordset("UPDATE_IND").value != "I")
                testgrid1.recordset("UPDATE_IND").value = "Y";

            // Do filter only when not meeting the selected filter criteria.
            if ((getObjectValue("flt_userSelectedB") == "Y" && testgrid1.recordset("CUSERSELECTEDB").value != "-1") ||
                (getObjectValue("flt_userSelectedB") == "N" && testgrid1.recordset("CUSERSELECTEDB").value == "-1")) {
                // Select the first after filtered records.
                setTableProperty(getTableForGrid("testgrid"), "selectedTableRowNo", 1);
                // Set time out to make setTableProperty to effective.
                setTimeout("handleOnChange(getObject(\"flt_userSelectedB\"))", 1);
            }
            break;
    }
}


function handleOnChange(field){
    if (isFilteringField(field.name)) {
        filterGrid();
    } else if (eval("handleOnChangeNonFilterFields")){
        handleOnChangeNonFilterFields(field);
    }
}

function filterGrid() {
    var filteringString = buildFilterStringBasedOnFilteringValues();
    // Select the first after filtered records.
    setTableProperty(getTableForGrid("testgrid"), "selectedTableRowNo", 1);
    testgrid_filter(filteringString);

    setFocusToFirstEditableFormField("testgrid");
}

function getAllFilterFieldsAsArray() {
    var filteringFields = new Array();
    filteringFields.push("flt_city");
    filteringFields.push("flt_stateCode");
    filteringFields.push("flt_zipCode");
    filteringFields.push("flt_addressLine1");
    filteringFields.push("flt_propertyDescription");
    filteringFields.push("flt_propertyID");
    filteringFields.push("flt_propertyType");
    filteringFields.push("flt_locationCode");
    filteringFields.push("flt_userSelectedB");
    return filteringFields;
}
function isFilteringField(fieldName){
    var is = false;
    var allFilteringFields = getAllFilterFieldsAsArray();
    for (var i=0; i<allFilteringFields.length && !is; i++){
        is = (allFilteringFields[i]==fieldName);
    }
    return is;
}

function buildFilterStringBasedOnFilteringValues() {
    var allFilteringFields = getAllFilterFieldsAsArray();
    var filterString = " 1=1 ";
    for (var i = 0; i < allFilteringFields.length; i++) {
        var fieldValue = getObjectValue(allFilteringFields[i]);
        if (!isEmpty(fieldValue) && fieldValue != "-1") {
            var gridColumnName = allFilteringFields[i].replace("flt_", 'C').toUpperCase();
            filterString += " and " + gridColumnName + "='" + fieldValue + "'";
        }
    }
    return filterString;
}

function clearFilterCriteria(){
    var allFilteringFields = getAllFilterFieldsAsArray();
    for (var i = 0; i < allFilteringFields.length; i++) {
        var field = getObject(allFilteringFields[i]);
        field.value = ""; // set to null,
    }

    testgrid_filter("1=1");

    setFocusToFirstEditableFormField("testgrid");
}

function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
}

