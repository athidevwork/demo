package dti.ci.credentialrequestmgr.rest.pojo;

public class CredentialLetterDefaults {
    public String originatingSystem;
    public String coi;
    public String claimsHistory;
    public String coverageHistory;
    public String chargeFee;

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public String getCoi() {
        return coi;
    }

    public void setCoi(String coi) {
        this.coi = coi;
    }

    public String getClaimsHistory() {
        return claimsHistory;
    }

    public void setClaimsHistory(String claimsHistory) {
        this.claimsHistory = claimsHistory;
    }

    public String getCoverageHistory() {
        return coverageHistory;
    }

    public void setCoverageHistory(String coverageHistory) {
        this.coverageHistory = coverageHistory;
    }

    public String getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(String chargeFee) {
        this.chargeFee = chargeFee;
    }

    @Override
    public String toString() {
        return "{" +
                "originatingSystem='" + originatingSystem + '\'' +
                ", coi='" + coi + '\'' +
                ", includeClaimsHistory='" + claimsHistory + '\'' +
                ", includeCoverageHistory='" + coverageHistory + '\'' +
                ", addChargeFee='" + chargeFee + '\'' +
                '}';
    }
}
