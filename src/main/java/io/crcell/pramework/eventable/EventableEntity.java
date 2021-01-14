package io.crcell.pramework.eventable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventableEntity<T, ID> {
  private ID key;
  private Type   eventType;
  private T payload;

  public enum Type {
    SAVE, DELETE
  }
}
