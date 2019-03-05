package dti.oasis.concurrent.pool;

import java.util.concurrent.atomic.AtomicInteger;

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
 * 06/19/2016       cesar       issue #176679 - Customize Threads.
 * ---------------------------------------------------
 */

public class OasisThread extends Thread{
    public static final String NAME = "OasisThread";
    private static final AtomicInteger created = new AtomicInteger();

    public OasisThread(Runnable r, String name){
        super(r,name+"-"+created.incrementAndGet());
    }

    public void run(){
        super.run();
    }
}
