package dti.ci.core.error;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

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
public class EntityPkNotExistsException extends AppException {
    private final Logger l = LogUtils.getLogger(getClass());

    public EntityPkNotExistsException() {
        super("Entity PK doesn't exists");
    }
}
