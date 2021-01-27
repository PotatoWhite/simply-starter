package io.crcell.simply.eventable.producer;

import io.crcell.simply.eventable.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ProducibleAspect.class, KafkaConfig.class})
@Configuration
public @interface EnableProducer {
}
