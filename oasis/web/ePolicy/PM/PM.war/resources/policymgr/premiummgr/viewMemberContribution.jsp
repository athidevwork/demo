<%--
  Description: view member contribution jsp

  Author: rlli
  Date: July 18, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  //11/30/2010     dzhang      Issue#114880 - Add panel title.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
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

<form name="memberContributionList" action="viewMemberContribution.do" method=post>
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

            <fmt:message key="pm.viewMemberContribution.memberContribution.header" var="panelTitleIdForContribution" scope="page">
                <fmt:param value="${riskName}"/>
            </fmt:message>
            <%
                String panelTitleIdForContribution = (String) pageContext.getAttribute("panelTitleIdForContribution");
            %>
            <oweb:panel panelTitleId="panelTitleIdForContribution" panelContentId="panelContentIdForContribution" panelTitle="<%= panelTitleIdForContribution %>" >

            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="memberContributionList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="memberContributionListGrid" scope="request"/>
                    <c:set var="datasrc" value="#memberContributionListGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>                
            <tr>
                <td align=center>
                    <fmt:message key= "pm.viewMemberContribution.memberContributionInfo.header" var="panelTitleFormemberContributionInfo" scope="page"/>
                    <% String panelTitleFormemberContributionInfo = (String) pageContext.getAttribute("panelTitleFormemberContributionInfo"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  panelTitleFormemberContributionInfo %>" />
                        <jsp:param name="isGridBased" value="false" />
                        <jsp:param name="excludeAllLayers" value="true" />
                    </jsp:include>
                </td>
            </tr>
                
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_ENT_CONTRIB_AIG"/>
                </td>
            </tr>

        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>