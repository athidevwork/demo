//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   unknown
// Author: unknown
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/05/2011       dzhang       115986 - Modified componentListGrid_setInitialValues() and added function getLatestCoverageExpDate.
// 02/09/2011       jshen        117529 - Modified getInitialValuesForOoseComponent() to pass into the componentEffectiveFromDate.
// 04/15/2011       ryzhao       116160 - Add function componentRegularNote() to view component note detail in pop up div.
// 05/04/2011       syang        120017 - Rollback the changes of issue 116160.
// 05/06/2011       wqfu         120258 - Modified componentListGrid_setInitialValues to add condition only for coverage.
// 05/18/2011       dzhang       117246 - Add getCoverageContinuousEffDate & getCoverageContinuousExpDate: for dateChangeAllow
//                                        risk, the component's initial dates dependence on coverage continuous dates.
// 07/11/2011       ryzhao       122363 - Modified componentListGrid_setInitialValues() to add coverageStatus field to the url if
//                                        the componentOwner is COVERAGE.
// 08/26/2011       ryzhao       122840 - Modified getInitialValuesForOoseComponent() to pass into the componentEffectiveToDate.
// 09/16/2011       ryzhao       122840 - Rollback previous changes.
// 04/25/2013       xnie         142770 - Modified getInitialValuesForOoseComponent() to add coverageStatus field to url.
// 04/26/2013       awu          141758 - Added addAllComponent().
// 07/23/2013       awu          146030 - Modified addComponents to remove the selectRowById. selectRowById is called in endAddMultipleRow.
// 01/22/2014       jyang        150639 - 1.Removed getCoverageContinuousEffDate and getCoverageContinuousExpDate methods.
//                                        2.Removed coverageContinuousEffDate and coverageContinuousExpDate from getInitialValues request.
// 03/13/2014       awu          152963 - Modified addAllComponent to alert a message when adding the Part Time component.
// 04/11/2014       adeng        153774 - Modified componentListGrid_setInitialValues() to pass in tailCovBaseRecordId
//                                        for tail component.
// 10/14/2014       jyang        157749 - Modified addAllComponent() to choose the correct coverage version for adding
//                                        components.
// 10/16/2014       wdang        156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 06/28/2017       tzeng        186273 - Modified setInitialValues() to add mainCoverageBaseRecordId for tail coverage.
// 09/14/2017       tzeng        188381 - Modified getInitialValuesForOoseComponent() to add scheduledB in URL.
// 06/11/2018       cesar        193651 - 1) Modified getLatestCoverageExpDate() to include item(0) to get the value.
// 07/03/2018       cesar        193651 - 1) Modified filterComponentData(), added a space in condition variable
//-----------------------------------------------------------------------------
var changeComponentType;
var productCovComponentId;
var origComponentId = "";
var ooseRowData;

//-----------------------------------------------------------------------------
// Following are component related functions
//-----------------------------------------------------------------------------

