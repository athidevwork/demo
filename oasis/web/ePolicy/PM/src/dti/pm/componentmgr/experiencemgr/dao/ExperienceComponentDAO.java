package dti.pm.componentmgr.experiencemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * An interface that provides DAO operation for experience component.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 24, 2009
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



public interface ExperienceComponentDAO {

    /**
     * To process and load all failed policies while processing experience comp
     *
     * @param inputRecord with user entered search criteria
     * @return RecordSet
     */
    RecordSet loadAllExperienceDetail(Record inputRecord);
}