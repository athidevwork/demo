package dti.ci.entitymgr.impl.jdbchelpers;



import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.*;


public class EducationalInstitutionType  implements SQLData {

    protected String institutionName;
    protected String countryCode;
    protected String city;
    protected StateOrProvinceCodeType stateOrProvinceCode;

    protected com.delphi_tech.ows.party.EducationalInstitutionType jaxbEducationInstitutionType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }

     public   EducationalInstitutionType(){
         jaxbEducationInstitutionType = new  com.delphi_tech.ows.party.EducationalInstitutionType();
     }

     public  EducationalInstitutionType(String institutionName,
                                        String countryCode,
                                        String city,
                                        StateOrProvinceCodeType stateOrProvinceCode){
         this.institutionName = institutionName;
         this.countryCode = countryCode;
         this.city = city;
         this.stateOrProvinceCode = stateOrProvinceCode;

         jaxbEducationInstitutionType = new  com.delphi_tech.ows.party.EducationalInstitutionType();
         jaxbEducationInstitutionType.setInstitutionName(this.institutionName);
         jaxbEducationInstitutionType.setCountryCode(this.countryCode);
         jaxbEducationInstitutionType.setCity(this.city);
         jaxbEducationInstitutionType.setStateOrProvinceCode(this.stateOrProvinceCode.getJaxbStateOrProvinceCodeType());

     }
      public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         institutionName = stream.readString();
         countryCode =  stream.readString();
         city = stream.readString();
         stateOrProvinceCode =   (StateOrProvinceCodeType) setClass(stream.readObject(),StateOrProvinceCodeType.class);


          jaxbEducationInstitutionType = new  com.delphi_tech.ows.party.EducationalInstitutionType();
          jaxbEducationInstitutionType.setInstitutionName((this.institutionName==null)?"":institutionName);
          jaxbEducationInstitutionType.setCountryCode((this.countryCode==null)?"":countryCode);
          jaxbEducationInstitutionType.setCity((this.city==null)?"":city);
           if(stateOrProvinceCode != null){
               jaxbEducationInstitutionType.setStateOrProvinceCode(this.stateOrProvinceCode.getJaxbStateOrProvinceCodeType());
           }else{
                jaxbEducationInstitutionType.setStateOrProvinceCode(new com.delphi_tech.ows.party.StateOrProvinceCodeType());
           }
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
    public com.delphi_tech.ows.party.EducationalInstitutionType getJaxbEducationInstitutionType(){
        return this.jaxbEducationInstitutionType;
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
     * Gets the value of the institutionName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Sets the value of the institutionName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstitutionName(String value) {
        this.institutionName = value;
    }

    /**
     * Gets the value of the countryCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the city property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCity(String value) {
        this.city = value;
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

}