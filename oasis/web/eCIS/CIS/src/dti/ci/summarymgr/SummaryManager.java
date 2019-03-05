package dti.ci.summarymgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 16, 2008
 */
/*
 *  CIS Summary Business Layer Object Interface
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface SummaryManager {
    /**
     * Load all Entity's Policy and Quote
     * @param inputRecord
     * @return
     */
    RecordSet loadAllPolandQteByEntity(Record inputRecord);

    /**
     * Load all Entity's Policy and Quote including MiniPolicy
     * @param inputRecord
     * @return
     */
    RecordSet loadCombinedPolandQteByEntity(Record inputRecord);


    /**
     * Load all Entity's Risks including MiniRisk
     * @param inputRecord
     * @return
     */
    RecordSet loadCombinedRiskByEntity(Record inputRecord);

    /**
     * Load all Entity's Account
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAccountsByEntity(Record inputRecord);

    /**
     * Load all Account and Policy's Billings. DB update needed, so start with perform_
     * @param inputRecord
     * @return
     */
    RecordSet performAllBillingsByAccountAndPolicy(Record inputRecord);

    /**
     * Load all Entity's Claims
     * @param inputRecord
     * @return
     */
    RecordSet loadAllClaimsByEntity(Record inputRecord);
}
