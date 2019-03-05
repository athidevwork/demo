//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 06, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/29/2009       Kenney      Modified for issue 98735.
// 04/30/2010       Colton      Resize the Producer Agent Entry Popup page by modifying maintainAgent.
// 05/06/2010       syang       106538 - Modified refreshPage() to add riskId for risk relation page.
// 05/12/2010       fcb         107461 - commonOnLogOut() added.
// 07/06/2010       syang       103797 - Added setLinkAsReadyOnly() and resetReadOnlyLink() to handle the link of field.
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 08/25/2010       syang       108651 - Added enableDisableRenewIndicator() to handle Renew indicator.
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 09/16/2010       dzhang      103813 - Added refreshWithNewPolicyTermHistory().
// 10/12/2010       wfu         111776: Replaced hardcode variable deleteQuestion with resource definition
// 10/14/2010       tzhao      issue#109875 - Modified money format script to support multiple currency.
// 10/19/2010       gzeng      112909 - Set isScrolling to false for special pages.
// 12/21/2010       dzhang     115895 - Modified processAjaxResponseFordeleteWipTransaction: For New Business, Quote, Conversion Renewal
//                             and Conversion Reissue WIP Deletion, system should forwarding to ePolicy home page instead of retrieve the policy/quote again.
// 01/11/2011       dzhang     114424 - Added navigateTerms().
// 01/28/2011       fcb        117193 - document.all(gridDetailDivId) replaced with hasObject(gridDetailDivId)
// 01/31/2011       wfu         116334 - Added function viewClaimsSummary() and gotoSource() for view claims summary.
// 02/24/2011       kshen       Changed to use the notesImage and noNotesImage
//                              variables created in header.jsp for notes icon path.
// 03/17/2011       wfu         118437 - Modified viewMultiCancelPage to adjust pop up display. 
// 02/03/2011       fcb         112664 & 107021: viewPolicyXMLReport added.
// 03/30/2011       dzhang     94232 - Added maintainIbnrRisk() and modified submitForm() to support multi-Grid.
// 03/30/2011       syang       106634 - Added processAcf() to open process ACF page.
// 04/22/2011       dzhang      117338 - Added setNegativeRed().
// 04/26/2011       dzhang      119388 - Modified processOutputFromPM() to resize processOutput Div.
// 04/27/2011       dzhang      94232 -  Modified maintainIbnrRisk() to resize maintainIbnrRisk Div.
// 05/26/2011       fcb         119027 - getSelectedOnly() added.
// 05/01/2011       fcb         105791 - handleOnCaptureTransactionDetails() modified.
// 07/28/2011       syang       121208 - Add the methods removeValueFromArray() and isArrayContains(). 
// 08/01/2011       ryzhao      118806 - Added parameter amountType to viewFund() to support the new View Fund/Tax/Fee/Surcharge page.
// 08/19/2011       dzhang      124320 - Modified parseXML(): escape the newline characters.
// 08/24/2011       lmjiang     124365 - re-size the 'View Preminum' grid height to remove the duplicate bar.

// 08/26/2011       ldong       124449 - Enhancement issue, notes hot key.
// 09/01/2011       MLM         Refactored reconnectAllFields for performance and move it into xmlproc.js.
// 09/01/2011       ryzhao      118806 - Rename viewFund() to viewNonPremium().
// 09/21/2011       wfu         120554 - Fixed errors in quote pages and multiple grid pages.
// 09/28/2011       ryzhao      124995 - Added function maintainFileActivityNotesFromPM().
// 10/25/2011       syang       125752 - Modified captureTransactionDetails() to initialize endorsement code to empty for saving additional insured,
//                              avoid reusing the previous endorsement code in TransactionManagerImpl.getCommonInitialValuesForCreateTransaction().
// 11/07/2011       wfu         125597 - Modified getChangesOnly to exclude fields ended with DISP_ONLY and used only for displaying.
// 11/08/2011       bhong       112837 - Moved showProcessingDivPopup to divpopup.js
// 11/09/2011       wfu         125597 - Moved function getChangesOnly to xmlproc.js
// 11/09/2011       bhong       112837 - Remove logics to check "processingDivPopupId" object  
// 11/10/2011       syang       126447 - Added showHideTableColumn() to show/hide the column in grid.
// 11/17/2011       wfu         127139 - Modified refreshPage to add reverse attribute for reverse risk relation.
// 11/28/2011       ryzhao      127626 - Modified postCommonAddRow() to add logic to call handlePostAddRow() if exists.
// 12/29/2011       syang       128757 - Modified viewNonPremium() to adjust the size of page.
// 01/05/2012       clm         126620 - add sorting property for table
// 01/11/2012       syang       128801 - Modified refreshPage() to handle riskId.
// 02/28/2012       xnie        129417 - Roll backed fix of 125752. We shouldn't fix problem here.
// 05/10/2012       jshen       133365 - Modified pmStartImageRightDeskTop() to support getting grid ID column value if the sourceFieldId is anchorcolumName.
// 07/11/2012       sxm         135029 - Display "Processing" dialog when change "Go To" risk/covg in Coverage Class tab
// 07/24/2012       awu         129250 - 1) Modified doMenuItem() to support auto save function.
//                                       2) Added autoSaveWip(), postAjaxSubmitWithProcessingDiv().
//                                       3) Modified postAjaxSubmit() to call postAjaxSubmitWithProcessingDiv().
// 11/13/2012       awu         138907 - Modified setRecordsetByObject() to check the type of the field value before to call the function formatDateForDisplay.
// 12/27/2012       tcheng      139862 - Added handleOnGetWarningMsg() to pop up warning message.
// 01/31/2012       adeng       141570 - Modified commonDeleteRow() to use right filterCondition for the call of filter.
// 02/06/2013       tcheng      140883 - Modified pmStartImageRightDeskTop() to get the field value from the grid first if the grid exists and
//                                       the field is defined in the grid. If it cannot get value from grid, try to get field value from the form.
// 02/19/2013       jshen       141982 - Modified viewTransaction() to call loadAllTransaction process which will
//                                       retrieve current term's transaction data in this case.
// 03/14/2013       adeng       142891 - Removed logic what filter component data by coverage base record Id from common
//                                       method commonDeleteRow(), these logic should be placed in maitainCoverage.js.
// 04/16/2013       xnie        143536 - Modified viewPolicyReport() to add commonGetMenuQueryString() to url so that
//                                       policy header/risk header can be got correctly.
// 05/29/2013       jshen       145577 - Modified autoSaveWip to get correct changed data to populate.
// 05/22/2013       wfu         144678 - Modified commonGetMenuQueryString to set correct policy no to URL.
// 06/21/2013       adeng       117011 - 1) Modified handleOnCaptureTransactionDetails() to add a new parameter "comment2".
//                                       2) Modified handleOnCaptureTransactionDetails_Ajax() to set the new object
//                                          "newTransactionComment2" to a new global variable "objectComment2 ".
// 07/12/2013       awu         145144 - Modified commonGetMenuQueryString to set correct policy number to URL.
// 07/05/2013       xnie        145721 - Added viewPolicyHeaderPremium() to show policy premium for a given
//                                       transactionId. This is called by hyperlink of official/current written premium
//                                       in policy header.
// 09/12/2013       Parker      148260 - Add processing dialog for some policy page.
// 11/11/2013       JYang       148189 - Add revertHiddenFields function to revert the field hidden by
//                                       autoSaveWIP.enableFieldsForSubmit when autoSaveWIP failed.
// 12/06/2013       Parker      Issue 148036 Refactor maintainRecordExists code to make one call per subsystem to the database.
// 12/09/2013       Parker      148034 - Retrieve HTML to replace LOV when loading LOV via AJAX.
// 12/13/2013       adeng       150356 - Modified preOoseChangeValidation() to pass correct parameter to selectRowById().
// 12/26/2013       awu         148187 - Added viewDividendAudit().
// 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
// 12/26/2013       xnie        148083 - 1) Modified doMenuItem() and autoSaveWip() to support auto save function for
//                                          risk summary tab page.
//                                       2) Modified commonGetMenuQueryString() to add PM_PT_VIEWRISKSUMMARY for
//                                          risk summary case.
//                                       3) Added commonEnableDisableFormFields() to disable fields based on edit
//                                          indicator.
// 03/13/2014       xnie        152940 - Modified commonEnableDisableFormFields() to make fields disable/readOnly based
//                                       on edit indicator.
// 03/26/2014       awu         152706 - Modified refreshWithNewPolicyTermHistory to remove policyTermHistoryId
//                                       if it was at the end of the URL
// 07/04/2014       kxiang      155331 - Added function addMultiFilterConditions().
// 07/07/2014       Jyang2      154814 - Added resetRenewIndicator method to reset renew field value when expiration date
//                                       is changed.
// 07/23/2014       xnie        156208 - Modified commonHandleOnGetAddlInfo() to fix bug when current page has no grid
//                                       case.
// 07/25/2014       awu         152034 - 1). Modified commonOnChange, remove the riskId, coverageId, coverageClassId from
//                                           the URL if changed the policyTerms.
//                                       2). Modified processAjaxResponseFordeleteWipTransaction to send 'ALL' to refresh
//                                           function to remove the riskId/coverageId/coverageClassId from the URL.
//                                       3). Modified refresh, remove the riskId, coverageId, coverageClassId from the
//                                           URL depends on the input parameter value.
// 08/06/2014       Elvin       149341: resize workflow diary window
// 08/07/2014       kxiang      156439 - Modified navigateTerms(),add if condition: iHtml didn't contain "No Future Term"
//                                       or "No Prior Term" ,then  added to iHtml.
// 08/13/2014       kxiang      155534 - Modified commonHandleOnGetInitialValues add parameter nameHref to active popup
//                                       Entity window of new added record.
// 09/17/2014       jyang2      157345 - Modified function handleOnSelectPolicyHolderForCreatePolicy, encode the policy
//                                       holder name before append it to url.
// 10/09/2014       wdang       156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 11/26/2014       wdang       158689 - 1) Added setRowStyleForNewRow() to change CSS style for newly-added row.
//                                       2) Added setRowStyleForOneRow() to extract the common loop block in 
//                                          setRowStyle() and setRowStyleForNewRow(). 
// 12/04/2014       awu         159187 - Modified commonOnChange, don't call auto save process if no data changed.
// 12/12/2014       jyang       158577 - Added enableDisableRenewIndicatorWithoutGrid for page level renew field.
// 12/17/2014       fcb         149906 - commonOnLogOut: replaced MaintainUnlockPolicyAction with MaintainLockAction
// 01/06/2015       wdang       158738 - Updated the comments of refreshWithNewPolicyTermHistory().
// 01/07/2015       xnie        160019 - Modified autoSaveWip() to add riskId to URL. This change fixed problem of risk
//                                       header is setting incorrectly.
// 01/08/2015       awu         157105 - Modified commonDeleteRow to add beginDeleteMultipleRow and endDeleteMultipleRow
//                                       to wrap the multiple rows deleting.
// 03/20/2015       wdang       161448 - Modified thisopenEntityMiniPopupWin to support location risk.
// 06/08/2015       wdang       163197 - Modified thisopenEntityMiniPopupWin to support opening it by frame.
// 09/07/2015       lzhang      165797 - 1. Modified autoSaveWip to add coverageId into the send url.
// 09/22/2015       Elvin       Issue 160360: add preview functionality
// 10/13/2015       tzeng       164679 - 1. Modified handleOnAutoSaveWip to get risk relation result after auto save
//                                          risk then put it in request to display message at selected tab page.
//                                       2. Modified refreshPage to add risk relation result in request to display
//                                          message after rating or save official.
// 12/15/2015       jyang2      167179 - 1. Modified loadPageByViewMode to add riskRelationMessageType parameter to url.
//                                       2. Modified handleOnGetWarningMsg to not refresh page when save endorsement
//                                          quote done.
// 12/30/2015       kxiang      168449 - Modified refreshWithNewPolicyTermHistory to remove endQuoteId.
// 01/06/2016       wdang       168069 - Added auto save support for new tab page - Policy Summary.
// 01/28/2016       wdang       169024 - Reverted changes of 164679.
// 07/14/2016       mlm         170307 - Integration of Ghostdraft.
// 06/17/2016       ssheng      164927 - filter PM entity
// 08/26/2016       wdang       167534 - 1) Added support for Renewal Quote.
//                                       2) Modified captureTransactionDetails to support configuration.
// 10/21/2016       kxiang      180685 - Modified handleOnGetWarningMsg to not refreshpage when the policy need to forward
//                                       to quote.
// 10/26/2016       lzhang      180644 - Modified csProcessFormLetterFromPM(): pass empty string "" rather than null
//                                       as the first parameter when calling the openDivPopup function
// 11/01/2016       eyin        180850 - Modified commonOnSubmit(), add removeMessages() if there is no Data change on
//                                       the screen when the 'SAVE' button is clicked on.
// 02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
// 03/10/2017       eyin        180675 - Added codes to handle auto-save function in new UI tab style.
// 03/09/2017       tzeng       166929 - 1) Add viewSoftValidation() to support Validation Information page.
//                                       2) Add initiateApp() to call eApp for initiate application.
// 05/12/2017       eyin        185077:  Add JS function commonOnGetParentWindow();
// 05/09/2017       ssheng      185360 - Add system parameter 'PM_NB_QUICK_QUOTE' to indicate
//                                       if check entity is policy entity.
// 05/23/2017       lzhang      185079 - remove commonOnGetParentWindow and
//                                       pass parameter when call getParentWindow()
// 07/11/2017       kxiang      185483 - Modified getReturnCtxOfDivPopUp to check if IFrameWindow is defined before being called.
// 07/17/2017       wrong       168374 - 1) Added loadIsFundStateValue() to get isFundState field for risk information
//                                          and risk relation page.
//                                       2) Added setDefaultValueForPcfRiskCounty() to set default value for pcf risk
//                                          county.
//                                       3) Added setDefaultValueForPcfRiskClass() to set default value for pcf risk
//                                          class.
//                                       4) Added handleOnloadIsFundStateValue() to process isFundState field value.
//                                       5) Added handleOnGetDefaultValueForPcfCountyAndClass() to set return value
//                                          for pcf risk county/class field.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
// 07/26/2017       lzhang      182246 - clean up unsaved message for page changes and add commonSaveRequiredToChangePages
// 08/18/2017       wli         187806 - Modified processAfterCancelAutoSaveSubTab() to unlock the main page.
// 09/01/2017       wrong       186656 - 1) Added commonCheckIsJobBasedOutput() to check if current policy is configured
//                                          to process outputs as job.
//                                       2) Modified onPreviewButtonClick() to add logic to check if it is output job
//                                          configuration. If true, alert warning message.
// 09/11/2017       kshen      Grid replacement. Changed commonReadyStateReady for jqxGrid support.
//                             Removed the function parseXML. The new parseXML is in the common.js of oasistags.
// 10/13/2017       lzhang      189127 - Modified commonOnGlobalSearch: encode URL parameter policyNoCriteria value
// 11/09/2017       tzeng       187689 - Modified doMenuItem(), commonIsOkToChangePages(), commonOnChange() and added
//                                       clearOperationForTabStyle(), isIframeDataChanged() to support processAutoSaveSubTab().
// 12/06/2017       wrong       190019 - Modified handleOnGetDefaultValueForPcfCountyAndClass to set pcf values after
//                                       loading lov instead of setFormFieldValuesByObject directly.
// 12/13/2017       wrong       190191 - 1) Delete variable subTabGridId and function isNoDataFoundPage. No data found
//                                          error message has been handled in gui.js for sub tab, so there is no need to
//                                          invoke isNoDataFoundPage().
//                                       2) Modified isExeInvokeWorkflowInCallBack to remove operation check.
// 12/15/2017       eyin        190085 - Added JS function commonOnRemoveMessages();
// 12/20/2017       lzhang      189983 - move keyup() from commonSecondlyTab.js to common.js
// 01/12/2018       xnie        190796 - Changed commonOnBeforeGotoPage to correctly select first row of previous page.
// 01/16/2018       eyin        190859 - Moved isExeInvokeWorkflowInCallBack() to commonSecondlyTab.js;
// 01/31/2018       wrong       191057 - 1) Added isWindowStyle to check current page style.
//                                       2) Modified commonOnPutParentWindowOfDivPopup() to change logic of getting
//                                          oParentWindowFlag.
//                                       3) Modified getOpenCtxOfDivPopUp() to add additional logic to get popups.
// 04/18/2018       wrong       192537 - Modified getOpenCtxOfDivPopUp() and getReturnCtxOfDivPopUp() to add additional
//                                       logic of !window.opener to return the correct window.
// 05/23/2018       ryzhao      193086 - Modified commonDeleteRow() to select the first tab by default
//                                       if there is no record in the grid after deletion.
// 05/24/2018       mlm         193214 - Replaced .attributes('dataSrc') and .attributes('dataFld') with getDataSrc() and
//                                       getDataField() respectively.
// 06/11/2018       cesar       193651 - Modified userReadyStateReady() to continue if setRowStyle() fails.
// 07/13/2018       cesar       194021 - Refactor commonAddRow(), postCommonAddRow(), setInitialUrlInGrid() into dti.oasis.grid
// 07/18/2018       cesar       194022 - Refactor commonDeleteRow() into dti.oasis.grid
// 07/31/2018        mlm        193967 - Refactored to promote logic from commonOnBeforeGotoPage, selectFirstRowInTable
//                                       and moveToFirstRowInTable into framework.
// 08/01/2018        mlm        193968 - Refactored to promote setRecordsetByObject into framework as setCurrentRecordValues.
// 08/16/2018       wrong       192865 - Modified thisopenEntityMiniPopupWin() to add getOpenCtxOfDivPopUp.
// 10/11/2018       cesar       193937 - Moved setRowStyle to the framework (xmlproc.xml).
// 10/16/18         xgong       195889 - Updated enableDisableRenewIndicatorWithoutGrid for grid replacement
// 10/23/2018       dpang       195835 - Grid replacement: change window.frameElement.document.parentWindow to getParentWindow().
// 10/26/2018       wrong       193599 - Added viewPolicySummaryReport() for new PDF report.
// 10/25/2018       xgong       195889 - Updated commonOnButtonClick for grid replacement
// 11/22/2018       wrong       197214 - Modified getReturnCtxOfDivPopUp to add an additional condition to get the
//                                       correct parent window.
//12/04/2018        clm         195889 - Grid replacement: change fireEvent to dispatchElementEvent
//-----------------------------------------------------------------------------
var priorRowId = -1;
// var deleteQuestion = getMessage("pm.common.selected.record.delete.confirm");
var oldPolicyViewMode;
var oldPolicyTerm;
var hidElems = new Array();  // To record the elements which is hid in autoSaveWIP.enableFieldsForSubmit
var entityOwnerId = null; // This parameter is used to transfer result between getEntityOwnerId and setEntityOwnerId.

