/*
 Description: js file for manageApplication.js

 Author: adeng
 Date: April 18, 2013


 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 04/18/2013       adeng       141786 - Modified handleOnButtonClick to use HTTP method "get" to transfer data
                              by pass parameters to the url when end user click on button "search" "Refresh" "Clear".
 05/11/2013       adeng       144754 - Modified loadAllApplication to trim out blank from the fields.
 06/27/2013       adeng       145166 - Modified userReadyStateReady() to call commonReadyStateReady() to select the row.
 03/12/2014       xnie        152831 - Modified handleOnButtonClick() to support re-wip all of checked records.
 01/12/2017       kshen       Grid replacement. 1. Correct the codes abut getting select IND value in the function updateAssignee.
                              2. Skip the processPlusMinusImages function in userReadyStateReady. Similar function will be done in handleGetCustomPageOptions
                              3. Changed groupExpandCollapse to not update update_ind for changing group flag. It may be
                              done in handleGetCustomPageOptions in the future.
                              4. Changed to use both form code "GROUP" and "GRP_APP" to check if an application is group application.
                              5. Added the function handleGetCustomPageOptions for jqxGrid to handle group image.
 -----------------------------------------------------------------------------
 (C) 2010 Delphi Technology, inc. (dti)
 */
var rowId = 0;
var curPageNo = 1;
var filtering = false;
function handleOnButtonClick(action) {
    switch (action) {
        case 'FILTER':
            loadAllApplication();
            break;
        case 'REFRESH':
            loadAllApplication();
            break;
        case 'CLEAR':
            var url = getAppPath() + "/policymgr/applicationmgr/manageApplication.do";
            setWindowLocation(url);
            showProcessingDivPopup();
            break;
        case 'REASSIGN':
            var selectedRows = applicationListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
            if (selectedRows.length <= 0) {
                alert(getMessage("pm.applicationManagement.noSelection.error"));
            }
            else {
                // Open select underwriter popup page
                var url = getAppPath() + "/policymgr/applicationmgr/selectAppReviewer.do";
                openDivPopup("", url, true, true, "", "", 620, 520, "", "", "", false);
            }
            break;
        case 'HISTORY':
            // Open select underwriter popup page
            var rowId = applicationListGrid1.recordset("id").value;
            var url = getAppPath() + "/policymgr/applicationmgr/viewHistory.do?webformWorkItemId=" + rowId;
            openDivPopup("", url, true, true, "", "", 800, 600, "", "", "", false);
            break;
        case 'SAVE':
            commonOnSubmit('SAVE');
            break;
        case 'REWIP':
            if (isGridDataChanged("applicationListGrid")) {
                alert(getMessage("pm.applicationManagement.unsavedData.error"));
            }
            else {
                var rowid = applicationListGrid1.recordset("ID").value;
                first(applicationListGrid1);
                while (!applicationListGrid1.recordset.eof) {
                    if (applicationListGrid1.recordset("CSELECT_IND") == -1) {
                        // Set change type code and comments
                        setInputFormField("changeTypeCode", "REWIP");
                        applicationListGrid1.recordset("CSTATUS").value = "WIP";
                        applicationListGrid1.recordset("UPDATE_IND").value = "Y";
                        // update status of associated parent application to WIP
                        var parentId = applicationListGrid1.recordset("CPARENTWEBFORMWORKITEMID").value;
                        if (!isEmpty(parentId)) {
                            var childNodes = origapplicationListGrid1.documentElement.selectNodes("//ROW[@id='" + parentId + "']");
                            if (childNodes.length > 0) {
                                var currentRecord = childNodes.item(0);
                                currentRecord.selectSingleNode("./CSTATUS").text = "WIP";
                                currentRecord.selectSingleNode("./UPDATE_IND").text = "Y";
                            }
                        }
                    }
                    next(applicationListGrid1);
                }
                first(applicationListGrid1);
                getRow(applicationListGrid1, rowid);

                commonOnSubmit('SAVE');
            }
            break;
        case 'REMINDER':
            if (isGridDataChanged("applicationListGrid")) {
                alert(getMessage("pm.applicationManagement.unsavedData.error"));
            }
            else {
                // Set change type code and comments
                setInputFormField("changeTypeCode", "SDREMINDER");
                applicationListGrid1.recordset("CNOTIFYSTATUS").value = "REQUEST_ADDTL_NOTIFY";
                applicationListGrid1.recordset("UPDATE_IND").value = "Y";
                commonOnSubmit('SAVE');
            }
            break;
        case 'VIEW':
            var status = applicationListGrid1.recordset("CSTATUS").value;
            if (status == "SUBMITTED") {
                var rowId = applicationListGrid1.recordset("id").value;
                var url = getAppPath() +
                        "/policymgr/applicationmgr/manageApplication.do?process=getApplicationUrl&workItemId=" + rowId + "&date=" + new Date();
                new AJAXRequest("get", url, '', viewApplication, false);
            }
            break;
        default:break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'FILTER':
            document.forms[0].process.value = "loadAllApplication";
            showProcessingDivPopup();
            break;
        case 'CLEAR':
            clearThisFormFields(document.forms[0], true);
            document.forms[0].process.value = "clear";
            showProcessingDivPopup();
            break;
        case 'SAVE':
            document.forms[0].process.value = "saveAllApplication";
            showProcessingDivPopup();
            break;
        default : proceed = false;
    }
    return proceed;
}

