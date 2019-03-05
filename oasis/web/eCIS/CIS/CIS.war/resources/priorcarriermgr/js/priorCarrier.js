var isChanged = false;

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    rowcount = testgrid1.recordset.recordCount;
    // issue #107079
    first(testgrid1);
    while (!testgrid1.recordset.eof) {
        var upd_ind = testgrid1.recordset("UPDATE_IND").value;
        if (upd_ind == 'I' || upd_ind == 'Y') {
            selectRowById("testgrid", testgrid1.recordset("ID").value);
            if (!validate(document.forms[0], true)) {
                return false;
            }
        }
        next(testgrid1);
    }
    return true;
}
//-----------------------------------------------------------------------------
// This function is needed to navi to some tabs(vendor,address...)
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID != 'save' && btnID != 'add' && btnID != 'delete' && confirmChanges()) {
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
    if (btnID == 'address' ||
        btnID == 'phonenumber' ||
        btnID == 'entityclass' ||
        btnID == 'entityrole' ||
        btnID == 'vendor' ||
        btnID == 'vendorAddress' ||
        btnID == 'entity') {
        var entityType = getObjectValue('entityType');
        var entityName = getObjectValue('entityName');
        if (btnID == 'entity') {
            goToEntityModify(getObjectValue("pk"), entityType);
        } else {
            goToEntityModule(btnID, getObjectValue("pk"),
                entityName, entityType);
        }
    }
    if (btnID == 'add') {
        commonAddRow("testgrid");
        checkSelectBox();
    } else if (btnID == 'delete') {
        // get selected keys
        var dataArray = getSelectedKeys(testgrid1);
        if (dataArray.length == 0) {
            alert(getMessage("ci.common.error.rowSelect.delete"));
            return;
        }
        if (confirm(getMessage("js.delete.confirmation"))) {
            var len = dataArray.length;
            beginDeleteMultipleRow("testgrid");
            for (var i = 0; i < len; i++) {
                selectRow("testgrid", dataArray[i]);
                if (rowid == dataArray[i] || testgrid1.recordset.recordcount == 1)
                    rowid = "-1";

                if (getObjectValue("idAuditEnabled") == "Y") {
                    if (testgrid1.recordset("CCHAR5").value == "Y") {
                        addedAuditRecord = false;
                        deletedAuditRecord = true;
                    }
                }
                testgrid_deleterow();
                checkForm();
            }
            endDeleteMultipleRow("testgrid");
        }
    } else if (btnID == 'search') {
        if (getObject("filter_lossDate").value == "mm/dd/yyyy") {
            setObjectValue("filter_lossDate", "");
        }
        setObjectValue("process", "loadAllPriorCarrier");
        submitFirstForm();
    } else if (btnID == 'clear') {
        clearAllFilterCriteria();
        setObjectValue("process", "loadAllPriorCarrier");
        submitFirstForm();
    } else if (btnID == 'save') {
        if (!validateGrid()) {
            return;
        }
        setObjectValue("process", "savePriorCarrier");
        testgrid_update();
    } else if (btnID == 'refresh') {
        setObjectValue("process", "loadAllPriorCarrier");
        submitFirstForm();
    }
}

function clearAllFilterCriteria(){
    var allFields = getObjectValue('filterFieldList').split(",");
    for (var i=0; i<allFields.length; i++){
        if (!isEmpty(allFields[i]) && hasObject(allFields[i])){
            setObjectValue(allFields[i], "");
        }
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------

function handleOnChange(field) {
    // Exclude the filter field.
    if (isFilterCriteriaField(field.name)) {
        return;
    }

    // If we are changing Loss Run Date, we need to set the orig loss run date to empty,
    // so the system will always think it's a new value.
    if (field.name == 'lossRunDate' && field.value != "") {
        //setItem(testgrid1, "CORIGLOSSRUNDATE", "");
        setItem(testgrid1, "CLOSSRUNDATECHANGEDB", "Y");
    }

    // If Changing a field except Loss Run Date, reset the Loss Run Date if it is not modified.
    if (field.name != 'lossRunDate' && getObjectValue("isLogHist") == "Y"
        && getObjectValue('lossRunDate') == testgrid1.recordset('CORIGLOSSRUNDATE').value) {
        setItem(testgrid1, "CLOSSRUNDATE", "");
    }

    // Reset the Term year if record changed.
    if (getObjectValue("CI_PRIOR_CARR_RST_YR") == "Y") {
        if (field.name != 'externalClaimsReportSummary_termYear') {
            if (getObjectValue("defaultTermYear") != "") {
                setItem(testgrid1, "CTERMYEAR", getObjectValue("defaultTermYear"));
            }
        }
    }

    if (field.name == "claimantname") {
        setObjectValue("claimantEntityId", "");
    }
    if (field.name == "externalClaimsReportSummary_carrierName") {
        setObjectValue("carrierEntityId", "");
    }
    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";
    lb = getLabel(field);

}

function isFilterCriteriaField(fieldName){
    return (","+getObjectValue('filterFieldList')+",").indexOf(","+fieldName+",") !=-1;
}

function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
}

function confirmChanges() {
    return isPageGridsDataChanged();
}
function testgrid_setInitialValues() {
    setItem(testgrid1, "CSUPPLEMENTALFLAG","Y");
    setItem(testgrid1, "CTERMYEAR", getObjectValue("defaultTermYear"));
    setItem(testgrid1, "CENTITYID", getObjectValue("pk"));

    var path = getCISPath() + "/ciPriorCarrier.do?process=getInitialValuesForPriorCarrier&entityId=" + getObject("pk").value;
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForPriorCarrier, false);
}

