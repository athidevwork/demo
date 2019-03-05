//*********************************************************************
// GUI functions (gui.js)
// Purpose: Functions used for user interface on a web page.
//*********************************************************************
/*
Revision Date    Revised By  Description
----------------------------------------------------------------------------
07/10/2008       Fred        Added code to support inputting time
04/13/2009       mxg         82494: Changes to handle Date Format Internationalization
08/11/2010       wfu         109874: Added function URLEncode to handle url encoding
08/12/2010       wfu         109874: Changes function name to urlEncode
08/23/2010       wfu         109874: Adds function urlEncode scope with blank (%20)
08/25/2010       wfu         109874: Changes function urlEncode scope with excluding %xy form
08/26/2010       wfu         109874: Format code of function urlEncode for better readability
04/02/2013       jshen       142992: Modified baseOnFind function to support both finder field and multiple select field
09/04/2013       jshen       145164: Modified baseOnFind function to reset the currentFinderFieldName to empty
03/10/2017       eyin        180675: Modified message function for Oasis Message tag.
04/21/2017       ddai        184899: Modified message function, change innerText to innerHTML to make html tag working.
05/05/2017       ddai        185077: Modified getParentWindow function to avoid the code loop.
05/12/2017       ddai        185077: Modified getParentWindow function to match PM function.
09/12/2017       dpang       187778: Added function hasHiddenStyle.
09/28/2017       eyin        188724: Modified getParentWindow function, make sub-function getParentFrame && getOpenerWindow
                             also support input parameter 'isNeedCurrentWindowB', when parameter isNeedCurrentWindowB
                             equals TRUE, return current window if parent window doesn't exist or parent window is NOT
                             in the same domain with current window.
10/12/2017       kshen       Grid replacement. Add fixEvent method for supporting IE8 Event API in Firefox.
11/16/2017       eyin        189818: Modified initializeMessages, change to remove the error messages that were previously
                             displayed before displaying new messages.
12/12/2017       kshen       Grid replacement. Changed closeWindow to support callback function.
12/15/2017       eyin        190085 - Modified removeMessages(), add to call commonOnRemoveMessages();
12/27/2017       kshen       Grid replacement: enforce call preventDefault if the return vale is false.
01/23/2018       dzhang      Grid replacement: Get the current window reference before callback function invoked. add comments.
05/07/2018       cesar       #192599 - Modified fixEvent() if the the parameter is an event. this is because the name of the
                                       html element has the same name of "event". Firefox will pass the event regardless of the
                                       name of the HTML element.
05/31/2018       cesar       #191837 - Modified getParentWindow() not to return null.
06/08/2018       cesar       revert changes for #191837. Claims is expecting a null to be returned to exit loop.
06/14/2018       htwang      #191837 - Fixed JS error while closing multiple popup pages.
06/14/2018       wrong       193831 - Modified generateConfirmationMessages to add a "null" case for if condition.
07/25/2018       dzhang      194134 - fix if eventName start with "on" string.
11/23/2018       wrong       197214 - Enhance getParentWindow to add a condition to exclude getting opener window.
12/07/2018       dpang       196632 - Modified dispatchElementEvent() to call dti.oasis.ui.dispatchEvent().
----------------------------------------------------------------------------
*/

var DHTML_ARROW_MIN = 37;
var DHTML_ARROW_MAX = 40;
var DHTML_LEFT_ARROW = 37;
// left-arrow
var DHTML_UP_ARROW = 38;
// up-arrow
var DHTML_RIGHT_ARROW = 39;
// right-arrow
var DHTML_DOWN_ARROW = 40;
// down-arrow

var overimg = new Array()
var overmsg = new Array()
var outimg = new Array()

var me = "_self"
//var parent = "_parent"
//var top = "_top"
var currentFinderFieldName="";

function Browser() {
    var nm = navigator.appName
    this.je = navigator.javaEnabled()
    var v = parseInt(navigator.appVersion)
    this.ns = (nm == "Netscape" && v >= 4)
    this.ie = (nm == "Microsoft Internet Explorer" && v >= 4)
    this.minreq = (this.ns || this.ie && this.je)
}

browser = new Browser()
function setCookie(cName, Props) {
    document.cookie = cName + "=" + Props + "; path=/; expires=" + expiration.toGMTString()
    DocCook = document.cookie;
}
function setCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000))
        var expires = "; expires=" + date.toGMTString()
    }
    else expires = ""
    document.cookie = name + "=" + value + expires + "; path=/"
}

function getCookie(name) {
    var c = document.cookie
    if (c.indexOf(name) == -1) return null
    else {
        var start = c.indexOf(name) + name.length + 1
        for (var end = start; end < c.length; end++) if (c.charAt(end) == ";") break
        return c.substring(start, end)
    }
}

function deleteCookie(name) {
    setCookie(name, "", -1)
}
function setMsg(msg) {
    window.status = msg
}


function getStyle(id) {
    return getObject(id).style;
}

// show and hide functions
function show(id) {
    getStyle(id).visibility = "visible"
}

function hide(id) {
    getStyle(id).visibility = "hidden"
}

// background color
function setbg(id, color) {
    getStyle(id).backgroundColor = color
}


function mouseOverImg() {
    // arguments contain image url, imagename and message to be displayed
    // when mouse rolls over it
    var args = mouseOverImg.arguments
    var len = (args.length) / 3
    for (i = 0; i < len; i++) {
        j = i * 3
        overimg[args[j + 1]] = new Image()
        overimg[args[j + 1]].src = args[j]
        overmsg[args[j + 1]] = args[j + 2]
    }
}

function imageOver(img) {
    var imgobj
    if (typeof img == "string") {
        imgobj = getObject(img)
    } else {
        imgobj = img
    }
    if (imgobj) {
        if (!outimg[imgobj.name]) {
            outimg[imgobj.name] = new Image()
            outimg[imgobj.name].src = imgobj.src
        }
        if (overimg[imgobj.name]) {
            imgobj.src = overimg[imgobj.name].src
            setMsg(overmsg[imgobj.name])
        }
    }
}

function imageOut(img) {
    var imgobj
    if (typeof img == "string") {
        imgobj = getObject(img)
    } else {
        imgobj = img
    }
    if (imgobj) {
        imgobj.src = outimg[imgobj.name].src
        setMsg('')
    }
}

