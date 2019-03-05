<%--
  Description:

  Author: Wayne Rong
  Date: Aug 31, 2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/31/2018       wrong         188391 - Initial version.
  11/13/2018       tyang         194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2018 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyFields" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<c:set var="isForDivPopup" value="true"></c:set>

<jsp:useBean id="selectRelatedCovgListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="selectRelatedCovgListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/selectUnderlyingRelation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
</script>
<%
    String policyUnderPolNo = request.getParameter("policyUnderPolNo");
    if (!StringUtils.isBlank(policyUnderPolNo)) {
        policyUnderPolNo = policyUnderPolNo.substring(0, policyUnderPolNo.indexOf("(") - 1);
    }
    pageContext.setAttribute("policyUnderPolNo", policyUnderPolNo);
%>

<input type="hidden" name="policyUnderPolId"
       value="<%=(String) request.getParameter(UnderlyingPolicyFields.POLICY_UNDER_POL_ID)%>">
<input type="hidden" name="policyUnderPolNo"
       value="<%=(String) pageContext.getAttribute("policyUnderPolNo")%>">
<input type="hidden" name="hasAvailableRelatedCoverages"
       value="<%=(String) request.getAttribute("hasAvailableRelatedCoverages")%>">

<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="selectUnderlyingRelation.do" name ="selectUnderlyingRelation">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <!-- Display grid -->
    <tr>
        <td align="center">
            <fmt:message key="pm.maintainUnderlyingRelation.current.policy.header" var="panelTitleForCurPol"
                         scope="page"/>
                <%
                String panelTitleForCurPol = (String) pageContext.getAttribute("panelTitleForCurPol");
                if (policyHeader.getPolicyNo() != null) {
                    panelTitleForCurPol = policyHeader.getPolicyNo() + panelTitleForCurPol;
                }
            %>
            <oweb:panel panelTitleId="panelTitleIdForCurCovg" panelContentId="panelContentIdForCurCovg"
                        panelTitle="<%=panelTitleForCurPol%>" >
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="selectCurrentCovgList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="selectCurrentCovgListGrid" scope="request"/>
                        <c:set var="gridId" value="selectCurrentCovgListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="PM_SEL_CUR_COVG_GH"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <c:set var="selectable" value="true"/>
                        <c:set var="gridSortable" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
<%
    if (request.getAttribute("hasAvailableRelatedCoverages").equals("Y")) {
%>
    <tr>
        <td align="center">
            <fmt:message key="pm.maintainUnderlyingRelation.related.policy.header" var="panelTitleForRelPol"
                         scope="page"/>
                <%
                String panelTitleForRelPol = (String) pageContext.getAttribute("panelTitleForRelPol");
                panelTitleForRelPol = policyUnderPolNo + panelTitleForRelPol;
            %>
            <oweb:panel panelTitleId="panelTitleIdForRelCovg" panelContentId="panelContentIdForRelCovg"
                        panelTitle="<%=panelTitleForRelPol%>" >
        <tr>
            <td colspan="6" align=center><br/>
                <c:set var="gridDisplayFormName" value="selectRelatedCovgList" scope="request"/>
                <c:set var="gridDisplayGridId" value="selectRelatedCovgListGrid" scope="request"/>
                <c:set var="gridId" value="selectRelatedCovgListGrid" scope="request"/>
                <c:set var="gridSizeFieldIdPrefix" value="PM_SEL_REL_COVG_GH"/>
                <%  dataBean = selectRelatedCovgListGridDataBean;
                    gridHeaderBean = selectRelatedCovgListGridHeaderBean;
                %>
                <c:set var="cacheResultSet" value="false"/>
                <c:set var="selectable" value="true"/>
                <c:set var="gridSortable" value="false"/>
                <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        </oweb:panel>
        </td>
    </tr>
<%
    }
%>
    <%-- Display buttons --%>
    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="PM_SEL_UND_RELATION_AIG" />
        </td>
    </tr>

    <script type="text/javascript" >

    </script>

<jsp:include page="/core/footerpopup.jsp"/>
