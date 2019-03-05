<%--
    Description: Standard dti footer for popup pages

    Instructions for creating your own JSP.
    1. Include the headerpopup.jsp at the beginning of your JSP
    2. Provide your content.
       It should be in the context of an existing table element.
       Create and close your own row, cell & embedded table elements only.
    3. Include footerpopup.jsp at the end of your JSP

    Sample: your.jsp
    ---------------------------------
    <%@ include file="headerpopup.jsp" %>
    <form name="myform" method="post" action="foo.do">
    <tr>
    <td>My content</td>
    </tr>
    </form>
    <jsp:include page="footerpopup.jsp" />
    ---------------------------------

    Author: jbe
    Date: Nov 13, 2003


    Revision Date    Revised By  Description
    ---------------------------------------------------
    01/11/2006	     Gxc	 Issue 54932 - Added label.footer.copyright
				 instead of hardcoding
    01/25/2006       gxc	 Add taglib for fmt
    01/23/2007       lmm        Reformated UI with tables
    01/23/2007       wer        Changed appPath to corePath, using the Module.getCorePath();
    11/27/2007       yhchen     Update parent window's token key
    04/02/2009       Fred       Block updating parent token if the pop window is
                                from different application
    10/18/2010       wfu        109875 - Added parameters used in calendarAll.js.
    07/01/2013       hxk        Issue 141840
                                Include logic for entity level security in CIS.
    08/28/2014       Elvin      Issue 156768: use isWindowOpenerExists to determine parent window status
    01/12/2-18       kshen      Grid replacement: load jqxGrid after pageEntitlement.
    06/22/2018       ylu        Issue 102591: remove extra "%"
    11/13/2018       wreeder    196147 - handle getParentWindow() method undefined
    ---------------------------------------------------
    (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.oasis.http.Module" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.util.MenuBean" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.request.RequestStorageManager" %>
<%@ page import="dti.oasis.session.pageviewstate.PageViewStateManager" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>

<%
    String corePath = Module.getCorePath(request);

    String pageViewStateId = "";
    if (request.getAttribute(dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW) != null) {
        pageViewStateId = (String) request.getAttribute(dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW);
        RequestStorageManager.getInstance().set(PageViewStateManager.TERMINATE_PAGE_VIEW_STATE, Boolean.FALSE);
    }
%>

<script type="text/javascript">
    function getPopupWidth() {
        var width = '<%= pageBean.getWidth() %>';
        width = (width < 0 ? null : width);
        return width;
    }

    function getPopupHeight() {
        var height = '<%= pageBean.getHeight() %>';
        height = (height < 0 ? null : height);
        return height;
    }

    function getPopupTop() {
        var top = '<%= pageBean.getTop() %>';
        top = (top < 0 ? null : top);
        return top;
    }

    function getPopupLeft() {
        var left = '<%= pageBean.getLeft() %>';
        left = (left < 0 ? null : left);
        return left;
    }
</script>

<c:choose>
<c:when test="${UIStyleEdition=='0'}">
    </table>
    <input type="hidden" name="OBREnforcingUpdateIndicator" value="">
    <input type="hidden" name="OBREnforcingFieldList" value="">
    <input type="hidden" name="OBRConsequenceFieldList" value="">
    <input type="hidden" name="OBRAllAccessedFieldList" value="">
    <input type="hidden" name="OBREnforcedResult" value="">
    <input type="hidden" name="OBRGridIdList" value="">
    <input type="hidden" name="OBRhasRuleForSave" value="">
    </td></tr>
    <c:if test="${skipHeaderFooterContent == false}">
        <tr>
            <td height="27" colspan="3" valign="top">
                <table border="0" cellpadding="0" cellspacing="0" width="983">
                    <tr align="right">
                        <td style="font-size:1px">&nbsp;</td>
                    </tr>
                    <tr valign="top">
                        <td width="100%" height="27" align="left">
                            <div style="position:relative">
                                <img
                                    style="position:absolute;height:27px; width:100%; top:-6px; left:0px;z-index:-1;overflow:hidden;"
                                    src="<%=corePath%>/images/footer.jpg">

                                <div align="right" class="copyright">&copy;
                                    <fmt:message key="label.footer.copyright"/>
                                    <a href="#" onmouseout="MM_swapImgRestore()"
                                       onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif',1)"><img
                                        align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtitext.gif"
                                        name="dtitext"
                                        border="0" id="dtitext">
                                    </a></div>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </c:if>
    </table>
    <input type="hidden" id="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" name="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" value="<%= pageViewStateId %>">
    <%-- Close the form created by the developer in the JSP that included this jsp --%>
    </form>
</c:when>
<c:when test="${UIStyleEdition=='1'}">
    <input type="hidden" id="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" name="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" value="<%= pageViewStateId %>">
    </form><%-- Close the form created by the developer in the JSP that included this jsp --%>
    </table><%-- Close the main content table --%>
    </td>
    </tr>
    <c:if test="${skipHeaderFooterContent == false}">
        <% if (request.getUserPrincipal() != null) { %>
        <tr>
            <td valign=bottom>
                <table class="copyright" cellspacing=0 cellpadding=0>
                    <tr>
                        <td valign="bottom" width=100%>
                            &copy;
                            <fmt:message key="label.footer.copyright"/>
                        </td>
                        <td>
                            <a href="#" onmouseout="MM_swapImgRestore()"
                               onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif',1)">
                                <img align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtifooter.gif"
                                     name="dtitext" border="0" id="dtitext">
                            </a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <% } %>
    </c:if>
    </table>
    </div> <%-- Close div content --%>
    </div> <%-- Close div bodyContent --%>
    </td>
    </tr>
    <c:if test="${skipHeaderFooterContent == false}">
        <% if (request.getUserPrincipal() == null) { %>
        <tr>
            <td>
                <table class="copyright" cellspacing=0 cellpadding=0>
                    <tr>
                        <td valign="bottom" width=100%>
                            &copy;
                            <fmt:message key="label.footer.copyright"/>
                        </td>
                        <td>
                            <a href="#" onmouseout="MM_swapImgRestore()"
                               onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif',1)">
                                <img align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtifooter.gif"
                                     name="dtitext" border="0" id="dtitext">
                            </a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <% } %>
    </c:if>
    </table>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${useJqxGrid}">
    <tr class="dti-hide"><td>
</c:when>
<c:otherwise>
    <tr style="display: none"><td>
</c:otherwise>
</c:choose>
    <input type="hidden" name="_isNonGridFieldChanged" value="false"/>

    <input type="hidden" name="OBREnforcingUpdateIndicator" value="<%=request.getAttribute("OBREnforcingUpdateIndicator")==null?"":request.getAttribute("OBREnforcingUpdateIndicator")%>"/>
    <input type="hidden" name="OBREnforcingFieldList" value="<%=request.getAttribute("OBREnforcingFieldList")==null?"":request.getAttribute("OBREnforcingFieldList")%>"/>
    <input type="hidden" name="OBRConsequenceFieldList" value="<%=request.getAttribute("OBRConsequenceFieldList")==null?"":request.getAttribute("OBRConsequenceFieldList")%>"/>
    <input type="hidden" name="OBRAllAccessedFieldList" value="<%=request.getAttribute("OBRAllAccessedFieldList")==null?"":request.getAttribute("OBRAllAccessedFieldList")%>"/>
    <input type="hidden" name="OBREnforcedResult" value="<%=request.getAttribute("OBREnforcedResult")==null?"":request.getAttribute("OBREnforcedResult")%>"/>
    <input type="hidden" name="OBRGridIdList" value="<%=request.getAttribute("OBRGridIdList")==null?"":request.getAttribute("OBRGridIdList")%>"/>
    <input type="hidden" name="OBRhasRuleForSave" value="<%=request.getAttribute("OBRhasRuleForSave")==null?"false":request.getAttribute("OBRhasRuleForSave")%>"/>

    <input type="hidden" id="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" name="<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW %>" value="<%= pageViewStateId %>">
    </td></tr>

    </form><%-- Close the form created by the developer in the JSP that included this jsp --%>
    </table>
    </td>
    </tr>
    </table>
    </div>

    </td>
    </tr>
    <!-- END: Main Body Content -->

    <c:if test="${skipHeaderFooterContent == false}">

        <!-- BEGIN: footer -->
        <tr><td>
        <div id="pageFooter" class="footer">&copy;
            <fmt:message key="label.footer.copyright"/>
        </div>
        </td></tr>
        <!-- END: footer -->
    </c:if>
    </table>
</c:otherwise>
</c:choose>
<link rel="stylesheet" type="text/css" href="<%=corePath%>/css/calendar.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"/>
<script type="text/javascript">
    // Added for Chinese Calender tools using
    var CAL_JAN                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Jan")%>';
    var CAL_FEB                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Feb")%>';
    var CAL_MAR                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Mar")%>';
    var CAL_APR                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Apr")%>';
    var CAL_MAY                 = '<%=MessageManager.getInstance().formatMessage("cal.month.May")%>';
    var CAL_JUN                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Jun")%>';
    var CAL_JUL                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Jul")%>';
    var CAL_AUG                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Aug")%>';
    var CAL_SEP                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Sep")%>';
    var CAL_OCT                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Oct")%>';
    var CAL_NOV                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Nov")%>';
    var CAL_DEC                 = '<%=MessageManager.getInstance().formatMessage("cal.month.Dec")%>';

    var CAL_JAN_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.January")%>';
    var CAL_FEB_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.February")%>';
    var CAL_MAR_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.March")%>';
    var CAL_APR_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.April")%>';
    var CAL_MAY_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.MayF")%>';
    var CAL_JUN_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.June")%>';
    var CAL_JUL_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.July")%>';
    var CAL_AUG_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.August")%>';
    var CAL_SEP_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.September")%>';
    var CAL_OCT_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.October")%>';
    var CAL_NOV_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.November")%>';
    var CAL_DEC_FULLNAME        = '<%=MessageManager.getInstance().formatMessage("cal.month.December")%>';

    var CAL_WEEK_SUN            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.sun")%>';
    var CAL_WEEK_MON            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.mon")%>';
    var CAL_WEEK_TUE            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.tue")%>';
    var CAL_WEEK_WED            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.wed")%>';
    var CAL_WEEK_THU            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.thu")%>';
    var CAL_WEEK_FRI            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.fri")%>';
    var CAL_WEEK_SAT            =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.sat")%>';

    var CAL_WEEK_SUN_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Sunday")%>';
    var CAL_WEEK_MON_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Monday")%>';
    var CAL_WEEK_TUE_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Tuesday")%>';
    var CAL_WEEK_WED_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Wednesday")%>';
    var CAL_WEEK_THU_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Thursday")%>';
    var CAL_WEEK_FRI_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Friday")%>';
    var CAL_WEEK_SAT_FULLNAME   =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.Saturday")%>';

    var CAL_INFO                =  '<%=MessageManager.getInstance().formatMessage("cal.info.about")%>';
    var CAL_ABOUT_DATE_PART1    =  '<%=MessageManager.getInstance().formatMessage("cal.about.date1")%>';
    var CAL_ABOUT_DATE_PART2    =  '<%=MessageManager.getInstance().formatMessage("cal.about.date2")%>';
    var CAL_PREV_YEAR           =  '<%=MessageManager.getInstance().formatMessage("cal.year.prev")%>';
    var CAL_PREV_MONTH          =  '<%=MessageManager.getInstance().formatMessage("cal.month.prev")%>';
    var CAL_GO_TODAY            =  '<%=MessageManager.getInstance().formatMessage("cal.goto.today")%>';
    var CAL_NEXT_MONTH          =  '<%=MessageManager.getInstance().formatMessage("cal.month.next")%>';
    var CAL_NEXT_YEAR           =  '<%=MessageManager.getInstance().formatMessage("cal.year.next")%>';
    var CAL_SEL_DATE            =  '<%=MessageManager.getInstance().formatMessage("cal.select.date")%>';
    var CAL_DRAG_MOVE           =  '<%=MessageManager.getInstance().formatMessage("cal.drag.move")%>';
    var CAL_PART_TODAY          =  '<%=MessageManager.getInstance().formatMessage("cal.part.today")%>';
    var CAL_WEEK_FIRSTDAY       =  '<%=MessageManager.getInstance().formatMessage("cal.weekday.firstday")%>';
    var CAL_CLOSE               =  '<%=MessageManager.getInstance().formatMessage("cal.label.close")%>';
    var CAL_TODAY               =  '<%=MessageManager.getInstance().formatMessage("cal.label.today")%>';
    var CAL_WEEKNUM             =  '<%=MessageManager.getInstance().formatMessage("cal.label.weeknum")%>';
    var CAL_DAY_SYMBOL          =  '<%=MessageManager.getInstance().formatMessage("cal.symbol.day")%>';
    var CAL_YEAR_SYMBOL         =  '<%=MessageManager.getInstance().formatMessage("cal.symbol.year")%>';
</script>
<script type="text/javascript" src="<%=corePath%>/js/calendarAll.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<link href="<%=corePath%>/css/oasiscustom.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>

<oweb:oasispageentitlements></oweb:oasispageentitlements>

<c:if test="${useJqxGrid}">
    <script type="text/javascript">
        // Init and load all jqxGrid on the page.
        dti.oasis.page.initAndLoadGrids();
    </script>
</c:if>

<script type="text/javascript">
     
    if (window.frameElement) {  //If this page is a divPopup
        var parentFrame = getParentFrame();
        var divPopup = parentFrame.getDivPopupFromDivPopupControl(this.frameElement);
        if (divPopup) {
            parentFrame.resizeByDivPopup(divPopup, getPopupWidth(), getPopupHeight());
            parentFrame.moveToDivPopup(divPopup, getPopupLeft(), getPopupTop());
        }
    }

    function isWindowOpenerExists() {
        var isExists = false;
        if (window.opener && !window.opener.closed) {
            try{
                if (window.opener.document) {
                    isExists = true;
                }
            } catch (Exception) {
                // here window.opener.closed is false but we fail to get access to parent window
                // do nothing but return false
            }
        }
        return isExists;
    }

    function updateParentToken(token, fromApp) {
        if (typeof getParentWindow != "undefined") {
            var parentWindow = getParentWindow();
            if (parentWindow != null && parentWindow != window) {
                try {
                    // Update parent token only when the parent and from windows are in same application
                    if (parentWindow.hasObject &&
                        parentWindow.hasObject('<%=Constants.TOKEN_KEY%>') &&
                        fromApp == parentWindow.getAppPath()) {
                        parentWindow.setInputFormField('<%=Constants.TOKEN_KEY%>', token);
                    }
                    if (eval("parentWindow.updateParentToken"))
                        parentWindow.updateParentToken(token, fromApp);
                } catch (Exception) {
                    // do nothing
                }
            }
        }
    }

    //update parent window's token key
    var token = '<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>';
    if (token != 'null')
        updateParentToken(token, getAppPath());

</script>
<%--
****************************************************************************************************
*** This is a common block of CIS entity level security code that will also be in the footer.jsp ***
****************************************************************************************************
--%>
<c:if test="${isEntityReadOnlyYN=='Y'}">

    <script type="text/javascript">

        // Do security on page load if we have no filters, we will do security after filtering when we have filters
        function handleOnLoad(){
            if  (!getSingleObject('FilterCriteria') ) {
                doSecurity();
            }
        }


        // if we have security set, Override filtering so we can call security after we filter
        // because sometimes filtering screws up our setting of ojbects to disabled.
        function handleFilterCisList() {

            if  (getSingleObject('FilterCriteria')) {
                filterList();
                setTimeout(function() {doSecurity()},500);
            }

        }
        // If we have security set, we don't we should not trigger page data change
        function isPageDataChanged() {

            return false;
        }

    </script>
</c:if>
<% if (request.getAttribute("AppDynamics_JS_FOOTER") != null) { %> <%=request.getAttribute("AppDynamics_JS_FOOTER")%> <% } %>
</body>
</html>
