package dti.pm.policymgr.service.cache;

import java.util.ArrayList;
import java.util.List;

public class QuickQuoteServiceDefaultsCache {
    private QuickQuoteServiceDefaultPolicy policyType = new QuickQuoteServiceDefaultPolicy();
    private List<QuickQuoteServiceDefaultRisk> riskType = new ArrayList<QuickQuoteServiceDefaultRisk>();
    private List<QuickQuoteServiceDefaultCoverage> covgType = new ArrayList<QuickQuoteServiceDefaultCoverage>();

    public QuickQuoteServiceDefaultPolicy getPolicyType() {
        return policyType;
    }

    public void setPolicyType(QuickQuoteServiceDefaultPolicy policyType) {
        this.policyType = policyType;
    }

    public List<QuickQuoteServiceDefaultRisk> getRiskType() {
        return riskType;
    }

    public void setRiskType(List<QuickQuoteServiceDefaultRisk> riskType) {
        this.riskType = riskType;
    }

    public List<QuickQuoteServiceDefaultCoverage> getCovgType() {
        return covgType;
    }

    public void setCovgType(List<QuickQuoteServiceDefaultCoverage> covgType) {
        this.covgType = covgType;
    }

    @Override
    public String toString() {
        return "QuickQuoteServiceDefaultsCache{" +
                "policyType=" + policyType +
                ", riskType=" + riskType +
                ", covgType=" + covgType +
                '}';
    }
}
