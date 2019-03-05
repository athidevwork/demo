//-----------------------------------------------------------------------------
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/02/2014       Jyang      148771 - Modified addInsured process, added logic to do
//                                      pageEntitlement when coverageForm field is changed.
// 11/26/2015       Tzeng      165794 - Modified copyInsured to add newly fields.
// 03/10/2017       Wli        180675 - Used "getOpenCtxOfDivPopUp()" to call "openDivPopup".
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            commonOnSubmit('saveAllUnderlyingPolicy', false, false, false, true);
            break;
        case 'ADDCOMPINS':
            addInsured(true);
            break;
        case 'ADDNONINS':
            addInsured(false);
            break;
        case 'COPY':
            copyInsured();
    }
}
//-----------------------------------------------------------------------------
// When fields are changed, create Ajax request to get initial values
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if ((obj.name == 'policyFormCode' || obj.name == "covPartCoverageCode")) {
        if (getObjectValue("policyFormCode") != 'OCCURRENCE') {
            var url = getAppPath() + "/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do?"
                    + commonGetMenuQueryString() + "&process=getRetroDateForReset";

            url = url + "&policyFormCode=" + getObjectValue("policyFormCode")
                    + "&covPartCoverageCode=" + getObjectValue("covPartCoverageCode")
                    + "&effectiveFromDate=" + getObjectValue("effectiveFromDate")
                    + "&effectiveToDate=" + getObjectValue("effectiveToDate")
                    + "&policyUnderPolId=" + getObjectValue("policyUnderPolId")
                    + "&companyInsuredB=" + getObjectValue("companyInsuredB");

            new AJAXRequest("get", url, '', handleOnGetRetroDateResetInfo, false);
        }
        else {
            var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
            selectedDataGrid.recordset("CISRETRODATEAVAILABLE").value = "N";
            selectedDataGrid.recordset("CRETROACTIVEDATE").value = "";
        }
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, getCurrentlySelectedGridId());
        }
    }
    return true;
}

//-----------------------------------------------------------------------------
// Call back function for changing fields
//-----------------------------------------------------------------------------
function handleOnGetRetroDateResetInfo(ajax) {
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
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0], true);
                    if (getObjectValue("companyInsuredB") == "Y") {
                        selectedDataGrid.recordset("CISRETRODATEAVAILABLE").value = "N";
                    }
                    else {
                        selectedDataGrid.recordset("CISRETRODATEAVAILABLE").value = "Y";
                    }
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Add Company/Non Insured
//-----------------------------------------------------------------------------
function addInsured(isAddCompanyInsured) {
    setInputFormField("addOperation", "ADD");
    //add compnay insured
    if (isAddCompanyInsured) {
        setInputFormField("seledEntityId", 0);
        setInputFormField("seledEntityName", "");
        openEntitySelectWinFullName("seledEntityId", "seledEntityName", "handleFindClient()");
    }
    //add non-insured
    else {
        setInputFormField("queryParms", "");
        getInitialValuesForNewInsured();
    }
}
//-----------------------------------------------------------------------------
// Callback function, when active policy is selected in the select active policy page
//-----------------------------------------------------------------------------
function handleOnSelectPolicyDone(underPolicyId, underPolicyNo) {
    var queryParms = "&policyUnderPolId=" + underPolicyId
        + "&policyUnderPolNo=" + underPolicyNo
        + "&underIssCompEntId=" + getObjectValue("seledEntityId")
    setInputFormField("queryParms", queryParms);
    getInitialValuesForNewInsured();
}
//-----------------------------------------------------------------------------
// Copy Insured
//-----------------------------------------------------------------------------
function copyInsured() {
    setInputFormField("addOperation", "COPY");

    var queryParms = "&effectiveFromDate=" + getObjectValue("effectiveFromDate")
        + "&effectiveToDate=" + getObjectValue("effectiveToDate")
        + "&policyUnderPolId=" + getObjectValue("policyUnderPolId")
        + "&policyUnderPolNo=" + getObjectValue("policyUnderPolNo")
        + "&underPolicyTypeCode=" + getObjectValue("underPolicyTypeCode")
        + "&underIssCompEntId=" + getObjectValue("underIssCompEntId")
        + "&covPartCoverageCode=" + getObjectValue("covPartCoverageCode")
        + "&policyFormCode=" + getObjectValue("policyFormCode")
        + "&coverageLimitCode=" + getObjectValue("coverageLimitCode")
        + "&retroactiveDate=" + getObjectValue("retroactiveDate")
        + "&companyInsuredB=" + getObjectValue("companyInsuredB")
        + "&outputB=" + getObjectValue("outputB")
        + "&renewB=" + getObjectValue("renewB")
        + "&groupNo=" + getObjectValue("groupNo")
        + "&subGroupCode=" + getObjectValue("subGroupCode")
        + "&limitValue1=" + getObjectValue("limitValue1")
        + "&limitValue1Code=" + getObjectValue("limitValue1Code")
        + "&limitValue2=" + getObjectValue("limitValue2")
        + "&limitValue2Code=" + getObjectValue("limitValue2Code")
        + "&limitValue3=" + getObjectValue("limitValue3")
        + "&limitValue3Code=" + getObjectValue("limitValue3Code")

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
    var path = getAppPath() + "/policymgr/underlyingpolicymgr/selectActivePolicy.do?"
        + commonGetMenuQueryString() + "&process=loadAllActivePolicy"
        + "&entityId=" + entityId;
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true,
        null, null, "450", "400", "", "", "loadAllActivePolicy", false);
}

function getInitialValuesForNewInsured() {
    var url = getAppPath() + "/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForUnderlyingPolicy"
        + "&addOperation=" + getObjectValue("addOperation");

    url = url + getObjectValue("queryParms");

    new AJAXRequest("get", url, '', handleOnGetInitialValues, false);
}

function underlyingPolicyListGrid_setInitialValues() {
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
