package dti.ci.helpers;

/**
 * Interface for CIS Claims constants.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 12, 2005
 *
 * @author HXY
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic constant for issue #97673
 * ---------------------------------------------------
*/

public interface ICIClaimsConstants extends ICIConstants {
    public static final String PARTICIPANTS_LAYER = "Participants_Layer";
    public static final String PARTICIPANTS_GRID_DATA_BEAN = "participantsGridDataBean";
    public static final String PARTICIPANTS_GRID_HEADER_BEAN = "participantsGridHeaderBean";
    public static final String PARTICIPANT_GRID_ID = "participantsGrid";

    public static final String COMPANION_CLAIMS_LAYER = "Companion_Claims_Layer";
    public static final String COMPANION_GRID_DATA_BEAN = "companionGridDataBean";
    public static final String COMPANION_GRID_HEADER_BEAN = "companionGridHeaderBean";
    public static final String COMPANION_GRID_ID = "companionGrid";

    public static final String CLAIMPK = "claimPK";
    public static final String ECLAIM_CONTEXT_ROOT = "eClaimContextRoot";
    public static final String CASE_PHRASE = "casePhrase";
    public static final String CLAIM_PHRASE = "claimPhrase";

    public static final String LOSS_DATE = "lossDate";
    public static final String REPORT_DATE = "reportDate";
    public static final String CASENO = "caseNo";
    public static final String PATIENT_NAME = "patientName";
    public static final String EXAMINER_NAME = "examinerName";
    public static final String CLAIM_STATUS_CODE_LD = "claimStatusCodeLD";
    public static final String CLAIM_TYPE_CODE_SD = "claimTypeCodeSD";
    public static final String BRANCH_OFFICE = "branchOffice";
    public static final String PAID_LOSS = "paidLoss";
    public static final String PAID_EXPENSE = "paidExpense";
    public static final String DEFENSE_ATTORNEY = "defenseAttorney";
    public static final String PLAINTIFF_ATTORNEY = "plaintiffAttorney";
    public static final String DEFENSE_LAW_FIRM = "defenseLawFirm";
    public static final String PLAINTIFF_LAW_FIRM = "plaintiffLawFirm";
    public static final String RESERVE_LOSS = "reserveLoss";
    public static final String RESERVE_EXPENSE = "reserveExpense";
    public static final String CASE_PK = "casePK";
    public static final String CLAIMNO = "claimNo";

    String CLAIM_TABLE_NAME = "CLAIM";

}
