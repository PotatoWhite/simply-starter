package io.easywalk.simply.serviceable;

import io.easywalk.simply.specification.SimplySpec;
import io.easywalk.simply.specification.annotation.SimplyProducer2;
import io.easywalk.simply.specification.annotation.SimplyProducerId;
import io.easywalk.simply.utils.GsonTools;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public abstract class AbstractServiceable<T, ID> implements SimplySpec<T, ID> {
    protected final JpaRepository<T, ID> repository;

    protected AbstractServiceable(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }


    // create
    @SimplyProducer2("CREATE")
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
    @SimplyProducer2("UPDATE")
    @Override
    public T updateById(@SimplyProducerId ID id, Map<String, Object> fields) throws Throwable {
        return repository.save(GsonTools.merge(get(id), fields));
    }

    // delete
    @SimplyProducer2("DELETE")
    @Override
    public void deleteById(@SimplyProducerId ID id) {
        repository.deleteById(id);
    }

    // delete
    @SimplyProducer2("DELETE")
    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    // replace
    @SimplyProducer2("UPDATE")
    @Override
    public T replaceById(@SimplyProducerId ID id, T replace) throws EntityNotFoundException {
        T retrieved = get(id);
        BeanUtils.copyProperties(replace, retrieved, "id");
        return repository.save(retrieved);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }
}
