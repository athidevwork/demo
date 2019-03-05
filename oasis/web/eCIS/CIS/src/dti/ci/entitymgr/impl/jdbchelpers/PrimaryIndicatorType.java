//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-1.0.0.0_2-1-9-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.08 at 09:49:15 AM EST 
//


package dti.ci.entitymgr.impl.jdbchelpers;

import dti.oasis.busobjs.YesNoEmptyFlag;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class PrimaryIndicatorType   implements SQLData {

    protected String value;
    protected String description;

    protected com.delphi_tech.ows.party.PrimaryIndicatorType jaxbPrimaryIndicatorType;
    public String sql_type;


    public PrimaryIndicatorType(){
        jaxbPrimaryIndicatorType = new com.delphi_tech.ows.party.PrimaryIndicatorType();
    }

    public PrimaryIndicatorType(String value, String description) {
      this.value =value;
      this.description = description;
      jaxbPrimaryIndicatorType = new com.delphi_tech.ows.party.PrimaryIndicatorType();
      jaxbPrimaryIndicatorType.setDescription(this.description);
      jaxbPrimaryIndicatorType.setValue(this.value);
    }

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }
    public void readSQL(SQLInput stream,String typeName ) throws SQLException{
          sql_type = typeName;
          value = stream.readString();
          description = stream.readString();
   //     jaxbPrimaryIndicatorType.setDescription((this.description==null)?"":description);
        jaxbPrimaryIndicatorType.setValue((this.value==null)?"":value);

      }
     public void writeSQL(SQLOutput stream) throws SQLException{
         /*
             This method should not have implementation
             We are not planning to make any updates using this method.
         */
      }

    public com.delphi_tech.ows.party.PrimaryIndicatorType getJaxbPrimaryIndicatorType() {
        com.delphi_tech.ows.party.PrimaryIndicatorType primaryIndicator = new com.delphi_tech.ows.party.PrimaryIndicatorType();
        primaryIndicator.setValue(YesNoEmptyFlag.getInstance(this.jaxbPrimaryIndicatorType.getValue()).trueFalseEmptyValue());
        return primaryIndicator;
    }

    public com.delphi_tech.ows.party.PrimaryIndicatorType getJaxbFuturePrimaryIndicatorType() {
        com.delphi_tech.ows.party.PrimaryIndicatorType futurePrimaryIndicator = new com.delphi_tech.ows.party.PrimaryIndicatorType();
        futurePrimaryIndicator.setValue(YesNoEmptyFlag.getInstance("F".equals(this.jaxbPrimaryIndicatorType.getValue())).trueFalseEmptyValue());
        return futurePrimaryIndicator;
    }
    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

}