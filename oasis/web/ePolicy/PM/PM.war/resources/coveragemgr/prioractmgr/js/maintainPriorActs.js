//-----------------------------------------------------------------------------
// Javascript file for maintainPriorActs.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   June 10, 2011
// Author: wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/10/2011       wfu       103799 - Add logic to handle copy prior act stats.
// 08/12/2011       ryzhao    123687 - Add postAjaxRefresh().
// 10/09/2014       wdang     156038 - 1. Replaced getObject('riskId') with policyHeader.riskHeader.riskId. 
//                                     2. Replaced getObject('coverageId') with policyHeader.coverageHeader.coverageId.
// 12/30/2014       jyang     157750 - Modified handleOnButtonClick() to encode currentRiskTypeCode before append it to
//                                     URL for COPY_STATS operation.
// 01/08/2015       awu       157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                     to wrap the multiple rows deleting.
// 08/10/2015       tzeng     164420 - 1. Modified submitSave() to add coverageBaseRecordId for break validation.
//                                     2. Modified handleOnChange() to ensure that only overlapping coverage removed
//                                     when change prior risk state and all temp components of deleted coverage removed.
// 03/13/2017       eyin      180675 - 1. Initialized the parameters 'subFrameId, autoSaveResultType', added condition
//                                        'isTabStyle()'. added method 'lookupEntity' for UI change.
// 05/23/2017       lzhang    185079 - pass parameter when call getParentWindow()
//-----------------------------------------------------------------------------
var origPrefix = "CORIG";
var subFrameId = getParentWindow(true).subFrameId;

function handleOnLoad() {
    // If just after copy process, user needs to save data
    if (getObjectValue("isCopyActsStats") == "Y") {
        isChanged = true;
        setInputFormField("isCopyActsStats", '');
    }
    // If validation does not pass, user still needs to save data
    if (hasErrorMessages) {
        isChanged = true;
    }
}

function getPARiskGridId() {
    return "riskListGrid";
}

function getPACovgGridId() {
    return "coverageListGrid";
}

function getPAComponentGridId() {
    return "componentListGrid";
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'RISK_ADD':
            addPriorActRisk();
            break;
        case 'RISK_DELETE':
            deletePriorActRisk();
            break;
        case 'COVG_ADD':
            addPriorActCoverage();
            break;
        case 'COVG_DELETE':
            deletePriorActCoverage();
            break;
        case 'ADDCOMP':
            if (!isGridDataChanged(getPACovgGridId())) {
                var currentCovgDataGrid = getXMLDataForGridName(getPACovgGridId());
                var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
                var coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
                var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
                var riskId = currentCovgDataGrid.recordset("CRISKBASERECORDID").value;
                var coverageId = currentCovgDataGrid.recordset('CCOVERAGEID').value;

                var url = getAppPath() + "/componentmgr/selectComponent.do?"
                    + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                    + "&coverageBaseRecordId=" + coverageBaseRecordId
                    + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
                    + "&riskId=" + riskId + "&coverageId=" + coverageId;
                if(isTabStyle()){
                    url += "&subFrameId=" + subFrameId;
                }

                var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            }
            else {
                //if coverage data changed show warning message
                handleError(getMessage("pm.maintainPriorActs.saveCoverageFirst.warning"));
            }
            break;
        case 'DELETECOMP':
            var gridId = getPAComponentGridId();
            commonDeleteRow(gridId);
            break;
        case 'SAVE':
            if (isPageDataChanged()) {
				autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
                submitSave();
            }else{
                autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
            }
			syncResultToParent(autoSaveResultType);
            break;
        case 'COPY_STATS':
            if (!commonIsOkToChangePages()) {
                return;
            }
            var parentWindow = window.frameElement.document.parentWindow;
            var url = getAppPath() + "/riskmgr/selectCompInsRiskRelation.do?"
                    + commonGetMenuQueryString()
                    + "&riskId=" + parentWindow.policyHeader.riskHeader.riskId
                    + "&termEffectiveFromDate=" + getObjectValue("coverageRetroDate")
                    + "&termEffectiveToDate=" + parentWindow.coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value
                    + "&policyCycleCode=" + policyHeader.policyCycleCode
                    + "&transEffectiveFromDate=" + policyHeader.lastTransactionInfo.transEffectiveFromDate
                    + "&currentRiskTypeCode=" + encodeURIComponent(parentWindow.coverageListGrid1.recordset("CRISKTYPECODE").value)
                    + "&isCopyActsStats=Y";
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
    }
}

