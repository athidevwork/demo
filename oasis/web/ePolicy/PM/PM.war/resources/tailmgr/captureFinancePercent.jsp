<%--
  Description:

  Author: yhchen
  Date: Aug 16, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script language="javascript" src="<%=appPath%>/tailmgr/js/captureFinancePercent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<FORM action="/tailmgr/captureFinancePercent.do" method="POST" NAME="captureFinancePercentFORM">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=left>
            <fmt:message key="pm.maintainTail.captureFinancePercent.formHeader" var="captureFinancePercentHeader"
                         scope="request"/>
            <% String captureFinancePercentHeader = (String) request.getAttribute("captureFinancePercentHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  captureFinancePercentHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_TAIL_FIN_PCT_AIG"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
