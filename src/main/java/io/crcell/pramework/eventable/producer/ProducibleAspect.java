package io.crcell.pramework.eventable.producer;

import io.crcell.pramework.eventable.EventableEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class ProducibleAspect {

  private final KafkaTemplate kafkaTemplate;

  public static Class<?> getEntity(JpaRepository repo) {
    Type   clazzes  = getGenericType(repo.getClass())[0];
    Type[] jpaClass = getGenericType(getClass(clazzes));
    return getClass(((ParameterizedType) jpaClass[0]).getActualTypeArguments()[0]);
  }

  public static Type[] getGenericType(Class<?> target) {
    if(target == null)
      return new Type[0];
    Type[] types = target.getGenericInterfaces();
    if(types.length > 0) {
      return types;
    }
    Type type = target.getGenericSuperclass();
    if(type != null) {
      if(type instanceof ParameterizedType) {
        return new Type[]{type};
      }
    }
    return new Type[0];
  }

  private static Class<?> getClass(Type type) {
    if(type instanceof Class) {
      return (Class) type;
    } else if(type instanceof ParameterizedType) {
      return getClass(((ParameterizedType) type).getRawType());
    } else if(type instanceof GenericArrayType) {
      Type     componentType  = ((GenericArrayType) type).getGenericComponentType();
      Class<?> componentClass = getClass(componentType);
      if(componentClass != null) {
        return Array.newInstance(componentClass, 0)
                    .getClass();
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @After(value = "execution(* io.crcell.pramework.eventable.producer.repository.ProducibleRepository.save(..))")
  private void publishSave(JoinPoint point) throws RuntimeException {
    Object[] args = point.getArgs();

    if(args == null) {
      log.error("Empty entity");
      return;
    }

    String    topic  = getEntity((JpaRepository) point.getTarget()).getName();
    Eventable entity = (Eventable) args[0];

    publish(topic, EventableEntity.Type.SAVE, entity.getId()
                                                    .toString(), (Eventable) args[0]);
  }

  @After(value = "execution(* io.crcell.pramework.eventable.producer.repository.ProducibleRepository.deleteById(..))")
  private void publishDeleteById(JoinPoint point) throws RuntimeException {
    Object[] args  = point.getArgs();
    String   topic = getEntity((JpaRepository) point.getTarget()).getName();

    var entity = (Long) args[0];

    publish(topic, EventableEntity.Type.DELETE, entity.toString(), null);
  }

  @After(value = "execution(* io.crcell.pramework.eventable.producer.repository.ProducibleRepository.delete(..))")
  private void publishDelete(JoinPoint point) throws RuntimeException {
    Object[] args  = point.getArgs();
    String   topic = getEntity((JpaRepository) point.getTarget()).getName();

    var entity = (Eventable) args[0];

    publish(topic, EventableEntity.Type.DELETE, entity.getId()
                                                      .toString(), null);
  }

  public void publish(String topic, EventableEntity.Type type, String key, Eventable entity) {

    EventableEntity message = new EventableEntity(key, type, entity);
    kafkaTemplate.send(topic, key, message);
  }
}