function componentListGrid_setInitialValues() {
    if (isForOose == "Y") {
        //Copy original data to new row
        setRecordsetByObject(componentListGrid1, ooseRowData);
        getInitialValuesForOoseComponent();
        // Reset flag
        isForOose = "N";
        return;
    }

    /* Call Ajax call to get addtional default value */
    var currentCovgDataGrid;
    var productCoverageCode;

    var coverageBaseEffectiveToDate;
    var riskId;
    var coverageBaseEffectiveFromDate
    var componentOwner = getObjectValue("componentOwner");
    var tailStatus;
    var tailScreenMode;
    var coverageBaseRecordId;
    var coverageId;
    var coverageEffectiveToDate;
    var tailCovBaseRecordId;
    var mainCoverageBaseRecordId;
    if(componentOwner == 'POLICY'){
        productCoverageCode = getObjectValue("policyTypeCode");
        coverageBaseRecordId = getObjectValue("policyId");
        coverageBaseEffectiveFromDate = getObjectValue("termEffectiveFromDate");
        coverageBaseEffectiveToDate = getObjectValue("termEffectiveToDate");
    }
    else {
        currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
        productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;

        //if is in tail page
        if (componentOwner == 'TAIL') {
            coverageBaseRecordId = currentCovgDataGrid.recordset("ID").value;
            coverageBaseEffectiveToDate = currentCovgDataGrid.recordset("CEFFECTIVETODATE").value;
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CEFFECTIVEFROMDATE").value;
            riskId = currentCovgDataGrid.recordset("CRISKBASERECORDID").value;
            tailStatus = currentCovgDataGrid.recordset("CTAILSTATUS").value;
            tailScreenMode = currentCovgDataGrid.recordset("CTAILSCREENMODE").value;
            mainCoverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
            //This is for processing entitlements.
            tailCovBaseRecordId = currentCovgDataGrid.recordset("ID").value;
        }
        //if is prior act
        else if (componentOwner == 'PRIOR_ACT') {
            coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
            coverageBaseEffectiveToDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVETODATE").value;
            riskId = currentCovgDataGrid.recordset("CRISKBASERECORDID").value;
            coverageId = currentCovgDataGrid.recordset("ID").value;
        }
        //if is in coverage page
        else {
            coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
            coverageBaseEffectiveToDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVETODATE").value;
            riskId = policyHeader.riskHeader.riskId;
            coverageEffectiveToDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVETODATE").value;
        }
    }
    var url = getAppPath() + "/coveragemgr/maintainCoverage.do?" + commonGetMenuQueryString() +
              "&productCovComponentId=" + productCovComponentId +
              "&productCoverageCode=" + productCoverageCode +
              "&coverageBaseRecordId=" + coverageBaseRecordId +
              "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate +
              "&coverageBaseEffectiveToDate=" + coverageBaseEffectiveToDate +
              "&componentOwner=" + componentOwner +               
              "&process=getInitialValuesForAddComponent";
    if(componentOwner != 'POLICY'){
        url = url +  "&riskId=" + riskId ;
    }
    if (componentOwner == 'TAIL') {
        url = url + "&tailStatus=" + tailStatus
            + "&tailScreenMode=" + tailScreenMode
            + "&tailCovBaseRecordId=" + tailCovBaseRecordId
            + "&mainCoverageBaseRecordId=" + mainCoverageBaseRecordId;
    }
    else if (componentOwner == "PRIOR_ACT") {
        url = url + "&coverageId=" + coverageId
    }
    else if (componentOwner == "COVERAGE") {
        url = url + "&coverageEffectiveToDate=" + coverageEffectiveToDate +
            /**
             * The reason why we need to add a coverageStatus field here:
             * 1.When we add a component to an existing coverage, there is no coverageStatus field in the inputRecord
             *   and system need to get coverageStatusCode from policy header in the java code. If we add coverageStatus
             *   to the url, we can get coverageStatus value from inputRecord and all the remaining operations will be
             *   the same as when we add a coverage and get initial value for auto component.
             * 2.When we renew/reissue a policy, we delete the coverages and save WIP, and then add those coverages back
             *   and add other components to a coverage. In this case, the coverage header is null and we can not get
             *   coverageStatusCode from policy header, and there will be an exception when we try to get coverageStatus
             *   value from inputRecord in the java code.
             * So when we add the coverageStatus field to the url, both of the two cases can be resolved.
             */
                    "&coverageStatus=" + currentCovgDataGrid.recordset("CCOVERAGESTATUS").value +
                    "&latestCoverageEffectiveToDate=" + getLatestCoverageExpDate(coverageBaseRecordId, coverageEffectiveToDate);
    }

    // initiate async call
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function addAllComponent(oComponentList, divPopup) {
    var len = oComponentList.length;
    var componentOwner = getObjectValue("componentOwner");
    var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
    var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
    var coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
    var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
    var coverageBaseEffectiveToDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVETODATE").value;
    var riskId = policyHeader.riskHeader.riskId;
    var coverageEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
    var coverageEffectiveToDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVETODATE").value;
    var coverageStatus = currentCovgDataGrid.recordset("CCOVERAGESTATUS").value;
    var latestCoverageEffectiveToDate = getLatestCoverageExpDate(coverageBaseRecordId, coverageEffectiveToDate);
    var coverageId = currentCovgDataGrid.recordset("ID").value;
    var partTimeB = "";
    var compStr = "";
    if (componentOwner == "COVERAGE"){
        var transEffDate = getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate);
        var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
        var selectedRecords = currentCovgDataGrid.documentElement.selectNodes("//ROW[(CBASERECORDB='N' and DISPLAY_IND = 'Y')"
                + " and (CCOVERAGEBASERECORDID = '" + coverageBaseRecordId + "')]");
        var size = selectedRecords.length;
        for (var i = 0; i < size; i++) {
            var currentRecord = selectedRecords.item(i);
            var effDate = currentRecord.selectNodes("CCOVERAGEEFFECTIVEFROMDATE").item(0).text;
            var expDate = currentRecord.selectNodes("CCOVERAGEEFFECTIVETODATE").item(0).text;
            if ((getRealDate(effDate) <= transEffDate) && (getRealDate(expDate) > transEffDate)) {
                coverageEffectiveFromDate = effDate;
                coverageEffectiveToDate = expDate;
                break;
            }
        }
    }
    for (var i = 0; i < len; i++) {
        if (!componentDulplicateValidations(oComponentList[i], true)) {
            continue;
        }
        compStr += oComponentList[i].PRODUCTCOVCOMPONENTID + "@ "
                + productCoverageCode + "@ "
                + coverageBaseRecordId + "@ "
                + coverageBaseEffectiveFromDate + "@ "
                + coverageBaseEffectiveToDate + "@ "
                + componentOwner + "@ "
                + riskId + "@ "
                + coverageEffectiveToDate + "@ "
                + coverageStatus + "@ "
                + latestCoverageEffectiveToDate + "@ "
                + oComponentList[i].CODE + "@ "
                + coverageId + "@"
                + coverageEffectiveFromDate + "@,";

        if (oComponentList[i].PARTTIMEB == "Y") {
            partTimeB = "Y";
        }
    }
    if (partTimeB == 'Y' && componentOwner != "PRIOR_ACT") {
        alert(getMessage("pm.addComponent.partTime.component.info"));
    }

    closeDiv(divPopup);
    if (compStr == "") {
        return;
    }
    showProcessingDivPopup();
    setInputFormField("selectedComponentCode", compStr);
    enableFieldsForSubmit(document.forms[0]);
    document.forms[0].process.value = "addAllComponent";
    alternateGrid_update('coverageListGrid');
    alternateGrid_update('componentListGrid');
    document.forms[0].action = buildMenuQueryString("PM_ADD_DEF_COMP", getFormActionAttribute());
    baseOnSubmit(document.coverageList);
}

