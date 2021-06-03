package io.easywalk.simply.eventable.kafka;

import lombok.*;

import javax.annotation.PostConstruct;

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
    }
}
