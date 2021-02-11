package io.easywalk.simply.eventable.producer;

import io.easywalk.simply.eventable.EventableEntity;
import io.easywalk.simply.eventable.config.KafkaProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


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

    private boolean isValid(Eventable entity) {
        if (entity == null) {
            log.error("Entity could not be published (entity is null)");
            return true;
        }
        return false;
    }


    @AfterReturning(value = "@target(io.easywalk.simply.eventable.producer.SimplyProducer) && execution(* io.easywalk.simply.serviceable.AbstractServiceable.create(..))", returning = "entity")
    private void publishCreate(Eventable entity) throws RuntimeException {
        if (isValid(entity)) return;
        publish(entity.getClass().getName(), EventableEntity.Type.CREATE, entity.getId().toString(), entity);
    }

    @AfterReturning(pointcut = "@target(io.easywalk.simply.eventable.producer.SimplyProducer) && execution(* io.easywalk.simply.serviceable.AbstractServiceable.updateById(..))", returning = "entity")
    private void publishUpdate(Eventable entity) throws RuntimeException {
        if (isValid(entity)) return;
        publish(entity.getClass().getName(), EventableEntity.Type.UPDATE, entity.getId().toString(), entity);
    }

    @AfterReturning(value = "@target(io.easywalk.simply.eventable.producer.SimplyProducer) && execution(* io.easywalk.simply.serviceable.AbstractServiceable.replaceById(..))", returning = "entity")
    private void publishReplace(Eventable entity) throws RuntimeException {
        if (isValid(entity)) return;
        publish(entity.getClass().getName(), EventableEntity.Type.UPDATE, entity.getId().toString(), entity);
    }


    @AfterReturning(value = "@target(io.easywalk.simply.eventable.producer.SimplyProducer) && execution(* io.easywalk.simply.serviceable.AbstractServiceable.deleteById(..))")
    private void publishDeleteById(JoinPoint point) throws RuntimeException {
        Object[] args  = point.getArgs();
        String   topic = getEntity(point.getTarget()).getName();
        var      id    = args[0];

        publish(topic, EventableEntity.Type.DELETE, id.toString(), null);
    }

    @AfterReturning(value = "@target(io.easywalk.simply.eventable.producer.SimplyProducer) && execution(* io.easywalk.simply.serviceable.AbstractServiceable.delete(..))")
    private void publishDelete(JoinPoint point) throws RuntimeException {
        Object[] args  = point.getArgs();
        String   topic = getEntity(point.getTarget()).getName();

        var entity = (Eventable) args[0];

        publish(topic, EventableEntity.Type.DELETE, entity.getId().toString(), null);
    }

    @Async
    public void publish(String topic, EventableEntity.Type type, String key, Eventable entity) {

        EventableEntity message = new EventableEntity(key, type, entity);
        // publish to specific partition
        kafkaTemplate.send(topic, key.hashCode() % kafkaConfig.getNumPartitions(), key, message);
    }
}
