package dti.oasis.jpa.mock.nonkeyentity;

import dti.oasis.jpa.BaseEntity;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/10/2015
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
public class NonKeyEntity extends BaseEntity {
    protected Long id;
    private String value;

    public NonKeyEntity() {
    }

    public NonKeyEntity(Long id, String value, String updateIndicator) {
        this.id = id;
        this.value = value;

        setUpdateIndicator(updateIndicator);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
