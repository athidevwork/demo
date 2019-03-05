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
public abstract class HasFieldValue<T> extends RecordMatcher {
    private final Logger l = LogUtils.getLogger(getClass());

    private final String fieldName;
    private final T fieldValue;

    public HasFieldValue(String fieldName, T fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    protected boolean matchesSafely(Record item) {
        if (item == null) {
            return false;
        }

        if (!item.hasField(fieldName)) {
            return false;
        }

        return fieldValue.equals(getActualFieldValue(item));
    }

    protected abstract T getActualFieldValue(Record record);

    @Override
    public void describeTo(Description description) {

    }

    protected String getFieldName() {
        return fieldName;
    }

    protected T getFieldValue() {
        return fieldValue;
    }
}
