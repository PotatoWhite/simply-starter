package io.easywalk.simply.eventable.consumer;

public interface SimplyConsumer<T> {
    T onCreate(String key, T entity);

    T onUpdate(String key, T entity);

    Boolean onDelete(String key);
}
