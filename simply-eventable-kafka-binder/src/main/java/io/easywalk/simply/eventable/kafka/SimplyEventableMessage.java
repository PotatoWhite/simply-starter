package io.easywalk.simply.eventable.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.easywalk.simply.specification.serviceable.annotations.SimplyEntity;
import lombok.*;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimplyEventableMessage<T> {
    @NonNull
    private String key;
    @NonNull
    private String eventType;

    private String payloadType;
    private T      payload;

    @PostConstruct
    private void init() {
        payloadType = payload.getClass().getName();
    }}
