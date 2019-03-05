<%@ page import="dti.oasis.tags.XMLGridHeader,
                 dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  Description: Maintain  EntityGlance  Split

  Author: Dean  Dai
  Date: October 28, 2016


  Revision Date    Revised By  Description
  ---------------------------------------------------
  10/22/2018        dpang     195835: Grid replacement.
  11/27/2018        ylu       Issue 195886: item7-auto adjust iframe height as grid's foot cannot display inside iframe.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<jsp:useBean id="relationshipGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="relationshipGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="claimGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="claimGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="participantGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="participantGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="policyGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="transactionGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="transactionGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="transactionFormGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="transactionFormGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="financialGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="financialGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="financialFormGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="financialFormGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<tr>
<td align=center>
<oweb:panel panelTitleId="financialTitleId"
            panelContentId="financialContentId"
            panelTitleLayerId="Entity_Glance_Financial_Layer"
            isPanelCollaspedByDefault="true">
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
            <c:set var="gridDisplayGridId" value="financialGrid" scope="request"/>
            <c:set var="datasrc" value="#financialGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%  BaseResultSet dataBean = financialGridDataBean;
                XMLGridHeader gridHeaderBean = financialGridHeaderBean; %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="ci.entity.glance.form.title.financial.invoice" var="invoice"
                         scope="page"/>
            <%
                String invoice = (String) pageContext.getAttribute("invoice");
            %>
            <oweb:panel panelTitleId="invoiceTitleId"
                        panelContentId="invoiceContentId"
                        panelTitle="<%= invoice %>">
                <iframe id="iframeInvoice" scrolling="no" allowtransparency="true" width="100%" onload="this.height=iframeInvoice.document ? iframeInvoice.document.body.scrollHeight : iframeInvoice.contentWindow.document.documentElement.scrollHeight"
                        frameborder="0" src=""></iframe>
            </oweb:panel>
        </td>

        </td>
    </tr>
    <tr>
    <td align=center>
    <oweb:panel panelTitleId="financialFormTitleId"
                panelContentId="financialFormContentId"
                panelTitleLayerId="Entity_Glance_Financial_From_Layer">

        <tr>
            <td colspan="6" align=center>
                <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                <c:set var="gridDisplayGridId" value="financialFormGrid" scope="request"/>
                <c:set var="datasrc" value="#financialFormGrid1" scope="request"/>
                <c:set var="cacheResultSet" value="false"/>
                <%  dataBean = financialFormGridDataBean;
                    gridHeaderBean = financialFormGridHeaderBean; %>
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
<td align=center>
<oweb:panel panelTitleId="claimTitleId"
            panelContentId="claimContentId"
            panelTitleLayerId="Entity_Glance_Claim_Layer"
            isPanelCollaspedByDefault="true">

    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
            <c:set var="gridDisplayGridId" value="claimGrid" scope="request"/>
            <c:set var="datasrc" value="#claimGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%  BaseResultSet dataBean = claimGridDataBean;
                XMLGridHeader gridHeaderBean = claimGridHeaderBean; %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
    <td align=center>
    <oweb:panel panelTitleId="participantTitleId"
                panelContentId="participantContentId"
                panelTitleLayerId="Entity_Glance_Participant_Layer">
        <tr>
            <td colspan="6" align=center>
                <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                <c:set var="gridDisplayGridId" value="participantGrid" scope="request"/>
                <c:set var="datasrc" value="#participantGrid1" scope="request"/>
                <c:set var="cacheResultSet" value="false"/>
                <%  dataBean = participantGridDataBean;
                    gridHeaderBean = participantGridHeaderBean; %>
                <%@ include file="/core/gridDisplay.jsp" %>
            </td>
        </tr>
    </oweb:panel>
    </td>
    </tr>
</oweb:panel>
</td>
</tr>