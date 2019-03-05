package dti.ci.entityminipopupmgr.dao;

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
 *
 * ---------------------------------------------------
 */
public interface EntityMiniPopupDAO {

    /**
     * Load entity address
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityAddressList(Record inputRecord);

    /**
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAddressPhoneList(Record inputRecord);

    /**
     * Load entity general phone list
     * @param inputRecord
     * @return
     */
    RecordSet loadEntityGeneralPhoneList(Record inputRecord);
}
