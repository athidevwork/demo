package dti.oasis.obr;

import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.SysParmProvider;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.RuleContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This RuleFunction class provides all functions for rules
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class RuleFunction {


    /**
     * function example
     *
     * @param ruleContext
     * @param message
     */
    public static void example(RuleContext ruleContext, String message) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(RuleFunction.class.getName(), "example", new Object[]{ruleContext,message});
        }
        Context context = getContext(ruleContext);
        PageBean page = context.getPageBean();
        HttpServletRequest request = context.getRequest();
        HttpSession session = context.getSession();
        System.out.println(message);
        l.exiting(RuleFunction.class.getName(), "example");
    }

    /**
     * get global Context object
     *
     * @param ruleContext
     * @return
     */
    public static Context getContext(RuleContext ruleContext) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(RuleFunction.class.getName(), "getContext", new Object[]{ruleContext});
        }

        KnowledgeRuntime knowledgeRuntime = ruleContext.getKnowledgeRuntime();
        Context context = (Context) knowledgeRuntime.getGlobal("c");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(RuleFunction.class.getName(), "getContext", context);
        }
        return context;
    }
    private static final Logger l = LogUtils.getLogger(RuleFunction.class);
}