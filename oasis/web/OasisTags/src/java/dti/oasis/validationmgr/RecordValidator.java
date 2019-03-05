package dti.oasis.validationmgr;

import dti.oasis.recordset.Record;

/**
 * Interface of validator for Record.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 9, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord    the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    boolean validate(Record inputRecord);
}
