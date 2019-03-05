package dti.oasis.obr;

/**
 * Java bean for user profile
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 27, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class UserProfile {

    public UserProfile(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.obr.UserProfile");
        buf.append("{name=").append(m_name);
        buf.append('}');
        return buf.toString();
    }

    private String m_name;
}
