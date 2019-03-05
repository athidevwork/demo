//-----------------------------------------------------------------------------
// JavaScript file for processPredictiveAnalytics.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date: May 06, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/08/2011       syang       Handled the "Clear".
// 03/30/2017       lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'PROCESS':
            document.forms[0].process.value = "processOpa";
            break;
        default:
            proceed = false;
            break;
    }
    return proceed;
}

function handleOnButtonClick(action) {
    switch (action) {       
        case 'FILTER':
            var entityId = getObjectValue("entityId");
            var scoreRequestId = requestListGrid1.recordset("ID").value;
            var filterStr = "CRISKENTITYFK=" + entityId + " and COPASCOREREQFK=" + scoreRequestId;
           // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
            setTableProperty(eval("resultListGrid"), "selectedTableRowNo", null);
            resultListGrid_filter(filterStr);
            break;
        case 'opaError':
            var policyId = getObjectValue("policyId");
            var scoreReqTypeCode = "";

            if (!isEmptyRecordset(requestListGrid1.recordset)) {
                var scoreReqType = requestListGrid1.recordset("CSCOREREQUESTTYPE").value.toUpperCase();
                var scoreRequestId = requestListGrid1.recordset("ID").value;

                switch (scoreReqType) {
                    case 'NEW BUSINESS'.toUpperCase():
                    case 'N':
                        scoreReqTypeCode = 'N';
                        break;
                    case 'ON DEMAND RENEWAL'.toUpperCase():
                    case 'D':
                        scoreReqTypeCode = 'D';
                        break;
                    case 'Batch Renewal'.toUpperCase():
                    case 'R':
                        scoreReqTypeCode = 'R';
                        break;

                }
                opaErrors(policyId, scoreReqTypeCode, scoreRequestId);
            }
            break;
        case 'CLEAR':
            getObject("entityId").value = "";
            // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
            setTableProperty(eval("resultListGrid"), "selectedTableRowNo", null);
            resultListGrid_filter("");
            break;
        default:break;
    }
}

function requestListGrid_selectRow(id) {
    // Clear filter criteria.
    getObject("entityId").value = "";

    setTableProperty(eval("resultListGrid"), "selectedTableRowNo", null);
    resultListGrid_filter("COPASCOREREQFK=" + id);
    if (isEmptyRecordset(resultListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(resultListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(resultListGrid1));
    }
}

function resultListGrid_selectRow(id) {
    setTableProperty(eval("reasonListGrid"), "selectedTableRowNo", null);
    reasonListGrid_filter("COPASCORERESULTFK=" + id);
    if (isEmptyRecordset(reasonListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(reasonListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(reasonListGrid1));
    }
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}
