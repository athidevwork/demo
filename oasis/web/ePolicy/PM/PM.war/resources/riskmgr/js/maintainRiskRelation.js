//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 14, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 07/15/2010       dzhang      103806 - Added logic for non-base company insured.
// 07/16/2010       dzhang      103806 - Remove unused code and format code.
// 07/28/2010       syang       109479 - Added parameter "riskId" to open policy insured risk relation page.
// 10/12/2010       wfu         111776: Replaced hardcode variable deleteQuestion with resource definition
// 04/20/2011       dzhang      118640 - Modified handleOnGetProductCoverageCode to force fire AJAX for productCoverageCode.
// 04/21/2011       fcb         119793 - postAjaxRefresh added.
// 07/19/2011       wfu         122833 - Logic modified to correct role list records in entity search page.
//                                       Use policyHolderName to call method openEntitySelectWinFullName.
// 08/22/2011       wfu         123837 - Added page entitlement logic after select specialty.
// 10/11/2011       wfu         123837 - Changed logic to get values of field riskCountyCode and transEffectiveFromDate
//                                       to avoid values lost after system throws validation error message.
// 11/09/2011       dzhang      126933 - Modified handleOnIsAnnualPremiumEditable: In editable mode, if AnnualPremium is disabled, set AnnualPremium to blank.
// 04/30/2013       tcheng      143402 - Modified to refresh the parent page before closing the risk relation.
// 10/22/2013       xnie        148088 - Modified handleOnButtonClick() to add reverse condition for refreshing parent
//                                       page. When risk relation page isn't from reverse and there is no any risk
//                                       relation data change, system refreshes parent page.
// 04/04/2013       fcb         153617 - Added riskId to the sendAJAXRequest.
// 05/06/2014       fcb         151632 - Added refreshParentB for Risk Relation screen.
// 05/09/2014       xnie        154169 - Modified deleteRiskRelation() to correct two problems below
//                                       a. For new added TEMP records which have same parent risk id, when user deletes
//                                          one record, system doesn't show user 'All of the relation records to the
//                                          same risk and policy will be deleted' warning message and not all of those
//                                          records gets deleted. (Example case one: Add two same new type/parent risk
//                                          id and then delete one. Example case two: Add one new risk relation which
//                                          parent risk is a slot risk with multiple occupants and then delete one.
//                                       b. For TEMP risk relations which are from same official record, when user
//                                          deletes one record, system doesn't delete all of them.
// 06/06/2014       adeng       152052 - Modified handleOnGetProductCoverageCode() to use function
//                                       setObjectValue(objid, objectValue, fireOnchange) to set all fields value
//                                       and use the fireEvent("onChange") to instead of the fireAjax function.
// 01/17/2014       kxiang      155699 - Modified handleOnGetProductCoverageCode() to resolve problems below
//                                       a.when no productCoverageCode found,set it to "";
//                                       b.set original value for retro date,when wrong message pops up.
//                                       c.when change practiceStateCode/niRiskTypeCode and niRetroDate is not empty,
//                                       call sendAJAXRequest twice.
//                                       Modified handleOnChange() to resolve problems below
//                                       a.sendAJAXRequest will not be called until both niRiskTypeCode and
//                                       practiceStateCode are filled.
//                                       b.when RetroDate is not changed,set isChangeNiRetroDate to "N".
//                                       c.if setOrigNiRetroDate is "Y", after call sendAJAXRequest,set it to "N".
// 10/09/2014       wdang       156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 11/25/2014       kxiang      158853 - a. Modified functions which called commonHandleOnGetInitialValues.
//                                       b. Added handlePostAddRow to set href to grid risk relation.
// 12/30/2014       awu         157105 - Modified deleteRiskRelation to call beginDeleteMultipleRow and endDeleteMultipleRow.
// 03/20/2015       wdang       161448 - a. Added field location when inserting record to support location risk.
//                                       b. Added getLocationPropertyId() to support entity mini popup window for location risk.
// 09/18/2015       lzhang      165941 - Modify sendAJAXRequest: add screenModeCode to URL
// 03/10/2017       wrong       180675 - 1) Added invokeWorkflow() case for new UI tab style.
//                                       2) Modified code to open div popups in primary page in new UI tab style.
// 05/23/2017       lzhang      185079 - pass parameter when call getParentWindow()
// 07/04/2017       wrong       168374 - 1) Modified sendAjaxRequest() to add parameter pcfRiskCountyCode and
//                                          pcfRiskClassCode for getIntitialValuesForAddPolInsRiskRelation case.
//                                       2) Modified handleOnChange() to post ajax request for changing lov values
//                                          when change risk county or risk specialty fields.
//                                       3) Modified postAjaxRefresh() to add logic to display pcf territory and
//                                          pcf class field value.
//                                       4) Overwrite commonHandleOnGetIntitialValues() to add logic to load
//                                          isFundState field.
// 01/16/2018       eyin      190859 - Remove isCallBackAutoSaveForFrameB();
// 11/02/2018       clm       195889 -  Grid replacement using  getParentWindow
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (isExeInvokeWorkFlow()) {
        invokeWorkflow();
    }

    // change the title if it is to view reverse relation
    if (getObjectValue("isReverse") == "true") {
        if (getObject("pageTitleForpageHeader")) {
            getObject("pageTitleForpageHeader").innerText = getMessage("pm.reverseRiskRelation.title");
        }
    }
}

