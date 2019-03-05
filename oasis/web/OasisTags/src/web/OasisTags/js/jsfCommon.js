//-----------------------------------------------------------------------------
// Common ConfigProps javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   June 22, 2012
// Time: 2:47 PM
// Author: mgitelman
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
var isChanged = false;

function testCommon(){
    alert('ConfigProp Test');
}

function nvl(value, defaultValue) {
    if (null == value || "undefined" == typeof(value)) {
        return defaultValue;
    }
    else {
        return value;
    }
}

function baseOnPageLoad() {
    initializeSessionTimeout();
}

function baseOnPageLoadPopup() {
    resetSessionTimeoutObject();
}

//logout
function commonOnLogOut() {
    var isLogOutEventSuccess = false;

    $("#processingDialog").dialog("open");

    var functionExists = eval("window.handleOnLogOut");
    if (functionExists) {
        window.event.returnValue = handleOnLogOut();
        isLogOutEventSuccess = nvl(window.event.returnValue, true);
    }
    var targetPath = getAppPath() + '/core/logout.jsp';

    document.location = targetPath;
}

function baseOnChange() {
    if (window.commonOnChange) {
        window.commonOnChange();
    }
    if (window.handleOnChange) {
        window.handleOnChange();
    }
}

function commonOnChange() {
    isChanged = true;
}

function isDataChanged(){
/*
    if(isChanged)
        alert('Data has been changed');
    else
        alert('Data has not been changed');
*/
    return isChanged;
}

//change password
function openWebApplication(url, isOpenInNewWindow) {
    if (!isOpenInNewWindow) {
        var functionExists = eval("window.commonIsOkToChangePages");
        if (functionExists) {
            isOkToProceed = commonIsOkToChangePages(null, url);
            if (!isOkToProceed) {
                return;
            }
        }
        if (window.isOkToChangePages)
            if (!isOkToChangePages(null, url))
                return;
    }

    if (url.indexOf("('~/") > 0) {
        url = url.replace("('~/", "('" + getCorePath() + "/");
    }
    if (url.indexOf('~envPath/') != -1) {
        url = getEnvPath() + url.substr(url.indexOf('~envPath/') + 8);
    }
    if (isOpenInNewWindow) {
        window.open(url);
    }
    else {
        $("#processingDialog").dialog("open");
        document.location.href = url;
    }
}

// Note: popupId is required. It must be unique on page. No space character in popupId.
function openDialog(popupId, popupTitle, urlToOpen, isModal, popupWidth, popupHeight) {

    var popupDiv = document.getElementById(popupId);

    if (popupDiv != null) {
        $(popupDiv).empty();
    } else {
        popupDiv = document.createElement("div");
        popupDiv.id = popupId;
        var thisbody = document.getElementsByTagName("body")[0];
        thisbody.appendChild(popupDiv);
    }
    var iframeId = popupId + "iframeId";
    var iframeName = popupId + "iframeName";

    var iframeHTML = "<iframe id=\"" + iframeId + "\" width=\"100%\" height=\"100%\" " +
        " marginWidth=\"0\" marginHeight=\"0\" frameBorder=\"0\" scrolling=\"auto\" />";

    $(popupDiv).html(iframeHTML).dialog(
        {
            title:popupTitle,
            modal:isModal,
            height:popupHeight,
            width:popupWidth,
            beforeClose:function( event, ui ){
                var allowToClose = true;
                var iframeElement = $(this).find("iframe").get(0);
                if (iframeElement){
                    var childWindow = iframeElement.contentWindow;
                    if (childWindow && childWindow.beforeCloseDialog){
                        allowToClose = childWindow.beforeCloseDialog();
                    }
                }
                return allowToClose;
            },
            close:function (event, ui) {
                var iframeElement = $(this).find("iframe").get(0);
                $(iframeElement).attr("src", getAppPath()+"/empty.html");
            }
        }
    );

    // fix stop resizing issues with iFrames see http://dev.jqueryui.com/ticket/3176
    $(popupDiv).parent().resizable({
        start: function(){
            var divElement = $(this).find(".ui-dialog-content").get(0);
            var divJquery = $(divElement);
            var d = $('<div></div>');

            $(divJquery).after(d[0]);
            d[0].id = 'temp_div';
            d.css({position:'absolute'});
            d.css({top: divJquery.position().top, left:0});
            d.height(divJquery.height());
            d.width('100%');
        },
        stop: function(){
            $('#temp_div').remove();
        }
    });

    $("#" + iframeId).attr("src", urlToOpen);
}

