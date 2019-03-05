package dti.ci.entitymgr.impl.jdbchelpers;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2017
 *
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
public class OrganizationAdditionalInfoType implements SQLData {
    protected String veryLongName;
    protected String federalTaxIdVerifiedB;
    protected String defaultTaxId;
    protected String taxInfoEffectiveDate;
    protected String discardedB;
    protected String char1;
    protected String char2;
    protected String char3;
    protected String char4;
    protected String char5;
    protected String num1;
    protected String num2;
    protected String num3;
    protected String date1;
    protected String date2;
    protected String date3;
    protected String lossFreeDate;
    protected String claimsFreeDate;
    protected String dynamicClaimEntrySource;
    protected String industryDesc;
    protected String component;
    protected String entityStatusCode;
    protected String adaNumber;
    protected String dbaName;
    protected String legalName;
    protected String referenceNumber;
    protected String electronicDistrbB;
    protected String legalNameEffectiveDate;
    protected String organizationReference;

    protected com.delphi_tech.ows.party.OrganizationAdditionalInfoType jaxbOrganizationAdditionalInfoType;
    public String sql_type;

    public OrganizationAdditionalInfoType() {
    }

    public OrganizationAdditionalInfoType(String veryLongName, String federalTaxIdVerifiedB, String defaultTaxId,
                                          String taxInfoEffectiveDate, String discardedB,
                                          String char1, String char2, String char3, String char4, String char5,
                                          String num1, String num2, String num3,
                                          String date1, String date2, String date3,
                                          String lossFreeDate, String claimsFreeDate, String dynamicClaimEntrySource, String industryDesc,
                                          String component, String entityStatusCode, String adaNumber,
                                          String dbaName, String legalName, String referenceNumber,
                                          String electronicDistrbB, String legalNameEffectiveDate,
                                          String organizationReference) {
        this.veryLongName = veryLongName;
        this.federalTaxIdVerifiedB = federalTaxIdVerifiedB;
        this.defaultTaxId = defaultTaxId;
        this.taxInfoEffectiveDate = taxInfoEffectiveDate;
        this.discardedB = discardedB;
        this.char1 = char1;
        this.char2 = char2;
        this.char3 = char3;
        this.char4 = char4;
        this.char5 = char5;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.date1 = date1;
        this.date2 = date2;
        this.date3 = date3;
        this.lossFreeDate = lossFreeDate;
        this.claimsFreeDate = claimsFreeDate;
        this.dynamicClaimEntrySource = dynamicClaimEntrySource;
        this.industryDesc = industryDesc;
        this.component = component;
        this.entityStatusCode = entityStatusCode;
        this.adaNumber = adaNumber;
        this.dbaName = dbaName;
        this.legalName = legalName;
        this.referenceNumber = referenceNumber;
        this.electronicDistrbB = electronicDistrbB;
        this.legalNameEffectiveDate = legalNameEffectiveDate;
        this.organizationReference = organizationReference;

        jaxbOrganizationAdditionalInfoType = new com.delphi_tech.ows.party.OrganizationAdditionalInfoType();

        jaxbOrganizationAdditionalInfoType.setVeryLongName((this.veryLongName == null) ? "" : veryLongName);
        jaxbOrganizationAdditionalInfoType.setFederalTaxIdVerifiedB((this.federalTaxIdVerifiedB == null) ? "" : federalTaxIdVerifiedB);
        jaxbOrganizationAdditionalInfoType.setDefaultTaxId((this.defaultTaxId == null) ? "" : defaultTaxId);
        jaxbOrganizationAdditionalInfoType.setTaxInfoEffectiveDate((this.taxInfoEffectiveDate == null) ? "" : taxInfoEffectiveDate);
        jaxbOrganizationAdditionalInfoType.setDiscardedB((this.discardedB == null) ? "" : discardedB);
        jaxbOrganizationAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbOrganizationAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbOrganizationAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbOrganizationAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbOrganizationAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbOrganizationAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbOrganizationAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbOrganizationAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbOrganizationAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbOrganizationAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbOrganizationAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbOrganizationAdditionalInfoType.setLossFreeDate((this.lossFreeDate == null) ? "" : lossFreeDate);
        jaxbOrganizationAdditionalInfoType.setClaimsFreeDate((this.claimsFreeDate == null) ? "" : claimsFreeDate);
        jaxbOrganizationAdditionalInfoType.setDynamicClaimEntrySource((this.dynamicClaimEntrySource == null) ? "" : dynamicClaimEntrySource);
        jaxbOrganizationAdditionalInfoType.setIndustryDesc((this.industryDesc == null) ? "" : industryDesc);
        jaxbOrganizationAdditionalInfoType.setComponent((this.component == null) ? "" : component);
        jaxbOrganizationAdditionalInfoType.setEntityStatusCode((this.entityStatusCode == null) ? "" : entityStatusCode);
        jaxbOrganizationAdditionalInfoType.setAdaNumber((this.adaNumber == null) ? "" : adaNumber);
        jaxbOrganizationAdditionalInfoType.setDbaName((this.dbaName == null) ? "" : dbaName);
        jaxbOrganizationAdditionalInfoType.setLegalName((this.legalName == null) ? "" : legalName);
        jaxbOrganizationAdditionalInfoType.setReferenceNumber((this.referenceNumber == null) ? "" : referenceNumber);
        jaxbOrganizationAdditionalInfoType.setElectronicDistrbB((this.electronicDistrbB == null) ? "" : electronicDistrbB);
        jaxbOrganizationAdditionalInfoType.setLegalNameEffectiveDate((this.legalNameEffectiveDate == null) ? "" : legalNameEffectiveDate);
        jaxbOrganizationAdditionalInfoType.setOrganizationReference(organizationReference);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;

        organizationReference = stream.readString();
        veryLongName = stream.readString();
        federalTaxIdVerifiedB = stream.readString();
        defaultTaxId = stream.readString();
        taxInfoEffectiveDate = stream.readString();
        discardedB = stream.readString();
        char1 = stream.readString();
        char2 = stream.readString();
        char3 = stream.readString();
        char4 = stream.readString();
        char5 = stream.readString();
        num1 = stream.readString();
        num2 = stream.readString();
        num3 = stream.readString();
        date1 = stream.readString();
        date2 = stream.readString();
        date3 = stream.readString();
        lossFreeDate = stream.readString();
        claimsFreeDate = stream.readString();
        dynamicClaimEntrySource = stream.readString();
        industryDesc = stream.readString();
        component = stream.readString();
        entityStatusCode = stream.readString();
        adaNumber = stream.readString();
        dbaName = stream.readString();
        legalName = stream.readString();
        referenceNumber = stream.readString();
        electronicDistrbB = stream.readString();
        legalNameEffectiveDate = stream.readString();

        jaxbOrganizationAdditionalInfoType = new com.delphi_tech.ows.party.OrganizationAdditionalInfoType();

        jaxbOrganizationAdditionalInfoType.setVeryLongName((this.veryLongName == null) ? "" : veryLongName);
        jaxbOrganizationAdditionalInfoType.setFederalTaxIdVerifiedB((this.federalTaxIdVerifiedB == null) ? "" : federalTaxIdVerifiedB);
        jaxbOrganizationAdditionalInfoType.setDefaultTaxId((this.defaultTaxId == null) ? "" : defaultTaxId);
        jaxbOrganizationAdditionalInfoType.setTaxInfoEffectiveDate((this.taxInfoEffectiveDate == null) ? "" : taxInfoEffectiveDate);
        jaxbOrganizationAdditionalInfoType.setDiscardedB((this.discardedB == null) ? "" : discardedB);
        jaxbOrganizationAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbOrganizationAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbOrganizationAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbOrganizationAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbOrganizationAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbOrganizationAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbOrganizationAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbOrganizationAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbOrganizationAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbOrganizationAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbOrganizationAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbOrganizationAdditionalInfoType.setLossFreeDate((this.lossFreeDate == null) ? "" : lossFreeDate);
        jaxbOrganizationAdditionalInfoType.setClaimsFreeDate((this.claimsFreeDate == null) ? "" : claimsFreeDate);
        jaxbOrganizationAdditionalInfoType.setDynamicClaimEntrySource((this.dynamicClaimEntrySource == null) ? "" : dynamicClaimEntrySource);
        jaxbOrganizationAdditionalInfoType.setIndustryDesc((this.industryDesc == null) ? "" : industryDesc);
        jaxbOrganizationAdditionalInfoType.setComponent((this.component == null) ? "" : component);
        jaxbOrganizationAdditionalInfoType.setEntityStatusCode((this.entityStatusCode == null) ? "" : entityStatusCode);
        jaxbOrganizationAdditionalInfoType.setAdaNumber((this.adaNumber == null) ? "" : adaNumber);
        jaxbOrganizationAdditionalInfoType.setDbaName((this.dbaName == null) ? "" : dbaName);
        jaxbOrganizationAdditionalInfoType.setLegalName((this.legalName == null) ? "" : legalName);
        jaxbOrganizationAdditionalInfoType.setReferenceNumber((this.referenceNumber == null) ? "" : referenceNumber);
        jaxbOrganizationAdditionalInfoType.setElectronicDistrbB((this.electronicDistrbB == null) ? "" : electronicDistrbB);
        jaxbOrganizationAdditionalInfoType.setLegalNameEffectiveDate((this.legalNameEffectiveDate == null) ? "" : legalNameEffectiveDate);
        jaxbOrganizationAdditionalInfoType.setOrganizationReference(organizationReference);
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public com.delphi_tech.ows.party.OrganizationAdditionalInfoType getJaxbOrganizationAdditionalInfoType() {
        return jaxbOrganizationAdditionalInfoType;
    }

    public String getVeryLongName() {
        return veryLongName;
    }

    public void setVeryLongName(String veryLongName) {
        this.veryLongName = veryLongName;
    }

    public String getFederalTaxIdVerifiedB() {
        return federalTaxIdVerifiedB;
    }

    public void setFederalTaxIdVerifiedB(String federalTaxIdVerifiedB) {
        this.federalTaxIdVerifiedB = federalTaxIdVerifiedB;
    }

    public String getDefaultTaxId() {
        return defaultTaxId;
    }

    public void setDefaultTaxId(String defaultTaxId) {
        this.defaultTaxId = defaultTaxId;
    }

    public String getTaxInfoEffectiveDate() {
        return taxInfoEffectiveDate;
    }

    public void setTaxInfoEffectiveDate(String taxInfoEffectiveDate) {
        this.taxInfoEffectiveDate = taxInfoEffectiveDate;
    }

    public String getDiscardedB() {
        return discardedB;
    }

    public void setDiscardedB(String discardedB) {
        this.discardedB = discardedB;
    }

    public String getChar1() {
        return char1;
    }

    public void setChar1(String char1) {
        this.char1 = char1;
    }

    public String getChar2() {
        return char2;
    }

    public void setChar2(String char2) {
        this.char2 = char2;
    }

    public String getChar3() {
        return char3;
    }

    public void setChar3(String char3) {
        this.char3 = char3;
    }

    public String getChar4() {
        return char4;
    }

    public void setChar4(String char4) {
        this.char4 = char4;
    }

    public String getChar5() {
        return char5;
    }

    public void setChar5(String char5) {
        this.char5 = char5;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getNum3() {
        return num3;
    }

    public void setNum3(String num3) {
        this.num3 = num3;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public String getLossFreeDate() {
        return lossFreeDate;
    }

    public void setLossFreeDate(String lossFreeDate) {
        this.lossFreeDate = lossFreeDate;
    }

    public String getClaimsFreeDate() {
        return claimsFreeDate;
    }

    public void setClaimsFreeDate(String claimsFreeDate) {
        this.claimsFreeDate = claimsFreeDate;
    }

    public String getDynamicClaimEntrySource() {
        return dynamicClaimEntrySource;
    }

    public void setDynamicClaimEntrySource(String dynamicClaimEntrySource) {
        this.dynamicClaimEntrySource = dynamicClaimEntrySource;
    }

    public String getIndustryDesc() {
        return industryDesc;
    }

    public void setIndustryDesc(String industryDesc) {
        this.industryDesc = industryDesc;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getEntityStatusCode() {
        return entityStatusCode;
    }

    public void setEntityStatusCode(String entityStatusCode) {
        this.entityStatusCode = entityStatusCode;
    }

    public String getAdaNumber() {
        return adaNumber;
    }

    public void setAdaNumber(String adaNumber) {
        this.adaNumber = adaNumber;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getElectronicDistrbB() {
        return electronicDistrbB;
    }

    public void setElectronicDistrbB(String electronicDistrbB) {
        this.electronicDistrbB = electronicDistrbB;
    }

    public String getLegalNameEffectiveDate() {
        return legalNameEffectiveDate;
    }

    public void setLegalNameEffectiveDate(String legalNameEffectiveDate) {
        this.legalNameEffectiveDate = legalNameEffectiveDate;
    }

    public String getOrganizationReference() {
        return organizationReference;
    }

    public void setOrganizationReference(String organizationReference) {
        this.organizationReference = organizationReference;
    }
}
