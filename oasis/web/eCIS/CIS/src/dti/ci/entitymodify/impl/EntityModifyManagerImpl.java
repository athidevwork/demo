package dti.ci.entitymodify.impl;

import dti.ci.core.recordset.RecordHelper;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitymgr.dao.EntityDAO;
import dti.ci.entitymodify.EntityModifyManager;
import dti.ci.entitymodify.EntityModifyInfo;
import dti.cs.notemgr.NoteManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.entitymgr.EntityConstants.*;


/**
 * Helper class for modifying an Entity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 10, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------
 *         03/31/2005       HXY         Removed singleton implementation.
 *         04/14/2005       HXY         Added connection commit logic.
 *         04/18/2005       HXY         Created one instance DAO.
 *         08/02/2006       ligj        Add Loss DAO.
 *         02/16/2007       kshen       Added DbaHistory DAO.
 *                                      Added method retrieveEntityDbaData. (iss68160)
 *         07/02/2007       FWCH        Added method updateEntityType.
 *         03/31/2008       wer         Always use CIS as the subSystemCode when recording activity history so the link opens the entity in CIS, in case this was called from the Entity Mini Popup within another application.
 *         03/02/2009       Leo         Issue 87902.
 *         10/16/2009       hxk         Added userHasExpertWitnessClass  and setExpWitTabVisibility functions for issue 97591.
 *         08/27/2010       Kenny       Iss#110852.
 *         04/18/2013       bzhu        Issue 139501.
 *         06/02/2018       ylu         Issue 109088: refactor update to use new note's function
 *         -----------------------------------------------------------------
 */

public class EntityModifyManagerImpl implements EntityModifyManager {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Create a message telling the user what happened on the save of entity add.
     *
     * @param modInfo Object with info on what happened with entity modify.
     * @return String - Message to display to user.
     */
    public String generateDupMessageForUser(EntityModifyInfo modInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateDupMessageForUser", new Object[]{modInfo});
        }

