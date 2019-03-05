/*
* Revision Date    Revised By  Description
* ----------------------------------------------------------------------------
* 08/11/2010       wfu         109874: Changed to handle url encoding
* 08/12/2010       wfu         109874: Changed function name to urlEncode
* 08/24/2010       dzhang      110306: Change the openLookupDivPopup function attachEvent&detachEvent's event from 'onmousedown' to 'onclick'.
* 10/19/2010       gzeng       112909 - Set isScrolling to true by default
* 12/21/2010       wfu         issue 115931 - Added logic to capture/release mouse event.
* 11/08/2011       bhong       112837 - Added logics to support standardize processing dialog.
* 11/10/2011       bhong       112837 - Set timeout to 0 in showProcessingDivPopup and showProcessingImgIndicatorin to solve possible timing problems
* 11/21/2011       bhong       127408 - Removed timeout functions in "showProcessingDivPopup" and "showProcessingImgIndicatorin" to solve timing problem
*                              Implemented different solution to solve the problem that animated GIF is not animating.
* 06/01/2012       kshen       124505. Fixed resizeByDivPopup.
* 06/06/2012       kshen       124505. Updated for renaming method getSubElementByClassName to getChildElementByClassName.
* 06/13/2012       kshen       124505. Rename varibles for code review.
* 05/07/2013       jxgu        140985. Use JQuery Dialog.
* 04/18/2014       wkong       153396. When click the close button, if the popup page is changed will warning a message.
* 05/23/2017       cesar       185197 - Modified popupMultiSelectDiv() function to pass the popup height to be display according to browser's height.
* 05/23/2017       ddai        185197. Change document.body.clientHeight to document.documentElement.clientHeight to get the real height of the window.
* 06/23/2017       cesar       186364 - revert back #185197. Only the changes by cesar on popupMultiSelectDiv(). Problem occurs when popHeight is negative.
* 07/24/2017       eyin        185377. Modified showProgressIndicator(), for Tab style in PM, if processing running is
*                              displaying on parent screen already, then processing running will be not needed on iFrame screen.
* 09/11/2017       kshen       Grid replacement. Changed to not open popup in DIV if the current page and child page use
*                              jqxGrid or don't use jqxGrid in the same time. Because the current Oasis runs in IE8 mode,
*                              jqxGrid requires HTML5 mode, and a page in iframe (div popup) will use the same document
*                              with the parent frame
* 10/12/2017       kshen       Grid replacement. Changed checkLookupDivPopup to support firefox.
* 12/12/2017       kshen       Grid replacement. Added isInWindowPopup to check if the current window is a popup window opened by window.open.
*                              Changed closeThisDivPopup to handle both div popup and window popup.
*                              Changed reAdjustIFrameSize to enforce only do resize for real div popup.
* 12/28/2017       kshen       Grid replacement. Changed to open a popup in div popup if the popup page supports both IE8 and ECS5.
* 05/16/2018       htwang      192558 - Modified hideShowCovered() to call it only for IE and Opera browsers
* 07/11/2018       cesar       193446 - 1) created dti.divpopuputils() to clear the src for iframe for csrf implementation.
*                                       2) modified openDivPopup() to add the token page.
*                                       3) modified closeDiv() to check for undefined popupDiv
* 07/18/2018        wreeder     194551 Update openLookupDivPopup() to display the popup no taller than 200px
* ----------------------------------------------------------------------------
*/

var popupIdx = 0;
var moveState = 0;
var currentXLoc = 0;
var currentYLoc = 0;
var selectedPopup = "";
var lastFocuszIndex = 5000;
var activeNoOfPopups = 0;
var activeNoOfModelPopups = 0;

var useJQueryDialog = true;

/**
 * @param popupTitle
 * @param urlToOpen
 * @param isModal
 * @param isDragable
 * @param popupTop      This parameter is ignored. Instead popupAlignment is used to align the dialog
 * @param popupLeft     This parameter is ignored. Instead popupAlignment is used to align the dialog
 * @param popupWidth
 * @param popupHeight
 * @param contentWidth
 * @param contentHeight
 * @param popupId
 * @param isShowCloseLink
 * @param popupAlignment
 * @param startInactive
 * @param isScrolling
 */
function openDivPopup(popupTitle, urlToOpen, isModal, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling) {
    // Check if the parent current window and the popup window will use jqxGrid or not use jqxGrid in the same time.
    // If not, use window.open to open a popup because a div popup (iframe) will use the same document mode with the parent window.
    var openInNewWindow = false;

    urlToOpen = dti.csrf.setupCSRFTokenForUrl(urlToOpen);

    if (!isCompatiblePopup(urlToOpen)) {
        var popupUseJqxGrid = checkIfPopupUseJqxGrid(urlToOpen);

        if ((typeof useJqxGrid != "undefined" && useJqxGrid && popupUseJqxGrid == "N") ||
            ((typeof useJqxGrid == "undefined" || !useJqxGrid) && popupUseJqxGrid == "Y")) {
            openInNewWindow = true;
        }
    }

    if (openInNewWindow) {
        return doOpenWindowPopup(popupTitle, urlToOpen, isModal, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling);
    } else {
        return doOpenDivPopup(popupTitle, urlToOpen, isModal, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling);
    }
}

/**
 * Check if the popup page support cross-browser.
 * @param popupUrl
 * @returns {boolean}
 */
function isCompatiblePopup(popupUrl) {
    // HTML files are changed to support both IE8 and IE11.
    // If the url ends with .html, or contains .html?, or contains .html#, it's an html page.
    // The HTML page list: note.html, jumpToOptionList.html
    return new RegExp("\\.html$").test(popupUrl) || popupUrl.indexOf(".html?") > -1 || popupUrl.indexOf(".html#") > -1;
}

function checkIfPopupUseJqxGrid(popupUrl) {
    var useJqxGrid = "N";

    if (popupUrl.indexOf(".do") > 0) {
        var checkIfPopupUseJqxGridUrl = popupUrl;
        if (checkIfPopupUseJqxGridUrl.indexOf("?") > 0) {
            checkIfPopupUseJqxGridUrl += "&checkIfPageUseJqxGrid=Y";
        } else {
            checkIfPopupUseJqxGridUrl += "?checkIfPageUseJqxGrid=Y";
        }

        new AJAXRequest("get", checkIfPopupUseJqxGridUrl, '', function (ajax) {
            if (ajax.readyState == 4) {
                if (ajax.status == 200) {
                    var data = ajax.responseXML;
                    if (!handleAjaxMessages(data, null)) {
                        return useJqxGrid;
                    }

                    var oValueList = parseXML(data);
                    if (oValueList.length > 0) {
                        useJqxGrid = oValueList[0]["useJqxGrid"];
                    }
                }
            }
        }, false);
    }

    return useJqxGrid;
}