function setZIndex(name, index) {
    obj = getStyle(name)
    obj.zIndex = index
}

function layer(id, left, top, width, height, color, vis, z, other) {

    var str = (left != null && top != null)? '#' + id + ' {position:absolute; left:' + left + 'px; top:' + top + 'px;' : '#' + id + ' {position:relative;'
    if (arguments.length >= 4 && width != null) str += ' width:' + width + 'px;'
    if (arguments.length >= 5 && height != null) {
        str += ' height:' + height + 'px;'
        if (arguments.length < 9 || other.indexOf('clip') == -1) str += ' clip:rect(0px ' + width + 'px ' + height + 'px 0px);'
    }
    if (arguments.length >= 6 && color != null) str += (window.document.layers)? ' layer-background-color:' + color + ';' : ' background-color:' + color + ';'
    if (arguments.length >= 7 && vis != null) str += ' visibility:' + vis + ';'
    if (arguments.length >= 8 && z != null) str += ' z-index:' + z + ';'
    if (arguments.length == 9 && other != null) str += ' ' + other
    str += '}\n'
    str = '<STYLE TYPE="text/css">\n' + str + '</STYLE>\n'
    return str
}

function createLayer(id, nestref, left, top, width, height, content, bgColor, visibility, zIndex) {
    var str = '\n<DIV id=' + id + ' style="position:absolute; left:' + left + '; top:' + top + '; width:' + width
    if (height != null) {
        str += '; height:' + height
        str += '; clip:rect(0,' + width + ',' + height + ',0)'
    }
    if (bgColor != null) str += '; background-color:' + bgColor
    if (zIndex != null) str += '; z-index:' + zIndex
    if (visibility) str += '; visibility:' + visibility
    str += ';">' + ((content)?content:'') + '</DIV>'
    if (nestref) {
        index = nestref.lastIndexOf(".")
        var nestlyr = (index != -1)? nestref.substr(index + 1) : nestref
        document.all[nestlyr].insertAdjacentHTML("BeforeEnd", str);
    }
    else {
        document.body.insertAdjacentHTML("BeforeEnd", str)
    }

}

function destroyLayer(id, nestref) {

    document.all[id].innerHTML = ""
    document.all[id].outerHTML = ""

}

function GifAnim(layer, imgName, imgSeries, end, speed, startFrame) {
    this.layer = layer
    this.imgName = imgName
    this.frame = new Array()
    for (var i = 0; i <= end; i++) this.frame[i] = imgSeries + i
    this.end = end
    this.speed = speed
    this.active = false
    this.count = (startFrame)? startFrame : 0
    this.obj = imgName + "GifAnim"
    eval(this.obj + "=this")
    this.play = GifAnimPlay
    this.run = GifAnimRun
    this.stop = GifAnimStop
    this.goToFrame = GifAnimGoToFrame
}

function GifAnimPlay(loop, reset, fn) {
    if (!this.active) {
        this.active = true
        if (!loop) loop = false
        if (!reset) reset = false
        if (!fn) fn = null
        this.run(loop, reset, fn)
    }
}

function GifAnimRun(loop, reset, fn) {
    if (this.active && this.count <= this.end) {
        changeImage(this.layer, this.imgName, this.frame[this.count])
        this.count += 1
        setTimeout(this.obj + ".run(" + loop + "," + reset + ",\"" + fn + "\")", this.speed)
    }
    else {
        if (loop && this.active) {
            this.count = 0
            this.run(loop, reset, fn)
        }
        else {
            this.active = false
            if (reset) this.goToFrame(0)
            eval(fn)
        }
    }
}

function GifAnimStop() {
    this.active = false
}

function GifAnimGoToFrame(index) {
    this.count = index
    changeImage(this.layer, this.imgName, this.frame[this.count])
}

function changeImage(layer, imgName, imgObj) {
    if (document.images) {
        if (document.layers && layer != null) eval('document.' + layer + '.document.images["' + imgName + '"].src = ' + imgObj + '.src')
        else document.images[imgName].src = eval(imgObj + ".src")
    }
}

function whichButton(theForm, buttonName, buttonValue) {
    buttonName.value = buttonValue;
    baseOnSubmit(theForm);
    return(true);
}

function maximize() {
    window.moveTo(0, 0)
    window.resizeTo(screen.availWidth, screen.availHeight)
}

var message
var loops
var delay
var currCount = 1
var timerID

function scrollMsg(msg, loopcount, dly) {
    message = msg
    loops = loopcount
    delay = dly
    myScroll()
    return true;
}

function myScroll() {
    window.status = message
    if (loops == 0 || loops == null) {
        loops = 1
    }
    maxCount = loops * message.length
    currCount++
    message = message.substring(1, message.length) + message.substring(0, 1)
    if (currCount >= maxCount) {
        stopScroll()
        currCount = 1
        return;
    } else {
        timerID = setTimeout("myScroll()", delay)
    }
}

function stopScroll() {
    clearTimeout(timerID)
    window.status = ''
}

function TabClick(nTab, sUrl)
{


    nTab = parseInt(nTab);
    var oTab;
    var prevTab = nTab - 1;
    var nextTab = nTab + 1;
    event.cancelBubble = true;
    el = event.srcElement;

    for (var i = 0; i < TotalTabs; i++)
    {
        oTab = tabs[i];
        oTab.className = "clsTab";
        oTab.style.borderLeftStyle = "";
        oTab.style.borderRightStyle = "";
        if (sUrl == "") {
            if (tabContent[i])
                tabContent[i].style.display = "none";
        }
    }

    if (sUrl == "") {
        if (tabContent[nTab])
            tabContent[nTab].style.display = "block";
    }
    tabs[nTab].className = "clsTabSelected";
    oTab = tabs[nextTab];
    if (oTab) oTab.style.borderLeftStyle = "none";
    oTab = tabs[prevTab];
    if (oTab) oTab.style.borderRightStyle = "none";
    if (sUrl != "")
    {
        var index = sUrl.indexOf("tab=");
        if (index <= 0)
        {
            index = sUrl.indexOf("?");
            if (index > 0)
                sUrl = sUrl + "&tab=" + nTab;
            else
                sUrl = sUrl + "?tab=" + nTab;
        }
        setWindowLocation(sUrl);
    }

    event.returnValue = false;

}