var directionUrl;
function doMenuItem(id, url) {
    if (url == '#') {
        //attempting to access the same page - do nothing
        return;
    }

    if (url.indexOf('~envPath/') != -1) {
        url = getEnvPath() + url.substr(url.indexOf('~envPath/') + 8);
    }
    if (url.indexOf('~') == 0) {
        url = getAppPath() + url.substring(1);
    }

    var functionExists;
    var isOkToProceed;
    var menuItemDivPopup = false;
    if (id.indexOf('PUP') > 0) {
        menuItemDivPopup = true;
    }

    var isAutoSaveWip = false;
    if (!menuItemDivPopup) {
        if (url.indexOf("maintainPolicy.do") < 0
                && url.indexOf("maintainRisk.do") < 0
                && url.indexOf("maintainCoverage.do") < 0
                && url.indexOf("maintainCoverageClass.do") < 0
                && url.indexOf("viewRiskSummary.do") < 0
                && url.indexOf("viewPolicySummary.do") < 0) {
            isAutoSaveWip = false;
        }
        else {
            if ((isPageDataChanged() || isIframeDataChanged()) && getSysParmValue("PM_AUTO_SAVE_WIP") == "Y") {
                isAutoSaveWip = true;
            }
            else {
                isAutoSaveWip = false;
            }
        }
    }

    if (!isAutoSaveWip && (isPageDataChanged() || isIframeDataChanged())) {
        functionExists = eval("window.commonIsOkToChangePages");
        if (functionExists) {
            isOkToProceed = commonIsOkToChangePages(id, url);
            if (!isOkToProceed) {
                return;
            }
        }

        if (window.isOkToChangePages)
            if (!isOkToChangePages(id, url))
                return;
    }

    var queryString = null;
    var st = "";
    functionExists = eval("window.commonGetMenuQueryString");
    if (functionExists) {
        queryString = commonGetMenuQueryString(id, url);
        if (queryString != null) {
            st = queryString.substring(0, 1);
            if (st == '?' || st == '&')
                queryString = queryString.substring(1);
            if (url.indexOf('?') > -1)
                url += '&' + queryString;
            else
                url += '?' + queryString;
        }
    }
    queryString = null;
    st = "";
    if (window.getMenuQueryString)
        queryString = getMenuQueryString(id, url);
    if (queryString != null) {
        st = queryString.substring(0, 1);
        if (st == '?' || st == '&')
            queryString = queryString.substring(1);
        if (url.indexOf('?') > -1)
            url += '&' + queryString;
        else
            url += '?' + queryString;
    }
    if (menuItemDivPopup) {
        var idName = 'R_menuitem_' + id;
        var mi = getObject(idName);
        if (mi) {
            mi.children[0].style.backgroundImage = '';
        }
        var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
    }
    else {
        if (isAutoSaveWip) {
            directionUrl = url;
            if (getUIStyle() == "T") { // tab style
                if (eval("window.handleOnAutoSaveWhenSwitchPrimaryTab")) {
                    handleOnAutoSaveWhenSwitchPrimaryTab(id, url);
                }
            }
            else { // button style
                autoSaveWip();
            }
        }
        else {
            showProcessingImgIndicator();
            setWindowLocation(url);
        }
    }
}

function autoSaveWip(directionUrl) {
    var action;
    var url = getAppPath();
    var currentHref = document.location.href;
    if (currentHref.indexOf("maintainPolicy.do") > 0) {
        syncChanges(origcomponentListGrid1, componentListGrid1);
        setInputFormField("txtXML", getChanges(origcomponentListGrid1));
        action = "autoSavePolicy";
        url += "/policymgr/maintainPolicy.do?";
    }
    else if (currentHref.indexOf("maintainRisk.do") > 0) {
        syncChanges(origriskListGrid1, riskListGrid1);
        setInputFormField("txtXML", getChanges(origriskListGrid1));
        action = "autoSaveAllRisk";
        url += "/riskmgr/maintainRisk.do?";
    }
    else if (currentHref.indexOf("viewRiskSummary.do") > 0) {
        syncChanges(origriskListGrid1, riskListGrid1);
        setInputFormField("txtXML", getChanges(origriskListGrid1));
        action = "autoSaveAllRiskSummary";
        url += "/riskmgr/viewRiskSummary.do?";
    }
    else if (currentHref.indexOf("maintainCoverage.do") > 0) {
        alternateGrid_update('coverageListGrid');
        alternateGrid_update('componentListGrid');
        action = "autoSaveAllCoverage";
        url += "/coveragemgr/maintainCoverage.do?";
    }
    else if (currentHref.indexOf("maintainCoverageClass.do") > 0) {
        syncChanges(origcoverageClassListGrid1, coverageClassListGrid1);
        setInputFormField("txtXML", getChanges(origcoverageClassListGrid1));
        action = "autoSaveAllCoverageClass";
        url += "/coverageclassmgr/maintainCoverageClass.do?";
    }
    enableFieldsForSubmit(document.forms[0]);
    url += "newSaveOption=WIP&date=" + new Date();
    if(policyHeader.riskHeader) {
        url += "&riskId=" + policyHeader.riskHeader.riskId;
    }
    if(policyHeader.coverageHeader) {
        url += "&coverageId=" + policyHeader.coverageHeader.coverageId;
    }
    postAjaxSubmitWithProcessingDiv(url, action, false, false, handleOnAutoSaveWip, false, isButtonStyle(), false);
}

function handleOnAutoSaveWip(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                revertHiddenFields(hidElems);
                hidElems.splice(0, hidElems.length);
                return;
            }
            hidElems.splice(0, hidElems.length);
            showProcessingDivPopup();

            if(isTabStyle()){
                if(operation == "switchPrimaryTab") {
                    setWindowLocation(nextPrimaryTabAction);
                }else if(operation == "switchSecondlyTab") {
                    setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
                    refreshPage();
                }else if(operation == "switchGridRow") {
                    setCacheRowIds(getCurrentRow() + "," + getCurrentRow());
                    refreshPage();
                }else {
                    refreshPage();
                }
            }else {
                setWindowLocation(directionUrl);
            }
        }
    }
}

function commonIsOkToChangePages(id, url) {
    if (isPageDataChanged() || isIframeDataChanged()) {
        if (!confirm(getMessage("pm.common.clickOk.changesLost.confirm"))) {
            return false;
        }
    }
    return true;
}

function isIframeDataChanged() {
    if(isTabStyle()){
        if (eval("window.getIFrameWindow") && getIFrameWindow() &&
                typeof getIFrameWindow().isChanged != 'undefined' &&
                eval("getIFrameWindow().isPageGridsDataChanged") &&
                (getIFrameWindow().isChanged || getIFrameWindow().isPageGridsDataChanged() ||
                // The following code would check the data change for the case listed below.
                // On the Excess tab of policy page, when only make changes on Current Carrier section.
                // The Current Carrier section is not have its own grid list but it would be belong to the grid of Prior
                // Carrier section after do commonOnChange(). Then UPDATE_IND of Prior Carrier grid would be set to Y.
                (getIFrameWindow().getXMLDataForGridName(getIFrameWindow().getCurrentlySelectedGridId()) &&
                 getIFrameWindow().getXMLDataForGridName(getIFrameWindow().getCurrentlySelectedGridId()).recordset("UPDATE_IND").value == "Y"))) {
            return true;
        }
    }
    return false;
}

function commonGetMenuQueryString(id, url) {

    if ((id == "PM_FIND_POLICY") || (id == "PM_PT_FIND_POL") || (id == "PM_MAIN_HOME") || (id == "PM_CREPOL_HOME")
        || (id == "PM_CREATE_RENEWAL_BATCH") || (id == "PM_RENEWAL_EVENT") || (id == "PM_TRANS_UW")
        || (id == "PM_PLC_MLNG")) {
        // Don't pass any policy header fields to these pages so they don't load a policy header.
        return null;
    }
    // unlock previously held lock
    if (id == "PM_WFDIARY") {
        unLockPreviouslyHeldLock();
        return null;
    }

    // The majority of ePolicy pages always need the policyNo, policyTermHistoryId, and policyViewMode
    var tempUrl = '';
    if (hasObject("policyNo")) {
        var policyNo = getObjectValue("policyNo");
        if ((id == "PM_PT_VIEWRISK"
                || id == "PM_PT_VIEWRISKSUMMARY"
                || id == "PM_PT_VIEWCVG"
                || id == "PM_PT_VIEWCLASS"
                || id == "PM_PT_VIEWPOLICYSUMMARY") &&
                getSysParmValue("PM_AUTO_SAVE_WIP") == "Y" &&
                hasObject("policyNoEdit") && getObjectValue("policyNoEdit") != policyNo) {
            policyNo = getObjectValue("policyNoEdit");
        }
        tempUrl = tempUrl + "policyNo=" + policyNo;
    }

    if (hasObject("policyTermHistoryId")) {
        var policyTermHistoryId = getObjectValue("policyTermHistoryId");
        tempUrl = tempUrl + "&policyTermHistoryId=" + policyTermHistoryId;
    }

    if (hasObject("policyViewMode")) {
        var policyViewMode = getObjectValue("policyViewMode");
        tempUrl = tempUrl + "&policyViewMode=" + policyViewMode;
    }

    if (hasObject("wipNo")) {
        var wipNo = getObjectValue("wipNo");
        tempUrl = tempUrl + "&wipNo=" + wipNo;
    }

    if (hasObject("offNo")) {
        var offNo = getObjectValue("offNo");
        tempUrl = tempUrl + "&offNo=" + offNo;
    }
    if (typeof(policyHeader) != 'undefined') {
        var endorsementQuoteId = policyHeader.lastTransactionInfo.endorsementQuoteId;
        if (!isEmpty(endorsementQuoteId) && (endorsementQuoteId != 'null')) {
            tempUrl = tempUrl + "&endQuoteId=" + endorsementQuoteId;
        }
    }
    // Add parameter date to request to avoid IE cache
    tempUrl = tempUrl + "&date=" + new Date();

    return tempUrl;
}

/**
 Handle the Submit event with the desired action.
 The form[0].process is first set to the action name.
 Next, this function validates required form fields.
 If form fields pass the validation, this function checks for a custom page-specific submit handler
 in the form of 'handleOnSubmit' where action is passed as a parameter
 If the submit handler exists, it is called, and the return value used to determine if the submit should proceed.
 The submit handler is usefull for such things as validating the form, and for overriding the process value.

 If the submit handler returns true, or if there is not submit handler, this functions finishes by submitting the form.
 Otherwise, the submit handler will not submit the form.
 */
var currentSubmitAction;
var commonOnSubmitReturnTypes = {
    submitSuccessfully : "successful",
    commonValidationFailed : "commonValidationFailed",
    noDataChange : "noDataChange",
    saveInProgress : "saveInProgress",
    submitSuccessfullyWithPopup : "submitSuccessfullyWithPopup"
}
var autoSaveResultType;

function commonOnSubmit(action, skipFormValidation, skipGridValidation, saveIfNoChanges, showProcessingDivPopup) {
    currentSubmitAction = action;
    if (action.length >= 4 && action.substring(0, 4).toUpperCase() == "SAVE") {
        if (!isChanged && !isPageGridsDataChanged() && !saveIfNoChanges) {
            removeMessages();
            syncResultToParent(commonOnSubmitReturnTypes.noDataChange);
            return;
        }
    }
    if (isSaveInProgress() == false) {
        var proceed = true;

        // By default, set the process parameter to the proviced action, and submit the form.
        document.forms[0].process.value = action;

        // validate required fields in form/grid, except those in hidden Div
        var selectedGridId = getCurrentlySelectedGridId();
        if (!skipGridValidation && isDefined(selectedGridId) && (selectedGridId != "")) {
            proceed = commonValidateGrid(selectedGridId);
        }
        else if (!skipFormValidation) {
            proceed = commonValidateForm();
        }

        if (proceed) {
            // Check if a submit handler exists for this page
            var functionExists = eval("window.handleOnSubmit");

            if (functionExists != null) {
                // Call the page specific submit handler
                proceed = handleOnSubmit(action);
            }
        }else {
            autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
        }

        if (proceed) {
			autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
            ////processing after change 'change exp date', set cache before refresh page.
            if(isTabStyle() && (typeof secondlyTabDivId == 'undefined')){
                if(eval("getParentWindow(true).getCurrentTab")){
                    if(getParentWindow(true).getCurrentTab() == "CHG_EXP_DATE"){
                        getParentWindow(true).setCacheTabIds(getParentWindow(true).getCurrentTab(true) + "," + getParentWindow(true).getCurrentTab());
                        getParentWindow(true).setBtnOperation(getParentWindow(true).operation);
                    }
                }
            }
            submitForm(showProcessingDivPopup);
        }
        else {
            removeMessages();
        }
    }
    else {
		autoSaveResultType = commonOnSubmitReturnTypes.saveInProgress;
        alert(getMessage("pm.common.process.notCompleted"));
    }

	syncResultToParent(autoSaveResultType);
}

/*
 Get elements by class
 */
function getElementsByClass(searchClass, node, tag) {
    var classElements = new Array();
    if ( node == null )
        node = document;
    if ( tag == null )
        tag = '*';
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
    for (i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
            classElements[j] = els[i];
            j++;
        }
    }
    return classElements;
}

/*
    Submit the form as a Save action.
*/
function submitForm(forceToShowProcessingDivPopup) {
    if (isSaveInProgress() == false) {

        if (forceToShowProcessingDivPopup == undefined && (currentSubmitAction.substring(0, 4).toUpperCase() == "SAVE"
                || currentSubmitAction == 'Create' ||  currentSubmitAction == 'NAVIGATE'))
            showProcessingDivPopup();
        else if (forceToShowProcessingDivPopup)
            showProcessingDivPopup();

        enableFieldsForSubmit(document.forms[0]);

        var handleMultipleGrids = false;
        var functionExists = eval("window.submitMultipleGrids");
        if (functionExists) {
            handleMultipleGrids = submitMultipleGrids();
        }

        if (handleMultipleGrids) {
            // Populate data xml for all grids
            var len = tblPropArray.length;
            for (var i = 0; i < len; i++) {
                alternateGrid_update(tblPropArray[i].id);
            }
            submitFirstForm();
        } else {
            // Single grid submit
            if (getCurrentlySelectedGridId()) {
                eval(getCurrentlySelectedGridId() + "_update();")
            }
            else {
                submitFirstForm();
            }
        }
    }
    else {
        alert(getMessage("pm.common.process.notCompleted"));
    }
}

/*
    Enable and hide all disabled fields in a form before submit
*/
function enableFieldsForSubmit(theform) {
    var elems = theform.elements;

    for (var i = 0; i < elems.length; i++) {
        if (elems[i].disabled) {
            if (!(elems[i].style.visibility == "hidden")) {
                hidElems.push(elems[i]);
            }
            elems[i].style.visibility = "hidden";
            elems[i].disabled = false;
        }
    }
}

/*
    For function enableFieldsForSubmit() roll back
 */
function revertHiddenFields(hidElems) {
    for (var i = 0; i < hidElems.length; i++) {
        hidElems[i].style.visibility = "";
        //System will re-pageEntitle the enabled field, so no need to do it manually
    }
}

/*
    Sends an Ajax POST Request to submit the form, posting the form fields to the given uri.

    Parameters:
    uri - REQUIRED - The URI to invoke.
    process - OPTIONAL - If not null, the process request parameter is set to the provided process value.
    validateForm - OPTIONAL - If true, form will be validated.
        If validation fails, all processing is stopped.
    validateGrid - OPTIONAL - If true, grid is be validated before sending the request.
        If validation fails, all processing is stopped.
    callbackFunction - OPTIONAL - The JavaScript function to invoke when the Ajax request is complete.
        If not specified, the current page (or parent page if this is a DIV Popup) is reloaded.
    addConfirmationValues - OPTIONAL - If true, the confirmation values are added to the url.
 */
