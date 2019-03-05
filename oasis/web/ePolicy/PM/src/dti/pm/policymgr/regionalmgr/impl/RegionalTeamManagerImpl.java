package dti.pm.policymgr.regionalmgr.impl;

import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.policymgr.regionalmgr.RegionalTeamFields;
import dti.pm.policymgr.regionalmgr.RegionalTeamManager;
import dti.pm.policymgr.regionalmgr.dao.RegionalTeamDAO;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load all regional team.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/16/2013       awu         138241 - Modified validateAllRegionalTeamAndUnderwriter to add new validation logic
 * 01/13/2014       awu         148783 - 1. Added MemberEntitlementRecordLoadProcessor to enforce entitlements for team member.
 *                                       2. Modified RegionalTeamEntitlementRecordLoadProcessor to enforce entitlements for team.
 * 03/03/2017       lzhang      182510 - Modified validateAllRegionalTeamAndUnderwriter to Add PM_ONE_UNDW_ONE_TEAM system parameter
 *                                       to control whether allow multi-underwriter
 * ---------------------------------------------------
 */
public class RegionalTeamManagerImpl implements RegionalTeamManager {

    /**
     * Returns a RecordSet loaded with list of regional teams.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRegionalTeam(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRegionalTeams", new Object[]{inputRecord});
        }

        DefaultRecordLoadProcessor entitlementRLP = new RegionalTeamEntitlementRecordLoadProcessor();
        RecordSet rs = getRegionalTeamDAO().loadAllRegionalTeam(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRegionalTeams", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of regional team members.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTeamUnderwriter(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTeamUnderwriter", new Object[]{inputRecord});
        }
        DefaultRecordLoadProcessor entitlementRLP = new MemberEntitlementRecordLoadProcessor();
        RecordSet rs = getRegionalTeamDAO().loadAllTeamUnderwriter(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTeamUnderwriter", rs);
        }
        return rs;
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'N' - Not changed.
     *
     * @param inputRecords       A set of regional team.
     * @param memberInputRecords A set of team member.
     * @return
     */
    public int saveAllRegionalTeamAndUnderwriter(RecordSet inputRecords, RecordSet memberInputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRegionalTeamAndUnderwriter", new Object[]{inputRecords, memberInputRecords});
        }
        // Validate the regional team and team member.
        validateAllRegionalTeamAndUnderwriter(inputRecords, memberInputRecords);
        // Set the rowStatus to all records.
        RecordSet changedTeams = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        RecordSet changedMemebers = PMRecordSetHelper.setRowStatusOnModifiedRecords(memberInputRecords);

