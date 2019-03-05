package dti.ci.entityquickaddmgr.dao;

import dti.oasis.recordset.Record;

/**
 * The DAO component of Quick Add Person.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  08/15/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityQuickAddDAO {
    /**
     * Save Entity.
     * @param inputRecord
     * @return Record
     */
    Record saveEntity(Record inputRecord);

    /**
     * Save Entity Class.
     * @param inputRecord
     * @return Record
     */
    Record saveClass(Record inputRecord);

    /**
     * Save Entity Address.
     * @param inputRecord
     * @return Record
     */
    Record saveAddress(Record inputRecord);

    /**
     * Save Address Phone.
     * @param inputRecord
     * @return Record
     */
    Record savePhone(Record inputRecord);

    /**
     * Save Denominator.
     *
     * @param inputRecord
     * @return Record
     */
    Record saveDenominator(Record inputRecord);
}
