package dti.oasis.tags;

/**
 * Interface that defines Factory for OasisElements objects
 * Implement this interface in a class to construct descendants
 * of OasisElements. Prior to calling OasisElements.createInstance
 * you would construct the class that implements this interface
 * and pass it to OasisElements.setFactory
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

public interface IOasisElementsFactory {
    public OasisElements newInstance();
}
