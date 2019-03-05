<%--
  Description:

  Author: lzhang
  Date: July 6, 2016


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/11/2016       lzhang       177681 - Initial version
  11/15/2018       eyin         194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2006 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/js/maintainRetroDate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="retroDateList" action="maintainRetroDate.do" method=post>
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
        <td align=center>
            <fmt:message key="pm.maintainRetroDate.header"var="panelTitleForMaintainRetroDate" scope="page"/>
                <%
                String panelTitleForMaintainRetroDate = (String) pageContext.getAttribute("panelTitleForMaintainRetroDate");
            %>
            <oweb:panel panelTitleId="panelTitleIdForMaintainRetroDate"
                        panelContentId="panelContentIdForMaintainRetroDate" panelTitle="<%=panelTitleForMaintainRetroDate%>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="retroDateList" scope="request" />
                        <c:set var="gridDisplayGridId" value="retroDateListGrid" scope="request" />
                        <c:set var="gridDetailDivId" value="retroDateDetailDiv" scope="request" />
                        <c:set var="datasrc" value="#retroDateListGrid1" scope="request" />
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_RETRO_DATE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
