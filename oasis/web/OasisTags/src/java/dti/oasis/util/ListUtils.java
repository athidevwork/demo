package dti.oasis.util;

import java.util.Iterator;
import java.util.List;

/**
 * Helper class for working with Lists.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
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
public class ListUtils {

    /**
     * Remove the first matching String item from the list, matching elements using String.equalsIgnoreCase()
     *
     * @param list the list of String elements
     * @param item the element to remove.
     * @return <tt>true</tt> if the collection contained the specified
     *         element; otherwise, false.
     */
    public static boolean removeCaseInsensitive(List list, String item) {
        Iterator e = list.iterator();
        if (item == null) {
            while (e.hasNext()) {
                if (e.next() == null) {
                    e.remove();
                    return true;
                }
            }
        }
        else {
            while (e.hasNext()) {
                if (item.equalsIgnoreCase((String) e.next())) {
                    e.remove();
                    return true;
                }
            }
        }
        return false;
    }
}
