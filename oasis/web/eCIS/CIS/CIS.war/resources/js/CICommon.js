//-----------------------------------------------------------------------------
// Common functions for CIS.
// Author: unknown
// Date:   unknown
// Modifications:
// 07/02/2007    kshen    Added method openEntitySelectWinFullNameWithParams (
//                        Opens the entity selection window with params.)
// 11/01/2007    FWCH     Added function openEntityListWindow() .
// 12/04/2007    kshen    Added method getSelectedEntityJsonValue.
// 05/12/2008    FWCH     Modified openEntityMiniPopupWin() to support invoked
//                        from sub-frame.
// 05/22/2008    FCB      Added openEntitySelectWinFullName with 3 parameters.
// 06/18/2008    Kenney   Added CI_SUMMARY_MI tab menu
// 08/13/2008    Leo      Modify for issue 84778
// 08/13/2008    Jacky   added function openEntitySelectWinFullNameAgentAddlSql
//                                to enchance issue 84785
// 10/10/2008    Jacky  Added function entityHeaderHandleNotesExist() to handle entity notes icon
// 6/1/2009      Colton   Add Billing Menu item Id
// 05/28/2009    MSN Added OrgGroup Tab 
// 12/18/2009    Jacky claim log module refactoring
// 12/30/2009    Jacky Modified for issue #102270(ePolicy issue)
// 04/29/2010    Modify function for popuping ciEntityMiniPopup for issue 106174
// 05/12/2010    kshen    Added codes to handle callback event when entity name was auto completed.
// 09/28/2010    wfu      111776: Replaced hardcode string with resource definition
// 02/24/2011    kshen    Use notesImage and noNotesImage variables for notes icons. 
// 09/01/2011    Michael Li  issue 121133
// 09/28/2011    parker  issue 125550. fix goto error when entityName contain '#'
// 12/09/2011    hxk     Issue 127123
//                       1)  Add logic for creating std CIS url for "tab hopping" for
//                           Client, Address,  Vendor,   Vendor address,
//                           Phone number, entity class, entity role
// 02/24/2012    kmv     Issue 130774
//                       1)  Replaced entAgentAddlSqlFldName in function
//                           openEntitySelectWinFullNameAgentAddlSql with roleTypeCode
// 03/19/2012    jld     Issue 131728. Add common call to ImageRight.
// 04/26/2012    ryzhao  Issue 128030.
//                       1) Add getExactFirstLastName to get exact first name and last name for search.
//                       2) Add textTrim to remove left and right space.
//                       3) Modified appendNameCriteriaForFinder to call getExactFirstLastName.
// 06/21/2012   bzhu     Issue 134345. Append phoneNumber_sourceRecordFK parameter in tempUrl.
// 12/26/2012   htwang   Issue 135339. For Account No search, it is not necessary to
//                                     add the lastName,firstName to the given url for entitySearch.
// 04/16/2013   htwang   Issue 132272. For Policy No search, it is not necessary to
//                                     add the lastName,firstName to the given url for entitySearch.
// 04/16/2013   Parker   Issue 141103. Use the javascript self function encodeURIComponent to replace the function encodeUrl
// 03/28/2014   bzhu     Issue 152538. Modify email address validation.
// 06/19/2014   jld      Issue 154576. check for mainwin existance prior to focus.
// 08/05/2014   wkong    Issue 156066: Add country,state,city attribute into entity search method.
// 08/14/2014   bzhu     Issue 155077.
// 12/17/2015   kshen    Issue 153889. Add method openEntitySelectWithParams.
// 05/16/2016   jld      Issue 161565. Remove the split for last_name, first_name. Is now handled in SQL.
// 12/30/2016   dpang    Issue 181349. Add parameter orgSortInfo to goToEntityModify to append original sort info to URL if needed.
// 09/13/2017   kshen    Grid replacement. Removed the method
// 01/11/2018   dzhang   Issue 189948: Add paramter when use openEntityListWindow to open pop page.
// 01/12/2018   ylu      Issue 190718. not to validate masked secured fields
// 06/14/2018   dpang    Issue 109216. Refactor "Entity Select Search" popup.
// 07/28/2018   dpang    Issue 109216. Change code for checking "undefined" type.
// 09/07/2018   dpang    Issue 195305. Set default entity search criteria to empty String and change process to searchWithDefaultCriteria.
//-----------------------------------------------------------------------------

