package dti.ci.core;

import dti.oasis.util.LogUtils;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2018
 *
 * @author eouyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityInfo implements Serializable {

    private String entityId;
    private String entityName;
    private String entityType;
    private String clientId;
    private String gender;
    private String socialSecurityNumber;
    private String legacyDataID;
    private String referenceNumber;
    private String noteExistB;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getLegacyDataID() {
        return legacyDataID;
    }

    public void setLegacyDataID(String legacyDataID) {
        this.legacyDataID = legacyDataID;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getNoteExistB() {
        return noteExistB;
    }

    public void setNoteExistB(String noteExistB) {
        this.noteExistB = noteExistB;
    }
}
