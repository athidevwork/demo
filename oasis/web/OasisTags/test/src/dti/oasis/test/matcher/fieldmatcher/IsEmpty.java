package dti.oasis.test.matcher.fieldmatcher;

import dti.oasis.recordset.Field;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
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
public class IsEmpty extends FieldMatcher {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Subclasses should implement this. The item will already have been checked for
     * the specific type and will never be null.
     *
     * @param item
     */
    @Override
    protected boolean matchesSafely(Field item) {
        if (item == null) {
            return false;
        }

        String value = item.getStringValue();

        return (StringUtils.isBlank(value));
    }

    /**
     * Generates a description of the object.  The description may be part of a
     * a description of a larger object of which this is just a component, so it
     * should be worded appropriately.
     *
     * @param description The description to be built or appended to.
     */
    @Override
    public void describeTo(Description description) {

    }
}
