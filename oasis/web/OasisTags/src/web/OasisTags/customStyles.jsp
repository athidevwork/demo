<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="dti.oasis.app.AppException" %>
<%@ page import="dti.oasis.error.ExceptionHelper" %>
<%--
  Description: JSP for load customStyles.css

  Author: James Gu
  Date: Dec 10, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Content-Type", "text/css");
    String customStylesFile = ApplicationContext.getInstance().getProperty("custom.styles.file");
    InputStream inputStream = null;
    try {
        inputStream = ActionHelper.getResourceAsInputStream(customStylesFile);
        byte[] data = new byte[1024];
        int count = inputStream.read(data);
        while (count != -1) {
            response.getOutputStream().write(data, 0, count);
            count = inputStream.read(data);
        }
        response.getOutputStream().flush();
    } catch (Exception e) {
        AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting custom style file.", e);
        e.printStackTrace();
        throw ae;
    } finally {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }
%>
