package dti.ci.educationmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

import java.util.List;

/**
 * The DB Object for Education DAO.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 8, 2012
 *
 * @author yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EducationDAO {
    /**
     * Get the education list of an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet getEducationList(Record inputRecord);

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     * @param inputRecord
     * @return
     */
    public Record getEntityInfo(Record inputRecord);

    /**
     * Get the list of the Institution Name
     *
     * @param inputRecord
     * @return List
     */
    public List getListOfInstitutionName(Record inputRecord);

    /**
    * Method to save Education information
    *
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

}
