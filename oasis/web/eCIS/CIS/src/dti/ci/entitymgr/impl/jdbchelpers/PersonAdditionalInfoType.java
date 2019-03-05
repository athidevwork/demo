package dti.ci.entitymgr.impl.jdbchelpers;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2017
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
public class PersonAdditionalInfoType implements SQLData {
    protected String veryLongName;
    protected String veryImportantPersonB;
    protected String federalTaxIdVerifiedB;
    protected String ssnVerifiedB;
    protected String defaultTaxId;
    protected String taxInfoEffectiveDate;
    protected String maritalStatus;
    protected String deceasedB;
    protected String minorB;
    protected String dateOfDeath;
    protected String dateOfMaturity;
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
    protected String webUserB;
    protected String webUserId;
    protected String dbaName;
    protected String hicn;
    protected String legalName;
    protected String referenceNumber;
    protected String profDesignation;
    protected String electronicDistrbB;
    protected String legalNameEffectiveDate;
    protected String personReference;

    protected com.delphi_tech.ows.party.PersonAdditionalInfoType jaxbPersonAdditionalInfoType;

    public String sql_type;

    public PersonAdditionalInfoType() {
        jaxbPersonAdditionalInfoType = new com.delphi_tech.ows.party.PersonAdditionalInfoType();
    }

    public PersonAdditionalInfoType(String veryLongName, String veryImportantPersonB, String federalTaxIdVerifiedB,
                                    String ssnVerifiedB, String defaultTaxId, String taxInfoEffectiveDate,
                                    String maritalStatus, String deceasedB, String minorB, String dateOfDeath,
                                    String dateOfMaturity, String discardedB,
                                    String char1, String char2, String char3, String char4, String char5,
                                    String num1, String num2, String num3,
                                    String date1, String date2, String date3,
                                    String lossFreeDate, String claimsFreeDate, String dynamicClaimEntrySource,
                                    String industryDesc, String component, String entityStatusCode, String adaNumber,
                                    String webUserB, String webUserId, String dbaName,
                                    String hicn, String legalName, String referenceNumber,
                                    String profDesignation, String electronicDistrbB, String legalNameEffectiveDate,
                                    String personReference) {
        this.veryLongName = veryLongName;
        this.veryImportantPersonB = veryImportantPersonB;
        this.federalTaxIdVerifiedB = federalTaxIdVerifiedB;
        this.ssnVerifiedB = ssnVerifiedB;
        this.defaultTaxId = defaultTaxId;
        this.taxInfoEffectiveDate = taxInfoEffectiveDate;
        this.maritalStatus = maritalStatus;
        this.deceasedB = deceasedB;
        this.minorB = minorB;
        this.dateOfDeath = dateOfDeath;
        this.dateOfMaturity = dateOfMaturity;
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
        this.webUserB = webUserB;
        this.webUserId = webUserId;
        this.dbaName = dbaName;
        this.hicn = hicn;
        this.legalName = legalName;
        this.referenceNumber = referenceNumber;
        this.profDesignation = profDesignation;
        this.electronicDistrbB = electronicDistrbB;
        this.legalNameEffectiveDate = legalNameEffectiveDate;
        this.personReference = personReference;

        jaxbPersonAdditionalInfoType = new com.delphi_tech.ows.party.PersonAdditionalInfoType();

        jaxbPersonAdditionalInfoType.setVeryLongName((this.veryLongName == null) ? "" : veryLongName);
        jaxbPersonAdditionalInfoType.setVeryImportantPersonB((this.veryImportantPersonB == null) ? "" : veryImportantPersonB);
        jaxbPersonAdditionalInfoType.setFederalTaxIdVerifiedB((this.federalTaxIdVerifiedB == null) ? "" : federalTaxIdVerifiedB);
        jaxbPersonAdditionalInfoType.setSsnVerifiedB((this.ssnVerifiedB == null) ? "" : ssnVerifiedB);
        jaxbPersonAdditionalInfoType.setDefaultTaxId((this.defaultTaxId == null) ? "" : defaultTaxId);
        jaxbPersonAdditionalInfoType.setTaxInfoEffectiveDate((this.taxInfoEffectiveDate == null) ? "" : taxInfoEffectiveDate);
        jaxbPersonAdditionalInfoType.setMaritalStatus((this.maritalStatus == null) ? "" : maritalStatus);
        jaxbPersonAdditionalInfoType.setDeceasedB((this.deceasedB == null) ? "" : deceasedB);
        jaxbPersonAdditionalInfoType.setMinorB((this.minorB == null) ? "" : minorB);
        jaxbPersonAdditionalInfoType.setDateOfDeath((this.dateOfDeath == null) ? "" : dateOfDeath);
        jaxbPersonAdditionalInfoType.setDateOfMaturity((this.dateOfMaturity == null) ? "" : dateOfMaturity);
        jaxbPersonAdditionalInfoType.setDiscardedB((this.discardedB == null) ? "" : discardedB);
        jaxbPersonAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbPersonAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbPersonAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbPersonAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbPersonAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbPersonAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbPersonAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbPersonAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbPersonAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbPersonAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbPersonAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbPersonAdditionalInfoType.setLossFreeDate((this.lossFreeDate == null) ? "" : lossFreeDate);
        jaxbPersonAdditionalInfoType.setClaimsFreeDate((this.claimsFreeDate == null) ? "" : claimsFreeDate);
        jaxbPersonAdditionalInfoType.setDynamicClaimEntrySource((this.dynamicClaimEntrySource == null) ? "" : dynamicClaimEntrySource);
        jaxbPersonAdditionalInfoType.setIndustryDesc((this.industryDesc == null) ? "" : industryDesc);
        jaxbPersonAdditionalInfoType.setComponent((this.component == null) ? "" : component);
        jaxbPersonAdditionalInfoType.setEntityStatusCode((this.entityStatusCode == null) ? "" : entityStatusCode);
        jaxbPersonAdditionalInfoType.setAdaNumber((this.adaNumber == null) ? "" : adaNumber);
        jaxbPersonAdditionalInfoType.setWebUserB((this.webUserB == null) ? "" : webUserB);
        jaxbPersonAdditionalInfoType.setWebUserId((this.webUserId == null) ? "" : webUserId);
        jaxbPersonAdditionalInfoType.setDbaName((this.dbaName == null) ? "" : dbaName);
        jaxbPersonAdditionalInfoType.setHicn((this.hicn == null) ? "" : hicn);
        jaxbPersonAdditionalInfoType.setLegalName((this.legalName == null) ? "" : legalName);
        jaxbPersonAdditionalInfoType.setReferenceNumber((this.referenceNumber == null) ? "" : referenceNumber);
        jaxbPersonAdditionalInfoType.setProfDesignation((this.profDesignation == null) ? "" : profDesignation);
        jaxbPersonAdditionalInfoType.setElectronicDistrbB((this.electronicDistrbB == null) ? "" : electronicDistrbB);
        jaxbPersonAdditionalInfoType.setLegalNameEffectiveDate((this.legalNameEffectiveDate == null) ? "" : legalNameEffectiveDate);
        jaxbPersonAdditionalInfoType.setPersonReference(personReference);
    }

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;

