package dti.oasis.obr.log;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.logger.KnowledgeRuntimeLogger;

/**
 * This class provides the logger factory
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 08, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public abstract class RuleLogFactory {

    private static RuleLogFactory c_instance;

    /**
     * Returns a synchronized static instance of RuleLogFactory that has the implementation information.
     */
    public synchronized static RuleLogFactory getInstance() {
        if (c_instance == null) {
            c_instance = new RuleLogFactoryImpl();
        }
        return c_instance;
    }

    public abstract KnowledgeRuntimeLogger newLogger(KnowledgeRuntimeEventManager session);

}
