<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: gjlong
  Date: Jun 15, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/18/2008       Bhong       Copy from output process
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<form name="documentList" action="transaction.do" method="POST">
  <table>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
   </table>
</form>
