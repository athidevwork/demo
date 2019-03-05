package dti.oasis.recordset;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Write the RecordSet to the output stream as text.
 * Unless otherwise specified, the column headers are written on the first line, using the provided column delimiter.
 * Next, for each record, the value of each field is written, using the provided column delimiter.
 * If no column delimiter is provided, columns are tab delimited.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 19, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/12/2018       wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
 * ---------------------------------------------------
 */
public class RecordSetToTXTLoadProcessor extends DefaultRecordLoadProcessor {

    public RecordSetToTXTLoadProcessor(OutputStream ostream) {
        setOutputStream(ostream);
    }

    public RecordSetToTXTLoadProcessor(OutputStream ostream, boolean writeColumnHeaders) {
        setOutputStream(ostream);
        m_writeColumnHeaders = writeColumnHeaders;
    }

    public RecordSetToTXTLoadProcessor(OutputStream ostream, boolean writeColumnHeaders, String columnDelimiter) {
        setOutputStream(ostream);
        m_writeColumnHeaders = writeColumnHeaders;
        m_columnDelimiter = columnDelimiter;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if (m_recordCount == 0 && m_writeColumnHeaders) {
            writeColumnHeaders(record);
        }

        writeRecord(record);

        m_recordCount++;

        return true;
    }

    public void setOutputStream(OutputStream ostream) {
        if (ostream instanceof PrintStream) {
            m_out = (PrintStream) ostream;
        } else {
            m_out = new PrintStream(ostream);
        }
    }


    private void writeColumnHeaders(Record record) {
        Iterator iter = record.getFields();
        String sep = "";
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            m_out.print(field.getName());
            m_out.print(sep);
            sep = m_columnDelimiter;
        }
        m_out.println();
    }

    private void writeRecord(Record record) {
        Iterator iter = record.getFields();
        String sep = "";
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            m_out.print(field.getStringValue());
            m_out.print(sep);
            sep = m_columnDelimiter;
        }
        m_out.println();
    }


    private boolean m_writeColumnHeaders = true;
    private String m_columnDelimiter = "\t";
    private int m_recordCount = 0;
    private PrintStream m_out;
}
