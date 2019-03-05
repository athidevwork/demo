<%--
  Description: Amalgamation

  Author: yhyang
  Date: Feb 16, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/header.jsp" %>
<%@ include file="/cicore/common.jsp" %>

<script language="javascript" src="<%=cisPath%>/amalgamationmgr/js/maintainAmalgamation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="maintainAmalgamation.do" name="amalgamationList" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForAmalgamation"
                        panelContentId="panelContentIdForAmalgamation"
                        panelTitleLayerId="CIS_AMALGAMATION_GH">

            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AMALGAMATION_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="amalgamationList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="amalgamationListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="amalgamationDiv" scope="request"/>
                    <c:set var="datasrc" value="#amalgamationListGrid1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="hasPanelTitle" value="false"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="amalgamationDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="gridID" value="amalgamationListGrid"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="CI_AMALGAMATION_SAVE_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>