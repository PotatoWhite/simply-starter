package io.crcell.simply.eventable.consumer;

public interface Consumer<T, ID> {
    T onSave(T entity);

    Boolean onDelete(ID id);

}
