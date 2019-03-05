package dti.ci.entitymgr.impl.jdbchelpers;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;

import java.sql.*;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/17/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityAdditionalXmlDataType implements SQLData {
    private final Logger l = LogUtils.getLogger(getClass());

    protected String any;
    protected String personReference;
    protected String organizationReference;

    protected com.delphi_tech.ows.party.EntityAdditionalXmlDataType jaxbEntityAdditionalXmlDataType;

    public String sql_type;

    public EntityAdditionalXmlDataType() {
        jaxbEntityAdditionalXmlDataType = new com.delphi_tech.ows.party.EntityAdditionalXmlDataType();
    }

    public EntityAdditionalXmlDataType(String any, String personReference, String organizationReference) {
        this.any = any;
        this.personReference = personReference;
        this.organizationReference = organizationReference;

        jaxbEntityAdditionalXmlDataType = new com.delphi_tech.ows.party.EntityAdditionalXmlDataType();

        if (!StringUtils.isBlank(this.any)) {
            jaxbEntityAdditionalXmlDataType.setAny(XMLUtils.stringToElement(this.any));
        }
        jaxbEntityAdditionalXmlDataType.setPersonReference(this.personReference);
        jaxbEntityAdditionalXmlDataType.setOrganizationReference(this.organizationReference);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        this.sql_type = typeName;

        String key = stream.readString();
        String entityType = stream.readString();

        if (!StringUtils.isBlank(key)) {
            if ("P".equals(entityType)) {
                this.personReference = key;
            } else if ("O".equals(entityType)) {
                this.organizationReference = key;
            }
        }

        try {
            this.any = DatabaseUtils.ClobToString(stream.readClob());
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to read entityAddlXml.", e);
            l.throwing(getClass().getName(), "readSQL", ae);
            throw ae;
        }

        this.jaxbEntityAdditionalXmlDataType = new com.delphi_tech.ows.party.EntityAdditionalXmlDataType();

        jaxbEntityAdditionalXmlDataType.setPersonReference(this.personReference);
        jaxbEntityAdditionalXmlDataType.setOrganizationReference(this.organizationReference);
        if (!StringUtils.isBlank(this.any)) {
            jaxbEntityAdditionalXmlDataType.setAny(XMLUtils.stringToElement(this.any));
        }
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public String getAny() {
        return any;
    }

    public void setAny(String any) {
        this.any = any;
    }

    public String getPersonReference() {
        return personReference;
    }

    public void setPersonReference(String personReference) {
        this.personReference = personReference;
    }

    public String getOrganizationReference() {
        return organizationReference;
    }

    public void setOrganizationReference(String organizationReference) {
        this.organizationReference = organizationReference;
    }

    public com.delphi_tech.ows.party.EntityAdditionalXmlDataType getJaxbEntityAdditionalXmlDataType() {
        return jaxbEntityAdditionalXmlDataType;
    }

    public void setJaxbEntityAdditionalXmlDataType(com.delphi_tech.ows.party.EntityAdditionalXmlDataType jaxbEntityAdditionalXmlDataType) {
        this.jaxbEntityAdditionalXmlDataType = jaxbEntityAdditionalXmlDataType;
    }
}
