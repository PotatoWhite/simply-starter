package io.crcell.simply.eventable.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crcell.simply.eventable.EventableEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class EventHandler<ID, T> implements MessageListener<ID, EventableEntity<T, ID>> {

    private final Class<T>          type;
    private final SimplyConsumer<T> simplyConsumer;
    ObjectMapper mapper = new ObjectMapper();

    public EventHandler(Class<T> type, SimplyConsumer<T> simplyConsumer) {
        this.type           = type;
        this.simplyConsumer = simplyConsumer;
    }

    @Override
    public void onMessage(ConsumerRecord<ID, EventableEntity<T, ID>> message) {
        if (message.value() == null) return;

        switch (message.value()
                       .getEventType()) {
            case SAVE:
                T entity = mapper.convertValue(message.value()
                                                      .getPayload(), type);
                simplyConsumer.onSave(message.value().getKey(), entity);
                break;
            case DELETE:
                simplyConsumer.onDelete(message.value().getKey());
                break;
        }
    }


}

