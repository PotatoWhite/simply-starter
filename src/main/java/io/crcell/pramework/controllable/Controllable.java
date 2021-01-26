package io.crcell.pramework.controllable;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface Controllable<T1, T2> {

  T1 create(T1 createForm) throws Throwable;

  T1 replaceById(T2 id, T1 replace) throws Throwable;

  T1 updateById(T2 id, Map<String, Object> fields) throws Throwable;;

  T1 get(T2 id) throws Throwable;;

  List<T1> getAll() throws Throwable;;

  void deleteById(T2 id) throws Throwable;;
}
