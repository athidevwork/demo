package dti.pm.transactionmgr.transaction;

import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.TransactionCode;

/**
 * This class represents a transaction bean with information about a given transaction.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 3, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  02/12/2009      Bhong  Added new attribute "transEffectiveToDate". Issue#86047
 *  05/01/2011      fcb    105791 - added convertionType
 *  06/21/2013      adeng  117011 - Added transactionComment2 & transactionComments2, and added getter/setter methods
 *                                  for them.
 * ---------------------------------------------------
 */
public class Transaction implements Cloneable {

    public String getPolicyId() {
        return m_policyId;
    }

    public void setPolicyId(String policyId) {
        m_policyId = policyId;
    }

    public String getTransactionLogId() {
        return m_transactionLogId;
    }

    public void setTransactionLogId(String transactionLogId) {
        m_transactionLogId = transactionLogId;
    }

    public String getTransEffectiveFromDate() {
        return m_transEffectiveFromDate;
    }

    public void setTransEffectiveFromDate(String transEffectiveFromDate) {
        m_transEffectiveFromDate = transEffectiveFromDate;
    }

    public String getTransEffectiveToDate() {
        return m_transEffectiveToDate;
    }

    public void setTransEffectiveToDate(String transEffectiveToDate) {
        m_transEffectiveToDate = transEffectiveToDate;
    }

    public TransactionCode getTransactionCode() {
        return m_transactionCode;
    }

    public void setTransactionCode(TransactionCode transactionCode) {
        m_transactionCode = transactionCode;
    }

    public TransactionStatus getTransactionStatusCode() {
        return m_transactionStatusCode;
    }

    public void setTransactionStatusCode(TransactionStatus transactionStatusCode) {
        m_transactionStatusCode = transactionStatusCode;
    }

    public TransactionTypeCode getTransactionTypeCode() {
        return m_transactionTypeCode;
    }

    public void setTransactionTypeCode(TransactionTypeCode transactionTypeCode) {
        m_transactionTypeCode = transactionTypeCode;
    }

    public String getTransAccountingDate() {
        return m_transAccountingDate;
    }

    public void setTransAccountingDate(String transAccountingDate) {
        m_transAccountingDate = transAccountingDate;
    }

    public String getEndorsementCode() {
        return m_endorsementCode;
    }

    public void setEndorsementCode(String endorsementCode) {
        m_endorsementCode = endorsementCode;
    }

    public String getTransactionComments() {
        return m_transactionComments;
    }

    public void setTransactionComments(String transactionComments) {
        m_transactionComments = transactionComments;
    }

    public String getTransactionComments2() {
        return m_transactionComments2;
    }

    public void setTransactionComments2(String transactionComments2) {
        m_transactionComments2 = transactionComments2;
    }

    public String getTransactionComment2() {
        return m_transactionComment2;
    }

    public void setTransactionComment2(String transactionComment2) {
        m_transactionComment2 = transactionComment2;
    }

    public String getEndorsementQuoteId() {
        return m_endorsementQuoteId;
    }

    public void setEndorsementQuoteId(String endorsementQuoteId) {
        m_endorsementQuoteId = endorsementQuoteId;
    }

    public String getConvertionType() {
        return m_convertionType;
    }

    public void setConvertionType(String convertionType) {
        m_convertionType = convertionType;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.pm.transactionmgr.transaction.Transaction");
        buf.append(",transactionLogId=").append(getTransactionLogId());
        buf.append(",effectiveFromDate=").append(getTransEffectiveFromDate());
        buf.append(",effectiveToDate=").append(getTransEffectiveToDate());
        buf.append(",accountingDate=").append(getTransAccountingDate());
        buf.append(",transactionCode=").append(getTransactionCode());
        buf.append(",transactionStatusCode=").append(getTransactionStatusCode());
        buf.append(",transactionCode=").append(getTransactionCode());
        buf.append(",transactionTypeCode=").append(getTransactionTypeCode());
        buf.append(",endorsementCode=").append(getEndorsementCode());
        buf.append(",convertionType=").append(getEndorsementCode());
        buf.append('}');
        return buf.toString();
    }

    private String m_policyId;
    private String m_transactionLogId;
    private String m_transEffectiveFromDate;
    private String m_transEffectiveToDate;
    private String m_transAccountingDate;
    private TransactionCode m_transactionCode;
    private String m_endorsementCode;
    private String m_transactionComments;
    private String m_transactionComment2;
    private String m_transactionComments2;
    private TransactionStatus m_transactionStatusCode;
    private TransactionTypeCode m_transactionTypeCode;
    private String m_endorsementQuoteId;
    private String m_convertionType;
}
