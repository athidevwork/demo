package dti.oasis.error;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.StringUtils;

import java.sql.SQLException;

/**
 * The purpose of this class is to provide consistant handling of Exceptions.
 * The handleException method will always return an AppException.
 * If the provided Exception is an AppException, it is simply returned.
 * Otherwise, the Exception is nested into a new AppException.
 * <p/>
 * The exception is not logged by this class.
 * Usage of this class should always use the Logger.throwing(...) method
 * to log a Level.FINER message that the exception is being thrown.
 * <p/>
 * All logging of this Exception as Level.SEVERE or Level.WARNING
 * should be done by a top level request error handler (ex. app-specific BaseAction, ErrorPage.jsp, ErrorPagePopup.jsp).
 * For example, all errors in a HTTP Web Request are handled by the Action class.
 * If the current page is redisplayed with the error message, the Action class logs the Exception.
 * Otherwise, the request is forwarded to the ErrorPage.jsp, which takes care of logging the Exeption.
 * In Web Services, the Service class takes care of logging the Exception before throwing an AppException to the client.
 * <p/>
 * The following is common usage of this class:
 * <code>
 * <br> try {
 * <br>&nbsp;&nbsp; ...
 * <br> } catch (Exception e) {
 * <br>&nbsp;&nbsp; AppException ae = ExceptionHelper.getInstance().handleException("Failed to ...", e);
 * <br>&nbsp;&nbsp; l.throwing(getClass().getName(), "methodName", ae);
 * <br>&nbsp;&nbsp; throw ae;
 * <br> }
 * </code>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 19, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ExceptionHelper {

    /**
     * The bean name of a ExceptionHelper extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "ExceptionHelper";
    public static final String GENERIC_DB_ERROR_MESSAGE_KEY = "core.generic.db.error";

    public synchronized static final ExceptionHelper getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (ExceptionHelper) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                c_instance = new ExceptionHelper();
            }
        }
        return c_instance;
    }

    /**
     * Handle the Exception by returning an AppException describing the handled Exception.
     * Use the AppException.UNEXPECTED_ERROR as the default message key.
     * If the given Exception is an AppException, the message key is NOT replaced
     * in case a more descriptive message key is already specified.
     * The given debug message is pushed onto the front of the AppException's debug message.
     *
     * @param debugMessage a debug message to further describe the context of the error
     * @param e the exception
     * @return an AppException describing the handled Exception.
     */
    public AppException handleException(String debugMessage, Exception e) {
        return handleException(AppException.UNEXPECTED_ERROR, debugMessage, e, false);
    }

    /**
     * Handle the Exception by returning an AppException describing the handled Exception.
     * If the given Exception is an AppException, the message key is replaced with the provide message key,
     * and the given debug message is pushed onto the front of the AppException's debug message.
     *
     * @param messageKey a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e the exception
     * @return an AppException describing the handled Exception.
     */
    public AppException handleException(String messageKey, String debugMessage, Exception e) {
        return handleException(messageKey, debugMessage, e, true);
    }

    public AppException handleException(String messageKey, String debugMessage, Exception e, boolean replaceMessageKey) {
        AppException ae = null;

        if (e instanceof AppException) {
            ae = (AppException) e;
            if (replaceMessageKey) {
                ae.setMessageKey(messageKey);
            }
            ae.pushDebugMessage(debugMessage);
        } else {
            ae = new AppException(messageKey, debugMessage, e);
        }
        return ae;
    }

    /**
     * Handle SQL exception.
     * If the SQL exception is a user-defined exception, returns a UserDefinedDBException. Otherwise, returns UnexpectedDBException.
     *
     * @param debugMessage
     * @param e
     * @return
     */
    public AppException handleSQLException(String debugMessage, SQLException e) {
        return handleSQLException(GENERIC_DB_ERROR_MESSAGE_KEY, debugMessage, e);
    }

    /**
     * Handle SQL exception.
     * If the SQL exception is a user-defined exception, returns a UserDefinedDBException. Otherwise, returns UnexpectedDBException.
     *
     * @param messageKey
     * @param debugMessage
     * @param e
     * @return
     */
    public AppException handleSQLException(String messageKey, String debugMessage, SQLException e) {
        AppException ae = null;

        String errorMessage = getErrorMessage(e);

        getMessageManager().addErrorMessage(messageKey, new Object[]{errorMessage});

        if (isUserDefinedSQLException(e)) {
            ae = new UserDefinedDBException(debugMessage, e);
        } else {
            ae = new UnexpectedDBException(debugMessage, e);
        }

        return ae;
    }

    /**
     * Add the protected getMessageManager so we can mock a message manager.
     *
     * @return
     */
    protected MessageManager getMessageManager() {
        return MessageManager.getInstance();
    }

    /**
     * Check if it's an user-defined exception by checking if the error message contains "ORA-20".
     *
     * @param e
     * @return
     */
    private boolean isUserDefinedSQLException(SQLException e) {
        return (e.getMessage().contains("ORA-20"));
    }

    /**
     * Get error message from SQL exception.
     * @param e
     * @return
     */
    private String getErrorMessage(SQLException e) {
        return StringUtils.formatDBErrorForHtml(e.getMessage());
    }

    private static ExceptionHelper c_instance;
}
