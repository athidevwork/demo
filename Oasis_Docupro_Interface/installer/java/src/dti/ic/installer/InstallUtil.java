/**
 * Title: InstallUtil.java
 * Description:	Utility class for installation.
 * Copyright:	Copyright All Rights Reserved.
 * Company:
 * @author Toby Wang
 */

package dti.ic.installer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.installshield.wizard.service.ServiceException;
import com.installshield.wizard.service.file.FileService;
import com.zerog.ia.api.pub.*;
import oracle.jdbc.OracleTypes;

/**
 * This class provides a set of utility functions that will be invoked by other custom actions.
 * Note that all functions here are static.
 */
public class InstallUtil
{
	/**
	 * Resource Bundle for this package
	 */
	//private final static ResourceBundle bundle = ResourceBundle.getBundle("com.dti.ic.installer.Res");
	
	/**
	 * Resource Bundle for this package
	 */
	//private final static ResourceBundle uiBundle = ResourceBundle.getBundle("com.dti.ic.installer.UI");
	
	private static Logger logger = Logger.getLogger(Logger.class.getName());
	
	
	/**
	 * The input string except password is surrounded by double quotes, especially for console mode. 
	 * These double quotes need to be removed. In GUI mode, no such special handle.
	 * 
	 * @param installerProxy InstallerProxy
	 * @param variant through the specified variant to read input value
	 * @return the value of the specified variant without double quotes.
	 */
	public static String getUserInput(InstallerProxy installerProxy, String variant)
	{
		String inputString = installerProxy.substitute(variant);
		inputString= inputString.replaceAll("\"", "");
		installerProxy.setVariable(variant, inputString);
		
		return inputString;
	}
	
	/**
	 * Get system host name, remove domain name if exists. 
	 * If fail to get host name of target machine, "localhost" will be used as default host name.
	 * 
	 * @return the host name of target machine without domain name.
	 */
	public static String getHostName()
	{
		String hostname = " ";
		try 
		{	
			hostname = java.net.InetAddress.getLocalHost().getHostName();
			
			// Remove domain name if exists
			int dotIndex = hostname.indexOf(".");
			if (dotIndex > 0)
			{
				hostname =  hostname.substring(0, dotIndex);
			}
		}
		// If fail to get host name of target machine, "localhost" will be used as default host name.
		catch (Exception e)
		{
			hostname = "localhost";
		}
		
		return hostname;
	}
	
