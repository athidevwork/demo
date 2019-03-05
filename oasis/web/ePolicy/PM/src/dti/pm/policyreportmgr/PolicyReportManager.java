package dti.pm.policyreportmgr;

import dti.oasis.recordset.Record;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;

/**
 * Interface to handle Implementation of Policy Report Manager.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 26, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/02/2011       fcb         122664 & 107021: added generateXMLStream and generateCSVStream.
 * ---------------------------------------------------
 */
public interface PolicyReportManager {

    /**
     * Generates report as PDF stream
     *
     * @param inputRecord input parameters
     * @param conn        live JDBC Connection
     * @return PDF stream
     */
    public ByteArrayOutputStream generatePDFStream(Record inputRecord, Connection conn);

    /**
     * Check whether the data is empty.
     *
     * @param inputRecord input parameters
     * @param conn        live JDBC Connection
     * @return boolean    true is empty, false isn't empty.
     */
    public boolean isReportEmpty(Record inputRecord, Connection conn);

    /**
     * Generates a XML stream based on the input data.
     * @param inputRecord
     * @param conn
     * @return
     */
    public String generateXMLStream(Record inputRecord, Connection conn);

    /**
     * Generates a CSV stream based on the input data.
     * @param inputRecord
     * @param conn
     * @return
     */
    public String generateCSVStream(Record inputRecord, Connection conn);

}
