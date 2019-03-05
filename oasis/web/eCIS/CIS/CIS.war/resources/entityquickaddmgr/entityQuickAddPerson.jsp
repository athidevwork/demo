<%@ page language="java" %>
<%@ page import="dti.ci.helpers.ICIEntityConstants" %>
<%--
  Description:

  Author: jdingle
  Date: 08/12/2016

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/26/2018       kshen       195835. CIS grid replacement.
  10/26/2018       ylu         195835: remove duplicate message tag to acoid JS error.
  11/16/2018       Elvin       Issue 195835: grid replacement
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script language="javascript" src="<%=cisPath%>/clientmgr/js/entityAddCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=cisPath%>/entityquickaddmgr/js/entityQuickAddCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=cisPath%>/entityquickaddmgr/js/entityQuickAddPerson.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="duplicateList" class="java.util.ArrayList" scope="request"/>

<%
    String okToSkipEntityDups = (String) request.getAttribute(ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY);
    if (StringUtils.isBlank(okToSkipEntityDups)) {
        okToSkipEntityDups = "N";
    }
    String okToSkipTaxIDDups = (String) request.getAttribute(ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY);
    if (StringUtils.isBlank(okToSkipTaxIDDups)) {
        okToSkipTaxIDDups = "N";
    }
    String saveAndClose = (String) request.getAttribute("saveAndClose");
    if (StringUtils.isBlank(saveAndClose)) {
        saveAndClose = "N";
    }
%>

<%
    if (request.getAttribute("duplicatedEntityExists") != null && (boolean) (request.getAttribute("duplicatedEntityExists"))) {
%>
<script type="text/javascript">
    setIsChangedFlag(true);
</script>
<%
    }
%>

<FORM NAME="entityQuickAddForm" action="entityQuickAddPerson.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="entity_entityType" value="P"/>
    <input type="hidden" value="<%=String.valueOf(request.getAttribute("newPk"))%>" name="newPk"/>
    <input type="hidden" value="<%=saveAndClose%>" name="saveAndClose"/>
    <input type="hidden" value="<%=String.valueOf(request.getAttribute("CI_ENTY_CONTINUE_ADD"))%>" name="CI_ENTY_CONTINUE_ADD"/>
    <input type='hidden' name="ciSchoolClass" value="<%=SysParmProvider.getInstance().getSysParm("CI_SCHOOL_CLASS", "MEDSCHOOL")%>"/>
    <input type='hidden' name="ciInstitutionFlt" value="<%=SysParmProvider.getInstance().getSysParm("CIW_EDU_INST_FLT", "N")%>"/>
    <input type='hidden' name="CM_CHK_VENDOR_VERIFY" value="<%=SysParmProvider.getInstance().getSysParm("CM_CHK_VENDOR_VERIFY", "N")%>"/>

    <html:hidden value="<%=okToSkipEntityDups%>" property="<%=ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY%>"/>
    <html:hidden value="<%=okToSkipTaxIDDups%>" property="<%=ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY%>"/>

    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_QUICKADDPER_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="divId" value="quickAddPersonForm"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForAddress"
                        panelContentId="panelContentIdForAddress"
                        panelTitleLayerId="CI_QUICK_PER_ADDRESS">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="AddressDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_ADDRESS"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForPhone"
                        panelContentId="panelContentIdForPhone"
                        panelTitleLayerId="CI_QUICK_PER_PHONE">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="PhoneDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_PHONE"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForLicense"
                        panelContentId="panelContentIdForLicense"
                        panelTitleLayerId="CI_QUICK_PER_LICENSE">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="LicenseDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_LICENSE"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForDenominator"
                        panelContentId="panelContentIdForDenominator"
                        panelTitleLayerId="CI_QUICK_PER_DENOMINATOR">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="DenominatorDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_DENOMINATOR"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForEducation"
                        panelContentId="panelContentIdForEducation"
                        panelTitleLayerId="CI_QUICK_PER_EDUCATION">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="EducationDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_EDUCATION"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForPriorCarrier"
                        panelContentId="panelContentIdForPriorCarrier"
                        panelTitleLayerId="CI_QUICK_PER_PRIORCARRIER">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="divId" value="PriorCarrierDiv"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_QUICK_PER_PRIORCARRIER"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_QUICKADDPER_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footer.jsp"/>
