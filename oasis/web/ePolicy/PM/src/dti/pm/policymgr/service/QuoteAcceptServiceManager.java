package dti.pm.policymgr.service;

import com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptRequestType;
import com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   02/01/2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface QuoteAcceptServiceManager {
    public MedicalMalpracticeQuoteAcceptResultType quoteAccept(MedicalMalpracticeQuoteAcceptRequestType quoteAcceptResult);
}
