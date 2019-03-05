<%--
  Description:

  Author: skommi
  Date: Feb 01, 2013
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<FORM action="viewAgentHistory.do" method="POST" NAME ="agentHistoryList">
        <tr>
          <td colspan=8>
              <oweb:message/>
          </td>
      </tr>

    <c:if test="${dataBean != null && dataBean.columnCount > 0}">
        <tr>
            <td align=center>
                    <fmt:message key="pm.agentmgr.viewAgentHistory.listHeader" var="panelTitleForAgentHist" scope="page"/>
                        <% String panelTitleForAgentHist = (String) pageContext.getAttribute("panelTitleForAgentHist");  %>
                <oweb:panel panelTitleId="panelTitleIdForAgentHistGrid" panelContentId="panelContentIdForAgentHistGrid" panelTitle="<%= panelTitleForAgentHist %>">

                    <tr>
                        <td align="left">
                            <c:set var="gridDisplayFormName" value="agentHistoryList" scope="request"/>
                            <c:set var="gridDisplayGridId" value="agentHistoryListGrid" scope="request"/>
                            <c:set var="gridDetailDivId" value="agentHistoryDetailDiv" scope="request"/>
                            <c:set var="datasrc" value="#agentHistoryListGrid1" scope="request"/>
                            <c:set var="cacheResultSet" value="false"/>
                            <%@ include file="/pmcore/gridDisplay.jsp" %>
                        </td>
                    </tr>
                </oweb:panel>

            </td>
        </tr>

        <tr>
            <td colspan="8" align="left">
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="divId" value="agentHistoryDetailDiv" />
                    <jsp:param name="hasPanelTitle" value="false" />
                    <jsp:param name="isGridBased" value="true" />
                </jsp:include>
            </td>
        </tr>

        <tr>
            <td colspan="8" align="center">
                <oweb:actionGroup actionItemGroupId="PM_VIEW_AGENT_HIST_AIG" layoutDirection="horizontal"/>
            </td>
        </tr>

    </c:if>

<jsp:include page="/core/footerpopup.jsp"/>
