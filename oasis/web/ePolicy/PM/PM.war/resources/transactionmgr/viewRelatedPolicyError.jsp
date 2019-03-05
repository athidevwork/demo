<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%--
  Description: View Related Policy Errors page

  Author: jmp
  Date: June 19, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/31/2013       fcb         153463 - centered alligned the bottom buttons.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/viewRelatedPolicyError.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="<%=appPath%>/transactionmgr/viewRelatedPolicyError.do" method="POST" name="viewRelatedPolicyError">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td><oweb:message/></td>
                </tr>
            </table>
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
            <fmt:message key="pm.viewRelatedPolicyError.header" var="panelTitleForViewPolError" scope="page"/>
            <%
                String panelTitleForViewPolError = (String) pageContext.getAttribute("panelTitleForViewPolError");
            %>
            <oweb:panel panelTitleId="panelTitleIdForViewPolError" panelContentId="panelContentIdForViewPolError" panelTitle="<%= panelTitleForViewPolError %>" >

                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="relatedPolicyList" scope="request" />
                        <c:set var="gridDisplayGridId" value="relatedPolicyListListGrid" scope="request" />
                        <c:set var="datasrc" value="#relatedPolicyListGrid1" scope="request" />
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_VU_POL_ERR_AIG"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp" />