//-----------------------------------------------------------------------------
// Filter component data by coverage base record Id
//-----------------------------------------------------------------------------
function filterComponentData(coverageDataGrid) {
    var componentOwner = getObjectValue("componentOwner");

    var covgBaseRecordId;
    var condition = '';
    //if is in Prior Act page
    if (componentOwner == 'PRIOR_ACT') {
        covgBaseRecordId = coverageDataGrid.recordset("CCOVERAGEBASERECORDID").value;
        var covEffDate = coverageDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
        var covExpDate = coverageDataGrid.recordset("CCOVERAGEEFFECTIVETODATE").value;
        var filterStr1 = '';
        filterStr1 = addFilterCondition(filterStr1, "CCOMPONENTEFFECTIVEFROMDATE", "<", covExpDate, XML_DATE);
        filterStr1 = addFilterCondition(filterStr1, "CCOMPONENTEFFECTIVETODATE", ">", covEffDate, XML_DATE);
        var filterStr2 = "CCOMPONENTEFFECTIVEFROMDATE=CCOMPONENTEFFECTIVETODATE and CCOMPONENTEFFECTIVETODATE ='" + covEffDate + "'";
        condition = ' and ((' + filterStr1 + ') or (' + filterStr2 + '))';
    }
    //if is in tail page
    if (componentOwner == 'TAIL') {
        covgBaseRecordId = coverageDataGrid.recordset("ID").value;
    }
    //if is in coverage page
    else {
        covgBaseRecordId = coverageDataGrid.recordset("CCOVERAGEBASERECORDID").value;
    }

    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("componentListGrid"), "selectedTableRowNo", null);
    componentListGrid_filter("CCOVERAGEBASERECORDID=" + covgBaseRecordId + condition);
}

//-----------------------------------------------------------------------------
// Pre-Insert Validations for adding components
//-----------------------------------------------------------------------------
function componentPreInsertValidations() {
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");

    // Must exist one coverage. Joe 06/05/2007
    if (isEmptyRecordset(selectedDataGrid.recordset)) {
        handleError(getMessage("pm.addComponent.noCoverage.error"));
        return false;
    }
    var covgBaseRecordId = selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value;

    // A coverage must be selected
    if (isEmpty(covgBaseRecordId)) {
        handleError(getMessage("pm.addCoverageClass.missingCoverage.error"));
        return false;
    }

    return true;
}

//-----------------------------------------------------------------------------
// Add one component
//-----------------------------------------------------------------------------
function addOneComponent(component, showMessage) {
    productCovComponentId = component.PRODUCTCOVCOMPONENTID;
    // Add component and set default values
    commonAddRow("componentListGrid");
    return true;
}

