package dti.pm.policymgr.service.cache;

public class QuickQuoteServiceDefaultPolicy {
    private String level;
    private String startDate;
    private String endDate;
    private String value;

    public QuickQuoteServiceDefaultPolicy() {
    }

    public QuickQuoteServiceDefaultPolicy(String level, String startDate, String endDate, String value) {
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QuickQuoteServiceDefaultPolicy{" +
                "level='" + level + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
