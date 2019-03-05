//-----------------------------------------------------------------------------
// Javascript file for maintainUnderwriter.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 23, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/23/2010       syang       Issue 108651 - Updated handleOnChange() to handle Renew indicator when change effective to date.
// 11/25/2011       syang       127661 - Modified handleOnButtonClick() to refresh parent page if dataSavedB is Y.
// 06/05/2013       awu         138241 - 1. Add addUnderwriterTeam, changeUnderwriter, handleOnExpireUnderwriting, addUnderwriterTeam.
//                                       2. Modified handleOnChange to handle add underwriter logic.
//                                       3. Modified handleOnLoad to select the current row.
//07/30/2013        awu         147025 - Added a new variable originalEntityId to handle the entity change logic.
//11/22/2013        awu         148974 - Modified addUnderwriterTeam to remove calling enableFieldsForSubmit.
//26/11/2014        kxiang      158853 - Modified handleOnLoad to call new added function setGridNameHref.
//12/30/2014        jyang       159787 - Removed unused function underwriterListGrid_setInitialValues().
//08/18/2015        awu                - Add back the function underwriterListGrid_setInitialValues().
//                                       This function was removed in issue 159787 by mistake. It will be called
//                                       by the inactive button 'Add Member'.
//03/10/2017        wli         180675 - Used getOpenCtxOfDivPopUp() to call "openDivPopup".
//01/29/2018        wrong       191120 - 1. Added setUnderwriterNameForParentWindow() to set underwriter name for
//                                          parent window.
//                                       2. Modified handleOnLoad() to set underwriter name for parent window after
//                                          saving action instead of invoking it when submit underwriter form.
//08/23/2018        xnie        194578 - Modified setUnderwriterNameForParentWindow() to replace CENTITYIDLOVLABEL with
//                                       CTERMLATESTUW.
//10/17/18          xgong       195889 - Updated handleOnButtonClick/setUnderwriterNameForParentWindow/setGridNameHref for gird replacement
//-----------------------------------------------------------------------------
//indicator of data updating
var isUpdated = false;
var originalTypeCode;
var originalEntityId;
function handleOnChange(obj) {
    if (obj.name == 'legacyPolicyNo' ||
            obj.name == 'inceptionDate' ||
            obj.name == 'facultativeB' ||
            obj.name == 'char1' ||
            obj.name == 'char2' ||
            obj.name == 'char3' ||
            obj.name == 'date1' ||
            obj.name == 'date2' ||
            obj.name == 'date3' ||
            obj.name == 'num1' ||
            obj.name == 'num2' ||
            obj.name == 'num3') {
        getObject("addlPolicyInfoChangedB").value="Y";
    }
    else if (obj.name == 'entityId') {
        changeUnderwriter();
    }

    else if (obj.name == "uwTypeCode") {
        if (obj.value == "UNDWRITER") {
            obj.value = originalTypeCode;
            alert(getMessage("pm.maintainUnderwriter.addUnderwriter.indicator"));
            for (var i = 0; i < obj.options.length; i++) {
                if (obj.options[i].value == originalTypeCode) {
                    obj[i].selected = true;
                    break;
                }
            }
            return;
        }
        else if (originalTypeCode == "UNDWRITER") {
            underwriterListGrid1.recordset("CREGIONALTEAMCODE").value = "";
            underwriterListGrid1.recordset("CREGIONALTEAMCODELOVLABEL").value = "";

        }
    }
    // Issue 108651, handle Renew indicator.
    else if (obj.name == "effectiveToDate") {
        var effectiveToDate = getObjectValue("effectiveToDate");
        var termExpirationDate = policyHeader.termEffectiveToDate;
        enableDisableRenewIndicator(effectiveToDate, termExpirationDate, "renewalB", "isRenewalBAvailable", "underwriterListGrid");
    }
}

function changeUnderwriter() {
    var entityId = getObject("entityId").value;
    if (isEmpty(entityId)) {
        alert(getMessage("pm.maintainUnderwriter.invalidSelectedEntity.error"));
        getObject("entityId").value = originalEntityId;
        return;
    }
    else {
        originalEntityId = entityId;
    }
    var type = getObjectValue("uwTypeCode");
    if (type == 'UNDWRITER') {
        showProcessingDivPopup();
        var path = getAppPath() + "/policymgr/underwritermgr/maintainUnderwriter.do?"
                + "process=getUnderwriterTeam"
                + "&entityId=" + getObject("entityId").value
                + "&termEff=" + policyHeader.termEffectiveFromDate
                + "&termExp=" + policyHeader.termEffectiveToDate;
        new AJAXRequest("get", path, '', handleOnExpireUnderwriting, false);
    }
}

