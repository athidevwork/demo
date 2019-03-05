package dti.ci.entityminipopupmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * Interface to handle logics of Entity Mini Popup Manager
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 28, 2010
 *
 * @author bchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2018       dzhang      Issue 192649: entity mini popup refactor
 * ---------------------------------------------------
 */
public interface EntityMiniPopupManager {

    /**
     * Load entity information
     * @param inputRecord
     * @return
     */
    Record loadEntity(Record inputRecord);

    /**
     * load entity address
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityAddressList(Record inputRecord);

    /**
     * Load address phone list
     * @param inputRecord
     * @return
     */
    RecordSet loadAddressPhoneList(Record inputRecord);

     /**
     * To load contact list
     *
     * @param inputRecord
     * @return
     */
    RecordSet getContactList(Record inputRecord);

    /**
     * Load entity general phone list
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityGeneralPhoneList(Record inputRecord);
}
