package dti.oasis.test.matcher.recordmatcher;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.hamcrest.Description;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/10/2018
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
public class HasField extends RecordMatcher {
    private final Logger l = LogUtils.getLogger(getClass());

    private final String[] fieldNames;

    public HasField(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    protected boolean matchesSafely(Record item) {
        if (item == null) {
            return false;
        }

        for (String fieldName : fieldNames) {
            if (!item.hasField(fieldName)) {
                return false;
            }
        }

        return  true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has field: ").appendValueList("", ", ", ".", fieldNames);
    }

    @Override
    protected void describeMismatchSafely(Record item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("is null");
        } else {
            mismatchDescription.appendText("has field: ").appendValueList("", ", ", ".", item.getFieldNameList());
        }
    }
}
