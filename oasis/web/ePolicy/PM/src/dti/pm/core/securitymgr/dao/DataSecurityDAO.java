package dti.pm.core.securitymgr.dao;

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
public interface DataSecurityDAO {

    /**
     * Returns a security rule that indicates whether the data is secured for the provided parameters.
     *
     *
     * @param subSystem sub-system code
     * @param securityType security type code
     * @param sourceTable source table for the sub-system
     * @param sourceId source id for the source table.
     * @return security rule {READONLY,READWRITE or RESTRICTED} if 3rd parameter is provided.
     *  Otherwise, if only 1st and 2nd parameters are provided, returns specialized user security addl sql to append to the query.
     */
     String getUserSecurity(String subSystem, String securityType, String sourceTable, String sourceId);
}
