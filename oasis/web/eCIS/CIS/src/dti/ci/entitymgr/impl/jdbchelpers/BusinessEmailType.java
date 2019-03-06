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


public class BusinessEmailType  implements SQLData {

    protected String email;
    protected String key;
    protected com.delphi_tech.ows.party.BusinessEmailType jaxbBusinessEmailType;

     public String sql_type;

     public String getSQLTypeName() throws SQLException {
           return sql_type;
     }

    public  BusinessEmailType(){
        jaxbBusinessEmailType = new com.delphi_tech.ows.party.BusinessEmailType();
    }

    public BusinessEmailType(String email, String key){
        this.email = email;
        this.key = key;
        jaxbBusinessEmailType = new com.delphi_tech.ows.party.BusinessEmailType();
        jaxbBusinessEmailType.setEmail(this.email);
        jaxbBusinessEmailType.setKey(this.key);

    }
         public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         email = stream.readString();
         key = stream.readString();
         jaxbBusinessEmailType.setEmail((this.email==null)?"":email);
         jaxbBusinessEmailType.setKey((this.key==null)?"":key);
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }

    public com.delphi_tech.ows.party.BusinessEmailType getJaxbBusinessEmailType(){
        return this.jaxbBusinessEmailType;
    }
    /**
     * Gets the value of the email property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the key property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKey(String value) {
        this.key = value;
    }

}