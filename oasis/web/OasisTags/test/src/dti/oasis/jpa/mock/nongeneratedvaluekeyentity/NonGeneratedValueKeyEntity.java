package dti.oasis.jpa.mock.nongeneratedvaluekeyentity;

import dti.oasis.jpa.BaseEntity;

import javax.persistence.Id;

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
public class NonGeneratedValueKeyEntity extends BaseEntity {
    @Id
    protected Long id;
    private String value;

    public NonGeneratedValueKeyEntity() {
    }

    public NonGeneratedValueKeyEntity(Long id, String value, String updateIndicator) {
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
