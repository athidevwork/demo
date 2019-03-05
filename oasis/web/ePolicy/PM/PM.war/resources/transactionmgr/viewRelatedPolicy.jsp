<%--
  Description: view related policy jsp

  Author: rlli
  Date: Aug 27, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/07/2011       ryzhao      103801 - Added form layer PAGE_FIELDS_LAYER to the page to display bottom portion.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script language="javascript" src="<%=appPath%>/transactionmgr/js/viewRelatedPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="relatedPolicyList" action="<%=appPath%>/transactionmgr/viewRelatedPolicy.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
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

             <fmt:message key="pm.viewRelatedPolInfo.policyList.header" var="panelTitleForRelatedPolicy" scope="page"/>
            <%
                String panelTitleForRelatedPolicy  = (String) pageContext.getAttribute("panelTitleForRelatedPolicy");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRelatedPolicy" panelContentId="panelContentIdForRelatedPolicy"
                        panelTitle="<%= panelTitleForRelatedPolicy %>">

            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="relatedPolicyList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="relatedPolicyListGrid" scope="request"/>
                    <c:set var="datasrc" value="#relatedPolicyListGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value=""/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="displayAsPanel" value="false"/>
                        <jsp:param name="includeLayerIds" value="PM_VIEW_CURRENT_POLICY_LAYER"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_REL_POL_AIG"/>
                </td>
            </tr>

        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>