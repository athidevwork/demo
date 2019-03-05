<%@ page language="java" %>

<%--
  Description: Add Address Page.

  Author: Kyle Shen
  Date: Oct 15, 2008

  Revision Date    Revised By  Description
  ---------------------------------------------------
  12/02/2008       kshen       Added hidden field for system parameter "ZIP_CODE_ENABLE",
                               "ZIP_OVERRIDE_ADDR", and "CS_SHOW_ZIPCD_LIST".
  08/27/2009       Leo         Issue 95363
  05/13/2011    Blake      Modified for issue 120677
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  11/27/2018       hxk         Issue 196791
                               Add tags so form is not malformed w/ closing tags as a temporary fix until
                               jsp form/tag construction problem is fully addressed.
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>


<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/openAddressRoleChgPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<tr>                                                    <%-- Temporary fix --%>
    <td width="100%" colspan="6">                       <%-- Temporary fix --%>
        <FORM action="ciAddressAdd.do" method="POST">
            <table width="100%">                        <%-- Temporary fix --%>
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="saveSucceed" value="<%=request.getAttribute("saveSucceed")%>"/>

    <tr>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="hasPanelTitle" value="false"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="panelContentForAddress"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_ADDR_ADD_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>
<%-- These closing tags are temporary until form issue can be fully addressed --%>
            </table>                                <%-- Temporary fix --%>
        </form>                                     <%-- Temporary fix --%>
    </td>                                           <%-- Temporary fix --%>
</tr>                                               <%-- Temporary fix --%>

<jsp:include page="/core/footerpopup.jsp"/>