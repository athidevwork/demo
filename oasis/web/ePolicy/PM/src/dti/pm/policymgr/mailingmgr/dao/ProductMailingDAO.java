package dti.pm.policymgr.mailingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for product mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface ProductMailingDAO {

    /**
     * This method used to save product mailing data.
     *
     * @param inputRecordSet
     * @return
     */
    public int saveProductMailingInfo(RecordSet inputRecordSet);

    /**
     * This method used to load all product mailing.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllProductMailing(Record inputRecord, RecordLoadProcessor loadProcessor);
}
