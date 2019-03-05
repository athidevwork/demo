package dti.oasis.dwr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ConfigurationException;
import dti.oasis.dwr.dao.RuleDAO;
import dti.oasis.dwr.RuleManager;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong
 * Date: Apr 6, 2009
 * Time: 3:06:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleManagerImpl implements RuleManager {
    public RecordSet loadAllArguments(Record inputRecord) {
        return getRuleDAO().loadAllArguments(inputRecord);
    }

    public RuleDAO getRuleDAO() {
        return m_ruleDAO;
    }
    public RecordSet getRules(Record inputRecord){
         return getRuleDAO().getRules(inputRecord);
    }
    public RecordSet getRuleConditions(Record inputRecord){
          return getRuleDAO().getRuleConditions(inputRecord);
    }
    public RecordSet getRuleCondFunctions(Record inputRecord){
           return getRuleDAO().getRuleCondFunctions(inputRecord);
    }
    public RecordSet getFuncionArgs(Record inputRecord){
            return getRuleDAO().getFuncionArgs(inputRecord);
    }
    public RecordSet getRuleActions(Record inputRecord){
            return getRuleDAO().getRuleActions(inputRecord);
    }
    public RecordSet getActionArgs(Record inputRecord){
            return getRuleDAO().getActionArgs(inputRecord);
    }

    public void insertDiaryItem(Record inputRecord){
        getRuleDAO().insertDiaryItem(inputRecord);
    }
    public void setRuleDAO(RuleDAO ruleDAO) {
        m_ruleDAO = ruleDAO;
    }

    public void verifyConfig() {
        if (getRuleDAO() == null)  {
            throw new ConfigurationException("The required property 'ruleDAO' is missing.");
    }
}

    private RuleDAO m_ruleDAO;
    private static String ARGUMENT_NAME_FIELD = "argumentName";
    private static String RULE_PK_FIELD = "rulePk";
    private static String RULE_CONDITION_PK_FIELD = "ruleConditionPk";
    private static String CONDITION_LOGIC_FIELD = "conditionLogic";
    private static String FUNC_EXPRESSION_FIELD ="funcExpression";
    private static String FUNC_EXPRESSION_TYPE_FIELD = "funcExpressionType";
    private static String COND_FUNCTION_PK_FIELD = "CondFunctionPk";
    private static String FUNCTION_ARG_NAME_FIELD ="functionArgName";
    private static String FUNCTION_ARG_TYPE_FIELD="functionArgType";
    private static String FUNCTION_ORIG_IND_FIELD = "origInd";
    private static String RULE_ACTION_PK_FIELD = "ruleActionPk";
    private static String ACTION_TYPE_FIELD="actionType";
    private static String ACTION_EXPRESSION_FIELD = "actionExpression";
    private static String ACTION_ARG_NAME_FIELD ="actionArgName";
    private static String ACTION_ARG_VALUE_FIELD = "actionArgValue";
    private static String ACTION_ARG_TYPE_FIELD ="actionArgType";
    private static String ACTION_ORIG_IND_FIELD = "origInd";
}
