package dti.oasis.util;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.XMLRecordSetMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Singleton to facilitate composite Web Reports.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2005
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/09/2005       sxm         add PDF report generation using FOP
 * 06/10/2005       jbe         Move PDF/fop code out to FOPWebReport
 * 06/30/2005       sxm         Modified generatePDFFile():
 *                              - replaced the risk_base_fk in PDF filename with time stamp
 * 07/26/2005       sxm         Modified generatePDFFile():
 *                              - changed PDF filename as <report code>+<file ID>+<time stamp>
 * 07/26/2005       sxm         Modified generatePDFFile():
 *                              - changed PDF filename as <report code>_<file ID>_<datetime>
 * 06/23/3008       Larry       issue 83734, filepath should be checked if it ends with "/"
 * 05/08/2009       Guang       93664: template access
 * 06/22/2009       yhyang      92861: Add setFOPBaseDir().
 * 06/25/2009       Leo         Modified for issue 95048
 * 07/01/2009       kshen       Generage pdf file in temp folder.
 * 03/11/2011       fcb         generateCSV added.
 * 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
 * ---------------------------------------------------
*/

public class WebReport {
    private static final WebReport INSTANCE = new WebReport();

    private static final String SQL_REPORT =
        " SELECT web_report_pk, short_description, " +
           "long_description, report_type, report_path, " +
           " substr(report_filename, instr(replace(report_filename,'\\','/'),'/',-1)+1) as report_filename," +
           " report_output_type " +
            "FROM web_report " +
            "WHERE code=?";

    private static final String SQL_REPORT_QUERIES = "SELECT web_query_fk FROM web_report_query " +
            "WHERE web_report_fk=? ORDER BY sequence";


    /**
     * Call this method to get the single instance of this class
     *
     * @return
     */
    public static WebReport getInstance() {
        return INSTANCE;
    }

