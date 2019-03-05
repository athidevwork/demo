<%--
  Description: Maintain National Program page

  Author: Dzhang
  Date: May 25, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/10/2017       eyin        180675 - Added code to display message on parent Window in new
                                        UI tab style.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
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
<script type="text/javascript" src="js/maintainNationalProgram.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainNationalProgram.do" name="nationalProgramForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="riskBaseRecordId" value="<c:out value="${param.riskBaseRecordId}"/>" />

    <tr>
        <td colspan=8>
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
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_MNT_NATL_PROG_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainNationalProgram.nationalProgramList.header" var="panelTitleForNationalProgram" scope="page">
            </fmt:message>
            <%
                String panelTitleForNationalProgram = (String) pageContext.getAttribute("panelTitleForNationalProgram");
            %>
            <oweb:panel panelTitleId="panelTitleIdForNationalProgram" panelContentId="panelContentIdForNationalProgram" panelTitle="<%= panelTitleForNationalProgram %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_OPT_NATL_PROG_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="nationalProgramForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="nationalProgramListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="nationalProgramListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#nationalProgramListGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%-- Display grid form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainNationalProgram.nationalProgramForm.header" var="nationalProgramFormHeader" scope="request"/>
                    <% String nationalProgramFormHeader = (String) request.getAttribute("nationalProgramFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= nationalProgramFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="divId" value="nationalProgramListGridDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_MNT_NATL_PROG_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <br>

<jsp:include page="/core/footerpopup.jsp"/>
