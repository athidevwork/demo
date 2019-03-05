<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description: View Experience Discount History - Claim Info page

  Author: ryzhao
  Date: Aug 23, 2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/31/2018       ryzhao      188891 - Initial version.
  -----------------------------------------------------------------------------
  (C) 2018 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="js" method=post name="claimInfo">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.viewExpDiscHistory.claimInfo.header" var="claimTitle" scope="page"/>
            <%
                String claimTitle = (String) pageContext.getAttribute("claimTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForClaimGrid" panelContentId="panelContentIdForClaimGrid"
                        panelTitle="<%= claimTitle %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="claimInfo" scope="request"/>
                    <c:set var="gridDisplayGridId" value="claimGrid" scope="request"/>
                    <c:set var="datasrc" value="#claimGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_CLOSE_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>