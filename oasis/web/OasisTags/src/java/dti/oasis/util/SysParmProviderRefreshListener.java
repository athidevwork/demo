package dti.oasis.util;

/**
 * Interface for refresh system parameter
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Parker Xu
 * Date:   Feb 1, 2016
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/21/2016       Parker      Issue#168627 Optimize the system parameter logic.
 *
 * ---------------------------------------------------
 */

public interface SysParmProviderRefreshListener {

    /**
     * Provide a refresh method for a business logic to refresh itself which implement this interface.
     *
     * @return A SubsystemInfo class
     */
    public void refresh();
}
