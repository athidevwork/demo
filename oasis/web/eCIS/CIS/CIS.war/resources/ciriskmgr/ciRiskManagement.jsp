<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.ci.riskmgr.RiskFields" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page language="java"%>
<%--
  Description: CIS Risk Management Tab

  Author: kshen
  Date: March 27, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  01/06/2009       kshen       Don't load additional rm discount grid if no
                               recordset is retrieved.
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/15/2009       Fred        Added Image Right button in Program History grid
  01/27/2014       Elvin       Issue 150732: remove sysParams which control layer visible or not,
                                       use layer Hidden property directly
  05/22/2015       bzhu        Issue 156487 - display accumulated discount point.
  07/06/2016       Elvin       Issue 177662: use c:out to set entityName in order to avoid display problem
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  06/29/2018       ylu         Issue 194117: update for CSRF security.
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%-- Tag Libs--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request" />
<jsp:useBean id="programHistoryGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="programHistoryGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="windowPeriodHistoryGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="windowPeriodHistoryGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="ersPointHistoryGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="ersPointHistoryGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%-- page header --%>
<%@include file="/core/header.jsp"%>
<jsp:include page="/cicore/common.jsp"/>

<link href="<%=appPath%>/ciriskmgr/css/ciRiskManagement.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>

<%-- Entity Infos --%>
<jsp:include page="/CI_EntitySelect.jsp"/>

<%-- Tab Menus --%>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%-- Js files --%>
<script type='text/javascript' src="<%=appPath%>/ciriskmgr/js/ciRiskmanagement.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIRiskManagementForm" action="ciRiskManagent.do" method="POST">
    <%@include file="/cicore/commonFormHeader.jsp"%>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
<%--<input type="hidden" name="<%=RiskFields.MANDATE_WINDOW_PERIOD_FULFILLED%>"--%>
       <%--value="<%=request.getAttribute(RiskFields.MANDATE_WINDOW_PERIOD_FULFILLED)%>">--%>
<input type="hidden" name="<%=RiskFields.SATISFIED_B%>"
       value="<%=request.getAttribute(RiskFields.SATISFIED_B)%>">

