package dti.ic.installer;


import com.zerog.ia.api.pub.*;

import java.sql.*;

/**
 * Create APPLICANT and REVIEWER users if they do not exist, and add the appropriate profile information to them.
 * 
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2012
 *
 * @author Toby Wang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class CreateOsFormIntfConfig extends CustomCodeAction {

    protected CustomError m_error;

    @Override
    public String getInstallStatusMessage() {
        return null;
    }

    @Override
    public String getUninstallStatusMessage() {
        return null;
    }

    @Override
    public void install(InstallerProxy installerProxy) throws InstallException {
        m_error = (CustomError) installerProxy.getService(CustomError.class);

        Connection connection = null;
        try {
            // Load the JDBC driver
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);

            // Create a connection to the database
            String serverName = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_NAME);
            String portNumber = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_PORT);
            String sid = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SID_NAME);
            String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
            String username = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_NAME);
            String password = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD);
            connection = DriverManager.getConnection(url, username, password);
            
            configPropOff(connection);

            String formsEngine = installerProxy.substitute("$FORMS_ENGINE$");
            //m_error.appendMessage("Forms engine : " + formsEngine);
            if (formsEngine.contains("Ghostdraft"))
                executeGhostdraftConfig(connection, installerProxy);
            if (formsEngine.contains("Eloquence"))
                executeEloquenceConfig(connection, installerProxy);
        } catch (Exception e) {
            m_error.appendError(e.getMessage(), CustomError.FATAL_ERROR);
            m_error.log();

            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                m_error.appendError(e.getMessage(), CustomError.ERROR);
                m_error.log();
            }
        }
        m_error.log();
    }

    private void executeEloquenceConfig(Connection connection, InstallerProxy installerProxy) {
        String eloquenceOutputWsHost = installerProxy.substitute("$ELOQUENCE_OUTPUT_WS_HOST$");
        eloquenceOutputWsHost = eloquenceOutputWsHost.replaceAll("^\"|\"$", "");
        String eloquenceOutputWsPort = installerProxy.substitute("$ELOQUENCE_OUTPUT_WS_PORT$");
        eloquenceOutputWsPort = eloquenceOutputWsPort.replaceAll("^\"|\"$", "");
        String eloquenceWebHost = installerProxy.substitute("$ELOQUENCE_WEB_HOST$");
        eloquenceWebHost = eloquenceWebHost.replaceAll("^\"|\"$", "");
        String eloquenceWebPort = installerProxy.substitute("$ELOQUENCE_WEB_PORT$");
        eloquenceWebPort = eloquenceWebPort.replaceAll("^\"|\"$", "");
        String interactiveForms = installerProxy.substitute("$ELOQUENCE_INTERACTIVE_FORMS$");
        interactiveForms = interactiveForms.replaceAll("^\"|\"$", "");
        String inputResourceLocator = installerProxy.substitute("$ELOQUENCE_INPUT_RESOURCE_LOCATOR$");
        inputResourceLocator = inputResourceLocator.replaceAll("^\"|\"$", "");
        String defaultDevice = installerProxy.substitute("$ELOQUENCE_DEFAULT_DEVICE_NAME$");
        defaultDevice = defaultDevice.replaceAll("^\"|\"$", "");
        String mapFileName = installerProxy.substitute("$ELOQUENCE_MAP_FILE_NAME$");
        mapFileName = mapFileName.replaceAll("^\"|\"$", "");
        String configPath = installerProxy.substitute("$CONFIG_PATH$");
        configPath = configPath.replaceAll("^\"|\"$", "");
        String previewArchivePath = installerProxy.substitute("$PREVIEW_ARCHIVE_PATH$");
        previewArchivePath = previewArchivePath.replaceAll("^\"|\"$", "");
        String previewGeneralCollection = installerProxy.substitute("$PREVIEW_GENERAL_COLLECTION$");
        previewGeneralCollection = previewGeneralCollection.replaceAll("^\"|\"$", "");
        String previewGeneralEntity = installerProxy.substitute("$PREVIEW_GENERAL_ENTITY$");
        previewGeneralEntity = previewGeneralEntity.replaceAll("^\"|\"$", "");
        String previewLocalDevice = installerProxy.substitute("$PREVIEW_LOCAL_DEVICE$");
        previewLocalDevice = previewLocalDevice.replaceAll("^\"|\"$", "");
        String previewLogLevel = installerProxy.substitute("$PREVIEW_LOG_LEVEL$");
        previewLogLevel = previewLogLevel.replaceAll("^\"|\"$", "");
        String previewNetworkDevice = installerProxy.substitute("$PREVIEW_NETWORK_DEVICE$");
        previewNetworkDevice = previewNetworkDevice.replaceAll("^\"|\"$", "");
        String previewVariableSet = installerProxy.substitute("$PREVIEW_VARIABLE_SET$");
        previewVariableSet = previewVariableSet.replaceAll("^\"|\"$", "");
        String archivePath = installerProxy.substitute("$ARCHIVE_PATH$");
        archivePath = archivePath.replaceAll("^\"|\"$", "");
        String generalCollection = installerProxy.substitute("$GENERAL_COLLECTION$");
        generalCollection = generalCollection.replaceAll("^\"|\"$", "");
        String generalEntity = installerProxy.substitute("$GENERAL_ENTITY$");
        generalEntity = generalEntity.replaceAll("^\"|\"$", "");
        String localDevice = installerProxy.substitute("$LOCAL_DEVICE$");
        localDevice = localDevice.replaceAll("^\"|\"$", "");
        String logLevel = installerProxy.substitute("$LOG_LEVEL$");
        logLevel = logLevel.replaceAll("^\"|\"$", "");
        String networkDevice = installerProxy.substitute("$NETWORK_DEVICE$");
        networkDevice = networkDevice.replaceAll("^\"|\"$", "");
        String variableSet = installerProxy.substitute("$VARIABLE_SET$");
        variableSet = variableSet.replaceAll("^\"|\"$", "");
        String cleanupAfterPreview = installerProxy.substitute("$CLEANUP_AFTER_PREVIEW$");
        cleanupAfterPreview = cleanupAfterPreview.replaceAll("^\"|\"$", "");
        String pmsCollection = installerProxy.substitute("$PMS_COLLECTION$");
        pmsCollection = pmsCollection.replaceAll("^\"|\"$", "");
        String pmsEntity = installerProxy.substitute("$PMS_ENTITY$");
        pmsEntity = pmsEntity.replaceAll("^\"|\"$", "");
        String fmsCollection = installerProxy.substitute("$FMS_COLLECTION$");
        fmsCollection = fmsCollection.replaceAll("^\"|\"$", "");
        String fmsEntity = installerProxy.substitute("$FMS_ENTITY$");
        fmsEntity = fmsEntity.replaceAll("^\"|\"$", "");
        String cmsCollection = installerProxy.substitute("$CMS_COLLECTION$");
        cmsCollection = cmsCollection.replaceAll("^\"|\"$", "");
        String cmsEntity = installerProxy.substitute("$CMS_ENTITY$");
        cmsEntity = cmsEntity.replaceAll("^\"|\"$", "");
        String rmsCollection = installerProxy.substitute("$RMS_COLLECTION$");
        rmsCollection = rmsCollection.replaceAll("^\"|\"$", "");
        String rmsEntity = installerProxy.substitute("$RMS_ENTITY$");
        rmsEntity = rmsEntity.replaceAll("^\"|\"$", "");

        String prcName = "form_interface_cfg.eloquence_os_form_intf_config";
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call form_interface_cfg.eloquence_os_form_intf_config(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

            //m_error.appendMessage("\n" + "env : " + eloquenceEnv + "\n");
            cStmt.setString(1, eloquenceOutputWsHost);
            cStmt.setString(2, eloquenceOutputWsPort);
            cStmt.setString(3, eloquenceWebHost);
            cStmt.setString(4, eloquenceWebPort);
            cStmt.setString(5, interactiveForms);
            cStmt.setString(6, inputResourceLocator);
            cStmt.setString(7, defaultDevice);
            cStmt.setString(8, mapFileName);
            cStmt.setString(9, configPath);
            cStmt.setString(10, previewArchivePath);
            cStmt.setString(11, previewGeneralCollection);
            cStmt.setString(12, previewGeneralEntity);
            cStmt.setString(13, previewLocalDevice);
            cStmt.setString(14, previewLogLevel);
            cStmt.setString(15, previewNetworkDevice);
            cStmt.setString(16, previewVariableSet);
            cStmt.setString(17, archivePath);
            cStmt.setString(18, generalCollection);
            cStmt.setString(19, generalEntity);
            cStmt.setString(20, localDevice);
            cStmt.setString(21, logLevel);
            cStmt.setString(22, networkDevice);
            cStmt.setString(23, variableSet);
            cStmt.setString(24, cleanupAfterPreview);
            cStmt.setString(25, rmsCollection);
            cStmt.setString(26, rmsEntity);
            cStmt.setString(27, fmsCollection);
            cStmt.setString(28, fmsEntity);
            cStmt.setString(29, cmsCollection);
            cStmt.setString(30, cmsEntity);
            cStmt.setString(31, rmsCollection);
            cStmt.setString(32, rmsEntity);

            boolean result = cStmt.execute();
            //m_error.appendMessage("\n" + "Eloquence Result = " + result);
            //cStmt.executeUpdate();
        } catch (Exception e) {
            m_error.appendMessage("error happened while running procedure: " + prcName);
            m_error.appendError(e.getMessage(), CustomError.ERROR);
            m_error.log();

            System.out.println("error happened while running procedure: " + prcName);
            System.out.println("");
            e.printStackTrace();
        } finally {
            if (cStmt != null) {
                try {
                    cStmt.close();
                } catch (SQLException e) {
                    m_error.appendError(e.getMessage(), CustomError.ERROR);
                    m_error.log();
                }
            }
        }
    }

    /**
     * Execute sql statements
     * 
     * @param connection
     * @return
     */
    protected void executeGhostdraftConfig(Connection connection, InstallerProxy installerProxy) {
        String ghostDraftInstance = installerProxy.substitute("$GHOST_DRAFT_INSTANCE$");
        ghostDraftInstance = ghostDraftInstance.replaceAll("^\"|\"$", "");
        String ghostDraftDocumentMapping = installerProxy.substitute("$GHOST_DRAFT_DOCUMENT_MAPPING$");
        ghostDraftDocumentMapping = ghostDraftDocumentMapping.replaceAll("^\"|\"$", "");
        String ghostDraftTemplateMapping = installerProxy.substitute("$GHOST_DRAFT_TEMPLATE_MAPPING$");
        ghostDraftTemplateMapping = ghostDraftTemplateMapping.replaceAll("^\"|\"$", "");

        String prcName = "form_interface_cfg.ghostdraft_os_form_intf_config";
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call form_interface_cfg.ghostdraft_os_form_intf_config(?, ?, ?)}");

            cStmt.setString(1, ghostDraftInstance);
            cStmt.setString(2, ghostDraftDocumentMapping);
            cStmt.setString(3, ghostDraftTemplateMapping);
            //cStmt.executeUpdate();
            boolean result = cStmt.execute();
            //m_error.appendMessage("\n" + "Ghostdraft Result = " + result);
        } catch (Exception e) {
            m_error.appendMessage("error happened while running procedure: " + prcName);
            m_error.appendError(e.getMessage(), CustomError.ERROR);
            m_error.log();

            System.out.println("error happened while running procedure: " + prcName);
            System.out.println("");
            e.printStackTrace();
        } finally {
            if (cStmt != null) {
                try {
                    cStmt.close();
                } catch (SQLException e) {
                    m_error.appendError(e.getMessage(), CustomError.ERROR);
                    m_error.log();
                }
            }
        }
    }

    /**
     * config prop off
     * 
     * @param conn
     * @return
     */
    protected void configPropOff(Connection conn) {
        CallableStatement cstmt = null;
        try {
            cstmt = conn.prepareCall("{call Oasis_Config.Set_Integration('Y')}");
            cstmt.execute();

            m_error.appendMessage("config prop off");
            m_error.log();
        } catch (Exception e) {
            m_error.appendError("error happen while running {call Oasis_Config.Set_Integration('Y')} " + InstallConstants.newLine + e.getMessage(), CustomError.ERROR);
            m_error.log();

            e.printStackTrace();
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException e) {
                    m_error.appendError(e.getMessage(), CustomError.ERROR);
                    m_error.log();

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void uninstall(UninstallerProxy arg0) throws InstallException {

    }
}
