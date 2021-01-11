package io.crcell.pramework.controllable;

import io.crcell.pramework.serviceable.Serviceable;
import io.crcell.pramework.utils.GsonTools;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class ControllableImpl<T1, T2> implements Controllable<T1, T2> {
  private final Serviceable service;

  @Override
  @PostMapping
  public ResponseEntity create(@Valid @RequestBody T1 createForm) {
    Optional<T1> save = null;
    try {
      save = service.create(createForm);
      return save.map(created -> ResponseEntity.status(HttpStatus.CREATED)
                                               .body(created))
                 .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                       .build());
    } catch(EntityExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                           .build();
    } catch(DataIntegrityViolationException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                           .build();
    }
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity replaceById(@PathVariable T2 id, @RequestBody @Valid T1 replace) {
    try {
      return (ResponseEntity) service.replace(replace)
                                     .map(replaced -> ResponseEntity.status(HttpStatus.OK)
                                                                    .body(replaced))
                                     .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                           .build());
    } catch(EntityNotFoundException e) {
      return ResponseEntity.noContent()
                           .build();
    }
  }

  @Override
  @PatchMapping("/{id}")
  public ResponseEntity updateById(@PathVariable T2 id, @RequestBody Map<String, Object> fields) {
    try {
      return (ResponseEntity) service.patch(id, fields)
                                     .map(user -> ResponseEntity.status(HttpStatus.OK)
                                                                .body(user))
                                     .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                           .build());
    } catch(EntityNotFoundException e) {
      return ResponseEntity.noContent()
                           .build();
    } catch(GsonTools.JsonObjectExtensionConflictException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body(e.getMessage());
    }
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity get(@PathVariable T2 id) {
    Optional<T1> byId = service.retrieve(id);
    return byId.map(retrieved -> ResponseEntity.status(HttpStatus.OK)
                                               .body(retrieved))
               .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT)
                                     .build());
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity deleteById(@PathVariable T2 id) {
    service.deleteById(id);
    return ResponseEntity.noContent()
                         .build();
  }
}
