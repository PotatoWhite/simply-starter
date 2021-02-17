package io.easywalk.simply.eventable.kafka.consumer;

import io.easywalk.simply.eventable.kafka.SimplyEventableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class EventHandler<ID, T> implements MessageListener<ID, SimplyEventableMessage<T, ID>> {

    private final Class<T>          type;
    private final SimplyConsumer<T> simplyConsumer;

    public EventHandler(Class<T> type, SimplyConsumer<T> simplyConsumer) {
        this.type           = type;
        this.simplyConsumer = simplyConsumer;
    }

    @Override
    public void onMessage(ConsumerRecord<ID, SimplyEventableMessage<T, ID>> message) {
        if (message.value() == null) return;
        simplyConsumer.on(message.value());
    }


}

