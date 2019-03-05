package dti.pm.componentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LocaleUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.componentmgr.ComponentFields;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 21, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/14/2010       tzhao       Issue#109875:Modified postProcessRecord method to support mutiple currency.
 * 12/26/2012       adeng       Issue#140084:Modified postProcessRecord method to correctly display single
 *                              value and value range.
 * ---------------------------------------------------
 */
public class ComponentAvailableRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // Fill component value for the available component record
        StringBuffer componentValue = new StringBuffer();
        YesNoFlag percentValueB = ComponentFields.getPercentValueB(record);
        String lowValue = ComponentFields.getLowValue(record);
        String highValue = ComponentFields.getHighValue(record);
        String componentSign = ComponentFields.getComponentSigh(record);
        if (!StringUtils.isBlank(lowValue) && !StringUtils.isBlank(highValue)) {
            // If Percentage component
            if (percentValueB.booleanValue()) {
                if (lowValue.equals(highValue)) {
                    if (componentSign.equals("-1")) {
                        componentValue.append("-").append(lowValue).append("%");
                    }
                    else if (componentSign.equals("1")) {
                        componentValue.append(lowValue).append("%");
                    }
                }
                else if (componentSign.equals("-1")) {
                    componentValue.append("-").append(lowValue).append("% to -").append(highValue).append("%");
                }
                else if (componentSign.equals("1")) {
                    componentValue.append(lowValue).append("% to ").append(highValue).append("%");
                }
            }
            // If Dollar component
            else {
                if (lowValue.equals(highValue)) {
                    if (componentSign.equals("-1")) {
                        componentValue.append(LocaleUtils.getOasisCurrencySymbol() + "-").append(lowValue);
                    }
                    else if (componentSign.equals("1")) {
                        componentValue.append(LocaleUtils.getOasisCurrencySymbol()).append(lowValue);
                    }
                }
                else if (componentSign.equals("-1")) {
                    componentValue.append(LocaleUtils.getOasisCurrencySymbol()+"-").append(lowValue).append(" to "+LocaleUtils.getOasisCurrencySymbol()+"-").append(highValue);
                }
                else if (componentSign.equals("1")) {
                    componentValue.append(LocaleUtils.getOasisCurrencySymbol()).append(lowValue).append(" to "+LocaleUtils.getOasisCurrencySymbol()).append(highValue);
                }
            }
        }
        ComponentFields.setComponentValue(record, componentValue.toString());

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
    }

    public ComponentAvailableRecordLoadProcessor() {
    }
}
