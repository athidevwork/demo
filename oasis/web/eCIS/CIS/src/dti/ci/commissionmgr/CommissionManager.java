package dti.ci.commissionmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 23, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * ---------------------------------------------------
 */
public interface CommissionManager {

    /**
     * method to load all commission rate Bracket for a given commRateSchedId
     *
     * @param inputRecord a record containing a commRateSchedId field
     * @return recordset
     */
    RecordSet loadAllCommissionBracket(Record inputRecord);
}
