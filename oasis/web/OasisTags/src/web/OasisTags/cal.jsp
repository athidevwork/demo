<%@ page import="java.util.Date,
                 java.text.DateFormat"%>
<%@ page import="dti.oasis.http.Module" %>
<%@ page language="java"%>
<%--
  Description: Calendar Popup
  
  Author: jbe
  Date: Jul 8, 2003
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  9/13/2005         jbe       Use type='text/javascript' instead of language='javascript'
  6/13/2006         sxm       remove parameter 'event' and call handleOnChange() instead
                              to prevent possible Cross Site Scripting
  8/14/2006        gjli       remove window.moveTo(10,10);           
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Pragma", "no-cache");
    String fieldName = request.getParameter("fld");
    String dateMask = request.getParameter("DATE_MASK");
    String corePath = Module.getCorePath(request);
%>
<HTML>
<head>
<title>Calendar</title>
<link href="<%=corePath%>/css/oasisnew.css" rel="STYLESHEET" type="text/css"/>
</head>
<body class="basebackground" onload="presetdate()">
<form name="calendar">
<input type="hidden" name="calMode" value="DATE"/>
<input type="hidden" name="calDay" value="<%=DateFormat.getDateInstance(DateFormat.SHORT).format(new Date())%>"/>
<input type="hidden" name="DateMask" value="<%=dateMask%>" />
<table width="100%"><tr><td class="content">
<div style="padding-left: 10px; display:block;padding-right: 5px;">
    <fieldset ID="calendar">
    <legend><b>Calendar</b></legend>
    <table width="100%" border="0" cellpadding="1" cellspacing="0">
    <tbody><tr><td align="center">
    <DIV STYLE="behavior:url('js/calendar.htc')" ID="Calendar1"></DIV>
    </td></tr>
    <tr><td align="center"><div id="buttondiv" style="display:none">
    <input class="buttons" type="button" name="btnOk" value="  OK  " onClick="javascript:btnClick('OK')"/>
    &nbsp;&nbsp;
    <input class="buttons" type="button" name="btnCancel" value="Cancel" onClick="javascript:btnClick('CANCEL')"/>
    </div>
    </td></tr></table>
    </fieldset>
</div>
</td></tr></table>
</form>
<script for="Calendar1" type="text/javascript" event="onreadystatechange">
document.all("buttondiv").style.display="block";
</script>
<script type="text/javascript">
function btnClick(sBtn)
{
	if ( sBtn == 'CANCEL' )
	{
		document.calendar.calMode.value = 'CANCEL';
		baseCloseWindow();
	}
	else
	{
        try {
			eval(<%=fieldName %>);
		}
		catch (ex){
            alert("***WARNING***\nYou have navigated away from the calling page.\nThis calendar will be closed to avoid system error!");
            baseCloseWindow();
            return;
		}
		var obj = eval(<%=fieldName %>);
		var calday = document.all("Calendar1").day < 10 ? ( "0" + document.all("Calendar1").day) : document.all("Calendar1").day;
		var calmon = document.all("Calendar1").month < 10 ? ( "0" + document.all("Calendar1").month) : document.all("Calendar1").month;
		if ( document.calendar.DateMask.value == "mm/dd/yyyy" ||
             document.calendar.DateMask.value.substring(0,1) == "m")
			document.calendar.calDay.value =  calmon + '/' + calday + '/' + document.all("Calendar1").year;
		else
			document.calendar.calDay.value = calday + '.' + calmon + '.' + document.all("Calendar1").year;
		obj.value=document.calendar.calDay.value;
        // Check for existence of handleOnChange().  If exists, then fire it.
        if (eval("window.opener.handleOnChange"))
            opener.handleEvent("handleOnChange("+"<%=fieldName%>".replace("window.opener.","")+");");
        baseCloseWindow();
	}
}
 function presetdate() {
	var obj = eval(<%=fieldName %>);
	if(obj.value==null || obj.value==""){return;}
	var newdate = new Date(obj.value);
	var aYear = newdate.getFullYear();
	if(isNaN(aYear)) {return;}
	document.all("Calendar1").year=aYear;
  	document.all("Calendar1").month=newdate.getMonth() + 1;
  	document.all("Calendar1").day=newdate.getDate();
}
//window.moveTo(10,10);
</script>
</body></html>

