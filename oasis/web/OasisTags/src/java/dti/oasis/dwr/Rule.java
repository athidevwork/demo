package dti.oasis.dwr;

import dti.oasis.error.ValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 4:24:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule {
    LinkedHashMap conditions;  
    ArrayList actions;
    Connection conn;

    public void setConnection(Connection con){
        conn = con;
    }

    public LinkedHashMap getConditions() {
        return conditions;
    }

    public void setConditions(LinkedHashMap conditions) {
        this.conditions = conditions;
    }

    public ArrayList getActions() {
        return actions;
    }

    public void setActions(ArrayList actions) {
        this.actions = actions;
    }

    public void execRule() throws ValidationException {
        String logic = null;
        ArrayList condArray = null;
        boolean temp = true;
        boolean condResult = true;
        try {
            Iterator it = conditions.keySet().iterator();
            //check cnditions
            while (it.hasNext()) {
                logic = (String) it.next();

                condArray = (ArrayList) conditions.get(logic);
                if (logic.equalsIgnoreCase(DwrConstants.LOGIC_AND)) {
                    Iterator cit = condArray.iterator();
                    while (cit.hasNext()) {
                        RuleCondition ruleCondition = (RuleCondition) cit.next();

                        temp = ruleCondition.assertCondition();
                        condResult &= temp;
                    }
                }
                if (logic.equalsIgnoreCase(DwrConstants.LOGIC_OR)) {
                    Iterator cit = condArray.iterator();
                    while (cit.hasNext()) {
                        RuleCondition ruleCondition = (RuleCondition) cit.next();
                        temp = ruleCondition.assertCondition();
                        condResult |= temp;
                    }
                }
                //for NOT assume internal AND
                if (logic.equalsIgnoreCase(DwrConstants.LOGIC_NOT)) {
                    Iterator cit = condArray.iterator();
                    while (cit.hasNext()) {
                        RuleCondition ruleCondition = (RuleCondition) cit.next();
                        temp = ruleCondition.assertCondition();
                        condResult &= !temp;
                    }
                }
                //XOR    Result is true if not all the conditions return the same boolean values
                if (logic.equalsIgnoreCase(DwrConstants.LOGIC_XOR)){
                    int cnt = 0;
                     Iterator cit = condArray.iterator();
                     condResult = false;
                    while (cit.hasNext()) {
                        RuleCondition ruleCondition = (RuleCondition) cit.next();
                        temp = ruleCondition.assertCondition();
                        if(temp) cnt++;
                    }
                        if(cnt >0 && cnt != condArray.size()) condResult = true;
                }
            }
            //execute actions
            if (condResult) {
                boolean result = false;
                Iterator ait = actions.iterator();
                while (ait.hasNext()) {
                    RuleAction ruleAction = (RuleAction) ait.next();

                    ruleAction.setConnection(conn);
                    if (ruleAction.executeAction()) {
                        throw new ValidationException(DwrConstants.DWR_VALIDATION_EXCEPTION_MSG);
                    }
                    ruleAction = null;
                }
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new ValidationException(e.getMessage());
        }
    }

}
