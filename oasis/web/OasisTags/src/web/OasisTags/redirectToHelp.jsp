<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: mgitelman
  Date: 9/7/2016
  Time: 3:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    Logger l = LogUtils.enterLog(getClass(), "redirectToHelp.jsp");
//    l.logp(Level.FINER, getClass().getName(), "jsp_service_method", "WE ARE IN");

    String url = request.getParameter("helpPageUrl");
//    l.logp(Level.FINER, getClass().getName(), "jsp_service_method", "URL: "+url);
%>

<script type="text/javascript">
    window.onfocus = function() {
        window.location.href = '<%=url%>';
    };
</script>