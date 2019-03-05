<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page language="java" %>
<%--
  Description:

  Author: gjlong
  Date: Apr 23, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
  11/30/2010       dzhang      Issue#114880 Add panel title.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       dzhang      195835: Grid replacement
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<script language="javascript" src="<%=appPath%>/commissionmgr/js/lookupCommission.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<FORM action="lookupCommission.do" method="POST" NAME ="lookupCommissionForm">
<%@ include file="/cicore/commonFormHeader.jsp" %>

<tr><td>&nbsp;</td></tr>
<tr><td> <oweb:message/> </td></tr>
<tr>
    <td align=left>
        <fmt:message key="ci.commissionmgr.lookupCommission.formHeader" var="lookupCommissionHeader" scope="request"/>
        <% String lookupCommissionHeader = (String) request.getAttribute("lookupCommissionHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="divId" value="lookupCommissionDiv" />
            <jsp:param name="headerText" value="<%=  lookupCommissionHeader %>" />
            <jsp:param name="isGridBased" value="false" />
        </jsp:include>
    </td>
</tr>

<% if (request.getAttribute(RequestIds.DATA_BEAN) != null) { %>

    <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

       <tr>
        <td align=left>
            <%
               if (dataBean.getRowCount() ==0) {
            %>
               <fmt:message key= "ci.commissionmgr.lookupCommission.gridHeader" var="panelTitleForCommission" scope="page"/>
            <%
                } else { %>

               <fmt:message key= "ci.commissionmgr.lookupCommission.detailGridHeader" var="panelTitleForCommission" scope="page"/>
            <%
                }
            %>
            <% String panelTitleForCommission = (String) pageContext.getAttribute("panelTitleForCommission"); %>
            <oweb:panel panelTitleId="panelTitleIdForCommission"
                        panelContentId="panelContentIdForCommission"
                        panelTitle="<%= panelTitleForCommission %>"
                        panelTitleLayerId="CI_COMM_RATE_BRACKET_GH">

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="commRateBracketList" scope="request" />
                    <c:set var="gridDisplayGridId" value="commRateBracketListGrid" scope="request" />
                    <c:set var="datasrc" value="#commRateBracketListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
                
            </oweb:panel>
        </td>
    </tr>

<% } %>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="CI_LOOKUP_COMM_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />
