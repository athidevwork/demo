<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.ci.trainingmgr.TrainingFields" %>
<%--
  Description: Maintain Special Handling JSP

  Author: kenney
  Date: Jan 24, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/15/2010       Kenney      Issue#106087: Load initial values when adding special handling
  08/11/2010       Ldong       Issue#110376: Add Note function
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/10/2018       dzou        Grid replacement
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script type="text/javascript" src="<%=appPath%>/demographic/clientmgr/specialhandlingmgr/js/maintainSpecialHandling.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="specialHandlingListForm" action="maintainSpecialHandling.do" method=post>
      <jsp:include page="/cicore/commonFormHeader.jsp"/>
      <input type="hidden" name="<%=TrainingFields.PK_PROPERTY%>" value="<%=request.getParameter(TrainingFields.PK_PROPERTY)%>">
    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForSpecialHandlingList"
                        panelContentId="panelContentIdForSpecialHandlingList" panelTitleLayerId="CI_SPH_GH">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_SPH_GRID_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="specialHandlingListForm" scope="request"/>
                        <c:set var="gridDetailDivId" value="specialHandlingDetailDiv" scope="request"/>
                        <c:set var="gridDisplayGridId" value="specialHandlingListGrid" scope="request" />
                        <c:set var="gridSizeFieldIdPrefix" value="specialHandling_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                    <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=detailTitle%>"/>
                            <jsp:param name="gridID" value="specialHandlingListGrid"/>
                            <jsp:param name="divId" value="specialHandlingDetailDiv"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_SPH_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
