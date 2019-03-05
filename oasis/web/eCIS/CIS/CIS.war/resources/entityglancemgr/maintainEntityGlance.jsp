<%@ page import="dti.ci.entityglancemgr.EntityGlanceFields,
                 dti.oasis.tags.XMLGridHeader,
                 dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  Description: Maintain  EntityGlance

  Author: Michael  Li
  Date: September 08, 2011


  Revision Date    Revised By  Description
  ---------------------------------------------------
  10/28/2016      ddai      for issue 180790, split the jsp to parts.
  06/28/2018      dpang     194157: Add buildNumber parameter to static file references to improve performance
  10/22/2018      dpang     195835: Grid replacement.
  11/28/2018      ylu       Issue 195886: only additional fix when testing item7
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
<%
String entityNameDisplay = (String) request.getAttribute(EntityGlanceFields.ENTITY_NAME_PROPERTY);
String clientId = (String) request.getAttribute("displayClientId");
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.glance.form.title");
}
else {
  //entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.glance") + " " + entityNameDisplay;
}
String message = (String) request.getAttribute(EntityGlanceFields.MSG_PROPERTY);
if (StringUtils.isBlank(message) || message.equalsIgnoreCase("null")) {
  message = "";
}
 if(clientId !=null) entityNameDisplay=entityNameDisplay+"("+clientId+")";
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="<%=cisPath%>/entityglancemgr/js/maintainEntityGlance.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="frmGrid" action="ciEntityGlance.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%>
            </b>
        </td>
    </tr>

    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="EntityGlance"/>
                <jsp:param name="headerTextLayerId" value="Entity_Glance_Demographic_Layer"/>
                <jsp:param name="includeLayerIds" value="Entity_Glance_Demographic_Layer"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
            </jsp:include>
        </td>
  <tr>
    <td align=center>
        <oweb:panel panelTitleId="relationshipTitleId"
                    panelContentId="relationshipContentId"
                    panelTitleLayerId="Entity_Glance_Relationship_Layer">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                    <c:set var="gridDisplayGridId" value="relationshipGrid" scope="request"/>
                    <c:set var="datasrc" value="#relationshipGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%  BaseResultSet dataBean = relationshipGridDataBean;
                       XMLGridHeader gridHeaderBean = relationshipGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

  <tr>
    <td align=center>
        <oweb:panel panelTitleId="policyTitleId"
                    panelContentId="policyContentId"
                    panelTitleLayerId="Entity_Glance_Policy_Layer"
                    isPanelCollaspedByDefault="true">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                    <c:set var="gridDisplayGridId" value="policyGrid" scope="request"/>
                    <c:set var="datasrc" value="#policyGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%  BaseResultSet dataBean = policyGridDataBean;
                       XMLGridHeader gridHeaderBean = policyGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <oweb:panel panelTitleId="transactionTitleId"
                                panelContentId="transactionContentId"
                                panelTitleLayerId="Entity_Glance_Transaction_Layer">
                      <tr>
                           <td colspan="6" align=center>
                            <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                            <c:set var="gridDisplayGridId" value="transactionGrid" scope="request"/>
                             <c:set var="datasrc" value="#transactionGrid1" scope="request"/>
                            <c:set var="cacheResultSet" value="false"/>
                              <%  dataBean = transactionGridDataBean;
                                gridHeaderBean = transactionGridHeaderBean; %>
                          <%@ include file="/core/gridDisplay.jsp" %>
                            </td>
                        </tr>
                  </oweb:panel>
                 </td>
             </tr>
            <tr>
                <td align=center>
                    <oweb:panel panelTitleId="transactionFormTitleId"
                                panelContentId="transactionFormContentId"
                                panelTitleLayerId="Entity_Glance_Transaction_From_Layer">
                      <tr>
                           <td colspan="6" align=center>
                            <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                            <c:set var="gridDisplayGridId" value="transactionFormGrid" scope="request"/>
                             <c:set var="datasrc" value="#transactionFormGrid1" scope="request"/>
                            <c:set var="cacheResultSet" value="false"/>
                              <%  dataBean = transactionFormGridDataBean;
                                gridHeaderBean = transactionFormGridHeaderBean; %>
                          <%@ include file="/core/gridDisplay.jsp" %>
                            </td>
                        </tr>
                  </oweb:panel>
                 </td>
             </tr>
        </oweb:panel>
    </td>
</tr>

        <jsp:include page="maintainEntityGlanceSplit.jsp" />

        <script type="text/javascript">
            setNoteFileImg("entityNotesExistB");
            setNoteFileImg("entityFilesExistB");
        </script>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp" />