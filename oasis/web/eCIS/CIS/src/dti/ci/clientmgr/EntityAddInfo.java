package dti.ci.clientmgr;

import dti.ci.helpers.ICIEntityConstants;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for passing information about adding an Entity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Mar 9, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/27/2010       Kenny       Iss#110852.
 * ---------------------------------------------------
 */

public class EntityAddInfo {

    public boolean isEntityAdded() {
        return entityAdded;
    }

    public void setEntityAdded(boolean entityAdded) {
        this.entityAdded = entityAdded;
        if (!this.entityAdded) {
            this.setEntityPK("-1");
        }
    }

    public long getEntityDupCount() {
        return entityDupCount;
    }

    public void setEntityDupCount(long entityDupCount) {
        this.entityDupCount = entityDupCount;
    }

    public long getMergedDupCount() {
        return mergedDupCount;
    }

    public void setMergedDupCount(long mergedDupCount) {
        this.mergedDupCount = mergedDupCount;
    }

    public String getEntityDupsXmlDocStr() {
        return entityDupsXmlDocStr;
    }

    public void setEntityDupsXmlDocStr(String entityDupsXmlDocStr) {
        this.entityDupsXmlDocStr = entityDupsXmlDocStr;
    }

    public String getMergedDupsXmlDocStr() {
        return mergedDupsXmlDocStr;
    }

    public void setMergedDupsXmlDocStr(String mergedDupsXmlDocStr) {
        this.mergedDupsXmlDocStr = mergedDupsXmlDocStr;
    }

    public List<String> getEntityDupsInfo() {
        return entityDupsInfo;
    }

    public List<String> getMergedDupsInfo() {
        return mergedDupsInfo;
    }

    public void setEntityDupsInfo(List<String> entityDupsInput) {
        entityDupsInfo.clear();
        if (entityDupsInput != null) {
            entityDupsInfo.addAll(entityDupsInput);
        }
    }

    public void setMergedDupsInfo(List<String> allDupsInput) {
        mergedDupsInfo.clear();
        if (allDupsInput != null) {
            mergedDupsInfo.addAll(allDupsInput);
        }
    }

    public boolean isEntityDupsTruncated() {
        return isEntityDupsTruncated;
    }

    public void setEntityDupsTruncated(boolean entityDupsTruncated) {
        isEntityDupsTruncated = entityDupsTruncated;
    }

    public int getDisplayedEntityDupCount() {
        displayedEntityDupCount = entityDupsInfo.size();
        return displayedEntityDupCount;
    }

    public int getDisplayedMergedDupCount() {
        displayedMergedDupCount = mergedDupsInfo.size();
        return displayedMergedDupCount;
    }

    public boolean isMergedDupsTruncated() {
        return isMergedDupsTruncated;
    }

    public void setMergedDupsTruncated(boolean mergedDupsTruncated) {
        isMergedDupsTruncated = mergedDupsTruncated;
    }

    public boolean isTaxIdDupsTruncated() {
        return isTaxIdDupsTruncated;
    }

    public void setTaxIdDupsTruncated(boolean taxIdDupsTruncated) {
        isTaxIdDupsTruncated = taxIdDupsTruncated;
    }

    public String getEntityPK() {
        return entityPK;
    }

