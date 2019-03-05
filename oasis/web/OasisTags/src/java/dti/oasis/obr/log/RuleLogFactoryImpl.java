package dti.oasis.obr.log;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.logger.KnowledgeRuntimeLogger;

/**
 * This class provides the implementation of logger factory
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
public class RuleLogFactoryImpl extends RuleLogFactory {

    /**
     * Creates a rule logger
     *
     * @param session
     * @return
     */
    public KnowledgeRuntimeLogger newLogger(KnowledgeRuntimeEventManager session) {
        return new RuleLoggerWrapper(new RuleLogger(session));
    }


    private class RuleLoggerWrapper implements KnowledgeRuntimeLogger {

        // private RuleLoggerWrapper logger;

        public RuleLoggerWrapper(RuleLogger logger) {
            // this.logger = logger;
        }

        public void close() {
            // Do nothing
        }
    }

}
