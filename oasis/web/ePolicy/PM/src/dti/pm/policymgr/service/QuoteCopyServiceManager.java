package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyRequestType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/23/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public interface QuoteCopyServiceManager {

    /**
     * The main entrance for quote copy web service.
     * @param quoteCopyRequest
     * @param quoteCopyResult
     * @return
     */
    public MedicalMalpracticeQuoteCopyResultType doCopyToNewQuote(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult);

    /**
     * In transactional, copy to a new quote then return the new quote information in quoteCopyResult.
     * @param quoteCopyRequest
     * @param quoteCopyResult
     * @return
     */
    public String performCopy(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult);

    /**
     * In transactional, invoke PolicyChangeWebService then do action upon the value of ActionCode node provided.
     * @param quoteCopyRequest
     * @param quoteCopyResult
     */
    public void performPolicyChanges(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult);
}
