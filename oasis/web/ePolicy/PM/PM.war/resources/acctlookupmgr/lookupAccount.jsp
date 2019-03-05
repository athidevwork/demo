<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page language="java"%>
<%--
  Description: Lookup Billing Account page

  Author: sxm
  Date: mar 14, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang 194100 Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/lookupAccount.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="lookupAccount.do" method="POST" name="lookupAccountList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<tr>
    <td>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td>&nbsp;&nbsp;</td>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<tr><td>&nbsp;</td></tr>

<% if (request.getAttribute(RequestIds.DATA_BEAN) != null) { %>
    <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <tr>
        <td align=center>
            <table class="table" cellpadding=0 cellspacing=0 width=98%>
                <tr>
                    <td class="tablehdr">
                        &nbsp;<fmt:message key="pm.accountlookupList.header"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="lookupAccountList" scope="request" />
                        <c:set var="gridDisplayGridId" value="lookupAccountListGrid" scope="request" />
                        <c:set var="datasrc" value="#lookupAccountListGrid1" scope="request" />
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
            </table>
        </td>
    </tr>
<% } %>
<tr><td>&nbsp;</td></tr>

<tr>
    <td colspan="7" align="center">
        <oweb:actionGroup actionItemGroupId="PM_ACCTLKUP_AIG" layoutDirection="horizontal" />
    </td>
</tr>
 
<tr><td>&nbsp;</td></tr>
<jsp:include page="/core/footerpopup.jsp" />