function TabSaveState() {
    var iTabSelected = 0;
    var iLength = tabs.length;
    for (var i = 0; i < iLength; i++) {
        if (tabs[i].className == "clsTabSelected") iTabSelected = i;
    }
    idTabs.setAttribute("tabstate", iTabSelected);
}


function TabGetState() {
    var iTabSelected = idTabs.getAttribute("tabstate");
    var iLength = tabs.length;
    for (var i = 0; i < iLength; i++) {
        if (i != iTabSelected) {
            tabs[i].className = "clsTab";
            //if (newsContent[i])
            //	newsContent[i].style.display = "none";
        } else {
            tabs[i].className = "clsTabSelected";
            //if (newsContent[i]) newsContent[i].style.display = "block";
        }
    }
}


function ToggleDisplay(oButton, oItems)
{

    if ((oItems.style.display == "") || (oItems.style.display == "none")) {
        oItems.style.display = "block";
        oButton.src = "../images/minus.gif";
    } else {
        oItems.style.display = "none";
        oButton.src = "../images/plus.gif";
    }
    return false;
}

function leftnav_keyup()
{
    var iKey = window.event.keyCode;

    // BUGBUG: IE4 returns BODY instead of element with the focus. Use event object instead
    //var oActive = document.activeElement;
    var oActive = window.event.srcElement;

    if (DHTML_LEFT_ARROW == iKey || DHTML_RIGHT_ARROW == iKey)
    {
        if ('clsTocHead' == oActive.className)
        {
            // handle headings that expand/collapse
            HandleKeyForHeading(oActive, iKey);
        }
        else if ("A" == oActive.tagName)
        {
            MoveFocus(oActive, iKey);
        }
    }

    return;
}


function MoveFocus(oActive, iKey)
{
    iSrcIndex = oActive.sourceIndex;

    if (iKey == DHTML_RIGHT_ARROW)
    {
        while (oItem = document.all[ ++iSrcIndex ])
        {
            if (!leftNavTable.contains(oItem)) return;
            if ("A" == oItem.tagName)
            {
                oItem.focus();
                break;
            }

        }
    }
    else
    {
        while (oItem = document.all[ --iSrcIndex ])
        {
            if (( "clsTocHead" == oItem.className || "clsTocHead" == oItem.parentElement.className ) && "A" == oItem.tagName)
            {
                oItem.focus();
                break;
            }

        }
    }
}


// Handle keyboard action on a section
function HandleKeyForHeading(oActive, iKey)
{

    sActiveId = oActive.id;
    oItem = document.all[ sActiveId + "Items" ];
    oBtn = document.all[ sActiveId + "Btn" ];

    if (( "block" != oItem.style.display ) ^ ( DHTML_LEFT_ARROW == iKey ))
    {
        ToggleDisplay(oBtn, oItem);
    }
    else
    {
        MoveFocus(oActive, iKey);
    }
}


function handleMouseover() {
    eSrc = window.event.srcElement;
    eSrcTag = eSrc.tagName.toUpperCase();
    eSrcType = eSrc.type.toUpperCase();
    if (eSrcTag == "DIV" && eSrc.className.toUpperCase() == "CLSTOCHEAD")    eSrc.style.textDecoration = "underline";
    if (eSrcTag == "LABEL") eSrc.style.color = "#003399";
    if (eSrcType == "SUBMIT") eSrc.className="buttonMouseOver";
}

function handleMouseout() {
    eSrc = window.event.srcElement;
    eSrcTag = eSrc.tagName.toUpperCase();
    eSrcType = eSrc.type.toUpperCase();
    if (eSrcTag == "DIV" && eSrc.className.toUpperCase() == "CLSTOCHEAD")    eSrc.style.textDecoration = "";
    if (eSrcTag == "LABEL") eSrc.style.color = "";
    if (eSrcType == "SUBMIT") eSrc.className="buttons";
}

//document.onmouseover=handleMouseover;
//document.onmouseout=handleMouseout;
function getPositionTop(e) {
    var t = e.offsetTop;
    while (e = e.offset) {
        t += e.offsetTop;
    }
    return t;
}
function getPositionLeft(e) {
    var l = e.offsetLeft;
    while (e = e.offset) {
        l += e.offsetLeft;
    }
    return l;
}
// Get field position ,if filed's height or width over document's width or height to do set new hrightor width .
function getAbsPosition(obj, offsetObj) {
    var _offsetObj = (offsetObj)?offsetObj:document.body;
    var x = obj.offsetLeft;
    var y = obj.offsetTop;
    var tmpObj = obj.offsetParent;

    while ((tmpObj != _offsetObj) && tmpObj) {
        x += tmpObj.offsetLeft + tmpObj.clientLeft - tmpObj.scrollLeft;
        y += tmpObj.offsetTop + tmpObj.clientTop - tmpObj.scrollTop;
        tmpObj = tmpObj.offsetParent;
    }
    var editor = obj;
    var maxHeight = 250;
    var _posLeft = x + 1;
    var _posTop = y + editor.offsetHeight + 1;
    var new_width = 350;
    var new_height = 250;

    var document_width = document.body.clientWidth + document.body.scrollLeft;
    var document_height = document.body.clientHeight + document.body.scrollTop;

    _posTop = _posTop - document.body.scrollTop + parseInt(window.screenTop);

    if (_posLeft + new_width > document_width && document_width > new_width)
    {
        _posLeft = document_width - new_width;
    }
    if (parseInt(window.screen.availHeight) - parseInt(_posTop) < new_height + 35)
    {
        _posTop = _posTop - new_height - (editor.offsetHeight + 3) * 2;
    }
    return {left:_posLeft, top:_posTop};
}

