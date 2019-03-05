<%@ page language="java"%>
<%--
  JSP for displaying entity search criteria but without a list of entities.

  Author: Gerald C. Carney
  Date: Oct 21, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance

  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean class="dti.oasis.tags.OasisFields" id="fieldsMap" scope="request"/>

<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/importmgr/js/dataImport.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="DataImportForm" enctype="multipart/form-data" action="ciImport.do" method="POST">
<jsp:include page="/cicore/commonFormHeader.jsp"/>
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td>
            <oweb:panel hasTitle="false" panelContentId="ciImport">
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
<jsp:include page="/core/footer.jsp"/>