var postAjaxSubmitUrl;
var postAjaxSubmitCallbackFunction;

function postAjaxSubmit(uri, process, validateForm, validateGrid, callbackFunction, addConfirmationValue, async) {
	postAjaxSubmitWithProcessingDiv(uri, process, validateForm, validateGrid, callbackFunction, addConfirmationValue, async, true);
}

/**
** Added this function to allow user control if displaying the processing div or not.
**/
function postAjaxSubmitWithProcessingDiv(uri, process, validateForm, validateGrid, callbackFunction, addConfirmationValue, async, needToShowProcessingDiv) {

    var proceed = true;

    // validate required fields in form/grid, except those in hidden Div
    var selectedGridId = getCurrentlySelectedGridId();
    if (validateGrid && isDefined(selectedGridId) && (selectedGridId != "")) {
        proceed = commonValidateGrid(selectedGridId);
    }
    else if (validateForm) {
        proceed = commonValidateForm();
    }

    if (proceed) {

        // Add the app path if not present.
        if (uri.indexOf(getAppPath()) == -1) {
            uri = getAppPath() + uri;
        }

        // Add the process if requested
        if (isDefined(process)) {
            if (uri.indexOf('?') > -1) {
                uri += '&process=' + process;
            }
            else {
                uri += '?process=' + process;
            }
        }
        postAjaxSubmitUrl = uri;

        // Add the confirmation responses if requested
        if (addConfirmationValue) {
            for (var i = 0; i < confirmMessages.length; i++) {
                uri += "&" + confirmMessages[i].messageKey + ".confirmed=" + confirmResponses[i];
            }
        }

        if (isUndefined(callbackFunction)) {
            callbackFunction = commonHandleOnPostAjaxSubmitDone;
        }
        else {
            postAjaxSubmitCallbackFunction = callbackFunction;
        }
        showProcessingDivPopup();
        startRefresh(document.forms[0], uri, callbackFunction, async);
    }
}

function commonHandleOnPostAjaxSubmitDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            if (isDefined(window.frameElement)) {
                getParentWindow().refreshPage();
            }
            else {
                refreshPage();
            }
        }
    }
}

/*
    Sends an Ajax POST Request to submit the form after getting confirmation responses.
 */
function repostAjaxSubmitWithConfirmationValue(async) {
    // Get cached url
    var uri = postAjaxSubmitUrl;

    // Add the confirmation responses
    for (var i = 0; i < confirmMessages.length; i++) {
        uri += "&" + confirmMessages[i].messageKey + ".confirmed=" + confirmResponses[i];
    }

    // Set callback function
    var callbackFunction = postAjaxSubmitCallbackFunction;
    if (isUndefined(callbackFunction)) {
        callbackFunction = commonHandleOnPostAjaxSubmitDone;
    }

    // Start process
    showProcessingDivPopup();
    startRefresh(document.forms[0], uri, callbackFunction, async);
}

/*
    Return all columns, not just the changed columns as listed in the ROW.col attribute.
*/
function getChanges(ReferenceXML, filter) {
    return getChangesInRowsFormat(ReferenceXML, filter);
}

/*
    Return just the selected columns
*/
function getSelectedOnly(XMLData) {
    var modXML = XMLData.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    var nodelen = modXML.length;
    var rowNode;
    var columnNode;
    var numColumnNodes;
    var result;
    var ID;
    var displayInd;
    var displayRows = "";
    var nonDisplayRows = "";

    for (var i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        ID = rowNode.getAttribute("id");

        // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";

            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes()) {
                numColumnNodes = rowNode.childNodes.length;
                for (var j = 0; j < numColumnNodes; j++) {
                    columnNode = rowNode.childNodes.item(j);
                    var nodeValue = encodeXMLChar(columnNode.text);
                    result += "<" + columnNode.nodeName + ">" + nodeValue + "</" + columnNode.nodeName + ">";

                    if (columnNode.nodeName == "DISPLAY_IND")
                        displayInd = nodeValue;
                }
            }
            result += "</ROW>";

            if (displayInd == "Y")
                displayRows += result;
            else
                nonDisplayRows += result;
        }
    }

    result = "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
    return result;
}

/*
  Return an Array of form elements that are children of the DIV element with id = the given divId string.
 */
function getDetailFormElements(divId) {
    var detailElements = new Array();
    var detailDiv = getObject(divId);
    //to be canceled
    var allFormElements = detailDiv.document.forms[0].elements;
    detailIdx = 0;
    for (var i = 0; i < allFormElements.length; i++) {
        if (isNodeChildOfId(allFormElements[i], divId)) {
            detailElements[detailIdx++] = allFormElements[i];
        }
    }
    return detailElements
}

/*
  Return true if the given node is a child of a node with the id = the given id string; otherwise false.
 */
function isNodeChildOfId(node, id) {
    var isChild = false;
    for (var parent = node.parentNode; parent.nodeName.toUpperCase() != "FORM" && parent != document; parent = parent.parentNode) {
        if (parent.id == id) {
            isChild = true;
            break;
        }
    }
    return isChild;
}

function showPolicyHeaderLayer() {
    if (hasObject("PM_Policy_Header")) {
        var obj = getObject("PM_Policy_Header");
        if (obj.length) {
            for (var i = 0; i < obj.length; i++)
                hideShowElementByClassName(obj[i], false);
        }
        else
            hideShowElementByClassName(obj, false);
    }
}

var userReadyStateReadyPass = 0;
function commonOnBeforeSort(table, XMLData) {

    if (XMLData.recordset.recordCount > 0 && !getTableProperty(table, "sorting")) {
        userReadyStateReadyPass = 1
    }

    if (window.handleOnBeforeSort) {
        handleOnBeforeSort(table, XMLData);
    }
}

function commonOnAfterSort(table, XMLData) {

    if (window.handleOnAfterSort) {
        handleOnAfterSort(table, XMLData);
    }
}

function userReadyStateReady(table) {
    // do nothing if the table is not ready
    if (!table.id || table.readyState != 'complete')
        return;

    // When there is pagination, this function is called twice for sorting - once for the header and once for the data.
    // So do nothing for the first time.
    var pages = getTableProperty(table, "pages");
    var sorting = getTableProperty(table, "sorting");
    if (sorting && pages > 1 && userReadyStateReadyPass == 1) {
        userReadyStateReadyPass ++;
    }
    else {
        var functionExists = eval("window.setRowStyle");
        if (functionExists) {
            setRowStyle(table);
        }
        // invoke the commonReadyStateReady only if we're not in middle of a process
        if (!getTableProperty(table, "isInCommonAddRow") && !getTableProperty(table, "isInSelectRowById")) {
            commonReadyStateReady(table);

            if (window.handleReadyStateReady)
                handleReadyStateReady(table);
        }
    }
    // set the table ready flag so that commonAddRow can take care of the rest
    setTableProperty(table, "isUserReadyStateReadyComplete", true);
}

function commonReadyStateReady(table) {
    var selectedRow = getTableProperty(table, "selectedTableRowNo");
    var sorting = getTableProperty(table, "sorting");
    if (selectedRow && !sorting) {
        if (!dti.oasis.page.useJqxGrid()) {
            hiliteSelectRow(table.rows[selectedRow]);
            var rowid = getSelectedRow(table.id);
            selectRow(table.id, rowid);
        }
    }
    else {
        var fireSelectFirstRowInGrid = dti.oasis.grid.getProperty(table.id, "autoSelectFirstRow", true);
        if (fireSelectFirstRowInGrid == true) {
            selectFirstRowInGrid(table.id);
        }

        // rset sorting indicators
        if (sorting) {
            userReadyStateReadyPass = 0;
        }
    }
}

function commonOnLoad() {
    MM_preloadImages(getCorePath() + notesImage);
    MM_preloadImages(getCorePath() + noNotesImage);
    var empty = " ";
    var N = "N";
    var Y = "Y";
    var YSpace = "Y ";
    //    alert("empty = " + (empty ? true : false) + ", N = " + (N ? true : false) + ", Y = " + (Y ? true : false) + ", YSpace = " + (YSpace ? true : false))
    //    alert(!(Y == )
    /*
        if (empty) alert("empty is true");
        else alert("empty is false");
        if (N) alert("N is true");
        else alert("N is false");
        if (Y) alert("Y is true");
        else alert("Y is false");
        if (YSpace) alert("YSpace is true");
        else alert("YSpace is false");
    */

    //Invoke process dependent field-value layer function
    var functionExists = eval("window.processDeps");
    if (functionExists) {
        processDeps();
    }

    var functionExists = eval("window.handleConfirmations");
    if (functionExists) {
        handleConfirmations();
    }

    // take care of possible errors
    if (isDefined(hasErrorMessages)) {
        if (hasErrorMessages)
            handleError("");
    }

    if (hasObject("policyViewMode")) {
        oldPolicyViewMode = getObjectValue("policyViewMode");
    }
    if (hasObject("availablePolicyTerms")) {
        oldPolicyTerm = getObjectValue("availablePolicyTerms");
    }

    invokePreview();

    // issue 160360
    invokeODS();

    return true;
}

function commonOnLoadForDivPopup(popupframe) {
}

function commonOnChange(field) {
    var fieldName = getFieldName(field); 
    if (isObject(getObject("policyNo"))) {
        var policyNo = getObject("policyNo").value;
        if (policyNo == null) {
            if (getObject("policyNo").length > 1) {
                policyNo = getObject("policyNo")[0].value;
            }
        }
        if (fieldName == "availablePolicyTerms") {

            if (!commonIsOkToChangePages()) {
                field.value = oldPolicyTerm;
                return false;
            }

            var termId = field.value;
            var tempUrl = "";
            showProcessingDivPopup();

            //alert(parseInt(field.value))
            if (parseInt(field.value) > 0){
                var url = document.location.protocol + "//" + document.location.host + document.location.pathname
                    + "?policyNo=" + policyNo + "&policyTermHistoryId=" + termId
                    + tempUrl;
                setWindowLocation(url);
            } else {
                alert(getMessage("pm.common.list.validTerm.select.error"));
            }
        }
        else if (fieldName == "policyViewMode") {
            if (!commonIsOkToChangePages()) {
                field.value = oldPolicyViewMode;
                return false;
            }
            var termId = getObject("availablePolicyTerms").value;
            var viewMode = field.value;
            //wip or official
            if (viewMode == "WIP" || viewMode == "OFFICIAL") {
                loadPageByViewMode(viewMode);
            }
            //endquote
            else if (viewMode == "ENDQUOTE") {
                var endQuoteText = field.options[field.selectedIndex].text;
                //get endquoteId from the seletedOption.text
                var endQuoteId = endQuoteText.substring((parseInt(endQuoteText.indexOf(':')) + 2));
                loadPageByViewMode(viewMode, endQuoteId);
            }
            else if (viewMode.startsWith("QUOTE:")) {
                var policyNo = viewMode.substring(6);
                loadPageByViewMode("QUOTE", policyNo);
            }
        }
    }

    try {
        var dataSrc = getDataSrc(field);
        var dataFld = getDataField(field);
        dataSrc = dataSrc.substring(1);

        var dataGrid = eval(dataSrc);

        if (dataGrid.recordset("UPDATE_IND").value == "N")
            dataGrid.recordset("UPDATE_IND").value = "Y";

        setTableProperty(dataGrid, "gridDataChange", true);
        if (window.postOnChange) {
            postOnChange(field);
        }

    }
    catch(ex) {
        // handle case where the field is not part of the grid and has no dataSrc
        var isExcluded = false;
        var functionExists = eval("window.excludeFieldsForSettingUpdateInd");
        if (functionExists) {
            var excludedFields = excludeFieldsForSettingUpdateInd();
            for (var i = 0; i < excludedFields.length; i++) {
                if (fieldName == excludedFields[i]) {
                    isExcluded = true;
                    break;
                }
            }
        }

        //not set isChanged if a filter criteria is changed by a user in a filter panel
        //filter field should have a filter suffix
        if (!isExcluded) {
            var filterSuffix = "FILTER"
            // Fix the issue 103539, system should exclude the field that starts or ends with "FILTER".
            if(fieldName.toUpperCase().startsWith(filterSuffix) || fieldName.toUpperCase().endsWith(filterSuffix)){
                isExcluded = true;
            }
        }

        if (!isExcluded) {
            isChanged = true;
        }
    }

    // handle navigation
    if (fieldName == "policyNavSourceId" || fieldName == "riskNavSourceId" || fieldName == "coverageNavSourceId") {
        if(isButtonStyle()){
            if (isPageDataChanged() && getSysParmValue("PM_AUTO_SAVE_WIP") == "Y") {
                if (fieldName == "policyNavSourceId") {
                    directionUrl = getAppPath() + "/coveragemgr/maintainCoverage.do?" + commonGetMenuQueryString() +
                            getCoverageAutoSaveQueryString();
                    autoSaveSelectedCoverageWip();
                } else {
                    directionUrl = getAppPath() + "/coverageclassmgr/maintainCoverageClass.do?" + commonGetMenuQueryString() +
                            getCoverageClassAutoSaveQueryString();
                    autoSaveSelectedCoverageClassWip();
                }
            } else {
                if (field.value != '' && commonIsOkToChangePages()) {
                    commonOnSubmit('NAVIGATE', true, true, true);
                }
            }
        }else{
            if(eval("window.isReservedTab") && isReservedTab(getCurrentTab())){
                if (isPageDataChanged() && getSysParmValue("PM_AUTO_SAVE_WIP") == "Y") {
                    if (fieldName == "policyNavSourceId") {
                        directionUrl = getAppPath() + "/coveragemgr/maintainCoverage.do?" + commonGetMenuQueryString() +
                                getCoverageAutoSaveQueryString();
                        autoSaveSelectedCoverageWip();
                    } else {
                        directionUrl = getAppPath() + "/coverageclassmgr/maintainCoverageClass.do?" + commonGetMenuQueryString() +
                                getCoverageClassAutoSaveQueryString();
                        autoSaveSelectedCoverageClassWip();
                    }
                } else {
                    if (field.value != '' && commonIsOkToChangePages()) {
                        commonOnSubmit('NAVIGATE', true, true, true);
                    }
                }
            }else {
                if (isDefined(subFrameId) && eval("getIFrameWindow().isPageDataChanged") && getIFrameWindow().isPageDataChanged()) {
                    autoSaveSubIFrameForNavigation();
                } else {
                    if (field.value != '' && commonIsOkToChangePages()) {
                        commonOnSubmit('NAVIGATE', true, true, true);
                    }
                }
            }
        }
    }
}

function setIsChanged(boolIsChanged) {
    isChanged = boolIsChanged;
}

function commonOnGlobalSearch(field) {
     if (validatePolicyNoCriteria(field.value)) {
         var path = getAppPath() + "/policymgr/findPolicy.do?isGlobalSearch=Y" +
                    "&policyNoCriteria=" + encodeURIComponent(field.value) +
                    "&termStatusCode=ALL&process=findAllPolicy";
         setWindowLocation(path);
     }
}

function validatePolicyNoCriteria(policyNo) {
    if (!isEmpty(policyNo) && policyNo.indexOf("'") != -1) {
        alert(getMessage("pm.common.validate.policyNo.error"));
        hideProcessingImgIndicator();
        return false;
    }
    else {
        return true;
    }
}

function showMiniCISForEntityPk(entityIdField) {
    if (getObject(entityIdField) != null) {
        thisopenEntityMiniPopupWin(getObject(entityIdField).value);
    }
    else {
        alert(getMessage("pm.common.miniPopup.noEntityId.error", new Array(entityIdField)));
    }
}

function endorseTransaction(transactionCode) {
    var path = getAppPath() + "/transactionmgr/endorseTransaction.do?"
        + commonGetMenuQueryString("PM_CREATE_TRANSACTION")
        + "&transactionCode=" + transactionCode.toUpperCase() + "&process=display";
    var divPopupId = openDivPopup("", path, true, true, "", "", "", "", "", "", "", false);
}

var origTransactionCodeForDeleteWIP = "";
function deleteWipTransaction() {
    if (!confirm(getMessage("pm.common.wip.delete.confirm"))) {
        return;
        // return, nothing to do if user does not want to delete WIP
    }

    origTransactionCodeForDeleteWIP = policyHeader.lastTransactionInfo.transactionCode;
    var path = getAppPath() + "/transactionmgr/deleteWIPTransaction.do?"
        + commonGetMenuQueryString("PM_DEL_WIP_TRANSACTION")
        + "&process=delete";
    postAjaxSubmit(path, "deletWip", false, false, processAjaxResponseFordeleteWipTransaction);
}

function maintainAgent() {
    var maintainPolicyAgentUrl = getAppPath() + "/agentmgr/maintainPolicyAgent.do?"
        + commonGetMenuQueryString("PM_MTN_AGENT")
        + "&process=loadAllAgent";

    var divPopupId = openDivPopup("", maintainPolicyAgentUrl, true, true, "", "", 1220, 850, "", 875, "", false);

}
function viewPremium() {
    var viewPremiumUrl = getAppPath() + "/policymgr/premiummgr/viewPremium.do?"
        + commonGetMenuQueryString() + "&process=loadAllPremium";

    var divPopupId = openDivPopup("", viewPremiumUrl, true, true, "", "", 900, 600, "", "", "", false);
}

