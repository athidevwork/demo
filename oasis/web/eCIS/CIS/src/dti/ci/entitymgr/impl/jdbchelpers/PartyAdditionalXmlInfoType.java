package dti.ci.entitymgr.impl.jdbchelpers;

import dti.oasis.util.LogUtils;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class PartyAdditionalXmlInfoType implements SQLData {
    private final Logger l = LogUtils.getLogger(getClass());
    protected List<EntityAdditionalXmlDataType> entityAdditionalXmlData;
    protected List<AddressAdditionalXmlDataType> addressAdditionalXmlData;

    protected com.delphi_tech.ows.party.PartyAdditionalXmlInfoType  jaxbPartyAdditionalXmlInfoType;

    public String sql_type;

    public PartyAdditionalXmlInfoType() {
        jaxbPartyAdditionalXmlInfoType = new com.delphi_tech.ows.party.PartyAdditionalXmlInfoType();
    }

    public PartyAdditionalXmlInfoType(EntityAdditionalXmlDataType entityAdditionalXmlData, AddressAdditionalXmlDataType addressAdditionalXmlData) {
        if (entityAdditionalXmlData != null) {
            this.getEntityAdditionalXmlData().add(entityAdditionalXmlData);
        }

        if (addressAdditionalXmlData != null) {
            this.getAddressAdditionalXmlData().add(addressAdditionalXmlData);
        }

        jaxbPartyAdditionalXmlInfoType = new com.delphi_tech.ows.party.PartyAdditionalXmlInfoType();

        if (entityAdditionalXmlData != null) {
            jaxbPartyAdditionalXmlInfoType.getEntityAdditionalXmlData().add(entityAdditionalXmlData.getJaxbEntityAdditionalXmlDataType());
        }
        if (entityAdditionalXmlData != null) {
            jaxbPartyAdditionalXmlInfoType.getAddressAdditionalXmlData().add(addressAdditionalXmlData.getJaxbAddressAdditionalXmlDataType());
        }
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;

        entityAdditionalXmlData = (List) setList(stream.readObject(), EntityAdditionalXmlDataType.class);
        addressAdditionalXmlData = (List) setList(stream.readObject(), AddressAdditionalXmlDataType.class);

        jaxbPartyAdditionalXmlInfoType = new com.delphi_tech.ows.party.PartyAdditionalXmlInfoType();

        Iterator it = entityAdditionalXmlData.iterator();
        while(it.hasNext()){
            jaxbPartyAdditionalXmlInfoType.getEntityAdditionalXmlData().add(((EntityAdditionalXmlDataType) it.next()).getJaxbEntityAdditionalXmlDataType()) ;
        }

        it = addressAdditionalXmlData.iterator();
        while(it.hasNext()){
            jaxbPartyAdditionalXmlInfoType.getAddressAdditionalXmlData().add(((AddressAdditionalXmlDataType) it.next()).getJaxbAddressAdditionalXmlDataType()) ;
        }
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    private Object setClass(Object obj, Class clazz) {
        Object type = null;
        try {
            ARRAY tmp = (ARRAY) obj;
            ResultSet rs = tmp.getResultSet();
            while (rs.next()) {
                STRUCT struct = (STRUCT) rs.getObject(2);
                type = struct.toClass(clazz);
            }
        } catch (Exception e) {
            //do nothing  just return null
            e.getMessage();
        }
        return type;
    }

    private List setList(Object obj, Class clazz) {
        List lst = new ArrayList();
        try {
            Object resultElems = ((ARRAY) obj).getOracleArray();
            Datum[] listElems = (Datum[]) resultElems;
            for (int r = 0; r < listElems.length; r++) {
                STRUCT struct = (STRUCT) listElems[r];
                lst.add(struct.toClass(clazz));
            }

        } catch (Exception e) {
            // do nothing just return null
            e.getMessage();
        }
        return lst;
    }

    public List<EntityAdditionalXmlDataType> getEntityAdditionalXmlData() {
        if (entityAdditionalXmlData == null) {
            entityAdditionalXmlData = new ArrayList<EntityAdditionalXmlDataType>();
        }
        return entityAdditionalXmlData;
    }

    public List<AddressAdditionalXmlDataType> getAddressAdditionalXmlData() {
        if (addressAdditionalXmlData == null) {
            addressAdditionalXmlData = new ArrayList<AddressAdditionalXmlDataType>();
        }
        return this.addressAdditionalXmlData;
    }

    public com.delphi_tech.ows.party.PartyAdditionalXmlInfoType getJaxbPartyAdditionalXmlInfoType() {
        return jaxbPartyAdditionalXmlInfoType;
    }

    public void setJaxbPartyAdditionalXmlInfoType(com.delphi_tech.ows.party.PartyAdditionalXmlInfoType jaxbPartyAdditionalXmlInfoType) {
        this.jaxbPartyAdditionalXmlInfoType = jaxbPartyAdditionalXmlInfoType;
    }
}
