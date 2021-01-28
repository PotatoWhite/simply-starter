package io.crcell.simply.eventable.producer;

import io.crcell.simply.eventable.config.KafkaProducerConfig;
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
