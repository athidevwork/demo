package dti.ci.entitymgr.service.impl;

import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.partyinquiryservice.*;
import dti.ci.addressmgr.addresslistmgr.AddressListManager;
import dti.ci.contactmgr.ContactManager;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymgr.impl.jdbchelpers.ViewFilter;
import dti.ci.entitymgr.service.PartyInquiryServiceFields;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.ci.entitymgr.service.dao.PartyInquiryServiceDAO;
import dti.ci.phonemgr.PhoneListManager;
import dti.ci.propertymgr.PropertyManager;
import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FilterView;
import dti.oasis.ows.util.FilterViewFactory;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.*;
import dti.ows.common.MessageStatusHelper;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
 * 06/10/2013       ldong       issue 145682
 * 01/06/2014       kshen       Issue 148789. Corrected getIntersection.
 * 01/27/2014       bzhu        issue 148789. Refactored to include party note and relationship for party inquiry service.
 * 06/20/2014       kshen       Issue 154676. Changes for relationship.
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 04/25/2016       Elvin       Issue 149588: Add cis hub logic
 * 05/24/2016       Elvin       Issue 176524: add searchEntityForWS
 * 01/31/2018       Elvin       Issue 190997: exclude duplicated search conditions
 * 01/31/2018       Elvin       Issue 190210: support load party by classification code
 * 06/07/2018       mproekt     Issue 193752. Add check and set value for AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE  in a session if missing
 * ---------------------------------------------------
 */
public class PartyInquiryServiceManagerImpl implements PartyInquiryServiceManager {
    public final static QName _PartyInquiryRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PartyInquiryService", "PartyInquiryRequest");
    public final static QName _PartyInquiryResult_QNAME = new QName("http://www.delphi-tech.com/ows/PartyInquiryService", "PartyInquiryResult");

/*
    The following map is a collection of the define views.
    the key is view name and the value is object of type dti.ci.entitymgr.impl.jdbchelpers.ViewFilter

     */
    private static Map<String, ViewFilter> views = new HashMap();

    static {
        /*
            View processing specification consists of map views that contains collection of ViewFilter classes.
            Each view should have name that is a map key. Each ViewFilter contains Strings of each filter (Person, Organization, etc.)
            Each filter condition is an exclusion of the specified element from the result.
        */
        //  Party View    -- Person/Organization/Property with email addresses (no phones or addresses)

        ViewFilter filter = new ViewFilter();
        filter.setAddressFilter("'|AddressType|'"); //exclude address type from the view
        filter.setPersonFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|EducationInformationType|ProfessionalLicenseType|CertificationType|ContactType|PartyNoteType|RelationshipType|PartyClassificationType|'");
        filter.setOrganizationFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|OrganizationLicenseType|CertificationType|PartyNoteType|RelationshipType|PartyClassificationType|'");
        views.put("Party", filter);

        //  Address View
        filter = new ViewFilter();
        filter.setPersonFilter("'|BusinessEmailType|BasicPhoneNumberType|EducationInformationType|ProfessionalLicenseType|CertificationType|ContactType|PartyNoteType|RelationshipType|PartyClassificationType|'");
        filter.setOrganizationFilter("'|BusinessEmailType|BasicPhoneNumberType|OrganizationLicenseType|CertificationType|PartyNoteType|RelationshipType|PartyClassificationType|'");
        views.put("Address", filter);

//        //  PersonOrganizationOnly View    -- This filter will not return any property data and will restricted to only PersonType and/or OrganizationType.
//        filter = new ViewFilter();
//        filter.setAddressFilter("'|AddressType|'"); //exclude address type from the view
//        filter.setPropertyFilter("'|PropertyType|'");
//        filter.setPersonFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|EducationInformationType|ProfessionalLicenseType|CertificationType|ContactType|'");
//        filter.setOrganizationFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|OrganizationLicenseType|CertificationType|'");
//        views.put("PersonOrganizationOnly", filter);
//
//        //  PropertyOnly View
//        filter = new ViewFilter();
//        filter.setAddressFilter("'|AddressType|'");
//        filter.setPersonFilter("'|PersonType|'");
//        filter.setOrganizationFilter("'|OrganizationType|'");
//        views.put("PropertyOnly", filter);
//
//        //  PersonOrganizationAddress View
//        filter = new ViewFilter();
//        filter.setPropertyFilter("'|PropertyType|'");
//        filter.setPersonFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|EducationInformationType|ProfessionalLicenseType|CertificationType|ContactType|'");
//        filter.setOrganizationFilter("'|BusinessEmailType|BasicPhoneNumberType|BasicAddressType|OrganizationLicenseType|CertificationType|'");
//        views.put("PersonOrganizationAddress", filter);

    }