function submitSave() {
    var parentWindow = getParentWindow(); //window.frameElement.document.parentWindow;
    alternateGrid_update('riskListGrid');
    alternateGrid_update('coverageListGrid');
    alternateGrid_update('componentListGrid');
    setInputFormField("processCode", 'SAVE');
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    setInputFormField("coverageId", policyHeader.coverageHeader.coverageId);
    setInputFormField("coverageBaseRecordId", parentWindow.coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value);
    document.forms[0].process.value = "saveAllPriorAct";
    showProcessingDivPopup();
    submitFirstForm();
}

function addPriorActRisk() {
    setInputFormField("seledEntityId", 0);
    setInputFormField("seledEntityName", "");
    lookupEntity("CARRIER", policyHeader.termEffectiveFromDate,
        'seledEntityId', 'seledEntityName', 'handleFindClient()', subFrameId);
}

/*
** Overwrite lookupEntity with additional field in URL
 */
function lookupEntity(entityClassCode, effectiveFromDate, entityIdFieldName, entityNameFieldName, eventHandler, iFrameId) {
    entitylookupEntityIdFieldName = entityIdFieldName;
    entitylookupEntityNameFieldName = entityNameFieldName;
    entityLookupEventHandler = eventHandler;

    var path = getAppPath() +
            "/entitymgr/lookupEntity.do?entityClassCode=" + entityClassCode +
            "&effectiveFromDate=" + effectiveFromDate;

    if(isTabStyle()){
        path += "&subFrameId=" + iFrameId;
    }
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, null, null, "", "", "", "", "lookupEntity", false);
}

function deletePriorActRisk() {

    var xmlData = getXMLDataForGridName(getPARiskGridId());

    if (xmlData.recordset("UPDATE_IND").value == 'I') {
        commonDeleteRow(getPARiskGridId());
        // If no record exists, Copy Stats button should display.
        if (!isPageDataChanged() && isEmptyRecordset(getPARiskGridId().recordset)) {
            if(isTabStyle()){
                setInputFormField("isRiskAIGCopyStatAvailable", "Y");
                setInputFormField("isCopyStatAvailable", "N");
            }else{
                setInputFormField("isCopyStatAvailable", "Y");
                setInputFormField("isRiskAIGCopyStatAvailable", "N");
            }
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(false);
            }
        }
        return true;
    }

    var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
        + commonGetMenuQueryString() + "&process=validateForDelete";

    url = url + "&seledRiskId=" + xmlData.recordset("ID").value

    new AJAXRequest("get", url, '', handleOnValidationDone, false);

    if (!hasProcessedAjaxErrorMessages()) {
        commonDeleteRow(getPARiskGridId());
    }

    return !hasProcessedAjaxErrorMessages();

}

function deletePriorActCoverage() {
    commonDeleteRow(getPACovgGridId());
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleFindClient() {
    commonAddRow(getPARiskGridId());
    // If add a risk record, Copy Stats button should hide.
    setInputFormField("isCopyStatAvailable", "N");
    setInputFormField("isRiskAIGCopyStatAvailable", "N");
    var functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(false);
    }
}