function closeDialog(closeAnyway) {
    if (window.frameElement) {
        var divElement = window.frameElement.parentElement;
        if (window.parent) {
            if (closeAnyway){
                window.parent.$(divElement).dialog({
                    beforeClose: function( event, ui ) {return true;}
                })
            }
            window.parent.$(divElement).dialog('close');
        }
    }
}

function selectRowForIndex(dataTableVar, rowIndex) {
    var indexOnCurrentPage = rowIndex;
    if (PF(dataTableVar).cfg.paginator) {
        var rowsPerPage = PF(dataTableVar).cfg.paginator.rows;
        indexOnCurrentPage = rowIndex % rowsPerPage;
    }
    PF(dataTableVar).unselectAllRows();
    PF(dataTableVar).selectRow(indexOnCurrentPage, false);
}

function resetRows(dataTableVar){
    selectRowForIndex(dataTableVar);
}

function resetPaginator(dataTableVar){
    PF(dataTableVar).getPaginator().setPage(0);
}

var showProcessingDialog = true;
var skipCount = 0;

function skipProcessingDialog() {
    showProcessingDialog = false;
}

function onAjaxStart() {
    document.body.style.cursor = 'wait';
    if (showProcessingDialog) {
        $("#processingDialog").dialog("open");
    } else {
        showProcessingDialog = true;
    }
    resetSessionTimeoutObject();
}

function onAjaxSend(xhr, status, args) {
/*
    if (window.event) {
        if (window.event.type == "change") {
            showProcessingDialog = false;
        }
    }
*/
/*
    if (showProcessingDialog) {
        $("#processingDialog").dialog("open");
    } else {
        ++skipCount;
        showProcessingDialog = true;
    }
    resetSessionTimeoutObject();
*/
}

function onAjaxComplete(event, xhr, settings) {
//    alert("onAjaxComplete");

    if(settings && settings.args && settings.args.lazyLoadException) {
        displayErrorMessageDialog();
    }

    if (window.handleOnAjaxComplete) {
        window.handleOnAjaxComplete(event, xhr, settings);
    }
    $("#processingDialog").dialog("close");
/*
    if (skipCount > 0) {
        skipCount--;
    } else {
        $("#processingDialog").dialog("close");
    }
*/
    document.body.style.cursor = 'default';
}

function onAjaxStop() {
//    alert("onAjaxStop");
    $("#processingDialog").dialog("close");

/*
    // Reset the focus to the last focused element if an element does not currently have focus.
    var currentFocusedElementId = getFocusElementId();
    if ((!isDefined(currentFocusedElementId) || currentFocusedElementId == "") &&
         (isDefined(lastFocusedElementId) && (lastFocusedElementId != ""))) {
//        alert("Setting focus to: " + lastFocusedElementId);
        setFocus(lastFocusedElementId);
    }
    lastFocusedElementId = null;
*/

    document.body.style.cursor = 'default';
}

/*
var lastFocusedElementId;
function updateLastFocusedElementId() {
    lastFocusedElementId = getFocusElementId();
//    alert("updateLastFocusedElementId: " + lastFocusedElementId);
}

 // doesn't work because the event handlers get wiped out when the datatable is updated.
 // Also, the calendar fields don't allow the next/previous field to get focus when tabbing after changing the date
$(":input").focus(function () {
    updateLastFocusedElementId();
});
*/

function getFocusElementId() {
//    alert("getFocusElementId: " + $("*:focus").attr("id"));
    return $("*:focus").attr("id");
}

function setFocus(elementId) {
    var element = document.getElementById(elementId);
    if (element.focus) element.focus();
    if (element.setSelectionRange) element.setSelectionRange(element.value.length, element.value.length);
    return;
}

function displayWarningMessageDialog(dialogTitle, message) {
    $(document.getElementById("headerForm:warningMessageDialogId")).find(".ui-dialog-title").html(dialogTitle);
    $(document.getElementById("headerForm:warningMessageDetail")).html(message);
    PF('warningMessageDialog').show();
}

