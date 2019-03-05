//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 05/10/2013       adeng       143247: Add a new function  doDispControl(), it must be called after grid filter.
// 08/05/2013       awu         147001 - Added regionalTeamListGrid_deleteDependentRow.
// 01/13/2014       awu         148783 - Modified to set pageEntitlements for Show All/Show Term on row level.
// 11/25/20104      kxiang      158853 -
//                              1. Modified regionalTeamListGrid_setInitialValues to change call function
//                              2. Added function handleOnGetInitialValuesForRegionalTeamMember
//                              3. Added function handlePostAddRow to set href to grid Regional Team.
// 01/08/2015       awu         157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                       to wrap the multiple rows deleting.
// 03/03/2015       wdang       160953 - 1) Added handleOnChange() to sync effectiveToDateLong for filtering.
//                                       2) Modified the input parameters for pageEntitlements in handleOnButtonClick().
// 10/22/2015       lzhang      166954 - Modify regionalTeamListGrid_selectRow function: only display current members
//                                       if show all button is displayed when switch team.
// 03/30/2017       lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADDTEAM':
            commonAddRow("regionalTeamListGrid");
            break;
        case 'DELETETEAM':
            commonDeleteRow("regionalTeamListGrid");
            break;
        case 'ADDMEMBER':
            commonAddRow("regionalTeamMemberListGrid");
            break;
        case 'DELETEMEMBER':
            commonDeleteRow("regionalTeamMemberListGrid");
            break;
        case 'CURRENT':
            // Get the time of today.
            var today = formatDate(new Date, "mm/dd/yyyy");
            today = getRealDate(today, "mm/dd/yyyy").getTime();
            var currentTeamDataGrid = getXMLDataForGridName("regionalTeamListGrid");
            var regionalTeamId = currentTeamDataGrid.recordset("ID").value;
            setTableProperty(eval("regionalTeamMemberListGrid"), "selectedTableRowNo", null);
            regionalTeamMemberListGrid_filter("CREGIONALTEAMID = " + regionalTeamId + " and CEFFECTIVETODATELONG > " + today);
            doDispControl();
            currentTeamDataGrid.recordset("CISSHOWCURRENTAVAILABLE").value = 'N';
            currentTeamDataGrid.recordset("CISSHOWALLAVAILABLE").value = 'Y';
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(true, "regionalTeamListGrid");
            }
            break;
        case 'ALL':
            var currentTeamDataGrid = getXMLDataForGridName("regionalTeamListGrid");
            var regionalTeamId = currentTeamDataGrid.recordset("ID").value;
            setTableProperty(eval("regionalTeamMemberListGrid"), "selectedTableRowNo", null);
            regionalTeamMemberListGrid_filter("CREGIONALTEAMID = " + regionalTeamId);
            doDispControl();
            currentTeamDataGrid.recordset("CISSHOWCURRENTAVAILABLE").value = 'Y';
            currentTeamDataGrid.recordset("CISSHOWALLAVAILABLE").value = 'N';
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(true, "regionalTeamListGrid");
            }
            break;
        default:break;
    }
}

//-----------------------------------------------------------------------------
// Delete team member row when deleting team row. This function is called by commonDeleteRow() of common.js
//
function regionalTeamListGrid_deleteDependentRow() {
    var currentTeamDataGrid = getXMLDataForGridName("regionalTeamListGrid");
    var regionalTeamId = currentTeamDataGrid.recordset("ID").value;
    first(regionalTeamMemberListGrid1)
    beginDeleteMultipleRow("regionalTeamMemberListGrid");
    while (!regionalTeamMemberListGrid1.recordset.eof) {
        if (regionalTeamMemberListGrid1.recordset("CREGIONALTEAMID").value == regionalTeamId) {
            setSelectedRow("regionalTeamMemberListGrid", regionalTeamMemberListGrid1.recordset("ID").value);
            regionalTeamMemberListGrid_deleterow();
        }
        next(regionalTeamMemberListGrid1);
    }
    endDeleteMultipleRow("regionalTeamMemberListGrid");
    first(regionalTeamMemberListGrid1);
    hideShowForm("regionalTeamMemberListGrid");
}

