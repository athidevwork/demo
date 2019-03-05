package dti.oasis.error.rest;

import dti.oasis.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/5/2019
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
@ControllerAdvice
@RestController
public class DelphiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity(handleExceptionResponse(ex, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity(handleExceptionResponse(ex, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableValidationException.class)
    public final ResponseEntity<Object> handleUnprocessableValidationExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity(handleExceptionResponse(ex, request), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity(handleExceptionResponse(ex, request, "Validation Failed", ex.getBindingResult().toString()), HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse handleExceptionResponse(Exception ex, WebRequest request) {
        return handleExceptionResponse(ex, request, ex.getMessage(), "");
    }

    private ExceptionResponse handleExceptionResponse(Exception ex, WebRequest request, String message, String detail) {
        String method = ((ServletWebRequest) request).getHttpMethod().name();
        String requestName = request.getDescription(false);
        requestName = request.getHeader("host") + requestName.substring(requestName.indexOf("=")+1, requestName.length());
        return handleExceptionResponse(ex, method, requestName, message, detail);
    }

    private ExceptionResponse handleExceptionResponse(Exception ex, String method, String requestName, String message, String detail) {
        l.log(Level.SEVERE, "Failed executing: [" + method + " " + requestName + "]: " + message + (StringUtils.isEmpty(detail) ? "" : detail), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), method, requestName, message, detail);
        return exceptionResponse;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
