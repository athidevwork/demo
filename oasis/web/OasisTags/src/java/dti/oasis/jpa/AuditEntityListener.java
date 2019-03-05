package dti.oasis.jpa;

import javax.persistence.*;
import java.util.Date;

/**
 * <p/>
 * <p/>
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date: 10/24/2014
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class AuditEntityListener {

    @PrePersist
    public void prePersist(AuditableEntity e) {
        //TODO use real user name
        Date date = new Date();
        e.setSysCreatedBy("createuser");
        e.setSysCreateTime(date);
        e.setSysUpdatedBy("createuser");
        e.setSysUpdateTime(date);

    }

    @PreUpdate
    public void preUpdate(AuditableEntity e) {
        //TODO use real user name
        e.setSysUpdatedBy("updateuser");
        e.setSysUpdateTime(new Date());
    }


    @PreRemove
    public void preRemove(AuditableEntity e) {
        //The record is delete
    }

    @PostPersist
    public void postPersist(AuditableEntity e) {

    }

    @PostUpdate
    public void postUpdate(AuditableEntity e) {

    }

    @PostRemove
    public void postRemove(AuditableEntity e) {

    }


}
