<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="dti.oasis.workflowmgr.WorkflowAgent" %>
<%--
  Description:
  This page shows information after multi cancel is finished, and will invoke the workflow if is necessary
  Author: yhchen
  Date: Jul 17, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/03/2010       syang      Issue 111417 - Added "isTailCreated", it is used to check whether the tail was generated.
  09/13/2010       wfu         111776: Replaced hardcode string with resource definition
  04/09/2011       Joe        94232 - call performClosePage() if not create tail and default ibnr active risk
  08/20/2014       jyang      issue 156829 - Removed useless code, because the closeMultiCancelPage only works
                                             for multi-cancel COI holder now.
  11/15/2018       lzhang     194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%-- Show error message --%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/closeMultiCancelPage.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript">
    <%if(((YesNoFlag)request.getAttribute("refreshPage")).booleanValue()){
    %>
      refreshPage();
    <%  }%>
</script>

