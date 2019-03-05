// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/08/2015       awu       157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                     to wrap the multiple rows deleting.
// 10/09/2015       ssheng    166602 - Added one field riskBaseId in function handleOnSelectPolicyDone
//                                     and getInitialValuesForNewInsured.
// 03/13/2017       eyin      180675 - Added condition 'isExeInvokeWorkFlow()' when calling 'invokeWorkflow' in the
//                                     method 'handleOnLoad', used 'getOpenCtxOfDivPopUp()' to call 'openDivPopup'.
// 05/23/2017       lzhang    185079 - pass parameter when call getParentWindow()
// 01/16/2018       eyin      190859 - Remove isCallBackAutoSaveForFrameB();
// 09/12/2018       tyang     195751 - Remove risk type value of parameter underPolicyNo when call function
//                                     handleOnSelectPolicyDone()
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (isExeInvokeWorkFlow()) {
        invokeWorkflow();
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            commonOnSubmit('saveAllVLRisk', false, false, false, true);
            break;
        case 'ADDINSURED':
            setInputFormField("addCompanyInsured", "Y");
            addInsured();
            break;
        case 'ADDNONINSURED':
            setInputFormField("addCompanyInsured", "N");
            addInsured();
            break;
        case 'CANCEL':
            var covRelatedEntityId = vlRiskListGrid1.recordset("CCOVRELATEDENTITYID").value;
            var effFromDate = vlRiskListGrid1.recordset("CSTARTDATE").value;
            var effToDate = vlRiskListGrid1.recordset("CENDDATE").value;
            performCancellation("EMPLOYEE", covRelatedEntityId, effFromDate, effToDate);
            break;
        case 'CLOSE':
            if (isNeedToRefreshParentB()) {
                window.frameElement.document.parentWindow.refreshPage(); 
            }
            else {
                closeThisDivPopup(false);
            }
            break;
    }
}

function isNeedToRefreshParentB(){
    var refreshParentPage = getObjectValue("refreshParentPageRequired");
    return refreshParentPage == "Y";
}

//-----------------------------------------------------------------------------
// Add Insured/Non Insured employee
//-----------------------------------------------------------------------------
function addInsured() {
    setInputFormField("seledEntityId", 0);
    setInputFormField("seledEntityName", "");
    openEntitySelectWinFullName("seledEntityId", "seledEntityName", "handleFindClient()");
}

//-----------------------------------------------------------------------------
// Callback function, when active policy is selected in the select active policy page
//-----------------------------------------------------------------------------
function handleOnSelectPolicyDone(underPolicyId, underPolicyNo, riskBaseId) {
    if(underPolicyNo.indexOf("(")>0){
        underPolicyNo = underPolicyNo.split("(")[0];
    }
    setInputFormField("externalId", underPolicyId);
    setInputFormField("externalNo", underPolicyNo);
    setInputFormField("riskBaseId", riskBaseId);
    getInitialValuesForNewInsured();
}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleFindClient() {
    //add non-insured employee
    if (getObjectValue("addCompanyInsured") == 'N') {
        getInitialValuesForNewInsured();
    }
    //add insured employee
    else {
        openSelectPolicy(getObjectValue("seledEntityId"));
    }

}

function openSelectPolicy(entityId) {
    var path = getAppPath() + "/policymgr/underlyingpolicymgr/selectActivePolicy.do?"
        + commonGetMenuQueryString() + "&process=loadAllActivePolicy"
        + "&entityId=" + entityId
        + "&effDate=" + getObjectValue("termEffectiveFromDate")
        + "&expDate=" + getObjectValue("termEffectiveToDate")
        + "&policyCycle=" + policyHeader.policyCycleCode;

    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true,
        null, null, "450", "400", "", "", "loadAllActivePolicy", false);
}

function getInitialValuesForNewInsured() {
    var url = getAppPath() + "/coveragemgr/vlcoveragemgr/maintainVLCoverage.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForVLRisk"
        + "&entityId=" + getObjectValue("seledEntityId")
        + "&companyInsuredB=" + getObjectValue("addCompanyInsured")
        + "&coverageBaseRecordId=" + getObjectValue("coverageBaseRecordId")
        + "&vlScreenModeCode=" + getObjectValue("vlScreenModeCode")
        + "&vlCoverageStatus=" + getObjectValue("vlCoverageStatus")
        + "&riskBaseId=" + getObjectValue("riskBaseId")
        + "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate");
    if (getObjectValue("addCompanyInsured") == 'Y') {
        url = url + "&externalId=" + getObjectValue("externalId")
            + "&externalNo=" + getObjectValue("externalNo");
    }
    new AJAXRequest("get", url, '', handleOnGetInitialValues, false);
}

