//-----------------------------------------------------------------------------
// Common iFrame javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   January 19, 2017
// Author: eyin
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/10/2017       eyin        180675 - Initial version to display page in tab style.
// 05/11/2017       lzhang      185023 - Modified isReservedTab() for policy summary page.
// 06/19/2017       ssheng      185402 - Add function showWBWithMainPageCode and showWBWithSubPageCode.
// 06/23/2017       ssheng      185382 - Add TAIL to allowedToModifyTabArr, tail page can be modified
//                                       and save even when official policy.
// 07/24/2017       eyin        185377 - 1) Modified showPageInFrame(), added parameter noLoadingDiv = true.
//                                       2) Modified showSubContent(), to display blank page in iFrame by default.
//                                       3) Added ignoreProgressIndicator(), to check if need to display processing
//                                          indicator after iFrame page is submitted.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
// 08/24/2017       wrong       187831 - 1) Modified highlightTab() to add logic to process user pressing backspace
//                                          case in UI Tab style.
//                                       2) Added getIframePageId() and getTabIdByIframePage() to get page Id
//                                          and tab Id.
//                                       3) Added highlightWhenBackSpaceCase() to high light tab when "Backspace" case.
// 09/26/2017       lzhang      188568 - Modified showWBWithSubPageCode(): use main page code as workbenchUrl
//                                       when iframe page code is undefined
// 09/22/2017       eyin        169483 - Enhanced to support for Risk Additional Exposure
// 10/20/2017       eyin        188810 - Changed to call highlightWhenBackSpaceCase() only after 'Backspace' is pressed.
// 11/09/2017       tzeng       187689 - 1) Add skipAutoSave, setSkipAutoSave(), getSkipAutoSave().
//                                       2) Changed getFirstTab() to show ADDTL_EXPOSURE tab as the first tab when there
//                                          have grid data on risk summary page.
//                                       3) Changed getIFrameWindow(), hideButtonsInSubTab(), hideButtons() and added
//                                          processAutoSaveSubTab(), pageGridDepended_tabArray, commonIsOkToPendSubTab()
//                                          , doNotAutoSaveSubTab(), handleOnClearOperation(), isPreviewButtonClicked()
//                                          to show the renamed save button, support system parameter PM_AUTO_SAVE_WIP N
//                                          to prompt when leave the sub tab which had changes without save.
//                                       4) Changed handleOnAutoSaveWhenSwitchPrimaryTab() refactor code.
// 12/06/2017       eyin        189984 - Modified getTabNameByTabId() to get previous cache tab as default tab id.
// 12/12/2017       wrong       190014 - 1) Modified handleOnSubTabOnload(), showSubContent(), selectTabById() and
//                                          processSubTabAfterOnload() to add logic to set sub tab italic style when
//                                          clicking save button in sub tab.
//                                       2) Modified commonOnUIProcess() to add an additional condition to check if it
//                                          has been selected by tab Id.
// 12/13/2017       wrong       190191 - 1) Delete function ignoreErrorMessageCheck. No data found error message has
//                                          been handled in gui.js for sub tab, so there is no need to invoke
//                                          ignoreErrorMessageCheck() in hasErrorMessage().
//                                       2) Modified hasErrorMessage() to delete invoking ignoreErrorMessageCheck().
//                                       3) Modified handleonSubTabOnLoad() move invoking workFlow logic from
//                                          commonOnCallBackAutoSaveForFrame to processSubTabAfterOnload for clicking
//                                          save button to save sub tab data in tab style case.
//                                       4) Modified hideButtonsInSubTab to hide update and rate button which is on the
//                                          top position of tail sub tab.
// 12/13/2017       eyin        190085 - Modified processAutoSaveSubTab(), changed to do NOT process some tabs automatically.
// 12/15/2017       eyin        190085 - Added JS function handleOnRemoveMessages();
// 12/20/2017       lzhang      189983 - Remove keyup();
// 12/22/2017       tzeng       190451 - Modified handleOnAutoSaveWhenSwitchPrimaryTab() to replace autoSaveSubTab with
//                                       processAutoSaveSubTab to not process auto save sub tab for the sub tabs defined
//                                       in doNotAutoProcess_TabArr when click primary tab.
// 12/28/2017       tzeng       190488 - 1) Modified processAutoSaveSubTab() and added autoSaveProcessingB flag to wrap
//                                          this auto save indicator when call autoSaveSubTab().
//                                       2) Add commonOnRevertSubTabChangesBeforeLeave() to revert WIP changes before
//                                          leave sub tab. Modified doNotAutoSaveSubTab() to call this new method.
// 01/02/2018       wrong       190192 - Modified processSubTabAfterOnload to add an additional condition to ensure
//                                       system will not call handleOnUnloadForDivPopup in no data change situation.
// 01/10/2018       eyin        190766 - Modified ExeWorkFlow(), Add back the logic to call isCallBackAutoSaveForFrameB(),
//                                       which was lost in the 3rd change of issue#190191.
// 01/16/2018       eyin        190859 - 1) Modified handleOnSubTabOnload(), to remove function isCallBackAutoSaveForFrameB;
//                                       2) Added variable workFlowPopupOpenedB, and the get/set function.
//                                       3) Modified ExeWorkFlow() to avoid opening 2 workflow popup, and moved it to
//                                          handleOnSubTabOnload(), to execute it firstly.
//                                       4) Moved isExeInvokeWorkflowInCallBack() from common.js to commonSecondlyTab.js
//                                       5) Modified hideButtonsInSubTab(), hide Rate button on Tail sub-tab when policy
//                                          view mode is WIP.
// 06/11/2018       cesar       193651 - 1) Modified showSubContent() to include page token.
//                                       2) converted to be browser independent
// 08/17/2018       wrong       195160 - Add QUOTE_STATUS into allowedToModifyTabArr to allow processing auto-save when
//                                       policy in official status.
// 08/23/2018       wrong       194175 - Modified getFirstTab() to add validation for COI tab access if system has no
//                                       access to addtl-exposure tab in risk summary page.
// 09/07/2018       wrong       195567 - 1) Modified commonOnUIProcess to remove calling commonOnProcessIframe logic.
//                                       2) Modified handleOnSubTabOnload() to add logic to call/stop timer for iframe
//                                          height adjustment.
//                                       3) Added new function stopInterval().
//                                       4) Modified createTimerForIframeHeightReset() to add new flag.
// 09/06/2018       xnie        195106 - Modified getFirstTab() to introduce a system parameter to decide which tab
//                                       system should display as the first tab on policy information page.
// 09/19/2018       tyang       195522 - Modified getFirstTab() to introduce a system parameter to decide which tab
//                                       system should display as the first tab on coverage information page.
// 09/10/2018       cesar       194821 - modified hideButtons() to retrieve only elements with type button
// 10/15/2018       wrong       188391 - Enhanced to support for Underlying coverage.
// 10/18/2018       xgong       195889 - Updated getIframePageId/commonOnUIProcess/showWBWithSubPageCode
//                                       for grid replacement
// 10/26/2018       xgong       195889 - Updated showSubContent/getTabIdWOUnderScore for grid replacement
//-----------------------------------------------------------------------------
var switchSecondlyTabFlg = false;
var switchGridRowFlg = false;
var switchPrimaryTabFlg = false;
var nextPrimaryTabId;
var nextPrimaryTabAction;
var rollback = false;
var autoSaveProcessingB = false;
/*
 * skipAutoSave can be set as true,
 * it means auto save logic should be skipped in XXXXGrid_selectRow() when select row is involved by user's action
 */
