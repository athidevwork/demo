package dti.ic.installer;

import com.zerog.ia.api.pub.CustomCodeAction;
import com.zerog.ia.api.pub.InstallException;
import com.zerog.ia.api.pub.InstallerProxy;
import com.zerog.ia.api.pub.UninstallerProxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Validate the input Oracle Database Schema Owner Information
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 05, 2013
 *
 * @author Toby Wang
 */
/**
 * 
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class ValidateSchemaOwner extends CustomCodeAction {

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
        Connection connection = null;
        Statement stmt = null;

        try {
            // Load the JDBC driver
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);

            // Create a connection to the database
            String serverName = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_NAME).trim();
            String portNumber = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_PORT).trim();
            String sid = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SID_NAME).trim();
            String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
            String username = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_NAME).trim();
            String password = installerProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD);

            connection = DriverManager.getConnection(url, username, password);

            installerProxy.setVariable(InstallConstants.VAR_IS_DB_SCHEMA_VALID, InstallConstants.TRUE);
            
            //String tdesInstance = installerProxy.substitute(InstallConstants.VAR_TDES_INSTANCE_NAME);
            
            // Check whether tables TDES_SETTING, PFUSER and WEBFORM_WORK_ITEM exist
            Map<String, String> tableErrorMsgMap = new HashMap<String, String>();
            {
                /*tableErrorMsgMap.put("TDES_SETTING", InstallUtil.getBundle().getString("error_tdes_setting") + " '" + tdesInstance + "'.");
                tableErrorMsgMap.put("PFUSER", InstallUtil.getBundle().getString("error_pfuser"));
                tableErrorMsgMap.put("WEBFORM_WORK_ITEM", InstallUtil.getBundle().getString("error_webform_work_item"));*/
            }
            
            StringBuilder errMsg = new StringBuilder();
            for(String tableName: tableErrorMsgMap.keySet())
            {
                if(!isTableExist(connection, tableName))
                    errMsg.append(tableErrorMsgMap.get(tableName) + InstallConstants.newLine);
            }
            
            if(errMsg.length() > 0)
            {
                installerProxy.setVariable(InstallConstants.VAR_IS_DATABASE_SCHEMA_READY, InstallConstants.FALSE);
                installerProxy.setVariable(InstallConstants.VAR_ERROR_SCHEMA_CHECK_TABLE_EXIST, errMsg.toString());
            }
            else
            {
                installerProxy.setVariable(InstallConstants.VAR_IS_DATABASE_SCHEMA_READY, InstallConstants.TRUE);
            }
            
        } catch (Exception e) {
            installerProxy.setVariable(InstallConstants.VAR_IS_DB_SCHEMA_VALID, InstallConstants.FALSE);
            installerProxy.setVariable(InstallConstants.VAR_ERROR_DB_SCHEMA_CONNECT, e.getMessage());
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Check whether the specific table exist or not;
     * 
     * @param conn
     * @param tableName
     * @return boolean
     */
    protected boolean isTableExist(Connection conn, String tableName) {
        Statement stmt = null;
        try {
            String querySQL = "SELECT 1 FROM " + tableName;
            stmt = conn.createStatement();
            stmt.executeQuery(querySQL);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return false;
    }

    @Override
    public void uninstall(UninstallerProxy arg0) throws InstallException {

    }
    
}
