package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.sql.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/20/14
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
public class PartyClassificationType implements SQLData {
    protected String key;
    protected String classificationNumberId;
    protected String classificationCode;
    protected String subClassificationCode;
    protected String subTypeCode;
    protected EffectivePeriodType effectivePeriod;

    protected com.delphi_tech.ows.party.PartyClassificationType jaxbPartyClassification;

    public String sql_type;

    public PartyClassificationType() {
        jaxbPartyClassification = new com.delphi_tech.ows.party.PartyClassificationType();
    }

    public PartyClassificationType(String key,
                                   String classificationNumberId,
                                   String classificationCode,
                                   String subClassificationCode,
                                   String subTypeCode,
                                   EffectivePeriodType effectivePeriod) {
        this.key = key;
        this.classificationNumberId = classificationNumberId;
        this.classificationCode = classificationCode;
        this.subClassificationCode = subClassificationCode;
        this.subTypeCode = subTypeCode;
        this.effectivePeriod = effectivePeriod;

        jaxbPartyClassification = new com.delphi_tech.ows.party.PartyClassificationType();
        jaxbPartyClassification.setKey(this.key);
        jaxbPartyClassification.setClassificationNumberId(this.classificationNumberId);
        jaxbPartyClassification.setClassificationCode(this.classificationCode);
        jaxbPartyClassification.setSubClassificationCode(this.subClassificationCode);
        jaxbPartyClassification.setSubTypeCode(this.subTypeCode);
        if (this.effectivePeriod != null) {
            jaxbPartyClassification.setEffectivePeriod(this.effectivePeriod.getJaxbEffectivePeriod());
        } else {
            jaxbPartyClassification.setEffectivePeriod(new com.delphi_tech.ows.party.EffectivePeriodType());
        }

    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        key = stream.readString();
        classificationNumberId = stream.readString();
        classificationCode = stream.readString();
        subClassificationCode = stream.readString();
        subTypeCode = stream.readString();
        effectivePeriod = (EffectivePeriodType)setClass(stream.readObject(),EffectivePeriodType.class );

        jaxbPartyClassification.setKey((this.key == null) ? "" : this.key);
        jaxbPartyClassification.setClassificationNumberId((this.classificationNumberId == null) ? "" : this.classificationNumberId);
        jaxbPartyClassification.setClassificationCode((this.classificationCode == null) ? "" : this.classificationCode);
        jaxbPartyClassification.setSubClassificationCode((this.subClassificationCode == null) ? "" : this.subClassificationCode);
        jaxbPartyClassification.setSubTypeCode((this.subTypeCode == null) ? "" : this.subTypeCode);
        if (this.effectivePeriod != null) {
            jaxbPartyClassification.setEffectivePeriod(this.effectivePeriod.getJaxbEffectivePeriod());
        } else {
            jaxbPartyClassification.setEffectivePeriod(new com.delphi_tech.ows.party.EffectivePeriodType());
        }
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
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

    public com.delphi_tech.ows.party.PartyClassificationType getJaxbPartyClassification() {
        return jaxbPartyClassification;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassificationNumberId() {
        return classificationNumberId;
    }

    public void setClassificationNumberId(String classificationNumberId) {
        this.classificationNumberId = classificationNumberId;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public String getSubClassificationCode() {
        return subClassificationCode;
    }

    public void setSubClassificationCode(String subClassificationCode) {
        this.subClassificationCode = subClassificationCode;
    }

    public String getSubTypeCode() {
        return subTypeCode;
    }

    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    public EffectivePeriodType getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(EffectivePeriodType effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }
}
