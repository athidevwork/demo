//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 06/01/2012       xnie        132114: Modified maintainAffiliationListGrid_setInitialValues() to replace
//                              riskEffectiveToDate with affiliationRiskExpDate for path.
// 10/09/2014       wdang       156038 - Modified handleOnSubmit() to add "riskId" as an input field.
// 11/25/2014       kxiang      158853 -
//                              1. Modified maintainAffiliationListGrid_setInitialValues to change call function
//                              2. Added handleOnGetInitialValuesForAddAffiliation.
//                              3. Added handlePostAddRow to set href to grid affiliation.
// 06/08/2015       wdang       163197 - 1) Removed the function thisopenEntityMiniPopupWin().
//                                       2) Added function isOpenEntityMiniPopupByFrame() which will be called
//                                          by thisopenEntityMiniPopupWin() in common.js file.
// 03/10/2017       wrong       180675 - Override function lookupEntity for new UI tab style.
// 11/02/2018       clm         195889 -  Grid replacement using item(0) instead of (0)
//-----------------------------------------------------------------------------


function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADDAFFI':
            addAffiliation();
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllAffiliation";
            var needToCaptureTransaction = "N";
            if (getObject("needToCaptureTransaction")) {
                needToCaptureTransaction = getObjectValue("needToCaptureTransaction");
            }
            if (needToCaptureTransaction == "Y") {
                var transactionEffDate = getAffiEffectiveDateFromNewRecord();
                captureTransactionDetailsWithEffDate("ENDAFFILIA", "submitForm", transactionEffDate);
            }
            else {
                proceed = true;
            }
            break;
    }
    return proceed;
}
function getAffiEffectiveDateFromNewRecord() {
    var affiEffDate = null;
    var modXML = maintainAffiliationListGrid1.documentElement.selectNodes("//ROW[(UPDATE_IND='I')]");
    if (modXML.length > 0) {
        affiEffDate = modXML.item(0).selectNodes("CEFFDATE").item(0).text;
    }
    return affiEffDate;

}

//-----------------------------------------------------------------------------
// Add Affiliation
//-----------------------------------------------------------------------------
function addAffiliation() {
    setInputFormField("affiliationEntityId", 0);
    setInputFormField("affiliationEntityName", "");
    var isCisDesired = getSysParmValue("PM_AFF_CS_SEARCH");
    if (isEmpty(isCisDesired) || (isCisDesired == 'Y')) {
        openEntitySelectWinFullName("affiliationEntityId", "affiliationEntityName", "handleOnSelectAffiliationEntity()", "O");
    }
    else {
        var entityClassCode = getEntityRoleType("PM_AFFL_CLASSIF_BY_POLRISK", policyHeader.policyTypeCode, getObjectValue("riskTypeCode"), policyHeader.lastTransactionInfo.transEffectiveFromDate);
        if (isEmpty(entityClassCode)) {
            entityClassCode = getSysParmValue("PM_AFF_CS_ROLES");
            if (isEmpty(entityClassCode)) {
                entityClassCode = 'HOSPITAL,VAPHOSP';
            }
        }
        lookupEntity(entityClassCode, policyHeader.termEffectiveFromDate,
            'affiliationEntityId', null, 'handleOnSelectAffiliationEntity()', getParentWindow(true).subFrameId);
    }

}

//-----------------------------------------------------------------------------
// Call back function for select entity
//-----------------------------------------------------------------------------
function handleOnSelectAffiliationEntity() {
    if ((!policyHeader.wipB) && (isEmpty(getObjectValue("capturedAffiStartDate")))) {
        var path = getAppPath() + "/riskmgr/affiliationmgr/captureAffiliationStartDate.do?process=displayPage";
        var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, "", "", "800", "", "", "", "", false);
    }
    else {
        commonAddRow(getCurrentlySelectedGridId());
    }
}


function maintainAffiliationListGrid_setInitialValues() {
    var path = getAppPath() + "/riskmgr/affiliationmgr/maintainAffiliation.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForAffiliation"
        + "&entityParentId=" + getObjectValue("affiliationEntityId")
        + "&entityChildId=" + getObjectValue("riskEntityId")
        + "&affiliationRiskExpDate=" + getObjectValue("affiliationRiskExpDate");
    var affiliationStartDate = getObjectValue("capturedAffiStartDate");
    if (affiliationStartDate != null) {
        path = path + "&affiliationStartDate=" + affiliationStartDate;
    }
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddAffiliation, false);
}

function handleOnGetInitialValuesForAddAffiliation(ajax){
    commonHandleOnGetInitialValues(ajax, 'ORGANIZATIONNAMEHREF');
}

function isOpenEntityMiniPopupByFrame() {
    return true;
}

function handleOnChange(obj) {
    if (obj.name == "vapB") {
        var vapB = getObjectValue('vapB');
        if (vapB == 'Y') {
            handleError(getMessage("pm.maintainAffiliation.selectVapB.warning"));
        }
        else if (vapB == 'N') {
            handleError(getMessage("pm.maintainAffiliation.deSelectVapB.warning"));
        }
    }
    return true;
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "maintainAffiliationListGrid") {
        var xmlData = getXMLDataForGridName("maintainAffiliationListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var organizationNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CORGANIZATIONNAME") {
                organizationNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + organizationNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("ORGANIZATIONNAMEHREF"))) {
                    href = "javascript:handleOnGridHref('maintainAffiliationListGrid', '"
                            + getObjectValue("ORGANIZATIONNAMEHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}

//-----------------------------------------------------------------------------
// overwrite function lookupEntity to openPopup in parent window.
//-----------------------------------------------------------------------------
function lookupEntity(entityClassCode, effectiveFromDate, entityIdFieldName, entityNameFieldName, eventHandler, subFrameId) {
    entitylookupEntityIdFieldName = entityIdFieldName;
    entitylookupEntityNameFieldName = entityNameFieldName;
    entityLookupEventHandler = eventHandler;

    var path = getAppPath() +
            "/entitymgr/lookupEntity.do?entityClassCode=" + entityClassCode +
            "&effectiveFromDate=" + effectiveFromDate;
    if (isTabStyle()) {
        path += "&subFrameId=" + subFrameId;
    }
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, null, null, "", "", "", "", "lookupEntity", false);
}
