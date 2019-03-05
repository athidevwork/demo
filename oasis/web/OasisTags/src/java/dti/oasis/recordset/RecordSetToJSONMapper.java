package dti.oasis.recordset;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.json.JsonHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Mapper;
import dti.oasis.util.StringUtils;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/28/2016
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/13/2018       wreeder     196147 - updated to use the JsonHelper to write JSON data
 * ---------------------------------------------------
 */
public class RecordSetToJSONMapper implements Mapper {

    public static final String QUOTE_WRAPPER = "\"";

    private final Logger l = LogUtils.getLogger(getClass());
    private static final RecordSetToJSONMapper singleInstance = new RecordSetToJSONMapper();

    private RecordSetToJSONMapper() {

    }

    /**
     * get an instance
     * @return RecordSetToJSONMapper
     */
    public static RecordSetToJSONMapper getInstance() {
        return singleInstance;
    }

    /**
     * map record set data to json object
     *
     * @param recordSet
     * @param out
     * @param keepCase
     */
    public void mapRecordSetToJSON(RecordSet recordSet, PrintWriter out, boolean keepCase) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapRecordSetToJSON", new Object[]{recordSet, out, keepCase});
        }

        try {
            //start writing: "data":[{record1},{record2}]
            JsonHelper.writePropertyName(out,"data", true);
            JsonHelper.addArrayStartTag(out, false);
            Iterator<Record> records = recordSet.getRecords();
            while (records.hasNext()) {
                Record record = records.next();
                JsonHelper.addObjectStartTag(out, false);

                // Loop record all columns
                Iterator<Field> fields = record.getFields();
                while (fields.hasNext()) {
                    Field field = fields.next();
                    //write: "key":"value"
                    if (!StringUtils.isBlank(field.getName())) {
                        String name = keepCase ? field.getName().trim() : field.getName().trim().toUpperCase();
                        String value = field.getStringValue((String) null);
                        JsonHelper.writeProperty(out, name, value, fields.hasNext(),false);
                    }
                } //end loop all record fields
                JsonHelper.addCommaSeparator(out, false);

                // additional fields: write the UPDATE_IND, DISPLAY_IND and EDIT_IND attributes separately
                JsonHelper.writeProperty(out, "UPDATE_IND", record.getUpdateIndicator(), true, false);
                JsonHelper.writeProperty(out, "DISPLAY_IND", record.getDisplayIndicator(), true, false);
                JsonHelper.writeProperty(out, "EDIT_IND", record.getEditIndicator(), false, false);

                JsonHelper.addObjectEndTag(out, records.hasNext(), false, false);
            } // End looping through all records
            JsonHelper.addArrayEndTag(out, false, false);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate the json: "
                    + recordSet, e);
            l.throwing(getClass().getName(), "mapRecordSetToJSON", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "mapRecordSetToJSON");
    }
}
