package io.crcell.simply.eventable.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crcell.simply.eventable.EventableEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class EventHandler<ID, T> implements MessageListener<ID, EventableEntity<T, ID>> {

    private final Class<T>    type;
    private final Consumer<T> consumer;
    ObjectMapper mapper = new ObjectMapper();

    public EventHandler(Class<T> type, Consumer<T> consumer) {
        this.type     = type;
        this.consumer = consumer;
    }

    @Override
    public void onMessage(ConsumerRecord<ID, EventableEntity<T, ID>> message) {
        if (message.value() == null) return;

        switch (message.value()
                       .getEventType()) {
            case SAVE:
                T entity = mapper.convertValue(message.value()
                                                      .getPayload(), type);
                consumer.onSave(entity);
                break;
            case DELETE:
                consumer.onDelete(message.value().getKey());
                break;
        }
    }


}

