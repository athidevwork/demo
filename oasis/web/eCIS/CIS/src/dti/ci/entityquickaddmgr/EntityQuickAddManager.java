package dti.ci.entityquickaddmgr;

import dti.ci.clientmgr.EntityAddInfo;
import dti.oasis.recordset.Record;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * The business component of Quick Add Person.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  08/15/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityQuickAddManager {

    /**
     * Get the Record for matching prefix
     *
     * @param inputRecord
     * @return record
     */
    Record getRecordForPrefix(Record inputRecord, String prefix);


    /**
     * Save data of below tables and other info (e.g Education for person)
     *
     * Entity
     * Address
     * Phone
     * Entity Class
     * License
     * Denominator
     * Prior Carrier
     *
     * @param inputRecord
     * @return Record
     */
    EntityAddInfo saveAllEntity(Record inputRecord) throws Exception;

}
