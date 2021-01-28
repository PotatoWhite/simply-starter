package io.crcell.simply.controllable;

import io.crcell.simply.serviceable.Serviceable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractControllable<T1, T2> implements Controllable<T1, T2> {
    private final Serviceable service;


    @Override
    @SimplyLogging
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T1 create(@Valid @RequestBody T1 createForm) throws Throwable {
        Optional<T1> save = service.create(createForm);
        return save.orElseThrow(() -> new Exception());
    }

    @Override
    @SimplyLogging
    @PutMapping("/{id}")
    public T1 replaceById(@PathVariable T2 id, @RequestBody @Valid T1 replace) throws Throwable {
        Optional<T1> replaced = service.replace(id, replace);
        return replaced.orElseThrow(() -> new Exception());
    }

    @Override
    @SimplyLogging
    @PatchMapping("/{id}")
    public T1 updateById(@PathVariable T2 id, @RequestBody Map<String, Object> fields) throws Throwable {
        Optional<T1> patched = service.patch(id, fields);
        return patched.orElseThrow(() -> new Exception());
    }

    @Override
    @GetMapping("/{id}")
    public T1 get(@PathVariable T2 id) throws Throwable {
        Optional<T1> byId = service.retrieve(id);
        return byId.orElseThrow(() -> new EntityNotFoundException());
    }

    @Override
    @GetMapping()
    public List<T1> getAll() {
        var retrieveAll = service.retrieveAll();

        // fast exit
        if (retrieveAll.isEmpty())
            throw new EntityNotFoundException();

        return retrieveAll;
    }


    @Override
    @SimplyLogging
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable T2 id) {
        Optional<T1> byId   = service.retrieve(id);
        T1           target = byId.orElseThrow(() -> new EntityNotFoundException("Entity(" + id + ") does not exist."));
        service.deleteById(target);
    }
}
