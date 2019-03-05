package dti.ci.core.error;

import dti.oasis.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

/**
 * Common class to handle exception.
 * <p/>
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2008
 *
 * @author
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
*/
public class ExpMsgConvertor {
    /**
     * Private constructor to block initializing this class.
     */
    private ExpMsgConvertor() {
    }

    /**
     * Generate first line of the exception detail message.
     *
     * @param ex
     * @return
     */
    public static String getExceptionDetail(Throwable ex) {
        String errorDetail = "";
        ByteArrayOutputStream baos = null;
        PrintStream ps = null;
        try {
            baos = new ByteArrayOutputStream();
            ps = new PrintStream(baos);
            ex.printStackTrace(ps);
            baos.flush();
            byte[] errorBytes = baos.toByteArray();
            errorDetail = new String(errorBytes);
            errorDetail = errorDetail.substring(0, errorDetail.indexOf("\n"));
            return errorDetail.replaceAll("\r", "");
        } catch (IOException ioe) {
            return "";
        } finally {
            try {
                if (baos != null)
                    baos.close();
                if (ps != null)
                    ps.close();
                baos = null;
                ps = null;
            } catch (IOException e) {
            }
        }
    }
    /**
     * Remove the prefix of the error message.
     *
     * @param t
     * @return
     */
    public static String getBeautifyExpDetailForJS(Throwable t) {
        String tempExp = getExcepDetailForJS(t);
        if (tempExp.indexOf(":") > 0) {
            tempExp = tempExp.substring(tempExp.lastIndexOf(":"));
        }
        return tempExp;
    }
    /**
     * Generating the first line of the exception detail.
     * It is used to display as the javascript message.
     *
     * @param t
     * @return
     */
    public static String getExcepDetailForJS (Throwable t) {
        return convertJSMsg(getExceptionDetail(t));
    }

    /**
     * Remove the prefix of the error message.
     *
     * @param t
     * @return
     */
    public static String getBeautifyExpDetailForHtml(Throwable t) {
        String tempExp = getExceptionDetailForHtml(t);
        if (tempExp.indexOf(":") > 0) {
            tempExp = tempExp.substring(tempExp.lastIndexOf(":"));
        }
        return tempExp;
    }
    public static String getExceptionDetailForHtml(Throwable t) {
        return convertHtmlMsg(getExceptionDetail(t));
    }
    
    public static String convertHtmlMsg(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        String temp = input;
        temp = StringUtils.replace(temp, "\"", "&#34;");
        temp = StringUtils.replace(temp, ",", "&#44;");
        temp = StringUtils.replace(temp, "'", "&#39;");
        temp = StringUtils.replace(temp, "<", "&#60;");
        temp = StringUtils.replace(temp, ">", "&#62;");
        return temp;
    }

    public static String convertJSMsg(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        String temp = input;
        temp = StringUtils.replace(temp, "\"", "\\\"");
        temp = StringUtils.replace(temp, "\n", "\\n");
        temp = StringUtils.replace(temp, "\r", "\\n");
        temp = StringUtils.replace(temp, "'", "\\'");
        temp = StringUtils.replace(temp, ",", ";");
        return temp;
    }

    public static String trimSQLException(SQLException se) {
        String msg = se.getMessage();
        if (StringUtils.isBlank(msg)) {
            msg = getExceptionDetail(se);
        }
        return trimSQLErrorMsg(msg);
    }

    /** method returns the trimmed version of the error message
     *  to match the existing behavior. return the first delphi-defined message if possible.
     *
     **/

    public static String trimSQLErrorMsg(String err) {
        return trimSQLErrorMsg(err, false);
    }
    /** method modified to return error message :
     *
     *  if there are delphi defined messages:
     *    combine All user messages  if specified
     *    return the first non-empty message (this is the behavior before this change)
     *  otherwise:
     *    return the last oracle internal message.
     *
     * @param err a string containing possibly zero or more messages starting with ORA-ddddd:
     *        delphi defined messages start with ORA-2dddd:
     * @return
     */
    public static String trimSQLErrorMsg(String err, boolean combineAllUserMessages) {
        if (StringUtils.isBlank(err)) {
            return "";
        }

        String trimmedMessage ="";
        boolean hasDelphiDefinedMessage = err.indexOf("ORA-2")>-1; // delphi-defined messages contains ORA-2dddd:

        String[] errors = err.split("ORA-");
        if (hasDelphiDefinedMessage) {
            for (int i = errors.length-1; i >= 0; i--) {
                if (errors[i].startsWith("2")) {
                    String message = errors[i].substring(6);
                    if (message.length() > 0) {
                        if (combineAllUserMessages) {
                            trimmedMessage = message+ trimmedMessage;
                        } else {
                            trimmedMessage = message;
                        }
                    }
                }
            }
        } else {
            trimmedMessage = errors[errors.length - 1].substring(6);  // get the last oracle internal message
        }
        return convertHtmlMsg(trimmedMessage.toString());
    }
}
