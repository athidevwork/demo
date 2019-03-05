package dti.oasis.util;

import dti.oasis.app.ConfigurationException;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.*;

/**
 * Logging Utility class
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2004
 * @author jbe
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 01/23/2007       wer     Added getLogger()
 * ---------------------------------------------------
 */

public class LogUtils {

    /**
     * Get a Logger for a Java Class.
     *
     * @param cls
     */
    public static Logger getLogger(Class cls) {
        return getLogger(cls.getName());
    }

    /**
     * Get a named Logger.
     *
     * @param name logger name
     */
    public static Logger getLogger(String name) {
        LogManager manager = LogManager.getLogManager();
        Logger result = manager.getLogger(name);
        if (result == null) {
            result = new dti.oasis.log.Logger(name);
            manager.addLogger(result);
            c_loggers.put(name, result);
            result = manager.getLogger(name);
        }
        return result;
    }

    /**
     * Gets a Logger and logs the entry of a method
     * @param cls Class
     * @param method Method
     * @param parms Array of parameters
     * @return Logger
     */
    public static Logger enterLog(Class cls, String method, Object[] parms) {
        Logger l = getLogger(cls.getName());
        l.entering(cls.getName(), method, parms);
        return l;
    }

    /**
     * Gets a Logger and logs the entry of a method
     * @param cls Class
     * @param method Method
     * @param parm Single parameter
     * @return Logger
     */
    public static Logger enterLog(Class cls, String method, Object parm) {
        Logger l = getLogger(cls.getName());
        l.entering(cls.getName(), method, parm);
        return l;
    }

    /**
     * Gets a Logger and logs the entry of a method
     * @param cls Class
     * @param method Method
     * @return Logger
     */
    public static Logger enterLog(Class cls, String method) {
        Logger l = getLogger(cls.getName());
        l.entering(cls.getName(), method);
        return l;
    }

    /**
     * Initialize the Logger default formatter to the one configured with the property
     */
    public static void setFormatter(String formatterName) {
        l.entering(LogUtils.class.getName(), "setFormatter");

        Formatter formatter = null;
        if (!StringUtils.isBlank(formatterName)) {
            try {
                formatter = (Formatter) Class.forName(formatterName).newInstance();
            } catch (Exception e) {
                ConfigurationException ce = new ConfigurationException("Failed to locate the Logging Formatter class named <" + formatterName + ">", e);
                l.throwing(LogUtils.class.getName(), "setFormatter", ce);
                throw ce;
            }

            LogManager lm = LogManager.getLogManager();
            Logger logger = lm.getLogger("");
            Handler[] h = logger.getHandlers();
            for (int i = 0; i < h.length; i++) {
                Handler handler = h[i];
                handler.setFormatter(formatter);
            }
        }
        l.exiting(LogUtils.class.getName(), "setFormatter");
    }

