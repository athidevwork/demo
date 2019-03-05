package dti.pm.policymgr.service.cache;

public class QuickQuoteServiceDefaultRisk {
    private String level;
    private String startDate;
    private String endDate;
    private String policyType;
    private String value;

    public QuickQuoteServiceDefaultRisk(String level, String startDate, String endDate, String policyType, String value) {
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
        this.policyType = policyType;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QuickQuoteServiceDefaultRisk{" +
                "level='" + level + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", policyType='" + policyType + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
