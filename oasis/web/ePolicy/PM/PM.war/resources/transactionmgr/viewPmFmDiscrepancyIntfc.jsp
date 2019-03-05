<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: View Pm/FM Discrepancy Intfc sub-page

  Author: jmp
  Date: Junly 19, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/11/2010       bhong       Changed "discrepancyIntfcListGrid" to "discrepancyInftcListGrid"
                               to match with column name in procedure
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="discrepancyInftcListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="discrepancyInftcListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%String appPath = request.getContextPath();%>

<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/viewPmFmDiscrepancy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<tr>
    <td align=center>
        <table class="table" cellpadding=0 cellspacing=0 width=98%>
            <tr>
                <td class="tablehdr">
                    &nbsp;<fmt:message key="pm.pmFmDiscrepancy.pmFmTransInterface.header"/>
                </td>
            </tr>
            <tr>
                <td colspan="8" align=center><br/>
                    <c:set var="gridDisplayGridId" value="discrepancyInftcList" scope="request"/>
                    <c:set var="datasrc" value="#discrepancyInftcList1" scope="request"/>
                    <c:set var="gridId" value="discrepancyInftcListGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="intfc_"/>
                    <% BaseResultSet dataBean = discrepancyInftcListGridDataBean;
                       XMLGridHeader gridHeaderBean = discrepancyInftcListGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </table>
    </td>
</tr>
