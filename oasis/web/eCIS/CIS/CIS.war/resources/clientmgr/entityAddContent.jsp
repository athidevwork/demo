<%@ page language="java"%>
<%@ page import="dti.ci.helpers.ICIEntityConstants"%>
<%--
  Description: Add Person

  Author: Kyle Shen
  Date: Oct 17, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  12/02/2008       kshen       Added hidden field for system parameter "ZIP_CODE_ENABLE",
                               "ZIP_OVERRIDE_ADDR", and "CS_SHOW_ZIPCD_LIST".
  08/20/2010       kenny       Issue 110474: Added TOKEN_KEY
  11/16/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@ include file="/cicore/commonFormHeader.jsp" %>
<jsp:include page="/cicore/entityCommonFields.jsp"/>

<input type="hidden" name ="<%=ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY%>" value="<%=(String) request.getAttribute(ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY)%>" />
<input type="hidden" name ="<%=ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY%>" value="<%=(String) request.getAttribute(ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY)%>" />
<input type="hidden" name ="<%=ICIEntityConstants.VENDOR_VERIFY_SYS_PARAM%>" value="<%=(String) request.getAttribute(ICIEntityConstants.VENDOR_VERIFY_SYS_PARAM)%>" />
<input type="hidden" name="processAfterSave" value="<%=(String) request.getAttribute("processAfterSave")%>"/>

<tr>
    <td colspan="6">
        <b><oweb:message informationStyleClass="txtBold"/></b>
    </td>
</tr>

<%
    if ((boolean) (request.getAttribute("duplicatedEntityExists"))) {
%>
    <script type="text/javascript">
        setIsChangedFlag(true);
    </script>
<%
    }
%>

<c:choose>
    <c:when test="${formAction=='ciEntityOrgAdd.do'}">
        <c:set var="EntityAddActionGroupName" value="CI_ENTORGUSAADD_AIG" scope="request"></c:set>
    </c:when>
    <c:when test="${formAction=='ciEntityPersonAdd.do'}">
        <c:set var="EntityAddActionGroupName" value="CI_ENTPERUSAADD_AIG" scope="request"></c:set>
    </c:when>
</c:choose>

<jsp:useBean id="EntityAddActionGroupName" class="java.lang.String" scope="request"/>
<c:choose>
    <c:when test="${EntityAddActionGroupName=='CI_ENTORGUSAADD_AIG' || EntityAddActionGroupName=='CI_ENTPERUSAADD_AIG'}">
        <tr>
            <td colspan="6">
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="hasPanelTitle" value="false"/>
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="divId" value="AddEntity"/>
                    <jsp:param name="headerText" value=""/>
                    <jsp:param name="excludeAllLayers" value="true"/>
                </jsp:include>
            </td>
        </tr>
        <tr>
            <td colspan="6" align="center" style="padding-top:6px">
                <oweb:actionGroup actionItemGroupId="<%=EntityAddActionGroupName%>"/>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
        <tr>
            <td colspan="6">
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="hasPanelTitle" value="false"/>
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="divId" value="AddEntity"/>
                    <jsp:param name="headerText" value=""/>
                    <jsp:param name="excludeAllLayers" value="true"/>
                </jsp:include>
            </td>
        </tr>
    </c:otherwise>
</c:choose>

