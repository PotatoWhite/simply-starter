package io.easywalk.simply.clientable;

import io.easywalk.simply.controllable.SimplyErrorResponse;
import io.easywalk.simply.specification.SimplySpec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public class AbstractClientable<T, ID> implements SimplySpec<T, ID> {
    private WebClient client;
    private Class<T>  entityTypeClass;

    public AbstractClientable(Class<T> typeParameterClass, WebClient client) {
        this.entityTypeClass = typeParameterClass;
        this.client          = client;
    }

    private Throwable makeException(SimplyErrorResponse errorbody) {
        try {
            Class         exception    = Class.forName(errorbody.getOriginalExceptionType());
            Constructor[] constructors = exception.getDeclaredConstructors();
            var exceptionClass = Arrays.stream(constructors)
                                       .filter(constructor -> constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(String.class))
                                       .map(newException -> {
                                           try {
                                               return newException.newInstance(errorbody.getDescription());
                                           } catch (Exception e) {
                                               log.debug(e.getMessage());
                                           }
                                           return null;
                                       })
                                       .findFirst();

            return (Throwable) exceptionClass.orElseGet(() -> new Exception(errorbody.getDescription()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Exception(errorbody.getDescription());

    }

    @Override
    public T create(T createForm) throws Throwable {
        return client.post()
                     .body(Mono.just(createForm), createForm.getClass())
                     .retrieve()
                     .onStatus(status -> !status.equals(HttpStatus.CREATED), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                             .flatMap(errorbody -> Mono.error(makeException(errorbody))))
                     .bodyToMono(entityTypeClass).block();
    }


    public T replaceById(ID id, T replace) throws Throwable {
        String path = "/" + id.toString();
        return client.put()
                     .uri(path)
                     .body(Mono.just(replace), replace.getClass())
                     .retrieve()
                     .onStatus(status -> !status.equals(HttpStatus.OK), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                        .flatMap(errorbody -> Mono.error(makeException(errorbody))))
                     .bodyToMono(entityTypeClass).block();
    }

    @Override
    public T updateById(ID id, Map<String, Object> fields) throws Throwable {
        String path = "/" + id.toString();

        return client.patch()
                     .uri(path)
                     .body(Mono.just(fields), fields.getClass())
                     .retrieve()
                     .onStatus(status -> !status.equals(HttpStatus.OK), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                        .flatMap(errorbody -> Mono.error(makeException(errorbody))))
                     .bodyToMono(entityTypeClass).block();
    }

    @Override
    public T get(ID id) throws Throwable {
        String path = "/" + id.toString();
        return client.get()
                     .uri(path)
                     .retrieve()
                     .onStatus(status -> !status.equals(HttpStatus.OK), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                        .flatMap(errorbody -> Mono.error(makeException(errorbody))))
                     .bodyToMono(entityTypeClass).block();
    }

    @Override
    public List<T> getAll() throws Throwable {
        return client.get()
                     .retrieve()
                     .onStatus(status -> !status.equals(HttpStatus.OK), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                        .flatMap(errorbody -> Mono.error(makeException(errorbody))))
                     .bodyToFlux(entityTypeClass)
                     .collect(Collectors.toList())
                     .block();
    }

    @Override
    public void deleteById(ID id) throws Throwable {
        String path = "/" + id.toString();
        client.delete()
              .uri(path)
              .retrieve()
              .onStatus(status -> !status.equals(HttpStatus.NO_CONTENT), clientResponse -> clientResponse.bodyToMono(SimplyErrorResponse.class)
                                                                                                         .flatMap(errorbody -> Mono.error(makeException(errorbody))))
              .bodyToMono(Void.class)
              .block();
    }

    @Override
    public void delete(T entity) throws Throwable {
        throw new Exception("Not Implement");
    }
}
