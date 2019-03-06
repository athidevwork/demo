//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-1.0.0.0_2-1-9-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.03.08 at 05:55:40 PM EST
//


package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class OrganizationType   implements SQLData {

    protected String organizationNumberId;
    protected String clientId;
    protected String externalReferenceId;
    protected String externalDataId;
    protected String feinId;
    protected String key;
    protected OrganizationNameType organizationName;
    protected String nationalProviderId;
    protected String insuredSinceDate;
    protected String sicCode;
    protected String webAddress1;
    protected String webAddress2;
    protected String webAddress3;
    protected List<BusinessEmailType> businessEmail;
    protected List<BasicPhoneNumberType> basicPhoneNumber;
    protected List<BasicAddressType> basicAddress;
    protected List<OrganizationLicenseType> organizationLicense;
    protected List<CertificationType> certification;
    protected List<PartyNoteType> partyNote;
    protected List<RelationshipType> relationship;
    protected List<PartyClassificationType> partyClassification;

    protected com.delphi_tech.ows.party.OrganizationType jaxbOrganizationType;

    public String sql_type;

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    public  OrganizationType(){
        jaxbOrganizationType = new com.delphi_tech.ows.party.OrganizationType();
    }

    public OrganizationType(String organizationNumberId, String clientId, String externalReferenceId, String feinId,
                            String key, OrganizationNameType organizationName, String nationalProviderId, String insuredSinceDate, String sicCode,
                            String webAddress1, String webAddress2, String webAddress3, String externalDataId,
                            BusinessEmailType businessEmail, BasicPhoneNumberType basicPhoneNumber,
                            BasicAddressType basicAddress, OrganizationLicenseType organizationLicense,
                            CertificationType certification,
                            PartyNoteType partyNote,
                            RelationshipType relationship,
                            PartyClassificationType partyClassification) {
        this.organizationNumberId = organizationNumberId;
        this.clientId = clientId;
        this.externalReferenceId = externalReferenceId;
        this.externalDataId = externalDataId;
        this.feinId = feinId;
        this.key = key;
        this.organizationName = organizationName;
        this.nationalProviderId = nationalProviderId;
        this.insuredSinceDate = insuredSinceDate;
        this.sicCode = sicCode;
        this.webAddress1 = webAddress1;
        this.webAddress2 = webAddress2;
        this.webAddress3 = webAddress3;
        this.getOrganizationLicense().add(organizationLicense);
        this.getCertification().add(certification);
        this.getPartyNote().add(partyNote);
        this.getRelationship().add(relationship);
        this.getPartyClassification().add(partyClassification);

        jaxbOrganizationType = new com.delphi_tech.ows.party.OrganizationType();
        jaxbOrganizationType.setClientId(this.clientId);
        jaxbOrganizationType.setExternalReferenceId(this.externalReferenceId);
        jaxbOrganizationType.setExternalDataId(this.externalDataId);
        jaxbOrganizationType.setKey(this.key);
        jaxbOrganizationType.setOrganizationNumberId(this.organizationNumberId);

        if(organizationName != null){
            jaxbOrganizationType.setOrganizationName(this.organizationName.getJaxbOrganizationNameType());
        }
        jaxbOrganizationType.setNationalProviderId(nationalProviderId);
        jaxbOrganizationType.setInsuredSinceDate(insuredSinceDate);
        jaxbOrganizationType.getBusinessEmail().add(businessEmail.getJaxbBusinessEmailType());
        jaxbOrganizationType.getBasicPhoneNumber().add(basicPhoneNumber.getJaxbBasicPhoneNumberType());
        jaxbOrganizationType.getBasicAddress().add(basicAddress.getJaxbBasicAddressType());
        jaxbOrganizationType.getOrganizationLicense().add(organizationLicense.getJaxbOrganizationLicenseType());
        jaxbOrganizationType.getCertification().add(certification.getJaxbCertificationType());
        jaxbOrganizationType.getPartyNote().add(partyNote.getJaxbPartyNoteType());
        jaxbOrganizationType.getRelationship().add(relationship.getJaxbRelationshipType());
        jaxbOrganizationType.getPartyClassification().add(partyClassification.getJaxbPartyClassification());
    }
    public void readSQL(SQLInput stream,String typeName ) throws SQLException{
        sql_type = typeName;
        organizationNumberId = stream.readString();
        clientId = stream.readString();
        externalReferenceId = stream.readString();
        nationalProviderId = stream.readString();
        externalDataId = stream.readNString();
        feinId = stream.readString();
        key = stream.readString();
        organizationName = (OrganizationNameType) setClass(stream.readObject(), OrganizationNameType.class);
        insuredSinceDate = stream.readString();
        sicCode = stream.readString();
        webAddress1 = stream.readString();
        webAddress2 = stream.readString();
        webAddress3 = stream.readString();
        businessEmail = (List) setList(stream.readObject(),BusinessEmailType.class );
        basicPhoneNumber= (List) setList(stream.readObject(),BasicPhoneNumberType.class );
        basicAddress = (List) setList(stream.readObject(),BasicAddressType.class );
        organizationLicense = (List)setList(stream.readObject(), OrganizationLicenseType.class);
        certification = (List)setList(stream.readObject(), CertificationType.class);
        partyNote = (List)setList(stream.readObject(), PartyNoteType.class);
        relationship = (List)setList(stream.readObject(), RelationshipType.class);
        partyClassification = (List)setList(stream.readObject(), PartyClassificationType.class);

        jaxbOrganizationType.setClientId((this.clientId==null)?"":clientId);
        jaxbOrganizationType.setExternalReferenceId((this.externalReferenceId == null) ? "" : externalReferenceId);
        jaxbOrganizationType.setExternalDataId(this.externalDataId == null ? "" : this.externalDataId);
        jaxbOrganizationType.setFEINId((this.feinId==null)?"":feinId);
        jaxbOrganizationType.setKey(this.key);
        jaxbOrganizationType.setOrganizationNumberId((this.organizationNumberId==null)?"":organizationNumberId);

        if(organizationName != null){
            jaxbOrganizationType.setOrganizationName(this.organizationName.getJaxbOrganizationNameType());
        }else{
            jaxbOrganizationType.setOrganizationName(new com.delphi_tech.ows.party.OrganizationNameType());
        }
        jaxbOrganizationType.setNationalProviderId(this.nationalProviderId == null ? "" : nationalProviderId);
        jaxbOrganizationType.setInsuredSinceDate(this.insuredSinceDate == null ? "" : insuredSinceDate);
        jaxbOrganizationType.setSicCode(this.sicCode == null ? "" : sicCode);
        jaxbOrganizationType.setWebAddress1(this.webAddress1 == null ? "" : webAddress1);
        jaxbOrganizationType.setWebAddress2(this.webAddress2 == null ? "" : webAddress2);
        jaxbOrganizationType.setWebAddress3(this.webAddress3 == null ? "" : webAddress3);
        Iterator it =businessEmail.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getBusinessEmail().add(((BusinessEmailType)it.next()).getJaxbBusinessEmailType()) ;
        }
        if(jaxbOrganizationType.getBusinessEmail() == null){
            jaxbOrganizationType.getBusinessEmail().add(new com.delphi_tech.ows.party.BusinessEmailType());
        }

        it =basicPhoneNumber.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getBasicPhoneNumber().add(((BasicPhoneNumberType)it.next()).getJaxbBasicPhoneNumberType()) ;
        }
        if(jaxbOrganizationType.getBasicPhoneNumber() == null){
            jaxbOrganizationType.getBasicPhoneNumber().add( new com.delphi_tech.ows.party.BasicPhoneNumberType());
        }


        it =basicAddress.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getBasicAddress().add(((BasicAddressType)it.next()).getJaxbBasicAddressType()) ;
        }
        if(jaxbOrganizationType.getBasicAddress() == null){
            jaxbOrganizationType.getBasicAddress().add(new com.delphi_tech.ows.party.BasicAddressType());
        }

        it = organizationLicense.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getOrganizationLicense().add(((OrganizationLicenseType)it.next()).getJaxbOrganizationLicenseType()) ;
        }
        if(jaxbOrganizationType.getOrganizationLicense() == null){
            jaxbOrganizationType.getOrganizationLicense().add(new com.delphi_tech.ows.party.OrganizationLicenseType());
        }

        it =certification.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getCertification().add(((CertificationType)it.next()).getJaxbCertificationType()) ;
        }
        if(jaxbOrganizationType.getCertification() == null){
            jaxbOrganizationType.getCertification().add( new   com.delphi_tech.ows.party.CertificationType());
        }

        it =partyNote.iterator();
        while(it.hasNext()){
            jaxbOrganizationType.getPartyNote().add(((PartyNoteType)it.next()).getJaxbPartyNoteType());
        }
        if(jaxbOrganizationType.getPartyNote() == null){
            jaxbOrganizationType.getPartyNote().add(new com.delphi_tech.ows.party.PartyNoteType());
        }
        it = relationship.iterator();
        while(it.hasNext()) {
            jaxbOrganizationType.getRelationship().add(((RelationshipType)it.next()).getJaxbRelationshipType());
        }
        if (jaxbOrganizationType.getRelationship() == null) {
            jaxbOrganizationType.getRelationship().add(new com.delphi_tech.ows.party.RelationshipType());
        }
        it = partyClassification.iterator();
        while (it.hasNext()) {
            jaxbOrganizationType.getPartyClassification().add(((PartyClassificationType) it.next()).getJaxbPartyClassification());
        }
        if (jaxbOrganizationType.getPartyClassification() == null) {
            jaxbOrganizationType.getPartyClassification().add(new com.delphi_tech.ows.party.PartyClassificationType());
        }
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
    public com.delphi_tech.ows.party.OrganizationType getJaxbOrganizationType(){
        return this.jaxbOrganizationType;
    }
    public List<BusinessEmailType> addBusinessEmailType(BusinessEmailType type){
        this.getBusinessEmail().add(type);
        jaxbOrganizationType.getBusinessEmail().add(type.getJaxbBusinessEmailType());
        return  this.getBusinessEmail();
    }

    public List<BasicPhoneNumberType> addBasicPhoneNumberType(BasicPhoneNumberType type){
        this.getBasicPhoneNumber().add(type);
        jaxbOrganizationType.getBasicPhoneNumber().add(type.getJaxbBasicPhoneNumberType());
        return  this.getBasicPhoneNumber();
    }

    public List<BasicAddressType> addBasicAddressType(BasicAddressType type){
        this.basicAddress.add(type);
        jaxbOrganizationType.getBasicAddress().add(type.getJaxbBasicAddressType());
        return  this.getBasicAddress();

    }

    public List<OrganizationLicenseType> getOrganizationLicense() {
        if (organizationLicense == null){
            organizationLicense = new ArrayList<OrganizationLicenseType>();
        }
        return organizationLicense;
    }

    public List<CertificationType> getCertification() {
        if (certification == null){
            certification  = new ArrayList<CertificationType>();
        }
        return certification;
    }

    public List<PartyNoteType> getPartyNote() {
        if (partyNote == null){
            partyNote  = new ArrayList<PartyNoteType>();
        }
        return partyNote;
    }

    public List<RelationshipType> getRelationship() {
        if (relationship == null){
            relationship = new ArrayList<RelationshipType>();
        }
        return relationship;
    }
    /**
     * Gets the value of the organizationNumberId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrganizationNumberId() {
        return organizationNumberId;
    }

    /**
     * Sets the value of the organizationNumberId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrganizationNumberId(String value) {
        this.organizationNumberId = value;
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
     * Gets the value of the organizationName property.
     *
     * @return
     *     possible object is
     *     {@link OrganizationNameType }
     *
     */
    public OrganizationNameType getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the value of the organizationName property.
     *
     * @param value
     *     allowed object is
     *     {@link OrganizationNameType }
     *
     */
    public void setOrganizationName(OrganizationNameType value) {
        this.organizationName = value;
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
     * {@link BusinessEmailType }
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
     * {@link BasicAddressType }
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

    /**
     * Gets the value of the FeinId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFeinId() {
        return feinId;
    }

    /**
     * Sets the value of the FeinId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFeinId(String value) {
        this.feinId = value;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
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