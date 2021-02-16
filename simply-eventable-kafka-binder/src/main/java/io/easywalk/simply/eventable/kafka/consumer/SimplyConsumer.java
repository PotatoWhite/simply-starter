package io.easywalk.simply.eventable.kafka.consumer;

public interface SimplyConsumer<T> {
    T on(String eventType, String key, T entity);
}
