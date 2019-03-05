package dti.oasis.jpa;

import javax.persistence.EntityManager;

import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

import org.springframework.orm.jpa.EntityManagerHolder;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   3/13/12
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Deprecated
public class EntityManagerHolderImpl { //  extends ResourceHolderSupport {

    public EntityManagerHolder getEntityManagerHolder() {
        return entityManagerHolder;
    }

    public void setEntityManagerHolder(EntityManagerHolder entityManagerHolder) {
        this.entityManagerHolder = entityManagerHolder;
    }

    EntityManagerHolder entityManagerHolder;

    public EntityManagerHolderImpl(EntityManagerHolder entityManagerHolder){
        this.entityManagerHolder = entityManagerHolder;
    }
//    private final EntityManager entityManager;
//
//    private boolean transactionActive;
//
//    private SavepointManager savepointManager;
//
//
//    public EntityManagerHolderImpl(EntityManager entityManager) {
//        Assert.notNull(entityManager, "EntityManager must not be null");
//        this.entityManager = entityManager;
//    }
//
//
//    public EntityManager getEntityManager() {
//        return this.entityManager;
//    }
//
//    protected void setTransactionActive(boolean transactionActive) {
//        this.transactionActive = transactionActive;
//    }
//
//    protected boolean isTransactionActive() {
//        return this.transactionActive;
//    }
//
//    protected void setSavepointManager(SavepointManager savepointManager) {
//        this.savepointManager = savepointManager;
//    }
//
//    protected SavepointManager getSavepointManager() {
//        return this.savepointManager;
//    }
//
//    @Override
//    public void clear() {
//        super.clear();
//        this.transactionActive = false;
//        this.savepointManager = null;
//    }

}
