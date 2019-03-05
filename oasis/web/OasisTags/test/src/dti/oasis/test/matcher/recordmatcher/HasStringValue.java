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
public class HasStringValue extends HasFieldValue<String> {
    private final Logger l = LogUtils.getLogger(getClass());

    public HasStringValue(String fieldName, String fieldValue) {
        super(fieldName, fieldValue);
    }

    @Override
    protected String getActualFieldValue(Record record) {
        return record.getStringValue(getFieldName(), "");
    }
}
