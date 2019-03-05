//-----------------------------------------------------------------------------
// Functions to support entity list page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 07/13/2010       shchen      Provide search on CIS relationships and entitytype for issue 106849.
// 09/01/2011       Michael Li  issue 121133
// 11/10/2011       kshen       Issue 126394.
// 11/30/2011       Leo         Issue 117873.
// 03/20/2014                   Issue 151540
//                              1) add DBA Name field's data into URL in js to pass it,
// 10/30/2014       Elvin       Issue 158667: pass in country code/email address from search
// 12/07/2015       ylu         Issue 165742: support phone number wild search
// 06/21/2016       Elvin       Issue 177406: add entity_externalDataId when checking input
// 12/30/2016       dpang       Issue 181349: sort grid per orgSortInfo when go back to entity list.
// 06/15/2018       jld         Issue 193183: Make sure that date fields do not submit mask for search.
// 08/23/2018       jdingle     Issue 195413. Do not allow search on enter if search button is not available.
// 09/08/2018       dpang       Issue 195518: Fix incorrect county field id for adding entity.
// 11/8/2018        wer         Issue 196147: Use new grid LoadingPromise and SortingPromise to wait for the grid to finish loading/sorting before calling processBackToList/processBackToListGoToPage
//-----------------------------------------------------------------------------

var stateFldID = "searchCriteria_stateCode";
var countyFldID = "searchCriteria_countyCode";
var entityClassFldID = "searchCriteria_entityClassCode";
var entitySubClassFldID = "searchCriteria_entitySubClassCode";
var entitySubTypeCode = "searchCriteria_entitySubTypeCode";
var lastOrOrgNameFldID = "searchCriteria_lastOrOrgName";
var firstNameFldID = "searchCriteria_firstName";
var taxIDFldID = "searchCriteria_taxId";
var clientIDFldID = "searchCriteria_clientId";
var dateOfBirthFldID = "searchCriteria_dateOfBirth";
var roleTypeCodeFldID = "searchCriteria_roleTypeCode";
var roleExternalIDFldID = "searchCriteria_roleExternalId";
var cityFldID = "searchCriteria_city";
var ZIPCodeFldID = "searchCriteria_zipCode";
var licenseNoFldID = "searchCriteria_licenseNo";
var riskClassCodeFldID = "searchCriteria_riskClassCode";
var eMailAddress1FldID = "searchCriteria_emailAddress1";
var entityLegacyDataID = "searchCriteria_legacyDataId";
var entityExternalDataID = "searchCriteria_externalDataId";
var entityPk = '';
var vendorCode = "searchCriteria_vendorCode";
var dbaNameFldID = "searchCriteria_dbaName";
var entitySearch_addlField = "searchCriteria_addlField";
var longNameFldId = "searchCriteria_veryLongName";
var countryCodeFldId = "searchCriteria_countryCode";
var zipCodeForeignFldId = "searchCriteria_zipCodeForeign";
var provinceFldId = "searchCriteria_province";
var otherProvinceFldId = "searchCriteria_otherProvince";
var zipcodeFidID = "searchCriteria_zipCode";

var phoneNumberTypeCodeFldId = "searchCriteria_phoneNumberTypeCode";
var areaCodeFldId = "searchCriteria_areaCode";
var phoneNumberFldId = "searchCriteria_phoneNumber";
var addressLine1FldId = "searchCriteria_addressLine1";
var propertyDescriptionFldId = "searchCriteria_propertyDescription";
var propertyIDFldId = "searchCriteria_propertyId";
var cmLobCodeFldId = "searchCriteria_cmLobCode";
var surveyVisitFromDateFldId = "searchCriteria_surveyVisitFromDate";
var surveyVisitToDateFldId = "searchCriteria_surveyVisitToDate";
var entityType = "searchCriteria_entityTypeCode";
var relationShip = "searchCriteria_relationshipCode";
var entityReferenceNum = "searchCriteria_referenceNumber";
var char2FldID = "searchCriteria_char2";
var riskStateFldID = "searchCriteria_riskState";
var riskCountyFldID = "searchCriteria_riskCounty";