function handleOnExpireUnderwriting(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            var teamCode = oValueList[0]["regionalTeamCode"];
            var entityId = oValueList[0]["entityId"];
            var changeTeamB = oValueList[0]["changeTeamB"];
            if (changeTeamB == 'Y') {
                if (confirm(getMessage("pm.maintainUnderwriter.expireOtherRoles.confirm", new Array(teamCode)))) {
                    showProcessingDivPopup();
                    setInputFormField("entityId", entityId);
                    alternateGrid_update('underwriterListGrid');
                    enableFieldsForSubmit(document.forms[0]);
                    document.forms[0].process.value = "addTeamMembers";
                    baseOnSubmit(document.underwriterList);
                }
                else {
                    closeProcessingDivPopup();
                }
            }
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SHOW_ALL':
            var url = "maintainUnderwriter.do?" + commonGetMenuQueryString() + "&process=loadAllUnderwriters";
            setWindowLocation(url);
            break;
        case 'SHOW_TERM':
            var url = "maintainUnderwriter.do?" + commonGetMenuQueryString() + "&process=loadTermUnderwriters";
            setWindowLocation(url);
            break;
        case 'CLOSE_DIV':
            if(isNeedToRefreshParentB()){
               getParentWindow().refreshPage();
            }
            break;
        case "ADD_UNDERWRITER":
            var url = getAppPath() + "/policymgr/underwritermgr/addUnderwriter.do?" + commonGetMenuQueryString()
                    + "&process=display";
            var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", 780, 300, "", "", "", false);
    }

}

function isNeedToRefreshParentB(){
    return getObjectValue("dataSavedB") == 'Y';
}

function handleOnSubmit(action) {
    var proceed=false;

    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllUnderwriters";
            captureTransactionDetails("ENDPOLADD", "submitForm");    //TLDECLINE, ENDPOLADD
            break;
    }

    return proceed;
}

function setUnderwriterNameForParentWindow() {
    var selectedDataGrid = getXMLDataForGridName("underwriterListGrid");
    var rowId = selectedDataGrid.recordset("ID").value;
    selectedDataGrid.recordset.movelast();
    var underwriterName = selectedDataGrid.recordset("CTERMLATESTUW").value;
    first(selectedDataGrid);
    getRow(selectedDataGrid,rowId);

    if (!hasErrorMessages) {
        getParentWindow().setUnderwriter(underwriterName);
    }
}

function underwriterListGrid_selectRow() {
    originalTypeCode = getObjectValue("uwTypeCode");
    originalEntityId = getObjectValue("entityId");
}

function underwriterListGrid_setInitialValues() {
    var path = getAppPath() + "/policymgr/underwritermgr/maintainUnderwriter.do?"
            + "process=getInitialValuesForUnderwriter"
            + "&policyTermHistoryId=" + getObject("policyTermHistoryId").value
            + "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate
            + "&termEffectiveToDate=" + policyHeader.termEffectiveToDate;

    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}

function addUnderwriterTeam(underwriterArray) {
    showProcessingDivPopup();
    var undwriStr = underwriterArray[0] + " @ " + underwriterArray[1] + " @ " + underwriterArray[2] + " @ "
            + underwriterArray[3] + " @ " + underwriterArray[4] + " @ " + underwriterArray[5];
    setInputFormField("underwriterParams", undwriStr);
    alternateGrid_update('underwriterListGrid');
    document.forms[0].process.value = "addUnderwriterTeam";
    document.forms[0].action = buildMenuQueryString("", getFormActionAttribute());
    baseOnSubmit(document.underwriterList);
}


function handleOnLoad() {
    originalTypeCode = getObjectValue("uwTypeCode");
    originalEntityId = getObjectValue("entityId");
    if (getObjectValue("isSaveAction") == 'Y') {
        setUnderwriterNameForParentWindow();
    }
    if (!isNaN(currentUnderwritingId)) {
        selectRowById("underwriterListGrid", currentUnderwritingId);
    }
    setGridNameHref();
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function setGridNameHref() {
    var xmlData = getXMLDataForGridName("underwriterListGrid");
    var recordSet = xmlData.recordset;
    var fieldCount = recordSet.Fields.count;
    var recordCount = recordSet.recordCount;
    for(var i = 0; i < recordCount; i++) {
        //when add underwriter with name filled or first open underWriting Team page with no data
        // then no need to set name href.
        if(!isEmpty(recordSet("CENTITYID").value) ||
                !getTableProperty(getTableForGrid("underwriterListGrid"), "hasrows")) {
            continue;
        }
        var entityHref = replace(recordSet("CENTITYIDHREF").value, "\'", "\\\'");
        var entityCount;
        for (var j = 0; j < fieldCount; j++) {
            if (recordSet.Fields.Item(j).name == "CENTITYID") {
                entityCount = j;
            }
            if (recordSet.Fields.Item(j).name.substr(4) == "" + entityCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(entityHref)) {
                    href = "javascript:handleOnGridHref('underwriterListGrid', '" + entityHref + "');";
                }
                xmlData.recordset.Fields.Item(j).value = href;
            }
        }
    }
}
