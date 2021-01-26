package io.crcell.pramework.controllable;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({ControllableAdvice.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Configuration
public @interface ControllableResponse {
}
