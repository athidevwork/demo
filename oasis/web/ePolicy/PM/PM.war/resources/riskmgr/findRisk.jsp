<%--
  Description:

  Author: rlli
  Date: Oct 11, 2007


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/findRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="riskNameList" action="findRisk.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set scope="request" var="commentsCOLSPAN" value="7"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.findRiskList.searchCriteria.header" var="riskFilterHeader" scope="request"/>
            <% String riskFilterHeader = (String) request.getAttribute("riskFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  riskFilterHeader %>"/>
                <jsp:param name="divId" value="riskListFilter"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_SEARCH_RISK_AIG"/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.findRiskList.riskList.header" var="panelTitleForSearchRisk" scope="page"/>
            <%
                String panelTitleForSearchRisk = (String) pageContext.getAttribute("panelTitleForSearchRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSearchRisk" panelContentId="panelContentIdForSearchRisk"
                        panelTitle="<%= panelTitleForSearchRisk %>">
                <c:if test="${dataBean!=null}">
                    <tr>
                        <td colspan="6" align=center><br/>
                            <c:set var="gridDisplayFormName" value="riskNameList" scope="request"/>
                            <c:set var="gridDisplayGridId" value="riskNameListGrid" scope="request"/>
                            <c:set var="datasrc" value="#riskNameListGrid1" scope="request"/>
                            <c:set var="gridSortable" value="false" scope="request"/>
                            <%@ include file="/pmcore/gridDisplay.jsp" %>
                        </td>
                    </tr>
                </c:if>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEARCH_RISK_BOTTOM_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>