<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: xnie
  Date: September 27, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/27/2012       xnie        133766 - Initial version.
  11/16/2012       xnie        138948 - Added iframe for mass rerate detail.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/reRateResult.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="massReRateForm" action="massReRateAction.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.reRatePolicy.reRate.reRateResult.filter.formHeader" var="reRateResultFilterHeader" scope="request"/>
            <% String reRateResultFilterHeader = (String) request.getAttribute("reRateResultFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  reRateResultFilterHeader %>" />
                <jsp:param name="divId" value="reRateResultFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludeAllLayers" value="true" />
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_MASS_RERATE_RESULT_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount != 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.reRatePolicy.reRate.reRateResultList.header" var="panelTitleForReRateResult" scope="page"/>
            <%
                String panelTitleForReRateResult = (String) pageContext.getAttribute("panelTitleForReRateResult");
            %>
            <oweb:panel panelTitleId="panelTitleIdForReRateResult" panelContentId="panelContentIdForReRateResult" panelTitle="<%= panelTitleForReRateResult %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="massReRateForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="massReRateResultGrid" scope="request"/>
                    <c:set var="datasrc" value="#massReRateResultGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>
                    <iframe id="iframeMassReRateResultDetail" scrolling="no" allowtransparency="true" width="100%" height="335"
                            frameborder="0" src=""></iframe>
                </td>
            </tr>

            </oweb:panel>
        </td>
    </tr>

    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>