package dti.oasis.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/21/2015
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Entity
public class SelectOption implements Serializable, Cloneable {
    @Id
    private String value;
    private String label;

    public SelectOption() {
    }

    public SelectOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String code) {
        this.value = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectOption that = (SelectOption) o;

        if (!value.equals(that.value)) return false;
        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }
}
