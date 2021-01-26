package io.crcell.pramework.controllable;

import io.crcell.pramework.serviceable.Serviceable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class ControllableImpl<T1, T2> implements Controllable<T1, T2> {
    private final Serviceable service;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T1 create(@Valid @RequestBody T1 createForm) throws Throwable {
        Optional<T1> save = service.create(createForm);
        return save.orElseThrow(() -> new Exception());
    }

    @Override
    @PutMapping("/{id}")
    public T1 replaceById(@PathVariable T2 id, @RequestBody @Valid T1 replace) throws Throwable {
        Optional<T1> replaced = service.replace(id, replace);
        return replaced.orElseThrow(() -> new Exception());
    }

    @Override
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable T2 id) throws Throwable {
        Optional retrieve = service.retrieve(id);
        retrieve.map(target -> {
            service.delete(target);
            return true;
        }).orElseThrow(() -> new Exception());
    }
}
