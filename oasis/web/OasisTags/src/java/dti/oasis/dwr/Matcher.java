package dti.oasis.dwr;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xpath.XPathAPI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.io.ByteArrayInputStream;

import dti.oasis.app.AppException;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 2, 2009
 * Time: 8:09:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Matcher {
    
    ArrayList result = new ArrayList();

    public ArrayList match(String currentXml, String origXml,  LinkedHashMap template){
        int ret = 0;
        //match form elements
        matchField(currentXml, template, DwrConstants.FIELD_XPATH, DwrConstants.FIELD_NODE_NAME);
        matchField(currentXml, template, DwrConstants.GRID_XPATH, DwrConstants.FIELD_NODE_NAME);
        return result;
    }


    /**
     * Accept XML string and template.
     * Match field nodes from XML and populate result map with template matchning keys.
     * @param xml
     * @param template
     * @param matchOrig
     * @param mp
     * @return
     */
    private void matchField(String xml, LinkedHashMap template, String xPath, String fieldPath){

        try{
            Document doc;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            NodeIterator rows = XPathAPI.selectNodeIterator(doc, xPath);
            Node rowNode;
            String key;
            String val;
            int i=0;
            boolean rowAdded = false;
            while ((rowNode = rows.nextNode()) != null) {
                NodeIterator fields = XPathAPI.selectNodeIterator(rowNode, fieldPath);
                Node fieldNode;
                i++;
                if(result.size()<i){
                    result.add(template);
                    rowAdded = true;
                }
                int r=0;
                while((fieldNode = fields.nextNode()) != null){

                 key =((Element)fieldNode).getAttribute(DwrConstants.FIELD_NODE_NAME_ATTR);
                 val =((Element)fieldNode).getAttribute(DwrConstants.FIELD_NODE_VAL_ATTR);

                 //REPLACE VALUE FOR DEFINED KEY

                    if(template.containsKey(DwrConstants.ORIGNAL_INDICATOR+key)){
                        populateTemplateValues(result, DwrConstants.ORIGNAL_INDICATOR+key, val);
                        r++;
                    }else{
                     if(template.containsKey(key)){
                       populateTemplateValues(result, key, val);
                        r++;
                     }
                    }
                }
                if(rowAdded && r==0){
                    result.remove(result.size()-1);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            throw new AppException(e.getMessage());
        }
    }
    /**
     * Update all matching fields in every element in array
     * @param ar
     * @param key
     * @param val
     * @return
     */
    private ArrayList populateTemplateValues(ArrayList ar, String key, String val){
         int ars = ar.size();
         for (int i=0; i<ars; i++){
               LinkedHashMap mp = (LinkedHashMap) ar.get(i);
               mp.put(key, val);
               ar.set(i,mp);
         }


    return ar;
    }

    public static void main(String[] args){
        Matcher o = new Matcher();
        String currentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<form>\n" +
            "<fields>\n" +
            "<field name=\"f1\" value=\"n1\"/>\n" +
            "<field name=\"f2\" value=\"n2\"/>\n" +
            "<field name=\"f3\" value=\"n3\"/>\n" +
            "</fields>\n" +
            "<ROWS>\n" +
            "<ROW>\n" +
            "<field name=\"rf1\" value=\"r1n1\"/>\n" +
            "<field name=\"rf2\" value=\"r1n2\"/>\n" +
            "<field name=\"rf3\" value=\"r1n3\"/>\n" +
            "</ROW>\n" +
            "<ROW>\n" +
            "<field name=\"rf1\" value=\"r2n1\"/>\n" +
            "<field name=\"rf2\" value=\"r2n2\"/>\n" +
            "<field name=\"rf3\" value=\"r2n3\"/>\n" +
            "</ROW>\n" +
            "</ROWS>\n" +
            "</form>";
        String origXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<form>\n" +
            "<fields>\n" +
            "<field name=\"f1\" value=\"n1\"/>\n" +
            "<field name=\"f2\" value=\"n2\"/>\n" +
            "<field name=\"f3\" value=\"n3\"/>\n" +
            "</fields>\n" +
            "<ROWS>\n" +
            "<ROW>\n" +
            "<field name=\"rf1\" value=\"r1n1\"/>\n" +
            "<field name=\"rf2\" value=\"r1n2\"/>\n" +
            "<field name=\"rf3\" value=\"r1n3\"/>\n" +
            "</ROW>\n" +
            "<ROW>\n" +
            "<field name=\"rf1\" value=\"r2n1\"/>\n" +
            "<field name=\"rf2\" value=\"r2n2\"/>\n" +
            "<field name=\"rf3\" value=\"ORIGr2n3\"/>\n" +
            "</ROW>\n" +
            "</ROWS>\n" +
            "</form>";
        LinkedHashMap mp = new LinkedHashMap();
        mp.put("f1", null);
        mp.put("f2", null);
        o.match(currentXml, origXml, mp);
        Iterator it = o.result.iterator();
        while (it.hasNext()) {
            LinkedHashMap m = (LinkedHashMap) it.next();
        }
    }

}
