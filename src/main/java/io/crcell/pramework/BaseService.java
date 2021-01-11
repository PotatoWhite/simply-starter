package io.crcell.pramework;

import io.crcell.pramework.utils.GsonTools;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

/**
 * P-ramework(PostatoWhite) / 2021-01-06
 * */
public abstract class BaseService<T1, T2> {
  protected final JpaRepository<T1, T2> repository;

  protected BaseService(JpaRepository<T1, T2> repository) {
    this.repository = repository;
  }

  // create
  public Optional<T1> create(T1 entity) throws EntityExistsException {
    return Optional.ofNullable(repository.save(entity));
  }

  // retrieve
  public Optional<T1> retrieve(T2 id) {
    return repository.findById(id);
  }

  // update
  public Optional<T1> patch(T2 id, Map<String, Object> fields) throws GsonTools.JsonObjectExtensionConflictException {
    Optional<T1> byId = repository.findById(id);
    var          user = byId.orElseThrow(() -> new EntityNotFoundException("entity not found id="+id));

    return Optional.ofNullable(repository.save(GsonTools.merge(user, fields)));
  }

  // delete
  public void deleteById(T2 id) {
    repository.deleteById(id);
  }

  // delete
  public void delete(T1 entity) {
    repository.delete(entity);
  }


  // replace
  public Optional<T1> replace(T1 replace) {
    return Optional.ofNullable(repository.save(replace));
  }
}
