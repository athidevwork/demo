package dti.oasis.tags;

/**
 * Interface that defines Factory for OasisFields objects
 * Implement this interface in a class to construct descendants
 * of OasisFields. Prior to calling OasisFields.createInstance
 * you would construct the class that implements this interface
 * and pass it to OasisFields.setFactory
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Jul 3, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
 */

public interface IOasisFieldsFactory {
    public OasisFields newInstance();
}
