package dti.oasis.messagemgr;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * An interface that handles administration of message manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 12, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       fcb         1)getAllMessagesList, and addAllMessagesFromList added. 
 * 04/09/2008       wer         refactord logic to initialize the resource bundle for a Servlet into initResourceBundleForServletContext() to make it reusable.
 * ---------------------------------------------------
 */

public interface MessageManagerAdmin {
    /**
     * Store the message associated with the given message category and message key
     * in the Message Manager for the scope of this request.
     *
     * @param messageCategory, category of the message.
     * @param messageKey, key to the message.
     * @param message,  message.
     * @param messageValue,  default value for the meesage
     */
    void defineMessageForRequest(MessageCategory messageCategory, String messageKey, String message, String messageValue);

    /**
     * Returns a list of all messages in the Message Manager.
     * @return
     */
    List getAllMessagesList();

    /**
     * Sets into the Message Manager messages from an input list.
     * @param messageList
     */
    void setAllMessagesFromList(List messageList);

    /**
     * Initialize the ResourceBundel for the ServletContext.
     * @param servletContext
     */
    void initResourceBundleForServletContext(ServletContext servletContext);
}