var skipAutoSave = false;
var operation  = undefined;
var requiredSubmitMainPageTabArr = [];
var subFrameId;
var secondlyTabDivId;
var secondlyTabPrefix;
var secondlyTabMenuGroupId;
var reservedTabArrObj = {
    "policyPageFrame": ["COMPONENT"],
    "riskPageFrame": ["RISK"],
    "riskSummaryPageFrame": ["RISK"],
    "coveragePageFrame": ["COVERAGE","COMPONENT"],
    "coverageClassPageFrame": ["COVERAGE_CLASS"]
};
var tokenName = "org.apache.struts.taglib.html.TOKEN";
var isMainPageRefreshedFlg = true;
var map_popID_parentWindow = {};
var secondlyTabRegExpGroup = ['PM_MAINTAIN_', 'PM_MNT_', 'PM_VIEW_', 'PM_RISK_', 'PM_PROCESS_', 'PM_'];
var specialPageTabIdMapping = {
    "policyPageFrame": {"PM_ADDI_INSURED": "ADDIINS",
                        "PM_SPECIALHANDLING": "SPHND",
                        "PM_VIEW_FUND": "TAX",
                        "PM_CHANGE_TERM_EXP": "CHGEXPDATE",
                        "PM_SELECT_AUDIT_LEVEL": "AUDIT",
                        "PM_UNDER_POL": "UNDERLYING",
                        "PM_EXCESS_COVERAGE": "EXCESS",
                        "PM_VIEW_PROF_ENTITY_DETAIL": "ENTITYDTL",
                        "PM_VIEW_POLICY_ADMIN_HISTORY": "ADMINHISTORY",
                        "PM_SCHEDULE": "SCHEDULE",
                        "PM_SEL_ADDRESS": "SELECTADDRESS",
                        "PM_MAINTAIN_TAX": "MAINTAINTAX"},
    "riskPageFrame": {"PM_RISK_SURCHG_POINTS": "SURCHARGEPOINTS",
                      "PM_CREATE_POLICY": "COPYNEW",
                      "PM_SEL_ADDRESS": "SELECTADDRESS",
                      "PM_VIEW_INSURED_HISTORY": "HISTORY",
                      "PM_RISK_RELATION": "RISKRELATION"},
    "riskSummaryPageFrame": {"PM_RISK_SURCHG_POINTS": "SURCHARGEPOINTS",
                             "PM_CREATE_POLICY": "COPYNEW",
                             "PM_SEL_ADDRESS": "SELECTADDRESS",
                             "PM_VIEW_INSURED_HISTORY": "HISTORY",
                             "PM_RISK_RELATION": "RISKRELATION"},
    "coveragePageFrame" : {"PM_VL_COVERAGE": "VLCOVG",
                           "PM_MANU_EXCESS_PREMIUM": "MANEXCESSPREM",
                           "PM_MANUSCRIPT": "MANU",
                           "PM_MINI_TAIL": "MINI",
                           "PM_SCHEDULE": "SCH",
                           "PM_PRIOR_ACTS": "PRIORACT"}
};
// The relation between pageFrame and pageGrid is one-one, the relation between pageGrid and pageTab is one-many.
var pageGridDepended_tabArray = {
    "riskPageFrame":
        {"riskListGrid": ["ADDTL_EXPOSURE",
                          "COI",
                          "AFFILIATION",
                          "EMPPHYS",
                          "COPYALL",
                          "RISK_RELATION",
                          "SCHEDULE",
                          "NATIONAL_PROGRAM",
                          "SELECT_ADDRESS",
                          "INSURED_TRACKING"]
        },
    "coveragePageFrame":
        {"coverageListGrid": ["PRIOR_ACT",
                              "MANU",
                              "MAN_EXCESS_PREM",
                              "SCH"]
        }
}
var lastLeaveReservedTabId = undefined;
var invokedByBackspaceB = false;
//The variable indicates if system has set the italic info when switching sub tab.
var hasSetItalicForSubTab = false;
//The variable indicates the count which is used to record the number of invoking selectTabBy(subTabId).
var handleonSubTabCount = 0;

// issue#190085, for some tabs, do NOT process/apply/save automatically once user tries to switch grid row/sub-tab.
var doNotAutoProcess_TabArr = ["COMP_UPDATE", "COPYALL", "DELETEALL", "CHG_EXP_DATE"];

var workFlowPopupOpenedB = false;
var adjFrameHeightInterval;
var intervalStartFlag = false;
//-----------------------------------------------------------------------------
// sync the token value with iframe once iframe auto save successfully
//-----------------------------------------------------------------------------
function updateMainTokenWithIframe(ifrmObj) {
    if(eval("window.setObjectValue") && eval("ifrmObj.contentWindow.getObjectValue")){
        setObjectValue(tokenName,ifrmObj.contentWindow.getObjectValue(tokenName));
    }
}

//-----------------------------------------------------------------------------
// cache the previous and current(target) tab ids in request scope
//-----------------------------------------------------------------------------
function setCacheTabIds(tabIds) {
    setInputFormField("cacheTabIds",tabIds);
}

//-----------------------------------------------------------------------------
// cache the previous and current(target) row ids in request scope
//-----------------------------------------------------------------------------
function setCacheRowIds(rowIds) {
    setInputFormField("cacheRowIds",rowIds);
}

function setBtnOperation(operation) {
    setInputFormField("cacheBtnOperation",operation);
}

function clearCacheTabIds() {
    setCacheTabIds("");
}

function clearCacheRowIds() {
    setCacheRowIds("");
}

function clearBtnOperation() {
    setBtnOperation("");
}

function getPreviousTab() {
    return getObjectValue("cacheTabIds").split(",")[0];
}

function getCurrentTab() {
    return getObjectValue("cacheTabIds").split(",")[1];
}

function getPreviousRow() {
    return getObjectValue("cacheRowIds").split(",")[0];
}

function getCurrentRow() {
    return getObjectValue("cacheRowIds").split(",")[1];
}

function setSkipAutoSave(flag){
    skipAutoSave = flag;
}

function getSkipAutoSave(){
    return skipAutoSave;
}

function setAutoSaveProcessingB(flag){
    autoSaveProcessingB = flag;
}

function getAutoSaveProcessingB(){
    return autoSaveProcessingB;
}

function setWorkFlowPopupOpenedB(openedB) {
    workFlowPopupOpenedB = openedB;
}

function getWorkFlowPopupOpenedB() {
    return workFlowPopupOpenedB;
}

function getBtnOperation() {
    if(isEmpty(getObjectValue("cacheBtnOperation"))){
        return undefined;
    }else{
        return getObjectValue("cacheBtnOperation");
    }
}

function needAutoProcessing(tabId) {
    return $.inArray(tabId, doNotAutoProcess_TabArr) == -1;
}

function handleOnPutParentWindowOfDivPopup(popupDivId, parentWindowFlag){
    map_popID_parentWindow[popupDivId] = parentWindowFlag;
}

function getParentWindowOfDivPopup(popupDivId){
    return map_popID_parentWindow[popupDivId];
}

function selectTabById(tabId) {
    if(eval("window.currentTabIsHideForCurrentRow") && currentTabIsHideForCurrentRow(tabId)){
        tabId = getFirstTab();
    }
    if(isMainPageRefreshedFlg){
        isMainPageRefreshedFlg = false;
    }
    handleonSubTabCount ++;
    handleOnShowPageInFrame(tabId);
}

function selectFirstTab() {
    selectTabById(getFirstTab());
}

function getReservedTabArr() {
    return eval("reservedTabArrObj." + subFrameId);
}

function isReservedTab(tabId) {
    if(subFrameId == "policySummaryPageFrame"){
        return true;
    }
    return $.inArray(tabId, getReservedTabArr()) > -1 ? true : false;
}