//when date fields are changed, create Ajax request to validate the date fields
function handleOnChange(obj) {
    var riskXmlData = getXMLDataForGridName(getPARiskGridId());
    var covgXmlData = getXMLDataForGridName(getPACovgGridId());
    var compXmlData = getXMLDataForGridName(getPAComponentGridId());
    if (obj.name == 'coverageEffectiveFromDate' || obj.name == "coverageEffectiveToDate") {
        var nodeValue = obj.value;
        var xmlData = getXMLDataForGridName(getCurrentlySelectedGridId());
        var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
            + commonGetMenuQueryString() + "&process=validatePriorActCoverageDate";

        url = url + "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate")
            + "&coverageEffectiveToDate=" + getObjectValue("coverageEffectiveToDate")
            + "&coverageRetroDate=" + getObjectValue("coverageRetroDate")
            + "&coverageEffectiveDate=" + getObjectValue("coverageEffectiveDate")
            + "&coverageId=" + covgXmlData.recordset("ID").value
            + "&isToValidate=Y";

        new AJAXRequest("get", url, '', handleOnValidationDone, false);

        if (!hasProcessedAjaxErrorMessages()) {
            if (!isEmptyRecordset(compXmlData.recordset)) {
                var chgedfieldName = "COMPONENTEFFECTIVEFROMDATE";
                if (obj.name == 'coverageEffectiveToDate') {
                    chgedfieldName = "COMPONENTEFFECTIVETODATE";
                }
                eval(getPAComponentGridId() + "_updatenode('C" + chgedfieldName + "', '" + nodeValue + "')");
                eval(getPAComponentGridId() + "_updatenode('" + origPrefix + chgedfieldName + "', '" + nodeValue + "')");

                coverageListGrid_selectRow(covgXmlData.recordset("ID").value);
            }
            covgXmlData.recordset(origPrefix + obj.name.toUpperCase()).value = nodeValue;
        }
        else {
            covgXmlData.recordset("C" + obj.name.toUpperCase()).value
                = covgXmlData.recordset(origPrefix + obj.name.toUpperCase()).value;
        }
        return !hasProcessedAjaxErrorMessages();
    }
    else if (obj.name == 'componentEffectiveFromDate' || obj.name == "componentEffectiveToDate") {
        if (!commonValidateForm()) {
            obj.value = compXmlData.recordset(origPrefix + obj.name.toUpperCase()).value;
            return false;
        }

        var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
            + commonGetMenuQueryString() + "&process=validatePriorActComponentDate";

        url = url + "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate")
            + "&coverageEffectiveToDate=" + getObjectValue("coverageEffectiveToDate")
            + "&componentEffectiveFromDate=" + getObjectValue("componentEffectiveFromDate")
            + "&componentEffectiveToDate=" + getObjectValue("componentEffectiveToDate")
            + "&policyCovComponentId=" + compXmlData.recordset("ID").value
            + "&isToValidate=Y";

        new AJAXRequest("get", url, '', handleOnValidationDone, false);

        if (hasProcessedAjaxErrorMessages()) {
            obj.value = compXmlData.recordset(origPrefix + obj.name.toUpperCase()).value;
        }
        else {
            compXmlData.recordset(origPrefix + obj.name.toUpperCase()).value = obj.value;
        }

        return !hasProcessedAjaxErrorMessages();
    }
    else if (obj.name == 'practiceStateCode') {
        if (isEmptyRecordset(covgXmlData.recordset)) {
            riskXmlData.recordset(origPrefix + "PRACTICESTATECODE").value = obj.value;
            //set risk county code to ''
            riskXmlData.recordset("CRISKCOUNTYCODE").value = "";
        }
        else if (confirm(getMessage("pm.maintainPriorActs.confirm.deleteExistCovgComp"))) {
            //delete all coverage data
            if (!isEmptyRecordset(covgXmlData.recordset)) {
                first(covgXmlData);
                beginDeleteMultipleRow(getPACovgGridId());
                while (!covgXmlData.recordset.eof) {
                    if(new Date(riskXmlData.recordset("CRISKEFFECTIVEFROMDATE").value) <
                            new Date(covgXmlData.recordset("CCOVERAGEEFFECTIVETODATE").value)
                            && new Date(riskXmlData.recordset("CRISKEFFECTIVETODATE").value) >
                            new Date(covgXmlData.recordset("CCOVERAGEEFFECTIVEFROMDATE").value)
                            && covgXmlData.recordset("CRECORDMODECODE").value == "TEMP"){
                        setSelectedRow(getPACovgGridId(), covgXmlData.recordset("ID").value);
                        eval(getPACovgGridId() + "_deleterow();");
                        //delete all component data of coverage
                        var compXmlData = getXMLDataForGridName(getPAComponentGridId());
                        if (!isEmptyRecordset(compXmlData.recordset)) {
                            first(compXmlData);
                            beginDeleteMultipleRow(getPAComponentGridId());
                            while (!compXmlData.recordset.eof) {
                                if(((new Date(compXmlData.recordset("CCOMPONENTEFFECTIVEFROMDATE").value) <
                                        new Date(covgXmlData.recordset("CCOVERAGEEFFECTIVETODATE").value)
                                        && new Date(compXmlData.recordset("CCOMPONENTEFFECTIVETODATE").value) >
                                        new Date(covgXmlData.recordset("CCOVERAGEEFFECTIVEFROMDATE").value))
                                        || (new Date(compXmlData.recordset("CCOMPONENTEFFECTIVEFROMDATE").value) ==
                                        new Date(compXmlData.recordset("CCOMPONENTEFFECTIVETODATE").value)
                                        && new Date(compXmlData.recordset("CCOMPONENTEFFECTIVETODATE").value) ==
                                        new Date(covgXmlData.recordset("CCOVERAGEEFFECTIVEFROMDATE").value)))
                                        && compXmlData.recordset("CCOVERAGEBASERECORDID").value ==
                                        covgXmlData.recordset("CCOVERAGEBASERECORDID").value
                                        && covgXmlData.recordset("COFFICIALRECORDID").value == ""){
                                    setSelectedRow(getPAComponentGridId(), compXmlData.recordset("ID").value);
                                    eval(getPAComponentGridId() + "_deleterow();");
                                }
                                next(compXmlData);
                            }
                            endDeleteMultipleRow(getPAComponentGridId());
                            hideShowForm(getPAComponentGridId());
                        }
                    }
                    next(covgXmlData);
                }
                first(covgXmlData);
                endDeleteMultipleRow(getPACovgGridId());
                hideShowForm(getPACovgGridId());
            }

            riskXmlData.recordset(origPrefix + "PRACTICESTATECODE").value = obj.value;

            //set risk county code to ''
            riskXmlData.recordset("CRISKCOUNTYCODE").value = "";
        }
        else {
            obj.value = riskXmlData.recordset(origPrefix + "PRACTICESTATECODE").value;
            return false;
        }
    }


    return true;
}

