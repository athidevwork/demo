package dti.ci.commissionmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 24, 2007
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
public interface CommissionDAO {

   /**
     * method to load all commission rate Bracket for a given commRateSchedId
     *
     * @param inputRecord a record containing a commRateSchedId field
     * @return recordset
     */
    RecordSet loadAllCommissionBracket(Record inputRecord);
}
