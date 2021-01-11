package io.crcell.pramework.eventable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventableEntity {
  private String key;
  private Type   eventType;
  private Object payload;

  public enum Type {
    SAVE, DELETE
  }
}
