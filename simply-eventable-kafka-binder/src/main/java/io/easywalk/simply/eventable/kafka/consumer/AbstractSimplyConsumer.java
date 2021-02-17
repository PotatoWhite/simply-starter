package io.easywalk.simply.eventable.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.easywalk.simply.eventable.kafka.SimplyEventableMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
public abstract class AbstractSimplyConsumer<T, ID> implements SimplyConsumer<T> {
    protected ObjectMapper mapper = new ObjectMapper();

    protected String          topic;
    protected Class<T>        type;
    @Value("${spring.application.name}")
    private   String          groupId;
    @Autowired
    private   KafkaProperties kafkaProperties;

    protected AbstractSimplyConsumer(String topic, Class<T> type) {
        this.topic = topic;
        this.type  = type;
    }

    @Override
    public abstract void on(SimplyEventableMessage<T> message);

    @Bean
    public void messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener(new EventHandler<ID, T>(type, this));
        KafkaMessageListenerContainer<ID, SimplyEventableMessage<T>> container = createContainer(containerProps);
        container.setBeanName(type.getName() + "ListenerBean");
        container.start();
    }


    private Map<String, Object> consumerProps() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, SimplyEventableMessage.class.getPackageName());

        return props;
    }


    private KafkaMessageListenerContainer<ID, SimplyEventableMessage<T>> createContainer(ContainerProperties containerProps) {
        Map<String, Object>                                          props     = consumerProps();
        DefaultKafkaConsumerFactory<ID, SimplyEventableMessage<T>>   cf        = new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<ID, SimplyEventableMessage<T>> container = new KafkaMessageListenerContainer<>(cf, containerProps);

        return container;
    }

    protected T convertToEntity(Object source, Class<T> clazz) throws JsonProcessingException {
        String s = mapper.writeValueAsString(source);
        return mapper.readValue(s, clazz);
    }
}
