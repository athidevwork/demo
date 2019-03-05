<%@ page language="java" %>
<%--
  Description: Choose Clients for Address Copy

  Author: bhong
  Date: Feb 07, 2007


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressCopy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="ciAddressCopy.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="entityId" value="<c:out value="${entityId}"/>"/>
    <input type="hidden" name="addressId" value="<c:out value="${addressId}"/>"/>

    <tr>
        <td colspan="6">
            <oweb:panel panelContentId="panelContentForClientList" hasTitle="false">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="addressCopyGrid" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center" >
            <oweb:actionGroup actionItemGroupId="CI_ADDR_COPY_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>