<%@ page import="dti.pm.policymgr.PolicyHeader" %>
<%--
  Description:

  Author: yhchen
  Date: Mar 14, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/10/2017       eyin        180675 - Added code to display message on parent Window in new
                                        UI tab style.
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/pmcore/handleConfirmations.jsp" %>

<script type="text/javascript" src="<%=appPath%>/schedulemgr/js/maintainSchedule.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>


<form name="scheduleList" action="maintainSchedule.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=policyNo value="<c:out value="${policyHeader.policyNo}"/>">
    <%if(request.getAttribute("isFromCoverage")!=null){%>
       <input type=hidden name=isFromCoverage value="Y"/>
    <%}%>
    <input type=hidden name=chkPolschdOverlap value="<%=request.getAttribute("chkPolschdOverlap")%>"/>
    <input type=hidden name=chkPolschdOverlapNoEnt value="<%=request.getAttribute("chkPolschdOverlapNoEnt")%>"/>
    <input type=hidden name=transEffDate value="<c:out value="${policyHeader.lastTransactionInfo.transEffectiveFromDate}"/>"/>
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
    <c:set var="policyHeaderDisplayMode" value="hide"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainSchedule.scheduleList.header" var="panelTitleForSchedule" scope="page"/>
            <fmt:message key="pm.maintainSchedule.scheduleList.header.for" var="forForSchedule" scope="page"/>
            <fmt:message key="pm.maintainSchedule.scheduleList.header.coverage" var="coverageForSchedule" scope="page"/>
            <%
                String panelTitleForSchedule = (String) pageContext.getAttribute("panelTitleForSchedule");
                String forForSchedule = (String) pageContext.getAttribute("forForSchedule");
                String coverageForSchedule = (String) pageContext.getAttribute("coverageForSchedule");
                panelTitleForSchedule += " " + forForSchedule + " " + policyHeader.getRiskHeader().getRiskName();
                if (policyHeader.getCoverageHeader() != null) {
                    panelTitleForSchedule += ", " + coverageForSchedule + " " + policyHeader.getCoverageHeader().getCoverageName() ;
                }
            %>
            <oweb:panel panelTitleId="panelTitleIdForSchedule" panelContentId="panelContentIdForSchedule" panelTitle="<%= panelTitleForSchedule %>" >
            <tr>
                <td align>
                    <oweb:actionGroup actionItemGroupId="PM_SCHED_GRID_AIG" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="scheduleList" scope="request" />
                    <c:set var="gridDisplayGridId" value="scheduleListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="scheduleListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#scheduleListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
             <tr><td>&nbsp;</td></tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainSchedule.scheduleForm.header" var="scheduleFormHeader" scope="request"/>
                    <% String scheduleFormHeader = (String) request.getAttribute("scheduleFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  scheduleFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_SCHED_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
