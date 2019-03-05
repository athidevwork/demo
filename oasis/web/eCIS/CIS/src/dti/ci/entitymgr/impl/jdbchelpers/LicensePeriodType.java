package dti.ci.entitymgr.impl.jdbchelpers;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class LicensePeriodType  implements SQLData {

    protected String startDate;
    protected String endDate;

    protected com.delphi_tech.ows.party.LicensePeriodType jaxbLicensePeriodType;
    public String sql_type;

    public String getSQLTypeName() throws SQLException {
            return sql_type;
    }
    public LicensePeriodType(){
        jaxbLicensePeriodType = new com.delphi_tech.ows.party.LicensePeriodType();
    }

    public LicensePeriodType(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
        jaxbLicensePeriodType = new com.delphi_tech.ows.party.LicensePeriodType();
        jaxbLicensePeriodType.setStartDate(startDate);
        jaxbLicensePeriodType.setEndDate(endDate);
    }
     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         startDate = stream.readString();
         endDate = stream.readString();
         jaxbLicensePeriodType.setStartDate((startDate==null)?"":startDate);
         jaxbLicensePeriodType.setEndDate((endDate==null)?"":endDate);

     }
    public void writeSQL(SQLOutput stream) throws SQLException{
         /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }

     public com.delphi_tech.ows.party.LicensePeriodType getJaxbLicensePeriodType(){
         return this.jaxbLicensePeriodType;
     }



    /**
     * Gets the value of the startDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

}