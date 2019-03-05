<%@ page language="java" %>
<%--
  Description:

  Author: wkong
  Date: 8/12/14


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       Elvin       Issue 195835: grid replacement
  11/08/2018       Elvin       Issue 195627: enable default values setting when adding phone log
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/vendormgr/phonelogmgr/js/phoneLog.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="PhoneLogForm" action="phoneLog.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <jsp:include page="/cicore/commonFormHeader.jsp"/>

    <input type="hidden" name="entityFK" value="<c:out value="${entityFK}"/>"/>
    <input type="hidden" name="vendorPK" value="<c:out value="${vendorPK}"/>"/>

    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForphoneLogGrid"
                        panelContentId="panelContentIdForphoneLogGrid"
                        panelTitleLayerId="CI_VENDOR_PHONELOG_GH">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="CI_PHONE_LOG_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                    </td>
                </tr>

                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="PhoneLogForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="phoneLogGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <c:set var="datasrc" value="#phoneLogGrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="gridDataBean"/>
                            <jsp:param name="gridID" value="phoneLogGrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="headerTextLayerId" value="CI_VENDOR_PHONELOG_GH"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_PHONE_LOG_FORM_AIG" layoutDirection="horizontal" cssColorScheme="blue"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>