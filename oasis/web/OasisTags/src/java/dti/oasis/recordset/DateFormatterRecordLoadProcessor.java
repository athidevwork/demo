package dti.oasis.recordset;

import java.text.Format;
import java.util.Date;

/**
 * If the specified date field exists, format it and replace the field with the new formated date string
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
public class DateFormatterRecordLoadProcessor extends DefaultRecordLoadProcessor {

    public DateFormatterRecordLoadProcessor(String fieldName, Format format) {
        if (fieldName == null)
            throw new IllegalArgumentException("Missing the required fieldName argument.");

        if (format == null)
            throw new IllegalArgumentException("Missing the required format argument.");

        m_fieldName = fieldName;
        m_format = format;
    }

    /**
     * If the specified date field exists, format it and replace the field with the new formated date string
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Date dateValue = null;
        if (record.hasField(m_fieldName) && (dateValue = record.getDateValue(m_fieldName)) != null) {
            record.setField(m_fieldName, new Field(m_format.format(dateValue)));
        }
        return true;
    }

    private String m_fieldName;
    private Format m_format;
}
