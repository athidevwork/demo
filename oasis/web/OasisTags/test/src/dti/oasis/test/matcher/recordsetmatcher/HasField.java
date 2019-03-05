package dti.oasis.test.matcher.recordsetmatcher;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.hamcrest.Description;

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
public class HasField extends RecordSetMatcher {
    private final Logger l = LogUtils.getLogger(getClass());

    private String[] fieldNames;

    public HasField(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    protected boolean matchesSafely(RecordSet item) {
        if (item == null) {
            return false;
        }

        List<String> rsFieldNames = item.getFieldNameList();

        for(String fieldName: fieldNames) {
            if (!rsFieldNames.contains(fieldName)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has field: ").appendValueList("", ", ", ".", fieldNames);
    }

    @Override
    protected void describeMismatchSafely(RecordSet item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("is null");
        } else {
            mismatchDescription.appendText("has field: ").appendValueList("", ", ", ".", item.getFieldNameList());
        }
    }
}