	 /**
     * Prints some data to a file using a BufferedWriter
     */
    public static void writeToFile(String filename, CustomError error, String content) 
    {
        BufferedWriter bufferedWriter = null;
        
        try 
        {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            
            //Start writing to the output stream
            bufferedWriter.write(content);
        } 
        catch (FileNotFoundException fe) 
        {
        	error.appendError(fe.getMessage(), CustomError.ERROR);
        	error.log();
        	fe.printStackTrace(IASys.out);
        } 
        catch (IOException ioe) 
        {
        	error.appendError(ioe.getMessage(), CustomError.ERROR);
        	error.log();
        	ioe.printStackTrace(IASys.out);
        } 
        finally 
        {
            //Close the BufferedWriter
            try 
            {
                if (bufferedWriter != null) 
                {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } 
            catch (IOException ex) 
            {
            	error.appendError(ex.getMessage(), CustomError.ERROR);
            	error.log();
            	ex.printStackTrace(IASys.out);
            }
        }
        
        error.log();
    }
	
	/**
	 * Remove the specify directory only if it is an empty directory.
	 * 
	 * @param fs FileService
	 * @param dirpath The folder to be removed
	 */
	public static void removeEmptyFolder(FileService fs, String dirpath)
	{
		try 
		{
			File dir = new File(dirpath);
			if (dir.isDirectory() && dir.list().length == 0)
			{
				// Delete the specified directory only if it is empty
				fs.deleteDirectory(dirpath, true, false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(IASys.out);
		}
	}
	
	/**
	 * Remove the specify directory no matter if it is empty or not
	 * 
	 * @param fs FileService
	 * @param dirpath The folder to be removed
	 */
	public static void removeNonEmptyFolder(FileService fs, String dirpath)
	{
		try 
		{
			if (fs.isDirectory(dirpath))
			{
				// Delete the specified directory no matter if it is empty or not
				fs.deleteDirectory(dirpath, false, true);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(IASys.out);
		}
	}
	
	/**
	 * Remove the specify file.
	 * 
	 * @param fs FileService
	 * @param filePath The file to be removed
	 */
	public static void removeFile(FileService fs, String filePath)
	{
		try
		{
			if (fs.fileExists(filePath))
			{
				fs.deleteFile(filePath);
			}
		}
		catch (ServiceException e)
		{
			e.printStackTrace(IASys.out);
		}
	}
	
	/**
	 * Concatenate two String arrays.
	 * 
	 * @param firstArr the first String array
	 * @param secondArr the second String array
	 */
	public static String[] concatArray (String[] firstArr, String[] secondArr)
	{
		int len = firstArr.length + secondArr.length;
		String [] arr = new String [len];
		
		int i =0;
		for(String a : firstArr)
		{
			arr[i++]=a;
		}
		
		for(String a : secondArr)
		{
			arr[i++]=a;
		}
		
		return arr;
	}
	
	/**
	 * Concatenate two File arrays.
	 * 
	 * @param firstArr the first File array
	 * @param secondArr the second File array
	 */
	public static File[] concatArray (File[] firstArr, File[] secondArr)
	{
		if (firstArr == null && secondArr == null)
			return null;
		else if (firstArr != null && secondArr == null)
			return firstArr;
		else if (firstArr == null && secondArr != null)
			return secondArr;
		
		int len = firstArr.length + secondArr.length;
		File [] arr = new File [len];
		
		int i =0;
		for(File a : firstArr)
		{
			arr[i++]=a;
		}
		
		for(File a : secondArr)
		{
			arr[i++]=a;
		}
		
		return arr;
	}

	/**
	 * Validate whether the string is an integer number
	 *
	 * @param intStr
	 * @return boolean
	 */
	public static boolean isInteger(String intStr) {
		try {
			Integer.parseInt(intStr);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the resource bundle for this package
	 * @return ResourceBundle
	 */
	//public static ResourceBundle getBundle()
	{
		//return bundle;
	}
	
	/**
	 * Get the Installer UI control resource bundle for this package
	 * @return ResourceBundle
	 */
	//public static ResourceBundle getUIBundle()
	{
		//return uiBundle;
	}
	
	public static Logger getLogger(String logPath)
	{
		try
		{
			FileHandler fh = new FileHandler(logPath, true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return logger;
	}
	
    /**
     * Returns true if value is fundamentally blank
     *
     * @param val
     * @return
     */
    public static boolean isBlank(String val) {
        return (val == null || val.trim().length() == 0);
    }

	public static String getSystemParameterUtilData(Connection connection, CustomError m_error, String spuCode) {
		String prcName = "form_interface_cfg.get_sys_parm_cfg";
		CallableStatement cStmt = null;
		ResultSet rs = null;
		try {
			cStmt = connection.prepareCall("{call form_interface_cfg.get_sys_parm_cfg(?)}");

			cStmt.registerOutParameter(1, OracleTypes.CURSOR);

			boolean hadResults = cStmt.execute();
			rs = (ResultSet) cStmt.getObject(1);

			// process result set
			int columnsNumber = rs.getMetaData().getColumnCount();
			while (rs.next())
			{
				for (int i = 1; i <= columnsNumber; i++) {
					String columnValue = rs.getString("SYSPARM_CODE");
					if (columnValue.equalsIgnoreCase(spuCode)) {
						String value = rs.getString("SYSPARM_VALUE");
						//m_error.appendMessage("value for code : " + spuCode + " is : " + value + "\n");
						return value;
					}
				}
			}
		} catch (Exception e) {
			m_error.appendMessage("error happened while running get_sys_parm_cfg procedure: " + prcName);
			m_error.appendError(e.getMessage(), CustomError.ERROR);
			m_error.log();

			System.out.println("error happened while running get_sys_parm_cfg procedure: " + prcName);
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
		return "";
	}
}
