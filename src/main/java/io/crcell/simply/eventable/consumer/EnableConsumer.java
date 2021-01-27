package io.crcell.simply.eventable.consumer;

import io.crcell.simply.eventable.config.EventableConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

import java.lang.annotation.*;

@EnableKafka
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EventableConfig.class})
@Configuration
public @interface EnableConsumer {
}
