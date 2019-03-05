<%--
  Description: Maintain Batch Renewal Event page

  Author: Joe Shen
  Date: August 20, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  // 11/26/2010    dzhang      114880 - Added Panel title for this page.
  // 02/16/2012    wfu         126027 - Added logic to load new sysparm for JS using.
  // 11/15/2018    lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainBatchRenewalProcess.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="maintainBatchRenewalProcess.do" method=post name="batchRenewalProcessForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="detailUpdated" value="N"/>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.batchRenewalProcess.searchCriteria.Header" var="panelTitleIdForSearchCriteria"
                     scope="page"/>
            <%
                String panelTitleIdForSearchCriteria = (String) pageContext.getAttribute("panelTitleIdForSearchCriteria");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">                
                <jsp:param name="headerText" value="<%=panelTitleIdForSearchCriteria %>" />
                <jsp:param name="divId" value="batchRenewalFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludeAllLayers" value="true" />
                <jsp:param name="actionItemGroupId" value="PM_BAT_REN_FILTER_AIG" />
            </jsp:include>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount != 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.batchRenewalProcess.renewalEvent.gridHeader" var="panelTitleIdForBatchRenewal" scope="page"/>
            <%
                String panelTitleIdForBatchRenewal = (String) pageContext.getAttribute("panelTitleIdForBatchRenewal");
            %>
            <oweb:panel panelTitleId="panelTitleIdForBatchRenewal" panelContentId="panelContentIdForBatchRenewal"
                        panelTitle="<%= panelTitleIdForBatchRenewal %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_BAT_RENEW_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="batchRenewalProcessForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="batchRenewalEventListGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>

            <tr>
                <td>&nbsp;</td>
            </tr>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.batchRenewalProcess.renewalDetail.gridHeader" var="panelTitleIdForEventDetailGrid" scope="page"/>
            <%
                String panelTitleIdForEventDetailGrid = (String) pageContext.getAttribute("panelTitleIdForEventDetailGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForEventDetailGrid" panelContentId="panelContentIdForEventDetailGrid" panelTitle="<%= panelTitleIdForEventDetailGrid %>">
            <tr><td>
                <iframe id="iframeEventDetails" scrolling="no" allowtransparency="true" width="100%" height="300" frameborder="0" src=""></iframe>
            </td></tr>
            </oweb:panel>
        </td>
   </tr>
   </c:if>

   <%
       // Initialize Sys Parm for JavaScript to use
       String pmBatchRenprt  = SysParmProvider.getInstance().getSysParm("PM_BATCH_RENPRT", "Y");
   %>
   <script type="text/javascript">
       setSysParmValue("PM_BATCH_RENPRT", '<%=pmBatchRenprt%>');
   </script>
   
<jsp:include page="/core/footer.jsp"/>