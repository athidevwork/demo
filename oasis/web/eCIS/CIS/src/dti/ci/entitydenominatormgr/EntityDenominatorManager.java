package dti.ci.entitydenominatormgr;

import dti.oasis.recordset.Record;
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
public interface EntityDenominatorManager {
    /**
     * Load all denominator of an entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllEntityDenominator(Record inputRecord);

    /**
     * Save all entity denominator.
     *
     * @param rs
     */
    void saveAllEntityDenominator(RecordSet rs);
}
