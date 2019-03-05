<%@ page import="dti.oasis.struts.IOasisAction,
                 dti.oasis.util.*,
                 java.util.*"%>
<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page language="java" %>
<%--
  Revision Date    Revised By  Description
  ---------------------------------------------------
  04/19/2006       sxm         issue 58592 - has secure and nonsecure items on the same page.
  06/12/2006       sxm         disable AUTOCOMPLETE for password type input if required
  12/10/2008       yhyang      Replace the hard code logic of setting PageBean with a call to ActionHelper.securePage().
  01/18/2010       James       Issue#101408 Record user access trail
  10/13/2010       wfu         111776: Replaced string hardcode.
  11/27/2018       dzhang      196632: Grid replacement
  ---------------------------------------------------
  (C) 2006 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        // Set a dummy strtus action to the securePage, so that the help ID (and page title) can be defined for the page.
        ActionHelper.securePage(request,"dummyActionForChangePassword");
        // Get the OasisUser object and set the last logged in date to right now.
        // We do this because we don't want the header to get into a
        OasisUser user = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);
        // Set the ignorePasswordExp attribute of the OasisUser object. We don't
        // want to have it checked now because we'll wind up in a loop caused by
        // the header jsp we include
        user.setIgnorePasswordExp(true);
        // If there was an error on a previous password change atttempt,
        // the user will be redirected back here and the "errormsg"
        // paramater will be set with the error message.
        // Display the message.
        String error = "";
        String origurl = (String) request.getAttribute(IOasisAction.PARM_URL);
        String msg = MessageManager.getInstance().formatMessage("label.header.page.changePassword");
        boolean passwordOnly = false;

        if(origurl == null)
            origurl = request.getParameter("origurl");
        else {
            msg = MessageManager.getInstance().formatMessage("label.header.page.expired"); 
            passwordOnly = true;
        }

        if (request.getParameter("errormsg") != null)
          error = request.getParameter("errormsg");

        // Disable AUTOCOMPLETE if configured
        boolean disableAutoComplete = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISABLELOGINAUTOCOMPLETE,"N")).booleanValue();
        String autoComplete = (disableAutoComplete ? "AUTOCOMPLETE=\"OFF\"" : "");
%>
<%@include file="headerpopup.jsp"%>
<%
    // restore to false
    user.setIgnorePasswordExp(false);
%>
<script type='text/javascript'>

    function show(objectId,show) {
        var obj = getObject(objectId);
        if(useJqxGrid) {
            if (obj) hideShowElementByClassName(obj, !show);
        } else {
            if(obj) obj.style.display = (show) ? 'block':'none';
        }
    }

    function changeview() {
        var check = getObject("changetype");
        if(!check) return;
        if(check[0] && check[0].checked) {
            show("passc",true);
            show("passr",false);
            show("subscribe",false);
    		window.resizeTo(520,510);
        }
        else if (check[1] && check[1].checked) {
            show("passc",false);
            show("passr",true);
            show("subscribe",false);
    		window.resizeTo(520,420);
        }
        else {
            show("subscribe",true);
            show("passc",false);
            show("passr",false);
            window.resizeTo(700,560);
        }
    }
</script>

<form NAME="userform" METHOD="POST" ACTION="updatepassword.jsp">
    <input type="hidden" name="<%=IOasisAction.PARM_URL%>" value="<%=origurl%>">
<% if(passwordOnly) { %>
    <input type="HIDDEN" value="PWD">
<% } {%>
    <tr><td>
<% if(!disallowPassword) { %>
        <input type="radio" name="changetype" value="PWD" CHECKED onclick="changeview()"><b><fmt:message key="label.header.page.changePassword"/></b>&nbsp;
        <input type="radio" name="changetype" value="REMIND" onclick="changeview()"><b><fmt:message key="label.header.page.setPassReminder"/></b>
<% } %>
<% if(subscribeEvents) { %>
        <input type="radio" name="changetype"
        value="EVENT"<%=(disallowPassword) ? "CHECKED" : ""%> onclick="changeview()"><b><fmt:message key="label.header.page.notifications"/></b>
<% }%>
    </td></tr>
<% } %>
    <tr><td>
