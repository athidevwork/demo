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


public class OrganizationNameType   implements SQLData {

    protected String fullName;
    protected com.delphi_tech.ows.party.OrganizationNameType jaxbOrganizationNameType;

     public String sql_type;

     public String getSQLTypeName() throws SQLException {
           return sql_type;
     }

    public  OrganizationNameType(){
        jaxbOrganizationNameType = new com.delphi_tech.ows.party.OrganizationNameType();
    }

    public OrganizationNameType(String fullName){
        this.fullName = fullName;
        jaxbOrganizationNameType = new com.delphi_tech.ows.party.OrganizationNameType();
        jaxbOrganizationNameType.setFullName(this.fullName);
    }
         public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         fullName = stream.readString();
             jaxbOrganizationNameType.setFullName((this.fullName==null)?"":fullName);
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }

    public com.delphi_tech.ows.party.OrganizationNameType getJaxbOrganizationNameType(){
        return this.jaxbOrganizationNameType;
    }
    /**
     * Gets the value of the fullName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

}