package dti.ci.educationmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * The business component of Education.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 8, 2012
 *
 * @author Yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/14/2016        dpang      176370: set initial values when adding education.
 * ---------------------------------------------------
 */
public interface EducationManager {
    /**
     * Get Education List for an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet loadEducationList(Record inputRecord);

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     * @param inputRecord
     * @return
     */
    public Record getEntityInfo(Record inputRecord);

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     * @param inputRecord
     * @return List
     */
    public List getInstitutionNameList(Record inputRecord);

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     * @param inputRecords
     * @return int
     */
    public int saveEducationData(RecordSet inputRecords);

    /**
     * Save education
     *
     * @param inputRecord
     */
    public Record saveEducation(Record inputRecord);

    /**
     * Get the initial values when adding education
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddEducation(Record inputRecord);

}
