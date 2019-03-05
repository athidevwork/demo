package dti.oasis.concurrent;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.concurrent.pool.OasisCallable;
import dti.oasis.concurrent.pool.OasisRunnable;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   06/19/2016
 *  This class has been designed to maintain and execute threading throughout Oasis.
 *  <p>
 *  An ExecutorService will be created for each thread category been defined
 *  in customApplicationConfigEnv.properties. If there is none, then a default
 *  category will be created using the default core thread size and default thread timeout that has
 *  been defined in application-core.properties.
 *  <p>
 *  After an ExecutorService is created for a category, it will be inserted into a ConcurrentHashMap for future use.
 *  <p>
 *  The OasisExecutorServiceManager can be used to invoke a single or a list of callable/runnable threads.
 *  <p>
 *  See PolicyInquiryServiceManagerImpl.loadPolicy for usage of the OasisExecutorServiceManager.
 * @author cesar valencia
 */

/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2016       cesar       issue #176679 - maintain and execute threading.
 * ---------------------------------------------------
 */
public class OasisExecutorServiceManagerImpl<T> implements OasisExecutorServiceManager<T>{

    /**
     * Submit a single callable task.
     *
     * @param threadCategoryName - thread pool category name.
     * @param task - single callable task.
     * @return T is the return type.
     */
    public T submit(String threadCategoryName, OasisCallable<T> task){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "T submit", new Object[]{threadCategoryName, task});
        }

        T result = null;
        List<T> results = new ArrayList<T>();
        List<T> tasks = new ArrayList<T>();

        tasks.add((T)task);
        results = submit(threadCategoryName, tasks);
        for (T r: results){
            result = r;
        }

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "T submit", new Object[]{threadCategoryName, results});
        }

        return result;

    }

    /**
     * Submit a single callable task using the default thread pool category.
     *
     * @param task  - single callable task.
     * @return T is the return type.
     */
    public T submit(OasisCallable<T> task){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "T submit without category", task);
        }

        T result = null;
        result = submit(null, task);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "T submit with out category", result);
        }

        return result;

    }

    /**
     * Submit a single runnable task using the default thread pool category.
     *
     * @param task - single runnable.
     */
    public void submit(OasisRunnable task){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "entering Runnable submit without category", task);
        }

        submit("", task);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "exiting Runnable submit without category", task);
        }

    }

    /**
     * Submit a runnable with a thread pool category name.
     *
     * @param threadCategoryName - thread pool category name.
     * @param task - single runnable.
     */
    public void submit(String threadCategoryName, OasisRunnable task){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "entering Runnable submit with category", new Object[]{threadCategoryName, task});
        }

        OasisExecutorService executorService = this.getExecutorService(threadCategoryName);
        executorService.getExecutorService().submit((Runnable) task);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "exiting Runnable submit with category", new Object[]{threadCategoryName, task});
        }
    }


    /**
     * Submit a list of task using the default thread pool category.
     *
     * @param tasks - list of tasks (callable / runnable).
     * @return T - is the return type.
     */
    public  List<T> submit(List<T> tasks){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "entering  submit List<T> without  category", new Object[]{tasks});
        }

        List<T> results = new ArrayList<T>();

        //use default thread pool category
        results = submit("", tasks);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "exiting submit List<T> without category", new Object[]{tasks, results});
        }
        return results;
    }

    /**
     * Submit a list of task using a thread pool category name.
     *
     * @param threadCategoryName - thread pool category name.
     * @param tasks - list of tasks (callable / runnable).
     * @return T - is the return type.
     */
    public  List<T> submit(String threadCategoryName, List<T> tasks){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "List<T> submit with category", new Object[]{threadCategoryName, tasks});
        }

        List<T> results = new ArrayList<T>();
        OasisExecutorService executorService = this.getExecutorService(threadCategoryName);
        List<Future<T>> futures = new ArrayList<>();

        try {
            for (T task : tasks) {
                if (task instanceof OasisRunnable || task instanceof Runnable) {
                    executorService.getExecutorService().submit((Runnable) task);
                } else if ( task instanceof OasisCallable || task instanceof Callable) {
                    futures.add(executorService.getExecutorService().submit((Callable) task));
                }
            }

            if (futures.size() >0 ) {
                retrieveFuture(futures, results);
            }

        } catch (Exception exx){
            cancelRunningFutures(futures);
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "submit : ", exx);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "List<T> submit with category",  new Object[]{threadCategoryName, results});
        }

        return results;
    }

    /**
     * Returns all the thread futures results that have been submitted.
     * The get() method will wait for the thread to be completed until
     * the specified thread pool time out. If the thread still running after
     * the thread pool time out exceeds, then a Timeout exception will occur.
     *
     * @param futures - list of futures thread results.
     * @param results - list of results.
     */
    private void retrieveFuture(List<Future<T>> futures, List<T> results){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "retrieveFuture", new Object[]{futures, results});
        }

        boolean bRunCancelFutures = false;

        try {
            int threadPoolTimeOut = getThreadPoolTimeOut();

            for (Future<T> f: futures) {
                results.add(f.get(threadPoolTimeOut, TimeUnit.SECONDS));
            }

        } catch (CancellationException ce) {
            //skipping any subsequent app exceptions to handle situations where threads are interrupted.
            l.logp(Level.FINER, getClass().getName(), "retrieveFuture", "Skipping cancellation exceptions during policy check tasks");
        } catch (InterruptedException inte) {
            //skipping any subsequent app exceptions to handle situations where threads are interrupted.
            l.logp(Level.FINER, getClass().getName(), "retrieveFuture", "Skipping interrupted exceptions during policy check tasks");
        } catch (TimeoutException te) {
            bRunCancelFutures = true;

            l.logp(Level.FINER, getClass().getName(), "retrieveFuture", "Time out exception", te);
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "TimeoutException: problem getting thread result in policy thread : ", te);
            throw ae;
        } catch (Exception e) {
            bRunCancelFutures = true;
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Exception: problem getting thread result in policy thread : ", e);
            l.logp(Level.FINER, getClass().getName(), "retrieveFuture", "Exception ", e);
            throw ae;
        } finally {
            if (bRunCancelFutures) {
                cancelRunningFutures(futures);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "retrieveFuture",  new Object[]{results});
        }

    }

    /**
     * Cancel all running futures that have not been completed yet.
     * This is use if a list of threads have been submitted and one of
     * the threads have failed.
     *
     * @param futures - list of futures thread results.
     */
    private void cancelRunningFutures(List<Future<T>> futures) {
        int len = futures.size();

        for (Future<T> futureTask : futures) {
            if (!futureTask.isDone()) {
                futureTask.cancel(true);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "retrieveFuture",  new Object[]{futures});
        }
    }

    /**
     * The the executor service from the concurrent hash map.
     * If the thread pool category does not exist, then it will create one.
     *
     * @param threadPoolCategoryName
     * @return
     */
    private OasisExecutorService getExecutorService (String threadPoolCategoryName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExecutorService", new Object[]{threadPoolCategoryName});
        }

        String threadPoolName = getThreadPoolName(threadPoolCategoryName);

        if (concurrentOasisExecutorService.containsKey(threadPoolName)){
            l.exiting(OasisExecutorServiceManagerImpl.class.getName(), "getExecutorService", threadPoolName);
            return concurrentOasisExecutorService.get(threadPoolName);
        } else {
            l.exiting(OasisExecutorServiceManagerImpl.class.getName(), "getExecutorService", threadPoolName);
            return addThreadPoolName(threadPoolName);
        }
    }

    /**
     * Add a new executor service into the concurrent hash map.
     *
     * @param threadPoolName - value of the category name.
     * @return returns the executor service for the category name.
     */
    private OasisExecutorService addThreadPoolName (String threadPoolName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addThreadPoolName", new Object[]{threadPoolName});
        }

        OasisExecutorService oasisExecutorService = new OasisExecutorService();
        int corePoolSize =0;
        int maxPoolSize = 0;
        int index = threadPoolName.indexOf(".names");
        String poolNamePrefix = threadPoolName;

        if (index > 0 ) {
            poolNamePrefix = threadPoolName.substring(0,index);
        }

        corePoolSize = getThreadPoolSize(poolNamePrefix + ".core.thread.size", DEFAULT_THREAD_POOL_CORE_SIZE);
        maxPoolSize =  getThreadPoolSize(poolNamePrefix + ".max.thread.size", DEFAULT_THREAD_POOL_MAX_SIZE);
        if(corePoolSize<1)
            corePoolSize = maxPoolSize;

        oasisExecutorService.init(threadPoolName, corePoolSize, maxPoolSize);

        //put it into the hashmap
        concurrentOasisExecutorService.put(threadPoolName, oasisExecutorService);

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "addThreadPoolName - added " + threadPoolName + " to concurrentOasisExecutorService", threadPoolName);
            l.logp(Level.FINER, getClass().getName(), "addThreadPoolName", threadPoolName + "core size: " +  corePoolSize + " max size: " + maxPoolSize, threadPoolName);
        }

        return oasisExecutorService;
    }

    /**
     * Retrieve the property name for the thread pool category name.
     *
     * @param threadPoolCategoryName - thread pool category name.
     * @return
     */

    private String getThreadPoolName(String threadPoolCategoryName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getThreadPoolName", new Object[]{threadPoolCategoryName});
        }

        String threadPoolName = DEFAULT_THREAD_POOL_NAME;

        if (!StringUtils.isBlank(threadPoolCategoryName)) {
            Properties properties = ApplicationContext.getInstance().getProperties();
            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith("thread.pool.") && propertyName.endsWith("category.names")) {
                    String value = properties.getProperty(propertyName);
                    if (value.indexOf(threadPoolCategoryName) >= 0) {
                        threadPoolName = propertyName;
                        break;
                    }
                }
            }
        }

        l.exiting(OasisExecutorServiceManagerImpl.class.getName(), "getThreadPoolName", threadPoolName);
        return threadPoolName;
    }

    /**
     * Retrieve the thread pool size.
     *
     * @param threadCategoryName - thread pool category name.
     * @param defaultPoolSize - default thread pool size.
     *
     * @return
     */
    private int getThreadPoolSize (String threadCategoryName, String defaultPoolSize) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getThreadPoolSize", new Object[]{threadCategoryName, defaultPoolSize});
        }

        int threadPoolSize=0;

        if (ApplicationContext.getInstance().hasProperty(threadCategoryName)) {
            threadPoolSize = Integer.parseInt(ApplicationContext.getInstance().getProperty(threadCategoryName));
        } else if (ApplicationContext.getInstance().hasProperty(defaultPoolSize)) {
            threadPoolSize = Integer.parseInt(ApplicationContext.getInstance().getProperty(defaultPoolSize));
        }

        l.exiting(OasisExecutorServiceManagerImpl.class.getName(), "getThreadPoolSize", threadCategoryName + "=" + threadPoolSize);
        return threadPoolSize;
    }

    /**
     * Return the thread pool time out.
     * @return
     */
    private int getThreadPoolTimeOut() {
        if (ApplicationContext.getInstance().hasProperty(DEFAULT_THREAD_POOL_TIME_OUT)) {
            threadPoolTimeOut = Integer.parseInt(ApplicationContext.getInstance().getProperty(DEFAULT_THREAD_POOL_TIME_OUT));
        }
        return threadPoolTimeOut;
    }

    int threadPoolTimeOut;
    private final String DEFAULT_THREAD_POOL_NAME = "thread.pool.default.category";
    private final String DEFAULT_THREAD_POOL_CORE_SIZE = "thread.pool.default.core.thread.size";
    private final String DEFAULT_THREAD_POOL_TIME_OUT = "thread.pool.default.timeout";
    private final String DEFAULT_THREAD_POOL_MAX_SIZE = "thread.pool.default.max.thread.size";
    
    ConcurrentHashMap<String, OasisExecutorService> concurrentOasisExecutorService = new ConcurrentHashMap<String, OasisExecutorService>();
    private final Logger l = LogUtils.getLogger(getClass());
}
