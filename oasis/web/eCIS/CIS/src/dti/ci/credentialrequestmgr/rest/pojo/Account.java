package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Account implements Serializable {
    @XmlElement(name = "BillingAcctNumberId")
    @JsonProperty("billingAcctNumberId")
    public long acctNumberId;

    @XmlElement(name = "AccountName")
    @JsonProperty("accountName")
    public String acctName;

    @XmlElement(name = "AccountDesc")
    @JsonProperty("accountDesc")
    public String acctDesc;

    @XmlElement(name = "AccountType")
    @JsonProperty("accountType")
    public String acctType;

    @XmlElement(name = "CurrentBalance")
    @JsonProperty("currentBalance")
    public double currentBalance;

    @XmlElement(name = "NextBillingDate")
    @JsonProperty("nextBillingDate")
    public String nextBillingDate;

    public Account() { }

    public long getAcctNumberId() {
        return acctNumberId;
    }

    public void setAcctNumberId(long acctNumberId) {
        this.acctNumberId = acctNumberId;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getAcctDesc() {
        return acctDesc;
    }

    public void setAcctDesc(String acctDesc) {
        this.acctDesc = acctDesc;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(String nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    @Override
    public String toString() {
        return "{" +
                "acctNo='" + acctNumberId + '\'' +
                ", acctName='" + acctName + '\'' +
                ", acctDesc='" + acctDesc + '\'' +
                ", acctType='" + acctType + '\'' +
                ", currentBalance=" + currentBalance +
                ", nextBillingDate='" + nextBillingDate + '\'' +
                '}';
    }
}
