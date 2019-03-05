package dti.oasis.recordset;

import dti.oasis.busobjs.EnumType;
import dti.oasis.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents Ascending or Descending sort order.
 * The getInstance method is a convenience method for parsing a string into sort order.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 2, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class SortOrder extends EnumType {
    public final static int ASC_VALUE = 1;
    public final static SortOrder ASC = new SortOrder(ASC_VALUE, "ASC");

    public final static int DESC_VALUE = -1;
    public final static SortOrder DESC = new SortOrder(DESC_VALUE, "DESC");


    public static SortOrder getInstance(String sortOrder) {
        if (StringUtils.isBlank(sortOrder))
            return SortOrder.ASC;
        else if (ASC.getName().equalsIgnoreCase(sortOrder))
            return SortOrder.ASC;
        else
            return SortOrder.DESC;
    }

    public SortOrder(int value, String name) {
        super(value, name);
    }

    /**
     * This constructor is for use with Serialization only.
     */
    public SortOrder() {
    }

    public boolean isAscending() {
        return intValue() == ASC_VALUE;
    }

    public boolean isDescending() {
        return intValue() == DESC_VALUE;
    }
}