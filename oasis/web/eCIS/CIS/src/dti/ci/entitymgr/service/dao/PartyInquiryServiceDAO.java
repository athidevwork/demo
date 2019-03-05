package dti.ci.entitymgr.service.dao;

import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.oasis.ows.util.FilterView;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 25, 2012
 * Time: 1:02:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PartyInquiryServiceDAO {
    public PartyInquiryResultType loadPartyInquiryResult(Hashtable conditionList, String asOfDate, String sourceSystem, Map<String, String> filterStringMap);
}

