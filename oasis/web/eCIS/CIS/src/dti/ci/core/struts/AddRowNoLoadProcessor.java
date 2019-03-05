package dti.ci.core.struts;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Feb 16, 2011
 */
/*
 * This class adds "rowNo" field to record set.
 * The purpose of this class is to display one more column "rowNo" for the grid.
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddRowNoLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        if (recordSet.getSize() > 0) {
            // check if the page has multiple grids
            if (getIsMultiGrid().booleanValue()) {
                String parentKey = getParentKey();
                if (!StringUtils.isBlank(parentKey)) {
                    ArrayList keyValueList = new ArrayList();
                    Iterator recIter = recordSet.getRecords();
                    while (recIter.hasNext()) {
                        Record r = (Record) recIter.next();
                        String keyValue = r.getStringValue(parentKey);
                        if (!keyValueList.contains(keyValue)) {
                            keyValueList.add(keyValue);
                            RecordSet subRs = recordSet.getSubSet(new RecordFilter(parentKey, keyValue));
                            addRowNo(subRs);
                        }
                    }
                    ArrayList fields = new ArrayList();
                    fields.add(ROW_NUMBER);
                    recordSet.addFieldNameCollection(fields);
                }
            } else {
                addRowNo(recordSet);
            }
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public static void setRowNoForRecordSet(RecordSet rs) {
        AddRowNoLoadProcessor loadProcessor = new AddRowNoLoadProcessor();
        loadProcessor.postProcessRecordSet(rs);
    }

    public YesNoFlag getIsMultiGrid() {
        return m_isMultiGrid;
    }

    public void setIsMultiGrid(YesNoFlag isMultiGrid) {
        this.m_isMultiGrid = isMultiGrid;
    }

    public String getParentKey() {
        return m_parentKey;
    }

    public void setParentKey(String parentKey) {
        this.m_parentKey = parentKey;
    }

    private void addRowNo(RecordSet rs) {
        Iterator recIter = rs.getRecords();
        int k = 0;
        while (recIter.hasNext()) {
            Record r = (Record) recIter.next();
            r.setFieldValue(ROW_NUMBER, String.valueOf(k + 1));
            k++;
        }
    }

    public AddRowNoLoadProcessor() {
        this(YesNoFlag.N, null);
    }

    public AddRowNoLoadProcessor(YesNoFlag isMultiGrid, String parentKey) {
        setIsMultiGrid(isMultiGrid);
        setParentKey(parentKey);
    }

    public static final String ROW_NUMBER = "rowNo";
    private YesNoFlag m_isMultiGrid;
    private String m_parentKey;
}
