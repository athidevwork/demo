<%--
  Description:

  Author: Bhong
  Date: Oct 23, 2008

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
   12/05/2018        xjli        195889 - Reflect grid replacement project changes.
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<input type="hidden" name="policyNo" value="<c:out value="${policyNo}"/>"/>
<tr>
    <td colspan=8>
        <oweb:message showAllMessages="true"/>
    </td>
</tr>
<script type="text/javascript">
    function handleOnLoad() {
        if (confirm(getMessage("pm.amalgamation.saveAsOfficialFowardConfirm.info"))) {
            // Forward to parent page
            var url = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + getObject("policyNo").value;
            getParentWindow().setWindowLocation(url);
        }
        else {
            getParentWindow().refreshPage();
        }
    }
</script>
<jsp:include page="/core/footerpopup.jsp"/>


