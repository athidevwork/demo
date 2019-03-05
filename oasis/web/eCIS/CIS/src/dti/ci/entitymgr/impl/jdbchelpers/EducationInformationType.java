package dti.ci.entitymgr.impl.jdbchelpers;

import com.delphi_tech.ows.party.*;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.*;


public class EducationInformationType implements SQLData {

    protected String key;
    protected String educationInformationNumberId;
    protected String educationTypeCode;
    protected String educationDegree;
    protected String graduationYear;
    protected EducationalInstitutionType educationalInstitution;

    protected com.delphi_tech.ows.party.EducationInformationType jaxbEducationInformationType;

        public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }

     public   EducationInformationType(){
         jaxbEducationInformationType = new  com.delphi_tech.ows.party.EducationInformationType();
     }

     public  EducationInformationType(String key,
                                      String educationInformationNumberId,
                                        String educationTypeCode,
                                        String educationDegree,
                                        String graduationYear,
                                        EducationalInstitutionType educationalInstitution){

         this.key = key;
         this.educationInformationNumberId = educationInformationNumberId;
         this.educationTypeCode = educationTypeCode;
         this.educationDegree = educationDegree;
         this.graduationYear = graduationYear;
         this.educationalInstitution = educationalInstitution;

         jaxbEducationInformationType = new  com.delphi_tech.ows.party.EducationInformationType();
         jaxbEducationInformationType.setKey(this.key);
         jaxbEducationInformationType.setEducationInformationNumberId(this.educationInformationNumberId);
         jaxbEducationInformationType.setEducationTypeCode(this.educationTypeCode);
         jaxbEducationInformationType.setEducationDegree(this.educationDegree);
         jaxbEducationInformationType.setGraduationYear(this.graduationYear);
         jaxbEducationInformationType.setEducationalInstitution(this.educationalInstitution.getJaxbEducationInstitutionType());

     }
      public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         key = stream.readString();
         educationInformationNumberId = stream.readString();
         educationTypeCode =  stream.readString();
         educationDegree = stream.readString();
         graduationYear = stream.readString();
         educationalInstitution =   (EducationalInstitutionType) setClass(stream.readObject(),EducationalInstitutionType.class);


          jaxbEducationInformationType = new  com.delphi_tech.ows.party.EducationInformationType();
          jaxbEducationInformationType.setKey((this.key == null) ? "":key);
          jaxbEducationInformationType.setEducationInformationNumberId((this.educationInformationNumberId==null)?"":educationInformationNumberId);
          jaxbEducationInformationType.setEducationTypeCode((this.educationTypeCode==null)?"":educationTypeCode);
          jaxbEducationInformationType.setEducationDegree((this.educationDegree==null)?"":educationDegree);
          jaxbEducationInformationType.setGraduationYear((this.graduationYear==null)?"":graduationYear);
           if(educationalInstitution != null){
               jaxbEducationInformationType.setEducationalInstitution(this.educationalInstitution.getJaxbEducationInstitutionType());
           } else {
               jaxbEducationInformationType.setEducationalInstitution(new com.delphi_tech.ows.party.EducationalInstitutionType());
           }
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
    public com.delphi_tech.ows.party.EducationInformationType getJaxbEducationInformationType(){
        return this.jaxbEducationInformationType;
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
     * Gets the value of the educationInformationNumberId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEducationInformationNumberId() {
        return educationInformationNumberId;
    }

    /**
     * Sets the value of the educationInformationNumberId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEducationInformationNumberId(String value) {
        this.educationInformationNumberId = value;
    }

    /**
     * Gets the value of the educationTypeCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEducationTypeCode() {
        return educationTypeCode;
    }

    /**
     * Sets the value of the educationTypeCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEducationTypeCode(String value) {
        this.educationTypeCode = value;
    }

    /**
     * Gets the value of the educationDegree property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEducationDegree() {
        return educationDegree;
    }

    /**
     * Sets the value of the educationDegree property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEducationDegree(String value) {
        this.educationDegree = value;
    }

    /**
     * Gets the value of the graduationYear property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGraduationYear() {
        return graduationYear;
    }

    /**
     * Sets the value of the graduationYear property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGraduationYear(String value) {
        this.graduationYear = value;
    }

    /**
     * Gets the value of the educationalInstitution property.
     *
     * @return
     *     possible object is
     *     {@link EducationalInstitutionType }
     *
     */
    public EducationalInstitutionType getEducationalInstitution() {
        return educationalInstitution;
    }

    /**
     * Sets the value of the educationalInstitution property.
     *
     * @param value
     *     allowed object is
     *     {@link EducationalInstitutionType }
     *
     */
    public void setEducationalInstitution(EducationalInstitutionType value) {
        this.educationalInstitution = value;
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