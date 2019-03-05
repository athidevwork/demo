<%--
  Description: Risk Copy Address Phone page

  Author: Joe Shen
  Date: May 14, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
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

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script type="text/javascript" src="js/performRiskAddressPhoneCopy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="performRiskAddressPhoneCopy.do" name="addressPhoneForm" method="post">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<input type="hidden" name="policyId" value="<c:out value="${policyId}"/>"/>

<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>

<tr>
    <td colspan=8>
        <b>
            <fmt:message key="pm.copyAddrPhone.addressPhone.header"/>
        </b>
    </td>
</tr>
<tr>
    <td align=center>
        <%-- Address/Phone grid --%>
        <fmt:message key="pm.copyAddrPhone.addressPhoneList.header" var="panelTitleForRiskAddrPhoneList"
                     scope="page"/>
        <%
            String panelTitleForRiskAddrPhoneList = (String) pageContext.getAttribute("panelTitleForRiskAddrPhoneList");
        %>
        <oweb:panel panelTitleId="panelTitleIdForRiskAddrPhoneList"
                    panelContentId="panelContentIdForRiskAddrPhoneList"
                    panelTitle="<%= panelTitleForRiskAddrPhoneList %>">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="addressPhoneForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="performRiskAddrPhoneCopyListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="performRiskAddrPhoneCopyListGridDiv" scope="request"/>
                    <c:set var="datasrc" value="#performRiskAddrPhoneCopyListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%-- Address/Phone form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.copyAddrPhone.addressPhoneForm.header" var="riskAddrPhoneFormHeader"
                                 scope="request"/>
                    <% String riskAddrPhoneFormHeader = (String) request.getAttribute("riskAddrPhoneFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= riskAddrPhoneFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_ADDR"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<!-- Reset gridDetailDivId to avoid conflicts for the below form-->
<c:set var="gridDetailDivId" value="" scope="request"/>

<c:if test="${dataBean != null && dataBean.rowCount > 0}">
    <%-- Change Date form --%>
<tr>
    <td align=center>
        <fmt:message key="pm.copyAddrPhone.changeDateForm.header" var="panelTitleForChangeDate" scope="page"/>
        <%
            String panelTitleForChangeDate = (String) pageContext.getAttribute("panelTitleForChangeDate");
        %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=panelTitleForChangeDate%>"/>
            <jsp:param name="divId" value="changeDateFormDiv"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="excludePageFields" value="true"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_CHANGE_DATE"/>
        </jsp:include>
    </td>
</tr>

    <%-- Risk Name grid --%>
    <jsp:useBean id="riskNameListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <jsp:useBean id="riskNameListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<tr>
    <td align=center>
        <fmt:message key="pm.copyAddrPhone.riskNameList.header" var="panelTitleForRiskNameList" scope="page"/>
        <%
            String panelTitleForRiskNameList = (String) pageContext.getAttribute("panelTitleForRiskNameList");
        %>
        <oweb:panel panelTitleId="panelTitleIdForRiskNameGrid" panelContentId="panelContentIdForRiskNameGrid"
                    panelTitle="<%=panelTitleForRiskNameList%>">
                <tr>
                    <td colspan="8" align=center>
                        <c:set var="gridDisplayFormName" value="riskNameForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="riskNameListGrid" scope="request"/>
                        <% dataBean = riskNameListGridDataBean;
                            gridHeaderBean = riskNameListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
        </oweb:panel>
    </td>
</tr>
</c:if>

<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_COPY_ADDR_PHONE_AIG"/>
    </td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>