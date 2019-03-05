package dti.ci.entitymgr.service;

import com.delphi_tech.ows.partyinquiryservice.*;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/17/2015
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
public class PartyInquiryRequestBuilder {
    private PartyInquiryRequestBuilder() {}

    public static PartyInquiryRequestBuilder getInstance() {
        return new PartyInquiryRequestBuilder();
    }

    public PartyInquiryRequestType build() {
        Logger l = LogUtils.enterLog(getClass(), "build");

        PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();
        partyInquiryRequest.setCorrelationId(m_correlationId);
        partyInquiryRequest.setMessageId(m_messageId);
        partyInquiryRequest.setUserId(m_userId);

        for (PartyType party: m_partyList) {
            if (party != null) {
                PartyInquiryType partyInquiry = new PartyInquiryType();
                partyInquiry.setParty(party);

                PartyInquiryRequestParametersType partyInquiryRequestParameters = new PartyInquiryRequestParametersType();
                partyInquiryRequestParameters.setPartyInquiry(partyInquiry);

                partyInquiryRequest.getPartyInquiryRequestParameters().add(partyInquiryRequestParameters);
            }
        }

        if (m_viewNames.size() > 0) {
            PartyInquiryResultParametersType partyInquiryResultParameters = new PartyInquiryResultParametersType();
            partyInquiryRequest.setPartyInquiryResultParameters(partyInquiryResultParameters);

            for (String viewName: m_viewNames) {
                if (!StringUtils.isBlank(viewName)) {
                    partyInquiryResultParameters.getViewName().add(viewName);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "build", partyInquiryRequest);
        }

        return partyInquiryRequest;
    }

    public PartyInquiryRequestBuilder setCorrelationId(String correlationId) {
        m_correlationId = correlationId;
        return this;
    }

    public PartyInquiryRequestBuilder setMessageId(String messageId) {
        m_messageId = messageId;
        return this;
    }

    public PartyInquiryRequestBuilder setUserId(String userId) {
        m_userId = userId;
        return this;
    }

    public PartyInquiryRequestBuilder addParty(Collection<PartyType> parties) {
        m_partyList.addAll(parties);
        return this;
    }

    public PartyInquiryRequestBuilder addParty(PartyType party) {
        m_partyList.add(party);
        return this;
    }

    public PartyInquiryRequestBuilder addViewName(Collection<String> viewNames) {
        m_viewNames.addAll(viewNames);
        return this;
    }

    public PartyInquiryRequestBuilder addViewName(String viewName) {
        m_viewNames.add(viewName);
        return this;
    }

    private String m_correlationId;
    private String m_messageId;
    private String m_userId;
    private List<PartyType> m_partyList = new ArrayList<PartyType>();
    private Set<String> m_viewNames = new HashSet<String>();

    public static class SimplePartyBuilder {
        private SimplePartyBuilder() {}

        public static PartyType buildWithPartyNumberId(String partyNumberId) {
            Logger l = LogUtils.getLogger(SimplePartyBuilder.class);
            if (l.isLoggable(Level.FINER)) {
                l.entering(SimplePartyBuilder.class.getName(), "buildWithPartyNumberId", new Object[]{partyNumberId});
            }

            PartyType party = null;

            if (!StringUtils.isBlank(partyNumberId)) {
                party = new PartyType();
                party.setPartyNumberId(partyNumberId);
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(SimplePartyBuilder.class.getName(), "buildWithPartyNumberId", party);
            }

            return party;
        }

        public static List<PartyType> buildWithPartyNumberIds(Collection<String> partyNumberIds) {
            Logger l = LogUtils.getLogger(SimplePartyBuilder.class);
            if (l.isLoggable(Level.FINER)) {
                l.entering(SimplePartyBuilder.class.getName(), "buildWithPartyNumberIds", new Object[]{partyNumberIds});
            }

            List<PartyType> partyList = new ArrayList<PartyType>();

            if (partyNumberIds != null && partyNumberIds.size() > 0) {
                for (String partyNumberId : partyNumberIds) {
                    if (!StringUtils.isBlank(partyNumberId)) {
                        partyList.add(buildWithPartyNumberId(partyNumberId));
                    }
                }
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(SimplePartyBuilder.class.getName(), "buildWithPartyNumberIds", partyList);
            }

            return partyList;
        }
    }
}
