package io.crcell.pramework.eventable;

import io.crcell.pramework.eventable.config.EventableConfig;
import io.crcell.pramework.eventable.repository.EventableAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EventableAspect.class, EventableConfig.class})
@Configuration
public @interface EnableEventable {
}
