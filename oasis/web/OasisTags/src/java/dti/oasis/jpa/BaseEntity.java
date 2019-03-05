package dti.oasis.jpa;

import dti.oasis.busobjs.EnumType;
import dti.oasis.util.StringUtils;

import javax.persistence.Transient;

/**
 * The base class for entity.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/4/2015
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
public abstract class BaseEntity {
    @Transient
    private String updateIndicator;

    public String getUpdateIndicator() {
        return updateIndicator;
    }

    public void setUpdateIndicator(String updateIndicator) {
        this.updateIndicator = updateIndicator;
    }

    public boolean isNew() {
        return RowStatus.getInstance(updateIndicator).isNew();
    }

    public boolean isModified() {
        return RowStatus.getInstance(updateIndicator).isModified();
    }

    public boolean isDeleted() {
        return RowStatus.getInstance(updateIndicator).isDeleted();
    }

    public boolean isUnmodified() {
        return RowStatus.getInstance(updateIndicator).isUnmodified();
    }

    public static class RowStatus extends EnumType {
        public static final int NEW_VALUE = 1;
        public static final RowStatus NEW = new RowStatus(NEW_VALUE, "I");

        public static final int MODIFIED_VALUE = 2;
        public static final RowStatus MODIFIED = new RowStatus(MODIFIED_VALUE, "U");

        public static final int DELETED_VALUE = 3;
        public static final RowStatus DELETED = new RowStatus(DELETED_VALUE, "D");

        public static final int UNMODIFIED_VALUE = 0;
        public static final RowStatus UNMODIFIED = new RowStatus(UNMODIFIED_VALUE, "N");

        public static RowStatus getInstance(String rowStatus) {
            if (StringUtils.isBlank(rowStatus)) {
                return RowStatus.UNMODIFIED;
            } else if ("I".equalsIgnoreCase(rowStatus)) {
                return RowStatus.NEW;
            } else if ("U".equalsIgnoreCase(rowStatus)) {
                return RowStatus.MODIFIED;
            } else if ("D".equalsIgnoreCase(rowStatus)) {
                return RowStatus.DELETED;
            } else
                return RowStatus.UNMODIFIED;
        }

        public boolean isNew() {
            return intValue() == NEW_VALUE;
        }

        public boolean isModified() {
            return intValue() == MODIFIED_VALUE;
        }

        public boolean isDeleted() {
            return intValue() == DELETED_VALUE;
        }

        public boolean isUnmodified() {
            return intValue() == UNMODIFIED_VALUE;
        }

        private RowStatus(int value, String name) {
            super(value, name);
        }

        public RowStatus() {
            super();
        }
    }
}
