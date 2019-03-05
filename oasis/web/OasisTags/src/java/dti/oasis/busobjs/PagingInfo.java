package dti.oasis.busobjs;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * This class contains information about the paganation of a set of rows.
 * Namely, this class keeps track of the:
 * <ul>
 *  <li>current page number</li>
 *  <li>total number of rows</li>
 *  <li>number of rows per page</li>
 * </ul>
 * By default, the current page is 0, the total rows is 0, and the rows per page is Integer.MAX_VALUE.
 * Additional methods are provided to determine the first/last row on the current page and
 * if there is a next/previous page.
 *
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
public class PagingInfo implements Info {

    /**
     * Construct a default PagingInfo.
     */
    public PagingInfo() {
        Logger l = LogUtils.enterLog(getClass(), "PagingInfo");
        l.exiting(getClass().getName(), "PagingInfo");
    }

    /**
     * Construct a PagingInfo with the given page number, total rows, and rows per page.
     */
    public PagingInfo(int pageNum, int totalRows, int rowsPerPage) {
        Logger l = LogUtils.enterLog(getClass(), "PagingInfo", new Object[]{String.valueOf(pageNum), String.valueOf(totalRows), String.valueOf(rowsPerPage)});

        m_pageNum = new Integer(pageNum);
        m_totalRows = new Integer(totalRows);
        setRowsPerPage(new Integer(rowsPerPage));

        l.exiting(getClass().getName(), "PagingInfo");
    }

    /**
     * Construct a PagingInfo with the given page number, total rows, and rows per page.
     */
    public PagingInfo(Integer pageNum, Integer totalRows, Integer rowsPerPage) {
        Logger l = LogUtils.enterLog(getClass(), "PagingInfo", new Object[]{pageNum, totalRows, rowsPerPage});

        m_pageNum = pageNum;
        m_totalRows = totalRows;
        setRowsPerPage(rowsPerPage);

        l.exiting(getClass().getName(), "PagingInfo");
    }

    /**
     * Return true if there is a previous page (ie. we are not on the first page); otherwise, false.
     */
    public boolean hasPreviousPage() {
        Logger l = LogUtils.enterLog(getClass(), "hasPreviousPage");

        boolean hasPreviousPage = m_pageNum.intValue() != 1;

        l.exiting(getClass().getName(), "hasPreviousPage", String.valueOf(hasPreviousPage));
        return hasPreviousPage;
    }

    /**
     * Return the previous page number.
     *
     * @throws IllegalStateException if there is no previous page.
     */
    public Integer getPreviousPageNum() {
        Logger l = LogUtils.enterLog(getClass(), "getPreviousPageNum");

        if (!hasPreviousPage())
            throw new IllegalStateException("There is no previous page.");

        Integer previousPageNum = new Integer(m_pageNum.intValue() - 1);

        l.exiting(getClass().getName(), "getPreviousPageNum", previousPageNum);
        return previousPageNum;
    }

    /**
     * Return the current page number.
     */
    public Integer getPageNum() {
        Logger l = LogUtils.enterLog(getClass(), "getPageNum");
        l.exiting(getClass().getName(), "getPageNum", m_pageNum);
        return m_pageNum;
    }

    /**
     * Set the current page number.
     */
    public void setPageNum(Integer pageNum) {
        Logger l = LogUtils.enterLog(getClass(), "setPageNum", new Object[]{pageNum});

        m_pageNum = pageNum;

        l.exiting(getClass().getName(), "setPageNum");
    }

    /**
     * Return true if there is a next page (ie. we are not on the last page); otherwise, false.
     */
    public boolean hasNextPage() {
        Logger l = LogUtils.enterLog(getClass(), "hasNextPage");

        boolean hasNextPage = m_pageNum.intValue() != getTotalPages().intValue();

        l.exiting(getClass().getName(), "hasNextPage", String.valueOf(hasNextPage));
        return hasNextPage;
    }

    /**
     * Return the next page number.
     *
     * @throws IllegalStateException if there is no next page.
     */
    public Integer getNextPageNum() {
        Logger l = LogUtils.enterLog(getClass(), "getNextPageNum");

        if (!hasNextPage())
            throw new IllegalStateException("There is no next page.");

        Integer nextPageNum = new Integer(m_pageNum.intValue() + 1);

        l.exiting(getClass().getName(), "getNextPageNum", nextPageNum);
        return nextPageNum;
    }

