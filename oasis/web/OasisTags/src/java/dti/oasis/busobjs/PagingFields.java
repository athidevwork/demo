package dti.oasis.busobjs;

import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;

/**
 * This class contains helper methods to access fields of a Record with Paging Info fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 19, 2006
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
public class PagingFields {

    public static final String PAGE_NUM = "pageNum";
    public static final String TOTAL_ROWS = "totalRows";
    public static final String ROWS_PER_PAGE = "rowsPerPage";

    public static boolean hasPageNum(Record record) {
        return record.hasFieldValue(PAGE_NUM);
    }

    public static int getPageNum(Record record) {
        return record.getIntegerValue(PAGE_NUM).intValue();
    }

    public static void setPageNum(Record record, int pageNum) {
        record.setField(PAGE_NUM, new Field(new Integer(pageNum)));
    }

    public static boolean hasTotalRows(Record record) {
        return record.hasFieldValue(TOTAL_ROWS);
    }

    public static int getTotalRows(Record record) {
        return record.getIntegerValue(TOTAL_ROWS).intValue();
    }

    public static void setTotalRows(Record record, int totalRows) {
        record.setField(TOTAL_ROWS, new Field(new Integer(totalRows)));
    }

    public static boolean hasRowsPerPage(Record record) {
        return record.hasFieldValue(ROWS_PER_PAGE);
    }

    public static int getRowsPerPage(Record record) {
        return record.getIntegerValue(ROWS_PER_PAGE).intValue();
    }

    public static void setRowsPerPage(Record record, int rowsPerPage) {
        record.setField(ROWS_PER_PAGE, new Field(new Integer(rowsPerPage)));
    }
}
