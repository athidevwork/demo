package dti.ci.orggroupmgr.impl;

import dti.ci.core.CIFields;
import dti.ci.entitymgr.EntityFields;
import dti.ci.helpers.ICIConstants;
import dti.ci.orggroupmgr.OrgGroupManager;
import dti.ci.orggroupmgr.dao.OrgGroupDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.BaseResultSetRecordSetAdaptor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;


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
 * 07/28/2009       Leo         Issue 95771
 * 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
 * ---------------------------------------------------
 */
public class OrgGroupManagerImpl implements OrgGroupManager {

    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(XMLUtils.class);

    /**
     * Get Org Group Members
     *
     * @param inputRecord Group Entity Info
     * @return Member Info
     */
    @Override
    public RecordSet loadAllMember(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMember", new Object[]{inputRecord});
        }

        RecordSet rs = getOrgGroupDAO().loadAllMember(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMember", rs);
        }
        return rs;
    }

    @Override
    public RecordSet loadSummary(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSummary", new Object[]{inputRecord});
        }
        RecordSet rs = getOrgGroupDAO().loadSummary(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSummary", rs);
        }
        return rs;
    }

    @Override
    public RecordSet loadAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddress", new Object[]{inputRecord});
        }
        RecordSet rs = getOrgGroupDAO().loadAddress(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddress", rs);
        }
        return rs;
    }

    /**
     * Generates report as PDF stream
     *
     * @param conn       Live JDBC Connection
     * @param inputRecord
     * @return PDF stream
     * @throws Exception
     */
    @Override
    public ByteArrayOutputStream generatePDFStream(Connection conn, Record inputRecord) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePDFStream", new Object[]{conn, inputRecord});
        }

        inputRecord.setFieldValue(EntityFields.ENTITY_ID, inputRecord.getStringValue(CIFields.PK_PROPERTY));
        if (inputRecord.getStringValueDefaultEmpty(ICIConstants.PRINT_TYPE).equals("CURRENT")) {
            inputRecord.setFieldValue("memberType", "*");
            inputRecord.setFieldValue("memberStatus", "*");
        }

        //Get Members List
        RecordSet rs = loadAllMember(inputRecord);
        //Get Members Summary
        RecordSet rsSummary = loadSummary(inputRecord);

        // get report data in XML
        String xmlDataList = resultSetToXml(BaseResultSetRecordSetAdaptor.getInstance(rs));
        String xmlDataSum = resultSetToXml(BaseResultSetRecordSetAdaptor.getInstance(rsSummary));

        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append("<REPORT>\n");
        xml.append("<SECTION1>\n");
        xml.append(xmlDataList);
        xml.append("</SECTION1>\n");
        xml.append("<SECTION2>\n");
        xml.append(xmlDataSum);
        xml.append("</SECTION2>\n");
        xml.append("</REPORT>");

        WebReport report = WebReport.getInstance();
        // render report in PDF
        WebReportInfo reportInfo = report.getReportInfo(conn, "CIS_ORG_GROUP_REPORT", false);

        String fileName = reportInfo.getFileName();

        ByteArrayOutputStream bos = new FOPWebReport().FOPStream(xml.toString(), fileName);

        // done
        l.exiting(getClass().getName(), "generatePDFStream");
        return bos;
    }

    /**
     * Encodes a text value by wrapping it in a CDATA:
     * <![CDATA[mytext]]>
     *
     * @param val The value to encode
     * @return encoded value
     */
    public static String encode(String val) {
        return new StringBuilder("<![CDATA[").append(val).append("]]>").toString();
    }

    /**
     * Convert a BaseResultSet to XML in the format:
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL name="col1name"&gt;val&lt;/COL&gt;&lt;COL name="col2name"&gt;val
     * &lt;/COL&gt;&lt/ROW&gt;&lt/ROWS&gt
     *
     * @param data BaseResultSet
     * @return XML String
     */
    public static String resultSetToXml(BaseResultSet data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(OrgGroupManagerImpl.class.getName(), "resultSetToXml", new Object[]{data});
        }

        int colCount = data.getColumnCount();
        final String strTab = "  ";
        final String strOpen = "<";
        final String strClose = ">";
        final String strEndOpen = "</";
        final String strEndClose = "/>";
        StringBuilder xml = new StringBuilder("<ROWS>\n");

        // Start loop through rows
        while (data.next()) {
            xml.append("<ROW>\n");
            // Start loop through columns
            for (int i = 1; i <= colCount; i++) {
                String dataItem = data.getString(i);

                // tag start
                xml.append(strTab).append(strOpen).append(data.getColumnName(i).trim().toUpperCase());

                if (dataItem != null) {
                    xml.append(strClose);
                    xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                            encode(dataItem) : dataItem);
                    // closing tag
                    xml.append(strEndOpen).append(data.getColumnName(i).trim().toUpperCase()).append(strClose).append("\n");
                } else
                    xml.append(strEndClose);
            }
            xml.append("</ROW>\n");
            // End looping through columns
        }
        // End looping through rows
        xml.append("</ROWS>\n");

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(OrgGroupManagerImpl.class.getName(), "resultSetToXml", xml);
        }
        return xml.toString();
    }

    public void verifyConfig() {
        if (getOrgGroupDAO() == null) {
            throw new ConfigurationException("The required property 'OrgGroupDAO' is missing.");
        }
    }

    public OrgGroupDAO getOrgGroupDAO() {
        return orgGroupDAO;
    }

    public void setOrgGroupDAO(OrgGroupDAO orgGroupDAO) {
        this.orgGroupDAO = orgGroupDAO;
    }

    private OrgGroupDAO orgGroupDAO;
}

