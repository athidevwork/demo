package dti.oasis.jsf;

import java.lang.reflect.Field;

/**
 * A default implementation that matches all fields and records.
 *
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
public class DefaultDataTableModelFilterProcessor implements DataTableModelFilterProcessor {
    @Override
    public boolean postProcessMatchField(Field field, Object filterValue, Object recordValue) {
        // Match all fields by default
        return true;
    }

    @Override
    public boolean postProcessMatchRecord(Object record) {
        // Match all records by default
        return true;
    }
}