function doOpenDivPopup(popupTitle, urlToOpen, isModal, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling)
{
    if (useJQueryDialog){
        return openJQueryDialog(popupTitle, urlToOpen, isModal, isDragable, popupWidth, popupHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling);
    }

    //    alert("in openDivPopup for \"" + popupTitle + "\" with url: " + urlToOpen);
    var popupTitleHeight = 22;
    var isUrlHTMLContent = false;

    popupIdx++;

    if (!startInactive) {
        activeNoOfPopups++;
    }

    if (isModal) {
        if (!startInactive) {
            activeNoOfModelPopups++;
        }
        disableall();
        hideDropDownListControls();
    }
    else if (!startInactive) {
        hideDropDownListControls();
    }

    if (!urlToOpen) {
        alert("Missing URL Information to loaded. Cannot proceed.")
        return;
    }

    if (!(urlToOpen.substring(0, 1) == "/" || urlToOpen.substring(0, 2) == "./" || urlToOpen.substring(0, 7) == "http://" || urlToOpen.substring(0, 8) == "https://"))
        isUrlHTMLContent = true;

    if (!popupTop) {
        popupTop = 50;
    }
    if (!popupLeft) {
        popupLeft = 150;
    }

    if (!popupWidth) {
        popupWidth = 700;
    }
    if (!popupHeight) {
        popupHeight = 600;
    }

    if (!popupAlignment) {
        popupAlignment = "center";
    }

    /*
        switch(popupAlignment) {
          case "center" :
              popupTop = (document.body.clientHeight/2) - (popupHeight/2);
              if(popupTop<0) {
                  popupTop = 0;
              }
              popupLeft = (document.body.clientWidth/2) - (popupWidth/2);
              if(popupLeft<0) {
                  popupLeft = 0;
              }
              //alert(self.screen.height + "/" + self.screen.width + "/" + document.body.clientHeight + "/" + document.body.clientWidth + "/" + popupTop + "/" + popupLeft)
              break;
          case "left" :
              popupLeft = window.screenLeft;
              break;
          case "top" :
              popupTop = 0;
              break;
          case "topleft" :
              popupTop = 0;
              popupLeft = window.screenLeft;
              break;
          case "topright" :
              popupTop = 0;
              popupLeft = document.body.clientWidth - popupWidth;
              break;
        }
    //    alert("popupAlignment:"+popupAlignment+"popupTop:"+popupTop+"popupLeft:"+popupLeft+"popupHeight:"+popupHeight+"popupWidth:"+popupWidth);
    */

    if (!contentWidth) {
        contentWidth = "100%";
    }
    if (!contentHeight) {
        contentHeight = "100%";
    }
    if (!popupId) {
        popupId = "newpopup" + popupIdx;
    }
    if (isShowCloseLink == null) {
        isShowCloseLink = true;
    }

    lastFocuszIndex = lastFocuszIndex + 1;

    var thisbody = document.getElementsByTagName("body")[0];

    var popup = document.createElement("div");
    popup.id = popupId;
    popup.className = "popup";
    popup.style.display = startInactive ? "none" : "block";
    popup.style.position = "absolute";
    //popup.style.border = "1px solid black";
    popup.style.top = popupTop;
    popup.style.left = popupLeft;
    popup.style.width = popupWidth;
    popup.style.height = popupHeight;
    popup.style.overflow = "auto";
    popup.style.zIndex = lastFocuszIndex;
    //popup.style.zIndex = noOfPopups * 2;

    var isModalSpan = document.createElement("span");
    isModalSpan.id = "isModal";
    isModalSpan.innerText = (isModal ? "Y" : "N");
    isModalSpan.style.display = "none";
    popup.appendChild(isModalSpan);

    var popuptbl = document.createElement("table");
    popuptbl.style.height = "100%";
    popuptbl.style.width = "100%";
    popuptbl.cellPadding = "0";
    popuptbl.cellSpacing = "0";

    var popuptblbody = document.createElement("tbody");
    if (popupTitle != null) {
        var headertr = document.createElement("tr");
        headertr.id = "popupheader" + popupIdx;
        //headertr.style.backgroundColor = "#669999";
        //headertr.style.height = popupTitleHeight;

        var headertd = document.createElement("td");
        /*headertd.style.width = "100%";
        headertd.style.color = "white";
        headertd.style.fontWeight = "bold";
        headertd.style.borderBottom = "solid 1px black";
        headertd.style.backgroundImage = "url(" + getAppPath() + "/core/images/hdr.gif)";
    */
        var headercontenttbl = document.createElement("table");
        var headercontenttbody = document.createElement("tbody");
        var headercontenttr = document.createElement("tr");
        var headercontenttd1 = document.createElement("td");
        var headercontenttd2 = document.createElement("td");

        /*headercontenttr.style.color = "white"
        headercontenttr.style.fontWeight = "Bold"
    */
        headercontenttbl.id = "header" + popupIdx;
        headercontenttbl.style.width = "100%";
        headercontenttbl.style.height = "100%";
        headercontenttbl.cellPadding = "0";
        headercontenttbl.cellSpacing = "0";

        headercontenttd1.style.width = "95%";
        headercontenttd1.className = "popupheader";
        //headercontenttd1.style.color = "white"
        if (isDragable) {
            // commentted out by Joe, to support move the div popup. 03/21/2008
            //            if (isModal) {
            //                headercontenttd1.setAttribute("onselectstart", "javascript:popupSelectStart('" + popupId + "');");
            //                headercontenttd1.setAttribute("onmousedown", "javascript:popupMouseDown('" + popupId + "');");
            //                headercontenttd1.setAttribute("onmouseup", "javascript:popupMouseUp('" + popupId + "');");
            //            }
            //            else {
            headercontenttd1.onselectstart = function() {
                popupSelectStart(popup.id);
            };
            headercontenttd1.onmousedown = function() {
                popupMouseDown(popup.id);
            };
            headercontenttd1.onmouseup = function() {
                popupMouseUp();
            };
            //            }
            headercontenttd1.style.cursor = "move";
        }

        headercontenttd2.style.width = "5%";
        headercontenttd2.style.align = "right";
        headercontenttd2.className = "popupheader";
        if (isShowCloseLink) {
            headercontenttd2.onmousedown = "javascript:closeDiv(" + popupId + ");"
        }

        //headercontenttd2.style.backgroundColor = "red";

        var headertext = document.createTextNode(popupTitle);
        // If the popup tile is empty, assign " " as it's data to display title bar correctly.
        if (popupTitle == "") {
            headertext.data = " ";
        }
        //headertext.style.color = "white";

        if (isShowCloseLink) {
            var aclose = document.createElement('a');
            //aclose.style.backgroundColor = "red";
            //    aclose.style.width = "100%";
            //    aclose.style.align = "right";
            aclose.setAttribute('href', "javascript:closeDiv(" + popupId + ");");

            aclose.onclick = function() {
                skipUnloadPageForCurrentAndIFrame();
            };
            aclose.className = "popupheader";
            //aclose.setAttribute('href',"http://www.hotmail.com");
            var closeText = document.createTextNode("Close");
            aclose.appendChild(closeText);
            headercontenttd2.appendChild(aclose);
        }
        else {
            headercontenttd2.innerHTML = "&nbsp;";
            if (isDragable) {
                headercontenttd2.style.cursor = "move";
            }
        }

        headercontenttd1.appendChild(headertext);
        headercontenttr.appendChild(headercontenttd1);
        headercontenttr.appendChild(headercontenttd2);
        headercontenttbody.appendChild(headercontenttr);
        headercontenttbl.appendChild(headercontenttbody);

        //headertd.appendChild(headertext);
        headertd.appendChild(headercontenttbl);
        headertr.appendChild(headertd);
        popuptblbody.appendChild(headertr);
    }

    var contentareatr = document.createElement("tr");

    if (isDragable) {
        contentareatr.onmousemove = "javascript:popupMouseMove();";
        contentareatr.onmouseup = "javascript:popupMouseUp();";
        /*
                contentareatr.onmousemove = function() {
                    popupMouseMove();
                };
                contentareatr.onmouseup = function() {
                    popupMouseUp();
                };
        */
    }

    var contentareatd = document.createElement("td");
    contentareatd.style.width = "100%";
    contentareatd.style.height = "100%";

    var contenttbl = document.createElement("table");
    contenttbl.style.height = "100%";
    contenttbl.style.width = "100%";
    contenttbl.cellPadding = "0";
    contenttbl.cellSpacing = "0";
    contenttbl.className = "popupcontent"
    /*
        contenttbl.style.border = "0"
        contenttbl.style.backgroundColor = "#669999";
        contenttbl.style.backgroundImage = "url(" + getAppPath() + "/core/images/lines.gif)";
    */

    var contenttblbody = document.createElement("tbody");
    var contenttr = document.createElement("tr");
    var contenttd = document.createElement("td");
    contenttd.align = "center"
    contenttd.style.verticalAlign = "middle"

    //contenttd.innerHTML="<b> Popup content area for popup" + noOfPopups + "</b>";
    //contenttd.innerHTML  = getPopupContent(urlToOpen);

    var contentdiv = document.createElement("div");
    if (popupTitle && popupTitle != "") {
        contentdiv.style.width = popupWidth - 52;
        contentdiv.style.height = popupHeight - popupTitleHeight - 52;
    }
    else {
        // Subtract 3px for popup border.
        /*
                contentdiv.style.width = popupWidth - 3;
                contentdiv.style.height = popupHeight - 3;
        */
        contentdiv.style.width = "100%";
        contentdiv.style.height = "100%";
    }
    contentdiv.style.overflow = "auto";
    //    contentdiv.style.border = "1px solid black";
    contentdiv.className = "popupcontent"

    if (isUrlHTMLContent == false) {

        // For Chinese character parameter values support
        urlToOpen = urlEncode(urlToOpen);

        if (urlToOpen.indexOf("?") > 0 && urlToOpen.substring(urlToOpen.length - 1, 1) != "?") {
            urlToOpen += "&isPopupPage=Y"
        } else {
            if (urlToOpen.indexOf("?") > 0) {
                urlToOpen += "isPopupPage=Y"
            } else {
                urlToOpen += "?isPopupPage=Y"              
            }
        }

        var popupframe = document.createElement("iframe")
        popupframe.id = "popupframe" + popupIdx;
        popupframe.name = "popupframe" + popupIdx;
        popupframe.width = contentWidth;
        popupframe.height = contentHeight;
        popupframe.src = urlToOpen;
        popupframe.frameBorder = "no";

        if (popupTitle != "" &&
            (urlToOpen.toUpperCase().indexOf(".DO") > 0 || urlToOpen.toUpperCase().indexOf(".JSP") > 0)) {
            //The JSP pages do not load at the 0,0 co-ordinate in the frame. So, need manual adjustment.
            //Commented for now to analysis the loading location.
            /*
                        popupframe.vspace = "-10";
                        popupframe.hspace = "-10";
            */
        }

        //Issue#112909: set isScrolling to true in case of long names.
        popupframe.scrolling = "yes";
        if (isScrolling != undefined) {
            if (isScrolling == false) {
                popupframe.scrolling = "no";
            }
        }


        popupframe.style.overflowX = "auto";
        popupframe.style.overflowY = "auto";

        contentdiv.appendChild(popupframe)

        if (eval(window.baseOnLoadForDivPopup)) {
            baseOnLoadForDivPopup(popupframe)
        }

    }
    else {
        contentdiv.innerHTML = urlToOpen;
    }

    contenttd.appendChild(contentdiv);
    contenttr.appendChild(contenttd);
    contenttblbody.appendChild(contenttr);
    contenttbl.appendChild(contenttblbody);

    contentareatd.appendChild(contenttbl);
    contentareatr.appendChild(contentareatd);

    popuptblbody.appendChild(contentareatr);

    popuptbl.appendChild(popuptblbody);

    popup.appendChild(popuptbl);
    alignPopup(popup, popupAlignment);

    thisbody.appendChild(popup);

    // Must set focus back to "parent" window, else it will always have focus on the element which opens this div.
    // For example: when select any option of Policy Actions select field, an popup window is displayed, if not set focus
    // on its parent window, the focus will always be on the select field, if user scrolls the mouse, another option of it
    // will be checked and another popup page will be displayed. Joe 1/29/2008
    try {
        thisbody.setActive();
    } catch (e) {
        // do nothing.
    }

    return popup.id;
}

