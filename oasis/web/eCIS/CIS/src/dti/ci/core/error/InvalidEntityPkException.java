package dti.ci.core.error;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/26/2018
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
public class InvalidEntityPkException extends AppException {
    private final Logger l = LogUtils.getLogger(getClass());

    public InvalidEntityPkException(String entityId) {
        super("Cannot find entity with entity PK: " + entityId + ".");
    }
}
