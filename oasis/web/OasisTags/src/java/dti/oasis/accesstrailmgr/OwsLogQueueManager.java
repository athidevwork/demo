package dti.oasis.accesstrailmgr;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public interface OwsLogQueueManager {

    /**
     * Method to schedule a job to be executed.
     *
     * @param owsLogRequest
     */
    public void schedule(OwsLogRequest owsLogRequest);

    /**
     * Method to finish a processing job.
     *
     * @param owsLogRequest the job request object
     */
    public void finishedProcessing(OwsLogRequest owsLogRequest);

    /**
     * Method to check the request in the processing queue.
     *
     * @param owsLogRequest the job request object
     */
    public boolean checkJobStatusForRequest(OwsLogRequest owsLogRequest);
}
