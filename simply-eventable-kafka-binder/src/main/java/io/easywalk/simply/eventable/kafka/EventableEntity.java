package io.easywalk.simply.eventable.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventableEntity<T, ID> {
    private String key;
    private String   eventType;
    private T      payload;
}
