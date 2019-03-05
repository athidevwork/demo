package dti.ci.entitymgr.impl.jdbchelpers;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: May 11, 2012
 * Time: 2:22:11 PM
 * To change this template use File | Settings | File Templates.
 *
 * This file represents filter conditions applicable for the named views.
 * Each of the field represents comma separated list of JAXB Classes to be excluded from the result.
 *
 */
public class ViewFilter {

    private String addressFilter;
    private String personFilter;
    private String organizationFilter;
    private String propertyFilter;
    private String relationshipFilter;




    public String getAddressFilter() {
        return addressFilter;
    }

    public void setAddressFilter(String addressFilter) {
        this.addressFilter = addressFilter;
    }

    public String getPersonFilter() {
        return personFilter;
    }

    public void setPersonFilter(String personFilter) {
        this.personFilter = personFilter;
    }

    public String getOrganizationFilter() {
        return organizationFilter;
    }

    public void setOrganizationFilter(String organizationFilter) {
        this.organizationFilter = organizationFilter;
    }

    public String getPropertyFilter() {
        return propertyFilter;
    }

    public void setPropertyFilter(String propertyFilter) {
        this.propertyFilter = propertyFilter;
    }

    public String getRelationshipFilter() {
        return relationshipFilter;
    }

    public void setRelationshipFilter(String relationshipFilter) {
        this.relationshipFilter = relationshipFilter;
    }
}
