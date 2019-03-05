package dti.oasis.concurrent;

import dti.oasis.concurrent.pool.OasisThreadFactory;
import dti.oasis.concurrent.pool.OasisThreadPoolExecutor;
import dti.oasis.util.LogUtils;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   06/19/2016
 *  OasisExecutorService - This class has been designed to handle any future threading in Oasis.
 *  This class is handled through OasisExecutorServiceManagerImpl.java.
 * @author cesar valencia
 */

/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2016       cesar       issue #176679 - Main class for ExecutorService thread pool.
 * ---------------------------------------------------
 *
 */
public class OasisExecutorService {
    private String threadPoolName;
    private ExecutorService executorService = null;
    private int coreThreadSize;
    private int maxThreadSize;
    private ExecutorCompletionService exeutorCompletionService = null;

    public OasisExecutorService(){

    }

    public void init(String threadPoolName, int coreThreadSize, int  maxThreadSize){
        this.threadPoolName = threadPoolName;
        this.coreThreadSize = coreThreadSize;
        this.maxThreadSize = maxThreadSize;

        if ( executorService == null){
            this.executorService = newFixedThreadPool();
            exeutorCompletionService = new ExecutorCompletionService(executorService);
        }
    }

    public int getActiveThreads(){
        return  ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    public int getQueueSize(){
        return ((ThreadPoolExecutor) executorService).getQueue().size();
    }

    public long getCompletedTaskCount(){
        return ((ThreadPoolExecutor) executorService).getCompletedTaskCount();
    }

    public int getPoolSize(){
        return ((ThreadPoolExecutor) executorService).getPoolSize();
    }


    public int getCoreThreadSize() {
        return coreThreadSize;
    }


    public int getMaxThreadSize() {
        return maxThreadSize;
    }


    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = newFixedThreadPool();
        }

        return executorService;
    }

    public ExecutorCompletionService getExecutorCompletionService() {
        if (executorService == null) {
            executorService = newFixedThreadPool();
            exeutorCompletionService = new ExecutorCompletionService(executorService);
        }
        return exeutorCompletionService;
    }

    public void setExecutorService(ExecutorService executorThreadPool) {
        this.executorService = executorThreadPool;
    }

    private ExecutorService newFixedThreadPool() {
        Logger l = LogUtils.enterLog(getClass(), "newFixedThreadPool threadPool Name: " + threadPoolName);

        ThreadPoolExecutor pool = new OasisThreadPoolExecutor(getCoreThreadSize(),
            getMaxThreadSize(),
            1L, TimeUnit.NANOSECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new OasisThreadFactory(threadPoolName));

        pool.allowCoreThreadTimeOut(true);

        l.exiting(getClass().getName(), "newFixedThreadPool ThreadPoolName: " + threadPoolName + ", core size: " + getCoreThreadSize() + ", max size: " + getMaxThreadSize(), threadPoolName);

        return pool;
    }

}