function getFirstTab(){
    //handle first tab in risk summary page.
    if (subFrameId == "riskSummaryPageFrame") {
        var riskSummaryPageFirstTab = isEmptyRecordset(riskListGrid1.recordset) ? "RISK_RELATION" : "ADDTL_EXPOSURE";

        if (eval("window.currentTabIsHideForCurrentRow") && riskSummaryPageFirstTab == "ADDTL_EXPOSURE") {
            if (currentTabIsHideForCurrentRow(riskSummaryPageFirstTab)) {
                if (currentTabIsHideForCurrentRow("COI")) {
                    riskSummaryPageFirstTab = "RISK_RELATION";
                } else {
                    riskSummaryPageFirstTab = "COI";
                }
            }
        }

        return riskSummaryPageFirstTab;
    }
    else {
        if (subFrameId == "policyPageFrame") {
            var policyPageFirstTab = getSysParmValue("PM_POL_FIRST_SUB_TAB");

            return policyPageFirstTab;
        }
        else if (subFrameId == "coveragePageFrame") {
            var coveragePageFirstTab = getSysParmValue("PM_COV_FIRST_SUB_TAB");
            return coveragePageFirstTab;
        }
        else {
            return getReservedTabArr()[0];
        }
    }
}

//-----------------------------------------------------------------------------
// check if any error message exists in main page after frame auto save
//-----------------------------------------------------------------------------
function hasErrorMessage() {
    return  hasErrorMessages
           || ($("#" + subFrameId).attr("src") != ""
               && isDefined(getIFrameWindow().hasErrorMessages)
               && (isDefined(getIFrameWindow().hasErrorMessages)? getIFrameWindow().hasErrorMessages : false)
                );
}

function handleOnSubTabOnload() {
    //isInvokedBySelectBySubTabId indicates if function handleOnSubTabOnload is triggered by selectTabBy sub tab Id.
    var isInvokedBySelectBySubTabId = true;
    if (!intervalStartFlag && isDefined(getCurrentTab()) && !isReservedTab(getCurrentTab())) {
        commonOnProcessIframe($("#" + subFrameId)[0]);
    }
    if (intervalStartFlag && isDefined(getCurrentTab()) && isReservedTab(getCurrentTab())) {
        stopInterval(adjFrameHeightInterval);
    }
    mainPageLock.unlock();
    // Issue 190859 - Moved ExeWorkFlow() from processSubTabAfterOnload() to here
    //              - Execute work flow firstly.
    ExeWorkFlow();
    if (handleonSubTabCount == 0) {
        isInvokedBySelectBySubTabId = false;
    }

    //Issue 190859 - Continue to process following logic after workflow is done.
    if(!isWorkFlowPopupOpened()){
        if(switchSecondlyTabFlg || switchGridRowFlg || (operation && !isReservedTab(getPreviousTab())) || switchPrimaryTabFlg){
            commonOnCallBackAutoSaveForFrame();
        }

        processSubTabAfterOnload(isInvokedBySelectBySubTabId);
    }
}

function commonOnCallBackAutoSaveForFrame(){
    callBackAutoSaveForFrame(!hasErrorMessage());
}

/**
 * if the main page is first time load or click RATE/SAVE WIP/OFFICIAL button to refresh the main page, returns true,
 * else return false
 * @returns {boolean}
 */
function isMainPageRefreshed() {
    return ((getPreviousRow() == getCurrentRow()) && (getPreviousTab() == getCurrentTab()));
}

function requiredSubmitMainPage(tabId) {
    return $.inArray(tabId, requiredSubmitMainPageTabArr) > -1 ? true : false;
}

function commonOnUIProcess() {
    commonOnAdjustPageSize();
    var isExistFunction = eval("window.handleOnUIProcess");
    if(isExistFunction) {
        handleOnUIProcess();
    }

    /*
     * After click Copy button to copy Endorsement Quote, if no tab is selected after main page loaded,
     * then load current sub-tab
     */
    var pageCodeId;
    var iframeId = $("iframe").attr("id");
    if(iframeId != undefined) {
        if (document.getElementById(iframeId).contentWindow.pageCode == undefined
            && !isReservedTab(getCurrentTab())
            && handleonSubTabCount == 0){
            selectTabById(getCurrentTab());
        }
    }
}

//-----------------------------------------------------------------------------
// return iframe screen based on iframe id subFrameId
//-----------------------------------------------------------------------------
function getIFrameWindow(oWindow, iFrameId) {
    var _subFrameId;
    if(isDefined(iFrameId)){
        _subFrameId = iFrameId;
    }
    else if(isDefined(subFrameId)){
        _subFrameId = subFrameId;
    }
    else if(isDefined(oWindow) && isDefined(oWindow.subFrameId)){
        _subFrameId = oWindow.subFrameId;
    }else{
        return null;
    }

    if(isDefined(oWindow)){
        return oWindow.getObject(_subFrameId).contentWindow;
    }else if(getObject(_subFrameId)){
        return getObject(_subFrameId).contentWindow;
    }else{
        return null;
    }

}


//-----------------------------------------------------------------------------
// set a timer task to reset the Iframe height as per its content
//-----------------------------------------------------------------------------
function createTimerForIframeHeightReset(obj) {
    intervalStartFlag = true;
    adjFrameHeightInterval = setInterval(function() {
        obj.height=isNull(obj.contentDocument.body) //Fix obj.contentWindow.document.documentElement is null in Chrome
                ? (!isNull(obj.contentWindow.document.documentElement)? obj.contentWindow.document.documentElement.scrollHeight : 0)
                : obj.contentDocument.body.scrollHeight;
    }, 200);
}

//-----------------------------------------------------------------------------
// Stop Timer for iframe height reset
//-----------------------------------------------------------------------------
function stopInterval(intervalId) {
    clearInterval(intervalId);
    intervalStartFlag = false;
}

//-----------------------------------------------------------------------------
// reset the iframe height with a timer every certain time
//-----------------------------------------------------------------------------
function resetIframeHeight(obj) {
    createTimerForIframeHeightReset(obj);
}

//-----------------------------------------------------------------------------
// common function to process Iframe post main page loaded
//-----------------------------------------------------------------------------
function commonOnProcessIframe(obj) {
    resetIframeHeight(obj);
}

//-----------------------------------------------------------------------------
// common function to secondary tab click
//-----------------------------------------------------------------------------
function commonOnSecondaryTabClick(tabId) {
    var isExistFunction = eval("window.handleOnSecondaryTabClick");
    if(isExistFunction) {
        handleOnSecondaryTabClick(tabId);
    }
}

function setEventHandle(handler) {
    eventHandler = handler;
}

