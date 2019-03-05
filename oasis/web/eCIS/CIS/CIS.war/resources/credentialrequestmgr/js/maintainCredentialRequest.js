/**
 *  Created by jdingle on 03/08/2016.
 Revision Date    Revised By  Description
 ---------------------------------------------------------------------------------------------------------------
   05/12/2016     jld         Remove ProcessingImgIndicator. refresh account grid on fee reversal.
   10/15/2018     dpang       195835: Grid replacement
 ---------------------------------------------------------------------------------------------------------------
 */
var isChanged = false;
var DETAIL_LIST_GRID_ID = "detailListGrid";
var ACCOUNT_LIST_GRID_ID = "accountListGrid";
//-----------------------------------------------------------------------------
// page on load
//-----------------------------------------------------------------------------
function handleOnLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("accountListGrid")).then(function () {
        processAfterAccountListGridReady();
    });
}

function processAfterAccountListGridReady() {
    if (getTableProperty(getTableForGrid("accountListGrid"), "hasrows")) {
        if (hasObject("billingAccountId") && isStringValue(getObjectValue("billingAccountId"))) {
            first(accountListGrid1);
            while (!accountListGrid1.recordset.eof) {
                if  (accountListGrid1.recordset("CBILLINGACCOUNTID").value == getObjectValue("billingAccountId"))  {
                    accountListGrid_selectRow(accountListGrid1.recordset("ID").value);
                    accountListGrid1.recordset("CSELECT_IND").value = '-1';
                    break;
                }
                next(accountListGrid1);
            }
            first(accountListGrid1);
        }
    } else {
        enableDisableField(getObject('CI_CRACCT_ADJ'), true);
        enableDisableField(getObject('CI_CRACCT_CLR'), true);
    }
    if (!getTableProperty(getTableForGrid("detailListGrid"), "hasrows")) {
        enableDisableField(getObject('CI_CRDET_DEL_AI'), true);
    }
    if (hasObject("submitRequest") && getObjectValue("submitRequest") == "Y") {
        var ciCredReqId = getObjectValue("ciCredReqId");
        if (ciCredReqId > 0) {
            var path = getAppPath() + "/ciCredentialRequest.do?"
                + "process=submitRequest&ciCredReqId="+ciCredReqId;
            path += "&processingType=CREATE&currentTime=" + Date.parse(new Date());
            var formWin = openPopup(path, "Forms", 1024, 768, 10, 10, 'yes');
            formWin.focus();
        }
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
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// click on button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'save':
            if (!validate(document.forms[0])) {
                return;
            }
            if (!validateGrid()) {
                return;
            }
            alternateGrid_update("detailListGrid");
            setInputFormField("process", "saveRequest");
            submitFirstForm();
            break;
        case 'createAcct':
            alternateGrid_update("detailListGrid");
            setInputFormField("process", "saveAccount");
            submitFirstForm();
            break;
        case 'adjust':
            var dataArray = getSelectedKeys(accountListGrid1);
            if (dataArray.length != 1) {
                alert(getMessage("js.selectRowBeforeNextAction"));
                return;
            }
            getRow(accountListGrid1, dataArray[0]);
            var adjUrl = getAppPath() + "/ciCredentialFeeAdjust.do?";
            adjUrl += "billingAccountId=" + accountListGrid1.recordset("CBILLINGACCOUNTID").value;
            var acctTitle = "Account: " + accountListGrid1.recordset("CACCOUNTNO").value;
            openDivPopup(acctTitle, adjUrl, true, true, 20, 10, 800, 400, "", "", "", false, "", "", true);
            break;
        case 'clearAcct':
            updateFilterNode(accountListGrid1, "CSELECT_IND", '-1', "CSELECT_IND", '0');
            selectFirstRowInGrid("accountListGrid");
            setObjectValue("billingAccountId", "",true);
            setObjectValue("accountNo", "",true);
            break;
        case 'close':
            if (!isOkToChangePages()) {
                return;
            }
            closeWindow();
            break;
        default:
            break;
    }
}

function find(findId) {
    if (findId == "entityName") {
        openEntitySelectWinFullName("detailEntityId", "entityName", "handleOnSelectEntity()","P");
    }
}

