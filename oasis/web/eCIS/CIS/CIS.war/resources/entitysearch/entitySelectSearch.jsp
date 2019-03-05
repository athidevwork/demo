<%@ page language="java" %>
<%@ page import="dti.ci.entitysearch.EntitySearchFields" %>

<%--
  Description:

  Author: Gerald C. Carney
  Date: Apr 20, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------
  04/22/2005       HXY         Added </FORM> tag.
  05/04/2005       HXY         Changed window size.
  08/07/2007       James       Add UI2 chagnes.
  08/23/2007       kshen       Added codes to resize window.
  09/05/2007       James       remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/13/2007       Jerry       Move the buttons into the actionGroup
  11/06/2007       Kenney      include asynchttp.js
  12/04/2007       kshen       Added entity's json value as a parameter of handle event.
  12/06/2007       wer         Added KeyDown handler to automatically search when enter key is pressed.
  11/26/2008       Jacky      Added a 'process div' after click 'Role' button
  02/02/2009       kshen       corrected the word 'a' to 'an'.
  04/23/2009       Fred        Move handleOnEntitySearchWindowLoad to handleOnLoad function
  05/04/2009       Jacky       issue #92802
  07/02/2009       Leo         Issue 95512
  12/28/2009       Stephen     Issue 102270.
  05/03/2010       fcb         Issue 105866: logic added for policy holder role.
  05/26/2010       kshen       Added field name policyHolderNameNew, policyHolder as the policy holder field name.
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  12/20/2010       tzhao       115496: Fixed window focus bug in IE8.
  11/08/2011       bhong       112837 - Removed closeProcessingDiv and change the call to hideProcessingImgIndicator
  11/24/2011       Michael Li  127569 - Add to call  hideProcessingImgIndicator()
  11/29/2011       ryzhao      127573 - Modified callbackCheckRole() to remove showProcessingImgIndicator().
  01/11/2012       Leo         128992
  02/15/2012       jshen       127752 - Call hideProcessingImgIndicator() to close the div processing
                               in call back function callbackCheckRole().
  09/16/2013       Elvin       Issue 146415: encode entity_name when it is passing into URL
  03/20/2014                   Issue 151540
                               1) add DBA Name field's data into URL in js to pass it,
  10/30/2014       Elvin       Issue 158667: pass in country code/email address from search
  06/08/2016       Elvin       Issue 170396: add addToCRM logic
  12/12/2017       kshen       Grid replacement.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/19/2018       dzou        grid replacement
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/entitysearch/js/entitySelectSearch.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="CIEntitySearchForm" action="ciEntitySelectSearch.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.CLAIM_PK)%>" name="<%=EntitySearchFields.CLAIM_PK%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_PK_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_PK_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_FULL_NAME_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_FULL_NAME_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_LAST_NAME_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_LAST_NAME_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_FIRST_NAME_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_FIRST_NAME_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_MIDDLE_NAME_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_MIDDLE_NAME_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ORG_NAME_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_ORG_NAME_FLD_NAME_PROPERTY%>"/>
    <input type="hidden"   value="<%=(String) request.getAttribute(ICIConstants.EVENT_NAME_PROPERTY)%>" name="<%=ICIConstants.EVENT_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_CLIENT_ID_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_CLIENT_ID_FLD_NAME_PROPERTY%>"/>

    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ACCOUNT_NO_FLD_NAME_PROPERTY)%>" name="<%=EntitySearchFields.ENT_ACCOUNT_NO_FLD_NAME_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ID_FOR_ADDL_SQL_PROPERTY)%>" name="<%=EntitySearchFields.ENT_ID_FOR_ADDL_SQL_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ID_FOR_POLICY_NO_PROPERTY)%>" name="<%=EntitySearchFields.ENT_ID_FOR_POLICY_NO_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ID_FOR_FROM_FM_PROPERTY)%>" name="<%=EntitySearchFields.ENT_ID_FOR_FROM_FM_PROPERTY%>"/>

    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ROLE_TYPE_CODE_ARG)%>" name="<%=EntitySearchFields.ROLE_TYPE_CODE_ARG%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ROLE_TYPE_CODE_ARG_READ_ONLY)%>" name="<%=EntitySearchFields.ROLE_TYPE_CODE_ARG_READ_ONLY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENTITY_CLASS_CODE_ARG)%>" name="<%=EntitySearchFields.ENTITY_CLASS_CODE_ARG%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENTITY_CLASS_CODE_ARG_READ_ONLY)%>" name="<%=EntitySearchFields.ENTITY_CLASS_CODE_ARG_READ_ONLY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.FLD_ENT_REL_ENT_PK)%>" name="<%=EntitySearchFields.FLD_ENT_REL_ENT_PK%>" />
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.FROM_DOC_PROCESS)%>" name="<%=EntitySearchFields.FROM_DOC_PROCESS%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.ENT_ROLE_TYPE_CODE)%>" name="<%=EntitySearchFields.ENT_ROLE_TYPE_CODE%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.EXTERNAL_NUMBER)%>" name="<%=EntitySearchFields.EXTERNAL_NUMBER%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.DEFAULT_ROLE_TYPE_CODE)%>" name="<%=EntitySearchFields.DEFAULT_ROLE_TYPE_CODE%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(EntitySearchFields.DEFAULT_ENTITY_TYPE)%>" name="<%=EntitySearchFields.DEFAULT_ENTITY_TYPE%>"/>
    <input type="hidden"   value="<%=(String) request.getAttribute("CI_PHONE_PART_SRCH") %>" name="CI_PHONE_PART_SRCH"/>

    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <% if ( null != request.getAttribute(EntitySearchFields.ENT_ACCOUNT_NO_FLD_NAME_PROPERTY) || null != request.getAttribute(EntitySearchFields.ENT_FULL_NAME_FLD_NAME_PROPERTY)) { %>
      <input type='hidden' name='AccountNo' value=''>
    <%} if ( null != request.getAttribute(EntitySearchFields.ENT_ID_FOR_POLICY_NO_PROPERTY)) {%>
      <input type='hidden' name='PolicyNo' value=''>
    <%}%>

    <%
    boolean isPanelCollapsed = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("collapse.panel.after.cispop.search")).booleanValue();
    isPanelCollapsed = dataBean.getRowCount() == 0 ? false : isPanelCollapsed;

    if (YesNoFlag.getInstance((String) request.getAttribute(EntitySearchFields.FROM_DOC_PROCESS)).booleanValue()) {%>
        <input type='hidden' name='entityRoleTypeCode' value=''>
        <input type='hidden' name='externalNumber' value=''>
    <%}%>

    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
    <% String filterCriteria = (String) request.getAttribute("filterCriteria");%>
    <tr>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  filterCriteria %>"/>
                <jsp:param name="isPanelCollaspedByDefault" value="<%=  isPanelCollapsed %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="SearchCriteria"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center">
          <oweb:actionGroup actionItemGroupId="CI_ENTITY_SELECT_SCH_AIG" cssColorScheme="blue" layoutDirection="horizontal">
          </oweb:actionGroup>
        </td>
   </tr>

  <% if (dataBean.getRowCount() > 0) {   %>
    <c:set var="isPopup" value="Y" scope="request"></c:set>
    <tr>
        <td colspan="6">
            <oweb:panel panelTitleId="panelTitleForEntityList" panelTitleLayerId="Entity_Select_List_Grid_Header_Layer"
                        panelContentId="panelContentForEntityList">

            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_ENTITY_SEARCH_LIST_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td width="100%">
                    <c:set var="gridDisplayFormName" value="CIEntitySearchForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="entityListGrid" scope="request"/>
                    <c:set var="selectable" value="false" scope="request"/>
                    <c:set var="datasrc" value="#entityListGrid1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align="center" colspan="6">
            <oweb:actionGroup actionItemGroupId="CI_ENT_SEL_LST_FORM_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
  <% } %>
</FORM>
<jsp:include page="/core/footerpopup.jsp"/>

<script type='text/javascript'>
    var isIE8 = "<%=BrowserUtils.isIE8(request.getHeader("User-Agent")) %>" == "true";
</script>