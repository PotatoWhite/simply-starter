package io.easywalk.simply.serviceable;

import io.easywalk.simply.specification.SimplySpec;
import io.easywalk.simply.specification.eventable.annotations.SimplyProducer;
import io.easywalk.simply.specification.eventable.annotations.SimplyProducerId;
import io.easywalk.simply.specification.serviceable.annotations.SimplyEntity;
import io.easywalk.simply.utils.GsonTools;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


public abstract class AbstractServiceable<T extends SimplyEntity, ID> implements SimplySpec<T, ID> {
    protected final JpaRepository<T, ID> repository;

    protected AbstractServiceable(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }


    // create
    @SimplyProducer("CREATE")
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
    @SimplyProducer("UPDATE")
    @Override
    public T updateById(@SimplyProducerId ID id, Map<String, Object> fields) throws Throwable {
        return repository.save(GsonTools.merge(get(id), fields));
    }

    // delete
    @SimplyProducer("DELETE")
    @Override
    public void deleteById(@SimplyProducerId ID id) {
        Optional<T> byId = repository.findById(id);
        byId.ifPresent(repository::delete);
    }

    // delete
    @SimplyProducer("DELETE")
    @Override
    public void delete(T entity) {
        Optional<T> byId = repository.findById((ID) entity.getId());
        byId.ifPresent(repository::delete);
    }

    // replace
    @SimplyProducer("UPDATE")
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
