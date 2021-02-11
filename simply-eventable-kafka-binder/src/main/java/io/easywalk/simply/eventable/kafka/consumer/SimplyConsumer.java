package io.easywalk.simply.eventable.kafka.consumer;

public interface SimplyConsumer<T> {
    T onCreate(String key, T entity);

    T onUpdate(String key, T entity);

    Boolean onDelete(String key);
}
