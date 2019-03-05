<%--
  Description: maintain regional team and underwriter.

  Author: yhyang
  Date: Noc 19, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  // 11/30/2010    dzhang      Issue #114880 - Add panel title.
  // 11/15/2018    eyin        Issue 194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainRegionalTeam.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="regionalTeamMemberListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="regionalTeamMemberListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="regionalTeamList" action="maintainRegionalTeam.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.regional.team.header" var="regionalTeamHeader" scope="page"/>
            <% String regionalTeamHeader = (String) pageContext.getAttribute("regionalTeamHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForRegionalTeamHeader"
                        panelContentId="panelContentIdForRegionalTeamHeader"
                        panelTitle="<%= regionalTeamHeader %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_REGIONAL_TEAM_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="regionalTeamList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="regionalTeamListGrid" scope="request"/>
                        <c:set var="datasrc" value="#regionalTeamListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="regionalTeam" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.regional.team.information" var="TeamInfoHeader" scope="page"/>
                        <% String TeamInfoHeader = (String) pageContext.getAttribute("TeamInfoHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%= TeamInfoHeader %>"/>
                            <jsp:param name="divId" value="regionalTeam"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_REGIONAL_TEAM_FM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.regional.team.memeber.header" var="panelTitleForRegionalTeamMemberHeader"
                         scope="page"/>
            <%
                String panelTitleForRegionalTeamMemberHeader = (String) pageContext.getAttribute("panelTitleForRegionalTeamMemberHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRegionalTeamMemberHeader"
                        panelContentId="panelContentIdForRegionalTeamMemberHeader"
                        panelTitle="<%= panelTitleForRegionalTeamMemberHeader %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_REGIONAL_TEAM_MEB_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="regionalTeamMemberList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="regionalTeamMemberListGrid" scope="request"/>
                        <c:set var="datasrc" value="#regionalTeamMemberListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="regionalTeamMember" scope="request"/>
                        <% dataBean = regionalTeamMemberListGridDataBean;
                            gridHeaderBean = regionalTeamMemberListGridHeaderBean; %>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.regional.team.member.information" var="memberInfoHeader" scope="page"/>
                        <% String memberInfoHeader = (String) pageContext.getAttribute("memberInfoHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%= memberInfoHeader %>"/>
                            <jsp:param name="divId" value="regionalTeamMember"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_TEAM_MEMBER_FM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_REGIONAL_TEAM_SAVE_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>