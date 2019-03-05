<%--
  Description: view insured information.

  Author: tcheng
  Date: June 29, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/04/2013       adeng       139879 - Added panel title for insured information list.
  03/20/2017       eyin        180675 - Changed message tag for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/viewInsuredInfo.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="insuredInfoList" action="viewInsuredInfo.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.insuredInfo.insuredInfoList.header" var="panelTitleForInsuredInfo" scope="page">
                <fmt:param value="${policyHeader.riskHeader.riskName}"/>
            </fmt:message>

            <%
                String panelTitleForInsuredInfo = (String) pageContext.getAttribute("panelTitleForInsuredInfo");
            %>
            <oweb:panel panelTitleId="panelTitleIdForViewInsuredInfo" panelContentId="panelContentIdForViewInsuredInfo"
                        panelTitle="<%= panelTitleForInsuredInfo %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="insuredInfoList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="insuredInfoListGrid" scope="request"/>
                        <c:set var="datasrc" value="#insuredInfoListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_INSURED_INFO_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>