var listroleUrl = '';

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    /* If the user has clicked on Search but has not entered or selected
       any criteria, don't do the search. */
    if (btnID == 'SEARCH' || btnID == 'search') {
        search();
    } else if (btnID == 'listrole'){
        if (!isStringValue(entityPk)) {
            alert(getMessage("ci.common.error.record.select"));
            return;
        }

        listroleUrl = getAppPath() + "/ciEntityListRole.do?process=loadEntityListRole&pk=" + entityPk
            + "&eventName=undefined";
        new AJAXRequest("get", listroleUrl, '', callbackCheckRole);
    } else if (btnID == 'ADD' || btnID == 'add') {
        var path = "ciEntityAddChoicePop.do?lNm=" + encodeUrl(getObject(lastOrOrgNameFldID).value) +
                   "&fNm=" + encodeUrl(getObject(firstNameFldID).value) +
                   "&taxId=" + getObject(taxIDFldID).value +
                   "&dob=" + getObject(dateOfBirthFldID).value +
                   "&cls=" + getObject(entityClassFldID).value +
                   "&city=" + encodeUrl(getObject(cityFldID).value) +
                   "&st=" + getObject(stateFldID).value +
                   "&cnty=" + getObject(countyFldID).value +
                   "&zip=" + getObject(zipcodeFidID).value +
                   "&subCls=" + getObjectValue(entitySubClassFldID) +
                   "&subType=" + getObjectValue(entitySubTypeCode) +
                   "&dbaName=" + encodeUrl(getObjectValue(dbaNameFldID)) +
                   "&countryCode=" + getObjectValue(countryCodeFldId) +
                   "&emailAddress=" + encodeURIComponent(getObjectValue(eMailAddress1FldID)) +
                   "&pageSource=entitySearch";
        var mainwin = window.open(path, 'CIEntityAddChoicePop',
                'top=10,left=10, width=845,height=250,innerHeight=250,innerWidth=840,scrollbars');
        mainwin.focus();
    }
}

//-----------------------------------------------------------------------------
// check roles whether exist (Add by Jacky 18/08/2008)
//-----------------------------------------------------------------------------
function callbackCheckRole(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data == 'no role') {
                listroleUrl = '';
                alert(getMessage("ci.entity.message.noRoles.associated"));
            }
            else {
                var divPopupId = openDivPopup("Entity Role List", listroleUrl, true, true, "", "", "", "", "", "", "", true);
            }
        }
    }

}

function search() {
    if (!validSearchCriteria()) {
        return;
    }
    else {
        document.forms[0].process.value = 'search';
        // When doing a new search, the original sort and selected row info should be cleared.
        clearOrgSortFieldValues();
        showProcessingImgIndicator();
        submitFirstForm();
    }
}

function clearOrgSortFieldValues() {
    if (hasObject("orgSortColumn")) {
        setObjectValue("orgSortColumn", "");
    }
    if (hasObject("orgSortType")) {
        setObjectValue("orgSortType", "");
    }
    if (hasObject("orgSortOrder")) {
        setObjectValue("orgSortOrder", "");
    }
    if (hasObject("orgRowId")) {
        setObjectValue("orgRowId", "");
    }
}

