package io.easywalk.simply.controllable;

import io.easywalk.simply.SimplySpec;
import io.easywalk.simply.serviceable.AbstractServiceable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public abstract class AbstractControllable<T, ID> implements SimplySpec<T, ID> {


    private final AbstractServiceable<T, ID> service;

    @Override
    @SimplyLogging
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@Valid @RequestBody T createForm) throws Throwable {
        return service.create(createForm);
    }

    @Override
    @SimplyLogging
    @PutMapping("/{id}")
    public T replaceById(@PathVariable ID id, @RequestBody @Valid T replace) throws Throwable {
        checkEntityExistenceAndThrow(id);
        return service.replaceById(id, replace);
    }

    @Override
    @SimplyLogging
    @PatchMapping("/{id}")
    public T updateById(@PathVariable ID id, @RequestBody Map<String, Object> fields) throws Throwable {
        checkEntityExistenceAndThrow(id);
        return service.updateById(id, fields);
    }


    @Override
    @GetMapping("/{id}")
    public T get(@PathVariable ID id) throws Throwable {
        return service.get(id);
    }


    @Override
    @GetMapping()
    public List<T> getAll() {
        List<T> all = service.getAll();
        if (all.isEmpty())
            throw new EntityNotFoundException();
        return all;
    }


    @Override
    @SimplyLogging
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable ID id) throws Throwable {
        try {
            checkEntityExistenceAndThrow(id);
        } catch (NoSuchElementException e) {
            //for idempotency
            return;
        }

        service.deleteById(id);
    }

    @Override
    public void delete(T entity) throws Throwable {
        // not use in controller
    }

    private void checkEntityExistenceAndThrow(@PathVariable ID id) throws NoSuchElementException {
        try {
            service.get(id);
        } catch (EntityNotFoundException e) {
            // for 404
            throw new NoSuchElementException(e.getMessage());
        }
    }
}
