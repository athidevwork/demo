<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Description:

  Author: jxgu
  Date: Jan 01, 2014


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<%
    ApplicationContext.getInstance().exposeMessageSourceForJstl(application, request);
    String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Content-Type", "text/html; charset=" + encoding);
    response.setHeader("Pragma", "no-cache");
    String corePath = Module.getCorePath(request);
%>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <script type="text/javascript" src="<%=corePath%>/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="<%=corePath%>/js/jquery-ui-1.10.3.js"></script>
    <script type="text/javascript" src="<%=corePath%>/js/jMenu.jquery.js"></script>

    <script type="text/javascript">
        function loadLiFromParent() {
            var parentULObject = window.frameElement.parentElement;
            var tabUL = $(parentULObject).closest(".jTabMenu");
            if (tabUL.length > 0) {
                $("#mainUL").addClass("jTabMenu");
            }else{
                $("#mainUL").addClass("jMenu");
            }

            var targetUL = document.getElementById("targetUL");
            $(parentULObject).children("li").clone().appendTo($(targetUL));

            var firstLi = $(parentULObject).children("li").first();
            var width = firstLi.width();
            var outerWidth = firstLi.outerWidth();
            if (width == 0) {
                // The li object may not be displayed, use default value
                // To avoid it, we can use fixed width for li object. See designRTFLetter.jsp
                if (tabUL.length > 0) {
                    width = 120;
                    outerWidth = 122;
                } else {
                    width = 145;
                    outerWidth = 147;
                }
            }
            var liObjects = $(targetUL).children("li");
            for (var i = 0; i < liObjects.length; i++) {
                var liObject = $(liObjects[i]);
                liObject.width(width);
                liObject.children("a")[0].onclick = window.clickOnSubMenu;
            }
            $(targetUL).show();
            $("#mainDiv").width(outerWidth);
            $("#mainDiv").height($(window.frameElement).height());
            $(window.frameElement).width(outerWidth);
        }

        function isInSunMenuIframe() {
            return true;
        }

        function clickOnSubMenu() {
            var menuId = this.parentElement.id;
            var parentWindow = window.frameElement.document.parentWindow;
            if (parentWindow.clickMenu) {
                parentWindow.clickMenu(menuId);
            }
        }
    </script>
    <link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
    <link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
    <link href="<%=corePath%>/css/jquery-ui.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
    <link href="<%=corePath%>/css/jmenu.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
</head>
<body onload="loadLiFromParent();">
<div id="mainDiv" class="jTabMenuDiv" style="margin: 0px;border: 0px;padding: 0px;overflow: hidden;">
    <ul id="mainUL" style="margin: 0px;border: 0px;padding: 0px;">
        <li id="dummy" class="tab" style="border:0px">
            <ul id="targetUL">

            </ul>
        </li>
    </ul>
</div>
</body>
</html>
