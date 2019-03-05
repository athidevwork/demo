package dti.oasis.healthcheckmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.healthcheckmgr.HealthCheck;
import dti.oasis.healthcheckmgr.dao.DataSourceHealthCheckDAO;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This class manages the health of Data Source.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 07, 2010
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class DataSourceHealthCheckImpl implements HealthCheck {

    public void checkHealth(HttpServletRequest request, HttpServletResponse response, String moduleName) {
        Logger l = LogUtils.enterLog(getClass(), "checkHealth", new String[] {moduleName});

        Record inputRecord = new Record();
        inputRecord.setFieldValue("moduleName", moduleName);

        MessageManager messageManager = MessageManager.getInstance();
        String formattedMessage = null;

        try {
            String output = getDataSourceHealthCheckDAO().checkDatabaseConnectivity(inputRecord);
            formattedMessage = messageManager.formatMessage("core.healthcheck.application.datasource.valid", new String[]{output});
            messageManager.addVerbatimMessage(formattedMessage, MessageCategory.INFORMATION);
        }
        catch(Exception e) {
            formattedMessage = messageManager.formatMessage("core.healthcheck.application.datasource.invalid");
            messageManager.addVerbatimMessage(formattedMessage, MessageCategory.ERROR);
        }

        l.exiting(getClass().getName(), "checkHealth");
    }

    public void verifyConfig() {
        if (getDataSourceHealthCheckDAO() == null)
            throw new ConfigurationException("The required property 'mdataSourceHealthCheckDAO' is missing.");
    }

    public String toString() {
        return "DataSourceHealthCheckImpl{" +
            ", m_name=" + m_dataSourceHealthCheckDAO +
            '}';
    }

    public DataSourceHealthCheckDAO getDataSourceHealthCheckDAO() {
        return m_dataSourceHealthCheckDAO;
    }

    public void setDataSourceHealthCheckDAO(DataSourceHealthCheckDAO dataSourceHealthCheckDAO) {
        this.m_dataSourceHealthCheckDAO = dataSourceHealthCheckDAO;
    }

    private DataSourceHealthCheckDAO m_dataSourceHealthCheckDAO;
}
