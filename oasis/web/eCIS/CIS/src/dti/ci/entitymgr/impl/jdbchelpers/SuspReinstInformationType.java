package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.sql.*;


public class SuspReinstInformationType implements SQLData {

    protected LicenseSuspensionPeriodType licenseSuspensionPeriod;
    protected String suspensionRevocationReasonCode;
    protected com.delphi_tech.ows.party.SuspensionReinstatementInformationType jaxbSuspensionReinstatementInformationType;
    public String sql_type;

    public String getSQLTypeName() throws SQLException {
            return sql_type;
    }

    public SuspReinstInformationType(){
        jaxbSuspensionReinstatementInformationType = new com.delphi_tech.ows.party.SuspensionReinstatementInformationType();
    }

    public SuspReinstInformationType(LicenseSuspensionPeriodType licenseSuspensionPeriod,
                                                  String suspensionRevocationReasonCode){
        this.licenseSuspensionPeriod = licenseSuspensionPeriod;
        this.suspensionRevocationReasonCode = suspensionRevocationReasonCode;

        jaxbSuspensionReinstatementInformationType = new com.delphi_tech.ows.party.SuspensionReinstatementInformationType();
        if(this.licenseSuspensionPeriod != null){
            jaxbSuspensionReinstatementInformationType.setLicenseSuspensionPeriod(this.licenseSuspensionPeriod.getJaxbLicenseSuspensionPeriodType());
        }
        jaxbSuspensionReinstatementInformationType.setSuspensionRevocationReasonCode(this.suspensionRevocationReasonCode);
    }
     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         licenseSuspensionPeriod = (LicenseSuspensionPeriodType)
                                    setClass(stream.readObject(),LicenseSuspensionPeriodType.class);

         suspensionRevocationReasonCode = stream.readString();


        jaxbSuspensionReinstatementInformationType = new com.delphi_tech.ows.party.SuspensionReinstatementInformationType();
        if(this.licenseSuspensionPeriod != null){
            jaxbSuspensionReinstatementInformationType.setLicenseSuspensionPeriod(this.licenseSuspensionPeriod.getJaxbLicenseSuspensionPeriodType());
        }else{
            jaxbSuspensionReinstatementInformationType.setLicenseSuspensionPeriod(new com.delphi_tech.ows.party.LicenseSuspensionPeriodType());
        }
        jaxbSuspensionReinstatementInformationType.setSuspensionRevocationReasonCode(this.suspensionRevocationReasonCode);
    }

    public  com.delphi_tech.ows.party.SuspensionReinstatementInformationType
                getJaxbSuspensionReinstatementInformationType(){
        return this.jaxbSuspensionReinstatementInformationType;
    }

    public void writeSQL(SQLOutput stream) throws SQLException{
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

    /**
     * Gets the value of the licenseSuspensionPeriod property.
     *
     * @return
     *     possible object is
     *     {@link LicenseSuspensionPeriodType }
     *
     */
    public LicenseSuspensionPeriodType getLicenseSuspensionPeriod() {
        return licenseSuspensionPeriod;
    }

    /**
     * Sets the value of the licenseSuspensionPeriod property.
     *
     * @param value
     *     allowed object is
     *     {@link LicenseSuspensionPeriodType }
     *
     */
    public void setLicenseSuspensionPeriod(LicenseSuspensionPeriodType value) {
        this.licenseSuspensionPeriod = value;
    }

    /**
     * Gets the value of the suspensionRevocationReasonCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuspensionRevocationReasonCode() {
        return suspensionRevocationReasonCode;
    }

    /**
     * Sets the value of the suspensionRevocationReasonCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuspensionRevocationReasonCode(String value) {
        this.suspensionRevocationReasonCode = value;
    }

}