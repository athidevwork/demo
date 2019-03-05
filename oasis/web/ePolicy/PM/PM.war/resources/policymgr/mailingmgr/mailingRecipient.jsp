<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: rlli
  Date: Dec 17, 2007

  Revision Date    Revised By  Description
  ---------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  05/03/2012       xnie        133041 - Set a valid action for form.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/mailingRecipient.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="mailingRecipientListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="mailingRecipientListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<form action="maintainPolicyMailing.do" method=post name="dummyForm">


    <tr>
        <td align=center>
            <fmt:message key="pm.maintainPolicyMailing.searchRecipient.header" var="recipientFilterHeader"
                         scope="request"/>
            <% String recipientFilterHeader = (String) request.getAttribute("recipientFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="mailingRecipientFilter"/>
                <jsp:param name="isPanelCollaspedByDefault" value="true"/>
                <jsp:param name="headerText" value="<%=  recipientFilterHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_RECIPIENT"/>
                <jsp:param name="actionItemGroupId" value="PM_RECIPIENT_SEARCH_AIG"/>

            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>
            <%
                String panelTitleForRecipientList = (String) request.getAttribute("resultHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRecipientList" panelContentId="panelContentForRecipientList"
                        panelTitle="<%= panelTitleForRecipientList %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_MAILING_RECIPIENT_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td align=center><br/>
                        <c:set var="gridDisplayFormName" value="mailingRecipientGridList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="mailingRecipientListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="mailingRecipientDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#mailingRecipientListGrid1" scope="request"/>
                        <%  BaseResultSet dataBean = mailingRecipientListGridDataBean;
                            XMLGridHeader gridHeaderBean = mailingRecipientListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.maintainPolicyMailing.mailingRecipientForm.header"
                                     var="mailingRecipientFormHeader"
                                     scope="request"/>
                        <% String mailingRecipientFormHeader = (String) request.getAttribute("mailingRecipientFormHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=  mailingRecipientFormHeader %>"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_MAILING_RECIPIENT"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
