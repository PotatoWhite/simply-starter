package io.crcell.simply.eventable.consumer;

public interface Consumer<T> {
    T onSave(T entity);

    Boolean onDelete(String id);

}
