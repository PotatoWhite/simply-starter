package io.crcell.simply;

import java.util.List;
import java.util.Map;

public interface SimplySpec<T, ID> {
    T create(T createForm) throws Throwable;

    T replaceById(ID id, T replace) throws Throwable;

    T updateById(ID id, Map<String, Object> fields) throws Throwable;

    T get(ID id) throws Throwable;

    List<T> getAll() throws Throwable;

    void deleteById(ID id) throws Throwable;

    void delete(T entity) throws Throwable;
}
