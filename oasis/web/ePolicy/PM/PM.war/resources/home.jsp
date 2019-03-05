<%@ page import="dti.oasis.http.Module" %>
<%@ page import="java.util.Enumeration" %>
<%@ page language="java" %>
<%--
  Description:

  Author: wreeder
  Date: Jul 12, 2006

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/16/2007       sxm         Include JavaScript files for Create Policy function
  02/27/2007       sxm         Replaced JavaScript include with common.jsp
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript">
    // add parameters to the query string
    function getMenuQueryString(id, url)
    {
        var tempUrl = '';
        /*
          if (id == 'CM_ADD_CLAIM_MI') {
            tempUrl = getDefaultAddClaimParmString();
          }
        */
        return tempUrl;
    }
</script>

<form>
    <tr><td colspan=8>
      <c:set var="hasTitle" value="false"></c:set>
      <oweb:panel panelContentId="panelcontentForHome" panelTitleId="panelTitleIdForHome" hasTitle="false">
          <tr><td>
                 <b>
                     <fmt:message key="pm.home.page.description"/>
                 </b>
          </td></tr>
      </oweb:panel>
    </td></tr>

<jsp:include page="/core/footer.jsp"/>
