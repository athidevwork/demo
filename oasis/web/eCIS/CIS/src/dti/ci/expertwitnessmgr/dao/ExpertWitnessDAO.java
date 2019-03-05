package dti.ci.expertwitnessmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The DB Object for Expert Witness DAO.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 8, 2012
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
public interface ExpertWitnessDAO {
        /**
     * Get Expert witness count of an entity.
     * @param inputRecord
     * @return
     */
    public int getExpertWitnessCountOfEntity(Record inputRecord);

    /**
     * Get person info of Expert Witness.
     * @param inputRecord
     * @return
     */
    public Record getPersonInfo(Record inputRecord);

    /**
     * Load Expert Witness addresses.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAddress(Record inputRecord);

    /**
     * Load Expert Witness addresses.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPhone(Record inputRecord);

    /**
     * Load Education info of Expert Witness.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllEducation(Record inputRecord);

    /**
     * Load all classification of an expert witness.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllClassification(Record inputRecord);

    /**
     * Load all relationship of an expert witness.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRelationship(Record inputRecord);

    /**
     * Load all claim of an expert witness.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllClaim(Record inputRecord);

        /**
     * Change expert witness status.
     * @param inputRecord
     */
    public void changeStatus(Record inputRecord);
}
