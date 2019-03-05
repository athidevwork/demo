package dti.oasis.test.mockito.argumentmatcher;

import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.mockito.ArgumentMatcher;

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
public class RecordSetArgumentMatchers {
    private final Logger l = LogUtils.getLogger(getClass());

    public static ArgumentMatcher<RecordSet> containRecords(RecordSet rs) {
        return new ContainRecords(rs);
    }
}
