package dti.oasis.test.matcher.recordsetmatcher;

import dti.oasis.recordset.RecordSet;
import org.hamcrest.Description;

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
public class HasRecord extends RecordSetMatcher {

    @Override
    protected boolean matchesSafely(RecordSet item) {
        return (item != null && item.getSize() > 0);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has records.");
    }

    @Override
    protected void describeMismatchSafely(RecordSet item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("is null.");
        } else if (item.getSize() == 0) {
            mismatchDescription.appendText("has no records.");
        }
    }
}
