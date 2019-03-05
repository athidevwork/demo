package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class InsuredDetail implements Serializable {
    @XmlElement(name = "PartyNumberId")
    @JsonProperty("partyNumberId")
    public long entityId;

    @XmlElement(name = "ExternalId")
    @JsonProperty("externalId")
    public String externalId;

    @XmlElement(name = "Prefix")
    @JsonProperty("prefix")
    public String prefix;

    @XmlElement(name = "FirstName")
    @JsonProperty("firstName")
    public String firstName;

    @XmlElement(name = "LastName")
    @JsonProperty("lastName")
    public String lastName;

    @XmlElement(name = "Suffix")
    @JsonProperty("suffix")
    public String suffix;

    @XmlElement(name = "Degree")
    @JsonProperty("degree")
    public String degree;

    @XmlElement(name = "IncludeCoi")
    @JsonProperty("includeCoi")
    public boolean includeCoi;

    @XmlElement(name = "IncludeCoverageHistory")
    @JsonProperty("includeCoverageHistory")
    public boolean includeCoverageHistory;

    @XmlElement(name = "IncludeClaimsHistory")
    @JsonProperty("includeClaimsHistory")
    public boolean includeClaimsHistory;

    @XmlElement(name = "AddChargeFee")
    @JsonProperty("addChargeFee")
    public boolean addChargeFee;

    public InsuredDetail() { }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public boolean getIncludeCoi() { return includeCoi; }

    public void setIncludeCoi(boolean includeCoi) {
        this.includeCoi = includeCoi;
    }

    public boolean getIncludeCoverageHistory() {
        return includeCoverageHistory;
    }

    public void setIncludeCoverageHistory(boolean includeCoverageHistory) {
        this.includeCoverageHistory = includeCoverageHistory;
    }

    public boolean getIncludeClaimsHistory() {
        return includeClaimsHistory;
    }

    public void setIncludeClaimsHistory(boolean includeClaimsHistory) {
        this.includeClaimsHistory = includeClaimsHistory;
    }

    public boolean getAddChargeFee() { return addChargeFee; }

    public void setAddChargeFee(boolean addChargeFee) { this.addChargeFee = addChargeFee; }

    @Override
    public String toString() {
        return "{" +
                "entityId='" + entityId + '\'' +
                ", externalId='" + externalId + '\'' +
                ", prefix='" + prefix + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", degree='" + degree + '\'' +
                ", coi='" + includeCoi + '\'' +
                ", includeCoverageHistory='" + includeCoverageHistory + '\'' +
                ", includeClaimsHistory='" + includeClaimsHistory + '\'' +
                ", addChargeFee='" + addChargeFee + '\'' +
                '}';
    }
}
