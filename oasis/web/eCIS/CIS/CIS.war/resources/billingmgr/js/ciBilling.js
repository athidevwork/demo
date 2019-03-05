function displayAccount(pk) {
    selectRow(getCurrentlySelectedGridId(), pk);
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    var selectedAccountNo = selectedDataGrid.recordset("CACCOUNTNO").value;
    var url = getTopNavApplicationUrl("FM")
            + "/fullinquirymgr/viewAllTransactionsForAccount.do?" + "accountNo="
            + selectedAccountNo + "&date=" + new Date();
    openWebApplication(url, true);
}

function accountListGrid_selectRow(id) {
    var xmlData = getXMLDataForGridName("accountListGrid");
    var acctNo = xmlData.recordset("CACCOUNTNO").value;
    var url = getTopNavApplicationUrl("FM")
            + "/fullinquirymgr/viewPolicyTermForAccount.do?"
            + "accountNo="+ acctNo
            + "&headerHidden=Y"
            + "&isAccountHeaderHidden=Y"
            + "&isTabMenuHidden=Y"
            + "&date=" + new Date();
    getObject("iframePolicyTerm").src = url;
}

function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function btnClick(btnID) {
    var url = window.location.href;
    var entityPk = url.substring(url.indexOf("?pk=") + 4, url.indexOf("&"));
    var entityType = url.substring(url.indexOf("entityType=") + 11, url.indexOf("entityType=") + 12);
    var entityName = url.substring(url.indexOf("entityName=") + 11);
    if (btnID == 'entity') {
        var url = "?pk=" + entityPk + "&entityType=" + entityType;
        if (entityType.substr(0, 1) == 'P') {
            url = getAppPath() + "/ciEntityPersonModify.do" + url;
            setWindowLocation(url);
        }
        else if (entityType.substr(0, 1) == 'O') {
            url = getAppPath() + "/ciEntityOrgModify.do" + url;
            setWindowLocation(url);
        }
    }
    else if (btnID == 'phonenumber'
        || btnID == 'entityclass'
        || btnID == 'entityrole'
        || btnID == 'vendor'
        || btnID == 'vendorAddress'
        || btnID == 'address') {
        // Go to the appropriate page.
        goToEntityModule(btnID, entityPk, entityName, entityType);
    } else if (btnID == "imageRight") {
        cisBillStartImageRightDeskTop(entityPk, "PROGRAM_STEP_HISTORY_VIEW");
    }
}
function cisBillStartImageRightDeskTop(sourceTable, sourceData) {

    // First get the data element to be used
    // Check if the field is in the form

    if (sourceData == '') {
        alert(getMessage("ci.entity.message.imageRight.determine"));
        return;
    }
    // Second take the source data and source table and get the IR file number and drawer
    var url = getCSPath() + "/imagerightmgr/maintainImageRight.do?" +
              "&sourceData=" + sourceData +
              "&sourceTable=" + sourceTable;
    url +=   "&date=" + new Date();
    // initiate async call
    new AJAXRequest("get", url, '', handleStartImageRightDeskTop, false);
}

//-----------------------------------------------------------------------------
// go to any of the common entity-related modules from any page
//-----------------------------------------------------------------------------
function goToEntityModule(module, pk, entityName, entityType) {
    var url = "?pk=" + pk +
              "&entityName=" + entityName +
              "&entityType=" + entityType;
    if (module == 'address') {
        // Go to the address page.
        url = getAppPath() + "/ciAddressList.do" + url;
        setWindowLocation(url);
    }
    else if (module == 'phonenumber') {
        // Go to the phone number page.
        url = getAppPath() + "/ciPhoneNumberList.do" + url;
        setWindowLocation(url);
    }
    else if (module == 'entityclass') {
        // Go to the entity class page.
        url = getAppPath() + "/ciEntityClassList.do" + url;
        setWindowLocation(url);
    }
    else if (module == 'entityrole') {
        // Go to the entity role page.
        url = getAppPath() + "/ciEntityRole.do" + url;
        setWindowLocation(url);
    }
    else if (module == 'vendor') {
        // Go to the vendor page.
        url = getAppPath() + "/ciVendor.do" + url;
        setWindowLocation(url);
    }
    else if (module == 'vendorAddress') {
        // Go to the vendor address page.
        url = getAppPath() + "/ciVendorAddress.do" + url;
        setWindowLocation(url);
    }
}

//-----------------------------------------------------------------------------
// Rewrite Opens the Form Letters popup method using absolute path
//-----------------------------------------------------------------------------
function cisFolderOpenFormLetters() {
    if (document.forms[0].pk) {
        // claimPK is a field on the form.
        var url = getCSPath() + "/csFormLetter.do?sourceTableName=ENTITY&sourceRecordFk=" + document.forms[0].pk.value;
        openPopup(url, "Forms", 720, 620, 10, 10, 'yes');
    } else {
        alert(getMessage("ci.entity.message.formLetters.open"));
    }
}
