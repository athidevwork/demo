<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="dti.oasis.app.ApplicationContext"%>
<%--
  Description: The common form header for CIS jsp pages.

  Author: Michael Li
  Date: Apr 24, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/01/2013       hxk         Issue 141840
                               Add panelId of FilterCriteria for use by security so
                               we re-enbable the filter elements.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    String filterPage = (String) request.getAttribute(ICIConstants.CIS_PAGE_INACT_FLR);
    if (StringUtils.isBlank(filterPage)) {
        filterPage = "";
    }

%>
<jsp:useBean id="cisHeaderFieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<script language="javascript" src="<%=cisPath%>/cicore/js/commonFilter.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<%
    if (pageBean != null && filterPage.indexOf(pageBean.getId()) == -1) {
%>
<c:if test="${cisHeaderFieldsMap['ListFilter'].isVisible}">
    <tr>
    <td width="100%" align="left">
    <oweb:panel panelContentId="panelContentForFilter"
                panelId="FilterCriteria"
                panelTitleId="panelTitleIdForFilter" panelTitle="Filter Criteria">
        <tr>
            <td width="100%">
                <table>
                    <tr>
                        <oweb:radio mapName="cisHeaderFieldsMap"
                                    fieldName="ListFilter" name="ListFilter"
                                    onclick="javascript:handleOnFilterChange(this);"/>
                    </tr>
                </table>
            </td>
        </tr>
    </oweb:panel>
    </td>
    </tr>
    <script type="text/javascript">
        function handleFilterCisList() {
            filterList();
        }
    </script>
</c:if>
<%
    }
%>