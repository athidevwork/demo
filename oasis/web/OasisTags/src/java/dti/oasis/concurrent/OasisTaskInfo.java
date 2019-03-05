package dti.oasis.concurrent;

import java.util.Date;

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
 * 06/19/2016       cesar       issue #176679 - used by OasisCallable or OasisRunnable to keep
 *                                              track of task execution information.
 * ---------------------------------------------------
 */
public class OasisTaskInfo {
    Date startDate;
    Date endDate;
    String taskName;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public long getElapseTime(){
        long elapseTime = 0;
        try{
            long startTime = this.startDate.getTime();
            long endTime = this.endDate.getTime();

            elapseTime = (endTime - startTime) /1000;

        } catch (Exception ex) {

        }

        return  elapseTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
