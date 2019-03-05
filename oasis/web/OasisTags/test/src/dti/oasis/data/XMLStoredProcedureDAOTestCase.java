package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.XMLRecordLoadProcessor;
import dti.oasis.test.TestCase;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 27, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class XMLStoredProcedureDAOTestCase extends TestCase {
    public XMLStoredProcedureDAOTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initializeApplicationConfiguration("PM", "dti/oasisTagsTestConfig.xml");
    }

    public void testFindAllPolicy_With_PolicyNoCriteria_CacheMetadata() throws Exception {
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyNoCriteria", "AP0062872");
        RecordSet rs;

        // DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            rs = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }

//        System.out.println("rs = " + rs.toString("\n", ", "));
    }

/*
    public void testFindAllPolicy_With_PolicyNoCriteria() {
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyNoCriteria", "AP0062872");
        RecordSet rs;

        // DataRecordMapping mapping = new DataRecordMapping();
        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            rs = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }

        System.out.println("rs = " + rs.toString("\n", ", "));
    }
*/

    public void testFindAllPolicy_With_NoCriteria() {
        Record inputRecord = new Record();
        RecordSet rs;

        // DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            rs = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }

//        System.out.println("rs = " + rs.toString("\n", ", "));
    }

    public void testFindAllPolicyXML_With_PolicyNoCriteria() {

        String inputXML = "<FindAllPolicyRequest><policyNoCriteria>AP0062</policyNoCriteria></FindAllPolicyRequest>";
        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            Document resultDoc = spDao.executeToXML(inputXML);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testFindAllPolicyXML_With_PolicyNoCriteria_And_FieldMapping() {
        String inputXML =
            "<FindAllPolicyRequest>" +
                "<policyNoCriteria>AP00622</policyNoCriteria>" +  // should find param-name before this one
                "<policyNo>AP0062872</policyNo>" +
                "<lastTermB>Y</lastTermB>" +
//              "<termEffectiveFromDate>01/01/2007</termEffectiveFromDate>" +
                "<term>" +
//                "<startDate>01/01/2007</startDate>" +
                "</term>" +
                "</FindAllPolicyRequest>";

        String paramMappingXML =
            "<spdao:paraMapping xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:param name=\"policyNoCriteria\" select=\"/FindAllPolicyRequest/policyNo\"/>" +
                "<spdao:param name=\"termEffectiveFromDate\" select=\"/FindAllPolicyRequest/startDate\"/>" +
                "<spdao:param name=\"termEffectiveToDate\" select=\"/FindAllPolicyRequest/endDate\"/>" +
             "</spdao:paraMapping>";

        String outputConfigXML =
            "<FindAllPolicyResponse xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:get-remaining-columns/>" +
                "<spdao:row-iterator>" +
                "\n  <Policy>" +
                "\n    <policyTermHistoryId/>" +
                "\n    <policyNo/>" +
                "\n    <policyId/>" +
                "\n    <term>" +
                "\n      <startDate spdao:get-column=\"termeffectivefromdate\"/>" +
                "\n      <endDate spdao:get-column=\"termeffectivetodate\"/>" +
                "\n    </term>" +
                "<spdao:get-remaining-columns/>" +
                "\n  </Policy>" +
                "</spdao:row-iterator>" +
                "\n</FindAllPolicyResponse>";

        System.out.println("inputXML = " + inputXML);
        System.out.println("paramMappingXML = " + paramMappingXML);
        System.out.println("outputConfigXML = " + outputConfigXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, paramMappingXML, outputConfigXML);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testFindAllPolicyXML_With_NoCriteria() {
        String inputXML = "<FindAllPolicyRequest></FindAllPolicyRequest>";
        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            Document resultDoc = spDao.executeToXML(inputXML);
            String result = DOMUtils.formatNode(resultDoc);
//            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }

    }

    public void testFindAllPolicyXML_With_NoCriteria_With_CustomOutputConfig() {
        String inputXML = "<FindAllPolicyRequest></FindAllPolicyRequest>";
        String outputConfigXML =
            "<FindAllPolicyResponse xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:get-remaining-columns/>" +
                "<spdao:row-iterator>" +
                "\n  <PolicyHeader>\n" +
                "<spdao:get-remaining-columns/>" +
                "\n  </PolicyHeader>\n" +
                "</spdao:row-iterator>" +
                "\n</FindAllPolicyResponse>\n";
        System.out.println("inputXML = " + inputXML);
        System.out.println("outputConfigXML = " + outputConfigXML);
//        Document doc = docBuilder.parse(new InputSource(new StringReader(xmlSource)));
        // DataRecordMapping mapping = new DataRecordMapping();
        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, null, outputConfigXML);
            String result = DOMUtils.formatNode(resultDoc);
//            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testSelCityDesc() {

        String inputXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<SelCityDescRequest xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<cityCode>163</cityCode>" +
                "</SelCityDescRequest>";
        String outputConfigXML =
            "<SelCityDescResponse xmlns:spdao=\"http://delphi-tech.com/xml/spdao\" testAttribute=\"true\">sample text" +
                "\n  <!-- <maxRows> -->" +
                "\n  <returnValue/>" +
                "\n  <label spdao:get-column=\"returnValue\"/>" +
                "\n  <description>" +
                "\n    <short spdao:get-column=\"returnValue\"/>" +
                "\n  </description>" +
//            "  <shortDescription noValueAttribute/>" + // Invalid because attribute must at least have =""
                "\n</SelCityDescResponse>";
        System.out.println("inputXML = " + inputXML);
        System.out.println("outputConfigXML = " + outputConfigXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Sel_City_Desc");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, null, outputConfigXML);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testGetPolicyHeader() {

        String inputXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<GetPolicyHeaderRequest xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
//                "<policynoCriteria>AP0062872</policynoCriteria>" +    // Mapped field name must match case sensitive
                "<policyNoCriteria>AP0062872</policyNoCriteria>" +
                "<policyTermHistoryId>15390489</policyTermHistoryId>" +
//                "<termBaseRecordId>15165904</termBaseRecordId>" +   // same case
                "<term>" +
                "<start>" +
                "<termbaserecordId>15165904</termbaserecordId>" +   // different case
                "</start>" +
                "</term>" +
                "</GetPolicyHeaderRequest>";

        String paramMappingXML =
            "<spd:paramMapping xmlns:spd=\"http://delphi-tech.com/xml/spdao\">" +
                "<spd:param name=\"policyNo\" select=\"/GetPolicyHeaderRequest/policyNoCriteria\"/>" +
            "</spd:paramMapping>";

        String outputConfigXML =
            "<GetPolicyHeaderResponse xmlns:foo=\"http://delphi-tech.com/xml/spdao\">" +
                "\n  <message foo:get-column=\"lockedmessage\"/>" +
//            "\n  <lockedMessage/>" +
                "<foo:get-remaining-columns/>" +
                "<foo:row-iterator>" +
                "\n  <PolicyHeader>" +
                "\n    <policyStatus/>" +
                "\n    <policyCycleCd foo:get-column=\"policyCycleCode\"/>" +
                "<foo:get-remaining-columns/>" +
                "\n    <wipB/>" +
                "\n  </PolicyHeader>" +
                "</foo:row-iterator>" +
                "\n  <actualViewMode/>" +
                "\n</GetPolicyHeaderResponse>"
/*
              "<GetPolicyHeaderResponse xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
            "\n  <message spdao:get-column=\"lockedmessage\"/>" +
//            "\n  <lockedMessage/>" +
                "<spdao:get-remaining-columns/>" +
                "<spdao:row-iterator>" +
                "\n  <PolicyHeader>" +
                "\n    <policyStatus/>" +
                "\n    <policyCycleCd spdao:get-column=\"policyCycleCode\"/>" +
                      "<spdao:get-remaining-columns/>" +
                "\n    <wipB/>" +
                "\n  </PolicyHeader>" +
                "</spdao:row-iterator>" +
            "\n  <actualViewMode/>" +
            "\n</GetPolicyHeaderResponse>"*/;
        System.out.println("inputXML = " + inputXML);
        System.out.println("paramMappingXML = " + paramMappingXML);
        System.out.println("outputConfigXML = " + outputConfigXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy_Header.Get_Policy_Header");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, paramMappingXML, outputConfigXML, new XMLRecordLoadProcessor() {
                public boolean postProcessRecord(Node record) {
                    Document ownerDoc = record.getOwnerDocument();
                    Node policyHeader = null;
                    try {
                        policyHeader = org.apache.xpath.XPathAPI.selectSingleNode(record, "//PolicyHeader");
                    }
                    catch (TransformerException e) {
                        AppException ae = ExceptionHelper.getInstance().handleException("Failed to find the PolicyHeader row element", e);
                        throw ae;
                    }
                    Element testNode = ownerDoc.createElementNS("", "author");
                    testNode.appendChild(ownerDoc.createTextNode("Bill Reeder"));
                    policyHeader.appendChild(testNode);
                    m_numRows++;
                    return true;
                }

                public void postProcessRecordSet(Node recordSet) {
                    Node testNode = recordSet.getOwnerDocument().createElementNS("", "numRows");
                    testNode.appendChild(recordSet.getOwnerDocument().createTextNode(String.valueOf(m_numRows)));
                    recordSet.appendChild(testNode);
                }

                private int m_numRows;
            });
//            Document resultDoc = spDao.executeToXML(inputXML, null, null);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

}
