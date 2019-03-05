package dti.oasis.ows.util;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/11/2014
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ElementValueConverter {
    /**
     * Convert web service element value to oasis format string value.
     * @param obj
     * @param elementPath
     * @return
     */
    public String convert(Object obj, String elementPath);
}
