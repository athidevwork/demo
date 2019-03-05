package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2017
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
public class PartyAdditionalInfoType implements SQLData {
    protected List<PersonAdditionalInfoType> personAdditionalInfo;
    protected List<OrganizationAdditionalInfoType> organizationAdditionalInfo;
    protected List<AddressAdditionalInfoType> addressAdditionalInfo;

    protected com.delphi_tech.ows.party.PartyAdditionalInfoType jaxbPartyAdditionalInfoType;

    public String sql_type;

    public PartyAdditionalInfoType() {
        jaxbPartyAdditionalInfoType = new com.delphi_tech.ows.party.PartyAdditionalInfoType();
    }

    public PartyAdditionalInfoType(PersonAdditionalInfoType personAdditionalInfo,
                                   OrganizationAdditionalInfoType organizationAdditionalInfo,
                                   AddressAdditionalInfoType addressAdditionalInfo) {
        if (personAdditionalInfo != null) {
            this.getPersonAdditionalInfo().add(personAdditionalInfo);
        }
        if (organizationAdditionalInfo != null) {
            this.getOrganizationAdditionalInfo().add(organizationAdditionalInfo);
        }
        if (addressAdditionalInfo != null) {
            this.getAddressAdditionalInfo().add(addressAdditionalInfo);
        }

        jaxbPartyAdditionalInfoType = new com.delphi_tech.ows.party.PartyAdditionalInfoType();

        if (personAdditionalInfo != null) {
            jaxbPartyAdditionalInfoType.getPersonAdditionalInfo().add(personAdditionalInfo.getJaxbPersonAdditionalInfoType());
        }
        if (organizationAdditionalInfo != null) {
            jaxbPartyAdditionalInfoType.getOrganizationAdditionalInfo().add(organizationAdditionalInfo.getJaxbOrganizationAdditionalInfoType());
        }
        if (addressAdditionalInfo != null) {
            jaxbPartyAdditionalInfoType.getAddressAdditionalInfo().add(addressAdditionalInfo.getJaxbAddressAdditionalInfoType());
        }
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;

        personAdditionalInfo = (List) setList(stream.readObject(), PersonAdditionalInfoType.class);
        organizationAdditionalInfo = (List) setList(stream.readObject(), OrganizationAdditionalInfoType.class);
        addressAdditionalInfo = (List) setList(stream.readObject(), AddressAdditionalInfoType.class);

        jaxbPartyAdditionalInfoType = new com.delphi_tech.ows.party.PartyAdditionalInfoType();

        Iterator personAdditionalInfoIt = personAdditionalInfo.iterator();
        while(personAdditionalInfoIt.hasNext()){
            jaxbPartyAdditionalInfoType.getPersonAdditionalInfo().add(((PersonAdditionalInfoType) personAdditionalInfoIt.next()).getJaxbPersonAdditionalInfoType()) ;
        }

        Iterator organizationAdditionalInfoIt = organizationAdditionalInfo.iterator();
        while(organizationAdditionalInfoIt.hasNext()){
            jaxbPartyAdditionalInfoType.getOrganizationAdditionalInfo().add(((OrganizationAdditionalInfoType) organizationAdditionalInfoIt.next()).getJaxbOrganizationAdditionalInfoType()) ;
        }

        Iterator addressAdditionalInfoIt = addressAdditionalInfo.iterator();
        while(addressAdditionalInfoIt.hasNext()){
            jaxbPartyAdditionalInfoType.getAddressAdditionalInfo().add(((AddressAdditionalInfoType) addressAdditionalInfoIt.next()).getJaxbAddressAdditionalInfoType()) ;
        }
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
    /*
        This method should not have implementation
        We are not planning to make any updates using this method.
    */
    }

    public List<PersonAdditionalInfoType> getPersonAdditionalInfo() {
        if (this.personAdditionalInfo == null) {
            this.personAdditionalInfo = new ArrayList<PersonAdditionalInfoType>();
        }
        return personAdditionalInfo;
    }

    public List<OrganizationAdditionalInfoType> getOrganizationAdditionalInfo() {
        if (this.organizationAdditionalInfo == null) {
            this.organizationAdditionalInfo = new ArrayList<OrganizationAdditionalInfoType>();
        }
        return organizationAdditionalInfo;
    }

    public List<AddressAdditionalInfoType> getAddressAdditionalInfo() {
        if (this.addressAdditionalInfo == null) {
            this.addressAdditionalInfo = new ArrayList<AddressAdditionalInfoType>();
        }
        return this.addressAdditionalInfo;
    }

    public com.delphi_tech.ows.party.PartyAdditionalInfoType getJaxbPartyAdditionalInfoType() {
        return jaxbPartyAdditionalInfoType;
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
}
