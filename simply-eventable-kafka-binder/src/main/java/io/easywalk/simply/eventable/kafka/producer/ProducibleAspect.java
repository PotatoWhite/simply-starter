package io.easywalk.simply.eventable.kafka.producer;

import io.easywalk.simply.eventable.kafka.SimplyEventableMessage;
import io.easywalk.simply.eventable.kafka.config.KafkaProducerConfig;
import io.easywalk.simply.specification.eventable.annotations.SimplyProducer;
import io.easywalk.simply.specification.eventable.annotations.SimplyProducerService;
import io.easywalk.simply.specification.serviceable.annotations.SimplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.*;


@SuppressWarnings("unchecked")
@EnableAsync
@Slf4j
@Aspect
@Component
public class ProducibleAspect {

    private final KafkaProducerConfig kafkaConfig;
    private final KafkaTemplate       kafkaTemplate;

    public ProducibleAspect(KafkaProducerConfig kafkaConfig, @Qualifier("eventableEntityKafkaTemplate") KafkaTemplate kafkaTemplate) {
        this.kafkaConfig   = kafkaConfig;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static Class<?> getEntity(Object target) {
        Type clazzes = getGenericType(target.getClass())[0];
        return getClass(((ParameterizedType) clazzes).getActualTypeArguments()[0]);
    }

    private static Type[] getGenericType(Class<?> target) {
        if (target == null)
            return new Type[0];
        Type[] types = target.getGenericInterfaces();
        if (types.length > 0) {
            return types;
        }
        Type type = target.getGenericSuperclass();
        if (type != null) {
            if (type instanceof ParameterizedType) {
                return new Type[]{type};
            }
        }
        return new Type[0];
    }

    private static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type     componentType  = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0)
                        .getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @PostConstruct
    private void init() {
        log.info("ProducibleAspect initiated");
    }

    private boolean isValid(SimplyEntity entity) {
        if (entity == null) {
            log.error("Entity could not be published (entity is null)");
            return true;
        }
        return false;
    }

    private String getTopic(JoinPoint call) {

        return call.getTarget().getClass().getAnnotation(SimplyProducerService.class).value();
    }

    private String getEventType(JoinPoint call) {
        MethodSignature signature = (MethodSignature) call.getSignature();
        Method          method    = signature.getMethod();
        return method.getAnnotation(SimplyProducer.class).value();
    }

    @Pointcut("@annotation(io.easywalk.simply.specification.eventable.annotations.SimplyProducer) && @target(io.easywalk.simply.specification.eventable.annotations.SimplyProducerService)")
    public void producibleAnnotations() {
    }

    // 반환이 Eventable 이거나 void 일때만 Publishing이 가능 하다.
    @AfterReturning(value = "producibleAnnotations() && execution(* *(..))", returning = "entity")
    private void publishCreate(JoinPoint call, SimplyEntity entity) throws RuntimeException {
        // entity가 null이면 할게 없어.
        if (entity == null) return;

        // payload가 없는 경우
        publish(getTopic(call)
                , getEventType(call)
                , entity.getId().toString()
                , entity);
    }

    /**
     * 반환이 void 이면서 args가 entity가 있다면, entity의 id만 취하고 payload는 비운다.
     * 단순히 Event 만 전달한다.
     */
    // delete
    @AfterReturning(value = "producibleAnnotations() && execution(void *(..)) && args(entity)")
    private void publish(JoinPoint call, SimplyEntity entity) throws RuntimeException {
        SimplyProducer caller = getSimplyProducer(call);

        publish(getTopic(call)
                , getEventType(call)
                , entity.getId().toString()
                , null);
    }

    // deleteById
    @AfterReturning(value = "producibleAnnotations() && execution(void *(.., @io.easywalk.simply.specification.eventable.annotations.SimplyProducerId (*), ..)) && args(id)")
    private void publish(JoinPoint call, Object id) throws RuntimeException {
        SimplyProducer caller = getSimplyProducer(call);

        publish(getTopic(call)
                , getEventType(call)
                , id.toString()
                , null);
    }


    // replaceById
    @AfterReturning(value = "producibleAnnotations() && execution(* *(.., @io.easywalk.simply.specification.eventable.annotations.SimplyProducerId (*), ..)) && args(id)", returning = "entity")
    private void publish(JoinPoint call, Object id, SimplyEntity entity) throws RuntimeException {
        publish(getTopic(call)
                , getEventType(call)
                , id.toString()
                , entity);
    }

    private SimplyProducer getSimplyProducer(JoinPoint call) {
        MethodSignature signature = (MethodSignature) call.getSignature();
        Method          method    = signature.getMethod();
        return method.getAnnotation(SimplyProducer.class);
    }

    public void publish(String topic, String type, String key, SimplyEntity entity) {
        SimplyEventableMessage message = null;

        if (entity == null)
            message = new SimplyEventableMessage(key, type, null, null);
        else
            message = new SimplyEventableMessage(key, type, entity.getClass().getName(), entity);

        log.info("[PUB] {} {}", topic, message);

        // publish to specific partition
        kafkaTemplate.send(topic, Math.abs(key.hashCode() % kafkaConfig.getNumPartitions()), key, message);
    }
}
