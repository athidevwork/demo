package dti.pm.busobjs;

/**
 * This interface holds common system parameter Ids and default values used by Business component.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 23, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/02/2007       fcb         PM_CHG_PRIMARY_RISK added.
 * Apr 17, 2008     James       Issue#75265 add Agent tab to eCIS
 * 04/25/2008       fcb         PM_QT_CM_OCC_CONV added.
 * 12/09/2008       sxm         issue 89002 - added PM_AUTO_CEEATE_TAIL
 * 03/31/2009       msn         91735 Add PM_ACCEPT_AGAIN
 * 11/09/2010       dzhang      114209 Add PM_OOS_RENEW_RISKREL.
 * 01/21/2011       wfu         113566 - Added PM_CONVERT_DEL_WIP
 * 02/18/2011       wfu         113063 - Added PM_QT_STATUS_CHG_FRM
 * 03/25/2011       dzhang      113062 - Added PM_TAIL_CXL_ANY_TERM.
 * 04/12/2011       wqfu        118226 - Added PM_LAYER_DETAIL
 * 06/27/2011       ryzhao      103808 - Added PM_CHECK_NOTICE
 * 01/04/2012       wfu         127802 - Added PM_COI_CS_SEARCH
 * 12/28/2012       awu         140186 - Added OS_CHECK_REMINDER
 * 02/01/2013       tcheng      141447 - Added PM_AUTO_VIEW_PREMIUM
 * 06/11/2013       fcb         145501 - Added COMMON.
 * 01/24/2013       awu         148187 - Added PM_TRANSFER_DIV
 * 01/11/2016       tzeng       166924 - Added PM_EDIT_POLICY_RETRO.
 * 04/14/2016       wdang       170793 - Added PM_UNDERLYING_TERM.
 * 06/23/2016       tzeng       167531 - Added PM_AUTOREN_BAT_SCHED.
 * 06/28/2016       wdang       167534 - Added PM_QTE_NO_FROM_POL.
 * 10/25/2016       tzeng       180688 - Added PM_COPY2QUOTE_RN.
 * 02/28/2017       mlm         183387 - Added ODS_INSTALLED
 * 03/10/2016       mlm         183481 - Moved ODS_INSTALLED to CS SysParmIds.
 * 03/09/2017       tzeng       166929 - Added PM_REC_SOFT_VALID, PM_CHECK_SOFT_VAL_B.
 * 05/05/2017       ssheng      185360 - Added PM_NB_QUICK_QUOTE.
 * 08/17/2017       wrong       187776 - Added PM_USE_RR_PCF_FLDS.
 * 11/19/2018       xnie        196983 - Added PM_TAX_MT_OPEN_EXP.
 * ---------------------------------------------------
 */

public interface SysParmIds extends dti.cs.core.busobjs.SysParmIds {
    public static final String PM_ACCEPT_COLLATERAL="PM_ACCEPT_COLLATERAL";
    public static final String PM_ADD_COMPONENT_DT = "PM_ADD_COMPONENT_DT";

    public static final String PM_CHK_ACCT_DATE = "PM_CHK_ACCT_DATE";
    public static final String PM_VALD_ACCT_DATE = "PM_VALD_ACCT_DATE";

    public static final String PM_CHECK_EXCESS_PY = "PM_CHECK_EXCESS_PY";

    public static final String PM_MIN_RETRO_DATE = "PM_MIN_RETRO_DATE";

    public static final String SET_RETRO_DATE = "SET_RETRO_DATE";
    public static final String PM_CHECK_EXCESS = "PM_CHECK_EXCESS";

    public static final String PM_SUBCOVG_ADDL1 = "PM_SUBCOVG_ADDL1";
    public static final String PM_SUBCOVG_ADDL2 = "PM_SUBCOVG_ADDL2";
    public static final String PM_SUBCOVG_ADDL3 = "PM_SUBCOVG_ADDL3";
    public static final String PM_SUBCOVG_ADDL_ISO_CUST_CODE = "ISO_CUST_CODE";
    public static final String PM_SUBCOVG_ADDL_EXPOSURE_BASIC_NO = "EXPOSURE_BASIC_NO";

    public static final String PM_ADD_COVG_CLASS_DT = "PM_ADD_COVG_CLASS_DT";

