package io.easywalk.simply.eventable;

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
    private Type   eventType;
    private T      payload;

    public enum Type {
        SAVE, DELETE
    }
}