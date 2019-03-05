/*
 Description: js file for CIEntityListRole.jsp

 Author: gjlong
 Date: Mar 28, 2008


 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 03/28/2008       Ldong       66237: List Role.
 09/24/2013       xnie        148240: Added function goToPolicy() to open policy information page.
 02/12/2014       hxk         151104
                              1)  Add code for CM and FM when to gotoPolicy function.
 07/13/2016       Elvin       Issue 177515: change goToSource to commonGoToSource
 10/26/2017       kshen       Grid replacement. Change to use getParentWindow to get parent window.
 -----------------------------------------------------------------------------
 (C) 2008 Delphi Technology, inc. (dti)
 */

function btnClick(btn) {
    switch (btn) {
        case 'close':
            if(getParentWindow().getObject("AccountNo"))
                getParentWindow().setObjectValue("AccountNo", "");
            closeWindow();
            break;
        case 'select':
            if (isEmpty(getSelectedRow("roleListGrid"))) {
                alert(getMessage("js.select.row"));
            } else {
                closeWindow();
            }
            break;
    }
}
//-----------------------------------------------------------------------------
// Fix issue 105023, system should retrieve the field's value by field's name rather than its index
// since the index may be changed in other issue.
//-----------------------------------------------------------------------------
function roleListGrid_selectRow(pk) {
    var xmlData = getXMLDataForGridName('roleListGrid');
    var externalObj = xmlData.recordset("CEXTERNALID");
    var roleTypeCodeObj = xmlData.recordset("CROLETYPECODE");
    if(externalObj && getParentWindow().getObject("AccountNo")) {
        getParentWindow().setObjectValue("AccountNo", externalObj.Value);
    }
    if (getObject("fromDocProcess") && !isEmpty(getObjectValue("fromDocProcess")) && 'true' == getObjectValue("fromDocProcess")) {
        if (externalObj && getParentWindow().getObject("externalNumber")) {
            getParentWindow().setObjectValue("externalNumber", externalObj.Value);
        }
        if (roleTypeCodeObj && getParentWindow().getObject("entityRoleTypeCode")) {
            getParentWindow().setObjectValue("entityRoleTypeCode", roleTypeCodeObj.Value);
        }
    }
}

//-----------------------------------------------------------------------------
// Handle On Change
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    switch (field.name) {
        case 'filterRoleTypeCode':
            filterRoleList();
            break;
    }
}

//-----------------------------------------------------------------------------
// Filter Grid List By Role Type Code
//-----------------------------------------------------------------------------
function filterRoleList() {
    if (document.all.roleListGrid1) {
        var filter = "";

        var objFilterRoleTypeCode = getObject("filterRoleTypeCode");
        var selectedFilterRoleTypeCodes = new Array();
        for (var i = 0; i < objFilterRoleTypeCode.options.length; i++) {
            if (objFilterRoleTypeCode.options[i].selected) {
                selectedFilterRoleTypeCodes.push(objFilterRoleTypeCode.options[i].value);
            }
        }

        var roleTypeCodesFilter = "";
        for (var i = 0; i < selectedFilterRoleTypeCodes.length; i++) {
            if (selectedFilterRoleTypeCodes[i] == "-1" || isEmpty(selectedFilterRoleTypeCodes[i])) {
                roleTypeCodesFilter = "";
                break;
            } else {
                if (roleTypeCodesFilter!="") {
                    roleTypeCodesFilter = roleTypeCodesFilter + " or ";
                }
                roleTypeCodesFilter = roleTypeCodesFilter + "CROLETYPECODE = '" + selectedFilterRoleTypeCodes[i] + "'";
            }
        }

        if (roleTypeCodesFilter!="") {
            if (filter!="") {
                filter = filter + " and ";
            }
            filter = filter + "(" + roleTypeCodesFilter + ")";
        }

        if (filter!="") {
            filter += " and ";
        }

        filter = filter + "(UPDATE_IND != 'D' and @id != '-9999')";

        roleListGrid_filter(filter);

        if (getTableProperty(getTableForGrid(roleListGrid), "hasrows")) {
            hideEmptyTable(roleListGrid);
        } else {
            showNonEmptyTable(roleListGrid);
            selectFirstRowInGrid("roleListGrid")
        }
    }
}

// this function is not used anymore
function goToExternalViaLink(pk){
    getRow(roleListGrid1, pk);
    //Get Role Type Code
    var roleTypeCode = roleListGrid1.recordset("CROLETYPECODE").value;
    //Send AJAX to get GoTo Type
    var action = getFormActionAttribute();
    var url = getAppPath() + "/" + action +
              "?process=getGotoSourceUrl&roleTypeCode=" + roleTypeCode +
              "&pk=" + pk + "&date=" + new Date();
    new AJAXRequest("get", url, '', afterGetGotoSourceUrl, false);
}

//-----------------------------------------------------------------------------
// Handle the processing after get Goto Type.
//-----------------------------------------------------------------------------
function afterGetGotoSourceUrl(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            var gotoSourceUrl = "";
            if (oValueList.length > 0) {
                gotoSourceUrl = oValueList[0]["GOTOSOURCEURL"];
                if (gotoSourceUrl != "") {
                    var applicationName = gotoSourceUrl.substring(0, gotoSourceUrl.indexOf(":"));
                    if (applicationName == "") {
                        alert(getMessage("ci.entityRoleList.improperlyGotoSourceUrl"));
                        return;
                    }
                    gotoSourceUrl = getTopNavApplicationUrl(applicationName) + gotoSourceUrl.substr(gotoSourceUrl.indexOf(":")+1)
                            + roleListGrid1.recordset("CEXTERNALID").value;
                }
            }

            if (gotoSourceUrl == "") {
                alert(getMessage("ci.entityRoleList.noGotoSourceUrlConfigured"));
                return;
            }
            var mainwin = window.open(gotoSourceUrl, '', 'width=1000,height=650,resizable=yes,scrollbars=yes,status=yes,top=5,left=5');
        }
    }
}
