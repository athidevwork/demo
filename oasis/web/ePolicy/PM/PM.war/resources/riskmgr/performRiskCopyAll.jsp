<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:
  Perform Risk Copy All page
  Author: yhchen
  Date: Feb 13, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/15/2010       dzhang      112064 - For Delete All set the form layers isLayerVisibleByDefault to false,
                               isPanelHiddenByDefault to true, then system will set the layer's display style to none.                           
  07/16/2015       kxiang      164599 - Remove unused txtXML fields.
  03/10/2017       wrong       180675 - Added code to display message on parent window in new
                                        UI tab style.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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

<script type="text/javascript" src="<%=appPath%>/riskmgr/js/performRiskCopyAll.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="coverageListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="coverageListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="coverageClassListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="coverageClassListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="riskListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="riskListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="componentListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="componentListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/riskmgr/performRiskCopyAll.do" method=post name="copyAllRisk">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>
<input type="hidden" name="sourceRiskBaseRecordId"
       value="<c:out value="${policyHeader.riskHeader.riskBaseRecordId}"/>"/>
<input type="hidden" name="transactionLogId" value="<c:out value="${policyHeader.lastTransactionId}"/>"/>
<input type="hidden" name="operation" value="<c:out value="${operation}"/>"/>

<% boolean isCopyAll = request.getAttribute("operation").equals("copyAll");%>
<%-- Show error message --%>
<tr>
    <td colspan=8>
        <%
            if(pmUIStyle.equals("T")) {
        %>
        <oweb:message displayMessagesOnParent="true"/>
        <%
            }
        %>
        <%
            if(pmUIStyle.equals("B")) {
        %>
        <oweb:message/>
        <%
            }
        %>
    </td>
</tr>

<c:set var="policyHeaderDisplayMode" value="invisible"/>
<tr>
    <td colspan=8 align=center>
        <%@ include file="/policymgr/policyHeader.jsp" %>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_RC_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<tr>
<td>
<fmt:message key="pm.maintainRiskCopy.source.header" var="panelTitleForSource" scope="page"/>
<%String panelTitleForSource = (String) pageContext.getAttribute("panelTitleForSource");%>
<oweb:panel panelTitleId="panelTitleForSource" panelContentId="panelContentIdForSource"
            panelTitle="<%= panelTitleForSource %>">


<!--Sourcr Risk Form-->
<input type="hidden" name="riskEffectiveFromDate" value="<c:out value="${riskEffectiveFromDate}"/>"/>
<input type="hidden" name="riskEffectiveToDate" value="<c:out value="${riskEffectiveToDate}"/>"/>
<tr>
    <td colspan="6">
        <oweb:actionGroup actionItemGroupId="PM_RC_SEL_RISK_AIG" layoutDirection="horizontal"
                          cssColorScheme="gray"/>
    </td>
</tr>
<tr>
    <td align=center>
        <% String riskFormHeader = policyHeader.getRiskHeader().getRiskName(); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=  riskFormHeader %>"/>
            <jsp:param name="divId" value="riskFormDiv"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="<%=isCopyAll %>"/>
            <jsp:param name="isPanelHiddenByDefault" value="<%=!isCopyAll %>"/>
            <jsp:param name="collaspeTitleForPanel" value="<%=  riskFormHeader %>"/>
            <jsp:param name="excludePageFields" value="true"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_SRC_RISK_FM"/>
        </jsp:include>
    </td>
</tr>

<!--Source Coverage Grid-->
<c:if test="${coverageListGridDataBean != null}">
<c:set var="gridDisplayGridId" value="" scope="request"/>
<tr>
    <td align=center>
        <fmt:message key="pm.maintainRiskCopy.coverage.header" var="panelTitleForCoverageList" scope="page"/>
        <%
            String panelTitleForCoverageList = (String) pageContext.getAttribute("panelTitleForCoverageList");
        %>
        <oweb:panel panelTitleId="panelTitleForCoverageList" panelContentId="panelContentIdForCoverageList"
                    panelTitle="<%= panelTitleForCoverageList %>">
            <%if (isCopyAll) {%>
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_RC_SEL_COVG_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <%}%>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>

                    <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                    <%if (isCopyAll) {%>
                    <c:set var="gridDetailDivId" value="coverageDetailDiv" scope="request"/>
                    <%}%>
                    <% BaseResultSet dataBean = coverageListGridDataBean;
                        XMLGridHeader gridHeaderBean = coverageListGridHeaderBean; %>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="gridSizeFieldIdPrefix" value="covg_"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr> &nbsp;</tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainRiskCopy.coverageDetail.header" var="coverageDetailFormHeader"
                                 scope="request"/>
                    <% String coverageDetailFormHeader = (String) request.getAttribute("coverageDetailFormHeader"); %>
                    <c:set var="datasrc" value="#coverageListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=coverageDetailFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="coverageDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="<%=isCopyAll %>"/>
                        <jsp:param name="isPanelHiddenByDefault" value="<%=!isCopyAll %>"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="dataBeanName" value="coverageListGridDataBean"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_SRC_COVG_FM"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>


    </td>
</tr>