//check ajax response to check if there is validation error
function handleOnValidationDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                return;
            }
        }
    }
}

function riskListGrid_setInitialValues() {
    var xmlData = getXMLDataForGridName(getPARiskGridId());
    xmlData.recordset("CENTITYID").value = getObjectValue("seledEntityId");
    xmlData.recordset("CRISKNAME").value = getObjectValue("seledEntityName");

    currentlySelectedGridId = getPARiskGridId();

    var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForPriorAct";

    url = url + "&coverageId=" + policyHeader.coverageHeader.coverageId
        + "&riskId=" + policyHeader.riskHeader.riskId
        + "&minimalNoseDate=" + getObjectValue("minimalNoseDate")
        + "&coverageRetroDate=" + getObjectValue("coverageRetroDate")
        + "&coverageEffectiveDate=" + getObjectValue("coverageEffectiveDate")
        + "&initialLevel=RISK";

    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function postAjaxRefresh(field, AjaxUrls) {
    if (AjaxUrls.indexOf('fieldId=territory')>0) {
        getObject("territoryLOVLABELSPAN").innerText = getObject("territory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=premiumClass')>0) {
        getObject("premiumClassLOVLABELSPAN").innerText = getObject("premiumClass").innerText;
    }
}

function addPriorActCoverage() {
    showProcessingDivPopup();

    var xmlData = getXMLDataForGridName(getPACovgGridId());
    var gridTxtXml = getPACovgGridId() + 'txtXML' ;

    currentlySelectedGridId = getPACovgGridId();

    alternateGrid_update(getPACovgGridId());


    var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForPriorAct";

    url = url + "&coverageId=" + policyHeader.coverageHeader.coverageId
        + "&riskId=" + policyHeader.riskHeader.riskId
        + "&coverageRetroDate=" + getObjectValue("coverageRetroDate")
        + "&coverageEffectiveDate=" + getObjectValue("coverageEffectiveDate")
        + "&commProductCoverageCode=" + getObjectValue("commProductCoverageCode")
        + "&practiceStateCode=" + getObjectValue("practiceStateCode")
        + "&initialLevel=COVERAGE";
    var data = gridTxtXml + "=" + escape(getObjectValue(gridTxtXml))

    new AJAXRequest("post", url, data, handleOnAddCoverage, false);
}

var addPACovgAjax;
function handleOnAddCoverage(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            addPACovgAjax = ajax;

            commonAddRow(getPACovgGridId());
        }
    }
}

function coverageListGrid_setInitialValues() {
    // do nothing here
    commonHandleOnGetInitialValues(addPACovgAjax);

    closeProcessingDivPopup();
}

function coverageListGrid_selectRow(id) {
    var covgXmlData = getXMLDataForGridName(getPACovgGridId());
    var compXmlData = getXMLDataForGridName(getPAComponentGridId());

    // Filter component data
    filterComponentData(covgXmlData);
    //getObject("coverageDetailDiv").style.display = "block";


    if (isEmptyRecordset(compXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(compXmlData));
        hideGridDetailDiv(getPAComponentGridId());
    }
    else {
        showNonEmptyTable(getTableForXMLData(compXmlData));
        // Do not need to reconnect to prevent limitCode LOV problem.
        //reconnectAllFields(document.forms[0]);
        //getObject("componentDetailDiv").style.display = "block";
    }

    return true;
}

//-----------------------------------------------------------------------------
// Delete component row when deleting coverage row. This function is called by commonDeleteRow() of common.js
//-----------------------------------------------------------------------------
function coverageListGrid_deleteDependentRow() {
    if (!getTableProperty(componentListGrid, "hasrows"))
        return;

    var covgId = coverageListGrid1.recordset("ID").value;

    first(componentListGrid1);
    beginDeleteMultipleRow(getPAComponentGridId());
    while (!componentListGrid1.recordset.eof) {

        if (componentListGrid1.recordset(0).value == "" || componentListGrid1.recordset("ID").value == '-9999') {
            break;
        }
        var compCovgId = componentListGrid1.recordset("CCOVERAGEID").value;
        if (compCovgId == covgId) {
            setSelectedRow(getPAComponentGridId(), componentListGrid1.recordset("ID").value);
            eval(getPAComponentGridId() + "_deleterow();");
        }
        next(componentListGrid1);
    }
    endDeleteMultipleRow(getPAComponentGridId());
    first(componentListGrid1);
    reconnectAllFields(document.forms[0]);
}

