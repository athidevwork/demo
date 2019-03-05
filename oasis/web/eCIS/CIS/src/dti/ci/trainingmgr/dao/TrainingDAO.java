package dti.ci.trainingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/5/2018
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
public interface TrainingDAO {

    /**
     * Get the list of the Institution Name
     *
     * @param inputRecord
     * @return List
     */
    public List getListOfInstitutionName(Record inputRecord);

    /**
     * Get the training list of an entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet getTrainingList(Record inputRecord);

    /**
     * Method to save Training information
     *
     * @param inputRecords
     * @return int
     */
    public int saveTrainingData(RecordSet inputRecords);

}