function displayConfirmMessageDialog(dialogTitle, message) {
    $(document.getElementById("headerForm:warningMessageDialogId")).find(".ui-dialog-title").html(dialogTitle);
    $(document.getElementById("headerForm:warningMessageDetail")).html(message);
    PF('warningMessageDialog').show();
}

function displayErrorMessageDialog(dialogTitle, message) {
    $(document.getElementById("headerForm:warningMessageDialogId")).find(".ui-dialog-title").html(dialogTitle);
    $(document.getElementById("headerForm:warningMessageDetail")).html(message);
    PF('errorMessageDialog').show();
}


var currentButtonId = null;
var disableButton = null;

/**
 * Example to use "parameters":
 *
 * baseOnButtonStart('editConfigEventDlgForm:saveEditButton', {disableButton:false});
 *
 * Just set the properties you want. These are the supported properties:
 *  Property Name    Default Value      Description
 *  disableButton    true               disable/enable button automatically
 *
 *
 * @param currentButtonIdOverride   required
 * @param parameters                optional
 */
function baseOnButtonStart(currentButtonIdOverride, parameters) {
    if (currentButtonIdOverride) {
        currentButtonId = currentButtonIdOverride;
    } else {
        if (window.event.currentTarget) {
            currentButtonId = window.event.currentTarget.id;
        } else if (window.event.srcElement) {
            currentButtonId = window.event.srcElement.id;
            if (!currentButtonId) {
                currentButtonId = window.event.srcElement.parentElement.id;
            }
        }
    }
    disableButton = true;
    if (parameters) {
        if (parameters.disableButton == false) {
            disableButton = false;
        }
    }
    if (currentButtonId) {
        if (disableButton) {
            var buttonElement = document.getElementById(currentButtonId);
            $(buttonElement).attr('disabled', 'disabled');
        }
        if (window.commonOnButtonStart) {
            window.commonOnButtonStart(currentButtonId);
        }
        if (window.handleOnButtonStart) {
            window.handleOnButtonStart(currentButtonId);
        }
    }
}

function commonOnButtonStart(buttonId) {
    // add common logic here
}

function baseOnButtonComplete(xhr, args) {
    if (disableButton && currentButtonId) {
        var buttonElement = document.getElementById(currentButtonId);
        $(buttonElement).removeAttr('disabled');
    }
    if (args){
        if (args.DISPLAY_WARNING_DIALOG == "true") {
            var title = args.WARNING_TITLE;
            var message = args.WARNING_MESSAGE.replace(/xyzXYZ/g, "<br />");
            displayWarningMessageDialog(title, message);
        } else if (args.DISPLAY_CONFIRM_DIALOG == "true") {
            var title = args.CONFIRM_TITLE;
            var message = args.CONFIRM_MESSAGE;
            displayConfirmMessageDialog(title, message);
        }
        if (currentButtonId) {
            if (window.commonOnButtonComplete) {
                window.commonOnButtonComplete(currentButtonId, xhr, args);
            }
            if (window.handleOnButtonComplete) {
                window.handleOnButtonComplete(currentButtonId, xhr, args);
            }
        }
    }
    currentButtonId = null;
}

function commonOnButtonComplete(buttonId, xhr, args) {
    // add common logic here
}

/*
$(document).ready(function () {
    $(".ui-datatable").keydown(function (event) {
        if (event.keyCode == 13) {
            event.preventDefault();
            return false;
        }
    });
});
*/


// The PrimeFaces.monitorDownload has bug to clear interval.
function monitorDownload(start, complete){
    if(start) {
        start();
    }

    window.downloadMonitor = setInterval(function() {
        var downloadComplete = PrimeFaces.getCookie('primefaces.download');

        if(downloadComplete == 'true') {
            if(complete) {
                complete();
            }
            clearInterval(window.downloadMonitor);
            PrimeFaces.setCookie('primefaces.download', null);
        }
    }, 500);
}

function baseOnClosePage() {
    if (window.commonOnClosePage) {
        window.commonOnClosePage();
    }
    if (window.handleOnClosePage) {
        window.handleOnClosePage();
    }
}

//funciton when hide the dialog
function baseOnHideDialog(dialog, resetButtonId) {
    if(resetButtonId){
        $(document.getElementById(resetButtonId)).click();
    }
}

//TODO: Refactor closePageDirectly() logic to  handleOnClosePage()
function commonOnClosePage(){
    closePageDirectly();
    PF('closePageDlg').hide();
    isChanged = false;
}