function doOpenWindowPopup(popupTitle, urlToOpen, isModal, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling) {
    var isUrlHTMLContent = false;

    if (!(urlToOpen.substring(0, 1) == "/" || urlToOpen.substring(0, 2) == "./" || urlToOpen.substring(0, 7) == "http://" || urlToOpen.substring(0, 8) == "https://"))
        isUrlHTMLContent = true;

    if (isUrlHTMLContent == false) {
        // For Chinese character parameter values support
        urlToOpen = urlEncode(urlToOpen);

        if (urlToOpen.indexOf("?") > 0 && urlToOpen.substring(urlToOpen.length - 1, 1) != "?") {
            urlToOpen += "&isPopupPage=Y"
        } else {
            if (urlToOpen.indexOf("?") > 0) {
                urlToOpen += "isPopupPage=Y"
            } else {
                urlToOpen += "?isPopupPage=Y"
            }
        }
    }

    if (!popupWidth) {
        popupWidth = 700;
    }

    if (popupWidth > $(window).width() - 40){
        popupWidth = $(window).width() - 40;
    }
    if (!popupHeight) {
        popupHeight = 600;
    }

    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var windowWidth = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var windowHeight = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = ((windowWidth / 2) - (popupWidth / 2)) + dualScreenLeft;
    var top = ((windowHeight / 2) - (popupHeight / 2)) + dualScreenTop;

    popupIdx++;
    if (!popupId) {
        popupId = "newpopup" + popupIdx;
    }

    return openPopup(urlToOpen, popupId, popupWidth, popupHeight, top, left);
}

function resizeByDivPopup(divPopup, width, height) {

    if (useJQueryDialog){
        resizeJQueryDialog(divPopup, width, height);
        return;
    }

    /*
     The divPopup is rendered as-

     <div>  --- divPopup DIV
         <table>
            <tr>    --- header, rendered only if the popupTitle is not null and non-empty string.
                <td><<Popupup Title>></td>
            </tr>
            <tr>
                <td>
                    <table>
                        <tr>
                            <td>
                                <div>   --- divPopupContent DIV
                                    <iframe />   --- iFrame that loads the popup page.
                                <div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
     </div>

     If the popup title bar is not rendered (it doesn't get rendered when the popupTitle is passed as null value
     or if the popupTitle is passed as "" (empty) string),  the divPopupContent DIV width and height dimensions
     are set to 100%. In such cases, the outermost divPopup width and height must be changed directly as per the
     resize configuration.

     If the popup title bar is rendered with some popupTitle,  then the divPopupContent width and height is set
     to the outermost divPopup's width and height - 52. In such cases, the divPopupContent width and height must
     be changed as per the resize configuration. This will automatically increase the divPopup dimension, if the
     requested resize dimension is greater than the original divPopup dimension.

     The contentWidth and contentHeight has a default value of 100%, if its corresponding value is not passed
     while rendering the divPopup. In such cases, leave the iFrame dimension as is and enable the scrolling
     - because it is 100% of the divPopupContent and divPopupContent dimensions gets changed anyway as per the
     requested resize dimension.

     The contentWidth and contentHeight are passed while rendering the divPopup, adjust the iFrame dimension as
     per the resize configuration and enable the scrolling (only if the original dimension is greater than the
     resized dimension - because, the original iFrame was rendered bigger than what has been requested in resize
     configuration. So in order to show all the content correctly without cutting it off,  enable the scrolling
     attribute for the iFrame element).
     */

    if (divPopup) {
        var iFrame = getPopupFrameFromPopupDiv(divPopup);
        var isAddIFrameScrolling  = false;
        var divPopupContentDiv = getDivPopupContentDiv(divPopup);

        if (width) {
            var divPopupOrigWidth = parseInt(divPopup.style.width.toLowerCase().replace("px","").replace("pt",""));

            if (iFrame.width.length > 0) {
                if (iFrame.width.substring(iFrame.width.length-1) == "%") {
                    iFrame.width = iFrame.width ; //reset the percentage, so that it fills accordingly based on new width.
                    isAddIFrameScrolling = true;
                } else {
                    var iFrameOrigWidth = parseInt(iFrame.width.toLowerCase().replace("px","").replace("pt",""));
                    var widthDelta = parseInt(width) - divPopupOrigWidth;

                    iFrame.width = iFrameOrigWidth + widthDelta;
                    if (iFrameOrigWidth > (iFrameOrigWidth + widthDelta)) {
                        isAddIFrameScrolling = true;
                    }
                }
            }

            if (divPopupOrigWidth != width) {
                if (divPopupContentDiv.style.width.length > 0) {
                    if (divPopupContentDiv.style.width.substring(divPopupContentDiv.style.width.length - 1) != "%") {
                        var divPopupContentOrigWidth = parseInt(divPopupContentDiv.style.width.toLowerCase().replace("px","").replace("pt",""));
                        divPopupContentDiv.style.width = divPopupContentOrigWidth + parseInt(width) - divPopupOrigWidth;
                    }
                }
            }

            divPopup.style.width = width;
        }
        if (height) {
            var divPopupOrigHeight = parseInt(divPopup.style.height.toLowerCase().replace("px","").replace("pt",""));

            if (iFrame.height.length>1) {
                if (iFrame.height.substring(iFrame.height.length-1) == "%") {
                    iFrame.height = iFrame.height ; //reset the percentage, so that it fills accordingly based on new height.
                    isAddIFrameScrolling = true;
                } else {
                    var iFrameOrigHeight = parseInt(iFrame.height.toLowerCase().replace("px","").replace("pt",""));
                    var heightDelta = parseInt(height) - divPopupOrigHeight;
                    iFrame.height = iFrameOrigHeight + heightDelta;
                    if (iFrameOrigHeight > (iFrameOrigHeight + heightDelta)) {
                        isAddIFrameScrolling = true;
                    }
                }
            }

            if (divPopupOrigHeight != height) {
                if (divPopupContentDiv.style.height.length > 0) {
                    if (divPopupContentDiv.style.height.substring(divPopupContentDiv.style.height.length - 1) != "%") {
                        var divPopupContentOrigHeight = parseInt(divPopupContentDiv.style.height.toLowerCase().replace("px","").replace("pt",""));
                        divPopupContentDiv.style.height = divPopupContentOrigHeight + parseInt(height) - divPopupOrigHeight;
                    }
                }
            }

            divPopup.style.height = height;
        }
        if (isAddIFrameScrolling) {
            if (iFrame.scrolling != "yes") {
                iFrame.scrolling = "yes";
                // Reload iFrame with new attributes
                iFrame.outerHTML = iFrame.outerHTML;
            }
        }
        if (width || height) {
            alignPopup(divPopup, null);
        }
    }
}

