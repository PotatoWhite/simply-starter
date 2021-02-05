package io.crcell.simply.eventable.consumer;

public interface SimplyConsumer<T> {
    T onSave(String key, T entity);

    Boolean onDelete(String key);
}
