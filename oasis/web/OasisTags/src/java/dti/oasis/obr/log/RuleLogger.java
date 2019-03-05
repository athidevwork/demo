package dti.oasis.obr.log;

import dti.oasis.util.LogUtils;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

/**
 * This class provides the logger for rule
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
public class RuleLogger extends WorkingMemoryLogger {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public RuleLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }

    public RuleLogger(KnowledgeRuntimeEventManager session) {
        super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        LogUtils.getLogger(this.getClass()).logp(Level.FINE, getClass().getName(), "logEventCreated", logEvent.toString());
    }
}