function refreshPage() {
    setInputFormField("process", "init");
    submitFirstForm();
}

//--------------------------------------------------------
//  call back function of  openEntitySelectWinFullName for selecting detailEntity
//--------------------------------------------------------
function handleOnSelectEntity() {
    var newEntityId = getObjectValue("detailEntityId");
    detailListGrid1.recordset("CDETAILENTITYID").value = newEntityId;
    var path = getAppPath() + "/ciCredentialRequest.do?"
        + "process=loadEntity&entityId="+newEntityId;
    path += "&currentTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', setEntityDetail, false);
}

function setEntityDetail(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
                for (var prop in oValueList[0]) {
                    setFieldReadonly(prop);
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// click on button for request detail grid
//-----------------------------------------------------------------------------
function commonOnButtonClickDetailListGrid(asBtn) {
    currentlySelectedGridId = DETAIL_LIST_GRID_ID;
    commonOnButtonClick(asBtn);
    if (!getTableProperty(getTableForGrid("detailListGrid"), "hasrows")) {
        enableDisableField(getObject('CI_CRDET_DEL_AI'), true);
    }
}

//-----------------------------------------------------------------------------
// click on button for request account grid
//-----------------------------------------------------------------------------
function commonOnButtonClickAccountListGrid(asBtn) {
    currentlySelectedGridId = ACCOUNT_LIST_GRID_ID;
    commonOnButtonClick(asBtn);
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;
}

//-----------------------------------------------------------------------------
// Select the detail row
//-----------------------------------------------------------------------------
function detailListGrid_selectRow(rowId) {
    getRow(detailListGrid1, rowId);
    var lastName = getObjectValue("lastName");
    var firstName = getObjectValue("firstName");
    if (isStringValue(lastName) && isStringValue(firstName) ) {
        setObjectValue("entityName",lastName+", "+firstName);
    } else{
        setObjectValue("entityName","");
    }
}

//-----------------------------------------------------------------------------
// Select the account row
//-----------------------------------------------------------------------------
function accountListGrid_selectRow(rowId) {
    getRow(accountListGrid1, rowId);
}

function detailListGrid_setInitialValues(){
    var initCharge = getObject("initChargeFeeB").value;
    setObjectValue("feeB",initCharge);
    setObjectValue("claimHistoryB","Y");
    setObjectValue("entityName","");
    setObjectValue("legacyDataId","Manual Entry");
    enableDisableField(getObject('CI_CRDET_DEL_AI'), false);
}

function accountListGrid_setInitialValues(){
}

function selectAccount() {
    setObjectValue("billingAccountId", accountListGrid1.recordset("CBILLINGACCOUNTID").value,true);
    setObjectValue("accountNo", accountListGrid1.recordset("CACCOUNTNO").value,true);
}

function userRowchange(c) {
    if ((c.name == 'chkCSELECT_IND') && c.checked) {
        var selId = accountListGrid1.recordset("ID").value;
        var accountHold = accountListGrid1.recordset("CACCOUNTONBILLINGHOLDB").value;
        updateFilterNode(accountListGrid1, "CSELECT_IND", '-1', "CSELECT_IND", '0');
        first(accountListGrid1);
        getRow(accountListGrid1, selId);
        if (accountHold=="Y"){
            alert(getMessage("ci.credentialRequest.account.onHold"));
        } else {
            accountListGrid1.recordset("CSELECT_IND").value = '-1';
            selectAccount();
        }
    }
}

//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    var rowcount = detailListGrid1.recordset.recordCount;
    var chargeFeeExists = false;
    var billingAccountId = getObjectValue("billingAccountId");

    if (!isStringValue(billingAccountId)) {
        first(detailListGrid1);
        for (i = 0; i < rowcount; i++) {
            if (detailListGrid1.recordset("CFEEB").value == "Y") {
                chargeFeeExists = true;
                break;
            }
            detailListGrid1.recordset.movenext();
        }

        if (chargeFeeExists) {
            alert(getMessage("ci.credentialRequest.account.missing"));
            selectFirstRowInGrid("detailListGrid");
            return false;
        }
    }
    return true;
}
