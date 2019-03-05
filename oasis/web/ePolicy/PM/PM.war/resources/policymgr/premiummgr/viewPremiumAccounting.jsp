<%--
  Description: view premium accounting data.

  Author: yhyang
  Date: June 29, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewPremiumAccounting.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="premiumAccountingList" action="viewPremiumAccounting.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
    <td align=center>
        <fmt:message key="pm.viewPremiumAccounting.date.header" var="premiumAccountingDateHeader"
                     scope="request"/>
        <% String premiumAccountingDateHeader = (String) request.getAttribute("premiumAccountingDateHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%= premiumAccountingDateHeader%>"/>
            <jsp:param name="divId" value="renewQuestSch"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
        </jsp:include>
    </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForPremiumAccountingHeader"
                        panelContentId="panelContentIdForPremiumAccountingHeader"
                        panelTitle="">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="premiumAccountingList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="premiumAccountingListGrid" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PREM_ACCOUNT_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>