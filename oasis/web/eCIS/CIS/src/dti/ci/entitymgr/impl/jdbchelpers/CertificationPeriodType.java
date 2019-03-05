package dti.ci.entitymgr.impl.jdbchelpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class CertificationPeriodType implements SQLData {

    protected String startDate;
    protected String endDate;
    protected com.delphi_tech.ows.party.CertificationPeriodType jaxbCertificationPeriodType;

     public String sql_type;

     public String getSQLTypeName() throws SQLException {
           return sql_type;
     }

     public CertificationPeriodType(){
        jaxbCertificationPeriodType = new   com.delphi_tech.ows.party.CertificationPeriodType();
     }

     public  CertificationPeriodType(String startDate, String endDate){
         this.startDate = startDate;
         this.endDate = endDate;
         jaxbCertificationPeriodType = new   com.delphi_tech.ows.party.CertificationPeriodType();

     }

      public void readSQL(SQLInput stream,String typeName ) throws SQLException{
         sql_type = typeName;
         startDate = stream.readString();
         endDate = stream.readString();
         jaxbCertificationPeriodType.setEndDate((this.endDate==null)?"":endDate);
         jaxbCertificationPeriodType.setStartDate((this.startDate==null)?"":startDate);
     }
    public void writeSQL(SQLOutput stream) throws SQLException{
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
     }
    public com.delphi_tech.ows.party.CertificationPeriodType getJaxbCertificationPeriodType(){
        return jaxbCertificationPeriodType;
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