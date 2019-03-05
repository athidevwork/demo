package dti.oasis.obr;

/**
 * Java bean for the field that triggered change event
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 03, 2012
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class TriggeredField {

    public TriggeredField(String id) {
        this.m_id = id;
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.obr.TriggeredField");
        buf.append("{id=").append(m_id);
        buf.append('}');
        return buf.toString();
    }

    public String getId() {
        return m_id;
    }

    public void setId(String m_id) {
        this.m_id = m_id;
    }

    private String m_id;

}