/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 10/21/2011       clm         issue 122654 not open policy agent output option page if agent id is empty
 02/01/2013       skommi      Issue #111565 Added History Button.
 10/25/2013       jshen       Issue 148195 - Don't pass the from agent name when changing agent info.
 07/17/2014       kmv         Issue 155923 - Revert previous fix for issue 148195 and add encodeURIComponent
                              to pass the agent name
 11/25/2014       kxiang      Issue 158853 -
                              1. Modified handleOnGetInitialValuesForAgentLicense to set value for entityId.
                              2. Modified handleOnGetInitialValuesForAddAgent to add href parameter.
                              3. Added handlePostAddRow to set href to grid agent.
 05/27/2014      iwang        Issue 157937 - Added function refreshPage().
 07/11/2016      kmv          Issue 176948 - Override commonEnableDisableGridDetailFields in Edit.js to add pageEntitlements
 06/05/2016      cesar        #184074 - Modified changeAgent() to retrieve the producer agent license id from agentListGrid1.
 12/01/2017      htwang       Issue 189990 - replace the enableDisableField() method with the page entitlement to
                              enable the producerAgentLicId LOV field.
 08/07/2018      kmv          Issue 192987 - Passs policy type and state code into Change Agent popup
 */
var areRatesReadOnly = false;

function handleOnChange(field) {
    var fieldName = field.name;
    switch (fieldName) {
        case "producerAgentLicId":
        case "subproducerAgentLicId":
        case "countersignerAgentLicId":
        case "authorizedrepAgentLicId":
            handleOnChangeForAgentLicenseId(field);
            break;
        case "ereCommBasis":
        case "renewalCommBasis":
        case "newbusCommBasis":
            resetRateFields(field);
            enableDisableRateFields(field);
            break;
    }
}

function enableDisableAllRateFields() {
    if (areRatesReadOnly) {
        disableAllRateFields(); // nerver enable them if it is not configured
    } else {
        enableDisableRateFields(getObject("renewalCommBasis"));
        enableDisableRateFields(getObject("ereCommBasis"));
        enableDisableRateFields(getObject("newbusCommBasis"));
    }
}

function resetRateFields(field) {
    var fieldName = field.name;
    var fieldValue = field.value;
    var fieldPrefix;

    // get the fieldPrefix based on the field passed in
    // since fields with same prefix share the same logic
    switch (fieldName) {
        case "ereCommBasis":
            fieldPrefix = "ere";
            break;
        case "renewalCommBasis":
            fieldPrefix = "renewal";
            break;
        case "newbusCommBasis":
            fieldPrefix = "newbus";
            break;
    }

    // if the rate fields are enabled globally. then we conditionally enable them.
    if (!areRatesReadOnly) {
        // Show specific fields based on the value selected
        // accordingly now per UC
        switch (fieldValue) {
            case "PERCENT":
                getObject(fieldPrefix + "CommFlatAmount").value = "";
                getObject(fieldPrefix + "CommRateScheduleId").value = "";
                getObject(fieldPrefix + "CommLimit").value = "";
                break;
            case "FLAT":
                getObject(fieldPrefix + "CommRate").value = "";
                getObject(fieldPrefix + "CommRateScheduleId").value = "";
                getObject(fieldPrefix + "CommLimit").value = "";
                break;
            case "SCHED":
                getObject(fieldPrefix + "CommRate").value = "";
                getObject(fieldPrefix + "CommFlatAmount").value = "";
                getObject(fieldPrefix + "CommLimit").value = "";
                break;
        }
    }

}

function enableDisableRateFields(field) {
    var fieldName = field.name;
    var fieldValue = field.value;
    var fieldPrefix;

    // get the fieldPrefix based on the field passed in
    // since fields with same prefix share the same logic
    switch (fieldName) {
        case "ereCommBasis":
            fieldPrefix = "ere";
            break;
        case "renewalCommBasis":
            fieldPrefix = "renewal";
            break;
        case "newbusCommBasis":
            fieldPrefix = "newbus";
            break;
    }

    // disable all rate fields with fieldNames start with {prefix}
    disableRateFieldsWithPrefix(fieldPrefix);

    // if the rate fields are enabled globally. then we conditionally enable them.
    if (!areRatesReadOnly) {
        // Show specific fields based on the value selected
        // accordingly now per UC
        switch (fieldValue) {
            case "PERCENT":
                enableDisableField(getObject(fieldPrefix + "CommRate"), false);
                enableRateField(fieldPrefix + "CommRate");
                enableRateField(fieldPrefix + "CommLimit");
                enableRateField(fieldPrefix + "CommPayCode");
                break;
            case "FLAT":
                enableRateField(fieldPrefix + "CommFlatAmount");
                enableRateField(fieldPrefix + "CommPayCode");
                break;
            case "SCHED":
                enableRateField(fieldPrefix + "CommRateScheduleId");
                enableRateField(fieldPrefix + "CommLimit");
                enableRateField(fieldPrefix + "CommPayCode");
                break;
        }
    }
}

