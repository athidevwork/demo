package dti.ci.entitymgr.impl.jdbchelpers;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/18/2017
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
public class AddressAdditionalXmlDataType implements SQLData {
    private final Logger l = LogUtils.getLogger(getClass());

    protected String any;
    protected String addressReference;

    protected com.delphi_tech.ows.party.AddressAdditionalXmlDataType jaxbAddressAdditionalXmlDataType;

    public String sql_type;

    public AddressAdditionalXmlDataType() {
        jaxbAddressAdditionalXmlDataType = new com.delphi_tech.ows.party.AddressAdditionalXmlDataType();
    }

    public AddressAdditionalXmlDataType(String any, String addressReference) {
        this.any = any;
        this.addressReference = addressReference;

        jaxbAddressAdditionalXmlDataType = new com.delphi_tech.ows.party.AddressAdditionalXmlDataType();

        if (!StringUtils.isBlank(this.any)) {
            jaxbAddressAdditionalXmlDataType.setAny(XMLUtils.stringToElement(this.any));
        }
        jaxbAddressAdditionalXmlDataType.setAddressReference(this.addressReference);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        this.sql_type = typeName;

        this.addressReference = stream.readString();
        try {
            this.any = DatabaseUtils.ClobToString(stream.readClob());
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to read entityAddlXml.", e);
            l.throwing(getClass().getName(), "readSQL", ae);
            throw ae;
        }

        this.jaxbAddressAdditionalXmlDataType = new com.delphi_tech.ows.party.AddressAdditionalXmlDataType();

        jaxbAddressAdditionalXmlDataType.setAddressReference(this.addressReference);
        if (!StringUtils.isBlank(this.any)) {
            jaxbAddressAdditionalXmlDataType.setAny(XMLUtils.stringToElement(this.any));
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

    public String getAddressReference() {
        return addressReference;
    }

    public void setAddressReference(String addressReference) {
        this.addressReference = addressReference;
    }

    public com.delphi_tech.ows.party.AddressAdditionalXmlDataType getJaxbAddressAdditionalXmlDataType() {
        return jaxbAddressAdditionalXmlDataType;
    }

    public void setJaxbAddressAdditionalXmlDataType(com.delphi_tech.ows.party.AddressAdditionalXmlDataType jaxbAddressAdditionalXmlDataType) {
        this.jaxbAddressAdditionalXmlDataType = jaxbAddressAdditionalXmlDataType;
    }
}
