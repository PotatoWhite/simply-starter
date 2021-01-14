package io.crcell.pramework.eventable.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crcell.pramework.eventable.EventableEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class EventHandler<ID, T> implements MessageListener<ID, EventableEntity<T, ID>> {

  ObjectMapper mapper = new ObjectMapper();

  private final Class<T>       type;
  private final Consumer<T,ID> consumer;

  public EventHandler(Class<T> type,  Consumer<T,ID> consumer) {
    this.type = type;
    this.consumer = consumer;
  }

  @Override
  public void onMessage(ConsumerRecord<ID, EventableEntity<T, ID>> message) {
    if(message.value() == null) return;

    switch(message.value().getEventType()) {
      case SAVE:
        T entity = mapper.convertValue(message.value().getPayload(), type);
        consumer.handleSave(entity);
        break;
      case DELETE:
        consumer.handleDelete(message.value().getKey());
        break;
    }
  }


}

