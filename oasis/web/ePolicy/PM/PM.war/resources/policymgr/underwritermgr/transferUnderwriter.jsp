<%--
  Description:Jsp for transfer underwriter

  Author: rlli
  Date: Mar 14, 2008

    Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
    05/09/2013         adeng     143242 - Correct the wrong div id for search criteria section.
    11/15/2018         eyin      194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/transferUnderwriter.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<form name="transferUnderwriter" action="transferUnderwriter.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.transferUnderwriter.underwriters.header" var="underwritersHeader" scope="request"/>
            <% String underwritersHeader = (String) request.getAttribute("underwritersHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="underwriters"/>
                <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                <jsp:param name="headerText" value="<%=  underwritersHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_TRANS_UW_UNDERWRITER"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.transferUnderwriter.policyDetails.header" var="panelTitleForPolicyDetals"
                         scope="page"/>
            <%
                String panelTitleForPolicyList = (String) request.getAttribute("resultHeader");
            %>
            <oweb:panel panelTitleId="panelTitleForPolicyDetals" panelContentId="panelContentIdForPolicyDetails"
                        panelTitle="<%= panelTitleForPolicyList %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_TRANS_UW_SEARCH_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.transferUnderwriter.searchCriteria.header" var="searchCriteriaHeader"
                                     scope="request"/>
                        <% String searchCriteriaHeader = (String) request.getAttribute("searchCriteriaHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="searchCriteria"/>
                            <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                            <jsp:param name="headerText" value="<%=  searchCriteriaHeader %>"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_TRANS_UW_CRITERIA"/>
                        </jsp:include>
                    </td>
                </tr>

                <tr>
                    <td align=center><br/>
                        <c:set var="gridDisplayFormName" value="transferUnderwriter" scope="request"/>
                        <c:set var="gridDisplayGridId" value="policyListGrid" scope="request"/>
                        <c:set var="datasrc" value="#policyListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="PM_TRANS_UW_PROCESS_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>

    <jsp:include page="/core/footer.jsp"/>
