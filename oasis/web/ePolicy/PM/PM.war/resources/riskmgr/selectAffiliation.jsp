<%--
  Description:
  Select affiliation page for Risk Copy UC
  Author: yhchen
  Date: Feb 14, 2008


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

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/selectAffiliation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainCoi.do" name="selectAffiliationForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskCopy.affiliation.header" var="panelTitleIdForAffiliation" scope="page"/>
            <%
                String panelTitleIdForAffiliation = (String) pageContext.getAttribute("panelTitleIdForAffiliation");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAffiliation" panelContentId="panelContentIdForAffiliation"
                        panelTitle="<%= panelTitleIdForAffiliation %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="selectAffiliationForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="affiliationListGrid" scope="request"/>                        
                        <c:set var="datasrc" value="#affiliationListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_DONE_CANCEL_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

    <jsp:include page="/core/footerpopup.jsp"/>

