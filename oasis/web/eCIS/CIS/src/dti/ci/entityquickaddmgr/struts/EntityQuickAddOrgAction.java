package dti.ci.entityquickaddmgr.struts;

import dti.ci.entityquickaddmgr.EntityQuickAddManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.oasis.app.ConfigurationException;

/**
 * The Action Class of Quick Add Organization.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  08/15/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/20/2017       ylu         Issue 187921:
 *                              1) add new entity into WorkCenter Active History.
 *                              2) navigate to Entity Modify page when click save&close button
 * ---------------------------------------------------
 */
public class EntityQuickAddOrgAction extends EntityQuickAddBaseAction implements ICIConstants, ICIEntityConstants {


    @Override
    public void verifyConfig() {
        super.verifyConfig();

        if (getEntityQuickAddManager() == null) {
            throw new ConfigurationException("The required property 'entityQuickAddManager' is missing.");
        }
    }
}
