<%--
    Description: Standard dti footer

    Instructions for creating your own JSP.
    1. Include the header.jsp at the beginning of your JSP
    2. Create a single FORM element after the include.
       ****IMPORTANT**** DO NOT USE THE STRUTS HTML:FORM AND DO NOT
       **** CLOSE THE FORM ELEMENT IN YOUR JSP!!!!!
       *** REMEMBER:
       *** NO <HTML:FORM
       *** NO </FORM>!!!!!
       *** The footer.jsp has a mandatory hidden form field and then
       *** closes the form for you

    3. Provide your content.
       It should be in the context of an existing table element.
       Create and close your own row, cell & embedded table elements only.
    3. Include footer.jsp at the end of your JSP

    Sample: your.jsp
    ---------------------------------
    <%@ include file="header.jsp" %>
    <form name="myform" method="post" action="foo.do">
    <tr>
    <td>My content</td>
    </tr>
    <jsp:include page="footer.jsp" />
    ---------------------------------

    Author: jbe
    Date: Oct 15, 2003


    Revision Date    Revised By  Description
    ---------------------------------------------------
    01/11/2006	     Gxc	 Issue 54932 - Added label.footer.copyright
				 instad of hardcoding
    01/25/2006       gxc	 Add taglib for fmt
    01/23/2007       lmm        Reformated UI with tables
    01/23/2007       wer        Changed appPath to corePath, using the Module.getCorePath();
    11/27/2007       yhchen     Update parent window's token key
    04/02/2009       Fred       Block updating parent token if the pop window is
                                from different application
    10/11/2010       wfu        111776 - String literals refactoring.
    10/18/2010       wfu        109875 - Added parameters used in calendarAll.js.
    07/01/2013       hxk        Issue 141840
                                Include logic for entity level security in CIS.
    12/06/2013       Parker     148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
    08/28/2014       Elvin      Issue 156768: use isWindowOpenerExists to determine parent window status
    01/12/2-18       kshen      Grid replacement: load jqxGrid after pageEntitlement.
    11/13/2018       wreeder    196147 - handle getParentWindow() method undefined
    ---------------------------------------------------
    (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.util.MenuBean" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="org.apache.commons.beanutils.DynaBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.session.pageviewstate.PageViewStateManager" %>
<%@ page import="dti.oasis.request.RequestStorageManager" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
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
                                src="<%=corePath%>/images/footer.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">

                            <div align="right" class="copyright">&copy;
                                <fmt:message key="label.footer.copyright"/>
                                <a href="#" onmouseout="MM_swapImgRestore()"
                                   onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>',1)"><img
                                    align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtitext.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"
                                    name="dtitext"
                                    border="0" id="dtitext">
                                </a></div>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
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
    <% if (request.getUserPrincipal() != null) { %>
    <tr>
        <td valign=bottom colspan=2>
            <table class="copyright" cellspacing=0 cellpadding=0>
                <tr>
                    <td valign="bottom" width=100%>
                        &copy;
                        <fmt:message key="label.footer.copyright"/>
                    </td>
                    <td>
                        <a href="#" onmouseout="MM_swapImgRestore()"
                           onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>',1)">
                            <img align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtifooter.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"
                                 name="dtitext" border="0" id="dtitext">
                        </a>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <% } %>
    </table>
    </div> <%-- Close div content --%>
    </div> <%-- Close div bodyContent --%>
    </td>
    </tr>
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
                           onmouseover="MM_swapImage('dtitext','','<%=corePath%>/images/dtitextred.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>',1)">
                            <img align="top" hspace="10" vspace="0" src="<%=corePath%>/images/dtifooter.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"
                                 name="dtitext" border="0" id="dtitext">
                        </a>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <% } %>
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

    <!-- BEGIN: footer -->
    <tr><td>
    <div id="pageFooter" class="footer">&copy;
        <fmt:message key="label.footer.copyright"/>
    </div>
    </td></tr>
    <!-- END: footer -->
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
<script type="text/javascript">// Init and load all jqxGrid on the page.

    dti.oasis.page.initAndLoadGrids();
</script>
</c:if>

<c:if test="${globalSearchVisibility=='true' || globalSearchVisibility=='TRUE'}">
    <script type="text/javascript">
        var showGlobalSearchFunctionExists = eval("window.showGlobalSearch");
        if (showGlobalSearchFunctionExists) {
            showGlobalSearch();
        }
    </script>
</c:if>
<script type="text/javascript">
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
                // Update parent token only when the parent and from windows are in same application
                try {
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

    <%
        if (pageBean.getJumpNavigations() != null) {
          ArrayList jumpNavList = (ArrayList) pageBean.getJumpNavigations();
          if (jumpNavList.size() > 0 ) {
    %>
            var globalDropdownList = getObject("gblJumpNav");
            if(useJqxGrid) {
                hideShowElementAsInlineByClassName(getObject("gblJumpNav").parentElement, false);
            } else {
                getObject("gblJumpNav").parentElement.style.display = "inline";
            }
            globalDropdownList.options.add(new Option("<fmt:message key="label.header.page.jumpTo"/>", ""));
    <%
          }
          Iterator it = jumpNavList.iterator();
          while (it.hasNext()) {
              MenuBean menuBean = (MenuBean) it.next();
    %>
              globalDropdownList.options.add(new Option("<%= menuBean.getLabel() %>", "<%= menuBean.getId()%>"));
    <%
          }
      }
    %>
</script>
<%--
*********************************************************************************************************
*** This is a common block of CIS entity level security code that will also be in the footerpopup.jsp ***
*********************************************************************************************************
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