    public void setEntityPK(String entityPK) {
        this.entityPK = entityPK;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getTaxIDDupCount() {
        return taxIDDupCount;
    }

    public void setTaxIDDupCount(long taxIDDupCount) {
        this.taxIDDupCount = taxIDDupCount;
    }

    public long getEmailDupCount() {
        return emailDupCount;
    }

    public void setEmailDupCount(long emailDupCount) {
        this.emailDupCount = emailDupCount;
    }

    public String getTaxIDDupsXmlDocStr() {
        return taxIDDupsXmlDocStr;
    }

    public void setTaxIDDupsXmlDocStr(String taxIDDupsXmlDocStr) {
        this.taxIDDupsXmlDocStr = taxIDDupsXmlDocStr;
    }

    public String getEmailDupsXmlDocStr() {
        return emailDupsXmlDocStr;
    }

    public void setEmailDupsXmlDocStr(String emailDupsXmlDocStr) {
        this.emailDupsXmlDocStr = emailDupsXmlDocStr;
    }

    public boolean isUserCanDupTaxID() {
        return userCanDupTaxID;
    }

    public void setUserCanDupTaxID(boolean userCanDupTaxID) {
        this.userCanDupTaxID = userCanDupTaxID;
    }

    public String getDupTaxIDSysParm() {
        return dupTaxIDSysParm;
    }

    public void setDupTaxIDSysParm(String dupTaxIDSysParm) {
        if (!StringUtils.isBlank(dupTaxIDSysParm) &&
                (
                        dupTaxIDSysParm.equals(ICIEntityConstants.DUP_TAX_ID_SYS_PARM_ERROR_VALUE) ||
                                dupTaxIDSysParm.equals(ICIEntityConstants.DUP_TAX_ID_SYS_PARM_WARNING_VALUE) ||
                                dupTaxIDSysParm.equals(ICIEntityConstants.DUP_TAX_ID_SYS_PARM_PROFILE_VALUE)
                )
                ) {
            this.dupTaxIDSysParm = dupTaxIDSysParm;
        }
    }

    public List<String> getTaxIDDupsInfo() {
        return taxIDDupsInfo;
    }

    public void setTaxIDDupsInfo(List<String> taxIDDupsInput) {
        taxIDDupsInfo.clear();

        if (taxIDDupsInput != null) {
            taxIDDupsInfo.addAll(taxIDDupsInput);
        }

        displayedTaxIdDupsCount = taxIDDupsInfo.size();
    }

    public List<String> getEmailDupsInfo() {
        return emailDupsInfo;
    }

    public void setEmailDupsInfo(List<String> emailDupsInfo) {
        emailDupsInfo.clear();

        if (emailDupsInfo != null) {
            emailDupsInfo.addAll(emailDupsInfo);
        }

        displayeEmailDupsCount = emailDupsInfo.size();
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        if (StringUtils.isBlank(userMessage)) {
            this.userMessage = "";
        } else {
            this.userMessage = userMessage;
        }
    }

    public int getDisplayeEmailDupsCount() {
        return displayeEmailDupsCount;
    }

    public int getDisplayedTaxIdDupsCount() {
        return displayedTaxIdDupsCount;
    }

    public boolean isEmailDupsTruncated() {
        return isEmailDupsTruncated;
    }

    public void setEmailDupsTruncated(boolean emailDupsTruncated) {
        isEmailDupsTruncated = emailDupsTruncated;
    }

    private boolean entityAdded = false;
    private long entityDupCount = 0;
    private long mergedDupCount = 0;
    private int displayedEntityDupCount = 0;
    private int displayedMergedDupCount = 0;
    private String entityDupsXmlDocStr = "";
    private String mergedDupsXmlDocStr = "";
    private List<String> entityDupsInfo = new ArrayList();
    private List<String> mergedDupsInfo = new ArrayList();
    private boolean isEntityDupsTruncated = false;
    private boolean isMergedDupsTruncated = false;

    private String entityPK = "-1";
    private String clientId;
    private long taxIDDupCount = 0;
    private long emailDupCount = 0;
    private String taxIDDupsXmlDocStr = "";
    private String emailDupsXmlDocStr = "";
    private List<String> taxIDDupsInfo = new ArrayList();
    private List<String> emailDupsInfo = new ArrayList();
    private boolean userCanDupTaxID = false;
    private String dupTaxIDSysParm = "";
    private String userMessage = "";
    private int displayeEmailDupsCount = 0;
    private int displayedTaxIdDupsCount = 0;
    private boolean isEmailDupsTruncated = false;
    private boolean isTaxIdDupsTruncated = false;
}
