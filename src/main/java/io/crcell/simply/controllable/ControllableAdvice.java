package io.crcell.simply.controllable;

import io.crcell.simply.utils.GsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = GeneralControllableResponse.class)
public class ControllableAdvice {

    private ResponseEntity<ErrorResponse> responseError(String message, HttpStatus conflict) {
        log.error("{}", message);
        return ResponseEntity.status(conflict).body(new ErrorResponse(message));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(EntityExistsException e) {
        return responseError(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoContents(EntityNotFoundException e) {
        return responseError(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return responseError(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(MethodArgumentNotValidException e) {
        String message = e.getFieldErrors().stream().map(field -> field.getField()).collect(Collectors.joining(","));
        message += " is invalid";
        return responseError(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        return responseError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GsonTools.JsonObjectExtensionConflictException.class)
    public ResponseEntity<ErrorResponse> handleGsonConflictException(GsonTools.JsonObjectExtensionConflictException e) {
        return responseError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
