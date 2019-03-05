<%@ page import="dti.oasis.struts.IOasisAction" %>
<%--
  Description:  This JSP renders the tab menu for the provided tab menu group.

  1. tabMenuGroupId - String value that represents the menu id for the tab menu group.
                      Default is empty string - Tab Menu will not be displayed for the default value.

  2. menuIdsToExclude - Comma delimited string value that represents a list of tab menu item ids to exclude.
                        Default is empty string.

  Author: mmanickam
  Date: May 2, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/09/2010        clm        enhance the funtionality to support tab selection by adding selectedMenuIds property.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<c:if test="${UIStyleEdition=='2'}">
    <c:set var="topborderstyle" value=""></c:set>
    <c:if test="${empty tabMenuGroupId}">
        <c:set var="tabMenuGroupId" value=""></c:set>
        <c:set var="topborderstyle" value="border-top:1px solid #ccc;"></c:set>
    </c:if>
    <c:if test="${empty menuIdsToExclude}">
        <c:set var="menuIdsToExclude" value="${tabMenuIdsToExclude}"></c:set>
        <c:if test="${menuIdsToExclude==null}">
            <c:set var="menuIdsToExclude" value=""></c:set>
        </c:if>
    </c:if>
    <c:if test="${empty selectedMenuIds}">
        <c:set var=" selectedMenuIds" value="${ selectedTabMenuIds}"></c:set>
        <c:if test="${ selectedMenuIds==null}">
            <c:set var=" selectedMenuIds" value=""></c:set>
        </c:if>
    </c:if>


    <%-- Remove the following <tr><td><table><tr><td>, once UI2 is fully converted to use DIV --%>
    <tr>
        <td colspan=8 valign="top">
            <c:if test="${tabMenuGroupId!=''}">
            <oweb:tabMenu menuGroupId='<%= (String) pageContext.getAttribute("tabMenuGroupId")%>'
                          selectedMenuIds ='<%= (String) pageContext.getAttribute("selectedMenuIds")%>'
                          menuIdsToExclude='<%= (String) pageContext.getAttribute("menuIdsToExclude")%>'>
            </oweb:tabMenu>
                <script>
                    var tabMenuGroupId = '<%= (String) pageContext.getAttribute("tabMenuGroupId")%>';
                    var tabMenuULObject = getObjectById(tabMenuGroupId);
                    $(tabMenuULObject).jMenu();
                </script>
            </c:if>
            <table width=100% class="tabHeader" cellpadding=2 cellspacing=0 style="margin:0;">
                    <%--
                    <div class="tabHeader" style="<c:out value="${topborderstyle}"></c:out>" >
                      <div class="tabDetail">
                    --%>

                    <%-- Remove all JSTL variables that has been set --%>
                    <c:remove var="tabMenuGroupId"></c:remove>
                    <c:remove var="selectedMenuIds"></c:remove>    
                    <c:remove var="menuIdsToExclude"></c:remove>
</c:if>