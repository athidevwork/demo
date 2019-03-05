<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  Description:

  Author: dpang
  Date: 2/8/2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/08/2018       dpang       191377: Split expertWitness.jsp to two jsps to avoid 'exceeding the 65535 bytes limit' error.
  10/19/2018       kshen       195835. CIS grid replacement.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="classificationListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="classificationListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="relationsListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="relationsListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="claimsListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="claimsListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<!-- Classification -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForClassification"
                        panelTitleId="panelTitleIdForClassificationID"
                        panelTitleLayerId="EXPWTN_CLASSIFICATION_GRIDHEADER">
    <tr>
        <td width="100%">
            <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="classificationGrid" scope="request"/>
            <c:set var="datasrc" value="#classificationGrid1" scope="request"/>
            <%
                BaseResultSet dataBean = classificationListDataBean;
                XMLGridHeader gridHeaderBean = classificationListHeaderBean;
            %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>

<!-- Relations -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForRelations"
                        panelTitleId="panelTitleIdForRelationsID"
                        panelTitleLayerId="EXPWTN_RELATIONS_GRIDHEADER">
    <tr>
        <td width="100%">
            <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="relationsGrid" scope="request"/>
            <c:set var="datasrc" value="#relationsGrid1" scope="request"/>
            <%
                BaseResultSet dataBean = relationsListDataBean;
                XMLGridHeader gridHeaderBean = relationsListHeaderBean;
            %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>

<!-- Claims -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForClaims"
                        panelTitleId="panelTitleIdForClaimsID"
                        panelTitleLayerId="EXPWTN_CLAIMS_GRIDHEADER">
    <tr>
        <td width="100%">
            <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="claimsGrid" scope="request"/>
            <c:set var="datasrc" value="#claimsGrid1" scope="request"/>
            <%
                BaseResultSet dataBean = claimsListDataBean;
                XMLGridHeader gridHeaderBean = claimsListHeaderBean;
            %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>