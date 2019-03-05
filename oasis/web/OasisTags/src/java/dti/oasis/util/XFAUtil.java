package dti.oasis.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2010
 *
 * @author qlxie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/22/2016      dpang        157243 - Enhance UFE to upload templates and manage versions of templates uploaded.
 * ---------------------------------------------------
 */
public class XFAUtil {

    /**
     * Create the datasets xml file based on a PDF template file and data map
     * @param templateBytes Byte array of the form template
     * @param pdfTemplateFile Name of PDF template file that contains datasets xml file
     * @param dataMapIn The fields data map
     * @param xfaFileOut Name of the datasets xml file
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    public static void createXFA(byte[] templateBytes, String pdfTemplateFile, Map dataMapIn, String xfaFileOut)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        Logger l = LogUtils.enterLog(XFAUtil.class, "createXFA", new Object[] {templateBytes, pdfTemplateFile, dataMapIn, xfaFileOut});

        // Create a new out datasets xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        PdfReader reader;
        if (templateBytes != null) {
            reader = new PdfReader(templateBytes);
        } else {
            reader = new PdfReader(pdfTemplateFile);
        }
        XfaForm xfa = new XfaForm(reader);
        Document doc = xfa.getDomDocument();
        NodeList list = doc.getElementsByTagNameNS("http://www.xfa.org/schema/xfa-data/1.0/", "datasets");

        // Get the xfa from PDF template
        Node newNode = list.item(0).getFirstChild();

        // Replace the xfa with data map
        if (dataMapIn != null) {
            Iterator it = dataMapIn.keySet().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                String val = (String) dataMapIn.get(name);

                XMLUtils.setElementValue(newNode, name, val);
            }
        }

        Element root = document.createElement("xfa:datasets");
        Attr xmlns = document.createAttribute("xmlns:xfa");
        xmlns.setValue("http://www.xfa.org/schema/xfa-data/1.0/");

        root.setAttributeNode(xmlns);
        document.appendChild(root);
        document.getDocumentElement().appendChild(document.importNode(newNode, true));

        // Write out the xml file
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();

        DOMSource domSource = new DOMSource(document);
        File xfaFile = new File(xfaFileOut);
        FileOutputStream out = new FileOutputStream(xfaFile);
        StreamResult xmlResult = new StreamResult(out);
        transformer.transform(domSource, xmlResult);

        l.exiting(XFAUtil.class.getName(), "createXFA");
    }

    /**
     * Merge a pdf file containing xfa, with a datasets xml File to create a new PDF File
     *
     * @param templateBytes Byte array of the form template
     * @param pdfFileIn Name of PDF File that contains fields
     * @param xfaFileIn Name of datasets xml File
     * @param pdfFileOut Name of PDF File to create upon merge
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws DocumentException
     */
    public static void merge(byte[] templateBytes, String pdfFileIn, String xfaFileIn, String pdfFileOut)
            throws ParserConfigurationException, IOException, SAXException, DocumentException {

        Logger l = LogUtils.enterLog(XFAUtil.class, "merge", new Object[] {pdfFileIn, xfaFileIn, pdfFileOut});

        // getting new data from a "datasets" XML file
        File xmlFileIn = new File(xfaFileIn);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document docIn = db.parse(xmlFileIn);
        Element element = docIn.getDocumentElement();
        NodeList nodelist = element.getElementsByTagNameNS("http://www.xfa.org/schema/xfa-data/1.0/", "data");

        // Get the data node from the datasets.xml file
        Node dataIn = nodelist.item(0);
        PdfReader reader;
        if (templateBytes != null) {
            reader = new PdfReader(templateBytes);
        } else {
            reader = new PdfReader(pdfFileIn);
        }
        XfaForm xfa = new XfaForm(reader);
        Document doc = xfa.getDomDocument();
        NodeList list = doc.getElementsByTagNameNS("http://www.xfa.org/schema/xfa-data/1.0/", "datasets");

        // replacing the XFA in the source PDF document
        list.item(0).replaceChild(doc.importNode(dataIn, true), list.item(0).getFirstChild());

        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdfFileOut));
        xfa.setDomDocument(doc);
        xfa.setChanged(true);
        XfaForm.setXfa(xfa, stamper.getReader(), stamper.getWriter());
        stamper.close();

        l.exiting(XFAUtil.class.getName(), "merge");
    }

    /**
     * @param xfaData
     * @param fieldDataMap
     * @return HashMap
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static HashMap extractXfaData(byte[] xfaData, Map fieldDataMap)
            throws ParserConfigurationException, IOException, SAXException {
        Logger l = LogUtils.enterLog(XFAUtil.class, "extractXfaData", new Object[] {xfaData, fieldDataMap});

        HashMap fields;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        ByteArrayInputStream bis = new ByteArrayInputStream(xfaData);
        Document xfaDoc = db.parse(bis);
        Node rootNode = xfaDoc.getDocumentElement();

        fields = new HashMap(fieldDataMap.size());

        Iterator it = fieldDataMap.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String val = XMLUtils.getChildElementValues(rootNode, name);
            if (!StringUtils.isBlank(val)) {
                fields.put(name, val);
            }
        }

        l.exiting(XFAUtil.class.getName(), "extractXfaData");
        return fields;
    }
}
