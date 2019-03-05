package dti.oasis.dwr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong
 * Date: Apr 6, 2009
 * Time: 3:03:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleJdbcDAO implements RuleDAO {
    public RecordSet loadAllArguments(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadAllArguments");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_LOAD_ALL_ARGUMENTS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "loadAllArguments", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_LOAD_ALL_ARGUMENTS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllArguments", STORED_PROC_LOAD_ALL_ARGUMENTS);
        return rs;
    }

    public RecordSet getRules(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getRules");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_RULES);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getRules", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_RULES, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getRules", STORED_PROC_GET_RULES);
        return rs;

    }

    public RecordSet getRuleConditions(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getRuleConditions");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_RULE_CONDITIONS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getRuleConditions", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_RULE_CONDITIONS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getRuleConditions", STORED_PROC_GET_RULE_CONDITIONS);
        return rs;
    }

    public RecordSet getRuleCondFunctions(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getRuleCondFunctions");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_COND_FUNCTIONS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getRuleCondFunctions", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_COND_FUNCTIONS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getRuleCondFunctions", STORED_PROC_GET_COND_FUNCTIONS);
        return rs;

    }

    public RecordSet getFuncionArgs(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getFuncionArgs");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_FUNCTION_ARGS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getFuncionArgs", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_FUNCTION_ARGS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getFuncionArgs", STORED_PROC_GET_FUNCTION_ARGS);
        return rs;

    }

    public RecordSet getRuleActions(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getRuleActions");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_RULE_ACTIONS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getRuleActions", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_RULE_ACTIONS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getRuleActions", STORED_PROC_GET_RULE_ACTIONS);
        return rs;

    }

    public RecordSet getActionArgs(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getActionArgs");
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_ACTION_ARGS);
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            l.throwing(getClass().getName(), "getActionArgs", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_GET_ACTION_ARGS, se);
            throw ae;
        }

        l.exiting(getClass().getName(), "getActionArgs", STORED_PROC_GET_ACTION_ARGS);
        return rs;


    }

    public void insertDiaryItem(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "insertDiaryItem");
        try {
            StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance(STORED_PROC_INSERT_DIARY_ITEM);
            spDAO.execute(inputRecord);
        } catch (SQLException se) {
            l.throwing(getClass().getName(), "insertDiaryItem", se);
            AppException ae = ExceptionHelper.getInstance().handleException("database error when calling " + STORED_PROC_INSERT_DIARY_ITEM, se);
            throw ae;
        }
    }


    private static String STORED_PROC_LOAD_ALL_ARGUMENTS = "WB_CS_RULE.get_args";
    private static String STORED_PROC_GET_RULES = "WB_CS_RULE.get_rules";
    private static String STORED_PROC_GET_RULE_CONDITIONS = "WB_CS_RULE.get_rule_conditions";
    private static String STORED_PROC_GET_COND_FUNCTIONS = "WB_CS_RULE.get_cond_functions";
    private static String STORED_PROC_GET_FUNCTION_ARGS = "WB_CS_RULE.get_function_args";
    private static String STORED_PROC_GET_RULE_ACTIONS = "WB_CS_RULE.get_rule_actions";
    private static String STORED_PROC_GET_ACTION_ARGS = "WB_CS_RULE.get_action_args";
    private static String STORED_PROC_INSERT_DIARY_ITEM = "oasis_process_event.insert_diary_item";
}
