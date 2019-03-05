package dti.ci.claimsmgr.impl;

import dti.ci.claimsmgr.ClaimsManager;
import dti.ci.claimsmgr.dao.ClaimsDAO;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Claims.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 7, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic method for issue #97673
 * 02/13/2015       bzhu        Issue 160886. Add overload function
 *                                            retrieveClaimNoFilterClauseSQL(HttpServletRequest request).
 * 06/26/2017       ddai        Issue 185457. Add new claim no filter.
 * 04/19/2018       jld         Issue 192609: Refactor for eCIS.
 * 05/24/2018       ylu         Issue 192609: refactor update.
 * 07/23/2018       kshen       Issue 194134. Add source table name to isSourceAcceptedDetail.
 * 10/04/2018       hxk         Issue 191329
 *                              1)  Add call to filterRecordSetViaAccessControl so we will
 *                              filter the fields or claims based on config.
 * 11/12/2018       hxk         Issue 196950
 *                              1)  Determine if we are restricting at the case level and
 *                              set restrictCaseB flag accordingly.
 * ---------------------------------------------------
*/

public class ClaimsManagerImpl implements ClaimsManager {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Get the first claim in dropdown.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadFirstClaim(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadFirstClaim", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getClaimsDAO().loadClaimLov(inputRecord);
        Record rec;
        if (rs.getSize() == 0) {
            rec = new Record();
            rec.setFieldValue("claimId", -1);
            rec.setFieldValue("claimNo", "");
        } else {
            rec = rs.getFirstRecord();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadFirstClaim", rs);
        }
        return rec;
    }

    /**
     * Get the claim info.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimInfo", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getClaimsDAO().loadClaimInfo(inputRecord);
        if (rs.getSize() > 0) {
            Record rec = rs.getFirstRecord();
            String claimNo = rec.getStringValue("claimNo");
            String caseNo  = rec.getStringValue("caseNo");
            //First see if case is restricted
            Record rdCase = getAccessControlFilterManager().isSourceAcceptedDetail("OCCURRENCE", caseNo);
            Boolean restrictIt = false;
            if (rdCase.hasField("restrictB") && rdCase.hasField("displayB")
                    && "Y".equalsIgnoreCase(rdCase.getStringValue("restrictB"))
                    && "Y".equalsIgnoreCase(rdCase.getStringValue("displayB"))) {
                restrictIt = true;
                rec.setFieldValue("restrictCaseB", "Y");
            }



            Record rd = getAccessControlFilterManager().isSourceAcceptedDetail("CLAIM", claimNo);
            if (rd.hasField("restrictB") && rd.hasField("displayB")
                    && "Y".equalsIgnoreCase(rd.getStringValue("restrictB"))
                    && "Y".equalsIgnoreCase(rd.getStringValue("displayB"))) {
                restrictIt = true;
                rec.setFieldValue("restrictB", "Y");
            }

            if (restrictIt){
                String pageCode = inputRecord.getStringValue("pageCode");
                RecordSet rsFilter = getAccessControlFilterManager().getRestrictFieldListByPage(pageCode);
                for (int j = 0; j < rsFilter.getSize(); j++) {
                    Record rsfRd = rsFilter.getRecord(j);
                    String fieldName = rsfRd.getStringValue("fieldId");
                    if (fieldName.endsWith("_GH")) {
                        fieldName = fieldName.replaceAll("_GH", "");
                    }
                    rec.setFieldValue(fieldName,"");

                }
                rs.replaceRecord(0,rec);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaimInfo", rs);
        }
        return rs;
    }

    /**
     * Get the claim participants list.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimParticipants(Record inputRecord, HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimParticipants", new Object[]{inputRecord});
        }

        RecordSet rs = getClaimsDAO().loadClaimParticipants(inputRecord);
        rs = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, rs, "", "companion_claimNo");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaimParticipants", rs);
        }
        return rs;
    }

    /**
     * Get the companion claims.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadCompanion(Record inputRecord,HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCompanion", new Object[]{inputRecord});
        }

        RecordSet rs = getClaimsDAO().loadCompanion(inputRecord);
        rs = getAccessControlFilterManager().filterRecordSetViaAccessControl(request,rs,"","companionClaimNo");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCompanion", rs);
        }
        return rs;
    }

    /**
     * verify configuration method
     */
    public void verifyConfig() {
        if (getClaimsDAO() == null)
            throw new ConfigurationException("The required property 'claimsDAO' is missing.");

        if (getAccessControlFilterManager() == null) {
            throw new ConfigurationException("The required property 'accessControlFilterManager' is missing.");
        }
    }

    public AccessControlFilterManager getAccessControlFilterManager() {
        return accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.accessControlFilterManager = accessControlFilterManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public ClaimsDAO getClaimsDAO() {
        return m_claimsDAO;
    }

    public void setClaimsDAO(ClaimsDAO m_claimsDAO) {
        this.m_claimsDAO = m_claimsDAO;
    }

    private AccessControlFilterManager accessControlFilterManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private ClaimsDAO m_claimsDAO;
}
