package dti.pm.riskmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PMStatusCode;

/**
 * This class provides header information about a risk. It typically contains key
 * risk information along with commonly accessed attributes.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 16, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/19/2010       yhyang      Add char3 to RiskHeader since it is used as a parameter while retrieving
 *                              all available coverage class.
 * 05/25/2010       syang       107771 - Add baseRiskStatusCode.
 * 01/21/2011       dzhang      116359 - Add riskEffectiveToDate.
 * 04/09/2012       syang       131516 - Removed the duplicated data element char3.
 * 08/06/2013       awu         146878 - Added m_currentRiskStatusCode.
 * 06/17/2015       wdang       157211 - Added riskEffectiveFromDate.
 * 12/06/2017       lzhang      182769 - Added contigRiskEffectiveDate/
 *                                       contigRiskExpireDate
  * ---------------------------------------------------
 */

public class RiskHeader {
    public String getRiskId() {
        return m_riskId;
    }

    public void setRiskId(String riskId) {
        m_riskId = riskId;
    }

    public String getRiskBaseRecordId() {
        return m_riskBaseRecordId;
    }

    public void setRiskBaseRecordId(String riskBaseRecordId) {
        m_riskBaseRecordId = riskBaseRecordId;
    }

    public String getRiskEntityId() {
        return m_riskEntityId;
    }

    public void setRiskEntityId(String riskEntityId) {
        m_riskEntityId = riskEntityId;
    }

    public String getRiskName() {
        return m_riskName;
    }

    public void setRiskName(String riskName) {
        m_riskName = riskName;
    }

    public String getRiskTypeCode() {
        return m_riskTypeCode;
    }

    public void setRiskTypeCode(String riskTypeCode) {
        m_riskTypeCode = riskTypeCode;
    }

    public String getPracticeStateCode() {
        return m_practiceStateCode;
    }

    public void setPracticeStateCode(String practiceStateCode) {
        m_practiceStateCode = practiceStateCode;
    }

    public String getEarliestContigEffectiveDate() {
        return m_earliestContigEffectiveDate;
    }

    public void setEarliestContigEffectiveDate(String earliestContigEffectiveDate) {
        m_earliestContigEffectiveDate = earliestContigEffectiveDate;
    }

    public YesNoFlag getDateChangeAllowedB() {
        return m_dateChangeAllowedB;
    }

    public void setDateChangeAllowedB(YesNoFlag dateChangeAllowedB) {
        m_dateChangeAllowedB = dateChangeAllowedB;
    }

    public String getTaxStatusCode() {
        return m_taxStatusCode;
    }

    public void setTaxStatusCode(String taxStatusCode) {
        m_taxStatusCode = taxStatusCode;
    }

    public String getDateChangeAllowedRiskDate() {
        return m_dateChangeAllowedRiskDate;
    }

    public void setDateChangeAllowedRiskDate(String dateChangeAllowedRiskDate) {
        m_dateChangeAllowedRiskDate = dateChangeAllowedRiskDate;
    }

    public YesNoFlag getRollingIbnrIndicator() {
        return m_rollingIbnrIndicator;
    }

    public void setRollingIbnrIndicator(YesNoFlag rollingIbnrIndicator) {
        m_rollingIbnrIndicator = rollingIbnrIndicator;
    }

    public PMStatusCode getRiskStatusCode() {
        return m_riskStatusCode;
    }

    public void setRiskStatusCode(PMStatusCode riskStatusCode) {
        m_riskStatusCode = riskStatusCode;
    }

    public YesNoFlag getPrimaryRiskB() {
        return m_primaryRiskB;
    }

    public void setPrimaryRiskB(YesNoFlag primaryRiskB) {
        m_primaryRiskB = primaryRiskB;
    }


    public String getRiskCountyCode() {
        return m_riskCountyCode;
    }

    public void setRiskCountyCode(String riskCountyCode) {
        m_riskCountyCode = riskCountyCode;
    }

    public PMStatusCode getBaseRiskStatusCode() {
        return m_baseRiskStatusCode;
    }

