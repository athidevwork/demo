/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 12/16/2011       jshen       Issue 127905: remove duplicate form validation.
 12/16/2011       jshen       Issue 128482: cannot add agent contract commission record which is caused by issue 122044.
 12/19/2011       jshen       Issue 127905: rollback last changes for issue 127905.
 12/02/2013       Elvin       Issue 149420: default start/end date with agent start/end date if no default value in eAdmin
 09/01/2014       wkong       Issue 156561: Set button disabled when page is read only.
 12/22/2014       htwang      Issue 159746: remove the duplicate contractListGrid_selectRow(), merge their codes together.
 03/25/2015       iwang       Issue 160869: 1) Modified function submitSave() to do the common validation on Agent Contract
                                            Commission grid before Agent Contract grid.
                                            2) Modified function filterContractCommissionData() to remove fireAjax calls.
                                            3) Modified function handleOnChange() to reset policy type value when state
                                            code gets changed.
 05/17/2016       iwang       Issue 168103: Modified function handleOnSelectContract() to use setObjectValue() and
                                            fire onChange Event.
 07/15/2016       iwang       Issue 177546: Enhance CIS Agent to optionally capture details of Agents working for an Agency.
 09/25/2018       dzhang      Issue 195835: Grid replacement
 10/22/2018       jdingle     Issue 160238: handle multiple deletes.
 */
var AGENT_PAY_COMMISSION_LIST_GRID_ID = "payListGrid";
var AGENT_CONTRACT_LIST_GRID_ID = "contractListGrid";
var AGENT_CONTRACT_COMMISSION_LIST_GRID_ID = "commissionListGrid";
var AGENT_STAFF_LIST_GRID_ID = "agentStaffListGrid";
var AGENT_OVERRIDE_LIST_GRID_ID = "agentStaffOverrideListGrid";
var hasErrorMessages = "";
var DEFAULT_DATE = "01/01/3000";

