package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.SysParmProvider;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents different transaction codes for a given transaction.
 * The getInstance method is a convenience method for parsing a string into transaction code.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 22, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/22/2011       wfu         113063 - Add TransactionCode ENDQTSTAT
 * 04/25/2011       fcb         105791 - Add TransactionCode CONVCOVG.
 * 06/23/2011       dzhang      117998 - Add TransactionCode ENDRATE.
 * 09/19/2011       ryzhao      123942 - Add TransactionCode PRIORACT and UNKNOWN.
 * 09/28/2011       ryzhao      123942 - 1) Roll back previous changes.
 *                                       2) Modify getInstance() method to dynamicly new a transaction code object
 *                                          if it is a customer configured transaction code.
 * 10/13/2011       ryzhao      123942 - Add decline tail reason code to a new map c_declineTailReasonTransactionCodes
 *                                       per Bill Reeder's comments.
 * 02/10/2012       xnie        130051 - 1) Add a new map c_customTypes for some special custom transactions.
 *                                       2) Remove TransactionCode ENDRATE which is added for 117998.
 *                                       3) Remove map c_declineTailReasonTransactionCodes which is added for 123942.
 *                                          it is replaced by c_customTypes.
 * 02/27/2013       xnie        138026 - Added fields for generate Additional Insured transaction code.
 * ---------------------------------------------------
 */
