package dti.ci.demographic.clientmgr.specialhandlingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for SpecialHandling information.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date: Jan 30, 2008
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface SpecialHandlingDAO {
    RecordSet loadSpecialHandlingsByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

    int addAllSpecialHandlings(RecordSet inputRecords);

    int updateAllSpecialHandlings(RecordSet inputRecords);
}