//we now search lastName, firstName, and org with no split
function appendNameCriteriaForFinder(url, fieldName) {
    var appendedUrl = url;
    if (hasObject(fieldName) && ! isEmpty(getObjectValue(fieldName))) {
        var lastName = getObjectValue(fieldName);
        if (isDefined(lastName)) {
            appendedUrl = appendedUrl.replace("searchCriteria_lastOrOrgName=", "searchCriteria_lastOrOrgNameReplaced=");
            appendedUrl += "&searchCriteria_lastOrOrgName=" + encodeURIComponent(lastName);
        }
    }
    return appendedUrl;
}

function cisStartImageRightDeskTop(sourceTable, sourceData) {

    // First get the data element to be used
    // Check if the field is in the form

    if (sourceData == '') {
        alert(getMessage("ci.entity.message.imageRight.determine"));
        return;
    }
    // First get the data element to be used
    // Check if the field is in the form
    if (hasObject(sourceData)) {
        sourceData = getObjectValue(sourceData);
    }
    else {
        // Next check if a selected grid exists - if it does look for the field
        // if not catch the exception, error will be raised later
        var selectedGridId = getCurrentlySelectedGridId();

        if (isDefined(selectedGridId) && selectedGridId != '') {
            var gridRecord = getObjectFromRecordset(eval(getCurrentlySelectedGridId() + "1"));

            try {
                sourceData = eval("gridRecord." + sourceData);
            }
            catch (ex) {
                //alert(sourceData + " not found in grid: " + selectedGridId);
            }
        }
    }
    // Second take the source data and source table and get the IR file number and drawer
    var url = getTopNavApplicationUrl("CS") + "/imagerightmgr/maintainImageRight.do?" +
        "&sourceData=" + sourceData +
        "&sourceTable=" + sourceTable;
    url +=   "&date=" + new Date();
    // initiate async call
    new AJAXRequest("get", url, '', handleStartImageRightDeskTop, false);
}

/**
 * @deprecated please use javascript standard function encodeURIComponent
 * @param url
 */
function encodeUrl(url) {
    var newUrl = url;
    newUrl = replace(newUrl, "%", "%25");
    newUrl = replace(newUrl, "[+]", "%2b");
    newUrl = replace(newUrl, "&", "%26");
    newUrl = replace(newUrl, "#", "%23");
    newUrl = replace(newUrl, "@", "%40");
    newUrl = replace(newUrl, "'", "%27");
    newUrl = replace(newUrl, "\"", "%22");
    return newUrl;
}

//with a given url, either peek at the result with a given url via ajax call (invoked if autoFind_{fieldId} is true)
// or manually open the entity search page when finder Icon is clicked
function findEntityWithUrl(url) {

    // For Account No and Policy No search, it is not necessary to add the lastName,firstName to the given url for entitySearch.
    if (url.indexOf("handleOnFindAccount()") == -1 && url.indexOf("handleOnSelectPolicy()") == -1) {
        url = appendNameCriteriaForFinder(url, currentFinderFieldName);
    }

    // reset for next finder..
    currentFinderFieldName = "";
    var isAutoFinder = (isDefined(currentAutoFinderFieldName) && !isEmpty(currentAutoFinderFieldName));
    if (isAutoFinder) {
        // store url for manual search, if autoFinder does not reuturn exact 1 row.
        // once CIEntittSelectList.java is refactored, this can be instead re-constructed from the ajax result,
        setInputFormField("urlForEntityFinder", url);
        ajaxUrl = substituteParmametersForPeekingResults(url);
        // reset for next autoFinder
        currentAutoFinderFieldName = "";
        new AJAXRequest("get", ajaxUrl, '', handleOnPeekingSearchResults, false);
    } else {
        openEntitySearchWindow(url);
    }
}

//-----------------------------------------------------------------------------
// Get a json value of an entity.
//-----------------------------------------------------------------------------
function getEntityJsonValue(xml, id) {
    var xmlJSONString = '{';
    var row = xml.selectSingleNode("//ROW[@id=\"" + id + "\"]");
    var cols = row.getElementsByTagName("*");
    var fieldCount = cols.length;
    var firstCol = true;

    for (var j = 0; j < fieldCount; j++) {
        if (!firstCol) {
            xmlJSONString += ",";
        }
        firstCol = false;
        var value = (cols.item(j).text).replace(/\"/g, "\\\"").replace(/\n/g," ");
        xmlJSONString += '"' + cols.item(j).tagName + '" : "' + value + '"';
    }

    xmlJSONString += "}";
    return eval("(" + xmlJSONString + ")");
}

