package dti.oasis.app;

/**
 * This Exception class is used for all exceptions encountered during configuration.
 * This includes verifying the configuration of any components.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ConfigurationException extends AppException {
    public static final String CONFIGURATION_ERROR = "appException.configuration.error";

    public ConfigurationException(String debugMessage) {
        super(CONFIGURATION_ERROR, debugMessage);
    }

    public ConfigurationException(String debugMessage, Throwable cause) {
        super(CONFIGURATION_ERROR, debugMessage, cause);
    }
}
