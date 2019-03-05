<%--
  Description: Process Org/Corp component.

  Author: yhyang
  Date: Jan 20, 2009


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
<script type="text/javascript" src="js/processCorpOrgComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="processCorpOrgComponentForm" action="processCorpOrgComponent.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processingCorpOrgComponent.search.header" var="discountSearchHeader" scope="request"/>
            <% String discountSearchHeader = (String) request.getAttribute("discountSearchHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= discountSearchHeader%>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="actionItemGroupId" value="PM_PROCESS_CODISC_SCH_AIG"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_OC_DISCOUNT_SEARCH"/>
            </jsp:include>
        </td>
    </tr>
   <tr>
        <td align=center>
        <fmt:message key="pm.processingCorpOrgComponent.member.header" var="discountMemberHeader"
                     scope="page"/>
        <% String discountMemberHeader = (String) pageContext.getAttribute("discountMemberHeader"); %>
        <oweb:panel panelTitleId="panelTitleIdForProcessDiscount" panelContentId="panelContentIdForProcessDiscount"
                    panelTitle="<%= discountMemberHeader %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="discountMemberList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="discountMemberListGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROCESS_CODISC_PRO_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>