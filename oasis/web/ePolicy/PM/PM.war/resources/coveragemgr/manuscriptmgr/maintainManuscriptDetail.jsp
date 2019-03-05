<%--
  Description: Manuscript Detail Page

  Author: Joe Shen
  Date: August 24, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/01/2011       lmjiang     Issue 126315 - Hold the value of 'isDeleteAvaliable' which is recieved from Manuscript Information Page
                                             for this Page.
  02/06/2012       xnie        Issue 128139 - Added a new web menu PM_MANU_DETAIL_GRID_AIG.
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

<script type="text/javascript" src="js/maintainManuscriptDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainManuscriptDetail.do" name="maintainManuscriptDetailForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="formCode" value="<c:out value="${formCode}"/>"/>
    <input type="hidden" name="manuscriptEndorsementId" value="<c:out value="${manuscriptEndorsementId}"/>"/>
    <input type="hidden" name="effectiveFromDate" value="<c:out value="${param.effectiveFromDate}"/>"/>
    <input type="hidden" name="effectiveToDate" value="<c:out value="${param.effectiveToDate}"/>"/>
    <input type="hidden" name="isDeleteAvailable" value="<c:out value="${param.isDeleteAvailable}"/>"/>   
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
            <fmt:message key="pm.maintainManu.detailList.header" var="panelTitleForManuscriptDetail" scope="page"/>
            <%
                String panelTitleForManuscriptDetail = (String) pageContext.getAttribute("panelTitleForManuscriptDetail");
            %>
            <oweb:panel panelTitleId="panelTitleForManuscriptDetail" panelContentId="panelContentForManuscriptDetail" panelTitle="<%= panelTitleForManuscriptDetail %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_MANU_DETAIL_GRID_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainManuscriptDetailForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="maintainManuscriptDetailListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="maintainManuscriptDetailListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#maintainManuscriptDetailListGrid1" scope="request"/>
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
                    <fmt:message key="pm.maintainManu.detailForm.header" var="manuscriptDetailFormHeader" scope="request"/>
                    <% String manuscriptDetailFormHeader = (String) request.getAttribute("manuscriptDetailFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= manuscriptDetailFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_MANU_DETAIL_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

  <% // Initialize Sys Parms for JavaScript to use
      String hasFileHeader = SysParmProvider.getInstance().getSysParm("PM_MANU_DTL_VERSION", "N");
  %>
  <script type="text/javascript">
      setSysParmValue("PM_MANU_DTL_VERSION", '<%=hasFileHeader %>');
  </script>        

<jsp:include page="/core/footerpopup.jsp"/>