/**
 * issue 128030
 * Get exactly first name and last name
 *
 * If firstName is not blank, it is a person, not an organization.
 * We will get the exactly first name and last name of that person for search.
 */
function getExactFirstLastName(policyHolderName) {
    var name = policyHolderName.split(",");
    if (name.length > 1 && isDefined(name[1]) && !isEmpty(name[1])) {
        // If it is a person, not an organization.
        // We will get the exactly first name and last name of that person for search.
        var csClientNameFmt = getSysParmValue("CSW_CLNTLIST_PERSFMT");
        if (isEmpty(csClientNameFmt)) {
            csClientNameFmt = getSysParmValue("CS_CLIENTNAME_FORMAT");
            if (isEmpty(csClientNameFmt)) {
                csClientNameFmt = "^L^, ^F^ ^M^";
            }
        }

        // prepare name array
        for (var i = 0; i < name.length; i++) {
            var tempName = textTrim(name[i]).split(" ");
            name[i] = tempName;
        }

        // prepare format array
        csClientNameFmt = csClientNameFmt.toUpperCase();
        var fmtArray = csClientNameFmt.split(",");
        var newFmtArray = new Array(fmtArray.length);
        for (var i = 0; i < fmtArray.length; i++) {
            var tempFmt = textTrim(fmtArray[i]).split(" ");
            newFmtArray[i] = tempFmt;
        }

        var finalLastName = "";
        var finalFirstName = "";
        var findLastName = false;
        var findFirstName = false;

        //  loop format array and name array to get the exact first name and last name
        for (var i = 0; i < fmtArray.length && i < name.length; i++) {
            /**
             * Get last name
             */
            if (!findLastName) {
                if (fmtArray[i].indexOf("^L^") != -1) {
                    for (var j = 0; j < newFmtArray[i].length && j < name[i].length; j++) {
                        if (newFmtArray[i][j] == "^L^") {
                            finalLastName = name[i][j];
                            findLastName = true;
                            break;
                        }
                    }
                }
            }
            /**
             * Get first name
             */
            if (!findFirstName) {
                if (fmtArray[i].indexOf("^F^") != -1) {
                    for (var j = 0; j < newFmtArray[i].length && j < name[i].length; j++) {
                        if (newFmtArray[i][j] == "^F^") {
                            finalFirstName = name[i][j];
                            findFirstName = true;
                            break;
                        }
                    }
                }
            }
            if (findLastName && findFirstName) {
                break;
            }
        }
        name[0] = finalLastName;
        name[1] = finalFirstName;
    }
    return name;
}

// get parameter value for a given parameter name from url
function getParameterValue(url,name){
    var value='';
    var parameters = url.substring(url.indexOf("?")+1).split("\&");
    for (var i=0; i<parameters.length; i++) {
        if (parameters[i].indexOf(name) !=-1) {
            value =  parameters[i].split("=")[1];
            break;
        }
    }
    return value;
}

//-----------------------------------------------------------------------------
// return to the entity modify page
//-----------------------------------------------------------------------------

function goToEntityModify(pk, type, name, goToGlance, orgSortInfo) {

    var url = "?pk=" + pk + "&entityType=" + type + (orgSortInfo ? orgSortInfo : "");
    if(goToGlance){
        url = "ciEntityGlance.do" + url+ "&entityName=" + name;
        showProcessingImgIndicator();
        setWindowLocation(url);

    } else  if (type.substr(0, 1) == 'P') {
        url = "ciEntityPersonModify.do" + url;
        showProcessingImgIndicator();
        setWindowLocation(url);
    }
    else if (type.substr(0, 1) == 'O') {
        url = "ciEntityOrgModify.do" + url;
        showProcessingImgIndicator();
        setWindowLocation(url);
    }
    else {
        alert(getMessage("ci.entity.message.entityType.unknown"));
    }

}

