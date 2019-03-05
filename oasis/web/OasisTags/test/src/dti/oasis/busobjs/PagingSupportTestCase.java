package dti.oasis.busobjs;

import dti.oasis.test.TestCase;

/**
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
public class PagingSupportTestCase extends TestCase {

    public PagingSupportTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testGetFirstRowIndex() {
        int expectedRowIndex = 1, rowsPerPage = 15, pageNum = 1;
        assertEquals(expectedRowIndex, PagingSupport.getFirstRowIndex(rowsPerPage, pageNum));

        expectedRowIndex = 16; rowsPerPage = 15; pageNum = 2;
        assertEquals(expectedRowIndex, PagingSupport.getFirstRowIndex(rowsPerPage, pageNum));

        expectedRowIndex = 30000; rowsPerPage = 15; pageNum = 2000;
        assertEquals(expectedRowIndex, PagingSupport.getFirstRowIndex(rowsPerPage, pageNum));
    }

    public void testGetPageNumber() {
        int expectedPage = 1, rowsPerPage = 15, rowIndex = 1;
        assertEquals(expectedPage, PagingSupport.getPageNumber(rowsPerPage, rowIndex));

        expectedPage = 1; rowsPerPage = 15; rowIndex = 0;
        try {
            PagingSupport.getPageNumber(rowsPerPage, rowIndex);
            fail("Row index should never be 0.");
        } catch (IndexOutOfBoundsException e) {
          // expected
        }

        expectedPage = 1; rowsPerPage = 15; rowIndex = 15;
        assertEquals(expectedPage, PagingSupport.getPageNumber(rowsPerPage, rowIndex));

        expectedPage = 2; rowsPerPage = 15; rowIndex = 16;
        assertEquals(expectedPage, PagingSupport.getPageNumber(rowsPerPage, rowIndex));

        expectedPage = 2000; rowsPerPage = 15; rowIndex = 30000;
        assertEquals(expectedPage, PagingSupport.getPageNumber(rowsPerPage, rowIndex));
    }

    public void testGetLastRowIndex() {
        int expectedRowIndex = 15, rowsPerPage = 15, pageNum = 1, totalRows = 30000;
        assertEquals(expectedRowIndex, PagingSupport.getLastRowIndex(rowsPerPage, pageNum, totalRows));

        expectedRowIndex = 30; rowsPerPage = 15; pageNum = 2;
        assertEquals(expectedRowIndex, PagingSupport.getLastRowIndex(rowsPerPage, pageNum, totalRows));

        expectedRowIndex = 30000; rowsPerPage = 15; pageNum = 2000;
        assertEquals(expectedRowIndex, PagingSupport.getLastRowIndex(rowsPerPage, pageNum, totalRows));

        expectedRowIndex = 0; rowsPerPage = 15; pageNum = 1; totalRows = 0;
        assertEquals(expectedRowIndex, PagingSupport.getLastRowIndex(rowsPerPage, pageNum, totalRows));
    }

    public void testGetTotalPages() {
        int expectedTotalPages = 1, rowsPerPage = 15, totalRows = 0;
        assertEquals(expectedTotalPages, PagingSupport.getTotalPages(rowsPerPage, totalRows));

        expectedTotalPages = 1; rowsPerPage = 15; totalRows = 15;
        assertEquals(expectedTotalPages, PagingSupport.getTotalPages(rowsPerPage, totalRows));

        expectedTotalPages = 2000; rowsPerPage = 15; totalRows = 30000;
        assertEquals(expectedTotalPages, PagingSupport.getTotalPages(rowsPerPage, totalRows));
    }
}
