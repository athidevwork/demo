package dti.pm.workflowmgr.jobqueuemgr;

import dti.oasis.request.RequestSession;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.pm.workflowmgr.jobqueuemgr.impl.RequestState;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Date;
import java.util.List;
import java.net.InetAddress;
import java.io.Serializable;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/13/2008       fcb         get/setReturnType removed.
 * 05/08/2009       fcb         Added RequestState for a JobRequest.
 *                              The following is the Finite-state machine of a JobRequest:
 *                              Waiting       Processing      Result
 *                              Queues        Queues          Queue
 *                                                            COMPLETED
 *                                                         /
 *                              INITIALIZD -- PROCESSING
 *                                                         \
 *                                                            FAILED
 * 05/20/2010       fcb         FINE level messages added.
 * 06/28/2010       fcb         109187: policyNo set into the request.
 * 09/20/2012       fcb         Issue 136956: processMessage - added logic to record the user id in the JobRequest.
 * ---------------------------------------------------
 */
public class JobRequest implements Serializable {

    public JobRequest(String policyNo) {
        setId(generateJobId(policyNo));
        setPolicyNo(policyNo);
    }

    public void setMethod (String methodName) {
        this.methodName = methodName;
    }

    public void setArgumentTypes (Class [] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    public void setArguments (Object [] arguments) {
        this.arguments = arguments;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getId () {
        return id;
    }

    public String getMethod () {
        return methodName;
    }

    public Class [] getArgumentTypes () {
        return argumentTypes;
    }

    public Object [] getArguments () {
        return arguments;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public synchronized void setJobInitialized() {
        Logger l = LogUtils.enterLog(getClass(), "setJobInitialized");
        requestState = RequestState.INITIALIZED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setJobInitialized", "LRT - JobRequest: job state is " + requestState);
        }
        l.exiting(getClass().getName(), "setJobInitialized");
    }

    public synchronized boolean isJobInitialized() {
        return requestState.isInitialized();
    }

    public synchronized void setJobProcessing() {
        Logger l = LogUtils.enterLog(getClass(), "setJobProcessing");
        requestState = RequestState.PROCESSING;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setJobProcessing", "LRT - JobRequest: job state is " + requestState);
        }
        l.exiting(getClass().getName(), "setJobProcessing");
    }

    public synchronized boolean isJobProcessing() {
        return requestState.isProcessing();
    }

    public synchronized void setJobComplete() {
        Logger l = LogUtils.enterLog(getClass(), "setJobComplete");
        requestState = RequestState.COMPLETED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setJobComplete", "LRT - JobRequest: job state is " + requestState);
        }
        l.exiting(getClass().getName(), "setJobComplete");
    }

    public synchronized boolean isJobComplete() {
        return requestState.isCompleted();
    }

    public synchronized void setJobFailed() {
        Logger l = LogUtils.enterLog(getClass(), "setJobFailed");
        requestState = RequestState.FAILED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setJobFailed", "LRT - JobRequest: job state is " + requestState);
        }
        l.exiting(getClass().getName(), "setJobFailed");
    }

    public synchronized boolean isJobFailed() {
        return requestState.isFailed();
    }

    public synchronized boolean isJobHandled() {
        return (isJobComplete() || isJobProcessing());
    }

    public void setBeanName (String beanName) {
        this.beanName = beanName;
    }

    public RequestState getState() {
        return requestState;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setRequestState (RequestSession requestSession) {
        this.m_requestSession = requestSession;
    }

    public RequestSession getRequestSession() {
        return m_requestSession;
    }

    public void setJobCategory(JobCategory category) {
        this.category = category;
    }

    public JobCategory getJobCategory() {
        return category;
    }

    public void setPolicyLockId(String policyLockId) {
        this.policyLockId = policyLockId;
    }

    public String getPolicyLockId() {
        return policyLockId;
    }

    public void setMessages(List messages) {
        this.messages = messages;
    }

    public List getMessages() {
        return messages;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setException(AppException exception) {
        this.exception = exception;
    }

    public AppException getException() {
        return exception;
    }

    public long getStartExecutionTime() {
        return startExecutionTime;
    }

    public void setStartExecutionTime(long startExecutionTime) {
        this.startExecutionTime = startExecutionTime;
    }

    public long getFinishExecutionTime() {
        return finishExecutionTime;
    }

    public void setFinishExecutionTime(long finishExecutionTime) {
        this.finishExecutionTime = finishExecutionTime;
    }

    public long getElapsedExecutionTime () {
        return getFinishExecutionTime() - getStartExecutionTime();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(
            "JobRequest{id="+id+
                      ",category:"+category+
                      ",methodName:"+methodName+
                      ",beanName:"+beanName+
                      ",argumentTypes{"+
                      ",m_requestSession:"+ m_requestSession +
                      ",returnObject:"+returnObject);
        for (int i=0; i<argumentTypes.length; i++)
                            sb.append(argumentTypes[i]+" ");
        sb.append("},arguments{");
        for (int i=0; i<argumentTypes.length; i++)
                    sb.append(arguments[i]+" ");

        sb.append("}");
        return sb.toString();
    }

    /**
     * Method that generates an unique request id for long running transactions.
     * <p/>
     * The unique id consists of:
     * 1. client ip address + "-" +
     * 2. policy id + "-" +
     * 3. GregorianCalendar object's current milli second +
     * 4. A Random number
     * <p/>
     *
     * @param policyNo         The current policy number for which we are attempting to generate a job id.
     * @return String           a unique lock id.
     */
    private String generateJobId(String policyNo) {
        Logger l = LogUtils.enterLog(getClass(), "generateJobId", new Object[]{policyNo});
        String UniqueId = "";

        try {
            Calendar cal = new GregorianCalendar();
            UniqueId = policyNo + "-" + String.valueOf(cal.getTimeInMillis());

            InetAddress ipAddress = InetAddress.getLocalHost();
            String clientIPAddress = "";
            if (ipAddress != null) {
                if (ipAddress.getHostAddress() != null) {
                    clientIPAddress = ipAddress.getHostAddress();
                }
            }
            UniqueId = clientIPAddress + " - " + UniqueId;

            String randomNumber = String.valueOf(Math.random());
            randomNumber = ((64 - UniqueId.length()) > 0 ? (randomNumber.length() > (64 - UniqueId.length()) ? randomNumber.substring(0, (64 - UniqueId.length())) : randomNumber) : "");
            UniqueId = UniqueId + randomNumber;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to generate unique job id.", e);
            l.throwing(getClass().getName(), "generateJobId", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "generateJobId", UniqueId);
        return UniqueId;
    }

    private String id;
    private JobCategory category;
    private String methodName;
    private String beanName;
    private RequestSession m_requestSession;
    private Class[] argumentTypes;
    private Object[] arguments;
    private Object returnObject;
    private volatile RequestState requestState;
    private List messages;
    private AppException exception;
    private String policyLockId;
    private String policyNo;
    private long startExecutionTime;
    private long finishExecutionTime;
    private String userId;

    public static final String JOB_ID = "jobId";
    private static final long serialVersionUID = (new Random((new Date()).getTime())).nextLong();
}
