package dti.ic.installer;


import com.zerog.ia.api.pub.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Create APPLICANT and REVIEWER users if they do not exist, and add the appropriate profile information to them.
 * 
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 12, 2018
 *
 * @author Athi Muthu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class CreateSystemParameterUtilConfig extends CustomCodeAction {

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

            executeSytemParameterUtilConfig(connection, installerProxy);
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

    /**
     * Execute sql statements
     * 
     * @param connection
     * @return
     */
    protected void executeSytemParameterUtilConfig(Connection connection, InstallerProxy installerProxy) {
        String formsEngineCode = "DOC_GEN_PRD_NAME";
        formsEngineCode = formsEngineCode.replaceAll("^\"|\"$", "");
        String formsEngineValue = installerProxy.substitute("$DOC_GEN_PRD_NAME$");
        formsEngineValue = formsEngineValue.replaceAll("^\"|\"$", "");
        String xmlDirCode = "OS_XML_DIRECTORY";
        xmlDirCode = xmlDirCode.replaceAll("^\"|\"$", "");
        String xmlDirValue = installerProxy.substitute("$OS_XML_DIRECTORY$");
        xmlDirValue = xmlDirValue.replaceAll("^\"|\"$", "");

        String prcName = "form_interface_cfg.handle_spu_data";
        CallableStatement cStmt = null;
        try {
            cStmt = connection.prepareCall("{call form_interface_cfg.handle_spu_data(?, ?, ?, ?)}");

            cStmt.setString(1, formsEngineCode);
            cStmt.setString(2, formsEngineValue);
            cStmt.setString(3, xmlDirCode);
            cStmt.setString(4, xmlDirValue);
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
