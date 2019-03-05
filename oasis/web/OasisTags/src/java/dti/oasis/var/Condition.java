package dti.oasis.var;

import bsh.Interpreter;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/** Condition class for Validation/Action Rule Engine
 *  Using BeanShell to evaluate expression with given fields
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 *
 * */

/* Revision Date    Revised By  Description
* ---------------------------------------------------
*
* ---------------------------------------------------
*/
public class Condition {

    private String syntaxCode = "BS";

    private String expr;

    /**
     * Constructor  with default syntax code
     *
     * @param expr   Condition expression
     */
    public Condition(String expr) {
        this.expr = expr;
        this.syntaxCode = "BS";
    }

    /**
     * Constructor
     * @param expr   Condition expression
     * @param syntaxCode  syntax code
     */
    public Condition(String expr, String syntaxCode) {
        this.expr = expr;
        this.syntaxCode = syntaxCode;
    }

    /**
     * check condition
     *
     * @param fields  in array
     * @return boolean
     * @throws Exception
     */
    public boolean check(Field[] fields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "check", new Object[]{fields});
        if (this.syntaxCode.equals("BS")) {
            Interpreter bsInterpreter = new Interpreter();
            int count = fields.length;
            for (int i = 0; i < count; i++) {
                bsInterpreter.set(fields[i].getName(), fields[i]);
            }
            Object r = bsInterpreter.eval(expr);
            log.exiting(this.getClass().getName(), "check", new Object[]{r});
            if (r instanceof Boolean) {
                return ((Boolean) r).booleanValue();
            } else {
                return false;
            }
        } else {
            throw new Exception("unknown Rule Condition Syntax Code");
        }
    }

    /**
     * check condition
     *
     * @param fields    in ArrayList
     * @return boolean
     * @throws Exception
     */
    public boolean check(ArrayList fields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "check", new Object[]{fields});
        Field fs[] = (Field[]) fields.toArray(new Field[0]);
        log.exiting(this.getClass().getName(), "check", new Object[]{fs});
        return check(fs);
    }

    /**
     * check condition
     *
     * @param fields    in map
     * @return boolean
     * @throws Exception
     */
    public boolean check(Map fields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "check", new Object[]{fields});
        if (this.syntaxCode.equals("BS")) {
            Interpreter bsInterpreter = new Interpreter();
            Set keys = fields.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                bsInterpreter.set(name, fields.get(name));
            }
            Object r = bsInterpreter.eval(expr);
            log.exiting(this.getClass().getName(), "check", new Object[]{r});
            if (r instanceof Boolean) {
                return ((Boolean) r).booleanValue();
            } else {
                return false;
            }
        } else {
            throw new Exception("unknown Rule Condition Syntax Code");
        }
    }
}

