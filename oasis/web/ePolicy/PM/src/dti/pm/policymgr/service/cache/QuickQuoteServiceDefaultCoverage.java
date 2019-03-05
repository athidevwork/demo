package dti.pm.policymgr.service.cache;

public class QuickQuoteServiceDefaultCoverage {
    private String level;
    private String startDate;
    private String endDate;
    private String policyType;
    private String riskType;
    private String state;
    private String value;

    public QuickQuoteServiceDefaultCoverage(String level, String startDate, String endDate, String policyType, String riskType, String state, String value) {
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
        this.policyType = policyType;
        this.riskType = riskType;
        this.state = state;
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QuickQuoteServiceDefaultCoverage{" +
                "level='" + level + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", policyType='" + policyType + '\'' +
                ", riskType='" + riskType + '\'' +
                ", state='" + state + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
