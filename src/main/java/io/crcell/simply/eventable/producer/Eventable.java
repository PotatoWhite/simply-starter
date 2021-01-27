package io.crcell.simply.eventable.producer;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public interface Eventable<ID> {
    ID getId();
}
