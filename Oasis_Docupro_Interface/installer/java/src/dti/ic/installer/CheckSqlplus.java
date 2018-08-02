package dti.ic.installer;

import java.io.File;

import com.installshield.wizard.platform.win32.Win32RegistryService;
import com.installshield.wizard.service.ServiceException;
import com.zerog.ia.api.pub.*;

public class CheckSqlplus extends CustomCodeAction 
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
		wrs = (Win32RegistryService)installerProxy.getService(Win32RegistryService.class);
		try
		{
			if (isSqlPlusExist("10g", installerProxy) || isSqlPlusExist("11g", installerProxy) || isSqlPlusEarlierExist("9i", installerProxy))
			{
				installerProxy.setVariable(InstallConstants.VAR_IS_SQLPLUS_EXIST, InstallConstants.TRUE);
			}
			else
			{
				installerProxy.setVariable(InstallConstants.VAR_IS_SQLPLUS_EXIST, InstallConstants.FALSE);
			}			
		}
		catch (Exception e)
		{
			installerProxy.setVariable(InstallConstants.VAR_IS_SQLPLUS_EXIST, InstallConstants.FALSE);
			e.printStackTrace();
		}
		
	}
	
	private boolean isSqlPlusExist(String versionNum, InstallerProxy installerProxy) throws ServiceException
	{
		boolean isBOEExist = false;
		
		if (wrs.keyExists(Win32RegistryService.HKEY_LOCAL_MACHINE, "SOFTWARE\\ORACLE\\KEY_OraClient" + versionNum + "_home1"))
		{
			String oracelHomePath = wrs.getStringValue(Win32RegistryService.HKEY_LOCAL_MACHINE, "SOFTWARE\\ORACLE\\KEY_OraClient" + versionNum + "_home1", "ORACLE_HOME", false).trim();
			String sqlpluswExeFile = oracelHomePath + File.separator + "BIN" + File.separator + "sqlplusw.exe";
			if(!InstallUtil.isBlank(oracelHomePath) && new File(oracelHomePath).exists() && new File(sqlpluswExeFile).exists())
			{
				installerProxy.setVariable(InstallConstants.VAR_SQLPLUS_HOME, sqlpluswExeFile);
				
				isBOEExist = true;
			}
		}
		
		return isBOEExist;
	}
	
	private boolean isSqlPlusEarlierExist(String versionNum, InstallerProxy installerProxy) throws ServiceException
	{
		boolean isBOEExist = false;
		
		if (wrs.keyExists(Win32RegistryService.HKEY_LOCAL_MACHINE, "SOFTWARE\\ORACLE"))
		{
			String oracelHomePath = wrs.getStringValue(Win32RegistryService.HKEY_LOCAL_MACHINE, "SOFTWARE\\ORACLE", "ORACLE_HOME", false).trim();
			String sqlpluswExeFile = oracelHomePath + File.separator + "BIN" + File.separator + "sqlplusw.exe";
			if(!InstallUtil.isBlank(oracelHomePath) && new File(oracelHomePath).exists() && new File(sqlpluswExeFile).exists())
			{
				installerProxy.setVariable(InstallConstants.VAR_SQLPLUS_HOME, sqlpluswExeFile);
				
				isBOEExist = true;
			}
		}
		
		return isBOEExist;
	}

	@Override
	public void uninstall(UninstallerProxy arg0) throws InstallException 
	{
	}
	
	private Win32RegistryService wrs;

}
