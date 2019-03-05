package dti.ci.helpers;

import com.sforce.soap.enterprise.*;
import com.sforce.soap.enterprise.fault.ExceptionCode;
import com.sforce.soap.enterprise.fault.LoginFault;
import com.sforce.soap.enterprise.sobject.Contact;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.clientmgr.EntityAddManager;
import dti.oasis.recordset.Record;
import dti.oasis.security.Authenticator;
import dti.oasis.util.*;

import javax.xml.rpc.ServiceException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for salesforce
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 *
 * @author James
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/24/2008       Larry       Issue 86826 DB connection leakage change
 * 05/13/2018       dpang       issue 192743 - Replace CIEntityAddHelper with EntityAddManager
 * ---------------------------------------------------
*/

public class CISalesForceHelper extends CIHelper implements ICIAddressConstants, Serializable {

    /**
     * date format
     */
    private static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * soap binding
     */
    private SoapBindingStub binding;

    /**
     * error message
     */
    private String errorMessage = null;

    /**
     * count
     */
    private int count = 0;

    /**
     * get contact list from salesforce
     *
     * @param conn
     * @return
     * @throws Exception
     */
    public DisconnectedResultSet getSalesforceContactList(Connection conn)
            throws Exception {
        String methodName = "getSalesforceContactList";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, conn);
        DisconnectedResultSet result = null;
        try {
            result = getEmptyList(conn);
            if (login()) {
                QueryOptions qo = new QueryOptions();
                qo.setBatchSize(new Integer(200));
                boolean done = false;
                binding.setHeader(new SforceServiceLocator().getServiceName().getNamespaceURI(),
                        "QueryOptions", qo);
                QueryResult qr = binding.query("select ID,FirstName,LastName,Birthdate,Email,Phone,MailingCity from Contact");
                if (qr.getSize() > 0) {
                    while (!done) {
                        for (int i = 0; i < qr.getRecords().length; i++) {
                            Contact contact = (Contact) qr.getRecords(i);
                            result.addEmptyRow();
                            result.setCurrent(result.getRowCount());
                            result.setString(1, contact.getId());
                            result.setString(2, "0");
                            result.setString(3, contact.getFirstName());
                            result.setString(4, contact.getLastName());
                            Date birthdate = contact.getBirthdate();
                            if (birthdate != null) {
                                result.setString(5, format.format(birthdate));
                            } else {
                                result.setString(5, "");
                            }
                            result.setString(6, contact.getEmail());
                            result.setString(7, contact.getMailingCity());
                            result.setString(8, contact.getPhone());
                        }
                        if (qr.isDone()) {
                            done = true;
                        } else {
                            qr = binding.queryMore(qr.getQueryLocator());
                        }
                    }
                }
            } else {
                errorMessage = "Fail to log on Salesforce";
            }
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return result;
    }

