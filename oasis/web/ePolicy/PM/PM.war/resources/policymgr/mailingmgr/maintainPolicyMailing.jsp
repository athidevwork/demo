<%--
  Description: jsp file for maintain policy mailing
  Author: rlli
  Date: Dec 17, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/23/2010       wtian       Issue 107312 - Display the "Save" button in the bottom.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainPolicyMailing.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="mailingAttributeListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="mailingAttributeListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="policyMailingList" action="maintainPolicyMailing.do" method=post>
<input type="hidden" name="pageType" value="<%=request.getAttribute("pageType")%>">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>
<fmt:message key="pm.maintainPolicyMailing.searchMailing.header" var="filterHeader" scope="request"/>
<% String filterHeader = (String) request.getAttribute("filterHeader"); 
   String toBeSelectedMailingEvent=(String) request.getAttribute("toBeSelectedMailingEvent");%>
<script type="text/javascript">
    var toBeSelectedRowId =<%=toBeSelectedMailingEvent%>;
</script>
<tr>
    <td>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=  filterHeader %>"/>
            <jsp:param name="divId" value="mailingEventFilter"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_EVENT"/>
            <jsp:param name="actionItemGroupId" value="PM_MAILING_SEARCH_AIG"/>
        </jsp:include>
    </td>
</tr>
<c:if test="${requestScope.pageType=='popup'}">
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_VIEW_LAYER_AIG"/>
    </td>
</tr>
</c:if>
<tr>
    <td align=center>
        <fmt:message key="pm.maintainPolicyMailing.mailingEventList.header" var="panelTitleForMailingEvent"
                     scope="page"/>
        <%
            String panelTitleForMailingEvent = (String) pageContext.getAttribute("panelTitleForMailingEvent");
        %>
        <oweb:panel panelTitleId="panelTitleIdForMailingEvent" panelContentId="panelContentIdForMailingEvent"
                    panelTitle="<%= panelTitleForMailingEvent %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_MAILING_EVENT_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="mailingEventList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="mailingEventListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="mailingEventDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#mailingEventListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainPolicyMailing.mailingEventForm.header" var="mailingEventFormHeader"
                                 scope="request"/>
                    <% String mailingEventFormHeader = (String) request.getAttribute("mailingEventFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  mailingEventFormHeader %>"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_MAILING_EVENT"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.maintainPolicyMailing.mailingAttributeList.header" var="panelTitleForMailingAttribute"
                     scope="page"/>
        <%
            String panelTitleForMailingAttribute = (String) pageContext.getAttribute("panelTitleForMailingAttribute");
        %>
        <oweb:panel panelTitleId="panelTitleForMailingAttribute" panelContentId="panelContentIdForMailingAttribute"
                    panelTitle="<%= panelTitleForMailingAttribute %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_MAILING_ATTRIBUTE_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="mailingAttributeList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="mailingAttributeListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="mailingAttributeDetailDiv" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="detail_"/>
                    <%
                        dataBean = mailingAttributeListGridDataBean;
                        gridHeaderBean = mailingAttributeListGridHeaderBean;
                    %>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>            
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#mailingAttributeListGrid1" scope="request"/>
                    <fmt:message key="pm.maintainPolicyMailing.mailingAttributeForm.header"
                                 var="mailingAttributeFormHeader"
                                 scope="request"/>
                    <% String mailingAttributeFormHeader = (String) request.getAttribute("mailingAttributeFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  mailingAttributeFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="mailingAttributeDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_MAILING_ATTRIBUTE"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.maintainPolicyMailing.mailingRecipientList.header" var="panelTitleIdForMailingRecipient"
                     scope="page"/>
        <%


            String panelTitleIdForMailingRecipientGrid = (String) pageContext.getAttribute("panelTitleIdForMailingRecipient");


        %>
        <oweb:panel panelTitleId="panelTitleIdForSeparateLimitsGrid" panelContentId="panelContentIdForChangeDetailGrid"
                    panelTitle="<%= panelTitleIdForMailingRecipientGrid %>">
            <tr>
                <td>
                    <iframe id="iframeMailingRecipient" scrolling="no" allowtransparency="true" width="98%" height="340"
                            frameborder="0" src=""></iframe>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<c:if test="${requestScope.pageType=='popup'}">
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_VIEW_LAYER_AIG"/>
    </td>
</tr>
</c:if>
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_POLICY_MAILING_AIG" />
    </td>
</tr>