function clearThisFormFields(theForm, clearHidden) {
    if (window.clearThisFormFields.arguments.length < 2)
        clearHidden = false;
    /* Clear all fields. */
    var coll = theForm.elements;
    var i = 0;
    var list = '';
    for (i = 0; i < coll.length; i++) {
        var obj = coll[i];
        list += 'type for obj ' + i + ' = ' + obj.type + ';  ';
        switch (coll[i].type) {
            case 'select-multiple':
                coll[i].value = '';
                var x = coll[i].options.length;
                if (coll[i].options[0].value == "") {
                    coll[i].options[0].selected = true;
                }
                else {
                    coll[i].options[0].selected = false;
                }
                for (var y = 1; y < x; y++) {
                    coll[i].options[y].selected = false;
                }

                break;
            case 'text':
            case 'textarea':
            case 'select-multiple':
            case 'file':
                coll[i].value = '';
                break;
            case 'select-one':
                if (coll[i].options[0].value == "") {
                    coll[i].options[0].selected = true;
                }
                else {
                    coll[i].value = '-1';
                }
                break;
            case 'checkbox':
            case 'radio':
                coll[i].checked = false;
                break;
            case 'hidden':
                if (clearHidden) coll[i].value = '';
                break;
        }
    }
}

//-----------------------------------------------------------------------------
// Disable this method which creats unexpected JS error when there's no web form attached to the grid
//-----------------------------------------------------------------------------
function commonEnableDisableGridDetailFields() {
}

//-----------------------------------------------------------------------------
// Handle select/deselect all button
//-----------------------------------------------------------------------------
function applicationList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

function updateAssignee(assigneeId, assigneeName) {
    var rowid = applicationListGrid1.recordset("ID").value;
    var count = 0;
    first(applicationListGrid1);
    while (!applicationListGrid1.recordset.eof) {
        if (applicationListGrid1.recordset("CSELECT_IND").value == -1) {
            if (applicationListGrid1.recordset("CREVIEWERID").value != assigneeId) {
                count ++;
                applicationListGrid1.recordset("CREVIEWERID").value = assigneeId;
                applicationListGrid1.recordset("CREVIEWERIDLOVLABEL").value = assigneeName;
                applicationListGrid1.recordset("UPDATE_IND").value = "Y";

                var curId = applicationListGrid1.recordset("id").value;
                // Find all child rows in original grid data
                var childNodes = origapplicationListGrid1.documentElement.selectNodes("//ROW[(CPARENTWEBFORMWORKITEMID = '" + curId + "')]");
                for (var i = 0; i < childNodes.length; i++) {
                    var currentRecord = childNodes.item(i);
                    currentRecord.selectSingleNode("./CREVIEWERID").text = assigneeId;
                    currentRecord.selectSingleNode("./CREVIEWERIDLOVLABEL").text = assigneeName;
                    currentRecord.selectSingleNode("./UPDATE_IND").text = "Y";
                }
            }
        }
        next(applicationListGrid1);
    }
    first(applicationListGrid1);
    getRow(applicationListGrid1, rowid);
    // Set change type code and comments
    //alert(count);
    if (count == 0) {
        alert(getMessage("pm.applicationManagement.reassign.warning"));
    }
    else {
        setInputFormField("changeTypeCode", "REASSIGN");
    }
}