        if (modInfo == null) {
            return "";
        }
        String message = "";
        if (!modInfo.isEntityUpdated() &&
                modInfo.getEmailDupCount() >= 1 ) {
            l.logp(Level.FINE, getClass().getName(), "generateDupMessageForUser", ":  email address dups were found.");
            message += MessageManager.getInstance().formatMessage("ci.entity.message.email.duplicate.count", new Long[]{modInfo.getEmailDupCount()});

            if (modInfo.isEmailDupsTruncated())
                message += MessageManager.getInstance().formatMessage("ci.entity.message.duplicate.truncated", new Integer[]{modInfo.getDisplayeEmailDupsCount()});

            message += MessageManager.getInstance().formatMessage("ci.entity.message.email.duplicate.checkagain");
            //populate the detail duplicate List into message
            message += populateDupListItemInMessage(modInfo.getEmailDupsInfo());
        } else if (!modInfo.isEntityUpdated() &&
                modInfo.getTaxIDDupCount() >= 1) {
            message = MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.count", new Long[]{modInfo.getTaxIDDupCount()});

            if (modInfo.isTaxIdDupsTruncated())
                message += MessageManager.getInstance().formatMessage("ci.entity.message.duplicate.truncated", new Integer[]{modInfo.getDisplayedTaxIdDupsCount()});

            String messageOptions ="";
            if (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_ERROR_VALUE)) {
                messageOptions = MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.error");
            } else if (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_WARNING_VALUE)) {
                messageOptions = MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.warning");
            } else if (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_PROFILE_VALUE)) {
                if (modInfo.isUserCanDupTaxID()) {
                    messageOptions = MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.has.profile");
                } else {
                    messageOptions = MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.has.no.profile");
                }
            }
            message += MessageManager.getInstance().formatMessage("ci.entity.message.taxid.duplicate.checkagain", new String[]{messageOptions});
            //populate the detail duplicate List into message
            message += populateDupListItemInMessage(modInfo.getTaxIDDupsInfo());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateDupMessageForUser", message);
        }
        return message;
    }

    /**
     * populate duplicate TaxId list into String
     * @param dupList
     * @return
     */
    public String populateDupListItemInMessage(ArrayList dupList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "populateDupListItemInfoInMessage", new Object[]{dupList});
        }

        String dupListAsStr = "";
        for (int i =0; i < dupList.size(); i++) {
            Object item = dupList.get(i);
            if (item instanceof String) {
                dupListAsStr += "<br>"+ (String) item;
            }
        }

        if (dupList.size() == 1) {
            dupListAsStr = (MessageManager.getInstance().formatMessage("ci.entity.duplicate.form.title")) + dupListAsStr;
        } else if (dupList.size() >=2) {
            dupListAsStr = (MessageManager.getInstance().formatMessage("ci.entity.duplicates.form.title")) + dupListAsStr;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "populateDupListItemInfoInMessage", dupListAsStr);
        }

        return dupListAsStr;
    }


    /**
     * Check whether the client has active policy associated
      * @param record
     * @return
     */
    public String getClientDiscardPolCheck(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClientDiscardPolCheck", new Object[]{record});
        }

        boolean hasActivePolicy = getEntityDAO().getClientDiscardPolCheck(record);
        String discardedMsgKey = "";

        if (hasActivePolicy) {
            String discardClientSysParamValue = SysParmProvider.getInstance().getSysParm(DISCARD_CLIENT_SYS_PARAM, DISCARD_CLIENT_SYS_PARAM_WARNING);
            if (DISCARD_CLIENT_SYS_PARAM_ERROR.equals(discardClientSysParamValue))
                discardedMsgKey = CLIENT_DISCARDED_MSG + DISCARD_CLIENT_SYS_PARAM_ERROR;
            else if (DISCARD_CLIENT_SYS_PARAM_IGNORE.equals(discardClientSysParamValue))
                discardedMsgKey = CLIENT_DISCARDED_MSG + DISCARD_CLIENT_SYS_PARAM_IGNORE;
            else
                discardedMsgKey = CLIENT_DISCARDED_MSG + DISCARD_CLIENT_SYS_PARAM_WARNING;
        } else {
            discardedMsgKey = CLIENT_DISCARDED_MSG;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClientDiscardPolCheck", discardedMsgKey);
        }
        return discardedMsgKey;
    }

    /**
     * get the flag to show or hide ExpWitness tab menu, which is determine by sysparm and profile
     * @param record
     * @return
     */
    public boolean getExpWitTabVisibilityflag(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExpWitTabVisibilityflag", new Object[]{record});
        }

        // get syspam value, default is Not hide
        String hideExperWitnessParm = SysParmProvider.getInstance().getSysParm(SHOW_HIDE_EXP_WIT,"N");
        boolean isShowExperWitnessB = true;

        if ("Y".equalsIgnoreCase(hideExperWitnessParm)) {
            isShowExperWitnessB = getClientHasExpertWitnessClass(record);;  //check if entity has profile
        }
        else {
            isShowExperWitnessB =  true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExpWitTabVisibilityflag", isShowExperWitnessB);
        }
        return isShowExperWitnessB;
    }

    /**
     * get if this client has Expert Witness classification.
     * @param record
     * @return
     */
    public boolean getClientHasExpertWitnessClass(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getHasExpertWitnessClass", new Object[]{record});
        }

        boolean isHasExpWitClassB = false;
        isHasExpWitClassB = getEntityDAO().getClientHasExpertWitnessClass(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getHasExpertWitnessClass", isHasExpWitClassB);
        }
        return isHasExpWitClassB;
    }

    /**
     * check if Entity Dup List Truncated and return flag
     *
     * @param xmlDoc
     * @return
     */
    public boolean getIsEntityDupListTruncated(Document xmlDoc) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getIsEntityDupListTruncated", new Object[]{xmlDoc,});
        }

        if (xmlDoc == null) {
            return false;
        }
        // Get the <isTruncated> node
        try {
            NodeList nlstIsTruncated = null;
            nlstIsTruncated = xmlDoc.getElementsByTagName("isTruncated");
            if (nlstIsTruncated != null) {
                if (nlstIsTruncated.getLength() >= 1)
                    if (YesNoFlag.getInstance(nlstIsTruncated.item(0).getFirstChild().getNodeValue()).booleanValue())
                        return true;
            }
        } catch (Exception e) {
            l.logp(Level.FINE, getClass().getName(), "getIsEntityDupListTruncated", ":  exception occurred " +
                    "parsing XML doc:  " + e.toString());
            return false;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getIsEntityDupListTruncated", false);
        }
        return false;
    }

    /**
     * Validate the entered policy number for reference number field
     *
     * @param record
     * @return
     */
    @Override
    public String validateReferenceNumberAsStr(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateReferenceNumberAsStr", new Object[]{record});
        }

        String out = "N";
        boolean isValid = false;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polNo", "referenceNumber"));
            boolean result1 = getEntityDAO().checkPolNo(record, mapping);
            boolean result2 = getEntityDAO().checkPolNoIsDuplicated(record);
            if (result1 && result2) {
                out = "Y";
                isValid = true;
            }
        }
        catch (Exception e) {
            l.logp(Level.WARNING, getClass().getName(), "validateReferenceNumberAsStr", ".validateRefNum:" + e.getMessage());
            throw new AppException(e.getMessage());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateReferenceNumberAsStr", out);
        }
        return out;
  }

    /**
     * load name history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadNameHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadNameHistory", new Object[]{inputRecord});
        }

        RecordSet rs = getEntityDAO().loadNameHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadNameHistory", rs);
        }

        return rs;
    }

    /**
     * load tax history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadTaxHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTaxHistory", new Object[]{inputRecord});
        }

        RecordSet rs = getEntityDAO().loadTaxHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTaxHistory", rs);
        }

        return rs;
    }

    /**
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadLossHistory(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadLossHistory", new Object[]{inputRecord});
        }

        RecordSet rs = getEntityDAO().loadLossHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadLossHistory", rs);
        }

        return rs;
    }

    /**
     * load DBA history
     * @param record
     * @return
     */
    @Override
    public RecordSet loadDbaHistory(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDbaHistory", new Object[]{record});
        }

        RecordSet rs = getEntityDAO().loadDbaHistory(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDbaHistory", rs);
        }

        return rs;
    }

    /**
     * load electronic data
     * @param record
     * @return
     */
    @Override
    public RecordSet loadEtdHistory(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEtdHistory", new Object[]{record});
        }

        RecordSet rs = getEntityDAO().loadEtdHistory(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEtdHistory", rs);
        }

        return rs;
    }

    /**
     * save entity data
     * @param record
     */
    @Override
    public void saveEntityData(Record record) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityData", new Object[]{record});
        }

        Record inputRecord = RecordHelper.removeRecordPrefix(record,"entity_");

        //set isOKToSkipDups flag to record,
        String okToSkipTaxIdDups = inputRecord.getStringValue(OK_TO_SKIP_TAX_ID_DUPS_PROPERTY,"N");
        if ("null".equalsIgnoreCase(okToSkipTaxIdDups)) {
            okToSkipTaxIdDups = "N";
        }
        inputRecord.setFieldValue(OK_TO_SKIP_TAX_ID_DUPS_PROPERTY, okToSkipTaxIdDups);

        //set inIncludeTaxId flag for duplist to record, default is Not hide TaxId in page
        String hideTaxIdSysParamVal = SysParmProvider.getInstance().getSysParm(HIDE_TAX_ID_PROPERTY,"N");
        boolean includeTaxId = true;
        if (YesNoFlag.getInstance(hideTaxIdSysParamVal).booleanValue()) {
            includeTaxId = false;
        }
        inputRecord.setFieldValue("includeTaxId", includeTaxId);

        //get if check dup emails flag into record, defaul is Not check
        String checkDupEmailParamVal = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_EMAIL","IGNORE");
        if ("EXACT".equalsIgnoreCase(checkDupEmailParamVal)) {
            inputRecord.setFieldValue("okToSkipDupEmail", "N"); //to check dup email
        }

        EntityModifyInfo modInfo = new EntityModifyInfo();
        Record retRecord = getEntityDAO().saveEntityData(inputRecord);

        //collect the data result into mod info object
        long taxIdDupCount = retRecord.getLongValue("taxIdDupCount");
        modInfo.setTaxIDDupCount(taxIdDupCount);

        String entityDupXmlDoc = retRecord.getStringValue("taxIdDupXmlDoc","");
        modInfo.setTaxIDDupsXmlDocStr(entityDupXmlDoc);

        String dupTaxIDSysParm = retRecord.getStringValue("dupTaxIdSysparm","");
        modInfo.setDupTaxIDSysParm(dupTaxIDSysParm);

        if (YesNoFlag.getInstance(retRecord.getStringValue("userCanDupTaxId")).booleanValue()) {
            modInfo.setUserCanDupTaxID(true);
        } else {
            modInfo.setUserCanDupTaxID(false);
        }

        long emailDupCount = retRecord.getLongValue("emailDupCount");
        modInfo.setEmailDupCount(emailDupCount);

        String emailDupXmlDoc = retRecord.getStringValue("emailDupXmlDoc","");
        modInfo.setEmailDupsXmlDocStr(emailDupXmlDoc);

        // set update success or fail flag:
        // If we found tax ID dups, then don't update the entity.
        // Prevent update with duplicate if parm is set to ERROR or if set to PROFILE and user does not have profile
        if (retRecord.getLongValue("rc") == -1 &&
                modInfo.getEmailDupCount() >= 1) {
            modInfo.setEntityUpdated(false); //set fail flag
        } else if (retRecord.getLongValue("rc") == -1 &&
                modInfo.getTaxIDDupCount() >= 1 &&
                (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_ERROR_VALUE)
                || modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_WARNING_VALUE)
                || (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_PROFILE_VALUE) && !modInfo.isUserCanDupTaxID())) ) {
            modInfo.setEntityUpdated(false); //set fail flag
        } else {
            modInfo.setEntityUpdated(true); //set success flag
        }

        // add duplicates list into the mod info object.
        DefaultHandler errHandler = new DefaultHandler();
        ArrayList dupList = null;
        if (modInfo.getTaxIDDupCount() >= 1) {
            try {
                Document taxIDDupsDoc = XMLUtils.getDocument(modInfo.getTaxIDDupsXmlDocStr(), errHandler);
                dupList = this.getEntityDupListFromXMLDoc(taxIDDupsDoc, includeTaxId);
                modInfo.setTaxIdDupsTruncated(getIsEntityDupListTruncated(taxIDDupsDoc));
                modInfo.setTaxIDDupsInfo(dupList);
            } catch (Exception e) {
                l.throwing(getClass().getName(), "saveEntityData", e);
                throw e;
            }
        }

        if (modInfo.getEmailDupCount() >= 1) {
            try {
                Document emailDDupsDoc = XMLUtils.getDocument(modInfo.getEmailDupsXmlDocStr(), errHandler);
                dupList = this.getEntityDupListFromXMLDoc(emailDDupsDoc, includeTaxId);
                modInfo.setEmailDupsTruncated(getIsEntityDupListTruncated(emailDDupsDoc));
                modInfo.setEmailDupsInfo(dupList);
            } catch (Exception e) {
                l.throwing(getClass().getName(), "saveEntityData", e);
                throw e;
            }
        }

        //add message for user to mod info object
        String userMessage = this.generateDupMessageForUser(modInfo);

        //output error message to user
        if (modInfo.getEmailDupCount() >=1) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",new String[]{userMessage});
            //modInfo.setUserMessage(userMessage);
            throw new ValidationException(userMessage);
        } else if  (isDupTaxIdNeedToBeDisplayed(modInfo)) {
            if (modInfo.isUserCanDupTaxID()) {
                //output warning message to user
                MessageManager.getInstance().addWarningMessage("ci.generic.error", new String[]{userMessage});
            } else {
                //output error message to user
                MessageManager.getInstance().addErrorMessage("ci.generic.error", new String[]{userMessage});
                throw new ValidationException(userMessage);
            }
        }

        l.exiting(getClass().getName(), "saveEntityData");

    }

    /**
     * determine if need display detail duplicate list to user
     * @param modInfo
     * @return
     */
    private boolean isDupTaxIdNeedToBeDisplayed(EntityModifyInfo modInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDupTaxIdNeedToBeDisplayed", new Object[]{modInfo});
        }

        boolean dupTaxIdtoDisplayB = false;

        if (!modInfo.isEntityUpdated() && modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_ERROR_VALUE)) {
            dupTaxIdtoDisplayB = true;
        }

        if (!modInfo.isEntityUpdated() && modInfo.getTaxIDDupCount() >= 1 && (modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_WARNING_VALUE) || modInfo.getDupTaxIDSysParm().equals(DUP_TAX_ID_SYS_PARM_PROFILE_VALUE)) ) {
            dupTaxIdtoDisplayB = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDupTaxIdNeedToBeDisplayed", dupTaxIdtoDisplayB);
        }
        return dupTaxIdtoDisplayB;
    }

    /**
     * change Entity Type
     * @param record
     */
    @Override
    public String changeEntityType(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeEntityType", new Object[]{record});
        }

        Record ret = getEntityDAO().changeEntityType(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeEntityType", ret);
        }
        return ret.getStringValue(EntityFields.ENTITY_NEW_NAME,"");
    }

    /**
     * Copy from CIEntityHelper.java as this Helper.java is no need
     *
     * Converts an XML document with data about possible duplicate entities to an
     * ArrayList of Strings.
     *
     * @param xmlDoc          XML document with duplicates.
     * @param includeDupTaxID Whether or not to include tax ID with entity duplicates info.
     * @return ArrayList - One String for each duplicate entity.
     */
    public ArrayList getEntityDupListFromXMLDoc(Document xmlDoc,
                                                boolean includeDupTaxID) {
        String methodName = "getEntityDupListFromXMLDoc";
        String methodDesc = "Class " + this.getClass().getName() +
                ", method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{xmlDoc, new Boolean(includeDupTaxID)});
        ArrayList entities = new ArrayList();
        if (xmlDoc == null) {
            return entities;
        }
        NodeList nlDups = null;
        try {
            // Get the <duplicate> nodes.
            nlDups = xmlDoc.getElementsByTagName(DUP_REC_TAG);
        }
        catch (Exception e) {
            lggr.info(methodDesc + ":  exception occurred " +
                    "getting " + DUP_REC_TAG + " elements by name from XML doc:  " +
                    e.toString());
            return entities;
        }
        if (nlDups == null) {
            return entities;
        }
        for (int i = 0; i < nlDups.getLength(); i++) {
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
            int arrayListIndex = 0;
            try {
                // Within each <duplicate> node, get the data elements for each dup.
                NodeList nlDupChildren = nlDups.item(i).getChildNodes();
                for (int j = 0; j < nlDupChildren.getLength(); j++) {
                    Node nInnerElem = nlDupChildren.item(j);
                    nodeName = nInnerElem.getNodeName();
                    Node nInnerElemChild = ((Element) nInnerElem).getFirstChild();
                    if (nInnerElemChild != null &&
                            (nInnerElemChild.getNodeType() == Node.CDATA_SECTION_NODE ||
                                    nInnerElemChild.getNodeType() == Node.TEXT_NODE)) {
                        if (nodeName.equals(CLIENT_ID_TAG)) {
                            clientID = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(TAX_ID_TAG)) {
                            taxID = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(FULL_NAME_TAG)) {
                            fullName = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(CITY_ADDR1_TAG)) {
                            addr1 = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(CITY_ADDR2_TAG)) {
                            addr2 = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(CITY_STATE_TAG)) {
                            cityState = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(CITY_ZIPCODE_TAG)) {
                            zipcode = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(CITY_LICENSE_TAG)) {
                            license = nInnerElemChild.getNodeValue();
                        } else if (nodeName.equals(EMAIL_TAG)) {
                            email = nInnerElemChild.getNodeValue();
                        }
                    }
                }
                String entDesc = "Full name: " + fullName + " - " +
                        "Client ID: " + clientID;
                if (includeDupTaxID) {
                    entDesc += " - Tax ID:  " + taxID;
                }
                String dupAddr1 = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_ADDR1", "IGNORE");
                if (!dupAddr1.equalsIgnoreCase("IGNORE")) {
                    entDesc += " - Addr1:  " + addr1;
                }
                String dupAddr2 = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_ADDR2", "IGNORE");
                if (!dupAddr2.equalsIgnoreCase("IGNORE")) {
                    entDesc += " - Addr2:  " + addr2;
                }
                entDesc += " - City, State:  " + cityState;
                String dupZipcode = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_ZIPCODE", "IGNORE");
                if (!dupZipcode.equalsIgnoreCase("IGNORE")) {
                    entDesc += " - Zip:  " + zipcode;
                }
                String dupLicense = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_LICENSE", "IGNORE");
                if (!dupLicense.equalsIgnoreCase("IGNORE")) {
                    entDesc += " - License:  " + license;
                }
                String dupEmail = SysParmProvider.getInstance().getSysParm("CS_CLT_DUP_EMAIL", "IGNORE");
                if (dupEmail.equalsIgnoreCase("EXACT")) {
                    entDesc += " - Email Address:  " + email;
                }
                entities.add(arrayListIndex, entDesc);
                arrayListIndex += 1;
            }
            catch (Exception e) {
                lggr.info(methodDesc + ":  exception occurred " +
                        "traversing through XML doc:  " + e.toString());
            }
        }
        lggr.exiting(this.getClass().getName(), methodName, entities);
        return entities;
    }

    /**
     * get entity's type for the given entity
     * @param record
     * @return
     */
    public String getEntityType(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityType", new Object[]{record});
        }

        String entityType = getEntityDAO().getEntityType(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityType", entityType);
        }

        return entityType;
    }

    public boolean getIfEntityHasNoteExistsB(String entityPk) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getIfEntityHasNoteExistsB", new Object[]{entityPk});
        }

        Record notesParms = new Record();
        notesParms.setFieldValue("sourceTableName", "Entity");
        notesParms.setFieldValue("sourceRecordId", entityPk);
        notesParms.setFieldValue("noteGroupCode", "ENTITY");
        notesParms.setFieldValue("noteCategoryCode", "ALL");

        RecordSet noteRs = getNoteManager().loadNoteList(notesParms);

        boolean noteExistsB = noteRs.getSize() > 0;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getIfEntityHasNoteExistsB", noteExistsB);
        }

        return noteExistsB;
    }

    public void verifyConfig() {
        if (getEntityDAO() == null) {
            throw new ConfigurationException("The required property 'entityDAO' is missing.");
        }

        if (getNoteManager() == null) {
            throw new ConfigurationException("The required property 'noteManager' is missing.");
        }
    }

    public EntityDAO getEntityDAO() {
        return m_entityDAO;
    }

    public void setEntityDAO(EntityDAO m_entityDAO) {
        this.m_entityDAO = m_entityDAO;
    }

    public NoteManager getNoteManager() { return m_noteManager;}

    public void setNoteManager(NoteManager m_noteManager) { this.m_noteManager = m_noteManager;}

    private EntityDAO m_entityDAO;

    private NoteManager m_noteManager;
}
