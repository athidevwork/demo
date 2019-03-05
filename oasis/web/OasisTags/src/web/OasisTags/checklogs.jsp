<%@ page import="dti.oasis.http.Module,
                 dti.oasis.log.LogInitializer,
                 dti.oasis.util.LogUtils,
                 dti.oasis.util.StringUtils"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.logging.Handler" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.LogManager" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.data.StoredProcedureDAOHelper" %>
<%@ page import="dti.oasis.data.StoredProcedureDAO" %>
<%@ page import="dti.oasis.log.StoredProcedureLogFormatter" %>
<%@ page import="dti.oasis.log.StoredProcedureLogLevel" %>
<%@ page language="java"%>
<%--
  Description: Log Manager utility JSP
  Author: jbe
  Date: Feb 8, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------


  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    String[] levels = {"OFF","SEVERE","WARNING","INFO","CONFIG","FINE","FINER","FINEST","ALL"};
    int lsz = levels.length;
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Pragma", "no-cache");
    String corePath = Module.getCorePath(request);

%>
<%!
    private static void initializeStoredProcedureLogger() {
        StoredProcedureDAO.setStpDaoLogger(LogUtils.addLogger(StoredProcedureDAOHelper.STORED_PROCEDURE_LOGGER_NAME, new StoredProcedureLogFormatter(),
                StoredProcedureDAOHelper.getStoredProcedureLogFilePattern(), StoredProcedureDAOHelper.getStoredProcedureLogFileLimit(), StoredProcedureDAOHelper.getStoredProcedureLogFileCount(),
                true, StoredProcedureLogLevel.STORED_PROCEDURE, StoredProcedureDAOHelper.isLoggedToDefaultLogEnabled()));
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <link rel="shortcut icon" href="<%=corePath%>/images/logo-ftr.gif" />
    <link href="<%=corePath%>/css/oasisnew.css" rel="stylesheet" type="text/css"/>
    <h2>Logger Details</h2>
    <script type="text/javascript">
        function reloadPage() {
//            alert("Reload Page...");
            window.location = "checklogs.jsp";
        }
    </script>
</head>
<body>
<a class="content" href="#" onclick="reloadPage()">Reload Page</a>
</br>
<a class="content" href="checklogs.jsp?type=reload">Reload Logging Configuration File</a>
<%
    if(StoredProcedureDAOHelper.isStoredProcedureLoggingEnabled()){
%>
</br>
<a class="content" href="checklogs.jsp?type=reloadStoredProcedure">Reload Stored Procedure Logging Configuration</a>
<%
    }
%>
<table border="1" width="80%"><tr><th><b>Logger/Handler</b></th>
<th colspan="<%=lsz%>"><b>Levels</b></th></tr>
<%
    LogManager lm = LogManager.getLogManager();
    String type = request.getParameter("type");
    Logger l = null;
    if(!StringUtils.isBlank(type)) {
        if(type.equals("reload")) {
            lm.readConfiguration();
            LogInitializer.getInstance().initialize();
            initializeStoredProcedureLogger();
        }
        else if (type.equals("useWeblogicConsoleFormatter"))
            LogUtils.setFormatter("weblogic.logging.ConsoleFormatter");
        else if (type.equals("useWeblogicLogFileFormatter"))
            LogUtils.setFormatter("weblogic.logging.LogFileFormatter");
        else if (type.equals("useSingleLineLogFormatter"))
            LogUtils.setFormatter("dti.oasis.log.SingleLineLogFormatter");
        else if (type.equals("reloadStoredProcedure"))
            initializeStoredProcedureLogger();
/*
        else if (type.equals("useWeblogicConsoleFormatter") || type.equals("useWeblogicLogFileFormatter") || type.equals("useSingleLineLogFormatter")) {
            Enumeration<String> loggers = lm.getLoggerNames();
            Formatter formatter = type.equals("useWeblogicConsoleFormatter") ? new ConsoleFormatter() :
                                        type.equals("useWeblogicLogFileFormatter") ? new LogFileFormatter() : new SingleLineLogFormatter();
            while (loggers.hasMoreElements()) {
                String loggerName = loggers.nextElement();
                Handler[] h = lm.getLogger(loggerName).getParent().getHandlers();
                for (int i = 0; i < h.length; i++) {
                    Handler handler = h[i];
                    handler.setFormatter(formatter);
                }
                break;
            }
        }
*/
        else {
            String cls = request.getParameter("class");
            String lvl = request.getParameter("level");
            l = lm.getLogger(cls);
            l.setLevel(Level.parse(lvl));
            Handler[] h = l.getHandlers();
            int hsz = h.length;
            for(int i=0;i<hsz;i++) {
                h[i].setLevel(Level.parse(lvl));
            }
        }
    }
    l = lm.getLogger("");
    String dftLevel = l.getLevel().toString();
    Enumeration e = lm.getLoggerNames();

    HashMap<Integer,String> map = new HashMap<Integer,String>();
    map.put(Level.SEVERE.intValue(), "B");
    map.put(Level.WARNING.intValue(), "C");
    map.put(Level.INFO.intValue(), "D");
    map.put(Level.CONFIG.intValue(), "E");
    map.put(Level.FINE.intValue(), "F");
    map.put(Level.FINER.intValue(), "G");
    map.put(Level.FINEST.intValue(), "H");
    map.put(Level.ALL.intValue(), "I");
    map.put(0, "J");
    map.put(Level.OFF.intValue(), "Z");

    List<String> loggerNames = new ArrayList<String>();
    while(e.hasMoreElements())
    {
        String logger = (String) e.nextElement();
        l = LogUtils.getLogger(logger);
        if (l == null) {
            continue;
        }
        if ( logger == "")
            logger = "root";
        int level = (l.getLevel() == null ? 0 : l.getLevel().intValue());
        String sortPrefix = map.get(level);
        if (logger == "com.oracle.wls") sortPrefix = "X";
        if (logger == "root") sortPrefix = "A";
        loggerNames.add(sortPrefix + "," + logger);
    }
    Collections.sort(loggerNames);
    Iterator<String> iter = loggerNames.iterator();
    while(iter.hasNext()) {
        String nm = (String) iter.next();
        nm = nm.split(",")[1];
        if ( nm.equals("root") )
            nm = "";
        l = LogUtils.getLogger(nm);
        if (l == null) {
            System.out.println("Can't get the logger: " + nm);
            continue;
        }
        {

%>
<tr><td><%=(nm=="")?"<b>DEFAULT LOGGER</b>":nm%></td>
<%
            String level=null;
            for(int i=0;i<lsz;i++) {
                if((l.getLevel()==null && levels[i].equals(dftLevel)) ||
                  (l.getLevel() != null && l.getLevel().toString().equals(levels[i])))
                    level = levels[i];
                else
                    level = "<a class='content' href='checklogs.jsp?type=class&class="+nm+"&level="+levels[i]+"'>"+
                            levels[i] +"</a>";
%>
<td><%=level%></td>
<%
            }
%>
</tr>
<%
            Handler[] h = l.getHandlers();
            int sz = h.length;
            for(int i=0;i<sz;i++) {   %>
<tr><td><i><b>Handler:</b> <%=h[i]%></i></td>
<td><%=h[i].getLevel()%></td>
</tr>
            <%
            }
        }
    }
%>

</table>
<a class="content" href="checklogs.jsp?type=useSingleLineLogFormatter">Use SingleLineFormatter</a>
</br>
<a class="content" href="checklogs.jsp?type=useWeblogicConsoleFormatter">Use Weblogic ConsoleFormatter</a>
</br>
<a class="content" href="checklogs.jsp?type=useWeblogicLogFileFormatter">Use Weblogic LogFileFormatter</a>
</body>
</html>