function maintainRiskRelationListGrid_selectRow(id) {
    $.when(dti.oasis.grid.getLoadingPromise("maintainRiskRelationListGrid")).then(function () {
        var selectedDataGrid = getXMLDataForGridName("maintainRiskRelationListGrid");
        var annualPremium = selectedDataGrid.recordset("CISANNUALPREMIUMEDITABLE").value;
        //103806 for non-base company insured will skip this process.
        //and will not send this AJAX request.
        if (annualPremium == "X" &&
                !(maintainRiskRelationListGrid1.recordset("CCOMPANYINSURED").value == "CI" &&
                        getObjectValue("isCompanyInsuredStr") == 'N')) { // first select the row
            sendAJAXRequest("isRatingBasisEditable");
        }

        // keep original ni retro date value, it will be used when change niRetroDate failed.
        setInputFormField("origNiRetroDate", getObjectValue("niRetroDate"));
    });
}
//-----------------------------------------------------------------------------
// Instruct the baseOnRowSelected to exec the processFieldDeps and pageEntitlements after _selectRow.
//-----------------------------------------------------------------------------
function isFieldDepsAndPageEntitlementsAfter_selectRow(gridId) {
    return true;
}

function maintainRiskRelationListGrid_setInitialValues() {
    // Ajax call to get initial values
    var addInsured = getObjectValue("addInsured");
    if (addInsured && addInsured == "policy") {
        sendAJAXRequest("getInitialValuesForAddPolInsRiskRelation");
    }
    else if (addInsured && addInsured == "company") {
        sendAJAXRequest("getInitialValuesForAddCompInsRiskRelation");
    }
    else if (addInsured && addInsured == "non") {
        sendAJAXRequest("getInitialValuesForAddNonInsRiskRelation");
    } else if (addInsured && addInsured == "nonBaseCompany") {
        sendAJAXRequest("getInitialValuesForAddNonBaseCompanyInsured");       
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainRiskRelation.do?" + commonGetMenuQueryString();
    if (process != "isRatingBasisEditable" && process != "isAnnualPremiumEditable" &&
        process != "getProductCoverageCode" && process!= "getInitialValuesForAddNonBaseCompanyInsured") {
        url += "&process=getInitialValuesForAddRiskRelation" +
               "&currentRiskType=" + escape(getObjectValue("currentRiskTypeCode")) +
               "&addNiCoverageB=" + getObjectValue("addNiCoverageB") +
               "&origRiskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate");
    }
    else {
        url += "&process=" + process;
    }
    url += "&riskId=" + policyHeader.riskHeader.riskId;

    var riskCountyCode = getObjectValue("riskCountyCode");
    var transEffectiveFromDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
    if (isDefined(window.frameElement)) {
        var parentWindow = getParentWindow();
        if (parentWindow.hasXMLDataForGridName("riskListGrid")) {
            riskCountyCode = parentWindow.getXMLDataForGridName("riskListGrid").recordset("CRISKCOUNTY").value;
        }
    }
    switch (process) {
        case 'isRatingBasisEditable':
            url += "&addNiCoverageB=" + maintainRiskRelationListGrid1.recordset("CADDNICOVERAGEB").value +
                   "&niRiskTypeCode=" + escape(maintainRiskRelationListGrid1.recordset("CNIRISKTYPECODE").value) +
                   "&currentRiskType=" + escape(getObjectValue("currentRiskTypeCode")) +
                   "&screenModeCode=" + escape(getObjectValue("screenModeCode")) +
                   "&transEffectiveFromDate=" + transEffectiveFromDate +
                   "&riskClassCode=" + maintainRiskRelationListGrid1.recordset("CRISKCLASSCODE").value +
                   "&recordModeCode=" + maintainRiskRelationListGrid1.recordset("CRECORDMODECODE").value +
                   "&riskEffectiveFromDate=" + maintainRiskRelationListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value +
                   "&riskEffectiveToDate=" + maintainRiskRelationListGrid1.recordset("CRISKEFFECTIVETODATE").value +
                   "&riskRelEffectiveFromDate=" + maintainRiskRelationListGrid1.recordset("CRISKRELEFFECTIVEFROMDATE").value +
                   "&riskRelEffectiveToDate=" + maintainRiskRelationListGrid1.recordset("CRISKRELEFFECTIVETODATE").value +
                   "&officialRecordId=" + maintainRiskRelationListGrid1.recordset("COFFICIALRECORDID").value +
                   "&afterImageRecordB=" + maintainRiskRelationListGrid1.recordset("CAFTERIMAGERECORDB").value +
                   "&companyInsured=" + maintainRiskRelationListGrid1.recordset("CCOMPANYINSURED").value +
                   "&riskProcessCode=" + maintainRiskRelationListGrid1.recordset("CRISKPROCESSCODE").value +
                   "&overrideStatsB=" + maintainRiskRelationListGrid1.recordset("COVERRIDESTATSB").value +
                   "&riskRelationStatus=" + maintainRiskRelationListGrid1.recordset("CRISKRELATIONSTATUS").value;
            break;
        case 'getInitialValuesForAddPolInsRiskRelation':
            url += "&addCode=" + getObjectValue("genericCodeForAdd") +
                   "&entityId=" + getObjectValue("entityIdForAdd") +
                   "&location=" + getObjectValue("location") +
                   "&riskTypeCode=" + escape(getObjectValue("riskTypeCodeForAdd")) +
                   "&riskBaseRecordId=" + getObjectValue("riskBaseRecordIdForAdd") +
                   "&riskClassCode=" + getObjectValue("riskClassCodeForAdd") +
                   "&countyCodeUsedToRate="+ getObjectValue("countyCodeUsedToRateForAdd") +
                   "&practiceStateCode=" + getObjectValue("practiceStateCodeForAdd") +
                   "&riskRelationTypeCode=" + getObjectValue("riskRelTypeCodeForAdd") +
                   "&toRateB=" + trim(getObjectValue("riskRelRateForAdd")) +
                   "&riskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate") +
                   "&pcfRiskCountyCode=" + getObjectValue("pcfRiskCountyCodeForAdd") +
                   "&pcfRiskClassCode=" + getObjectValue("pcfRiskClassCodeForAdd") +
                   "&companyInsured=PI";
            break;
        case 'getInitialValuesForAddCompInsRiskRelation':
            url += "&policyId=" + getObjectValue("policyIdForAdd") +
                   "&sourceRecordId=" + getObjectValue("sourceRecordIdForAdd") +
                   "&entityId=" + getObjectValue("policyHolderNameEntityId") +
                   "&companyInsured=CI";
                    var multiRiskRelation = getObjectValue("multiRiskRelation");
                    if (multiRiskRelation == "Y") {
                        url += "&riskRelationTypeCode=" + getObjectValue("riskRelTypeCodeForAdd") +
                               "&toRateB=" + trim(getObjectValue("riskRelRateForAdd")) +
                               "&multiRiskEntityId=" + getObjectValue("multiRiskEntityId") +
                               "&multiRiskRelation=" + multiRiskRelation;
                    }
            break;
        case 'getInitialValuesForAddNonInsRiskRelation':
            url += "&entityId=" + getObjectValue("policyHolderNameEntityId") +
                   "&riskCountyCode=" + riskCountyCode +
                   "&transEffectiveFromDate=" + transEffectiveFromDate +
                   "&riskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate") +
                   "&companyInsured=NI";
            break;
        case 'isAnnualPremiumEditable':
            url += "&addNiCoverageB=" + maintainRiskRelationListGrid1.recordset("CADDNICOVERAGEB").value +
                   "&riskTypeCode=" + escape(maintainRiskRelationListGrid1.recordset("CRISKTYPECODE").value) +
                   "&niRiskTypeCode=" + escape(maintainRiskRelationListGrid1.recordset("CNIRISKTYPECODE").value) +
                   "&currentRiskType=" + escape(getObjectValue("currentRiskTypeCode")) +
                   "&transEffectiveFromDate=" + transEffectiveFromDate +
                   "&riskClassCode=" + getObjectValue("riskClassCode") +
                   "&riskRelationTypeCode=" + getObjectValue("riskRelationTypeCode");
            break;
        case 'getProductCoverageCode':
            url += "&niRiskTypeCode=" + escape(getObjectValue("niRiskTypeCode")) +
                   "&practiceStateCode=" + getObjectValue("practiceStateCode") +
                   "&policyId=" + getObjectValue("policyId") +
                   "&niRetroDate=" + getObjectValue("niRetroDate") +
                   "&transEffectiveFromDate=" + transEffectiveFromDate +
                   "&entityId=" + maintainRiskRelationListGrid1.recordset("CENTITYID").value;
            break;
        case 'getInitialValuesForAddNonBaseCompanyInsured':
            url += "&entityId=" + getObjectValue("policyHolderNameEntityId") +
                   "&transEffectiveFromDate=" + transEffectiveFromDate +
                   "&origRiskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate") +
                   "&origPracticeStateCode=" + getObjectValue("origPracticeStateCode") +
                   "&riskCountyCode=" + riskCountyCode +
                   "&companyInsured=CI";
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnIsRatingBasisEditable(ajax) {
    handleOnIsAnnualPremiumEditable(ajax);
}

