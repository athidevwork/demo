<%--
  Description:

  Author: Joe
  Date: April 10, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/09/2018       dzhang      196632 - Grid replacement.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ include file="headerpopup.jsp" %>

<form name="workflowExitAndRefreshParnetPage" action="workflow.do" method=post>

<script type="text/javascript">
    function handleOnLoad() {
        getParentWindow().refreshPage(true);
    }
</script>

<jsp:include page="footerpopup.jsp"/>