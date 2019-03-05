package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/16/14
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ReferredPartyType implements SQLData {

    protected String partyNumberId;
    protected String partyFullName;
    protected String clientId;

    protected com.delphi_tech.ows.party.ReferredPartyType jaxbReferredPartyType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    public ReferredPartyType() {
        jaxbReferredPartyType = new com.delphi_tech.ows.party.ReferredPartyType();
    }

    public ReferredPartyType(String partyNumberId,
                             String partyFullName,
                             String clientId) {
        this.partyNumberId = partyNumberId;
        this.partyFullName = partyFullName;
        this.clientId = clientId;

        jaxbReferredPartyType = new com.delphi_tech.ows.party.ReferredPartyType();
        jaxbReferredPartyType.setPartyNumberId((this.partyNumberId == null) ? "" : partyNumberId);
        jaxbReferredPartyType.setPartyFullName((this.partyFullName == null) ? "" : partyFullName);
        jaxbReferredPartyType.setClientId((this.clientId == null) ? "" : clientId);
    }

    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        partyNumberId = stream.readString();
        partyFullName = stream.readString();
        clientId = stream.readString();

        jaxbReferredPartyType.setPartyNumberId((this.partyNumberId == null) ? "" : partyNumberId);
        jaxbReferredPartyType.setPartyFullName((this.partyFullName==null)?"":partyFullName);
        jaxbReferredPartyType.setClientId((this.clientId==null)?"":clientId);
    }

    public void writeSQL(SQLOutput stream) throws SQLException {
       /*
       This method should not have implementation
       We are not planning to make any updates using this method.
       */
    }

    public com.delphi_tech.ows.party.ReferredPartyType getJaxbReferredPartyType(){
        return jaxbReferredPartyType;
    }

    private  Object setClass(Object obj, Class clazz){
        Object type=null;
        try{
            ARRAY tmp = (ARRAY)obj;
            ResultSet rs = tmp.getResultSet();
            while(rs.next()){
                STRUCT struct = (STRUCT) rs.getObject(2);
                type = struct.toClass(clazz);
            }
        }catch(Exception e){
            //do nothing  just return null
            e.getMessage();
        }
        return type;
    }

    private List setList(Object obj, Class clazz){
        List lst = new ArrayList();
        try{
            Object resultElems = ((ARRAY)obj).getOracleArray();
            Datum[] listElems = (Datum[]) resultElems;
            for(int r=0;r<listElems.length;r++){
                STRUCT struct = (STRUCT) listElems[r];
                lst.add( struct.toClass(clazz));
            }

        }catch(Exception e){
            // do nothing just return null
            e.getMessage();
        }
        return lst;
    }

    public String getPartyNumberId() {
        return partyNumberId;
    }

    public void setPartyNumberId(String partyNumberId) {
        this.partyNumberId = partyNumberId;
    }

    public String getPartyFullName() {
        return partyFullName;
    }

    public void setPartyFullName(String partyFullName) {
        this.partyFullName = partyFullName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
