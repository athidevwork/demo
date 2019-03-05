package dti.pm.core.securitymgr;

/**
 * An interface to handle implementation of data security.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface SecurityManager {

    /**
     * Returns a boolean that indicates whether the data is secured for the provided parameters.
     * The securityPattern is used to define 1 or more code=value combinations of the format:
     * ^code1^code1value^code2^code2value^codeN^codeNvalue^
     *
     *
     * @param subSystem sub-system code
     * @param securityType security type code
     * @param securityPattern a security pattern string in the format: ^code^codevalue^
     * @return boolean true, if the data is secured; otherwise false.
     */
     boolean isDataSecured(String subSystem, String securityType, String securityPattern);

    /**
     * Returns a boolean that indicates whether the data is secured for the provided parameters.
     *
     *
     * @param subSystem sub-system code
     * @param securityType security type code
     * @param sourceTable source table for the sub-system
     * @param sourceId source id for the source table.
     * @return boolean true, if the data is secured; otherwise false.
     */
     boolean isDataSecured(String subSystem, String securityType, String sourceTable, String sourceId);
}
