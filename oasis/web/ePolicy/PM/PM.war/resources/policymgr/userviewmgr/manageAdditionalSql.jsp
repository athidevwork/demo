<%--
  Description: add additional SQL jsp

  Author: rlli
  Date: August 6, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  // 11/30/2010    dzhang     Issue #114880 - Add panel title.
  // 11/15/2018    eyin       Issue #194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/manageAdditionalSql.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="addAdditinalSql" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set scope="request" var="commentsCOLSPAN" value="7"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <fmt:message key="pm.manageAddtionalSql.addlSqlInfo.header" var="addlSqlHeader" scope="page"/>
    <%
        String addlSqlHeader = (String) pageContext.getAttribute("addlSqlHeader");
    %>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= addlSqlHeader %>"/>
                <jsp:param name="divId" value="addlSqlDiv" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_ADDI_SQL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>