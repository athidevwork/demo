package dti.oasis.dwr;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.error.ValidationException;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 9:13:04 PM
 * To change this template use File | Settings | File Templates.
 */
class ActionExecuteError implements IExecuteAction {

    public void exec(RuleActionArgs args, Connection con)throws ValidationException {
        int err = 0;
        boolean result = false;
        MessageManager mm = MessageManager.getInstance();
        LinkedHashMap parms = args.getMdConst(); //no fields require for this exection
        Iterator it = parms.keySet().iterator();
        while(it.hasNext()){
             mm.addVerbatimMessage((String)parms.get(it.next()), MessageCategory.ERROR);
             err++;
        }

    }
}
