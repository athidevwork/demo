package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.AddressAdditionalInfoType;
import com.delphi_tech.ows.party.AddressAdditionalXmlDataType;
import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.BasicAddressType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
* <p>(C) 2003 Delphi Technology, inc. (dti)</p>
* Date:   4/27/2017
*
* @author kshen
*/
public class AddressInfo {
    private PartyChangeRequestType partyChangeRequest;
    private boolean newAddress;
    private String newAddressId;
    private BasicAddressType changedBasicAddress;
    private AddressType changedAddress;
    private Record changedAddressRecord;
    private Record originalAddressRecord;
    private Record dbAddressRecord;

    public PartyChangeRequestType getPartyChangeRequest() {
        return partyChangeRequest;
    }

    public void setPartyChangeRequest(PartyChangeRequestType partyChangeRequest) {
        this.partyChangeRequest = partyChangeRequest;
    }

    public boolean isNewAddress() {
        return newAddress;
    }

    public void setNewAddress(boolean newAddress) {
        this.newAddress = newAddress;
    }

    public String getAddressKey() {
        if (this.changedAddress != null) {
            return this.changedAddress.getKey();
        }
        return null;
    }

    public String getAddressId() {
        if (this.changedAddress != null) {
            return this.changedAddress.getAddressNumberId();
        }
        return null;
    }

    public String getNewAddressId() {
        return newAddressId;
    }

    public void setNewAddressId(String newAddressId) {
        this.newAddressId = newAddressId;
    }

    public BasicAddressType getChangedBasicAddress() {
        return changedBasicAddress;
    }

    public void setChangedBasicAddress(BasicAddressType changedBasicAddress) {
        this.changedBasicAddress = changedBasicAddress;
    }

    public AddressType getChangedAddress() {
        return changedAddress;
    }

    public void setChangedAddress(AddressType changedAddress) {
        this.changedAddress = changedAddress;
    }

    public Record getChangedAddressRecord() {
        return changedAddressRecord;
    }

    public void setChangedAddressRecord(Record changedAddressRecord) {
        this.changedAddressRecord = changedAddressRecord;
    }

    public Record getOriginalAddressRecord() {
        return originalAddressRecord;
    }

    public void setOriginalAddressRecord(Record originalAddressRecord) {
        this.originalAddressRecord = originalAddressRecord;
    }

    public Record getDbAddressRecord() {
        return dbAddressRecord;
    }

    public void setDbAddressRecord(Record dbAddressRecord) {
        this.dbAddressRecord = dbAddressRecord;
    }

    public AddressAdditionalInfoType getChangedAddressAdditionalInfo() {
        if (this.partyChangeRequest.getPartyAdditionalInfo() != null) {

            for (AddressAdditionalInfoType addressAdditionalInfo : this.partyChangeRequest.getPartyAdditionalInfo().getAddressAdditionalInfo()) {
                if (this.changedAddress.getKey().equals(addressAdditionalInfo.getAddressReference())) {

                    return addressAdditionalInfo;
                }
            }
        }

        return null;
    }

    public AddressAdditionalInfoType getOriginalAddressAdditionalInfo() {
        if (this.partyChangeRequest.getDataModificationInformation() != null &&
                this.partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() != null &&
                this.partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo() != null) {

            for (AddressAdditionalInfoType addressAdditionalInfo :
                    this.partyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getPartyAdditionalInfo().getAddressAdditionalInfo()) {
                if (this.changedAddress.getKey().equals(addressAdditionalInfo.getAddressReference())) {

                    return addressAdditionalInfo;
                }
            }
        }

        return null;
    }

    public AddressAdditionalXmlDataType getAddressAdditionalXmlData() {
        if (this.partyChangeRequest.getPartyAdditionalXmlInfo() != null) {
            for (AddressAdditionalXmlDataType addressAdditionalXmlData : this.partyChangeRequest.getPartyAdditionalXmlInfo().getAddressAdditionalXmlData()) {
                if (this.changedAddress.getKey().equals(addressAdditionalXmlData.getAddressReference())) {

                    return addressAdditionalXmlData;
                }
            }
        }

        return null;
    }
}