public class TransactionCode extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    // Map only contains normal transaction codes
    private static Map c_validTypes = new HashMap();
    // Map only contains special custom transaction codes
    private static Map c_customTypes = new HashMap();

    public static final int ACCEPTPOL_VALUE = getNextIntValue();
    public static final TransactionCode ACCEPTPOL = new TransactionCode(ACCEPTPOL_VALUE, "ACCEPTPOL");

    public static final int AUTOMATIC_VALUE = getNextIntValue();
    public static final TransactionCode AUTOMATIC = new TransactionCode(AUTOMATIC_VALUE, "AUTOMATIC");

    public static final int AUTORENEW_VALUE = getNextIntValue();
    public static final TransactionCode AUTORENEW = new TransactionCode(AUTORENEW_VALUE, "AUTORENEW");

    public static final int CANC_REWRT_VALUE = getNextIntValue();
    public static final TransactionCode CANC_REWRT = new TransactionCode(CANC_REWRT_VALUE, "CANC_REWRT");

    public static final int CANCEL_VALUE = getNextIntValue();
    public static final TransactionCode CANCEL = new TransactionCode(CANCEL_VALUE, "CANCEL");

    public static final int CHGADDRESS_VALUE = getNextIntValue();
    public static final TransactionCode CHGADDRESS = new TransactionCode(CHGADDRESS_VALUE, "CHGADDRESS");

    public static final int CHGNAME_VALUE = getNextIntValue();
    public static final TransactionCode CHGNAME = new TransactionCode(CHGNAME_VALUE, "CHGNAME");

    public static final int CLICOIHOLD_VALUE = getNextIntValue();
    public static final TransactionCode CLICOIHOLD = new TransactionCode(CLICOIHOLD_VALUE, "CLICOIHOLD");

    public static final int CONVREISSU_VALUE = getNextIntValue();
    public static final TransactionCode CONVREISSU = new TransactionCode(CONVREISSU_VALUE, "CONVREISSU");

    public static final int CONVRENEW_VALUE = getNextIntValue();
    public static final TransactionCode CONVRENEW = new TransactionCode(CONVRENEW_VALUE, "CONVRENEW");

    public static final int COVGCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode COVGCANCEL = new TransactionCode(COVGCANCEL_VALUE, "COVGCANCEL");

    public static final int COVGREINST_VALUE = getNextIntValue();
    public static final TransactionCode COVGREINST = new TransactionCode(COVGREINST_VALUE, "COVGREINST");

    public static final int CPRTCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode CPRTCANCEL = new TransactionCode(CPRTCANCEL_VALUE, "CPRTCANCEL");

    public static final int CPRTREINST_VALUE = getNextIntValue();
    public static final TransactionCode CPRTREINST = new TransactionCode(CPRTREINST_VALUE, "CPRTREINST");

    public static final int DECLINEAPP_VALUE = getNextIntValue();
    public static final TransactionCode DECLINEAPP = new TransactionCode(DECLINEAPP_VALUE, "DECLINEAPP");

    public static final int DECLINEPOL_VALUE = getNextIntValue();
    public static final TransactionCode DECLINEPOL = new TransactionCode(DECLINEPOL_VALUE, "DECLINEPOL");

    public static final int DISTRIB_VALUE = getNextIntValue();
    public static final TransactionCode DISTRIB = new TransactionCode(DISTRIB_VALUE, "DISTRIB");

    public static final int EMPCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode EMPCANCEL = new TransactionCode(EMPCANCEL_VALUE, "EMPCANCEL");

    public static final int ENDADDTLIN_VALUE = getNextIntValue();
    public static final TransactionCode ENDADDTLIN = new TransactionCode(ENDADDTLIN_VALUE, "ENDADDTLIN");

    public static final int ENDAFFILIA_VALUE = getNextIntValue();
    public static final TransactionCode ENDAFFILIA = new TransactionCode(ENDAFFILIA_VALUE, "ENDAFFILIA");

    public static final int ENDCHGTERM_VALUE = getNextIntValue();
    public static final TransactionCode ENDCHGTERM = new TransactionCode(ENDCHGTERM_VALUE, "ENDCHGTERM");

    public static final int ENDCOIHOLD_VALUE = getNextIntValue();
    public static final TransactionCode ENDCOIHOLD = new TransactionCode(ENDCOIHOLD_VALUE, "ENDCOIHOLD");

    public static final int ENDORSE_VALUE = getNextIntValue();
    public static final TransactionCode ENDORSE = new TransactionCode(ENDORSE_VALUE, "ENDORSE");

    public static final int ENDPOLADD_VALUE = getNextIntValue();
    public static final TransactionCode ENDPOLADD = new TransactionCode(ENDPOLADD_VALUE, "ENDPOLADD");

    public static final int ENDQUOTE_VALUE = getNextIntValue();
    public static final TransactionCode ENDQUOTE = new TransactionCode(ENDQUOTE_VALUE, "ENDQUOTE");

    public static final int EXTEND_VALUE = getNextIntValue();
    public static final TransactionCode EXTEND = new TransactionCode(EXTEND_VALUE, "EXTEND");

    public static final int GENCOIHOLD_VALUE = getNextIntValue();
    public static final TransactionCode GENCOIHOLD = new TransactionCode(GENCOIHOLD_VALUE, "GENCOIHOLD");

    public static final int GENADDINS_VALUE = getNextIntValue();
    public static final TransactionCode GENADDINS = new TransactionCode(GENADDINS_VALUE, "GENADDINS");

    public static final int GIVE_DISC_VALUE = getNextIntValue();
    public static final TransactionCode GIVE_DISC = new TransactionCode(GIVE_DISC_VALUE, "GIVE_DISC");

    public static final int MANRENEW_VALUE = getNextIntValue();
    public static final TransactionCode MANRENEW = new TransactionCode(MANRENEW_VALUE, "MANRENEW");

    public static final int NEWBUS_VALUE = getNextIntValue();
    public static final TransactionCode NEWBUS = new TransactionCode(NEWBUS_VALUE, "NEWBUS");

    public static final int OCCPCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode OCCPCANCEL = new TransactionCode(OCCPCANCEL_VALUE, "OCCPCANCEL");

    public static final int OOSENDORSE_VALUE = getNextIntValue();
    public static final TransactionCode OOSENDORSE = new TransactionCode(OOSENDORSE_VALUE, "OOSENDORSE");

    public static final int PURGE_VALUE = getNextIntValue();
    public static final TransactionCode PURGE = new TransactionCode(PURGE_VALUE, "PURGE");

    public static final int QUEST_VALUE = getNextIntValue();
    public static final TransactionCode QUEST = new TransactionCode(QUEST_VALUE, "QUEST");

    public static final int QUESTER_VALUE = getNextIntValue();
    public static final TransactionCode QUESTER = new TransactionCode(QUESTER_VALUE, "QUESTER");

    public static final int QUESTER2_VALUE = getNextIntValue();
    public static final TransactionCode QUESTER2 = new TransactionCode(QUESTER2_VALUE, "QUESTER2");

    public static final int QUESTER3_VALUE = getNextIntValue();
    public static final TransactionCode QUESTER3 = new TransactionCode(QUESTER3_VALUE, "QUESTER3");

    public static final int QUESTPT_VALUE = getNextIntValue();
    public static final TransactionCode QUESTPT = new TransactionCode(QUESTPT_VALUE, "QUESTPT");

    public static final int QUESTPT2_VALUE = getNextIntValue();
    public static final TransactionCode QUESTPT2 = new TransactionCode(QUESTPT2_VALUE, "QUESTPT2");

    public static final int QUESTPT3_VALUE = getNextIntValue();
    public static final TransactionCode QUESTPT3 = new TransactionCode(QUESTPT3_VALUE, "QUESTPT3");

    public static final int QUESTXSH_VALUE = getNextIntValue();
    public static final TransactionCode QUESTXSH = new TransactionCode(QUESTXSH_VALUE, "QUESTXSH");

    public static final int QUESTXSH2_VALUE = getNextIntValue();
    public static final TransactionCode QUESTXSH2 = new TransactionCode(QUESTXSH2_VALUE, "QUESTXSH2");

    public static final int QUESTXSI_VALUE = getNextIntValue();
    public static final TransactionCode QUESTXSI = new TransactionCode(QUESTXSI_VALUE, "QUESTXSI");

    public static final int QUOTE_VALUE = getNextIntValue();
    public static final TransactionCode QUOTE = new TransactionCode(QUOTE_VALUE, "QUOTE");

    public static final int REINSTATE_VALUE = getNextIntValue();
    public static final TransactionCode REINSTATE = new TransactionCode(REINSTATE_VALUE, "REINSTATE");

    public static final int REISSUE_VALUE = getNextIntValue();
    public static final TransactionCode REISSUE = new TransactionCode(REISSUE_VALUE, "REISSUE");

    public static final int RELCHG_VALUE = getNextIntValue();
    public static final TransactionCode RELCHG = new TransactionCode(RELCHG_VALUE, "RELCHG");

    public static final int REMOV_DISC_VALUE = getNextIntValue();
    public static final TransactionCode REMOV_DISC = new TransactionCode(REMOV_DISC_VALUE, "REMOV_DISC");

    public static final int RENCOIHOLD_VALUE = getNextIntValue();
    public static final TransactionCode RENCOIHOLD = new TransactionCode(RENCOIHOLD_VALUE, "RENCOIHOLD");

    public static final int RERATE_VALUE = getNextIntValue();
    public static final TransactionCode RERATE = new TransactionCode(RERATE_VALUE, "RERATE");

    public static final int RISKCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode RISKCANCEL = new TransactionCode(RISKCANCEL_VALUE, "RISKCANCEL");

    public static final int RISKREINST_VALUE = getNextIntValue();
    public static final TransactionCode RISKREINST = new TransactionCode(RISKREINST_VALUE, "RISKREINST");

    public static final int RRELCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode RRELCANCEL = new TransactionCode(RRELCANCEL_VALUE, "RRELCANCEL");

    public static final int RRELREIN_VALUE = getNextIntValue();
    public static final TransactionCode RRELREIN = new TransactionCode(RRELREIN_VALUE, "RRELREIN");

    public static final int SCVGCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode SCVGCANCEL = new TransactionCode(SCVGCANCEL_VALUE, "SCVGCANCEL");

    public static final int SCVGREINST_VALUE = getNextIntValue();
    public static final TransactionCode SCVGREINST = new TransactionCode(SCVGREINST_VALUE, "SCVGREINST");

    public static final int SPHANDLING_VALUE = getNextIntValue();
    public static final TransactionCode SPHANDLING = new TransactionCode(SPHANDLING_VALUE, "SPHANDLING");

    public static final int TLACCEPT_VALUE = getNextIntValue();
    public static final TransactionCode TLACCEPT = new TransactionCode(TLACCEPT_VALUE, "TLACCEPT");

    public static final int TLACTIVATE_VALUE = getNextIntValue();
    public static final TransactionCode TLACTIVATE = new TransactionCode(TLACTIVATE_VALUE, "TLACTIVATE");

    public static final int TLCANCEL_VALUE = getNextIntValue();
    public static final TransactionCode TLCANCEL = new TransactionCode(TLCANCEL_VALUE, "TLCANCEL");

    public static final int TLDECLINE_VALUE = getNextIntValue();
    public static final TransactionCode TLDECLINE = new TransactionCode(TLDECLINE_VALUE, "TLDECLINE");

    public static final int TLDECLINS_VALUE = getNextIntValue();
    public static final TransactionCode TLDECLINS = new TransactionCode(TLDECLINS_VALUE, "TLDECLINS");

    public static final int TLENDORSE_VALUE = getNextIntValue();
    public static final TransactionCode TLENDORSE = new TransactionCode(TLENDORSE_VALUE, "TLENDORSE");

    public static final int TLREINST_VALUE = getNextIntValue();
    public static final TransactionCode TLREINST = new TransactionCode(TLREINST_VALUE, "TLREINST");

    public static final int UNDOTERM_VALUE = getNextIntValue();
    public static final TransactionCode UNDOTERM = new TransactionCode(UNDOTERM_VALUE, "UNDOTERM");

    public static final int XSNBRATE_VALUE = getNextIntValue();
    public static final TransactionCode XSNBRATE = new TransactionCode(XSNBRATE_VALUE, "XSNBRATE");

    public static final int XSRERATE_VALUE = getNextIntValue();
    public static final TransactionCode XSRERATE = new TransactionCode(XSRERATE_VALUE, "XSRERATE");

    public static final int ENDQTSTAT_VALUE = getNextIntValue();
    public static final TransactionCode ENDQTSTAT = new TransactionCode(ENDQTSTAT_VALUE, "ENDQTSTAT");

    public static final int CONVCOVG_VALUE = getNextIntValue();
    public static final TransactionCode CONVCOVG = new TransactionCode(CONVCOVG_VALUE, "CONVCOVG");

    public static TransactionCode getInstance(String transactionCode) {
        String transactionCodeUpper = transactionCode.toUpperCase();
        TransactionCode result = (TransactionCode) c_validTypes.get(transactionCodeUpper);
        if (result == null) {
            result = (TransactionCode) c_customTypes.get(transactionCodeUpper);
            if (result == null) {
                result = new TransactionCode(getNextIntValue(), transactionCodeUpper);
                // When we create the transaction code object above, the object is put into c_validTypes map by the constructor.
                // Remove the special custom transactions from c_validTypes map which only contains normal transaction codes.
                c_validTypes.remove(transactionCodeUpper);
                c_customTypes.put(transactionCodeUpper, result);
            }
        }
        return result;
    }
    
    public boolean isRenewal() {
        return isManualRenewal() || isAutoRenewal();
    }

    public boolean isAcceptPol() {
        return intValue() == ACCEPTPOL_VALUE;
    }

    public boolean isAutoRenewal() {
        return intValue() == AUTORENEW_VALUE;
    }

    public boolean isCancRewrt() {
        return intValue() == CANC_REWRT_VALUE;
    }

    public boolean isCancellation() {
        return intValue() == CANCEL_VALUE;
    }

    public boolean isChgAddress() {
        return intValue() == CHGADDRESS_VALUE;
    }

    public boolean isChgName() {
        return intValue() == CHGNAME_VALUE;
    }

    public boolean isCliCoiHold() {
        return intValue() == CLICOIHOLD_VALUE;
    }

    public boolean isConvReissue() {
        return intValue() == CONVREISSU_VALUE;
    }

    public boolean isConvRenew() {
        return intValue() == CONVRENEW_VALUE;
    }

    public boolean isCovgCancel() {
        return intValue() == COVGCANCEL_VALUE;
    }

    public boolean isCovgReinst() {
        return intValue() == COVGREINST_VALUE;
    }

    public boolean isCprtCancel() {
        return intValue() == CPRTCANCEL_VALUE;
    }

    public boolean isCprtReinst() {
        return intValue() == CPRTREINST_VALUE;
    }

    public boolean isDeclineApp() {
        return intValue() == DECLINEAPP_VALUE;
    }

    public boolean isDeclinePol() {
        return intValue() == DECLINEPOL_VALUE;
    }

    public boolean isDistrib() {
        return intValue() == DISTRIB_VALUE;
    }

    public boolean isEmpCancel() {
        return intValue() == EMPCANCEL_VALUE;
    }

    public boolean isEndAddtlin() {
        return intValue() == ENDADDTLIN_VALUE;
    }

    public boolean isEndAffilia() {
        return intValue() == ENDAFFILIA_VALUE;
    }

    public boolean isEndchgTerm() {
        return intValue() == ENDCHGTERM_VALUE;
    }

    public boolean isEndCoiHold() {
        return intValue() == ENDCOIHOLD_VALUE;
    }

    public boolean isEndorsement() {
        return intValue() == ENDORSE_VALUE;
    }

    public boolean isEndPolAdd() {
        return intValue() == ENDPOLADD_VALUE;
    }

    public boolean isEndQuote() {
        return intValue() == ENDQUOTE_VALUE;
    }

    public boolean isExtendToCancel() {
        return intValue() == EXTEND_VALUE;
    }

    public boolean isGenCoiHold() {
        return intValue() == GENCOIHOLD_VALUE;
    }

    public boolean isGiveDisc() {
        return intValue() == GIVE_DISC_VALUE;
    }

    public boolean isManualRenewal() {
        return intValue() == MANRENEW_VALUE;
    }

    public boolean isNewBus() {
        return intValue() == NEWBUS_VALUE;
    }

    public boolean isOccpCancel() {
        return intValue() == OCCPCANCEL_VALUE;
    }

    public boolean isOosEndorsement() {
        return intValue() == OOSENDORSE_VALUE;
    }

    public boolean isPurge() {
        return intValue() == PURGE_VALUE;
    }

    public boolean isQuest() {
        return intValue() == QUEST_VALUE;
    }

    public boolean isQuester() {
        return intValue() == QUESTER_VALUE;
    }

    public boolean isQuester2() {
        return intValue() == QUESTER2_VALUE;
    }

    public boolean isQuester3() {
        return intValue() == QUESTER3_VALUE;
    }

    public boolean isQuestpt() {
        return intValue() == QUESTPT_VALUE;
    }

    public boolean isQuestpt2() {
        return intValue() == QUESTPT2_VALUE;
    }

    public boolean isQuestpt3() {
        return intValue() == QUESTPT3_VALUE;
    }

    public boolean isQuestxsh() {
        return intValue() == QUESTXSH_VALUE;
    }

    public boolean isQuestxsh2() {
        return intValue() == QUESTXSH2_VALUE;
    }

    public boolean isQuestxsi() {
        return intValue() == QUESTXSI_VALUE;
    }

    public boolean isQuote() {
        return intValue() == QUOTE_VALUE;
    }

    public boolean isReinstate() {
        return intValue() == REINSTATE_VALUE;
    }

    public boolean isReissue() {
        return intValue() == REISSUE_VALUE;
    }

    public boolean isRelChg() {
        return intValue() == RELCHG_VALUE;
    }

    public boolean isRemovDisc() {
        return intValue() == REMOV_DISC_VALUE;
    }

    public boolean isRenCoiHold() {
        return intValue() == RENCOIHOLD_VALUE;
    }

    public boolean isRerate() {
        return intValue() == RERATE_VALUE;
    }

    public boolean isRiskCancel() {
        return intValue() == RISKCANCEL_VALUE;
    }

    public boolean isRiskReinst() {
        return intValue() == RISKREINST_VALUE;
    }

    public boolean isRrelCancel() {
        return intValue() == RRELCANCEL_VALUE;
    }

    public boolean isRRelRein() {
        return intValue() == RRELREIN_VALUE;
    }

    public boolean isScvgCancel() {
        return intValue() == SCVGCANCEL_VALUE;
    }

    public boolean isScvgReinst() {
        return intValue() == SCVGREINST_VALUE;
    }

    public boolean isSpHandling() {
        return intValue() == SPHANDLING_VALUE;
    }

    public boolean isTailAccept() {
        return intValue() == TLACCEPT_VALUE;
    }

    public boolean isTailActivate() {
        return intValue() == TLACTIVATE_VALUE;
    }

    public boolean isTailCancel() {
        return intValue() == TLCANCEL_VALUE;
    }

    public boolean isTailDecline() {
        return intValue() == TLDECLINE_VALUE;
    }

    public boolean isTailDeclins() {
        return intValue() == TLDECLINS_VALUE;
    }

    public boolean isTailEndorse() {
        return intValue() == TLENDORSE_VALUE;
    }

    public boolean isTailReinstate() {
        return intValue() == TLREINST_VALUE;
    }

    public boolean isUndoTerm() {
        return intValue() == UNDOTERM_VALUE;
    }

    public boolean isXsnbRate() {
        return intValue() == XSNBRATE_VALUE;
    }

    public boolean isXsRerate() {
        return intValue() == XSRERATE_VALUE;
    }

    public boolean isEndQtStat() {
        return intValue() == ENDQTSTAT_VALUE;
    }
    
    public boolean isConvCovg() {
        return intValue() == CONVCOVG_VALUE;
    }

    private TransactionCode(int value, String name) {
        super(value, name);
        c_validTypes.put(name, this);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public TransactionCode() {
    }
}
