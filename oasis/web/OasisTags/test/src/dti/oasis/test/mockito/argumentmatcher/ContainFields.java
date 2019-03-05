package dti.oasis.test.mockito.argumentmatcher;

import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/6/2018
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
public class ContainFields implements ArgumentMatcher<Record> {
    private final Logger l = LogUtils.getLogger(getClass());

    private Record expectedFields;

    public ContainFields(Record expectedFields) {
        this.expectedFields = expectedFields;
    }

    /**
     * Informs if this matcher accepts the given argument.
     * <p>
     * The method should <b>never</b> assert if the argument doesn't match. It
     * should only return false.
     * <p>
     * See the example in the top level javadoc for {@link ArgumentMatcher}
     *
     * @param argument the argument
     * @return true if this matcher accepts the given argument.
     */
    @Override
    public boolean matches(Record argument) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "matches", new Object[]{argument});
        }

        if (argument == null) {
            return false;
        }

        List<String> fieldNames = expectedFields.getFieldNameList();

        for (String fieldName : fieldNames) {
            if (!expectedFields.getStringValueDefaultEmpty(fieldName).equals(argument.getStringValueDefaultEmpty(fieldName))) {
                return false;
            }
        }

        return true;
    }
}
