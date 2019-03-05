package dti.oasis.util;

import java.io.Serializable;

/**
 * JavaBean containing details about a panel.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 *
 * @author James
 *         Date:   Aug 16, 2010
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
*/

public class PanelBean implements Serializable, Cloneable {

    /**
     * Member variable declaration
     * NOTE : If any new variable is added to the PanelBean Class, make sure to clone the new variable as part of
     * clone method.
     */
    private String id;
    private String title;


    /**
     * Returns a deep copy clone of PanelBean.
     *
     * @return PanelBean
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        PanelBean clonedPanelBean = new PanelBean();
        clonedPanelBean.setId(this.getId());
        clonedPanelBean.setTitle(this.getTitle());
        return clonedPanelBean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.PanelBean");
        buf.append("{id=").append(id);
        buf.append(",title=").append(title);
        buf.append('}');
        return buf.toString();
    }

}