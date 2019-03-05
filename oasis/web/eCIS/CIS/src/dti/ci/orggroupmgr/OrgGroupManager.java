package dti.ci.orggroupmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;

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
 * 07/28/2009       Leo         Issue 95771
 * 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
 * ---------------------------------------------------
 */
public interface OrgGroupManager {
    /**
     * Method to load members
     *
     * @param inputRecord a record containing group info
     * @return recordSet resultset containing members
     */
    RecordSet loadAllMember(Record inputRecord);

    /**
     * Method to load summary
     *
     * @param inputRecord a record containing group info
     * @return recordSet resultset summary
     */
    RecordSet loadSummary(Record inputRecord);

/**
     * Method to load Address
     *
     * @param inputRecord a record containing group info
     * @return recordSet having address info  
     */
    RecordSet loadAddress(Record inputRecord);

    /**
     * Generates report as PDF stream
     *
     * @param conn       Live JDBC Connection
     * @param inputRecord
     * @return PDF stream
     * @throws Exception
     */
    ByteArrayOutputStream generatePDFStream(Connection conn, Record inputRecord) throws Exception;
}