// if the results from peeking returns 1 and exact 1 row, it will replace all form fields with the value returned.
//otherwise, it will open the entitySearch page as the finder icon was clicked.
function handleOnPeekingSearchResults(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // parse and get first record
            var oValueList = parseXML(data);
            if (oValueList.length > 0 && oValueList[0]["recordCount"] == 1) {
                for (var prop in oValueList[0]) {
                    setInputFormField(prop, oValueList[0][prop]);
                }

                var urlForEntityFinder = getObjectValue('urlForEntityFinder');
                var eventName = getParameterValue(urlForEntityFinder, "eventName");
                if (eventName != "" && eventName != "undefined") {
                    window.handleEvent(eventName);
                }
            }
            else {
                openEntitySearchWindow(getObjectValue('urlForEntityFinder'));
            }
        }
    }
}

/*
  (1) To open address search / add page, please call method:
      openAddressSearchAddWin(inAddressPK,
                              inSourceRecordFK,
                              inSourceTableName,
                              inAddressTypeCode,
                              allowOtherClient,
                              claimsOnlyAddrOnTop,
                              readOnly)

      where allowOtherClient, claimsOnlyAddrOnTop, and readOnly should be 'Y' or 'N'.

  (2) You need to define function getInfoFromAddressSearchAdd(infoFromAddressSearchAdd)
      to get the info returned from Address search / add popup page.
      Inside the function, you can get address detail from the parameter
      infoFromAddressSearchAdd and process it.

  (3) Parameter infoFromAddressSearchAdd is an array containing the address info.

      array index          address info
      0                    address_pk
      1                    address_type_code
      2                    address_name
      3                    address_line1
      4                    address_line2
      5                    address_line3
      6                    city
      7                    state_code
      8                    county_code
      9                    zip_code
      10                   zip_plus_four
      11                   post_office_address_b
      12                   usa_address_b
      13                   primary_address_b
      14                   effective_from_date
      15                   effective_to_date
      16                   province
      17                   country_code
      18                   address single line (1 line)
*/
var infoFromAddressSearchAdd = new Array(19);
function openAddressSearchAddWin(origAddressId, origSourceRecordId, origSourceTableName, origAddressTypeCode, allowOtherClient, claimsOnlyAddrOnTop, readOnly, entityName) {
    if (origAddressId == undefined)
        origAddressId = -1;
    if (origSourceRecordId == undefined)
        origSourceRecordId = -1;
    if (origSourceTableName == undefined)
        origSourceTableName = '';
    if (origAddressTypeCode == undefined)
        origAddressTypeCode = '';
    if (allowOtherClient == undefined || allowOtherClient != 'Y')
        allowOtherClient = 'N';
    if (claimsOnlyAddrOnTop == undefined || claimsOnlyAddrOnTop != 'Y')
        claimsOnlyAddrOnTop = 'N';
    if (readOnly == undefined || readOnly != 'Y')
        readOnly = 'N';
    entityName = entityName || "";

    var path = getCISPath() + "/ciAddressSearchAdd.do?origAddressId=" + origAddressId +
        "&origSourceRecordId=" + origSourceRecordId +
        "&origSourceTableName=" + origSourceTableName +
        "&origAddressTypeCode=" + origAddressTypeCode +
        "&allowOtherClient=" + allowOtherClient +
        "&claimsOnlyAddrOnTop=" + claimsOnlyAddrOnTop +
        "&readOnly=" + readOnly +
        "&entityName=" + entityName;
    var mainwin = window.open(path, 'AddressSearchAdd',
        'top=10,left=10, width=900,height=650,innerHeight=650,innerWidth=875,scrollbars');
    if (mainwin) {
        mainwin.focus();
    }
}

