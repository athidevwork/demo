package dti.pm.workflowmgr.jobqueuemgr;

import dti.pm.workflowmgr.jobqueuemgr.impl.RequestState;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       fcb         finishJob renamed to cleanupJob.
 * 05/13/2008       fcb         notifyQueueMonitor added.
 * 08/03/2010       fcb         finishedProcessing
 * ---------------------------------------------------
 */
public interface JobQueueManager {

    /**
     * Method to get a JobRequest based on a given id.
     * Throws IllegalArgumentException if the JobRequest is not found.
     * @param id
     * @return
     */
    public JobRequest getJob (String id);

    /**
     * Method to schedule a job to be executed.
     * @param jobRequest
     */
    public void scheduleJob (JobRequest jobRequest);

    /**
     * Method to clean up a job.
     * @param jobRequest
     */
    public void cleanupJob (JobRequest jobRequest);

    /**
     * Method to notify the queue monitor to check for jobs that could be processed.
     */
    public void notifyQueueMonitor();

    /**
     * Method to finish a processing job.
     * @param jobRequest the job request object
     * @param requestState the state the job is in
     */
    public void finishedProcessing(JobRequest jobRequest, RequestState requestState);

    /**
     * Method to return a job category evaluator
     */
    public JobCategoryEvaluator getJobCategoryEvaluator();

}
