package dti.oasis.util;

import java.io.Serializable;

/**
 * JavaBean containing WebForm information
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2004
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 4/4/2005         jbe         Revised to use WebQueryInfo
 *
 * ---------------------------------------------------
*/

public class WebFormInfo implements Serializable {
    private long formPk;
    private long queryFk;
    private String shortDescription;
    private String code;
    private String formType;
    private String fileName;
    private String pathName;
    private WebQueryInfo webQuery;

    /**
     * Getter of query primary key.
     * @return web_form.web_query_fk
     */
    public long getQueryFk() {
        return queryFk;
    }

    /**
     * Setter of query primary key.
     * @param queryFk web_form.web_query_fk
     */
    public void setQueryFk(long queryFk) {
        this.queryFk = queryFk;
    }

    /**
     * Getter of underlying query object.
     * @return WebQueryInfo object
     */
    public WebQueryInfo getWebQuery() {
        return webQuery;
    }

    /**
     * Setter of underlying query object.
     * @param webQuery WebQueryInfo object
     */
    public void setWebQuery(WebQueryInfo webQuery) {
        this.webQuery = webQuery;
    }

    /**
     * Getter of form description.
     * @return web_form.short_description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Getter of form primary key.
     * @return web_form.web_form_pk
     */
    public long getFormPk() {
        return formPk;
    }

    /**
     * Setter of form primary key
     * @param formPk web_form.web_form_pk
     */
    public void setFormPk(long formPk) {
        this.formPk = formPk;
    }

    /**
     * Setter of form description
     * @param shortDescription web_form.short_description
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Getter of unique alpha identifier of form.
     * @return web_form.code
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter of unique alpha identifier of form.
     * @param code web_form.code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Getter of form type.
     * @return web_form.form_type
     */
    public String getFormType() {
        return formType;
    }

    /**
     * Setter of form type
     * @param formType web_form.form_type
     */
    public void setFormType(String formType) {
        this.formType = formType;
    }

    /**
     * Getter of underlying query SQL
     * @return web_query.query_text
     */
    public String getQuery() {
        return webQuery.getSql();
    }

    /**
     * Setter of underlying query SQL.
     * @param query
     */
    public void setQuery(String query) {
        webQuery.setSql(query);
    }

    /**
     * Getter of form's underlying template file (pdf file for example).
     * @return web_form.form_filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter of form's underlying template file (pdf file for example).
     * @param fileName web_form.form_filename
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter of the fully qualified path for the form's template file: c:\forms\templates\
     * @return web_form.form_path
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * NoArg Constructor
     */
    public WebFormInfo() {
    }

    /**
     * Setter of the fully qualified path for the form's template file: c:\forms\templates\
     * @param pathName web_form.form_path
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * toString
     * @return String representation of object
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.WebFormInfo");
        buf.append("{formPk=").append(formPk);
        buf.append(",queryFk=").append(queryFk);
        buf.append(",shortDescription=").append(shortDescription);
        buf.append(",code=").append(code);
        buf.append(",formType=").append(formType);
        buf.append(",fileName=").append(fileName);
        buf.append(",pathName=").append(pathName);
        buf.append(",webQuery=").append(webQuery);
        buf.append('}');
        return buf.toString();
    }
}
