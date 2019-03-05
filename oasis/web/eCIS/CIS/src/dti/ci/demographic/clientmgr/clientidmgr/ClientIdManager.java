package dti.ci.demographic.clientmgr.clientidmgr;

import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       kshen       Removed class prefix "CI".
 * ---------------------------------------------------
 */
public interface ClientIdManager {
    /**
     * Load Client Ids By Entity PK
     * @param entityPk
     * @return
     */
    public RecordSet loadAllClientIds(Long entityPk);

    /**
     * Save All Client Ids.
     * @param entityPk
     * @param inputRecords
     * @return
     */
    public int saveAllClientIds(Long entityPk, RecordSet inputRecords);
}