function calendar(sFieldName, displayTime)
{
    var showTime = (typeof displayTime != 'undefined');
    var doPopUp = true;
    if (window.event != null) {
        var dateObj = window.event.srcElement;
    } else {
        var dateObj = getSingleObject(sFieldName);
    }
    if (dateObj) {    //don't popup if field is disabled
        if (dateObj.type == "text") {
            if (dateObj.disabled) doPopUp = false;
        }
    }
    //Using DHTML Calendar in calendarAll.js
//    if (doPopUp && showTime) {
//        showCalendar(sFieldName, '%m/%d/%Y %l:%M %p', "showTime");
//    } else if (doPopUp) {
//        showCalendar(sFieldName, '%m/%d/%Y');
//    }

    //Added Date Format Internationalization
    //Use global variable calendarFmt
    var fmtShort = calendarFmt;
    var fmtExtended = calendarFmt + ' %I:%M %p';
    if (doPopUp && showTime) {
        showCalendar(sFieldName, fmtExtended, "showTime");
    } else if (doPopUp) {
        showCalendar(sFieldName, fmtShort);
    }
}

function baseOnFind(fieldName)
{
    currentFinderFieldName = fieldName;
    var finderField = getObject(fieldName);
    if (finderField) {
        if (finderField.disabled) {
            // Skip the finder logic if the field is disabled
        }
        else {
            // Execute the custom find method
            var objAFD = getSingleObject("AFD_" + fieldName);
            if (eval(objAFD)) {
                var finderFunctionName = objAFD.getAttribute("finderFunctionName");
                if (isDefined(finderFunctionName)) {
                    var functionExists = eval("window." + finderFunctionName);
                    if (functionExists) {
                        eval(finderFunctionName+"('"+fieldName+"')");
                    }
                }
            }
        }
    }
    // reset the value to empty
    currentFinderFieldName = "";
}

function handleEvent(evt) {
    try{
        eval(evt);
    } catch (e) {
        // do nothing.
    }
}

function winopen(sPath)
{
    var sUrl = '';
    var obj = eval(sPath);
    if (obj != null && obj.type != null) {
        sUrl = obj.value;
    }
    else
    {
        sUrl = sPath;
    }
    if (sUrl.indexOf('http://') < 0 && sUrl.indexOf('https://') < 0 && sUrl.indexOf('file://') < 0)
    {
        sUrl = 'http://' + sUrl;
    }
    var mainwindow = window.open(sUrl, 'OASIS', 'toolbar=yes,location=yes,menubar=yes,scrollbars=yes,resizable=yes,status=yes,width=800,height=600,left=10,top=10');
    if (mainwindow.opener == null) mainwindow.opener = self;
    if (sUrl.indexOf('file://') < 0)
        mainwindow.focus();

    return;

}

function openPopup(sUrl, width, height)
{
    return openPopup(sUrl, 'popupWindow', width, height);
}

function openPopup(sUrl, winName, width, height)
{
    return openPopup(sUrl, winName, width, height, 100, 200);
}

function openPopup(sUrl, winName, width, height, top, left)
{
    var myPopup = '';
    myPopup = window.open(sUrl, winName, 'scrollbars=yes,width=' + width + ',height=' + height + ',top=' + top + ',left=' + left);
    if (!myPopup.opener)
        myPopup.opener = self;
    myPopup.focus();
    return myPopup;

}
function openPopup(sUrl, winName, width, height, top, left, resizable)
{
    var myPopup = '';
    myPopup = window.open(sUrl, winName, 'scrollbars=yes,width=' + width + ',height=' + height + ',top=' + top + ',left=' + left + ',resizable=' + resizable);
    if (!myPopup.opener)
        myPopup.opener = self;
    myPopup.focus();
    return myPopup;

}

function openApp(sUrl, sTitle)
{
    var mainwindow = window.open(sUrl, sTitle, 'toolbar=no,location=no,menubar=no,scrollbars=yes,resizable=yes,status=yes,width=800,height=600');
    if (mainwindow.opener == null) mainwindow.opener = self;
    mainwindow.moveTo(10, 10);
    mainwindow.focus();
}

function closeWindow(callBackEventName) {
    var parentWindow = getParentWindow();
    var divPopup = null;

    function __doCloseWindow() {
        if (divPopup) {
            // Close div pop up.
            // If there are two or more popup pages, system will close the first popup page first,
            // then it is impossible to continue closing the top popup page.
            // In such case, system should check whether it is possible to continue closing other popup pages first.
            if (parentWindow.closeDiv) {
                parentWindow.closeDiv(divPopup);
            }
        } else if (parentWindow != null) {
            // Close parent window.
            if (baseCloseWindow) {
                baseCloseWindow();
            }
        }
    }
    //if the parent window send a request to the server, the content of the popup window object could be undefined in IE11.
    // Callback function may let parent window send request to refresh other child frames. pop window objects will be undefined when request send.
    // So handle the div pop before callback function invoked since window.frameElement may undefined after callback executed.
    if (window.frameElement) {
        // Close div pop up.
        divPopup = parentWindow.getDivPopupFromDivPopupControl(window.frameElement);
    }
    if ($.isFunction(callBackEventName)) {
        // The callback event name is a function.
        var result = callBackEventName();

        if (result === false) {
            // If the function return false, the window should not be closed.
            return;
        }
    } else {
        var callBackEventRef = "";
        var callBackfunctionExists = false;

        if (callBackEventName) {
            if (parentWindow != null && parentWindow != window) {
                if (callBackEventName.indexOf("(") == -1) {
                    callBackEventName += "()";
                }
                callBackEventRef = "parentWindow.";
                callBackEventRef += callBackEventName.substring(0, callBackEventName.indexOf("("));
                callBackfunctionExists = eval(callBackEventRef) ;
            }
        }

        if (callBackfunctionExists) {
            parentWindow.handleEvent(callBackEventName);
        }
    }

    if (__doCloseWindow) {
        // If the IE debug tool is opened, the jqxGrid may throw some error about closing grid menu.
        // We can use setTimeout callback function to close the window after grid menu closed.
        window.setTimeout(function () {
            try {
                __doCloseWindow();
            } catch (e) {
               // Ignore the error about closing window.
            }
        });
    }
}

function MM_preloadImages() { //v3.0
    var d=document;
    if(d.images){
        if(!d.MM_p)
            d.MM_p=new Array();
        var i,j=d.MM_p.length,a=MM_preloadImages.arguments;
        for(i=0; i <a.length; i++)
            if ( a[i]. indexOf("#")!=0){
                d.MM_p[j]=new Image;
                d.MM_p[j++].src=a[i];
            }
    }
}