    /**
     * Load Party
     *
     * @param partyInquiryRequest
     * @return
     */
    @Override
    public PartyInquiryResultType loadParty(PartyInquiryRequestType partyInquiryRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadParty", new Object[]{partyInquiryRequest});
        }
        //Need to set access time if it was not set. the call could come from non web service calls and the time would not recorded in a session

        RequestStorageManager requestManager = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) requestManager.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        if(request.getAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE)==null){
            request.setAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE, new Date());
        }
        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(partyInquiryRequest, _PartyInquiryRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                    partyInquiryRequest.getMessageId(), partyInquiryRequest.getCorrelationId(), partyInquiryRequest.getUserId(), _PartyInquiryRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "loadParty", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(partyInquiryRequest, _PartyInquiryRequest_QNAME,
                    partyInquiryRequest.getMessageId(), partyInquiryRequest.getCorrelationId(), partyInquiryRequest.getUserId());
        }

        PartyInquiryResultType partyInquiryResult = null;
        try {
            partyInquiryResult = new PartyInquiryResultType();
            partyInquiryResult.setCorrelationId(partyInquiryRequest.getCorrelationId());
            partyInquiryResult.setMessageId(partyInquiryRequest.getMessageId());

            if (partyInquiryRequest.getPartyInquiryRequestParameters() != null && partyInquiryRequest.getPartyInquiryRequestParameters().size() > 0) {
                Hashtable<String, Object> conditionList = new Hashtable<String, Object>();
                conditionList.put("partyNumberIdList", "");
                conditionList.put("clientIdList", "");
                conditionList.put("fullNameList", "");
                conditionList.put("regexpFullNameList", "");
                conditionList.put("externalReferenceIdList", "");
                conditionList.put("externalDataIdList", "");
                conditionList.put("classificationCodeList", "");
                conditionList.put("complexConditionList", new ArrayList<Map<String, String>>());

                String sourceSystem = "";
                String asOfDate = "";

                List<String> partyNumberIdList = new ArrayList<>();
                List<String> clientIdList = new ArrayList<>();
                List<String> externalReferenceIdList = new ArrayList<>();
                List<String> externalDataIdList = new ArrayList<>();
                for (PartyInquiryRequestParametersType params : partyInquiryRequest.getPartyInquiryRequestParameters()) {
                    if (params.getPartyInquiry() != null && params.getPartyInquiry().getParty() != null) {

                        PartyType party = params.getPartyInquiry().getParty();

                        String partyNumberId = "";
                        String clientId = "";
                        String fullName = "";
                        String externalReferenceId = "";
                        String externalDataId = "";
                        String classificationCode = "";
                        Map<String, String> allParameters = new Hashtable<String, String>();

                        if (!StringUtils.isBlank(party.getPartyNumberId()) && !partyNumberIdList.contains(party.getPartyNumberId())) {
                            partyNumberId = party.getPartyNumberId();
                            partyNumberIdList.add(partyNumberId);
                            allParameters.put("partyNumberIdList", partyNumberId);
                        }

                        if (!StringUtils.isBlank(party.getClientId()) && !clientIdList.contains(party.getClientId())) {
                            clientIdList.add(party.getClientId());
                            clientId = "'" + StringUtils.replace(party.getClientId(), "'", "''") + "'";
                            allParameters.put("clientIdList", clientId);
                        }

                        PartyNameType partyName = party.getPartyName();
                        if (partyName != null) {
                            // Skip empty name filter and the name filter which only contains "*".
                            if (!StringUtils.isBlank(partyName.getFullName()) && !partyName.getFullName().matches("^(\\.\\*)*$")) {
                                if (partyName.getFullName().contains(".*")) {
                                    if (isRegexpValid(partyName.getFullName())) {
                                        fullName = "(" + StringUtils.replace(partyName.getFullName(), "'", "''") + ")";
                                        allParameters.put("regexpFullNameList", fullName);
                                    } else {
                                        MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                                                new Object[]{"Invalid full name regular expression: " + partyName.getFullName() + "."});
                                        throw new ValidationException("Invalid full name regular expression: " + partyName.getFullName());
                                    }
                                } else {
                                    fullName = "'" + StringUtils.replace(partyName.getFullName(), "'", "''") + "'";
                                    allParameters.put("fullNameList", fullName);
                                }
                            }
                        }

                        if (!StringUtils.isBlank(party.getExternalReferenceId()) && !externalReferenceIdList.contains(party.getExternalReferenceId())) {
                            externalReferenceIdList.add(party.getExternalReferenceId());
                            externalReferenceId = "'" + StringUtils.replace(party.getExternalReferenceId(), "'", "''") + "'";
                            allParameters.put("externalReferenceIdList", externalReferenceId);
                        }

                        if (!StringUtils.isBlank(party.getExternalDataId()) && !externalDataIdList.contains(party.getExternalDataId())) {
                            externalDataIdList.add(party.getExternalDataId());
                            externalDataId = "'" + StringUtils.replace(party.getExternalDataId(), "'", "''") + "'";
                            allParameters.put("externalDataIdList", externalDataId);
                        }

                        if (!StringUtils.isBlank(party.getClassificationCode())) {
                            classificationCode = "'" + StringUtils.replace(party.getClassificationCode(), "'", "''") + "'";
                            allParameters.put("classificationCodeList", classificationCode);
                        }

                        if (!StringUtils.isBlank(partyInquiryRequest.getAsOfDate())) {
                            try {
                                asOfDate = DateUtils.parseXMLDateToOasisDate(partyInquiryRequest.getAsOfDate());
                            } catch (Exception e) {
                                throw new AppException("ows.invalid.xml.date", "", new String[]{partyInquiryRequest.getAsOfDate()});
                            }
                        }

                        if (partyInquiryRequest.getSendingSystemInformation() != null &&
                                !StringUtils.isBlank(partyInquiryRequest.getSendingSystemInformation().getVendorProductName())) {
                            sourceSystem = partyInquiryRequest.getSendingSystemInformation().getVendorProductName();
                        }

                        if (allParameters.size() > 1) {
                            // Two or more parameters has been specified
                            ArrayList complexConditionList = (ArrayList)  conditionList.get("complexConditionList");

                            if (allParameters.containsKey("regexpFullNameList")) {
                                allParameters.put("regexpFullNameList", "'" + fullName + "'");
                            }
                            complexConditionList.add(allParameters);

                        } else {
                            // Single parameter has been specified
                            String value = "";
                            if (!StringUtils.isBlank(partyNumberId)) {
                                value = (String) conditionList.get("partyNumberIdList");
                                conditionList.put("partyNumberIdList" , (StringUtils.isBlank(value) ? "" :  (value + ",")) + partyNumberId);
                            }

                            if (!StringUtils.isBlank(clientId)) {
                                value = (String) conditionList.get("clientIdList");
                                conditionList.put("clientIdList" , (StringUtils.isBlank(value) ? "" :  (value + ",")) + clientId);
                            }


                            if (!StringUtils.isBlank(fullName)) {
                                if (allParameters.containsKey("fullNameList")) {
                                    value = (String) conditionList.get("fullNameList");
                                    conditionList.put("fullNameList" , (StringUtils.isBlank(value) ? "" : (value + ",")) + fullName);
                                } else if (allParameters.containsKey("regexpFullNameList")) {
                                    value = (String) conditionList.get("regexpFullNameList");
                                    conditionList.put("regexpFullNameList", (StringUtils.isBlank(value) ? "" : (value + "|")) + fullName);
                                }
                            }

                            if (!StringUtils.isBlank(externalReferenceId)) {
                                value = (String) conditionList.get("externalReferenceIdList");
                                conditionList.put("externalReferenceIdList" , (StringUtils.isBlank(value) ? "" : (value + ",")) + externalReferenceId);
                            }

                            if (!StringUtils.isBlank(externalDataId)) {
                                value = (String) conditionList.get("externalDataIdList");
                                conditionList.put("externalDataIdList" , (StringUtils.isBlank(value) ? "" : (value + ",")) + externalDataId);
                            }

                            if (!StringUtils.isBlank(classificationCode)) {
                                value = (String) conditionList.get("classificationCodeList");
                                conditionList.put("classificationCodeList" , (StringUtils.isBlank(value) ? "" : (value + ",")) + classificationCode);
                            }
                        }
                    }
                }

                String regexpFullNameList = (String) conditionList.get("regexpFullNameList");
                if (!StringUtils.isBlank(regexpFullNameList)) {
                    conditionList.put("regexpFullNameList", "'" +  regexpFullNameList + "'");
                }

                /*
                   Resolve filter expressions. The result should be single ViewFilter class containing
                   corresponding filter strings
                */
                // ViewFilter filter = computeView(partyInquiryRequest);
                FilterView filterView = getFilterView(partyInquiryRequest);

                // now call DAO to get result
                PartyInquiryServiceDAO dao = getPartyInquiryServiceDAO();
                partyInquiryResult = dao.loadPartyInquiryResult(conditionList, asOfDate, sourceSystem, filterView.getFilterStringMap());

                if (!StringUtils.isBlank(sourceSystem)) {
                    partyInquiryResult.setAsOfDate(StringUtils.isBlank(partyInquiryRequest.getAsOfDate()) ?
                            DateUtils.convertDateToDateOnlyString(new Date()) : partyInquiryRequest.getAsOfDate());
                    SendingSystemInformationType sendingSystemInformation = new SendingSystemInformationType();
                    sendingSystemInformation.setVendorProductName(sourceSystem);
                    partyInquiryResult.setSendingSystemInformation(sendingSystemInformation);
                }
            }
            // replace values coming from back-end with the the ones from request.

            partyInquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
            partyInquiryResult.setCorrelationId(partyInquiryRequest.getCorrelationId());
            partyInquiryResult.setMessageId(partyInquiryRequest.getMessageId());

            List<PersonType> person = partyInquiryResult.getPerson();
            List<OrganizationType> organization = partyInquiryResult.getOrganization();

            owsLogRequest.setSourceTableName("ENTITY");
            if (person.size() > 0) {
                owsLogRequest.setSourceRecordFk(person.get(0).getPersonNumberId());
                owsLogRequest.setSourceRecordNo(person.get(0).getClientId());
            } else if (organization.size() > 0) {
                owsLogRequest.setSourceRecordFk(organization.get(0).getOrganizationNumberId());
                owsLogRequest.setSourceRecordNo(organization.get(0).getClientId());
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PartyInquiryServiceManagerImpl", e);
            l.logp(Level.SEVERE, getClass().getName(), "PartyInquiryServiceManagerImpl", ae.getMessage(), ae);
            partyInquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
            throw ae;
        }

        owsLogRequest.setMessageStatusCode(partyInquiryResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(partyInquiryResult, _PartyInquiryResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "loadParty", xmlResult);
        } else {
            owsLogRequest.setServiceResult(partyInquiryResult);
            owsLogRequest.setServiceResultQName(_PartyInquiryResult_QNAME);
        }
        owsLogRequest.setRequestName(_PartyInquiryRequest_QNAME.getLocalPart());
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadParty", partyInquiryResult);
        }
        return partyInquiryResult;
    }

    private boolean isRegexpValid(String expression) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRegexpValid", new Object[]{expression});
        }

        boolean valid = true;

        try {
            Pattern.compile(expression);
        } catch (PatternSyntaxException e) {
            valid = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRegexpValid", valid);
        }
        return valid;
    }

    private ViewFilter computeView(PartyInquiryRequestType partyInquiryRequest) {
        // view name
        List viewName = null;
        ViewFilter resultFilter = new ViewFilter();
        if (partyInquiryRequest.getPartyInquiryResultParameters() != null
                && partyInquiryRequest.getPartyInquiryResultParameters().getViewName() != null) {

            viewName = partyInquiryRequest.getPartyInquiryResultParameters().getViewName();

            /*
             viewName is a list of names that should have corresponding entries in views map
             First check if all the views are known to the system (included into map)
             If not all of them known then throw exception

             All view names are known then process the list. The result should be ViewFilter object containing
             intersection of all specified views.
            */

            // code goes here.
            Iterator it = viewName.iterator();

            while (it.hasNext()) {
                String view = (String) it.next();
                //Ignore empty elements
                if (viewName == null) return new ViewFilter();

                if (views.containsKey(view)) {

                    ViewFilter filter = views.get(view);
                    if (resultFilter.getAddressFilter() == null &&
                            filter.getAddressFilter() != null) {
                        resultFilter.setAddressFilter(filter.getAddressFilter());
                    } else {
                        resultFilter.setAddressFilter("'" +
                                getIntersection(resultFilter.getAddressFilter(),
                                        filter.getAddressFilter()) + "'");
                    }
                    if (resultFilter.getOrganizationFilter() == null &&
                            filter.getOrganizationFilter() != null) {
                        resultFilter.setOrganizationFilter(filter.getOrganizationFilter());
                    } else {
                        resultFilter.setOrganizationFilter("'" +
                                getIntersection(resultFilter.getOrganizationFilter(),
                                        filter.getOrganizationFilter()) + "'");
                    }

                    if (resultFilter.getPersonFilter() == null &&
                            filter.getPersonFilter() != null) {
                        resultFilter.setPersonFilter(filter.getPersonFilter());
                    } else {
                        resultFilter.setPersonFilter("'" +
                                getIntersection(resultFilter.getPersonFilter(),
                                        filter.getPersonFilter()) + "'");
                    }
                    if (resultFilter.getPropertyFilter() == null &&
                            filter.getPropertyFilter() != null) {
                        resultFilter.setPropertyFilter(filter.getPropertyFilter());
                    } else {
                        resultFilter.setPropertyFilter("'" +
                                getIntersection(resultFilter.getPropertyFilter(),
                                        filter.getPropertyFilter()) + "'");
                    }
                    if (resultFilter.getRelationshipFilter() == null &&
                            filter.getRelationshipFilter() != null) {
                        resultFilter.setRelationshipFilter(filter.getRelationshipFilter());
                    } else {
                        resultFilter.setRelationshipFilter("'" +
                                getIntersection(resultFilter.getRelationshipFilter(),
                                        filter.getRelationshipFilter()) + "'");
                    }


                } else {
                    AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "View Name:" + view + " has no specification. Operation aborted. No views will be applied to the result", new Exception());
                }
            }
        }

        return resultFilter;
    }

    private String getIntersection(String one, String two) {
        String result = null;
        String[] str1 = null;
        String[] str2 = null;
        HashSet set1 = new HashSet();
        HashSet set2 = new HashSet();
        // both strings have | separated entries. Split first
        if (one == null && two == null) result = null;

        if (one != null) {
            str1 = one.split("|");
            for (int i = 0; i < str1.length; i++) {
                set1.add(str1[i]);
            }
        }

        if (two != null) {
            str2 = two.split("|");
            for (int i = 1; i < str2.length; i++) {
                set2.add(str2[i]);
            }
        }
        set1.retainAll(set2);
        Iterator it = set1.iterator();
        while (it.hasNext()) {
            result = "|" + (String) it.next();
        }
        result = (result == null) ? "" : result + "|";
        return result;
    }

    private FilterView getFilterView(PartyInquiryRequestType partyInquiryRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterView", new Object[]{partyInquiryRequest});
        }

        FilterView filterView = null;

        if (partyInquiryRequest != null && partyInquiryRequest.getPartyInquiryResultParameters() != null &&
                partyInquiryRequest.getPartyInquiryResultParameters().getViewName().size() > 0) {
            filterView = FilterViewFactory.getInstance().getFilterView(PartyInquiryServiceFields.PARTY_FILTER_VIEW_CATEGORY, partyInquiryRequest.getPartyInquiryResultParameters().getViewName());
        } else {
            filterView = FilterViewFactory.getInstance().getDefaultFilterView(PartyInquiryServiceFields.PARTY_FILTER_VIEW_CATEGORY);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterView", filterView);
        }
        return filterView;
    }

    public void verifyConfig() {
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getAddressListManager() == null)
            throw new ConfigurationException("The required property 'addressListManager' is missing.");

    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    private EntityManager m_entityManager;

    public void setPartyInquiryServiceDAO(PartyInquiryServiceDAO partyInquiryServiceDAO) {
        m_partyInquiryServiceDAO = partyInquiryServiceDAO;
    }

    public PartyInquiryServiceDAO getPartyInquiryServiceDAO() {
        return m_partyInquiryServiceDAO;
    }

    private PartyInquiryServiceDAO m_partyInquiryServiceDAO;

    public AddressListManager getAddressListManager() {
        return m_addressListManager;
    }

    public void setAddressListManager(AddressListManager addressListManager) {
        this.m_addressListManager = addressListManager;
    }

    private AddressListManager m_addressListManager;

    public PhoneListManager getPhoneListManager() {
        return m_phoneListManager;
    }

    public void setPhoneListManager(PhoneListManager phoneListManager) {
        this.m_phoneListManager = phoneListManager;
    }

    private PhoneListManager m_phoneListManager;


    public PropertyManager getPropertyManager() {
        return m_propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.m_propertyManager = propertyManager;
    }

    private PropertyManager m_propertyManager;

    public ContactManager getContactManager() {
        return m_contactManager;
    }

    public void setContactManager(ContactManager contactManager) {
        this.m_contactManager = contactManager;
    }

    private ContactManager m_contactManager;

    private final Logger l = LogUtils.getLogger(getClass());
}
