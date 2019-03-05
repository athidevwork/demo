<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  JSP for displaying entity search criteria but without a list of entities.

  Author: Gerald C. Carney
  Date: Oct 21, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------
  * 07/05/2017       dpang       Issue 184234. Check if dataBean is empty to avoid IndexOutOfBoundsException.
    06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean class="dti.oasis.tags.OasisFields" id="fieldsMap" scope="request"/>

<jsp:useBean id="entityGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="entityGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="addressGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="addressGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="phoneGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="phoneGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="licenseGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="licenseGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/importmgr/js/dataImport.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="DataImportForm" enctype="multipart/form-data" action="ciImport.do" method="POST">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <%
        BaseResultSet dataBean = null;
        XMLGridHeader gridHeaderBean = null;
    %>
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td>
            <oweb:panel panelContentId="ciImportGrid" panelTitleLayerId="CI_IMPORT" isTogglableTitle="false">
                <tr>
                    <td align="right" id="dataFileFLDLABEL"
                        class="oasis_formlabelreq">
                        <c:out value='${fieldsMap["dataFile"].label}'/>
                    </td>
                    <td align="left">
                        <table>
                            <tr>
                                <td><input type="file"
                                           name="dataFile"
                                           onchange="handleOnChange(this)">
                                    &nbsp;&nbsp;
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr>
                    <td colspan="2" style="padding-top:6px" align="center">
                        <oweb:actionGroup actionItemGroupId="CI_IMPORT_AIG"
                                          cssColorScheme="blue" layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <c:if test="${entityGridDataBean.rowCount > 0}">
    <tr id="ciImportEntityGridTbl">
        <td>
            <oweb:panel panelContentId="ciImportEntityGrid" panelTitleLayerId="CI_IMPORT_ENTITY">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="DataImportForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="entityGrid" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <c:set var="datasrc" value="#entityGrid1" scope="request"/>
                        <%
                            dataBean = entityGridDataBean;
                            gridHeaderBean = entityGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <c:if test="${licenseGridDataBean.rowCount > 0}">
    <tr id="ciImportLicenseGridTbl">
        <td>
            <oweb:panel panelContentId="ciImportLicenseGrid" panelTitleLayerId="CI_IMPORT_LICENSE">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="DataImportForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="licenseGrid" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <c:set var="datasrc" value="#licenseGrid1" scope="request"/>
                        <%
                            dataBean = licenseGridDataBean;
                            gridHeaderBean = licenseGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <c:if test="${addressGridDataBean.rowCount > 0}">
    <tr id="ciImportAddressGridTbl">
        <td>
            <oweb:panel panelContentId="ciImportAddressGrid" panelTitleLayerId="CI_IMPORT_ADDRESS">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="DataImportForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="addressGrid" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <c:set var="datasrc" value="#addressGrid1" scope="request"/>
                        <%
                            dataBean = addressGridDataBean;
                            gridHeaderBean = addressGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <c:if test="${phoneGridDataBean.rowCount > 0}">
    <tr id="ciImportPhoneGridTbl">
        <td>
            <oweb:panel panelContentId="ciImportPhoneGrid" panelTitleLayerId="CI_IMPORT_PHONE">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="DataImportForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="phoneGrid" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <c:set var="datasrc" value="#phoneGrid1" scope="request"/>
                        <%
                            dataBean = phoneGridDataBean;
                            gridHeaderBean = phoneGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <c:if test="${entityGridDataBean.rowCount > 0}">
    <tr id="ciImportButtonTbl">
        <td colspan="2" style="padding-top:6px" align="center">
            <oweb:actionGroup actionItemGroupId="CI_IMPORT_PAGE_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
    </c:if>

<jsp:include page="/core/footer.jsp"/>