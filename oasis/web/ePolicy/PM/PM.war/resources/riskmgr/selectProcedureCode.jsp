<%--
  Description:

  Author: Dzhang
  Date: Jun 04, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/28/2013       awu         149239 - Added new hidden field for isDoneAvailable.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
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
<script type="text/javascript" src="js/selectProcedureCode.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name="selectProcedureCodeForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="procedureCodes" value="<c:out value="${param.procedureCodes}"/>"/>
    <input type="hidden" name="isDoneAvailableInd" value="<c:out value="${isDoneAvailable}"/>"/>
    <tr>
        <td colspan=8>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td>
                        <oweb:message/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.selectProcedureCode.filterCriteria.header" var="filterFormHeader" scope="page"/>
            <%            
                String filterFormHeader = (String) pageContext.getAttribute("filterFormHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFilter" panelContentId="panelContentIdForFilter">
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= filterFormHeader %>"/>
                        <jsp:param name="divId" value="procedureCodeFilterDiv"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="actionItemGroupId" value="PM_SEL_PROC_FILTER_AIG"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_SEL_PROCEDURE_FILTER"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>

            <tr>
                <td align=center>
                    <fmt:message key="pm.selectProcedureCode.procedureList.header" var="panelTitleForProcedureList"
                                 scope="page"/>
                    <%
                        String panelTitleForProcedureList = (String) pageContext.getAttribute("panelTitleForProcedureList");
                    %>
                    <oweb:panel panelTitleId="panelTitleIdForProcedureList"
                                panelContentId="panelContentIdForProcedureList"
                                panelTitle="<%= panelTitleForProcedureList %>">
                        <tr>
                            <td colspan="6" align=center>
                                <c:set var="gridDisplayFormName" value="procedureList" scope="request"/>
                                <c:set var="gridDisplayGridId" value="procedureListGrid" scope="request"/>
                                <c:set var="gridSortable" value="false" scope="request"/>
                                <c:set var="cacheResultSet" value="true"/>
                                <%@ include file="/pmcore/gridDisplay.jsp" %>
                            </td>
                        </tr>
                    </oweb:panel>
                </td>
            </tr>
            <tr>
                <td align=center>
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEL_PROC_LIST_AIG" layoutDirection="horizontal"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>