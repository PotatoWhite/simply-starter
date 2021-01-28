package io.crcell.simply.controllable;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ControllableAdvice.class, SimplyLoggingAspect.class})
@Configuration
public @interface EnableSimplyControllable {
}
