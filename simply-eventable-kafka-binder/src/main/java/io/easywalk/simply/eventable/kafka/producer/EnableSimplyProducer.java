package io.easywalk.simply.eventable.kafka.producer;

import io.easywalk.simply.eventable.kafka.config.KafkaProducerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

import java.lang.annotation.*;

@EnableKafka
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ProducibleAspect.class, KafkaProducerConfig.class})
@Configuration
public @interface EnableSimplyProducer {
}
