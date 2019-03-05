//-----------------------------------------------------------------------------
// Javascript file for maintainAdditionalInsured.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 23, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/23/2010       syang       Issue 108651 - Added handleOnChange() to handle Renew indicator when change End Date.
// 09/21/2010       syang       Issue 111445 - Added getAddInsCoverageData() to get coverage data if system parameter
//                                             "PM_ADDINS_COVG_DATA" is "Y". 
// 10/19/2010       syang       Issue 113283 - When risk was changed, the coverage data should be changed for new record.
// 11/10/2010       gzeng       Issue 113763 - Update the ajax logic for 'Print Date' field in Additional Insured page
// 01/04/2012       wfu         Issue 127802 - Modified additionalInsuredListGrid_setInitialValues to get entity name.
// 02/28/2013       xnie        Issue 138026 - 1) Modified handleOnLoad to disable select check box based on Generate
//                                                button Show/Hide.
//                                             2) Modified handleOnButtonClick to call generateAddins.
//                                             3) Added generateAddIns to call validateAddInsSelection and
//                                                captureAsOfDate.
//                                             4) Added validateAddInsSelection to check if no any Additional Insured
//                                                gets selected.
//                                             5) Added captureAsOfDate to capture As of Date as the transaction date.
//                                             6) Added handleOnCaptureAsOfDate to handle capturing As of Date.
//                                             7) Added submitToGenerateAllAddIns to generate all Additional Insureds.
//                                             8) Added additionalInsuredList_btnClick to make all of select check boxes
//                                                selected.
// 05/20/2013       xnie        Issue 144334 - 1) Added handleReadyStateReady(), handlePostAddRow(), and
//                                                initGridSelectCheckbox() to disable/enable Select to Generate checkbox.
// 07/29/2014       kxiang      Issue 155534 - 1) Modified function handlePostAddRow(), add Handle the url of additional
//                                                insured name column.
//                                             2) Added function handleOnGetInitialValuesForAddAdditionalInsured to set
//                                                value for nameHref.
//                                             3) Used function handleOnGetInitialValuesForAddAdditionalInsured to replace
//                                                of commonHandleOnGetInitialValues.
// 08/13/2014      kxiang       Issue 155534 - Modified handleOnGetInitialValuesForAddAdditionalInsured to call common
//                                             function commonHandleOnGetInitialValues.
// 03/10/2017      wli          Issue 180675 - 1)Added condition "isExeInvokeWorkFlow()" when call invokeWorkflow() in the method
//                                               named "handleOnLoad()".
//                                             2)Added getOpenCtxOfDivPopUp() when call openDivPopup() in the method named
//                                               captureAsOfDate(), added condition of "isTabStyle()" in the method named "openSelectLocation".
// 05/23/2017      lzhang       Issue 185079 - pass parameter when call getParentWindow()
//                                             and use handleOnPutParentWindowOfDivPopup()
// 07/12/2017      lzhang       Issue 186847 - Reflect grid replacement project changes
// 07/31/2018      mlm          Issue 193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
// 10/27/2018      xgong        Issue 195889 - 1) Updated initGridSelectCheckbox/handleOnLoad/submitToGenerateAllAddIns/handleOnSubmit
//                                                for grid replacement
//                                             2) Added a new function handleGetCustomPageOptions for initGridSelectCheckbox won't work in new grid
//-----------------------------------------------------------------------------
function handleOnLoad() {
    // disable selectAll checkbox
    if (!isEmptyRecordset(additionalInsuredListGrid1.recordset) && !dti.oasis.page.useJqxGrid()) {
        var nodes = additionalInsuredListGrid1.documentElement.selectNodes("//ROW[CISGENERATEAVAILABLE='N']");
        if (nodes.length > 0 && hasObject("HCSELECT_IND")) {
            getObject("HCSELECT_IND").disabled = true;
        }
    }

    if (isExeInvokeWorkFlow()) {
        invokeWorkflow();
    }

    if (isEmptyRecordset(additionalInsuredListGrid1.recordset)) {
        hideShowElementByClassName(getObject("coverageLayer"), true);
    }
    else{
        hideShowElementByClassName(getObject("coverageLayer"), false);
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_ADDIINS':
            addAdditionalInsurd();            
            break;
        case 'LOCATION':
            selectLocation();
            break;
        case 'GENERATE':
            generateAddIns();
            break;
    }
}

//-----------------------------------------------------------------------------
// To generate Additional Insured
//-----------------------------------------------------------------------------
function generateAddIns() {
    // First check if no Additional Insured's selected
    if (validateAddInsSelection()) {
        captureAsOfDate();
    }
}