function getChanges(XMLData) {
    return getChangesOnly(XMLData);
}

function viewDocument() {
    var decodedFileFullPath = applicationListGrid1.recordset("CATTACHMENT").value;
    if (decodedFileFullPath.length < 2 || // too short to be a file identifier
            decodedFileFullPath.lastIndexOf("/") + 1 == decodedFileFullPath.length) {
        // it ends with /, so it is not a complete file identifier
        return;
    }
    var url = getAppPath() +
            "/policymgr/applicationmgr/manageApplication.do?process=viewDocument&decodedFileFullPath=" + decodedFileFullPath;
    window.open(url, "", "width=1200,height=800,innerHeight=770,innerWidth=1170,scrollbars,location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
}

function viewApplication(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var url = oValueList[0]["URL"];
                window.open(url, "", "width=1200,height=800,innerHeight=770,innerWidth=1170,scrollbars,location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To display image in the grid
//-----------------------------------------------------------------------------
function userReadyStateReady(tbl) {
    if (isEmptyRecordset(applicationListGrid1.recordset)) {
        pageEntitlements(true, "applicationListGrid");
        // According to the error message, we should not process grouping image.
        return;
    }

    if (!dti.oasis.page.useJqxGrid()) {
        // jqxGrid will use custom page setting to handle displaying grid grouping images.
        processPlusMinusImages("CGROUPEXPANDCOLLAPSE");
    }

    // System should go to the previous page since system goes to the first page after filter.
    if (curPageNo > 1) {
        for (var i = 1; i < curPageNo; i++) {
            baseOnBeforeGotoPage(applicationListGrid, 'N');
            gotopage(applicationListGrid, 'N');
            baseOnAfterGotoPage(applicationListGrid, 'N');
        }
    }
    // Select the row.
    commonReadyStateReady(tbl);
    // Reset the values.
    rowId = 0;
    curPageNo = 1;
    filtering = false;

}

//-----------------------------------------------------------------------------
// Expand or Collapse the application data
//-----------------------------------------------------------------------------
function groupExpandCollapse(pk, curStatus) {
    if (isGridDataChanged("applicationListGrid")) {
        alert(getMessage("pm.applicationManagement.unsavedData.error"));
        return;
    }
    rowId = applicationListGrid1.recordset("ID").value;
    curPageNo = getTableProperty(applicationListGrid, "pageno");
    filtering = true;

    var displayInd = "Y";
    var flag = "-";
    if (curStatus == "-") {
        // Hidden child rows if clicks "-" to collapse group
        displayInd = "N";
        flag = "+";
    }

    // Set flag for parent row
    var parentNode = origapplicationListGrid1.documentElement.selectSingleNode("//ROW[(@id='" + pk + "')]");
    if (parentNode != null) {
        var originalUpdateInd;
        if (dti.oasis.page.useJqxGrid()) {
            originalUpdateInd = parentNode.selectSingleNode("./UPDATE_IND").text;
        }

        parentNode.selectSingleNode("./CGROUPEXPANDCOLLAPSE").text = flag;

        if (dti.oasis.page.useJqxGrid()) {
            // Since jqxGrid would auto-update the update_ind, we need to set the update ind back.
            // We may use custom page options tell jqxGrid that we should skip set update_ind for the column CGROUPEXPANDCOLLAPSE
            parentNode.selectSingleNode("./UPDATE_IND").text = originalUpdateInd;
        }
    }

    // Find all child rows and set display indicator
    var childNodes = origapplicationListGrid1.documentElement.selectNodes("//ROW[(CPARENTWEBFORMWORKITEMID = '" + pk + "')]");
    for (var i = 0; i < childNodes.length; i++) {
        var currentRecord = childNodes.item(i);
        currentRecord.selectSingleNode("./DISPLAY_IND").text = displayInd;
    }
    setTableProperty(applicationListGrid, "selectedRowId", '0');
    applicationListGrid_filter("");
}

//-----------------------------------------------------------------------------
// To handle on-click event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    var formTypeCode = applicationListGrid1.recordset("CFORMTYPECODE").value;
    var rowId = applicationListGrid1.recordset("id").value;
    var selectInd = applicationListGrid1.recordset("CSELECT_IND").value;
    // Sync select indicator to original grid data for current row
    var parentNode = origapplicationListGrid1.documentElement.selectSingleNode("//ROW[(@id='" + rowId + "')]");
    if (parentNode != null) {
        parentNode.selectSingleNode("./CSELECT_IND").text = selectInd;
    }

    // In PM, we use GRP_APP as the form type code of group application.
    // In eAPP, we use GROUP as the form type code of group application.
    // Not sure what's the correct form type code, use both them as group form type code for now.
    if (formTypeCode == "GRP_APP" || formTypeCode == "GROUP") {
        var childNodes = applicationListGrid1.documentElement.selectNodes("//ROW[(CPARENTWEBFORMWORKITEMID = '" + rowId + "')]");
        for (var i = 0; i < childNodes.length; i++) {
            var currentRecord = childNodes.item(i);
            currentRecord.selectSingleNode("./CSELECT_IND").text = selectInd;
        }
        var origChildNodes = origapplicationListGrid1.documentElement.selectNodes("//ROW[(CPARENTWEBFORMWORKITEMID = '" + rowId + "')]");
        for (var i = 0; i < origChildNodes.length; i++) {
            var currentRecord = origChildNodes.item(i);
            currentRecord.selectSingleNode("./CSELECT_IND").text = selectInd;
        }
    }
}

function displayPolicy(pk) {
    var selectedPolicyNo = applicationListGrid1.recordset("CPOLICYNO").value;
    var policyURL = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + selectedPolicyNo + "&policyTermHistoryId=" + pk;
    window.open(policyURL, "newWindow", "");
}

function loadAllApplication() {
    var url = getAppPath() + "/policymgr/applicationmgr/manageApplication.do?"
            + commonGetMenuQueryString() + "&process=loadAllApplication";
    if (hasObject("ext1Criteria")) {
        url = url + "&ext1Criteria=" + trim(getObjectValue("ext1Criteria"));
    }
    if (hasObject("ext2Criteria")) {
        url = url + "&ext2Criteria=" + trim(getObjectValue("ext2Criteria"));
    }
    if (hasObject("ext3Criteria")) {
        url = url + "&ext3Criteria=" + trim(getObjectValue("ext3Criteria"));
    }
    if (hasObject("assigneeCriteria")) {
        url = url + "&assigneeCriteria=" + getObjectValue("assigneeCriteria");
    }
    if (hasObject("preparerIdCriteria")) {
        url = url + "&preparerIdCriteria=" + trim(getObjectValue("preparerIdCriteria"));
    }
    if (hasObject("issueCompanyCriteria")) {
        url = url + "&issueCompanyCriteria=" + trim(getObjectValue("issueCompanyCriteria"));
    }
    if (hasObject("firstNameCriteria")) {
        url = url + "&firstNameCriteria=" + trim(getObjectValue("firstNameCriteria"));
    }
    if (hasObject("policyTypeCriteria")) {
        url = url + "&policyTypeCriteria=" + trim(getObjectValue("policyTypeCriteria"));
    }
    if (hasObject("applicantEmailCriteria")) {
        url = url + "&applicantEmailCriteria=" + trim(getObjectValue("applicantEmailCriteria"));
    }
    if (hasObject("statusCriteria")) {
        url = url + "&statusCriteria=" + getObjectValue("statusCriteria");
    }
    if (hasObject("statusDateCriteria")) {
        url = url + "&statusDateCriteria=" + trim(getObjectValue("statusDateCriteria"));
    }
    if (hasObject("policyNoCriteria")) {
        url = url + "&policyNoCriteria=" + trim(getObjectValue("policyNoCriteria"));
    }
    setWindowLocation(url);
    showProcessingDivPopup();
}

function handleGetCustomPageOptions() {
    // Handle grouping image for jqxGrid.
    return dti.oasis.page.newCustomPageOptions().addGroupingSetting("applicationListGrid", "CGROUPEXPANDCOLLAPSE");
}