var originalStatus = "";
var isChanged = false;
//-----------------------------------------------------------------------------
// page on load
//-----------------------------------------------------------------------------
function handleOnLoad() {
    originalStatus = getObjectValue("status");

    $.when(dti.oasis.grid.getLoadingPromise(AGENT_CONTRACT_LIST_GRID_ID)).then(function () {
        enableAddButtonForChidGrid(AGENT_CONTRACT_LIST_GRID_ID, 'CI_AGENT_COMM_ADD');
    });
    $.when(dti.oasis.grid.getLoadingPromise(AGENT_STAFF_LIST_GRID_ID)).then(function () {
        enableAddButtonForChidGrid(AGENT_STAFF_LIST_GRID_ID, 'CI_AGENT_STAFF_OVRD_ADD');
    });
}
//-----------------------------------------------------------------------------
// click on button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            submitSave();
            break;
        case 'subProducer':
            var url = getAppPath() + "/agentmgr/maintainSubProducer.do?agentLicenseId=" + contractListGrid1.recordset("ID").value;
            openDivPopup("", url, true, true, "", "", 800, 500, "", "", "", true);
            break;
        case 'OUTPUT':
            maintainOutputOption();
            break;
    }
}
//-----------------------------------------------------------------------------
// handle on field change
//-----------------------------------------------------------------------------
function handleOnChange(field){
    isChanged = true;
    if (field.name == "status") {
        if (getObjectValue(field) == "INACTIVE") {
            if (!confirm(getMessage("ci.agentmgr.maintainAgent.modifyAgentStatus"))) {
                setObjectValue(field, originalStatus);
            }
        }
    } else if (field.name == "effectiveEndDate") {
        if (getObjectValue(field) == "") {
            setObjectValue(field, DEFAULT_DATE);
        }
        //set end date on pay commission list
        setEffectiveDate(payListGrid1, null, "CEFFECTIVEENDDATE1");
        //set end date on pay commission list
        setEffectiveDate(contractListGrid1, null, "CEFFECTIVEENDDATE2");
        //set end date on pay commission list
        setEffectiveDate(commissionListGrid1, null, "CEFFECTIVEENDDATE3");
    } else if (field.name == "newbusCommBasis") {
        if (getObjectValue(field) != "SCHED") {
            commissionListGrid1.recordset("CNEWBUSCOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CNEWBUSCOMMRATESCHEDIDLOVLABEL").value = "";
        }
        commissionListGrid1.recordset("CNEWBUSRATE").value = "";
        commissionListGrid1.recordset("CNEWBUSCOMMLIMIT").value = "";
        commissionListGrid1.recordset("CNEWBUSFLATAMT").value = "";
    } else if (field.name == "renewalCommBasis") {
        if (getObjectValue(field) != "SCHED") {
            commissionListGrid1.recordset("CRENEWALCOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CRENEWALCOMMRATESCHEDIDLOVLABEL").value = "";
        }
        commissionListGrid1.recordset("CRENEWALRATE").value = "";
        commissionListGrid1.recordset("CRENEWALCOMMLIMIT").value = "";
        commissionListGrid1.recordset("CRENEWALFLATAMT").value = "";
    } else if (field.name == "ereCommBasis") {
        if (getObjectValue(field) != "SCHED") {
            commissionListGrid1.recordset("CERECOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CERECOMMRATESCHEDIDLOVLABEL").value = "";
        }
        commissionListGrid1.recordset("CERERATE").value = "";
        commissionListGrid1.recordset("CERECOMMLIMIT").value = "";
        commissionListGrid1.recordset("CEREFLATAMT").value = "";
    } else if (field.name == "stateCode"){
        //Clear all the Schedule Information
        var contractCommissionXMLData = getXMLDataForGridName(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
        var selectedRowId = getSelectedRow(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
        //Trigger row selected event to load the LOV for contract commission grid
        selectRow(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID, selectedRowId);
        first(contractCommissionXMLData);
        var policyTypeList = getObject('policyTypeCode');
        var length = policyTypeList.length;
        var findFlg = false;
        var policyTypeCode;
        while (!contractCommissionXMLData.recordset.eof) {
            commissionListGrid1.recordset("CNEWBUSCOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CNEWBUSCOMMRATESCHEDIDLOVLABEL").value = "";
            commissionListGrid1.recordset("CRENEWALCOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CRENEWALCOMMRATESCHEDIDLOVLABEL").value = "";
            commissionListGrid1.recordset("CERECOMMRATESCHEDID").value = "";
            commissionListGrid1.recordset("CERECOMMRATESCHEDIDLOVLABEL").value = "";
            commissionListGrid1.recordset("UPDATE_IND").value = "Y";
            policyTypeCode = commissionListGrid1.recordset("CPOLICYTYPECODE").value;
            for(var i = 0; i < length; i++){
                if(policyTypeList.options[i].value == policyTypeCode){
                    findFlg = true;
                    break;
                }
            }
            if (!findFlg) {
                commissionListGrid1.recordset("CPOLICYTYPECODE").value = "";
                commissionListGrid1.recordset("CPOLICYTYPECODELOVLABEL").value = "";
            }
            findFlg = false;
            next(contractCommissionXMLData);
        }
        first(contractCommissionXMLData);
        getRow(contractCommissionXMLData, selectedRowId);
    } else if (field.name == "licenseClassCode"){
        if (getObjectValue(field) == "PRODUCER") {
            setInputFormField("producerAgentLicenseId", "");
            setInputFormField("producerLicenseNumber", "");
        }
    } else if (field.name == "producerLicenseNumber"){
            setInputFormField("producerAgentLicenseId", "");
    } else if (field.name == "agentStaffEntityName"){
        setObjectValue("agentStaffEntityId", "");
    }
}

/**
 * set effect date for payListGrid1,contractListGrid1,commissionListGrid1
 * @param gridId
 * @param effectiveStartDateFieldId
 * @param effectiveEndDateFieldId
 */
function setEffectiveDate(gridId, effectiveStartDateFieldId, effectiveEndDateFieldId) {
    var effectiveStartDate = getObjectValue("effectiveStartDate");
    var effectiveEndDate = getObjectValue("effectiveEndDate");

    if (!isEmptyRecordset(gridId.recordset)) {
        var rowNodes = gridId.documentElement.selectNodes("//ROW");
        var rowNodesLength = rowNodes.length;
        for (var i = 0; i < rowNodesLength; i++) {
            var rowNode = rowNodes.item(i);

            //Set gird row effective start date
            if (!dti.oasis.string.isEmpty(effectiveStartDateFieldId)) {
                var gridEffectiveStartDate = rowNode.selectSingleNode("./" + effectiveStartDateFieldId).text;
                if (gridEffectiveStartDate == null || gridEffectiveStartDate == "") {
                    rowNode.selectSingleNode("./" + effectiveStartDateFieldId).text = effectiveStartDate;
                }
            }

            //Set grid row effective end date
            if (!dti.oasis.string.isEmpty(effectiveEndDateFieldId)) {
                var gridEffectiveEndDate = rowNode.selectSingleNode("./" + effectiveEndDateFieldId).text;
                if (gridEffectiveEndDate == null || gridEffectiveEndDate == "" || gridEffectiveEndDate == DEFAULT_DATE) {
                    rowNode.selectSingleNode("./" + effectiveEndDateFieldId).text = effectiveEndDate;
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// save
//-----------------------------------------------------------------------------
function submitSave() {
    var proceed = true;
    proceed = commonValidateGrid(AGENT_PAY_COMMISSION_LIST_GRID_ID);
    proceed = proceed && commonValidateGrid(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
    proceed = proceed && commonValidateGrid(AGENT_CONTRACT_LIST_GRID_ID);
    proceed = proceed && commonValidateGrid(AGENT_STAFF_LIST_GRID_ID);
    proceed = proceed && commonValidateGrid(AGENT_OVERRIDE_LIST_GRID_ID);
    proceed = proceed && commonValidateForm();
    if (proceed) {
        alternateGrid_update(AGENT_PAY_COMMISSION_LIST_GRID_ID);
        alternateGrid_update(AGENT_CONTRACT_LIST_GRID_ID);
        alternateGrid_update(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
        alternateGrid_update(AGENT_STAFF_LIST_GRID_ID);
        alternateGrid_update(AGENT_OVERRIDE_LIST_GRID_ID);
        setInputFormField("processCode", 'SAVE');
        setObjectValue("process", "saveAllAgent");
        showProcessingDivPopup();
        submitFirstForm();
    }
}

//-----------------------------------------------------------------------------
// click on button for pay commission grid
//-----------------------------------------------------------------------------
function commonOnButtonClickPayGrid(asBtn) {
    currentlySelectedGridId = AGENT_PAY_COMMISSION_LIST_GRID_ID;
    commonOnButtonClick(asBtn);
}
//-----------------------------------------------------------------------------
// click on button for contract grid
//-----------------------------------------------------------------------------
function commonOnButtonClickContractGrid(asBtn) {
    currentlySelectedGridId = AGENT_CONTRACT_LIST_GRID_ID;
    commonOnButtonClick(asBtn);

    enableAddButtonForChidGrid(AGENT_CONTRACT_LIST_GRID_ID, 'CI_AGENT_COMM_ADD');

}
//-----------------------------------------------------------------------------
// click on button for contract commission grid
//-----------------------------------------------------------------------------
function commonOnButtonClickCommissionGrid(asBtn) {
    currentlySelectedGridId = AGENT_CONTRACT_COMMISSION_LIST_GRID_ID;
    switch (asBtn) {
        case 'DELETE':
        case 'ADD':
            commonOnButtonClick(asBtn);

            break;
        case "VIEWSCHEDULE":
            var viewScheduleUrl = getAppPath() + "/commissionmgr/lookupCommission.do";
            var divPopupId = openDivPopup("", viewScheduleUrl, true, true, "", "", "", "", "", "", "", true);
            break;
    }
}

//-----------------------------------------------------------------------------
// set initial values for pay commission list
//-----------------------------------------------------------------------------
function payListGrid_setInitialValues() {
    var path = getAppPath() + "/ciAgent.do?"
        + "process=getInitialValuesForAddAgent";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgentPayCommission, false);
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for pay commission list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAgentPayCommission(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(AGENT_PAY_COMMISSION_LIST_GRID_ID) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }

            //set start/end date if they have no default values
            setEffectiveDate(payListGrid1, "CEFFECTIVESTARTDATE1", "CEFFECTIVEENDDATE1");
        }
    }
}

//-----------------------------------------------------------------------------
// set initial values for contract list
//-----------------------------------------------------------------------------
function contractListGrid_setInitialValues() {
    var path = getAppPath() + "/ciAgent.do?"
        + "process=getInitialValuesForAddAgent";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgentContract, false);
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for contract list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAgentContract(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(AGENT_CONTRACT_LIST_GRID_ID) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }

            //set start/end date if they have no default values
            setEffectiveDate(contractListGrid1, "CEFFECTIVESTARTDATE2", "CEFFECTIVEENDDATE2");
        }
    }
}

//-----------------------------------------------------------------------------
// Filter contract commission data by contract id
//-----------------------------------------------------------------------------
function contractListGrid_selectRow(rowId) {
    //filter contract commission data
    $.when(dti.oasis.grid.getLoadingPromise(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID)).then(function () {
        filterContractCommissionData(rowId);
    });

    if (isEntityReadOnlyYN == "Y") {
        doSecurity();
    }
    getRow(contractListGrid1, rowId);
}

//-----------------------------------------------------------------------------
// delete dependent row in contract commission list
//-----------------------------------------------------------------------------
function contractListGrid_deleteDependentRow () {
    if (!getTableProperty(getTableForGrid(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID), "hasrows"))
        return;
    var agentLincenseId = getXMLDataForGridName(AGENT_CONTRACT_LIST_GRID_ID).recordset("ID").value;
    var contractCommissionXMLData = getXMLDataForGridName(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
    first(contractCommissionXMLData);
    beginDeleteMultipleRow(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
    while (!contractCommissionXMLData.recordset.eof) {
        if (contractCommissionXMLData.recordset("CAGENTLICENSEID").value == agentLincenseId) {
            setSelectedRow(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID, contractCommissionXMLData.recordset("ID").value);
            commissionListGrid_deleterow();
            first(contractCommissionXMLData);
            continue;
        }
        next(contractCommissionXMLData);
    }
    endDeleteMultipleRow(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
    first(contractCommissionXMLData);
    hideShowForm(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
}


//-----------------------------------------------------------------------------
// set initial values for contract commission list
//-----------------------------------------------------------------------------
function commissionListGrid_setInitialValues() {
    var licenseId = getXMLDataForGridName(AGENT_CONTRACT_LIST_GRID_ID).recordset("ID").value;
    getXMLDataForGridName(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID).recordset("CAGENTLICENSEID").value = licenseId;
    var path = getAppPath() + "/ciAgent.do?"
        + "process=getInitialValuesForAddAgent";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgentContractCommission, false);
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for contract commission list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAgentContractCommission(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }

            //set start/end date if they have no default values
            setEffectiveDate(commissionListGrid1, "CEFFECTIVESTARTDATE3", "CEFFECTIVEENDDATE3");
        }
    }
}

//-----------------------------------------------------------------------------
// Filter contract commission data by contract id
//-----------------------------------------------------------------------------
function filterContractCommissionData(contractId) {
    var commissionTable = getTableForXMLData(getXMLDataForGridName(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID));
    setTableProperty(commissionTable, "selectedTableRowNo", null);
    commissionListGrid_filter("CAGENTLICENSEID=" + contractId);
    if (!isEmptyRecordset(commissionListGrid1.recordset)) {
        showNonEmptyTable(commissionListGrid);
        hideShowForm(AGENT_CONTRACT_COMMISSION_LIST_GRID_ID);
    }
}
//-----------------------------------------------------------------------------
// click on finder type
//-----------------------------------------------------------------------------
function find(fieldname) {
    if (fieldname == "producerLicenseNumber") {
        var selectContractUrl = getAppPath() + "/agentmgr/selectAgentContract.do";
        var licenseNumber = getObjectValue("producerLicenseNumber");
        if (licenseNumber != "") {
            selectContractUrl += "?passLicenseNumber=" + licenseNumber;
        }
        var divPopupId = openDivPopup("", selectContractUrl, true, true, "", "", "", "", "", "", "", true);
    } else if (fieldname == "agentStaffEntityName") {
        openEntitySelectWinFullName("agentStaffEntityId", "agentStaffEntityName");
    }
}

//-----------------------------------------------------------------------------
// handle on select contract
//-----------------------------------------------------------------------------
function handleOnSelectContract(action, licenseId, licenseNumber) {
    switch (action.toUpperCase()) {
        case "SELECT":
            // set in the selected contract in caller's form
            setObjectValue("producerAgentLicenseId", licenseId, true);
            setObjectValue("producerLicenseNumber", licenseNumber);
            break;
        case "CANCEL":
            if (isEmpty(getObjectValue("producerAgentLicenseId"))) {
                setInputFormField("producerLicenseNumber", licenseNumber);
            }
            break;
    }

}

//-----------------------------------------------------------------------------
// Set the display status of Add Button. If parent grid no rows, add button on
// child grid menu bar should be disable. This function refactor from enableAgentOverrideAddButton
// and enableAgentCommissionAddButton.
//-----------------------------------------------------------------------------
function enableAddButtonForChidGrid(parentGridId, buttonFieldId) {
    if (isEntityReadOnlyYN == "Y") {
        enableDisableField(getObject(buttonFieldId), true);
    } else {
        var XMLData = getXMLDataForGridName(parentGridId);
        if (isEmptyRecordset(XMLData.recordset)) {
            enableDisableField(getObject(buttonFieldId), true);
        } else {
            enableDisableField(getObject(buttonFieldId), false);
        }
    }
}

function handleReadyStateReady(table) {
    if (isEntityReadOnlyYN == "Y") {
        enableDisableField(getObject('CI_AGENT_COMM_ADD'), true);
        enableDisableField(getObject('CI_AGENT_STAFF_OVRD_ADD'), true);
    }
}

//-----------------------------------------------------------------------------
// Overwrite getParentGridId and getChildGridId
//-----------------------------------------------------------------------------
function getParentGridId() {
    if (validateFieldId == "AgentPayCommissionId" ||
        validateFieldId == "commissionPayCode" ||
        validateFieldId == "effectiveStartDate1" ||
        validateFieldId == "effectiveEndDate1"
    ) {
        return AGENT_PAY_COMMISSION_LIST_GRID_ID;
    } else if (validateIfAgentOverrideGrid()) {
        return AGENT_STAFF_LIST_GRID_ID;
    } else {
        return AGENT_CONTRACT_LIST_GRID_ID;
    }
}

function getChildGridId() {
    if (validateIfAgentOverrideGrid()) {
        return AGENT_OVERRIDE_LIST_GRID_ID;
    } else {
        return AGENT_CONTRACT_COMMISSION_LIST_GRID_ID;
    }
}

function validateIfAgentOverrideGrid() {
    if (validateFieldId == "overridePolicyTypeCode" ||
        validateFieldId == "overrideStateCode" ||
        validateFieldId == "issueCompanyEntityId" ||
        validateFieldId == "overrideNewbusRate" ||
        validateFieldId == "overrideRenewalRate" ||
        validateFieldId == "overrideEreRate" ||
        validateFieldId == "overrideEffStartDate" ||
        validateFieldId == "overrideEffEndDate"
    )
    return true;
}

function maintainOutputOption() {
    var entityId = getObjectValue("entityId");
    var agentId = getObjectValue("agentId");
    var url = getAppPath() + "/agentmgr/maintainPolicyAgentOutputOptions.do?entityId="
        +entityId + "&agentId="+ agentId;
    openDivPopup("", url, true, true, "", "", "980", "600", "", "", "", true, true, "", true);
}

function payListGrid_selectRow(rowId) {
    if (isEntityReadOnlyYN == "Y") {
        doSecurity();
    }
    getRow(payListGrid1, rowId);
}

function commissionListGrid_selectRow(rowId) {
    if (isEntityReadOnlyYN == "Y") {
        doSecurity();
    }
    getRow(commissionListGrid1, rowId);
}

//-----------------------------------------------------------------------------
// Filter Agent Staff Override data by Agent Staff id
//-----------------------------------------------------------------------------
function agentStaffListGrid_selectRow(rowId) {
    //filter Agent Staff Override data
    $.when(dti.oasis.grid.getLoadingPromise(AGENT_OVERRIDE_LIST_GRID_ID)).then(function () {
        filterAgentStaffOverrideData(rowId);
    });

    if (isEntityReadOnlyYN == "Y") {
        doSecurity();
    }
    getRow(agentStaffListGrid1, rowId);

}

//-----------------------------------------------------------------------------
// Filter Agent Override data by agent id
//-----------------------------------------------------------------------------
function filterAgentStaffOverrideData(agentStaffId) {
    var agentOverrideTable = getTableForXMLData(getXMLDataForGridName(AGENT_OVERRIDE_LIST_GRID_ID));
    setTableProperty(agentOverrideTable, "selectedTableRowNo", null);
    agentStaffOverrideListGrid_filter("CAGENTSTAFFID=" + agentStaffId);
    if (!isEmptyRecordset(agentStaffOverrideListGrid1.recordset)) {
        showNonEmptyTable(agentStaffOverrideListGrid);
        hideShowForm(AGENT_OVERRIDE_LIST_GRID_ID);
    }
}

function agentStaffOverrideListGrid_selectRow(rowId) {
    if (isEntityReadOnlyYN == "Y") {
        doSecurity();
    }
    getRow(agentStaffOverrideListGrid1, rowId);
}

//-----------------------------------------------------------------------------
// set initial values for agent list
//-----------------------------------------------------------------------------
function agentStaffListGrid_setInitialValues() {
    var path = getAppPath() + "/ciAgent.do?"
        + "process=getInitialValuesForAddAgent";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgentStaff, false);
}

//-----------------------------------------------------------------------------
// set initial values for agent override list
//-----------------------------------------------------------------------------
function agentStaffOverrideListGrid_setInitialValues() {
    var agentStaffId = getXMLDataForGridName(AGENT_STAFF_LIST_GRID_ID).recordset("ID").value;
    getXMLDataForGridName(AGENT_OVERRIDE_LIST_GRID_ID).recordset("CAGENTSTAFFID").value = agentStaffId;
    var path = getAppPath() + "/ciAgent.do?"
        + "process=getInitialValuesForAddAgent";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAgentStaffOverride, false);
}

//-----------------------------------------------------------------------------
// click on button for agent grid
//-----------------------------------------------------------------------------
function commonOnButtonClickAgentStaffGrid(asBtn) {
    currentlySelectedGridId = AGENT_STAFF_LIST_GRID_ID;
    commonOnButtonClick(asBtn);

    enableAddButtonForChidGrid(AGENT_STAFF_LIST_GRID_ID, 'CI_AGENT_STAFF_OVRD_ADD');
}

//-----------------------------------------------------------------------------
// click on button for agent grid
//-----------------------------------------------------------------------------
function commonOnButtonClickAgentStaffOverrideGrid(asBtn) {
    currentlySelectedGridId = AGENT_OVERRIDE_LIST_GRID_ID;
    commonOnButtonClick(asBtn);
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for Agent list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAgentStaff(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(AGENT_STAFF_LIST_GRID_ID) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
            agentStaffListGrid1.recordset("CSTAFFEFFSTARTDATE").value = "";
        }
    }
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for Agent Override list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAgentStaffOverride(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(AGENT_OVERRIDE_LIST_GRID_ID) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }

            var staffEffStartDate = getObjectValue("staffEffStartDate");
            var staffEffEndDate = getObjectValue("staffEffEndDate");
            var agentStaffId = getXMLDataForGridName(AGENT_STAFF_LIST_GRID_ID).recordset("ID").value;
            if (!isEmptyRecordset(agentStaffOverrideListGrid1.recordset)) {
                var nodes = agentStaffOverrideListGrid1.documentElement.selectNodes("//ROW");
                var length = nodes.length;
                for (i = 0; i < length; i++) {
                    var rowNode = nodes.item(i);
                    var staffId = rowNode.selectSingleNode("./CAGENTSTAFFID").text;
                    if (staffId == agentStaffId) {
                        var effStartDate = rowNode.selectSingleNode("./COVERRIDEEFFSTARTDATE").text;
                        if (effStartDate == null || effStartDate == "") {
                            rowNode.selectSingleNode("./COVERRIDEEFFSTARTDATE").text = staffEffStartDate;
                        }
                        var effStartEnd = rowNode.selectSingleNode("./COVERRIDEEFFENDDATE").text;
                        if (effStartEnd == null || effStartEnd == "" || effStartEnd == DEFAULT_DATE) {
                            rowNode.selectSingleNode("./COVERRIDEEFFENDDATE").text = staffEffEndDate;
                        }
                    }
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// delete dependent row in agent overrides list
//-----------------------------------------------------------------------------
function agentStaffListGrid_deleteDependentRow () {
    if (!getTableProperty(getTableForGrid(AGENT_STAFF_LIST_GRID_ID), "hasrows"))
        return;
    var agentId = getXMLDataForGridName(AGENT_STAFF_LIST_GRID_ID).recordset("ID").value;
    var agentOverrideXMLData = getXMLDataForGridName(AGENT_OVERRIDE_LIST_GRID_ID);
    first(agentOverrideXMLData);
    beginDeleteMultipleRow(AGENT_OVERRIDE_LIST_GRID_ID);
    while (!agentOverrideXMLData.recordset.eof) {
        if (agentOverrideXMLData.recordset("CAGENTSTAFFID").value == agentId) {
            setSelectedRow(AGENT_OVERRIDE_LIST_GRID_ID, agentOverrideXMLData.recordset("ID").value);
            agentStaffOverrideListGrid_deleterow();
            first(agentOverrideXMLData);
            continue;
        }
        next(agentOverrideXMLData);
    }
    endDeleteMultipleRow(AGENT_OVERRIDE_LIST_GRID_ID);
    first(agentOverrideXMLData);
    hideShowForm(AGENT_OVERRIDE_LIST_GRID_ID);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}