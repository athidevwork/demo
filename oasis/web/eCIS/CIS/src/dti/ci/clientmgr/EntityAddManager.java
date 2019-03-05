package dti.ci.clientmgr;

import dti.oasis.recordset.Record;
import org.w3c.dom.Document;

import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/24/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityAddManager {

    /**
     * Save entity/address/phone number/entity_class
     *
     * @param inputRecord
     * @return
     */
    EntityAddInfo saveEntity(Record inputRecord);

    /**
     * Validate county code for premise address and save entity data.
     * Save activity history if necessary:
     *
     * @param inputRecord
     * @param shouldSaveActivityHist
     * @return
     */
    EntityAddInfo validateAddrAndSaveEntity(Record inputRecord, boolean shouldSaveActivityHist);

    /**
     * Save activityHistory after adding entity
     *
     * @param inputRecord
     */
    void saveActivityHistForAddEntity(Record inputRecord);


    /**
     * Converts an XML document with data about possible duplicate entities to an
     * ArrayList of Strings.
     *
     * Xml doc is in the following format:
     * <duplicates>
     *   <duplicate>
     *     <entityPK><![CDATA[1049344]]></entityPK>
     *     <clientID><![CDATA[AH1043]]></clientID>
     *     <taxID><![CDATA[999999999]]></taxID>
     *     <fullName><![CDATA[Wood, Thomas F,]]></fullName>
     *     <addr1><![CDATA[]]></addr1>
     *     <addr2><![CDATA[]]></addr2>
     *     <cityState><![CDATA[Somewhere, MA]]></cityState>
     *     <zipcode><![CDATA[]]></zipcode>
     *     <license><![CDATA[]]></license>
     *     <email><![CDATA[]]></email>
     *   </duplicate>
     *</duplicates>
     *
     * @param xmlDoc          XML document with duplicates.
     * @param includeDupTaxID Whether or not to include tax ID with entity duplicates info.
     * @return List<String> - String list for duplicate entities. String will be in format like
     *                        Full name: Wood, Thomas F, - Client ID: AH1043 - Tax ID: 999999999 - City, State: Somewhere, MA
     */
    List<String> getEntityDupListFromXMLDoc(Document xmlDoc, boolean includeDupTaxID);

}