function viewHistory(EXTERNAL_CLAIMS_RPT_SUMMARY_PK) {
    var path = "ciPriorCarrierHistory.do?pk=" +EXTERNAL_CLAIMS_RPT_SUMMARY_PK;

    var mainwin = window.open(path, 'ViewHistory',
        'top=10,left=10, width=1000,height=600,innerHeight=550,innerWidth=950,resizable,scrollbars');
    mainwin.focus();
}

function find(findId) {
    if (findId == "claimantname") {
        //openEntitySelectWinFullName("claimantEntityFk", "claimantname");
        openEntitySelectWinEvtDfltEntCls('claimantEntityId','claimantname','', '', '', '','CLAIMANT', 'N', "handleOnSelectEntity()");
    } else if (findId == "filter_claimantName") {
        //openEntitySelectWinFullName("", "filter_claimantName");
        openEntitySelectWinEvtDfltEntCls('','filter_claimantName','', '', '', '','CLAIMANT', 'N', '');
    }
    if (findId == "externalClaimsReportSummary_carrierName") {
        //openEntitySelectWinFullName("carrierEntityFk", "carrierName");
        openEntitySelectWinEvtDfltEntCls('carrierEntityId','externalClaimsReportSummary_carrierName','', '', '', '','CARRIER', 'N', 'handleOnSelectEntity()');
    } else if (findId == "filter_carrierName") {
        //openEntitySelectWinFullName("", "filter_carrierName");
        openEntitySelectWinEvtDfltEntCls('','filter_carrierName','', '', '', '','CARRIER', 'N', '');
    }
}

function handleOnSelectEntity() {
    if (testgrid1.recordset("UPDATE_IND").value != "I") {
        testgrid1.recordset("UPDATE_IND").value = "Y";
    }
}

var addedAuditRecord = false;
var deletedAuditRecord = false;
function handleOnGetInitialValuesForPriorCarrier(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                for (var prop in oValueList[0]) {
                    // If the current field is char5, and the value of it is "Y", we need to check
                    // confirm if chart can be set to "Y".
                    if (prop == "externalClaimsReportSummary_char5" && getObjectValue("idAuditEnabled") == "Y") {
                        if (deletedAuditRecord) {
                            setInputFormField(prop, "Y");
                            addedAuditRecord = true;
                            deletedAuditRecord = false;
                        } else if (!addedAuditRecord && oValueList[0][prop] == "Y") {
                            setInputFormField(prop, "Y");
                            addedAuditRecord = true;
                            deletedAuditRecord = false;
                        } else {
                            setInputFormField(prop, "N");
                        }
                    } else {
                        setInputFormField(prop, oValueList[0][prop]);
                    }
                }
            }
        }
    }
}

function handleReadyStateReady() {
    checkSelectBox();
}

function checkSelectBox() {

    if (!dti.oasis.page.useJqxGrid()){
        if (getObjectValue("idAuditEnabled") == "Y") {
            var isUserAllowedToDeleteAuditRecords = getObjectValue("isUserAllowedToDeleteAuditRecords");

            if (isUserAllowedToDeleteAuditRecords == "N") {
                var flags = $("input[name='txtCCHAR5']");
                for (var i = 0; i < flags.length; i++) {
                    if (flags[i].value == "Y") {
                        $(flags[i]).parent().parent().find("input[name='chkCSELECT_IND']")[0].disabled = true;
                    }
                }
            }
        }

    }
}

function handleGetCustomPageOptions() {
    function __isCellEditable (jqxRowIndex, datafield, columntype, value) {
        var CCHAR5Value = dti.oasis.grid.getRowDataByJqxRowIndex("testgrid", jqxRowIndex)["CCHAR5"];

        if (getObjectValue("idAuditEnabled") == "Y"){
            var isUserAllowedToDeleteAuditRecords = getObjectValue("isUserAllowedToDeleteAuditRecords");

            if (isUserAllowedToDeleteAuditRecords == "N"){
                return ("Y" != CCHAR5Value);
            }
        }
        return true;
    }

    return dti.oasis.page.newCustomPageOptions()
        .cellBeginEdit("testgrid", "CSELECT_IND", __isCellEditable)
        .addIsCellEditableFunction("testgrid", "CSELECT_IND", __isCellEditable);
}

function excludeFieldsForSettingUpdateInd() {
    return ["filter_claimantName", "filter_carrierName", "filter_healthcareProvider","filter_lossDate",
        "filter_reportDate", "filter_licenseNumber", "filter_licenseState", "filter_specialtyCode"];
}
