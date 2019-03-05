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
public class PartyNoteType implements SQLData {

    protected String key;
    protected String partyNoteNumberId;
    protected String partyNoteType;
    protected String partyNoteDateTime;
    protected String partyNoteStatus;
    protected String note;

    protected com.delphi_tech.ows.party.PartyNoteType jaxbPartyNoteType;

    public String sql_type;

    
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    public PartyNoteType() {
        jaxbPartyNoteType = new com.delphi_tech.ows.party.PartyNoteType();    
    }
    
    public PartyNoteType(String key,
                         String partyNoteNumberId,
                         String partyNoteType,
                         String partyNoteDateTime,
                         String partyNoteStatus,
                         String note) {
        this.key = key;
        this.partyNoteNumberId = partyNoteNumberId;
        this.partyNoteType = partyNoteType;
        this.partyNoteDateTime = partyNoteDateTime;
        this.partyNoteStatus = partyNoteStatus;
        this.note = note;

        jaxbPartyNoteType = new com.delphi_tech.ows.party.PartyNoteType();
        jaxbPartyNoteType.setKey((this.key == null) ? "" : key);
        jaxbPartyNoteType.setPartyNoteNumberId((this.partyNoteNumberId==null)?"":partyNoteNumberId);
        jaxbPartyNoteType.setPartyNoteType((this.partyNoteType==null)?"":partyNoteType);
        jaxbPartyNoteType.setPartyNoteDateTime((this.partyNoteDateTime==null)?"":partyNoteDateTime);
        jaxbPartyNoteType.setPartyNoteStatus((this.partyNoteStatus==null)?"":partyNoteStatus);
        jaxbPartyNoteType.setNote((this.note==null)?"":note);
    }

    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        key = stream.readString();
        partyNoteNumberId = stream.readString();
        partyNoteType = stream.readString();
        partyNoteDateTime = stream.readString();
        partyNoteStatus = stream.readString();
        note = stream.readString();

        jaxbPartyNoteType.setKey((this.key == null) ? "" : key);
        jaxbPartyNoteType.setPartyNoteNumberId((this.partyNoteNumberId==null)?"":partyNoteNumberId);
        jaxbPartyNoteType.setPartyNoteType((this.partyNoteType==null)?"":partyNoteType);
        jaxbPartyNoteType.setPartyNoteDateTime((this.partyNoteDateTime==null)?"":partyNoteDateTime);
        jaxbPartyNoteType.setPartyNoteStatus((this.partyNoteStatus==null)?"":partyNoteStatus);
        jaxbPartyNoteType.setNote((this.note==null)?"":note);
    }

    public void writeSQL(SQLOutput stream) throws SQLException {
       /*
       This method should not have implementation
       We are not planning to make any updates using this method.
       */
    }

    public com.delphi_tech.ows.party.PartyNoteType getJaxbPartyNoteType(){
        return jaxbPartyNoteType;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPartyNoteNumberId() {
        return partyNoteNumberId;
    }

    public void setPartyNoteNumberId(String partyNoteNumberId) {
        this.partyNoteNumberId = partyNoteNumberId;
    }

    public String getPartyNoteType() {
        return partyNoteType;
    }

    public void setPartyNoteType(String partyNoteType) {
        this.partyNoteType = partyNoteType;
    }

    public String getPartyNoteDateTime() {
        return partyNoteDateTime;
    }

    public void setPartyNoteDateTime(String partyNoteDateTime) {
        this.partyNoteDateTime = partyNoteDateTime;
    }

    public String getPartyNoteStatus() {
        return partyNoteStatus;
    }

    public void setPartyNoteStatus(String partyNoteStatus) {
        this.partyNoteStatus = partyNoteStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
