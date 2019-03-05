package dti.oasis.restful;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date: 5/27/2015
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class ResourceHelper {

    /**
     * Handle the Exception for the RESTful resources
     *
     * @param debugMessage
     * @param e
     * @param serviceResponse
     */
    public static void handleError(String debugMessage, Exception e, ServiceResponse serviceResponse) {
        Logger l = LogUtils.enterLog(ResourceHelper.class, "handleError", new Object[]{debugMessage, e, serviceResponse});

        l.logp(Level.SEVERE, ResourceHelper.class.getName(), "handleError", "caught exception: ", e);

        serviceResponse.setStatus(ServiceResponse.STATUS_FAILED);
        List<Message> messageList = serviceResponse.getMessageList();

        if (e instanceof ValidationException) {
            Iterator<Message> messageIterator = MessageManager.getInstance().getMessages();
            while (messageIterator.hasNext()) {
                Message message = messageIterator.next();
                message.setMessageCategory(MessageCategory.WARNING);
                messageList.add(message);
            }
        }
        else {
            String messageKey = AppException.UNEXPECTED_ERROR;
            if (e instanceof AppException && !StringUtils.isBlank(((AppException) e).getMessageKey())) {
                messageKey = ((AppException) e).getMessageKey();
            }

            AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
            Message message = new Message(MessageCategory.ERROR, ae.getMessageKey(), ae.getMessage(), null);
            messageList.add(message);
        }

        l.exiting(ResourceHelper.class.getName(), "handleError");
    }
}
