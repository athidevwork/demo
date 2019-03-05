<%@ page language="java" %>
<%--
  Description: Select Vehicle List Popup

  Author: cyzhao
  Date: Jan 12, 2010


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/19/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type="text/javascript" src="<%=cisPath%>/vehiclemgr/vehiclefindmgr/js/selectVehiclePopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<oweb:constant constantClass="dti.ci.vehiclemgr.VehicleFields"/>

<form name="CISelectVehicleListForm" action="ciVehicleSearchPop.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="pk" value="<c:out value="${pk}"/>"/>
    <input type="hidden" name="entityDescription" value="<c:out value="${entityDescription}"/>"/>

    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerTextLayerId" value="CI_VEHICLE_SEARCH_CRITERIA"/>
                <jsp:param name="divId" value="vehicleFilterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayerIds" value="CI_VEHICLE_SEARCH_CRITERIA"/>
                <jsp:param name="actionItemGroupId" value="CI_VEH_SELECT_SEARCH_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <c:if test="${dataBean.rowCount> 0}">
        <tr>
            <td colspan="6">
                <oweb:panel panelContentId="panelContentForVehicleList" hasTitle="false">
                    <tr>
                        <td>
                            <c:set var="gridDisplayFormName" value="CISelectVehicleListForm" scope="request"/>
                            <c:set var="gridDisplayGridId" value="vehicleListGrid" scope="request"/>
                            <c:set var="datasrc" value="#vehicleListGrid1" scope="request"/>
                            <%@ include file="/core/gridDisplay.jsp" %>
                        </td>
                    </tr>
                </oweb:panel>
            </td>
        </tr>
    </c:if>

     <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_VEH_SELECT_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>