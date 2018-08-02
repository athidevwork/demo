package dti.ic.installer;

import com.zerog.ia.api.pub.*;

import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * This actions defines some setting for the eApp installer.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 30, 2012
 *
 * @author Toby Wang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  Apr 03, 2013     Toby Wang   Comment build id, which is not used.
 *  Sep 19, 2015     Toby Wang   Read the installer version from the properties file that was configured during build.
 * ---------------------------------------------------
 */
public class PrepareConfig extends CustomCodeAction {

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
		Calendar cal = Calendar.getInstance();
		Long beginTime = cal.getTimeInMillis();
		CustomError error = (CustomError) installerProxy.getService(CustomError.class);

		// This will be used to calculate the whole time of the eApp installation.
		installerProxy.setVariable(InstallConstants.VAR_START_TIME, beginTime);
		
		// Define Description in Pre-Installation Summary
		installerProxy.setVariable(InstallConstants.VAR_DATA_SOURCE_URL, "DataSource URL:");
		installerProxy.setVariable(InstallConstants.VAR_DATA_SOURCE_USER, "Interface Config Oracle Account:");
	    installerProxy.setVariable(InstallConstants.VAR_DATA_SCHEMA_USER, "OASIS Database Schema Owner:");

		String launchDir = installerProxy.substitute(InstallConstants.VAR_INSTALLER_LAUNCH_DIR);
		installerProxy.setVariable(InstallConstants.VAR_USER_INSTALL_DIR, launchDir);

		String localHostName = getLocalHostName();
		String localHostIP = getLocalHostIP();

		error.appendMessage("This installer is running on the machine: " + localHostName + ". Its IP is " + localHostIP);
		error.log();
	}

	private String getLocalHostName(){
		String localhostname = "";
		try {
			localhostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return localhostname;
	}

	private String getLocalHostIP(){
		String localhostIP = "";
		try {
			localhostIP = java.net.InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return localhostIP;
	}

	@Override
	public void uninstall(UninstallerProxy arg0) throws InstallException {

	}

}
