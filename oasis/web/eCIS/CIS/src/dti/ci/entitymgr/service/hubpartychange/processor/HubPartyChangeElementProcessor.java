package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;

import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2016
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
public interface HubPartyChangeElementProcessor<T> {
    /**
     * Process entity elements.
     *
     * @param partyChangeRequest
     * @param cisResultElements
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<T> cisResultElements,
                 String entityType, String entityId, List<T> changedElements, List<T> originalElements);

    /**
     * Process entity elements for HUB if successfully changed in CIS
     *
     * @param partyChangeRequest
     * @param cisResultElements
     * @param entityId
     * @param changedElements
     */
    void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<T> cisResultElements,
                              String entityId, List<T> changedElements);

    /**
     * Process entity elements for HUB if failed to change in CIS
     *
     * @param partyChangeRequest
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    void processForHub(PartyChangeRequestType partyChangeRequest,
                       String entityType, String entityId, List<T> changedElements, List<T> originalElements);

}
