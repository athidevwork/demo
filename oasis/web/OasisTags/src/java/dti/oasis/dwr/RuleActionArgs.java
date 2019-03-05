package dti.oasis.dwr;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xpath.XPathAPI;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 4:15:51 PM
 * To change this template use File | Settings | File Templates.
 *
 * Class to collect and supply action aguments.
 *Action argument types can be divided into 3 groups:
 * - context related (user entity pk) - not spefied in meta-data model but added based on type of action
 * - form related (claim pk, policy pk, etc)
 * - constant values
 * Action arguments meta-data model must contain the following:
 * -- procedure parameter name corresponding to the names set in DwrConstants
 * -- form field or grid field name of parameter value should be gathered from the form
 * -- or parameter value (e.g. note message)
 *
 */
public class RuleActionArgs {
    private ArrayList args;  // collection of form field values returned by matcher
    private LinkedHashMap argTemplate;// derived from mdFormParams
    private LinkedHashMap mdFields; // meta-data parameter name- field name pairs
    private LinkedHashMap mdConst; // meta-data parameter name - constant value


    protected void setMdFields(LinkedHashMap mdFields) {
        this.mdFields = mdFields;
        setArgTemplate();
    }

    protected void setMdConst(LinkedHashMap mdConst) {
        this.mdConst = mdConst;
    }

    protected LinkedHashMap getMdConst(){
        return mdConst;
    }
    protected LinkedHashMap getMdFields(){
        return mdFields;
    }
    /**
     *
     * @param mp is meta-data collection of param-names
     * KEY - PARAMETER NAME
     * Value - form field name
     */
    private void setArgTemplate(){
        argTemplate = mdFields;
   }


    /**
     * Obtain form field values and populate parameters
     * @param currentXml
     * @param origXml
     */
    public void setArgs(String currentXml, String origXml){
        Matcher matcher = new Matcher();
        args = matcher.match(currentXml, origXml, argTemplate);
        Iterator mt = args.iterator();
    }

    /**
     * Transform to collection (array list) of maps
     * Each map should have key - procedure parameter name
     * value should be set either to field value collected from form or constant from metadata.
     * @return
     */
    public LinkedHashMap getActionArgs(){

        LinkedHashMap temp = mdFields;  //param-field name pairs
        //Expect only one element returned from matcher
        if(args.size()>0){
        LinkedHashMap mp = (LinkedHashMap)args.get(0); //field-value pairs
            temp.putAll(mdFields);
            temp.putAll(mdConst);
        }
        return temp;
    }
}
