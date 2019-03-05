<%--
  Description:

  Author: fcbibire
  Date: Aug 21, 2007


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  01/14/2007        fcb         Fully qualified viewTax.js
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp"%>
<script type="text/javascript" src="<%=appPath%>/policymgr/taxmgr/js/viewTax.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="taxList" action="<%=appPath%>/policymgr/taxmgr/viewTax.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
        
    <tr>
        <td colspan=8>
            <oweb:message/>
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
            <fmt:message key="pm.viewTaxInfo.taxTrans.header" var="taxTransHeader" scope="request"/>
            <% String taxTransHeader = (String) request.getAttribute("taxTransHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  taxTransHeader %>" />
                <jsp:param name="divId" value="viewTaxTrans" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    
    <tr>
        <td align=center>
            <fmt:message key="pm.viewTaxInfo.taxList.header" var="panelTitleForTax" scope="page"/>
            <%
                String panelTitleForTax = (String) pageContext.getAttribute("panelTitleForTax");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTax" panelContentId="panelContentIdForTax" panelTitle="<%= panelTitleForTax %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="taxList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="taxListGrid" scope="request"/>
                    <c:set var="datasrc" value="#taxListGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            </oweb:panel>
            
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_TAX_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