//-----------------------------------------------------------------------------
// Overwrite getParentGridId and getChildGridId
//-----------------------------------------------------------------------------
function getParentGridId() {
    if (validateRowId.indexOf(",") > 0 || validateFieldId.indexOf("coverage") != -1 || validateFieldId.indexOf("component") != -1)
        return getPACovgGridId();
    else
        return getPARiskGridId();
}

function getChildGridId() {
    if (validateRowId.indexOf(",") > 0 || validateFieldId.indexOf("coverage") != -1 || validateFieldId.indexOf("component") != -1)
        return getPAComponentGridId();
    else
        return getPARiskGridId();
}

//-----------------------------------------------------------------------------
// Receive values from Select Policy page and generate copy process.
//-----------------------------------------------------------------------------
function addCompInsRisks(oCompInsRiskList, showMessage, multiRiskB) {
    if (showMessage == null) {
        showMessage = true;
    }

    var cfPolicyId = "";
    var cfRiskBaseId = "";
    var riskSize = oCompInsRiskList.length;
    for (var i = 0; i < riskSize; i++) {
        var risk = oCompInsRiskList[i];
        cfPolicyId += risk.POLICYID + ",";
        cfRiskBaseId += risk.SOURCERECORDID + ",";
    }
    if (!isEmpty(cfPolicyId) && !isEmpty(cfRiskBaseId)) {
        setInputFormField("cfPolicyId", cfPolicyId.substring(0, cfPolicyId.length-1));
        setInputFormField("cfRiskBaseId", cfRiskBaseId.substring(0, cfRiskBaseId.length-1));
        setInputFormField("isCopyActsStats", "Y");
        copyPriorActsStats();
    }
}

//-----------------------------------------------------------------------------
// Request the copy process.
//-----------------------------------------------------------------------------
function copyPriorActsStats() {
    var parentWindow = window.frameElement.document.parentWindow;
    setInputFormField("ctPolicyId", policyHeader.policyId);
    setInputFormField("ctRiskBaseId", parentWindow.getObjectValue("riskBaseRecordId"));
    setInputFormField("ctRiskTypeCode", parentWindow.coverageListGrid1.recordset("CRISKTYPECODE").value);
    setInputFormField("ctPracticeState", parentWindow.getObjectValue("practiceStateCode"));
    setInputFormField("ctCovBaseId", parentWindow.coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value);
    setInputFormField("ctProdCovCode", parentWindow.coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value);
    setInputFormField("ctRetroDate", parentWindow.coverageListGrid1.recordset("CRETRODATE").value);
    setInputFormField("ctEffToDate", parentWindow.coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value);
    setInputFormField("ctTransLogId", policyHeader.lastTransactionId);
    var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do";
    var process = "copyPriorActsStats";
    postAjaxSubmit(url, process, true, false, handleOnCopyStats, false);
}

function handleOnCopyStats(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            showProcessingDivPopup();
            loadAllPriorActs();
        }
    }
}

function loadAllPriorActs() {
    document.forms[0].process.value = "loadAllPriorAct";
    var parentWindow = window.frameElement.document.parentWindow;
    setInputFormField("isCopyActsStats","Y");
    setInputFormField("riskId", parentWindow.policyHeader.riskHeader.riskId);
    setInputFormField("coverageId", parentWindow.coverageListGrid1.recordset("ID").value);
    setInputFormField("riskBaseRecordId", parentWindow.getObjectValue("riskBaseRecordId"));
    setInputFormField("riskEffectiveFromDate", parentWindow.getObjectValue("riskEffectiveFromDate"));
    setInputFormField("riskEffectiveToDate", parentWindow.getObjectValue("riskEffectiveToDate"));
    setInputFormField("coverageBaseRecordId", parentWindow.coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value);
    setInputFormField("productCoverageCode", parentWindow.coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value);
    setInputFormField("retroDate", parentWindow.coverageListGrid1.recordset("CRETRODATE").value);
    setInputFormField("coverageBaseEffectiveFromDate",
            parentWindow.coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value);
    setInputFormField("coverageEffectiveFromDate",
            parentWindow.coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value);
    submitFirstForm();
}