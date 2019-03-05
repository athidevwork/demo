package dti.oasis.test.matcher.recordmatcher;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/9/2018
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
public abstract class RecordMatcher extends TypeSafeMatcher<Record> {
    private final Logger l = LogUtils.getLogger(getClass());

    public static Matcher<Record> hasField(String... fieldNames) {
        return new HasField(fieldNames);
    }

    public static Matcher<Record> hasField(Collection<String> fieldNames) {
        return new HasField(fieldNames.toArray(new String[0]));
    }
}
