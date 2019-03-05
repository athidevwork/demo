package dti.oasis.util;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;

/**
 * Interface for all Parm objects
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Dec 4, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1/6/2005         JBE         Add getSubsystemInfo
 * 01/21/2016       Parker      Issue#168627 Optimize the system parameter logic.
 *
 * ---------------------------------------------------
 */

public interface IParm extends Serializable {
    public String get(String key) throws SQLException, NamingException;

    public HashMap get(String[] keys) throws SQLException, NamingException;

    public void refresh() throws SQLException, NamingException;

    public SubsystemInfo getSubSystemInfo(String subsystem) throws SQLException, NamingException;
}
