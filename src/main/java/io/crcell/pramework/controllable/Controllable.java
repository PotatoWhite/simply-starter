package io.crcell.pramework.controllable;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface Controllable<T1, T2> {

  ResponseEntity create(T1 createForm);

  ResponseEntity replaceById(T2 id, T1 replace);

  ResponseEntity updateById(T2 id, Map<String, Object> fields);

  ResponseEntity get(T2 id);

  ResponseEntity deleteById(T2 id);
}
