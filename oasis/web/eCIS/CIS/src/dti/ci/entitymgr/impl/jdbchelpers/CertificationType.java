package dti.ci.entitymgr.impl.jdbchelpers;

import dti.oasis.busobjs.YesNoEmptyFlag;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.sql.*;


public class CertificationType  implements SQLData {

    protected String key;
    protected String certificationNumberId;
    protected String certificationCode;
    protected String certifiedIndicator;
    protected CertificationPeriodType certificationPeriod;
    protected String certificationBoard;
    protected String specialty;
    protected CtlStateOrProvinceCodeType ctlStateOrProvinceCode;

    protected com.delphi_tech.ows.party.CertificationType jaxbCertificationType;
        public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }
    public  CertificationType(){
         jaxbCertificationType = new   com.delphi_tech.ows.party.CertificationType();
    }

    public CertificationType(
               String key,
               String certificationNumberId,
               String certificationCode,
               String certifiedIndicator,
               CertificationPeriodType certificationPeriod,
               String certificationBoard,
               String specialty,
               CtlStateOrProvinceCodeType ctlStateOrProvinceCode
            )
    {
      this.key = key;
      this.certificationNumberId = certificationNumberId;
      this.certificationCode = certificationCode;
      this.certifiedIndicator = certifiedIndicator;
      this.certificationPeriod =certificationPeriod;
      this.certificationBoard  =certificationBoard;
      this.specialty = specialty;
      this.ctlStateOrProvinceCode = ctlStateOrProvinceCode;
        jaxbCertificationType = new   com.delphi_tech.ows.party.CertificationType();
        jaxbCertificationType.setKey(this.key);
        jaxbCertificationType.setCertificationNumberId(this.certificationNumberId);
        jaxbCertificationType.setCertificationCode(this.certificationCode);
        jaxbCertificationType.setCertifiedIndicator(this.getCertifiedIndicator());
        if(this.certificationPeriod != null){
            jaxbCertificationType.setCertificationPeriod(this.certificationPeriod.getJaxbCertificationPeriodType());
        }else{
            jaxbCertificationType.setCertificationPeriod(new com.delphi_tech.ows.party.CertificationPeriodType());
        }
        jaxbCertificationType.setCertificationBoard(this.certificationBoard);
        jaxbCertificationType.setSpecialty(this.specialty);
        if(this.ctlStateOrProvinceCode != null){
            jaxbCertificationType.setControllingStateOrProvinceCode(this.ctlStateOrProvinceCode.getJaxbControllingStateOrProvinceCodeType());
        }else{
           jaxbCertificationType.setControllingStateOrProvinceCode(new com.delphi_tech.ows.party.ControllingStateOrProvinceCodeType());
        }


    }

     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         this.key = stream.readString();
         this.certificationNumberId  = stream.readString();
         this.certificationCode = stream.readString();
         this.certifiedIndicator = stream.readString();
         this.certificationPeriod = (CertificationPeriodType) setClass(stream.readObject(),CertificationPeriodType.class);
         this.certificationBoard = stream.readString();
         this.specialty = stream.readString();
         this.ctlStateOrProvinceCode = (CtlStateOrProvinceCodeType) setClass(stream.readObject(),CtlStateOrProvinceCodeType.class);

             jaxbCertificationType.setKey((this.key == null) ? "":key);
             jaxbCertificationType.setCertificationNumberId((this.certificationNumberId==null)?"":certificationNumberId);
             jaxbCertificationType.setCertificationCode((this.certificationCode==null)?"":certificationCode);
             jaxbCertificationType.setCertifiedIndicator((this.certifiedIndicator==null)?"":getCertifiedIndicator());
             if(this.certificationPeriod != null){
                 jaxbCertificationType.setCertificationPeriod(this.certificationPeriod.getJaxbCertificationPeriodType());
             } else{
                 jaxbCertificationType.setCertificationPeriod(new com.delphi_tech.ows.party.CertificationPeriodType());
             }
             jaxbCertificationType.setCertificationBoard(this.certificationBoard);
             jaxbCertificationType.setSpecialty((this.specialty==null)?"":specialty);
             if(this.ctlStateOrProvinceCode != null){
                 jaxbCertificationType.setControllingStateOrProvinceCode(this.ctlStateOrProvinceCode.getJaxbControllingStateOrProvinceCodeType());
             }
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }

    public com.delphi_tech.ows.party.CertificationType getJaxbCertificationType(){
        return this.jaxbCertificationType;
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
     * Gets the value of the certificatioNumberId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCertificationNumberId() {
        return certificationNumberId;
    }

    /**
     * Sets the value of the certificatioNumberId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCertificationNumberId(String value) {
        this.certificationNumberId = value;
    }

    /**
     * Gets the value of the certificationCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCertificationCode() {
        return certificationCode;
    }

    /**
     * Sets the value of the certificationCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCertificationCode(String value) {
        this.certificationCode = value;
    }

    /**
     * Gets the value of the certifiedIndicator property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCertifiedIndicator() {
        return YesNoEmptyFlag.getInstance(certifiedIndicator).trueFalseEmptyValue();
    }

    /**
     * Sets the value of the certifiedIndicator property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCertifiedIndicator(String value) {
        this.certifiedIndicator = value;
    }

    /**
     * Gets the value of the certificationPeriod property.
     *
     * @return
     *     possible object is
     *     {@link CertificationPeriodType }
     *
     */
    public CertificationPeriodType getCertificationPeriod() {
        return certificationPeriod;
    }

    /**
     * Sets the value of the certificationPeriod property.
     *
     * @param value
     *     allowed object is
     *     {@link CertificationPeriodType }
     *
     */
    public void setCertificationPeriod(CertificationPeriodType value) {
        this.certificationPeriod = value;
    }

    /**
     * Gets the value of the certificationBoard property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCertificationBoard() {
        return certificationBoard;
    }

    /**
     * Sets the value of the certificationBoard property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCertificationBoard(String value) {
        this.certificationBoard = value;
    }

    /**
     * Gets the value of the specialty property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the value of the specialty property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpecialty(String value) {
        this.specialty = value;
    }

    /**
     * Gets the value of the ctlStateOrProvinceCode property.
     *
     * @return
     *     possible object is
     *     {@link CtlStateOrProvinceCodeType }
     *
     */
    public CtlStateOrProvinceCodeType getCtlStateOrProvinceCode() {
        return ctlStateOrProvinceCode;
    }

    /**
     * Sets the value of the ctlStateOrProvinceCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CtlStateOrProvinceCodeType }
     *
     */
    public void setCtlStateOrProvinceCode(CtlStateOrProvinceCodeType value) {
        this.ctlStateOrProvinceCode = value;
    }

    /**
     * Gets the value of the key property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKey(String value) {
        this.key = value;
    }

}