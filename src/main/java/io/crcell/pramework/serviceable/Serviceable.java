package io.crcell.pramework.serviceable;

import io.crcell.pramework.utils.GsonTools;

import javax.persistence.EntityExistsException;
import java.util.Map;
import java.util.Optional;

public interface Serviceable<T1, T2> {

  // create
  Optional<T1> create(T1 entity) throws EntityExistsException;

  // retrieve
  Optional<T1> retrieve(T2 id);

  // update
  Optional<T1> patch(T2 id, Map<String, Object> fields) throws GsonTools.JsonObjectExtensionConflictException;

  // delete
  void deleteById(T2 id);

  // delete
  void delete(T1 entity);


  // replace
  Optional<T1> replace(T1 replace);
}