    /**
     * get empty DisconnectedResultSet
     *
     * @param conn
     * @return
     * @throws Exception
     */
    public DisconnectedResultSet getEmptyList(Connection conn) throws Exception {
        String methodName = "getEmptyList";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, conn);
        String sqlStmt =
                "SELECT 1 pk, " +
                        "   '0' SELECT_IND, " +
                        "   '1'   firstname , " +
                        "   '1'   lastname, " +
                        "   '1'   birthday, " +
                        "   '1'   email, " +
                        "   '1'   city, " +
                        "   '1'   phone " +
                        "  FROM DUAL  " +
                        " WHERE 1 = 2";
        DisconnectedResultSet disconnectedResultSet = Querier.doQuery(sqlStmt, conn, false);
        lggr.exiting(this.getClass().getName(), methodName);
        return disconnectedResultSet;
    }


    /**
     * The login call is used to obtain a token from Salesforce.
     * This token must be passed to all other calls to provide
     * authentication and is valid for 2 hours.
     */
    private boolean login() throws ServiceException {
        String methodName = "login";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName);
        String userName = Authenticator.getEnvString("salesforce.username", "");
        String password = Authenticator.getEnvString("salesforce.password", "");

        /** Next, the sample client application initializes the binding stub.
         * This is our main interface to the Web service through which all
         * calls are made. The getSoap method takes an optional parameter,
         * (a java.net.URL) which is the endpoint of the Web service.
         * For the login call, the parameter always starts with
         * http(s)://www.salesforce.com. After logging in, the sample
         * client application changes the endpoint to the one specified 18
         * Sample Code Walkthrough
         * in the returned loginResult object.
         */
        binding = (SoapBindingStub) new SforceServiceLocator().getSoap();
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        LoginResult loginResult;
        try {
            System.out.println("LOGGING IN NOW....");
            loginResult = binding.login(userName, password);
        }
        catch (LoginFault ex) {
            // The LoginFault derives from AxisFault
            ExceptionCode exCode = ex.getExceptionCode();
            if (exCode == ExceptionCode.FUNCTIONALITY_NOT_ENABLED ||
                    exCode == ExceptionCode.INVALID_CLIENT ||
                    exCode == ExceptionCode.INVALID_LOGIN ||
                    exCode == ExceptionCode.LOGIN_DURING_RESTRICTED_DOMAIN ||
                    exCode == ExceptionCode.LOGIN_DURING_RESTRICTED_TIME ||
                    exCode == ExceptionCode.ORG_LOCKED ||
                    exCode == ExceptionCode.PASSWORD_LOCKOUT ||
                    exCode == ExceptionCode.SERVER_UNAVAILABLE ||
                    exCode == ExceptionCode.TRIAL_EXPIRED ||
                    exCode == ExceptionCode.UNSUPPORTED_CLIENT) {
                errorMessage = "Please be sure that you have a valid username and password.";
            } else {
                // Write the fault code to the console
                System.out.println(ex.getExceptionCode());
                // Write the fault message to the console
                errorMessage = "An unexpected error has occurred." + ex.getMessage();
            }
            return false;
        } catch (Exception ex) {
            errorMessage = "An unexpected error has occurred." + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
        // Check if the password has expired
        if (loginResult.isPasswordExpired()) {
            errorMessage = "An error has occurred. Your password has expired.";
            return false;
        }
        /** Once the client application has logged in successfully, it will use
         * the results of the login call to reset the endpoint of the service
         * to the virtual server instance that is servicing your organization.
         * To do this, the client application sets the ENDPOINT_ADDRESS_PROPERTY
         * of the binding object using the URL returned from the LoginResult.
         */
        binding._setProperty(SoapBindingStub.ENDPOINT_ADDRESS_PROPERTY,
                loginResult.getServerUrl());
        /** The sample client application now has an instance of the SoapBindingStub
         * that is pointing to the correct endpoint. Next, the sample client application
         * sets a persistent SOAP header (to be included on all subsequent calls that
         * are made with the SoapBindingStub) that contains the valid sessionId
         * for our login credentials. To do this, the sample client application
         * creates a new SessionHeader object and set its sessionId property to the
         * sessionId property from the LoginResult object.
         */
        // Create a new session header object and add the session id
        // from the login return object
        SessionHeader sh = new SessionHeader();
        sh.setSessionId(loginResult.getSessionId());
        /** Next, the sample client application calls the setHeader method of the
         * SoapBindingStub to add the header to all subsequent method calls. This
         * header will persist until the SoapBindingStub is destroyed until the header
         * is explicitly removed. The "SessionHeader" parameter is the name of the
         * header to be added.
         */
        // set the session header for subsequent call authentication
        binding.setHeader(new SforceServiceLocator().getServiceName().getNamespaceURI(),
                "SessionHeader", sh);
        // return true to indicate that we are logged in, pointed
        // at the right url and have our security token in place.
        lggr.exiting(this.getClass().getName(), methodName, new Boolean(true));
        return true;
    }

    /**
     * @param conn
     * @param contactIDStr
     */
    public boolean addNewEntity(Connection conn, String contactIDStr) throws Exception {
        String methodName = "addNewEntity";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, contactIDStr});
        boolean success = false;
        boolean autoSubmit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            if (login()) {
                String[] contactIDs = contactIDStr.split(",");
                for (int i = 0; i < contactIDs.length; i++) {
                    QueryOptions qo = new QueryOptions();
                    qo.setBatchSize(new Integer(200));
                    binding.setHeader(new SforceServiceLocator().getServiceName().getNamespaceURI(),
                            "QueryOptions", qo);

                    QueryResult qr = binding.query("select ID,FirstName,LastName,MailingStreet,Phone,MailingCity,Birthdate" +
                            "   ,Email,Fax,Salutation,Title " +
                            " from Contact" +
                            " where ID = '" + contactIDs[i] + "'");
                    if (qr.getSize() > 0) {
                        Contact contact = (Contact) qr.getRecords(0);
                        Map map = new HashMap();
                        Record inputRecord = new Record();
                        inputRecord.setFieldValue("entityType", "P");
                        inputRecord.setFieldValue("firstName", contact.getFirstName());
                        inputRecord.setFieldValue("lastName", contact.getLastName());
                        inputRecord.setFieldValue("primaryAddressB", "Y");
                        inputRecord.setFieldValue("usaAddressB", "Y");
                        inputRecord.setFieldValue("addressTypeCode", "BA");
                        inputRecord.setFieldValue("addressLine1", getSubString(convertString(contact.getMailingStreet()),60));
                        inputRecord.setFieldValue("city", convertString(contact.getMailingCity()));
                        inputRecord.setFieldValue("stateCode", "AK");
                        inputRecord.setFieldValue("phoneNumber", convertString(contact.getPhone()));
                        inputRecord.setFieldValue("primaryNumberB", "Y");
                        inputRecord.setFieldValue("emailAddress1", contact.getEmail());
                        Date birthdate = contact.getBirthdate();
                        if (birthdate != null) {
                            inputRecord.setFieldValue("dateOfBirth", format.format(birthdate));
                        }
                        inputRecord.setFieldValue("prefixName", contact.getTitle());
                        inputRecord.setFieldValue("okToSkipEntityDups", "Y");
                        String hideTaxIdSysParamVal = SysParmProvider.getInstance().getSysParm(ICIEntityConstants.HIDE_TAX_ID_PROPERTY,"N");
                        boolean includeTaxId = true;
                        if (hideTaxIdSysParamVal.equalsIgnoreCase("Y")) {
                            includeTaxId = false;
                        }

                        EntityAddInfo addInfo = getEntityAddManager().saveEntity(inputRecord);
                        if (!addInfo.isEntityAdded()) {
                            errorMessage = addInfo.getUserMessage();
                            break;
                        }
                        updateEmailAddress(conn, addInfo, contact.getEmail());
                    } else {
                        errorMessage = "Couldn't find the record in salesforce.";
                        break;
                    }
                     count ++;
                }
            } else {
                errorMessage = "Fail to log on salesforce";
            }
            if (errorMessage == null) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
            lggr.exiting(this.getClass().getName(), methodName, new Boolean(success));
            return success;
        } catch (Exception e) {
            errorMessage = "Fail to add new entity";
            conn.rollback();
            lggr.throwing(this.getClass().getName(), methodName, e);
        } finally {
            conn.setAutoCommit(autoSubmit);
        }
        return false;
    }

    /**
     * if string is null, convert to NULL
     *
     * @param s
     * @return string
     */
    private String convertString(String s) {
        if (s == null) {
            return "NULL";
        } else {
            return s;
        }
    }

    /**
     * get sub string
     * @param s
     * @param length
     * @return
     */
    private String getSubString(String s, int length) {
        String subString = s;
        if (s.length() > length) {
            subString = s.substring(0, length - 1);
        }
        return subString;
    }

    /**
     * @param conn
     * @param info
     * @param email
     * @throws SQLException
     */
    private void updateEmailAddress(Connection conn, EntityAddInfo info, String email) throws SQLException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEmailAddress", new Object[]{info, email});
        }

        CallableStatement cs = null;
        try {
            String sqlStmt = "update entity set email_address1 = ? where entity_pk = ?";
            cs = conn.prepareCall(sqlStmt);
            cs.setString(1, email);
            cs.setString(2, info.getEntityPK());
            cs.execute();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "updateEmailAddress");
            }

            return;
        } finally {
            if (cs != null) {
                DatabaseUtils.close(cs);
            }
        }
    }

    /**
     * get error message
     *
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * get entity count
     *
     * @return count
     */
    public int getCount() {
        return count;
    }

    public EntityAddManager getEntityAddManager() {
        return m_entityAddManager;
    }

    public void setEntityAddManager(EntityAddManager entityAddManager) {
        this.m_entityAddManager = entityAddManager;
    }

    private EntityAddManager m_entityAddManager;

}
