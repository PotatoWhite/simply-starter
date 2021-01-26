package io.crcell.pramework.serviceable;

import io.crcell.pramework.utils.GsonTools;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Serviceable<T1, T2> {

  // create
  Optional<T1> create(T1 entity) throws EntityExistsException, DataIntegrityViolationException;

  // retrieve
  Optional<T1> retrieve(T2 id);

  // retrieve all
  List<T1> retrieveAll();

  // update
  Optional<T1> patch(T2 id, Map<String, Object> fields) throws GsonTools.JsonObjectExtensionConflictException;

  // delete
  void deleteById(T2 id);

  // delete
  void delete(T1 entity);

  // replace
  Optional<T1> replace(T2 id, T1 replace);
}