        personReference = stream.readString();
        veryLongName = stream.readString();
        veryImportantPersonB = stream.readString();
        federalTaxIdVerifiedB = stream.readString();
        ssnVerifiedB = stream.readString();
        defaultTaxId = stream.readString();
        taxInfoEffectiveDate = stream.readString();
        maritalStatus = stream.readString();
        deceasedB = stream.readString();
        minorB = stream.readString();
        dateOfDeath = stream.readString();
        dateOfMaturity = stream.readString();
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
        webUserB = stream.readString();
        webUserId = stream.readString();
        dbaName = stream.readString();
        hicn = stream.readString();
        legalName = stream.readString();
        referenceNumber = stream.readString();
        profDesignation = stream.readString();
        electronicDistrbB = stream.readString();
        legalNameEffectiveDate = stream.readString();

        jaxbPersonAdditionalInfoType = new com.delphi_tech.ows.party.PersonAdditionalInfoType();

        jaxbPersonAdditionalInfoType.setVeryLongName((this.veryLongName == null) ? "" : veryLongName);
        jaxbPersonAdditionalInfoType.setVeryImportantPersonB((this.veryImportantPersonB == null) ? "" : veryImportantPersonB);
        jaxbPersonAdditionalInfoType.setFederalTaxIdVerifiedB((this.federalTaxIdVerifiedB == null) ? "" : federalTaxIdVerifiedB);
        jaxbPersonAdditionalInfoType.setSsnVerifiedB((this.ssnVerifiedB == null) ? "" : ssnVerifiedB);
        jaxbPersonAdditionalInfoType.setDefaultTaxId((this.defaultTaxId == null) ? "" : defaultTaxId);
        jaxbPersonAdditionalInfoType.setTaxInfoEffectiveDate((this.taxInfoEffectiveDate == null) ? "" : taxInfoEffectiveDate);
        jaxbPersonAdditionalInfoType.setMaritalStatus((this.maritalStatus == null) ? "" : maritalStatus);
        jaxbPersonAdditionalInfoType.setDeceasedB((this.deceasedB == null) ? "" : deceasedB);
        jaxbPersonAdditionalInfoType.setMinorB((this.minorB == null) ? "" : minorB);
        jaxbPersonAdditionalInfoType.setDateOfDeath((this.dateOfDeath == null) ? "" : dateOfDeath);
        jaxbPersonAdditionalInfoType.setDateOfMaturity((this.dateOfMaturity == null) ? "" : dateOfMaturity);
        jaxbPersonAdditionalInfoType.setDiscardedB((this.discardedB == null) ? "" : discardedB);
        jaxbPersonAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbPersonAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbPersonAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbPersonAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbPersonAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbPersonAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbPersonAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbPersonAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbPersonAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbPersonAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbPersonAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbPersonAdditionalInfoType.setLossFreeDate((this.lossFreeDate == null) ? "" : lossFreeDate);
        jaxbPersonAdditionalInfoType.setClaimsFreeDate((this.claimsFreeDate == null) ? "" : claimsFreeDate);
        jaxbPersonAdditionalInfoType.setDynamicClaimEntrySource((this.dynamicClaimEntrySource == null) ? "" : dynamicClaimEntrySource);
        jaxbPersonAdditionalInfoType.setIndustryDesc((this.industryDesc == null) ? "" : industryDesc);
        jaxbPersonAdditionalInfoType.setComponent((this.component == null) ? "" : component);
        jaxbPersonAdditionalInfoType.setEntityStatusCode((this.entityStatusCode == null) ? "" : entityStatusCode);
        jaxbPersonAdditionalInfoType.setAdaNumber((this.adaNumber == null) ? "" : adaNumber);
        jaxbPersonAdditionalInfoType.setWebUserB((this.webUserB == null) ? "" : webUserB);
        jaxbPersonAdditionalInfoType.setWebUserId((this.webUserId == null) ? "" : webUserId);
        jaxbPersonAdditionalInfoType.setDbaName((this.dbaName == null) ? "" : dbaName);
        jaxbPersonAdditionalInfoType.setHicn((this.hicn == null) ? "" : hicn);
        jaxbPersonAdditionalInfoType.setLegalName((this.legalName == null) ? "" : legalName);
        jaxbPersonAdditionalInfoType.setReferenceNumber((this.referenceNumber == null) ? "" : referenceNumber);
        jaxbPersonAdditionalInfoType.setProfDesignation((this.profDesignation == null) ? "" : profDesignation);
        jaxbPersonAdditionalInfoType.setElectronicDistrbB((this.electronicDistrbB == null) ? "" : electronicDistrbB);
        jaxbPersonAdditionalInfoType.setLegalNameEffectiveDate((this.legalNameEffectiveDate == null) ? "" : legalNameEffectiveDate);
        jaxbPersonAdditionalInfoType.setPersonReference(personReference);
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
         /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public com.delphi_tech.ows.party.PersonAdditionalInfoType getJaxbPersonAdditionalInfoType() {
        return jaxbPersonAdditionalInfoType;
    }

