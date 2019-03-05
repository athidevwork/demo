package dti.oasis.log;

import dti.oasis.util.LogUtils;
import weblogic.logging.LogEntryInitializer;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by IntelliJ IDEA.
 * User: wreeder
 * Date: Apr 5, 2010
 * Time: 2:04:09 PM
 * To change this template use File | Settings | File Templates.
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/20/2012       fcb         Issue 136956: processMessage - additional logic to format the message based on the
 *                              UserSessionManager or on the format of the input.
 * 08/03/2016       huixu       Issue#169625 Add check to all of the Logger.log* methods to return immediately if the log level is not loggable.
 * ---------------------------------------------------
 */
public class Logger extends java.util.logging.Logger {

    /**
     * Extend the java.util.logging.Logger to keep track of additional information
     */
    public Logger(String name) {
        super(name, null);
    }

    @Override
    public void log(LogRecord record) {
        super.log(record);
    }

    @Override
    public void log(Level level, String msg) {
        if (!isLoggable(level)) {
            return;
        }
        super.log(level, processMessage(msg));
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        if (!isLoggable(level)) {
            return;
        }
        super.log(level, processMessage(msg), param1);
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        if (!isLoggable(level)) {
            return;
        }
        super.log(level, processMessage(msg), params);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        if (!isLoggable(level)) {
            return;
        }
        super.log(level, processMessage(msg), thrown);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        if (!isLoggable(level)) {
            return;
        }
        super.logp(level, sourceClass, sourceMethod, processMessage(msg));
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        if (!isLoggable(level)) {
            return;
        }
        super.logp(level, sourceClass, sourceMethod, processMessage(msg), param1);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        if (!isLoggable(level)) {
            return;
        }
        super.logp(level, sourceClass, sourceMethod, processMessage(msg), params);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        if (!isLoggable(level)) {
            return;
        }
        super.logp(level, sourceClass, sourceMethod, processMessage(msg), thrown);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        if (!isLoggable(level)) {
            return;
        }
        super.logrb(level, sourceClass, sourceMethod, bundleName, processMessage(msg));
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        if (!isLoggable(level)) {
            return;
        }
        super.logrb(level, sourceClass, sourceMethod, bundleName, processMessage(msg), param1);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        if (!isLoggable(level)) {
            return;
        }
        super.logrb(level, sourceClass, sourceMethod, bundleName, processMessage(msg), params);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        if (!isLoggable(level)) {
            return;
        }
        super.logrb(level, sourceClass, sourceMethod, bundleName, processMessage(msg), thrown);
    }

    public String processMessage(String msg) {
        String userId = LogUtils.getUserId();
        StringBuffer buf = new StringBuffer(msg.length() + 25);

        if (userId != null) {
            buf.append("<").append(userId).append("> ");
        }
        else if (!msg.trim().startsWith("<")) {
            buf.append("<").append(LogEntryInitializer.getCurrentUserId()).append("> ");
        }

        buf.append("Thread_Id<").append(Thread.currentThread().getId()).append("> ");

        buf.append(msg);

        return buf.toString();
    }


}
