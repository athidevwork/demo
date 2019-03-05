<%--
  Description: maintain excess coverage.

  Author: yhyang
  Date: April 02, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/13/2017       eyin         Issue 180675 - Changed the error msg to be located in parent frame for UI change.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainExcessCoverage.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
</script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="priorCarrierList" action="maintainExcessCoverage.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainExcessCoverage.prior.header" var="priorCarrierHeader" scope="page"/>
            <% String priorCarrierHeader = (String) pageContext.getAttribute("priorCarrierHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForPriorCarrierHeader"
                        panelContentId="panelContentIdForPriorCarrierHeader"
                        panelTitle="<%= priorCarrierHeader %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_EXCESS_COVERAGE_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="priorCarrierList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="priorCarrierListGrid" scope="request"/>
                        <c:set var="datasrc" value="#priorCarrierListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="priorCarrier" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="divId" value="priorCarrier"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_EXCESS_COVG_PRIOR_FORM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainExcessCoverage.current.header" var="panelTitleForCurrentCarrierHeader"
                         scope="page"/>
            <%
                String panelTitleForCurrentCarrierHeader = (String) pageContext.getAttribute("panelTitleForCurrentCarrierHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForCurrentCarrierHeader"
                        panelContentId="panelContentIdForCurrentCarrierHeader"
                        panelTitle="<%= panelTitleForCurrentCarrierHeader %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="divId" value="currentCarrier"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_EXCESS_COVG_CURRENT_FORM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_EXCESS_COVG_SAVE_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>