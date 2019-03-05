package dti.ci.entitymgr.impl.jdbchelpers;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import javax.xml.bind.annotation.*;
import java.sql.*;


public class CtlStateOrProvinceCodeType implements SQLData {

    protected String value;
    protected String description;
    protected com.delphi_tech.ows.party.ControllingStateOrProvinceCodeType jaxbControllingStateOrProvinceCodeType;
        public String sql_type;

    public String getSQLTypeName() throws SQLException {
          return sql_type;
    }

    public CtlStateOrProvinceCodeType(){
        jaxbControllingStateOrProvinceCodeType = new com.delphi_tech.ows.party.ControllingStateOrProvinceCodeType();
    }

    public CtlStateOrProvinceCodeType(String value, String description){
        this.value = value;
        this.description = description;
        jaxbControllingStateOrProvinceCodeType = new com.delphi_tech.ows.party.ControllingStateOrProvinceCodeType();

         jaxbControllingStateOrProvinceCodeType.setDescription(this.description);
        jaxbControllingStateOrProvinceCodeType.setValue(this.value);
    }

     public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         this.value = stream.readString();
         this.description = stream.readString();
     //    jaxbControllingStateOrProvinceCodeType.setDescription((this.description==null)?"":description);
         jaxbControllingStateOrProvinceCodeType.setValue((this.value==null)?"":value);
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
    public com.delphi_tech.ows.party.ControllingStateOrProvinceCodeType getJaxbControllingStateOrProvinceCodeType(){
        return this.jaxbControllingStateOrProvinceCodeType;
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