function MM_swapImgRestore() { //v3.0
    var i,x,a=document.MM_sr;
    for(i=0;a&&i<a.length&&( x=a[i])&&x.oSrc;i++)
        x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
    var p,i,x;
    if(!d) d=document;
    if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document;
        n=n.substring(0,p);
    }
    if(!(x=d[n])&&d.all)
        x=d.all[n];
    for (i=0;!x&&i<d.forms.length; i++)
        x=d.forms[i][n];
    for(i=0;!x&&d.layers&&i<d.layers.length; i++)
        x=MM_findObj(n,d.layers[i].document);
    if(!x && d.getElementById)
        x=d.getElementById(n);
    return x;
}

function MM_swapImage() { //v3.0
    var i,j=0,x,a=MM_swapImage.arguments;
    document.MM_sr=new Array;
    for(i=0;i<(a.length-2); i+=3)
    if ((x=MM_findObj(a[i]))!=null){
       document.MM_sr[j++]=x;
       if(!x.oSrc)
           x.oSrc=x.src;
       x.src=a[i+2];
    }
}

function openMain(sUrl,sTitle) {
    var mainwindow = window.open(sUrl,sTitle,'toolbar=yes,location=yes,menubar=yes,scrollbars=yes,resizable=yes,status=yes');
    if (mainwindow.opener == null) mainwindow.opener = self;
    if (isPopup) mainwindow.moveTo(10,10);
    mainwindow.focus();
}

function openDWWindow(name)
{
    var mainwin = window.open('about:blank', 'DW_Report', 'width=1024,height=700,resizable=yes,scrollbars=yes,status=yes,top=5,left=5');
    mainwin.focus();
}

