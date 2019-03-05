<%--
  Description: calculate dividend.

  Author: wfu
  Date: Mar 30, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/calculateDividend.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="calculateDividendList" action="calculateDividend.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.calculate.parameter.header" var="panelTitleIdForFilterCalculateDividend" scope="page"/>
            <%
                String filterCalculateDividend = (String) pageContext.getAttribute("panelTitleIdForFilterCalculateDividend");
            %>
            <oweb:panel panelTitleId="panelTitleIdForCalculateDividendHeader"
                        panelContentId="panelContentIdForCalculateDividendHeader"
                        panelTitle="<%= filterCalculateDividend %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="calculateDividendDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_DIVIDEND_CAL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>