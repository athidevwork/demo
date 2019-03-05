package dti.oasis.test.matcher.recordsetmatcher;

import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.List;
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
public abstract class RecordSetMatcher extends TypeSafeMatcher<RecordSet> {
    private final Logger l = LogUtils.getLogger(getClass());

    public static Matcher<RecordSet> hasRecord() {
        return new HasRecord();
    }

    public static Matcher<RecordSet> hasField(String... fieldNames) {
        return new HasField(fieldNames);
    }

    public static Matcher<RecordSet> hasField(Collection<String> fieldNames) {
        return new HasField(fieldNames.toArray(new String[0]));
    }
}
