package dti.ci.contactmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/20/12
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/02/2012       kshen       Issue 133142.
 * ---------------------------------------------------
 */
public class ContactListLoadProcessor implements RecordLoadProcessor {
    public ContactListLoadProcessor() {
        this.addSelectIndLoadProcessor = AddSelectIndLoadProcessor.getInstance();
    }


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        this.addSelectIndLoadProcessor.postProcessRecord(record, rowIsOnCurrentPage);

        String firstName = record.getStringValue("firstName", "");
        String middleName = record.getStringValue("middleName", "");
        String lastName = record.getStringValue("lastName", "");

        StringBuilder nameComputedSb = new StringBuilder();

        if(!StringUtils.isBlank(firstName))
            nameComputedSb.append(firstName).append(" ");
        if(!StringUtils.isBlank(middleName))
            nameComputedSb.append(middleName).append(" ");
        if(!StringUtils.isBlank(lastName))
            nameComputedSb.append(lastName);

        record.setFieldValue("nameComputed", nameComputedSb.toString());

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        this.addSelectIndLoadProcessor.postProcessRecordSet(recordSet);

        if (recordSet.getSize() == 0) {
            List<String> fieldNameList = new ArrayList<String>();
            fieldNameList.add("nameComputed");
            recordSet.addFieldNameCollection(fieldNameList);
        }
    }

    private RecordLoadProcessor addSelectIndLoadProcessor;
}