function cancelClose(){
    PF('closePageDlg').hide();
}

function formatInputDate() {
    var DATE_MASK = getDateMask();
    var evt = window.event;
    var val = evt.srcElement.value;

    var code = evt.keyCode;

    // Changed to support Date format dd/mon/yyyy
    if (DATE_MASK.indexOf('MMM') <= 0) {
        if (code < 48 || code > 57 || val.length > 9) {
            evt.cancelBubble = true;
            evt.returnValue = false;
        }
        if (DATE_MASK.substring(0, 4) != 'yyyy') {
            switch (val.length) {
                case 1:
                    if (DATE_MASK.substring(0, 2) != 'dd') {
                        if (val.substring(0, 1) == "1") {
                            if (code > 50) {
                                evt.cancelBubble = true;
                                evt.returnValue = false;
                                return false;
                            }
                        }
                        else if (val.substring(0, 1) != "0") {
                            evt.srcElement.value = "0" + val + "/";
                        }
                    }
                    break;
                case 2:
                    if (DATE_MASK.substring(0, 2) != 'dd')
                        evt.srcElement.value = val + "/";
                    else
                        evt.srcElement.value = val + ".";
                    break;
                case 4:
                    if (DATE_MASK.substring(0, 2) == 'dd') {
                        if (val.substring(3, 4) == "1") {
                            if (code > 50) {
                                evt.cancelBubble = true;
                                evt.returnValue = false;
                                return false;
                            }
                        }
                        else {
                            if (val.substring(3, 4) != "0") {
                                evt.srcElement.value = val.substring(0, 3) + "0" + val.substring(3, 4) + ".";
                            }
                        }
                    }
                    break;
                case 5:
                    if (DATE_MASK.substring(0, 2) != 'dd')
                        evt.srcElement.value = val + "/";
                    else
                        evt.srcElement.value = val + ".";
                    break;
//            case 9:
//                if (val.substring(6, 9) > 300) {
//                    evt.srcElement.value = val.substring(0, 6) + "300";
//                }
            }
        } else if (DATE_MASK.substring(0, 4) == 'yyyy') {
            // Changed to support Chinese Date format yyyy/mm/dd
            switch (val.length) {
                case 4:
                    evt.srcElement.value = val + '/';
                    break;
                case 6:
                    if (val.substring(5, 6) == "1") {
                        if (code > 50) {
                            evt.cancelBubble = true;
                            evt.returnValue = false;
                            return false;
                        }
                    } else {
                        if (val.substring(5, 6) != "0") {
                            evt.srcElement.value = val.substr(0, 5) + '0' + val.substr(5, 6) + '/';
                        }
                    }
                    break;
                case 7:
                    evt.srcElement.value = val + '/';
                    break;
            }
        } else {
            switch (val.length) {

                case 1:
                    break;
                case 2:
                    evt.srcElement.value = val + "/";
                    break;
                case 4:
                    if (val.substring(3, 4) == "1") {
                        if (code > 50) {
                            evt.cancelBubble = true;
                            evt.returnValue = false;
                            return false;
                        }
                    }
                    else {
                        if (val.substring(3, 4) != "0") {
                            evt.srcElement.value = val.substring(0, 3) + "0" + val.substring(3, 4) + "/";
                        }
                    }
                    break;
                case 5:
                    evt.srcElement.value = val + "/";
                    break;
            }
        }
        return true;
    } else {
        // Changed to support Date format dd/mon/yyyy
        if ((code >= 48 && code <= 57 ) || (code >= 65 && code <= 90)) {
            var val = evt.srcElement.value;
            var txt = "";

            if (document.getSelection)
                txt = document.getSelection();
            else if (document.selection)
                txt = document.selection.createRange().text;

            if ((val.length - txt.length) > 10) {
                evt.cancelBubble = true;
                evt.returnValue = false;
                return false;
            }
            switch (val.length) {
                case 0:
                case 1:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
                case 2:
                    if (code < 65) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    evt.srcElement.value = val + "/";
                    break;
                case 3:
                case 4:
                case 5:
                    if (code < 65) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
                case 6:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    evt.srcElement.value = val + "/";
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
            }
            return true;
        }
        else {
            evt.cancelBubble = true;
            evt.returnValue = false;
            return false;
        }
    }
}