function openEntityListWindow(entityPkObj, entityNameObj, eventName) {
    var path = "ciEntitySelectSearch.do?entityPKValue=" + entityPkObj.value +
        "&entityPKFieldName=" + entityPkObj.name +
        "&entityFullNameFieldName=" + entityNameObj.name +
        "&eventName=" + eventName +
        "&process=searchWithDefaultCriteria";
    path = appendNameCriteriaForFinder(path, currentFinderFieldName);
    var mainwin = window.open(getCISPath() + '/' + path, 'EntityList',
        'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
    if (mainwin) {
        mainwin.focus();
    }
    return;
}

//-----------------------------------------------------------------------------
// Opens the entity mini popup window.
//-----------------------------------------------------------------------------
function openEntityMiniPopupWin(pk) {
    var isFrame = (window.frameElement != null);
    var path = "";
    if (isFrame) {
        path = getParentWindow().getCISPath();
    }
    else {
        path = getCISPath();
    }
    path += "/ciEntityMiniPopup.do?pk=" + pk;
    if (isFrame && getParentWindow().isDivPopupEnabled()) {
        //openDivPopup(popupTitle, urlToOpen, isModel, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling)
        getParentWindow().openDivPopup("Entity Mini Popup", path, true, true, null, null, 964, 890, 900, 750,'', true,null,null,true);
    }
    else if (isDivPopupEnabled()) {
        openDivPopup("Entity Mini Popup", path, true, true, null, null, 964, 890, 900, 750,'', true,null,null,true);
    }
    else {
        var mainwin = window.open(path, 'EntityMiniPopup',
            'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
        if (mainwin) {
            mainwin.focus();
        }
    }
    return;
}

function openEntitySearchWindow(url){
    // alter url,so the search is performed optionally
    if (!isEmpty(getParameterValue(url, "searchCriteria_firstName")) || !isEmpty(getParameterValue(url, "searchCriteria_lastOrOrgName"))) {
        url += '&process=searchWithDefaultCriteria';
    } else {
        //Initialize Entity Select Search page.
        url += '&process=initPage';
    }

    var mainwin = window.open(url, 'EntityList', 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
    if (mainwin) {
        mainwin.focus();
    }
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (event arg).
//-----------------------------------------------------------------------------
function openEntitySelectWin(entPKFldName, entFullNameFldName,
                             entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                             entOrgNameFldName, eventName, defaultEntityType, entClientIdFldName,
                             entAgentAddlSqlFldName, entAccountNoFldName,entPolicyNoFldName,
                             subsystemIdFldName, externalIdFldName, defaultRoleTypeCode,roleTypeCode) {
    var path = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&entityLastNameFieldName=" + entLastNameFldName +
        "&entityFirstNameFieldName=" + entFirstNameFldName +
        "&entityMiddleNameFieldName=" + entMiddleNameFldName +
        "&entityOrgNameFieldName=" + entOrgNameFldName +
        "&eventName=" + eventName;
    if (defaultEntityType != undefined) {
        path = path + "&defaultEntityType=" + defaultEntityType;
    }
    if (entClientIdFldName != undefined) {
        path = path + "&entityClientIdFieldName=" + entClientIdFldName;
    }
    // Added by Jacky (08-13-2008)
    if (entAgentAddlSqlFldName != undefined && !isEmpty(entAgentAddlSqlFldName)) {
        path = path + "&fromFM=true&idForAddlSql=" + entAgentAddlSqlFldName;
    }
    // Added by Jacky (09-05-2008)
    if (entAccountNoFldName != undefined && !isEmpty(entAccountNoFldName)) {
        path = path + "&fromFM=true&entityAccountNoFieldName=" + entAccountNoFldName;
    }

    // Added by Jacky (12-12-2008)
    if (entPolicyNoFldName != undefined && !isEmpty(entPolicyNoFldName)) {
        path = path + "&fromFM=true&idForPolicyNo=" + entPolicyNoFldName;
    }
    if(entFullNameFldName == 'accountHolder' && !isEmpty(eventName)) {
        path = path + "&fromFM=true";
    }
    // For issue 102270, added by Stephen (12-29-2009).
    // Passed the role type code as the default Role in Entity Search page.
    if (subsystemIdFldName != undefined && !isEmpty(subsystemIdFldName)) {
        path = path + "&fromDocProcess=true&entityRoleTypeCodeFldName=" + subsystemIdFldName +
            "&externalNumberFldName=" + externalIdFldName;
        if(!isEmpty(defaultRoleTypeCode)){
            path = path + "&defaultRoleTypeCode=" + defaultRoleTypeCode;
        }
    }
    // For issue 126557, added by Michael Li
    if (roleTypeCode != undefined && !isEmpty(roleTypeCode)) {
        path = path + "&searchCriteria_roleTypeCode=" + roleTypeCode;
    }

    return findEntityWithUrl(path);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (No event arg)
// Also with default entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinDfltEntCls(entPKFldName, entFullNameFldName,
                                       entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                       entOrgNameFldName, dfltEntityClassCode, entityClassReadOnly,
                                       entAddrCountryFldName, entAddrStateFldName, entAddrCityFldName, eventName) {
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }

    if(!eventName) {
        eventName =  "";
    }

    openEntitySelectWinEvtDfltEntCls(entPKFldName, entFullNameFldName,
        entLastNameFldName, entFirstNameFldName,
        entMiddleNameFldName, entOrgNameFldName,
        dfltEntityClassCode, entityClassReadOnly, eventName,
        entAddrCountryFldName, entAddrStateFldName, entAddrCityFldName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (No event arg)
// Also with default role type restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinDfltRoleType(entPKFldName, entFullNameFldName,
                                         entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                         entOrgNameFldName, dfltRoleTypeCode, roleTypeReadOnly) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    openEntitySelectWinEvtDfltRoleType(entPKFldName, entFullNameFldName,
        entLastNameFldName, entFirstNameFldName,
        entMiddleNameFldName, entOrgNameFldName,
        dfltRoleTypeCode, roleTypeReadOnly, '');
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (event arg)
// Also with default entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinEvtDfltEntCls(entPKFldName, entFullNameFldName,
                                          entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                          entOrgNameFldName, dfltEntityClassCode, entityClassReadOnly, eventName,
                                          entAddrCountryFldName, entAddrStateFldName, entAddrCityFldName) {
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }

    if(typeof entAddrCountryFldName == "undefined")
        entAddrCountryFldName = '';
    if(typeof entAddrStateFldName == "undefined")
        entAddrStateFldName = '';
    if(typeof entAddrCityFldName == "undefined")
        entAddrCityFldName = '';

    var path = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&entityLastNameFieldName=" + entLastNameFldName +
        "&entityFirstNameFieldName=" + entFirstNameFldName +
        "&entityMiddleNameFieldName=" + entMiddleNameFldName +
        "&entityOrgNameFieldName=" + entOrgNameFldName +
        "&eventName=" + eventName +
        "&entityClassCodeArg=" + dfltEntityClassCode +
        "&entityClassCodeArgReadOnly=" + entityClassReadOnly +
        "&entAddrCountryFldName=" + entAddrCountryFldName +
        "&entAddrStateFldName=" + entAddrStateFldName +
        "&entAddrCityFldName=" + entAddrCityFldName;

    return findEntityWithUrl(path);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (event arg)
// Also with default entity class restriction.
// State , Country Code
//-----------------------------------------------------------------------------
function openEntitySelectWinEvtDfltEntClsState(entPKFldName, entFullNameFldName,
                                               entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                               entOrgNameFldName, dfltEntityClassCode, entityClassReadOnly, eventName, addState, addCountry,
                                               entAddrCountryFldName, entAddrStateFldName, entAddrCityFldName) {

    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }

    if(typeof entAddrCountryFldName == "undefined")
        entAddrCountryFldName = '';
    if(typeof entAddrStateFldName == "undefined")
        entAddrStateFldName = '';
    if(typeof entAddrCityFldName == "undefined")
        entAddrCityFldName = '';

    var path = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&entityLastNameFieldName=" + entLastNameFldName +
        "&entityFirstNameFieldName=" + entFirstNameFldName +
        "&entityMiddleNameFieldName=" + entMiddleNameFldName +
        "&entityOrgNameFieldName=" + entOrgNameFldName +
        "&eventName=" + eventName +
        "&entityClassCodeArg=" + dfltEntityClassCode +
        "&entityClassCodeArgReadOnly=" + entityClassReadOnly +
        "&searchCriteria_stateCode=" + addState  +
        "&searchCriteria_countryCode=" + addCountry +
        "&entAddrCountryFldName=" + entAddrCountryFldName +
        "&entAddrStateFldName=" + entAddrStateFldName +
        "&entAddrCityFldName=" + entAddrCityFldName;

    return findEntityWithUrl(path);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (event arg)
// Also with default role type restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinEvtDfltRoleType(entPKFldName, entFullNameFldName,
                                            entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                            entOrgNameFldName, dfltRoleTypeCode, roleTypeReadOnly, eventName) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    var path = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&entityLastNameFieldName=" + entLastNameFldName +
        "&entityFirstNameFieldName=" + entFirstNameFldName +
        "&entityMiddleNameFieldName=" + entMiddleNameFldName +
        "&entityOrgNameFieldName=" + entOrgNameFldName +
        "&eventName=" + eventName +
        "&roleTypeCodeArg=" + dfltRoleTypeCode +
        "&roleTypeCodeArgReadOnly=" + roleTypeReadOnly;

    return findEntityWithUrl(path);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK, full name,  and name parts
// (first, middle, last, org) (event arg)
// Also with default role type / entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinEvtDfltRoleTypeEntCls(entPKFldName, entFullNameFldName,
                                                  entLastNameFldName, entFirstNameFldName, entMiddleNameFldName,
                                                  entOrgNameFldName,
                                                  dfltRoleTypeCode, roleTypeReadOnly,
                                                  dfltEntityClassCode, entityClassReadOnly, eventName) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }
    var path = getCISPath() + "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&entityLastNameFieldName=" + entLastNameFldName +
        "&entityFirstNameFieldName=" + entFirstNameFldName +
        "&entityMiddleNameFieldName=" + entMiddleNameFldName +
        "&entityOrgNameFieldName=" + entOrgNameFldName +
        "&eventName=" + eventName +
        "&roleTypeCodeArg=" + dfltRoleTypeCode +
        "&roleTypeCodeArgReadOnly=" + roleTypeReadOnly +
        "&entityClassCodeArg=" + dfltEntityClassCode +
        "&entityClassCodeArgReadOnly=" + entityClassReadOnly;

    return findEntityWithUrl(path);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name (event arg).
