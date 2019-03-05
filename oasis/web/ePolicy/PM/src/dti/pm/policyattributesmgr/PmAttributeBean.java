package dti.pm.policyattributesmgr;

import java.math.BigDecimal;

/**
 * Bean class for PM Attribute.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/01/16 wdang   167534 - Initial version.
 * ---------------------------------------------------
 */
public class PmAttributeBean {

    public String getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public void setEffectiveFromDate(String effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
    }

    public String getEffectiveToDate() {
        return effectiveToDate;
    }

    public void setEffectiveToDate(String effectiveToDate) {
        this.effectiveToDate = effectiveToDate;
    }

    public BigDecimal getCode1() {
        return code1;
    }

    public void setCode1(BigDecimal code1) {
        this.code1 = code1;
    }

    public BigDecimal getCode2() {
        return code2;
    }

    public void setCode2(BigDecimal code2) {
        this.code2 = code2;
    }

    public BigDecimal getCode3() {
        return code3;
    }

    public void setCode3(BigDecimal code3) {
        this.code3 = code3;
    }

    public BigDecimal getCode4() {
        return code4;
    }

    public void setCode4(BigDecimal code4) {
        this.code4 = code4;
    }

    public BigDecimal getCode5() {
        return code5;
    }

    public void setCode5(BigDecimal code5) {
        this.code5 = code5;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getAddlSql() {
        return addlSql;
    }

    public void setAddlSql(String addlSql) {
        this.addlSql = addlSql;
    }

    private String effectiveFromDate;
    private String effectiveToDate;
    private BigDecimal code1;
    private BigDecimal code2;
    private BigDecimal code3;
    private BigDecimal code4;
    private BigDecimal code5;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String value5;
    private String addlSql;
}
