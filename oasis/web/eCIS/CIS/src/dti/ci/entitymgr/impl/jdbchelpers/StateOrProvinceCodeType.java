//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-1.0.0.0_2-1-9-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.08 at 09:49:15 AM EST 
//


package dti.ci.entitymgr.impl.jdbchelpers;

import javax.xml.bind.annotation.*;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class StateOrProvinceCodeType  implements SQLData {

    protected String value;
    protected String description;
    protected com.delphi_tech.ows.party.StateOrProvinceCodeType jaxbStateOrProvinceCodfeType;

    public String sql_type="STATEORPROVINCECODETYPE";
    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }


    public  StateOrProvinceCodeType(){
        jaxbStateOrProvinceCodfeType = new com.delphi_tech.ows.party.StateOrProvinceCodeType();
    }

    public StateOrProvinceCodeType(String value,String description ){
        this.value = value;
        this.description = description;
    }


     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         value = stream.readString();
         description = stream.readString();
         jaxbStateOrProvinceCodfeType.setValue((this.value==null)?"":value);
       //  jaxbStateOrProvinceCodfeType.setDescription((this.description==null)?"":description);

     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
     public com.delphi_tech.ows.party.StateOrProvinceCodeType getJaxbStateOrProvinceCodeType(){
         return this.jaxbStateOrProvinceCodfeType;
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