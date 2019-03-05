package dti.ci.trainingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   3/6/2018
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
public interface TrainingManager {

    /**
     * Get Training List for an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet loadTrainingList(Record inputRecord);

    /**
     * Save Entity Training data.
     * @param inputRecords
     * @return int
     */
    public int saveTrainingData(RecordSet inputRecords);

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     * @param inputRecord
     * @return List
     */
    public List getInstitutionNameList(Record inputRecord);


    /**
     * Get the initial values when adding training
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddTraining(Record inputRecord);

}