function disableRateFieldsWithPrefix(fieldPrefix) {
    enableDisableField(getObject(fieldPrefix + "CommRate"), true);
    enableDisableField(getObject(fieldPrefix + "CommLimit"), true);
    enableDisableField(getObject(fieldPrefix + "CommFlatAmount"), true);
    enableDisableField(getObject(fieldPrefix + "CommPayCode"), true);
    enableDisableField(getObject(fieldPrefix + "CommRateScheduleId"), true);
}

function enableRateField(fieldName) {
    enableDisableField(getObject(fieldName), false);
}

function handleOnChangeForAgentLicenseId(field) {

    var fieldName = field.name;
    var agentLicenseId = field.value;
    var licenseClassCode;

    // get the licenseClassCode based on the field name
    switch (fieldName) {
        case "producerAgentLicId":
            licenseClassCode = "PRODUCER";
            break;
        case "subproducerAgentLicId":
            licenseClassCode = "SUB_PROD";
            break;
        case "countersignerAgentLicId":
            licenseClassCode = "COUNT_SIGN";
            break;
        case "authorizedrepAgentLicId":
            licenseClassCode = "AUTH_REP";
            break;
    }

    if (agentLicenseId == "") {
        if (licenseClassCode == "PRODUCER") {
            disableNonRateFields();
        }
        return;
    }

    // form url to make ajax call
    var path = getAppPath() + "/agentmgr/maintainPolicyAgent.do?"
        + "process=getInitialValuesForAgent"
        + "&" + commonGetMenuQueryString("PM_MTN_AGENT", "")
        + "&agentLicenseId=" + agentLicenseId
        + "&licenseClassCode=" + licenseClassCode;

    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAgentLicense, false);
}

function handleOnGetInitialValuesForAgentLicense(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }

            agentListGrid1.recordset("EDIT_IND").value = 'Y';

            getObject("subProducerIdCounterForSave").value = getObject('subproducerAgentLicId').options.length;

            if(oValueList[0]['entityId'] != null) {
                agentListGrid1.recordset("CENTITYID").value = oValueList[0]['entityId'];
            }

            // enable all non-rate related form fields
            enableNonRateFields();
            // adjust all rate-related fields based on the values from the corresponding CommBasis fields
            enableDisableAllRateFields();
            // enable disable add button
            enableDisableActionItemAdd();
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case "ADD":
            agentListGrid_selectRow(lastInsertedId);
            break;
        case "DELETE":
            enableDisableActionItemAdd();
            break;
        case "VIEWSCHEDULE":
            var viewScheduleUrl = getCISPath() + "/commissionmgr/lookupCommission.do";
            var divPopupId = openDivPopup("", viewScheduleUrl, true, true, "", "", "500", "400", "", "", "", false);
            break;
        case "SAVE":
            validatePolicyAgent();
            break;
        case "CHANGEAGENT":
            changeAgent();
            break;
        case 'OUTPUT':
            maintainOutputOption();
            break;
        case 'HISTORY':
            viewAgentHistory();
            break;
    }
}

function viewAgentHistory() {
    var policyId = getObjectValue("policyId");
    var agentHistUrl = getAppPath() + "/agentmgr/viewAgentHistory.do?policyId="+ policyId
        +"&process=loadAllAgentHist";
    var divPopupId = openDivPopup("", agentHistUrl, true, true,"","", 1115,650,"","","",false);
}

function agentListGrid_setInitialValues() {
    var path = getAppPath() + "/agentmgr/maintainPolicyAgent.do?"
        + "process=getInitialValuesForAddAgent"
        + "&" + commonGetMenuQueryString("PM_MTN_AGENT", "");
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgent, false);
}