//-----------------------------------------------------------------------------
// To validate if no Additional Insured is selected
//-----------------------------------------------------------------------------
function validateAddInsSelection() {
    var selectToGenerateAddInsIds = "";
    var isSelected = false;
    if (!isEmptyRecordset(additionalInsuredListGrid1.recordset)) {
        first(additionalInsuredListGrid1);
        while (!additionalInsuredListGrid1.recordset.eof) {
            var isGenerate = additionalInsuredListGrid1.recordset("CSELECT_IND").value;
            if (isGenerate == "-1") {
                isSelected = true;
                selectToGenerateAddInsIds += additionalInsuredListGrid1.recordset("ID").value + ",";
            }
            next(additionalInsuredListGrid1);
        }
        first(additionalInsuredListGrid1);
        if (selectToGenerateAddInsIds.length > 0) {
            selectToGenerateAddInsIds = selectToGenerateAddInsIds.substring(0, selectToGenerateAddInsIds.length - 1);
            setInputFormField("selectToGenerateAddInsIds", selectToGenerateAddInsIds);
        }
        if (!isSelected) {
            handleError(getMessage("pm.generateAddIns.noselection.error"));
        }
    }
    return isSelected;
}

//-----------------------------------------------------------------------------
// To capture As of Date as the transaction effective from date
//-----------------------------------------------------------------------------
function captureAsOfDate() {
    var url = getAppPath() + "/policymgr/additionalinsuredmgr/captureAddInsAsOfDate.do?"
            + commonGetMenuQueryString() + "&process=display";
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "500", "400", "", "", "", false);
}

//-----------------------------------------------------------------------------
// To handle capturing As of Date
//-----------------------------------------------------------------------------
function handleOnCaptureAsOfDate(asOfDate) {
    // set asOfDate into a input field
    setInputFormField("addInsAsOfDate", asOfDate);
    submitToGenerateAllAddIns();
}

//-----------------------------------------------------------------------------
// To generate all of Additional Insureds
//-----------------------------------------------------------------------------
function submitToGenerateAllAddIns() {
    setObjectValue("process", "generateAllAddIns");
    submitFirstForm();
}

function handleReadyStateReady(table) {
    if (table.id == "additionalInsuredListGrid")
        initGridSelectCheckbox();
}

function handlePostAddRow(table) {
    if (table.id == "additionalInsuredListGrid") {
        var absolutePosition = additionalInsuredListGrid1.recordset.AbsolutePosition;
        initGridSelectCheckbox();
        first(additionalInsuredListGrid1);
        additionalInsuredListGrid1.recordset.move(absolutePosition - 1);

        var xmlData = getXMLDataForGridName("additionalInsuredListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var additionalInsuredNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CNAME") {
                additionalInsuredNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + additionalInsuredNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("NAMEHREF"))) {
                    href = "javascript:handleOnGridHref('additionalInsuredListGrid', '" + getObjectValue("NAMEHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}
//-----------------------------------------------------------------------------
// To disable/enable Select to Generate checkbox
//-----------------------------------------------------------------------------
function initGridSelectCheckbox() {
    // Do nothing if the table is empty
    if (!getTableProperty(additionalInsuredListGrid, "hasrows")) {
        return;
    }

    if (dti.oasis.page.useJqxGrid()) {
        return;
    }

    var disableChkSelectAll = true;
    // When there is pagination, not all records are in table. So move to the proper record first.
    resetRecordPointerToFirstRowInGridCurrentPage(additionalInsuredListGrid);

    // Initialize the select check boxes in table
    var XMLData = additionalInsuredListGrid1;
    var chkSelArray = document.getElementsByName("chkCSELECT_IND");
    var size = chkSelArray.length;
    if (!isEmptyRecordset(XMLData.recordset)) {
        for (var i = 0; i < size; i++) {
            var isGenerateAvailable = XMLData.documentElement.selectNodes("//ROW").item(0).selectNodes("CISGENERATEAVAILABLE").item(0).text;
            chkSelArray[i].disabled = isGenerateAvailable == "N";
            next(XMLData);
        }
    }
    // Move back to where we started
    resetRecordPointerToFirstRowInGridCurrentPage(additionalInsuredListGrid);
}

//-----------------------------------------------------------------------------
// To make all of select check boxes selected
//-----------------------------------------------------------------------------
function additionalInsuredList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            setObjectValue("process", "saveAllAdditionalInsured");
            var needToCaptureTransaction = getObjectValue("needToCaptureTransaction");
            if (needToCaptureTransaction == "Y") {
                captureTransactionDetails("ENDADDTLIN", "submitForm");
            }
            else {
                proceed = true;
            }
            break;
    }

    return proceed;
}

function selectLocation(){
    var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
    var entityId =xmlData.recordset("CENTITYID").value;
    openSelectLocation(entityId);
}

function openSelectLocation(entityId) {
    var path = getAppPath() + "/riskmgr/selectLocation.do?entityId=" + entityId+"&singleSelect=Y";
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true,
        null, null, "", "", "", "", "selectLocation", false);
    if(isTabStyle()){
        var oParentWindowFlag = typeof subFrameId != 'undefined' ? "ParentWindow" : "iFrameWindow";
        getOpenCtxOfDivPopUp().handleOnPutParentWindowOfDivPopup(divPopupId, oParentWindowFlag);
    }
}

//-----------------------------------------------------------------------------
// Add Additional Insured
//-----------------------------------------------------------------------------
function addAdditionalInsurd() {
    setInputFormField("seledEntityId",0);
    setInputFormField("seledEntityName","");
    openEntitySelectWinFullName("seledEntityId", "seledEntityName", "handleFindClient()");
}

