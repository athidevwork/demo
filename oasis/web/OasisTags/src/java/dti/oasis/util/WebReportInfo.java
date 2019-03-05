package dti.oasis.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * JavaBean containing WebReport information
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2004
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 6/10/2005        jbe         Added outputType attribute
 * ---------------------------------------------------
*/

public class WebReportInfo implements Serializable {
    private long reportPk;
    private String shortDescription;
    private String longDescription;
    private String code;
    private String reportType;
    private String fileName;
    private String pathName;
    private String outputType;
    private ArrayList queries = new ArrayList(3);

    /**
     * Add a query to the list of queries that back this report.
     * @param query WebQueryInfo object describing the query
     */
    public void addQuery(WebQueryInfo query) {
        queries.add(query);    
    }

    /**
     * Getter of short report description
     * @return web_report.short_description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Setter of short report description
     * @param shortDescription web_report.short_description
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Getter of report output type
     * @return web_report.report_output_type
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * Setter of report output type
     * @param outputType web_report.report_output_type
     */
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    /**
     * Getter of unique alpha code identifying report.
     * @return web_report.code
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter of unique alpha code identifying report.
     * @param code web_report.code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Getter of report primary key.
     * @return web_report.web_report_pk
     */
    public long getReportPk() {
        return reportPk;
    }

    /**
     * Getter of list of Queries that back this report.
     * @return ArrayList of WebQueryInfo objects
     */
    public ArrayList getQueries() {
        return queries;
    }

    /**
     * Setter of report primary key.
     * @param reportPk web_report.web_report_pk
     */
    public void setReportPk(long reportPk) {
        this.reportPk = reportPk;
    }

    /**
     * Getter of report long description
     * @return web_report.long_description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Setter of report long description
     * @param longDescription web_report.long_description
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Getter of report type.
     * @return web_report.report_type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Setter of report type.
     * @param reportType web_report.report_type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Getter of report's template filename (typically, XSL file).
     * @return web_report.report_filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter of report's template filename (typically, XSL file).
     * @param fileName web_report.report_filename
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter of fully qualified path for report's template filename: c:\reports\templates\
     * @return web_report.report_path
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Noarg Constructor
     */
    public WebReportInfo() {
    }

    /**
     * Setter of fully qualified path for report's template filename: c:\reports\templates\
     * @param pathName web_report.report_path
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * toString
     *
     * @return String representation of object
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.WebReportInfo");
        buf.append("{reportPk=").append(reportPk);
        buf.append(",shortDescription=").append(shortDescription);
        buf.append(",longDescription=").append(longDescription);
        buf.append(",code=").append(code);
        buf.append(",reportType=").append(reportType);
        buf.append(",fileName=").append(fileName);
        buf.append(",pathName=").append(pathName);
        buf.append(",outputType=").append(outputType);
        buf.append(",queries=").append(queries);
        buf.append('}');
        return buf.toString();
    }
}