function viewPolicyHeaderPremium(transactionId) {
    var viewPremiumUrl = getAppPath() + "/policymgr/premiummgr/viewPremium.do?"
            + commonGetMenuQueryString() + "&transactionId=" + transactionId + "&process=loadAllPremium";

    var divPopupId = openDivPopup("", viewPremiumUrl, true, true, "", "", 900, 600, "", "", "", false);
}

function adjustPremium() {
    var url = getAppPath() + "/transactionmgr/premiumadjustmentprocessmgr/maintainPremiumAdjustment.do?"
        + commonGetMenuQueryString() + "&process=loadAllPremiumAdjustment";

    var divPopupId = openDivPopup("", url, true, true, "", "", 900, 600, 892, 592, "", false);
}

function viewTransaction() {
    var viewTransactionUrl = getAppPath() + "/transactionmgr/maintainTransaction.do?"
        + commonGetMenuQueryString() + "&process=loadAllTransaction";

    var divPopupId = openDivPopup("", viewTransactionUrl, true, true, "", "", 950, 830, 935, 830, "", false);
}
function extendCancelTerm() {
    var extendCancelTermConfirmMsg = getMessage("pm.transactionmgr.extendCancelTerm.confirm.info");
    if (confirm(extendCancelTermConfirmMsg)) {
        var extendCancelTermUrl = getAppPath() + "/transactionmgr/extendCancelTerm.do?"
            + commonGetMenuQueryString() + "&wipNo=" + getObjectValue("wipNo");
        var divPopupId = openDivPopup("", extendCancelTermUrl, true, true, "", "", 500, 400, "", "", "", false);
    }
}

function viewDividendAudit() {
    var dividendAuditUrl = getAppPath() + "/policymgr/dividendmgr/viewDividendAudit.do?"
            + commonGetMenuQueryString() + "&process=loadAllDividendAudit&showTermOrAll=T";
    var divPopupId = openDivPopup("", dividendAuditUrl, true, true, "", "", 980, 750, 950, 750, "", false);
}

function changePolicyAdmin() {
    var changePolicyAdminUrl = getAppPath() + "/transactionmgr/changePolicyAdministrator.do?"
        + commonGetMenuQueryString();

    var divPopupId = openDivPopup("", changePolicyAdminUrl, true, true, "", "", 500, 400, "", "", "", false);
}

function viewQuote() {
    var viewQuoteUrl = getAppPath() + "/quotemgr/viewQuote.do?"
            + commonGetMenuQueryString() + "&process=loadQuotes";

    var divPopupId = openDivPopup("", viewQuoteUrl, true, true, "", "", 800, 600, "", "", "", false);
}

function viewSoftValidation() {
    var viewSoftValidationsUrl = getAppPath() + "/policymgr/validationmgr/viewsoftvalidation.do?"
            + commonGetMenuQueryString() + "&process=loadSoftValidation";

    var divPopupId = openDivPopup("", viewSoftValidationsUrl, true, true, "", "", 1050, 500, "", "", "", false);
}

function initiateApp() {
    var initiateAppUrl = getAppPath() + "/policymgr/applicationmgr/maintainApplication.do?"
            + commonGetMenuQueryString() + "&process=initiateApp";
    new AJAXRequest("post", initiateAppUrl, "", handleOnInitiateApp, true);
    if (getSysParmValue("EAPP.PM.INIT.ASYNC") == "N") {
        showProcessingDivPopup();
    }
    else {
        alert(getMessage("pm.eApp.initiate.process.submit.info"));
    }
}

function handleOnInitiateApp(ajax) {
    if (getSysParmValue("EAPP.PM.INIT.ASYNC") == "N") {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                closeProcessingDivPopup();
                var data = ajax.responseXML;
                handleAjaxMessages(data);
            }
        }
    }
}

function maintainQuoteTransfer() {
    var viewQuoteUrl = getAppPath() + "/quotemgr/maintainQuoteTransfer.do?"
            + commonGetMenuQueryString() + "&process=loadQuoteTransfer";

    var divPopupId = openDivPopup("", viewQuoteUrl, true, true, "", "", 800, 600, "", "", "", false);
}

function processAjaxResponseFordeleteWipTransaction(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, refresh page
            else {
                if (origTransactionCodeForDeleteWIP == "NEWBUS" ||
                    origTransactionCodeForDeleteWIP == "CONVRENEW" ||
                    origTransactionCodeForDeleteWIP == "CONVREISSU" ||
                    origTransactionCodeForDeleteWIP == "QUOTE") {
                    setWindowLocation(getAppPath() + "/home.do");
                }
                else {
                    if(eval("window.clearCacheRowIds")){
                        clearCacheRowIds();
                    }
                    if(eval("window.clearCacheTabIds")){
                        clearCacheTabIds();
                    }
                    refreshPage("ALL");
                }
            }
        }
    }
}

var objectAccountingDate;
var objectComment;
var objectComment2;
var objectEndorsementCode;
var objectDeclineReasonCode;
var objectQuoteTransactionCode;
var objectConvertionType;
var transactionCode;
var eventHandler;

/*
*  eventHandler (function) is invoked without parameters.
*/
function captureTransactionDetails(transactionCode, eventHandler_, accountingDate) {

    // store the parameter value, so it can be used to call handleOnCapture.. function
    eventHandler = eventHandler_;

    var policyNo = getObjectValue("policyNo");
    var policyTermHistoryId = getObjectValue("policyTermHistoryId");
    var quoteCycleCode = getObjectValue("quoteCycleCode");

    var path = getAppPath() + "/transactionmgr/captureTransactionDetails.do?"
        + commonGetMenuQueryString("PM_CAPTURE_TRANSACTION_DETAIL", "")
        + "&transactionCode=" + transactionCode
        + "&quoteCycleCode=" + quoteCycleCode;

    if (isStringValue(accountingDate)) {
        path = path + "&accountingDate=" + accountingDate;
    }

    if (getSysParmValue("PM_SKIP_COMMENT_WIND") == 'N' ||
            (getSysParmValue("PM_DECLINE_TAIL_RESN") == 'Y' && transactionCode == 'TLDECLINE') ||
            policyAttributeObject.isDisplayCommentWindow(policyHeader.termEffectiveFromDate, transactionCode, quoteCycleCode, eventHandler_)) {
        //display a page to capture the input,
        path += "&process=display";
        var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, "", "", "600", "400", "", "", "", false);
        commonOnPutParentWindowOfDivPopup(divPopupId);
    }
    else {
        //use ajax to get default values as the background process
        path += "&process=getInitialValues";
        new AJAXRequest("get", path, '', handleOnCaptureTransactionDetails_Ajax, false);
    }
}
/*
*  eventHandler (function) is invoked without parameters.
*/
function captureTransactionDetailsWithEffDate(transactionCode, eventHandler_, transactionEffDate, accountingDate) {

    // store the parameter value, so it can be used to call handleOnCapture.. function
    eventHandler = eventHandler_;

    var policyNo = getObjectValue("policyNo");
    var policyTermHistoryId = getObjectValue("policyTermHistoryId");

    var path = getAppPath() + "/transactionmgr/endorseTransaction.do?"
        + commonGetMenuQueryString("PM_CREATE_TRANSACTION")
        + "&transactionCode=" + transactionCode.toUpperCase() + "&process=display" + "&isForCaptureTransactionDetail=Y";

    if (isStringValue(transactionEffDate)) {
        path = path + "&transactionEffDate=" + transactionEffDate;
    }

    if (isStringValue(accountingDate)) {
        path = path + "&accountingDate=" + accountingDate;
    }
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, "", "", "", "", "", "", "", false);
    commonOnPutParentWindowOfDivPopup(divPopupId);
}

function captureTransactionQuoteDetails(transactionCode, eventHandler_, accountingDate) {

    // store the parameter value, so it can be used to call handleOnCapture.. function
    eventHandler = eventHandler_;

    var policyNo = getObjectValue("policyNo");
    var policyTermHistoryId = getObjectValue("policyTermHistoryId");

    var path = getAppPath() + "/transactionmgr/captureTransactionQuoteDetails.do?"
        + commonGetMenuQueryString("PM_CAPTURE_TRANSACTION_DETAIL", "")
        + "&transactionCode=" + transactionCode;

    if (isStringValue(accountingDate)) {
        path = path + "&accountingDate=" + accountingDate;
    }

    path += "&process=display";
    var divPopupId = openDivPopup("", path, true, true, "", "", "", "", "", "", "", false);
}

function handleOnCaptureTransactionDetails_Ajax(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            setFormFieldValuesByObject(oValueList[0]);

            // create 4 objects to be used by eventHandler,
            objectAccountingDate = getObject("newAccountingDate");
            objectComment = getObject("newTransactionComment");
            objectComment2 = getObject("newTransactionComment2");
            objectEndorsementCode = getObject("newEndorsementCode");
            objectDeclineReasonCode = getObject("newDeclineReasonCode");

            if (isStringValue(eventHandler)) {
                autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
                eval(eventHandler + "()");
            }
        }
    }
}

function handleOnCaptureTransactionDetails(accountingDate, comment, endorsementCode, declineReasonCode, effecitiveFromDate, convertionType, transactionCode, comment2) {
    // insert fields dyanamically to the caller's form
    // This function is called by  clicking the button OK from captureTransactionDeatails.handleOnButtonClick
    // or after ajax call,
    objectAccountingDate = setInputFormField("newAccountingDate", accountingDate);
    objectComment = setInputFormField("newTransactionComment", comment);
    objectComment2 = setInputFormField("newTransactionComment2", comment2);
    objectEndorsementCode = setInputFormField("newEndorsementCode", endorsementCode);
    objectDeclineReasonCode = setInputFormField("newDeclineReasonCode", declineReasonCode);
    if (isStringValue(effecitiveFromDate)) {
        setInputFormField("newTransactionEffectiveFromDate", effecitiveFromDate)
    }
    objectConvertionType = setInputFormField("newConvertionType", convertionType);
    objectTransactionCode = setInputFormField("transactionCode", transactionCode);
    var functionExists = eval('window.processOperationBeforeSubTabSubmit');
    if(functionExists){
        processOperationBeforeSubTabSubmit();
    }
    if (isStringValue(eventHandler)) {
        eval(eventHandler + "()");
    }
}

function handleOnCaptureTransactionQuoteDetails(accountingDate, comment, endorsementCode, declineReasonCode, quoteTransactionCode, comment2) {
    // insert fields dyanamically to the caller's form
    // This function is called by clicking the button OK from captureTransactionQuoteDeatails.handleOnButtonClick
    objectAccountingDate = setInputFormField("newAccountingDate", accountingDate);
    objectComment = setInputFormField("newTransactionComment", comment);
    objectComment2 = setInputFormField("newTransactionComment2", comment2);
    objectEndorsementCode = setInputFormField("newEndorsementCode", endorsementCode);
    objectDeclineReasonCode = setInputFormField("newDeclineReasonCode", declineReasonCode);
    objectQuoteTransactionCode = setInputFormField("quoteTransactionCode", quoteTransactionCode);

    if (isStringValue(eventHandler)) {
        eval(eventHandler + "()");
    }
}

function valueDisplay() {
    //  this function exists for testing only!!!
    // in the submit_save() of maintainUnderwirter.js file:
    // after the line: document.forms[0].process.value = "saveAllUnderwriters";
    // add:
    ///////////////////////////////////////////////////////////////////////////
    //
    //   captureTransactionDetails("ENDPOLADD",'02/15/2008',"valueDisplay");
    //   isValid = false;
    //
    // ////////////////////////////////////////////////////////////////////
    alert("for maintainUnderwriter testing, The captured values:" +
          objectAccountingDate.value + ":" + ":" + objectComment.value + ":"
        + objectEndorsementCode.value + ":" + objectDeclineReasonCode.value);
}

function loadSaveOptions(id, eventHandler_) {

    // Store the parameter value
    eventHandler = eventHandler_;

    var url = "";
    var urlParameters = commonGetMenuQueryString(id, url);

    if (hasObject("collateralB")) {
        var collateralB = getObjectValue("collateralB");
        urlParameters = urlParameters + "&collateralB=" + collateralB;
    }
    url = getAppPath() + "/transactionmgr/loadSaveOptions.do?" + urlParameters;
    //it's for re-rate policy,it always has only two options
    if (id == "ONLY_WIP_OFFICIAL") {
        url = url + "&onlyWipOfficial=Y";
    }
    var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false,"","",false);
}

function handleSaveOptionSelection(saveOption) {

    // insert fields dyanamically to the caller's form
    setInputFormField("newSaveOption", saveOption);

    if (isStringValue(eventHandler)) {
        var functionExists = eval(eventHandler);
        if (functionExists) {
            eval(eventHandler + "()");
        }
        else {
            alert(getMessage("pm.common.handle.saveOption.error", new Array(eventHandler)));
        }
    }
}

function hideActionItem(itemId)
{
    if (getSingleObject("R_actionitem_" + itemId) != null)
        hideShowElementByClassName(getSingleObject("R_actionitem_" + itemId), true);
}

function showActionItem(itemId)
{
    if (getSingleObject("R_actionitem_" + itemId) != null)
        hideShowElementByClassName(getSingleObject("R_actionitem_" + itemId), false);
}

function hideShowForm(gridId) {
    var currentTbl = getTableForXMLData(getXMLDataForGridName(gridId));
    var gridDetailDivId = getTableProperty(currentTbl, "gridDetailDivId");

    if (isStringValue(gridDetailDivId)) {
        if (hasObject(gridDetailDivId))
            hideShowElementByClassName(getSingleObject(gridDetailDivId),
                ( (getTableProperty(currentTbl, "hasrows") == true)) ? false : true);
    }
}
function commonOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DELETE':
            var currentGrid = getCurrentlySelectedGridId();
            commonDeleteRow(currentGrid);
            break;

        case 'ADD':
            var currentGrid = getCurrentlySelectedGridId();
            commonAddRow(currentGrid);
            break;

        case 'CLOSE_DIV':
            closeThisDivPopup(false);
            break;

        case 'CLOSE_RO_DIV':
            closeThisDivPopup(true);
            break;

        case 'PREVIEW':
            //This function is called in maintainCoverage.js as well because the commonOnButtonClick()
            // has been overridden in maintaincoverage.js
            onPreviewButtonClick();
            break;
    }

    //Invoke page dependent button click logic if it exists
    var functionExists = window.eval && eval("window.handleOnButtonClick");
    if (functionExists != null) {
        eval("handleOnButtonClick('" + asBtn + "');");
    }
}

function onPreviewButtonClick() {
    var policyId = getObjectValue("policyId");
    var effFromDate = policyHeader.termEffectiveFromDate;
    var effToDate = policyHeader.termEffectiveToDate;
    var tranLogId = policyHeader.lastTransactionInfo.transactionLogId;
    var url = getCSPath() + "/outputmgr/processForms.do?process=validateManuscriptForms";
    url += "&policyId=" + policyId ;
    url += "&transLogId=" + policyHeader.lastTransactionInfo.transactionLogId;
    url += "&termEff=" + effFromDate;
    url += "&termExp=" + effToDate;
    if(commonCheckIsJobBasedOutput()) {
        alert(getMessage("cs.outputmgr.processOutput.job.based.output.warning", new Array("Preview")));
        return;
    }
    new AJAXRequest("get", url, "", function(ajax) {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var data = ajax.responseText;
                if ('Y' == data) {
                    setInputFormField("isPreviewRequest", "Y");
                    commonOnSubmit('SAVEWIP', true, true, true);
                } else {
                    alert(data);
                }
            }
        }
    }, false);

    return true;
}

function isEmptyRecordset(recordSet) {
    return !recordSet || (recordSet && recordSet.RecordCount == 0);
}

function commonDeleteRow(gridId) {
    var rs = getXMLDataForGridName(gridId).recordset;
    if (!isEmptyRecordset(rs) && confirm(getMessage("pm.common.selected.record.delete.confirm"))) {
        if (isTabStyle()) {
            var functionExists = eval("window.clearCacheRowIds");
            //clear cache only when delete risk/coverage
            if (functionExists && (gridId == "coverageListGrid" || gridId == "riskListGrid")) {
                clearCacheRowIds();
                clearCacheTabIds();
            }
        }

        dti.oasis.grid.commonDeleteRow(gridId);

        // After the record is deleted, check if there is no record in the grid.
        // If the grid is empty, manually select the first tab by default.
        rs = getXMLDataForGridName(gridId).recordset;
        if (isEmptyRecordset(rs) && isTabStyle() && (gridId == "coverageListGrid" || gridId == "riskListGrid")) {
            selectTabById(getFirstTab());
        }
    }else {
        operation = undefined;
    }
}

function commonAddRow(gridId) {
    dti.oasis.grid.commonAddRow(gridId);
}

function postCommonAddRow(gridId) {
    var table = getTableForGrid(gridId);
    priorRowId = dti.oasis.grid.postCommonAddRow(gridId);

    // call handlePostAddRow if exists
    if (window.handlePostAddRow)
        handlePostAddRow(table);
}