function handleOnGetInitialValuesForAddAgent(ajax) {
    commonHandleOnGetInitialValues(ajax, "producerAgentNameHref");
}

function agentListGrid_selectRow(rowId) {
    var producerAgentLicId = getObjectValue("producerAgentLicId");

    if (rowId < 0 && producerAgentLicId != "") {
        enableNonRateFields();
        enableDisableAllRateFields();
    }
    else {
        if (rowId > 0) {
            disableAllRateFields();
        }
        disableNonRateFields();
    }
    enableDisableActionItemAdd();
}

// Override and disable executing this method
//-----------------------------------------------------------------------------
function commonEnableDisableGridDetailFields(dataGrid, gridDetailDivId, selectedRowId, stageCode) {
    var detailFormElements = null;
    var currEditInd = dataGrid.recordset('EDIT_IND').value.toUpperCase();
    var isDisabled = (currEditInd == 'N');
    if (isDisabled && stageCode == 'POST') {
        // The row is non-editable. Get all enabled fields associated with the grid and make them read-only them.
        // Using JQuery, find all "editable" input fields (irrespective of input, select etc tags) which has
        // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.
        detailFormElements = $("td[isEditable='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
    } else if(!isDisabled && stageCode == 'PRE') {
        // The row is an editable. Get all readonly fields associated with the grid and make them editable.
        // Using JQuery, find all "non-editable" input fields (irrespective of input, select etc tags) which has
        // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.

        //Look for all hidden fields that are visible & editable when the page was loaded initially and make them editable.
        detailFormElements = $("td[isEditableWhenVisible][isEditableOnInitialPageLoad='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
        for (var i=0; i<detailFormElements.length; i++) {
            /*
             Radio Option and Checkbox HTML elements are rendered within a table of its own.
             So, locate the correct <TD> that holds the HTML elements to set "isEditableWhenVisible" attribute value to "Y".
             */
            var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
            for (var j = 0; j < suffixArray.length; j++) {
                var containerId = detailFormElements[i].name + suffixArray[j];
                if (hasObject(containerId)) {
                    var container = $("#" + containerId);
                    container.attr("isEditableWhenVisible", "Y");
                }
            }
            //Show the field as editable.
            hideShowField(detailFormElements[i], false);
        }

        //Look for all read-only fields and make them editable.
        detailFormElements = $("td[isEditable='N'][isEditableOnInitialPageLoad='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
    }
    if (detailFormElements) {
        for (var i = 0; i < detailFormElements.length; i++) {
            enableDisableField(detailFormElements[i], isDisabled);
        }
    }

    functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(true, "agentListGrid");
    }
}

// enable all non-rate related form fields
function enableNonRateFields() {
    var producerAgentLicId = getObjectValue("producerAgentLicId");
    if (producerAgentLicId != "") {
        agentListGrid1.recordset("CISPRODUCERAGENTLICIDAVAILABLE").value = "Y";
        enableDisableField(getObject("subproducerAgentLicId"), false);
        enableDisableField(getObject("countersignerAgentLicId"), false);
        enableDisableField(getObject("authorizedrepAgentLicId"), false);

        enableDisableField(getObject("effectiveFromDate"), false);
        enableDisableField(getObject("effectiveToDate"), false);

        if (!areRatesReadOnly) {
            // if the system globally configured to enable all rate related fields. then
            // we continue..
            enableDisableField(getObject("ereCommBasis"), false);
            enableDisableField(getObject("renewalCommBasis"), false);
            enableDisableField(getObject("newbusCommBasis"), false);
        } else {
            enableDisableField(getObject("ereCommBasis"), true);
            enableDisableField(getObject("renewalCommBasis"), true);
            enableDisableField(getObject("newbusCommBasis"), true);
        }

    }
}
// disable all non-rate related form fields
function disableNonRateFields() {
    var producerAgentLicId = getObjectValue("producerAgentLicId");
    if (producerAgentLicId == "") {
        enableDisableField(getObject("subproducerAgentLicId"), true);
        enableDisableField(getObject("countersignerAgentLicId"), true);
        enableDisableField(getObject("authorizedrepAgentLicId"), true);
        enableDisableField(getObject("ereCommBasis"), true);
        enableDisableField(getObject("renewalCommBasis"), true);
        enableDisableField(getObject("newbusCommBasis"), true);
        enableDisableField(getObject("effectiveFromDate"), true);
        enableDisableField(getObject("effectiveToDate"), true);
        disableAllRateFields();
    }
}

function disableAllRateFields() {
    disableRateFieldsWithPrefix("ere");
    disableRateFieldsWithPrefix("renewal");
    disableRateFieldsWithPrefix("newbus");
}

function hasInsertedRecord() {
    var inserted = false;
    var selectedGridId = getCurrentlySelectedGridId();
    var xmlData = getXMLDataForGridName(selectedGridId);
    var origXmlData = getOrigXMLData(xmlData);
    var changedNodes = xmlData.documentElement.selectNodes("//ROW[UPDATE_IND = 'I']");
    var changedOrigNodes = origXmlData.documentElement.selectNodes("//ROW[UPDATE_IND = 'I']");
    if (changedNodes.length > 0 || changedOrigNodes.length > 0)
        inserted = true;

    return inserted;
}
// to only allow one new record to be inserted before save.. per discussion with PM
function enableDisableActionItemAdd() {
    if (hasInsertedRecord()) {
        enableDisableField(getObject('PM_AGNT_ADD'), true);
    }
    else {
        enableDisableField(getObject('PM_AGNT_ADD'), false);
    }
}

function handleOnLoad(){
    if (getSysParmValue("areRateFieldsReadOnly") == "Y") {
        areRatesReadOnly = true;
    }
}

function changeAgent() {
    var entityId = agentListGrid1.recordset("CENTITYID").value;
    var agentLicenseId = agentListGrid1.recordset("CPRODUCERAGENTLICID").value;

    var fmUrl = getTopNavApplicationUrl("FM") + "/agentmgr/changeAgentRateInfo.do?process=loadAllAgentLicenseAndPolicies&policyId="
            + getObjectValue("policyId") + "&agentLicenseId=" + agentLicenseId
            + "&policyTypeCode=" + getObjectValue("policyTypeCode")
            + "&stateCode=" + getObjectValue("issueStateCode")
            + "&headerHidden=Y" + '&policyAgentFrameId=' + window.frameElement.id
            + "&fromAgent="+ encodeURIComponent(getObjectValue("producerAgentName"))
            + "&entityId="+ entityId;

    openDivPopup("", fmUrl, true, true, "", "", "1180", "780", "", "750", "", "", true, "", true);
}

function validatePolicyAgent() {
    if (isPageDataChanged()) {
        enableDisableField(getObject("subProducerIdCounterForSave"), false);
        getObject("subProducerIdCounterForSave").value = getObject('subproducerAgentLicId').options.length;

        // update txtXML field
        modValue = getChanges(agentListGrid1);
        document.agentList.txtXML.value = modValue;

        postAjaxSubmit(getAppPath() + "/agentmgr/maintainPolicyAgent.do", "validateAllAgent", false, false, handleOnValidatePolicyAgent);
    }
}

function handleOnValidatePolicyAgent(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // If no errors then proceed with save
            savePolicyAgent();
        }
    }
}