    public static void setHandlerLevel(String handlerName, Level level) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(LogUtils.class.getName(), "setHandlerLevel", new Object[]{handlerName, level});
        }

        Handler handler = getHandler(handlerName);
        handler.setLevel(level);

        l.exiting(LogUtils.class.getName(), "setHandlerLevel");
    }

    public static void removeHandler(String handlerName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(LogUtils.class.getName(), "removeHandler", new Object[]{handlerName});
        }

        LogManager lm = LogManager.getLogManager();
        Logger logger = lm.getLogger("");
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            if (handler.toString().startsWith(handlerName)) {
                logger.removeHandler(handler);
                break;
            }
        }

        l.exiting(LogUtils.class.getName(), "removeHandler");
    }

    /**
     * Format the message string from a log record.
     *
     * @param loggerName Name of the Logger
     */
    public static void removeHandlers(String loggerName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(LogUtils.class.getName(), "removeHandlers", new Object[]{loggerName});
        }
        l.logp(Level.INFO, Logger.class.getName(), "removeHandlers", "Removing Handlers for Logger: "+loggerName);
        LogManager lm = LogManager.getLogManager();
        Logger logger = lm.getLogger(loggerName);
        Handler[] handlers = logger.getHandlers();
        l.logp(Level.INFO, Logger.class.getName(), "removeHandlers", "Number of Handlers: "+handlers.length);
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            try {
                handler.flush();
                handler.close();
            } catch (Exception ex) {
                //Do nothing
            }
            logger.removeHandler(handler);
            l.logp(Level.INFO, Logger.class.getName(), "removeHandlers", "Handler["+i+"] for "+loggerName+" IS Removed");
        }

        l.exiting(LogUtils.class.getName(), "removeHandlers");
    }

    /**
     * Format the message string from a log record.
     *
     * @param name New Logger Name
     * @param formatter Formatter class for the new Logger
     * @param pattern File Pattern (See java.util.logging.FileHandler)
     * @param limit File Limit (See java.util.logging.FileHandler)
     * @param count File Count (See java.util.logging.FileHandler)
     * @param append Append or overwrite log
     * @param level Lorrer Level
     * @param logToDefaultLog Enable / Disable logging to default Java Logger
     * @return a new Logger
     */
    public static Logger addLogger(String name, Formatter formatter, String pattern, int limit, int count, boolean append, Level level, boolean logToDefaultLog){
        //TODO: Reset Formatter
        if (l.isLoggable(Level.FINER)) {
            l.entering(LogUtils.class.getName(), "addLogger", new Object[]{name, formatter,pattern,append,level});
        }
        Logger logger = LogUtils.getLogger(name);
        try {
            // Create an appending file handler
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Logger: " + logger.getClass().getName());
            Handler[] stpDaoLoggerHandlers = logger.getHandlers();
            if (l.isLoggable(Level.FINER))
                l.logp(Level.FINER, Logger.class.getName(), "addLogger", "# of stpDaoLoggerHandlers: " + stpDaoLoggerHandlers.length);
            for (int i = 0; i < stpDaoLoggerHandlers.length; i++) {
                Handler handler = stpDaoLoggerHandlers[i];
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Handler: " + handler.getClass().getName());
            }
            if (stpDaoLoggerHandlers.length < 1) {
                FileHandler fileHandler = new FileHandler(pattern, limit, count, append);
                fileHandler.setFormatter(formatter);
                fileHandler.setLevel(level);
                // Add to the desired logger
                logger.addHandler(fileHandler);
                if (l.isLoggable(Level.FINER))
                    l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Added handler to stpDaoLoggerHandlers: " + logger.getHandlers().length);
            } else {
                FileHandler handler = (FileHandler) stpDaoLoggerHandlers[0];
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Existing Handler's Level: " + handler.getLevel());
                    l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Existing Handler's Class: " + handler.getFormatter().getClass().getName());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (l.isLoggable(Level.FINER))
            l.logp(Level.FINER, Logger.class.getName(), "addLogger", "Exiting -- # of stpDaoLoggerHandlers2: " + logger.getHandlers().length);

        logger.setLevel(level);
        logger.setUseParentHandlers(logToDefaultLog);

        l.exiting(LogUtils.class.getName(), "addLogger");

        return logger;
    }

    private static Handler getHandler(String handlerName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(LogUtils.class.getName(), "getHandler", new Object[]{handlerName});
        }

        Handler resultHandler = null;

        LogManager lm = LogManager.getLogManager();
        Logger logger = lm.getLogger("");
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            if (handler.toString().startsWith(handlerName)) {
                resultHandler = handler;
                break;
            }
        }

        if (resultHandler == null) {
            l.logp(Level.WARNING, LogUtils.class.getName(), "getHandler", "Failed to find the handler <" + handlerName + ">");
        }

        l.exiting(LogUtils.class.getName(), "getHandler");
        return resultHandler;
    }

    public static void setupForRequest(String userId) {
        c_userId.set(userId);
    }

    public static void cleanupFromRequest() {
        c_userId.remove();
        c_page.remove();
    }

    public static String getUserId() {
        return c_userId.get();
    }

    public static String getPage() {
        return c_page.get();
    }

    public static void setPage(String page) {
        c_page.set(page);
    }

    private static Map<String, Logger> c_loggers = new Hashtable<String, Logger>();
    private static ThreadLocal<String> c_userId = new ThreadLocal<String>();
    private static ThreadLocal<String> c_page = new ThreadLocal<String>();
    private static final Logger l = LogUtils.getLogger(LogUtils.class);
}
