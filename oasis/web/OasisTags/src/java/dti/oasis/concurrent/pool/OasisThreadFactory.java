package dti.oasis.concurrent.pool;

import java.util.concurrent.ThreadFactory;

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
 * 06/19/2016       cesar       issue #176679 - Customize ThreadFactory.
 * ---------------------------------------------------
 */

public class OasisThreadFactory  implements ThreadFactory {
    private final String poolName;

    public OasisThreadFactory(String poolName){
        this.poolName = poolName;
    }

    public Thread newThread(Runnable r) {
        return new OasisThread(r,poolName);
    }
}
