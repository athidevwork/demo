package dti.ci.clientmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.dao.AddressDAO;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.clientmgr.EntityAddManager;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitymgr.dao.EntityDAO;
import dti.ci.helpers.ICIEntityConstants;
import dti.ci.helpers.ICIPhoneNumberConstants;
import dti.cs.activityhistorymgr.ActivityHistoryManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.entitymgr.EntityFields.ENTITY_TYPE_ORG;
import static dti.ci.entitymgr.EntityFields.ENTITY_TYPE_PERSON;
import static dti.ci.helpers.ICIEntityConstants.*;

/**
 * Helper class for adding an Entity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Feb 18, 2004
 */

/**
 * Revision Date    Revised By  Description
 * -------------------------------------------------------------
 * 04/01/2005       HXY         Removed singleton implementation.
 * 04/14/2005       HXY         Added transaction commit logic.
 * 04/20/2005       HXY         Created one instance DAO.
 * 02/24/2006       HXY         issue 56236 - premise address type county_code not null
 * added getCountyCode.
 * 08/27/2010       Kenny       Iss#110852. Modified createUserMessage
 * 04/28/2018       dpang       issue 192743 - eCS-eCIS Refactoring: Add Person/ Add Organization
 * 11/16/2018       Elvin       Issue 195835: grid replacement
 * -------------------------------------------------------------
 */

public class EntityAddManagerImpl implements EntityAddManager {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public EntityAddInfo saveEntity(Record inputRecord) {
        String methodName = "saveEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        Record result = getEntityDAO().saveEntity(inputRecord);
        EntityAddInfo entityAddInfo = getEntityAddInfoFromResult(result);