//-----------------------------------------------------------------------------
// Add components and their dependent componets
//-----------------------------------------------------------------------------
function addComponents(oCompList, showMessage) {
    try {
        //begin inserting multiple rows
        beginAddMultipleRow("componentListGrid");

        var compSize = oCompList.length;

        //validate exist component type first
        for (var i = 0; i < compSize; i++) {
            var comp = oCompList[i];
            if (!componentDulplicateValidations(comp, true)) {
                return false;
            }
        }

        var validCompList = new Array();
        var addedCount = 0;
        var firstComp = 0;
        for (var i = 0; i < compSize; i++) {
            var comp = oCompList[i];
            // Check if added one component
            if (addOneComponent(comp, showMessage)) {
                validCompList[addedCount] = comp;
                addedCount ++;
                if (firstComp == 0 ) {
                    firstComp = getTableProperty(getTableForGrid("componentListGrid"), "lastInsertedId");
                }
            }
        }

        // Add dependent components
        addDependentComponent(validCompList);
    }
    finally {
        //end inserting multiple rows
        endAddMultipleRow("componentListGrid");
    }
}

//-----------------------------------------------------------------------------
// Make Ajax call to add dependent component
//-----------------------------------------------------------------------------
function addDependentComponent(oComponentList) {
    var productCoverageCode;
    var coverageBaseRecordId;
    var coverageId;
    var coverageBaseEffectiveToDate;
    var riskId;
    var coverageBaseEffectiveFromDate
    var componentOwner = getObjectValue("componentOwner");
    if(componentOwner == 'POLICY'){
        productCoverageCode = getObjectValue("policyTypeCode");
        coverageBaseRecordId = getObjectValue("policyId");
        coverageBaseEffectiveFromDate = getObjectValue("termEffectiveFromDate");
        coverageId = getObjectValue("policyId");
    }
    else {
        var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
        productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
        coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
        coverageId = currentCovgDataGrid.recordset("ID").value;

        //if is in tail page
        if (componentOwner == 'TAIL') {
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CEFFECTIVEFROMDATE").value;
            riskId = currentCovgDataGrid.recordset("CRISKBASERECORDID").value;
        }
        //if is prior act
        else if (componentOwner == 'PRIOR_ACT') {
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
            riskId = policyHeader.riskHeader.riskId;
        }
        //if is in coverage page
        else {
            coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
            riskId = policyHeader.riskHeader.riskId;
        }
    }
    var url = getAppPath() + "/coveragemgr/maintainCoverage.do?" + commonGetMenuQueryString() +
              "&process=loadDependentComponent" +
              "&productCoverageCode=" + productCoverageCode +
              "&coverageBaseRecordId=" + coverageBaseRecordId +
              "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate +
              "&coverageId=" + coverageId +
              "&componentOwner=" + componentOwner;
    if(componentOwner != 'POLICY'){
       url = url +  "&riskId=" + riskId ;
    }

    /* Loop through parent components */
    var len = oComponentList.length;
    for (var i = 0; i < len; i++) {
        url += "&componentParent=" + oComponentList[i].CODE;

        // initiate async call
        new AJAXRequest("get", url, '', addDependentComponentDone, false);
    }
}

