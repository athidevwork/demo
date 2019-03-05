<%--
  Description:

  Author: zlzhu
  Date: Jul 20, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Jul 20, 2007         zlzhu      Created
  04/25/2013       jshen       143625 - Added one hidden field riskBaseRecordId which will be passed in from parent page
  03/13/2017       eyin        180675 - Changed the error msg to be located in parent frame for UI change.
  11/14/2017       lzhang      189417 - add hidden isFromCoverage field and change logic of error msg show
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp"%>
<script type="text/javascript" src="<%=appPath%>/coveragemgr/minitailmgr/js/processMinitail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="minitailGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="minitailGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/coveragemgr/minitailmgr/processMinitail.do" method=post name="minitailForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
    <input type="hidden" name="riskBaseRecordId" value="<c:out value="${riskBaseRecordId}"/>">
    <%if(request.getParameter("isFromCoverage")!=null){%>
        <input type=hidden name=isFromCoverage value="Y"/>
    <%}%>
    <tr>
        <td colspan=8>
            <%
                if(pmUIStyle.equals("T") && "Y".equals(request.getParameter("isFromCoverage"))) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
            } else{
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
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.processMinitail.riskCoverage.header" var="panelTitleIdForRiskCoverage" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String panelTitleIdForRiskCoverage = (String) pageContext.getAttribute("panelTitleIdForRiskCoverage");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRiskCoverage" panelContentId="panelContentIdForRiskCoverage" panelTitle="<%= panelTitleIdForRiskCoverage %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="minitailForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskCoverageGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <br>
            <fmt:message key="pm.processMinitail.minitail.header" var="panelTitleIdForMiniTail" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String panelTitleIdForMiniTail = (String) pageContext.getAttribute("panelTitleIdForMiniTail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForMiniTail" panelContentId="panelContentIdForMiniTail" panelTitle="<%= panelTitleIdForMiniTail %>">
                 <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="minitailForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="minitailGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="minitailDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#minitailGrid1" scope="request"/>
                        <% dataBean = minitailGridDataBean;
                           gridHeaderBean = minitailGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                 </tr>
            </oweb:panel>
            <tr>
                <td>&nbsp;</td>
            </tr>

            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#minitailGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="divId" value="minitailDetailDiv" />
                        <jsp:param name="isLayerVisibleByDefault" value="true" />
                        <jsp:param name="excludePageFields" value="true" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_MINI" />
                    </jsp:include>
                </td>
            </tr>
        </td>
    </tr>
    </c:if>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_MINI_TAIL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>