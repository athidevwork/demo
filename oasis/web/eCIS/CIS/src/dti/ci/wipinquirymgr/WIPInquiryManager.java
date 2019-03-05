package dti.ci.wipinquirymgr;

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
public interface WIPInquiryManager {

    /**
     * Load a record set of wip inquiries for a specific entity.
     *
     * @param entityId
     * @return
     */
    RecordSet loadWIPInquiry(String entityId);

}
