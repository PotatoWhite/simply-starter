package io.easywalk.simply.controllable;

import io.easywalk.simply.utils.GsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = SimplyControllableResponse.class)
public class SimplyControllableAdvice {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<SimplyErrorResponse> handle(EntityExistsException e) {
        return responseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<SimplyErrorResponse> handle(EntityNotFoundException e) {
        return responseError(e, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<SimplyErrorResponse> handle(NoSuchElementException e) {
        return responseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<SimplyErrorResponse> handle(DataIntegrityViolationException e) {
        return responseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SimplyErrorResponse> handle(MethodArgumentNotValidException e) {
        String message = e.getFieldErrors().stream().map(field -> field.getField()).collect(Collectors.joining(","));
        message += " is invalid";
        return responseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimplyErrorResponse> handle(Exception e) {
        return responseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GsonTools.JsonObjectExtensionConflictException.class)
    public ResponseEntity<SimplyErrorResponse> handle(GsonTools.JsonObjectExtensionConflictException e) {
        return responseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<SimplyErrorResponse> responseError(Throwable exception, HttpStatus status) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("[ERR] {} {} Param:{}, Response:{} {}", request.getMethod(), request.getRequestURI(), request.getQueryString(), status, exception.getMessage());

        return ResponseEntity.status(status).body(new SimplyErrorResponse(exception.getClass().getName(), exception.getMessage()));
    }
}
