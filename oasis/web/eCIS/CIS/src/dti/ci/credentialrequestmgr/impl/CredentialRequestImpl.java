package dti.ci.credentialrequestmgr.impl;

import dti.ci.credentialrequestmgr.CredentialRequestManager;
import dti.ci.credentialrequestmgr.dao.CredentialRequestDAO;
import dti.cs.core.busobjs.SysParmIds;
import dti.cs.documentmgr.service.DocumentManager;
import dti.cs.documentmgr.service.EloquenceUniversalTokenServiceManager;
import dti.cs.outputmgr.util.EloquenceUtil;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.*;
import org.w3c.dom.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Credential Request.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------------------------------------------------------
 * 07/14/2016       mlm         170307 - Integration of Ghostdraft.
 * 03/10/2016       mlm         183481 - Move form interface configuration from application property to DB.
 * 09/26/2017       MLM         183483 - Add support for subsystem level configuration.
 * 11/06/2017       MLM         189564 - Removed variable configPath which is not used.
 * 01/11/2019       athi        197346 - Adding Restful web service implementation.
 * ---------------------------------------------------------------------------------------------------------------------
*/

public class CredentialRequestImpl implements CredentialRequestManager {

    /**
     * Load Entity Detail.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadEntity(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntity");
        }

        Record rd = getCredentialRequestDAO().loadEntity(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntity", rd);
        }

        return rd;
    }

    /**
     * Load  Detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDetail(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDetail");
        }

        RecordSet rs = getCredentialRequestDAO().loadDetail(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDetail", rs);
        }

        return rs;
    }

    /**
     * Load Service Charge Accounts for Entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAccount(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAccount");
        }

        RecordSet rs = getCredentialRequestDAO().loadAllAccount(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAccount", rs);
        }

        return rs;
    }

    /**
     * Save Credential Request.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveRequest(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRequest");
        }

        Record rd = getCredentialRequestDAO().saveRequest(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRequest", rd);
        }

        return rd;
    }

    /**
     * Save Credential Request Detail.
     *
     * @param inputRecordSet
     * @return int
     */
    public int saveAllRequestDetail(RecordSet inputRecordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRequestDetail");
        }

