package dti.oasis.test.mockito.argumentmatcher;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
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
public class ContainRecords implements ArgumentMatcher<RecordSet> {
    private final Logger l = LogUtils.getLogger(getClass());

    private RecordSet rs;

    public ContainRecords(RecordSet records) {
        this.rs = records;
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
    public boolean matches(RecordSet argument) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "matches", new Object[]{argument});
        }

        if (argument == null || argument.getSize() == 0) {
            return false;
        }

        List<Record> records = rs.getRecordList();
        List<Record> argumentRecords = argument.getRecordList();

        for (Record record : records) {
            // Match the current record in the expected records.
            List<String> fieldNames = record.getFieldNameList();

            boolean contains = true;

            for (Record argumentRecord : argumentRecords) {
                contains = true;

                for (String fieldName : fieldNames) {
                    if (!record.getStringValueDefaultEmpty(fieldName).equals(argumentRecord.getStringValueDefaultEmpty(fieldName))) {
                        // The current argument record doesn't contain the expected field.
                        contains = false;
                        break;
                    }
                }

                if (contains) {
                    // If current record matches the expected record, check the next expected record.
                    // Otherwise, check if the next record in argument matches the current expected record.
                    break;
                }
            }

            if (!contains) {
                // The argument doesn't contain the current expected record.
                return false;
            }
        }

        return true;
    }
}
