package dti.ci.orggroupmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 16, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 */

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
 * ---------------------------------------------------
 */
public interface OrgGroupDAO {

    /**
     * Get Member Info
     * @param inputRecord Group Info
     * @return  Member Info
     */
    RecordSet loadAllMember(Record inputRecord);

    /**
     * Get Member Info
     * @param inputRecord Group Info
     * @return  Address Info
     */
    RecordSet loadAddress(Record inputRecord);

    /**
     * Get Member Info
     * @param inputRecord Group Info
     * @return  Summary Info
     */
    RecordSet loadSummary(Record inputRecord);
}
