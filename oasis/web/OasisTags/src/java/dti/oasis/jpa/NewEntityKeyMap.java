package dti.oasis.jpa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/9/2015
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NewEntityKeyMap <T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long entityKey;
    private Long newEntityKey;
    @XmlTransient
    private T entity;

    public Long getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(Long entityKey) {
        this.entityKey = entityKey;
    }

    public Long getNewEntityKey() {
        return newEntityKey;
    }

    public void setNewEntityKey(Long newEntityKey) {
        this.newEntityKey = newEntityKey;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewEntityKeyMap)) return false;

        NewEntityKeyMap that = (NewEntityKeyMap) o;

        if (entityKey != null ? !entityKey.equals(that.entityKey) : that.entityKey != null) return false;
        if (newEntityKey != null ? !newEntityKey.equals(that.newEntityKey) : that.newEntityKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityKey != null ? entityKey.hashCode() : 0;
        result = 31 * result + (newEntityKey != null ? newEntityKey.hashCode() : 0);
        return result;
    }
}
