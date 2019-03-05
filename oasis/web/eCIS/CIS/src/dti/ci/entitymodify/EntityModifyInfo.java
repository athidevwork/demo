package dti.ci.entitymodify;

import dti.oasis.util.StringUtils;

import java.util.ArrayList;

import static dti.ci.entitymgr.EntityConstants.*;

/**
 * Helper class for passing information about modifying an Entity.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Mar 17, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         <p/>
 *         04/13/2018       ylu         109088: refactor from CIEntityModifyInfo.java
 *         ---------------------------------------------------
 */

public class EntityModifyInfo {

  public long getEntityPK() {
    return entityPK;
  }

  public void setEntityPK(long entityPK) {
    this.entityPK = entityPK;
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
                    dupTaxIDSysParm.equals(DUP_TAX_ID_SYS_PARM_ERROR_VALUE) ||
                            dupTaxIDSysParm.equals(DUP_TAX_ID_SYS_PARM_WARNING_VALUE) ||
                            dupTaxIDSysParm.equals(DUP_TAX_ID_SYS_PARM_PROFILE_VALUE)
            )
            ) {
      this.dupTaxIDSysParm = dupTaxIDSysParm;
    }
  }

  public ArrayList getTaxIDDupsInfo() {
    return taxIDDupsInfo;
  }

  public void setTaxIDDupsInfo(ArrayList taxIDDupsInput) {
    int index = 0;
    this.taxIDDupsInfo.clear();
    if (taxIDDupsInput != null) {
      for (int i = 0; i < taxIDDupsInput.size(); i++) {
        Object o = taxIDDupsInput.get(i);
        if (o instanceof String) {
          this.taxIDDupsInfo.add(index, o);
          index += 1;
        }
      }
    }
    displayedTaxIdDupsCount = taxIDDupsInput.size();
  }

  public ArrayList getEmailDupsInfo() {
    return emailDupsInfo;
  }

  public void setEmailDupsInfo(ArrayList emailDupsInput) {
    int index = 0;
    this.emailDupsInfo.clear();
    if (emailDupsInput != null) {
      for (int i = 0; i < emailDupsInput.size(); i++) {
        Object o = emailDupsInput.get(i);
        if (o instanceof String) {
          this.emailDupsInfo.add(index, o);
          index += 1;
        }
      }
    }
    displayeEmailDupsCount = emailDupsInput.size();
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

  private long entityPK = -1;
  private long taxIDDupCount = 0;
  private long emailDupCount = 0;
  private String taxIDDupsXmlDocStr = "";
  private String emailDupsXmlDocStr = "";
  private ArrayList taxIDDupsInfo = new ArrayList();
  private ArrayList emailDupsInfo = new ArrayList();
  private boolean userCanDupTaxID = false;
  private String dupTaxIDSysParm = "";
  private String userMessage = "";

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

  public boolean isTaxIdDupsTruncated() {
    return isTaxIdDupsTruncated;
  }

  public void setTaxIdDupsTruncated(boolean taxIdDupsTruncated) {
    isTaxIdDupsTruncated = taxIdDupsTruncated;
  }

  private int displayeEmailDupsCount = 0;
  private int displayedTaxIdDupsCount = 0;
  private boolean isEmailDupsTruncated = false;
  private boolean isTaxIdDupsTruncated = false;

  public boolean isEntityUpdated() {
    return entityUpdated;
  }

  public void setEntityUpdated(boolean entityUpdated) {
    this.entityUpdated = entityUpdated;
  }

  private boolean entityUpdated;

}
