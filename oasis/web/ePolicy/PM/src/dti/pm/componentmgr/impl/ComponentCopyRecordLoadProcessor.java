package dti.pm.componentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Record load processor for component copy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/07/2010       dzhang     108261 - Modified constructor method ComponentCopyRecordLoadProcessor() to add parameter compGridFields.
 * ---------------------------------------------------
 */
public class ComponentCopyRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        //iterate through the m_nameList and set indicator's default value to N
        for (int i = 0; i < getNameList().size(); i++) {
            String fieldName = (String) getNameList().get(i);
            if (!fieldName.equals("componentValue")) {
                record.setFieldValue(fieldName, YesNoFlag.N);
            }
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            recordSet.addFieldNameCollection(getNameList());
        }

        //set srcCompGridFields
        String[] srcCompGridFields = (String[])getNameList().toArray(new String[getNameList().size()]); 
        recordSet.getSummaryRecord().setFieldValue("srcCompGridFields",
            StringUtils.arrayToDelimited(srcCompGridFields,",", false, false));
    }

    public ComponentCopyRecordLoadProcessor(String compGridFields) {
         setNameList(compGridFields);
    }


    public List getNameList() {
        return m_nameList;
    }

    private void setNameList(String srcCompGridFields) {
        if (!StringUtils.isBlank(srcCompGridFields)) {
            m_nameList = new ArrayList(Arrays.asList(srcCompGridFields.split(",")));
        }
        else {
            m_nameList = new ArrayList();
        }
    }

    private List m_nameList;
}
