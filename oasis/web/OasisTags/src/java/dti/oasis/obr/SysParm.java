package dti.oasis.obr;

/**
 * Java bean for system parameter
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class SysParm {

    public SysParm(String name, String value) {
        this.m_name = name;
        this.m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        this.m_value = value;
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.obr.SysParm");
        buf.append("{name=").append(m_name);
        buf.append(",value=").append(m_value);
        buf.append('}');
        return buf.toString();
    }

    private String m_name;
    private String m_value;

}