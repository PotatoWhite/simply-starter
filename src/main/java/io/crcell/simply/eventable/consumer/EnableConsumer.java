package io.crcell.simply.eventable.consumer;

import io.crcell.simply.eventable.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({KafkaConfig.class})
@Configuration
public @interface EnableConsumer {
}
