package dti.ci.entitysecuritymgr;

/**
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   07/01/2013
 *
 * @author Herb Koenig
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntitySecurityManager {


    public boolean isEntityReadOnly(Long pk);
}
