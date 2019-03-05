package dti.oasis.recordset;

import dti.oasis.test.TestCase;
import dti.oasis.busobjs.UpdateIndicator;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
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
public class RecordSetTestCase extends TestCase {

    public RecordSetTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testAddDeleteField() {
        RecordSet rs = new RecordSet();

        Record record = new Record();
        for (int i = 0; i < 5; i++) {
            record.setFieldValue("field" + i, String.valueOf(i));
        }
        rs.addRecord(record);

//        System.out.println("RecordSet fields before adding a field: " + rs.getFieldNameList());
//        System.out.println("Record    fields before adding a field: " + record.getFieldNameList());

        record.setFieldValue("fieldNew", "new");

//        System.out.println("RecordSet fields after adding a new field: " + rs.getFieldNameList());
//        System.out.println("Record    fields after adding a new field: " + record.getFieldNameList());

        record.remove("Field3");

//        System.out.println("RecordSet fields after removing field3: " + rs.getFieldNameList());
//        System.out.println("Record    fields after removing field3: " + record.getFieldNameList());

//        System.out.println("Record:    " + record);

        assertEquals("Field names counts are not in sync.", rs.getFieldCount(), record.getFieldCount());
        assertEquals("Field names are not in sync.", rs.getFieldNameList().get(rs.getFieldCount()-1), record.getFieldNameList().get(record.getFieldCount()-1));

    }

    public void testUpdateIndicatorRecordFilter() {
        RecordSet rs = new RecordSet();
        for (int i = 0; i < 12; i++) {
            Record record = new Record();
            if (i%4 == 0) {
                record.setUpdateIndicator(UpdateIndicator.NOT_CHANGED);
            }
            else if (i%4 == 1) {
                record.setUpdateIndicator(UpdateIndicator.DELETED);
            }
            else if (i%4 == 2) {
                record.setUpdateIndicator(UpdateIndicator.INSERTED);
            }
            else if (i%4 == 3) {
                record.setUpdateIndicator(UpdateIndicator.UPDATED);
            }
            rs.addRecord(record);
//            System.out.println("record.getUpdateIndicator() = " + record.getUpdateIndicator());
        }
        RecordSet notChangedRecords = rs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.NOT_CHANGED));
        assertEquals("Failed to find all Records with UpdateIndicator set to NOT_CHANGED by default", 3, notChangedRecords.getSize());

        RecordSet changedRecords = rs.getSubSet(
            new UpdateIndicatorRecordFilter(
                new String[] {UpdateIndicator.DELETED,
                              UpdateIndicator.INSERTED,
                              UpdateIndicator.UPDATED}));
        assertEquals("Failed to find all Records with UpdateIndicator set to DELETED", 9, changedRecords.getSize());

    }

    public void testRecordFilter() {
        RecordSet rs = new RecordSet();
        for (int i = 0; i < 12; i++) {
            Record record = new Record();
            record.setFieldValue("testField", String.valueOf(i%4));
            rs.addRecord(record);
//            System.out.println("record = " + record);
        }
        RecordSet zeroValueRecords = rs.getSubSet(new RecordFilter("testField", "0"));
        assertEquals("Failed to find all Records with value 0", 3, zeroValueRecords.getSize());

        RecordSet nonZeroValueRecords = rs.getSubSet(
            new RecordFilter("testField", new Object[] {"1", "2", "3"}));
        assertEquals("Failed to find all Records with value 0", 9, nonZeroValueRecords.getSize());

        nonZeroValueRecords.setFieldValueOnAll("newField", "value");

        assertTrue("The new field was not added to the subset's field name collection.", rs.getFieldNameList().contains("testField"));
        assertFalse("Adding a new field to the subset has incorrectly caused the source recordset's field name collection to be updated",
            rs.getFieldNameList().contains("newField"));
    }

    public void testMatchNotNull() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();

        // Test with matchNotNull = true
        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", null);
        test.addRecord(two);
        three.setFieldValue("firstCol", "");
        test.addRecord(three);

        RecordFilter notNullRecFilter = new RecordFilter("firstCol", true);
        RecordSet notNullRecords = test.getSubSet(notNullRecFilter);

        assertEquals(1, notNullRecords.getSize());
        assertEquals("A", notNullRecords.getFirstRecord().getFieldValue("firstCol"));

        // Test with matchNotNull = false
        RecordFilter nullRecFilter = new RecordFilter("firstCol", false);
        RecordSet nullRecords = test.getSubSet(nullRecFilter);

        assertEquals(2, nullRecords.getSize());
        assertEquals(null, nullRecords.getRecord(0).getFieldValue("firstCol"));
        assertEquals("", nullRecords.getRecord(1).getFieldValue("firstCol"));
    }

    public void testRecordNumber() {
        RecordSet rs = new RecordSet();
        for (int i = 0; i < 8; i++) {
            Record record = new Record();
            record.setFieldValue("testField", String.valueOf(i));
            record.setFieldValue("testField2", String.valueOf(i%4));
            rs.addRecord(record);
            //System.out.println("record = " + record);
        }

        for (int i = 0; i < 8; i++) {
            Record record = rs.getRecord(i);
            assertEquals("Record number is incorrect", record.getStringValue("testField"), String.valueOf(record.getRecordNumber()));
        }

        RecordSet zeroValueRecords = rs.getSubSet(new RecordFilter("testField2", "0"));
        for (int i = 0; i < zeroValueRecords.getSize(); i++) {
            Record record = zeroValueRecords.getRecord(i);
            assertEquals("Record number is incorrect for subset", record.getStringValue("testField"), String.valueOf(record.getRecordNumber()));
        }
    }
}
