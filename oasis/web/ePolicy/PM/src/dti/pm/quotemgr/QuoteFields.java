package dti.pm.quotemgr;

 /**
 * Field constants of Policy Summary Fields.
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * ---------------------------------------------------
 */
 public class QuoteFields {
     public static final String SELECT_MODE = "selectMode";
     public static final String EXCL_SELF = "EXCL_SELF";
     public static final String EXCL_POL = "EXCL_POL";
     public static final String EXCL_NB = "EXCL_NB";
     public static final String EXCL_RN = "EXCL_RN";
     public static final String EXCL_INVALID = "EXCL_INVALID";
     public static final String EXCL_ACCEPTED = "EXCL_ACCEPTED";
     public static final String TERM_SENS = "TERM_SENS";
     public static final String DISP_PREM = "DISP_PREM";
     public static final String TRANSFER_STATUS = "transferStatus";
     public static final String TRANSACTION_LOG_ID = "transactionLogId";
     public static final String QUOTE_ID = "quoteId";
     public static final String POLICY_ID = "policyId";
     public static final String POLICY_NO = "policyNo";
     public static final String PARALLEL_POL_NO = "parallelPolNo";
     public static final String SUCCESS = "Success";
     public static final String FAILURE = "Failure";

     public static final String joinSelectMode(String ... selectMode) {
         return String.join(",", selectMode);
     }
 }
