package dti.oasis.recordset;

import dti.oasis.test.TestCase;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 26, 2007
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
public class RecordTestCase extends TestCase {
    public RecordTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testGetFieldValue() {
        Record rec = new Record();

        String fieldName = "field1";
        String value = "value1";
        rec.setFieldValue(fieldName, value);
        assertEquals(value, rec.getFieldValue(fieldName));
    }

    public void testMultiValueField() {

        Record rec = new Record();

        String fieldName = "field1";
        MultiValueField mvField = new MultiValueField();
        mvField.addValue("1");
        mvField.addValue("2");
        mvField.addValue("3");

        rec.setField(fieldName, mvField);

        assertEquals("1,2,3", rec.getFieldValue(fieldName));
        assertEquals("1,2,3", rec.getStringValue(fieldName));
        assertEquals("2", mvField.getValue(1));
    }
}
