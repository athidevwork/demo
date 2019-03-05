<%--
  Description: Maintain COI Renewal Event page

  Author: Dzhang
  Date: Jun 21, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2010       dzhang      Renamed this file.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainCoiRenewalEvent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="maintainCoiRenewalEvent.do" method=post name="COIRenewalEventForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <td align=center>
        <fmt:message key="pm.coiRenewal.renewalEvent.resultHeader" var="panelTitleIdForEventResultGrid"
                     scope="page"/>
        <%
            String panelTitleIdForEventResultGrid = (String) pageContext.getAttribute("panelTitleIdForEventResultGrid");
        %>
        <oweb:panel panelTitleId="panelTitleIdForEventResultGrid" panelContentId="panelContentIdForEventResultGrid"
                    panelTitle="<%= panelTitleIdForEventResultGrid %>">
            <tr>

                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value=""/>
                        <jsp:param name="divId" value="COIRenewalEventFilter"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="actionItemGroupId" value="PM_BAT_COI_REN_FILTER_AIG"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_COI_RENEWAL_EVENT_FILTER"/>
                    </jsp:include>
                </td>
            </tr>
            <c:if test="${dataBean != null && dataBean.rowCount != 0}">
                <tr>
                    <td align=center>
                        <fmt:message key="pm.coiRenewal.renewalEvent.gridHeader"
                                     var="panelTitleIdForCOIRenewalEventListGrid"
                                     scope="page"/>
                        <%
                            String panelTitleIdForCOIRenewalEventListGrid = (String) pageContext.getAttribute("panelTitleIdForCOIRenewalEventListGrid");
                        %>
                        <oweb:panel panelTitleId="panelTitleIdForCOIRenewalEventListGrid"
                                    panelContentId="panelContentIdForpanelTitleIdForCOIRenewalEventListGrid"
                                    panelTitle="<%= panelTitleIdForCOIRenewalEventListGrid %>">
                            <tr>
                                <td colspan="6" align=center>
                                    <c:set var="gridDisplayFormName" value="COIRenewalEventForm" scope="request"/>
                                    <c:set var="gridDisplayGridId" value="COIRenewalEventListGrid"
                                           scope="request"/>
                                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                                </td>
                            </tr>
                        </oweb:panel>

                        <tr>
                            <td>&nbsp;</td>
                        </tr>
                    </td>
                </tr>
            </c:if>
        </oweb:panel>
    </td>

    <c:if test="${dataBean != null && dataBean.rowCount != 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.coiRenewal.renewalEventDetail.gridHeader" var="panelTitleIdForEventDetailGrid"
                         scope="page"/>
            <%
                String panelTitleIdForEventDetailGrid = (String) pageContext.getAttribute("panelTitleIdForEventDetailGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForEventDetailGrid" panelContentId="panelContentIdForEventDetailGrid"
                        panelTitle="<%= panelTitleIdForEventDetailGrid %>">
                <tr>
                    <td>
                        <iframe id="iframeCoiEventDetails" scrolling="no" allowtransparency="true" width="100%"
                                height="300" frameborder="0" src=""></iframe>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>

<jsp:include page="/core/footer.jsp"/>