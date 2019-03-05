<%@ page language="java" %>
<%--
  Description:

  Author: ldong
  Date: July 27, 2009


  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/28/2009       Leo         Issue 95771
  04/02/2014       Elvin       Issue 149361:
                                         1. use getObject instead of document.all
                                         2. re-align radio button, and check Displayed Members as default
  06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script language="javascript" src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=cisPath%>/orggroupmgr/js/orgGroupPrintSelect.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="CIOrgGroupForm" action="orgGroupView.do" method="POST">
    <tr>
        <TD>&nbsp;</TD>
        <TD colspan="2" align=left><input type="radio" name="print_type" value="DISPLAYED" checked><span
            class='oasis_formlabel'>Displayed Members</span></TD>
    </tr>
    <tr>
        <TD>&nbsp;</TD>
        <TD colspan="2" align=left><input type="radio" name="print_type" value="CURRENT"><span
            class='oasis_formlabel'>Current Members</span></TD>
    </tr>
    <tr>
        <TD colspan="6" align=center>&nbsp;</TD>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="CI_ORGGROUP_PRINT_AIG" layoutDirection="horizontal"
                              cssColorScheme="blue"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>