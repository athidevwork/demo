<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.cachemgr.Cache" %>
<%@ page import="dti.oasis.cachemgr.CacheManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.codelookupmgr.CodeLookupManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.struts.util.LabelValueBean" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ page language="java" %>
<%--
  Description:

  Author: mgitelman
  Date: 10/13/2017


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
    String process = request.getParameter("process");
    System.out.println("process: "+process);
    if ("clearLOVCache".equals(process)) {
        System.out.println("clearLOVCache");
        CodeLookupManager.getInstance().clearListOfValuesCache();
    } else if ("clearFieldCache".equals(process)) {
        System.out.println("clearFieldCache");
        //TODO:
        CodeLookupManager.getInstance().clearFieldCache();
    }

    Cache lovCache = CodeLookupManager.getInstance().getLovCache();
    Cache fieldCache = CodeLookupManager.getInstance().getFieldCache();


%>
<html>
<head>
    <title><%=ApplicationContext.getInstance().getProperty("applicationTitle", " ")%>: Cached LOV SQL</title>
    <script type="text/javascript">
        function clearLOVCache() {
//            alert("Clear LOV Cache...");
            window.location = "cachedLOVSql.jsp?process=clearLOVCache";
        }
        function clearFieldCache() {
//            alert("Clear Field Cache...");
            window.location = "cachedLOVSql.jsp?process=clearFieldCache";
        }

        function reloadPage() {
//            alert("Reload Page...");
            window.location = "cachedLOVSql.jsp";
        }
    </script>
</head>

<body>
<form name="cachedLOVSqlForm" method="post" action="cachedLOVSql.jsp">
    <button id="btnReload" onclick="reloadPage()">Reload</button>

    <table border="0" cellspacing="2" cellpadding="2" width="100%">
        <tr>
            <td>
                <fieldset>
                    <legend><b>Clear LOV Cache:</b></legend>
                    <table border="0" cellspacing="2" cellpadding="2" width="100%">
                        <tr>
                            <td>
                                <input type="button" id="clearLOVCacheButton" value="Clear LOV Cache" onclick="clearLOVCache();void(0);"></input>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </td>
        </tr>
        <tr>
            <td>LOVSQL Cache Size: <%=lovCache.getSize()%>
            </td>
        </tr>
        <tr>
            <td>
                <fieldset>
                    <legend><b>Cached LOV SQL:</b></legend>
                    <table border="0" cellspacing="2" cellpadding="2" width="100%">
                        <tr>
                            <th align="left" width="30%">Key</th>
                            <th align="left" width="70%">Value</th>
                        </tr>

                        <%
                            Iterator it = lovCache.keyIterator();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                ArrayList<LabelValueBean> lov = (ArrayList<LabelValueBean>) lovCache.get(key);
                                String contents = "";
                                for (LabelValueBean labelValueBean : lov) {
                                    contents += labelValueBean.toString() + " | ";
                                }
                        %>
                        <tr>
                            <td><%=key%>
                            </td>
                            <td><%=contents%>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </fieldset>
            </td>
        </tr>
        <tr>
            <td>
                <fieldset>
                    <legend><b>Clear Field Cache:</b></legend>
                    <table border="0" cellspacing="2" cellpadding="2" width="100%">
                        <tr>
                            <td>
                                <input type="button" id="clearFieldCacheButton" value="Clear Field Cache" onclick="clearFieldCache();void(0);"></input>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </td>
        </tr>
        <tr>
            <td>Field Cache Size: <%=fieldCache.getSize()%>
            </td>
        </tr>
        <tr>
            <td>
                <fieldset>
                    <legend><b>Cached Fields:</b></legend>
                    <table border="0" cellspacing="2" cellpadding="2" width="1200px">
                        <tr>
                            <th align="left">Key</th>
                            <th align="left">Value</th>
                        </tr>

                        <%
                            Iterator it2 = fieldCache.keyIterator();
                            while (it2.hasNext()) {
                                String key = (String) it2.next();
                                OasisFormField field = (OasisFormField) fieldCache.get(key);
                        %>
                        <tr>
                            <td><%=key%>
                            </td>
                            <td><%=field%>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </fieldset>
            </td>
        </tr>

    </table>

</form>
</body>
</html>
