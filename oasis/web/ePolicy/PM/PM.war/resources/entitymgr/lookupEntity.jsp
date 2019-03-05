<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page language="java"%>
<%--
  Description: Lookup Entity page

  Author: sxm
  Date: May 2, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/17/2011       sxm         It took about half minutes to load 5,000+ entities classified as COI_HOLDER in MLMIC.
                               Changed cacheResultSet to TRUE to load the data in a background thread.
  03/18/2011       jshen       1. Remove javascript which was going to disable Select button
                               2. Hide grid if dataBean.rowCount <= 0
  03/10/2017       eyin        Added variable oParentWindow for UI tab style.
  05/23/2017       lzhang      185079 - pass parameter when call getParentWindow()
  07/12/2017       kxiang      185483 - Modified to check if IFrameWindow is defined before being called.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/lookupEntity.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
    var subFrameId = '<%=request.getParameter("subFrameId")==null ? "" : request.getParameter("subFrameId")%>';
    var oParentWindow;
    if(isEmpty(subFrameId)){
        oParentWindow = getParentWindow(true);
    }else{
        oParentWindow = eval("getParentWindow(true).getIFrameWindow") ? getParentWindow(true).getIFrameWindow() : getParentWindow(true);
    }
</script>

<form action="lookupEntity.do" method="POST" name="lookupEntityList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td><oweb:message/></td>
                </tr>
            </table>
        </td>
    </tr>     

<c:if test="${dataBean != null && dataBean.rowCount > 0}">
    <tr>
        <td>
            <fmt:message key="pm.lookupEntity.filterByName" var="panelTitleForFilter" scope="page"/>
            <%
                String panelTitleForFilter = (String) pageContext.getAttribute("panelTitleForFilter");
            %>

            <oweb:panel panelTitleId="panelTitleIdForFilter" panelContentId="panelContentIdForFilter"
                        panelTitle="<%=panelTitleForFilter%>">
                <tr>
                    <td colspan="6" align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="filterDiv"/>
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="isGridBased" value="false"/>
                        </jsp:include>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align="center">
                        <oweb:actionGroup actionItemGroupId="PM_SELENTITY_AIG"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td>
            <fmt:message key="pm.lookupEntity.header" var="panelTitleForEntity" scope="page"/>
            <%
                String panelTitleForEntity = (String) pageContext.getAttribute("panelTitleForEntity");
            %>
            <oweb:panel panelTitleId="panelTitleIdForEntity" panelContentId="panelContentIdForEntity"
                        panelTitle="<%= panelTitleForEntity %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="lookupEntityList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="lookupEntityGrid" scope="request"/>
                        <c:set var="datasrc" value="#lookupEntityGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
</c:if>

<tr>
    <td align= "center" >
       <oweb:actionGroup actionItemGroupId="PM_SELENTITY_GRID_AIG" />
   </td>
</tr>

<jsp:include page="/core/footerpopup.jsp" />
