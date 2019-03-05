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
public class RelationshipType implements SQLData {

    protected String key;
    protected String relationshipNumberId;
    protected String relationshipType;
    protected EffectivePeriodType effectivePeriod;
    protected ReferredPartyType referredParty;
    protected String primaryIndicator;
    protected String reverseRelationshipIndicator;

    protected com.delphi_tech.ows.party.RelationshipType jaxbRelationshipType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    public RelationshipType(){
        jaxbRelationshipType = new com.delphi_tech.ows.party.RelationshipType();
    }

    public RelationshipType(String key,
                            String relationshipNumberId,
                            String relationshipType,
                            EffectivePeriodType effectivePeriod,
                            ReferredPartyType referredParty,
                            String primaryIndicator,
                            String reverseRelationshipIndicator
                           ){
        this.key = key;
        this.relationshipNumberId = relationshipNumberId;
        this.relationshipType = relationshipType;
        this.effectivePeriod = effectivePeriod;
        this.referredParty = referredParty;
        this.primaryIndicator = primaryIndicator;
        this.reverseRelationshipIndicator = reverseRelationshipIndicator;

        jaxbRelationshipType = new com.delphi_tech.ows.party.RelationshipType();
        jaxbRelationshipType.setKey((this.relationshipNumberId == null) ? "" : relationshipNumberId);
        jaxbRelationshipType.setRelationshipNumberId((this.relationshipNumberId == null) ? "" : relationshipNumberId);
        jaxbRelationshipType.setRelationshipType((this.relationshipType == null) ? "" : this.relationshipType);
        if (this.effectivePeriod != null) {
            jaxbRelationshipType.setEffectivePeriod(this.effectivePeriod.getJaxbEffectivePeriod());

        } else {
            jaxbRelationshipType.setEffectivePeriod(new com.delphi_tech.ows.party.EffectivePeriodType());
        }
        if(this.referredParty != null){
            jaxbRelationshipType.setReferredParty(this.referredParty.getJaxbReferredPartyType());
        }else{
            jaxbRelationshipType.setReferredParty(new com.delphi_tech.ows.party.ReferredPartyType());
        }
        jaxbRelationshipType.setPrimaryIndicator((this.primaryIndicator == null) ? "" : primaryIndicator);
        jaxbRelationshipType.setReverseRelationshipIndicator((this.reverseRelationshipIndicator == null) ? "" : this.reverseRelationshipIndicator);
    }

    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        key = stream.readString();
        relationshipNumberId = stream.readString();
        relationshipType = stream.readString();
        effectivePeriod = (EffectivePeriodType)setClass(stream.readObject(), EffectivePeriodType.class);
        referredParty = (ReferredPartyType)setClass(stream.readObject(),ReferredPartyType.class);
        primaryIndicator = stream.readString();
        reverseRelationshipIndicator = stream.readString();

        jaxbRelationshipType.setKey((this.relationshipNumberId == null) ? "" : relationshipNumberId);
        jaxbRelationshipType.setRelationshipNumberId((this.relationshipNumberId == null) ? "" : relationshipNumberId);
        jaxbRelationshipType.setRelationshipType((this.relationshipType == null) ? "" : this.relationshipType);
        if (this.effectivePeriod != null) {
            jaxbRelationshipType.setEffectivePeriod(this.effectivePeriod.getJaxbEffectivePeriod());
        } else {
            jaxbRelationshipType.setEffectivePeriod(new com.delphi_tech.ows.party.EffectivePeriodType());
        }
        if(this.referredParty != null){
            jaxbRelationshipType.setReferredParty(this.referredParty.getJaxbReferredPartyType());
        }else{
            jaxbRelationshipType.setReferredParty(new com.delphi_tech.ows.party.ReferredPartyType());
        }
        jaxbRelationshipType.setPrimaryIndicator((this.primaryIndicator == null) ? "" : primaryIndicator);
        jaxbRelationshipType.setReverseRelationshipIndicator((this.reverseRelationshipIndicator == null) ? "" : this.reverseRelationshipIndicator);
    }

    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public com.delphi_tech.ows.party.RelationshipType getJaxbRelationshipType(){
        return jaxbRelationshipType;
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

    public String getRelationshipNumberId() {
        return relationshipNumberId;
    }

    public void setRelationshipNumberId(String relationshipNumberId) {
        this.relationshipNumberId = relationshipNumberId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public EffectivePeriodType getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(EffectivePeriodType effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    public ReferredPartyType getReferredParty() {
        return referredParty;
    }

    public void setReferredParty(ReferredPartyType referredParty) {
        this.referredParty = referredParty;
    }

    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    public void setPrimaryIndicator(String primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }

    public String getReverseRelationshipIndicator() {
        return reverseRelationshipIndicator;
    }

    public void setReverseRelationshipIndicator(String reverseRelationshipIndicator) {
        this.reverseRelationshipIndicator = reverseRelationshipIndicator;
    }
}
