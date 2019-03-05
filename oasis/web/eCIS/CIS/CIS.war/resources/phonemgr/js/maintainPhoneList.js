//-----------------------------------------------------------------------------
//  Description: js file for entity phone number list page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  03/22/2010       Kenney      Remove the logic of phone number validation.
//                               Phone number format will be handled by framework
//  04/06/2012       Parker      Call the framework's validation function
//  03/21/2013       Elvin       Issue 141998: same with issue 140043.
//                               - current logic in validateGrid() allows saving more than one primary number
//                                  if there has one primary number and trying to add new primary number
//                               - change the logic to follow changes in issue 140043
//                               - doesn't align the codes as it may looks a lot of changes and hard to check the
//                                  compared files in p4
// 05/06/2013        Elvin       Issue 142696: Add Area Code check in onChange event and save action
// 10/09/2018        dmeng       Issue 195835:grid replacement
// 11/19/2018        hxk         Issue 197157
//                               Make sure we provide the proper message when trying to operate on an
//                               expired address.
//  ---------------------------------------------------
var isChanged = false;
var xdkInstalled = "Y";
//var deleteq = "Are you sure you want to delete the selected record(s)?";
var deleteInd = 'update_ind="D"';

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == 'areaCode') {
        if (!validateAreaCode(getObjectValue("areaCode"))){
            alert(getMessage("ci.entity.message.areaCode.invalid"));
        }
    }
}

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {

    if (getObjectValue("listDisplayed") == 'Y') {
        if (isPageDataChanged()) {
            isChanged = true;
        }
    }


    if (isChanged) {
        if (!confirm(ciDataChangedConfirmation)) {
            return;
        }
    }

    if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    }
    else if (btnID == 'phonenumber') {
        return;
    }
    else if (btnID == 'address'
            || btnID == 'entityclass'
            || btnID == 'entityrole'
            || btnID == 'vendor'
            || btnID == 'vendorAddress') {
        // Go to the appropriate page.
        goToEntityModule(btnID, getObjectValue("pk"),
            getObjectValue("entityName"),
            getObjectValue("entityType"));
    }

    else {
        setObjectValue("process", btnID);
        submitFirstForm();
    }
}
//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    rowcount = testgrid1.recordset.recordCount;
    if (rowcount <= 0) {
        return;
    }
    if (obj.name == "txtCAREACODE") {
        if ((!isSignedFloat(obj.value, true)) && (!obj.value == "")) {
            alert(getMessage("ci.entity.message.areaCode.invalid"));
            window.event.returnValue = false;
            //Old grid cannot use the setCellFocus method
            if(dti.oasis.page.useJqxGrid()){
                dti.oasis.grid.setCellFocus("testgrid", testgrid1.recordset("id").value, "CAREACODE");
            } else {
                obj.select();
            }

        }
        else if (obj.value != "") {
            if (obj.value.length != 3) {
                alert(getMessage("ci.entity.message.areaCode.invalid"));
                window.event.returnValue = false;
                //Old grid cannot use the setCellFocus method
                if(dti.oasis.page.useJqxGrid()){
                    dti.oasis.grid.setCellFocus("testgrid", testgrid1.recordset("id").value, "CAREACODE");
                } else {
                    obj.select();
                }
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Sets the initial values in a new row in the grid.
//-----------------------------------------------------------------------------
function testgrid_setInitialValues() {
        var path = getAppPath() + "/ciPhoneNumberList.do?"
            + "process=getIntialValuesForAddingPhoneNumber"
            +"&id="+getObjectValue("pk")
            +"&sourceRecordId="+getObjectValue("phoneNumber_sourceRecordFK")
            +"&currectTime=" + new Date();

    currentlySelectedGridId = "testgrid";
    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}


//-----------------------------------------------------------------------------
// Handles button click events on the form.
//-----------------------------------------------------------------------------
function CIPhoneNumberListForm_btnClick(asBtn) {
    var pk = getObjectValue("pk");
    
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            break;
        case 'delete':
        case 'DELETE':
            if(validateSourceFK("DELETE")){
                break;
            }
            commonDeleteRow("testgrid");
            break;
        case 'save':
        case 'SAVE':
            if (!isGridDataChanged("testgrid"))
                return;
            if(validateSourceFK("SAVE")){
                break;
            }
            if (!validateGrid()) {
                return;
            }
            setObjectValue("process", "savePhoneList");
            testgrid_update();
            break;
        case 'add':
        case 'ADD':
            if(validateSourceFK("ADD")){
                break;
            } else {
                commonAddRow("testgrid");
                break;
            }
        case 'refresh':
        case 'REFRESH':
            setObjectValue("process", "loadPhoneList");
            if (isChanged || isPageDataChanged()) {
                if (!confirm(ciRefreshPageConfirmation)) {
                    return;
                }
            }
            submitFirstForm();
        break;
    }
}

//-----------------------------------------------------------------------------
// page on load
//-----------------------------------------------------------------------------
function handleOnLoad() {
    var detailDiv = getObject("phoneDetailedDivId");
    var addRowButton = getObject("CI_PHNLST_ADDRW");
    var deleteRowButton = getObject("CI_PHNLST_DELET");

    if (getObjectValue("phoneNumber_sourceRecordFK") != '-2') {
        if(detailDiv!=null)
            hideShowElementByClassName(getObject("phoneDetailedDivId"), true);
        if(addRowButton!=null)
            addRowButton.disabled = false;
        if(deleteRowButton!=null)
            deleteRowButton.disabled = false;

    } else {
        if(detailDiv!=null)
            hideShowElementByClassName(getObject("phoneDetailedDivId"), false, "block");
        if(addRowButton!=null)
            addRowButton.disabled = true;
        if(deleteRowButton!=null)
            deleteRowButton.disabled = true;
    }
}

//-----------------------------------------------------------------------------
// Validates data in the grid.
//-----------------------------------------------------------------------------
function validateGrid() {
    var phnNumPK = "";
    var srcRecFK = "";
    var srcTblName = "";
    var phnNumType = "";
    var areaCode = "";
    var phnNum = "";
    var primNumB = "";
    var msg = "";
    var updateInd = "";
    var primNumFound = false;
    first(testgrid1);
    while (!testgrid1.recordset.eof) {
            msg = "";
            phnNumPK = testgrid1.recordset("CPHONENUMBERID").value;
            updateInd = testgrid1.recordset("UPDATE_IND").value;
            primNumB = testgrid1.recordset("CPRIMARYNUMBERB").value;
            //    alert('PK = ' + phnNumPK + ';  update ind = ' + updateInd +
            //    ';  prim num = ' + primNumB);
            if (primNumB != null && primNumB == "Y") {
                if (!primNumFound) {
                    //        alert('prim num found');
                    primNumFound = true;
                }
                else {
                    //        alert('extra prim num found');
                    msg += getMessage("ci.entity.message.source.onePrimary") + "\n";
                }
            }
            if (updateInd == 'I' || updateInd == 'Y') {
                selectRowById("testgrid", testgrid1.recordset("ID").value);
                if (!validate(document.forms[0])) {
                    return;
                }
                srcRecFK = testgrid1.recordset("CSOURCERECORDID").value;
                srcTblName = testgrid1.recordset("CSOURCETABLENAME").value;
                phnNumType = testgrid1.recordset("CPHONENUMBERTYPECODE").value;
                areaCode = testgrid1.recordset("CAREACODE").value;
                phnNum = testgrid1.recordset("CPHONENUMBER").value;
                if (!isStringCodeValue(srcRecFK)) {
                    msg += getMessage("ci.entity.message.record.sourceRequired") + "\n";
                }
                if (!isStringValue(srcTblName)) {
                    msg += getMessage("ci.entity.message.record.clientRequired") + "\n";
                }
                if (!isStringCodeValue(phnNumType)) {
                    msg += getMessage("ci.entity.message.record.typeRequired") + "\n";
                }
                if (!isStringValue(areaCode)) {
                    msg += getMessage("ci.entity.message.record.areaCodeRequired") + "\n";
                } else if (isNaN(areaCode)) {
                    msg += getMessage("ci.entity.message.areaCode.number") + "\n";
                } else if (!validateAreaCode(areaCode)) {
                    msg += getMessage("ci.entity.message.areaCode.invalid") + "\n";
                }
                //      alert('phone num = ' + phnNum);
                if (!isStringValue(phnNum)) {
                    msg += getMessage("ci.entity.message.record.phoneNumberRequired") + "\n";
                }
            }
            if (msg != '') {
                alert(msg);
                return false;
            }

            next(testgrid1);
    }
    return true;
}

function validateSourceFK(mode){
    var srcRecFK = getObjectValue("phoneNumber_sourceRecordFK");
    var selectSrcMsg = getMessage("ci.entity.message.source.noSelect");
    
    if (srcRecFK == null || srcRecFK == '' ||
        srcRecFK == '-1' || srcRecFK == '-2') {
        alert(selectSrcMsg);
        return true;
    }
    else if (srcRecFK.charAt(0) == 'X') {
        switch(mode) {

            case "ADD":
                alert(getMessage("ci.entity.message.phoneNumber.noAdded"));
                break;
            case  "DELETE":
                alert(getMessage("ci.entity.message.phoneNumber.noDeleted"));
                break;
            case "SAVE":
                alert(getMessage("ci.entity.message.phoneNumber.noChanged"));
                break;
        }
        return true;
    }
    return false;
}

function validateAreaCode(areaCode){
    var valid = true;
    if (areaCode != "") {
        if (areaCode.length != 3) {
            valid = false;
        } else {
            if (!isSignedFloat(areaCode, true)) {
                valid = false;
            }
        }
    }
    return valid;
}

//-----------------------------------------------------------------------------
// Refresh grid on source record FK change.
//-----------------------------------------------------------------------------
function changeSourceRecordFK() {

    if (getObjectValue("listDisplayed") == 'Y') {
         if (isChanged || isPageDataChanged()) {
            if (!confirm(ciDataChangedConfirmation)) {
                setObjectValue("phoneNumber_sourceRecordFK", getObjectValue("currentSourceRecordFK"));
                return;
            }
        }
    }
    setObjectValue("process", "loadPhoneList");
    submitFirstForm();

}

function testgrid_selectRow(pk) {
    rowid = pk;
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
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
//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    if (window.testgrid1 && window.origtestgrid1) {
        return (isChanged || isPageDataChanged());
    } else {
        return isChanged;
    }
}
