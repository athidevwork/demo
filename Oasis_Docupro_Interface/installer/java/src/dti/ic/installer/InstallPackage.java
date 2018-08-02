package dti.ic.installer;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import com.zerog.ia.api.pub.*;
import dti.ic.installer.InstallConstants;
import dti.ic.installer.InstallUtil;

public class InstallPackage extends CustomCodeAction
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
		
	    /*String sqlScriptPath = installerProxy.substitute(InstallConstants.VAR_USER_INSTALL_DIR) + File.separator + InstallConstants.FD_SQLSCRIPT;

		File sqlFolder = new File(sqlScriptPath);
		if (!sqlFolder.exists() || sqlFolder.list().length == 0)
		{
			error.appendError("The installer cannot find any db objects in " + sqlScriptPath, CustomError.ERROR);
			error.log();
			return;
		}*/
		
		Connection connection = null;
		try 
		{
		    // Load the JDBC driver
		    String driverName = "oracle.jdbc.driver.OracleDriver";
		    Class.forName(driverName);
		    
		    // Create a connection to the database		    
		    String serverName = installerProxy.substitute(InstallConstants.VAR_DB_SERVER_NAME);
		    String portNumber = installerProxy.substitute(InstallConstants.VAR_DB_SERVER_PORT);
		    String sid = installerProxy.substitute(InstallConstants.VAR_DB_SID_NAME);
		    String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
		    m_user = installerProxy.substitute(InstallConstants.VAR_DB_USER_NAME);
		    String password = installerProxy.substitute(InstallConstants.VAR_DB_USER_PASSWORD);
		    connection = DriverManager.getConnection(url, m_user, password);

		    File pkgFile_Path = new File("pkg/form_interface_cfg.pkg");
			executePkgScript(connection, error, pkgFile_Path);

		    // Config Prop Off first, only for OLTP, not for DW_MART
		    /*String isDBMart = installerProxy.substitute(InstallConstants.VAR_IS_DW_MART);
		    if (isDBMart.equals(InstallConstants.FALSE))
		    {
		    	 configPropOff(connection, error);
		    }
		    
			File[] pkgFiles = sqlFolder.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) 
		        {
					return name.toLowerCase().endsWith(".pkg");
		        }
		                
		     });
			
			File[] pkgFilesNewExecuteOrder = null;
			
			if (pkgFiles.length > 1)
			{
				//For pkg files, always publish common_extract first, then form_custom file, then others, last custom_extract
				ArrayList<File> pkgs = new ArrayList<File>();
				reOrderPkg(pkgs, pkgFiles, "common_extract.pkg");
				reOrderPkg(pkgs, pkgFiles, "form_custom.pkg");
				reOrderPkg(pkgs, pkgFiles, "");
				reOrderPkg(pkgs, pkgFiles, "custom_extract.pkg");
				pkgFilesNewExecuteOrder = pkgs.toArray(new File[] {});
			}
			else
			{
				pkgFilesNewExecuteOrder = pkgFiles;
			}*/

			/*File[] otherSqlFiles = sqlFolder.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name) 
		        {
					return !name.toLowerCase().endsWith(".pkg") && !name.toLowerCase().endsWith(".pat") && !name.toLowerCase().endsWith(".alt") && !name.toLowerCase().endsWith(".trg");
		        }
		                
		     });
			
			File[] patSqlFiles = sqlFolder.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) 
		        {
					return name.toLowerCase().endsWith(".pat") || name.toLowerCase().endsWith(".alt");
		        }
		                
		     });
			
			// Make sure pkg files are always executed firstly.
			File[] sqlFiles = InstallUtil.concatArray(pkgFilesNewExecuteOrder, otherSqlFiles);
			if (sqlFiles != null) 
			{
			    for (int index = 0; index < sqlFiles.length; index++) 
			    {
			    	executePkgScript(connection, error, sqlFiles[index]);
			    }
			}
			
			if (patSqlFiles != null)
			{
				for (int fIndex = 0; fIndex < patSqlFiles.length; fIndex++) 
			    {
					  // Get filename of file or directory			        
			        String line = null;   
			        String preLine = null;  
					StringBuilder patScript = new StringBuilder();
					FileInputStream fis = new FileInputStream(patSqlFiles[fIndex].getAbsoluteFile());
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr);       
					while ((line = br.readLine()) != null) 
					{         
						line = line.trim();
						String lineLC = line.toLowerCase();
						// set can be used to update table, but cannot be used as a dependent statement 
						boolean isSetValid = !lineLC.startsWith("set") || (lineLC.startsWith("set") && preLine != null && !preLine.endsWith(";"));
						// We should allow creating column like remit in the table
						if (line.length() > 0 && (!lineLC.startsWith("rem") || lineLC.startsWith("remit")) && !lineLC.startsWith("prompt") && !line.startsWith("--") 
								&& !line.equals("/") && !lineLC.startsWith("commit") && isSetValid)
						{							
							patScript.append(line + " ");  
							preLine = line;
						}
					}       
					br.close();  
					isr.close();
					
					executePatScript(connection, error, patScript, patSqlFiles[fIndex].getName());
			    }
			}
			
			File[] trgFiles = sqlFolder.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) 
		        {
					return name.toLowerCase().endsWith(".trg");
		        }
		                
		     });
			
			// Excute the trigger files finally
			if (trgFiles != null) 
			{
			    for (int index = 0; index < trgFiles.length; index++) 
			    {
			    	executeTrgScript(connection, error, trgFiles[index]);
			    }
			}*/
		} 
		catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.FATAL_ERROR);
			error.log();
		}
		finally
		{
			try 
			{
				if (connection != null)
					connection.close();
			} 
			catch (SQLException e) 
			{
				error.appendError(e.getMessage(), CustomError.ERROR);
				error.log();
			}
		}
		
		error.log();

	}

    private void executePkgScript(Connection conn, CustomError error, File dbFile)
    {
		PreparedStatement pstmt = null;
		
        // Get filename of file or directory
        String line = null;
        ArrayList<String> sqlStats = new ArrayList<String>();
        StringBuilder sqlScript = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
		try 
		{
			 fis = new FileInputStream(dbFile.getAbsoluteFile());
			 isr = new InputStreamReader(fis);
	         br = new BufferedReader(isr);
	         while ((line = br.readLine()) != null)
	         {
	            if (!line.trim().equals("/"))
	            {
	                sqlScript.append(line + "\n");
	            }
	            else
	            {
	                sqlStats.add(sqlScript.toString());
	                sqlScript = new StringBuilder();
	            }
	         }
		} catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.FATAL_ERROR);
			error.log();
		}
		finally
		{
			try 
			{
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (fis != null)
					fis.close();
			} 
			catch (Exception e) 
			{
				error.appendError(e.getMessage(), CustomError.ERROR);
				error.log();
			}
		}

        int successNum = 0;
        int len = sqlStats.size();
        for ( int sqlIndex = 0; sqlIndex< len; sqlIndex++)
        {
            String sqlSc = sqlStats.get(sqlIndex);
			error.appendMessage("sqlSc = " + sqlSc +"- success while running " + dbFile.getAbsoluteFile());
			error.log();
            if (!sqlSc.trim().equals(""))
            {
                try
                {
                    pstmt = conn.prepareStatement(sqlSc);
                    pstmt.executeUpdate();
                    successNum++;
					error.appendMessage("stmt = " + pstmt.toString() +"- success while running " + dbFile.getAbsoluteFile());
					error.log();
                }
                catch (Exception e)
                {
                    error.appendMessage("error happened while running " + dbFile.getAbsoluteFile());
                    error.log();
                    error.appendError(e.getMessage(), CustomError.ERROR);
                    error.log();
                }
                finally
                {
                    try
                    {
                        if (pstmt != null)
                            pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        error.appendError(e.getMessage(), CustomError.ERROR);
                        error.log();
                    }
                }
            }
        }

        if(successNum == len)
        {
            error.appendMessage("****************    execute " + dbFile.getName() + " successful    ****************");
            error.log();
        }
    }
    
    private void executeTrgScript(Connection conn, CustomError error, File dbFile)
    {
    	Statement stmt = null;		
		
        // Get filename of file or directory
        String line = null;
        ArrayList<String> sqlStats = new ArrayList<String>();
        StringBuilder sqlScript = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
		try 
		{
			 fis = new FileInputStream(dbFile.getAbsoluteFile());
			 isr = new InputStreamReader(fis);
	         br = new BufferedReader(isr);
	         while ((line = br.readLine()) != null)
	         {
	            if (!line.trim().equals("/"))
	            {
	                sqlScript.append(line + "\n");
	            }
	            else
	            {
	                sqlStats.add(sqlScript.toString());
	                sqlScript = new StringBuilder();
	            }
	         }	        
			
		} catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.FATAL_ERROR);
			error.log();
		}
		finally
		{
			try 
			{
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (fis != null)
					fis.close();
			} 
			catch (Exception e) 
			{
				error.appendError(e.getMessage(), CustomError.ERROR);
				error.log();
			}
		}

        int successNum = 0;
        int len = sqlStats.size();
        for ( int sqlIndex = 0; sqlIndex< len; sqlIndex++)
        {
            String sqlSc = sqlStats.get(sqlIndex);
            if (!sqlSc.trim().equals(""))
            {
                try
                {
					stmt = conn.createStatement();
					stmt.execute(sqlSc);
                    successNum++;
                }
                catch (Exception e)
                {
                    error.appendMessage("error happen while running " + dbFile.getAbsoluteFile());
                    error.log();
                    error.appendError(e.getMessage(), CustomError.ERROR);
                    error.log();
                }
                finally
                {
                    try
                    {
                        if (stmt != null)
                        	stmt.close();
                    }
                    catch (SQLException e)
                    {
                        error.appendError(e.getMessage(), CustomError.ERROR);
                        error.log();
                    }
                }
            }
        }

        if(successNum == len)
        {
            error.appendMessage("****************    execute " + dbFile.getName() + " successful    ****************");
            error.log();
        }
    }
	
	private void executePatScript(Connection conn, CustomError error, StringBuilder patScript, String fileName)
	{
		Statement stmt = null;		
		// trim() is necessary due to a space exist at the end of string
		String[] patScripts = patScript.toString().trim().split(";");
	    int len = patScripts.length;
	    int successNum = 0;
	    for (int i = 0; i < len; i++)
	    {
	    	String patSQL = patScripts[i];
			try 
			{
				if (patSQL.trim().length() > 0)
				{
					if (patSQL.toUpperCase().contains("&INDEX_TABLESPACE")) {
						String indexTablespaceName = getIndexName(conn, error);
						if (InstallUtil.isBlank(indexTablespaceName))
						{
							error.appendError("Failed to execute " + patSQL + " due to no INDEX_TABLESPACE exist on this schema.", CustomError.ERROR);
							error.log();
							error.appendError("You can execute " + patSQL + " manually with correct INDEX_TABLESPACE", CustomError.ERROR);
							error.log();
							continue;
						}
						else
						{
							patSQL = patSQL.replaceAll("&INDEX_TABLESPACE", indexTablespaceName);
							error.appendMessage(patSQL);
							error.log();
						}
					}
					stmt = conn.createStatement();
					stmt.execute(patSQL);
					successNum ++;
				}
			}
			catch (Exception e) 
			{
				if(e.getMessage().startsWith("ORA-00942") && patSQL.toUpperCase().startsWith("DROP TABLE"))
				{
					successNum ++;
					error.appendMessage(patSQL + ": " + e.getMessage());
					error.log();
					// do nothing.
				}
				else
				{
					error.appendMessage("error happen while running "  + fileName + ": " + patSQL);
					error.appendError(e.getMessage(), CustomError.ERROR);
					error.log();
					
					System.out.println("error happen while running "  + fileName + ": " + patSQL);
					System.out.println("");
					e.printStackTrace(System.out);
				}
			}
			finally
			{
				if (stmt != null)
				{
					try 
					{
						stmt.close();
					} 
					catch (SQLException e) 
					{
						error.appendError(e.getMessage(), CustomError.ERROR);
						error.log();
					}
				}
			}
	    } 
	    
	    if(successNum == len)
	    {
	    	error.appendMessage("****************    execute " + fileName + " successful    ****************");
			error.log();
	    }
		
	}
	
	private String getIndexName(Connection conn, CustomError error)
	{
		Statement stmt = null;
		String indexTablespaceName = "";
		try 
		{
			String cntObjScript = "SELECT tablespace_name FROM user_tablespaces where tablespace_name like '%INDEX%'";
			stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(cntObjScript);
		    int size = 0;
		    while (rs.next())
		    {
		    	size++;
		    	indexTablespaceName = rs.getString(1);
		    	if(indexTablespaceName.toUpperCase().contains(m_user.toUpperCase()))
		    	{
		    		return indexTablespaceName;
		    	}
		    } 
		    
		    // For customer site env, use the unqiue index if its index does not contain schema owner.
		    if(size == 1) 
		    {
		    	return indexTablespaceName;
		    }
		    else
		    {
		    	//TODO, ask users to select or enter the table space name
		    	return indexTablespaceName;
		    }
		}
		catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.FATAL_ERROR);
			error.log();
			e.printStackTrace(IASys.out);
		}
		finally
		{
			if (stmt != null)
			{
				try 
				{
					stmt.close();
				} 
				catch (SQLException e) 
				{
					error.appendError(e.getMessage(), CustomError.ERROR);
					error.log();
				}
			}
		}
		
		return indexTablespaceName;
	}
	
	private void configPropOff(Connection conn, CustomError error)
	{
		CallableStatement cstmt = null;
		try 
		{
			cstmt = conn.prepareCall("{call Oasis_Config.Set_Integration('Y')}");
		    cstmt.execute();  
		    error.appendMessage("config prop off");
		    error.log();
		}
		catch (Exception e) 
		{
			error.appendError(e.getMessage(), CustomError.ERROR);
			error.log();
			System.out.println("error happen while running {call Oasis_Config.Set_Integration('Y')}");
			e.printStackTrace(IASys.out);
		}
		finally
		{			
			if (cstmt != null)
			{
				try 
				{
					cstmt.close();
				} 
				catch (SQLException e) 
				{
					error.appendError(e.getMessage(), CustomError.ERROR);
					error.log();
					e.printStackTrace();
				}
			}
		}
	}
	
	private void reOrderPkg(ArrayList<File> pkgs, File[] pkgFiles, String rule)
	{
		for (int i = 0; i < pkgFiles.length; i++)
		{
			String fileName = pkgFiles[i].getName().toLowerCase();
			if (rule.equals(""))
			{
				if (!fileName.endsWith("custom_extract.pkg") && fileName.endsWith(rule) && !pkgs.contains(pkgFiles[i]))
					pkgs.add(pkgFiles[i]);
			}
			else 
			{
				if(fileName.endsWith(rule) && !pkgs.contains(pkgFiles[i]))
					pkgs.add(pkgFiles[i]);
			}

		}
	}
	
	@Override
	public void uninstall(UninstallerProxy arg0) throws InstallException 
	{
		
	}

	private String m_user;
}
