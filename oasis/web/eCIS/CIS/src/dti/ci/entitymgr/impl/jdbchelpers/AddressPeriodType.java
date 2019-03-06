//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-1.0.0.0_2-1-9-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.08 at 09:49:15 AM EST 
//


package dti.ci.entitymgr.impl.jdbchelpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class AddressPeriodType implements SQLData {

    public String sql_type;
    protected String startDate;
    protected String endDate;
    protected com.delphi_tech.ows.party.AddressPeriodType jaxbAddressPeriodType;

    public AddressPeriodType(){
        jaxbAddressPeriodType = new com.delphi_tech.ows.party.AddressPeriodType();
    }

    public AddressPeriodType(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
        jaxbAddressPeriodType = new com.delphi_tech.ows.party.AddressPeriodType();
        jaxbAddressPeriodType.setStartDate(startDate);
        jaxbAddressPeriodType.setEndDate(endDate);
    }


    public String getSQLTypeName() throws SQLException {
           return sql_type;
     }

     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         startDate = stream.readString();
         endDate = stream.readString();
         jaxbAddressPeriodType.setStartDate((startDate==null)? "":startDate);
         jaxbAddressPeriodType.setEndDate((endDate==null)? "":endDate);

     }
    public void writeSQL(SQLOutput stream) throws SQLException{
         /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }

     public com.delphi_tech.ows.party.AddressPeriodType getJaxbAddressPeriodType(){
         return this.jaxbAddressPeriodType;
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