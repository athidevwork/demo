<%--
  Description:

  Author: Bhong
  Date: Dec 19, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/25/2011       syang       127661 - Add dataSavedB to indicate whether data saved.
  06/06/2013       awu         138241 - Add currentUnderwritingId to highlight the current record.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  01/29/2018       wrong       191120 - Added isSaveAction to indicate if current action is a saving action.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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

<script type="text/javascript" src="js/maintainUnderwriter.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript">
    var currentUnderwritingId = <%=UserSessionManager.getInstance().getUserSession().get(RequestIds.ENTITY_ROLE_ID)%>;
</script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="underwriterList" action="maintainUnderwriter.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="showAll" value="<c:out value="${showAll}"/>"/>
    <input type="hidden" name="addlPolicyInfoChangedB" value="<c:out value="${addlPolicyInfoChangedB}"/>"/>
    <input type="hidden" name="dataSavedB" value="<c:out value="${dataSavedB}"/>"/>
    <input type="hidden" name="isSaveAction" value="<c:out value="${isSaveAction}"/>"/>
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
    <c:set var="policyHeaderDisplayMode" value="hide"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainUnderwriter.addlPolicyInfo.header" var="addlPolicyInfoFormHeader" scope="request"/>
            <% String addlPolicyInfoFormHeader = (String) request.getAttribute("addlPolicyInfoFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  addlPolicyInfoFormHeader %>" />
                <jsp:param name="divId" value="underwriterAddlInfoDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="excludePageFields" value="true" />
                <jsp:param name="includeLayersWithPrefix" value="PM_ADDL" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainUnderwriter.underwriterList.header" var="panelTitleForUnderwriter" scope="page"/>
            <%
                String panelTitleForUnderwriter = (String) pageContext.getAttribute("panelTitleForUnderwriter");
            %>
            <oweb:panel panelTitleId="panelTitleIdForUnderwriter" panelContentId="panelContentIdForUnderwriter" panelTitle="<%= panelTitleForUnderwriter %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_UNDERWRITER_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="underwriterList" scope="request" />
                    <c:set var="gridDisplayGridId" value="underwriterListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="underwriterDetailDiv" scope="request" />
                    <c:set var="datasrc" value="#underwriterListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainUnderwriter.underwriterForm.header" var="underwriterFormHeader" scope="request"/>
                    <% String underwriterFormHeader = (String) request.getAttribute("underwriterFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  underwriterFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_UNDW" />
                    </jsp:include>

                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_UNDERWRITER_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
