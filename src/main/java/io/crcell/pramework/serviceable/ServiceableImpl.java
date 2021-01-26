package io.crcell.pramework.serviceable;

import io.crcell.pramework.utils.GsonTools;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * P-ramework(PostatoWhite) / 2021-01-06
 */
public abstract class ServiceableImpl<T1, T2> implements Serviceable<T1, T2> {
  protected final JpaRepository<T1, T2> repository;

  protected ServiceableImpl(JpaRepository<T1, T2> repository) {
    this.repository = repository;
  }

  // create
  @Override
  public Optional<T1> create(T1 entity) throws EntityExistsException, DataIntegrityViolationException {
    return Optional.ofNullable(repository.save(entity));
  }

  // retrieve
  @Override
  public Optional<T1> retrieve(T2 id) {
    return repository.findById(id);
  }

  // update
  @Override
  public Optional<T1> patch(T2 id, Map<String, Object> fields) throws GsonTools.JsonObjectExtensionConflictException {
    Optional<T1> byId = repository.findById(id);
    var          user = byId.orElseThrow(() -> new EntityNotFoundException("entity not found id="+id));

    return Optional.ofNullable(repository.save(GsonTools.merge(user, fields)));
  }

  // delete
  @Override
  public void deleteById(T2 id) {
    repository.deleteById(id);
  }

  // delete
  @Override
  public void delete(T1 entity) {
    repository.delete(entity);
  }


  // replace
  @Override
  public Optional<T1> replace(T2 id, T1 replace) {
    return (Optional<T1>) repository.findById(id)
                                    .map(retrieved -> {
                                      BeanUtils.copyProperties(replace, retrieved, "id");
                                      return Optional.ofNullable(repository.save(retrieved));
                                    });
  }


  @Override
  public List<T1> retrieveAll() {
    return repository.findAll();
  }
}
