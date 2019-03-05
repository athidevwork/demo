package dti.oasis.filter;

/**
 * This interface represents of filter that will determine if a given object is accepted.
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
public interface Filter {
    /**
     * Returns true if the given object is accepted by this filter. Otherwise, false.
     */
    boolean accept(Object obj);

    boolean hasDataToCompare();
}