<!--Component Grid-->
<c:if test="${componentListGridDataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskCopy.component.header" var="panelTitleIdForComponentList" scope="page"/>
            <%
                String panelTitleIdForComponentList = (String) pageContext.getAttribute("panelTitleIdForComponentList");
                boolean isPanelCollasped = (componentListGridDataBean.getRowCount() == 0);
            %>
            <oweb:panel panelTitleId="panelTitleIdForComponentList" panelContentId="panelContentIdForComponentList"
                        panelTitle="<%= panelTitleIdForComponentList %>"
                        isPanelCollaspedByDefault="<%=isPanelCollasped%>">
                <%if (isCopyAll) {%>
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_RC_SEL_COMP_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <%}%>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="componentList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="componentListGrid" scope="request"/>
                        <%if (isCopyAll) {%>
                        <c:set var="gridDetailDivId" value="componentDetailDiv" scope="request"/>
                        <%}%>
                        <% BaseResultSet dataBean = componentListGridDataBean;
                            XMLGridHeader gridHeaderBean = componentListGridHeaderBean; %>
                        <c:set var="cacheResultSet" value="false"/>
                        <c:set var="gridSizeFieldIdPrefix" value="comp_"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr> &nbsp;</tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.maintainRiskCopy.componentDetail.header"
                                     var="componentDetailFormHeader"
                                     scope="request"/>
                        <% String componentDetailFormHeader = (String) request.getAttribute("componentDetailFormHeader"); %>
                        <c:set var="datasrc" value="#componentListGrid1" scope="request"/>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=componentDetailFormHeader %>"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="divId" value="componentDetailDiv"/>
                            <jsp:param name="isLayerVisibleByDefault" value="<%=isCopyAll %>"/>
                            <jsp:param name="isPanelHiddenByDefault" value="<%=!isCopyAll %>"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="dataBeanName" value="componentListGridDataBean"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_SRC_COMP_FM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
</c:if>

<!--Coverage Class Grid-->
<c:if test="${coverageClassListGridDataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskCopy.coverageClass.header" var="panelTitleIdForCoverageClassList"
                         scope="page"/>
            <%
                String panelTitleIdForCoverageClassList = (String) pageContext.getAttribute("panelTitleIdForCoverageClassList");
                boolean isPanelCollasped = (coverageClassListGridDataBean.getRowCount() == 0);
            %>
            <oweb:panel panelTitleId="panelTitleIdForCoverageClassList"
                        panelContentId="panelContentIdForCoverageClassList"
                        panelTitle="<%= panelTitleIdForCoverageClassList %>"
                        isPanelCollaspedByDefault="<%=isPanelCollasped%>">
                <%if (isCopyAll) {%>
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_RC_SEL_SUBCOVG_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <%}%>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="coverageClassList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="coverageClassListGrid" scope="request"/>
                        <%if (isCopyAll) {%>
                        <c:set var="gridDetailDivId" value="coverageClassDetailDiv" scope="request"/>
                        <%}%>
                        <% BaseResultSet dataBean = coverageClassListGridDataBean;
                            XMLGridHeader gridHeaderBean = coverageClassListGridHeaderBean; %>
                        <c:set var="gridSizeFieldIdPrefix" value="subcovg_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr> &nbsp;</tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.maintainRiskCopy.coverageClassDetail.header"
                                     var="coverageClassDetailFormHeader"
                                     scope="request"/>
                        <% String coverageClassDetailFormHeader = (String) request.getAttribute("coverageClassDetailFormHeader"); %>
                        <c:set var="datasrc" value="#coverageClassListGrid1" scope="request"/>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=coverageClassDetailFormHeader %>"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="divId" value="coverageClassDetailDiv"/>
                            <jsp:param name="isLayerVisibleByDefault" value="<%=isCopyAll %>"/>
                            <jsp:param name="isPanelHiddenByDefault" value="<%=!isCopyAll %>"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="dataBeanName" value="coverageClassListGridDataBean"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_SRC_SUB_COVG_FM"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
</c:if>
</c:if>

<!--Affiliations and COI Holder-->
<%if (isCopyAll) {%>
<tr>
    <td colspan="6">
        <%
            request.setAttribute("datasrc", "");
            request.setAttribute("datafld", "");
        %>
        <oweb:actionGroup actionItemGroupId="PM_RC_AFFI_COI_AIG" layoutDirection="horizontal"
                          cssColorScheme="gray"/>
    </td>
</tr>

<tr>
    <td>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value=""/>
            <jsp:param name="divId" value="coiAffiFormDiv"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="collaspeTitleForPanel" value=""/>
            <jsp:param name="excludePageFields" value="true"/>
            <jsp:param name="dataBeanName" value=""/>
            <jsp:param name="includeLayersWithPrefix" value="PM_SEL_AFFI_COI_FM"/>
        </jsp:include>

    </td>
</tr>
<%}%>
</oweb:panel>
</td>
</tr>
<!--target risk grid    -->
<tr>
    <td>
        <fmt:message key="pm.maintainRiskCopy.target.header" var="panelTitleForTarget" scope="page"/>
        <%String panelTitleForTarget = (String) pageContext.getAttribute("panelTitleForTarget");%>
        <oweb:panel panelTitleId="panelTitleForTarget" panelContentId="panelContentIdForTarget"
                    panelTitle="<%= panelTitleForTarget %>">
            <tr>
                <td align="left">
                    <%
                        OasisFormField riskTypeFilterField = fieldsMap.getField("riskTypeFilter");
                        request.setAttribute("field", riskTypeFilterField);   // Used in compiledTagFactory
                        request.setAttribute("datasrc", "");
                        request.setAttribute("datafld", "");
                        request.setAttribute("gridDetailDivId", "");
                    %>
                    <jsp:include page="/core/compiledTagFactory.jsp">
                    </jsp:include>

                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="riskList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskListGrid" scope="request"/>
                    <% BaseResultSet dataBean = riskListGridDataBean;
                        XMLGridHeader gridHeaderBean = riskListGridHeaderBean; %>
                    <c:set var="cacheResultSet" value="true"/>
                    <c:set var="gridSizeFieldIdPrefix" value="risk_"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>


<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_RC_AIG" layoutDirection="horizontal"/>
    </td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>


