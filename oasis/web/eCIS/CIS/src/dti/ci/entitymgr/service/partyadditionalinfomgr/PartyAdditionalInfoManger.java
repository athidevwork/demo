package dti.ci.entitymgr.service.partyadditionalinfomgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/3/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PartyAdditionalInfoManger {
    /**
     * Load address info record.
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressAdditionalInfo(Record inputRecord);

    /**
     * Load person additional info.
     *
     * @param inputRecord
     * @return
     */
    public Record loadPersonAdditionalInfo(Record inputRecord);

    /**
     * Load organization additional info.
     *
     * @param inputRecord
     * @return
     */
    public Record loadOrganizationAdditionalInfo(Record inputRecord);

    /**
     * Save additional xml data.
     *
     * @param record
     */
    public void saveAdditionalXmlData(Record record);

    /**
     * Save address additional info record.
     *
     * @param record
     */
    public void saveAddressAdditionalInfo(Record record);

    /**
     * Save person additional info.
     *
     * @param record
     */
    public void savePersonAdditionalInfo(Record record);

    /**
     * Save organization additional info.
     *
     * @param record
     */
    public void saveOrganizationAdditionalInfo(Record record);
}