    /**
     * Return the total number of pages.
     */
    public Integer getTotalPages() {
        Logger l = LogUtils.enterLog(getClass(), "getTotalPages");

        Integer totalPages = PagingSupport.getTotalPages(m_rowsPerPage, m_totalRows);

        l.exiting(getClass().getName(), "getTotalPages", totalPages);
        return totalPages;
    }

    /**
     * Return the number of rows per page.
     */
    public Integer getRowsPerPage() {
        Logger l = LogUtils.enterLog(getClass(), "getRowsPerPage");
        l.exiting(getClass().getName(), "getRowsPerPage", m_rowsPerPage);
        return m_rowsPerPage;
    }

    /**
     * Set the number of rows per page
     */
    public void setRowsPerPage(Integer rowsPerPage) {
        Logger l = LogUtils.enterLog(getClass(), "setRowsPerPage", new Object[]{rowsPerPage});

        if (rowsPerPage.intValue() > 0)
            this.m_rowsPerPage = rowsPerPage;

        l.exiting(getClass().getName(), "setRowsPerPage");
    }

    /**
     * Return the number of the first row on the current page.
     */
    public Integer getFirstRowOnPage() {
        Logger l = LogUtils.enterLog(getClass(), "getFirstRowOnPage");

        Integer firstRowIndex = PagingSupport.getFirstRowIndex(m_rowsPerPage, m_pageNum);

        l.exiting(getClass().getName(), "getFirstRowOnPage", firstRowIndex);
        return firstRowIndex;
    }

    /**
     * Return the number of the last row on the current page.
     */
    public Integer getLastRowOnPage() {
        Logger l = LogUtils.enterLog(getClass(), "getLastRowOnPage");

        Integer lastRowIndex = PagingSupport.getLastRowIndex(m_rowsPerPage, m_pageNum, m_totalRows);

        l.exiting(getClass().getName(), "getLastRowOnPage", lastRowIndex);
        return lastRowIndex;
    }

    /**
     * Return the total number of rows.
     */
    public Integer getTotalRows() {
        Logger l = LogUtils.enterLog(getClass(), "getTotalRows");
        l.exiting(getClass().getName(), "getTotalRows", m_totalRows);
        return m_totalRows;
    }

    /**
     * Set the total number of rows.
     */
    public void setTotalRows(Integer totalRows) {
        Logger l = LogUtils.enterLog(getClass(), "setTotalRows", new Object[]{totalRows});

        m_totalRows = totalRows;

        l.exiting(getClass().getName(), "setTotalRows");
    }

    public boolean equals(Object o) {
        if (null == o) return false;
        if (this == o) return true;
        if (!this.getClass().equals(o.getClass())) return false;


        final PagingInfo pagingInfo = (PagingInfo) o;

        if (!m_pageNum.equals(pagingInfo.m_pageNum)) return false;
        if (!m_totalRows.equals(pagingInfo.m_totalRows)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = m_pageNum.hashCode();
        result = 1000003 * result + m_totalRows.hashCode();
        return result;
    }

    public String toString() {
        return toString("; ");
    }

    public String toString(String sep) {
        return new StringBuffer("dti.oasis.busobjs.PagingInfo{")
            .append("m_pageNum=").append(m_pageNum)
            .append(sep).append("TotalPages=").append(getTotalPages())
            .append(sep).append("m_rowsPerPage=").append(m_rowsPerPage)
            .append(sep).append("m_totalRows=").append(m_totalRows)
            .append(sep).append("FirstRowOnPage=").append(getFirstRowOnPage())
            .append(sep).append("LastRowOnPage=").append(getLastRowOnPage())
            .append("}").append(super.toString()).toString();
    }

    /**
     * Reset the paging info to the default values.
     */
    public void clear() {
        m_pageNum = c_defaultPageNum;
        m_totalRows = c_defaultTotalRows;
        m_rowsPerPage = c_defaultRowsPerPage;
    }

    // Page information
    protected Integer m_pageNum = c_defaultPageNum;
    protected Integer m_totalRows = c_defaultTotalRows;
    protected Integer m_rowsPerPage = c_defaultRowsPerPage;

    private static Integer c_defaultPageNum = new Integer(0);
    private static Integer c_defaultTotalRows = new Integer(0);
    private static Integer c_defaultRowsPerPage = new Integer(Integer.MAX_VALUE);
}
