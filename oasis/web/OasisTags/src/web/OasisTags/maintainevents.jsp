<%@ page language="java" import="dti.oasis.util.*,
                 dti.oasis.tags.OasisFormField,
                 org.apache.struts.util.LabelValueBean,
                 java.util.ArrayList,
                 dti.oasis.struts.IOasisAction,
                 dti.oasis.struts.ActionHelper"%>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Pragma", "no-cache");
    // get IDs
    String dbPoolId = ActionHelper.getDbPoolId(request);
    String userId = ((OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER)).getUserId();
    // process request
    String process = request.getParameter("process");
    if (process==null) process="";
    try {
	    if(process.equals("SAVE")) {
            // Update email and events
	        EventUtils.updUserEvents(dbPoolId, userId, request.getParameter("email"),
                    request.getParameter("selectedevents"), "$");
	    }
    }
    catch(Throwable e) {
    	request.setAttribute("errormsg",StringUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage());
    }
    // get field(s) ready
    String email = EventUtils.getUserEmail(dbPoolId, userId);
    OasisFormField emailField = new OasisFormField("email","Email",true,true,null,null,false);
    pageContext.setAttribute("email", BeanDtiUtils.createValueBean(emailField,email));
    // get event lists ready
    ArrayList profEvents = EventUtils.getProfEvents(dbPoolId, userId);
    ArrayList userEvents = EventUtils.getUserEvents(dbPoolId, userId);
    int sz = userEvents.size();
    for (int i=0;i<sz;i++) {
        String event = ((LabelValueBean)userEvents.get(i)).getValue();
        int sz2 = profEvents.size();
        for(int j=0;j<sz2;j++) {
            if(((LabelValueBean)profEvents.get(j)).getValue().equals(event)) {
                profEvents.remove(j);
                break;
            }
        }
    }
    OasisFormField profEventField = new OasisFormField("profEvents","Available",true,true,null,null,false);
    pageContext.setAttribute("profEvents", BeanDtiUtils.createValueBean(profEventField,""));
    OasisFormField userEventField = new OasisFormField("userEvents",null,true,true,null,null,false);
    pageContext.setAttribute("userEvents",BeanDtiUtils.createValueBean(userEventField,""));
    // handle error message
    String error = (String)request.getAttribute("errormsg");
    if (error==null) error = "";
    String appPath = request.getContextPath();
    String msg = "";
    if(profEvents.size()==0 && userEvents.size()==0)
        msg = "You are not eligible to subscribe to any notifications.";
%>
<html>
<link href="<%=appPath%>/css/oasisnew.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=appPath%>/js/xmlproc.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/js/edits.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/js/gui.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/js/validation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/js/scriptlib.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<body class="basebackground">
<% if(msg!="") {%>
    <div class="errortext"><%=msg%></div>
<% } else { %>
    <table cellspacing="0" cellpadding="0" border="0"><tr>
    <td class="content" width="2">&nbsp;</td>
    <td class="bodycontent" id="maincontent" valign="top" width="100%">
    <table width="100%" border="0" cellpadding="2" cellspacing="0">

    <%
        if(error!="") {
    %>
    <tr><td class="errortext"><%=error%></td></tr>
    <% }
    %>

    <form name="maintain" METHOD="POST" ACTION="maintainevents.jsp">
    <input type="hidden" name="process">
    <input type="hidden" name="selectedevents">

    <tr><td><table border="0" cellspacing="0" cellpadding="2" width="75%">
      <tr><td colspan="4"><table>
        <tr><oweb:text oasisFormField="<%=emailField%>" name="email"/>
          <td><input type="button" value="Save Changes" class="buttons" onclick="save()"></td>
          <td><input type="button" value="Cancel" class="buttons" onclick="cancel()"></td>
         </tr>
      </table></td></tr>
    </table></td></tr>

    <tr><td style="font-size:2pt">&nbsp;</td></tr>
    <tr><td colspan="4" class="headerbox left right top bottom">Events</td></tr>
    <tr><td class="top left right bottom" colspan="4"><table cellpadding="2" width="100%">
      <tr valign="top">
        <oweb:select oasisFormField="<%=profEventField%>" listOfValues="<%=profEvents%>"
                     size="8"  name="profEvents" style="width:200px" />
        <td><br><input type="button" name="addevent" value="Add >" class="buttonText" onclick="doaddevent()"><br>
                <input type="button" name="delevent" value="< Remove" class="buttonText" onclick="dodelevent()"></td>
        <oweb:select oasisFormField="<%=userEventField%>" listOfValues="<%=userEvents%>"
                     size="8"  name="userEvents" style="width:200px"/>
      </tr>
    </table></td></tr>
    <tr><td style="font-size:2pt">&nbsp;</td></tr>
    </table>
    </td></tr></table>
    </form>

    <script type='text/javascript'>
    function addOption(el,txt,val) {
        var oOption = document.createElement("OPTION");
        el.options.add(oOption);
        oOption.innerText = txt;
        oOption.value = val;
    }

    function removeOption(el) {
        el.remove(el.selectedIndex);
    }

    function doaddevent() {
        var idx = document.maintain.profEvents.selectedIndex;
        if (idx==-1)
            return;
        var txt = document.maintain.profEvents.options(idx).innerText;
        var val = document.maintain.profEvents.value;
        addOption(document.maintain.userEvents,txt,val);
        removeOption(document.maintain.profEvents);
    }

    function dodelevent() {
        var idx = document.maintain.userEvents.selectedIndex;
        if (idx==-1)
            return;
        var txt = document.maintain.userEvents.options(idx).innerText;
        var val = document.maintain.userEvents.value;
        addOption(document.maintain.profEvents,txt,val);
        removeOption(document.maintain.userEvents);

    }

    function getCSV(el) {
        var sz = el.options.length;
        var rc = "";
        for(var i=0;i<sz;i++) {
            if(i>0) rc+="$";
            rc+=el.options[i].value;
        }
        return rc;
    }

    function cancel() {
        document.maintain.process.value="";
        baseOnSubmit(document.maintain);
    }

    function save() {
        if(isWhitespace(document.maintain.email.value)) {
            alert('Please provide an email address.');
            return;
        }
        document.maintain.selectedevents.value=getCSV(document.maintain.userEvents);
        document.maintain.process.value="SAVE";
        baseOnSubmit(document.maintain);
    }

    </script>
<% } %>
<link href="<%=appPath%>/css/oasiscustom.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
</body>
</html>