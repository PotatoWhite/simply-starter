package io.crcell.pramework.eventable.config;


import io.crcell.pramework.eventable.Eventable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.reflections.Reflections;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class EventableConfig {

  private final BeanFactory beanFactory;


  @Value("${pramework.entity-base-package}")
  private String basePackage;

  @Value("${pramework.topic-property.number-of-partitions:1}")
  private Integer numPartitions;

  @Value("${pramework.topic-property.number-of-replicas:1}")
  private Short numReplicas;

  @PostConstruct
  public void initialize() {
    createTopics();
  }

  private void createTopics() {
    Reflections                     reflections = new Reflections(basePackage);
    Set<Class<? extends Eventable>> subTypesOf  = reflections.getSubTypesOf(Eventable.class);

    ConfigurableBeanFactory factory = (ConfigurableBeanFactory) beanFactory;

    subTypesOf.stream()
              .forEach(item -> {
                NewTopic newTopic = new NewTopic(item.getName(), numPartitions, numReplicas);
                factory.registerSingleton(item.getName()+"topic", newTopic);
                log.info("Topic created {} : {} : {}", item.getName(), numPartitions, numReplicas);
              });
  }

  public NewTopic healthTopic() {
    return new NewTopic("healthCheck", 1, (short) 1);
  }
}
