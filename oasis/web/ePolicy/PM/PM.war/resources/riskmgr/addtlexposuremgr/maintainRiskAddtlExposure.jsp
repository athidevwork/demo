<%@ page import="dti.oasis.util.DateUtils" %>
<%--
  Description:

  Author: eyin
  Date: May 24, 2017


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  09/21/2017        eyin        169483, Initial version.
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2015 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<%dti.oasis.tags.XMLGridHeader gridHeaderBean = null;%>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="primaryExposureListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="riskAddtlExposureListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="riskAddtlExposureListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainRiskAddtlExposure.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="riskAddtlExposureList" action="<%=appPath%>/riskmgr/addtlexposuremgr/maintainRiskAddtlExposure.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="exposureRiskExpDate" value="<c:out value="${param.exposureRiskExpDate}"/>"/>
    <input type="hidden" name="riskEffectiveFromDate" value="<c:out value="${riskEffectiveFromDate}"/>" />
    <input type="hidden" name="riskName" value="<c:out value="${riskName}"/>" />
    <input type="hidden" name="dataSavedB" value="<c:out value="${dataSavedB}"/>"/>
    <input type="hidden" name="addtlPracticeSize" value="<c:out value="${addtlPracticeSize}"/>"/>
    <tr>
        <td>
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
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <% String panelTitleForPrimaryExposure = MessageManager.getInstance().formatMessage("pm.maintainRiskAddtlExposure.primaryExposureList.header",
                                             new String[]{request.getAttribute("riskName").toString(),
                                                  FormatUtils.formatDateTimeForDisplay(request.getAttribute("riskEffectiveFromDate").toString()),
                                                  FormatUtils.formatDateTimeForDisplay(request.getParameter("exposureRiskExpDate").toString())});
            %>
            <oweb:panel panelTitleId="panelTitleIdForPrimaryExposure" panelContentId="panelContentIdForPrimaryExposure" panelTitle="<%= panelTitleForPrimaryExposure %>" >
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="primaryExposureList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="primaryExposureListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="primaryExposureDetailDiv" scope="request" />
                    <c:set var="datasrc" value="#primaryExposureListGrid1" scope="request"/>
                    <% gridHeaderBean = primaryExposureListGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskAddtlExposure.addtlExposureList.header" var="panelTitleForRiskAddtlExposure" scope="page"/>
            <%
                String panelTitleForRiskAddtlExposure = (String) pageContext.getAttribute("panelTitleForRiskAddtlExposure");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRiskAddtlExposure" panelContentId="panelContentIdForRiskAddtlExposure" panelTitle="<%= panelTitleForRiskAddtlExposure%>" >
                <tr>
                    <td align=left>
                        <oweb:actionGroup actionItemGroupId="PM_RISK_ADDTL_EXP_GRD_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="riskAddtlExposureList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="riskAddtlExposureListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="riskAddtlExposureDetailDiv" scope="request" />
                        <c:set var="datasrc" value="#riskAddtlExposureListGrid1" scope="request"/>
                        <% dataBean = riskAddtlExposureListGridDataBean;
                            gridHeaderBean = riskAddtlExposureListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.maintainRiskAddtlExposure.addtlExposureForm.header" var="panelTitleForExpForm" scope="page" />
                        <% String panelTitleForForm = (String) pageContext.getAttribute("panelTitleForExpForm"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=panelTitleForForm %>" />
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
         </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_RISK_ADDTL_EXP_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>

