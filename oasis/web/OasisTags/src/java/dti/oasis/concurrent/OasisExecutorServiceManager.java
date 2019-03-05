package dti.oasis.concurrent;

import dti.oasis.concurrent.pool.OasisCallable;
import dti.oasis.concurrent.pool.OasisRunnable;

import java.util.List;
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
 * 06/19/2016       cesar       issue #176679 - interface for ExecutorService thread pool.
 * ---------------------------------------------------
 */

public interface OasisExecutorServiceManager<T> {
    public void submit(OasisRunnable task);
    public void submit(String threadCategoryName, OasisRunnable task);
    public T submit(OasisCallable<T> task);
    public T submit(String threadCategoryName, OasisCallable<T> task);
    public List<T> submit(List<T> tasks);
    public List<T> submit(String threadCategoryName, List<T> tasks);

}
