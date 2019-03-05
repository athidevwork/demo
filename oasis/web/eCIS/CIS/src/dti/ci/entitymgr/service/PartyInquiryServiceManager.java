package dti.ci.entitymgr.service;

import com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/2/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PartyInquiryServiceManager {
    public static final String VIEW_NAME_PARTY = "Party";
    public static final String VIEW_NAME_BASIC_PHONE_NUMBER = "BasicPhoneNumber";
    public static final String VIEW_NAME_ADDRESS = "Address";

    public PartyInquiryResultType loadParty(PartyInquiryRequestType partyInquiryRequest);
}
