package dti.pm.coveragemgr;

/**
 * This class contains key information about a coverage.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 17, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/07/2013        awu         issue146878 - Added m_currentCoverageStatus
 * 02/13/2014        awu         issue147405 - Added baseCoverageStatusCode.
 * 12/06/2017        lzhang      issue182769 - Added contigCoverageEffectiveDate/
 *                                             contigCoverageExpireDate
 * ---------------------------------------------------
 */
public class CoverageHeader {

    public String getCoverageId(){
        return m_coverageId;
    }

    public void setCoverageId(String coverageId) {
        m_coverageId = coverageId;
    }

    public String getCoverageBaseRecordId(){
        return m_coverageBaseRecordId;
    }

    public void setCoverageBaseRecordId(String coverageBaseRecordId){
        m_coverageBaseRecordId = coverageBaseRecordId;
    }

    public String getProductCoverageCode(){
        return m_productCoverageCode;
    }

    public void setProductCoverageCode(String productCoverageCode){
        m_productCoverageCode = productCoverageCode;
    }

    public String getCoverageName() {
        return m_coverageName;
   }

    public void setCoverageName(String coverageName) {
        m_coverageName = coverageName;
    }

    public String getCoverageCode() {
        return m_coverageCode;
    }

    public void setCoverageCode(String coverageCode){
        m_coverageCode = coverageCode;
    }

    public String getRetroactiveDate() {
        return m_retroactiveDate;
    }

    public void setRetroactiveDate(String retroactiveDate) {
        m_retroactiveDate = retroactiveDate;
    }

    public String getCoverageEffectiveFromDate() {
        return m_coverageEffectiveFromDate;
    }

    public void setCoverageEffectiveFromDate(String coverageEffectiveFromDate) {
        m_coverageEffectiveFromDate = coverageEffectiveFromDate;
    }

    public String getPolicyFormCode() {
        return m_policyFormCode;
    }

    public void setPolicyFormCode(String policyFormCode) {
        m_policyFormCode = policyFormCode;
    }

    public String getCoverageStatusCode() {
        return m_coverageStatusCode;
    }

    public void setCoverageStatusCode(String coverageStatusCode) {
        m_coverageStatusCode = coverageStatusCode;
    }

    public String getCoverageEffectiveToDate() {
        return m_coverageEffectiveToDate;
    }

    public void setCoverageEffectiveToDate(String coverageEffectiveToDate) {
        m_coverageEffectiveToDate = coverageEffectiveToDate;
    }

    public String getCurrentCoverageStatus() {
        return m_currentCoverageStatus;
    }

    public void setCurrentCoverageStatus(String currentCoverageStatus) {
        m_currentCoverageStatus = currentCoverageStatus;
    }

    public String getBaseCoverageStatusCode() {
        return m_baseCoverageStatusCode;
    }

    public void setBaseCoverageStatusCode(String baseCoverageStatusCode) {
        m_baseCoverageStatusCode = baseCoverageStatusCode;
    }

    public String getContigCoverageEffectiveDate() {
        return m_contigCoverageEffectiveDate;
    }

    public void setContigCoverageEffectiveDate(String contigCoverageEffectiveDate) {
        m_contigCoverageEffectiveDate = contigCoverageEffectiveDate;
    }

    public String getContigCoverageExpireDate() {
        return m_contigCoverageExpireDate;
    }

    public void setContigCoverageExpireDate(String contigCoverageEffectiveDate) {
        m_contigCoverageExpireDate = contigCoverageEffectiveDate;
    }

    public String toString() {
        return "CoverageHeader{" +
            "m_coverageId='" + m_coverageId + '\'' +
            ", m_coverageBaseRecordId='" + m_coverageBaseRecordId + '\'' +
            ", m_productCoverageCode='" + m_productCoverageCode + '\'' +
            ", m_coverageName='" + m_coverageName + '\'' +
            ", m_coverageCode='" + m_coverageCode + '\'' +
            ", m_retroactiveDate='" + m_retroactiveDate + '\'' +
            ", m_coverageEffectiveFromDate='" + m_coverageEffectiveFromDate + '\'' +
            ", m_policyFormCode='" + m_policyFormCode + '\'' +
            ", m_coverageStatusCode='" + m_coverageStatusCode + '\'' +
            ", m_coverageEffectiveToDate='" + m_coverageEffectiveToDate + '\'' +
            ", m_currentCoverageStatus='" + m_currentCoverageStatus + '\'' +
            ", m_baseCoverageStatusCode='" + m_baseCoverageStatusCode + '\'' +
            ", m_contigCoverageEffectiveDate='" + m_contigCoverageEffectiveDate + '\'' +
            ", m_contigCoverageExpireDate='" + m_contigCoverageExpireDate + '\'' +
            '}';
    }

    private String m_coverageId;
    private String m_coverageBaseRecordId;
    private String m_productCoverageCode;
    private String m_coverageName;
    private String m_coverageCode;
    private String m_retroactiveDate;
    private String m_coverageEffectiveFromDate;
    private String m_policyFormCode;
    private String m_coverageStatusCode;
    private String m_coverageEffectiveToDate;
    private String m_currentCoverageStatus;
    private String m_baseCoverageStatusCode;
    private String m_contigCoverageEffectiveDate;
    private String m_contigCoverageExpireDate;
}