function savePolicyAgent() {
    commonOnSubmit('saveAllAgent', false, false, false, true);
}

function maintainOutputOption() {
    var entityId = agentListGrid1.recordset("CENTITYID").value;
    var agentId = agentListGrid1.recordset("CAGENTID").value;
    var policyId = getObjectValue("policyId");
    if (!isEmpty(agentId)) {
        var url = getTopNavApplicationUrl("CIS") + "/agentmgr/maintainPolicyAgentOutputOptions.do?policyId="
            + policyId + "&entityId=" + entityId + "&agentId=" + agentId;
        openDivPopup("", url, true, true, "", "", "1050", "800", "", "", "", true, true, "", true);
    }
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "agentListGrid") {
        var xmlData = getXMLDataForGridName("agentListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var agentNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CPRODUCERAGENTNAME") {
                agentNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + agentNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("producerAgentNameHref"))) {
                    href = "javascript:handleOnGridHref('agentListGrid', '"
                            + getObjectValue("producerAgentNameHref") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}

// refresh the page with policyViewMode WIP
function refreshPage() {
    var url = location.href;
    // Strip of information after the "?"
    if (url.indexOf('?') > -1) {
        url = url.substring(0, url.indexOf('?'));
    }

    url = buildMenuQueryString("", url);

    setWindowLocation(url);
}

