<%@ page import="dti.ci.helpers.ICIClaimsConstants" %>
<%@ page language="java" %>
<%--
  Description: CIS Billing Tab page

  Author: yjmiao
  Date: May 27, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/19/2018       ylu         Issue 192741: refactor old style code
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/04/2018       dpang       195835: Grid replacement - add panelTitleLayerId and remove panelTitle.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/billingmgr/js/ciBilling.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="accountList" method=post>
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
    <tr valign="top">
        <td class="tabTitle">
            <b>
                <fmt:message key="ci.entity.search.label.billing"/><%=" " + request.getParameter(ICIConstants.ENTITY_NAME_PROPERTY)%>
            </b>
        </td>
    </tr>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.columnCount > 0}">
        <tr>
            <td align=center>
                <oweb:panel panelTitleId="panelTitleForAccount" panelContentId="panelContentIdForAccount"
                            panelTitleLayerId="CI_BILLING_ACCOUNTS_GH">
                    <tr>
                        <td colspan="6" align=center>
                            <c:set var="gridDisplayFormName" value="accountList" scope="request"/>
                            <c:set var="gridDisplayGridId" value="accountListGrid" scope="request"/>
                            <c:set var="datasrc" value="#accountListGrid1" scope="request"/>
                            <c:set var="cacheResultSet" value="false"/>
                            <%@ include file="/core/gridDisplay.jsp" %>
                        </td>
                    </tr>
                </oweb:panel>
            </td>
        </tr>
        <tr>
            <td>
                <iframe id="iframePolicyTerm" scrolling="no" allowtransparency="true" width="100%"
                        height="780"
                        frameborder="0" src=""></iframe>

            </td>
        </tr>
    </c:if>
<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
