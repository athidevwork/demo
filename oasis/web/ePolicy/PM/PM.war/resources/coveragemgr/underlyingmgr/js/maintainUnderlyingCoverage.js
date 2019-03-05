//-----------------------------------------------------------------------------
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/29/2018       wrong         188391 - Initial version.
//-----------------------------------------------------------------------------
var isAddCompanyInsured = true;
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            commonOnSubmit('saveAllUnderlyingCoverage', false, false, false, true);
            break;
        case 'ADDCOMPINS':
            isAddCompanyInsured = true;
            addInsured();
            break;
        case 'ADDNONINS':
            isAddCompanyInsured = false;
            addInsured();
            break;
        case 'COPY':
            copyInsured();
    }
}

//-----------------------------------------------------------------------------
// When fields are changed, create Ajax request to get initial values
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    if (obj.name == 'policyFormCode') {
        if (getObjectValue("policyFormCode") != 'OCCURRENCE') {
            selectedDataGrid.recordset("CISRETRODATEEDITABLE").value = "Y";
            selectedDataGrid.recordset("CUNDRETROACTIVEDATE").value = policyHeader.termEffectiveFromDate;
        }
        else {
            selectedDataGrid.recordset("CISRETRODATEEDITABLE").value = "N";
            selectedDataGrid.recordset("CUNDRETROACTIVEDATE").value = "";
        }
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, getCurrentlySelectedGridId());
        }
    }
    if (obj.name == "effectiveToDate") {
        var effectiveToDate = obj.value;
        var policyExpirationDate = policyHeader.policyExpirationDate;
        resetRenewIndicator(effectiveToDate, policyExpirationDate, "renewB", "underlyingCoverageListGrid");
    }
    return true;
}

//-----------------------------------------------------------------------------
// Update renew field value when change Expiration Date.
//-----------------------------------------------------------------------------
function resetRenewIndicator(effectiveToDate, expirationDate, indicator, currentGridId) {
    indicator = "C" + indicator.toUpperCase();
    var indicatorLOVLABEL = indicator + "LOVLABEL";
    if (isValueDate(effectiveToDate) && isValueDate(expirationDate)) {
        var xmlData = getXMLDataForGridName(currentGridId);
        if (getRealDate(effectiveToDate) < getRealDate(expirationDate)) {
            xmlData.recordset(indicator).value = "N";
            if (xmlData.recordset(indicatorLOVLABEL)) {
                xmlData.recordset(indicatorLOVLABEL).value = "No";
            }
        }
        else {
            xmlData.recordset(indicator).value = "Y";
            if (xmlData.recordset(indicatorLOVLABEL)) {
                xmlData.recordset(indicatorLOVLABEL).value = "Yes";
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Add Company/Non Insured
//-----------------------------------------------------------------------------
function addInsured() {
    setInputFormField("addOperation", "ADD");
    //add compnay insured
    setInputFormField("seledEntityId", 0);
    setInputFormField("seledEntityName", "");
    openEntitySelectWinFullName("seledEntityId", "seledEntityName", "handleFindClient()");
}
//-----------------------------------------------------------------------------
// Callback function, when active policy is selected in the select active policy page
//-----------------------------------------------------------------------------
function handleOnSelectPolicyDone(underPolicyId, underPolicyNo, sourceRecordFk, underEntityId, underEntityName) {
    if (!isAddCompanyInsured) {
        setInputFormField("underEntityName", underEntityName);
    }
    setInputFormField("underEntityId", underEntityId);
    var url = getAppPath() + "/coveragemgr/underlyingmgr/SelectUnderlyingRelation.do?"
            + commonGetMenuQueryString() + "&policyUnderPolId=" + underPolicyId
            + "&policyUnderPolNo=" + underPolicyNo
            + "&underEntityId="+ underEntityId;
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function handleOnSelectRelationDone(policyUnderPolId, underPolicyNo, underCoverageId) {
    var entityNameForNI = isDefined(getObjectValue("underEntityName")) ? getObjectValue("underEntityName") : '';
    var underRiskEntityId = isDefined(getObjectValue("underEntityId")) ? getObjectValue("underEntityId") : '';
    var queryParms = "&policyUnderCovgId=" + underCoverageId
            + "&policyUnderPolNo=" + underPolicyNo
            + "&policyUnderPolId=" + policyUnderPolId
            + "&addOperation=" + getObjectValue("addOperation")
            + "&entityNameForNI=" + entityNameForNI
            + "&underRiskEntityId=" + underRiskEntityId;
    setInputFormField("queryParms", queryParms);
    getInitialValuesForNewInsured();
}

//-----------------------------------------------------------------------------
// Copy Insured
//-----------------------------------------------------------------------------
function copyInsured() {


    var queryParms = "&effectiveFromDate=" + getObjectValue("effectiveFromDate")
            + "&effectiveToDate=" + getObjectValue("effectiveToDate")
            + "&policyUnderPolId=" + getObjectValue("policyUnderPolId")
            + "&policyUnderPolNo=" + getObjectValue("policyUnderPolNo")
            + "&underPolicyTypeCode=" + getObjectValue("underPolicyTypeCode")
            + "&underIssCompEntId=" + getObjectValue("underIssCompEntId")
            + "&practiceStateCode=" + getObjectValue("practiceStateCode")
            + "&underCoverageCode=" + getObjectValue("underCoverageCode")
            + "&policyFormCode=" + getObjectValue("policyFormCode")
            + "&coverageLimitCode=" + getObjectValue("coverageLimitCode")
            + "&underRiskType=" + getObjectValue("underRiskType")
            + "&undRetroactiveDate=" + getObjectValue("undRetroactiveDate")
            + "&companyInsuredB=" + getObjectValue("companyInsuredB")
            + "&underRiskEntityId=" + getObjectValue("underRiskEntityId")
            + "&coverageBaseId=" + getObjectValue("coverageBaseId")
            + "&policyUnderCovgBaseId=" + getObjectValue("policyUnderCovgBaseId")
            + "&outputB=" + getObjectValue("outputB")
            + "&renewB=" + getObjectValue("renewB")
            + "&riskName=" + underlyingCoverageListGrid1.recordset("CRISKNAME").value
            + "&riskType=" + underlyingCoverageListGrid1.recordset("CRISKTYPE").value
            + "&productCoverageCode=" + underlyingCoverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value
            + "&underRiskName=" + underlyingCoverageListGrid1.recordset("CUNDERRISKNAME").value
            + "&underRiskType=" + underlyingCoverageListGrid1.recordset("CUNDERRISKTYPE").value
            + "&underCovgType=" + underlyingCoverageListGrid1.recordset("CUNDERCOVGTYPE").value
            + "&underCoverageCode=" + underlyingCoverageListGrid1.recordset("CUNDERCOVERAGECODE").value
            + "&addOperation=COPY&recordModeCode=TEMP";

    setInputFormField("queryParms", queryParms);
    getInitialValuesForNewInsured();
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleFindClient() {
    openSelectPolicy(getObjectValue("seledEntityId"));
}

function openSelectPolicy(entityId) {
    if (isAddCompanyInsured) {
        var path = getAppPath() + "/policymgr/underlyingpolicymgr/selectActivePolicy.do?"
                + commonGetMenuQueryString() + "&process=loadAllActivePolicy"
                + "&entityId=" + entityId + "&isCoverageLevel=Y";
        var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true,
                null, null, "450", "400", "", "", "loadAllActivePolicy", false);
    }
    else {
        handleOnSelectPolicyDone("", "", "", entityId, getObjectValue("seledEntityName"));
    }

}

function getInitialValuesForNewInsured() {
    var url = getAppPath() + "/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForUnderlyingCoverage";

    url = url + getObjectValue("queryParms");

    new AJAXRequest("get", url, '', handleOnGetInitialValues, false);
}

function underlyingCoverageListGrid_setInitialValues() {
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
    if (selectedDataGrid != null) {
        setRecordsetByObject(selectedDataGrid, newInsuredData, true);
    }
}

var newInsuredData;
function handleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            //if there is any exception, return and do nothing
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            else {
                //if there is no exceptions, then add row and set initial values
                // parse and set initial values
                var oValueList = parseXML(data);
                if (oValueList.length > 0) {
                    newInsuredData = oValueList[0];
                    commonAddRow(getCurrentlySelectedGridId());
                }

            }
        }
    }
}
