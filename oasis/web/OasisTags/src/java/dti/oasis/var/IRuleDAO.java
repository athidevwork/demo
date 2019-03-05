package dti.oasis.var;

import java.util.ArrayList;

/** Interface of Data Access Object (DAO) for Validation/Action Rule Engine
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
public interface IRuleDAO {
    /**
     *  Retrieve Rule Information for given Web Form ID
     * @param webFormID
     * @return  RuleInfo
     */
      public RuleInfo retrieveFormRuleInfo(String webFormID) throws Exception;

    /**
     *  Retrieve Rule list for given Web Form ID with given rule type
     * @param webFormID
     * @param ruleType :ACTION|VALIDATION
     * @return ArrayList of Rule
     */
      public ArrayList retrieveRules(String webFormID, String ruleType) throws Exception;

    /**
     *  Retrieve Condition Information for given rule ID
     * @param rulePK
     * @return  ArrayList of Condition
     */
      public ArrayList retrieveRuleCondition(long rulePK) throws Exception;
}
