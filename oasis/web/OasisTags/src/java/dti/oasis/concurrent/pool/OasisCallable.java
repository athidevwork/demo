package dti.oasis.concurrent.pool;

import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestSession;
import java.util.concurrent.Callable;

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
 * 06/19/2016       cesar       issue #176679 - centralize all runnable. Any Callable should use the OasisExecutorService.
 * ---------------------------------------------------
 */

public abstract class OasisCallable<T> implements Callable<T> {
    public OasisCallable(){
        session = RequestLifecycleAdvisor.getInstance().getRequestState();
    }

    @Override
    public T call(){
        T obj = null;
        try {
            RequestLifecycleAdvisor.getInstance().initializeFromRequestState(this.session);
            obj = execute();
        } finally{
            RequestLifecycleAdvisor.getInstance().terminate();
        }
        return obj;
    }

    public abstract T execute();
    public abstract String getThreadName();
    private RequestSession session;
}
