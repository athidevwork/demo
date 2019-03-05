package dti.oasis.util;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;

/**
 * Interface for all Parm factory objects
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 4, 2003
 * @author jbe
 */
 /*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1/5/2005         JBE         Added getSubsystemInfo 
 *
 * ---------------------------------------------------
 */

public interface IParmProvider extends Serializable {
    String get(String dbPoolId, String key) throws SQLException, NamingException;

    HashMap get(String dbPoolId, String[] keys) throws SQLException, NamingException;

    void refresh(String dbPoolId) throws SQLException, NamingException;

    SubsystemInfo getSubsytemInfo(String dbPoolId, String subsystem)
            throws SQLException, NamingException;
}
