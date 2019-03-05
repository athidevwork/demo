<%--
  Description: JSP file to load coverage billing.

  Author: Bhong
  Date: Aug 23, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang 194100 Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<c:set var="skipPageTitle" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>
<script type="text/javascript" src="<%=appPath%>/billingmgr/js/loadCoverageBilling.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="coverageBillingForm" action="<%=appPath%>/billingmgr/loadCoverageBilling.do" method="post">
    <input type="hidden" name="isBillingSetupDone" value="<c:out value="${isBillingSetupDone}"/>"/>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td>
            <iframe id="covgBillingIframe" scrolling="no" allowtransparency="true" width="100%"
                    height="800"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