function handleOnIsAnnualPremiumEditable(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    // set isAnnualPremiumEditable directly (no need to set other fields.)
                    if (isFieldExistsInRecordset(selectedDataGrid.recordset, "CISANNUALPREMIUMEDITABLE")) {
                        var isAnnualPremiumEditable = oValueList[0]["ISANNUALPREMIUMEDITABLE"];
                        selectedDataGrid.recordset("CISANNUALPREMIUMEDITABLE").value = isAnnualPremiumEditable;
                        if (selectedDataGrid.recordset("EDIT_IND").value == 'Y' && isAnnualPremiumEditable == 'N') {
                            if (hasObject('ratingBasis') && !isEmpty(getObjectValue('ratingBasis'))) {
                                getObject('ratingBasis').value = '';
                            }
                        }

                        functionExists = eval("window.pageEntitlements");
                        if (functionExists) {
                            pageEntitlements(true, "maintainRiskRelationListGrid");
                        }
                    }
                }
            }
        }
    }
}

function handleOnGetProductCoverageCode(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                // set niRetroDate back to original value
                var isChangeNiRetroDate = getObject("isChangeNiRetroDate");
                if (isChangeNiRetroDate && isChangeNiRetroDate.value == "Y") {
                    //the second time to pop up message at one click change or( when is "CM" and set valid Retro date empty).
                    if (hasObject("setOrigNiRetroDate") && getObjectValue("setOrigNiRetroDate") == "Y"
                            || isEmpty(getObjectValue("niRetroDate"))){
                        setObjectValue("productCoverageCode","", true);
                    }
                    setObjectValue("niRetroDate",getObjectValue("origNiRetroDate"));
                }
                else {
                    //when change practiceStateCode/niRiskTypeCode and niRetroDate is not empty.
                    if(!isEmpty(getObjectValue("niRetroDate"))){
                        setInputFormField("setOrigNiRetroDate","Y");
                        setObjectValue("niRetroDate",getObjectValue("origNiRetroDate"),true);
                    }
                    else{
                        setObjectValue("productCoverageCode","", true);
                    }
                }
                return;
            }


            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    // set productCoverageCode directly (no need to set other fields.)
                    if (isFieldExistsInRecordset(selectedDataGrid.recordset, "CPRODUCTCOVERAGECODE")) {
                        selectedDataGrid.recordset("CPRODUCTCOVERAGECODE").value = oValueList[0]["PRODUCTCOVERAGECODE"];
                        // MUST fire ajax mannually here to reload the Coverage Limit field (it depends on productCoverageCode).
                        //getObject("productCoverageCode").fireEvent("onChange");
                        var ajaxInfoField = null;
                        try {
                            ajaxInfoField = eval("ajaxInfoForproductCoverageCode");
                        }
                        catch(ex) {
                            ajaxInfoField = null;
                        }
                        if (ajaxInfoField != null) {
                            getObject("productCoverageCode").fireEvent("onChange");
                        }
                    }
                }
            }
        }
    }
}

