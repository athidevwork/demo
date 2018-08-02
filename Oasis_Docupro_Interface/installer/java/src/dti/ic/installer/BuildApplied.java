package dti.ic.installer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zerog.ia.api.pub.*;

public class BuildApplied extends CustomCodeAction
{
	@Override
	public String getInstallStatusMessage()
	{
		return null;
	}

	@Override
	public String getUninstallStatusMessage()
	{
		return null;
	}

	@Override
	public void install(InstallerProxy installerProxy) throws InstallException
	{
		CustomError error = (CustomError)installerProxy.getService(CustomError.class);
		Connection connection = null;
		PreparedStatement ps = null;
		try 
		{
			String buildIdList = "select * from build_applied order by timestamp";
			         
			// Load the JDBC driver
		    String driverName = "oracle.jdbc.driver.OracleDriver";
		    Class.forName(driverName);

		    // Create a connection to the database
		    String serverName = installerProxy.substitute(InstallConstants.VAR_DB_SERVER_NAME);
		    String portNumber = installerProxy.substitute(InstallConstants.VAR_DB_SERVER_PORT);
		    String sid = installerProxy.substitute(InstallConstants.VAR_DB_SID_NAME);
		    String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
		    String username = installerProxy.substitute(InstallConstants.VAR_DB_USER_NAME);
		    String password = installerProxy.substitute(InstallConstants.VAR_DB_USER_PASSWORD);
		    connection = DriverManager.getConnection(url, username, password);
			
		    StringBuilder configData = new StringBuilder();
		    ps = connection.prepareStatement(buildIdList);
		    ResultSet rs = ps.executeQuery();
            int columnsNumber = rs.getMetaData().getColumnCount();
		    while (rs.next())
		    {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    //System.out.print(columnValue + " " + rs.getMetaData().getColumnName(i));
                    configData.append(columnValue).append(" ").append(rs.getMetaData().getColumnName(i));
                }
                //System.out.println("");
                configData.append("");
		    }
            /*String tbOWNER = rs.getString(1);
            String buildID = rs.getString(2);
            String time = rs.getString(3);
            int spaceslenFir = 60 - tbOWNER.length();
            int spaceslenSec = 60 - buildID.length();
            buildList.append(tbOWNER + getSpacesString(spaceslenFir) + buildID + getSpacesString(spaceslenSec) + time + InstallConstants.newLine);*/
		    
		    // String logPath = installerProxy.substitute(InstallConstants.VAR_INSTALL_LOG_DESTINATION);
		    String productName = installerProxy.substitute(InstallConstants.VAR_PRODUCT_NAME);
		    // Fix the issue that the install log file is too long while mutiple classifications are bundled
			if (productName.length() > 130)
			{
				String[] indivialLabels = productName.split(",");
				int maxIndex = indivialLabels.length;
				productName = indivialLabels[0] + "--" + indivialLabels[maxIndex - 1];
			}
			
		    String logPath = installerProxy.substitute(InstallConstants.VAR_USER_INSTALL_DIR)+ File.separator + "Logs";
		    String buildHistoryLog = logPath + File.separator + "os_form_interface_config_" + productName + ".log";
		    
		    InstallUtil.writeToFile(buildHistoryLog, error, configData.toString());
		} 
		catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.ERROR);
			error.log();
			e.printStackTrace(IASys.out);
		}
		finally
		{
			if (ps != null)
			{
				try 
				{
					ps.close();
				} 
				catch (SQLException e) 
				{
					error.appendError(e.getMessage(), CustomError.ERROR);
					error.log();
					e.printStackTrace(IASys.out);
				}
			}
			
			if (connection != null)
			{
				try 
				{
					connection.close();
				} 
				catch (SQLException e) 
				{
					error.appendError(e.getMessage(), CustomError.ERROR);
					error.log();
					e.printStackTrace(IASys.out);
				}
			}
		}
		
		error.log();
	}
	
	/**
	 * If len less than 0, then return 3 spaces string
	 * @param len
	 * @return the specified length of spaces
	 */
	private String getSpacesString(int len)
	{
		String spaces = "";
		if (len < 0)
			return "   ";
		
		for(int i = 0 ; i < len; i++)
		{
			spaces = spaces + " ";
		}
		
		return spaces;
	}

	@Override
	public void uninstall(UninstallerProxy arg0) throws InstallException
	{

	}

}