<tr>
    <td colspan="6">
        <%
            OasisFormField currentRmDiscountFld = (OasisFormField) fieldsMap.getLayerFieldsMap("currentRmDiscountLayer").get("currentRmDiscountDescr");
            String currentRmDiscountFldDataType = currentRmDiscountFld.getDatatype();
            String currentRmDiscountFldOnFocus = new StringBuffer("baseOnFocus('" + currentRmDiscountFldDataType + "');").toString();
            String currentRmDiscountFldOnKeyPress = new StringBuffer("baseOnKeyPress('" + currentRmDiscountFldDataType + "');").toString();
            String currentRmDiscountFldOnKeyDown = new StringBuffer("baseOnKeyDown('" + currentRmDiscountFldDataType + "');").toString();
            String currentRmDiscountFldOnBlur = new StringBuffer("baseOnBlur('" + currentRmDiscountFldDataType + "');").toString();
            String currentRmDiscountFldOnChange = new StringBuffer("baseOnChange('" + currentRmDiscountFldDataType + "');").toString();
            boolean currentRmDiscountFldShowLabel = !currentRmDiscountFld.getLabel().equals("&nbsp;");
        %>
        <oweb:panel panelId="currentRmDiscountPanel"
                    panelContentId="panelContentForCurrentRmDiscount"
                    panelTitleId="panelTitleIdForCurrentRmDiscount"
                    panelTitleLayerId="currentRmDiscountLayer">
            <tr>
                <td>
                    <oweb:panel panelContentId="currentRmDiscountFldContent" hasTitle="false">
                        <tr>
                            <td><table>
                                <oweb:text oasisFormField="<%=currentRmDiscountFld%>" name="<%=currentRmDiscountFld.getFieldId()%>"
                                           maxlength="<%=currentRmDiscountFld.getMaxLength()%>" size="<%=currentRmDiscountFld.getCols()%>"
                                           isInTable="true" onfocus="<%=currentRmDiscountFldOnFocus%>" onchange="<%=currentRmDiscountFldOnChange%>"
                                           onkeypress="<%=currentRmDiscountFldOnKeyPress%>" onkeydown="<%= currentRmDiscountFldOnKeyDown%>"
                                           onblur="<%=currentRmDiscountFldOnBlur%>" showLabel="<%=currentRmDiscountFldShowLabel%>"/>
                            </table></td>
                        </tr>
                    </oweb:panel>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<tr>
    <td>
        <%
            OasisFormField currentMandateWindowPeriodFld = (OasisFormField) fieldsMap.getLayerFieldsMap("currentMandateWindowPeriodLayer").get("currentMandateWindowPeriodDescr");
            String currentMandateWindowPeriodFldDataType = currentMandateWindowPeriodFld.getDatatype();
            String currentMandateWindowPeriodFldOnFocus = new StringBuffer("baseOnFocus('" + currentMandateWindowPeriodFldDataType + "');").toString();
            String currentMandateWindowPeriodFldOnKeyPress = new StringBuffer("baseOnKeyPress('" + currentMandateWindowPeriodFldDataType + "');").toString();
            String currentMandateWindowPeriodFldOnKeyDown = new StringBuffer("baseOnKeyDown('" + currentMandateWindowPeriodFldDataType + "');").toString();
            String currentMandateWindowPeriodFldOnBlur = new StringBuffer("baseOnBlur('" + currentMandateWindowPeriodFldDataType + "');").toString();
            String currentMandateWindowPeriodFldOnChange = new StringBuffer("baseOnChange('" + currentMandateWindowPeriodFldDataType + "');").toString();
            boolean currentMandateWindowPeriodFldShowLabel = !currentMandateWindowPeriodFld.getLabel().equals("&nbsp;");
        %>
        <oweb:panel panelId="currentMandateWindowPeriodPanel"
                    panelContentId="panelContentForCurrentMandateWindowPeriod"
                    panelTitleId="panelTitleIdForCurrentMandateWindowPeriod"
                    panelTitleLayerId="currentMandateWindowPeriodLayer">
            <tr>
                <td>
                    <oweb:panel panelContentId="currentMandateWindowPeriodFldContent" hasTitle="false">
                        <tr>
                            <td><table>
                                <oweb:text oasisFormField="<%=currentMandateWindowPeriodFld%>"
                                           name="<%=currentMandateWindowPeriodFld.getFieldId()%>"
                                           maxlength="<%=currentMandateWindowPeriodFld.getMaxLength()%>"
                                           size="<%=currentMandateWindowPeriodFld.getCols()%>"
                                           isInTable="true"
                                           onfocus="<%=currentMandateWindowPeriodFldOnFocus%>"
                                           onchange="<%=currentMandateWindowPeriodFldOnChange%>"
                                           onkeypress="<%=currentMandateWindowPeriodFldOnKeyPress%>"
                                           onkeydown="<%= currentMandateWindowPeriodFldOnKeyDown%>"
                                           onblur="<%=currentMandateWindowPeriodFldOnBlur%>"
                                           showLabel="<%=currentMandateWindowPeriodFldShowLabel%>"/>
                            </table></td>
                        </tr>
                    </oweb:panel>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<script type="text/javascript">
    // Set the currentmandateWindowPeriodFldLabel style
    // var mandateWindowPeriodFulfilledFlag = getSingleObject("mandateWindowPeriodFulfilled").value;
    var satisfiedB = getSingleObject("satisfiedB").value;
    var currentMandateWindowPeriodDescrRosPan = getSingleObject("currentMandateWindowPeriodDescrROSPAN");

    if (currentMandateWindowPeriodDescrRosPan) {
        if (satisfiedB == "N") {
            currentMandateWindowPeriodDescrRosPan.className = "unSatisfiedManadateWindowPeriod";
        } else {
            currentMandateWindowPeriodDescrRosPan.className = "satisfiedManadateWindowPeriod";
        }
    }
</script>

