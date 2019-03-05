package dti.oasis.recordset;

import dti.oasis.test.TestCase;
import dti.oasis.util.DateUtils;
import dti.oasis.converter.DateConverter;
import dti.oasis.converter.ConverterFactory;

import java.util.Iterator;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 3, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class RecordComparatorTestCase extends TestCase {

    public RecordComparatorTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", "V");
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol");
        RecordSet sortedTest = test.getSortedCopy(rc);
        assertEquals("A", sortedTest.getRecord(0).getStringValue("firstCol"));
        assertEquals("L", sortedTest.getRecord(1).getStringValue("firstCol"));
        assertEquals("V", sortedTest.getRecord(2).getStringValue("firstCol"));
        assertEquals("Z", sortedTest.getRecord(3).getStringValue("firstCol"));

        // Validate the original RecordSet remains unchanged
        assertEquals("Z", test.getRecord(1).getStringValue("firstCol"));
    }

    public void testMultipleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        one.setFieldValue("secondCol", "E");
        test.addRecord(one);
        two.setFieldValue("firstCol", "B");
        two.setFieldValue("secondCol", "E");
        test.addRecord(two);
        three.setFieldValue("firstCol", "A");
        three.setFieldValue("secondCol", "C");
        test.addRecord(three);
        four.setFieldValue("firstCol", "B");
        four.setFieldValue("secondCol", "D");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol");
        rc.addFieldComparator("secondCol");
        test = test.getSortedCopy(rc);
        assertEquals("A", test.getRecord(0).getStringValue("firstCol"));
        assertEquals("C", test.getRecord(0).getStringValue("secondCol"));

        assertEquals("A", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("E", test.getRecord(1).getStringValue("secondCol"));

        assertEquals("B", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("D", test.getRecord(2).getStringValue("secondCol"));

        assertEquals("B", test.getRecord(3).getStringValue("firstCol"));
        assertEquals("E", test.getRecord(3).getStringValue("secondCol"));
    }

    public void testDescSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", "V");
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", true, SortOrder.DESC, null);
        test = test.getSortedCopy(rc);
        assertEquals("Z", test.getRecord(0).getStringValue("firstCol"));
        assertEquals("V", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("L", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("A", test.getRecord(3).getStringValue("firstCol"));
    }

    public void testNullGreaterAscSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", null);
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol");
        test = test.getSortedCopy(rc);
        assertEquals("A", test.getRecord(0).getStringValue("firstCol"));
        assertEquals("L", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("Z", test.getRecord(2).getStringValue("firstCol"));
        assertNull(test.getRecord(3).getStringValue("firstCol"));
    }

    public void testNullGreaterDescSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", null);
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", SortOrder.DESC);
        test = test.getSortedCopy(rc);
        assertNull(test.getRecord(0).getStringValue("firstCol"));
        assertEquals("Z", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("L", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("A", test.getRecord(3).getStringValue("firstCol"));
    }

    public void testNullLesserAscSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", null);
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", false, SortOrder.ASC, null);
        test = test.getSortedCopy(rc);
        assertNull(test.getRecord(0).getStringValue("firstCol"));
        assertEquals("A", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("L", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("Z", test.getRecord(3).getStringValue("firstCol"));
    }

    public void testNullLesserDescSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "A");
        test.addRecord(one);
        two.setFieldValue("firstCol", "Z");
        test.addRecord(two);
        three.setFieldValue("firstCol", null);
        test.addRecord(three);
        four.setFieldValue("firstCol", "L");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", false, SortOrder.DESC, null);
        test = test.getSortedCopy(rc);
        assertEquals("Z", test.getRecord(0).getStringValue("firstCol"));
        assertEquals("L", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("A", test.getRecord(2).getStringValue("firstCol"));
        assertNull(test.getRecord(3).getStringValue("firstCol"));
    }

    public void testDateSingleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", DateUtils.parseDate("05/01/2006"));
        test.addRecord(one);
        two.setFieldValue("firstCol", DateUtils.parseDate("04/01/2002"));
        test.addRecord(two);
        three.setFieldValue("firstCol", DateUtils.parseDate("04/01/2001"));
        test.addRecord(three);
        four.setFieldValue("firstCol", DateUtils.parseDate("01/01/1999"));
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", SortOrder.ASC);
        test = test.getSortedCopy(rc);
        assertEquals("04/01/2001", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("04/01/2002", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("05/01/2006", test.getRecord(3).getStringValue("firstCol"));
        assertEquals("01/01/1999", test.getRecord(0).getStringValue("firstCol"));
    }

    public void testDateMultipleFieldCompare() {
        RecordSet test = new RecordSet();
        Record one = new Record();
        Record two = new Record();
        Record three = new Record();
        Record four = new Record();

        one.setFieldValue("firstCol", "07/01/2006");
        one.setFieldValue("secondCol", "07/01/2007");
        test.addRecord(one);
        two.setFieldValue("firstCol", "07/01/2006");
        two.setFieldValue("secondCol", "10/01/2006");
        test.addRecord(two);
        three.setFieldValue("firstCol", "10/01/2006");
        three.setFieldValue("secondCol", "07/01/2007");
        test.addRecord(three);
        four.setFieldValue("firstCol", "10/01/2006");
        four.setFieldValue("secondCol", "05/01/2007");
        test.addRecord(four);

        RecordComparator rc = new RecordComparator("firstCol", SortOrder.ASC, ConverterFactory.getInstance().getConverter(Date.class));
        rc.addFieldComparator("secondCol", SortOrder.ASC, ConverterFactory.getInstance().getConverter(Date.class));
        test = test.getSortedCopy(rc);
        assertEquals("07/01/2006", test.getRecord(0).getStringValue("firstCol"));
        assertEquals("10/01/2006", test.getRecord(0).getStringValue("secondCol"));
        assertEquals("07/01/2006", test.getRecord(1).getStringValue("firstCol"));
        assertEquals("07/01/2007", test.getRecord(1).getStringValue("secondCol"));
        assertEquals("10/01/2006", test.getRecord(2).getStringValue("firstCol"));
        assertEquals("05/01/2007", test.getRecord(2).getStringValue("secondCol"));
        assertEquals("10/01/2006", test.getRecord(3).getStringValue("firstCol"));
        assertEquals("07/01/2007", test.getRecord(3).getStringValue("secondCol"));
    }
}
