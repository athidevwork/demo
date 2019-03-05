package dti.ci.wipinquirymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/17/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2018       dpang       Issue 192648. Refactor WIP Inquiry.
 * ---------------------------------------------------
 */
public interface WIPInquiryDAO {

    /**
     * Load a record set of wip inquiries for a specific entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadWIPInquiry(Record inputRecord);


    /**
     * Get client name by the client entity fk.
     *
     * @param inputRecord
     * @return
     */
    String getClientName(Record inputRecord);

}