//-----------------------------------------------------------------------------
// Add dependent component
//-----------------------------------------------------------------------------
function addDependentComponentDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            if (data.documentElement == null || !handleAjaxMessages(data, null)) {
                /* no dependent component found */
                return;
            }

            /* Parse xml and add dependent component(s) */
            var oComponentList = parseXML(data);
            var len = oComponentList.length;
            for (var i = 0; i < len; i++) {
                addOneComponent(oComponentList[i], false);
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Add oose component
//-----------------------------------------------------------------------------
function addOoseComponent() {
    // Save current row's data into object
    origComponentId = componentListGrid1.recordset("ID").value;
    ooseRowData = getObjectFromRecordset(componentListGrid1);

    commonOnButtonClick('ADD_NEW_COMP');
}

//-----------------------------------------------------------------------------
// To get initial values for oose component
//-----------------------------------------------------------------------------
function getInitialValuesForOoseComponent() {
    // set url
    var riskId = 0;
    var componentOwner = getObjectValue("componentOwner");
    if(policyHeader.riskHeader){
        riskId = policyHeader.riskHeader.riskId;
    }
    var selectedDataGrid = getXMLDataForGridName("componentListGrid");
    var url = getAppPath() + "/coveragemgr/maintainCoverage.do?process=getInitialValuesForOoseComponent" +
              "&" + commonGetMenuQueryString("PM_MAINTAIN_COMPONENT", "") +
              "&policyCovComponentId=" + origComponentId +
              "&changeType=" + changeComponentType +
              "&riskId=" + riskId +
              "&componentTypeCode=" + selectedDataGrid.recordset("CCOMPONENTTYPECODE").value +
              "&code=" + selectedDataGrid.recordset("CCODE").value +
              "&cycledB=" + selectedDataGrid.recordset("CCYCLEDB").value +
              "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
              "&lowValue=" + selectedDataGrid.recordset("CLOWVALUE").value +
              "&highValue=" + selectedDataGrid.recordset("CHIGHVALUE").value +
              "&afterImageRecordB=" + selectedDataGrid.recordset("CAFTERIMAGERECORDB").value +
              "&officialRecordId=" + selectedDataGrid.recordset("COFFICIALRECORDID").value +
              "&expiryDateB=" + selectedDataGrid.recordset("CEXPIRYDATEB").value +
              "&percentValueB=" + selectedDataGrid.recordset("CPERCENTVALUEB").value +
              "&sequenceNo=" + selectedDataGrid.recordset("CSEQUENCENO").value +
              "&componentEffectiveFromDate=" + selectedDataGrid.recordset("CCOMPONENTEFFECTIVEFROMDATE").value +
              "&componentOwner=" + componentOwner +
              "&scheduledB=" + selectedDataGrid.recordset("CSCHEDULEDB").value;
    if (componentOwner == "COVERAGE") {
        url += "&coverageStatus=" + getXMLDataForGridName("coverageListGrid").recordset("CCOVERAGESTATUS").value;
    }
    if (isFieldExistsInRecordset(selectedDataGrid.recordset, "CCOVERAGEBASERECORDID")) {
        url = url + "&coverageBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value;
    }
    new AJAXRequest("get", url, "", handleOnGetInitialValuesForOoseComponent, false);
}

//-----------------------------------------------------------------------------
// Callback function for getting oose coverage initial values
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForOoseComponent(ajax) {
    commonHandleOnGetAddlInfo(ajax);
    if (changeComponentType == "chgCompValue") {
        getObject("componentValue").select();
    }
    else if (changeComponentType == "chgCompDate") {
        getObject("componentEffectiveToDate").select();
    }
}


//-----------------------------------------------------------------------------
// Dulplicated component validations
//-----------------------------------------------------------------------------
function componentDulplicateValidations(oComponent, showMessage) {
    if (!getTableProperty(getTableForGrid("componentListGrid"), "hasrows")) {
        return true;
    }

    var rowIndex = 0;
    first(componentListGrid1);

    while (!componentListGrid1.recordset.eof) {
        if (oComponent.PRODUCTCOVCOMPONENTID == componentListGrid1.recordset("CPRODUCTCOVCOMPONENTID").value &&
            componentListGrid1.recordset("CRECORDMODECODE").value != "OFFICIAL") {
            if (showMessage == true) {
                var parms = new Array(oComponent.SHORTDESCRIPTION);
                handleError(getMessage("pm.addComponent.duplicated.error", parms),
                    "", "");
            }
            return false;
        }

        rowIndex ++;
        next(componentListGrid1);
    }

    return true;
}

function getLatestCoverageExpDate(coverageBaseRecordId, coverageEffectiveToDate) {
    var transactionLogId = policyHeader.lastTransactionId;
    var latestCovgExpDate = coverageEffectiveToDate;
    var selectedRecords = coverageListGrid1.documentElement.selectNodes("//ROW[(CBASERECORDB='N')"
            + " and (CRECORDMODECODE='TEMP')"
            + " and (CTRANSACTIONLOGID='" + transactionLogId + "')"
            + " and (CCOVERAGEBASERECORDID = '" + coverageBaseRecordId + "')]");
    if (selectedRecords.length > 0) {
        latestCovgExpDate = selectedRecords.item(0).selectNodes("CCOVERAGEEFFECTIVETODATE").item(0).text;
    }
    return latestCovgExpDate;
}

var dateRE = /^(\d{2})[\/\- ](\d{2})[\/\- ](\d{4})/;
Array.prototype.deepsort = function() {
    var i, order = arguments, L = order.length, tem;
    return this.sort(function(a, b) {
        i = 0;
        while (i < L) {
            tem = order[i++];
            var ao = a[tem] || 0, bo = b[tem] || 0;
            ao = ao.replace(dateRE, "$3$2$1");
            bo = bo.replace(dateRE, "$3$2$1");
            if (ao == bo) continue;
            return ao > bo ? 1 : -1;
        }
        return 0;
    });
}
