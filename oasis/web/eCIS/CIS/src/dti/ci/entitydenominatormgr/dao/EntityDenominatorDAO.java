package dti.ci.entitydenominatormgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/10/2018
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
public interface EntityDenominatorDAO {
    /**
     * Load all denominator of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    RecordSet loadAllEntityDenominator(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save all entity denominator.
     *
     * @param rs
     */
    void saveAllEntityDenominator(RecordSet rs);
}
