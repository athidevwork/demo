package dti.ci.auditmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/9/2018
 *
 * @author yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/10/2018       ylu         109179: refactor audit trail tab page and popup page
 * ---------------------------------------------------
 */
public interface AuditTrailManager {

    /**
     * get default search criteria value from workbench
     * @param actionClassName
     * @return
     */
    Record getDefaultSearchCriteriaValue(String actionClassName);

    /**
     * search and load all audit trail data in tab page for this entity
     * @param inputRecord
     * @return
     */
    RecordSet searchAuditTrailData(Record inputRecord);

    /**
     * load audit trail history data in Popup page for this entity
     * @param inputRecord
     * @return
     */
    RecordSet loadAuditTrailBySource(Record inputRecord);
}
