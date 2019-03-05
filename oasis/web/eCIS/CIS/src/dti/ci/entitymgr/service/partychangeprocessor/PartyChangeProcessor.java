package dti.ci.entitymgr.service.partychangeprocessor;

import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/18/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PartyChangeProcessor {
    String ENTITY_TYPE_PERSON = "person";
    String ENTITY_TYPE_ORGANIZATION = "organization";

    /**
     * Process person
     * @param partyChangeRequest
     * @param partyChangeResult
     */
    void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult);
}
