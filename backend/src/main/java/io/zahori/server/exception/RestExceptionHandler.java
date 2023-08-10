package io.zahori.server.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    // 400
    /* TODO: uncomment when upgrading to spring boot 3. Class HttpStatusCode available in spring 6.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> responseBody = new HashMap<>();

        MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
        me.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            responseBody.put(fieldName, message);
        });

        return throwException(HttpStatus.BAD_REQUEST, responseBody.toString(), responseBody, e, request);
    }
     */
    // 400
    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<Object> badRequest(Exception e, WebRequest request) {
        return throwException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getMessage(), e, request);
    }

    // 404
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> notFound(Exception e, WebRequest request) {
        return throwException(HttpStatus.NOT_FOUND, String.format("Resource not found: %s", e.getMessage()), "", e, request);
    }

    // 503
    @ExceptionHandler(value = {ServiceUnavailableException.class})
    protected ResponseEntity<Object> serviceUnavailable(Exception e, WebRequest request) {
        return throwException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), "Service unavailable", e, request);
    }

    // 500
    @ExceptionHandler(value = {Exception.class, RuntimeException.class, Error.class})
    protected ResponseEntity<Object> generalException(Exception e, WebRequest request) {
        LOG.debug(ExceptionUtils.getStackTrace(e));
        return throwException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "Internal server error", e, request);
    }

    private ResponseEntity<Object> throwException(HttpStatus httpStatus, String internalError, Object body, Exception e, WebRequest request) {
        LOG.error("{}", internalError);
        return handleExceptionInternal(e, body, new HttpHeaders(), httpStatus, request);
    }
}
