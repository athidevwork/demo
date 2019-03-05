package dti.oasis.dwr;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.util.LogUtils;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 12, 2009
 * Time: 1:26:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionExecuteWarning implements IExecuteAction{
    public void exec(RuleActionArgs args, Connection con)throws ValidationException {

        Logger l = LogUtils.enterLog(getClass(), "exec");
        int err = 0;
        boolean result = false;
        MessageManager mm = MessageManager.getInstance();
        LinkedHashMap parms = args.getMdConst(); //we should use only constants that carry messages
        Iterator it = parms.keySet().iterator();
        while (it.hasNext()) {
            String wMessage = (String) parms.get(it.next());
            l.fine("ActionExecuteWarning:exec:message:" + wMessage);
            mm.addVerbatimMessage(wMessage, MessageCategory.WARNING);
            err++;
        }
        l.exiting(getClass().getName(), "exec", parms);
    }

}
