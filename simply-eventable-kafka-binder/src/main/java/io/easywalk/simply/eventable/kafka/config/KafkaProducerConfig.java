package io.easywalk.simply.eventable.kafka.config;


import io.easywalk.simply.eventable.kafka.SimplyEventableMessage;
import io.easywalk.simply.specification.eventable.annotations.SimplyProducerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.reflections.Reflections;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {

    private final BeanFactory        beanFactory;
    private final KafkaProperties    kafkaProperties;
    private       Map<Class, String> topics = new HashMap<>();
    @Value("${simply.eventable.entity-base-package}")
    private       String             basePackage;

    @Value("${simply.eventable.topic-property.number-of-partitions:1}")
    private Integer numPartitions;

    @Value("${simply.eventable.topic-property.number-of-replicas:1}")
    private Short numReplicas;

    @PostConstruct
    public void initialize() {
        createTopics();
        log.info("KafkaProducerConfig initiated");
    }

    private void createTopics() {
        Reflections reflections = new Reflections(basePackage);
        var         subTypesOf  = reflections.getTypesAnnotatedWith(SimplyProducerService.class);

        ConfigurableBeanFactory factory = (ConfigurableBeanFactory) beanFactory;

        subTypesOf.stream()
                .forEach(item -> {
                    this.topics.put(item, item.getName());
                    SimplyProducerService annotation = item.getAnnotation(SimplyProducerService.class);
                    NewTopic              newTopic   = new NewTopic(annotation.value(), numPartitions, numReplicas);
                    factory.registerSingleton(item.getName() + "topic", newTopic);
                    log.info("Topic created {} : {} : {}", item.getName(), numPartitions, numReplicas);
                });
    }


    @Bean("eventableProducerFactory")
    public ProducerFactory<String, SimplyEventableMessage> eventableProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("eventableEntityKafkaTemplate")
    public KafkaTemplate<String, SimplyEventableMessage> eventableEntityKafkaTemplate() {
        return new KafkaTemplate<>(eventableProducerFactory());
    }
}
