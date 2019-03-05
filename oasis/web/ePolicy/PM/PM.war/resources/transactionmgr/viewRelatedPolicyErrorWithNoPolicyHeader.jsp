<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%--
  Description: View Related Policy Errors page

  Author: ryzhao
  Date: Mar 18, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<form>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <fmt:message key="pm.processErp.error.policy.header" var="panelTitleForViewPolError" scope="page"/>
            <%
                String panelTitleForViewPolError = (String) pageContext.getAttribute("panelTitleForViewPolError");
            %>
            <oweb:panel panelTitleId="panelTitleIdForViewPolError" panelContentId="panelContentIdForViewPolError" panelTitle="<%= panelTitleForViewPolError %>" >
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="relatedPolicyList" scope="request" />
                        <c:set var="gridDisplayGridId" value="relatedPolicyListListGrid" scope="request" />
                        <c:set var="datasrc" value="#relatedPolicyListGrid1" scope="request" />
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align="center">
            <input type="button" name="Close" value="Close" onclick="javascript:closeThis();" class="buttonText">
        </td>
    </tr>
    <script type="text/javascript">
    function closeThis() {
        var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
        if (divPopup) {
            window.frameElement.document.parentWindow.closeDiv(divPopup);
        }
    }
    function handleOnLoad() {
        // change the title for the Process ERP issue 113559.
        if (getObject("pageTitleForpageHeader")) {
            getObject("pageTitleForpageHeader").innerText = getMessage("pm.processErp.error.policy.page.title");
        }
    }
    </script>
    <jsp:include page="/core/footerpopup.jsp" />



