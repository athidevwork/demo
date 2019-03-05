<%--
  Description:

  Author: unknown.
  Date: Oct 7, 2004


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/17/2007       kshen       Added try-catch block to handle conn (Make sure connection
                               is closed properly in any case).
  09/26/2008       Larry       Issue 86826 DB connection leakage change                             
  -----------------------------------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page contentType="text/xml;charset=UTF-8" language="java" import="dti.oasis.var.FormRule"%>
<%@ page import="dti.oasis.var.RuleDAO"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="dti.oasis.var.IRuleDAO"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="dti.oasis.util.DatabaseUtils" %>
<%
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");
    Connection conn = null;
    try {
        String values = request.getParameter("values");
        String orig = request.getParameter("orig");
        String formid = request.getParameter("formid");
        FormRule rule = new FormRule(formid, "VALIDATION");
        rule.setFieldsRows(values, orig);
        conn = dti.oasis.struts.ActionHelper.getConnection(request);
        IRuleDAO ruleDao = new RuleDAO(conn);
        rule.loadRules(ruleDao);
        rule.applyRules();

        String ruleReturn = rule.resultsToXml();
        out.println(ruleReturn);
    }
    catch (Exception e) {
        out.println("<Result>");
        out.println("<ERROR>");
        out.println("<msg><![CDATA[");
        e.printStackTrace(new PrintWriter(out));
        out.println("]]></msg>");
        out.println("</ERROR>");
        out.println("</Result>");
    } finally {
        if (conn != null) {
            DatabaseUtils.close(conn);         
        }
    }
%>
