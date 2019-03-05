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
public class AddressAdditionalInfoType implements SQLData {
    protected String reinsControlAddr;
    protected String char1;
    protected String char2;
    protected String char3;
    protected String char4;
    protected String char5;
    protected String num1;
    protected String num2;
    protected String num3;
    protected String num4;
    protected String num5;
    protected String date1;
    protected String date2;
    protected String date3;
    protected String date4;
    protected String date5;
    protected String undeliverableB;
    protected String vendorSiteCode;
    protected String addressReference;

    protected com.delphi_tech.ows.party.AddressAdditionalInfoType jaxbAddressAdditionalInfoType;

    public String sql_type;

    public AddressAdditionalInfoType() {
        jaxbAddressAdditionalInfoType = new com.delphi_tech.ows.party.AddressAdditionalInfoType();
    }

    public AddressAdditionalInfoType(String reinsControlAddr,
                                     String char1, String char2, String char3, String char4, String char5,
                                     String num1, String num2, String num3, String num4, String num5,
                                     String date1, String date2, String date3, String date4, String date5,
                                     String undeliverableB, String vendorSiteCode, String addressReference) {
        this.reinsControlAddr = reinsControlAddr;
        this.char1 = char1;
        this.char2 = char2;
        this.char3 = char3;
        this.char4 = char4;
        this.char5 = char5;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.num4 = num4;
        this.num5 = num5;
        this.date1 = date1;
        this.date2 = date2;
        this.date3 = date3;
        this.date4 = date4;
        this.date5 = date5;
        this.undeliverableB = undeliverableB;
        this.vendorSiteCode = vendorSiteCode;
        this.addressReference = addressReference;

        jaxbAddressAdditionalInfoType = new com.delphi_tech.ows.party.AddressAdditionalInfoType();

        jaxbAddressAdditionalInfoType.setReinsControlAddr((this.reinsControlAddr == null) ? "" : reinsControlAddr);
        jaxbAddressAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbAddressAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbAddressAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbAddressAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbAddressAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbAddressAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbAddressAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbAddressAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbAddressAdditionalInfoType.setNum4((this.num4 == null) ? "" : num4);
        jaxbAddressAdditionalInfoType.setNum5((this.num5 == null) ? "" : num5);
        jaxbAddressAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbAddressAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbAddressAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbAddressAdditionalInfoType.setDate4((this.date4 == null) ? "" : date4);
        jaxbAddressAdditionalInfoType.setDate5((this.date5 == null) ? "" : date5);
        jaxbAddressAdditionalInfoType.setUndeliverableB((this.undeliverableB == null) ? "" : undeliverableB);
        jaxbAddressAdditionalInfoType.setVendorSiteCode((this.vendorSiteCode == null) ? "" : vendorSiteCode);
        jaxbAddressAdditionalInfoType.setAddressReference(addressReference);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;

        addressReference = stream.readString();
        reinsControlAddr = stream.readString();
        char1 = stream.readString();
        char2 = stream.readString();
        char3 = stream.readString();
        char4 = stream.readString();
        char5 = stream.readString();
        num1 = stream.readString();
        num2 = stream.readString();
        num3 = stream.readString();
        num4 = stream.readString();
        num5 = stream.readString();
        date1 = stream.readString();
        date2 = stream.readString();
        date3 = stream.readString();
        date4 = stream.readString();
        date5 = stream.readString();
        undeliverableB = stream.readString();
        vendorSiteCode = stream.readString();

        jaxbAddressAdditionalInfoType = new com.delphi_tech.ows.party.AddressAdditionalInfoType();

        jaxbAddressAdditionalInfoType.setReinsControlAddr((this.reinsControlAddr == null) ? "" : reinsControlAddr);
        jaxbAddressAdditionalInfoType.setChar1((this.char1 == null) ? "" : char1);
        jaxbAddressAdditionalInfoType.setChar2((this.char2 == null) ? "" : char2);
        jaxbAddressAdditionalInfoType.setChar3((this.char3 == null) ? "" : char3);
        jaxbAddressAdditionalInfoType.setChar4((this.char4 == null) ? "" : char4);
        jaxbAddressAdditionalInfoType.setChar5((this.char5 == null) ? "" : char5);
        jaxbAddressAdditionalInfoType.setNum1((this.num1 == null) ? "" : num1);
        jaxbAddressAdditionalInfoType.setNum2((this.num2 == null) ? "" : num2);
        jaxbAddressAdditionalInfoType.setNum3((this.num3 == null) ? "" : num3);
        jaxbAddressAdditionalInfoType.setNum4((this.num4 == null) ? "" : num4);
        jaxbAddressAdditionalInfoType.setNum5((this.num5 == null) ? "" : num5);
        jaxbAddressAdditionalInfoType.setDate1((this.date1 == null) ? "" : date1);
        jaxbAddressAdditionalInfoType.setDate2((this.date2 == null) ? "" : date2);
        jaxbAddressAdditionalInfoType.setDate3((this.date3 == null) ? "" : date3);
        jaxbAddressAdditionalInfoType.setDate4((this.date4 == null) ? "" : date4);
        jaxbAddressAdditionalInfoType.setDate5((this.date5 == null) ? "" : date5);
        jaxbAddressAdditionalInfoType.setUndeliverableB((this.undeliverableB == null) ? "" : undeliverableB);
        jaxbAddressAdditionalInfoType.setVendorSiteCode((this.vendorSiteCode == null) ? "" : vendorSiteCode);
        jaxbAddressAdditionalInfoType.setAddressReference(addressReference);
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public String getReinsControlAddr() {
        return reinsControlAddr;
    }

    public void setReinsControlAddr(String reinsControlAddr) {
        this.reinsControlAddr = reinsControlAddr;
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

    public String getNum4() {
        return num4;
    }

    public void setNum4(String num4) {
        this.num4 = num4;
    }

    public String getNum5() {
        return num5;
    }

    public void setNum5(String num5) {
        this.num5 = num5;
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

    public String getDate4() {
        return date4;
    }

    public void setDate4(String date4) {
        this.date4 = date4;
    }

    public String getDate5() {
        return date5;
    }

    public void setDate5(String date5) {
        this.date5 = date5;
    }

    public String getUndeliverableB() {
        return undeliverableB;
    }

    public void setUndeliverableB(String undeliverableB) {
        this.undeliverableB = undeliverableB;
    }

    public String getVendorSiteCode() {
        return vendorSiteCode;
    }

    public void setVendorSiteCode(String vendorSiteCode) {
        this.vendorSiteCode = vendorSiteCode;
    }

    public String getAddressReference() {
        return addressReference;
    }

    public void setAddressReference(String addressReference) {
        this.addressReference = addressReference;
    }

    public com.delphi_tech.ows.party.AddressAdditionalInfoType getJaxbAddressAdditionalInfoType() {
        return jaxbAddressAdditionalInfoType;
    }
}
