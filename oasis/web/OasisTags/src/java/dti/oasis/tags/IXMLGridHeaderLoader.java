package dti.oasis.tags;

import java.util.HashMap;
import java.sql.Connection;

/**
 * Defines interface to load a XMLGridHeader object from some source.
 * It provides some constants to be used by any class that implements
 * this interface and utilizes XML as the source.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Aug 1, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
 */

public interface IXMLGridHeaderLoader {

    /**
     * http://java.sun.com/xml/jaxp/properties/schemaLanguage
     */
    static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /**
     * http://www.w3.org/2001/XMLSchema
     */
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    /**
     * http://java.sun.com/xml/jaxp/properties/schemaSource
     */
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * XMLHeader.xsd
     */
    static final String schemaSource = "XMLHeader.xsd";

     /**
     * Loads XMLGridHeader from XML Document found in headerFileName
     * @param headerFileName name of XML file
     * @param fields OasisFields object as HashMap. Used if an entry in the XML
     * file contains a fieldname element
     * @param conn JDBC Connection used in case the XML file contains a listsql element.
     * @throws Exception
     */
    public void load(String headerFileName, HashMap fields, Connection conn) throws Exception;

}
