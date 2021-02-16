package io.easywalk.simply.eventable.kafka.producer;

import io.easywalk.simply.eventable.kafka.EventableEntity;
import io.easywalk.simply.eventable.kafka.config.KafkaProducerConfig;
import io.easywalk.simply.specification.annotation.SimplyProducer2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
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

    @PostConstruct
    private void init(){
        log.info("ProducibleAspect loaded");
    }

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

    private boolean isValid(Eventable entity) {
        if (entity == null) {
            log.error("Entity could not be published (entity is null)");
            return true;
        }
        return false;
    }

    // 반환이 Eventable 이거나 void 일때만 Publishing이 가능 하다.
    @AfterReturning(value = "@annotation(io.easywalk.simply.specification.annotation.SimplyProducer2) && execution(Object *(..))", returning = "entity")
    private void publishCreate(JoinPoint call, Eventable entity) throws RuntimeException {
        // entity가 null이면 할게 없어.
        if (entity == null) return;

        SimplyProducer2 caller = getSimplyProducer(call);

        // payload가 없는 경우
        publish(getEntity(call.getTarget()).getName()
                , caller.value()
                , entity.getId().toString()
                , entity);
    }

    // 반환이 void 일 경우는 첫번째 Param이 ID Type이 어야 한다.
    @AfterReturning(value = "@annotation(io.easywalk.simply.specification.annotation.SimplyProducer2) && execution(void *(..)) && args(entity)")
    private void publish(JoinPoint call, Eventable entity) throws RuntimeException {
        SimplyProducer2 caller = getSimplyProducer(call);

        publish(getEntity(call.getTarget()).getName()
                , caller.value()
                , entity.toString()
                , entity);
    }

    @AfterReturning(value = "@annotation(io.easywalk.simply.specification.annotation.SimplyProducer2) && execution(void *(.., @io.easywalk.simply.specification.annotation.SimplyProducerId (*), ..)) && args(id)")
    private void publish(JoinPoint call, Object id) throws RuntimeException {
        SimplyProducer2 caller = getSimplyProducer(call);

        publish(getEntity(call.getTarget()).getName()
                , caller.value()
                , id.toString()
                , null);
    }


    @AfterReturning(value = "@annotation(io.easywalk.simply.specification.annotation.SimplyProducer2) && execution(* *(.., @io.easywalk.simply.specification.annotation.SimplyProducerId (*), ..)) && args(id)", returning = "entity")
    private void publish(JoinPoint call, Object id, Eventable entity) throws RuntimeException {
        SimplyProducer2 caller = getSimplyProducer(call);

        publish(getEntity(call.getTarget()).getName()
                , caller.value()
                , id.toString()
                , entity);
    }

    private SimplyProducer2 getSimplyProducer(JoinPoint call) {
        MethodSignature signature = (MethodSignature) call.getSignature();
        Method          method    = signature.getMethod();
        return method.getAnnotation(SimplyProducer2.class);
    }

    @Async
    public void publish(String topic, String type, String key, Eventable entity) {

        EventableEntity message = new EventableEntity(key, type, entity);
        // publish to specific partition
        kafkaTemplate.send(topic, key.hashCode() % kafkaConfig.getNumPartitions(), key, message);
    }
}