/*
    Set the initial values in the named grid from the array of dataPairs.
    The dataPairs parameter is an array of dataKey/dataValue pairs,
    where the first dataKey is at the provided startingIndex,
    and the first dataValue is at startingIndex + 1.
*/
function setInitialValuesInGrid(gridName, dataPairs, startingIndex) {
    var i = startingIndex;
    var dataLength = dataPairs.length - 1;
    while (i < dataLength) {
        var initValue = gridName + ".recordset('C" + dataPairs[i].toUpperCase() + "').value = '" + dataPairs[i + 1] + "'";
        eval(initValue);
        if (hasObject(dataPairs[i]) && hasObject(dataPairs[i] + "LOVLABELSPAN")) {
            field = getObject(dataPairs[i]);
            field.value = dataPairs[i + 1];
            syncToLovLabelIfExists(field);
        }
        i = i + 2;
    }
}

//-----------------------------------------------------------------------------
// Get entityOwnerId by location.
//-----------------------------------------------------------------------------
function getEntityOwnerId() {
    entityOwnerId = null;
    var functionExists = eval("window.getLocationPropertyId");
    if (functionExists) {
        var propertyId = getLocationPropertyId();
        if (!isEmpty(propertyId) && propertyId > 0) {
            var path = getAppPath() + "/riskmgr/maintainRisk.do?process=getEntityOwnerId&propertyId=" + propertyId + "&date=" + new Date();
            new AJAXRequest("get", path, '', setEntityOwnerId, false);
        }
    }
    return entityOwnerId;
}

//-----------------------------------------------------------------------------
// Callback function for getEntityOwnerId.
//-----------------------------------------------------------------------------
function setEntityOwnerId(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                entityOwnerId = oValueList[0]["ENTITYOWNERID"];
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Opens the entity mini popup window.
//-----------------------------------------------------------------------------
function thisopenEntityMiniPopupWin(pk) {
    if (pk == 0) {
        var entityOwnerId = getEntityOwnerId();
        if (!isEmpty(entityOwnerId) && entityOwnerId > 0) {
            pk = entityOwnerId;
        }
        else {
            alert(getMessage("pm.common.miniEntity.open.error"));
            return;
        }
    }
    if (getSysParmValue("PM_NB_QUICK_QUOTE") == "N") {
        var path = getAppPath() + "/policymgr/maintainPolicy.do?process=isPolicyEntity" +"&entityId=" + pk;
        new AJAXRequest("POST", path, null, validateEntitySource, false);
    } else {
        var path = getCISPath() + "/ciEntityMiniPopup.do?pk=" + pk;

        var functionExists = eval("window.isOpenEntityMiniPopupByFrame");
        var param = 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars';
        var mainwin;
        if (functionExists && isOpenEntityMiniPopupByFrame()) {
            var parentWindow = getParentWindow();
            if (parentWindow) {
                mainwin = parentWindow.open(path, 'EntityMiniPopup', param);
            }
            else {
                mainwin = window.open(path, 'EntityMiniPopup', param);
            }
            mainwin.focus();
        }
        else if (isDivPopupEnabled()) {
            getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, "", "", "900", "850", "890", "840", "", true);
        }
        else {
            mainwin = window.open(path, 'EntityMiniPopup',param);
            mainwin.focus();
        }
    }
}

function validateEntitySource(ajax) {
    if (ajax.readyState == 4 && ajax.status == 200) {
        var data = ajax.responseText;
        if (data.substr(0,1) == "Y") {
            alert(getMessage("pm.maintainPolicy.isPolicyEntity.filter"));
            return;
        }
        else {
            var path = getCISPath() + "/ciEntityMiniPopup.do?pk=" + data.substr(1,data.length-1);

            var functionExists = eval("window.isOpenEntityMiniPopupByFrame");
            var param = 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars';
            var mainwin;
            if (functionExists && isOpenEntityMiniPopupByFrame()) {
                var parentWindow = getParentWindow();
                if (parentWindow) {
                    mainwin = parentWindow.open(path, 'EntityMiniPopup', param);
                }
                else {
                    mainwin = window.open(path, 'EntityMiniPopup', param);
                }
                mainwin.focus();
            }
            else if (isDivPopupEnabled()) {
                getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, "", "", "900", "850", "890", "840", "", true);
            }
            else {
                mainwin = window.open(path, 'EntityMiniPopup',param);
                mainwin.focus();
            }
        }
    }
}

function commonEnableDisableFormFields(detailDivId) {
    if (isStringValue(detailDivId)) {
        var editInd;
        if (hasObject("EDIT_IND")) {
            editInd = getObjectValue('EDIT_IND');
        }
        if (editInd) {
            var isDisabled = (editInd == 'N');
            var detailFormElements = getDetailFormElements(detailDivId);
            for (var i = 0; i < detailFormElements.length; i++) {
                enableDisableField(detailFormElements[i], isDisabled);
            }
        }
    }
}

function commonOnRowSelected(gridId, rowId) {
    try {
        //        var indVal = "";
        //        if (indVal) alert("if(" + indVal + ") == true"); else alert("if(" + indVal + ") == false");
        //        indVal = "Y";
        //        if (indVal) alert("if(" + indVal + ") == true"); else alert("if(" + indVal + ") == false");
        //        indVal = "N";
        //        if (indVal) alert("if(" + indVal + ") == true"); else alert("if(" + indVal + ") == false");

        if (priorRowId != rowId || rowId == null) {
            //Invoke process dependent field-value layer function
            var functionExists = eval("window.processDeps");
            if (functionExists) {
                processDeps();
            }

            //processDeps() will hide all layers automatically - so make sure the policy header layer is always displayed
            showPolicyHeaderLayer();
            priorRowId = rowId;
        }
    }
    catch(ex) {
        alert(getMessage("pm.common.row.selected.error", new Array(ex.name,ex.message)));
    }

    return true;
}

/*
function commonRowchange(obj) {
    if (obj.name == "chkCSELECT_IND") {
        if (typeof obj.parentElement.parentElement.href!='undefined') {
            eval(obj.parentElement.parentElement.href);
        }
    }
}
*/


/**
 Open CIS Select Entity page to select a policyholder
 */
function selectPolicyHolderForCreatePolicy(requestContext, policyCycleCode) {
    // Fix issue 94398:Avoid changing the values of policyHolderNameEntityId and policyHolderName in page,system should create different parameters here.
    // add temp policyholder entity ID holder
    addInputFormField("policyHolderNameEntityIdNew");

    // add temp policyholder name holder
    addInputFormField("policyHolderNameNew");

    // add temp request context holder and store value in it
    setInputFormField("requestContext", requestContext);

    // add temp policy cycle holder and store value in it
    setInputFormField("policyCycleCode", policyCycleCode);

    // open client select page
    openEntitySelectWinFullName("policyHolderNameEntityIdNew", "policyHolderNameNew",
        "handleOnSelectPolicyHolderForCreatePolicy()");
}

/**
 Event handler called by CIS Select Entity page when a policyholder is selected
 */
function handleOnSelectPolicyHolderForCreatePolicy() {
    // check if we have a policyholder entity ID
    var obj = getObject("policyHolderNameEntityIdNew");
    if (obj) {
        var policyHolderNameEntityId = obj.value;
        if (policyHolderNameEntityId != null && policyHolderNameEntityId > 0) {
            // open Create Policy page
            var path = getAppPath() + "/policymgr/createPolicy.do?process=display" +
                       "&policyHolderNameEntityId=" + getObjectValue("policyHolderNameEntityIdNew") +
                       "&policyHolderName=" + encodeURIComponent(getObjectValue("policyHolderNameNew")) +
                       "&requestContext=" + getObjectValue("requestContext") +
                       "&policyCycleCode=" + getObjectValue("policyCycleCode");
            var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "createPolicy", false);
        }
    }
}

/**
 Entity Lookup
 */
var entitylookupEntityIdFieldName;
var entitylookupEntityNameFieldName;
var entityLookupEventHandler;

function lookupEntity(entityClassCode, effectiveFromDate, entityIdFieldName, entityNameFieldName, eventHandler) {
    entitylookupEntityIdFieldName = entityIdFieldName;
    entitylookupEntityNameFieldName = entityNameFieldName;
    entityLookupEventHandler = eventHandler;

    var path = getAppPath() +
               "/entitymgr/lookupEntity.do?entityClassCode=" + entityClassCode +
               "&effectiveFromDate=" + effectiveFromDate;
    var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "lookupEntity", false);
}

function handleOnLookupEntity(action, entityId, entityName) {
    if (action == "Select") {
        // set in the selected entity in caller's form
        setInputFormField(entitylookupEntityIdFieldName, entityId);
        if (entitylookupEntityNameFieldName && !isEmpty(entitylookupEntityNameFieldName)) {
            setInputFormField(entitylookupEntityNameFieldName, entityName);
        }
        // trigger caller's event handler
        if (isStringValue(entityLookupEventHandler)) {
            try {
                eval(entityLookupEventHandler);
            }
            catch (ex) {
                alert(getMessage("pm.common.handle.saveOption.error", new Array(entityLookupEventHandler)));
            }
        }
    }
}

//this method will call pm_get_pm_attr to get entity role type
var entityRoleType = null;
function getEntityRoleType(typeCode, policyType, riskType, transactionDate) {
    var path = getAppPath() + "/entitymgr/lookupEntity.do?process=getEntityRoleType" +
               "&typeCode=" + typeCode +
               "&policyType=" + policyType +
               "&riskType=" + escape(riskType) +
               "&transactionDate=" + transactionDate;
    new AJAXRequest("get", path, '', setEntityRoleType, false);
    return entityRoleType;
}

function setEntityRoleType(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }
            if (data.getElementsByTagName("ENTITYROLETYPE")[0].firstChild) {
                entityRoleType = data.getElementsByTagName("ENTITYROLETYPE")[0].firstChild.data;
            }
        }
    }
}
/**
 Account Lookup
 */
var acctLookupAccountIdFieldName;
var acctLookupAccountNoFieldName;
var acctLookupEventHandler;

function lookupAccount(accountHolderEntityId, effectiveFromDate, effectiveToDate,
                       accountIdFieldName, accountNoFieldName, eventHandler) {
    // store the parameter value, so it can be used later
    acctLookupAccountIdFieldName = accountIdFieldName;
    acctLookupAccountNoFieldName = accountNoFieldName;
    acctLookupEventHandler = eventHandler;

    // open the account lookup window
    var path = getAppPath() +
               "/acctlookupmgr/lookupAccount.do?accountHolderEntityId=" + accountHolderEntityId +
               "&effectiveFromDate=" + effectiveFromDate +
               "&effectiveToDate=" + effectiveToDate;
    var divPopupId = openDivPopup("", path, true, true, "", "", "", "", "", "", "lookupAccount", false);
}

function handleOnLookupAccount(action, accountId, accountNo) {
    switch (action.toUpperCase()) {
        case "SELECT":
        // set in the selected billing account in caller's form
            setInputFormField(acctLookupAccountIdFieldName, accountId);
            setInputFormField(acctLookupAccountNoFieldName, accountNo);

        // trigger caller's event handler
            if (isStringValue(acctLookupEventHandler)) {
                try {
                    eval(acctLookupEventHandler + "('" + action + "')");
                }
                catch (ex) {
                    alert(getMessage("pm.common.handle.saveOption.error", new Array(acctLookupEventHandler)));
                }
            }
            break;
        case "CANCEL":
        // trigger caller's event handler
            if (isStringValue(acctLookupEventHandler)) {
                try {
                    eval(acctLookupEventHandler + "('" + action + "')");
                }
                catch (ex) {
                    alert(getMessage("pm.common.handle.saveOption.error", new Array(acctLookupEventHandler)));
                }
            }
    }

}

function billingSetup() {
    // Verify the relation.
    var policyId = policyHeader.policyId;
    var url = getAppPath() + "/billingmgr/maintainBilling.do?policyId=" + policyId
        + "&process=validatePolicyRelation"+ "&date=" + new Date();
    new AJAXRequest("get", url, '', validateRelationExistForDone, false);
}

function viewNonPremium(amountType) {
    var viewFundUrl = getAppPath() + "/policymgr/premiummgr/viewFund.do?"
        + commonGetMenuQueryString() + "&process=loadAllFund&detailType=" + amountType;
    var divPopupId = openDivPopup("", viewFundUrl, true, true, "", "", 800, 600, "", "", "", false);
}

function validateRelationExistForDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["EXISTB"] == 'Y') {
                    var policyId = policyHeader.policyId;        
                    var policyNo = policyHeader.policyNo;
                    var fmUrl = getTopNavApplicationUrl("FM") + "/billingrelationmgr/maintainBillingRelation.do?process=loadAllBillingRelation&policyId="
                        + policyId + "&policyNo=" + policyNo + "&headerHidden=Y";
                    var divPopupId = openDivPopup("", fmUrl, true, true, "", "", "1180", "950", "", "", "", false);
                }
                else {
                    var policyId = policyHeader.policyId;
                    var billlingSetupURL = getAppPath() + "/billingmgr/maintainBilling.do?" + commonGetMenuQueryString("PM_BILLINGSETUP")
                        + "&policyId=" + policyId ;
                    var divPopupId = openDivPopup("", billlingSetupURL, true, true, "", "", 1100, 950, "", "", "", false);
                }
            }
        }
    }
}

function reissuePolicy() {
    var reissuePolicyUrl = getAppPath() + "/transactionmgr/reissueprocessmgr/reissuePolicy.do?" +
                           commonGetMenuQueryString("PM_REISSUE_POLICY");

    /*    var ai = getObject("R_actionitem_PM_REISSUE_POL");
    if (ai) {
        // From <TR> object, get to the <A> anchor element and fire the onmouseout event to unhilite the <A> element.
        ai.children[0].children[0].fireEvent('onmouseout');
    }*/
    var divPopupId = openDivPopup("", reissuePolicyUrl, true, true, "", "", "", "", "", "", "", false);
}

//-----------------------------------------------------------------------------
// Open a popup page to let underwriter change term dates
//-----------------------------------------------------------------------------
function changeTermEffDate() {
    // Check if there are any data changed
    var isOkToProceed = commonIsOkToChangePages("", "");
    if (!isOkToProceed) {
        return;
    }

    var changeTermEffUrl = getAppPath() + "/transactionmgr/changeTermDates.do?" +
                           commonGetMenuQueryString("PM_CHANGE_TERM_EFF_DATE");

    var divPopupId = openDivPopup("", changeTermEffUrl, true, true, "", "", "", "", "", "", "", false);
}

function processOutputFromPM() {
    var procecessFileUrl = getCSPath() +"/outputmgr/processOutput.do?"
        + commonGetMenuQueryString("PM_PROCESS_OUTPUT");

    var policyId = policyHeader.policyId;
    if (policyId != "") {
        procecessFileUrl += "&sourceTableName=POLICY&sourceRecordFk=" + policyId +
                            "&subsystemId=" + "PMS" +
                            "&lastTransactionId=" + policyHeader.lastTransactionId;
    }

    var divPopupId = openDivPopup("", procecessFileUrl, true, true, "", "", "900", "700", "", "", "", true);
}

//-----------------------------------------------------------------------------
// Transform the grid's current selected row to object
//-----------------------------------------------------------------------------
function getObjectFromRecordset(gridId) {
    var excludeFieldId = ["CSELECT_IND","UPDATE_IND",
        "DISPLAY_IND","EDIT_IND","id","index",
        "col","$Text"];
    var fieldCount = gridId.recordset.Fields.count;
    var oRecord = new Object();
    // loop through columns
    for (var i = 0; i < fieldCount; i++) {
        var fieldName = gridId.recordset.Fields.Item(i).name;
        var fieldValue = gridId.recordset.Fields.Item(i).value;
        var exlCount = excludeFieldId.length;

        // Check if current field should be excluded
        var isFound = false;
        for (var j = 0; j < exlCount; j++) {
            if (fieldName == excludeFieldId[j]) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            continue;
        }

        // Exclude field which value contain "selectRow"
        if (fieldValue.indexOf("selectRow") != -1) {
            continue;
        }

        // Exclude field which value contain "javascript"
        if (fieldValue.indexOf("javascript") != -1) {
            continue;
        }

        fieldName = fieldName.substring(1);
        try {
            eval("oRecord." + fieldName + " = gridId.recordset.Fields.Item(i).value");
        }
        catch (ex) {
            alert(getMessage("pm.common.getRecord.run.error", new Array(fieldName)));
        }
    }
    return oRecord;
}

//-----------------------------------------------------------------------------
// Set default value for grid's current selected row by data object
//-----------------------------------------------------------------------------
function setRecordsetByObject(XMLData, dataObject, excludeEditDisplayUpdateInd) {
    setCurrentRecordValues(XMLData, dataObject, excludeEditDisplayUpdateInd);
}