<%    if(!disallowPassword) { %>
    	<fieldset id="passc"><legend><b><%=msg%></b></legend>
    		<table border="0" cellspacing="2" cellpadding="2" width="100%">
    			<tr>
    				<td colspan="2">&nbsp;</td>
    			</tr>
         		<tr>
            		<td width="150" align="right" >
                    <fmt:message key="label.changepass.userid"/>:</td>
            		<td align="left"><%= request.getRemoteUser() %></td>
         		</tr>
         		<tr>
            		<td width="150" align="right" class="oasis_formlabelreq">
                    <fmt:message key="label.changepass.currentpassword"/>:</td>
            		<td align="left"><input <%=autoComplete%> class="inputText" TYPE="PASSWORD" NAME="currentpassword"> </td>
         		</tr>
         		<tr>
            		<td width="150" align="right" class="oasis_formlabelreq">
                    <fmt:message key="label.changepass.password"/>:</td>
            		<td align="left"><input <%=autoComplete%> class="inputText" TYPE="PASSWORD" NAME="newpassword"> </td>
         		</tr>
         		<tr>
            		<td width="150" align="right" class="oasis_formlabelreq">
                    <fmt:message key="label.changepass.confirmpassword"/>:</td>
            		<td align="left"><input <%=autoComplete%> class="inputText" TYPE="PASSWORD" NAME="confirmpassword"> </td>
         		</tr>
    			<tr>
					<td>&nbsp;</td>
					<td class="errortext" align="left"><%=error%></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td align="left">
						<input type="submit" class="btBlueLarge" value="<fmt:message key='label.header.page.changePassword'/>">&nbsp;
						<input type="button" class="buttons" value="<fmt:message key='label.header.page.cancel'/>" onclick="baseCloseWindow()">
					</td>
				</tr>
     			<tr>
     				<td colspan="2">&nbsp;</td>
     			</tr>
     		</table>
     	</fieldset>
         <fieldset id="passr" <%=((useJqxGrid)?"class='dti-hide'":"style='display:none'")%>><legend><b><fmt:message key="label.header.page.setPassReminder"/></b></legend>
    		<table border="0" cellspacing="2" cellpadding="2" width="100%">
    			<tr>
    				<td colspan="2">&nbsp;</td>
    			</tr>
                <tr>
                    <td width="150" align="right" class="oasis_formlabelreq">
                    <fmt:message key="label.changepass.passwordreminder"/>:</td>
                    <td align="left"><input maxlength="100" class="inputText" TYPE="TEXT" NAME="reminder"> </td>
                </tr>
	            <tr>
					<td>&nbsp;</td>
					<td align="left">
						<input type="submit" class="btBlueLarge" value="<fmt:message key='label.header.page.setPassReminder'/>">&nbsp;
						<input type="button" class="buttons" value="<fmt:message key='label.header.page.cancel'/>" onclick="baseCloseWindow()">
					</td>
				</tr>
     			<tr>
     				<td colspan="2">&nbsp;</td>
     			</tr>
     		</table>
     	</fieldset>
<% } %>
<% if(subscribeEvents) {
    if(useJqxGrid) {
%>
        <fieldset id="subscribe" style="display:block" class="<%=(disallowPassword) ? "" : "dti-hide"%>">
            <legend><b><fmt:message key="label.header.page.notifications"/></b></legend>
            <iframe name="subscribeframe" width="100%" height="240" allowtransparency="true"
                    frameborder="0" src='maintainevents.jsp'></iframe>
        </fieldset>
<%
    } else {
%>
        <fieldset id="subscribe" style="display:<%=(disallowPassword) ? "block" : "none"%>">
            <legend><b><fmt:message key="label.header.page.notifications"/></b></legend>
            <iframe name="subscribeframe" width="100%" height="240" allowtransparency="true"
                    frameborder="0" src='maintainevents.jsp'></iframe>
        </fieldset>
<%
    }
   }%>
     </td></tr>
     <tr>
     	<td>&nbsp;</td>
     </tr>
<%
if(origurl == null) {
%>
<SCRIPT type="text/javascript">
    changeview();
</SCRIPT>
<%
}
%>
</form>
<jsp:include page="footerpopup.jsp"/>
<%
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>