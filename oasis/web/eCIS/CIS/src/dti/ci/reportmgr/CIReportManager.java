package dti.ci.reportmgr;

import dti.oasis.recordset.Record;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;

/**
 * Interface to handle Implementation of CIS Report Manager.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   June 16, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface CIReportManager {

    /**
     * Generates report as PDF stream
     *
     * @param inputRecord input parameters
     * @param conn        live JDBC Connection
     * @return PDF stream
     */
    public ByteArrayOutputStream generatePDFStream(Record inputRecord, Connection conn);
}
