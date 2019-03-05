package dti.oasis.jpa.mock.generatedvaluekeyentity;

import dti.oasis.jpa.BaseEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class GeneratedValueKeyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="OASIS_SEQUENCE")
    protected Long id;
    private String value;

    public GeneratedValueKeyEntity() {
    }

    public GeneratedValueKeyEntity(Long id, String value, String updateIndicator) {
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
