<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description:

  Author: bzhu
  Date: 4/11/13


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>

<script language="javascript" src="<%=cisPath%>/entityaddlemailmgr/js/entityAddlEmail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="electronicDistributionEmailForm" action="entityAddlEmail.do" method="POST">
    <input type="hidden" name="<%=ICIConstants.PK_PROPERTY%>"
           value="<%=request.getAttribute(ICIConstants.PK_PROPERTY)%>"/>
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <tr>
        <td colspan=7>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="7">
            <oweb:panel panelContentId="panelContentForList" panelTitleLayerId="CI_ETD_EMAIL_LIST_GH">
            <tr>
                <td colspan=7>
                    <oweb:actionGroup actionItemGroupId="CI_ETD_EMAIL_LIST_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td>
                    <c:set var="gridDisplayFormName" value="electronicDistributionEmailForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="electronicDistributionEmailListGrid" scope="request"/>
                    <c:set var="datasrc" value="#electronicDistributionEmailListGrid1" scope="request"/>
                    <c:set var="gridDetailDivId" value="electronicDistributionDetailDiv" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="headerText" value="Detail"/>
                        <jsp:param name="divId" value="electronicDistributionDetailDiv"/>
                        <jsp:param name="gridID" value="electronicDistributionEmailListGrid"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_ETD_EMAIL_DETAIL"/>
                        <jsp:param name="headerTextLayerId" value="CI_ETD_EMAIL_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="CI_ETD_EMAIL_AIG" layoutDirection="horizontal"
                              cssColorScheme="blue"/>
        </td>
    </tr>


<jsp:include page="/core/footerpopup.jsp"/>