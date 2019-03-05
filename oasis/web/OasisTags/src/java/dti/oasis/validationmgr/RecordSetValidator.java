package dti.oasis.validationmgr;

import dti.oasis.recordset.RecordSet;

/**
 * Interface of validator for RecordSet.
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

public interface RecordSetValidator {

    /**
     * Validate the given record set.
     *
     * @param inputRecords    a data RecordSet
     * @return true if the RecordSet is valid; otherwise false.
     */
    boolean validate(RecordSet inputRecords);
}