//-----------------------------------------------------------------------------
// Since there are two grid in this page and system only checks the current grid (regionalTeamMemberListGrid) in commonOnSubmit(),
// we should call commonValidateGrid(regionalTeamListGrid) to ensure this grid will be validated.
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            if (commonValidateGrid("regionalTeamListGrid")) {
                document.forms[0].process.value = "saveAllRegionalTeamAndUnderwriter";
                alternateGrid_update('regionalTeamListGrid');
                alternateGrid_update('regionalTeamMemberListGrid');
            }
            else{
                proceed = false;
            }
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}

function regionalTeamListGrid_selectRow(id) {
    setTableProperty(eval("regionalTeamMemberListGrid"), "selectedTableRowNo", null);
    var currentTeamDataGrid = getXMLDataForGridName("regionalTeamListGrid");

    // Get the time of today.
    var today = formatDate(new Date, "mm/dd/yyyy");
    today = getRealDate(today, "mm/dd/yyyy").getTime();

    if (currentTeamDataGrid.recordset("CISSHOWALLAVAILABLE").value == 'Y') {
        regionalTeamMemberListGrid_filter("CREGIONALTEAMID = " + id + " and CEFFECTIVETODATELONG > " + today);
    }
    else {
        regionalTeamMemberListGrid_filter("CREGIONALTEAMID = " + id);
    }
    doDispControl();
}

function getParentGridId() {
    return "regionalTeamListGrid";
}

function getChildGridId() {
    return "regionalTeamMemberListGrid";
}

function regionalTeamListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/regionalmgr/maintainRegionalTeam.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForRegionalTeam";

    new AJAXRequest("get", url, '', setInitialValuesForRegionalTeam, false);
}

function regionalTeamMemberListGrid_setInitialValues() {
    var currentTeamDataGrid = getXMLDataForGridName("regionalTeamListGrid");
    var regionalTeamId = currentTeamDataGrid.recordset("ID").value;
    var url = getAppPath() + "/policymgr/regionalmgr/maintainRegionalTeam.do?regionalTeamId=" + regionalTeamId
            + commonGetMenuQueryString() + "&process=getInitialValuesForRegionalTeamMember";

    new AJAXRequest("get", url, '', handleOnGetInitialValuesForRegionalTeamMember, false);
}

function handleOnGetInitialValuesForRegionalTeamMember(ajax) {
    commonHandleOnGetInitialValues(ajax, "ENTITYIDHREF");
}

function setInitialValuesForRegionalTeam(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("regionalTeamListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}
//==================================================================================================
// When the administrator selects the team member name, system retrieves the underwriter Id from DB.
//==================================================================================================
function handleOnChange(obj) {
    if (obj.name == 'entityId') {
        var url = getAppPath() + "/policymgr/regionalmgr/maintainRegionalTeam.do?entityId=" + obj.value
                + commonGetMenuQueryString() + "&process=getUnderwriterId";

        new AJAXRequest("get", url, '', setUnderwriterId, false);
    }
}

function setUnderwriterId(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                getObject("underwriterID").value = oValueList[0]["UNDERWRITERID"];
            }
            else {
                getObject("underwriterID").value = "";
            }
        }
    }
}

function doDispControl() {
    var memberXmlData = getXMLDataForGridName("regionalTeamMemberListGrid");
    if (isEmptyRecordset(memberXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(memberXmlData));
        hideGridDetailDiv("regionalTeamMember");
    }
    else {
        showNonEmptyTable(getTableForXMLData(memberXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("regionalTeamMember"), false);
    }
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "regionalTeamMemberListGrid") {
        var xmlData = getXMLDataForGridName("regionalTeamMemberListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var entityCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CENTITYID") {
                entityCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + entityCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("ENTITYIDHREF"))) {
                    href = "javascript:handleOnGridHref('regionalTeamMemberListGrid', '"
                            + getObjectValue("ENTITYIDHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Sync effectiveToDateLong for filtering
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == "effectiveToDate" && isValueDate(field.value)) {
        regionalTeamMemberListGrid1.recordset("CEFFECTIVETODATELONG").value = getRealDate(field.value).getTime();
    }
}