        int processCount = 0;
        // Save all regional teams.
        if (changedTeams.getSize() > 0) {
            processCount = getRegionalTeamDAO().saveAllRegionalTeam(changedTeams);
        }
        // Save all team members.
        if (changedMemebers.getSize() > 0) {
            processCount += getRegionalTeamDAO().saveAllTeamUnderwriter(changedMemebers);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRegionalTeamAndUnderwriter", new Integer(processCount));
        }
        return processCount;
    }

    /**
     * Get initial values for the regional team when adds a team.
     *
     * @return
     */
    public Record getInitialValuesForRegionalTeam() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForRegionalTeam");
        }
        // Set the sequence No to regional team Id.
        Record output = new Record();
        RegionalTeamFields.setRegionalTeamId(output, getDbUtilityManager().getNextSequenceNo().toString());
        RegionalTeamEntitlementRecordLoadProcessor.setInitialEntitlementValuesForTeam(output);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForRegionalTeam", output);
        }
        return output;
    }

    /**
     * Get initial values for the regional team member when adds a member.
     *
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForRegionalTeamMember(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForRegionalTeamMember", inputRecord);
        }

        Record output = new Record();
        // Set the sequence No to underwriter sequence Id.
        RegionalTeamFields.setUnderwriterSequenceId(output, getDbUtilityManager().getNextSequenceNo().toString());
        RegionalTeamFields.setRegionalTeamId(output, RegionalTeamFields.getRegionalTeamId(inputRecord));
        RegionalTeamFields.setEffectiveFromDate(output, DateUtils.formatDate(new Date()));
        RegionalTeamFields.setEffectiveToDate(output, "01/01/3000");

        MemberEntitlementRecordLoadProcessor.setInitialEntitlementValuesForTeam(output);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForRegionalTeamMember", output);
        }
        return output;
    }

    /**
     * Get the underwriter Id when the administrator selects the team member name.
     *
     * @param inputRecord
     * @return
     */
    public Record getUnderwriterId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUnderwriterId", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        RecordSet rs = getRegionalTeamDAO().getUnderwriterId(inputRecord);
        if (rs.getSize() > 0) {
            outputRecord = rs.getRecord(0);
        }
        else {
            RegionalTeamFields.setUnderwriterId(outputRecord, "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUnderwriterId", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Validate all regional team and team member.
     *
     * @param inputRecords       A Set of regional team.
     * @param memberInputRecords A set of team member.
     */
    protected void validateAllRegionalTeamAndUnderwriter(RecordSet inputRecords, RecordSet memberInputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRegionalTeamAndUnderwriter", new Object[]{inputRecords, memberInputRecords});
        }
        // The Not changed records are also should be include, since system will compares all the team records.
        RecordSet teamChangedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        int teamSize = teamChangedRecords.getSize();

        // Loop the team recordSet to check whether there are two rows in the regional team has the same team code.
        for (int i = 0; i < teamSize; i++) {
            Record currentRecord = teamChangedRecords.getRecord(i);
            String rowId = RegionalTeamFields.getRegionalTeamId(currentRecord);
            // Validate the empty team code and description.
            if (StringUtils.isBlank(RegionalTeamFields.getRegionalTeamCode(currentRecord))) {
                MessageManager.getInstance().addErrorMessage("pm.regional.team.required", RegionalTeamFields.REGIONAL_TEAM_CODE, rowId);
            }
            if (StringUtils.isBlank(RegionalTeamFields.getDescription(currentRecord))) {
                MessageManager.getInstance().addErrorMessage("pm.regional.description.required",
                    RegionalTeamFields.DESCRIPTION, rowId);
            }
            // Loop the recordSet to compare the current record to others.
            for (int j = i + 1; j < teamSize; j++) {
                Record tempRecord = teamChangedRecords.getRecord(j);
                if (RegionalTeamFields.getRegionalTeamCode(currentRecord).equals(RegionalTeamFields.getRegionalTeamCode(tempRecord))) {
                    String currentRowNum = String.valueOf(currentRecord.getRecordNumber() + 1);
                    String tempRowNum = String.valueOf(tempRecord.getRecordNumber() + 1);

                    // System passes the currentRowNum and tempRowNum and goes to the current row.
                    MessageManager.getInstance().addErrorMessage("pm.regional.team.hasSameTeamCode", new String[]{currentRowNum, tempRowNum},
                        RegionalTeamFields.REGIONAL_TEAM_CODE, rowId);
                    throw new ValidationException("Team member start date is after end date.");
                }
            }
        }
        RecordSet avaliableMemberSubSet = memberInputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        // Loop the team member recordSet to check the whether there are two rows have the same team Id,
        // entity Id and overlapping time period.
        for (int k = 0; k < teamSize; k++) {
            // Retrieve the sub member list of each team record.
            // In order to pass the correct recordNumber of sub member list, system should recreate the recordNumber.
            Record currentRecord = teamChangedRecords.getRecord(k);
            RecordSet memberSubSet = avaliableMemberSubSet.getSubSet(new RecordFilter(RegionalTeamFields.REGIONAL_TEAM_ID,
                RegionalTeamFields.getRegionalTeamId(currentRecord)), false);
            boolean pmOneUndwOneTeamSysParm= YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_ONE_UNDW_ONE_TEAM", "Y")).booleanValue();

            // Loop the sub member list to compare the current record to others.
            int memberSubSize = memberSubSet.getSize();
            int underwriterSize = 0;
            for (int i = 0; i < memberSubSize; i++) {
                Record currentMemberRecord = memberSubSet.getRecord(i);
                String rowId = RegionalTeamFields.getUnderwriterSequenceId(currentMemberRecord);
                rowId = RegionalTeamFields.getRegionalTeamId(currentMemberRecord) + "," + rowId;
                // Validate the empty entityId, effectiveFromDate and effectiveToDate.
                // If the date is empty, system shouldn't run continue, since the date is used in the following.
                if(StringUtils.isBlank(RegionalTeamFields.getEntityId(currentMemberRecord))){
                   MessageManager.getInstance().addErrorMessage("pm.regional.name.required",
                            RegionalTeamFields.ENTITY_ID, rowId);
                }
                if(StringUtils.isBlank(RegionalTeamFields.getEffectiveFromDate(currentMemberRecord))){
                    MessageManager.getInstance().addErrorMessage("pm.regional.startdate.required",
                        RegionalTeamFields.EFFECTIVE_FROM_DATE, rowId);
                    throw new ValidationException("The start date is empty.");
                }
                if(StringUtils.isBlank(RegionalTeamFields.getEffectiveToDate(currentMemberRecord))){
                    MessageManager.getInstance().addErrorMessage("pm.regional.enddate.required",
                            RegionalTeamFields.EFFECTIVE_TO_DATE, rowId);
                    throw new ValidationException("The end date is empty.");
                }

                Date memberEffFromDate1 = DateUtils.parseDate(RegionalTeamFields.getEffectiveFromDate(currentMemberRecord));
                Date memberEffToDate1 = DateUtils.parseDate(RegionalTeamFields.getEffectiveToDate(currentMemberRecord));
                // Check whether the start date of member is after the end date.
                if (memberEffFromDate1.after(memberEffToDate1)) {
                    String currentMemberRowNum = String.valueOf(currentMemberRecord.getRecordNumber() + 1);
                    MessageManager.getInstance().addErrorMessage("pm.regional.team.member.date.error", new String[]{currentMemberRowNum},
                        RegionalTeamFields.EFFECTIVE_FROM_DATE, rowId);
                    throw new ValidationException("Team member start date is after end date.");
                }

                if (pmOneUndwOneTeamSysParm){
                    if (RegionalTeamFields.RegionalTeamCodeValues.UNDERWRITER.equals(RegionalTeamFields.getUnderwritingTypeCode(currentMemberRecord))) {
                        // Check whether the underwriter is existing on the other team.
                        if (currentMemberRecord.isUpdateIndicatorInserted() || currentMemberRecord.isUpdateIndicatorUpdated()) {
                            RecordSet underwriterSet = avaliableMemberSubSet.getSubSet(new RecordFilter(RegionalTeamFields.ENTITY_ID,
                                RegionalTeamFields.getEntityId(currentMemberRecord)), false);
                            for (int j = 0; j < underwriterSet.getSize(); j++) {
                                Record underwriterRec = underwriterSet.getRecord(j);
                                if (!StringUtils.isSame(RegionalTeamFields.getRegionalTeamId(currentMemberRecord),
                                    RegionalTeamFields.getRegionalTeamId(underwriterRec))) {
                                    RecordSet ownedTeamSet = teamChangedRecords.getSubSet(new RecordFilter(RegionalTeamFields.REGIONAL_TEAM_ID,
                                        RegionalTeamFields.getRegionalTeamId(underwriterRec)));
                                    String teamCode = RegionalTeamFields.getRegionalTeamCode(ownedTeamSet.getRecord(0));
                                    MessageManager.getInstance().addErrorMessage("pm.regional.team.member.underwriter.duplicate.error", new String[]{teamCode},
                                        RegionalTeamFields.UNDERWRITING_TYPE_CODE, rowId);
                                    throw new ValidationException("An underwriter can only be on one team.");
                                }
                            }
                        }
                        underwriterSize++;
                    }

                    if (underwriterSize > 1) {
                        String currentRowNum = String.valueOf(currentRecord.getRecordNumber() + 1);
                        MessageManager.getInstance().addErrorMessage("pm.regional.team.member.underwriter.duplicate", new String[]{currentRowNum},
                            RegionalTeamFields.UNDERWRITING_TYPE_CODE, rowId);
                        throw new ValidationException("Each team can only have one underwriter.");
                    }
                }

                // Compare the current member record to others.
                for (int j = i + 1; j < memberSubSize; j++) {
                    Record tempMemberRecord = memberSubSet.getRecord(j);
                    Date memberEffFromDate2 = DateUtils.parseDate(RegionalTeamFields.getEffectiveFromDate(tempMemberRecord));
                    Date memberEffToDate2 = DateUtils.parseDate(RegionalTeamFields.getEffectiveToDate(tempMemberRecord));
                    // Actually, the two regional team Id is equal here since they are belongs the same team.
                    if ((RegionalTeamFields.getRegionalTeamId(currentMemberRecord).equals(RegionalTeamFields.getRegionalTeamId(tempMemberRecord)))
                        && ((!pmOneUndwOneTeamSysParm && RegionalTeamFields.getEntityId(currentMemberRecord).equals(RegionalTeamFields.getEntityId(tempMemberRecord)))
                            ||(pmOneUndwOneTeamSysParm
                            && (RegionalTeamFields.getUnderwritingTypeCode(currentMemberRecord).equals(RegionalTeamFields.getUnderwritingTypeCode(tempMemberRecord)))
                            && (!RegionalTeamFields.RegionalTeamCodeValues.UNDERWRITER.equals(RegionalTeamFields.getUnderwritingTypeCode(currentMemberRecord)))))
                        && checkOverLappingDate(memberEffFromDate1, memberEffToDate1, memberEffFromDate2, memberEffToDate2)) {
                        String currentMemberRowNum = String.valueOf(currentMemberRecord.getRecordNumber() + 1);
                        String tempMemberRowNum = String.valueOf(tempMemberRecord.getRecordNumber() + 1);
                        // System passes the currentMemberRowNum and tempMemberRowNum and goes to the current row.
                        if (!pmOneUndwOneTeamSysParm){
                            MessageManager.getInstance().addErrorMessage("pm.regional.team.member.duplicate1", new String[]{currentMemberRowNum, tempMemberRowNum},
                                RegionalTeamFields.EFFECTIVE_FROM_DATE, rowId);
                            throw new ValidationException("Team member start date is after end date.");
                        } else{
                            MessageManager.getInstance().addErrorMessage("pm.regional.team.member.duplicate2", new String[]{currentMemberRowNum, tempMemberRowNum},
                                RegionalTeamFields.EFFECTIVE_FROM_DATE, rowId);
                            throw new ValidationException("The time period of team member is overlap.");
                        }
                    }
                }
            }
            if (pmOneUndwOneTeamSysParm){
                if (underwriterSize == 0) {
                    String rowId = RegionalTeamFields.getRegionalTeamId(currentRecord);
                    String currentRowNum = String.valueOf(currentRecord.getRecordNumber() + 1);
                    MessageManager.getInstance().addErrorMessage("pm.regional.team.no.member.underwriter", new String[]{currentRowNum},
                        RegionalTeamFields.UNDERWRITING_TYPE_CODE, rowId);
                    throw new ValidationException("Each team must have one underwriter.");
                }
            }

        }

        // Throw validation exception if there is any error message.
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("The team member is duplicate.");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllRegionalTeamAndUnderwriter");
        }
    }

    /**
     * Check whether date overlapping or not, if exists, return true, else return false;
     *
     * @param fromDate1
     * @param toDate1
     * @param fromDate2
     * @param toDate2
     * @return
     */
    private boolean checkOverLappingDate(Date fromDate1, Date toDate1, Date fromDate2, Date toDate2) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkOverLappingDate", new Object[]{fromDate1, toDate1, fromDate2, toDate2});
        }
        boolean result = false;
        if ((fromDate1.before(fromDate2) && (toDate1.after(fromDate2))) ||
            (fromDate1.before(toDate2) && (toDate1.after(toDate2))) ||
            (fromDate1.after(fromDate2) && (toDate1.before(toDate2))) ||
            (fromDate1.before(fromDate2) && (toDate1.after(toDate2))) ||
            (fromDate1.equals(fromDate2) && (toDate1.equals(toDate2))) ||
            (fromDate1.equals(fromDate2) && (toDate1.before(toDate2))) ||
            (fromDate1.equals(fromDate2) && (toDate1.after(toDate2))) ||
            (fromDate1.after(fromDate2) && (toDate1.equals(toDate2))) ||
            (fromDate1.before(fromDate2) && (toDate1.equals(toDate2)))) {
            result = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkOverLappingDate", YesNoFlag.getInstance(result));
        }

        return result;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getRegionalTeamDAO() == null)
            throw new ConfigurationException("The required property 'regionalTeamDAO' is missing.");
    }

    public RegionalTeamDAO getRegionalTeamDAO() {
        return this.m_regionalTeamDAO;
    }

    public void setRegionalTeamDAO(RegionalTeamDAO regionalTeamDAO) {
        this.m_regionalTeamDAO = regionalTeamDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return this.m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        this.m_dbUtilityManager = dbUtilityManager;
    }

    private RegionalTeamDAO m_regionalTeamDAO;
    private DBUtilityManager m_dbUtilityManager;
}