//-----------------------------------------------------------------------------
// Call back function for select location
//-----------------------------------------------------------------------------
function handleOnSelectLocation(action, locations) {
    if (action == "Select") {
        var locationCount = locations.length;
        if (locationCount > 0) {
            var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
            xmlData.recordset("CADDRESSID").value = locations[0].addressId;
            setInputFormField("address",locations[0].address);
            //fire commonOnChange event to, update UPDATE_IND
            commonOnChange(getObject("address"));
        }
    }
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleFindClient(){
    commonAddRow(getCurrentlySelectedGridId());   
}

function additionalInsuredListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do?"
            + commonGetMenuQueryString()+"&process=getInitialValuesForAdditionalInsured"
            + "&entityId="+getObjectValue("seledEntityId");
    new AJAXRequest("get", url, '', handleOnGetInitialValuesForAddAdditionalInsured, false);
}

//-----------------------------------------------------------------------------
// Change Renew indicator to "No" and disable it if the End Date is less than policy expiration date.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "endDate") {
        var endDate = obj.value;
        var policyExpirationDate = getObjectValue("policyExpirationDate");
        enableDisableRenewIndicator(endDate, policyExpirationDate, "renewalB", "isRenewalBAvailable", "additionalInsuredListGrid");
    }
    // If Attached Risk is changed, system retrieves coverage data for selected Attached Risk. 
    else if (obj.name == "riskId") {
        getAddInsCoverageData(obj.value);
    }
}

function additionalInsuredListGrid_selectRow(id) {
    getAddInsCoverageData(additionalInsuredListGrid1.recordset("CRISKID").value);
    // System should display the coverage layer after inserted a row.
    if (isElementHidden(getObject("coverageLayer"))) {
        hideShowElementByClassName(getObject("coverageLayer"), false);
    }
}

//-----------------------------------------------------------------------------
// If the system parameter "PM_ADDINS_COVG_DATA" is "Y" and the Attached Risk is changed, system should
// retrieve coverage data for selected risk. If no risk is selected, system set coverage data empty.
//-----------------------------------------------------------------------------
function getAddInsCoverageData(riskId) {
    if (getSysParmValue("PM_ADDINS_COVG_DATA") == "Y") {
        if (isEmpty(riskId)) {
            clearCoverageData();
        }
        else {
            var url = getAppPath() + "/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do?" +
                    commonGetMenuQueryString() + "&process=getAddInsCoverageData" +
                    "&riskBaseId=" + riskId +
                    "&effDate=" + additionalInsuredListGrid1.recordset("CSTARTDATE").value +
                    "&addlinsTransId=" + additionalInsuredListGrid1.recordset("CTRANSACTIONLOGID").value;
            new AJAXRequest("get", url, '', getAddInsCoverageDataDone, false);
        }
    }
}

function getAddInsCoverageDataDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setObjectValue("printDate", oValueList[0]["PRINTDATE"]);
                setObjectValue("covg1Sir", formatMoneyStrValAsStr(oValueList[0]["COVG1SIR"]));
                setObjectValue("covg1AggLmt", formatMoneyStrValAsStr(oValueList[0]["COVG1AGGLMT"]));
                setObjectValue("covg1OccLmt", formatMoneyStrValAsStr(oValueList[0]["COVG1OCCLMT"]));
                setObjectValue("covg1RetroDate", oValueList[0]["COVG1RETRODATE"]);
                setObjectValue("covg3OccLmt", formatMoneyStrValAsStr(oValueList[0]["COVG3OCCLMT"]));
                setObjectValue("covg2AggLmt", formatMoneyStrValAsStr(oValueList[0]["COVG2AGGLMT"]));
                setObjectValue("covg2OccLmt", formatMoneyStrValAsStr(oValueList[0]["COVG2OCCLMT"]));
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Set coverage data empty. 
//-----------------------------------------------------------------------------
function clearCoverageData() {
    setObjectValue("printDate", "");
    setObjectValue("covg1Sir", "");
    setObjectValue("covg1AggLmt", "");
    setObjectValue("covg1OccLmt", "");
    setObjectValue("covg1RetroDate", "");
    setObjectValue("covg3OccLmt", "");
    setObjectValue("covg2AggLmt", "");
    setObjectValue("covg2OccLmt", "");
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddAdditionalInsured(ajax) {
    commonHandleOnGetInitialValues(ajax, "NAMEHREF");
}


//-----------------------------------------------------------------------------
// Set Check All button readOnly for grid replacement
//-----------------------------------------------------------------------------
function handleGetCustomPageOptions() {
    function __isSelectAllEnabled(gridInfo){
        if (gridInfo.id == "additionalInsuredListGrid"){
            return dti.oasis.grid.getRawData("additionalInsuredListGrid")[0].CISGENERATEAVAILABLE == "Y";
        }
    }
    return dti.oasis.page.newCustomPageOptions()
            .addSelectAllEnabledFunction("additionalInsuredListGrid",__isSelectAllEnabled);
}