function handleOnGetInitialValuesForAddPolInsRiskRelation(ajax) {
    commonHandleOnGetInitialValues(ajax, "RISKNAMEHREF");
}

function handleOnGetInitialValuesForAddCompInsRiskRelation(ajax) {
    commonHandleOnGetInitialValues(ajax, "RISKNAMEHREF");
}

function handleOnGetInitialValuesForAddNonInsRiskRelation(ajax) {
    commonHandleOnGetInitialValues(ajax, "RISKNAMEHREF");
}

function handleOnGetInitialValuesForAddNonBaseCompanyInsured(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

//-----------------------------------------------------------------------------
// Validatioins
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    var isFundState = getObjectValue("isFundState");
    //103806 for non-base company insured will not send this AJAX request.
    if (!(maintainRiskRelationListGrid1.recordset("CCOMPANYINSURED").value =="CI" && getObjectValue("isCompanyInsuredStr") == 'N')) {

        if (obj.name == "riskClassCode") { // specialty
            sendAJAXRequest("isAnnualPremiumEditable");
        }
        else if ((obj.name == "practiceStateCode" || obj.name == "niRiskTypeCode" || obj.name == "niRetroDate")) {
            if (obj.name == "niRetroDate") {
                setInputFormField("isChangeNiRetroDate", "Y");
            }
            else {
                setInputFormField("isChangeNiRetroDate", "N");
            }
            if(!(maintainRiskRelationListGrid1.recordset("CCOMPANYINSURED").value =="NI" && maintainRiskRelationListGrid1.recordset("CADDNICOVERAGEB").value =="N")) {
                if(!(isEmpty(escape(getObjectValue("niRiskTypeCode"))) || isEmpty(getObjectValue("practiceStateCode")))) {
                    sendAJAXRequest("getProductCoverageCode");
                }
            }
            //If setOrigNiRetroDate is "Y", after call sendAJAXRequest,set it to "N".
            if (hasObject("setOrigNiRetroDate") && getObjectValue("setOrigNiRetroDate") == "Y") {
                setInputFormField("setOrigNiRetroDate","N");
            }
        }
    }
    if (obj.name == "practiceStateCode") {
        var practiceStateCode = getObjectValue(obj.name);
        loadIsFundStateValue(practiceStateCode);
    }
    // Set default value for pcf risk county/class when county changes.
    if (obj.name == "countyCodeUsedToRate" && isFundState == 'Y') {
            var riskCounty = getObjectValue(obj.name);
            setDefaultValueForPcfRiskCounty(riskCounty, true);
    }
    if (obj.name == "riskClassCode" && isFundState == 'Y') {
        var riskClassCode = getObjectValue(obj.name);
        setDefaultValueForPcfRiskClass(riskClassCode, true);
    }
    return true;
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllRiskRelation";
            break;
        default:
            proceed = false;
    }
    return proceed;
}
//-----------------------------------------------------------------------------
// Make a mid-term OOS Endorsement and change the risk, when add PI relation, there will be two risk relations in relation page,
// we should use risk Id as anchorColumnName since these two relation have the same risk relation id.
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADD_POL_INSURED':
            var url = getAppPath() + "/riskmgr/selectPolInsRiskRelation.do?" +
                      commonGetMenuQueryString() + "&riskId=" + policyHeader.riskHeader.riskId;
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'ADD_COMP_INSURED':
            setInputFormField("policyHolderNameEntityId", 0);
            setInputFormField("policyHolderName", "");
            if (getObjectValue("isCompanyInsuredStr") == 'N') {
                setInputFormField("addInsured", "nonBaseCompany");
                openEntitySelectWinFullName("policyHolderNameEntityId", "policyHolderName", "handleFindNonBaseCompanyInsuredClientDone()");
            }
            else {
                setInputFormField("addInsured", "company");
                openEntitySelectWinFullName("policyHolderNameEntityId", "policyHolderName", "handleFindClient()");
            }
            break;
        case 'ADD_NON_INSURED':
            setInputFormField("addInsured", "non");
            setInputFormField("policyHolderNameEntityId", 0);
            setInputFormField("policyHolderName", "");
            openEntitySelectWinFullName("policyHolderNameEntityId", "policyHolderName", "handleFindClient()");
            break;
        case 'CANCEL':
            closeWindow(function () {
                if (isNeedToRefreshParentB()) {
                    getParentWindow().refreshPage(true);
                }
            });
            break;
        case 'DELETE_RELATION':
            deleteRiskRelation(); 
            break;
        case 'CANCELLATION':
            var riskRelationId = maintainRiskRelationListGrid1.recordset("CRISKRELATIONID").value;
            var effFromDate = maintainRiskRelationListGrid1.recordset("CRISKRELEFFECTIVEFROMDATE").value;
            var effToDate = maintainRiskRelationListGrid1.recordset("CRISKRELEFFECTIVETODATE").value;
            performCancellation("RISK RELATION", riskRelationId, effFromDate, effToDate);
            break;
        case 'REINSTATE':
            var riskRelationId = maintainRiskRelationListGrid1.recordset("CRISKRELATIONID").value;
            performReinstate("RISK RELATION", riskRelationId);
            break;
        case 'REVERSE_RELATION':
            var parentWindow = getParentWindow();
            var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                + commonGetMenuQueryString() + "&process=loadAllRiskRelation"
                + "&riskId=" + policyHeader.riskHeader.riskId
                + "&riskBaseRecordId=" + parentWindow.getObjectValue("riskBaseRecordId")
                + "&currentRiskTypeCode=" + escape(parentWindow.getObjectValue("riskTypeCode"))
                + "&riskEffectiveFromDate=" + parentWindow.getObjectValue("riskEffectiveFromDate")
                + "&origRiskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate")
                + "&riskEffectiveToDate=" + parentWindow.getObjectValue("riskEffectiveToDate")
                + "&riskCountyCode=" + getObjectValue("riskCountyCode")
                + "&reverse=Y";
            var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
            break;
    }
}