//-----------------------------------------------------------------------------
// define a lock for main page to avoid user try to operate when sub tab
// is doing auto save or loading content
//-----------------------------------------------------------------------------
var mainPageLock = new MainPageLock();
function MainPageLock() {

    this.lockId = "lockDialog";

    /**
     *  initial the lock before main page onload called
     */
    this.initialLock = function() {
        var lockDiv = $("<div></div>");
        lockDiv.attr("id", this.lockId);
        lockDiv.attr("align", "center");

        var img = $("<img/>");
        img.attr("src", getCorePath() + "/images/running.gif");
        img.attr("alt", "saving");

        var span = $("<span></span>");
        span.addClass("txtOrange");
        span.text(getMessage("label.process.info.processing"));

        lockDiv.append(img);
        lockDiv.append(span);

        lockDiv.hide();
        $("body").prepend(lockDiv);
        var dialogConfig = {
            modal:true,
            autoOpen:false,
            closeOnEscape:false,
            dialogClass:'savingIndicator',
            resizable:false,
            draggable:false,
            height:67,
            width:192
        };
        $(lockDiv).dialog(dialogConfig);
    }

    /**
     * below situation need to add main page lock
     * 1. before auto save sub tabs except reserved tabs '@reference isReservedTab()'
     * 2. select sub tab except reserved tabs '@reference isReservedTab()' to load it's content
     */
    this.lock = function() {
        $("#" + this.lockId).dialog("open");
        if(hasObject(this.lockId)){
            getSingleObject(this.lockId).innerHTML = getSingleObject(this.lockId).innerHTML;
        }
    }

    /**
     * below situation need to unlock main page
     * 1. sub tab load completed
     * 2. auto save validate failed
     * 3. auto save returned no data change
     */
    this.unlock = function() {
        if(this.isLocked()) {
            $("#" + this.lockId).dialog("close");
        }
    }

    this.isLocked = function() {
        return $("#" + this.lockId).dialog("isOpen");
    }
}

function showPageInFrame(url, divId, tabId) {
    if(!isReservedTab(tabId) && !mainPageLock.isLocked()) {
        mainPageLock.lock();
    }
    setCacheTabIds(tabId + "," + tabId);
    highlightTab(tabId);

    url = url + "&noLoadingDiv=true";
    showSubContent(url, divId);
}

function highlightTab(tabId, isBackSpaceCase) {
    var liElements = $('#' + secondlyTabMenuGroupId).children('li');
    liElements.each(function(i) {
        var liElement = $(this);
        var aElement = liElement.children("a:first");
        var spanElement = aElement.children("span:first");
        var tabIdMappedByPage = aElement.attr('id')
                                ? aElement.attr('id').replace(secondlyTabPrefix, "")
                                : aElement.attr('id');
        if ((aElement.attr('id') == secondlyTabPrefix + tabId) ||
             (tabIdMappedByPage ? tabIdMappedByPage.replace(/_/g, "") : tabIdMappedByPage) == tabId) {
            if (isBackSpaceCase) {
                setCacheTabIds(tabIdMappedByPage + "," + tabIdMappedByPage);
            }
            liElement.removeClass('tab');
            liElement.removeClass('firstTab');
            aElement.removeClass('tab');
            aElement.removeClass('firstTab');
            spanElement.removeClass('tabWithNoDropDownImage');
            if (i == 0) {
                liElement.addClass('firstSelectedTab');
                aElement.addClass('firstSelectedTab');
            }
            else {
                liElement.addClass('selectedTab');
                aElement.addClass('selectedTab');
            }
            spanElement.addClass('selectedTabWithNoDropDownImage');
        }
        else {
            liElement.removeClass('firstSelectedTab');
            liElement.removeClass('selectedTab');
            aElement.removeClass('firstSelectedTab');
            aElement.removeClass('selectedTab');
            spanElement.removeClass('selectedTabWithNoDropDownImage');
            if (i == 0) {
                liElement.addClass('tab');
                aElement.addClass('firstTab');
            }
            else {
                liElement.addClass('tab');
                aElement.addClass('tab');
            }
            spanElement.addClass('tabWithNoDropDownImage');
        }
    });
}

function showSubContent(url, id) {
    var iFrameObj = getObject(subFrameId);
    if(url != "") {
        url = dti.csrf.setupCSRFTokenForUrl(url);
        iFrameObj.src = url;
        iFrameObj.width = "98%";
    }
    $("#" + secondlyTabDivId).children().hide();
    if(id != subFrameId) {
        $("#" + subFrameId).attr("height",0);
        showWBWithMainPageCode();
        lastLeaveReservedTabId = id.substring(0, id.length - 6).toUpperCase();
        iFrameObj.src = getAppPath() + "/blank.htm";
        //For reversed tab.
        handleonSubTabCount--;
    }
    $("#" + id).removeClass("dti-hide").show();
}

function showWBWithSubPageCode() {
    var pageCodeId;
    var iframeId = $("iframe").attr("id");
    if(iframeId != undefined) {
        if (document.getElementById(iframeId).contentWindow.pageCode != undefined){
            pageCodeId = document.getElementById(iframeId).contentWindow.pageCode;
        }
        else {
            pageCodeId = pageCode;
        }
    } else {
        pageCodeId = pageCode;
    }
    var relPath = "../eAdmin/CustWebWB/pageconfigmgr/maintainPageConfig.do?process=handleRedirect&code="+pageCodeId;
    var workbenchUrl = "javascript:openEAdmin('"+relPath+"');";
    $('.txtSmallBlue').each(function(){
        if($(this).text() == "Workbench") {
            $(this).attr('href', workbenchUrl);
        }
    });
}

function showWBWithMainPageCode() {
    var relPath = "../eAdmin/CustWebWB/pageconfigmgr/maintainPageConfig.do?process=handleRedirect&code="+pageCode;
    var workbenchUrl = "javascript:openEAdmin('"+relPath+"');";
    $('.txtSmallBlue').each(function(){
        if($(this).text() == "Workbench") {
            $(this).attr('href', workbenchUrl);
        }
    });
}

/**
 *  hide the sub tab page header once load completed
 */
function hideSubTabPageHeader() {
    hideElementById("pageHeader", getIFrameWindow().document);
}

/**
 *  hide the sub tab page header information once load completed
 */
function hideSubTabPageHeaderInfo() {
    hideElementById("policyHeaderInfo", getIFrameWindow().document);
}

function hideElementById(id, context) {
    if(isUndefined(context)) {
        context = window.document;
    }
    if($("#" + id, context)) {
        $("#" + id, context).hide();
    }
}

var hideButtoncfg = new HideButtonCfg();
function HideButtonCfg() {
    /**
     * the button ids required to hide
     * @example ["a", "b"]
     */
    this.hideButtonIds;

    /**
     * the index of above corresponding button with same id, start from 0, count from top to bottom
     * @example [[0,1],[1]]
     */
    this.hideButtonIndex;

    this.setHideButtonIds = function(hbIds) {
        this.hideButtonIds = hbIds;
    }

    this.setHideButtonIndex = function(hbInx) {
        this.hideButtonIndex = hbInx;
    }

    this.getHideButtonIds = function() {
        return this.hideButtonIds;
    }

    this.getHideButtonIndex = function() {
        return this.hideButtonIndex;
    }
}
/**
 *  hide buttons by button config for UI change tabs
 * @param tabId
 */