//-----------------------------------------------------------------------------
function openEntitySelectWinFullName(entPKFldName, entFullNameFldName,
                                     eventName, defaultEntityType) {
    if(typeof eventName == "undefined")
        eventName = '';

    openEntitySelectWin(entPKFldName, entFullNameFldName,
        '', '', '', '', eventName, defaultEntityType);
}

//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameAccountNo(entPKFldName, entFullNameFldName, eventName, entAccountNoFldName) {
    openEntitySelectWin(entPKFldName, entFullNameFldName,
        '', '', '', '', eventName, '', '', '', entAccountNoFldName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name and AgentAddlSql (no event arg).
// Added by Jacky (08-13-2008)  to support:
// eFM will pass "&idForAddlSql=AgentAddlSql" in the query string. If idForAddlSql is
// not null, eCIS should look for AddlSql and append it to the query.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameAgentAddlSql(entPKFldName, entFullNameFldName,
                                                 eventName, entClientIdFldName, roleTypeCode, entPolicyNoFldName) {
    openEntitySelectWin(entPKFldName, entFullNameFldName,
        '', '', '', '', eventName, '', entClientIdFldName, '', '', entPolicyNoFldName,
        '', '', '', roleTypeCode)
}

// the following is for entity class restriction on entity selection.
//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name.  (No event arg)
// Also with default entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameDfltEntCls(entPKFldName, entFullNameFldName,
                                               dfltEntityClassCode, entityClassReadOnly) {
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }
    openEntitySelectWinEvtDfltEntCls(entPKFldName, entFullNameFldName,
        '', '', '', '', dfltEntityClassCode, entityClassReadOnly, '');
}