    public String getVeryLongName() {
        return veryLongName;
    }

    public void setVeryLongName(String veryLongName) {
        this.veryLongName = veryLongName;
    }

    public String getVeryImportantPersonB() {
        return veryImportantPersonB;
    }

    public void setVeryImportantPersonB(String veryImportantPersonB) {
        this.veryImportantPersonB = veryImportantPersonB;
    }

    public String getFederalTaxIdVerifiedB() {
        return federalTaxIdVerifiedB;
    }

    public void setFederalTaxIdVerifiedB(String federalTaxIdVerifiedB) {
        this.federalTaxIdVerifiedB = federalTaxIdVerifiedB;
    }

    public String getSsnVerifiedB() {
        return ssnVerifiedB;
    }

    public void setSsnVerifiedB(String ssnVerifiedB) {
        this.ssnVerifiedB = ssnVerifiedB;
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

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getDeceasedB() {
        return deceasedB;
    }

    public void setDeceasedB(String deceasedB) {
        this.deceasedB = deceasedB;
    }

    public String getMinorB() {
        return minorB;
    }

    public void setMinorB(String minorB) {
        this.minorB = minorB;
    }

    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getDateOfMaturity() {
        return dateOfMaturity;
    }

    public void setDateOfMaturity(String dateOfMaturity) {
        this.dateOfMaturity = dateOfMaturity;
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

    public String getWebUserB() {
        return webUserB;
    }

    public void setWebUserB(String webUserB) {
        this.webUserB = webUserB;
    }

    public String getWebUserId() {
        return webUserId;
    }

    public void setWebUserId(String webUserId) {
        this.webUserId = webUserId;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public String getHicn() {
        return hicn;
    }

    public void setHicn(String hicn) {
        this.hicn = hicn;
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

    public String getProfDesignation() {
        return profDesignation;
    }

    public void setProfDesignation(String profDesignation) {
        this.profDesignation = profDesignation;
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

    public String getPersonReference() {
        return personReference;
    }

    public void setPersonReference(String personReference) {
        this.personReference = personReference;
    }
}
