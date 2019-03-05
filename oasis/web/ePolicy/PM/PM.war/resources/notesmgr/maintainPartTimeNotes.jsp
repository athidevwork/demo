<%--
  Description:

  Author: Bhong
  Date: Nov 25, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/notesmgr/js/maintainPartTimeNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="partTimeNotesForm" action="maintainPartTimeNotes.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
        <input type="hidden" name="refreshParentPageOnClose"
               value="<c:out value="${param.refreshParentPageOnClose}" />"/>
        <input type="hidden" name="refreshNoteStatus" value="<c:out value="${param.refreshNoteStatus}" />"/>
        <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <!-- Display search criteria -->
    <tr>
        <td align=center>
            <fmt:message key="pm.partTimeNotes.searchCriteria.header" var="searchCriteriaHeader" scope="request"/>
            <c:import url="/core/compiledFormFields.jsp">
                <c:param name="headerText" value="${searchCriteriaHeader}"/>
                <c:param name="divId" value="searchCriteriaDiv"/>
                <c:param name="isGridBased" value="false"/>
                <c:param name="isLayerVisibleByDefault" value="true"/>
                <c:param name="excludePageFields" value="true"/>
                <c:param name="includeLayerIds" value="PM_PTNOTES_SEARCH_LAYER"/>
                <c:param name="actionItemGroupId" value="PM_PTNOTES_SCH_AIG"/>
            </c:import>
        </td>
    </tr>
    <!-- Display Note List grid -->
    <tr>
        <td align=center>
            <fmt:message key="pm.partTimeNotes.notesList.header" var="notesListHeader" scope="page"/>
            <% String notesListHeader = (String) pageContext.getAttribute("notesListHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForNotesList" panelContentId="panelContentIdForNotesList"
                        panelTitle="<%= notesListHeader %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_PTNOTES_GRID_AIG"
                                          layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="partTimeNotesForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="partTimeNotesGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="partTimeNotesDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <!-- Display Notes list form -->
                <tr>
                    <td align=center>
                        <c:set var="datasrc" value="#partTimeNotesGrid1" scope="request"/>
                        <fmt:message key="pm.parTimeNotes.notesListForm.header" var="notesListForm" scope="request"/>
                        <c:import url="/core/compiledFormFields.jsp">
                            <c:param name="headerText" value="${notesListForm}"/>
                            <c:param name="isGridBased" value="true"/>
                            <c:param name="excludeLayerIds" value="PM_PTNOTES_SEARCH_LAYER"/>
                        </c:import>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align="center">
            <oweb:actionGroup actionItemGroupId="PM_PTNOTES_AIG"
                              layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>