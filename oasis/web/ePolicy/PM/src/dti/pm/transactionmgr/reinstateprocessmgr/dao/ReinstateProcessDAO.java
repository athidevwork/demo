package dti.pm.transactionmgr.reinstateprocessmgr.dao;

import dti.oasis.recordset.Record;

/**
 * An interface that provides DAO operation for Reinstate information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 20, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ReinstateProcessDAO {

    /**
     * to check if the entity is solo owner
     *
     * @param inputRecord intput record
     * @return String value to identifying Term
     */
    String identifyingTerm(Record inputRecord);

    /**
     * to check if the Policy prompt
     *
     * @param inputRecord intput record
     * @return String value to Policy prompt
     */
    String isPolicyprompt(Record inputRecord);

    /**
     * to check Active Reinstate
     *
     * @param inputRecord intput record
     * @return String value to get Active Reinstate
     */
    String validateActiveReinstate(Record inputRecord);

    /**
     * to check solo owner
     *
     * @param inputRecord intput record
     * @return String value to get Solo Owner Reinstate
     */
    Long validateSoloOwnerReinstate(Record inputRecord);

    /**
     * to check Custom Reinstate
     *
     * @param inputRecord intput record
     * @return Record
     */
    Record validateCustomReinstate(Record inputRecord);

    /**
     * to get perform Reinstate
     *
     * @param inputRecord intput record
     * @return Record value get perform Reinstate
     */
    Record performReinstate(Record inputRecord);

    /**
     * to perform reinstate for risk relationship
     *
     * @param inputRecord
     * @return
     */
    Record performRiskRelationReinstate(Record inputRecord);
}