    private WebReport() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * This basically runs a composite report by finding and executing all queries that it is
     * associated with... in sequence.  This will return XML as follows:
     * <pre>
     * &lt;REPORT&gt;
     * &lt;SECTION1&gt;
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL1NAME&gt;val&lt;/COL1NAME&gt;&lt;COL2NAME&gt;val
     * &lt;/COL2NAME&gt;&lt/ROW&gt;&lt/ROWS&gt
     * &lt;/SECTION1&gt;
     * &lt;SECTION2&gt;
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL1NAME&gt;val&lt;/COL1NAME&gt;&lt;COL2NAME&gt;val
     * &lt;/COL2NAME&gt;&lt/ROW&gt;&lt/ROWS&gt
     * &lt;/SECTION2&gt;
     * &lt;/REPORT&gt;
     * </pre>
     * It uses WebQuery to generate the report data and XMLUtils to generate most of the XML.
     *
     * @param conn       Live JDBC Connection
     * @param reportCode web_report.code
     * @param parms      Map of parameters to be bound to the underlying query or queries
     * @return XML in format above.
     * @throws SQLException
     * @see dti.oasis.util.XMLUtils#resultSetToXml(java.sql.ResultSet)
     * @see dti.oasis.util.WebQuery
     */
    public String generateXML(Connection conn, String reportCode, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "generateXML", new Object[]{conn, reportCode});
        // Outer XML
        // Get Report Info
        WebReportInfo info = getReportInfo(conn, reportCode, false);
        ArrayList queries = info.getQueries();
        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        if (queries != null) {
            int sz = info.getQueries().size();
            // Get Query object
            WebQuery query = WebQuery.getInstance();
            // <REPORT>
            xml.append("<REPORT>\n");
            // Iterate through Queries
            for (int i = 0; i < sz; i++) {
                StringBuffer sectionBuff = new StringBuffer("SECTION").append(i + 1).append(">\n");
                WebQueryInfo qInfo = (WebQueryInfo) info.getQueries().get(i);
                xml.append('<').append(sectionBuff);
                xml.append(query.getXML(conn, qInfo.getQueryPk(), parms));
                xml.append("</").append(sectionBuff).append('\n');
            }
            // </REPORT>
            xml.append("</REPORT>");
        }
        l.exiting(getClass().getName(), "generateXML", xml);
        return xml.toString();
    }

    /**
     *
     * @param conn
     * @param reportCode
     * @param parms
     * @return
     * @throws SQLException
     */
    public String generateCSV(Connection conn, String reportCode, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "generateCSV", new Object[]{conn, reportCode});

        WebReportInfo info = getReportInfo(conn, reportCode, false);
        ArrayList queries = info.getQueries();
        StringBuffer csv = new StringBuffer("");
        if (queries != null) {
            int sz = info.getQueries().size();
             WebQuery query = WebQuery.getInstance();
             for (int i = 0; i < sz; i++) {
                 WebQueryInfo qInfo = (WebQueryInfo) info.getQueries().get(i);
                 String output = query.getXML(conn, qInfo.getQueryPk(), parms);

                 RecordSet recordSet = new RecordSet();
                 XMLRecordSetMapper.getInstance().mapXMLExactly(output, recordSet);

                 Iterator it = recordSet.getRecords();
                 // Load first the Field names from the first record as header.
                 while (it.hasNext()) {
                     Record r = (Record)it.next();
                     Iterator fldNames = recordSet.getFieldNames();
                     if (fldNames.hasNext()) {
                         csv.append("\"").append(((String)fldNames.next()).replaceAll("_"," ")).append("\"");
                         while (fldNames.hasNext()) {
                            csv.append(",").append("\"").append(((String)fldNames.next()).replaceAll("_"," ")).append("\"");
                         }
                         csv.append('\n');
                         break;
                     }
                 }

                 //Load comma separated values.
                 it = recordSet.getRecords();
                 while (it.hasNext()) {
                     Record r = (Record)it.next();
                     Iterator fldNames = recordSet.getFieldNames();
                     if (fldNames.hasNext()) {
                         String fieldName = (String)fldNames.next();
                         String fieldValue = r.getStringValue(fieldName,"");
                         if (fieldValue==null) {
                             csv.append("\"").append("\"");
                         }
                         else {
                             csv.append("\"").append(fieldValue).append("\"");
                         }
                         while (fldNames.hasNext()) {
                             fieldName = (String)fldNames.next();
                             fieldValue = r.getStringValue(fieldName);
                             if (fieldValue==null) {
                                 csv.append(",").append("\"").append("\"");
                             }
                             else {
                                 csv.append(",").append("\"").append(fieldValue).append("\"");
                             }
                         }
                         csv.append('\n');
                     }
                 }
             }
        }

        l.exiting(getClass().getName(), "generateCSV", csv);
        return csv.toString();
    }

    /**
     * Returns a WebReportInfo object given a report code (web_report.code).  Each WebQueryInfo
     * object within the WebReportInfo.queries collection will be fully loaded with all Query
     * information from the WebQuery object.  This is a convenience method for calling
     * getReportInfo with TRUE for the last parameter.
     *
     * @param conn       Live JDBC Connection
     * @param reportCode web_report.code
     * @return WebReportInfo object
     * @throws SQLException
     */
    public WebReportInfo getReportInfo(Connection conn, String reportCode) throws SQLException {
        return getReportInfo(conn, reportCode, true);
    }

    /**
     * Returns a WebReportInfo object given a report code (web_report.code).
     *
     * @param conn             Live JDBC Connection
     * @param reportCode       web_report.code
     * @param getFullQueryInfo If TRUE, then each WebQueryInfo object in the WebReportInfo.queries collection
     *                         will be fully loaded with all Query information from the WebQuery object.  If FALSE, then only the queryPk
     *                         attribute will be set within the respective WebQueryInfo objects.
     * @return a WebReportInfo object
     * @throws SQLException
     */
    public WebReportInfo getReportInfo(Connection conn, String reportCode, boolean getFullQueryInfo) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getReportInfo", new Object[]{conn, reportCode});
        WebReportInfo info = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        WebQuery query = WebQuery.getInstance();
        try {
            // Get Report info
            stmt = conn.prepareStatement(SQL_REPORT);
            stmt.setString(1, reportCode);
            l.fine(new StringBuffer("Executing: ").append(SQL_REPORT).append(" with ").append(reportCode).toString());
            rs = stmt.executeQuery();
            // got report info?  good!
            if (rs.next()) {
                // construct WebReportInfo Bean and store stuff in it
                info = new WebReportInfo();
                info.setReportPk(rs.getLong(1));
                info.setShortDescription(rs.getString(2));
                info.setLongDescription(rs.getString(3));
                info.setReportType(rs.getString(4));
                info.setPathName(rs.getString(5));
                info.setFileName(rs.getString(6));
                info.setOutputType(rs.getString(7));
                // done with this statement & resultset
                DatabaseUtils.close(stmt, rs);
                // next statement and resultset
                stmt = conn.prepareStatement(SQL_REPORT_QUERIES);
                stmt.setLong(1, info.getReportPk());
                l.fine(new StringBuffer("Executing: ").append(SQL_REPORT_QUERIES).append(" with ").
                        append(info.getReportPk()).toString());
                rs = stmt.executeQuery();
                // Got queries with this report?  good.
                while (rs.next()) {
                    long queryFk = rs.getLong(1);
                    // For each query, get a QueryInfo object either from the WebQuery object
                    // or simply construct one with the querypk
                    info.addQuery(getFullQueryInfo ? query.getQuery(conn, queryFk) : new WebQueryInfo(queryFk));
                }
            }
            l.exiting(getClass().getName(), "getReportInfo", info);
            return info;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }

    /**
     * Generates report as PDF stream
     *
     * @param conn       Live JDBC Connection
     * @param reportCode web_report.code
     * @param map        Map of parameters to be bound to the underlying query or queries
     * @return PDF stream
     * @throws Exception
     */
    public ByteArrayOutputStream generatePDFStream(Connection conn, String reportCode, Map map)
            throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePDFStream",
                new Object[]{conn, reportCode, map});
        // get report data in XML
        WebReport report = WebReport.getInstance();
        String xml = report.generateXML(conn, reportCode, map);

        // render report in PDF
        WebReportInfo reportInfo = report.getReportInfo(conn, reportCode, false);

        String fileName = reportInfo.getFileName();

        ByteArrayOutputStream bos = new FOPWebReport().FOPStream(xml, fileName);

        // done
        l.exiting(getClass().getName(), "generatePDFStream");
        return bos;
    }


    /**
     * generate report as PDF file
     *
     * @param conn       Live JDBC Connection
     * @param reportCode web_report.code
     * @param fileId     filename ID
     * @param map        Map of parameters to be bound to the underlying query or queries
     * @return PDF filename
     * @throws Exception
     */
    public String generatePDFFile(Connection conn, String reportCode, String fileId, Map map)
            throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePDFFile",
                new Object[]{conn, reportCode, fileId, map});
        // get report data in XML
        WebReport report = WebReport.getInstance();
        String xml = report.generateXML(conn, reportCode, map);

        // render report in PDF
        WebReportInfo reportInfo = report.getReportInfo(conn, reportCode, false);
        //String pathName = reportInfo.getPathName() + "\\";
        String fileName = reportInfo.getFileName();

        // Set pdf file name.
        String today = DateUtils.formatDateTimeWithoutSeparator(new Date());
//      String xslFileName = new StringBuffer(pathName).append(fileName).toString();
        String tempPath = System.getProperty("java.io.tmpdir");
        StringBuffer pdfFileNameBuff = new StringBuffer(tempPath);
        if (!tempPath.endsWith(File.separator)) {
            pdfFileNameBuff.append(File.separator);
        }
        String pdfFileName = pdfFileNameBuff.append(reportCode)
            .append("_").append(fileId).append("_").append(today).append(".pdf").toString();

        new FOPWebReport().FOPFile(xml, fileName, pdfFileName);

        // done
        l.exiting(getClass().getName(), "generatePDFFile", pdfFileName);
        return pdfFileName;
    }

    /**
     * In FOP version 0.20.5, in order to show the image in PDF, we must set baseDir since we haven't config it in userConfig.xml.
     * 
     * @param baseDir
     */
    public void setFOPBaseDir(String baseDir){
        Logger l = LogUtils.enterLog(getClass(), "setFOPBaseDir",
                new Object[]{baseDir});
        FOPWebReport.setBaseURL(baseDir);
        l.exiting(getClass().getName(), "setFOPBaseDir");
    }

}
