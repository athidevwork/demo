package dti.ic.installer;

import com.zerog.ia.api.pub.CustomCodeAction;
import com.zerog.ia.api.pub.CustomError;
import com.zerog.ia.api.pub.InstallException;
import com.zerog.ia.api.pub.InstallerProxy;
import com.zerog.ia.api.pub.UninstallerProxy;

import java.util.Calendar;

/**
 * Write more log information about the status of the whole installation.
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
 * ---------------------------------------------------
 */
public class PostCompleteLog extends CustomCodeAction {

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
		CustomError error = (CustomError) installerProxy.getService(CustomError.class);
		String productName = installerProxy.substitute(InstallConstants.VAR_PRODUCT_NAME);
		String installStatus = installerProxy.substitute(InstallConstants.VAR_INSTALL_SUCCESS);
		if (installStatus.equals("SUCCESS")) {
			error.appendMessage(productName + " has been successfully installed.");
			error.log();
		} else if (installStatus.equals("WARNING")) {
			error.appendMessage("The installation of " + productName + " is finished, but some warnings occurred during the install.");
			error.appendMessage("Please check the above logs for details");
			error.log();
		} else if (installStatus.equals("NONFATAL_ERROR")) {
			error.appendMessage("The installation of " + productName + " is finished, but some errors occurred during the install.");
			error.appendMessage("Please check the above logs for details");
			error.log();
		} else if (installStatus.equals("FATAL_ERROR")) {
			error.appendMessage("The installation of " + productName + " is finished, but some serious errors occurred during the install.");
			error.appendMessage("Please check the above logs for details");
			error.log();
		}

		Long startTime = Long.parseLong(installerProxy.substitute(InstallConstants.VAR_START_TIME));
		Calendar cal = Calendar.getInstance();
		Long endTime = cal.getTimeInMillis();

		Long totalTime = (endTime - startTime) / 1000;
		if (totalTime > 60) {
			Long totalMins = totalTime / 60;
			Long leftSeconds = totalTime % 60;
			if (totalMins > 60) {
				Long totalHours = totalMins / 60;
				Long leftMinutes = totalMins % 60;
				error.appendMessage("The whole installation takes " + totalHours + " hours " + leftMinutes + " minutes " + leftSeconds + " seconds");
			} else {
				error.appendMessage("The whole installation takes " + totalMins + " minutes " + leftSeconds + " seconds");
			}
		} else {
			error.appendMessage("The whole installation takes " + totalTime + " seconds");
		}

		error.log();
	}

	@Override
	public void uninstall(UninstallerProxy arg0) throws InstallException {

	}

}
