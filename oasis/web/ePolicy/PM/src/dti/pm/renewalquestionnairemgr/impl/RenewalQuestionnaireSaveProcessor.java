package dti.pm.renewalquestionnairemgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/** Save Processor for save all mailing's questionnaires.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 02, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RenewalQuestionnaireSaveProcessor {

    /**
     * Update the mailing changed comment.
     *
     * @param inputRecord
     */
    public void updateMailingEventComment(Record inputRecord);

     /**
     * Update the mailing event questionnaire status.
     *
     * @param inputRecord
     */
    public void updateMailingQuestionnaireStatus(Record inputRecord);
}