function hideButtonsInSubTab() {
    var tabId = getPreviousTab() ? getPreviousTab() : getFirstTab();
    if(invokedByBackspaceB){
        tabId = getTabIdByIframePage(getTabIdWOUnderScore());
    }
    var hideButtonIds;
    var hideButtonIndex;
    var policyViewMode = getObjectValue("policyViewMode");
    switch(tabId) {
        // Policy sub tabs
        case "ADDIINS":
            hideButtonIds = ["PM_ADDIINS_SAVE", "PM_ADDIINS_CLOSE"];
            break;
        case "UNDERWRITER":
            hideButtonIds = ["PM_UNDWRI_SAVE", "PM_UNDWRI_BACK", "PM_UNDWRI_SH_AL", "PM_UNDWRI_SH_TM"];
            break;
        case "SPHND":
            hideButtonIds = ["PM_SPHAND_SAVE", "PM_SPHAND_BACK"];
            break;
        case "TAIL":
            hideButtonIds = ["PM_TAIL_SAVE", "PM_TAIL_CLOSE", "PM_TAIL_UPDATE", "PM_TAIL_RATE"];
            if(policyViewMode == "WIP"){
                hideButtonIndex = [[0],[0,1],[0],[0,1]];
            }else{
                hideButtonIndex = [[0],[0,1],[0],[0]];
            }

            break;
        case "MAINTAIN_TAX":
            hideButtonIds = ["PM_MAINTAIN_TAX_SAVE", "PM_MAINTAIN_TAX_CLOSE"];
            break;
        case "TAX":
            hideButtonIds = ["PM_VIEW_FUND_CLOSE"];
            break;
        case "REINSURANCE":
            hideButtonIds = ["PM_REINSURANCE_SAVE", "PM_REINSURANCE_CLOSE"];
            break;
        case "CHG_EXP_DATE":
            hideButtonIds = ["PM_CHG_TERM_EXP_DONE", "PM_CHG_TERM_EXP_CLOSE"];
            break;
        case "LIMITSHARING":
            hideButtonIds = ["PM_LIMIT_SHARING_SAVE", "PM_LIMIT_SHARING_CLOSE"];
            hideButtonIndex = [[0],[0,1]];
            break;
        case "AUDIT":
            hideButtonIds = ["PM_AUDIT_DISPLAY_CLOSE", "PM_VIEW_LAYER_CLOSE"];
            break;
        case "TAIL_QUOTE":
            hideButtonIds = ["PM_TAIL_QUOTE_CLOSE", "PM_TAIL_QUOTE_TRAN_PROCES"];
            break;
        case "SELECT_ADDRESS":
            hideButtonIds = ["PM_SEL_ADDRESS_SAVE", "PM_SEL_ADDRESS_CLOSE"];
            break;
        case "PAYMENT":
            hideButtonIds = ["PM_VIEW_PAYMENT_CLOSE"];
            break;
        case "ADMIN_HISTORY":
            hideButtonIds = ["PM_VIEW_POLICYADMINH_CLO"];
            break;
        case "QUOTE_STATUS":
            hideButtonIds = ["PM_PC_QUOTE_STATUS_SAVE", "PM_PC_QUOTE_STATUS_CLOSE"];
            break;
        case "UNDERLYING":
            hideButtonIds = ["PM_SAVCLOS_SAVE", "PM_SAVCLOS_CLOSE"];
            break;
        case "POL_UNDERLYING":
            hideButtonIds = ["PM_SAVCLOS_SAVE", "PM_SAVCLOS_CLOSE"];
            break;
        case "EXCESS":
            hideButtonIds = ["PM_EXCESS_COVG_SAVE", "PM_EXCESS_COVG_CLOSE"];
            break;
        case "ENTITYDTL":
            hideButtonIds = ["PM_VIEW_PROF_PRINT", "PM_VIEW_PROF_CLOSE"];
            hideButtonIndex = [[0],[0,1]];
            break;
        case "MANAGEQUICKPAY":
            hideButtonIds = ["PM_MANAGE_QUICK_PAY_SAVE", "PM_MANAGE_QUICK_PAY_CLOSE"];
            break;
        case "SHAREDLIMIT":
            hideButtonIds = ["PM_VIEW_SHLMT_CLOSE"];
            break;
        case "RENEWAL_FLAG":
            hideButtonIds = ["PM_RENEWAL_FLAG_SAVE", "PM_RENEWAL_FLAG_CLOSE"];
            break;
        // Risk sub tabs
        case "COI":
            hideButtonIds = ["PM_COI_SAVE", "PM_COI_CLOSE", "PM_COI_GENERATE", "PM_COI_SEL_ADDR"];
            break;
        case "AFFILIATION":
            hideButtonIds = ["PM_AFFILIATION_SAVE", "PM_AFFILIATION_CLOSE"];
            break;
        case "EMPPHYS":
            hideButtonIds = ["PM_EMP_PHYS_SAVE", "PM_EMP_PHYS_CLOSE"];
            break;
        case "RISK_RELATION":
            hideButtonIds = ["PM_RISK_REL_DONE", "PM_RISK_REL_CANCEL"];
            break;
        case "SCHEDULE":
            hideButtonIds = ["PM_SC_SAVE", "PM_SC_CLOSE"];
            break;
        case "SURCHARGE_POINTS":
            hideButtonIds = ["PM_RISK_SUR_PNT_SAVE", "PM_RISK_SUR_PNT_CLOS"];
            break;
        case 'COPYALL':
        case 'DELETEALL':
            hideButtonIds = ["PM_RC_PROCESS", "PM_RC_CLOSE"];
            hideButtonIndex = [[0],[0,1]];
            break;
        case "HISTORY"://Insured History
            hideButtonIds = ["PM_VIEW_INSURED_HIS_CLOSE"];
            break;
        case "INSUREDINFO"://View Insured Information
            hideButtonIds = ["PM_VIEW_INSURED_INF_CLOSE"];
            break;
        case "COPY_NEW"://Copy New
            hideButtonIds = ["PM_CREPOL_CANCL"];
            break;
        case "NATIONAL_PROGRAM"://Maintain National Program
            hideButtonIds = ["PM_MNT_NATL_PROG_SAVE", "PM_MNT_NATL_PROG_CLOSE"];
            hideButtonIndex = [[0],[0,1]];
            break;
        case "ADDTL_EXPOSURE"://Risk Additional Exposure
            hideButtonIds = ["PM_RISK_ADDTL_EXP_SAVE", "PM_RISK_ADDTL_EXP_CLOSE"];
            break;
        case "INSURED_TRACKING"://Maintain Insured Tracking
            hideButtonIds = ["PM_INS_TRK_SAVE", "PM_INS_TRK_CLOSE"];
            break;
        // Coverage sub tabs
        case "MANU":
            hideButtonIds = ["PM_MANU_SAVE", "PM_MANU_CLOSE", "PM_MANU_DETAIL"];
            break;
        case "MINI":
            hideButtonIds = ["PM_MINI_SAVE", "PM_MINI_CLOSE"];
            break;
        case "SCH":
            hideButtonIds = ["PM_SC_SAVE", "PM_SC_CLOSE"];
            break;
        case "PRIOR_ACT":
            hideButtonIds = ["PM_PA_SAVE", "PM_PA_CLOSE"];
            break;
        case "VL_COVG":
            hideButtonIds = ["PM_VLCOVG_SAV_SAVE", "PM_VLCOVG_SAV_CLOSE"];
            break;
        case "COMP_UPDATE":
            hideButtonIds = ["PM_COMP_UPDATE_CLOSE", "PM_COMP_UPDATE_PREM", "PM_COMP_UPDATE_RATE"];
            break;
        case "MAN_EXCESS_PREM":
            hideButtonIds = ["PM_MXS_PREM_SAVE", "PM_MXS_PREM_REFRESH", "PM_MXS_PREM_TRANS", "PM_MXS_SUMMARY", "PM_MXS_CLOSE"];
            hideButtonIndex = [[0],[0],[0],[0],[0,1]];
            break;
        case "UNDER_COVG":
            hideButtonIds = ["PM_SAVCLOS_SAVE", "PM_SAVCLOS_CLOSE"];
            break;
    }

    hideButtoncfg.setHideButtonIds(hideButtonIds);
    hideButtoncfg.setHideButtonIndex(hideButtonIndex);

    if(isDefined(hideButtoncfg.getHideButtonIds())) {
        hideButtons(hideButtoncfg, getIFrameWindow().document);
    }
}

