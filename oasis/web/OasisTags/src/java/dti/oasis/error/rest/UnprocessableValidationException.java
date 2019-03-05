package dti.oasis.error.rest;

import dti.oasis.util.LogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/6/2019
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableValidationException extends RuntimeException {
    private final Logger l = LogUtils.getLogger(getClass());

    public UnprocessableValidationException(String message) {
        super(message);
    }
}
