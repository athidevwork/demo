package dti.oasis.jpa;

import dti.oasis.util.LogUtils;

import javax.faces.model.SelectItem;
import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   11/19/14
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
public abstract class SearchCriteria {

    public abstract void clear();

    public abstract boolean isEmpty();

    public SelectItem[] getSelectOptions(Collection<Object[]> data) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSelectOptions", data);
        }
        SelectItem[] resultOptions = getSelectOptions(data, false);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSelectOptions", resultOptions);
        }
        return resultOptions;
    }

    public SelectItem[] getSelectOptions(Collection<Object[]> data, Boolean isSelectOptionAllowed) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSelectOptions", new Object[]{data, isSelectOptionAllowed});
        }
        SelectItem[] resultOptions;
        int i = 0;
        if (isSelectOptionAllowed) {
            resultOptions = new SelectItem[data.size() + 1];
            resultOptions[i] = new SelectItem("", "-SELECT-");
            i++;
        } else {
            resultOptions = new SelectItem[data.size()];
        }
        for (Object[] str : data) {
            if (str.length == 1) {
                String valueOption = str[0] == null ? "" : str[0].toString().trim();
                resultOptions[i] = new SelectItem(valueOption, valueOption);
            } else {
                String valueOption = str[0] == null ? "" : str[0].toString().trim();
                String labelOption = str[1] == null ? "" : str[1].toString().trim();
                resultOptions[i] = new SelectItem(valueOption, labelOption);
            }
            i++;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSelectOptions", resultOptions);
        }
        return resultOptions;
    }

    public SelectItem[] generateSelectOptions(List data) {
        return generateSelectOptions(data, true);
    }

    public SelectItem[] generateSelectOptions(List data, boolean isEmptyOptionAllowed) {

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateSelectOptions", new Object[]{data, isEmptyOptionAllowed});
        }
        List<SelectItem> selectOptionsList = new ArrayList<SelectItem>(data.size());
        SelectItem selectOption = null;
        if (isEmptyOptionAllowed) {
            selectOption = new SelectItem("", "-SELECT-");
            selectOptionsList.add(selectOption);
        }

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof String) {
                String value = data.get(i) == null ? "" : data.get(i).toString().trim();
                selectOption = new SelectItem(value, value);
            } else {
                Object[] option = (Object[]) data.get(i);
                String key = option[0] == null ? "" : option[0].toString().trim();
                String value = option[1] == null ? "" : option[1].toString();
                selectOption = new SelectItem(key, value);
            }
            selectOptionsList.add(selectOption);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateSelectOptions", selectOptionsList);
        }
        return selectOptionsList.toArray(new SelectItem[selectOptionsList.size()]);
    }

    public BaseService getBaseService() {
        return baseService;
    }

    public void seBaseService(BaseService baseService) {
        this.baseService = baseService;
    }

    @Embedded
    protected BaseService baseService;
    private final Logger l = LogUtils.getLogger(getClass());
}
