package dti.oasis.busobjs;

/**
 * This interface contains all possible string values for the UPDATE_IND field in the data grid.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 25, 2006
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
public interface UpdateIndicator {
    public static final String FIELD_NAME = "UPDATE_IND";

    public static final String INSERTED = "I";
    public static final String UPDATED = "Y";
    public static final String DELETED = "D";
    public static final String NOT_CHANGED = "N";
}
