package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class PersonType implements SQLData {

    protected String personNumberId;
    protected String clientId;
    protected String externalReferenceId;
    protected String externalDataId;
    protected String key;
    protected PersonNameType personName;
    protected String nationalProviderId;
    protected String genderCode;
    protected String socialSecurityNumberId;
    protected String feinId;
    protected String birthDate;
    protected String insuredSinceDate;
    protected String sicCode;
    protected String webAddress1;
    protected String webAddress2;
    protected String webAddress3;
    protected List<BusinessEmailType> businessEmail;
    protected List<BasicPhoneNumberType> basicPhoneNumber;
    protected List<BasicAddressType> basicAddress;
    protected List<EducationInformationType> educationInformation;
    protected List<ProfessionalLicenseType> professionalLicense;
    protected List<CertificationType> certification;
    protected List<ContactType> contact;
    protected List<PartyNoteType> partyNote;
    protected List<RelationshipType> relationship;
    protected List<PartyClassificationType> partyClassification;

    protected com.delphi_tech.ows.party.PersonType jaxbPersonType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }

    public PersonType(){
        jaxbPersonType = new com.delphi_tech.ows.party.PersonType();
    }

    public PersonType(String personNumberId,String clientId, String externalReferenceId, String key,PersonNameType personName,
                        String nationalProviderId,
                        String externalDataId,
                        String genderCode,
                        String socialSecurityNumberId,
                        String feinId,
                        String birthDate,
                        String insuredSinceDate,
                        String sicCode,
                        String webAddress1,
                        String webAddress2,
                        String webAddress3,
                        BusinessEmailType businessEmail,
                        BasicPhoneNumberType basicPhoneNumber,
                        BasicAddressType basicAddress,
                        EducationInformationType educationInformation,
                        ProfessionalLicenseType professionalLicense,
                        CertificationType certification,
                        ContactType contact,
                        PartyNoteType partyNote,
                        RelationshipType relationship,
                        PartyClassificationType partyClassification){

      this.personNumberId = personNumberId;
      this.clientId = clientId;
      this.externalReferenceId = externalReferenceId;
      this.externalDataId = externalDataId;
      this.key = key;
      this.personName = personName;
      this.nationalProviderId = nationalProviderId;
      this.genderCode = genderCode;
      this.socialSecurityNumberId = socialSecurityNumberId;
      this.feinId = feinId;
      this.birthDate = birthDate;
      this.insuredSinceDate = insuredSinceDate;
      this.webAddress1 = webAddress1;
      this.webAddress2 = webAddress2;
      this.webAddress3 = webAddress3;
      this.getBusinessEmail().add(businessEmail);
      this.getBasicPhoneNumber().add(basicPhoneNumber);
      this.getBasicAddress().add(basicAddress);
      this.getEducationInformation().add(educationInformation);
      this.getProfessionalLicense().add(professionalLicense);
      this.getCertification().add(certification);
      this.getContact().add(contact);
      this.getPartyNote().add(partyNote);
      this.getRelationship().add(relationship);
      this.getPartyClassification().add(partyClassification);

        jaxbPersonType = new com.delphi_tech.ows.party.PersonType();
        jaxbPersonType.setPersonNumberId((this.personNumberId==null)?"":personNumberId);
        jaxbPersonType.setClientId((this.clientId == null) ? "" : this.clientId);
        jaxbPersonType.setExternalReferenceId((this.externalReferenceId == null) ? "" : externalReferenceId);
        jaxbPersonType.setExternalDataId(this.externalDataId == null ? "" : externalDataId);
        jaxbPersonType.setKey((this.key==null)?"":this.key);
        jaxbPersonType.setGenderCode((this.genderCode==null)?"":this.genderCode);
        jaxbPersonType.setSocialSecurityNumberId((this.socialSecurityNumberId==null)?"":this.socialSecurityNumberId);
        jaxbPersonType.setFEINId((this.feinId==null)?"":this.feinId);
        jaxbPersonType.setBirthDate((this.birthDate==null)?"":this.birthDate);
        jaxbPersonType.setInsuredSinceDate((this.insuredSinceDate==null)?"":this.insuredSinceDate);
        jaxbPersonType.setSicCode((this.sicCode==null)?"":this.sicCode);
        jaxbPersonType.setWebAddress1((this.webAddress1==null)?"":this.webAddress1);
        jaxbPersonType.setWebAddress2((this.webAddress2==null)?"":this.webAddress2);
        jaxbPersonType.setWebAddress3((this.webAddress3==null)?"":this.webAddress3);
        jaxbPersonType.setNationalProviderId(this.nationalProviderId);
        if(this.personName !=null){
            jaxbPersonType.setPersonName(this.personName.getJaxbPersonNameType());
        }else{
            jaxbPersonType.setPersonName(new com.delphi_tech.ows.party.PersonNameType());
        }

        jaxbPersonType.getBusinessEmail().add(businessEmail.getJaxbBusinessEmailType());
        jaxbPersonType.getBasicPhoneNumber().add(basicPhoneNumber.getJaxbBasicPhoneNumberType());
        jaxbPersonType.getBasicAddress().add(basicAddress.getJaxbBasicAddressType());
        jaxbPersonType.getEducationInformation().add(educationInformation.getJaxbEducationInformationType());
        jaxbPersonType.getProfessionalLicense().add(professionalLicense.getJaxbProfessionalLicenseType());
        jaxbPersonType.getCertification().add(certification.getJaxbCertificationType());
        jaxbPersonType.getContact().add(contact.getJaxbContactType());
        jaxbPersonType.getPartyNote().add(partyNote.getJaxbPartyNoteType());
        jaxbPersonType.getRelationship().add(relationship.getJaxbRelationshipType());
        jaxbPersonType.getPartyClassification().add(partyClassification.getJaxbPartyClassification());

    }
    public void readSQL(SQLInput stream,String typeName ) throws SQLException{
            sql_type = typeName;
            personNumberId = stream.readString();
            clientId = stream.readString();
            externalReferenceId = stream.readString();
            nationalProviderId = stream.readString();
            externalDataId = stream.readNString();
            key = stream.readString();
            personName = (PersonNameType) setClass(stream.readObject(),PersonNameType.class );
            genderCode = stream.readString();
            socialSecurityNumberId = stream.readString();
            feinId = stream.readString();
            birthDate = stream.readString();
            insuredSinceDate = stream.readString();
            sicCode = stream.readString();
            webAddress1 = stream.readString();
            webAddress2 = stream.readString();
            webAddress3 = stream.readString();
            this.businessEmail=  (List) setList(stream.readObject(),BusinessEmailType.class );
            this.basicPhoneNumber = (List) setList(stream.readObject(),BasicPhoneNumberType.class );
            this.basicAddress = (List) setList(stream.readObject(),BasicAddressType.class );
            this.educationInformation = (List)setList(stream.readObject(), EducationInformationType.class);
        this.professionalLicense = (List)setList(stream.readObject(), ProfessionalLicenseType.class);
        this.certification = (List)setList(stream.readObject(), CertificationType.class);
        this.contact = (List)setList(stream.readObject(), ContactType.class);
        this.partyNote = (List)setList(stream.readObject(), PartyNoteType.class);
        this.relationship = (List)setList(stream.readObject(), RelationshipType.class);
        this.partyClassification = (List)setList(stream.readObject(), PartyClassificationType.class);

        jaxbPersonType.setPersonNumberId((this.personNumberId==null)?"":personNumberId);
        jaxbPersonType.setClientId((this.clientId == null) ? "" : this.clientId);
        jaxbPersonType.setExternalReferenceId((this.externalReferenceId == null) ? "" : this.externalReferenceId);
        jaxbPersonType.setExternalDataId(this.externalDataId == null ? "" : this.externalDataId);
        jaxbPersonType.setKey((this.key==null)?"":this.key);
        jaxbPersonType.setGenderCode((this.genderCode==null)?"":this.genderCode);
        jaxbPersonType.setSocialSecurityNumberId((this.socialSecurityNumberId==null)?"":this.socialSecurityNumberId);
        jaxbPersonType.setFEINId((this.feinId==null)?"":this.feinId);
        jaxbPersonType.setBirthDate((this.birthDate==null)?"":this.birthDate);
        jaxbPersonType.setInsuredSinceDate((this.insuredSinceDate==null)?"":this.insuredSinceDate);
        jaxbPersonType.setSicCode((this.sicCode==null)?"":this.sicCode);
        jaxbPersonType.setWebAddress1((this.webAddress1==null)?"":this.webAddress1);
        jaxbPersonType.setWebAddress2((this.webAddress1==null)?"":this.webAddress2);
        jaxbPersonType.setWebAddress3((this.webAddress1==null)?"":this.webAddress3);
        jaxbPersonType.setNationalProviderId(this.nationalProviderId == null ? "" : nationalProviderId);
        if(this.personName != null){
            jaxbPersonType.setPersonName(this.personName.getJaxbPersonNameType());
        }else{
            jaxbPersonType.setPersonName(new com.delphi_tech.ows.party.PersonNameType());
        }

        Iterator it = professionalLicense.iterator();
        while(it.hasNext()){
         jaxbPersonType.getProfessionalLicense().add(((ProfessionalLicenseType)it.next()).getJaxbProfessionalLicenseType()) ;
        }
        if(jaxbPersonType.getProfessionalLicense() == null){
           jaxbPersonType.getProfessionalLicense().add(new com.delphi_tech.ows.party.ProfessionalLicenseType());
        }

        it =certification.iterator();
        while(it.hasNext()){
         jaxbPersonType.getCertification().add(((CertificationType)it.next()).getJaxbCertificationType()) ;
        }
        if(jaxbPersonType.getCertification() == null){
          jaxbPersonType.getCertification().add( new   com.delphi_tech.ows.party.CertificationType());
        }

        it =contact.iterator();
        while(it.hasNext()){
         jaxbPersonType.getContact().add(((ContactType)it.next()).getJaxbContactType()) ;
        }
        if(jaxbPersonType.getContact() == null){
           jaxbPersonType.getContact().add(new com.delphi_tech.ows.party.ContactType());
        }

        it =partyNote.iterator();
        while(it.hasNext()){
            jaxbPersonType.getPartyNote().add(((PartyNoteType)it.next()).getJaxbPartyNoteType()) ;
        }
        if(jaxbPersonType.getPartyNote() == null){
            jaxbPersonType.getPartyNote().add(new com.delphi_tech.ows.party.PartyNoteType());
        }

        it =businessEmail.iterator();
        while(it.hasNext()){
         jaxbPersonType.getBusinessEmail().add(((BusinessEmailType)it.next()).getJaxbBusinessEmailType()) ;
        }
        if(jaxbPersonType.getBusinessEmail() == null){
          jaxbPersonType.getBusinessEmail().add(new com.delphi_tech.ows.party.BusinessEmailType());
        }

        it =basicPhoneNumber.iterator();
        while(it.hasNext()){
         jaxbPersonType.getBasicPhoneNumber().add(((BasicPhoneNumberType)it.next()).getJaxbBasicPhoneNumberType()) ;
        }
        if(jaxbPersonType.getBasicPhoneNumber() == null){
           jaxbPersonType.getBasicPhoneNumber().add(new com.delphi_tech.ows.party.BasicPhoneNumberType());
        }

        it =basicAddress.iterator();
        while(it.hasNext()){
         jaxbPersonType.getBasicAddress().add(((BasicAddressType)it.next()).getJaxbBasicAddressType()) ;
        }
        if(jaxbPersonType.getBasicAddress() == null){
           jaxbPersonType.getBasicAddress().add( new com.delphi_tech.ows.party.BasicAddressType());
        }
        it =educationInformation.iterator();
        while(it.hasNext()){
         jaxbPersonType.getEducationInformation().add(((EducationInformationType)it.next()).getJaxbEducationInformationType()) ;
        }
        if(jaxbPersonType.getEducationInformation() == null){
          jaxbPersonType.getEducationInformation().add(new  com.delphi_tech.ows.party.EducationInformationType());
        }
        it = relationship.iterator();
        while(it.hasNext()) {
            jaxbPersonType.getRelationship().add(((RelationshipType)it.next()).getJaxbRelationshipType());
        }
        if (jaxbPersonType.getRelationship() == null) {
            jaxbPersonType.getRelationship().add(new com.delphi_tech.ows.party.RelationshipType());
        }
        it = partyClassification.iterator();
        while (it.hasNext()) {
            jaxbPersonType.getPartyClassification().add(((PartyClassificationType) it.next()).getJaxbPartyClassification());
        }
        if (jaxbPersonType.getPartyClassification() == null) {
            jaxbPersonType.getPartyClassification().add(new com.delphi_tech.ows.party.PartyClassificationType());
        }
}
   public void writeSQL(SQLOutput stream) throws SQLException{
   /*
       This method should not have implementation
       We are not planning to make any updates using this method.
   */
}   public com.delphi_tech.ows.party.PersonType getJaxbPersonType(){
        return jaxbPersonType;
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

     public void addBusinessEmailType(BusinessEmailType type){
         this.getBusinessEmail().add(type);
         jaxbPersonType.getBusinessEmail().add(type.getJaxbBusinessEmailType());
     }
     public void addBasicPhoneNumberType(BasicPhoneNumberType type){
         this.getBasicPhoneNumber().add(type);
         jaxbPersonType.getBasicPhoneNumber().add(type.getJaxbBasicPhoneNumberType());
     }
     public void addBasicAddressType(BasicAddressType type){
         this.getBasicAddress().add(type);
         jaxbPersonType.getBasicAddress().add(type.getJaxbBasicAddressType());
     }
    public void addEducationInformationType(EducationInformationType type){
          this.getEducationInformation().add(type);
          jaxbPersonType.getEducationInformation().add(type.getJaxbEducationInformationType());
      }
     public void  addProfessionalLicenseType(ProfessionalLicenseType type){
         this.getProfessionalLicense().add(type);
         jaxbPersonType.getProfessionalLicense().add(type.getJaxbProfessionalLicenseType());
     }
     public void addCertificationType(CertificationType type){
         this.getCertification().add(type);
         jaxbPersonType.getCertification().add(type.getJaxbCertificationType());
     }
     public void addContactType(ContactType type){
        this.getContact().add(type);
        jaxbPersonType.getContact().add(type.getJaxbContactType());
    }
    public void addPartyNoteType(PartyNoteType type){
        this.getPartyNote().add(type);
        jaxbPersonType.getPartyNote().add(type.getJaxbPartyNoteType());
    }
    public void addRelationshipType(RelationshipType type){
        this.getRelationship().add(type);
        jaxbPersonType.getRelationship().add(type.getJaxbRelationshipType());
    }


    public List<EducationInformationType> getEducationInformation() {
         if(educationInformation == null){
             educationInformation = new ArrayList<EducationInformationType>();
         }
        return educationInformation;
    }

    public List<ProfessionalLicenseType> getProfessionalLicense() {
        if (professionalLicense == null){
            professionalLicense = new ArrayList<ProfessionalLicenseType>();
        }
        return professionalLicense;
    }

    public List<CertificationType> getCertification() {
        if (certification == null){
            certification  = new ArrayList<CertificationType>();
        }
        return certification;
    }

    public List<ContactType> getContact() {
        if (contact==null){
            contact = new ArrayList<ContactType>();
        }
        return contact;
    }

    public List<PartyNoteType> getPartyNote() {
        if (partyNote==null){
            partyNote = new ArrayList<PartyNoteType>();
        }
        return partyNote;
    }

    public List<RelationshipType> getRelationship() {
        if (relationship == null) {
            relationship = new ArrayList<RelationshipType>();
        }
        return relationship;
    }

    /**
     * Gets the value of the personNumberId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPersonNumberId() {
        return personNumberId;
    }

    /**
     * Sets the value of the personNumberId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPersonNumberId(String value) {
        this.personNumberId = value;
    }

    /**
     * Gets the value of the clientId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClientId(String value) {
        this.clientId = value;
    }

    /**
     * Gets the value of the externalReferenceId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    /**
     * Sets the value of the externalReferenceId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExternalReferenceId(String value) {
        this.externalReferenceId = value;
    }

    /**
     * Gets the value of the personName property.
     *
     * @return
     *     possible object is
     *     {@link dti.ci.entitymgr.impl.jdbchelpers.PersonNameType }
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
     *     {@link dti.ci.entitymgr.impl.jdbchelpers.PersonNameType }
     *
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the businessEmail property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessEmail property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessEmail().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link dti.ci.entitymgr.impl.jdbchelpers.BusinessEmailType }
     *
     *
     */
    public List<BusinessEmailType> getBusinessEmail() {
        if (businessEmail == null) {
            businessEmail = new ArrayList<BusinessEmailType>();
        }
        return this.businessEmail;
    }

    /**
     * Gets the value of the basicPhoneNumber property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the basicPhoneNumber property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBasicPhoneNumber().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicPhoneNumberType }
     *
     *
     */
    public List<BasicPhoneNumberType> getBasicPhoneNumber() {
        if (basicPhoneNumber == null) {
            basicPhoneNumber = new ArrayList<BasicPhoneNumberType>();
        }
        return this.basicPhoneNumber;
    }

    /**
     * Gets the value of the basicAddress property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the basicAddress property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBasicAddress().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link dti.ci.entitymgr.impl.jdbchelpers.BasicAddressType }
     *
     *
     */
    public List<BasicAddressType> getBasicAddress() {
        if (basicAddress == null) {
            basicAddress = new ArrayList<BasicAddressType>();
        }
        return this.basicAddress;
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

    public List<PartyClassificationType> getPartyClassification() {
        if (partyClassification == null) {
            partyClassification = new ArrayList<PartyClassificationType>();
        }
        return partyClassification;
    }

    public void setPartyClassification(List<PartyClassificationType> partyClassification) {
        this.partyClassification = partyClassification;
    }

    public String getNationalProviderId() {
        return nationalProviderId;
    }

    public void setNationalProviderId(String nationalProviderId) {
        this.nationalProviderId = nationalProviderId;
    }

    public String getExternalDataId() {
        return externalDataId;
    }

    public void setExternalDataId(String externalDataId) {
        this.externalDataId = externalDataId;
    }

    public String getFeinId() {
        return feinId;
    }

    public void setFeinId(String feinId) {
        this.feinId = feinId;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getWebAddress1() {
        return webAddress1;
    }

    public void setWebAddress1(String webAddress1) {
        this.webAddress1 = webAddress1;
    }

    public String getWebAddress2() {
        return webAddress2;
    }

    public void setWebAddress2(String webAddress2) {
        this.webAddress2 = webAddress2;
    }

    public String getWebAddress3() {
        return webAddress3;
    }

    public void setWebAddress3(String webAddress3) {
        this.webAddress3 = webAddress3;
    }
}
