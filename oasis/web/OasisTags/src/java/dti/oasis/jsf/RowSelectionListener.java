package dti.oasis.jsf;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/5/13
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2013       Parker      use an inner class to replace the implementation of RowSelectionListener interface
 *
 * ---------------------------------------------------
 */
public interface RowSelectionListener<T> {

    /**
     * execute when row is selected
     * @param row                null if no row is selected
     */
    public void processRowSelect(T row);

}
