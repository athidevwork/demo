<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    Description: Required to set JavaScript variables to be used bu edits.js and gui.js.
        This jsp has to be included in header.jsp and headerpopup.jsp

  Author: mgitelman
  Date: April 8, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/27/2009       kenney      enh to support phone format
  10/15/2010       wfu         109875 - Change to support Chinese Date format.
  02/18/2011       ldong       112568 - Enchanced to handle the new date format DD/MON/YYYY
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ page import="dti.oasis.util.FormatUtils" %>
<%@ page import="dti.oasis.session.UserSessionManager" %>

<%
    if (UserSessionManager.isConfigured()) {
%>
<script type="text/javascript">
var DISPLAY_FIELD_EXTENTION = '<%=FormatUtils.DISPLAY_FIELD_EXTENTION%>';
var PHONENUMBER_FORMAT = '<%=FormatUtils.getLocalPhoneNumberFormat()%>';
</script>
<!--Add support for Date Format Internationalization -->
<%
        if (FormatUtils.isDateFormatUS()) {
            /*
            *   TODO: Phase 2. Make it generic
            */
%>
<!--For now just use dd/mm/yyyy-->
            <script type="text/javascript">
                var calendarFmt = '%m/%d/%Y';
                var dateFormatUS = true;
            </script>
<%
        } else {
%>
            <script type="text/javascript">
                var calendarFmt = '%d/%m/%Y';
                if (typeof localeDataMask != 'undefined' && localeDataMask != null) {
                    calendarFmt = localeDataMask.toLowerCase().replace("yyyy", "%Y").replace("mm", "%m").replace("dd", "%d").replace("mon","%b");
                }
                var dateFormatUS = false;
            </script>

<%
        }
    }
%>
<script type="text/javascript">
var LOCALE = '<%=LocaleUtils.getJsNbrFormatterLocale()%>';
</script>