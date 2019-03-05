package dti.ci.entityquickaddmgr.struts;

import dti.ci.entityquickaddmgr.EntityQuickAddManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;

/**
 * The Action Class of Quick Add Person.
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
public class EntityQuickAddPersonAction extends EntityQuickAddBaseAction implements ICIConstants, ICIEntityConstants {

    /**
     * add js messages to messagemanager for the current request
     */
    @Override
    protected void addJsMessages() {
        super.addJsMessages();

        MessageManager.getInstance().addJsMessage("ci.entity.message.year.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.year.outOfRange");
        MessageManager.getInstance().addJsMessage("ci.entity.message.year.later");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.afterStartDate");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.entered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.earlier");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.notLater");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.notLater");
        MessageManager.getInstance().addJsMessage("ci.entity.message.stateCode.required");
    }

    @Override
    public void verifyConfig() {
        super.verifyConfig();

        if (getEntityQuickAddManager() == null) {
            throw new ConfigurationException("The required property 'entityQuickAddManager' is missing.");
        }
    }
}
