<%--
  Description:

  Author: Awu
  Date: 05/22/2013


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/addUnderwriter.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="underwriterForm" action="addUnderwriter.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <fmt:message key="pm.addUnderwriter.addForm.header" var="addUnderwriterFormHeader" scope="request"/>
            <% String addUnderwriterInfoFormHeader = (String) request.getAttribute("addUnderwriterFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  addUnderwriterInfoFormHeader %>" />
                <jsp:param name="divId" value="addUnderwriterDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />

            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_ADD_UNDERWRITER_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
