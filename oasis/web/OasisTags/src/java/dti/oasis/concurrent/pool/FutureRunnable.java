package dti.oasis.concurrent.pool;

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
 *
 */

public class FutureRunnable extends FutureTask {
    private Runnable runnable;

    public FutureRunnable(Runnable runnable, Object result) {
        super(runnable, result);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