// refresh the page with policyViewMode WIP
function refreshPage(level) {
    var url = location.href;
    // Strip of information after the "?"
    if (url.indexOf('?') > -1) {
        url = url.substring(0, url.indexOf('?'));
    }
    else if (url.indexOf('#') > -1) {
        url = url.substring(0, url.indexOf('#'));
    }

    url = buildMenuQueryString("", url);
    url = removeParameterFromUrl(url, "policyViewMode");
    url += "&policyViewMode=WIP";
    // add parameters for loading risk relation page only
    if (getObject("riskEffectiveFromDate")) {
        url += "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate");
    }
    if (getObject("riskEffectiveToDate")) {
        url += "&riskEffectiveToDate=" + getObjectValue("riskEffectiveToDate");
    }
    if (getObject("riskCountyCode")) {
        url += "&riskCountyCode=" + getObjectValue("riskCountyCode");
    }
    if (getObject("currentRiskTypeCode")) {
        url += "&currentRiskTypeCode=" + escape(getObjectValue("currentRiskTypeCode"));
    }
    if (getObject("origRiskEffectiveFromDate")) {
        url += "&origRiskEffectiveFromDate=" + getObjectValue("origRiskEffectiveFromDate");
    }
    if (policyHeader.riskHeader) {
        url += "&riskId=" + policyHeader.riskHeader.riskId;
    }
    if (hasObject("isReverse") && getObjectValue("isReverse")=="true") {
        url += "&reverse=Y";
    }
    // end adding parameters for loading risk relation page

    if (level == "RISK" || level == "ALL" || level == "POLICY") {
        url = removeParameterFromUrl(url, "riskId");
        url = removeParameterFromUrl(url, "coverageId");
        url = removeParameterFromUrl(url, "coverageClassId");
    }else if (level == "COVERAGE"){
        url = removeParameterFromUrl(url, "coverageId");
        url = removeParameterFromUrl(url, "coverageClassId");
    }else if (level == "COVERAGE CLASS"){
        url = removeParameterFromUrl(url, "coverageClassId");
    }

	if(isTabStyle()){
        url = appendUIparameterIntoUrl(url);
    }
    if(isDefined(url)){
        setWindowLocation(url);
    }
}

function appendUIparameterIntoUrl(url) {
    //processing after change 'change exp date', then changes primary tab in tab style
    if(typeof switchPrimaryTabFlg != 'undefined'){
        if(isDefined(switchPrimaryTabFlg) && switchPrimaryTabFlg){
            nextPrimaryTabAction = removeParameterFromUrl(nextPrimaryTabAction, "policyViewMode");
            nextPrimaryTabAction += "&policyViewMode=WIP";
            switchPrimaryTabFlg = false;
            setWindowLocation(nextPrimaryTabAction);
            return;
        }
    }
    url += "&cacheTabIds=" + (isUndefined(getObjectValue("cacheTabIds")) ? "" : getObjectValue("cacheTabIds"));
    url += "&cacheRowIds=" + (isUndefined(getObjectValue("cacheRowIds")) ? "" : getObjectValue("cacheRowIds"));
    url += "&cacheBtnOperation=" + (isUndefined(getObjectValue("cacheBtnOperation")) ? "" : getObjectValue("cacheBtnOperation"));
    return url;
}

// remove a given parameter (its name and value) from url
function removeParameterFromUrl(url, parameterName) {
    while (url.indexOf(parameterName) != -1) { // url might contain the same parameter more than once!
        var parameterIndex = url.indexOf(parameterName);
        var nextAmpsantIndex = url.indexOf('&', parameterIndex + 1);
        if (nextAmpsantIndex == -1) {
            parameterNameValueText = url.substring(parameterIndex);
        }
        else {
            parameterNameValueText = url.substring(parameterIndex, nextAmpsantIndex);
        }
        url = url.replace(parameterNameValueText, "");
        url = url.replace("&&", "&");
    }
    return url;
}

function setInitialUrlInGrid(xmlData) {
    dti.oasis.grid.setInitialUrlInGrid(xmlData);
}

function setFormFieldValuesByObject(dataObject) {
    for (var prop in dataObject) {
        setInputFormField(prop, dataObject[prop]);
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
        }
    }
}

//-----------------------------------------------------------------------------
// Commond method to handle get Additional informaton by Ajax
//-----------------------------------------------------------------------------
function commonHandleOnGetAddlInfo(ajax) {
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
                var currentlySelectedGridId = getCurrentlySelectedGridId();
                if (!isEmpty(currentlySelectedGridId)) {
                    var selectedDataGrid = getXMLDataForGridName(currentlySelectedGridId) ;
                    setRecordsetByObject(selectedDataGrid, oValueList[0], true);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Get the object ignore the case
//-----------------------------------------------------------------------------
function getObjectIgnoreCase(objName) {
    var elems = document.forms[0].elements;

    /* loop through elements */
    for (var i = 0; i < elems.length; i++) {
        if (elems[i].name.toUpperCase() == objName.toUpperCase()) {
            return elems[i];
        }
    }
    return undefined;
}


//-----------------------------------------------------------------------------
// Sync LOV value in both grid header and page form
//-----------------------------------------------------------------------------
function syncLov(XMLData, fieldName, fieldValue) {
    /* Sync only LOVLABEL field is populated */
    if (isFieldExistsInRecordset(XMLData.recordset, "C" + fieldName + "LOVLABEL")) {
        /* Sync value in grid header */
        var fld = getObjectIgnoreCase(fieldName + "_GH");
        if (fld != undefined && fld.tagName == "SELECT") {
            XMLData.recordset("C" + fieldName + "LOVLABEL").value = getOptionDescription(fld, fieldValue);
        }

        /* Sync value in page form */
        fld = getObjectIgnoreCase(fieldName);
        if (fld != undefined && fld.tagName == "SELECT") {
            XMLData.recordset("C" + fieldName + "LOVLABEL").value = getOptionDescription(fld, fieldValue);
        }
    }
}

//-----------------------------------------------------------------------------
// Check if there are any modified records before OOSE risk/coverage/class/component data
//-----------------------------------------------------------------------------
var isForOose = "N";
function preOoseChangeValidation(type, gridId, baseRecordIdColumnName) {
    isForOose = "Y";
    var valid = true;
    var XMLData = getXMLDataForGridName(gridId);
    var rowIndex = 0;
    var currentRowId = getSelectedRow(gridId);
    var baseRecordId = XMLData.recordset(baseRecordIdColumnName).value;
    var recordModeCode = XMLData.recordset("CRECORDMODECODE").value;
    var selectedRowIndex = -1;

    // Loop the riskListGrid recordset to do the check
    first(XMLData);
    while (!XMLData.recordset.eof) {
        var curRecordModeCode = XMLData.recordset("CRECORDMODECODE").value;
        var curBaseRecordId = XMLData.recordset(baseRecordIdColumnName).value;

        if(currentRowId == XMLData.recordset("ID").value) {
            selectedRowIndex = rowIndex;
        }

        if (!isEmpty(curBaseRecordId)
            && curBaseRecordId == baseRecordId
            && (curRecordModeCode == "TEMP" || curRecordModeCode == "REQUEST")) {
            valid = false;
            currentRowId = XMLData.recordset("ID").value;
            alert(getMessage("pm.oose.modified.record.exist.error2", new Array(type)));
            break;
        }
        rowIndex ++;
        next(XMLData);
    }
    selectRowById(gridId, currentRowId, (selectedRowIndex != -1 && valid ) ? selectedRowIndex : rowIndex);
    return valid;
}

//-----------------------------------------------------------------------------
// update the alternated grid
//-----------------------------------------------------------------------------
function alternateGrid_update(grid, filterValue) {
    // get coverageListGrid changes and set into coverageListGridtxtXML
    var modValue = '';
    var isFilterFlag = filterflag;
    var gridTbl = eval(grid);
    var gridXML = eval(grid + '1');
    var origGridXml = eval('orig' + grid + '1');
    var gridTxtXml = grid + 'txtXML' ;

    if (isMultiGridSupported) {
        isFilterFlag = getTableProperty(getTableForXMLData(gridXML), 'filterflag');
    }
    if (isFilterFlag) {
        syncChanges(origGridXml, gridXML, filterValue);
        modValue = getChanges(origGridXml);
    }
    else {
        modValue = getChanges(gridXML);
    }
    setInputFormField(gridTxtXml, modValue);
}

//-----------------------------------------------------------------------------
// update the status of SELECT_IND checkbox in the grid
// when click top selectAll/deselectAll checkbox
//-----------------------------------------------------------------------------
function updateAllSelectInd(asBtn, gridId) {
    var selectedGridId = gridId;
    if (!selectedGridId || selectedGridId == null || !eval(selectedGridId)) {
        selectedGridId = getCurrentlySelectedGridId();
    }
    var XMLData = getXMLDataForGridName(selectedGridId);
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        switch (asBtn) {
            case 'SELECT':
                eval(selectedGridId + "_updatenode('CSELECT_IND', -1)");
                break;
            case 'DESELECT':
                eval(selectedGridId + "_updatenode('CSELECT_IND', 0)");
                break;
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}

function navigateRecords(direction) {
    if (!commonIsOkToChangePages()) {
        return;
    }

    var policyNo = "";
    var policyTermHistoryId = "";

    showProcessingDivPopup();

    if (direction == 'previous') {
        policyNo = navigatePrevPolicyNo;
        policyTermHistoryId = navigatePrevTermId;
    }
    else if (direction == 'next') {
        policyNo = navigateNextPolicyNo;
        policyTermHistoryId = navigateNextTermId;
    }

    if (policyNo != "" && policyTermHistoryId != "") {
        if (window.handleNavigateRecords) {
            handleNavigateRecords(policyNo, policyTermHistoryId);
        }
        else {
            setWindowLocation(getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + policyNo
                    + "&policyTermHistoryId=" + policyTermHistoryId);
        }
    }
}

function navigateTerms(direction) {
    if (!commonIsOkToChangePages()) {
        return;
    }

    var policyNo = getObject("policyNo").value;
    var policyTermHistoryId = "";
    var naviPrevTermId = "";
    var naviNextTermId = "";
    showProcessingDivPopup();
    if (isEmpty(policyNo)) {
        if (getObject("policyNo").length > 1) {
            policyNo = getObject("policyNo")[0].value;
        }
    }

    if (!isEmpty(getObject("availablePolicyTerms"))) {
        var obj = getObject("availablePolicyTerms");
        policyTermHistoryId = obj.value;
        if (obj.options != null) {
            var i;
            for (i = 0; i < obj.options.length; i++) {
                if (obj.options[i].value == policyTermHistoryId) {
                    if (i - 1 >= 0) {
                        naviNextTermId = obj.options[i - 1].value;
                    }
                    else {
                        enableDisableField(getObject('nextTerm'), true);
                    }
                    if (i + 1 < obj.options.length) {
                        naviPrevTermId = obj.options[i + 1].value;
                    }
                    else {
                        enableDisableField(getObject('previousTerm'), true);
                    }
                }
            }
        }
    }


    if (direction == 'previous') {
        policyTermHistoryId = naviPrevTermId;
        if (isEmpty(naviPrevTermId)) {
            var iHtml = getObject("navigateTerms").innerHTML;
            if(iHtml == null || iHtml.indexOf("No Prior Term")< 0){
               getObject("navigateTerms").innerHTML = "No Prior Term" + iHtml;
            }
            closeProcessingDivPopup();
        }
    }
    else if (direction == 'next') {
        policyTermHistoryId = naviNextTermId;
        if (isEmpty(naviNextTermId)) {
            var iHtml = getObject("navigateTerms").innerHTML;
            if(iHtml == null || iHtml.indexOf("No Future Term")< 0){
                getObject("navigateTerms").innerHTML = iHtml + " No Future Term";
            }
            closeProcessingDivPopup();
        }
    }

    if (!isEmpty(policyNo) && !isEmpty(policyTermHistoryId)) {
        if (window.handleNavigateRecords) {
            handleNavigateRecords(policyNo, policyTermHistoryId);
        }
        else {
            setWindowLocation(getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + policyNo
                    + "&policyTermHistoryId=" + policyTermHistoryId);
        }
    }
}

// call csworkflowDiary from PM.war, passing some policy specific parameters
function csworkflowDiaryFromPM() {
    var diaryUrl = getCSPath() + "/workflowdiary.do?"
        + commonGetMenuQueryString("PM_DIARY");
    diaryUrl += "&diary_sourceProvided=Y";
    diaryUrl += "&diary_sourceTableName=POLICY";
    diaryUrl += "&diary_claimsOnly=N";

    var policyId = policyHeader.policyId;
    var policyNo = policyHeader.policyNo;
    if (policyId != "") {
        diaryUrl += "&diary_sourceKey=" + policyId;
    }
    if (policyNo != "") {
        diaryUrl += "&diary_sourceNumber=" + policyNo;
    }
    // to use the same action class, but in order to know what jsp to forward,
    // let us add a request parameter to it. without this parameter, the action class
    // will forward to the regular page.
    diaryUrl += "&displayAsDivPopup=Y";
    diaryUrl += "&diary_sourceRecordFK=" + policyId;
    // diaryUrl +="&diary_submit=Y"; // enable this line if automatically query the results
    var divPopupId = openDivPopup("", diaryUrl, true, true, "", "", 1500, 900, "", "", "", true);
}

// open a Process File Page as divPopup from PM.war
function csProcessFileFromPM() {
    var procecessFileUrl = getCSPath() + "/csAttach.do?"
        + commonGetMenuQueryString("PM_PROCESS_FILE");

    var policyId = policyHeader.policyId;
    if (policyId != "") {
        procecessFileUrl += "&sourceTableName=POLICY&sourceRecordFk=" + policyId;
    }
    var divPopupId = openDivPopup("", procecessFileUrl, true, true, "", "", "850", "750", "840", "740", "", true);
}

// open a Form Letters Page as divPopup from PM.war
function csProcessFormLetterFromPM() {
    var procecessFormLetterUrl = getCSPath() + "/csFormLetter.do?sourceTableName=POLICY";
    + commonGetMenuQueryString("PM_PROCESS_FORM_LETTER");

    var policyId = policyHeader.policyId;
    if (policyId != "") {
        procecessFormLetterUrl += "&sourceRecordFk=" + policyId;
    }
    // add policyTermHistoryId
    procecessFormLetterUrl += "&policyTermHistoryPk=" + getObjectValue("policyTermHistoryId");

    if (hasObject("riskID")) {
        var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
        if (selectedDataGrid != null) {
            if (getCurrentlySelectedGridId() == "riskListGrid") {
                try { // xmldata might not have any rows at all
                    var riskId = selectedDataGrid.recordset('ID').value;
                    procecessFormLetterUrl += "&riskPk=" + riskId;
                }
                catch (ex) {
                    //
                }
            }
        }
    }

    var divPopupId = openDivPopup("", procecessFormLetterUrl, true, true, null, null, 820, 670, "", "", "formLetterFromPM",  false, null, null, null);
}

function reOpenFormLetterList() {
    csProcessFormLetterFromPM();
}

function pmStartImageRightDeskTop(sourceTable, sourceFieldId) {
    var sourceData = '';

    // First check if a selected grid exists - if it does look for the field
    // if not catch the exception, error will be raised later
    var selectedGridId = getCurrentlySelectedGridId();
    if (isDefined(selectedGridId) && !isEmpty(selectedGridId) && isFieldDefinedForGrid(selectedGridId, "C"+sourceFieldId.toUpperCase())) {
        var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
        sourceData = selectedDataGrid.recordset("C"+sourceFieldId.toUpperCase()).value;
        if (!isEmpty(sourceData) && sourceData.indexOf("javascript:selectRowWithProcessingDlg(") >= 0) {
            sourceData = selectedDataGrid.recordset("ID").value;
        }
    }
    // Next get the data element to be used
    // Check if the field is in the form
    if (isEmpty(sourceData) && hasObject(sourceFieldId)) {
        sourceData = getObjectValue(sourceFieldId);
    }

    if (isEmpty(sourceData)) {
        alert(getMessage("pm.common.fileNumber.determine.error"));
        return;
    }

    // Second take the source data and source table and get the IR file number and drawer
    var url = getCSPath() + "/imagerightmgr/maintainImageRight.do?" +
            commonGetMenuQueryString() +
            "&sourceData=" + sourceData +
            "&sourceTable=" + sourceTable;
    // initiate async call
    new AJAXRequest("get", url, '', handleStartImageRightDeskTop, false);
}

//get parameter value by name from url
//url:the url where parameter exists
//name:parameter name
function getUrlParam(url, name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = url.search.substr(1).match(reg);
    if (r != null)   return   unescape(r[2]);
    return   null;
}

function loadPageByViewMode(viewMode, targetId) {
    var policyNo = getObjectValue("policyNo");
    var termId = getObjectValue("availablePolicyTerms");
    var url = document.location.protocol + "//" + document.location.host + document.location.pathname + "?policyNo=" + policyNo + "&policyTermHistoryId=" + termId + "&policyViewMode=" + viewMode;
    if (viewMode == 'ENDQUOTE') {
        setWindowLocation(url + "&endQuoteId=" + targetId);
    }
    else if (viewMode == 'QUOTE') {
        setWindowLocation(document.location.protocol + "//" + document.location.host + document.location.pathname + "?policyNo=" + targetId);
    }
    else {
        setWindowLocation(url);
    }
}
function reRatePolicy() {
    postAjaxSubmit("/transactionmgr/rateTransaction.do", "reRatePolicy", false, false, handleOnReRatePolicy);
}
function handleOnReRatePolicy(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var policyNo;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, we're done
            else {
                /* Parse xml and get inital values(s) */
                var oValue = parseXML(data);

                /* Set default value which fieldID is match with object's attribute name */
                if (oValue.length > 0) {
                    if (!isEmpty(oValue[0]["POLICYNO"])) {
                        policyNo = oValue[0]["POLICYNO"];
                    }
                    else {
                        return;
                    }
                }
                window.refreshPage();
            }
        }
    }
}


function viewMultiCancelPage(cancelLevel) {

    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performMultiCancellation.do?"
        + commonGetMenuQueryString() + "&process=loadAllCancelableItem"
    if (cancelLevel) {
        url = url + "&cancellationLevel=" + cancelLevel;
    }
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 700, "", "", "", false);
}

