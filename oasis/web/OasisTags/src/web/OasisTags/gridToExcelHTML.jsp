<%@ page import="java.util.logging.Logger"%>
<%@ page import="dti.oasis.util.LogUtils"%>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.tags.OasisGrid"%>
<%--
  Description: Utility JSP for opening HTML file in Excel.

  Author: GCCarney
  Date: Jan 2, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/18/2007       GCC         Changed contentType attribute of page
                               directive from "application/vnd.ms-excel" to
                               "application/unknown" (suggested by Kyle Shen).
                               Kyle's comments:  contentType must be
                               "application/x-msdownload" or
                               "application/unknown";  otherwise, the XLS file
                               may be opened in the current page (the current
                               browser window) in some cases (it depends on the
                               configuration of the local machine), which we
                               don't want. The "application/x-msdownload" and
                               "application/unknown" content types force the
                               browser to display a download dialog.  We don't
                               need to worry about the content type.  Because we
                               have set the value of attribute
                               "Content-Disposition" (dispType and filename),
                               the browser will know that this file is an XLS file.
                               The system will know to use Excel to open the file
                               (if MS Excel is properly installed in the
                               local machine).
  -----------------------------------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page contentType="application/unknown"	 %>
<%
    String dispType = request.getParameter("dispositionType");
    if (StringUtils.isBlank(dispType)) {
        dispType = OasisGrid.ATTACH_DISP_TYPE;
    }
    response.setHeader("Content-Disposition", dispType + "; filename=grid.xls");
    String values = request.getParameter("textForFile");
    Logger lggr = LogUtils.enterLog(getClass(), "jsp");
    lggr.fine(values);
    out.print(values);
    out.flush();
%>
<%=values%>
