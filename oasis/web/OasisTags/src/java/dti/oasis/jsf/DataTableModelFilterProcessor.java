package dti.oasis.jsf;

import java.lang.reflect.Field;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/4/13
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
public interface DataTableModelFilterProcessor {

    /**
     * The DataTableModel.filterData() method invokes this method after matching each field in each record,
     * but only if the record's field value matches the corresponding value in the DataTableModel's filter object.
     * @param field
     * @param filterValue
     * @param recordValue
     * @return
     */
    boolean postProcessMatchField(Field field, Object filterValue, Object recordValue);

    /**
     * The DataTableModel.filterData() method invokes this method after matching each record,
     * but only if all the fields in the record match the DataTableModel's filter object.
     * @param record
     * @return
     */
    boolean postProcessMatchRecord(Object record);
}
