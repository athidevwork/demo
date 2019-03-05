package dti.oasis.test.matcher.messagematcher;

import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
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
public abstract class MessageMatcher extends TypeSafeMatcher<MessageManager> {
    private final Logger l = LogUtils.getLogger(getClass());

    public static Matcher<MessageManager> hasMessage(MessageCategory category, String... keys) {
        return new HasMessage(category, keys);
    }

    public static Matcher<MessageManager> hasMessage(MessageCategory category, Collection<String> keys) {
        return new HasMessage(category, keys.toArray(new String[0]));
    }
}
