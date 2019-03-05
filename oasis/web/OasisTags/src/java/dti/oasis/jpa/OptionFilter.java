package dti.oasis.jpa;

import java.io.Serializable;
import java.util.List;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   5/13/2015
 *
 * @author Parker
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 * ---------------------------------------------------
 */
public class OptionFilter<T> implements Serializable {

    private BaseService<T> service;
    private List orderByList;
    private String[] returnColumn;
    private T filterEntity;
    private boolean emptyOptionAllowed = false;

    public BaseService<T> getService() {
        return service;
    }

    public void setService(BaseService<T> service) {
        this.service = service;
    }

    public List getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List orderByList) {
        this.orderByList = orderByList;
    }

    public String[] getReturnColumn() {
        return returnColumn;
    }

    public void setReturnColumn(String[] returnColumn) {
        this.returnColumn = returnColumn;
    }

    public T getFilterEntity() {
        return filterEntity;
    }

    public void setFilterEntity(T filterEntity) {
        this.filterEntity = filterEntity;
    }

    public boolean isEmptyOptionAllowed() {
        return emptyOptionAllowed;
    }

    public void setEmptyOptionAllowed(boolean emptyOptionAllowed) {
        this.emptyOptionAllowed = emptyOptionAllowed;
    }
}
