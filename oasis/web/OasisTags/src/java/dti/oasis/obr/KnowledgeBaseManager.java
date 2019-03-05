package dti.oasis.obr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.obr.event.RuleEvent;
import dti.oasis.obr.impl.KnowledgeBaseManagerImpl;
import dti.oasis.recordset.Record;

/**
 * This KnowledgeBaseManager class provides abstract methods to access the Drools knowledge base
 *
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
public abstract class KnowledgeBaseManager {

    /**
     * The bean name of a KnowledgeBaseManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "KnowledgeBaseManager";

    /**
     * Returns a synchronized static instance of Knowledge Manager that has the implementation information.
     */
    public synchronized static KnowledgeBaseManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (KnowledgeBaseManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                c_instance = new KnowledgeBaseManagerImpl();
                ((KnowledgeBaseManagerImpl) c_instance).verifyConfig();
                ((KnowledgeBaseManagerImpl) c_instance).initialize();
            }
        }
        return c_instance;
    }

    /**
     * has knowledge or not
     * @param pageCode
     * @return
     */
    public abstract boolean hasKnowledgeBase(String pageCode);

    /**
     * Fire the rules in the knowledge base for the page.
     * @param pageCode
     * @param context
     * @param factData
     * @param ruleEvents
     * @param processingEvents
     */
    public abstract void fireRules(String pageCode, Context context, Object[] factData, RuleEvent[] ruleEvents,
                                   ProcessingEvents processingEvents);
    
    /**
     * get package metaData
     * @param pageCode
     * @return
     */
    public abstract PackageMetaData getPackageMetaData(String pageCode);

    /**
     * validate rule
     *
     * @param pageCode
     * @param record
     * @return
     */
    public abstract String validateRule(String pageCode, Record record);


    /**
     * get rule source code
     *
     * @param record
     * @return
     */
    public abstract String getRuleSourceCode(Record record);


    private static KnowledgeBaseManager c_instance;
}
