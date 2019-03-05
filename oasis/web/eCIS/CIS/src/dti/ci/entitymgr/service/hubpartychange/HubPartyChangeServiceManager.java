package dti.ci.entitymgr.service.hubpartychange;

import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2016
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface HubPartyChangeServiceManager {

    void saveParty(PartyChangeResultType partyChangeResult, PartyChangeRequestType partyChangeRequest);

}
