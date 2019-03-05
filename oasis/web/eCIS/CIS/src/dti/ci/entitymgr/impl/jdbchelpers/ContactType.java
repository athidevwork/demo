package dti.ci.entitymgr.impl.jdbchelpers;


import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.sql.*;


public class ContactType implements SQLData {

    protected String key;
    protected PersonNameType personName;
    protected BusinessEmailType businessEmail;
    protected BasicPhoneNumberType basicPhoneNumber;
    protected String contactNumberId;
    protected com.delphi_tech.ows.party.ContactType jaxbContactType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }

    public ContactType(){
        jaxbContactType = new com.delphi_tech.ows.party.ContactType();
    }

    public ContactType(String key,
                       String contactNumberId,
                       PersonNameType personName,
                       BusinessEmailType businessEmail,
                       BasicPhoneNumberType basicPhoneNumber
                       ){
        this.key = key;
        this.personName = personName;
        this.businessEmail =  businessEmail;
        this.basicPhoneNumber = basicPhoneNumber;
        this.contactNumberId = contactNumberId;
        jaxbContactType = new com.delphi_tech.ows.party.ContactType();
        jaxbContactType.setKey(this.key);
        jaxbContactType.setPersonName(this.personName.getJaxbPersonNameType());
        jaxbContactType.setBusinessEmail(this.businessEmail.getJaxbBusinessEmailType());
        jaxbContactType.setBasicPhoneNumber(this.basicPhoneNumber.getJaxbBasicPhoneNumberType());
        jaxbContactType.setContactNumberId(this.contactNumberId);


    }
        public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         key = stream.readString();
         contactNumberId = stream.readString();
         personName = (PersonNameType)setClass(stream.readObject(),PersonNameType.class);
         businessEmail =  (BusinessEmailType) setClass(stream.readObject(),BusinessEmailType.class);
         basicPhoneNumber =   (BasicPhoneNumberType) setClass(stream.readObject(),BasicPhoneNumberType.class);

           jaxbContactType.setKey((this.key == null) ? "":key);
           jaxbContactType.setContactNumberId((this.contactNumberId ==null)? "" : contactNumberId);
           if(personName != null){
               jaxbContactType.setPersonName(this.personName.getJaxbPersonNameType());
           }else{
               jaxbContactType.setPersonName(new com.delphi_tech.ows.party.PersonNameType());
           }
           if(businessEmail != null){
               jaxbContactType.setBusinessEmail(this.businessEmail.getJaxbBusinessEmailType());
           }else{
               jaxbContactType.setBusinessEmail(new com.delphi_tech.ows.party.BusinessEmailType());
           }
           if(basicPhoneNumber != null){
               jaxbContactType.setBasicPhoneNumber(this.basicPhoneNumber.getJaxbBasicPhoneNumberType());
           }else{
               jaxbContactType.setBasicPhoneNumber( new com.delphi_tech.ows.party.BasicPhoneNumberType());
           }

     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
    public com.delphi_tech.ows.party.ContactType getJaxbContactType(){
        return this.jaxbContactType;
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
     * Gets the value of the personName property.
     *
     * @return
     *     possible object is
     *     {@link com.delphi_tech.ows.party.PersonNameType }
     *
     */
    public PersonNameType getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     *
     * @param value
     *     allowed object is
     *     {@link com.delphi_tech.ows.party.PersonNameType }
     *
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the businessEmail property.
     *
     * @return
     *     possible object is
     *     {@link com.delphi_tech.ows.party.BusinessEmailType }
     *
     */
    public BusinessEmailType getBusinessEmail() {
        return businessEmail;
    }

    /**
     * Sets the value of the businessEmail property.
     *
     * @param value
     *     allowed object is
     *     {@link com.delphi_tech.ows.party.BusinessEmailType }
     *
     */
    public void setBusinessEmail(BusinessEmailType value) {
        this.businessEmail = value;
    }

    /**
     * Gets the value of the basicPhoneNumber property.
     *
     * @return
     *     possible object is
     *     {@link com.delphi_tech.ows.party.BasicPhoneNumberType }
     *
     */
    public BasicPhoneNumberType getBasicPhoneNumber() {
        return basicPhoneNumber;
    }

    /**
     * Sets the value of the basicPhoneNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link com.delphi_tech.ows.party.BasicPhoneNumberType }
     *
     */
    public void setBasicPhoneNumber(BasicPhoneNumberType value) {
        this.basicPhoneNumber = value;
    }

    /**
     * Gets the value of the key property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContactNumberId() {
        return contactNumberId;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContactNumberId(String value) {
        this.contactNumberId = value;
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