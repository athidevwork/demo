package dti.ci.entitymgr.service.partychangeprocessor;

import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;

import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/18/14
 *
 * @author kshen
// */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PartyChangeElementProcessor <T> {
    /**
     * Process entity elements.
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    void process(PartyChangeRequestType partyChangeRequest , PartyChangeResultType partyChangeResult,
                 String entityType, String entityId,
                 List<T> changedElements, List<T> originalElements);

    /**
     * Process entity element.
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param entityType
     * @param entityId
     * @param changedElement
     * @param originalElement
     */
    void process(PartyChangeRequestType partyChangeRequest , PartyChangeResultType partyChangeResult,
                 String entityType, String entityId,
                 T changedElement, T originalElement);
}
