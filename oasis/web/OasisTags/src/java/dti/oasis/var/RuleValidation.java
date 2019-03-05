package dti.oasis.var;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/** Implenmentaion to
 * Apply Validation Rules  for Validation/Action Rule Engine
 * Validation Rules includes ERROR, WARNING and MESSAGE
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */
public class RuleValidation extends Rule{
    public Result applyRule() {
        Logger log = LogUtils.enterLog(this.getClass(), "applyRule");
        Result result= new Result();
        result.setRuleType(this.getActionType());
        result.setMessage(this.getMessage());
        log.exiting(this.getClass().getName(), "applyRule", result.toString());
        return result;
    }

}
