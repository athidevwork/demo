//-----------------------------------------------------------------------------
// Javascript logics for maintain schedule
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 28, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/28/2010       bhong      Removed logics to fire "onchange" event which created infinite loop when eCIS execute call back function.
//                             Also deleted junk codes
// 01/04/2012       wfu        127802 - Modified handleFindClient to reload entity name for correct entity format.
// 05/31/2013       jshen      145239 - Modified handleFindClient to make sure the newly added row is selected
// 09/03/2014       kxiang     157239 - 1.Added function handleOnGetEntityName.
//                                      2.Modified handleFindClient, change call commonHandleOnGetAddlInfo to
//                                        handleOnGetEntityName.
// 10/09/2014       wdang      156038 - 1. Replaced getObject('riskId') with policyHeader.riskHeader.riskId. 
//                                      2. Replaced getObject('coverageId') with policyHeader.coverageHeader.coverageId.
//-----------------------------------------------------------------------------
var entityName = "entityName";
var entityFK = "entityId";

function find(fieldId) {
    if (fieldId == entityName) {
        selectRecord();
    }
}

function handleFindClient() {
    if (scheduleListGrid1.recordset("UPDATE_IND").value == "N")
        scheduleListGrid1.recordset("UPDATE_IND").value = "Y";

    var url = getAppPath() + "/entitymgr/lookupEntity.do?process=getEntityName"
                           + "&entityId=" + getObjectValue("entityId")
                           + "&entityIdFieldName=entityId"
                           + "&entityNameFieldName=entityName"
                           + "&date=" + new Date();
    new AJAXRequest("get", url, '', handleOnGetEntityName, false);
    selectRowById("scheduleListGrid", scheduleListGrid1.recordset("ID").value);
}

function selectRecord() {
  openEntitySelectWinFullName(entityFK, entityName, "handleFindClient()");
}

function validateGrid() {
    return true;
}

function scheduleListGrid_setInitialValues() {
    var url = getAppPath() + "/schedulemgr/maintainSchedule.do?"
        + commonGetMenuQueryString()+"&process=getInitialValuesForSchedule";

    if (policyHeader.riskHeader) {
        var riskId = policyHeader.riskHeader.riskId;
        url = url + "&riskId=" + riskId;
    }
    if(hasObject("isFromCoverage")){
        url = url+"&isFromCoverage=Y";
        var coverageId = policyHeader.coverageHeader.coverageId;
        url = url + "&coverageId=" + coverageId;
    }
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            if (policyHeader.riskHeader) {
                setInputFormField("riskId", policyHeader.riskHeader.riskId);
            }
            if (policyHeader.coverageHeader) {
                setInputFormField("coverageId", policyHeader.coverageHeader.coverageId);
            }
            document.forms[0].process.value = "saveAllSchedules";
            break;

        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Handle get Entity Name by Ajax, set back original ID.
//-----------------------------------------------------------------------------
function handleOnGetEntityName(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var currentlySelectedGridId = getCurrentlySelectedGridId();
                // set back original ID.
                oValueList[0]["ID"] = scheduleListGrid1.recordset("ID").value;
                if (!isEmpty(currentlySelectedGridId)) {
                    var selectedDataGrid = getXMLDataForGridName(currentlySelectedGridId) ;
                    setRecordsetByObject(selectedDataGrid, oValueList[0], true);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
        }
    }
}