function moveToDivPopup(divPopup, x, y) {
    if (useJQueryDialog){
        moveJQueryDialog(divPopup, x, y);
        return;
    }
    if (divPopup) {
        if (y) {
            divPopup.style.top = y
        }
        if (x) {
            divPopup.style.left = x;
        }
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function alignPopup(popupCtrl, popupAlignment) {

    var scrollPosX, scrollPosY;
    if (self.pageYOffset) // all except Explorer
    {
        scrollPosX = self.pageXOffset;
        scrollPosY = self.pageYOffset;
    }
    else if (document.documentElement && document.documentElement.scrollTop)
    // Explorer 6 Strict
    {
        scrollPosX = document.documentElement.scrollLeft;
        scrollPosY = document.documentElement.scrollTop;
    }
    else if (document.body) // all other Explorers
    {
        scrollPosX = document.body.scrollLeft;
        scrollPosY = document.body.scrollTop;
    }

    var documentX, documentY;
    if (self.innerHeight) // all except Explorer
    {
        documentX = self.innerWidth;
        documentY = self.innerHeight;
    }
    else if (document.documentElement && document.documentElement.clientHeight)
    // Explorer 6 Strict Mode
    {
        documentX = document.documentElement.clientWidth;
        documentY = document.documentElement.clientHeight - 30;
    }
    else if (document.body) // other Explorers
    {
        documentX = document.body.clientWidth;
        documentY = document.body.clientHeight - 30;
    }

    if (!popupAlignment) {
        if (popupCtrl.getAttribute("popupAlignment")) {
            popupAlignment = popupCtrl.getAttribute("popupAlignment");
        }
    }

    var popupTop = 0;
    var popupLeft = 0;
    switch (popupAlignment) {
        case "center" :
            popupTop = (documentY - parseInt(popupCtrl.style.height)) / 2;
            if (popupTop < 0) {
                popupTop = 0;
            }
            popupLeft = (documentX - parseInt(popupCtrl.style.width)) / 2;
            if (popupLeft < 0) {
                popupLeft = 0;
            }
            //alert(self.screen.height + "/" + self.screen.width + "/" + document.body.clientHeight + "/" + document.body.clientWidth + "/" + popupTop + "/" + popupLeft)
            break;
        case "left" :
            popupLeft = window.screenLeft;
            break;
        case "top" :
            popupTop = 0;
            break;
        case "topcenter" :
            popupTop = 0;
            popupLeft = (documentX - parseInt(popupCtrl.style.width)) / 2;
            if (popupLeft < 0) {
                popupLeft = 0;
            }
            break;
        case "topleft" :
            popupTop = 0;
            popupLeft = window.screenLeft;
            break;
        case "topright" :
            popupTop = 0;
            popupLeft = document.body.clientWidth - parseInt(popupCtrl.style.width);
            break;
    }
    popupTop = parseInt(popupTop) + scrollPosY;
    popupLeft = parseInt(popupLeft) + scrollPosX;
    popupCtrl.style.top = popupTop;
    popupCtrl.style.left = popupLeft;
    popupCtrl.setAttribute("popupAlignment", popupAlignment);
    return;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function popupMouseDown(ctrlid)
{
    if (moveState == 0) {
        moveState = 1;
        var e = null;
        if (document.all) {
            e = window.event;
            currentXLoc = e.offsetX;
            currentYLoc = e.offsetY;
        }
        else {
            e = window.event;
            currentXLoc = e.layerX;
            currentYLoc = e.layerY;
        }
        selectedPopup = ctrlid;
        setzIndex(ctrlid);

        // Added for issue 115931: capture mouse event no care of iframe.
        addElementEventListener(document.body, "mousemove", popupMouseMove);
        addElementEventListener(document.body, "mouseup", popupMouseUp);
        document.body.setCapture();
    }
    else {
        popupMouseUp();
    }
    return true;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function popupMouseUp() {
    if (moveState == 1) {

        // Added for issue 115931: release mouse event.
        document.body.releaseCapture();
        removeElementEventListener(document.body, "mousemove", popupMouseMove);
        removeElementEventListener(document.body, "mouseup", popupMouseUp);

        getSingleObject(selectedPopup).style.cursor = "";
        currentXLoc = 0;
        currentYLoc = 0;
        moveState = 0;
        selectedPopup = null;
    }
    return true;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function popupMouseMove() {
    if (moveState == 1) {
        ctrl = getSingleObject(selectedPopup);
        var e = window.event;
        if (e.pageX || e.pageY) {   //Other Browser
            if (e.pageX > 0 && e.pageY > 0) {
                ctrl.pixelLeft = e.pageX - currentXLoc;
                ctrl.pixelTop = e.pageY - currentYLoc;
            }
            else {
                popupMouseUp();
            }
        }
        else {    //IE
            if (e.clientX > 0 && e.clientY > 0) {
                ctrl.style.left = e.clientX - currentXLoc + document.body.scrollLeft;
                ctrl.style.top = e.clientY - currentYLoc + document.body.scrollTop;
            }
            else {
                popupMouseUp();
            }
        }
    }
    return true;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function popupSelectStart() {
    window.event.returnValue = false;
    return true;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function setzIndex(ctrlid, idx) {
    if (!idx) {
        lastFocuszIndex = lastFocuszIndex + 1;
        idx = lastFocuszIndex;
    }
    getSingleObject(ctrlid).style.zIndex = idx;
    if (getSingleObject(ctrlid).length > 0) {
        for (var i = 0; i < getSingleObject(ctrlid).length; i++) {
            setzIndex(getSingleObject(ctrlid)[i], idx);
        }
    }
}

function closeDiv(popupDiv) {
    if (popupDiv == null || typeof popupDiv == "undefined") {
        return;
    }

    if (useJQueryDialog){
        closeJQueryDialog(popupDiv);
        return;
    }

    var popupFrame = getPopupFrameFromPopupDiv(popupDiv);

    if (eval(window.baseOnUnloadForDivPopup)) {
        baseOnUnloadForDivPopup(popupFrame);
    }

    var isModalSpan = popupDiv.all.isModal;
    popupFrame.src = getCorePath()+"/emptyIFrame.jsp";
    popupFrame.parentElement.removeChild(popupFrame);
    popupDiv.parentElement.removeChild(popupDiv);

    //    alert("in closeDiv, activeNoOfPopups: " + activeNoOfPopups);
    if (activeNoOfPopups <= 1) {
        //        alert("in closeDiv, calling showDropDownListControls...");
        showDropDownListControls();
    }
    activeNoOfPopups--;
    if (isModalSpan.innerText == "Y") {
        //        alert("in closeDiv, activeNoOfModelPopups: " + activeNoOfModelPopups);
        if (activeNoOfModelPopups <= 1) {
            //            alert("in closeDiv, calling enableall...");
            enableall(true);
        }
        activeNoOfModelPopups--;
    }
}

function getPopupFrameFromPopupDiv(popupDiv) {
    if (useJQueryDialog){
        var iframeElement = $(popupDiv).find("iframe").get(0);
        return iframeElement;
    }
    //Frame element is added as the last element in the divPopup.
    //So, always go from the last element in the div popup to get the frame element.
    for (var i = popupDiv.all.length - 1; i >= 0; i--) {
        if (popupDiv.all[i].id.length > "popupframe".length) {
            if (popupDiv.all[i].id.substring(0, "popupframe".length) == "popupframe") {
                return popupDiv.all[i];
            }
        }
    }
}

function getDivPopupFromDivPopupControl(ctrl) {

    if (useJQueryDialog) {
        return $(ctrl).closest(".popupDialogDiv").get(0);
    }

    var divPopup = null;
    while (ctrl.parentElement != null) {
        if (ctrl.parentElement.tagName.toUpperCase() == 'DIV') {
            divPopup = ctrl.parentElement;
        }
        ctrl = ctrl.parentElement;
    }
    return divPopup;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function getDivPopupFromDivPopupFrame(divPopFrame) {
    var parentElmt = null;
    for (var i=1; i<=10; i++) {     //from Frame element, we need to go back 10 times to get to the popup table.
        parentElmt = (i==1 ? divPopFrame.parentElement : parentElmt.parentElement);
        if (parentElmt==null) {
            break;
        }
    }
    if (parentElmt!=null) {
        return parentElmt;
    } else {
        return null;
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function showDropDownListControls() {
    var elem = document.getElementsByTagName("select");
    for (var i = 0; i < elem.length; i++)
    {
        if (elem[i].getAttribute("style_display_back") != null) {
            elem[i].getAttribute("style").display = elem[i].getAttribute("style_display_back");
            elem[i].setAttribute("style_display_back", null);
        }
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function hideDropDownListControls() {
    var elem = document.getElementsByTagName("select");
    for (var i = 0; i < elem.length; i++)
    {
        if (elem[i].getAttribute("style_display_back") == null) {
            elem[i].setAttribute("style_display_back", elem[i].getAttribute("style").display);
            elem[i].getAttribute("style").display = "none";
        }
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function enableall(forceEnableall)
{
    //    begintime = new Date();
    if (forceEnableall || activeNoOfModelPopups == 1) {
        var elem = getSingleObject("mainBodyTable");
        // for UI 1.0 - WebWB
        if (!elem) {
            elem = getSingleObject("leftnav");
        }
        if (elem) {
            elem.getAttribute("style").filter = "";
        }
        elem = document.forms[0].elements;
        for (var i = 0; i < elem.length; i++)
        {
            elem[i].readOnly = false;
            elem[i].getAttribute("style").color = elem[i].getAttribute("style_color");
            elem[i].getAttribute("style").bgColor = elem[i].getAttribute("style_bgcolor");
            elem[i].removeAttribute("style_color");
            elem[i].removeAttribute("style_bgcolor");
        }

        enableMouseEvents("A");
        enableMouseEvents("TR");
        enableMouseEvents("input");
    }
    //    endtime = new Date();
    //    alert(endtime.getTime() - begintime.getTime() + " in enableall");
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function enableMouseEvents(tagName) {
    elem = document.getElementsByTagName(tagName);
    for (var i = 0; i < elem.length; i++) {
        if (elem[i].getAttribute("onmouseover_backup")) {
            elem[i].setAttribute("onmouseover", elem[i].getAttribute("onmouseover_backup"));
            elem[i].removeAttribute("onmouseover_backup");
        }

        if (elem[i].getAttribute("onmouseout_backup")) {
            elem[i].setAttribute("onmouseout", elem[i].getAttribute("onmouseout_backup"));
            elem[i].removeAttribute("onmouseout_backup");
        }

        if (elem[i].getAttribute("onmousemove_backup")) {
            elem[i].setAttribute("onmousemove", elem[i].getAttribute("onmousemove_backup"));
            elem[i].removeAttribute("onmousemove_backup");
        }

        if (elem[i].getAttribute("onclick_backup")) {
            elem[i].setAttribute("onclick", elem[i].getAttribute("onclick_backup"));
            elem[i].removeAttribute("onclick_backup");
        }

        if (elem[i].tagName == 'A')
        {
            var href_backup = elem[i].getAttribute("href_backup");
            if (href_backup != null && href_backup != '' && href_backup)
            {
                elem[i].setAttribute("href", href_backup);
                elem[i].removeAttribute("href_backup");
            }
        }
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function enableField(fieldId) {
    elem = getSingleObject(fieldId);
    if (elem) {
        elem[i].readOnly = false;
        elem[i].getAttribute("style").color = elem[i].getAttribute("style_color");
        elem[i].getAttribute("style").bgColor = elem[i].getAttribute("style_bgcolor");
        elem[i].removeAttribute("style_color");
        elem[i].removeAttribute("style_bgcolor");
        //getSingleObject(fieldId).setAttribute("disabled", false)
        if (elem.childNodes.length) {
            for (var i = 0; i < elem.childNodes.length; i++) {
                if (elem.childNodes[i].id) {
                    enableField(elem.childNodes[i].id);
                }
                else {
                    if (elem.childNodes[i].name) {
                        enableField(elem.childNodes[i].name);
                    }
                }

            }
        }
    }

    /*
        alert(getSingleObject (fieldId.id).childNodes[0].disabled);
        for (var i = 0; i < fieldId.length; i++)
        {
            fieldId[i].setAttribute("onmouseover", fieldId[i].getAttribute("onmouseover_backup"));
            fieldId[i].setAttribute("onmouseout", fieldId[i].getAttribute("onmouseout_backup"));
            fieldId[i].setAttribute("onmousemove", fieldId[i].getAttribute("onmousemove_backup"));
            fieldId[i].setAttribute("onclick", fieldId[i].getAttribute("onclick_backup"));
            if (fieldId[i].tagName == 'A')
            {
                var href_backup = fieldId[i].getAttribute("href_backup");
                if (href_backup != null && href_backup != '' && href_backup)
                {
                    fieldId[i].setAttribute("href", href_backup);
                    fieldId[i].setAttribute("onclick", fieldId[i].getAttribute("onclick_backup"));
                    fieldId[i].removeAttribute("href_backup");
                    fieldId[i].removeAttribute("onclick_backup");
                }
            }
            fieldId[i].removeAttribute("onmouseover_backup");
            fieldId[i].removeAttribute("onmouseout_backup");
            fieldId[i].removeAttribute("onmousemove_backup");
            fieldId[i].removeAttribute("onclick_backup");

            enableField(fieldId[i]);
        }
    */
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function disableall()
{
    //    begintime = new Date();
    if (activeNoOfModelPopups > 0) {
        var elem = getSingleObject("mainBodyTable");
        // for UI 1.0 - WebWB
        if (!elem) {
            elem = getSingleObject("leftnav");
        }
        if (elem) {
            elem.getAttribute("style").filter = "gray";
        }
        elem = document.forms[0].elements;
        for (var i = 0; i < elem.length; i++) {
            if (elem[i].getAttribute("style_color") == null && elem[i].getAttribute("style_bgcolor") == null) {
                elem[i].readOnly = true;
                elem[i].setAttribute("style_color", elem[i].getAttribute("style").color);
                elem[i].setAttribute("style_bgcolor", elem[i].getAttribute("style").bgColor);
                elem[i].getAttribute("style").color = "#999999";
                elem[i].getAttribute("style").bgColor = "#DEDEDE";
            }
        }

        disableMouseEvents("A");
        disableMouseEvents("TR");
        disableMouseEvents("input");
    }
    //    endtime = new Date();
    //    alert(endtime.getTime() - begintime.getTime() + " in disableall");
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function disableMouseEvents(tagName) {
    elem = document.getElementsByTagName(tagName);
    for (var i = 0; i < elem.length; i++) {

        if (elem[i].getAttribute("onmouseover") && elem[i].getAttribute("onmouseover_backup") == null) {
            elem[i].setAttribute("onmouseover_backup", elem[i].getAttribute("onmouseover"));
            elem[i].setAttribute("onmouseover", "javascript:return void(0)");
        }

        if (elem[i].getAttribute("onmouseout") && elem[i].getAttribute("onmouseout_backup") == null) {
            elem[i].setAttribute("onmouseout_backup", elem[i].getAttribute("onmouseout"));
            elem[i].setAttribute("onmouseout", "javascript:return void(0)");
        }

        if (elem[i].getAttribute("onmousemove") && elem[i].getAttribute("onmousemove_backup") == null) {
            elem[i].setAttribute("onmousemove_backup", elem[i].getAttribute("onmousemove"));
            elem[i].setAttribute("onmousemove", "javascript:return void(0)");
        }

        if (elem[i].getAttribute("onclick") && elem[i].getAttribute("onclick_backup") == null) {
            elem[i].setAttribute("onclick_backup", elem[i].getAttribute("onclick"));
            elem[i].setAttribute("onclick", "javascript:return void(0)");
        }

        if (elem[i].tagName == 'A') {
            var href = elem[i].getAttribute("href");
            if (href != null && href != '' && href && elem[i].getAttribute("href_backup") == null)
            {
                elem[i].setAttribute("href_backup", href);
                elem[i].removeAttribute("href");
            }
        }
    }
}

//look up divPopup
var currentlySelectedLookupFieldId;

function openLookupDivPopup(popupTop, popupLeft, popupWidth, popupHeight, popupId, lookupContent, lookupFieldHeight)
{
    var screenHeight = document.documentElement.clientHeight + document.body.scrollTop;
    var screenWidth = document.documentElement.clientWidth + document.body.scrollLeft;
    if (!popupId) {
        popupId = "newpopup" + popupIdx;
    }
    var thisbody = document.getElementsByTagName("body")[0];

    //create popup div
    var popup = document.createElement("div");
    popup.id = popupId;
    popup.className = "popup";
    popup.style.display = "block";
    popup.style.position = "absolute";
    $(popup).css("zIndex", "20000");

    $(popup).css("left", popupLeft);
    $(popup).css("height", popupHeight);
    popup.style.overflow = "auto";
    popup.style.backgroundColor = "white";
    //if the popup is in the leftest side
    var isMaxWidth = false;
    //set max width
    if (popupWidth >= screenWidth - popupLeft) {
        popupWidth = screenWidth - popupLeft;
        isMaxWidth = true;
    }
    $(popup).css("height", popupHeight);
    $(popup).css("width", popupWidth);
    //make content fill the whole div
    $(lookupContent).css("width", "100%");
    popup.appendChild(lookupContent);
    thisbody.appendChild(popup);

    // fix popup height and width
    if (popupHeight === "" || popupHeight > screenHeight) {
        popupHeight = popup.clientHeight > 200 ? 200 : popup.clientHeight;
        // add width of the scroll bar
        if (!isMaxWidth) {
            popupWidth = popupWidth + 20;
        }
        $(popup).css("height", popupHeight);
        $(popup).css("width", popupWidth);
    }


    var popupBottom = popupTop + popupHeight;
    var screenBottom = screenHeight + document.body.scrollTop;
    //fix positons
    var flipUp = false;
    if (popupBottom > screenBottom) {
        //flip up
        flipUp = true;
    }
    if (flipUp) {
        popupTop = popupTop - popupHeight - lookupFieldHeight;
        if (popupTop < 0) {
            popupTop = 0;
        }
    }
    $(popup).css("top", popupTop);

    addElementEventListener(document, "click", checkLookupDivPopup);
    hideShowCovered(popup);
    return popup.id;
}

function popupMultiSelectDiv(fldId) {
    // close the current select popup div
    if (fldId != currentlySelectedLookupFieldId)
        closeLookupDivPopup();

    currentlySelectedLookupFieldId = fldId;
    var popup = getObject(currentlySelectedLookupFieldId + "LookupPopup");
    var comboTxtFld = getObject(fldId + "MultiSelectText");
    var comboDiv = getObject(fldId + "LookupFieldDiv");
    var comboRectPos = getAbsolutePos(comboDiv);
    var minLookupPopupWidth = 175;
    var lookupFieldHeight = comboTxtFld.clientHeight + 4;

    if (!popup) {
        var popupWidth = minLookupPopupWidth;
        var popupHeight = "";
        //iterate multi-select list add table row for every option
        var multiSelFld = getObject(fldId);
        var popupContentTbl = document.createElement("table");


        var popupContentTbody = document.createElement("tbody");
        //collect the checked items
        var checkedItems = new Array(multiSelFld.options.length);

        for (var i = 0; i < multiSelFld.options.length; i++) {
            var expired = isMultiSelectOptionExpired(fldId,multiSelFld.options[i].innerHTML,"");
           //if (!multiSelFld.options[i].selected && expired) continue; // do not hide expired option for now
            if (multiSelFld.options[i].value != '' ) {
                var trElement = document.createElement("tr");
                //set tr style
                if (i % 2)
                    trElement.className = "alternate_colors_one";
                else
                    trElement.className = "alternate_colors_two";
                var selectTdElement = document.createElement("td");
                var selectElement = document.createElement("input");
                selectElement.name = "chkMultiSelect" + fldId;
                selectElement.setAttribute("type", "checkbox");
                selectElement.setAttribute("value", multiSelFld.options[i].value);
                if (expired) {
                   selectElement.setAttribute("title", expiredOptionTitle);
                }
                selectElement.onclick = function() {
                    onCheckMultiSelectOption(fldId, this);
                };

                if (multiSelFld.options[i].selected) {
                    checkedItems[i] = selectElement;
                }

                selectTdElement.appendChild(selectElement);

                var labelTdElement = document.createElement("td");
                labelTdElement.width = "80%";
                labelTdElement.innerHTML = multiSelFld.options[i].innerHTML;
                if (expired) {
                   if (!isEmpty(expiredOptionDisplayableSuffix)){   //replace label conditionally
                       var indexOfExpiredSuffix = labelTdElement.innerHTML.lastIndexOf(expiredOptionSuffixIndicator);
                       if (indexOfExpiredSuffix >0) labelTdElement.innerHTML =labelTdElement.innerHTML.substring(0,indexOfExpiredSuffix)+expiredOptionDisplayableSuffix;
                   }
                   labelTdElement.className="expiredOption";
                }
                trElement.appendChild(selectTdElement);
                trElement.appendChild(labelTdElement);

                popupContentTbody.appendChild(trElement);
            }
        }

        popupContentTbl.appendChild(popupContentTbody);

        popupContentTbl.style.height = "";
        popupContentTbl.cellPadding = "0";
        popupContentTbl.cellSpacing = "0";
        popupContentTbl.className = "popupcontent";


        //adjust popup's position
        var popupTop = comboRectPos.y + lookupFieldHeight;
        var popupLeft = comboRectPos.x;


        popupId = fldId + "LookupPopup";
        var thisbody = document.getElementsByTagName("body")[0];
        thisbody.appendChild(popupContentTbl);
        //set popup width as the lookupContent's width
        if (popupContentTbl.clientWidth > popupWidth) {
            popupWidth = popupContentTbl.clientWidth;
        }

        if (multiSelFld.size) {
          popupHeight =  multiSelFld.size * 20;
        } else {
            popupHeight = "";
        }

        openLookupDivPopup(popupTop, popupLeft, popupWidth, popupHeight, popupId, popupContentTbl, lookupFieldHeight);

        //render checked items by checking the select options in checkedItems array
        for (var i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                checkedItems[i].checked = true;
            }
        }
    }
    else {
        closeLookupDivPopup();
    }
}

function checkLookupDivPopup(ev) {
    var el = ev.srcElement || ev.target;
    var popup = getObject(currentlySelectedLookupFieldId + "LookupPopup");
    if (popup) {
        for (; el != null && el != popup && el.id != 'btnFind'; el = el.parentNode);
        if (el == null) {
            closeLookupDivPopup();
            ev.cancelBubble = true;
            ev.returnValue = false;
        }
    }
}

function closeLookupDivPopup() {
    var popup = getObject(currentlySelectedLookupFieldId + "LookupPopup");
    if (popup) {
        currentlySelectedLookupFieldId = '';
        hideShowCovered(popup);
        popup.parentElement.removeChild(popup);
        removeElementEventListener(document, "click", checkLookupDivPopup);
    }
}

function getAbsolutePos(el) {
    var SL = 0, ST = 0;
    var is_div = /^div$/i.test(el.tagName);
    if (is_div && el.scrollLeft)
        SL = el.scrollLeft;
    if (is_div && el.scrollTop)
        ST = el.scrollTop;
    var r = { x: el.offsetLeft - SL, y: el.offsetTop - ST };
    if (el.offsetParent) {
        var tmp = getAbsolutePos(el.offsetParent);
        r.x += tmp.x;
        r.y += tmp.y;
    }
    return r;
}

function isMultiSelectOptionExpired(fieldId, optionText, optionValue) {
    var expired = false;
    if (isDefined(dropdownFieldsWithExpiredOptions) ) {
        var indicator = ((isEmpty(expiredOptionDisplayableSuffix))?expiredOptionSuffixIndicator:expiredOptionDisplayableSuffix);
        if (!isEmpty(indicator) && !isEmpty(dropdownFieldsWithExpiredOptions) && dropdownFieldsWithExpiredOptions.indexOf(fieldId) != -1) {
            // get the text for the given option value
            if (getObject(fieldId)) {
                if (getObject(fieldId).options) {
                    text = optionText;
                    if (isEmpty(text)) text =getMultiSelectOptionTextByValue(getObject(fieldId).options, optionValue);
                    if (text.endsWith(indicator)) {
                        expired = true;
                    }
                }
            }
        }
    }
    return expired;
}

function getMultiSelectOptionTextByValue(options, value){
   var text='';
   if (options) {
       for (var i=0; i<options.length; i++){
           if (options[i].value == value) {
               text = options[i].text;
               break;
           }
       }
   }
    return text;
}

function onCheckMultiSelectOption(fldId, obj) {
    var checkedValue = obj.value;
    var multiSelFld = getObject(fldId);
    var comboTxtFld = getObject(fldId + "MultiSelectText");
    var labelValue;
    comboTxtFld.value = '';
    var anExpiredOption = isMultiSelectOptionExpired(fldId,"",checkedValue);
    if (anExpiredOption)   obj.checked = false;
    for (var i = 0; i < multiSelFld.options.length; i++) {
        // update hidden multi-select field
        if (checkedValue == multiSelFld.options[i].value) {
            multiSelFld.options[i].selected = !multiSelFld.options[i].selected;
            if (anExpiredOption)  multiSelFld.options[i].selected = false;
        }

        //update text field
        if (multiSelFld.options[i].selected && multiSelFld.options[i].value != '') {
            if (comboTxtFld.value == '') {
                comboTxtFld.value = multiSelFld.options[i].innerText;
            }
            else {
                comboTxtFld.value = comboTxtFld.value + "," + multiSelFld.options[i].innerText;
            }
        }
    }
    //set --selected-- option
    if (isEmpty(multiSelFld.options[0].value)) {
        if (isEmpty(comboTxtFld.value)) {
            multiSelFld.options[0].selected = true;
            comboTxtFld.value = multiSelFld.options[0].text;
        }
        else {
            multiSelFld.options[0].selected = false;
        }
    }

    //call onChange event
    selectedIndexesForDropdownField= getSelectedIndexesForDropdownField(multiSelFld);
    dispatchElementEvent(getObject(fldId), "change");

    if (window.handleOnMultiSelectOption) {
        handleOnMultiSelectOption(fldId, obj);
    }
}

//update the mutliselect filed according to the hidden multiselect field
function updateMultiSelectField(fldId) {
    var multiSelFld = getObject(fldId);
    var comboTxtFld = getObject(fldId + "MultiSelectText");
    var multiCheckBoxes = getObject("chkMultiSelect" + fldId);
    var labelValue;
    comboTxtFld.value = '';
    for (var i = 0; i < multiSelFld.options.length; i++) {
        multiCheckBoxes[i].selected = multiSelFld.options[i].selected;
        //update text field
        if (multiSelFld.options[i].selected && multiSelFld.options[i].value != '') {
            if (comboTxtFld.value == '') {
                comboTxtFld.value = multiSelFld.options[i].innerHTML;
            }
            else {
                comboTxtFld.value = comboTxtFld.value + "," + multiSelFld.options[i].innerHTML;
            }
        }
    }
}

function clearMultipleSelectPopupValues(fldId) {
    var multiSelFld = getObject(fldId);
    var comboTxtFld = getObject(fldId + "MultiSelectText");
    comboTxtFld.value = "";
    for (var i = 0; i < multiSelFld.options.length; i++) {
        var selectOption = multiSelFld.options[i];
        if (selectOption.value == "") {
            comboTxtFld.value = selectOption.innerHTML;
            selectOption.selected = true;
        } else if (selectOption.selected) {
            selectOption.selected = false;
        }
    }
}

function hideShowCovered(el) {
    // The codes of this method are copied from calendarAll.js. It should be used on IE or Opera only.
    // For Chrome or other browsers, system should not hide or show the covered components under the popup component.
    if (!Calendar.is_ie && !Calendar.is_opera)
        return;

    function getVisib(obj) {
        var value = obj.style.visibility;
        if (!value) {
            if (document.defaultView && typeof (document.defaultView.getComputedStyle) == "function") { // Gecko, W3C
                if (!Calendar.is_khtml)
                    value = document.defaultView.
                        getComputedStyle(obj, "").getPropertyValue("visibility");
                else
                    value = '';
            }
            else if (obj.currentStyle) { // IE
                value = obj.currentStyle.visibility;
            }
            else
                value = '';
        }
        return value;
    }
    ;

    var tags = new Array("applet", "select");


    var p = getAbsolutePos(el);
    var EX1 = p.x;
    var EX2 = el.offsetWidth + EX1;
    var EY1 = p.y;
    var EY2 = el.offsetHeight + EY1;

    for (var k = tags.length; k > 0;) {
        var ar = document.getElementsByTagName(tags[--k]);
        var cc = null;

        for (var i = ar.length; i > 0;) {
            cc = ar[--i];

            p = getAbsolutePos(cc);
            var CX1 = p.x;
            var CX2 = cc.offsetWidth + CX1;
            var CY1 = p.y;
            var CY2 = cc.offsetHeight + CY1;

            if (currentlySelectedLookupFieldId == '' || (CX1 > EX2) || (CX2 < EX1) || (CY1 > EY2) || (CY2 < EY1)) {
                if (!cc.__msh_save_visibility) {
                    cc.__msh_save_visibility = getVisib(cc);
                }
                cc.style.visibility = cc.__msh_save_visibility;
            }
            else {
                if (!cc.__msh_save_visibility) {
                    cc.__msh_save_visibility = getVisib(cc);
                }
                cc.style.visibility = "hidden";
            }
        }
    }
}

function hasActivePopups() {
    var hasActivePopups = false;
    if (activeNoOfPopups > 0) {
        hasActivePopups = true;
    } else {
        //check if there is popup on iframe
        for (var i = 0; i < document.frames.length; i++) {
            if (document.frames[i].hasActivePopups) {
                if (document.frames[i].hasActivePopups()) {
                    hasActivePopups = true;
                    break;
                }
            }
        }
    }
    return hasActivePopups;
}

function isInDivPopup() {
    var isInDivPopup = false;
    if (isInIframe()) {
        if (window.frameElement.id) {
            if (window.frameElement.id.startsWith("popupframe")) {
                isInDivPopup = true;
            }
        }
    } else if (isInWindowPopup()) {
        isInDivPopup = true;
    }
    return isInDivPopup;
}

/**
 * Check if it's a popup window opened with window.open.
 *
 * Since the popup window and the parent window could not use jqxGrid in same time, so they need to be run in different window.
 * In this case, we cannot open a popup with DIV popup. Because div popup uses iframe, and the page in iframe will use the same document window with the parent frame.
 * @returns {boolean}
 */
function isInWindowPopup() {
    var inWindowPopup = false;

    if (window.name && window.name.startsWith("newpopup")) {
        inWindowPopup = true;
    }

    return inWindowPopup;
}

function showProcessingDivPopup() {
    showProgressIndicator('CENTER');
}

function closeProcessingDivPopup() {
    hideProgressIndicator("CENTER");
}

function showProcessingImgIndicator() {
    showProgressIndicator('UPRIGHT');
}

function showProcessingImgIndicatorWithPromise() {
    var defer = $.Deferred();

    showProcessingImgIndicator();

    setTimeout(function () {
        defer.resolve()
    }, 0);

    return defer.promise();
}

function hideProcessingImgIndicator() {
    hideProgressIndicator("UPRIGHT");
}

//-----------------------------------------------------------------------------
// Show the progress indicator div popup
//-----------------------------------------------------------------------------
function showProgressIndicator(position) {
    var functionExists = eval("window.isTabStyle");
    if(functionExists){
        //function isTabeStyle() is defined in PM only.
        if(isTabStyle()){
            //Below logic is for tab style only.
            if(getParentWindow() != null && eval("getParentWindow().divPopupExists") && eval("getParentWindow().ignoreProgressIndicator")){
                if(!getParentWindow().divPopupExists() && getParentWindow().ignoreProgressIndicator()){
                    //if current sub-page is not div popup
                    //if processing running is displaying on parent page
                    //then do NOT need to open dialog on current sub-page.
                    return;
                }
            }
        }
    }

    var displayPosition = getProgressIndicatorPosition(position);
    var obj = null;
    if (displayPosition == "CENTER") {
        $("#savingDialog").dialog("open");
        // Below code is to solve the problem that animated GIF is not animating
        obj = getSingleObject('savingDialog');
        obj = (!obj ? getObjectById('savingDialog') : obj);
    } else if (displayPosition == "UPRIGHT") {
        $("#processingDialog").dialog("open");
        // Below code is to solve the problem that animated GIF is not animating
        obj = getSingleObject('processingDialog');
        obj = (!obj ? getObjectById('processingDialog') : obj);
    }
    else {
        // Display in up-right by default if the value position is undefined or invalid
        // Below code is to solve the problem that animated GIF is not animating
        obj = getSingleObject('processingDialog');
        obj = (!obj ? getObjectById('processingDialog') : obj);
    }
    if (obj) {
        obj.innerHTML = obj.innerHTML;
    }
}

//-----------------------------------------------------------------------------
// Hide the progress indicator div popup
//-----------------------------------------------------------------------------
function hideProgressIndicator(position) {
    var displayPosition = getProgressIndicatorPosition(position);
    if (displayPosition == "CENTER") {
        var savingDialog = $("#savingDialog");

        if (savingDialog.data("uiDialog") != null) {
            savingDialog.dialog("close");
        }
    } else if (displayPosition == "UPRIGHT") {
        var processingDialog = $("#processingDialog");

        if (processingDialog.data("uiDialog") != null) {
            processingDialog.dialog("close");
        }
    }
    else {
        var processingDialog = $("#processingDialog");

        if (processingDialog.data("uiDialog") != null) {
            processingDialog.dialog("close");
        }
    }
}

//-----------------------------------------------------------------------------
// Get the progress indicator position
//-----------------------------------------------------------------------------
function getProgressIndicatorPosition(position) {
    // The position of indicator div popup will be determined by the value in applicationConfig.properties
    // DEFAULT: the saving indicator displays in the center, and the processing indicator displays in the up-right.
    // CENTER: use saving indicator which displays in the center.
    // UPRIGHT: use processing indicator which displays in the up-right
    var displayPosition;
    if (progressIndicatorPosition == "CENTER" ||
            progressIndicatorPosition == "UPRIGHT") {
        displayPosition = progressIndicatorPosition;
    } else {
        displayPosition = position.toUpperCase();
    }
    return displayPosition;
}

/**
 * If the parameter isReadOnly is false, this function may popup confirm message about changes will be loss.
 *
 * Since we may have additional page level logic to handle before closing a window, we can call isOkToCloseThisDivPopup
 * to check if it's ok to close this div popup first. In this case, we need to avoid the function isOkToCloseThisDivPopup
 * called again. So we should pass {false} value to the {isReadOnly} parameter.
 *
 * @param isReadOnly
 * @returns {boolean}
 */
function closeThisDivPopup(isReadOnly) {
    var parentWindow = getParentWindow();

    if (parentWindow != null) {
        if (!isReadOnly) {
            // Check if OK to close this window.
            if (!isOkToCloseThisDivPopup()) {
                return false;
            }
        }

        if (window.frameElement) {
            // Close div pop up.
            var divPopup = parentWindow.getDivPopupFromDivPopupControl(window.frameElement);

            if (divPopup) {
                parentWindow.closeDiv(divPopup);
            }
        } else if (parentWindow != null) {
            // Close parent window.
            baseCloseWindow();
        }
    }
}

function isOkToCloseThisDivPopup() {
    var functionExists = eval("window.commonIsOkToChangePages");
    if (functionExists) {
        var isOkToProceed = commonIsOkToChangePages("DIV_PUP", "");
        if (!isOkToProceed) {
            return false;
        }
    }

    if (window.isOkToChangePages) {
        if (!isOkToChangePages("DIV_PUP", "")){
            return false;
        }
    }

    return true;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function getDivPopupContentDiv(divPopup) {
    return getChildElementByClassName(divPopup, "div", "popupcontent");
}


/**
 *
 * @param popupTitle        If it is null, there is no title bar
 * @param urlToOpen         URL or HTML text.
 * @param isModal
 * @param isDragable
 * @param popupWidth
 * @param popupHeight
 * @param popupId
 * @param isShowCloseLink   Default value: true
 * @param popupAlignment    Default value: center
 * @param startInactive     Default value: false
 * @param isScrolling       IFRAME scrolling. Default value: true.
 */
function openJQueryDialog(popupTitle, urlToOpen, isModal, isDragable, popupWidth, popupHeight,
                          popupId, isShowCloseLink, popupAlignment, startInactive, isScrolling){
    var isUrlHTMLContent = false;

    popupIdx++;

    if (!startInactive) {
        activeNoOfPopups++;
    }

    if (isModal) {
        if (!startInactive) {
            activeNoOfModelPopups++;
        }
    }

    if (!urlToOpen) {
        alert("Missing URL Information to loaded. Cannot proceed.")
        return;
    }

    if (!(urlToOpen.substring(0, 1) == "/" || urlToOpen.substring(0, 2) == "./" || urlToOpen.substring(0, 7) == "http://" || urlToOpen.substring(0, 8) == "https://"))
        isUrlHTMLContent = true;

    if (!popupWidth) {
        popupWidth = 700;
    }
    if (popupWidth > $(window).width() - 40){
        popupWidth = $(window).width() - 40;
    }
    if (!popupHeight) {
        popupHeight = 600;
    }

    if (!popupAlignment) {
        popupAlignment = "center";
    }
    var popupPosition = getPosition(popupAlignment);

    if (!popupId) {
        popupId = "newpopup" + popupIdx;
    }
    if (isShowCloseLink == null) {
        isShowCloseLink = true;
    }

    var autoOpen = true;
    if (startInactive){
        autoOpen = false;
    }

    var thisbody = document.getElementsByTagName("body")[0];

    var popupDiv = document.createElement("div");
    popupDiv.id = popupId;
    popupDiv.className = "popupDialogDiv";
    popupDiv.setAttribute("isModal", isModal ? "Y" : "N");
    popupDiv.setAttribute("popupAlignment", popupAlignment);

    thisbody.appendChild(popupDiv);

    var dialogClass = "popupDialog popupNoClose";
    if (isShowCloseLink) {
        dialogClass = "popupDialog";
    }
    if (popupTitle == null){
        dialogClass += " popupNoTitle";
    }

    var dialogParameters = {
        title:popupTitle,
        modal:isModal,
        position:popupPosition,
        height:popupHeight,
        width:popupWidth,
        resizable:true,
        draggable:isDragable,
        autoOpen:autoOpen,
        closeOnEscape:false,
        dialogClass:dialogClass,
        beforeClose:window.beforeDialogClose,
        close:window.dialogClose
    };

    if (isUrlHTMLContent == false) {

        // For Chinese character parameter values support
        urlToOpen = urlEncode(urlToOpen);

        if (urlToOpen.indexOf("?") > 0 && urlToOpen.substring(urlToOpen.length - 1, 1) != "?") {
            urlToOpen += "&isPopupPage=Y"
        } else {
            if (urlToOpen.indexOf("?") > 0) {
                urlToOpen += "isPopupPage=Y"
            } else {
                urlToOpen += "?isPopupPage=Y"
            }
        }

        var popupframe = document.createElement("iframe");
        popupframe.id = "popupframe" + popupIdx;
        popupframe.name = "popupframe" + popupIdx;
        popupframe.width = "100%";
        popupframe.height = "100%";
        popupframe.frameBorder = "no";

        popupframe.scrolling = "auto";
        if (isScrolling != undefined) {
            if (isScrolling == false) {
                popupframe.scrolling = "no";
            }
        }
        // hide it first, otherwise the dialog height is not correct
        popupframe.style.display="none";
        popupframe.style.overflowX = "auto";
        popupframe.style.overflowY = "auto";

        popupDiv.appendChild(popupframe);

        $(popupDiv).dialog(dialogParameters);
        // To resolve the backend error "An existing connection was forcibly closed by the remote host" for IE 11.
        $(popupframe).attr("src", urlToOpen);

        if (eval(window.baseOnLoadForDivPopup)) {
            baseOnLoadForDivPopup(popupframe)
        }

        popupframe.style.display="";

    } else {
        $(popupDiv).html(urlToOpen);
        $(popupDiv).dialog(dialogParameters);
    }

    popupDiv.style.paddingTop="0px";
    popupDiv.style.paddingBottom="0px";
    popupDiv.style.paddingLeft="0px";
    popupDiv.style.paddingRight="0px";

    return popupDiv.id;
}

function beforeDialogClose(event, ui) {
    var iframeElement = this.children[0];

    var functionExists = eval("iframeElement.contentWindow.checkChangeStatusBeforeClose");
    if(functionExists){
        if(!iframeElement.contentWindow.checkChangeStatusBeforeClose(this)){
            //return false to stop jQuery before close event.
            return false;
        }
    }

    var divElement = this;
    setTimeout(function () {
        closeJQueryDialog(divElement);
    }, 1);
    //always return false, the real close logic is in function closeJQueryDialog
    return false;
}

function dialogClose(event, ui) {
    var iframeElement = this.children[0];
    $(iframeElement).attr("src", getCorePath() + "/emptyIFrame.jsp");
    iframeElement.parentElement.removeChild(iframeElement);
}

function emptyBeforeDialogClose(event, ui) {
    return true;
}

function closeJQueryDialog(popupDiv){

    var isModal = popupDiv.getAttribute("isModal");
    var popupFrame = getPopupFrameFromPopupDiv(popupDiv);

    if (eval(window.baseOnUnloadForDivPopup)) {
        baseOnUnloadForDivPopup(popupFrame);
    }

    $(popupDiv).dialog({
        beforeClose:window.emptyBeforeDialogClose
    });
    $(popupDiv).dialog("close");
    $(popupDiv).dialog("destroy");

    popupDiv.parentElement.removeChild(popupDiv);

    activeNoOfPopups--;
    if (isModal == "Y") {
        activeNoOfModelPopups--;
    }
}

function resizeJQueryDialog(divPopup, width, height) {
    if (divPopup) {
        if (width) {
            if (width > $(window).width() - 40){
                width = $(window).width() - 40;
            }
            // need to hide iframe first, otherwise the dialog height it not correct
            var iframeElement = getPopupFrameFromPopupDiv(divPopup);
            iframeElement.style.display="none";
            $(divPopup).dialog({
                width:width
            });
            iframeElement.style.display="";
        }
        if (height) {
            // need to hide iframe first, otherwise the dialog height it not correct
            var iframeElement = getPopupFrameFromPopupDiv(divPopup);
            iframeElement.style.display="none";
            $(divPopup).dialog({
                height:height
            });
            iframeElement.style.display="";
        }
        if (width || height) {
            var popupAlignment = divPopup.getAttribute("popupAlignment");
            if (popupAlignment != null) {
                var popupPosition = getPosition(popupAlignment);
                $(divPopup).dialog({
                    position:popupPosition
                });
            }
        }
    }
}

function moveJQueryDialog(divPopup, x, y) {
    if (divPopup) {
        if (x) {
            divPopup.parentElement.style.left = x;
        }
        if (y) {
            divPopup.parentElement.style.top = y;
        }
    }
}

function getPosition(popupAlignment) {
    var popupPosition = ["center", "center"];
    switch (popupAlignment) {
        case "center" :
            popupPosition = ["center", "center"];
            break;
        case "left" :
        case "leftcenter" :
            popupPosition = ["left", "center"];
            break;
        case "top" :
        case "topcenter" :
            popupPosition = ["center", "top"];
            break;
        case "topleft" :
            popupPosition = ["left", "top"];
            break;
        case "topright" :
            popupPosition = ["right", "top"];
            break;
        case "right" :
        case "rightcenter" :
            popupPosition = ["right", "center"];
            break;
        case "bottom" :
        case "bottomcenter" :
            popupPosition = ["center", "bottom"];
            break;
        case "bottomleft" :
            popupPosition = ["left", "bottom"];
            break;
        case "bottomright" :
            popupPosition = ["right", "bottom"];
            break;
    }
    return popupPosition;
}

// Issue#145438
// Resize the dialog, the iframe size changes with the dialog
// Then reload the iframe, the iframe size is changed back. Need to re-adjust the iframe size when loading popup page.
function reAdjustIFrameSize() {
    // Enforce this only for true div popup.
    if (isInDivPopup() && window.frameElement) {
        window.frameElement.width = "99%";
        window.frameElement.height = "99%";
        window.frameElement.width = "100%";
        window.frameElement.height = "100%";
    }
}


/**
 * when using iframe within a div popup, the src element must be cleared,
 * this will ensure the iframe not to request whatever it is in the src before closing the div popup.
 */
if (typeof dti == "undefined") {
    dti = {};
}
if (typeof dti.divpopuputils == "undefined") {
    dti.divpopuputils = (function() {
        return {
            clearIFrameSrc: function() {
                var parentWindow = getParentWindow();
                if (parentWindow) {
                    var divPopup = parentWindow.getDivPopupFromDivPopupControl(window.frameElement);
                    if (typeof divPopup != "undefined") {
                        var popupFrame = getPopupFrameFromPopupDiv(divPopup);
                        popupFrame.src = "";
                    }
                }

            }
        }
    })()
}