function hideButtons(hbCfg, context) {
    var hbIds = hbCfg.getHideButtonIds();
    var hbIndex = hbCfg.getHideButtonIndex();
    /**
     * as there could be multiple elements with same button id in the FrameWork for actionGroup
     * so add attribute name for it for further process
     */
    $.each($("button, input[type='button']", context), function(index, o) {
        if($.inArray(o.id, hbIds) > -1) {
            $(o).attr("name", o.id);
        }
    });

    $.each(hbIds, function(index, buttonId) {
        if($("#" + buttonId, context)) {
            // issue 187689: change save button name
            if(buttonId.toUpperCase().indexOf("_SAVE") > -1 ||
                    buttonId.toUpperCase().indexOf("PM_RISK_REL_DONE") > -1){
                $("input[name='" + buttonId + "']", context).val("Save " + getTabNameByTabId());
            }

            if(isUndefined(hbIndex)) { // if index is undefined, hide all buttons with the id
                // issue 187689: enable save button
                if(buttonId.toUpperCase().indexOf("_SAVE") == -1 &&
                   buttonId.toUpperCase().indexOf("_DONE") == -1){
                    var parentElm = $("input[name='" + buttonId + "']", context).parent().parent().parent().parent();
                    parentElm.each(function () {
                        hideShowElementByClassName($(this)[0], true);
                    })
                }
            }else { // else hide button with specific index
                $.each(hbIndex[index], function(i, p) {
                    if(isNaN(p)) {
                        handleError("Invalid index:" + p + " For button:" + buttonId);
                    }else {
                        var parentElm = $("input[name='" + buttonId + "']", context).eq(p).parent().parent().parent().parent();
                        parentElm.each(function () {
                            hideShowElementByClassName($(this)[0], true);
                        })
                    }
                });
            }
        }
    })
}

function getTabNameByTabId(tabId){
    var subTabId = tabId;
    var tabName = "";
    if(!tabId){
        subTabId = getPreviousTab();
    }

    if(invokedByBackspaceB){
        subTabId = getTabIdByIframePage(getTabIdWOUnderScore());
    }

    subTabId = subTabId.indexOf(secondlyTabPrefix) > -1 ? subTabId : secondlyTabPrefix + subTabId;

    var liElements = $('#' + secondlyTabMenuGroupId).children('li');
    liElements.each(function(i) {
        var liElement = $(this);
        var aElement = liElement.children("a:first");
        var spanElement = aElement.children("span:first");
        if (aElement.attr('id') == subTabId) {
            tabName = spanElement.text();
        }
    })

    return tabName;
}

function processSubTabAfterOnload(isInvokedBySelectBySubTabId) {
    showWBWithSubPageCode();
    hideSubTabPageHeader();
    hideSubTabPageHeaderInfo();
    highlightWhenBackSpaceCase();
    if (!hasSetItalicForSubTab && (getCurrentTab() == getPreviousTab()) && !isReservedTab(getCurrentTab())
        && autoSaveResultType != commonOnSubmitReturnTypes.noDataChange) {
        handleOnUnloadForDivPopup();
    }
    if (isInvokedBySelectBySubTabId) {
        handleonSubTabCount --;
    }
    //hasSetItalicForSubTab will be set to false the last time invoking handleOnSubTabOnload.
    if (handleonSubTabCount == 0 && !isMainPageRefreshedFlg) {
        hasSetItalicForSubTab = false;
    }
}

// value would be undefined if use ajax to submit page
function setAutoSavedTabResultType(result) {
    autoSaveResultType = result;
}

/**
 *  some tabs can be modified and save even when official policy
 */
var allowedToModifyTabArr = [
    //policy sub tabs
    "ADDIINS", "UNDERWRITER", "SPHND", "CHG_EXP_DATE", "MANAGEQUICKPAY", "EXCESS", "TAIL", "QUOTE_STATUS"
    //risk
    , "COI", "AFFILIATION"
    //coverage sub tabs
    , "MAN_EXCESS_PREM"
];
function allowToModifyWhenOfficial(tabId) {
    return $.inArray(tabId, allowedToModifyTabArr) > -1 ? true : false;
}

function manualSaveWIP(queryId, process) {
    if(!isEmpty(queryId)) {
        document.forms[0].action = buildMenuQueryString(queryId, getFormActionAttribute());
    }
    document.forms[0].process.value = process;
    setEventHandle("submitForm");
    handleSaveOptionSelection("WIP");
}

function manualSaveOfficial() {
    var transactionId = "";
    if (policyHeader) {
        transactionId = policyHeader.lastTransactionId;
    }
    if (!isEmpty(transactionId)) {
        var url = getAppPath() + "/transactionmgr/loadSaveOptions.do?" + commonGetMenuQueryString()
                + "&process=isSourcePolicyInWip&transactionId=" + transactionId;
        // initiate async call
        new AJAXRequest("get", url, '', checkSourcePolicyWipDone, false);
    }
}

function checkSourcePolicyWipDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);

            if (oValueList.length > 0) {
                var wipB = oValueList[0]["RETURNVALUE"];
                if (wipB == "Y") {
                    if (!confirm(getMessage("pm.amalgamation.saveAsOfficialConfirm.info"))) {
                        // Cancel current action if user chooses "No"
                        return;
                    }
                }

                // continue save process
                handleSaveOptionSelection("OFFICIAL");
            }
        }
    }
}

function handleOnAutoSaveWhenSwitchPrimaryTab(id, url) {
    operation = "switchPrimaryTab";
    nextPrimaryTabId = id;
    nextPrimaryTabAction = url;
    if(isReservedTab(getCurrentTab()) || (eval("window.isEmptyMainPageGridRecordset") && isEmptyMainPageGridRecordset())) {
        autoSaveWip();
    }else {
        processAutoSaveSubTab(getCurrentTab());

        if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully) {
            switchPrimaryTabFlg = true;

            if(getCurrentTab() == "COPYALL" || getCurrentTab() == "DELETEALL"){
                callBackAutoSaveForFrame(true);
            }
        }else if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
            autoSaveWip();
        }else if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
            switchPrimaryTabFlg = true;
        }
    }
}

/**
 * in tab style, need to hide the action buttons migrated to tabs in the main page
 * @param frmId
 */