    public void setBaseRiskStatusCode(PMStatusCode baseRiskStatusCode) {
        m_baseRiskStatusCode = baseRiskStatusCode;
    }

    public String getRiskEffectiveFromDate() {
        return m_riskEffectiveFromDate;
    }

    public void setRiskEffectiveFromDate(String riskEffectiveFromDate) {
        m_riskEffectiveFromDate = riskEffectiveFromDate;
    }

    public String getRiskEffectiveToDate() {
        return m_riskEffectiveToDate;
    }

    public void setRiskEffectiveToDate(String riskEffectiveToDate) {
        m_riskEffectiveToDate = riskEffectiveToDate;
    }

    public PMStatusCode getCurrentRiskStatusCode() {
        return m_currentRiskStatusCode;
    }

    public void setCurrentRiskStatusCode(PMStatusCode statusCode) {
        m_currentRiskStatusCode = statusCode;
    }

    public String getContigRiskEffectiveDate() {
        return m_contigRiskEffectiveDate;
    }

    public void setContigRiskEffectiveDate(String contigRiskEffectiveDate) {
        m_contigRiskEffectiveDate = contigRiskEffectiveDate;
    }

    public String getContigRiskExpireDate() {
        return m_contigRiskExpireDate;
    }

    public void setContigRiskExpireDate(String contigRiskExpireDate) {
        m_contigRiskExpireDate = contigRiskExpireDate;
    }
    public String toString() {
        return "RiskHeader{" +
            "m_riskId='" + m_riskId + '\'' +
            ", m_riskBaseRecordId='" + m_riskBaseRecordId + '\'' +
            ", m_riskEntityId='" + m_riskEntityId + '\'' +
            ", m_riskName='" + m_riskName + '\'' +
            ", m_riskTypeCode='" + m_riskTypeCode + '\'' +
            ", m_practiceStateCode='" + m_practiceStateCode + '\'' +
            ", m_earliestContigEffectiveDate='" + m_earliestContigEffectiveDate + '\'' +
            ", m_dateChangeAllowedB='" + m_dateChangeAllowedB + '\'' +
            ", m_dateChangeAllowedRiskDate='" + m_dateChangeAllowedRiskDate + '\'' +            
            ", m_taxStatusCode='" + m_taxStatusCode + '\'' +
            ", m_rollingIbnrIndicator='" + m_rollingIbnrIndicator + '\'' +
            ", m_riskStatusCode='" + m_riskStatusCode + '\'' +
            ", m_primaryRiskB='" + m_primaryRiskB + '\'' +
            ", m_riskCountyCode='" + m_riskCountyCode + '\'' +
            ", m_baseRiskStatusCode='" + m_baseRiskStatusCode + '\'' +
            ", m_riskEffectiveFromDate='" + m_riskEffectiveFromDate + '\'' +
            ", m_riskEffectiveToDate='" + m_riskEffectiveToDate + '\'' +
            ", m_currentRiskStatusCode='" + m_currentRiskStatusCode +'\'' +
            ", m_contigRiskEffectiveDate='" + m_contigRiskEffectiveDate +'\'' +
            ", m_contigRiskExpireDate='" + m_contigRiskExpireDate +'\'' +
            '}';
    }

    private String m_riskId;
    private String m_riskBaseRecordId;
    private String m_riskEntityId;
    private String m_riskName;
    private String m_riskTypeCode;
    private String m_practiceStateCode;
    private String m_earliestContigEffectiveDate;
    private YesNoFlag m_dateChangeAllowedB;
    private String m_taxStatusCode;
    private String m_dateChangeAllowedRiskDate;
    private String m_riskCountyCode;
    private YesNoFlag m_rollingIbnrIndicator;
    private PMStatusCode m_riskStatusCode;
    private YesNoFlag m_primaryRiskB;
    private PMStatusCode m_baseRiskStatusCode;
    private String m_riskEffectiveFromDate;
    private String m_riskEffectiveToDate;
    private PMStatusCode m_currentRiskStatusCode;
    private String m_contigRiskEffectiveDate;
    private String m_contigRiskExpireDate;
}
