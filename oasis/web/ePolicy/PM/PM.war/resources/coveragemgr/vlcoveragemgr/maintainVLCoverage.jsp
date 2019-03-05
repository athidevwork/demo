<%--
  Description:
  JSP file to maintain VL Coverage page
  Author: yhchen
  Date: Jul 8, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/07/2014       kxiang      156492  - Moved tag <%@ include file="/core/invokeWorkflow.jsp" %> behind tag
                                         <%@ include file="/pmcore/common.jsp" %>.
  03/13/2017       eyin        180675  - Changed the error msg to be located in parent frame for UI change.
  11/13/2018       tyang       194100  - Add buildNumber Parameter
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
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/coveragemgr/vlcoveragemgr/js/maintainVLCoverage.js
?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="vlRiskList" action="<%=appPath%>/coveragemgr/vlcoveragemgr/maintainVLCoverage.do"
      method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
        <%
            if(pmUIStyle.equals("T")) {
        %>
        <oweb:message displayMessagesOnParent="true"/>
        <%
            }
        %>
        <%
            if(pmUIStyle.equals("B")) {
        %>
        <oweb:message/>
        <%
            }
        %>
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
            <fmt:message key="pm.maintainVLCoverage.riskList.header" var="panelTitleForVlRisk"
                         scope="page"/>
            <%
                String panelTitleForVlRisk = (String) pageContext.getAttribute("panelTitleForVlRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForVlRisk" panelContentId="panelContentIdForVlRisk"
                        panelTitle="<%=panelTitleForVlRisk%>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_VLCOVG_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="vlRiskList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="vlRiskListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="vlRiskDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#vlRiskListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td colspan="8" width="100%">
                        <div id="vlRiskDetailDiv" style="display:block; width:100%;">
                            <table width="100%">
                                <tr>
                                    <td align=center>
                                        <fmt:message key="pm.maintainVLCoverage.employeeInfo.header"
                                                     var="employeeFormHeader" scope="request"/>
                                        <% String employeeFormHeader = (String) request.getAttribute("employeeFormHeader"); %>
                                        <jsp:include page="/core/compiledFormFields.jsp">
                                            <jsp:param name="divId" value="employeelDiv"/>
                                            <jsp:param name="headerText" value="<%=  employeeFormHeader %>"/>
                                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                            <jsp:param name="includeLayersWithPrefix" value="PM_VLRISK_EMPDETAIL_FM"/>
                                            <jsp:param name="isGridBased" value="true"/>
                                        </jsp:include>
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td align=center>
                                        <fmt:message key="pm.maintainVLCoverage.rateInfo.header"
                                                     var="rateFormHeader" scope="request"/>
                                        <% String rateFormHeader = (String) request.getAttribute("rateFormHeader"); %>
                                        <jsp:include page="/core/compiledFormFields.jsp">
                                            <jsp:param name="divId" value="ratingDiv"/>
                                            <jsp:param name="headerText" value="<%=  rateFormHeader %>"/>
                                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                            <jsp:param name="includeLayersWithPrefix" value="PM_VLRISK_RATE_FM"/>
                                            <jsp:param name="isGridBased" value="true"/>
                                        </jsp:include>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_VLCOVG_SAVE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>