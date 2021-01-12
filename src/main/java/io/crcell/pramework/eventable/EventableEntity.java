package io.crcell.pramework.eventable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventableEntity<T> {
  private String key;
  private Type   eventType;
  private T payload;

  public enum Type {
    SAVE, DELETE
  }
}
