//-----------------------------------------------------------------------------
// Javascript file for processMinitail.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/08/2011       ryzhao      123646 - Modified riskCoverageGrid_selectRow. The parameter id is coverageBaseRecordId.
//                              We should get riskBaseRecordId from data grid.
// 04/17/2012       xnie        132237 - Modified submitForm to add rowId for getMessage.
// 04/25/2012       xnie        132237 - Modified submitForm
//                                       a.Removed logic which checks Rating Basis field value. This part logic will be
//                                       handled in impl.
//                                       b.Called alternateGrid_update for riskCoverageGrid.
// 01/07/2015       fcb         159796 - policyTermHistoryId passed to Ajax call to getMinitailEditable.
// 03/13/2017       eyin        180675 - Added condition 'isButtonStyle()' when calling 'hideShowField' in the method 'setEditable',
//                                       Added condition 'isChanged || isPageGridsDataChanged()' in the method 'handleOnButtonClick'
//                                       for UI change.
// 03/30/2017       lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var currentMinitailId;
var APPLY = "toRateB";
var RATING_BASIS = "miniTailRatingBasis";
var SAVE = "PM_MINI_SAVE";
//for riskCoverageGrid_selectRow(id) and minitail_selectRow(id) to append,so it should be defined here
var url = "";
//the editable flag to whole risk/coverage
var riskCoverageFlag;

//when click the top grid,do something to make the bottom grid display different data
function riskCoverageGrid_selectRow(id) {

    var selectedDataGrid = getXMLDataForGridName("riskCoverageGrid");
    //currently the two field obey the same rule
    riskCoverageFlag = selectedDataGrid.recordset("CISAPPLYEDITABLE").value
    // Get risk base record id
    var riskBaseRecordId = selectedDataGrid.recordset("CRISKBASERECORDID").value;
    var policyTermHistoryId;
    if (hasObject("policyTermHistoryId")) {
        policyTermHistoryId = getObjectValue("policyTermHistoryId");
    }

    //need to call ajax to judge if it is editable
    url = getAppPath()+"/coveragemgr/minitailmgr/processMinitail.do?process=getMinitailEditable&riskBaseRecordId=" + riskBaseRecordId +
            "&policyTermHistoryId=" + policyTermHistoryId;
    // Filter component data
    filterMinitailData(selectedDataGrid);
    doDispControl();
}
function doDispControl() {
    var compXmlData = getXMLDataForGridName("minitailGrid");
    if (isEmptyRecordset(compXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(compXmlData));
        hideGridDetailDiv("minitailDetailDiv");
    }
    else {
        showNonEmptyTable(getTableForXMLData(compXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("minitailDetailDiv"), false);
    }
}
//when user clicked one row,send it to server side to see if it's editable
function minitailGrid_selectRow(id) {
    //store current mini tail id so we could put it into a map in other function
    currentMinitailId = id;
    var selectedDataGrid = getXMLDataForGridName("minitailGrid");
    //if in risk level it is not editable
    if (riskCoverageFlag == "N") {
        selectedDataGrid.recordset("CHASCHECKED").value = "N";
        setEditable("N");
        return;
    }
    var flag = selectedDataGrid.recordset("CHASCHECKED").value;
    if (flag != "Y" && flag != "N") {
        // Load Additional Info fields via AJAX call if this is not a newly inserted row
        var param = "&miniTailPk=" + id +
               "&effectiveFromDate=" + selectedDataGrid.recordset("CEFFECTIVEFROMDATE").value +
               "&effectiveToDate=" + selectedDataGrid.recordset("CEFFECTIVETODATE").value +
               "&coverageBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value +
               "&transactionLogId=" + selectedDataGrid.recordset("CTRANSACTIONLOGID").value +
               "&mandatoryMinitailB=" + selectedDataGrid.recordset("CMANDATORYMINITAILB").value+
               "&policyNo=" + getObjectValue("policyNo") +
               "&date=" + new Date();

        // initiate async call
        new AJAXRequest("get", url+param, '', handleOnSetEditable, false);

    }
    else {
        if (flag == "Y")
            setEditable("Y");
        else
            setEditable("N");
    }
}

function alertNoData(){
    //to be change
    handleError(getMessage("pm.processMinitail.nodata.error"));
    closeWindow();
}

function handleOnSetEditable(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // get flag,currently the two fields share one flag,but if rule changes you still can retrieve
            // isBasisEditable
            var flag;
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
                flag = getObjectValue("isApplyEditable");
                setEditable(flag);
            }
        }
    }
}

function setEditable(flag) {
    var selectedDataGrid = getXMLDataForGridName("minitailGrid");
    if (flag == "Y") {
        selectedDataGrid.recordset("CHASCHECKED").value = "Y";
        getObject(APPLY).disabled = false;
        getObject(RATING_BASIS).disabled = false;
        if(isButtonStyle()){
            hideShowField(getObject(SAVE),false);
        }
    }
    else {
        selectedDataGrid.recordset("CHASCHECKED").value = "N";
        getObject(APPLY).disabled = true;
        getObject(RATING_BASIS).disabled = true;
        hideShowField(getObject(SAVE),true);
    }
}

function filterMinitailData(coverageRiskGrid) {
    //    var componentOwner = getObjectValue("componentOwner");
    var covgBaseRecordId = coverageRiskGrid.recordset("id").value;

    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("minitailGrid"), "selectedTableRowNo", null);
    minitailGrid_filter("CCOVERAGEBASERECORDID=" + covgBaseRecordId);
}

function handleOnButtonClick(btn) {
    var proceed = true;
    switch (btn) {
    //this should be exactly same with the name of save method in Action class,because of workflow reason
        case 'saveAllMinitail':
            proceed = false;
            if (isChanged || isPageGridsDataChanged()){
                document.forms[0].process.value = "saveAllMinitail";
				autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
                commonOnSubmit('saveAllMinitail', true, true, true, true);
            }else{
                autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
            }
			syncResultToParent(autoSaveResultType);
        //disable the defualt process(because default process submits wrong data when there are
        //two grids)

            break;
        case 'Close':
            if (isEmpty(getObjectValue("workflowState"))) {
                closeWindow();
            }
            else {
                commonOnSubmit('closePage', true, true, true);
            }
            break;
    }
    return proceed;
}

function closeWindow() {
    commonOnButtonClick("CLOSE_DIV");
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}