        if (!entityAddInfo.isEntityAdded()) {
            addMessage(entityAddInfo);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, new Object[]{entityAddInfo});
        }
        return entityAddInfo;
    }

    @Override
    public EntityAddInfo validateAddrAndSaveEntity(Record inputRecord, boolean shouldSaveActivityHist) {
        String methodName = "validateAddrAndSaveEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, shouldSaveActivityHist});
        }

        handleCountryFields(inputRecord);

        validateCountyCodeForPremiseAddress(inputRecord);

        Record entityInfoRec = getEntityInfoRecord(inputRecord);

        EntityAddInfo entityAddInfo = saveEntity(entityInfoRec);

        if (entityAddInfo.isEntityAdded() && shouldSaveActivityHist) {
            Record record = new Record();
            EntityFields.setEntityId(record, entityAddInfo.getEntityPK());
            Record entityRecord = getEntityDAO().loadEntityData(record);
            saveActivityHistForAddEntity(entityRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, new Object[]{entityAddInfo});
        }
        return entityAddInfo;
    }

    @Override
    public void saveActivityHistForAddEntity(Record inputRecord) {
        String methodName = "saveActivityHistForAddEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        String entityType = EntityFields.getEntityType(inputRecord).toUpperCase();
        if (StringUtils.isBlank(entityType) || !(ENTITY_TYPE_PERSON.equals(entityType) || ENTITY_TYPE_ORG.equals(entityType))) {
            return;
        }

        String activityDisplayInfo = "";
        switch (entityType) {
            case ENTITY_TYPE_PERSON:
                activityDisplayInfo = getActivityDisplayMsg(inputRecord, EntityFields.ENTITY_NAME_COMPUTED);
                break;

            case ENTITY_TYPE_ORG:
                activityDisplayInfo = getActivityDisplayMsg(inputRecord, EntityFields.ORGANIZATION_NAME);
                break;
        }

        getActivityHistoryManager().recordActivityHistory(
                "CIS",    // Always use CIS as the subSystemCode so the link opens the entity in CIS, in case this was called from the Entity Mini Popup within another application.
                "ENTITY", EntityFields.getClientId(inputRecord), EntityFields.getEntityId(inputRecord),
                "", activityDisplayInfo, "");

        l.exiting(getClass().getName(), methodName);
    }

    private String getActivityDisplayMsg(Record inputRecord, String fieldName) {
        if (!StringUtils.isBlank(inputRecord.getStringValueDefaultEmpty(fieldName))) {
            return getMessageManager().formatMessage("cs.cis.activityHistory.displayInformation", new String[]{(inputRecord.getStringValue(fieldName))});
        }

        return BLANK_STR;
    }

    /**
     * Get entity record to match the procedure parameters. Remove field prefix if necessary.
     *
     * @param inputRecord
     * @return
     */
    private Record getEntityInfoRecord(Record inputRecord) {
        String methodName = "getEntityInfoRecord";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record entityInfoRec = new Record();
        List<String> fieldNames = inputRecord.getFieldNameList();

        String fieldPrefix = null;
        for (String fieldName : fieldNames) {
            if (fieldName.startsWith(ENTITY_FIELD_PREFIX)) {
                fieldPrefix = ENTITY_FIELD_PREFIX;
            } else if (fieldName.startsWith(ADDRESS_FIELD_PREFIX)) {
                fieldPrefix = ADDRESS_FIELD_PREFIX;
            } else if (fieldName.startsWith(PHONENUMBER_FIELD_PREFIX)) {
                fieldPrefix = PHONENUMBER_FIELD_PREFIX;
            } else if (fieldName.startsWith(ENTITYCLASS_FIELD_PREFIX)) {
                fieldPrefix = ENTITYCLASS_FIELD_PREFIX;
            }

            //1. Except for specified fields, the entity info fields should have table prefix.
            if (fieldPrefix != null) {
                //2. Exclude fields in DIFFERED_BY_PREFIX_FIELD_LIST to distinguish columns of same names on different tables.
                if (DIFFERED_BY_PREFIX_FIELD_LIST.contains(fieldName)) {
                    entityInfoRec.setFieldValue(StringUtils.capitalizeRemovingDelimiter(fieldName), inputRecord.getStringValue(fieldName));
                } else {
                    entityInfoRec.setFieldValue(fieldName.substring(fieldPrefix.length()), inputRecord.getStringValue(fieldName));
                }
            }

            fieldPrefix = null;
        }

        setBlankFieldValueToN(inputRecord, "ssnVerifiedB");
        setBlankFieldValueToN(inputRecord, "federalTaxIDVerifiedB");
        entityInfoRec.setFieldValue("isPhnNumNotRltdToAddr", setBlankFieldValueToN(inputRecord, ICIPhoneNumberConstants.NOT_RELATED_TO_ADDR_ID));
        entityInfoRec.setFieldValue("isOkToSkipDupEnts", setBlankFieldValueToN(inputRecord, ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY));
        entityInfoRec.setFieldValue("isOkToSkipDupTaxIds", setBlankFieldValueToN(inputRecord, ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY));
        entityInfoRec.setFieldValue("includeTaxIdDupXmlDoc", YesNoFlag.Y);

        if (AddressFields.COUNTRY_CODE_USA.equals(entityInfoRec.getStringValueDefaultEmpty(AddressFields.COUNTRY_CODE)) &&
                "N".equals(entityInfoRec.getStringValueDefaultEmpty(AddressFields.USA_ADDRESS_B))) {
            entityInfoRec.setFieldValue(AddressFields.USA_ADDRESS_B, "Y");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, new Object[]{entityInfoRec});
        }
        return entityInfoRec;
    }

    private EntityAddInfo getEntityAddInfoFromResult(Record record) {
        String methodName = "getEntityAddInfoFromResult";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record});
        }

        EntityAddInfo addInfo = new EntityAddInfo();

        if ("Y".equals(record.getStringValue("entityCreated"))) {
            addInfo.setEntityAdded(true);
            addInfo.setEntityPK(record.getStringValue("newEntityId"));
            addInfo.setClientId(record.getStringValue("newEntityClientId"));
        }

        if (record.hasFieldValue("entityDupCount")) {
            addInfo.setEntityDupCount(record.getLongValue("entityDupCount"));
        }
        if (record.hasFieldValue("taxIdDupCount")) {
            addInfo.setTaxIDDupCount(record.getLongValue("taxIdDupCount"));
        }
        if (record.hasFieldValue("mergedDupCount")) {
            addInfo.setMergedDupCount(record.getLongValue("mergedDupCount"));
        }
        addInfo.setEntityDupsXmlDocStr(record.getStringValue("entityDupXmlDoc"));
        addInfo.setTaxIDDupsXmlDocStr(record.getStringValue("taxIdDupXmlDoc"));
        addInfo.setMergedDupsXmlDocStr(record.getStringValue("mergedDupXmlDoc"));
        addInfo.setUserCanDupTaxID("Y".equals(record.getStringValue("userCanDupTaxID")));
        addInfo.setDupTaxIDSysParm(record.getStringValue("dupTaxIDSysParm"));

        setDupsDataForEntityAddInfo(addInfo);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, new Object[]{addInfo});
        }
        return addInfo;
    }

    private void setDupsDataForEntityAddInfo(EntityAddInfo addInfo) {
        String methodName = "setDupsDataForEntityAddInfo";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{addInfo});
        }

        List<String> dupList = null;
        boolean includeDupTaxID = true;

        if ("Y".equalsIgnoreCase(getSysParmProvider().getSysParm(ICIEntityConstants.HIDE_TAX_ID_PROPERTY))) {
            includeDupTaxID = false;
        }

        DefaultHandler errHandler = new DefaultHandler();

        // Put info about the duplicates found into the entity add info object.
        try {
            Document entDupsDoc = XMLUtils.getDocument(addInfo.getEntityDupsXmlDocStr(), errHandler);
            dupList = getEntityDupListFromXMLDoc(entDupsDoc, includeDupTaxID);
            addInfo.setEntityDupsTruncated(getIsEntityDupListTruncated(entDupsDoc));
            addInfo.setEntityDupsInfo(dupList);

            Document taxIDDupsDoc = XMLUtils.getDocument(addInfo.getTaxIDDupsXmlDocStr(), errHandler);
            dupList = getEntityDupListFromXMLDoc(taxIDDupsDoc, includeDupTaxID);
            addInfo.setTaxIdDupsTruncated(getIsEntityDupListTruncated(taxIDDupsDoc));
            addInfo.setTaxIDDupsInfo(dupList);

            Document mergedDupsDoc = XMLUtils.getDocument(addInfo.getMergedDupsXmlDocStr(), errHandler);
            dupList = getEntityDupListFromXMLDoc(mergedDupsDoc, includeDupTaxID);
            addInfo.setMergedDupsTruncated(getIsEntityDupListTruncated(mergedDupsDoc));
            addInfo.setMergedDupsInfo(dupList);
        } catch (Exception e) {
            l.throwing(this.getClass().getName(), "getEntityAddInfoFromResult", e);
            throw new AppException(e.getMessage());
        }

        l.exiting(getClass().getName(), methodName);
    }

    @Override
    public List<String> getEntityDupListFromXMLDoc(Document xmlDoc, boolean includeDupTaxID) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityDupListFromXMLDoc", new Object[]{xmlDoc, includeDupTaxID});
        }

        List<String> entities = new ArrayList();
        if (xmlDoc == null) {
            return entities;
        }
        NodeList nodeList = null;
        try {
            // Get the <duplicate> nodes.
            nodeList = xmlDoc.getElementsByTagName(DUP_REC_TAG);
        } catch (Exception e) {
            l.throwing(getClass().getName(), "getEntityDupListFromXMLDoc", e);
            return entities;
        }

        if (nodeList == null || nodeList.getLength() == 0) {
            return entities;
        }

        String ignoreDupAddr1 = getSysParmProvider().getSysParm("CS_CLT_DUP_ADDR1", "IGNORE");
        String ignoreDupAddr2 = getSysParmProvider().getSysParm("CS_CLT_DUP_ADDR2", "IGNORE");
        String ignoreDupZipcode = getSysParmProvider().getSysParm("CS_CLT_DUP_ZIPCODE", "IGNORE");
        String ignoreDupLicense = getSysParmProvider().getSysParm("CS_CLT_DUP_LICENSE", "IGNORE");
        String ignoreDupEmail = getSysParmProvider().getSysParm("CS_CLT_DUP_EMAIL", "IGNORE");

        String fullNameLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.fullName.label");
        String clientIdLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.clientId.label");
        String taxIdLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.taxId.label");
        String addr1Label = getMessageManager().formatMessage("ci.entity.message.duplicate.addr1.label");
        String addr2Label = getMessageManager().formatMessage("ci.entity.message.duplicate.addr2.label");
        String cityStateLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.cityState.label");
        String zipcodeLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.zipcode.label");
        String licenseLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.license.label");
        String emailAddressLabel = getMessageManager().formatMessage("ci.entity.message.duplicate.emailAddress.label");

        for (int i = 0; i < nodeList.getLength(); i++) {
            String clientID = "";
            String taxID = "";
            String addr1 = "";
            String addr2 = "";
            String cityState = "";
            String fullName = "";
            String zipcode = "";
            String license = "";
            String email = "";
            String nodeName = "";

            try {
                // Within each <duplicate> node, get the data elements for each dup.
                NodeList childNodes = nodeList.item(i).getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node nInnerElem = childNodes.item(j);
                    nodeName = nInnerElem.getNodeName();
                    Node firstChild = ((Element) nInnerElem).getFirstChild();

                    if (firstChild != null && (firstChild.getNodeType() == Node.CDATA_SECTION_NODE || firstChild.getNodeType() == Node.TEXT_NODE)) {
                        String value = firstChild.getNodeValue();
                        switch (nodeName) {
                            case CLIENT_ID_TAG:
                                clientID = value;
                                break;
                            case TAX_ID_TAG:
                                taxID = value;
                                break;
                            case FULL_NAME_TAG:
                                fullName = value;
                                break;
                            case CITY_ADDR1_TAG:
                                addr1 = value;
                                break;
                            case CITY_ADDR2_TAG:
                                addr2 = value;
                                break;
                            case CITY_STATE_TAG:
                                cityState = value;
                                break;
                            case CITY_ZIPCODE_TAG:
                                zipcode = value;
                                break;
                            case CITY_LICENSE_TAG:
                                license = value;
                                break;
                            case EMAIL_TAG:
                                email = value;
                                break;
                        }
                    }
                }

                StringBuilder entDescBuilder = new StringBuilder();
                entDescBuilder.append(fullNameLabel).append(": ").append(fullName).append(
                getLabelValueMsg(clientIdLabel, clientID));

                if (includeDupTaxID) {
                    entDescBuilder.append(getLabelValueMsg(taxIdLabel, taxID));
                }

                if (!ignoreDupAddr1.equalsIgnoreCase("IGNORE")) {
                    entDescBuilder.append(getLabelValueMsg(addr1Label, addr1));
                }

                if (!ignoreDupAddr2.equalsIgnoreCase("IGNORE")) {
                    entDescBuilder.append(getLabelValueMsg(addr2Label, addr2));
                }

                entDescBuilder.append(getLabelValueMsg(cityStateLabel, cityState));

                if (!ignoreDupZipcode.equalsIgnoreCase("IGNORE")) {
                    entDescBuilder.append(getLabelValueMsg(zipcodeLabel, zipcode));
                }

                if (!ignoreDupLicense.equalsIgnoreCase("IGNORE")) {
                    entDescBuilder.append(getLabelValueMsg(licenseLabel, license));
                }

                if (ignoreDupEmail.equalsIgnoreCase("EXACT")) {
                    entDescBuilder.append(getLabelValueMsg(emailAddressLabel, email));
                }

                entities.add(entDescBuilder.toString());
            } catch (Exception e) {
                l.throwing(getClass().getName(), "getEntityDupListFromXMLDoc", e);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityDupListFromXMLDoc", entities);
        }
        return entities;
    }

    private String getLabelValueMsg(String label, String value) {
        return " - " + label + ":  " + value;
    }

    /**
     * If Entity Dup List Truncated
     *
     * @param xmlDoc
     * @return
     */
    private boolean getIsEntityDupListTruncated(Document xmlDoc) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getIsEntityDupListTruncated", new Object[]{xmlDoc});
        }

        if (xmlDoc == null) {
            return false;
        }
        // Get the <isTruncated> node
        try {
            NodeList isTruncatedNodeList = xmlDoc.getElementsByTagName("isTruncated");

            if (isTruncatedNodeList != null && isTruncatedNodeList.getLength() >= 1) {
                if (YesNoFlag.getInstance(isTruncatedNodeList.item(0).getFirstChild().getNodeValue()).booleanValue()) {
                    return true;
                }
            }
        } catch (Exception e) {
            l.throwing(getClass().getName(), "getIsEntityDupListTruncated", e);
            return false;
        }
        l.exiting(getClass().getName(), "getIsEntityDupListTruncated");
        return false;
    }

    /**
     * Create a message telling the user what happened when saving entity.
     *
     * @param addInfo Object with info on what happened with entity add.
     */
    public void addMessage(EntityAddInfo addInfo) {
        String methodName = "addMessage";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{addInfo});
        }
        if (addInfo == null) {
            return;
        }

        StringBuilder msgBuilder = new StringBuilder();
        if (!addInfo.isEntityAdded()) {
            msgBuilder.append(getMessageManager().formatMessage("ci.entity.message.data.not.saved")).append(TWO_SPACES);
        }

        msgBuilder.append(getOneOrMoreDupsFoundMessage(addInfo));
        if (addInfo.isMergedDupsTruncated()) {
            msgBuilder.append(getMessageManager().formatMessage("ci.entity.message.display.duplicates.after.truncate",
                    new Integer[]{addInfo.getDisplayedMergedDupCount()})).append(" ");
        }
        msgBuilder.append(getTaxIdBasedDupsMessage(addInfo));
        msgBuilder.append(getDupsIgnoreMessage(addInfo));
        msgBuilder.append(getDuplicatesListMessage(addInfo));

        if (msgBuilder.length() > 0) {
            getMessageManager().addInfoMessage("ci.entity.message.duplicate.info", new String[]{msgBuilder.toString()});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addMessage", msgBuilder);
        }
    }

    private String getOneOrMoreDupsFoundMessage(EntityAddInfo addInfo) {
        String msg = BLANK_STR;
        //1 dup found, not a tax ID dup
        if (addInfo.getMergedDupCount() == 1 && addInfo.getMergedDupCount() > addInfo.getTaxIDDupCount()) {
            msg = getMessageManager().formatMessage("ci.entity.message.one.duplicate.found");

        } else if (addInfo.getMergedDupCount() > 1) {
            msg = getMessageManager().formatMessage("ci.entity.message.more.than.one.duplicates.found", new Long[]{addInfo.getMergedDupCount()});
        }

        //Add two spaces for format
        return BLANK_STR.equals(msg) ? msg : msg + TWO_SPACES;
    }

    private String getTaxIdBasedDupsMessage(EntityAddInfo addInfo) {
        String msg = BLANK_STR;
        if (addInfo.getTaxIDDupCount() == 1) {
            msg = getMessageManager().formatMessage("ci.entity.message.one.taxId.duplicate.found");
        } else if (addInfo.getTaxIDDupCount() > 1) {
            if (addInfo.getMergedDupCount() > 1) {
                msg = getMessageManager().formatMessage("ci.entity.message.all.duplicates.count", new Long[]{addInfo.getMergedDupCount()}) + " ";
            }

            msg += getMessageManager().formatMessage("ci.entity.message.taxId.based.duplicates.count", new Long[]{addInfo.getTaxIDDupCount()});
        }

        return BLANK_STR.equals(msg) ? msg : msg + TWO_SPACES;
    }

    private String getDupsIgnoreMessage(EntityAddInfo addInfo) {
        String msg = BLANK_STR;
        String ignoreDupsSaveMsg = getMessageManager().formatMessage("ci.entity.message.save.ignore.duplicate");
        String checkAgainMsg = getMessageManager().formatMessage("ci.entity.message.duplicate.checkAgain.after.change");

        if ((addInfo.getMergedDupCount() >= 1 || addInfo.getEntityDupCount() >= 1) && addInfo.getTaxIDDupCount() == 0) {
            msg = ignoreDupsSaveMsg + checkAgainMsg;
        } else if (addInfo.getTaxIDDupCount() >= 1) {
            if (addInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_ERROR_VALUE)) {
                msg = getMessageManager().formatMessage("ci.entity.message.taxid.duplicate.error");
            } else if (addInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_WARNING_VALUE)) {
                msg = getMessageManager().formatMessage("ci.entity.message.taxid.duplicate.warning");
            } else if (addInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_PROFILE_VALUE)) {
                if (addInfo.isUserCanDupTaxID()) {
                    msg = getMessageManager().formatMessage("ci.entity.message.taxid.duplicate.has.profile");
                } else {
                    msg = getMessageManager().formatMessage("ci.entity.message.taxid.duplicate.has.no.profile");
                }
            }
            if (!BLANK_STR.equals(msg)) {
                msg += TWO_SPACES + checkAgainMsg + TWO_SPACES;
            }
        }
        return msg;
    }

    private StringBuilder getDuplicatesListMessage(EntityAddInfo addInfo) {
        List<String> dupsInfo = addInfo.getMergedDupsInfo();
        StringBuilder msgBuilder = new StringBuilder();
        if (!dupsInfo.isEmpty()) {
            msgBuilder.append("<br/>");

            String title = BLANK_STR;
            if (dupsInfo.size() == 1) {
                title = getMessageManager().formatMessage("ci.entity.message.duplicate.title");
            } else {
                title = getMessageManager().formatMessage("ci.entity.message.duplicates.title");
            }

            msgBuilder.append(title).append("<br/>");
            for (String dupInfo : dupsInfo) {
                msgBuilder.append(dupInfo).append("<br/>");
            }
        }
        return msgBuilder;
    }

    private String setBlankFieldValueToN(Record inputRecord, String fieldId) {
        String fieldValue = "";
        fieldValue = inputRecord.getStringValueDefaultEmpty(fieldId);
        if (StringUtils.isBlank(fieldValue) || (fieldValue.charAt(0) != 'Y' && fieldValue.charAt(0) != 'N')) {
            fieldValue = "N";
        }

        return fieldValue;
    }

    private void validateCountyCodeForPremiseAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCountyCodeForPremiseAddress", new Object[]{inputRecord});
        }

        /* 56236: For add usa address, premise address type must have a valid county_code */
        if ("Y".equals(inputRecord.getStringValueDefaultEmpty("address_usaAddressB"))
                && "PREMISE".equalsIgnoreCase(inputRecord.getStringValueDefaultEmpty("address_addressTypeCode"))) {
            Record record = new Record();
            record.setFieldValue("zipCode", inputRecord.getStringValueDefaultEmpty("address_zipCode"));
            AddressFields.setCity(record, inputRecord.getStringValueDefaultEmpty("address_city"));
            AddressFields.setStateCode(record, inputRecord.getStringValueDefaultEmpty("address_stateCode"));

            Record outRecord = getAddressDAO().loadCountyCode(record);
            String returnCountyCode = AddressFields.getCountyCode(outRecord);

            if (StringUtils.isBlank(returnCountyCode, true)) {
                getMessageManager().addErrorMessage("error.usaaddress.addresstypepremise");
                throw new ValidationException("Invalid county code for premise address.");
            }
        }

        l.exiting(getClass().getName(), "validateCountyCodeForPremiseAddress");
    }

    private void handleCountryFields(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleCountryFields", new Object[]{inputRecord});
        }

        String countryCode = inputRecord.getStringValue("address_countryCode");
        String configuredUsaCountryCode = SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_USA, "USA");

        if (countryCode.equalsIgnoreCase(configuredUsaCountryCode)) {
            inputRecord.setFieldValue("address_usaAddressB", "Y");
        } else {
            inputRecord.setFieldValue("address_usaAddressB", "N");

            if (!isCountryCodeConfigured(countryCode)) {
                inputRecord.setFieldValue("address_province", inputRecord.getStringValueDefaultEmpty("address_otherProvince"));
            }
        }

        l.exiting(getClass().getName(), "handleCountryFields");
    }

    private boolean isCountryCodeConfigured(String countryCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCountryCodeConfigured", new Object[]{countryCode});
        }

        boolean result = false;
        String configuredCountryCodes = SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_CONFIG, "USA");
        String[] configuredCountryCodeArray = configuredCountryCodes.split(",");
        for (String configuredCountryCode : configuredCountryCodeArray) {
            if (configuredCountryCode.equals(countryCode)) {
                result = true;
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCountryCodeConfigured", result);
        }
        return result;
    }

    public void verifyConfig() {
        if (getEntityDAO() == null) {
            throw new ConfigurationException("The required property 'entityDAO' is missing.");
        }

        if (getAddressDAO() == null) {
            throw new ConfigurationException("The required property 'addressDAO' is missing.");
        }

        if (getActivityHistoryManager() == null) {
            throw new ConfigurationException("The required property 'activityHistoryManager' is missing.");
        }

        if (getMessageManager() == null) {
            throw new ConfigurationException("The required property 'messageManager' is missing.");
        }

        if (getSysParmProvider() == null) {
            throw new ConfigurationException("The required property 'sysParmProvider' is missing.");
        }
    }

    public EntityDAO getEntityDAO() {
        return m_entityDAO;
    }

    public void setEntityDAO(EntityDAO entityDAO) {
        this.m_entityDAO = entityDAO;
    }

    public AddressDAO getAddressDAO() {
        return m_addressDAO;
    }

    public void setAddressDAO(AddressDAO addressDAO) {
        this.m_addressDAO = addressDAO;
    }

    public ActivityHistoryManager getActivityHistoryManager() {
        return m_activityHistoryManager;
    }

    public void setActivityHistoryManager(ActivityHistoryManager activityHistoryManager) {
        this.m_activityHistoryManager = activityHistoryManager;
    }

    public MessageManager getMessageManager() {
        return m_messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.m_messageManager = messageManager;
    }

    public SysParmProvider getSysParmProvider() {
        return m_sysParmProvider;
    }

    public void setSysParmProvider(SysParmProvider sysParmProvider) {
        this.m_sysParmProvider = sysParmProvider;
    }

    private EntityDAO m_entityDAO;
    private AddressDAO m_addressDAO;
    private ActivityHistoryManager m_activityHistoryManager;
    private MessageManager m_messageManager;
    private SysParmProvider m_sysParmProvider;

    private static final String ENTITY_FIELD_PREFIX = "entity_";
    private static final String ADDRESS_FIELD_PREFIX = "address_";
    private static final String PHONENUMBER_FIELD_PREFIX = "phoneNumber_";
    private static final String ENTITYCLASS_FIELD_PREFIX = "entityClass_";
    private static final String TWO_SPACES = "  ";
    private static final String BLANK_STR = "";

    private static final List<String> DIFFERED_BY_PREFIX_FIELD_LIST =
            Arrays.asList("address_effectiveFromDate",
                    "address_effectiveToDate",
                    "address_legacyDataId",
                    "entity_legacyDataId",
                    "entityClass_effectiveFromDate",
                    "entityClass_effectiveToDate");
}