function isNeedToRefreshParentB(){
    var refreshParentB = "N";
    if (!isEmpty(getObjectValue("refreshParentB"))) {
        refreshParentB = getObjectValue("refreshParentB");
    }

    if (!isPageDataChanged() && getObjectValue("isReverse") == "false" && refreshParentB == "Y") {
        return true;
    }else{
        return false;
    }
}

function addPolInsRisks(oPolInsRiskList, showMessage) {
    if (showMessage == null) {
        showMessage = true;
    }

    setInputFormField("addInsured", "policy");

    var riskSize = oPolInsRiskList.length;
    for (var i = 0; i < riskSize; i++) {
        var risk = oPolInsRiskList[i];
        setInputFormField("genericCodeForAdd", risk.GENERICCODE);
        setInputFormField("entityIdForAdd", risk.ENTITYID);
        setInputFormField("location", risk.LOCATION);
        setInputFormField("riskBaseRecordIdForAdd", risk.RISKBASERECORDID);
        setInputFormField("riskTypeCodeForAdd", risk.RISKCODE);
        setInputFormField("riskClassCodeForAdd", risk.RISKCLASSCODE);
        setInputFormField("countyCodeUsedToRateForAdd", risk.COUNTYCODEUSEDTORATE);
        setInputFormField("practiceStateCodeForAdd", risk.PRACTICESTATECODE);
        setInputFormField("riskRelTypeCodeForAdd", risk.RISKRELATIONTYPECODE);
        setInputFormField("riskRelRateForAdd", risk.RISKRELATIONRATE);
        setInputFormField("pcfRiskCountyCodeForAdd", risk.PCFRISKCOUNTYCODE);
        setInputFormField("pcfRiskClassCodeForAdd", risk.PCFRISKCLASSCODE);
        addPolInsRisk();
    }
}

