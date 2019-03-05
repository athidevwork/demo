<%@ page language="java" %>
<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.ci.entitymgr.EntityConstants" %>
<%--
  Description: Modify Person

  Author: Gerald C. Carney
  Date: Jan 9, 2004



  Revision Date    Revised By  Description
  ---------------------------------------------------
  04/05/2005       HXY         Use tagfactory.jsp
  04/13/2005       HXY         Added logic for controlling grid size.
  08/02/2006       ligj        Add loss history .
  02/12/2007       FWang       Change div default height
  02/16/2007       kshen       Added DBA Name History Grid (iss68160)
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  10/11/2007       FWCH        Added dbclick event for entity_eMailAddress1,
                               entity_eMailAddress2,entity_eMailAddress3 and
                               entity_webAddress1
  03/19/2009       kenney      Added Form Letter support for eCIS
  07/16/2009       hxk         Added sysparm  CS_INS_SNC_DATE_EDIT to support logic so we 
                               can avoid doing the "Today" edit for insured since date
  09/03/2009       Leo         issue 91274
  09/12/2009       hxk         Added sysparm  CS_LOSS_FR_DATE_EDIT and CS_CLM_FR_DATE_EDIT
                               to support logic so we can avoid doing the "Today" edit
                               for loss/claim free date.
  04/16/2008       kshen       Used Email Text display type to display email icon for entity.
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  10/22/2010       kshen       Added hidden field SYS_NO_VLD_EMAILADDR1, SYS_NO_VLD_EMAILADDR2, SYS_NO_VLD_EMAILADDR3.
  04/26/2013       bzhu        Issue 139501. Added electronic distribution for field,button and history grid.
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Add message tag.
  09/13/2013       kshen       Issue 144341.
  03/17/2014       Elvin       Issue 151570: include system parameter CI_LONGNAME_OVERRIDE(added in issue 132748)
  04/20/2018       ylu         Issue 109088: refactor: move displayDeceasedDate() function to Field Dependence config
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/22/2018       dzou        grid replacement
  10/22/2018       dzhang      Issue 195835: grid replacement - Add CIPERSONSPECIALHANDLINGB field to fix the italic style is not set when SP handling grid has data.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="nameGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="taxGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="lossGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="dbaGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="etdGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>

<jsp:useBean id="nameGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="taxGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="lossGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dbaGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="etdGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="duplicateList" class="java.util.ArrayList" scope="request"/>

<%
    WebLayer taxLayerFieldsMap = fieldsMap.getLayerFieldsMap("Entity_Person_Tax_History_Grid_Header_Layer");
    boolean taxLayerHidden = taxLayerFieldsMap.isHidden();
    taxLayerFieldsMap = fieldsMap.getLayerFieldsMap("Entity_Person_Loss_History_Grid_Header_Layer");
    boolean lossLayerHidden = taxLayerFieldsMap.isHidden();
    taxLayerFieldsMap = fieldsMap.getLayerFieldsMap("Entity_Person_Dba_History_Grid_Header_Layer");
    boolean dbaLayerHidden = taxLayerFieldsMap.isHidden();
    taxLayerFieldsMap = fieldsMap.getLayerFieldsMap("Entity_Electr_Distrib_History_Grid_Header_Layer");
    boolean etdLayerHidden = taxLayerFieldsMap.isHidden();
    BaseResultSet dataBean = null;
    XMLGridHeader gridHeaderBean = null;
%>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>
<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAddCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=cisPath%>/entitymodify/js/EntityModify.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<!-- Content -->

<FORM action="ciEntityPersonModify.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <input type="hidden" name="<%=EntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY%>"
           value="<%=(String) request.getAttribute(EntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY)%>"/>
    <input type="hidden" name="SYS_NO_VLD_EMAILADDR1"
           value="<%=SysParmProvider.getInstance().getSysParm("CI_NO_VLD_EMAILADDR1", "N")%>"/>
    <input type="hidden" name="SYS_NO_VLD_EMAILADDR2"
           value="<%=SysParmProvider.getInstance().getSysParm("CI_NO_VLD_EMAILADDR2", "N")%>"/>
    <input type="hidden" name="SYS_NO_VLD_EMAILADDR3"
           value="<%=SysParmProvider.getInstance().getSysParm("CI_NO_VLD_EMAILADDR3", "N")%>"/>
    <input type="hidden" name="CI_REF_NUM_PREFIX"
           value="<%=SysParmProvider.getInstance().getSysParm("CI_REF_NUM_PREFIX", "")%>"/>
    <input type="hidden" name="CI_LONGNAME_OVERRIDE"
           value="<%=SysParmProvider.getInstance().getSysParm("CI_LONGNAME_OVERRIDE", "Y")%>"/>
    <input type="hidden" name="<%=EntityConstants.VENDOR_VERIFY_SYS_PARAM%>"
           value="<%=(String) request.getAttribute(EntityConstants.VENDOR_VERIFY_SYS_PARAM)%>"/>
    <input type="hidden" name="CIPERSONSPECIALHANDLINGB">

