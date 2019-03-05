package dti.oasis.busobjs;

import java.util.Iterator;

/**
 * This interface represents a group of info objects. When a collection of Info objects must travel
 * through the system, they travel within an InfoGroup object. This object contains a collection of
 * Info objects, and any significant attributes related to the group as a whole (ex. total premium).
 * The InfoGroup object is also an Info object, and thus contains no business logic.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface InfoGroup extends Info {

    /**
     * Add the given Info object to this InfoGroup.
     */
    public void add(Info info);

    /**
     * Return an Iterator of the contained Info objects.
     */
    public Iterator iterator();

    /**
     * Return the number of Info objects in this InfoGroup.
     */
    public int size();

    /**
     * Clear the contents of this InfoGroup.
     */
    public void clear();

}
