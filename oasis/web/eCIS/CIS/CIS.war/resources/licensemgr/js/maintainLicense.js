//-----------------------------------------------------------------------------
//  Description: js file for entity license list page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  04/06/2012       Parker      Call the framework's validation function
//  02/01/2018       dpang       Issue 191109: add system parameter to enable deleting existing license record.
//  10/10/2018       dmeng       Issue 195835: grid replacement.
//  ---------------------------------------------------
//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// override the function in gridbtnclicks.js to prevent deleting saved record
//-----------------------------------------------------------------------------
function CILicenseForm_btnClick(asBtn){
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            handleDeleteButton();
            break;
    }
}

//-----------------------------------------------------------------------------
// override the function in gridbtnclicks.js to prevent deleting saved record
//-----------------------------------------------------------------------------
function frmGrid_btnClick(asBtn) {
    switch (asBtn) {
        case 'DELETE':
            rowid = commonDeleteRow("testgrid", allowDeleteExistRecord() ? 'Y' : 'N');
            break;
        case 'SAVE':
            first(testgrid1);
            while (!testgrid1.recordset.eof) {
                var upd = testgrid1.recordset("UPDATE_IND").value;
                if (upd == 'I' || upd == 'Y')
                    if (!validateGrid()) {
                        return;
                    }

                next(testgrid1);
            }
            setObjectValue("process", "saveLicense");
            testgrid_update();
            break;
        case 'ADD':
            commonAddRow("testgrid");
            break;
        case 'REFRESH':
            if (isPageDataChanged()) {
                if (!confirm(ciRefreshPageConfirmation)) {
                    return;
                }
            }
            setObjectValue("process", "loadLicense");
            submitFirstForm();
            break;
    }
}

//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    selectRowById("testgrid", testgrid1.recordset("ID").value);
    if (!validate(document.forms[0])) {
        return;
    }
    var olicensedDt = getObject("licenseProfile_dateLicensed");
    var oexpDt = getObject("licenseProfile_expirationDate");
    var licensedDtLb = getLabel(olicensedDt);
    var expDtLb = getLabel(oexpDt);

    /* validate date field */
    if (isDate2OnOrAfterDate1(olicensedDt.value, oexpDt.value) == 'N') {
        alert(getMessage("ci.common.error.licenseDate.before", [licensedDtLb, expDtLb]));
        return false;
    }

	/*VA Rule checking:  varChecking()    in var.js                        */
	try {
        if(!varChecking()){
			return false;
	    }
    }
    catch (e){
        /* ignore */
    }
    return true;

}

//-----------------------------------------------------------------------------
// This function is to set initial values for new added row in grid.
// It sets the entityId for new row here.
//-----------------------------------------------------------------------------
function testgrid_setInitialValues(){
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
    testgrid1.recordset("CVALIDLICENSEB").value = getObjectValue("ci_license_valid_b");
    var path = getAppPath() + "/ciLicense.do?"
        + "process=getInitialValuesForAddLicense";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', localHandleOnGetInitialValues, false)
}

//-----------------------------------------------------------------------------
// handle record checkbox click event to enable/disable delete button.
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    switch (obj.name) {
        case "chkCSELECT_IND":
            handleDeleteButton();
            break;
    }
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for License
//-----------------------------------------------------------------------------
function localHandleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                // set page fields also
                setFormFieldValuesByObject(oValueList[0]);
            }
        }
    }
}

function handleReadyStateReady() {
    handleDeleteButton();
}

function handleDeleteButton() {
    checkIfEnableDeleteButton(testgrid1, "LISEN_DEL", allowDeleteExistRecord());
}

function getGridId() {
    return 'testgrid';
}

function allowDeleteExistRecord() {
    return sys_parm_ci_del_license == "Y" && isEntityReadOnlyYN == 'N';
}

function handleOnSelectAll(gridId, checked) {
    handleDeleteButton();
}