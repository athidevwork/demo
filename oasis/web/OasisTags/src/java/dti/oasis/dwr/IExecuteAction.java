package dti.oasis.dwr;

import java.util.LinkedHashMap;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 9:06:41 PM
 * To change this template use File | Settings | File Templates.
 */
 interface IExecuteAction {
    /**
     * 
     * @param parms
     */
    void exec(RuleActionArgs args, Connection con);

}
