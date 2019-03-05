package dti.oasis.var;

import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;
import dti.oasis.util.QueryParm;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Logger;
/** Implenmentaion of IRuleDAO with DB for Validation/Action Rule Engine
 * Need construct the object with Connection
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */
public class RuleDAO implements IRuleDAO {
    Connection conn=null;
    String sqlSelectValidationFormRules = "select web_va_rule_pk,\n" +
            "       web_form_id,\n" +
            "       descr,\n" +
            "       action_type,\n" +
            "       action_message,\n" +
            "       parms \n" +
            "  from web_va_rule\n" +
            " where action_type in ('ERROR', 'WARNING', 'MESSAGE')\n" +
            "   and web_form_id = ? ";
    String sqlSelectActionFormRules = "select web_va_rule_pk,\n" +
            "       web_form_id,\n" +
            "       descr,\n" +
            "       action_type,\n" +
            "       action_message,\n" +
            "       parms \n" +
            "  from web_va_rule\n" +
            " where action_type not in ('ERROR', 'WARNING', 'MESSAGE')\n" +
            "   and web_form_id = ? ";
    String sqlSelectIsOriginalInvolved = "select 'Y' from web_va_rule r, web_va_rule_cond c\n" +
            "where r.web_va_rule_pk = c.web_va_rule_fk\n" +
            "and c.condition_expr like '%.original%'";
    String sqlSelectRuleConditions = "select  c.condition_expr, c.condition_syntax_code \n" +
            "  from web_va_rule_cond c\n" +
            " where c.web_va_rule_fk = ?";
    String sqlSelectFormRuleInfo="Select r.web_va_rule_pk,\n" +
            "       r.web_form_id,\n" +
            "       r.descr,\n" +
            "       r.action_type,\n" +
            "       r.action_message,\n" +
            "       parms,\n" +
            "       (select count(*)\n" +
            "          from web_va_rule_cond c0\n" +
            "         where c0.web_va_rule_fk = r.web_va_rule_pk) count_conds,\n" +
            "       nvl((select 'Y'\n" +
            "             from web_va_rule_cond c1\n" +
            "            where c1.web_va_rule_fk = r.web_va_rule_pk\n" +
            "              and c1.condition_expr like '%.original%'),\n" +
            "           'N') hasOriginal\n" +
            "  From web_va_rule r\n" +
            " where r.web_form_id = ?";

    /**
     *  Constructor
      * @param conn Connection
     */
    public RuleDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieve Rule information for the given form id
     * @param webFormID
     * @return RuleInfo
     * @throws Exception
     */
    public RuleInfo retrieveFormRuleInfo(String webFormID) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "retrieveFormRuleInfo", new Object[]{webFormID});
        DisconnectedResultSet drsRuleInfo = Querier.doQuery(this.sqlSelectFormRuleInfo, conn, new Object[]{new QueryParm(Types.VARCHAR, webFormID)},false);
        RuleInfo ri = new RuleInfo();
        if (drsRuleInfo.first()) {
            ri.setFormID(drsRuleInfo.getString("WEB_FORM_ID"));
            ri.setInvolvedOriginal(drsRuleInfo.getString("HASORIGINAL").equals("Y"));
            ri.setParms(!drsRuleInfo.isNull("PARMS") ? drsRuleInfo.getString("PARMS") : "");
            ri.setRuleCount(drsRuleInfo.getRowCount());
        }
        log.exiting(this.getClass().getName(), "retrieveFormRuleInfo", new Object[]{ri});
        return ri;
   }

    /**
     * Retireve Rules for the given form and type
     * @param webFormID
     * @param ruleType   ACTION/VALIDATION
     * @return ArrayList of Rule
     * @throws Exception
     */
    public ArrayList retrieveRules(String webFormID, String ruleType) throws Exception{
        Logger log = LogUtils.enterLog(this.getClass(), "retrieveRules", new Object[]{webFormID,ruleType});
        String sql=this.sqlSelectValidationFormRules;
        if(ruleType.equalsIgnoreCase("ACTION")){
            sql=this.sqlSelectActionFormRules;
        }
        DisconnectedResultSet drsRules= Querier.doQuery(sql, conn, new Object[]{new QueryParm(Types.VARCHAR, webFormID)},false);
        ArrayList ruleList = new ArrayList(drsRules.getRowCount());
        while (drsRules.next()) {
/*
            " web_va_rule_pk,         web_form_id,\n" +
            "       descr,\n" +
            "       action_type,\n" +
            "       action_message,\n" +
            "       form_proc_col_name,\n" +
            "       form_proc_col_vale\n"
*/
            long rulePK = drsRules.getLong("web_va_rule_pk");
            String action_type = drsRules.getString("action_type");
            String action_message = drsRules.getString("action_message");
            String descr = drsRules.getString("descr");
            String parms = drsRules.getString("parms");

            String ruleClassName = "dti.oasis.var.RuleApply" + action_type;
            Class ruleClass = Class.forName(ruleClassName);
            Rule r = (Rule) ruleClass.newInstance();
            r.setFormID(webFormID);
            r.setRuleID(rulePK);
            r.setActionType(action_type);
            r.setMessage(action_message);
            r.setDescr(descr);
            r.setParms(parms);
            r.setConditions(this.retrieveRuleCondition(rulePK));
            ruleList.add(r);
        }
        log.exiting(this.getClass().getName(), "retrieveRules", new Object[]{"ruleList.size()="+ruleList.size(),ruleList});

        return ruleList;
    }

    /**
     * Retrieve conditions for a given rule
     * @param rulePK
     * @return ArrayList of condition
     * @throws Exception
     */
    public ArrayList retrieveRuleCondition(long rulePK) throws Exception{
        Logger log = LogUtils.enterLog(this.getClass(), "retrieveRuleCondition", new Object[]{new Long(rulePK)});
        ArrayList conditions = new  ArrayList();
        DisconnectedResultSet drsConnditions= Querier.doQuery(this.sqlSelectRuleConditions, conn, new Object[]{new QueryParm(Types.NUMERIC, rulePK)},false);
         while(drsConnditions.next()){
            String expr = drsConnditions.getString(1);
            String syntaxCode = drsConnditions.getString(2);
            Condition c = new  Condition(expr,syntaxCode );
            conditions.add(c);
         }
        log.exiting(this.getClass().getName(), "retrieveRuleCondition", new Object[]{conditions});
        return  conditions;
    }


}