    public static final String PM_VAL_LOCUM_CNT = "PM_VAL_LOCUM_CNT";
    public static final String PM_VAL_LOCUM_DURATN = "PM_VAL_LOCUM_DURATN";
    public static final String CHK_POLSCHD_OVRLP = "PM_CHK_POLSCHD_OVRLP";
    public static final String SCHD_OVRLP_NOENT = "PM_SCHD_OVRLP_NOENT";

    public static final String PM_PENDING_RENEWAL = "PM_PENDING_RENEWAL";

    public static final String PM_GIVE_COUNTY_WRNNG = "PM_GIVE_COUNTY_WRNNG";

    public static final String PM_CVGCMPCD_MORE_BTN = "PM_CVGCMPCD_MORE_BTN";

    public static final String PM_OOS_DEF_EXP_DATE = "PM_OOS_DEF_EXP_DATE";

    public static final String PM_OOS_DEF_CCLEXP_DT = "PM_OOS_DEF_CCLEXP_DT";
    public static final String PM_NO_TAIL_REASONS = "PM_NO_TAIL_REASONS";
    public static final String PM_ACTIVE_TAIL_MSG = "PM_ACTIVE_TAIL_MSG";
    public static final String PM_REINST_COUNT = "PM_REINST_COUNT";
    public static final String PM_AUTO_CEEATE_TAIL = "PM_AUTO_CREATE_TAIL";

    public static final String PM_COI_WIP_TRANS = "PM_COI_WIP_TRANS";
    public static final String PM_COI_CLAIMS = "PM_COI_CLAIMS";

    public static final String PM_ENT_PREM_CONTRIB = "PM_ENT_PREM_CONTRIB";
    public static final String PM_LAYER_DETAIL = "PM_LAYER_DETAIL";

    public static final String PM_SHOW_TAIL_ERR_SCN = "PM_SHOW_TAIL_ERR_SCN";
    public static final String FM_TAIL_FIN_CHARGE =  "FM_TAIL_FIN_CHARGE";
    public static final String PM_DECLINE_TAIL_RESN =  "PM_DECLINE_TAIL_RESN";

    public static final String PM_VAL_MANEND_DATES = "PM_VAL_MANEND_DATES";
    public static final String PM_ALLOW_DUP_MANUSPT = "PM_ALLOW_DUP_MANUSPT";

    public static final String PM_RENEW_DFLT_ALL = "PM_RENEW_DFLT_ALL";
    public static final String PM_AUTO_REN_MAX_DAYS = "PM_AUTO_REN_MAX_DAYS";
    public static final String PM_SYNCHR_BATCH_REN = "PM_SYNCHR_BATCH_REN";
    public static final String PRERENEWAL = "PRERENEWAL";
    public static final String NON_COMMON = "NON_COMMON";
    public static final String COMMON = "COMMON";
    public static final String PM_REN_EVT_TERM_DFLT = "PM_REN_EVT_TERM_DFLT";
    public static final String PM_REINRTR_USE_PTHDT="PM_REINRTR_USE_PTHDT";
    public static final String PM_RSKREL_PI_ALLWCAN = "PM_RSKREL_PI_ALLWCAN";

    public static final String PM_CHG_PRIMARY_RISK = "PM_CHG_PRIMARY_RISK";
    public static final String PM_FTE_BY_ENTITY = "PM_FTE_BY_ENTITY";
    public static final String PM_VIEW_RELATED_POLS= "PM_VIEW_RELATED_POLS";
    public static final String PM_ADDLINS_WIP_TRANS= "PM_ADDLINS_WIP_TRANS";
    public static final String PM_FTE_VAL_EXPDATE = "PM_FTE_VAL_EXPDATE";

    public static final String PM_RREL_VALIDATION = "PM_RREL_VALIDATION";
    public static final String PM_USE_COMP_INSURED = "PM_USE_COMP_INSURED";
    public static final String PM_ENTER_NINS_PREM = "PM_ENTER_NINS_PREM";
    public static final String PM_NONINS_DEF_COUNTY = "PM_NONINS_DEF_COUNTY";
    public static final String PM_SHARE_DEDUCT_LEV = "PM_SHARE_DEDUCT_LEV";
    public static final String PM_NONINS_RISKTYPE = "PM_NONINS_RISKTYPE";
    public static final String PM_MIN_NOSE_DATE = "PM_MIN_NOSE_DATE";
    public static final String PM_CHECK_SIMILAR_COVG = "PM_CHK_SIMILAR_COVG";
    public static final String PM_ALLOW_RD_GAP = "PM_ALLOW_RD_GAP";
    public static final String PM_MLNG_WARN_DAYS="PM_MLNG_WARN_DAYS";

