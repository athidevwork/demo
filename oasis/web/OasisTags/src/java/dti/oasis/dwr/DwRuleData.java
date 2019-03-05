package dti.oasis.dwr;

import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 5, 2009
 * Time: 2:22:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class DwRuleData {


    protected ArrayList createRulesCollection(Connection con, String view, String event, String currXml, String origXml)
        throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "createRulesCollection");

        ArrayList result = new ArrayList();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("busView", view);
        inputRecord.setFieldValue("busEvent", event);
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getRules(inputRecord);
        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                Record rec = (Record) rs.next();
                long rulePK = rec.getLongValue("ruleId").longValue();
                l.fine("ruleId:" + rulePK);
                LinkedHashMap conditions = getConditions(rulePK, currXml, origXml);
                ArrayList actions = getActions(rulePK, currXml, origXml);
                Rule rule = new Rule();
                rule.setConditions(conditions);
                rule.setActions(actions);
                result.add(rule);

            }

        }
        l.exiting(getClass().getName(), "createRulesCollection", resultRec);
        return result;
    }

    private LinkedHashMap getConditions(long rulePK, String currXml, String origXml)
        throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getConditions");
        LinkedHashMap conds = new LinkedHashMap();
        ArrayList ruleCons = new ArrayList();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("rule", new Long(rulePK));
        String logic = null;
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getRuleConditions(inputRecord);
        Record rec = null;
        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                rec = (Record) rs.next();
                long ruleCondPK = rec.getLongValue("ruleConditionId").longValue();
                l.fine("rule condition pk:" + ruleCondPK);
                if (logic == null) {
                    logic = rec.getStringValue("conditionLogic");
                }
                l.fine("DAO getConditions logic: " + logic);
                l.fine("DAO getConditions conditionLogic: " + rec.getStringValue("conditionLogic"));
                if (rec.getStringValue("conditionLogic").equalsIgnoreCase(logic)) {
                    ruleCons = getRuleConditions(ruleCondPK, currXml, origXml);
                } else {
                    l.fine("conidtion logic" + logic);
                    conds.put(logic, ruleCons);
                    ruleCons = new ArrayList();
                    logic = rec.getStringValue("conditionLogic");
                }
            }
            //add te last
            l.fine("conidtion logic" + logic);
            conds.put(logic, ruleCons);
            ruleCons = new ArrayList();
            logic = rec.getStringValue("conditionLogic");

        }

        l.exiting(getClass().getName(), "getConditions", resultRec);
        l.fine("!!!!!!getConditions:" + " size=" + conds.size());
        Iterator mmp = conds.keySet().iterator();
        while (mmp.hasNext()) {
            String key = (String) mmp.next();
            ArrayList lst = (ArrayList) conds.get(key);
            Iterator lstIt = lst.iterator();
            while (lstIt.hasNext()) {
                RuleCondition r = (RuleCondition) lstIt.next();
                l.fine(" CONDITIONS ARE :logic:" + key + " expression" + r.getConditionExpression());
            }
        }
        return conds;
    }

    private ArrayList getRuleConditions(long ruleCondPK, String currXml, String origXml)
        throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getRuleCondition");
        ArrayList result = new ArrayList();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("ruleCondition", new Long(ruleCondPK));
        String logic = null;
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getRuleCondFunctions(inputRecord);

        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                Record rec = (Record) rs.next();
                RuleCondition rc = new RuleCondition();
                long ruleFuncPK = rec.getLongValue("condFunctionId").longValue();
                l.fine("func expression:" + rec.getStringValue("funcExpression"));
                l.fine("func exp type:" + rec.getStringValue("funcExpressionType"));
                rc.setConditionExpression(rec.getStringValue("funcExpression"));
                rc.setExpType(rec.getStringValue("funcExpressionType"));
                rc.setConditionArgs(getRuleConditionArgs(ruleFuncPK, currXml, origXml));
                result.add(rc);
            }
        }
        l.exiting(getClass().getName(), "getRuleCondition", resultRec);
        return result;
    }

    private RuleConditionArgs getRuleConditionArgs(long ruleFuncPK, String currXml, String origXml)
        throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getRuleConditionArgs");
        RuleConditionArgs result = new RuleConditionArgs();
        LinkedHashMap template = new LinkedHashMap();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("condFunc", new Long(ruleFuncPK));
        String logic = null;
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getFuncionArgs(inputRecord);

        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                l.fine("getRuleConditionArgs:hasNext");
                Record rec = (Record) rs.next();
                String orig = rec.getStringValue("origInd");
                if (orig != null && orig.equalsIgnoreCase("Y")) {
                    template.put(DwrConstants.ORIGNAL_INDICATOR + rec.getStringValue("functionArgName").trim(),
                        rec.getStringValue("functionArgValue"));
                } else {
                    template.put(rec.getStringValue("functionArgName").trim(), rec.getStringValue("functionArgValue"));
                }
            }
            l.fine("condition args map:" + template);
            result.setArgTemplate(template);
            result.setArgs(currXml, origXml);
        }
        l.exiting(getClass().getName(), "getRuleConditionArgs", resultRec);
        return result;
    }

    /**
     *
     * @param con
     * @param rulePK
     * @param currXml
     * @param origXml
     * @return   collection of types RuleAction
     * @throws SQLException
     */
    private ArrayList getActions(long rulePK, String currXml, String origXml)
        throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getActions");
        ArrayList result = new ArrayList();
        RuleAction ruleAction = new RuleAction();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("rule", new Long(rulePK));
        String logic = null;
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getRuleActions(inputRecord);
        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                Record rec = (Record) rs.next();
                long ruleActPK = rec.getLongValue("ruleActionId").longValue();
                ruleAction.setActionExp(rec.getStringValue("actionExpression"));
                ruleAction.setActionType(rec.getStringValue("actionType"));
                //getargs
                ruleAction.setActionArgs(getActionArgs(ruleActPK, currXml, origXml));
                l.fine("!!!ruleActPK=" + ruleActPK);
                l.fine("action exp:" + ruleAction.getActionExp());
                l.fine("action  type:" + ruleAction.getActionType());
                result.add(ruleAction);
                ruleAction = new RuleAction();
            }
        }

        l.exiting(getClass().getName(), "getActions", resultRec);
        //test
        Iterator itt = result.iterator();
        int i = 0;
        while (itt.hasNext()) {
            RuleAction ra = (RuleAction) itt.next();

            l.fine("i = " + i + "const=" + ra.getActionArgs().getMdConst());
            i++;
        }
        return result;
    }

    private RuleActionArgs getActionArgs(long actPK, String currXml, String origXml)
                                    throws SQLException{
        Logger l = LogUtils.enterLog(getClass(), "getActionArgs");
        RuleActionArgs result = new RuleActionArgs();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("ruleAction", new Long(actPK));
        String logic = null;
        RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
        RecordSet resultRec = rm.getActionArgs(inputRecord);
        LinkedHashMap fields = new LinkedHashMap();
        LinkedHashMap cons = new LinkedHashMap();
        Iterator rs = resultRec.getRecords();
        if (rs != null) {
            while (rs.hasNext()) {
                Record rec = (Record) rs.next();
                if (rec.getStringValue("actionArgType").equalsIgnoreCase(DwrConstants.ACTION_ARG_FIELD_TYPE)) {
                    l.fine("DwRuleData:getActionArgs:ArgFieldType:FieldName:" + rec.getStringValue("actionArgName"));
                    fields.put(rec.getStringValue("actionArgName"), null);
                }
                if (rec.getStringValue("actionArgType").equalsIgnoreCase(DwrConstants.ACTION_ARG_CONST_TYPE)) {
                    cons.put(rec.getStringValue("actionArgName"), rec.getStringValue("actionArgValue"));
                    l.fine("DwRuleData:getActionArgs:ArgFieldType:ConstdName:" + rec.getStringValue("actionArgName") + " value:" + rec.getStringValue("actionArgValue"));
                }
            }
            result.setMdConst(cons);
            result.setMdFields(fields);
            l.fine("action fields:" + fields);
            l.fine("action const:" + cons);
            result.setArgs(currXml, origXml);
        }
        l.exiting(getClass().getName(), "getActionArgs", resultRec);

        return result;
    }

}