function handleSearchOnEnter() {
    if (hasObject('CI_ENSRC_SEARCH')) {
        if (getObject('CI_ENSRC_SEARCH').disabled === false) {
            search();
        }
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    /* Refresh page with new LOVs for county and subclassification.*/
    if (field.name == countryCodeFldId) {
        setObjectValue(stateFldID, "");
        setObjectValue(countyFldID, "");
        setObjectValue(cityFldID, "");
        setObjectValue(ZIPCodeFldID, "");
        setObjectValue(zipCodeForeignFldId, "");
        setObjectValue(provinceFldId, "");
        setObjectValue(otherProvinceFldId, "");
    }
}

function handleOnLoad() {
    if (isDefined(getObject("entityListGrid1"))) {
        $.when(dti.oasis.grid.getLoadingPromise("entityListGrid")).then(function () {
            processBackToList();
        });
    }
}

//-----------------------------------------------------------------------------
// Clear search criteria.
//-----------------------------------------------------------------------------
function clearSearchCriteria() {
    /* Clear all the search criteria. */
    clearFormFields(document.forms[0], true);
    //fix issue 65133 Clear both the search criteria and the Search results.
    //modify by Kyle (kshen)
    document.forms[0].process.value = "initPage";
    document.forms[0].action = "ciEntitySearch.do";
    showProcessingImgIndicator();
    submitFirstForm();
}

//-----------------------------------------------------------------------------
// Validate search criteria.
//-----------------------------------------------------------------------------
function validSearchCriteria() {
    //Last/ORG name and Long Name is configured as required then apply this validation rule
    if (isRequiredField(lastOrOrgNameFldID) && isRequiredField(longNameFldId)) {
        if (!isStringValue(getObjectValue(lastOrOrgNameFldID)) &&
            !isStringValue(getObjectValue(longNameFldId))) {
            var orgNameLabel = getLabel(lastOrOrgNameFldID);
            var longNameLabel = getLabel(longNameFldId);
            if (!isStringValue(orgNameLabel)) {
                orgNameLabel = getMessage("ci.common.error.entitySearch.lastName");
            }
            if (!isStringValue(longNameLabel)) {
                longNameLabel = getMessage("ci.common.error.entitySearch.longName");
            }
            alert(getMessage("ci.common.error.value.mustEntered", new Array(orgNameLabel, longNameLabel)));
            return false;
        }
    }
    /* The user must select at least one search criterion. */
    if (
        !isValue(getSingleObject(stateFldID)) &&
        !isValue(getSingleObject(countyFldID)) &&
        !isValue(getSingleObject(entityClassFldID)) &&
        !isValue(getSingleObject(entitySubClassFldID)) &&
        !isValue(getSingleObject(roleTypeCodeFldID)) &&
        !isValue(getSingleObject(riskClassCodeFldID)) &&
        !isValue(getSingleObject(lastOrOrgNameFldID)) &&
        !isValue(getSingleObject(firstNameFldID)) &&
        !isValue(getSingleObject(taxIDFldID)) &&
        !isValue(getSingleObject(clientIDFldID)) &&
        !isValue(getSingleObject(dateOfBirthFldID)) &&
        !isValue(getSingleObject(cityFldID)) &&
        !isValue(getSingleObject(ZIPCodeFldID)) &&
        !isValue(getSingleObject(roleExternalIDFldID)) &&
        !isValue(getSingleObject(licenseNoFldID)) &&
        !isValue(getSingleObject(eMailAddress1FldID)) &&
        !isValue(getSingleObject(entityLegacyDataID)) &&
        !isValue(getSingleObject(entityExternalDataID)) &&
        !isValue(getSingleObject(vendorCode)) &&
        !isValue(getSingleObject(dbaNameFldID)) &&
        !isValue(getSingleObject(entitySearch_addlField)) &&
        !isValue(getSingleObject(longNameFldId)) &&
        !isValue(getSingleObject(countryCodeFldId)) &&
        !isValue(getSingleObject(zipCodeForeignFldId)) &&
        !isValue(getSingleObject(provinceFldId)) &&
        !isValue(getSingleObject(otherProvinceFldId)) &&
        !isValue(getSingleObject(phoneNumberTypeCodeFldId)) &&
        !isValue(getSingleObject(areaCodeFldId)) &&
        !isValue(getSingleObject(phoneNumberFldId)) &&
        !isValue(getSingleObject(addressLine1FldId)) &&
        !isValue(getSingleObject(propertyDescriptionFldId)) &&
        !isValue(getSingleObject(cmLobCodeFldId)) &&
        !isValue(getSingleObject(surveyVisitFromDateFldId)) &&
        !isValue(getSingleObject(surveyVisitToDateFldId)) &&
        !isValue(getSingleObject(entityType)) &&
        !isValue(getSingleObject(relationShip)) &&
        !isValue(getSingleObject(entityReferenceNum)) &&
        !isValue(getObject(char2FldID)) &&
        !isValue(getObject(riskStateFldID)) &&
        !isValue(getObject(riskCountyFldID))
        ) {
        alert(getMessage("ci.entity.message.searchCriteria.enter"));
        return false;
    }
    /* It is possible after the user clicks on "Clear" that no state will be
selected but the counties from the previously selected state will still
be displayed.  This check prevents the user from searching on
county only without selecting a state. */
    if (!isValue(getSingleObject(stateFldID)) && isValue(getSingleObject(countyFldID))) {
        alert(getMessage("ci.entity.message.selected.without", new Array(getLabel(getSingleObject(countyFldID)),
            getLabel(getSingleObject(stateFldID)),
            getLabel(getSingleObject(stateFldID)),
            getLabel(getSingleObject(countyFldID)),
            getLabel(getSingleObject(countyFldID)))));
        return false;
    }
    /* It is possible after the user clicks on "Clear" that no class will be
selected but the subclasses from the previously selected class will still
be displayed.  This check prevents the user from searching on
subclass only without selecting a class. */
    if (!isValue(getSingleObject(entityClassFldID)) && isValue(getSingleObject(entitySubClassFldID))) {
        alert(getMessage("ci.entity.message.selected.without", new Array(getLabel(getSingleObject(entitySubClassFldID)),
            getLabel(getSingleObject(entityClassFldID)),
            getLabel(getSingleObject(entityClassFldID)),
            getLabel(getSingleObject(entitySubClassFldID)),
            getLabel(getSingleObject(entitySubClassFldID)))));
        return false;
    }

    if (isValue(getSingleObject(phoneNumberFldId))) {
        if (getObjectValue("CI_PHONE_PART_SRCH")=="Y") {
            var phoneNum = getObjectValue(phoneNumberFldId);
            var reTestForNumOnly = /^[\d]{1,7}$/;
            var reTestForNumWithDash = /^[\d]{1,3}-[\d]{1,4}$/;
            if (!reTestForNumOnly.test(phoneNum)  &&
                !reTestForNumWithDash.test(phoneNum)) {
                alert(getMessage("ci.entity.message.phoneNumber.invalid"));
                return false;
            }
        }
    }

    if (isValue(getSingleObject(dateOfBirthFldID))) {
        if (getObjectValue(dateOfBirthFldID)==='mm/dd/yyyy') {
            setObjectValue(dateOfBirthFldID,'');
        }
    }

    if (isValue(getSingleObject(surveyVisitFromDateFldId))) {
        if (getObjectValue(surveyVisitFromDateFldId)==='mm/dd/yyyy') {
            setObjectValue(surveyVisitFromDateFldId,'');
        }
    }

    if (isValue(getSingleObject(surveyVisitToDateFldId))) {
        if (getObjectValue(surveyVisitToDateFldId)==='mm/dd/yyyy') {
            setObjectValue(surveyVisitToDateFldId,'');
        }
    }


    return true;
}

//-----------------------------------------------------------------------------
// Go to the entity modify page.
//-----------------------------------------------------------------------------

function goToModify(pk,goToGlance) {

    getRow(entityListGrid1, pk);
    var type = entityListGrid1.recordset("CENTITYTYPE").value;
    var name = entityListGrid1.recordset("CCLIENTNAME").value;

    var orgSortInfo = '&orgSortColumn=' + getOrgSortInfo("currentSortColumn") +
                      '&orgSortType=' + getOrgSortInfo("currentSortType") +
                      '&orgSortOrder=' + getOrgSortInfo("sortOrder");

    if (entityGridSorted || isGridSortOrderConfigured) {
        updateEntityListSession();
    }

    goToEntityModify(pk, type, name, goToGlance, orgSortInfo);
}

function getOrgSortInfo(prop) {
    return getTableProperty(getTableForXMLData(entityListGrid1), prop) ? getTableProperty(getTableForXMLData(entityListGrid1), prop) : '';
}

var entityGridSorted = false;
function handleOnAfterSort() {
    entityGridSorted = true;
}

function updateEntityListSession() {
    var navigationList = "";

    var splitSign = "!~";
    entityListGrid1.recordset.movefirst();
    while (!entityListGrid1.recordset.eof) {
        navigationList += entityListGrid1.recordset("ID").value + splitSign +
                          entityListGrid1.recordset("CENTITYTYPE").value + splitSign +
                          entityListGrid1.recordset("CCLIENTNAME").value + splitSign;
        entityListGrid1.recordset.movenext();
    }
    entityListGrid1.recordset.movefirst();

    if (isStringValue(navigationList)) {
        navigationList = navigationList.substring(0, navigationList.length - splitSign.length);
        var path = getAppPath() + "/ciEntityModify.do?process=setBackToEntityListSession" + "&date=" + new Date();
        var data = "navigationList=" + encodeURIComponent(navigationList);

        new AJAXRequest("POST", path, data, handleSaveSearchFilterCriteriaResult, false);
    }
}

function handleSaveSearchFilterCriteriaResult(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var xml = ajax.responseXML;
            handleAjaxMessages(xml, null);
        }
    }
}