<tr>
    <td>
        <oweb:panel panelId="programHistoryGridPanel"
                    panelContentId="panelContentForProgramHistoryGrid"
                    panelTitleId="panelTitleIdForProgramHistoryGrid"
                    panelTitleLayerId="programHistoryGrid_GH">
            <c:if test="${programHistoryGridDataBean==null || programHistoryGridDataBean.rowCount==0}">
            <tr>
                <td colspan="6" class="infomessage">
                    <fmt:message key="ci.rm.noProgramHistoryRetrieved"/>
                </td>
            </tr>
            </c:if>
             <tr>
                 <td>
                     <table cellpadding="0" cellspacing="0">
                         <tr>
                             <td width="100px">
                                 <oweb:actionGroup
                                         actionItemGroupId="CI_RM_PRO_HIS_AIG"
                                         cssColorScheme="gray"
                                         layoutDirection="horizontal">
                                 </oweb:actionGroup>
                            </td>
                             <td align="left" width="200px">
                                 <jsp:include page="/core/compiledFormFields.jsp">
                                     <jsp:param name="isGridBased" value="false"/>
                                     <jsp:param name="hasPanelTitle" value="false"/>
                                     <jsp:param name="divId" value="detailDiv"/>
                                     <jsp:param name="includeLayerIds" value="accumulatedDiscountPointLayer"/>
                                     <jsp:param name="headerTextLayerId" value="accumulatedDiscountPointLayer"/>
                                     <jsp:param name="excludePageFields" value="true"/>
                                     <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                     <jsp:param name="displayAsPanel" value="false"/>
                                 </jsp:include>
                             </td>
                         </tr>
                     </table>
                 </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="programHistoryGrid" scope="request"/>
                    <c:set var="datasrc" value="#programHistoryGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = programHistoryGridDataBean;
                        XMLGridHeader gridHeaderBean = programHistoryGridHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<tr>
    <td>
        <oweb:panel panelId="windowPeriodHistoryGridPanel"
                    panelContentId="panelContentForWindowPeriodHistoryGrid"
                    panelTitleId="panelTitleIdForWindowPeriodHistoryGrid"
                    panelTitleLayerId="windowPeriodHistoryGrid_GH">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="windowPeriodHistoryGrid" scope="request"/>
                    <c:set var="datasrc" value="#windowPeriodHistoryGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = windowPeriodHistoryGridDataBean;
                        XMLGridHeader gridHeaderBean = windowPeriodHistoryGridHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<%
    BaseResultSet additionalRmDiscountGridDataBean = (BaseResultSet) request.getAttribute("additionalRmDiscountGridDataBean");
    XMLGridHeader additionalRmDiscountGridHeaderBean = (XMLGridHeader) request.getAttribute("additionalRmDiscountGridHeaderBean");
    if (additionalRmDiscountGridDataBean != null) {
%>
<tr>
    <td>
        <oweb:panel panelId="additionalRmDiscountGridPanel"
                    panelContentId="panelContentForAdditionalRmDiscountGrid"
                    panelTitleId="panelTitleIdForAdditionalRmDiscountGrid"
                    panelTitleLayerId="additionalRmDiscountGrid_GH">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="additionalRmDiscountGrid" scope="request"/>
                    <c:set var="datasrc" value="#additionalRmDiscountGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = additionalRmDiscountGridDataBean;
                        XMLGridHeader gridHeaderBean = additionalRmDiscountGridHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<%
    }
%>

<tr>
    <td>
        <oweb:panel panelId="ersPointHistoryGridPanel"
                    panelContentId="panelContentForErsPointHistoryGrid"
                    panelTitleId="panelTitleIdForErsPointHistoryGrid"
                    panelTitleLayerId="ersPointHistoryGrid_GH">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="ersPointHistoryGrid" scope="request"/>
                    <c:set var="datasrc" value="#ersPointHistoryGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = ersPointHistoryGridDataBean;
                        XMLGridHeader gridHeaderBean = ersPointHistoryGridHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>

<%
    OasisFormField negativePointsCountFld = (OasisFormField) fieldsMap.getLayerFieldsMap("ersPointHistoryCountLayer").get("negativePointsCount");
    if (negativePointsCountFld!=null && negativePointsCountFld.getIsVisible()) {
        String dataType = negativePointsCountFld.getDatatype();
        String onFocus = new StringBuffer("baseOnFocus('" + dataType + "');").toString();
        String onKeyPress = new StringBuffer("baseOnKeyPress('" + dataType + "');").toString();
        String onKeyDown = new StringBuffer("baseOnKeyDown('" + dataType + "');").toString();
        String onBlur = new StringBuffer("baseOnBlur('" + dataType + "');").toString();
        String onChange = new StringBuffer("baseOnChange('" + dataType + "');").toString();
%>
            <tr>
                <td colspan="6" align=left>
                    <oweb:panel panelContentId="ersPointsHistoryCountContent" hasTitle="false">
                        <table><tr><td>
                                <oweb:text oasisFormField="<%=negativePointsCountFld%>" name="<%=negativePointsCountFld.getFieldId()%>"
                                           maxlength="<%=negativePointsCountFld.getMaxLength()%>"
                                           size="<%=negativePointsCountFld.getCols()%>" isInTable="true"
                                           onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>"
                                           onkeydown="<%= onKeyDown%>" onblur="<%=onBlur%>"
                                           showLabel="true"/>
                        </td></tr></table>
                    </oweb:panel>
                </td>
            </tr>
<%
    }
%>
        </oweb:panel>
    </td>
</tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp" />
