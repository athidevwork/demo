<%--
  Description:

  Author: Bhong
  Date: Mar 31, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/04/2010       syang       Initialize system parameter PM_CUST_SURCG_POINTS for JavaScript to use.
  11/13/2018       tyang    194100   -Add buildNumber Parameter
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
<script type="text/javascript" src="js/maintainSurchargePoints.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<%-- Show error message --%>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>
<br>
<!-- form -->
<form action="maintainSurchargePoints.do" name="surchargePointsForm" method="post">
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr><td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
    </td></tr>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
        <!-- Display total fields -->
        <tr>
            <td align=center>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="isGridBased" value="false" />
                    <jsp:param name="isLayerVisibleByDefault" value="true" />
                    <jsp:param name="excludePageFields" value="true" />
                </jsp:include>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td align=center>
                <fmt:message key="pm.maintainSurchargePoints.page.header" var="panelTitleForSurChrgPts" scope="page"/>
                <%
                    String panelTitleForSurChrgPts = (String) pageContext.getAttribute("panelTitleForSurChrgPts");
                %>
                <oweb:panel panelTitleId="panelTitleIdForSurChrgPts" panelContentId="panelContentIdForSurChrgPts" panelTitle="<%= panelTitleForSurChrgPts %>" >
                <!-- Display grid -->
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="surchargePointsForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="surchargePointsGrid" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <c:set var="selectable" value="true"/>
                        <c:set var="gridDetailDivId" value="surchargePointsDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <!-- Display form -->
                <tr>
                    <td align=center>
                        <fmt:message key="pm.maintainSurchargePoints.form.header" var="formHeader"/>
                        <c:set var="datasrc" value="#surchargePointsGrid1" scope="request"/>

                        <% String formHeader = (String) request.getAttribute("formHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=  formHeader %>" />
                            <jsp:param name="isGridBased" value="true" />
                            <jsp:param name="excludeAllLayers" value="true" />
                            <jsp:param name="divId" value="surchargePointsDetailDiv" />
                        </jsp:include>
                    </td>
                </tr>
                </oweb:panel>

            </td>
        </tr>

      <tr>
        <td colspan="7" align="center">
             <oweb:actionGroup actionItemGroupId="PM_SUR_PNT_AIG" />
        </td>
    </tr>
        <%--</c:if>--%>
<%
    // Initialize Sys Parms for JavaScript to use
    String pmCustSurgPoints = SysParmProvider.getInstance().getSysParm("PM_CUST_SURCG_POINTS", "N");
%>
<script type="text/javascript">
    setSysParmValue("PM_CUST_SURCG_POINTS", '<%=pmCustSurgPoints %>');
</script>
<jsp:include page="/core/footerpopup.jsp"/>