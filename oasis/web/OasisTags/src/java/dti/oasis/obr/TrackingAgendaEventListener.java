package dti.oasis.obr;

import dti.oasis.app.ConfigurationException;
import dti.oasis.obr.event.OnLoadAddOrChangeEvent;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This TrackingAgendaEventListener class provides logic before/after rule activation
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
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    @Override
    public void beforeActivationFired(org.drools.event.rule.BeforeActivationFiredEvent event) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "beforeActivationFired", new Object[]{event});
        }
        // get the grid Record if it exists
        Record firstNonHeaderRecord  = null;
        Record gridRecord = null;
        boolean isLoadOrChangeEvent = false;
        int gridRecordCount = 0;
        for (Object object : event.getActivation().getObjects()) {
            if (object instanceof Record) {
                Record record = (Record) object;
                if (Record.TYPE_GRID.equals(record.getType())) {
                    gridRecord = record;
                    gridRecordCount++;
                }
                if (firstNonHeaderRecord == null &&
                        (Record.TYPE_GRID.equals(record.getType()) || Record.TYPE_NONGRID.equals(record.getType()))) {
                    firstNonHeaderRecord = record;
                }
            }
            else if (object instanceof OnLoadAddOrChangeEvent) {
                isLoadOrChangeEvent = true;
            }
        }
        if (isLoadOrChangeEvent && gridRecordCount > 1) {
            String message = new StringBuffer().append("More than one grid Records are fired in rule \"")
                .append(event.getActivation().getRule().getName())
                .append("\" for OnLoadAddOrChangeEvent").toString();
            ConfigurationException e = new ConfigurationException(message);
            l.logp(Level.SEVERE, getClass().getName(), "addEnforcedResult", message);
            throw e;
        }
        m_context.setGridRecord(gridRecord);
        m_context.setFirstNonHeaderRecord(firstNonHeaderRecord);
        m_context.setActivationEvent(event);
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "beforeActivationFired", "Before Fire rule = " + event.getActivation().getRule().getName());
        }
        l.exiting(getClass().getName(), "beforeActivationFired");
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "afterActivationFired", new Object[]{event});
        }
        m_context.removeGridRecord();
        m_context.removeFirstNonHeaderRecord();
        m_rulesFiredList.add(event.getActivation().getRule().getName());
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "afterActivationFired", "After Fire rule = " + event.getActivation().getRule().getName());
        }
        l.exiting(getClass().getName(), "afterActivationFired");
    }

    public boolean isRuleFired(String ruleName) {
        for (String firedRuleName : m_rulesFiredList) {
            if (firedRuleName.equals(ruleName)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        m_rulesFiredList.clear();
    }

    public Context getContext() {
        return m_context;
    }

    public void setContext(Context context) {
        this.m_context = context;
    }

    private Context m_context;
    private List<String> m_rulesFiredList = new ArrayList<String>();
    private final Logger l = LogUtils.getLogger(getClass());
}