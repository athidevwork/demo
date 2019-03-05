package dti.oasis.concurrent.pool;

import dti.oasis.util.LogUtils;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * 06/19/2016       cesar       issue #176679 - Monitor threads execution.
 * ---------------------------------------------------
 *
 */

public class OasisThreadPoolExecutor extends ThreadPoolExecutor {
    private final ThreadLocal startTime = new ThreadLocal();
    private final AtomicLong totalTime = new AtomicLong();
    private final AtomicLong numberOfTasks = new AtomicLong();

    public OasisThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    public OasisThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
    public OasisThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }
    public OasisThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    protected void beforeExecute(Thread t, Runnable r){
        Logger l = LogUtils.enterLog(OasisThreadPoolExecutor.class, "beforeExecute");

        super.beforeExecute(t,r);

        String taskName = getTaskName(r);
        Date threadStartTime = new Date();

        startTime.set(System.nanoTime());

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "beforeExecute - [Thread :" + r + "] - Thread Name: " + taskName + " start: " + threadStartTime.toString(), taskName);
        }

    }

    public <T> FutureCallable<T> newTaskFor(Callable<T> callable) {
        return new FutureCallable<T>(callable);
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureRunnable(runnable, value);
    }

    protected void afterExecute(Runnable r, Throwable t){
        Logger l = LogUtils.enterLog(OasisThreadPoolExecutor.class, "afterExecute");

        String taskName = getTaskName(r);

        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - (Long)startTime.get();
            numberOfTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);

            if (l.isLoggable(Level.FINER)) {
                Date threadEndTime = new Date();
                double elapsedTimeInSeconds=0;
                elapsedTimeInSeconds = TimeUnit.MILLISECONDS.convert(taskTime, TimeUnit.NANOSECONDS) / 1000.0;
                l.logp(Level.FINER, getClass().getName(), "afterExecute - [Thread :" + r + "] - Thread Name: " + taskName + " end: " + threadEndTime.toString() + " , total time taken = " + elapsedTimeInSeconds + " sec.", taskName);
            }
        } finally {
            super.afterExecute(r, t);
        }

    }

    private String getTaskName(Runnable r){
        String taskName="";

        try {
            if (r instanceof FutureCallable){
                OasisCallable oasisCallable = (OasisCallable) ((FutureCallable) r).getCallable();
                taskName = oasisCallable.getThreadName();
            } else if (r instanceof FutureRunnable) {
                OasisRunnable oasisRunnable = (OasisRunnable)((FutureRunnable) r).getRunnable();
                taskName = oasisRunnable.getThreadName();
            }
        } catch (Exception e) {
            // do nothing
        }

        return taskName;
    }

    protected void terminated() {
        try{
            if ( numberOfTasks.get() > 0) {
                long sec = (totalTime.get() / numberOfTasks.get()) /1000;
            }
        }finally{
            super.terminated();
        }
    }
}