        int count = getCredentialRequestDAO().saveAllRequestDetail(inputRecordSet);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRequestDetail", count);
        }

        return count;
    }

    /**
     * Ask FM to create a new Service Charge Account for Entity.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveAccount(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAccount");
        }

        Record rd = getCredentialRequestDAO().saveAccount(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAccount", rd);
        }

        return rd;
    }

    /**
     * Send Credential Request To FM.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveProcessRequest(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveProcessRequest");
        }

        Record rd = getCredentialRequestDAO().saveProcessRequest(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveProcessRequest", rd);
        }

        return rd;
    }

    /**
     * Send Request To Cincom.
     *
     * @param inputRecord
     * @return Record
     */
    public Record submitRequest(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "submitRequest");
        }
        boolean okToFire = true;
        Record rd = new Record();
        String request = genReportXml(inputRecord);
        String resultString = null;
        String successFlag = null;
        String response = null;

        if (request.startsWith("Error")) {
            okToFire = false;
            resultString = request;
            successFlag = "N";
        }

        String formGenerationProductId = SysParmProvider.getInstance().getSysParm(SysParmIds.DOC_GEN_PRD_NAME, "");
        if (StringUtils.isBlank(formGenerationProductId)) {
            formGenerationProductId = ApplicationContext.getInstance().getProperty("FormGeneration.ProductId", IFormConstants.GHOSTDRAFT_PRODUCT_ID );
        }

        if (okToFire) {
            response = DocumentManager.getInstance().submitRequest(formGenerationProductId, request);
            try {
                Document responseDoc = XMLUtils.getDocument(response, null);
                String resultFlag = XMLUtils.getAttributeValue(responseDoc.getElementsByTagName("result").item(0).getAttributes(), "value");
                if ("OK".equalsIgnoreCase(resultFlag)) {
                    resultString = responseDoc.getElementsByTagName("document").item(0).getFirstChild().getNodeValue();
                    successFlag = "Y";
                }  else {
                    // error, set status to ERROR and set status_msg
                    String errorCode = XMLUtils.getAttributeValue(responseDoc.getElementsByTagName("error").item(0).getAttributes(), "code");
                    String errorDetails = responseDoc.getElementsByTagName("errorMessage").item(0).getFirstChild().getNodeValue();
                    resultString = "Error code " + errorCode + ": " + errorDetails;
                    successFlag = "N";
                }
            }  catch (Exception e) {
                l.throwing(getClass().getName(), "submitRequest", e);
            }
        }

        rd.setFieldValue("resultString",resultString);
        rd.setFieldValue("successFlag",successFlag);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "submitRequest", response);
        }

        return rd;
    }

    /**
     * Generate XML for Cincom.
     *
     * @param inputRecord
     * @return String
     */
    public String genReportXml(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "genReportXml");
        }
        String processingType = inputRecord.getStringValue("processingType");
        // generate xml file related variables
        String xmlContent = "";
        String docDestination = SysParmProvider.getInstance().getSysParm("OS_DOC_DIRECTORY");
        String xmlDestination = SysParmProvider.getInstance().getSysParm("OS_XML_DIRECTORY", docDestination);
        String credReqId = inputRecord.getStringValue("ciCredReqId");
        String fileName = "";
        String fileFullPath = "";
        // read in CEOutputRequest.xml related variables
        BufferedReader br = null;
        String line = "";
        StringBuffer buffer = new StringBuffer();
        // 3rd party config path
        String configPath = EloquenceUtil.getConfigPath();
        // CEOutputRequest
        String request = "";
        String requestFileName = "";
        boolean okToFire = true;
        String subsystemCode = "CIS";
        String categoryCode = EloquenceUtil.getCategoryCode(subsystemCode, false);
        try {

            //check system configuration first
            if (StringUtils.isBlank(docDestination)) {
                throw new ValidationException("The path for output document is not properly configured.");
            } else {
                // Create the folder automatically, if doesn't exists.
                if (!(new File(docDestination)).isDirectory()) {
                    // Create the folder automatically, if doesn't exists.
                    try {
                        Files.createDirectories(Paths.get(docDestination.replace("\\", "/")));
                        l.info("Directory " + docDestination + " successfully created.");
                    } catch (Exception e) {
                        l.severe("Unable to create directory " + docDestination + ".");
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace( pw );
                        l.info(sw.toString());
                        pw.close();
                        sw.close();
                        AppException ae = ExceptionHelper.getInstance().handleException("Unable to create directory " + docDestination + ".", e);
                        throw ae;
                    }
                }
            }
            if (StringUtils.isBlank(xmlDestination)) {
                throw new ValidationException("The XML destination path is not properly configured.");

            } else {
                // Create the folder automatically, if doesn't exists.
                if (!(new File(xmlDestination)).isDirectory()) {
                    try {
                        Files.createDirectories(Paths.get(xmlDestination.replace("\\", "/")));
                        l.info("Directory " + xmlDestination + " successfully created.");
                    } catch (Exception e) {
                        l.severe("Unable to create directory " + xmlDestination + ".");
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace( pw );
                        l.info(sw.toString());
                        pw.close();
                        sw.close();
                        AppException ae = ExceptionHelper.getInstance().handleException("Unable to create directory " + xmlDestination + ".", e);
                        throw ae;
                    }
                }
            }

            fileName = "credentialRequest_" + credReqId + ".xml";
            fileFullPath = xmlDestination + "\\" + fileName;

            // get xml from database tables
            xmlContent = getCredentialRequestDAO().exportData(inputRecord);

            // generate xml file
            FileOutputStream outputStream = new FileOutputStream(fileFullPath);
            outputStream.write(xmlContent.getBytes());
            outputStream.close();

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "genReportXml", "Full path of generated file: " + fileFullPath);
            }

            // get token
            String uToken = getEloquenceUniversalTokenServiceManager().getEloquenceUniversalToken();

            // Only doing Preview at this time
            requestFileName = configPath + "\\CEOutputPreviewRequest.xml";

            br = new BufferedReader(new FileReader(requestFileName));
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }

            // prepare CEoutputRequest
            request = buffer.toString();

            // Only using Preview type
            request = request.replaceAll(":Username", UserSessionManager.getInstance().getUserSession().getUserId());
            request = request.replaceAll(":LogLevel", EloquenceUtil.getOutputPreviewRequestLogLevel(categoryCode));
            request = request.replaceAll(":EntityName", EloquenceUtil.getOutputPreviewRequestEntityName(categoryCode));
            request = request.replaceAll(":ArchivePath", EloquenceUtil.getOutputPreviewRequestArchivePath(categoryCode));
            request = request.replaceAll(":LocalDeviceName", EloquenceUtil.getOutputPreviewRequestLocalDeviceName(categoryCode));
            request = request.replaceAll(":NetworkDeviceName", EloquenceUtil.getOutputPreviewRequestNetworkDeviceName(categoryCode));
            request = request.replaceAll(":InflowXMLFileName", fileName);
            request = request.replaceAll(":VariableSetName", EloquenceUtil.getOutputPreviewRequestVariableSetName(categoryCode));
            request = request.replaceAll(":CollectionName", EloquenceUtil.getOutputPreviewRequestCollectionName(categoryCode));
            request = request.replaceAll(":UToken", uToken);

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "genReportXml", "Request XML: " + request);
            }
        } catch (Exception e) {
            request = "Error: " + e.getMessage() + "<br/> " + e.getCause() ;
            okToFire = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "genReportXml", request);
        }

        return request;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getCredentialRequestDAO() == null)
            throw new ConfigurationException("The required property 'credentialRequestDAO' is missing.");
    }

    public void setCredentialRequestDAO(CredentialRequestDAO m_credentialRequestDAO) {
        this.credentialRequestDAO = m_credentialRequestDAO;
    }

    public EloquenceUniversalTokenServiceManager getEloquenceUniversalTokenServiceManager() {
        return m_eloquenceUniversalTokenServiceManager;
    }

    public void setEloquenceUniversalTokenServiceManager(EloquenceUniversalTokenServiceManager eloquenceUniversalTokenServiceManager) {
        this.m_eloquenceUniversalTokenServiceManager = eloquenceUniversalTokenServiceManager;
    }

    public CredentialRequestDAO getCredentialRequestDAO() {
        return credentialRequestDAO;
    }

    private CredentialRequestDAO credentialRequestDAO =  (CredentialRequestDAO) ApplicationContext.getInstance().getBean("credentialRequestDAO");

    private EloquenceUniversalTokenServiceManager m_eloquenceUniversalTokenServiceManager;

    private Logger l = LogUtils.getLogger(getClass());
}
