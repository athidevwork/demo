package dti.oasis.jpa;

import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 *
 * ---------------------------------------------------
 */
public abstract class BaseResource {

    protected List<HashMap> initialOrderByCondition(String[] orderByArray) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initialOrderByCondition", new Object[]{orderByArray});
        }
        List<HashMap> orderByList = new ArrayList<HashMap>();
        if (orderByArray.length > 0) {
            HashMap<String, String> orderByMap = new HashMap<String, String>();
            for (int i = 0; i < orderByArray.length; i++) {
                String[] orderBy = orderByArray[i].split(",");
                String orderByColumn = orderBy[0];
                String orderByType = "Asc";
                if (orderBy.length == 2) {
                    orderByType = orderBy[1];
                }
                orderByMap = new HashMap<String, String>();
                orderByMap.put("orderByColumn", orderByColumn);
                orderByMap.put("orderByType", orderByType);
                orderByList.add(orderByMap);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initialOrderByCondition", orderByList);
        }
        return orderByList;
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
