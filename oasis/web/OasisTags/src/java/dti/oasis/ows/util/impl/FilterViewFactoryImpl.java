package dti.oasis.ows.util.impl;

import dti.oasis.ows.util.FilterView;
import dti.oasis.ows.util.FilterViewFactory;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/14/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/31/2018       Elvin       Issue 190210: add Party Classification view
 * ---------------------------------------------------
 */
public class FilterViewFactoryImpl extends FilterViewFactory {
    private final Logger l = LogUtils.getLogger(getClass());

    private static Map<String, List<FilterView>> c_filterViewsMap;

    private AllElementView c_allElementView;

    static {
        // Filter view configurations.
        c_filterViewsMap = new HashMap<String, List<FilterView>>();

        // Filter views of party filter.
        List<FilterView> partyFilterView = new ArrayList<>();
        c_filterViewsMap.put("PartyFilterView", partyFilterView);

        // Default view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "[DEFAULT]", ExcludeFilterView.class)
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType",
                        "OrganizationAdditionalInfoType",
                        "AddressAdditionalInfoType"})
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType",
                        "AddressAdditionalXmlDataType"})
                .build());

        // Party view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "Party", ExcludeFilterView.class)
                .addFilterElements("addressFilter", new String[]{
                        "AddressType"})
                .addFilterElements("personFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "EducationInformationType",
                        "ProfessionalLicenseType",
                        "CertificationType",
                        "ContactType",
                        "PartyNoteType",
                        "RelationshipType",
                        "PartyClassificationType"})
                .addFilterElements("organizationFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "OrganizationLicenseType",
                        "CertificationType",
                        "PartyNoteType",
                        "RelationshipType",
                        "PartyClassificationType"})
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType",
                        "OrganizationAdditionalInfoType",
                        "AddressAdditionalInfoType"})
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType",
                        "AddressAdditionalXmlDataType"})
                .build());

        // Party Classification view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "PartyClassification", ExcludeFilterView.class)
                .addFilterElements("addressFilter", new String[]{
                        "AddressType"})
                .addFilterElements("personFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "EducationInformationType",
                        "ProfessionalLicenseType",
                        "CertificationType",
                        "ContactType",
                        "PartyNoteType",
                        "RelationshipType"})
                .addFilterElements("organizationFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "OrganizationLicenseType",
                        "CertificationType",
                        "PartyNoteType",
                        "RelationshipType"})
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType",
                        "OrganizationAdditionalInfoType",
                        "AddressAdditionalInfoType"})
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType",
                        "AddressAdditionalXmlDataType"})
                .build());

        // Address view
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "Address", ExcludeFilterView.class)
                .addFilterElements("personFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "EducationInformationType",
                        "ProfessionalLicenseType",
                        "CertificationType",
                        "ContactType",
                        "PartyNoteType",
                        "RelationshipType",
                        "PartyClassificationType"})
                .addFilterElements("organizationFilter", new String[]{
                        "BusinessEmailType",
                        "BasicPhoneNumberType",
                        "BasicAddressType",
                        "OrganizationLicenseType",
                        "CertificationType",
                        "PartyNoteType",
                        "RelationshipType",
                        "PartyClassificationType"})
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType",
                        "OrganizationAdditionalInfoType",
                        "AddressAdditionalInfoType"})
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType",
                        "AddressAdditionalXmlDataType"})
                .build());

        // PartyAdditionalInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "PartyAdditionalInfo", IncludeFilterView.class)
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType",
                        "OrganizationAdditionalInfoType",
                        "AddressAdditionalInfoType"})
                .build());

        // PersonAdditionalInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "PersonAdditionalInfo", IncludeFilterView.class)
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "PersonAdditionalInfoType"})
                .build());

        // PersonAdditionalInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "OrganizationAdditionalInfo", IncludeFilterView.class)
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "OrganizationAdditionalInfoType"})
                .build());

        // PersonAdditionalInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "AddressAdditionalInfo", IncludeFilterView.class)
                .addFilterElements("partyAdditionalInfoFilter", new String[]{
                        "AddressAdditionalInfoType"})
                .build());

        // PartyAdditionalXmlInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "PartyAdditionalXmlInfo", IncludeFilterView.class)
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType",
                        "AddressAdditionalXmlDataType"})
                .build());

        // EntityAdditionalXmlInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "EntityAdditionalXmlData", IncludeFilterView.class)
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "EntityAdditionalXmlDataType"})
                .build());

        // AddressAdditionalXmlInfo view.
        partyFilterView.add(FilterViewBuilder.newInstance("PartyFilterView", "AddressAdditionalXmlData", IncludeFilterView.class)
                .addFilterElements("partyAdditionalXmlInfoFilter", new String[]{
                        "AddressAdditionalXmlDataType"})
                .build());
    }

    @Override
    public FilterView getFilterView(String category, List<String> filterViewNames) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterView", new Object[]{category});
        }

        FilterView filterView = null;

        if (filterViewNames != null && filterViewNames.contains(ALL_ELEMENT_VIEW_NAME)) {
            // Getting all element view.
            filterView = getAllElementView();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getFilterView", filterView);
            }
            return filterView;
        }

        List<FilterView> categoryFilterViews = getFilterViews(category);
        if (categoryFilterViews == null) {
            // Category is not found.
            filterView = getAllElementView();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getFilterView", filterView);
            }
            return filterView;
        }

        if (filterViewNames != null && filterViewNames.contains(category + ALL_ELEMENT_VIEW_NAME)) {
            // Getting all elements for category.
            filterView = getAllElementView();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getFilterView", filterView);
            }
            return filterView;
        }

        List<FilterView> excludeFilterViews = new ArrayList<FilterView>();
        List<FilterView> includeFilterViews = new ArrayList<FilterView>();

        if (filterViewNames != null) {
            for (String filterViewName : filterViewNames) {
                if (!StringUtils.isBlank(filterViewName)) {
                    FilterView tempFilterView = getFilterView(categoryFilterViews, filterViewName);

                    if (tempFilterView != null) {
                        if (tempFilterView instanceof ExcludeFilterView) {
                            excludeFilterViews.add(tempFilterView);

                        } else if (tempFilterView instanceof IncludeFilterView) {
                            includeFilterViews.add(tempFilterView);

                        } else if (tempFilterView instanceof AllElementView) {
                            filterView = getAllElementView();

                            if (l.isLoggable(Level.FINER)) {
                                l.exiting(getClass().getName(), "getFilterView", filterView);
                            }
                            return filterView;
                        }
                    }
                }
            }
        }

        List<FilterView> filterViews = new ArrayList<FilterView>();

        if (excludeFilterViews.size() == 0) {
            // If there is no filter view, add default filter view.
            filterViews.add(getDefaultFilterView(category));
        } else {
            filterViews.addAll(excludeFilterViews);
        }

        filterViews.addAll(includeFilterViews);

        if (filterViews.size() == 1) {
            // If there is only one filter, return this one.
            filterView = filterViews.get(0);
        } else {
            // If there are more than one filter, return a composite one.
            filterView = new CompositeFilterView(category, filterViews);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterView", filterView);
        }
        return filterView;
    }

    @Override
    public FilterView getDefaultFilterView(String category) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultFilterView", new Object[]{category});
        }

        FilterView filterView;

        List<FilterView> categoryFilterViews = getFilterViews(category);
        if (categoryFilterViews == null) {
            // If filter view map for category is not found, return all element view.
            filterView = getAllElementView();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getFilterView", filterView);
            }
            return filterView;
        }

        filterView = getDefaultFilterView(categoryFilterViews);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultFilterView", filterView);
        }
        return filterView;
    }

    protected AllElementView getAllElementView() {
        l.entering(getClass().getName(), "getAllElementView");

        if (c_allElementView == null) {
            synchronized (this) {
                if (c_allElementView == null) {
                    c_allElementView = new AllElementView();
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllElementView", c_allElementView);
        }
        return c_allElementView;
    }

    /**
     * Get all filter views of a category.
     * @param category
     * @return
     */
    protected List<FilterView> getFilterViews(String category) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterViews", new Object[]{category});
        }

        List<FilterView> filterViews = null;

        if (!StringUtils.isBlank(category) && c_filterViewsMap != null) {
            filterViews = c_filterViewsMap.get(category);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterViews", filterViews);
        }
        return filterViews;
    }

    /**
     * Get filter view from filer view list.
     * @param filterViews
     * @param filterViewName
     * @return
     */
    protected FilterView getFilterView(List<FilterView> filterViews, String filterViewName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterView", new Object[]{filterViews, filterViewName});
        }

        FilterView filterView = null;

        if (filterViews != null && !StringUtils.isBlank(filterViewName)) {
            for (FilterView tempFilterView : filterViews) {
                if (tempFilterView != null && filterViewName.equals(tempFilterView.getName())) {
                    filterView = tempFilterView;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterView", filterView);
        }
        return filterView;
    }

    /**
     * Get default filter view from filter view list.
     * @param filterViews
     * @return
     */
    protected FilterView getDefaultFilterView(List<FilterView> filterViews) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultFilterView", new Object[]{filterViews});
        }

        FilterView filterView = getFilterView(filterViews, FilterViewFactory.DEFAULT_ELEMENT_VIEW_NAME);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultFilterView", filterView);
        }
        return filterView;
    }

    public FilterViewFactoryImpl() {
    }
}
