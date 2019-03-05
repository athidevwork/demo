package dti.pm.coverageclassmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coverageclassmgr.CoverageClassFields;

import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

/**
 * Record Load Processor for coverage class copy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/07/2010       dzhang      108261 - Modified constructor method CoverageClassCopyRecordLoadProcessor() to add parameter covgClassGridFields.
 * ---------------------------------------------------
 */
public class CoverageClassCopyRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        String covBseId = CoverageClassFields.getParentCoverageBaseRecordId(record);
        Record ownerRecord = (Record) getOwnerRecordMap().get(covBseId);

        if (ownerRecord != null) {
            record.setFieldValue("parentCoverageDesc", CoverageFields.getProductCoverageDesc(ownerRecord));
            record.setFieldValue("parentCoverageId", CoverageFields.getCoverageId(ownerRecord));
        }
        else {
            return false;
        }

        //iterate through the m_nameList and set indicator's default value to N
        for (int i = 0; i < getNameList().size(); i++) {
            String fieldName = (String) getNameList().get(i);
            if (!fieldName.equals("exposureUnit")) {
                record.setFieldValue((String) getNameList().get(i), YesNoFlag.N);
            }
        }
        record.setFieldValue("isSubAnnualRateAvailable", YesNoFlag.N);
        String ratingModuleCode = CoverageClassFields.getRatingModuleCode(record);
        if (ratingModuleCode.substring(0, 1).equals("M")) {
            record.setFieldValue("isSubAnnualRateAvailable", YesNoFlag.Y);
        }
        return true;
    }

    private Map getOwnerRecordMap() {
        return m_ownerRecordMap;
    }

    private void setOwnerRecordMap(Map ownerRecordMap) {
        m_ownerRecordMap = ownerRecordMap;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            getNameList().add("parentCoverageDesc");
            getNameList().add("parentCoverageId");
            getNameList().add("isSubAnnualRateAvailable");
            recordSet.addFieldNameCollection(getNameList());
        }
        //set srcCovgClassGridFields
        String[] srcCovgClassGridFields = (String[])getNameList().toArray(new String[getNameList().size()]); 
        recordSet.getSummaryRecord().setFieldValue("srcCovgClassGridFields",
            StringUtils.arrayToDelimited(srcCovgClassGridFields,",", false, false));
       
    }


    public CoverageClassCopyRecordLoadProcessor(RecordSet ownerRecords, String covgClassGridFields) {
        setOwnerRecordMap(new HashMap());
        Iterator ownerRecordsIter = ownerRecords.getRecords();
        while (ownerRecordsIter.hasNext()) {
            Record ownerRecord = (Record) ownerRecordsIter.next();
            m_ownerRecordMap.put(CoverageFields.getCoverageBaseRecordId(ownerRecord), ownerRecord);
        }
        setNameList(covgClassGridFields);
    }


    public List getNameList() {
        return m_nameList;
    }

    private void setNameList(String srcCovgClassGridFields) {
        if (!StringUtils.isBlank(srcCovgClassGridFields)) {
            m_nameList = new ArrayList(Arrays.asList(srcCovgClassGridFields.split(",")));
        }
        else {
            m_nameList = new ArrayList();
        }
    }

    private List m_nameList;
    private Map m_ownerRecordMap;
}
