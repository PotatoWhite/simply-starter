package io.crcell.simply.eventable.consumer;

public interface Consumer<T, ID> {
    T handleSave(T entity);

    Boolean handleDelete(ID id);

}