function viewLockedPolicy() {
    var url = getAppPath() + "/policymgr/lockmgr/maintainUnlockPolicy.do?process=loadAllLockedPolicy&noLoadData=Y"
        + "&date=" + new Date();
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 630, 890, 600, "", false);
}

function unLockPreviouslyHeldLock() {
    // Invoke Ajax call to refresh the Policy Lock
    var url = getAppPath() + "/policymgr/lockmgr/maintainLock.do" + "?process=unlockPolicy" + "&date=" + new Date();
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, true);
}

//-----------------------------------------------------------------------------
// This function is to refresh the token value in current page.
// It invokes ajax call to get current token value from session and update hidden
// field(org.apache.struts.taglib.html.TOKEN) in the page.
//-----------------------------------------------------------------------------
function refreshStrutsToken() {
    var oToken = getObject("org.apache.struts.taglib.html.TOKEN");
    if (oToken) {
        // make ajax call to retrieve current token value in the session
        var url = getAppPath() + "/core/maintainStrutsToken.do?process=getCurrentToken&date=" + new Date();
        new AJAXRequest("get", url, '', refreshStrutsTokenDone, false);
    }
}

//-----------------------------------------------------------------------------
// Update token field's value in the page by the value returns from XMLRequest
//-----------------------------------------------------------------------------
function refreshStrutsTokenDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var oToken = getObject("org.apache.struts.taglib.html.TOKEN");
                if (oToken) {
                    logDebug("Token value is freshed:old token:"+oToken.value+";new token:"+oValueList[0].TOKENVALUE);
                    oToken.value = oValueList[0].TOKENVALUE;
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Open the policy in new popup page (not div popup)
//-----------------------------------------------------------------------------
function openPolicyInNewPage(policyNo) {
    var url = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + policyNo;
    window.open(url, "", "location=yes,menubar=yes,toolbar=yes,scrollbars=yes,directories=no,resizable=yes,opyhistory=no");
}

//-----------------------------------------------------------------------------
// Open part time notes div popup
//-----------------------------------------------------------------------------
function openPartTimeNotes() {
    var url = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?date=" + new Date();
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 630, 890, 600, "", false);
}

//-----------------------------------------------------------------------------
// Generate policy report(PDF Report) by default.
//-----------------------------------------------------------------------------
function viewPolicyReport(parmsObj) {
    viewPolicyReport(parmsObj, "PDF");
}

function viewPolicyReport(parmsObj, type) {
    var url = getAppPath() + "/policyreportmgr/maintainPolicyReport.do?" + commonGetMenuQueryString();
    var pageName = pageTitle+'('+pageCode+')';
    pageName = encodeURIComponent(pageName);

    if (type=="CSV") {
        url = url + "&process=generatePolicyCsvReport";
    }
    else  if (type=="XLS") {
        url = url + "&process=generatePolicyXLSReport&exportType=XLS&pageName="+pageName;
    }
    else  if (type=="XLSX"){
        url = url + "&process=generatePolicyXLSReport&exportType=XLSX&pageName="+pageName;
    }
    else if (type=="XML") {
        url = url + "&process=generatePolicyXmlReport";
    }
    else {
       url = url + "&process=generatePolicyReport";
    }

    // Generate the url from the parmsObj.
    if (parmsObj != null) {
        var str = "";
        for (var parm in parmsObj) {
            str += "&" + parm + "=" + parmsObj[parm];
        }
        url = url + str;
    }
    window.open(url, 'POLICY_REPORT', 'resizable=yes,width=800,height=600');

}
//-----------------------------------------------------------------------------
// Open quick quote
//-----------------------------------------------------------------------------
function loadQuickQuote() {
    // Check if unsaved data exists
    var functionExists = eval("window.commonIsOkToChangePages");
    if (functionExists) {
        var isOkToProceed = commonIsOkToChangePages();
        if (!isOkToProceed) {
            return;
        }
    }
    var url = getAppPath() + "/policymgr/quickquotemgr/processQuickQuote.do?" + commonGetMenuQueryString() + "&process=display";
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 630, 890, 600, "", false);
}

//-----------------------------------------------------------------------------
// Open view cancellation detail page
//-----------------------------------------------------------------------------
function loadCancelDetail() {
    var url = getAppPath() + "/transactionmgr/cancelprocessmgr/viewTransactionSnapshot.do?" + commonGetMenuQueryString();
    var divPopupId = openDivPopup("", url, true, true, null, null, 980, 830, "", 1400, "", false);
}



//-----------------------------------------------------------------------------
// Set row style to a newly-added row.
// table: <table> html object
// row: <tr> html object, the newly-added <tr> row
// ***NOTE*** When calling this function, the XML data pointer must point to
//            the newly-added row.
//-----------------------------------------------------------------------------
function setRowStyleForNewRow(table, row) {
    var XMLData = getXMLDataForTable(table);
    var rowStyleColumnName = "C" + table.id.toUpperCase() + "ROWSTYLE";
    if (isFieldExistsInRecordset(XMLData.recordset, rowStyleColumnName)) {
        var rowStyle = XMLData.recordset.Fields(rowStyleColumnName).value;
        if (!isEmpty(rowStyle)) {
            setRowStyleForOneRow(row, rowStyle);
        }
    }
}

function commonOnLogOut() {
    if (hasObject("policyNo")) {
        var policyId = policyHeader.policyId;
        var wipB;
        if (hasObject("wipB")) {
            wipB = getObjectValue("wipB");
        }
        postAjaxSubmit("/policymgr/lockmgr/maintainLock.do?wipB=" + wipB, "unlockPolicy", false, false, commonHandleOnPostAjaxLogOutDone, false, false);
    }
}

