<%--
  Description: Select Manuscript page.

  Author: Joe
  Date: August 21, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang       194100 - Add buildNumber Parameter
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
<script type="text/javascript" src="js/selectManuscript.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name ="selectManuscriptForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainManu.manuscriptList.header" var="panelTitleForManuscript" scope="page">
            </fmt:message>
            <%
                String panelTitleForManuscript = (String) pageContext.getAttribute("panelTitleForManuscript");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAddManuscript" panelContentId="panelContentIdForAddManuscript" panelTitle="<%= panelTitleForManuscript %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectManuscriptForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="selectManuscriptGrid" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEL_MANU_AIG"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>