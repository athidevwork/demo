<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page language="java"%>
<%--
  Description: Select Location page

  Author: sxm
  Date: Apr 24, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/18/2011       Jerry       Enhancement 103805 - Add a hidden field 'transEffDate' for OBR rule.
  11/10/2011       Jerry       126975 - Change the hidden filed 'transEffDate' to grid level.
  03/10/2017       eyin        180675 - Added variable oParentWindow for UI tab style.
  05/23/2017       lzhang      185079 - remove getParentWindow() logic.
  10/23/2018       xgong       195889 - Updated for grid replacement
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/selectLocation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectLocation.do" method="POST" name="selectLocationList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<input type=hidden name=entityId value="<c:out value="${entityId}"/>">

<input type=hidden name=singleSelect value="<c:out value="${singleSelect}"/>">
<input type=hidden name=transEffDate value="<c:out value="${param.transEffDate}"/>">

<tr>
    <td>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td>&nbsp;&nbsp;</td>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<tr><td>&nbsp;</td></tr>

<c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.selectLocation.header" var="panelTitleForSelectLoc" scope="page"/>
            <%
                String panelTitleForSelectLoc = (String) pageContext.getAttribute("panelTitleForSelectLoc");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelectLoc" panelContentId="panelContentIdForSelectLoc" panelTitle="<%= panelTitleForSelectLoc %>" >
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="selectLocationList" scope="request" />
                    <c:set var="gridDisplayGridId" value="selectLocationGrid" scope="request" />
                    <c:set var="datasrc" value="#selectLocationGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
</c:if>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_SELECT_LOC_AIG" layoutDirection="horizontal"/>
    </td>
</tr>

<script type="text/javascript">
    if (typeof selectLocationGrid1 != "undefined") {
        if (isEmptyRecordset(selectLocationGrid1.recordset))
            getObject("PM_SELECT_LOC_SEL").disabled = true;
    }

</script>

<jsp:include page="/core/footerpopup.jsp" />
