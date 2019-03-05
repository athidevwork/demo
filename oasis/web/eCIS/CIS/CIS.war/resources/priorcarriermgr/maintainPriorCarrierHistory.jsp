<%--
  Description: JSP for Prior Carrier History
  
  Author: jdingle
  Date: Feb 18, 2009
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang         194157: Add buildNumber parameter to static file references to improve performance
  10/22/2018       dzou          grid replacement
  ---------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    //   String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    String entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.priorHistory.form.title");
%>
<%@include file="/core/headerpopup.jsp"%>
<%@ include file="/cicore/common.jsp" %>
<%-- include some js --%>
<script type='text/javascript' src="js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="priorcarriermgr/js/maintainPriorCarrierHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<!--form-->
<form name="CIPriorCarrierHistoryForm" action="ciPriorCarrierHistory.do" method="POST">
<jsp:include page="/cicore/commonFormHeader.jsp"/>
    <input type="hidden" name="pk" value="<c:out value="${pk}"/>"/>
<tr valign="top">
    <td colspan="6" class="tabTitle">
        <b><%=entityNameDisplay%></b>
    </td>
</tr>
<tr>
    <td>
        <oweb:message/>
    </td>
</tr>
<tr>
    <td>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="CIPriorCarrierHistoryForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="priorCarrierHistoryGrid" scope="request"/>
                    <c:set var="datasrc" value="#priorCarrierHistoryGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
    </td>
</tr>
<tr>
    <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
        <oweb:actionGroup actionItemGroupId="CI_PRIOR_CARHST_AIG"
                          cssColorScheme="blue" layoutDirection="horizontal">
        </oweb:actionGroup>
    </td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>

