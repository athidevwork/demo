<%--
  Description: Process Rm component.

  Author: yhyang
  Date: Jan 15, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang    194100   -Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainRmComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="processingDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="processingDetailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="processRmComponentForm" action="maintainRmComponent.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processingRmComponent.event.header" var="processingEventHeader" scope="page"/>
            <% String ProcessingEvent = (String) pageContext.getAttribute("processingEventHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForProcessingEvent"
                        panelContentId="panelContentIdForProcessingEvent"
                        panelTitle="<%= ProcessingEvent %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_PROCESS_RM_CP_ADD_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="processingEventList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="processingEventListGrid" scope="request"/>
                        <c:set var="datasrc" value="#processingEventListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="divId" value="processingEvent"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_RM_DISCOUNT_EVENT_FM"/>
                        </jsp:include>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <oweb:actionGroup actionItemGroupId="PM_PROCESS_RM_CP_SAVE_AIG"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processingRmComponent.detail.header" var="panelTitleForProcessingDetailHeader"
                         scope="page"/>
            <%
                String panelTitleForProcessingDetail = (String) pageContext.getAttribute("panelTitleForProcessingDetailHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForProcessingDetail"
                        panelContentId="panelContentIdForProcessingDetail"
                        panelTitle="<%= panelTitleForProcessingDetail %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="processingDetailList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="processingDetailListGrid" scope="request"/>
                        <% dataBean = processingDetailListGridDataBean;
                            gridHeaderBean = processingDetailListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>