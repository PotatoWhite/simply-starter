package io.crcell.pramework.controllable;

import io.crcell.pramework.utils.GsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Slf4j
@RestControllerAdvice(annotations = ControllableResponse.class)
public class ControllableAdvice {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity handleConflict(EntityExistsException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleNoContents(EntityNotFoundException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnknownException(Exception e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(GsonTools.JsonObjectExtensionConflictException.class)
    public ResponseEntity handleGsonConflictException(GsonTools.JsonObjectExtensionConflictException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
