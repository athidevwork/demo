package dti.oasis.dwr;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.apache.xpath.XPathAPI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import dti.oasis.error.ValidationException;
import dti.oasis.util.LogUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 30, 2009
 * Time: 5:35:01 PM
 * To change this template use File | Settings | File Templates.
 *
 * RuleConditionsArgs class is used to keep values of all arguments required by the rule condition.
 * Each rule condition contains several condition items.
 * Each condition item item could require several arguments.
 * Each argument should have name and position as specified by the condition function.
 *
 * There are possibly 2 collections of form fields that are used for condition arguments
 * such as collection of riginal form field values as well as current (updated by the user) values.
 *
 * The process will first take collection of current fields and create collection of arguments on the first run
 * The second run will fill original values into existing collection.
 *
 *Both runs will use argument template condArgs to match form fields with condition arguments.
 *This template will use distinc notion of currect and original values.
 *The requirement for original value is set in the rule meta-model
 * If required value is current than the condAgrs key will be set to the actual field name.
 * If required value is original than the condArgs key will be set to ORIGINAL_INDICATOR+actual field name
 *
 * Updatable grid could return multiple values for the same field.
 * In this case expression should be applied to all the values.
 *
 * Therefore, all args will be retuned as elements of an ArrayList regarless of number of values.
 * 
 *
 */
public class RuleConditionArgs {
    private ArrayList args;
    private LinkedHashMap argTemplate;
    Logger l;
    public void setArgTemplate(LinkedHashMap template){
        argTemplate = template;
    }

    public void setArgs(String currentXml, String origXml){
        l  = LogUtils.enterLog(getClass(), "setArgs");

        Matcher matcher = new Matcher();
        args = matcher.match(currentXml, origXml, argTemplate);
       l.exiting(getClass().getName(), "setArgs", args);
    }

    public ArrayList getRuleConditionArgs(){
        return args;
    }

}
