package dti.ci.entitymgr.service.partychangeprocessor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/27/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddressChangeResult {
    private String entityId;
    private boolean primaryAddressChanged;
    private Map<String, String> expiredAddressMap;

    public AddressChangeResult() {
        this.primaryAddressChanged = false;
    }

    public AddressChangeResult(String entityId) {
        this();
        this.entityId = entityId;
    }

    public void addExpiredAddress(String expiredAddressKey, String newAddressKey) {
        if (expiredAddressMap == null) {
            expiredAddressMap = new HashMap<String, String>();
        }
        expiredAddressMap.put(expiredAddressKey, newAddressKey);
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isPrimaryAddressChanged() {
        return primaryAddressChanged;
    }

    public void setPrimaryAddressChanged(boolean primaryAddressChanged) {
        this.primaryAddressChanged = primaryAddressChanged;
    }

    public Map<String, String> getExpiredAddressMap() {
        return expiredAddressMap;
    }

    public void setExpiredAddressMap(Map<String, String> expiredAddressMap) {
        this.expiredAddressMap = expiredAddressMap;
    }
}
