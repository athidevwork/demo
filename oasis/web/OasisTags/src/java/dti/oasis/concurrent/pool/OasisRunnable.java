package dti.oasis.concurrent.pool;

import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestSession;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   06/19/2016
 *
 * @author cesar valencia
 */

/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2016       cesar       issue #176679 - centralize all runnable. Any Runnable should use the OasisExecutorService.
 * ---------------------------------------------------
 */

public abstract class OasisRunnable implements Runnable {
    public OasisRunnable(){
        session = RequestLifecycleAdvisor.getInstance().getRequestState();
    }

    @Override
    public void run() {
        try {
            RequestLifecycleAdvisor.getInstance().initializeFromRequestState(this.session);
            execute();
        } finally {
            RequestLifecycleAdvisor.getInstance().terminate();
        }
    }

    public abstract void execute();
    public abstract String getThreadName();
    private RequestSession session;
}
