package dti.pm.coveragemgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Loadprocessor for coverage copy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 21, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/05/2010       syang       107018 - Retrieves rateModuleCode from record and set the value to the varible rateModuleCode. 
 * 08/17/2010       fcb         97217  - CoverageCopyRecordLoadProcessor: limitErosionB added.
 * 09/07/2010       dzhang      108261 - Modified constructor method CoverageCopyRecordLoadProcessor() to add parameter covgGridFields.
 * 01/17/2012       wfu         125059 - Modified postProcessRecord to set value for field srcCoverageCode.
 * ---------------------------------------------------
 */
public class CoverageCopyRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        //iterate through the m_nameList and set indicator's default value to N
        for (int i =0; i< getNameList().size();i++){
            record.setFieldValue((String)getNameList().get(i), YesNoFlag.N);
        }
        record.setFieldValue("isRetroAvailable", YesNoFlag.N);
        String policyFormCode = CoverageFields.getPolicyFormCode(record);
        if ("CM".equals(policyFormCode)) {
            record.setFieldValue("isRetroAvailable", YesNoFlag.Y);
        }
        record.setFieldValue("isAnnualRateAvailable", YesNoFlag.N);
        String rateModuleCode = CoverageFields.getRatingModuleCode(record);
        if (getCoverageManager().isManuallyRated(rateModuleCode)) {
            record.setFieldValue("isAnnualRateAvailable", YesNoFlag.Y);
        }

        //Set value for source coverage code using coverage code for field dependency.
        CoverageFields.setSrcCoverageCode(record, CoverageFields.getCoverageCode(record));

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            getNameList().add("isRetroAvailable");
            getNameList().add("isAnnualRateAvailable");
            recordSet.addFieldNameCollection(getNameList());
        }
        //set srcCovgGridFields
        String[] srcCovgGridFields = (String[])getNameList().toArray(new String[getNameList().size()]);
        recordSet.getSummaryRecord().setFieldValue("srcCovgGridFields",
            StringUtils.arrayToDelimited(srcCovgGridFields,",", false, false));
    }
    public CoverageCopyRecordLoadProcessor(CoverageManager coverageManager, String covgGridFields) {
        this.setCoverageManager(coverageManager);
        setNameList(covgGridFields);
    }


    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }


    public List getNameList() {
        return m_nameList;
    }

    private void setNameList(String srcCovgGridFields) {
        if (!StringUtils.isBlank(srcCovgGridFields)) {
            m_nameList = new ArrayList(Arrays.asList(srcCovgGridFields.split(",")));
        }
        else {
            m_nameList = new ArrayList();
        }
    }

    private List m_nameList;
    private CoverageManager m_coverageManager;
}
