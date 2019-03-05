package dti.oasis.var;

import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
/** VarUtil
 * Utitliy class to support Validation/Action Rule Engine
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */
public class VarUtil {

    /**
     * Convert one or multiple rows of data in xml to Maps
     * Each map holds each row of data in String
     * @param xmlText
     * @return ArrayList of maps
     * @throws Exception
     */
    public static ArrayList rowDataXMLtoMaps(String xmlText) throws Exception {
        Logger log = LogUtils.enterLog(VarUtil.class, "rowDataXMLtoMaps", new Object[]{xmlText});
        ArrayList al = new ArrayList();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        InputStream xmlStream = new java.io.ByteArrayInputStream(xmlText.getBytes("UTF-8"));
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlStream);
        NodeList rowNodes = doc.getFirstChild().getChildNodes();
        int rowCount = rowNodes.getLength();
        for (int i = 0; i < rowCount; i++) {
            Node rowNode = rowNodes.item(i);
            NodeList colNodes = rowNode.getChildNodes();
            int colCount = colNodes.getLength();
            HashMap rowMap = new HashMap(colCount);
            for (int j = 0; j < colCount; j++) {
                String key = colNodes.item(j).getNodeName();
                String value = "";
                if (colNodes.item(j).hasChildNodes()) {
                    value = colNodes.item(j).getFirstChild().getNodeValue();
                }
                rowMap.put(key, value);
            }
            al.add(rowMap);
        }
        log.exiting("VarUtil", "rowDataXMLtoMaps", new Object[]{al});
        return al;
    }

    /**
     *  Convert one or multiple rows of current values and original values in xml to VAR Fields
     *  Each map holds each row of fields
      * @param xmlValues
     * @param xmlOriginals
     * @return  ArrayList of Maps
     * @throws Exception
     */
    public static ArrayList rowDataXMLtoVARFields(String xmlValues, String xmlOriginals) throws Exception {
        Logger log = LogUtils.enterLog(VarUtil.class, "rowDataXMLtoVARFields", new Object[]{xmlValues,xmlOriginals});
        ArrayList al = new ArrayList();
        ArrayList values = rowDataXMLtoMaps(xmlValues);
        ArrayList originals = rowDataXMLtoMaps(xmlOriginals);
        int countOriginal=originals.size();
        int rowCount = values.size();
        for (int i = 0; i < rowCount; i++) {
            Map rowValue = (Map) values.get(i);
            int colCount = rowValue.size();
            Map rowOriginal = new HashMap();
            if(i<countOriginal) {
              rowOriginal = (Map) originals.get(i);
            }
            HashMap fields = new HashMap(colCount);
            Set keys = rowValue.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String val = (String) rowValue.get(key);
                String orig = (String) rowOriginal.get(key);
                dti.oasis.var.Field f = new dti.oasis.var.Field(key, val, orig);
                fields.put(key, f);
            }
            al.add(fields);
        }
        log.exiting("VarUtil", "rowDataXMLtoMaps", new Object[]{al});
        return al;
    }

    /**
     *
     * @param dsr
     * @return  String
     */
    public static String formatDSRToJSON(DisconnectedResultSet dsr) {
        Logger log = LogUtils.enterLog(VarUtil.class, "formatDSRToJSON", new Object[]{dsr});
        StringBuffer sb = new StringBuffer();
        sb.append("{\"data\":[ ");
        int colCount = dsr.getColumnCount();
        dsr.first();
        while (dsr.next()) {
            if (!dsr.first()) {
                sb.append(", ");
            }
            sb.append("{");
            for (int i = 0; i < colCount; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("\"");
                sb.append(dsr.getColumn(i).getColumnName());
                sb.append("\": ");
                sb.append("\"");
                sb.append(dsr.getString(i));
                sb.append("\"");
            }
            sb.append("}");
        }
        sb.append("]}");
        log.exiting(VarUtil.class.getName(), "formatDSRToJSON", new Object[]{sb.toString()});
        return sb.toString();
    }

    /**   Format a list of MAPs into a JSON string
     *
     * @param list
     * @return   JSON String
     */
    public static String formatMapsToJSON(ArrayList list) {
        Logger log = LogUtils.enterLog(VarUtil.class, "formatMapsToJSON", new Object[]{list});
        StringBuffer sb = new StringBuffer();
        sb.append("{\"data\":[ ");
        Iterator it = list.iterator();
        boolean isFirstRow = true;
        while (it.hasNext()) {
            if (isFirstRow) {
                isFirstRow = false;
            } else {
                sb.append(", ");
            }
            Map rowData = (Map) it.next();
            Set keys = rowData.keySet();
            Iterator iKey = keys.iterator();
            sb.append("{");
            boolean isFirstField = true;
            while (iKey.hasNext()) {
                String name = (String) iKey.next();
                String value = (String) rowData.get(name);
                if (isFirstField) {
                    isFirstField = false;
                } else {
                    sb.append(", ");
                }
                sb.append("\"");
                sb.append(name);
                sb.append("\": ");
                sb.append("\"");
                if(value!=null) {
                    sb.append(value.replaceAll("[\\n\\r]", "\\\\n "));
                }
                sb.append("\"");
            }
            sb.append("}");
        }
        if(isFirstRow){    /* it is empty */
          sb.append("{}");  
        }
        sb.append("]}");
        log.exiting(VarUtil.class.getName(), "formatMapsToJSON", new Object[]{sb.toString()});
        return sb.toString();
    }

}
 