function addPolInsRisk() {
    // insert one empty record
    commonAddRow("maintainRiskRelationListGrid");
}

function handleFindClient() {
    var addInsured = getObjectValue("addInsured");
    if (addInsured && addInsured == "company") {
        var multiRiskRelation = getObjectValue("multiRiskRelation");
        if (multiRiskRelation != "Y") {
            var url = getAppPath() + "/riskmgr/selectCompInsRiskRelation.do?" +
                      commonGetMenuQueryString() + "&entityId=" + getObjectValue("policyHolderNameEntityId");
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
        }
        else {
            var url = getAppPath() + "/riskmgr/selectPolInsRiskRelation.do?" +
                      commonGetMenuQueryString() + "&riskEntityId=" + getObjectValue("policyHolderNameEntityId") +
                      "&multiRiskRelation=" + multiRiskRelation;
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
        }
    }
    else if (addInsured && addInsured == "non") {
        // insert one empty record
        commonAddRow("maintainRiskRelationListGrid");
    }
}

function addCompInsRisks(oCompInsRiskList, showMessage, multiRiskB) {
    if (showMessage == null) {
        showMessage = true;
    }

    setInputFormField("addInsured", "company");

    var riskSize = oCompInsRiskList.length;
    for (var i = 0; i < riskSize; i++) {
        var risk = oCompInsRiskList[i];
        setInputFormField("externalIdForAdd", risk.EXTERNALID);
        setInputFormField("policyIdForAdd", risk.POLICYID);
        setInputFormField("sourceRecordIdForAdd", risk.SOURCERECORDID);
        if (multiRiskB) {
            setInputFormField("riskRelTypeCodeForAdd", risk.RISKRELATIONTYPECODE);
            setInputFormField("riskRelRateForAdd", risk.RISKRELATIONRATE);
            setInputFormField("multiRiskEntityId", risk.MULTIRISKENTITYID);
        }
        addCompInsRisk();
    }
}

