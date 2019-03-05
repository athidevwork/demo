<%@ page import="dti.oasis.util.PageBean,
                 dti.oasis.struts.IOasisAction,
                 dti.oasis.util.StringUtils,
                 java.util.*,
                 dti.oasis.security.J2EESecuritySelector"%>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page language="java"%>
<%--
  Description: Forgot Password page
  
  Author: jbe
  Date: Feb 18, 2005
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  9/13/2005         jbe       Use type='text/javascript' instead of language='javascript'  
  06/12/2006        sxm        disable AUTOCOMPLETE for password type input if required
  08/31/2006        sxm        add validation of DB pool ID
  04/09/2008        wer        Enhanced to support configuring a dbPoolId for a role associated with a user or it's group, and removed passing of DBPOOLID as request parameter
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    PageBean bean = new PageBean();
    bean.setLeftNavActions(new ArrayList());
    bean.setLeftNavMenu(new ArrayList());
    bean.setTopNavMenu(new ArrayList());
    bean.setTitle("User Settings");
    request.setAttribute(IOasisAction.KEY_PAGEBEAN,bean);
    String submit = request.getParameter("submit");
    boolean changed = false;
    String reminder = "", error = null;
    String dbPool = ActionHelper.getDbPoolId(request);
    String user = request.getParameter("j_username");
    if(user==null) user = "";
    // did we submit?
    if(!StringUtils.isBlank(submit)) {
        // get & validate reminder
        reminder = request.getParameter("reminder");
        user = request.getParameter("user");
        String password = request.getParameter("newpassword");
        String confPassword = request.getParameter("confirmpassword");
        if(StringUtils.isBlank(user)) // missing user
            error = "Invalid user.";
        if(error==null &&
                (StringUtils.isBlank(password) || StringUtils.isBlank(confPassword) ||
                !password.equals(confPassword)))  // bad passwords!
            error = "Invalid passwords";
        if(error==null && StringUtils.isBlank(reminder))   // none was passed!
            error = "Invalid password reminder.";


        if(error==null) {
            ArrayList list = new ArrayList(1);
            list.add("password_reminder");

            // get stored reminder
            Map map = Authenticator.getUserData(dbPool,list,user);
            String dReminder = (map!=null) ? (String) map.get("password_reminder") : null;
            // do we have a match?
            if(reminder!=null && dReminder!=null && reminder.equalsIgnoreCase(dReminder)) {
                try { // change password
                    J2EESecuritySelector.getJ2EESecurityFactory().getInstance().resetPassword(request, user, password);
                    changed = true;
                }
                catch( IllegalArgumentException ie ) {
                    error = "Invalid user and/or password.  " + ie.toString().substring(ie.toString().indexOf(":")+2) ;
                }

            }
            else { // we do NOT have a match.
                error = "Invalid user and/or password reminder.";
            }
        }
    }
    if(error==null) error = "";
    // Disable AUTOCOMPLETE if configured
    boolean disableAutoComplete = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISABLELOGINAUTOCOMPLETE,"N")).booleanValue();
    String autoComplete = (disableAutoComplete ? "AUTOCOMPLETE=\"OFF\"" : "");
%>
<%@ include file="headerpopup.jsp"%>
<script type='text/javascript'>
    if(<%=changed%>) {
        alert("Your password has been changed.");
        baseCloseWindow();
    }
    else
        window.resizeTo(520,450);

    var DISPLAY_FIELD_EXTENTION = '<%=FormatUtils.DISPLAY_FIELD_EXTENTION%>';
    var REQ_reminder = true;
	var REQ_user = true;
    function checkValid() {
    	if(!validate(document.forgotpassword))
    		return false;
        if(getObject("newpassword").value=="") {
        	alert("Please enter a password.")
        	getObject("newpassword").focus();
        	return false;
        }
        if(getObject("newpassword").value!=getObject("confirmpassword").value) {
            alert("Your passwords must match.");
            getObject("newpassword").focus();
            return false;
        }
        return true;
    }
</script>
<form name="forgotpassword" action="forgotpassword.jsp" method="post" onsubmit="return(checkValid());">
<tr><td>&nbsp;</td>
<td class="errortext" align="left"><%=error%></td>
</tr>
<tr>
    <td align="right" class="oasis_formlabelreq">
    <fmt:message key="label.forgotpass.userid"/>:</td>
    <td align="left">
    <input <%=autoComplete%> type="text" maxlength="100" class="inputText" name="user"
    title="<fmt:message key="label.forgotpass.userid"/>" value="<%=user%>"></td>
</tr>
<tr>
    <td align="right" class="oasis_formlabelreq">
    <fmt:message key="label.forgotpass.passwordreminder"/>:</td>
    <td align="left">
    <input type="text" maxlength="100" name="reminder" class="inputText"
    title="<fmt:message key="label.forgotpass.passwordreminder"/>" value="<%=reminder%>"></td>
</tr>
<tr>
    <td align="right" class="oasis_formlabelreq">
    <fmt:message key="label.forgotpass.password"/>:</td>
    <td align="left"><input <%=autoComplete%> type="password" name="newpassword" class="inputText"
    title="<fmt:message key="label.forgotpass.password"/>"></td>
</tr>
<tr>
    <td align="right" class="oasis_formlabelreq">
    <fmt:message key="label.forgotpass.confirmpassword"/>:</td>
    <td align="left">
    <input <%=autoComplete%> type="password" name="confirmpassword" class="inputText"
    title="<fmt:message key="label.forgotpass.confirmpassword"/>">
    </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2" align="center">
<input type="submit" class="btBlueLarge" name="submit" value="Reset Password">&nbsp;
<input type="button" class="buttons" value="Cancel" onclick="baseCloseWindow()">
</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2">
</td></tr>
</form>
<jsp:include page="footerpopup.jsp"/>
