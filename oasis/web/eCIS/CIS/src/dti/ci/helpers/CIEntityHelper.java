package dti.ci.helpers;

import dti.ci.entityclassmgr.EntityClassFields;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper superclass for adding and modifying an Entity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Feb 18, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         09/08/2009       Kenney      Modified for issue 97135
 *         08/27/2010       Kenny       Iss#110852. Added getIsEntityDupListTruncated
 *         04/03/2013       kshen       Issue 141547
 *         09/06/2013       Parker      Issue 146181.Support multi classfication when add an entity
 *         01/22/2015       bzhu        Issue 159510.Change 'state' to 'State' in message for consistency.
 *         02/07/2017       jld         Issue 181813. Corrections and additions for missing fields.
 *         08/14/2017       dpang       Issue 187318. Change to import legacy_data_id.
 *         ---------------------------------------------------
 */

public abstract class CIEntityHelper extends CIHelper implements ICIEntityConstants {

    /**
     * Creates an XML document string from a Map containing data about a new
     * or existing entity.
     *
     * @param inputMap Map with data about the entity.
     * @return String - The XML document.
     */
    public String mapToXmlDocString(Map inputMap) {
        Map newMap = this.transformMap(inputMap);
        String xmlDoc = XMLUtils.mapToXML(newMap);
        return xmlDoc;
    }

    /**
     * Iterates through a Map created from a form and creates a new
     * Map without the "process" and "msg" keys.  Also calls
     * shortenFieldID to make the key shorter in length.
     *
     * @param inputMap The Map created from a form.
     * @return Map - The transformed Map.
     */
    private Map transformMap(Map inputMap) {
        Map outputMap = new HashMap();
        Iterator itr = inputMap.keySet().iterator();
        while (itr.hasNext()) {
            String mapKey = (String) itr.next();
            if (mapKey != null && !mapKey.equals(PROCESS_PROPERTY) &&
                    !mapKey.equals(MSG_PROPERTY)) {
                String mapValue = "";
                if (SUFFIX_NAME_ID.equals(mapKey)||PROFESSIONAL_DESIGNATION_ID.equals(mapKey)|| EntityClassFields.ENT_CLS_CODE_ID.equals(mapKey)) {
                    String[] mapValues = (String[]) inputMap.get(mapKey);
                    for (int i = 0; i < mapValues.length; i++)
                        mapValue += mapValues[i] + ",";
                    if (mapValue.length() > 0)
                        mapValue = mapValue.substring(0, mapValue.length() - 1);
                } else
                    mapValue = (String) inputMap.get(mapKey);

                if (!StringUtils.isBlank(mapValue)) {
                    outputMap.put(this.shortenFieldID(mapKey), mapValue);
                }
            }
        }
        return outputMap;
    }

    /**
     * Shortens a field ID by removing "entity_" or "address_" or
     * "phoneNumber_" from the beginning of the string.
     *
     * @param fieldID Input string.
     * @return String - shortened field ID
     */
    private String shortenFieldID(String fieldID) {
        if (StringUtils.isBlank(fieldID)) {
            return "";
        }
        /* There are two field IDs called "legacyDataID":  entity_legacyDataID and
           address_legacyDataID.  So don't truncate the field ID because both may be
           tags in an XML document for entity add.
        */
        else if (fieldID.indexOf("legacyDataID") >= 0) {
            return fieldID;
        }
        /* There are at least four field IDs called "effectiveFromDate"
           or "effectiveToDate":  address_effectiveFromDate, address_effectiveToDate,
           entityClass_effectiveFromDate, and entityClass_effectiveToDate.
           So don't truncate the field ID because both may be
           tags in an XML document for entity add.
        */
        else if (fieldID.indexOf("effectiveFromDate") >= 0 || fieldID.indexOf("effectiveToDate") >= 0) {
            return fieldID;
        } else if (fieldID.length() >= 8 && fieldID.substring(0, 7).equalsIgnoreCase("entity_")) {
            return fieldID.substring(7);
        } else if (fieldID.length() >= 9 && fieldID.substring(0, 8).equalsIgnoreCase("address_")) {
            return fieldID.substring(8);
        } else if (fieldID.length() >= 13 && fieldID.substring(0, 12).equalsIgnoreCase("phoneNumber_")) {
            return fieldID.substring(12);
        } else if (fieldID.length() >= 13 && fieldID.substring(0, 12).equalsIgnoreCase("entityClass_")) {
            return fieldID.substring(12);
        } else {
            return fieldID;
        }
    }

    /**
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
     * If Entity Dup List Truncated
     *
     * @param xmlDoc
     * @return
     */
    public boolean getIsEntityDupListTruncated(Document xmlDoc) {
        String methodName = "getIsEntityDupListTruncated";
        String methodDesc = "Class " + this.getClass().getName() +
                ", method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{xmlDoc});
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
            lggr.info(methodDesc + ":  exception occurred " +
                    "parsing XML doc:  " + e.toString());
            return false;
        }
        lggr.exiting(this.getClass().getName(), methodName, false);
        return false;
    }
}
