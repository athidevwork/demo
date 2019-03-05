package dti.pm.riskmgr.affiliationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Affiliation information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 21, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
* 10/31/2017       lzhang      188425 Add validatePractPercForOOSENonInitTerms method
 * ---------------------------------------------------
 */
public interface AffiliationDAO {

    /**
     * To calculate dates for load Affiliation.
     * <p/>
     *
     * @param inputRecord
     * @return
     */
    Record calculateDateForAffiliation(Record inputRecord);

    /**
     * validate affiliation copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllAffiliation(Record inputRecord);

    /**
     * copy all affliation data to target risk
     *
     * @param inputRecord
     */
    void copyAllAffiliation(Record inputRecord);

    /**
     * load all affiliations
     *
     * @param inputRecord
     * @param processor
     * @return affliations recordset
     */
    RecordSet loadAllAffiliation(Record inputRecord, RecordLoadProcessor processor);

    /**
     * Save all Affiliation informations.
     * <p/>
     *
     * @param inputRecords intput records
     * @return the number of row updateds
     */
    int saveAllAffiliation(RecordSet inputRecords);

    /**
     * validate percent of practice for same time period cannot total over 100%.
     *
     * @param inputRecord
     */
    String validatePractPercForOOSENonInitTerms(Record inputRecord);
}
