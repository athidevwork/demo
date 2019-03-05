package dti.oasis.test.matcher.fieldmatcher;

import dti.oasis.recordset.Field;
import dti.oasis.util.LogUtils;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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
public abstract class FieldMatcher extends TypeSafeMatcher<Field> {
    private final Logger l = LogUtils.getLogger(getClass());

    public static Matcher<Field> isEmpty() {
        return new IsEmpty();
    }
}