    public static final String PM_CISCOI_POPUP_DT = "PM_CISCOI_POPUP_DT";
    public static final String PM_QT_CM_OCC_CONV = "PM_QT_CM_OCC_CONV";

    public static final String NEW_BUS_RATE_MAX = "NEW BUS. RATE MAX";
    public static final String RENEWAL_RATE_MAX = "RENEWAL RATE MAX";
    public static final String ERE_RATE_MAX = "ERE RATE MAX";
    public static final String PM_VAL_AFFI_DATES="PM_VAL_AFFI_DATES";

    public static final String PM_PREM_CLASS_EFF_DT = "PM_PREM_CLASS_EFF_DT";
    public static final String PM_VL_RISK_TYPE = "PM_VL_RISK_TYPE";
    public static final String PM_SHR_GRPS_NODELDTL = "PM_SHR_GRPS_NODELDTL";

    public static final String PM_ACCEPT_AGAIN = "PM_ACCEPT_AGAIN";

    public static final String PM_WEB_URL = "PM_WEB_URL";
    
    public static final String AUTORENEW = "AUTORENEW";
    public static final String PM_MAN_XS_DYN_SQL = "PM_MAN_XS_DYN_SQL";

    public static final String PM_SET_BILLING_COVG = "PM_SET_BILLING_COVG";
    public static final String PM_OOS_RENEW_RREL = "PM_OOS_RENEW_RREL";

    public static final String PM_CONVERT_DEL_WIP = "PM_CONVERT_DEL_WIP";

    public static final String PM_QT_STATUS_CHG_FRM = "PM_QT_STATUS_CHG_FRM";
    public static final String PM_TAIL_CXL_ANY_TERM = "PM_TAIL_CXL_ANY_TERM";

    public static final String PM_REINST_IBNR_RISK = "PM_REINST_IBNR_RISK";

    public static final String PM_CHECK_NOTICE = "PM_CHECK_NOTICE";

    public static final String PM_COI_CS_SEARCH = "PM_COI_CS_SEARCH";

    public static final String PM_OS_CHECK_REMINDER = "OS_CHECK_REMINDER";

    public static final String PM_AUTO_VIEW_PREMIUM = "PM_AUTO_VIEW_PREMIUM";
    
    public static final String PM_TRANSFER_DIVIDEND = "PM_TRANSFER_DIV";

    public static final String PM_PROPERTY_POL_TYPE = "PM_PROPERTY_POL_TYPE";

    public static final String PM_EDIT_POLICY_RETRO = "PM_EDIT_POLICY_RETRO";
    public static final String PM_UNDERLYING_TERM = "PM_UNDERLYING_TERM";
    public static final String PM_AUTOREN_BAT_SCHED = "PM_AUTOREN_BAT_SCHED";
    public static final String PM_QTE_NO_FROM_POL = "PM_QTE_NO_FROM_POL";
    public static final String PM_COPY2QUOTE_RN = "PM_COPY2QUOTE_RN";
    public static final String PM_REC_SOFT_VALID = "PM_REC_SOFT_VALID";
    public static final String PM_CHECK_SOFT_VAL_B = "PM_CHECK_SOFT_VAL_B";
    public static final String PM_NB_QUICK_QUOTE = "PM_NB_QUICK_QUOTE";
    public static final String PM_USE_RR_PCF_FLDS = "PM_USE_RR_PCF_FLDS";
    public static final String PM_TAX_MT_OPEN_EXP = "PM_TAX_MT_OPEN_EXP";

    public class AddComponentDateValues {
        public static final String TRANS = "TRANS";
        public static final String TERM = "TERM";
    }

    public class AddCovgClassDateValues {
        public static final String TRANS = "TRANS";
        public static final String TERM = "TERM";
    }

    public class OosDefaultExpDateValues {
        public static final String TERM = "TERM";
        public static final String POLICY = "POLICY";
    }

    public class MinRetroDateValues {
        public static final String DEFAUL_OPEN_DATE = "01/01/1900";
    }

    public class RenewDefaultAllValues {
        public static final String DEFAULT = "Y";
    }

    public class AutoRenewMaxDayValues {
        public static final String DEFAULT = "0";
    }

    public class NewBusRateMaxValues {
        public static final String DEFAULT = "20";
    }

    public class RenewalRateMaxValues {
        public static final String DEFAULT = "20";
    }

    public class EreRateMaxValues {
        public static final String DEFAULT = "20";
    }
}