function commonHandleOnPostAjaxLogOutDone() {
}
//-----------------------------------------------------------------------------
// Set the linked field to read only.
// Loop the cells in current row to find the cell that used to display this field.
// Copy the child span tag to be a peer of the A anchor tag and name it read only span.
// Set the A invisible and read only span tag visible.
//-----------------------------------------------------------------------------
function setLinkAsReadyOnly(table, rowId, fieldName) {
    var urlFieldName = "URL_" + fieldName.toUpperCase();
    var readOnlySpanId = fieldName.toUpperCase() + "_readOnly";
    var rowCells = getCellsInRow(table.rows[rowId]);
    for (var i = 0; i < rowCells.length; i++) {
        var currentCell =rowCells[i];
        if (currentCell.childNodes.length > 0) {
            var tagANode = currentCell.childNodes[0];
            var tagNodeId = tagANode.id;
            var tagNodeName = tagANode.tagName;
            if (!isEmpty(tagNodeName) && tagNodeName == "A" && urlFieldName == tagNodeId) {
                // Hide the tag A.
                var existASpan = tagANode.childNodes[0];
                if (currentCell.childNodes.length > 1) {
                    var readOnlySpan = currentCell.childNodes[1];
                    if (readOnlySpan.id == readOnlySpanId) {
                        // The existASpan's style maybe updated, so we should make the two spans consistent.
                        if (!isEmpty(existASpan.style.cssText)) {
                            readOnlySpan.style.cssText = existASpan.style.cssText;
                        }
                        hideShowElementByClassName(readOnlySpan, false);
                        hideShowElementByClassName(tagANode, true);
                    }
                }
                else {
                    // Clone the tag span, update its id to readOnlySpanId and make it visible.
                    var readOnlySpan = existASpan.cloneNode(true);
                    readOnlySpan.id = readOnlySpanId;
                    hideShowElementByClassName(readOnlySpan, false);
                    currentCell.appendChild(readOnlySpan);
                    hideShowElementByClassName(tagANode, true);
                }
                return;
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Reset the linked field.
// Loop the cells in current row to find the cell that used to display this field.
// Set the A visible and read only span tag invisible.
//-----------------------------------------------------------------------------
function resetReadOnlyLink(table, rowId, fieldName) {
    var urlFieldName = "URL_" + fieldName.toUpperCase();
    var readOnlySpanId = fieldName.toUpperCase() + "_readOnly";
    var rowCells = getCellsInRow(table.rows[rowId]);
    for (var i = 0; i < rowCells.length; i++) {
        var currentCell = rowCells[i];
        if (currentCell.childNodes.length > 1) {
            var tagANode = currentCell.childNodes[0];
            var spanNode = currentCell.childNodes[1];
            var tagNodeId = tagANode.id;
            var tagNodeName = tagANode.tagName;
            var spanNodeId = spanNode.id;
            if (!isEmpty(tagNodeName) && tagNodeName == "A" && urlFieldName == tagNodeId && readOnlySpanId == spanNodeId) {
                hideShowElementByClassName(spanNode, true);
                hideShowElementByClassName(tagANode, false);
                return;
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Handle the Renew indicator when change Expiration Date.
//-----------------------------------------------------------------------------
function enableDisableRenewIndicator(effectiveToDate, expirationDate, indicator, indFieldName, currentGridId) {
    indFieldName = "C" + indFieldName.toUpperCase();
    indicator = "C" + indicator.toUpperCase();
    var indicatorLOVLABEL = indicator + "LOVLABEL";
    if (isValueDate(effectiveToDate) && isValueDate(expirationDate)) {
        var xmlData = getXMLDataForGridName(currentGridId);
        if (getRealDate(effectiveToDate) < getRealDate(expirationDate)) {
            xmlData.recordset(indicator).value = "N";
            xmlData.recordset(indFieldName).value = "N";
            if (xmlData.recordset(indicatorLOVLABEL)) {
                xmlData.recordset(indicatorLOVLABEL).value = "No";
            }
        }
        else {
            xmlData.recordset(indicator).value = "Y";
            xmlData.recordset(indFieldName).value = "Y";
            if (xmlData.recordset(indicatorLOVLABEL)) {
                xmlData.recordset(indicatorLOVLABEL).value = "Yes";
            }
        }
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, currentGridId);
        }
    }
}

//-----------------------------------------------------------------------------
// Handle page level Renew indicator when change Expiration Date.
//-----------------------------------------------------------------------------
function enableDisableRenewIndicatorWithoutGrid(effectiveToDate, expirationDate, indicator) {
    if (isValueDate(effectiveToDate) && isValueDate(expirationDate)) {
        var renewalB = getObject(indicator);
        var lovLableSpan = indicator + "LOVLABELSPAN";
        if (getRealDate(effectiveToDate) < getRealDate(expirationDate)) {
            renewalB.value = 'N';
            enableDisableField(renewalB, true);
        } else {
            renewalB.value = 'Y';
            enableDisableField(renewalB, false);
        }
        if (hasObjectId(lovLableSpan)){
            var renewalBLOVLABELSPAN = getObjectById(lovLableSpan);
            renewalBLOVLABELSPAN.innerHTML = renewalB.options[renewalB.selectedIndex].text;
        }
    }
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
// Handle the refresh page for Undo Term and Purge.
// Since the term, endQuoteId specified in URL could be invalid, remove them from parameters.
//-----------------------------------------------------------------------------
function refreshWithNewPolicyTermHistory(calledFrom) {
    var url = location.href;
    var removeParam1 = "policyTermHistoryId";
    var removeParam2 = "endQuoteId";

    var regex1 = new RegExp("&?" + removeParam1 + "=[^&]+");
    var regex2 = new RegExp("&?" + removeParam2 + "=[^&]+");
    var newUrl = url.replace(regex1, '');
    if (calledFrom == "UNDOTERM") {
        newUrl = newUrl.replace(regex2, '');
    }
    setWindowLocation(newUrl);
}

//-----------------------------------------------------------------------------
// Handle the Claims Summary display when clicking Policy Action Claims Summary.
//-----------------------------------------------------------------------------
function viewClaimsSummary() {
    var riskId = "";
    var entityId = "";
    var policyNo = policyHeader.policyNo;
    if (hasXMLDataForGridName('riskListGrid')) {
        riskId = riskListGrid1.recordset("ID").value;
        entityId = riskListGrid1.recordset("CENTITYID").value;
    } else if (policyHeader.riskHeader) {
        riskId = policyHeader.riskHeader.riskId;
    }

    var url = getAppPath() + "/riskmgr/viewClaimsSummary.do?process=getPrimaryRisk"
                           + "&policyNo=" + policyHeader.policyNo
                           + "&riskId=" + riskId
                           + "&entityId=" + entityId
                           + "&date=" + new Date();
    new AJAXRequest("get", url, '', handleOnGetPrimaryRisk, false);
}

function handleOnGetPrimaryRisk(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }

            var riskId = "";
            var entityId = "";
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                riskId = oValueList[0]["riskId"];
                entityId = oValueList[0]["entityId"];
            }
            if (isEmpty(riskId) || parseInt(riskId) <= 0 ||
                isEmpty(entityId) || parseInt(entityId) <= 0 ) {
                alert(getMessage("pm.viewClaimsSummary.risk.select.error"));
                return;
            }
            var url = getAppPath() + "/riskmgr/viewClaimsSummary.do?process=loadAllClaims"
                                   + "&entityId=" + entityId
                                   + "&date=" + new Date();
            var divPopupId = openDivPopup("", url, true, true, "", "", 950, 600, "", "", "", false);
        }
    }
}

function maintainIbnrRisk() {
    var maintainIbnrRiskUrl = getAppPath() + "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?"
                              + commonGetMenuQueryString() + "&process=loadAllIbnrRisk";
    var divPopupId = openDivPopup("", maintainIbnrRiskUrl, true, true, "", "", 910, 950, "", "", "", false);
} 

//-----------------------------------------------------------------------------
// Open process acf page
//-----------------------------------------------------------------------------
function processAcf() {
    var acfUrl = getAppPath() + "/policymgr/processacfmgr/maintainAcf.do?"
            + commonGetMenuQueryString() + "&process=loadAllAcf";
    acfUrl = acfUrl + "&transId=" + policyHeader.lastTransactionId + "&policyType=" + policyHeader.policyTypeCode +
            "&termEff=" + policyHeader.termEffectiveFromDate;
    var divPopupId = openDivPopup("", acfUrl, true, true, "", "", 960, 600, "", "", "", false);
}

function setNegativeRed(table) {
    var reQMark = eval("/\\(\\" + currency_symbol + "/");
    for (var i = 1; i < table.rows.length; i++) {
        var rowCells = getCellsInRow(table.rows[i]);
        for (var j = 3; j < rowCells.length; j++) {
            var text = trim(rowCells[j].innerText);
            if (reQMark.test(text)) {
                rowCells[j].childNodes[0].style.color = "red";
            }
            else {
                rowCells[j].childNodes[0].style.color = "black";
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Open Predictive Analytics
//-----------------------------------------------------------------------------
function processOpa() {
    var url = getAppPath() + "/policymgr/analyticsmgr/processOpa.do?" + commonGetMenuQueryString();
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 800, "", "", "", false);
}

function opaErrors(policyId, scoreReqTypeCode, scoreRequestId) {
    var url = getAppPath() + "/policymgr/analyticsmgr/opaErrors.do?policyId=" + policyId
        + "&searchCriteria_scoreReqTypeCode=" + scoreReqTypeCode
        + "&searchCriteria_scoreReqId=" + scoreRequestId;
    var divPopupId = openDivPopup("", url, true, true, null, null, 900, 650, "", "", "", true);
}

//-----------------------------------------------------------------------------
// Remove the value from the array if it exists.
//-----------------------------------------------------------------------------
function removeValueFromArray(array, value) {
    var newArray = new Array();
    var size = array.length;
    var k = 0;
    for (var i = 0; i < size; i++) {
        if (array[i] != value) {
            newArray[k++] = array[i];
        }
    }
    return newArray;
}

//-----------------------------------------------------------------------------
// Check whether the array contains the value.
//-----------------------------------------------------------------------------
function isArrayContains(array, value) {
    var arraySize = array.length;
    for (var i = 0; i < arraySize; i++) {
        if (array[i] == value) {
            return true;
        }
    }
    return false;
}

function loadPolicyNotesCommon() {
    if (window.loadNotes) {
        var policyId = getObjectValue("policyId");
        loadNotes(policyId, "POLICY", "POLICY", true);
    }
    else {
        alert(getMessage("pm.common.notes.functionality.notAvailable.error"));
    }
}

//-----------------------------------------------------------------------------
// File Activity Notes
//-----------------------------------------------------------------------------
function maintainFileActivityNotesFromPM() {
    var notesUrl = getCSPath() + "/fileactivitynotesmgr/maintainFileNotes.do?";
    notesUrl += "search_entityId=" + getObjectValue("policyHolderNameEntityId");
    notesUrl += "&search_subsystemCode=PMS";
    notesUrl += "&search_externalId=" + getObjectValue("policyNo");
    var divPopupId = openDivPopup("", notesUrl, true, true, "", "", "830", "600", "820", "590", "", true);
}

//-----------------------------------------------------------------------------
// Show or hide the column in grid.
// The first parameter columnName should be the uppercase column name in data island, for example:CRISKDESCRIPTION.
// The second parameter is a boolean value to indicate show/hide the specified column.
//-----------------------------------------------------------------------------
function showHideTableColumn(columnName, show) {
    // Handle all the td elements
    if (getObject(columnName)) {
        var tdArray = document.getElementsByName(columnName);
        for (var i = 0; i < tdArray.length; i++) {
            if (show){
                hideShowElementAsInlineByClassName(tdArray[i].parentElement, false);
            }
            else {
                hideShowElementByClassName(tdArray[i].parentElement, true);
            }
        }
    }
    // Handle the th element
    var hColumnName = "H" + columnName;
    if (getObject(hColumnName)) {
        if (show){
            hideShowElementAsInlineByClassName(getObject(hColumnName), false);
        }
        else {
            hideShowElementByClassName(getObject(hColumnName), true);
        }
    }
}

//-----------------------------------------------------------------------------
// Pop up warning message to page
//-----------------------------------------------------------------------------
function handleOnGetWarningMsg(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var msg = oValueList[0]["WARNING"];
                if (msg != '') {
                    alert(msg);
                }
                if(hasObject("needToForwardToQuote") && getObjectValue("needToForwardToQuote")=="Y"){
                    return;
                }
                if (!hasObject('needToForwardToEndQuote') || getObjectValue('needToForwardToEndQuote') != 'Y') {
                    refreshPage();
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// use "or" as the joint condition to do multi select.
//-----------------------------------------------------------------------------
function addMultiFilterConditions(filterStr, filterField, multiSelFld) {

    var tmpFilterStr = "";
    if (multiSelFld) {
        for (var i = 0; i < multiSelFld.options.length; i++) {
            if (multiSelFld.options[i].selected) {
                if (!isEmpty(tmpFilterStr)) {
                    tmpFilterStr = tmpFilterStr + " or ";
                }
                tmpFilterStr = tmpFilterStr + addFilterCondition("", filterField, "=", multiSelFld.options[i].value);
            }
        }
        if (!isEmpty(tmpFilterStr)) {
            if (isEmpty(filterStr)) {
                filterStr = tmpFilterStr;
            }
            else {
                filterStr = filterStr + " and (" + tmpFilterStr + ")";
            }
        }
    }

    return filterStr
}

function invokeODS(workflowInstanceId) {
    if (typeof(policyHeader) != 'undefined') {
        if (policyHeader.isInvokeODS) {

            var transLogId = policyHeader.lastTransactionId;
            var termEff = policyHeader.termEffectiveFromDate;
            var termExp = policyHeader.termEffectiveToDate;
            var url = getCSPath() + "/outputmgr/processForms.do?transLogId=" + transLogId +
                    "&termEff=" + termEff +
                    "&termExp=" + termExp;

            //policyHeader.isInvokeODS = false;
            new AJAXRequest("get", url, "", "", true);

        }
    }
}

function invokePreview() {
    if (typeof(policyHeader) != 'undefined') {
        if (policyHeader.isPreviewRequest) {
            var url = getCSPath() + "/outputmgr/processForms.do?process=preview&transLogId=" + policyHeader.lastTransactionInfo.transactionLogId;
            url += "&termEff=" + policyHeader.termEffectiveFromDate;
            url += "&termExp=" + policyHeader.termEffectiveToDate;

            openDivPopup('PREVIEW', url, true, true, "", "", "", "", "", "", "", true);

            policyHeader.isPreviewRequest = false;
        }
    }
}

//-----------------------------------------------------------------------------
// Policy Attribute object to wrap methods of getting data.
//-----------------------------------------------------------------------------
var policyAttributeObject = new PolicyAttribute();
function PolicyAttribute() {

    var contains = function (config, parm){
        return (parm != null) && (config == null || (","+config+",").indexOf(","+parm+",") >= 0);
    };

    this.items = {};

    this.put = function (typeCode, jsonArray) {
        this.items[typeCode] = jsonArray;
    };

    this.isDisplayCommentWindow = function (effectiveDate, transactionCode, quoteCycleCode, eventHandler) {
        var jsonArray = this.items['PM_DISP_COMMENT_WIND'];
        var jsonObject = jQuery.grep(jsonArray, function(e){
            return getRealDate(e.effectiveFromDate) <= getRealDate(effectiveDate)
                    && getRealDate(e.effectiveToDate) > getRealDate(effectiveDate)
                    && contains(e.value1, transactionCode)
                    && contains(e.value2, quoteCycleCode)
                    && contains(e.value3, eventHandler);
        });
        return jsonObject.length > 0;
    };
}

function syncResultToParent(result) {
    if(getUIStyle() == "T") { // only tab style need to sync to parent page
        if(eval("window.setAutoSavedTabResultType")) {
            setAutoSavedTabResultType(result);
        }else if(eval("getParentWindow(true).setAutoSavedTabResultType")) {
            getParentWindow(true).setAutoSavedTabResultType(result);
        }
    }
}

/**
 * return the UI style value defined in SYSTEM_PARAMETER_UTIL table for epolicy page showing
 * @returns {*}
 */
function getUIStyle() {
    return getSysParmValue("PM_UI_STYLE");
}

function isTabStyle() {
    return getUIStyle() == "T";
}

function isButtonStyle() {
    return getUIStyle() == "B";
}

var parentWindowFlagReturnTypes = {
    ParentWindow : "ParentWindow",
    iFrameWindow : "iFrameWindow"
};

// return the parent window of div popup
function getOpenCtxOfDivPopUp() {
    //Issue 191057: if tab style and current page is window style, return current window.
    //Issue 192537: Add additional condition to deal with the case satisfied with below condition:
    //              1) The current window is the top window.
    //              2) Exist the opener window for the current window
    return (getUIStyle() == "T" && !isWindowStyle() && !((window == window.top) && window.opener)) ?
           getParentWindow(true) : window;
}

// return the target window invoked in popup
function getReturnCtxOfDivPopUp(popupDivId) {
    // Issue 192537: if the current window is the top window and exists the opener window,
    //               then return the current window directly.
    if (getUIStyle() == "T" && window == window.top && window.opener && !isInWindowPopup() && !isDefined(popupDivId)) {
        return window;
    }
    if(isDefined(popupDivId)){
        var functionExists = eval("getParentWindow(true, false, true).getParentWindowOfDivPopup");
        if(functionExists){
            var parentWindowFlag = getParentWindow(true, false, true).getParentWindowOfDivPopup(popupDivId);
            if(parentWindowFlag == parentWindowFlagReturnTypes.ParentWindow)
                return getParentWindow(true, false, true);
            if(parentWindowFlag == parentWindowFlagReturnTypes.iFrameWindow)
                return getParentWindow(true, false, true).getIFrameWindow();
        }
    }
    return (getUIStyle() == "T") ? (eval("getParentWindow(true, false, true).getIFrameWindow") ?
                                     getParentWindow(true, false, true).getIFrameWindow() : getParentWindow(true, false, true))
                                      : getParentWindow(true, false, true);
}

// Issue 191057: check current page is iframe style or window style.
function isWindowStyle() {
    if (window.frameElement) {
        return !isEmpty(window.frameElement.name);
    } else {
        return false;
    }
}

function commonOnPutParentWindowOfDivPopup(popupDivId){
    if (isTabStyle()) {
        var oParentWindowFlag = typeof subFrameId != 'undefined' ?
                parentWindowFlagReturnTypes.ParentWindow : (isWindowStyle() ? parentWindowFlagReturnTypes.ParentWindow
                : parentWindowFlagReturnTypes.iFrameWindow);
        functionExists = eval("getOpenCtxOfDivPopUp().handleOnPutParentWindowOfDivPopup");
        if(functionExists){
            getOpenCtxOfDivPopUp().handleOnPutParentWindowOfDivPopup(popupDivId, oParentWindowFlag);
        }
    }
}

function isExeInvokeWorkFlow() {
    var returnFlag = false;
    if (isButtonStyle() || (isTabStyle() && divPopupExists())) {
        returnFlag = true;
    }
    return returnFlag;
}

function divPopupExists(){
    var divPopup = getParentWindow(true).getDivPopupFromDivPopupControl(this.frameElement);
    if(divPopup){
        return true;
    }else{
        return false;
    }
}

function hideButtonsAccordingToUIStyle(){
    if(isTabStyle()){
        // if this is main page, then hide some unused buttons according to UI Style for this row
        var functionExists = eval("window.commonOnHideMigratedButtonsInMainPage");
        if (functionExists) {
            commonOnHideMigratedButtonsInMainPage(subFrameId);
        }else{
            try{
                // if this is sub-tab screen, then hide some unused buttons according to UI Style for this row
                functionExists = eval("getParentWindow(true).hideButtonsInSubTab");
                if (functionExists) {
                    if(!divPopupExists()){
                        getParentWindow(true).hideButtonsInSubTab();
                    }
                }
            }catch(e){
                //Do nothing
            }
        }
    }
}

/**
 *  used before sub tab submit
 */
function processOperationBeforeSubTabSubmit() {
    if(isTabStyle() && (typeof secondlyTabDivId == 'undefined')){
        if(isDefined(getParentWindow(true).operation)){
            if(getParentWindow(true).operation == "switchPrimaryTab") {
                getParentWindow(true).switchPrimaryTabFlg = true;
            }else if(getParentWindow(true).operation == "switchSecondlyTab"){
                getParentWindow(true).switchSecondlyTabFlg = true;
            }else if(getParentWindow(true).operation == "switchGridRow"){
                getParentWindow(true).switchGridRowFlg = true;
            }
        }
    }
}

/**
 *  used when cancel auto-save sub-tab
 */
function processAfterCancelAutoSaveSubTab() {
    if(isTabStyle() && (typeof secondlyTabDivId == 'undefined')){
        if(isDefined(getParentWindow(true).rollback) && eval("getParentWindow(true).selectRowById")
                && eval("getParentWindow(true).getPreviousRow") && eval("getParentWindow(true).setCacheTabIds")
                && eval("getParentWindow(true).getPreviousTab")){
            if(getParentWindow(true).operation == "switchSecondlyTab"){
                //common validation failed on the Grid/Form of sub-Tab
                //No actions.
                getParentWindow(true).setCacheTabIds(getParentWindow(true).getPreviousTab() + "," + getParentWindow(true).getPreviousTab());
            }else if(getParentWindow(true).operation == "switchGridRow"){
                getParentWindow(true).rollback=true;
                getParentWindow(true).selectRowById("riskListGrid", getParentWindow(true).getPreviousRow());
            }
            if(eval("getParentWindow(true).mainPageLock")) {
                getParentWindow(true).mainPageLock.unlock();
            }
        }
    }
}

/**
 *  This method is invoked to load isFundState field value.
 */
function loadIsFundStateValue(practiceStateCode) {
    var url = getAppPath() + "/riskmgr/maintainRisk.do?process=loadIsFundStateValue" +
            '&' + '&practiceStateCode=' + practiceStateCode +
            '&' + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate");
    new AJAXRequest("get", url, '', handleOnloadIsFundStateValue, false);
}

/**
 *  This method is invoked to set pcf risk county field value.
 */
function setDefaultValueForPcfRiskCounty(riskCounty, isFromRiskRelationPage) {
    var practiceStateCode = getObjectValue("practiceStateCode");
    var riskTypeCode = getObjectValue("riskTypeCode");
    var url = getAppPath() + "/riskmgr/maintainRisk.do?process=getDefaultValueForPcfCounty" +
            '&' + '&riskCounty=' + riskCounty +
            '&' + '&practiceStateCode=' + practiceStateCode +
            '&' + '&riskTypeCode=' + riskTypeCode +
            '&' + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate");
    if (isFromRiskRelationPage) {
        url += '&&isFromRiskRelationPage=Y'
    }
    new AJAXRequest("get", url, '', handleOnGetDefaultValueForPcfCountyAndClass, false);
}

/**
 *  This method is invoked to set pcf risk class field value.
 */
function setDefaultValueForPcfRiskClass(riskClass, isFromRiskRelationPage) {
    var practiceStateCode = getObjectValue("practiceStateCode");
    var riskTypeCode = getObjectValue("riskTypeCode");
    var url = getAppPath() + "/riskmgr/maintainRisk.do?process=getDefaultValueForPcfRiskClass" +
            '&' + '&riskClass=' + riskClass +
            '&' + '&practiceStateCode=' + practiceStateCode +
            '&' + '&riskTypeCode=' + riskTypeCode +
            '&' + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate");
    if (isFromRiskRelationPage) {
        url += '&&isFromRiskRelationPage=Y'
    }
    new AJAXRequest("get", url, '', handleOnGetDefaultValueForPcfCountyAndClass, false);
}

/**
 *  Process set return value to isFundState field.
 */
function handleOnloadIsFundStateValue(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var result = parseXML(data)[0]["RETURNVALUE"];
            setObjectValue("isFundState", result);
            dispatchElementEvent(getObject("isFundState"),"change");
        }
    }
}

/**
 *  Process set return value to pcfRiskCounty/Class field.
 */
function handleOnGetDefaultValueForPcfCountyAndClass(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var result = parseXML(data)[0];
            var fieldName;
            var fieldValue;
            var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
            for (var prop in result) {
                if (prop.toUpperCase().indexOf("PCF") != -1) {
                    fieldName = prop;
                    fieldValue = result[prop];
                    break;
                }
            }
            if (selectedDataGrid != null && selectedDataGrid != '1') {
                if (isFieldExistsInRecordset(selectedDataGrid.recordset, "C" + fieldName)) {
                    selectedDataGrid.recordset("C" + fieldName.toUpperCase()).value = fieldValue;
                }
            }
            else {
                var ajaxInfoField;
                var fld;
                if (fieldName.indexOf("COUNTY") > -1) {
                    ajaxInfoField = eval("ajaxInfoForriskCounty");
                    fld = getObject("riskCounty");
                } else {
                    ajaxInfoField = eval("ajaxInfoForriskClass");
                    fld = getObject("riskClass");
                }
                //Call ajax to load field lov.
                if (ajaxInfoField != null) {
                    fireAjax(ajaxInfoField, null, fld);
                }
                //Check if the mapped field value exits in lov.
                for (var i = 0; i < getObject(fieldName).options.length; i++) {
                    if (getObject(fieldName).options[i].value == fieldValue) {
                        setObjectValue(fieldName, fieldValue, true);
                    }
                }
            }
        }
    }
}
/**
 *  parent must be saved before change child page
 */
function commonSaveRequiredToChangePages(parentPageName, childPageName, isCheckInsertRec, updateInd) {
    var parms = new Array(parentPageName, childPageName);
    if (isCheckInsertRec == "Y") {
        if (updateInd == "I") {
            handleError(getMessage("pm.common.unsaved.changes.error", parms));
            return true;
        }
    }else{
        if (isChanged || isPageGridsDataChanged()) {
            handleError(getMessage("pm.common.unsaved.changes.error", parms));
            return true;
        }
    }
    return false;
}

function commonCheckIsJobBasedOutput() {
    var checkResult = false;
    var url = getCSPath() + "/outputmgr/processOutput.do?process=checkIsJobBasedOutput";
    url += "&transactionLogId=" + policyHeader.lastTransactionInfo.transactionLogId;
    new AJAXRequest("get", url, "", function(ajax) {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var result = ajax.responseText;
                if ('Y' == result) {
                    checkResult = true;
                }
            }
        }
    }, false);
    return checkResult;
}

function clearOperationForTabStyle(){
    if (eval("window.handleOnClearOperation")) {
        handleOnClearOperation();
    }
}

function commonOnRemoveMessages(){
    if(isTabStyle() && eval("getParentWindow(true).handleOnRemoveMessages")){
        getParentWindow(true).handleOnRemoveMessages();
    }
}

function viewPolicySummaryReport() {
    if (isUndefined(policyHeader.riskHeader)) {
        alert("No Policy Sunmmary Data. Please add a risk first");
        return;
    }
    var policyId = policyHeader.policyId;
    var policyNo = policyHeader.policyNo;
    var termEffDate = policyHeader.termEffectiveFromDate;
    var termExpDate = policyHeader.termEffectiveToDate;
    var termBaseRecordId = policyHeader.termBaseRecordId;
    var transactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
    var recordModeCode = policyHeader.recordModeCode;
    var endorsementQuoteId = policyHeader.lastTransactionInfo.endorsementQuoteId;
    var paramsObj = new Object();
    paramsObj.reportCode = "PM_VIEW_POLICY_SUMMARY_WORKSHEET";
    paramsObj.policyId = policyId;
    paramsObj.policyno = policyNo;
    paramsObj.termEffDate = termEffDate;
    paramsObj.termExpDate = termExpDate;
    paramsObj.termBaseRecordId = termBaseRecordId;
    paramsObj.transactionLogId = transactionLogId;
    paramsObj.endorsementQuoteId = endorsementQuoteId;
    paramsObj.recordModeCode = recordModeCode;
    viewPolicyReport(paramsObj);
}

$(document).keyup(function(event){
    switch(event.keyCode) {
        case 8:
            //handle when 'Backspace' on keyboard is pressed by user
            if(isTabStyle()){
                if(typeof invokedByBackspaceB != 'undefined'){
                    //handle the keyboard event occurs on Main tab
                    invokedByBackspaceB = true;
                }else if(typeof getParentWindow(true).invokedByBackspaceB != 'undefined'){
                    //handle the keyboard event occurs on Sub tab
                    getParentWindow(true).invokedByBackspaceB = true;
                }
            }
            break;
    }
});