function addCompInsRisk() {
    // insert one empty record
    commonAddRow("maintainRiskRelationListGrid");
}

function deleteRiskRelation() {
    var officialRecordId = maintainRiskRelationListGrid1.recordset("COFFICIALRECORDID").value;
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    var riskParentId = selectedDataGrid.recordset("CRISKPARENTID").value;
    var id = selectedDataGrid.recordset("ID").value;
    var deleteConfirmB = true;

    // New added TEMP risk relation
    if (isEmpty(officialRecordId)) {
        var findIt = false;
        first(selectedDataGrid);
        while (!selectedDataGrid.recordset.eof) {
            // Find multiple records which have same risk parent id
            if (selectedDataGrid.recordset("CRISKPARENTID").value == riskParentId &&
                selectedDataGrid.recordset("CRECORDMODECODE").value != "OFFICIAL" &&
                selectedDataGrid.recordset("COFFICIALRECORDID").value == "" &&
                selectedDataGrid.recordset("ID").value != id) {
                findIt = true;
                break;
            }
            next(selectedDataGrid);
        }
        first(selectedDataGrid);

        if (findIt) {
            if (confirm(getMessage("pm.maintainRiskRelation.delete.changedRecord.warning"))) {
                // delete all of new TEMP risk relationships which have same risk parent id
                beginDeleteMultipleRow("maintainRiskRelationListGrid");
                first(selectedDataGrid);
                while (!selectedDataGrid.recordset.eof) {
                    if (selectedDataGrid.recordset("CRISKPARENTID").value == riskParentId &&
                        selectedDataGrid.recordset("CRECORDMODECODE").value != "OFFICIAL" &&
                        selectedDataGrid.recordset("COFFICIALRECORDID").value == "") {
                        setSelectedRow("maintainRiskRelationListGrid", selectedDataGrid.recordset("ID").value);
                        maintainRiskRelationListGrid_deleterow();
                    }
                    next(selectedDataGrid);
                }
                first(selectedDataGrid);
                endDeleteMultipleRow("maintainRiskRelationListGrid");
            }
        }
        else {
            commonDeleteRow("maintainRiskRelationListGrid");
        }
    }
    // TEMP risk relation which is from a official record
    else {
        first(selectedDataGrid);
        beginDeleteMultipleRow("maintainRiskRelationListGrid");
        while (!selectedDataGrid.recordset.eof) {
            // find the TEMP record which is from same official record and delete it
            if (selectedDataGrid.recordset("COFFICIALRECORDID").value == officialRecordId &&
                selectedDataGrid.recordset("CRECORDMODECODE").value != "OFFICIAL") {
                setSelectedRow("maintainRiskRelationListGrid", selectedDataGrid.recordset("ID").value);
                if (deleteConfirmB) {
                    deleteConfirmB = false;
                    if (confirm(getMessage("pm.common.selected.record.delete.confirm"))) {
                        maintainRiskRelationListGrid_deleterow();
                        next(selectedDataGrid);
                    }
                    else {
                        break;
                    }
                }
                else {
                    maintainRiskRelationListGrid_deleterow();
                    next(selectedDataGrid);
                }
            }
            else {
                next(selectedDataGrid);
            }
        }
        first(selectedDataGrid);
        endDeleteMultipleRow("maintainRiskRelationListGrid");
    }
}

