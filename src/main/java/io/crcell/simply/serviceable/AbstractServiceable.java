package io.crcell.simply.serviceable;

import io.crcell.simply.SimplySpec;
import io.crcell.simply.utils.GsonTools;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * P-ramework(PostatoWhite) / 2021-01-06
 */
public abstract class AbstractServiceable<T, ID> implements SimplySpec<T, ID> {
    protected final JpaRepository<T, ID> repository;

    protected AbstractServiceable(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }


    // create
    @Override
    public T create(T entity) throws EntityExistsException, DataIntegrityViolationException {
        return repository.save(entity);
    }

    // retrieve
    @Override
    public T get(ID id) throws NoSuchElementException {
        return repository.findById(id)
                         .orElseThrow(() -> new NoSuchElementException("entityid=" + id));
    }

    // update
    @Override
    public T updateById(ID id, Map<String, Object> fields) throws Throwable {
        return repository.save(GsonTools.merge(get(id), fields));
    }

    // delete
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    // delete
    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    // replace
    @Override
    public T replaceById(ID id, T replace) throws EntityNotFoundException {
        T retrieved = get(id);
        BeanUtils.copyProperties(replace, retrieved, "id");
        return repository.save(retrieved);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }
}
