<%--
  Description:

  Author: Bhong
  Date: Mar 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/18/2011       syang       121208 - Add grid form PM_SEL_COVG_FORM for grouping coverage.
  08/12/2011       syang       121208 - Handle the grid and form side by side per coverageDetailDisplay.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<script type="text/javascript" src="js/selectProductCoverage.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name ="selectCoverageForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="coverageDetailDisplay" value="<c:out value="${coverageDetailDisplay}"/>">
    <tr>
        <td align=center <c:if test="${coverageDetailDisplay eq 'Y'}"> width="58%" </c:if> >
            <fmt:message key="pm.selectCoverage.header"var="panelTitleForAddCovg" scope="page"/>
            <%
                String panelTitleForAddCovg = (String) pageContext.getAttribute("panelTitleForAddCovg");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAddCovg" panelContentId="panelContentIdForAddCovg" panelTitle="<%= panelTitleForAddCovg %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectCoverageForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="selectCoverageGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="selectCoverageDetailDiv" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <c:set var="gridSortable" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
         </td>
         <td id="selectCoverageDetail" style="vertical-align:top" <c:if test="${coverageDetailDisplay eq 'Y'}"> width="42%" </c:if>>
            <fmt:message key="pm.selectCoverage.coverage.detail.header"var="panelTitleForAddCovgDetail" scope="page"/>
            <% String panelTitleForAddCovgDetail = (String) pageContext.getAttribute("panelTitleForAddCovgDetail"); %>
            <c:set var="datasrc" value="#selectCoverageGrid1" scope="request"/>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= panelTitleForAddCovgDetail%>"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="divId" value="selectCoverageDetailDiv"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_SEL_COVG_FORM"/>
            </jsp:include>
        </td>
    </tr>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEL_COVG_AIG"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>