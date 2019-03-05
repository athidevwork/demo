package dti.pm.componentmgr.experiencemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Experience Component Manager
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 29, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ExperienceComponentManager {

    /**
     * Initialize the search dates.
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForProcess(Record inputRecord);

    /**
     * To load all payment details for a particular set of search criteria
     *
     * @param inputRecord with user entered search criteria
     * @return RecordSet
     */
    RecordSet loadAllExperienceDetail(Record inputRecord);

    /**
     * Public method to validate search criteria
     *
     * @param inputRecord
     */
    public void validateSearchCriteria(Record inputRecord);
}