function commonOnHideMigratedButtonsInMainPage(frmId) {
    var hideButtonIds;
    var hideButtonIndex;
    switch(frmId) {
        case "policyPageFrame": // policy page
            if (policyHeader.policyCycleCode == 'POLICY') {
                hideButtonIds = [
                    "PM_ADDIINS_PUP", "PM_UNDW_PUP", "PM_SPHND_PUP", "PM_TAIL_PUP", "PM_MAINTAIN_TAX_PUP",
                    "PM_VIEW_TAX_PUP", "PM_REINSURANCE", "PM_CHG_EXP_DATE", "PM_MANAGE_QUICK_PAY_PUP",
                    "PM_LIMIT_SHARING", "PM_VIEW_AUDIT", "PM_TAIL_QUOTE_PUP", "PM_SELECT_ADDRESS_PUP",
                    "PM_PAYMENT_PUP", "PM_POLICY_ADMIN_HIS_PUP", "PM_QUOTE_STATUS_PUP", "PM_UNDER_POL_PUP",
                    "PM_EXCESS_COVERAGE", "PM_ENTITY_MEM_DTL", "PM_SHARED_LIMIT", "PM_RENEWAL_FLAG_PUP",
                    "PM_POL_UNDERLYING"
                ];
            }
            else {
                hideButtonIds = [
                    "PM_QT_ADDIINS_PUP", "PM_QT_UNDW_PUP", "PM_QT_SPHND_PUP", "PM_QT_TAIL_PUP", "PM_QT_MAINTAIN_TAX_PUP",
                    "PM_QT_VIEW_TAX_PUP", "PM_QT_REINSURANCE", "PM_QT_CHG_EXP_DATE", "PM_QT_LIMIT_SHARING",
                    "PM_QT_VIEW_AUDIT", "PM_QT_TAIL_QUOTE_PUP", "PM_QT_SELECT_ADDRESS_PUP", "PM_QT_PAYMENT_PUP",
                    "PM_QT_POL_ADMIN_HIS_PUP", "PM_QT_QUOTE_STATUS_PUP", "PM_QT_UNDER_POL_PUP", "PM_QT_SHARED_LIMIT",
                    "PM_QT_RENEWAL_FLAG_PUP", "PM_QT_POL_UNDERLYING"
                ];
            }
            break;
        case "riskPageFrame": // risk page
            if(policyHeader.policyCycleCode == 'POLICY'){
                hideButtonIds = [
                    "PM_RISK_COI_PUP", "PM_RISK_AFFILIATION", "PM_EMPPHYS_PUP", "PM_RISK_RELATION",
                    "PM_RISK_SCH_PUP", "PM_RISK_SUG_POINTS","PM_RISK_COPY_PUP", "PM_RISK_DELETE_PUP",
                    "PM_RISK_INSURED_HISTORY", "PM_RISK_INSURED_INFO", "PM_RISK_COPYNEW_PUP", "PM_RISK_NATL_PROGRAM",
                    "PM_RISK_SEL_ADDR", "PM_RISK_INS_TRK_PUP", "PM_RISK_EXPOSURE"
                ];
            }else{
                hideButtonIds = [
                    "PM_QT_RISK_COI_PUP", "PM_QT_RISK_AFFILIATION", "PM_QT_EMPPHYS_PUP", "PM_QT_RISK_RELATION",
                    "PM_QT_RISK_SCH_PUP", "PM_QT_RISK_COPY_PUP", "PM_QT_RISK_DELETE_PUP", "PM_QT_RISK_INSURED_HIST",
                    "PM_QT_RISK_NATL_PROGRAM", "PM_QT_RISK_SEL_ADDR", "PM_QT_RISK_INS_TRK_PUP", "PM_QT_RISK_EXPOSURE"
                ];
            }
            break;
        case "riskSummaryPageFrame": //risk summary page
            if(policyHeader.policyCycleCode == 'POLICY'){
                hideButtonIds = [
                    "PM_RISK_SUM_COI_PUP", "PM_RISK_SUM_AFFILIATION", "PM_SUM_EMPPHYS_PUP",
                    "PM_RISK_SUM_RELATION", "PM_RISK_SUM_SCH_PUP", "PM_RISK_SUM_SUG_POINTS", "PM_RISK_SUM_COPY_PUP",
                    "PM_RISK_SUM_DELETE_PUP", "PM_RISK_SUM_INSURED_INFO", "PM_RISK_SUM_INSURED_HIS",
                    "PM_RISK_SUM_COPYNEW_PUP", "PM_RISK_SUM_NATL_PROGRAM", "PM_RISK_SUM_SEL_ADDR",
                    "PM_RISK_SUM_INS_TRK_PUP", "PM_RISK_SUM_EXPOSURE"
                ]
            }else{
                hideButtonIds = [
                    "PM_QT_RISK_SUM_COI_PUP", "PM_QT_RISK_SUM_AFFIL", "PM_QT_SUM_EMPPHYS_PUP",
                    "PM_QT_RISK_SUM_RELATION", "PM_QT_RISK_SUM_SCH_PUP", "PM_QT_RISK_SUM_COPY_PUP",
                    "PM_QT_RISK_SUM_DELETE_PUP", "PM_QT_RISK_SUM_INSUD_HIS", "PM_QT_RISK_SUM_NA_PROGRAM",
                    "PM_QT_RISK_SUM_SEL_ADDR", "PM_QT_RISK_SUM_INS_TRK", "PM_QT_RISK_SUM_EXPOSURE"
                ];
            }
            break;
        case "coveragePageFrame": // coverage page
            if(policyHeader.policyCycleCode == 'POLICY'){
                hideButtonIds = [
                    "PM_COVG_MINI", "PM_MANU_PUP", "PM_COVG_SCH_PUP", "PM_PRIOR_ACT_PUP",
                    "PM_VL_COVG_PUP", "PM_COMP_UPDATE", "PM_MAN_EXCESS_PREM", "PM_UNDER_COVG_PUP"
                ];
            }else{
                hideButtonIds = [
                    "PM_QT_COVG_MINI", "PM_QT_MANU_PUP", "PM_QT_COVG_SCH_PUP", "PM_QT_PRIOR_ACT_PUP",
                    "PM_QT_VL_COVG_PUP", "PM_QT_COMP_UPDATE", "PM_QT_MAN_EXCESS_PREM", "PM_QT_UNDER_COVG_PUP"
                ];
            }
            break;
        case "coverageClassPageFrame": // coverage class page
            break;
    }

    hideButtoncfg.setHideButtonIds(hideButtonIds);
    hideButtoncfg.setHideButtonIndex(hideButtonIndex);

    if(isDefined(hideButtoncfg.getHideButtonIds())) {
        hideButtons(hideButtoncfg, window.document);
    }
}

/**
 *  in tab style, need to set tab font style once auto save done, it's same as
 *  corresponding button font style once popup unload
 */
function handleOnItalicTabStyle() {
    hasSetItalicForSubTab = true;
    handleOnUnloadForDivPopup();
}

/**
 *  as there could be too many tabs which make scrollWidth overflow screen too much,
 *  in this case we need to adjust the page size
 */
function commonOnAdjustPageSize() {
    if(document.body.scrollWidth > window.screen.width) {
        document.body.style.zoom = window.screen.width/document.body.scrollWidth;
    }
}

/**
 * In Tab style, As Main page already opened the dialog in the center when iFrame screen is processing;
 * So that we need to check if iFrame screen needs to open the dialog again based on this function.
 */
function ignoreProgressIndicator(){
    return mainPageLock.isLocked();
}

/**
 * In Tab style, get Iframe page Id. If located the tab which is not real, return undefined.
 */
function getIframePageId() {
    var pageCodeId;
    var iframeId = $("iframe").attr("id");
    if(iframeId != undefined) {
        pageCodeId = document.getElementById(iframeId).contentWindow.pageCode;
    } else {
        pageCodeId = pageCode;
    }
    return pageCodeId;
}

/**
 * In Tab style, get tab Id mapped by page Id, return value will exclude the character "_".
 */
function getTabIdWOUnderScore() {
    var pageId = getIframePageId();
    //1. Process reversed tab case.
    if (!pageId) {
        var reservedTabArray = reservedTabArrObj[subFrameId];
        var returnTab = reservedTabArray[0];
        if (reservedTabArray.length > 1) {
            if (lastLeaveReservedTabId && $.inArray(lastLeaveReservedTabId, reservedTabArray) > -1) {
                returnTab = lastLeaveReservedTabId;
            }
        }
        return returnTab;
    }
    //2. Process the special mapping relation case.
    if (specialPageTabIdMapping[subFrameId] && specialPageTabIdMapping[subFrameId][pageId]) {
        return specialPageTabIdMapping[subFrameId][pageId];
    }
    //3. Process common tab case.
    for (var i = 0; i < secondlyTabRegExpGroup.length; i++) {
        var secondlyTabRegExp = secondlyTabRegExpGroup[i];
        if (new RegExp(secondlyTabRegExp).test(pageId)) {
            pageId = pageId.replace(secondlyTabRegExp, "").replace(/_/g, "");
            break;
        }
    }
    //4. Distinguish copyAll or deleteAll action for risk page
    if (pageId == "COPYALL") {
        var copyDeleteAction = window.frames[subFrameId].getObjectValue("operation").toUpperCase();
        if (copyDeleteAction == "DELETEALL") {
            pageId = "DELETEALL";
        }
    }
    return pageId;
}