// the following is for role and entity class restriction on entity selection.
//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name.  (No event arg)
// Also with default role / entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameDfltRoleTypeEntCls(entPKFldName, entFullNameFldName,
                                                       dfltRoleTypeCode, roleTypeReadOnly,
                                                       dfltEntityClassCode, entityClassReadOnly) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }
    openEntitySelectWinEvtDfltRoleTypeEntCls(entPKFldName, entFullNameFldName,
        '', '', '', '', dfltRoleTypeCode, roleTypeReadOnly,
        dfltEntityClassCode, entityClassReadOnly, '');
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name.  (event arg)
// Also with default entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameEvtDfltEntCls(entPKFldName, entFullNameFldName,
                                                  dfltEntityClassCode, entityClassReadOnly, eventName) {
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }
    openEntitySelectWinEvtDfltEntCls(entPKFldName, entFullNameFldName,
        '', '', '', '', dfltEntityClassCode, entityClassReadOnly, eventName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name.  (event arg)
// Also with default role type restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameEvtDfltRoleType(entPKFldName, entFullNameFldName,
                                                    dfltRoleTypeCode, roleTypeReadOnly, eventName) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    openEntitySelectWinEvtDfltRoleType(entPKFldName, entFullNameFldName,
        '', '', '', '', dfltRoleTypeCode, roleTypeReadOnly, eventName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name.  (event arg)
// Also with default role type / entity class restriction.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameEvtDfltRoleTypeEntCls(entPKFldName, entFullNameFldName,
                                                          dfltRoleTypeCode, roleTypeReadOnly,
                                                          dfltEntityClassCode, entityClassReadOnly, eventName) {
    if (!dfltRoleTypeCode) {
        dfltRoleTypeCode = "";
    }
    if (!dfltEntityClassCode) {
        dfltEntityClassCode = "";
    }
    openEntitySelectWinEvtDfltRoleTypeEntCls(entPKFldName, entFullNameFldName,
        '', '', '', '',
        dfltRoleTypeCode, roleTypeReadOnly,
        dfltEntityClassCode, entityClassReadOnly, eventName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for subsystemId and externalId, passes the defaultRoleTypeCode.
// Added by Stephen (12-28-2009):
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameExternalId(subsystemIdFldName, externalIdFldName, eventName, defaultRoleTypeCode) {
    openEntitySelectWin('', '', '', '', '', '', eventName, '', '', '', '', '', subsystemIdFldName, externalIdFldName, defaultRoleTypeCode);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name and entPolicyNo (no event arg).
// Added by Jacky (12-12-2008):
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNamePolicyNo(entPKFldName, entFullNameFldName, eventName, entPolicyNoFldName) {
    openEntitySelectWin(entPKFldName, entFullNameFldName,
        '', '', '', '', eventName, '', '', '', '', entPolicyNoFldName);
}

//-----------------------------------------------------------------------------
// Opens the entity selection window for PK and full name with params.
//-----------------------------------------------------------------------------
function openEntitySelectWinFullNameWithParams(entPKFldName, entFullNameFldName, paramKeys, paramValues, eventName) {
    var path =getCISPath()+ "/ciEntitySelectSearch.do?entityPKFieldName=" + entPKFldName +
        "&entityFullNameFieldName=" + entFullNameFldName +
        "&eventName=" + eventName;
    // Get the params, and added to the path.
    for (var i = 0; i < paramKeys.length; i++) {
        path += "&" + paramKeys[i] + "=" + paramValues[i];
    }

    return findEntityWithUrl(path);
}

function openEntitySelectWithParams(params) {
    var url = getCISPath() + "/ciEntitySelectSearch.do?";

    for (var p in params) {
        if (params.hasOwnProperty(p)) {
            url += "&" + p + "=" + encodeURIComponent(params[p]);
        }
    }

    findEntityWithUrl(url);
}

// for a given url, function to prepare the url for ajax call by adding process = peekAtSearchResult
function substituteParmametersForPeekingResults(url){
    var ajaxUrl = url;
    if (ajaxUrl.indexOf("process=") != -1) {
        ajaxUrl = ajaxUrl.replace('process=', "processReplaced=");
    }
    ajaxUrl += "&process=peekAtSearchResult";
    ajaxUrl += "&date= " + new Date();
    return ajaxUrl;
}

// Remove left space and right space
function textTrim(txt) {
    return txt.replace(/(^\s*)|(\s*$)/g, '');
}

//-----------------------------------------------------------------------------
// Generate report(PDF Report).
//-----------------------------------------------------------------------------
function viewCIReport(parmsObj) {
    var url = getAppPath() + "/reportmgr/maintainCIReport.do?"
        + "&process=generateReport";
    // Generate the url from the parmsObj.
    if (parmsObj != null) {
        var str = "";
        for (var parm in parmsObj) {
            str += "&" + parm + "=" + parmsObj[parm];
        }
        url = url + str;
    }
    window.open(url, 'REPORT', 'resizable=yes,width=800,height=600');
}

function validateEMail(eMailFld) {

    if (isFieldMasked(eMailFld)) return '';

    if (eMailFld.value == null || eMailFld.value == '') {
        return '';
    }
    // Reg exp for bad e-mail addresses.
    var reNoGood = /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)/;
    // not valid
    // Reg exp for good e-mail addresses.
    var reGood = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    // valid
    if (!reNoGood.test(eMailFld.value) && reGood.test(eMailFld.value)) {
        return '';
    }
    else {
        return getMessage("ci.common.error.format.email", new Array(getLabel(eMailFld.name), "username@domain.topleveldomain")) + "\n";
    }
}
