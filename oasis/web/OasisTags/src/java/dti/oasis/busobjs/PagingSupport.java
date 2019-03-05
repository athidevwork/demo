package dti.oasis.busobjs;

/**
 * Support class for determining pagination information.
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
public class PagingSupport {
    /**
     * Get the first row index on the given page, where both the pageNum and firstRowIndex are 1 based.
     *
     * @param rowsPerPage number of rows per page.
     * @param pageNum     desired page number
     */
    public static Integer getFirstRowIndex(Integer rowsPerPage, Integer pageNum) {
        return new Integer(getFirstRowIndex(rowsPerPage.intValue(), pageNum.intValue()));
    }

    /**
     * Get the first row index on the given page, where both the pageNum and firstRowIndex are 1-based.
     *
     * @param rowsPerPage number of rows per page.
     * @param pageNum     desired page number
     */
    public static int getFirstRowIndex(int rowsPerPage, int pageNum) {
        return (rowsPerPage * (pageNum - 1)) + 1;
    }

    /**
     * Get the page number for the given index
     *
     * @param rowsPerPage number of rows per page.
     * @param rowIndex    desired row index
     */
    public static int getPageNumber(int rowsPerPage, int rowIndex) {
        if (rowIndex == 0)
            throw new IndexOutOfBoundsException("PagingSupport treats row indexes as 1-based.");
        if (rowIndex % rowsPerPage == 0) {
            return rowIndex / rowsPerPage;
        } else
            return rowIndex / rowsPerPage + 1;
    }

    /**
     * Get the last row index on the given page, where both the pageNum and firstRowIndex are 1 based.
     *
     * @param rowsPerPage number of rows per page.
     * @param pageNum     desired page number
     * @param totalRows   total number of rows.
     */
    public static Integer getLastRowIndex(Integer rowsPerPage, Integer pageNum, Integer totalRows) {
        return new Integer(getLastRowIndex(rowsPerPage.intValue(), pageNum.intValue(), totalRows.intValue()));
    }

    /**
     * Get the last row index on the given page, where both the pageNum and firstRowIndex are 1 based.
     * If the total rows is 0, the last row index is 0, indicating there are no rows.
     *
     * @param rowsPerPage number of rows per page.
     * @param pageNum     desired page number
     * @param totalRows   total number of rows.
     */
    public static int getLastRowIndex(int rowsPerPage, int pageNum, int totalRows) {
        if (pageNum == getTotalPages(rowsPerPage, totalRows))
            return totalRows % rowsPerPage;
        else
            return getFirstRowIndex(rowsPerPage, pageNum) + rowsPerPage;
    }

    /**
     * Get the total number of pages.
     *
     * @param rowsPerPage number of rows per page.
     * @param totalRows   total number of rows.
     */
    public static Integer getTotalPages(Integer rowsPerPage, Integer totalRows) {
        return new Integer(getTotalPages(rowsPerPage.intValue(), totalRows.intValue()));
    }

    /**
     * Get the total number of pages.
     *
     * @param rowsPerPage number of rows per page.
     * @param totalRows   total number of rows.
     */
    public static int getTotalPages(int rowsPerPage, int totalRows) {
        int totalPages = 1;
        totalRows--;

        if (totalRows > 0)
            totalPages = (totalRows / rowsPerPage) + 1;

        return totalPages;
    }
}
