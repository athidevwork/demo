package dti.oasis.concurrent.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
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
 * 06/19/2016       cesar       issue #176679 - centralize all FutureTask.
 * ---------------------------------------------------
 */

public class FutureCallable<V> extends FutureTask<V>
{
    private Callable<V> callable;
    public FutureCallable(Callable<V> callable) {
        super(callable);
        this.callable = callable;
    }
    public Callable<V> getCallable() {
        return callable;
    }
}