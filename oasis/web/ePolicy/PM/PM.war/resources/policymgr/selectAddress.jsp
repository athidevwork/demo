<%--
  Description: Select Address for policyholder or COI Holder

  Author: Joe Shen
  Date: Feb 19, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
   08/26/2011       dzhang     121130: Added hidden fields riskStatus.
   03/10/2017       wli        180675 - Changed the error msg to be located in parent frame for UI change.
   09/21/2017       eyin       Issue 169483 - Modified handleOnButtonClick() to set address info for Exposure.
   11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/selectAddress.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
    var isFromExposure = '<%=request.getParameter("isFromExposure")==null ? "" : request.getParameter("isFromExposure")%>';
</script>


<form action="selectAddress.do" name="selectAddressForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="type" value="<c:out value="${type}"/>"/>
    <input type="hidden" name="entityId" value="<c:out value="${entityId}"/>"/>
    <input type="hidden" name="entityRoleId" value="<c:out value="${entityRoleId}"/>"/>
    <input type="hidden" name="riskStatus" value="<c:out value="${param.riskStatus}"/>"/>
    <input type="hidden" name="riskBaseRecordId" value="<c:out value="${param.riskBaseRecordId}"/>"/>

    <tr>
        <%
            if(pmUIStyle.equals("T")) {
        %>
        <oweb:message displayMessagesOnParent="true"/>
        <%
            }
        %>
        <%
            if(pmUIStyle.equals("B")) {
        %>
        <oweb:message/>
        <%
            }
        %>
    </tr>                                                            
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>

    <!-- Display Available Address grid -->
    <tr>
        <td align="center">
            <fmt:message key="pm.selectAddress.header" var="panelTitleForRiskRelation" scope="page"/>
            <%
                String panelTitleForAvailableAddress = (String) pageContext.getAttribute("panelTitleForRiskRelation");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAvailableAddress"
                        panelContentId="panelContentIdForAvailableAddress" panelTitle="<%=panelTitleForAvailableAddress%>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="selectAddressForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="availableAddressListGrid" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <c:set var="selectable" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%-- Display buttons --%>
    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="PM_SEL_ADDRESS_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
