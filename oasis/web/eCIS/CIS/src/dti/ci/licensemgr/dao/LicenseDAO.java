package dti.ci.licensemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * DAO for License
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   2012-02-03
 *
 * @author parker
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface LicenseDAO {

    /**
     * load license information.
     * @param record
     * @return
     */
    public RecordSet loadLicense(Record inputRecord);
        
    /**
     * save license information.
     * @param record
     * @return
     */
    public int saveLicense(RecordSet rs);

    /**
     * Save License
     *
     * @param inputRecord
     */
    public Record saveLicense(Record inputRecord);

}
