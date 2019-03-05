<%--
  Description:
  Multi Cancel Info page
  Author: yhchen
  Date: Mar 19, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript"
        src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/performMultiCancellation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
 <script type="text/javascript"
        src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/viewMultiCancelInfo.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/transactionmgr/cancelprocessmgr/performMultiCancellation.do" method=post name="cancelList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="validateResult" value="<c:out value="${status}"/>"/>
    <input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>


    <%-- Show error message --%>
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
        <td>
            <div <%=((useJqxGrid)?"class='dti-hide'":"style='display:none'")%>>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="headerText" value=""/>
                    <jsp:param name="divId" value="cancelInfoDiv"/>
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="isLayerVisibleByDefault" value="false"/>
                    <jsp:param name="collaspeTitleForPanel" value="false"/>
                    <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                    <jsp:param name="excludePageFields" value="false"/>
                    <jsp:param name="actionItemGroupId" value=""/>
                </jsp:include>
            </div>
        </td>
    </tr>

    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.matainMultiCancel.cancelIterGh.header" var="panelTitleForCancelList" scope="page"/>
            <%
                String panelTitleForCancelList = (String) pageContext.getAttribute("panelTitleForCancelList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTailList" panelContentId="panelContentIdForTailList"
                        panelTitle="<%= panelTitleForCancelList %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="cancelList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="cancelListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="cancel_"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_CONITNUE_CANCEL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
