package io.crcell.pramework.eventable.consumer;

import io.crcell.pramework.eventable.EventableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import javax.persistence.MappedSuperclass;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@MappedSuperclass
public abstract class ConsumerImpl<T, ID> implements Consumer<T, ID> {
  @Value("${spring.application.name}")
  private   String   groupId;
  protected Class<T> type;

  @Autowired
  private KafkaProperties kafkaProperties;

  @Override
  public abstract T handleSave(T entity);

  @Override
  public abstract Boolean handleDelete(ID id);

  protected ConsumerImpl(Class<T> type) {
    this.type = type;
  }


  @Bean
  public void messageListenerContainer() {
    Map<String, String> properties     = kafkaProperties.getProperties();
    ContainerProperties containerProps = new ContainerProperties(type.getName());
    containerProps.setMessageListener(new EventHandler<ID, T>(type, this));
    KafkaMessageListenerContainer<ID, EventableEntity<T, ID>> container = createContainer(containerProps);
    container.setBeanName(type.getName()+"ListenerBean");
    container.start();
  }


  private Map<String, Object> consumerProps() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, EventableEntity.class.getPackageName());

    return props;
  }


  private KafkaMessageListenerContainer<ID, EventableEntity<T, ID>> createContainer(ContainerProperties containerProps) {
    Map<String, Object>                                       props     = consumerProps();
    DefaultKafkaConsumerFactory<ID, EventableEntity<T, ID>>   cf        = new DefaultKafkaConsumerFactory<>(props);
    KafkaMessageListenerContainer<ID, EventableEntity<T, ID>> container = new KafkaMessageListenerContainer<>(cf, containerProps);

    return container;
  }


}
