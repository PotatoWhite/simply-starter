package io.easywalk.simply.eventable.kafka.consumer;

import io.easywalk.simply.eventable.kafka.SimplyEventableMessage;

public interface SimplyConsumer<T> {
    void on(SimplyEventableMessage message);
}
