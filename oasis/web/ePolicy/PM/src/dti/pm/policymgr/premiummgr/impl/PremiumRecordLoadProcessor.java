package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.policymgr.premiummgr.PremiumFields;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class extends the default record load processor .Data dupication is processed.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 15, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/10/2011       dzhang      113567 - Added isPremiumTypeEditable() to set the editIndicator for premium type.
 * 08/01/2011       ryzhao      118806 - Do refactoring to move PremiumFields to dti.pm.policymgr.premiummgr package.
 * 10/11/2011       fcb         125838 - Changes due to move of filtering of data from JS to DB
 * 08/23/2012       tcheng      136685 - Modified postProcessRecord to set componentCode as "" when componentCode is null.
 * ---------------------------------------------------
 */
public class PremiumRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if (!record.hasStringValue("componentCode")
            || record.getStringValue("componentCode").equalsIgnoreCase("pmfm")) {
            record.setFieldValue("componentCode", "");
        }

        //add for page entitlement for row
        String coverageId = record.getStringValue("coverageId");
        String multiLayer = record.getStringValue("multiLayer");
        if ((!StringUtils.isBlank(coverageId)) && (coverageId.trim().length() < 15) && (multiLayer.equals("Y"))) {
            record.setFieldValue(PremiumFields.IS_ROW_ELIGIBLE_FOR_VIEW_LAYER, YesNoFlag.getInstance("Y"));
        }
        else {
            record.setFieldValue(PremiumFields.IS_ROW_ELIGIBLE_FOR_VIEW_LAYER, YesNoFlag.getInstance("N"));
        }

        String riskId = record.getStringValue("riskId");
        if ((StringUtils.isBlank(riskId)) || (riskId.equals("-1"))) {
            record.setFieldValue(PremiumFields.IS_ROW_ELIGIBLE_FOR_VIEW_MEM_CONT, YesNoFlag.getInstance("N"));
        }
        else {
            record.setFieldValue(PremiumFields.IS_ROW_ELIGIBLE_FOR_VIEW_MEM_CONT, YesNoFlag.getInstance("Y"));
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() > 0) {
            YesNoFlag isPremiumTypeEditable = YesNoFlag.N;
            Iterator recIter = recordSet.getRecords();
            while (recIter.hasNext()) {
                Record record = (Record) recIter.next();
                String excessExistsB = PremiumAccountingFields.hasExcessExistsB(record) ? PremiumAccountingFields.getExcessExistsB(record) : "";

                if (excessExistsB.equalsIgnoreCase("Y")) {
                    isPremiumTypeEditable = YesNoFlag.Y;
                    break;
                }
            }
            recordSet.getSummaryRecord().setFieldValue(IS_PREMIUM_TYPE_EDITABLE, isPremiumTypeEditable);
        }
    }

    private static String IS_PREMIUM_TYPE_EDITABLE = "isPremiumTypeEditable";
}
