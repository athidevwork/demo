package dti.ci.entitymgr.impl.jdbchelpers;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/18/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EffectivePeriodType implements SQLData {
    protected String startDate;
    protected String endDate;

    protected com.delphi_tech.ows.party.EffectivePeriodType jaxbEffectivePeriod;

    public String sql_type;

    public EffectivePeriodType() {
        jaxbEffectivePeriod = new com.delphi_tech.ows.party.EffectivePeriodType();
    }

    public EffectivePeriodType(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        jaxbEffectivePeriod = new com.delphi_tech.ows.party.EffectivePeriodType();
        jaxbEffectivePeriod.setStartDate(this.startDate);
        jaxbEffectivePeriod.setEndDate(this.endDate);
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        startDate = stream.readString();
        endDate = stream.readString();

        jaxbEffectivePeriod.setStartDate((this.startDate == null) ? "" : this.startDate);
        jaxbEffectivePeriod.setEndDate((this.endDate == null) ? "" : this.endDate);
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        /*
            This method should not have implementation
            We are not planning to make any updates using this method.
        */
    }

    public com.delphi_tech.ows.party.EffectivePeriodType getJaxbEffectivePeriod() {
        return jaxbEffectivePeriod;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