/**
 * In Tab style, highlight the secondary tab when user press the "Backspace" button.
 */
function highlightWhenBackSpaceCase() {
    if(!invokedByBackspaceB) return;
    invokedByBackspaceB = false;
    var tabIdWOUnderScore = getTabIdWOUnderScore();
    if(getCurrentTab() && getCurrentTab().replace("_", "") != tabIdWOUnderScore
       && tabIdWOUnderScore != "AUDITINFORMATION") {
        //Load non real tab
        if ($.inArray(tabIdWOUnderScore, ["RISK", "COMPONENT", "COVERAGE"]) > -1) {
            selectTabById(tabIdWOUnderScore);
        } else if (isReservedTab(getCurrentTab()) && !isReservedTab(tabIdWOUnderScore)) {
            return;
        } else {
            highlightTab(tabIdWOUnderScore, true);
        }
    }
}

function getTabIdByIframePage(tabIdWOUnderScore) {
    var tabId = "";
    var liElements = $('#' + secondlyTabMenuGroupId).children('li');
    liElements.each(function(i) {
        var liElement = $(this);
        var aElement = liElement.children("a:first");
        var spanElement = aElement.children("span:first");
        var tabIdMappedByPage = aElement.attr('id')
                ? aElement.attr('id').replace(secondlyTabPrefix, "")
                : aElement.attr('id');
        if ((aElement.attr('id') == secondlyTabPrefix + tabIdWOUnderScore) ||
                (tabIdMappedByPage ? tabIdMappedByPage.replace(/_/g, "") : tabIdMappedByPage) == tabIdWOUnderScore) {
            tabId = tabIdMappedByPage;
        }
    });
    return tabId;
}

// Process auto save sub tab via system paramter PM_AUTO_SAVE_WIP
function processAutoSaveSubTab(previousTab) {
    if (getSysParmValue("PM_AUTO_SAVE_WIP") == "Y" && needAutoProcessing(previousTab)) {
        setAutoSaveProcessingB(true);
        autoSaveSubTab(previousTab);
        setAutoSaveProcessingB(false);
    }
    /*
     * issue 190085, COPY ALL, DELETE ALL page always need to refer page even though there is error occurs after user
     * clicks the Process button, it is according to the original logic.
     */
    else if(!needAutoProcessing(previousTab) && eval("getIFrameWindow().isNeedToRefreshParentB")
            && getIFrameWindow().isNeedToRefreshParentB()) {
        setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
        setCacheRowIds(getCurrentRow() + "," + getCurrentRow());
        refreshPage();
        return;
    }
    else {
        doNotAutoSaveSubTab(previousTab);
    }
}

// Check whether sub tab would be pending after prompt.
function commonIsOkToPendSubTab(toBeSavedTab, currentTab) {
    var fromReservedTabB = isReservedTab(toBeSavedTab);
    var toReservedTabB = isReservedTab(currentTab);
    var isOkToPendB = false;
    var functionExists = eval("window.handleOnIsOkToPendSubTab");
    if (functionExists) {
        isOkToPendB = handleOnIsOkToPendSubTab();
    }
    else {
        var dependPageGridId;
        for (var obj in pageGridDepended_tabArray[subFrameId]) {
            if (pageGridDepended_tabArray[subFrameId].hasOwnProperty(obj)) {
                dependPageGridId = obj;
            }
        }
        var gridData = getXMLDataForGridName(dependPageGridId);
        // Prompt when add a new page grid data without save and then click the current real tab which is depended on it.
        if (!toReservedTabB && gridData && gridData.recordset("UPDATE_IND").value == 'I' &&
            gridData.recordset("COFFICIALRECORDID").value == '' &&
            gridData.recordset("CRECORDMODECODE").value == 'TEMP' &&
            pageGridDepended_tabArray[subFrameId][dependPageGridId] &&
            $.inArray(currentTab, pageGridDepended_tabArray[subFrameId][dependPageGridId]) > -1 &&
            commonSaveRequiredToChangePages(pageName, getTabNameByTabId(currentTab), "Y", "I")) {
            isOkToPendB = true;
        }
        // Prompt when the page grid data is changed and then click the current real tab from fake tab.
        else if (fromReservedTabB) {
            if (!toReservedTabB && isPageGridsDataChanged() &&
                !confirm(getMessage("pm.subTab.reserved.clickOk.changesWitoutSave.confirm"))) {
                isOkToPendB = true;
            }
        }
        // Prompt when the real tab is changed and then leave it.
        else if (isIframeDataChanged() && !confirm(getMessage("pm.subTab.real.clickOk.changesLost.confirm"))) {
                isOkToPendB = true;
        }
    }
    return isOkToPendB;
}

// Stop the auto save but prompt user for save when the Save button is visible/enable.
function doNotAutoSaveSubTab(toBeSavedTab) {
    setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
    if (commonIsOkToPendSubTab(toBeSavedTab, getCurrentTab())) {
        setAutoSavedTabResultType(commonOnSubmitReturnTypes.saveInProgress);
        handleOnClearOperation();
    }
    else {
        commonOnRevertSubTabChangesBeforeLeave(toBeSavedTab);
    }
}

// Check if Preview button clicked.
function isPreviewButtonClicked(){
    return hasObject("isPreviewRequest") && getObjectValue("isPreviewRequest") == "Y";
}

// Clear the operation variable.
function handleOnClearOperation(){
    operation = undefined;
}

// Execute workflow in handleOnSubTabOnLoad.
function ExeWorkFlow () {
    // Add back this condition, which was lost in issue 190191.
    // Issue 190859 - As the original validation is specific processing for Cancel button.
    //                It is for specific case when click cancel button of sub-tab, then switchXXXflag and operation is undefined
    //                for now, face the same situation when click save button [added in issue#187689].
    //                so correct the validation to check if workflow popup is opened already
    //                in order to avoid opening 2 workflow popup
    if (isExeInvokeWorkflowInCallBack() && !isWorkFlowPopupOpened()) {
        var functionExists = eval("getIFrameWindow().invokeWorkflow");
        if (functionExists) {
            var invokeWorkFlowResult = getIFrameWindow().invokeWorkflow();
            if (invokeWorkFlowResult) {
                return;
            }
        }
    }
}

function isExeInvokeWorkflowInCallBack() {
    var returnFlag = false;
    var workFlowArray = ["ADDIINS", "TAIL_QUOTE", "TAIL",//policy tab
        "COI", "RISK_RELATION",  //risk/risk summary tab
        "VL_COVG" //coverage tab
    ];
    if (isTabStyle() && $.inArray(getPreviousTab(), workFlowArray) > -1) {
        returnFlag = true;
    }
    return returnFlag;
}

function isWorkFlowPopupOpened(){
    return getWorkFlowPopupOpenedB();
}

/*
 * In below case, we need to remove the error message of sub-tab displayed on Main page:
 * 1) we made some data change on sub-tab and save.
 * 2) if validation error exists, then the error message will be displayed on the Mian page
 * 3) After delete the data change and click save button on sub-tab.
 * 4) As there is no data change exists, system doesn't submit sub-tab, we should remove the error message at the same time.
 */
function handleOnRemoveMessages(){
    removeMessagesForFrame();
}

/*
 * When auto save wip is N and there have changes on sub tab, we need to revert its DB changes before leave sub tab.
 */
function commonOnRevertSubTabChangesBeforeLeave(tabId) {
    var isExistFunction = eval("window.handleOnRevertSubTabChangesBeforeLeave");
    if (isExistFunction) {
        handleOnRevertSubTabChangesBeforeLeave(tabId);
    }
}
