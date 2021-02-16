package io.easywalk.simply.eventable.kafka.consumer;

import io.easywalk.simply.eventable.kafka.EventableEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class EventHandler<ID, T> implements MessageListener<ID, EventableEntity<T, ID>> {

    private final Class<T>          type;
    private final SimplyConsumer<T> simplyConsumer;

    public EventHandler(Class<T> type, SimplyConsumer<T> simplyConsumer) {
        this.type           = type;
        this.simplyConsumer = simplyConsumer;
    }

    @Override
    public void onMessage(ConsumerRecord<ID, EventableEntity<T, ID>> message) {
        if (message.value() == null) return;
        simplyConsumer.on(message.value().getEventType(), message.value().getKey(), message.value().getPayload());
    }


}

