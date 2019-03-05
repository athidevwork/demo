package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.test.TestCase;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 6, 2008
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
public class XMLStoredProcedureDAOUpdateTestCase extends TestCase {
    public XMLStoredProcedureDAOUpdateTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initializeApplicationConfiguration("PM", "dti/oasisTagsTestConfig.xml");
    }

    public void testAddUserView() {

        String inputXML =
            "<SaveUserViewRequest>" +
                "<longDescription>" + getNewUserViewId() + "</longDescription>" +
                "<policyNo>AP0062872</policyNo>" +
                "<policyCycle>POLICY</policyCycle>" +
                "<lastTermB>N</lastTermB>" +
                "<termStatusCode>ALL</termStatusCode>" +
                "<updateind>I</updateind>" +
                "</SaveUserViewRequest>";

        String paramMappingXML =
            "<spdao:paramMapping xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:param name=\"policyNoCriteria\"  select=\"/SaveUserViewRequest/policyNo\"/>" +
            "</spdao:paramMapping>";

        System.out.println("inputXML = " + inputXML);
        System.out.println("paramMappingXML = " + paramMappingXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Save_User_View");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, paramMappingXML);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
            addUserViewId(resultDoc);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testAddMultipleUserView() {

        String paramMappingXML =
            "<spdao:paramMapping xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:param name=\"policyNoCriteria\"/ select=\"/SaveBatchUserViewRequest/UserView/policyNo\">" +
            "</spdao:paramMapping>";

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Save_User_View");

        try {
/*  For case-insensitive test */
            String inputXML =
                "<SaveUserViewRequest>" +
                    "\n  <UserView>" +
                    "\n    <longdescription>" + getNewUserViewId() + "</longdescription>" +
                    "\n    <policyNo>AP0062276</policyNo>" +
                    "\n    <policycycle>POLICY</policycycle>" +
                    "\n    <lasttermb>N</lasttermb>" +
                    "\n    <termstatuscode>ALL</termstatuscode>" +
                    "\n    <UPDATEIND>I</UPDATEIND>" +
                    "\n  </UserView>" +
                    "</SaveUserViewRequest>";
/*
            String inputXML =
                  "<SaveUserViewRequest>" +
                "\n  <UserView>" +
                "\n    <longDescription>" + getNewUserViewId() + "</longDescription>" +
                "\n    <policyNo>AP0062276</policyNo>" +
                "\n    <policyCycle>POLICY</policyCycle>" +
                "\n    <lastTermB>N</lastTermB>" +
                "\n    <termStatusCode>ALL</termStatusCode>" +
                "\n    <updateind>I</updateind>" +
                "\n  </UserView>" +
                  "</SaveUserViewRequest>";
*/
            Document resultDoc = spDao.executeToXML(inputXML, paramMappingXML);
            addUserViewId(resultDoc);

/* For case-insensitive test
            inputXML =
                  "<SaveUserViewRequest>" +
                "\n  <UserView>" +
                "\n    <longdescription>" + getNewUserViewId() + "</longdescription>" +
                "\n    <policyNo>AP0062478</policyNo>" +
                "\n    <policycycle>POLICY</policycycle>" +
                "\n    <lasttermb>N</lasttermb>" +
                "\n    <termstatuscode>ALL</termstatuscode>" +
                "\n    <UPDATEIND>I</UPDATEIND>" +
                "\n  </UserView>" +
                  "</SaveUserViewRequest>";
*/
            inputXML =
                "<SaveUserViewRequest>" +
                    "\n  <UserView>" +
                    "\n    <longDescription>" + getNewUserViewId() + "</longDescription>" +
                    "\n    <policyNo>AP0062478</policyNo>" +
                    "\n    <policyCycle>POLICY</policyCycle>" +
                    "\n    <lastTermB>N</lastTermB>" +
                    "\n    <termStatusCode>ALL</termStatusCode>" +
                    "\n    <updateind>I</updateind>" +
                    "\n  </UserView>" +
                    "</SaveUserViewRequest>";
            resultDoc = spDao.executeToXML(inputXML, paramMappingXML);
            addUserViewId(resultDoc);

            inputXML =
                "<SaveUserViewRequest>" +
                    "\n  <UserView>" +
                    "\n    <longDescription>" + getNewUserViewId() + "</longDescription>" +
                    "\n    <policyNo>AP0062872</policyNo>" +
                    "\n    <policyCycle>POLICY</policyCycle>" +
                    "\n    <lastTermB>N</lastTermB>" +
                    "\n    <termStatusCode>ALL</termStatusCode>" +
                    "\n    <updateind>I</updateind>" +
                    "\n  </UserView>" +
                    "</SaveUserViewRequest>";
            resultDoc = spDao.executeToXML(inputXML, paramMappingXML);
            addUserViewId(resultDoc);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testUpdateUserView() {

        String inputXML =
            "<SaveUserViewRequest>" +
                "<pmUserViewId>" + getFirstAddedUserViewId() + "</pmUserViewId>" +
                "<longDescription>" + getNewUserViewId() + "</longDescription>" +
                "<policyNo>Updated</policyNo>" +
                "<policyCycle>POLICY</policyCycle>" +
                "<lastTermB>N</lastTermB>" +
                "<termStatusCode>ALL</termStatusCode>" +
                "<updateind>Y</updateind>" +
                "</SaveUserViewRequest>";

        String paramMappingXML =
            "<spdao:paramMapping xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
                "<spdao:param name=\"policyNoCriteria\" select=\"/SaveUserViewRequest/policyNo\"/>" +
            "</spdao:paramMapping>";

        System.out.println("inputXML = " + inputXML);
        System.out.println("paramMappingXML = " + paramMappingXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Save_User_View");

        try {
            Document resultDoc = spDao.executeToXML(inputXML, paramMappingXML);
            String result = DOMUtils.formatNode(resultDoc);
            System.out.println("result = " + result);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    public void testDeleteUserView() {

        if (hasAddedUserViewIds()) {
            String inputXML =
                "<DeleteUserViewRequest>" +
                    "<pmUserViewId>" + removeFirstAddedUserViewId() + "</pmUserViewId>" +
                    "</DeleteUserViewRequest>";

            System.out.println("inputXML = " + inputXML);

            XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Delete_User_View");

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
        else {
            System.out.println("There are no added user view ids to delete.");
        }
    }

    public void testDeleteBatchUserView() {
        StringBuffer inputXML = new StringBuffer();
        inputXML.append("<DeleteUserViewRequest>");
        Iterator userViewIds = getAllAddedUserViewIds();
        while (userViewIds.hasNext()) {
            String userViewId = (String) userViewIds.next();
            inputXML.append(
                "\n  <UserView>" +
                    "\n    <pmUserViewId>" + userViewId + "</pmUserViewId>" +
                    "\n  </UserView>");
        }
        inputXML.append("</DeleteUserViewRequest>");

        // TODO: fix namespace for spdao
        String paramMappingXML =
            "<DeleteUserViewRequest xmlns:spdao=\"http://delphi-tech.com/xml/spdao\">" +
//            "<DeleteUserViewRequest>" +
                "<spdao:row-iterator>" +
//              "<row-iterator>" +
                "<UserView/>" +
                "</spdao:row-iterator>" +
//              "</row-iterator>" +
                "</DeleteUserViewRequest>";

        System.out.println("inputXML = " + inputXML);
        System.out.println("paramMappingXML = " + paramMappingXML);

        XMLStoredProcedureDAO spDao = XMLStoredProcedureDAO.getXMLInstance("Pm_Web_Policy.Delete_User_View");

        try {
            int updateCount = spDao.executeBatchXML(inputXML.toString(), paramMappingXML);
//            int updateCount= spDao.executeBatchXML(inputXML.toString(), paramMappingXML, true);
            System.out.println("updateCount = " + updateCount);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            throw ae;
        }
    }

    private void addUserViewId(Node output) {
        try {
            Node pmUserViewId = org.apache.xpath.XPathAPI.selectSingleNode(output, "//pmUserViewId");
            c_addedUserViews.add(pmUserViewId.getFirstChild().getNodeValue());
        }
        catch (TransformerException e) {
            fail("Failed to locate the longDescription", e);
        }
    }

    private String getNewUserViewId() {
        return "XMLSpDaoTest" + (int) (Math.random() * 100);
    }

    private boolean hasAddedUserViewIds() {
        return c_addedUserViews.size() > 0;
    }

    private Iterator getAllAddedUserViewIds() {
        return c_addedUserViews.iterator();
    }

    private String getFirstAddedUserViewId() {
        return (String) c_addedUserViews.get(0);
    }

    private String removeFirstAddedUserViewId() {
        return (String) c_addedUserViews.remove(0);
    }

    private static List c_addedUserViews = new ArrayList();

    static {
//        c_addedUserViews.add("18222996");
//        c_addedUserViews.add("18222997");
//        c_addedUserViews.add("18222998");
    }
}
