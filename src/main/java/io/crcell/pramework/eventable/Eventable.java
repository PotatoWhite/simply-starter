package io.crcell.pramework.eventable;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public interface Eventable<ID> {
  ID getId();
}
