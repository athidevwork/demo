<%--
  Description:

  Author: Bhong
  Date: Mar 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/20/2015       awu         164686 - Aligned the buttons to center and change it class style to Green with white text.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/reissuePolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="reissuePolicy.do" name ="reissuePolicyForm" method="POST">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

  <!-- It does not include policyHeader because:
  1): the field names used for this page are as same as policyHeader, so
  2): the field names for validators are not "changable".. they use termEffectiveFromDate, termEffectiveToDate.
  -->
   <c:if test="${process != 'policyReissued'}">
    <tr>
        <td colspan="6" align=center><br/>
            <fmt:message key="pm.transactionmgr.reissueprocessmgr.form.header" var="reissuePolicyHeader" scope="request"/>
            <% String reissuePolicyHeader = (String) request.getAttribute("reissuePolicyHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="reissuePolicyDetailDiv" />
                <jsp:param name="headerText" value="<%=  reissuePolicyHeader %>" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
     <tr>
         <td align="center">
             <oweb:actionGroup actionItemGroupId="PM_REISS_AIG"  layoutDirection="horizontal"/>
         </td>
     </tr>
  </c:if>
<jsp:include page="/core/footerpopup.jsp"/>