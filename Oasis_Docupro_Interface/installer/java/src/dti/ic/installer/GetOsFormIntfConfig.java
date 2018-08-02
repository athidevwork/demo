package dti.ic.installer;


import com.zerog.ia.api.pub.*;
import oracle.jdbc.OracleTypes;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Create APPLICANT and REVIEWER users if they do not exist, and add the appropriate profile information to them.
 * 
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 05, 2018
 *
 * @author Athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class GetOsFormIntfConfig extends CustomCodeAction {

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

            executeDisplayConfig(connection, installerProxy);
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
    protected void executeDisplayConfig(Connection connection, InstallerProxy installerProxy) {
        String prcName = "form_interface_cfg.get_os_form_interface_cfg";
        CallableStatement cStmt = null;
        ResultSet rs = null;
        try {
            cStmt = connection.prepareCall("{call form_interface_cfg.get_os_form_interface_cfg(?)}");

            cStmt.registerOutParameter(1, OracleTypes.CURSOR);

            boolean hadResults = cStmt.execute();
            rs = (ResultSet) cStmt.getObject(1);
            StringBuilder configData = new StringBuilder();

            // process result set
            int columnsNumber = rs.getMetaData().getColumnCount();
            while (rs.next())
            {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    switch (i) {
                        case 1:
                            configData.append(pad(columnValue, 15));
                            break;
                        case 2:
                            configData.append(pad(columnValue, 20));
                            break;
                        case 3:
                            configData.append(pad(columnValue, 30));
                            break;
                        case 4:
                            configData.append(pad(columnValue, 30));
                            break;
                        case 5:
                            configData.append(pad(columnValue, 100)).append("\n");
                            break;
                    }
                    //configData.append(columnValue).append(" ").append(rs.getMetaData().getColumnName(i));
                }
                configData.append("");
            }

            String logPath = installerProxy.substitute(InstallConstants.VAR_USER_INSTALL_DIR)+ File.separator + "Logs";
            DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
            String configLog = logPath + File.separator + "os_form_interface_config"+ df.format(new java.util.Date()) +".log";

            InstallUtil.writeToFile(configLog, m_error, configData.toString());
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
        m_error.log();
    }

    public String pad(String str, int len){
        if (str == null)
            return("");
        else {
            //m_error.appendMessage("Str : " + str + ", str.length: " + str.length() + ", length : " + len + "\n");
            if (len - str.length() <= 0) return str;
            StringBuffer sb = new StringBuffer();
            sb.append(str);
            for (int i = str.length() + 1; i < len; i++) {
                sb.append(" ");
            }
            return sb.toString();
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
