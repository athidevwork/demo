<%@ page import="org.apache.struts.Globals" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%--<%@ page import="dti.pm.core.http.RequestIds" %>--%>
<%@ page language="java" %>
<%--
  Description: select Agent contract page

  Author: James
  Date: Mar 28, 2008

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       dzhang      195835: Grid replacement
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<script language="javascript" src="<%=cisPath%>/agentmgr/js/selectAgentContract.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectAgentContract.do" method="POST" name="selectContractList">
    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td>&nbsp;&nbsp;</td>
                    <td>
                        <oweb:message/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="ci.agentmgr.selectcontract.panelTitle"
                         var="panelTitleForSelectAgentContract"
                         scope="page"/>
            <%
                String panelTitleForSelectAgentContract = (String) pageContext.getAttribute("panelTitleForSelectAgentContract");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelectAgentContract"
                        panelContentId="panelContentIdForSelectAgentContract"
                        panelTitle="<%= panelTitleForSelectAgentContract %>"
                        isTogglableTitle="false">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="divId" value="searchFields"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                            <jsp:param name="actionItemGroupId" value="CI_SEL_CONT_SRCH_AIG"/>
                        </jsp:include>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <oweb:panel hasTitle="false"
                                    panelContentId="panelContentIdForAgentContract"
                                    panelTitleLayerId="OM_CI_SEL_AGENT_CONTRACT_GH">
                            <tr>
                                <td colspan="6" align=center>
                                    <c:set var="gridDisplayFormName" value="selContractList" scope="request"/>
                                    <c:set var="gridDisplayGridId" value="selContractListGrid" scope="request"/>
                                    <c:set var="datasrc" value="#selContractListGrid1" scope="request"/>
                                    <c:set var="cacheResultSet" value="false"/>
                                    <%@ include file="/core/gridDisplay.jsp" %>
                                </td>
                            </tr>
                        </oweb:panel>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="CI_SEL_CONT_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
