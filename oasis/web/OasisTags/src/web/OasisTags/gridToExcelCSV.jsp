<%@ page import="dti.oasis.util.StringUtils"%><%@ page import="dti.oasis.tags.OasisGrid"%><%@ page import="dti.oasis.app.ApplicationContext" %><%@ page import="dti.oasis.filter.CharacterEncodingFilter" %><%@page contentType="application/unknown"%><%
/*
  Description: Utility JSP for opening CSV file in Excel.

  Author: GCCarney
  Date: Jan 2, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/18/2007       GCC         Changed contentType attribute of page
                               directive from "application/vnd.ms-excel" to
                               "application/unknown" (suggested by Kyle Shen).
                               Kyle's comments:  contentType must be
                               "application/x-msdownload" or
                               "application/unknown";  otherwise, the CSV file
                               may be opened in the current page (the current
                               browser window) in some cases (it depends on the
                               configuration of the local machine), which we
                               don't want. The "application/x-msdownload" and
                               "application/unknown" content types force the
                               browser to display a download dialog.  We don't
                               need to worry about the content type.  Because we
                               have set the value of attribute
                               "Content-Disposition" (dispType and filename),
                               the browser will know that this file is a CSV file.
                               The system will know to use Excel to open the file
                               (if MS Excel is properly installed in the
                               local machine).
  10/21/2010        wer        issue#112038 - fix Excel export for running in HTTPS to allow cacheing of file so IE can store the file in a temp file.
  06/27/2017        kshen      Issue 186298. Remove blank rows in CSV file.
  -----------------------------------------------------------------------------
*/

    String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Content-Type", "text/csv; charset=" + encoding);
// Can not instruct the browser to not cache the export file when used with HTTPS or the Microsoft Excel application will fail to open the file
//    See http://support.microsoft.com/kb/316431 for details
//    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
//    response.setHeader("Pragma", "no-cache");


    String dispType = request.getParameter("dispositionType");
    if (StringUtils.isBlank(dispType)) {
        dispType = OasisGrid.ATTACH_DISP_TYPE;
    }

    String sColNames = request.getParameter("colNames");

    String columnNames ="";
    if (!StringUtils.isBlank(sColNames)) {
        for (String columnName: sColNames.split(",")) {
            columnNames += "\""+columnName.replaceAll(":;:",",")+"\",";
        }
    }

    String gridId = request.getParameter("gridId");

    String fileName = "";
    if (StringUtils.isBlank(gridId)) {
        fileName = "grid.csv";
    } else {
        fileName = gridId+".csv";
    }

    response.setHeader("Content-Disposition", dispType + "; filename="+fileName);
    String values = columnNames + request.getParameter("textForFile");
    out.print(values);
    out.flush();
%>
