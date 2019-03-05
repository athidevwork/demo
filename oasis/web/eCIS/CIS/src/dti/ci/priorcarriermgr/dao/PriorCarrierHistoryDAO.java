package dti.ci.priorcarriermgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

import java.sql.SQLException;

/**
 * The DAO class for Prior carrier.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 8, 2010
 *
 * @author jdingle
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/22/2009       kshen       Set default term year to request when loading page.
 * ---------------------------------------------------
 */
public interface PriorCarrierHistoryDAO {
    /**
     * Load all prior carrier of a entity by filter criteria.
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Get the default term year.
     * @return
     */
    public String getDefaultTermYear(Record inputRecord);

    /**
     * Save all the prior carrier records.
     * @param inputRecords
     * @return
     */
    public int saveAllPriorCarrier(RecordSet inputRecords);

    /**
     * load all prior carrier history
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPriorCarrierHistory(Record inputRecord) throws SQLException;

    /**
     * save all prior carrier history
     *
     * @param rs prior carrier info
     */
    public int saveAllPriorCarrierHistory(RecordSet rs) throws SQLException;

    /**
     * Check if audit record exists for an entity.
     * @param inputRecord
     * @return
     */
    public YesNoFlag hasAuditRecord(Record inputRecord);
}