function vlRiskListGrid_setInitialValues() {
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


function deleteEmployee() {
    var vlCovgGridId = getCurrentlySelectedGridId();
    var rs = getXMLDataForGridName(getCurrentlySelectedGridId()).recordset;
    var xmlData = getXMLDataForGridName(vlCovgGridId);
    var origXMLData = getOrigXMLData(xmlData);
    if (!isEmptyRecordset(rs) && confirm(getMessage("pm.common.selected.record.delete.confirm"))) {
        var officialRecordId = rs.Fields("COFFICIALRECORDID").value;
        var riskFk = rs.Fields("CRISKBASEID").value

        var vlScreenMode = getObjectValue("vlScreenModeCode");

        //delete new records
        if (isEmpty(officialRecordId) || officialRecordId == '0') {
            eval(vlCovgGridId + "_deleterow();");
        }
        //delete changed records
        else {
            //if the screen mode is OOSWIP
            if (vlScreenMode == 'OOSWIP') {
                eval(vlCovgGridId + "_deleterow();");

                // Use xmlData.recordset instead of rs to avoid infinite loop as rs does NOT move along with xmlData
                first(xmlData);
                beginDeleteMultipleRow(vlCovgGridId);
                while (!xmlData.recordset.eof) {
                    if (xmlData.recordset.Fields("CRISKBASEID").value == riskFk) {
                        var recordMode = xmlData.recordset.Fields("CRECORDMODECODE").value
                        if (recordMode != 'OFFICIAL') {
                            setSelectedRow(vlCovgGridId, xmlData.recordset("ID").value);
                            eval(vlCovgGridId + "_deleterow();");
                        }
                        else {
                            xmlData.recordset.Fields("DISPLAY_IND").value = 'Y';
                        }
                    }
                    next(xmlData);
                }
                first(xmlData);
                endDeleteMultipleRow(vlCovgGridId);
            }
            //if the screen mode is not OOSWIP
            else {
                var officalNode = origXMLData.documentElement.selectSingleNode("//ROW[CRISKID='" + officialRecordId + "']");
                if (officalNode != null) {
                    officalNode.selectSingleNode("./DISPLAY_IND").text = "Y";
                    eval(vlCovgGridId + "_filter(\"UPDATE_IND != 'D'\")");
                }

                // Use xmlData.recordset instead of rs to avoid infinite loop as rs does NOT move along with xmlData
                first(xmlData);
                beginDeleteMultipleRow(vlCovgGridId);
                while (!xmlData.recordset.eof) {
                    if (xmlData.recordset.Fields("COFFICIALRECORDID").value == officialRecordId) {
                        setSelectedRow(vlCovgGridId, xmlData.recordset("ID").value);
                        eval(vlCovgGridId + "_deleterow();");
                    }
                    next(xmlData);
                }
                first(xmlData);
                endDeleteMultipleRow(vlCovgGridId);
            }
        }
        hideShowForm(vlCovgGridId);
    }
}


//refresh page for VL Coverage, this function is called when employee is cancelled sucessfully
function refreshPage() {
    var url = location.href;
    // Strip of information after the "?"
    if (url.indexOf('?') > -1) {
        url = url.substring(0, url.indexOf('?'));
    }

    url = buildMenuQueryString("", url);
    url = removeParameterFromUrl(url, "policyViewMode");
    url += "&policyViewMode=WIP";
    url += "&refreshParentPageRequired=Y";
    // add parameters for loading VL Coverage page
    if (getObject("coverageBaseRecordId")) {
        url += "&coverageBaseRecordId=" + getObjectValue("coverageBaseRecordId");
    }
    if (getObject("coverageStatus")) {
        url += "&coverageStatus=" + getObjectValue("coverageStatus");
    }
    if (getObject("coverageBaseStatus")) {
        url += "&coverageBaseStatus=" + getObjectValue("coverageBaseStatus");
    }
    if (getObject("coverageEffectiveFromDate")) {
        url += "&coverageEffectiveFromDate=" + getObjectValue("coverageEffectiveFromDate");
    }
    // end adding parameters for loading VL Coverage page
    setWindowLocation(url);
}