function openSelectPolInsRiskRelation(riskEntityId, multiRiskRelation, policyList) {
    var url = getAppPath() + "/riskmgr/selectPolInsRiskRelation.do?" +
                  commonGetMenuQueryString() + "&riskEntityId=" + riskEntityId +
                  "&multiRiskRelation=" + multiRiskRelation +
                  "&policyList=" + policyList;
    var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function handleFindNonBaseCompanyInsuredClientDone() {
    commonAddRow("maintainRiskRelationListGrid");
}

function postAjaxRefresh(field, AjaxUrls) {
    if (AjaxUrls.indexOf('fieldId=territory')>0) {
        getObject("territoryLOVLABELSPAN").innerText = getObject("territory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=premiumClass')>0) {
        getObject("premiumClassLOVLABELSPAN").innerText = getObject("premiumClass").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=pcfTerritory')>0) {
        getObject("pcfTerritoryLOVLABELSPAN").innerText = getObject("pcfTerritory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=pcfClass')>0) {
        getObject("pcfClassLOVLABELSPAN").innerText = getObject("pcfClass").innerText;
    }
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "maintainRiskRelationListGrid") {
        var xmlData = getXMLDataForGridName("maintainRiskRelationListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var riskNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CRISKNAME") {
                riskNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + riskNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("RISKNAMEHREF"))) {
                    href = "javascript:handleOnGridHref('maintainRiskRelationListGrid', '"
                            + getObjectValue("RISKNAMEHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    if (isFieldExistsInRecordset(maintainRiskRelationListGrid1.recordset, "CLOCATION")) {
        return maintainRiskRelationListGrid1.recordset("CLOCATION").value;
    } else {
        return null;
    }
}

function commonHandleOnGetInitialValues(ajax, nameHref) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
                var nameHrefValue = "";
                if (!isEmpty(oValueList[0][nameHref])) {
                    nameHrefValue = replace(oValueList[0][nameHref], "\'", "\\\'");
                }
                setInputFormField(nameHref, nameHrefValue);
            }
            loadIsFundStateValue(oValueList[0]["PRACTICESTATECODE"]);
        }
    }
}