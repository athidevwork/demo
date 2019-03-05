<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: xnie
  Date: November 16, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/16/2012       xnie        138948 - Initial version.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="massReRateDetailForm" action="massReRateDetailAction.do" method=post>
    <tr>
        <td align=center>
            <fmt:message key="pm.reRatePolicy.reRate.reRateResultDetailList.header" var="panelTitleForReRateResultDetail" scope="page"/>
            <%
                String panelTitleForReRateResultDetail = (String) pageContext.getAttribute("panelTitleForReRateResultDetail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForReRateResultDetail" panelContentId="panelContentIdForReRateResultDetail" panelTitle="<%= panelTitleForReRateResultDetail %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayGridId" value="massReRateResultDetailGrid" scope="request"/>
                    <c:set var="datasrc" value="#massReRateResultDetailGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            </oweb:panel>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>