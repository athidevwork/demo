package dti.oasis.util;

import java.io.Serializable;

/**
 * JavaBean encapsulating Subsystem information
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 6, 2005 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class SubsystemInfo implements Serializable {
    private String subsystem;
    private boolean installed;
    private boolean used;

    /**
     * Convenience Constructor
     * @param subsystem
     * @param installed
     * @param used
     */
    public SubsystemInfo(String subsystem, boolean installed, boolean used) {
        this.subsystem = subsystem;
        this.installed = installed;
        this.used = used;
    }

    /**
     * Default constructor
     */
    public SubsystemInfo() {
    }

    /**
     * Getter for Subsystem
     * @return e.g. CM, PM, RM, FM
     */
    public String getSubsystem() {
        return subsystem;
    }

    /**
     * Setter for subsystem
     * @param subsystem e.g. CM, PM, RM, FM
     */
    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Getter for installed flag
     * @return true/false
     */
    public boolean isInstalled() {
        return installed;
    }

    /**
     * Setter for installed flag
     * @param installed
     */
    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    /**
     * Getter for used flag
     * @return true/false
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Setter for used flag
     * @param used
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Overridden toString method
     * @return String dump of object.
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.SubsystemInfo");
        buf.append("{subsystem=").append(subsystem);
        buf.append(",installed=").append(installed);
        buf.append(",used=").append(used);
        buf.append('}');
        return buf.toString();
    }
}