function entityListGrid_selectRow(pk) {
    entityPk = pk;
}

function processBackToList() {
    var orgSortColumnObjValue = getObjectValue("orgSortColumn");

    if (isStringValue(orgSortColumnObjValue) && orgSortColumnObjValue != 'null') {
        var order = "-";
        if (getObjectValue("orgSortOrder") == "-") {
            order = "+"
        }
        setTableProperty(getTableForXMLData(entityListGrid1), "sortOrder", order);
        if (dti.oasis.page.useJqxGrid()) {
            entityListGrid_sort(orgSortColumnObjValue, getObjectValue("orgSortType"), getObjectValue("orgSortOrder"));

            $.when(dti.oasis.grid.getLoadingPromise("entityListGrid"), dti.oasis.grid.getSortingPromise("entityListGrid")).then(function () {
                processBackToListGoToPage();
            });

        } else {
            entityListGrid_sort(orgSortColumnObjValue);

            var testCode = 'isGridReadyStateIsCompleted("entityListGrid") && ' +
                '!getTableProperty(getTableForGrid("entityListGrid"), "sorting")';

            var callbackCode = 'processBackToListGoToPage();';
            executeWhenTestSucceeds(testCode, callbackCode, 50);
        }
    }
}

function processBackToListGoToPage() {
    var orgRowIdObjValue = getObjectValue("orgRowId");

    if (isStringValue(orgRowIdObjValue) && orgRowIdObjValue != 'null') {
        selectRowById("entityListGrid", orgRowIdObjValue);
    }
}