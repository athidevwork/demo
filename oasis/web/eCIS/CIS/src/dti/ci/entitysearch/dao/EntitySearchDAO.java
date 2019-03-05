package dti.ci.entitysearch.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/2/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntitySearchDAO {

    /**
     * Get policy count according to policyNo
     *
     * @param inputRecord
     * @return int
     */
    int getPolicyCnt(Record inputRecord);

    /**
     * Retrieve entity list based on search criteria passed in record.
     *
     * @param inputRecord Record containing search criteria.
     * @param recordLoadProcessor
     * @return RecordSet                The entity list record set.
     */
    RecordSet getEntityList(Record inputRecord, RecordLoadProcessor recordLoadProcessor);


    /**
     * Retrieve claims the entity participates.
     *
     * @param inputRecord
     * @return
     */
    RecordSet getEntityClaims(Record inputRecord);

}