function doMenuItem(id, url) {

    if (id == 'CS_DW_RPT_PUP') {
        url = url.replace("%pentahoserver%", datawarehouseServer);
        var tempForm = document.createElement("form");
        tempForm.id = "tempForm1";
        tempForm.method = "post";
        tempForm.action = url;
        tempForm.target = "DW_Report";
        tempForm.attachEvent("onsubmit", function () {
            openDWWindow('DW_Report');
        });
        document.body.appendChild(tempForm);
        tempForm.fireEvent("onsubmit");
        baseOnSubmit(tempForm);
        document.body.removeChild(tempForm);

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
    var menuItemDivPopup = isDivPopUpMenu(id);

    if (!menuItemDivPopup) {
        if (window.isOkToChangePages) {
            if (!isOkToChangePages(id, url))
                return;
        } else {
            functionExists = eval("window.commonIsOkToChangePages");
            if (functionExists) {
                isOkToProceed = commonIsOkToChangePages(id, url);
                if (!isOkToProceed) {
                    return;
                }
            }
        }
    }

    var newUrl = buildMenuQueryString(id, url);

    if (menuItemDivPopup) {
        var idName = 'R_menuitem_' + id;
        var mi = getObject(idName);
        if (mi) {
            mi.children[0].style.backgroundImage = '';
        }
        var divPopupId = openDivPopup("", newUrl, true, true, "", "", "", "", "", "", "", false);
    } else {
        if (getUIStyleEdition()!="0") {
            showProcessingImgIndicator();
        }

        setWindowLocation(newUrl);
    }
}

function buildMenuQueryString(id, url) {

    var queryString = null;
    var st = "";
    var returnUrl = url;

    var functionExists = eval("window.commonGetMenuQueryString");
    if(functionExists) {
        queryString = commonGetMenuQueryString(id, url);

        if(queryString!=null) {
            st = queryString.substring(0,1);
            if(st=='?' || st=='&')
                queryString = queryString.substring(1);
            if(url.indexOf('?')>-1)
                returnUrl+='&'+queryString;
            else
                returnUrl+='?'+queryString;
        }
    }

    queryString=null;
    st="";
    if(window.getMenuQueryString)
        queryString = getMenuQueryString(id,url);

    if(queryString!=null) {
        st = queryString.substring(0,1);
        if(st=='?' || st=='&')
            queryString = queryString.substring(1);
        if(returnUrl.indexOf('?')>-1)
            returnUrl+='&'+queryString;
        else
            returnUrl+='?'+queryString;
    }

    return returnUrl;
}

/**
 * Check if the menu opens a div popup window.
 *
 * This function should be overwrites by common.js in projects.
 *
 * @param menuId
 * @returns {boolean}
 */
function isDivPopUpMenu(menuId) {
    return false;
}

function isInIframe() {
    var isInIframe = false;
    if (window.frameElement) {
        isInIframe = true;
    }
    return isInIframe;
}

// encode url to unicode exclude %xy which is % followed by two characters as number or letter
function urlEncode(url){
    var result = '', c = '';
    var pattern = /^[A-Fa-f0-9]+$/;
    for(var i=0; i<url.length; i++) {
        if(url.charAt(i) == '%' &&
           url.length >= i + 3 &&
           pattern.test(url.substr(i + 1, 2))) {
            result += url.substr(i, 3);
            i += 2;
        }else {
            c = encodeURI(url.charAt(i));
            result += c;
        }
    }
    return result;
}

/**
 * Get parent window of the current window.
 *
 * For a popup window, the parent window is the opener window.
 * For a div popup (iframe), the parent window is the parent frame.
 *
 * If the current window doesn't have a parent window, the function will return null by default. If the value of
 * the parameter isNeedCurrentWindowB is true, the function will return the current window.
 *
 * When trying to invokes the functions on the parent window which is not on same domain with the current window, the
 * system would throw no permission error. To avoid the error, the function returns null by default in this case. If the
 * value of allowCrossOrigin is true, the function will return the parent window instead of null value.
 *
 * @param isNeedCurrentWindowB
 * @param allowCrossOrigin
 * @param isExcludeOpenerWindow
 * @returns {*}
 */
function getParentWindow(isNeedCurrentWindowB, allowCrossOrigin, isExcludeOpenerWindow) {
    var parentWindow = null;

    var functionExists = eval("window.handleOnGetParentWindow");
    if(functionExists){
        parentWindow = handleOnGetParentWindow();
    }else{
        functionExists = eval("window.commonOnGetParentWindow");
        if(functionExists){
            parentWindow = commonOnGetParentWindow();
        }else if (window.frameElement) {
            parentWindow = getParentFrame(allowCrossOrigin);
        }else if ((window.opener && !window.opener.closed) && !isExcludeOpenerWindow) {
            parentWindow = getOpenerWindow(allowCrossOrigin);
        }
    }

    if (isNeedCurrentWindowB && parentWindow == null) {
        return window;
    }else{
        return parentWindow;
    }
}

function getParentFrame(allowCrossOrigin) {
    allowCrossOrigin = (typeof allowCrossOrigin == "undefined") ? false : allowCrossOrigin;

    var parentWindow = null;

    if (window.frameElement) {
        if (window.frameElement.document && window.frameElement.document.parentWindow) {
            // IE8
            parentWindow = window.frameElement.document.parentWindow;
        } else if (window.frameElement && window != window.parent) {
            // HTML5
            parentWindow = window.parent;
        }

        if (parentWindow != null) {
            if (!allowCrossOrigin) {
                try {
                    parentWindow.document;
                } catch (e) {
                    return null;
                }
            }

            return parentWindow;
        }
    }

    return null;
}

function getOpenerWindow(allowCrossOrigin) {
    allowCrossOrigin = (typeof allowCrossOrigin == "undefined") ? false : allowCrossOrigin;

    //it's a child window
    if (window.opener && !window.opener.closed) {
        var parentWindow = window.opener;

        if (!allowCrossOrigin) {
            try {
                parentWindow.document;
            } catch (e) {
                return null;
            }
        }
        return parentWindow;
    }

    return null;
}

function getParentWindowOfDivPopupFrame(divPopFrame, allowCrossOrigin) {
    allowCrossOrigin = (typeof allowCrossOrigin == "undefined") ? false : allowCrossOrigin;

    var parentWindow = null;

    if (divPopFrame.contentWindow && divPopFrame.contentWindow.parent) {
        // HTML5
        parentWindow = divPopFrame.contentWindow.parent;
    } else if (divPopFrame.document && divPopFrame.document.parentWindow) {
        // IE8
        parentWindow = divPopFrame.document.parentWindow;
    }

    if (parentWindow != null) {
        if (!allowCrossOrigin) {
            try {
                parentWindow.document;
            } catch (e) {
                return null;
            }
        }

        return parentWindow;
    }

    return parentWindow;
}

// display messages on the parent/current screen based on parameter displayMessagesOnParent
function initializeMessages(oasisMessages, displayMessagesOnParent){
    var autoSaveSuccessfully = true;
    var oasisMsgElements = [];

    var errorMessages = oasisMessages.errorMessages;
    if(errorMessages.messageArray.length > 0){
        autoSaveSuccessfully = false;
        generateErrorMessages(errorMessages, displayMessagesOnParent, oasisMsgElements);
    }

    if(errorMessages.messageArray.length == 0 || oasisMessages.showAllMessages){
        var warningMessages = oasisMessages.warningMessages;
        var informationMessages = oasisMessages.informationMessages;
        var confirmationMessages = oasisMessages.confirmationMessages;

        if(warningMessages.messageArray.length > 0){
            generateWarningMessages(warningMessages, oasisMsgElements);
        }

        if(informationMessages.messageArray.length > 0){
            generateInformationMessages(informationMessages, oasisMsgElements);
        }

        if(confirmationMessages.messageArray.length > 0){
            generateConfirmationMessages(confirmationMessages, oasisMsgElements);
        }
    }

    if(displayMessagesOnParent && isDefined(displayMessagesOnParent)){
        //189818: Remove the error messages that were previously displayed before displaying new messages.
        getParentWindow(true).removeMessagesForFrame();
    }

    if(oasisMsgElements.length > 0){
        var oTable = getMsgTblNode(displayMessagesOnParent);
        appendMessages(oasisMsgElements, displayMessagesOnParent, oTable);
    }
}

function getMsgDivNode(displayMessagesOnParent){
    if(displayMessagesOnParent && isDefined(displayMessagesOnParent)){
        var functionExists = eval("window.divPopupExists");
        if (functionExists && divPopupExists()) {
            return document.getElementById("oasisMessageBody");
        }
        return getParentWindow(true).document.getElementById("oasisMessageForFrameBody");
    }else{
        return document.getElementById("oasisMessageBody");
    }
}

function getMsgTblNode(displayMessagesOnParent){
    var oDiv = getMsgDivNode(displayMessagesOnParent);
    var oTblArray = [];
    if(displayMessagesOnParent && isDefined(displayMessagesOnParent)){
        var functionExists = eval("window.divPopupExists");
        if (functionExists && divPopupExists()) {
            return getTableObject(oDiv);
        }else{
            oTblArray = oDiv.querySelectorAll("#oasisMessageForFrameBody>table");
        }
    }else{
        return getTableObject(oDiv);
    }
    if(oTblArray.length > 0){
        return oTblArray[0];
    }
    return null;
}

function getTableObject(oDiv){
    var oTableArr = oDiv.childNodes;
    for(var i = 0; i < oTableArr.length; i++){
        if(oTableArr[i].nodeName.toUpperCase() == "TABLE"){
            if(oTableArr[i].childNodes.length == 0){
                return oTableArr[i];
            }else{
                var oTbodyArr = oTableArr[i].childNodes;
                for(var j = 0; j < oTbodyArr.length; j++){
                    if(oTbodyArr[j].nodeName.toUpperCase() == "TBODY"){
                        return oTbodyArr[j];
                    }
                }
            }
        }
    }
}

function generateErrorMessages(errorMessages, displayMessagesOnParent, oasisMsgElements) {
    var errorMsgArray = errorMessages.messageArray;
    var handleErrorFunctionName = "";
    if(displayMessagesOnParent && isDefined(displayMessagesOnParent)){
        handleErrorFunctionName = "handleErrorForFrame";
    }else{
        handleErrorFunctionName = "handleError";
    }
    for(var i = 0; i< errorMsgArray.length; i++){
        var oTr = document.createElement("tr");
        var oTd = document.createElement("td");
        oTd.setAttribute("colspan", "2");
        oTd.setAttribute("class", errorMessages.messageStyleClass);
        var textStr;
        if((errorMsgArray[i].messageFieldId != null && errorMsgArray[i].messageFieldId.length > 0) ||
                (errorMsgArray[i].messageRowId != null && errorMsgArray[i].messageRowId.length > 0)){
            textStr = errorMsgArray[i].message +
                    "<a href='#' onclick='javascript:" + handleErrorFunctionName + "(\"\",\"" + errorMsgArray[i].messageFieldId + "\",\"" +
                    errorMsgArray[i].messageRowId + "\",\"" + errorMsgArray[i].messageGridId +
                    "\");' class='txtSmallBlue'> Goto Error</a>";
            if(i == 0){
                if(!(displayMessagesOnParent && isDefined(displayMessagesOnParent))){
                    textStr += "\n<script type='text/javascript'>" +
                            "\nvalidateFieldId = '" + errorMsgArray[i].messageFieldId + "';" +
                            "\nvalidateRowId = '" + errorMsgArray[i].messageRowId + "';";
                    if(isDefined(errorMsgArray[i].messageGridId) && (errorMsgArray[i].messageGridId != "")){
                        textStr += "\nvalidateFieldId = '" + errorMsgArray[i].messageGridId + "';";
                    }
                    textStr += "\n</script>\n";
                }
            }
        }else{
            if(displayMessagesOnParent && isDefined(displayMessagesOnParent)){
                var functionExists = eval("window.divPopupExists");
                var lowerTextStr = (errorMsgArray[i].message).toLowerCase();
                if (functionExists && divPopupExists()) {
                    textStr = errorMsgArray[i].message;
                }else if(new RegExp("^no.*found.*$").test(lowerTextStr) || new RegExp("^no.*data$").test(lowerTextStr)
                        || new RegExp("^there.*no.*$").test(lowerTextStr) || new RegExp("^no data.*$").test(lowerTextStr)){
                    continue;
                }else{
                    textStr = errorMsgArray[i].message;
                }
            }else{
                textStr = errorMsgArray[i].message;
            }
        }
        oTd.innerHTML = textStr;
        oTr.appendChild(oTd);
        oasisMsgElements.push(oTr);
    }
}

function generateWarningMessages(warningMessages, oasisMsgElements) {
    _generateInfoWarningMessages(warningMessages, oasisMsgElements);
}

function generateInformationMessages(informationMessages, oasisMsgElements) {
    _generateInfoWarningMessages(informationMessages, oasisMsgElements);
}

function _generateInfoWarningMessages(infoWarningMessages, oasisMsgElements) {
    var infoWarningMsgArray = infoWarningMessages.messageArray;
    for(var i = 0; i< infoWarningMsgArray.length; i++){
        var oTr = document.createElement("tr");
        var oTd = document.createElement("td");
        oTd.setAttribute("colspan", "2");
        oTd.setAttribute("class", infoWarningMessages.messageStyleClass);
        oTd.innerHTML = infoWarningMsgArray[i].message;
        oTr.appendChild(oTd);
        oasisMsgElements.push(oTr);
    }
}

function generateConfirmationMessages(confirmationMessages, oasisMsgElements) {
    var confirmationMsgArray = confirmationMessages.messageArray;
    for(var i = 0; i< confirmationMsgArray.length; i++){
        var oTr = document.createElement("tr");
        var oTd1 = document.createElement("td");
        oTd1.setAttribute("class", confirmationMessages.messageStyleClass);
        oTd1.innerHTML = confirmationMsgArray[i].message;
        oTr.appendChild(oTd1);
        var oTd2 = document.createElement("td");
        oTd2.setAttribute("align", "left");
        var textStr = "<select name='" + confirmationMsgArray[i].messageKey + ".confirmed'>";
        if($.inArray(confirmationMsgArray[i].defaultConfirmationValue, ["null", "Y", ""]) > -1){
            textStr += "<option value='Y' selected='selected'>Yes</option>" +
                            "<option value='N'>No</option>";
        }else{
            textStr += "<option value='Y'>Yes</option>" +
                            "<option value='N' selected='selected'>No</option>";
        }
        textStr += "</select>";
        oTd2.innerHTML = textStr;
        oTr.appendChild(oTd2);
        oasisMsgElements.push(oTr);
    }
}

function appendMessages(oasisMsgElements, displayMessagesOnParent, parentElement){
    var oTable;
    if(parentElement && parentElement != undefined){
        oTable = parentElement;
    }else{
        oTable = getMsgTblNode(displayMessagesOnParent);
    }

    for(var i = 0; i < oasisMsgElements.length; i++){
        oTable.appendChild(oasisMsgElements[i]);
    }
    return oTable;
}

/*
 Remove messages added from Iframe(included in div 'oasisMessageForFrameBody') if when the user leaves the tab successfully.
 */
function removeMessagesForFrame() {
    _removeMessagesByObjectId("oasisMessageForFrameBody");
}

function removeAllMessages(){
    removeMessagesForFrame();
    removeMessages();
}

/*
 Remove messages from the page if there is no need to submit form when user clicking the Save button.
 It will be called by commonOnSubmit() function.
 */
function removeMessages() {
    var functionExists = eval("window.commonOnRemoveMessages");
    if(functionExists){
        commonOnRemoveMessages();
    }

    _removeMessagesByObjectId("oasisMessageBody");
}

function clearMessage(messageElements) {
    var msgCount = messageElements.length;
    if (msgCount > 0) {
        for (var i = 0; i < msgCount; i++) {
            while(messageElements[i].lastChild){
                messageElements[i].removeChild(messageElements[i].lastChild);
            }
        }
    }
}

function _removeMessagesByObjectId(divId) {
    if(hasObject(divId)){
        var msgTables = getObjectById(divId).querySelectorAll("#" + divId + ">table");
        clearMessage(msgTables);
    }
}

function addElementEventListener(element, eventName, callBack) {
    var upperEventName = dti.oasis.string.isEmpty(eventName) ? "" : eventName.toUpperCase();
    if (dti.oasis.string.startsWith(upperEventName, "ON")) {
        eventName = eventName.substring(2);
    }
    dti.oasis.ui.addEventListenerToElements(element, eventName, callBack);
}

function removeElementEventListener(element, eventName, callBack) {
    var upperEventName = dti.oasis.string.isEmpty(eventName) ? "" : eventName.toUpperCase();
    if (dti.oasis.string.startsWith(upperEventName, "ON")) {
        eventName = eventName.substring(2);
    }
    dti.oasis.ui.removeEventListenerToElements(element, eventName, callBack);
}

function dispatchElementEvent(eventTarget, eventName) {
    dti.oasis.ui.dispatchEvent(eventTarget, eventName);
}

/**
 * Check whether the element has a hidden style without checking the visibility of its ancestor elements.
 *
 * @param jQueryObj
 * @returns {boolean}
 */
function hasHiddenStyle(jQueryObj) {
    return jQueryObj.css("display") === "none" || jQueryObj.css("visibility") === "hidden";
}


/**
 * Fix event to support firefox.
 *
 * @param evt The original event.
 * @returns {*} The wrapped event which supports firefox.
 */
function fixEvent(evt) {
    evt = evt || window.event;

    if (!(evt instanceof Event) && typeof evt.isCustomEvent == "undefined") {
        evt = window.event;
    }

        if (window["useJqxGrid"]) {
        // If the current page uses jqxGrid, fix the event to support event API of IE8 mode.

        if (evt.isCustomEvent) {
            // If it's already a custom event, return the current event.
            return evt;
        }

        var event = {
            _protected: {
                _isCustomEvent: true,
                _originalEvent: evt || window.event,
                _isDefaultPrevented: false,
                _isPropagationStopped: false
            },

            preventDefault: function () {
                this._protected._isDefaultPrevented = true;

                if (this._protected._originalEvent.preventDefault) {
                    this._protected._originalEvent.preventDefault();
                } else {
                    // IE
                    this._protected._originalEvent.returnValue = false;
                }
            },

            stopPropagation: function () {
                this._protected._isPropagationStopped = true;

                if (this._protected._originalEvent.stopPropagation) {
                    this._protected._originalEvent.stopPropagation();
                }

                // IE
                this._protected._originalEvent.cancelBubble = true;
            }
        };

        Object.defineProperty(event, "isCustomEvent", {
            get: function () {
                return this._protected._isCustomEvent;
            }
        });

        Object.defineProperty(event, "type", {
            get: function () {
                return this._protected._originalEvent.type;
            }
        });

        Object.defineProperty(event, "defaultPrevented", {
            get: function () {
                return this._protected._isDefaultPrevented;
            }
        });

        Object.defineProperty(event, "propagationStopped", {
            get: function () {
                return this._protected._isPropagationStopped;
            }
        });

        Object.defineProperty(event, "returnValue", {
            get: function () {
                if (this.type == "beforeunload") {
                    return this._protected._originalEvent.returnValue;
                } else {
                    return !this.defaultPrevented;
                }
            },
            set: function (val) {
                if (this.type == "beforeunload") {
                    if (val) {
                        // The return value of beforeunload is the message for confirm leave page.
                        this._protected._originalEvent.returnValue = val;
                    }
                } else if (val === false) {
                    // Only call prevent default if the return vale is false.
                    this.preventDefault();
                }
            }
        });

        Object.defineProperty(event, "cancelBubble", {
            get: function () {
                return this.propagationStopped;
            },
            set: function (val) {
                if (val) {
                    this.stopPropagation();
                }
            }
        });

        Object.defineProperty(event, "srcElement", {
            get: function () {
                return this.target;
            }
        });

        Object.defineProperty(event, "target", {
            get: function () {
                return this._protected._originalEvent.srcElement || this._protected._originalEvent.target;
            }
        });

        Object.defineProperty(event, "currentTarget", {
            get: function () {
                return this._protected._originalEvent.currentTarget;
            }
        });

        Object.defineProperty(event, "propertyName", {
            get: function () {
                return this._protected._originalEvent.propertyName;
            }
        });

        // Key event.
        Object.defineProperty(event, "keyCode", {
            get: function () {
                if (this._protected._originalEvent.charCode) {
                    return this._protected._originalEvent.charCode;
                } else {
                    return this._protected._originalEvent.keyCode;
                }

            },
            set: function (keyCode) {
                if (this._protected._originalEvent.charCode) {
                    this._protected._originalEvent.charCode = keyCode;
                } else {
                    this._protected._originalEvent.keyCode = keyCode;
                }
            }
        });

        Object.defineProperty(event, "altKey", {
            get: function () {
                return this._protected._originalEvent.altKey;
            }
        });

        Object.defineProperty(event, "shiftKey", {
            get: function () {
                return this._protected._originalEvent.shiftKey;
            }
        });

        Object.defineProperty(event, "ctrlKey", {
            get: function () {
                return this._protected._originalEvent.ctrlKey;
            }
        });

        // Mouse event
        Object.defineProperty(event, "clientX", {
            get: function () {
                return this._protected._originalEvent.clientX;
            }
        });

        Object.defineProperty(event, "clientY", {
            get: function () {
                return this._protected._originalEvent.clientY;
            }
        });

        Object.defineProperty(event, "pageX", {
            get: function () {
                return this._protected._originalEvent.pageX;
            }
        });

        Object.defineProperty(event, "pageY", {
            get: function () {
                return this._protected._originalEvent.pageY;
            }
        });

        Object.defineProperty(event, "offsetX", {
            get: function () {
                return this._protected._originalEvent.offsetX;
            }
        });

        Object.defineProperty(event, "offsetY", {
            get: function () {
                return this._protected._originalEvent.offsetY;
            }
        });

        Object.defineProperty(event, "layerX", {
            get: function () {
                return this._protected._originalEvent.layerX;
            }
        });

        Object.defineProperty(event, "layerY", {
            get: function () {
                return this._protected._originalEvent.layerY;
            }
        });

        Object.defineProperty(event, "button", {
            get: function () {
                return this._protected._originalEvent.button;
            }
        });

        return event;
    } else {
        // If the current page doesn't use jqxGrid, return the event object. If the event is null, return window.event.
        return evt;
    }
}
