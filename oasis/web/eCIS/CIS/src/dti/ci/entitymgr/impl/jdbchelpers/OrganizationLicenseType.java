package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.sql.*;

public class OrganizationLicenseType implements SQLData {

    protected String key;
    protected String licenseNumberId;
    protected String licenseNumber;
    protected LicensePeriodType licensePeriod;
    protected StateOrProvinceCodeType stateOrProvinceCode;
    protected SuspReinstInformationType suspReinstInformation;

    protected com.delphi_tech.ows.party.OrganizationLicenseType jaxbOrganizationLicenseType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }
    public OrganizationLicenseType(){
        jaxbOrganizationLicenseType = new com.delphi_tech.ows.party.OrganizationLicenseType();
    }

    public OrganizationLicenseType(String key,
                                   String licenseNumberId,
                                   String licenseNumber,
                                   LicensePeriodType licensePeriod,
                                   StateOrProvinceCodeType stateOrProvinceCode,
                                   SuspReinstInformationType suspReinstInformation){
        this.key = key;
        this.licenseNumberId = licenseNumberId;
        this.licenseNumber = licenseNumber;
        this.licensePeriod = licensePeriod;
        this.stateOrProvinceCode = stateOrProvinceCode;
        this.suspReinstInformation = suspReinstInformation;

        jaxbOrganizationLicenseType = new com.delphi_tech.ows.party.OrganizationLicenseType();
        jaxbOrganizationLicenseType.setKey(this.key);
        jaxbOrganizationLicenseType.setLicenseNumberId(this.licenseNumberId);
        jaxbOrganizationLicenseType.setLicenseNumber(this.licenseNumber);
        if(this.licensePeriod != null){
            jaxbOrganizationLicenseType.setLicensePeriod(this.licensePeriod.getJaxbLicensePeriodType());
        }else{
            jaxbOrganizationLicenseType.setLicensePeriod(new com.delphi_tech.ows.party.LicensePeriodType());
        }
        if(this.stateOrProvinceCode != null){
            jaxbOrganizationLicenseType.setStateOrProvinceCode(this.stateOrProvinceCode.getJaxbStateOrProvinceCodeType());
        }else{
            jaxbOrganizationLicenseType.setStateOrProvinceCode(new com.delphi_tech.ows.party.StateOrProvinceCodeType());
        }
        if(this.suspReinstInformation != null){
            jaxbOrganizationLicenseType.setSuspensionReinstatementInformation(
                    this.suspReinstInformation.getJaxbSuspensionReinstatementInformationType());
        }else{
            jaxbOrganizationLicenseType.setSuspensionReinstatementInformation(new com.delphi_tech.ows.party.SuspensionReinstatementInformationType());
        }
    }
    public void readSQL(SQLInput stream,String typeName ) throws SQLException{
        sql_type = typeName;
        this.key = stream.readString();
        this.licenseNumberId = stream.readString();
        this.licenseNumber = stream.readString();
        this.licensePeriod = (LicensePeriodType) setClass(stream.readObject(), LicensePeriodType.class);
        this.stateOrProvinceCode =  (StateOrProvinceCodeType)  setClass(stream.readObject(),StateOrProvinceCodeType.class);
        this.suspReinstInformation =
                (SuspReinstInformationType)
                        setClass(stream.readObject(), SuspReinstInformationType.class);

        jaxbOrganizationLicenseType.setKey((this.key == null) ? "":key);
        jaxbOrganizationLicenseType.setLicenseNumberId((licenseNumberId==null)?"":licenseNumberId);
        jaxbOrganizationLicenseType.setLicenseNumber((licenseNumber==null)?"":licenseNumber);
        if(licensePeriod != null){
            jaxbOrganizationLicenseType.setLicensePeriod(licensePeriod.getJaxbLicensePeriodType());
        } else{
            jaxbOrganizationLicenseType.setLicensePeriod(new com.delphi_tech.ows.party.LicensePeriodType());
        }

        if(stateOrProvinceCode != null){
            jaxbOrganizationLicenseType.setStateOrProvinceCode(stateOrProvinceCode.getJaxbStateOrProvinceCodeType());
        }else{
            jaxbOrganizationLicenseType.setStateOrProvinceCode( new com.delphi_tech.ows.party.StateOrProvinceCodeType());
        }
        if(suspReinstInformation != null ){
            jaxbOrganizationLicenseType.setSuspensionReinstatementInformation(suspReinstInformation.getJaxbSuspensionReinstatementInformationType());
        }else{
            jaxbOrganizationLicenseType.setSuspensionReinstatementInformation(new com.delphi_tech.ows.party.SuspensionReinstatementInformationType());
        }



    }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }
    public com.delphi_tech.ows.party.OrganizationLicenseType getJaxbOrganizationLicenseType(){
        return this.jaxbOrganizationLicenseType;
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
     * Gets the value of the licenseNumberId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLicenseNumberId() {
        return licenseNumberId;
    }

    /**
     * Sets the value of the licenseNumberId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLicenseNumberId(String value) {
        this.licenseNumberId = value;
    }
    /**
     * Gets the value of the licenseNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the value of the licenseNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLicenseNumber(String value) {
        this.licenseNumber = value;
    }

    /**
     * Gets the value of the licensePeriod property.
     *
     * @return
     *     possible object is
     *     {@link LicensePeriodType }
     *
     */
    public LicensePeriodType getLicensePeriod() {
        return licensePeriod;
    }

    /**
     * Sets the value of the licensePeriod property.
     *
     * @param value
     *     allowed object is
     *     {@link LicensePeriodType }
     *
     */
    public void setLicensePeriod(LicensePeriodType value) {
        this.licensePeriod = value;
    }

    /**
     * Gets the value of the stateOrProvinceCode property.
     *
     * @return
     *     possible object is
     *     {@link com.delphi_tech.ows.party.StateOrProvinceCodeType }
     *
     */
    public StateOrProvinceCodeType getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }

    /**
     * Sets the value of the stateOrProvinceCode property.
     *
     * @param value
     *     allowed object is
     *     {@link com.delphi_tech.ows.party.StateOrProvinceCodeType }
     *
     */
    public void setStateOrProvinceCode(StateOrProvinceCodeType value) {
        this.stateOrProvinceCode = value;
    }

    /**
     * Gets the value of the suspReinstInformation property.
     *
     * @return
     *     possible object is
     *     {@link SuspReinstInformationType }
     *
     */
    public SuspReinstInformationType getSuspReinstInformation() {
        return suspReinstInformation;
    }

    /**
     * Sets the value of the suspReinstInformation property.
     *
     * @param value
     *     allowed object is
     *     {@link SuspReinstInformationType }
     *
     */
    public void setSuspReinstInformation(SuspReinstInformationType value) {
        this.suspReinstInformation = value;
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