<tr valign="top">
    <td colspan="6">
        <oweb:message/>
    </td>
</tr>
<tr>
    <td colspan="6">
        <fmt:message key="ci.entity.client.form.title" var="clientInfo" scope="request"/>
        <% String clientInfo = (String) request.getAttribute("clientInfo"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="divId" value="Client"/>
            <jsp:param name="headerText" value="<%=clientInfo%>"/>
            <jsp:param name="excludeAllLayers" value="true"/>
            <jsp:param name="actionItemGroupIdCssWidthInPX" value="120px"/>
            <jsp:param name="actionItemGroupId" value="CI_ENT_PER_MOD_AIG"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForNameHistory"
                    panelTitleId="panelTitleIdForNameHistory" panelTitleLayerId="Entity_Person_Name_History_Grid_Header_Layer">
            <tr>                                   
                <td colspan="6">
                    <c:set var="gridDisplayFormName" value="CIEntityPersonNameList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="personNameGrid" scope="request"/>
                    <c:set var="datasrc" value="#personNameGrid1" scope="request"/>
                    <% dataBean = nameGridDataBean;
                        gridHeaderBean = nameGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<%if (!taxLayerHidden) {%>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForTaxHistory"
                    panelTitleId="panelTitleIdForTaxHistory" panelTitleLayerId="Entity_Person_Tax_History_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <c:set var="gridDisplayFormName" value="CIEntityPersonTaxList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="personTaxGrid" scope="request"/>
                    <c:set var="datasrc" value="#personTaxGrid1" scope="request"/>
                    <% dataBean = taxGridDataBean;
                        gridHeaderBean = taxGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<%
}
if (!lossLayerHidden) {
%>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForLossHistory"
                    panelTitleId="panelTitleIdForLossHistory" panelTitleLayerId="Entity_Person_Loss_History_Grid_Header_Layer">
            <tr>
                <td colspan="6"><b></b></td>
            </tr>
            <tr>
                <td colspan="6">
                    <c:set var="gridDisplayFormName" value="CIEntityPersonLossList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="personLossGrid" scope="request"/>
                    <c:set var="datasrc" value="#personLossGrid1" scope="request"/>
                    <% dataBean = lossGridDataBean;
                        gridHeaderBean = lossGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<%
}
if (!dbaLayerHidden) {
%>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForDBANameHistory"
                    panelTitleId="panelTitleIdForDBANameHistory" panelTitleLayerId="Entity_Person_Dba_History_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <c:set var="gridDisplayFormName" value="CIEntityPersonDbaList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="personDbaGrid" scope="request"/>
                    <c:set var="datasrc" value="#personDbaGrid1" scope="request"/>
                    <% dataBean = dbaGridDataBean;
                        gridHeaderBean = dbaGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<%
}
if (!etdLayerHidden) {
%>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForElctrncDistrbHistory"
                    panelTitleId="panelTitleIdForElctrncDistrbHistory" panelTitleLayerId="Entity_Electr_Distrib_History_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <c:set var="gridDisplayFormName" value="CIEntityElctrncDistrbList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="elctrncDistrbGrid" scope="request"/>
                    <c:set var="datasrc" value="#elctrncDistrbGrid1" scope="request"/>
                    <% dataBean = etdGridDataBean;
                        gridHeaderBean = etdGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<%}%>
<script language="javascript">
    <c:if test="${clientDiscardedMsg == 'clientDiscardedMsgERROR'}">
    clientDiscardedError = "Y";
    </c:if>
    clientDiscardedMsg = "<fmt:message key='${clientDiscardedMsg}'/>";
</script>

<%
    // Initialize Sys Parms for JavaScript to use
    String insuredSinceDateEdit = SysParmProvider.getInstance().getSysParm("CS_INS_SNC_DATE_EDIT", "ALL");
    String lossFreeDateEdit     = SysParmProvider.getInstance().getSysParm("CS_LOSS_FR_DATE_EDIT", "ALL");
    String claimFreeDateEdit    = SysParmProvider.getInstance().getSysParm("CS_CLM_FR_DATE_EDIT", "ALL");
%>
<script type="text/javascript">
    setSysParmValue("CS_INS_SNC_DATE_EDIT", '<%= insuredSinceDateEdit %>');
    setSysParmValue("CS_LOSS_FR_DATE_EDIT", '<%= lossFreeDateEdit %>');
    setSysParmValue("CS_CLM_FR_DATE_EDIT", '<%= claimFreeDateEdit %>');
</script>
<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>
