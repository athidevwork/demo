package dti.ci.licensemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;


/**
 * The business component of License information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   2012-02-17
 *
 * @author parker
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface LicenseManager {
    
    /**
     * load license information.
     * @param record
     * @return
     */
    public RecordSet loadLicense(Record record);
    
    /**
     * save license information.
     * @param record
     * @return
     */
    public int saveLicense(RecordSet inputRecords);

    /**
     * Save License
     *
     * @param inputRecord
     */
    public Record saveLicense(Record inputRecord);

    /**
     * method to get the initial value when adding license
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddLicense(Record inputRecord);

}
