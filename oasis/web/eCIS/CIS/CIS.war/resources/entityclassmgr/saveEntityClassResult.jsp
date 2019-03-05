<%@ page language="java" %>
<%--
  Description:

  Author: kshen
  Date: 4/3/2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/18/2018       ylu         Issue 195835: remove unnecessary code
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type="text/javascript">
    function handleOnLoad() {
        closeWindow(function () {
            var parentWindow = getParentWindow();
            if (parentWindow && !parentWindow.closed && parentWindow.refreshPage) {
                parentWindow.refreshPage();
            }
        });
    }
</script>

<FORM name="saveEntityClassResult" method="POST">

<jsp:include page="/core/footerpopup.jsp" />