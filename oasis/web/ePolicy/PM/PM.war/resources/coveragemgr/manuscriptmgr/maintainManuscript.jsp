<%--
  Description: Manuscript Page

  Author: Joe Shen
  Date: August 20, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/06/2012       xnie        Issue 128139 Added a new web menu PM_MANU_GRID_AIG.
  02/10/2012       wfu         125055 - Added reference of openFileUpload.js for using of CS Upload File function.
  05/16/2012       jshen       132118 - Added one hidden field coverageBaseEffectiveToDate
  05/24/2012       jshen       132118 - Roll back previous change.
  06/13/2012       xnie        134250 - Added system parameter PM_MANU_PREM_ROUND.
  06/15/2012       xnie        134250 - Roll backed.
  03/13/2017       eyin        180675 - 1. Changed the error msg to be located in parent frame for UI change.
                                        2. Modified the logic of displaying page for UI change.
  09/01/2017       wrong       186656 - Initialize system parameter 'OS_MANUSCRIPT_EXPORT' for javascript use.
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
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainManuscript.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=csPath%>/fileuploadmgr/js/openFileUpload.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
    var selectedTermId = "<%= policyHeader.getSelectedPolicyTermId(policyHeader.getPolicyTermHistoryId())%>";
</script>

<form action="maintainManuscript.do" name="maintainManuscriptForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

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

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainManu.manuscriptList.header" var="panelTitleForManuscript" scope="page">
            </fmt:message>
            <%
                String panelTitleForManuscript = (String) pageContext.getAttribute("panelTitleForManuscript");
            %>
            <oweb:panel panelTitleId="panelTitleForManuscript" panelContentId="panelContentForManuscript" panelTitle="<%= panelTitleForManuscript %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_MANU_GRID_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainManuscriptForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="maintainManuscriptListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="maintainManuscriptListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#maintainManuscriptListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%-- Display grid form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainManu.manuscriptForm.header" var="manuscriptFormHeader" scope="request"/>
                    <% String manuscriptFormHeader = (String) request.getAttribute("manuscriptFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= manuscriptFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_MANUSCRIPT_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

<%
// Initialize Sys Parms for JavaScript to use
String sysParmOsManuscriptExp = SysParmProvider.getInstance().getSysParm("OS_MANUSCRIPT_EXPORT", "OLTP");
%>
<script type="text/javascript">
    setSysParmValue("OS_MANUSCRIPT_EXPORT", '<%=sysParmOsManuscriptExp%>');
</script>
<jsp:include page="/core/footerpopup.jsp"/>
