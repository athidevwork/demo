package dti.oasis.test.matcher.messagematcher;

import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import org.hamcrest.Description;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/14/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class HasMessage extends MessageMatcher {
    private final Logger l = LogUtils.getLogger(getClass());

    private MessageCategory category;
    private String[] messageKeys;

    public HasMessage(MessageCategory category, String... messageKeys) {
        this.category = category;
        this.messageKeys = messageKeys;
    }

    @Override
    protected boolean matchesSafely(MessageManager item) {
        if (item == null) {
            return false;
        }

        if (!item.hasMessages()) {
            return false;
        }

        for (String messageKey: messageKeys) {
            boolean hasMessage = false;

            Iterator messages = item.getMessages();

            while (messages.hasNext()) {
                Message message = (Message) messages.next();

                if (message.getMessageCategory().equals(category) && message.getMessageKey().equals(messageKey)) {
                    hasMessage = true;
                    break;
                }
            }

            if (!hasMessage) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has " + category.getCategory() + " message: ").appendValueList("", ",", ".", messageKeys);
    }

    @Override
    protected void describeMismatchSafely(MessageManager item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("is null.");
        } else {
            mismatchDescription.appendText("not has " + category.getCategory() + " message: ").appendValueList("", ",", ".", messageKeys);
        }
    }
}
