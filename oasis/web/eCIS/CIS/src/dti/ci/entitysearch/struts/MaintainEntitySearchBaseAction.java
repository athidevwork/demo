package dti.ci.entitysearch.struts;

import dti.ci.entitysearch.EntitySearchManager;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/3/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainEntitySearchBaseAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    protected void checkIfEnablePhoneNumberPartSearch(HttpServletRequest request) {
        String methodName = "checkIfEnablePhoneNumberPartSearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, request);
        }

        OasisFields fields = ActionHelper.getFields(request);
        String ciPhonePartSrch = "CI_PHONE_PART_SRCH";

        if (YesNoFlag.Y.toString().equalsIgnoreCase(SysParmProvider.getInstance().getSysParm(ciPhonePartSrch, YesNoFlag.N.getName()))) {
            if (fields.hasField(EntitySearchFields.SEARCH_CRITERIA_PHONE_NUMBER)) {
                OasisFormField phoneNumberFld = fields.getField(EntitySearchFields.SEARCH_CRITERIA_PHONE_NUMBER);
                if ("PH".equalsIgnoreCase(phoneNumberFld.getDatatype()) && phoneNumberFld.getIsVisible()) {
                    phoneNumberFld.setDatatype("");
                }
            }
            request.setAttribute(ciPhonePartSrch, YesNoFlag.Y.getName());
        } else {
            request.setAttribute(ciPhonePartSrch, YesNoFlag.N.getName());
        }

        l.exiting(getClass().getName(), methodName);
    }

    protected String getIncludedAddlDataByCoverageLayering(HttpServletRequest request) {
        String methodName = "getIncludedAddlDataByCoverageLayering";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, request);
        }

        String coverageLayering = SysParmProvider.getInstance().getSysParm("CM_CVG_LAYERING", "N");
        String includedAddlData = null;
        String claimPK = request.getParameter(EntitySearchFields.CLAIM_PK);
        if (claimPK == null) {
            claimPK = "null";
        }

        request.setAttribute(EntitySearchFields.CLAIM_PK, claimPK);

        if (coverageLayering.equalsIgnoreCase("Y") && !claimPK.equalsIgnoreCase("null")) {
            includedAddlData = "cm_custom.has_coverage(ent.entity_pk, " + claimPK + ")";

            OasisFields fields = ActionHelper.getFields(request);
            if (fields != null) {
                OasisFormField formField = fields.getField(EntitySearchFields.SEARCH_CRITERIA_ADDL_FIELD);
                formField.setIsVisible(true);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, includedAddlData);
        }
        return includedAddlData;
    }

    protected boolean checkIfIncludePolicyNoWithinSearch(HttpServletRequest request, Record inputRecord) {
        String methodName = "checkIfIncludePolicyNoWithinSearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{request, inputRecord});
        }

        boolean includedPolicyNo = getEntitySearchManager().isPolicyNoIncludedWithinSearch(inputRecord);
        // if policyNo is not part of the search criteria, we are to set it invisible
        if (!includedPolicyNo) {
            OasisFields oasisFields = ActionHelper.getFields(request);

            if (oasisFields != null) {
                OasisFormField formField = oasisFields.getField("ENTITYLISTISPRIMARYRISK_GH");
                formField.setIsVisible(false);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, includedPolicyNo);
        }
        return includedPolicyNo;
    }

    protected Record getSearchCriteriaRecord(HttpServletRequest request, Record inputRecord) {
        String methodName = "getSearchCriteriaRecord";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{request, inputRecord});
        }

        Record searchRecord = new Record();
        searchRecord.setFields(inputRecord);
        EntitySearchFields.setIncludedPolicyNo(searchRecord, checkIfIncludePolicyNoWithinSearch(request, inputRecord));
        EntitySearchFields.setIncludedAddlData(searchRecord, getIncludedAddlDataByCoverageLayering(request));

        if (EntitySearchFields.GLOBAL_SEARCH_PROCESS.equalsIgnoreCase(inputRecord.getStringValueDefaultEmpty(RequestIds.PROCESS))) {
            searchRecord.setFieldValue(inputRecord.getStringValueDefaultEmpty(RequestIds.GLOBAL_SEARCH_FIELD_NAME),
                    inputRecord.getStringValueDefaultEmpty(RequestIds.GLOBAL_SEARCH_FIELD_VALUE));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, searchRecord);
        }
        return searchRecord;
    }

    @Override
    public void verifyConfig() {
        if (getEntitySearchManager() == null) {
            throw new ConfigurationException("The required property 'entitySearchManager' is missing.");
        }
    }

    public EntitySearchManager getEntitySearchManager() {
        return m_entitySearchManager;
    }

    public void setEntitySearchManager(EntitySearchManager entitySearchManager) {
        this.m_entitySearchManager = entitySearchManager;
    }

    private EntitySearchManager m_entitySearchManager;
}
