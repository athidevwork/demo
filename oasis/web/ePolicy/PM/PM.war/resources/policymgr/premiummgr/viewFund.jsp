<%--
  Description:

  Author: rlli
  Date: June 18, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/10/2007       fcb         gridSortable set to false.
  08/30/2011       ryzhao      118806 - Set cacheResultSet to true for fundListGrid.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/viewFund.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="fundList" action="viewFund.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set scope="request" var="commentsCOLSPAN" value="7"/>

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
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewFundInfo.fundFilter.header" var="fundFilterHeader" scope="request"/>
            <% String fundFilterHeader = (String) request.getAttribute("fundFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  fundFilterHeader %>"/>
                <jsp:param name="divId" value="viewFundFilter"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewFundInfo.fundTrans.header" var="fundTransHeader" scope="request"/>
            <% String fundTransHeader = (String) request.getAttribute("fundTransHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  fundTransHeader %>"/>
                <jsp:param name="divId" value="viewFundTrans"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <c:if test="${dataBean.rowCount!=0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.viewFundInfo.fundList.header" var="panelTitleForFund" scope="page"/>
            <%
                String panelTitleForFund = (String) pageContext.getAttribute("panelTitleForFund");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFund" panelContentId="panelContentIdForFund"
                        panelTitle="<%= panelTitleForFund %>">

                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="fundList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="fundListGrid" scope="request"/>
                        <c:set var="datasrc" value="#fundListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>

            </oweb:panel>

        </td>
    </tr>
    </c:if>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_FUND_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>