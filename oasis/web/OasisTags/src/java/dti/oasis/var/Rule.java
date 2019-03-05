package dti.oasis.var;

import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
/**
 * Rule class for Validation/Action Rule Engine
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */

/* Revision Date    Revised By  Description
* ---------------------------------------------------
*
* ---------------------------------------------------
*/
public abstract class Rule {

    private long ruleID;
    private String actionType;


    private ArrayList conditions;

    private String descr;

    private String formID;

    private boolean isApplied;

    private String Message;

    private String parms;

    public long getRuleID() {
        return ruleID;
    }

    public void setRuleID(long ruleID) {
        this.ruleID = ruleID;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    protected Rule() {

    }

    private Result result;

    public Rule(String formID, String actionType) {
        this.formID = formID;
        this.actionType = actionType;
    }

    public ArrayList getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList conditions) {
        this.conditions = conditions;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean applied) {
        isApplied = applied;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getParms() {
        return parms;
    }

    public void setParms(String parms) {
        this.parms = parms;
    }

    /**
     *  Check and Apply rule based on a given row of fields in Field array
      * @param aRowOfFields
     * @return  boolean
     * @throws Exception
     */
    public boolean checkRule(Field[] aRowOfFields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "checkRule", new Object[]{aRowOfFields});
        boolean r;
        if (checkConditions(aRowOfFields)) {
            this.result = applyRule();
            r = true;
        } else {
            r = false;
        }
        log.exiting(this.getClass().getName(), "checkRule", Boolean.valueOf(r));
        return r;
    }

    /**
     *  Check and Apply rule based on a given row of fields in Map
      * @param aRowOfFields
     * @return
     * @throws Exception
     */
    public boolean checkRule(Map aRowOfFields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "checkRule", aRowOfFields.toString());
        boolean r;
        if (checkConditions(aRowOfFields)) {
            this.result = applyRule();
            r = true;
        } else {
            r = false;
        }
        log.exiting(this.getClass().getName(), "checkRule", Boolean.valueOf(r));
        return r;
    }

    /**  How to apply the rule, the abstract method must be implenmented.
     *
      * @return Result
     */
    public abstract Result applyRule();

    /**
     *   Load conditions for the rule based on given DAO
      * @param dao
     * @throws Exception
     */
    public void loadConditions(IRuleDAO dao) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "loadConditions", dao.toString());
        this.conditions.clear();
        this.conditions = new ArrayList(dao.retrieveRuleCondition(this.ruleID));
        log.exiting(this.getClass().getName(), "loadConditions", "conditions.size()="+this.conditions.size());
    }

    /**
     *  Evaluate condition for a row of fields in array.
      * @param aRowofFields
     * @return  boolean
     * @throws Exception
     */
    protected boolean checkConditions(Field[] aRowofFields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "checkConditions", new Object[]{aRowofFields});
        boolean r = true;
        Iterator i = this.conditions.iterator();
        while (i.hasNext()) {
            Condition c = (Condition) i.next();
            if (!c.check(aRowofFields)) {
                r = false;
                break;
            }
        }
        log.exiting(this.getClass().getName(), "checkConditions", Boolean.valueOf(r));
        return r;
    }

    /**
     *  Evaluate condition for a row of fields in map.
      * @param aRowofFields
     * @return boolean
     * @throws Exception
     */
    protected boolean checkConditions(Map aRowofFields) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "checkConditions", aRowofFields.toString());
        boolean r = true;
        Iterator i = this.conditions.iterator();
        while (i.hasNext()) {
            Condition c = (Condition) i.next();
            if (!c.check(aRowofFields)) {
                return false;
            }
        }
        log.exiting(this.getClass().getName(), "checkConditions", Boolean.valueOf(r));
        return r;
    }

}

