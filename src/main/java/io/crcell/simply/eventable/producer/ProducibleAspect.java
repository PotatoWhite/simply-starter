package io.crcell.simply.eventable.producer;

import io.crcell.simply.eventable.EventableEntity;
import io.crcell.simply.eventable.config.KafkaProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


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

    public static Class<?> getEntity(JpaRepository repo) {
        Type   clazzes  = getGenericType(repo.getClass())[0];
        Type[] jpaClass = getGenericType(getClass(clazzes));
        return getClass(((ParameterizedType) jpaClass[0]).getActualTypeArguments()[0]);
    }

    public static Type[] getGenericType(Class<?> target) {
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

    @AfterReturning(value = "execution(* io.crcell.simply.eventable.producer.repository.ProducibleRepository.save(..))", returning = "entity")
    private void publishSave(Eventable entity) throws RuntimeException {
        if (entity == null) {
            log.error("Entity could not be published (entity is null)");
            return;
        }

        publish(entity.getClass().getName(), EventableEntity.Type.SAVE, entity.getId().toString(), entity);
    }

    @AfterReturning(value = "execution(* io.crcell.simply.eventable.producer.repository.ProducibleRepository.deleteById(..))")
    private void publishDeleteById(JoinPoint point) throws RuntimeException {
        Object[] args  = point.getArgs();
        String   topic = getEntity((JpaRepository) point.getTarget()).getName();
        var      id    = args[0];

        publish(topic, EventableEntity.Type.DELETE, id.toString(), null);
    }

    @AfterReturning(value = "execution(* io.crcell.simply.eventable.producer.repository.ProducibleRepository.delete(..))")
    private void publishDelete(JoinPoint point) throws RuntimeException {
        Object[] args  = point.getArgs();
        String   topic = getEntity((JpaRepository) point.getTarget()).getName();

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
