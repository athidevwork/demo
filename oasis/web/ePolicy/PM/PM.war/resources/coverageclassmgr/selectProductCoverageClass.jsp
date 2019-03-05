<%--
  Description:

  Author: Jshen
  Date: Mar 29, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/20/2011       syang       121208 - Add grid form PM_SEL_COVG_CLASS_FORM for grouping coverage class.
  08/12/2011       syang       121208 - Handle the grid and form side by side per coverageClassDetailDisplay.
  01/10/2014       adeng       149172 - Added scope for grid sort.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<script type="text/javascript" src="js/selectProductCoverageClass.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="selectCoverageClassForm">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<input type="hidden" name="coverageClassDetailDisplay" value="<c:out value="${coverageClassDetailDisplay}"/>">
<input type="hidden" name="isButtonDoneEnable" value="NO"/>
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <td align=center <c:if test="${coverageClassDetailDisplay eq 'Y'}"> width="58%" </c:if> >
        <fmt:message key="pm.selectCoverageClass.header"var="panelTitleForAddClass" scope="page"/>
        <%
            String panelTitleForAddClass = (String) pageContext.getAttribute("panelTitleForAddClass");
        %>
        <oweb:panel panelTitleId="panelTitleIdForAddClass" panelContentId="panelContentIdForAddClass" panelTitle="<%= panelTitleForAddClass %>" >

        <tr>
            <td colspan="6" align=center>
                <c:set var="gridDisplayFormName" value="selectCoverageClassForm" scope="request"/>
                <c:set var="gridDisplayGridId" value="selectCoverageClassGrid" scope="request"/>
                <c:set var="cacheResultSet" value="false"/>
                <c:set var="selectable" value="true"/>
                <c:set var="gridDetailDivId" value="selectCoverageClassDetailDiv" scope="request"/>
                <c:set var="gridSortable" value="false" scope="request"/>
                <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        </oweb:panel>
    </td>
    <td id="selectCoverageClassDetail" style="vertical-align:top" <c:if test="${coverageClassDetailDisplay eq 'Y'}"> width="42%" </c:if>>
        <fmt:message key="pm.selectCoverageClass.detail.header"var="panelTitleForAddCovgClassDetail" scope="page"/>
        <% String panelTitleForAddCovgClassDetail = (String) pageContext.getAttribute("panelTitleForAddCovgClassDetail"); %>
        <c:set var="datasrc" value="#selectCoverageClassGrid1" scope="request"/>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%= panelTitleForAddCovgClassDetail%>"/>
            <jsp:param name="isGridBased" value="true"/>
            <jsp:param name="divId" value="selectCoverageClassDetailDiv"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="excludePageFields" value="true"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_SEL_COVG_CLASS_FORM"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_SEL_CLASS_AIG"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>