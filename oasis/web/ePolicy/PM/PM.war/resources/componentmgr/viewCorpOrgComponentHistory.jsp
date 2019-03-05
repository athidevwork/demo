<%--
  Description: Process Org/Corp component.

  Author: yhyang
  Date: Jan 20, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewCorpOrgComponentHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="processingDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="processingDetailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="viewCorpOrgComponentHistoryForm" action="viewCorpOrgComponentHistory.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processingRmComponent.event.header" var="processingEventHeader" scope="page"/>
            <% String panelTitleForProcessingEvent = (String) pageContext.getAttribute("processingEventHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForProcessingEvent"
                        panelContentId="panelContentIdForProcessingEvent"
                        panelTitle="<%= panelTitleForProcessingEvent %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="processingEventList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="processingEventListGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processingRmComponent.detail.header" var="processingDetailHeader"
                         scope="page"/>
            <%
                String panelTitleForProcessingDetail = (String) pageContext.getAttribute("processingDetailHeader");
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
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROCESS_CODISC_HIS_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>